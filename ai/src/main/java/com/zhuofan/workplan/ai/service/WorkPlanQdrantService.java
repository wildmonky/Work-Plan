package com.zhuofan.workplan.ai.service;

import com.google.common.util.concurrent.ListenableFuture;
import com.zhuofan.workplan.ai.dto.WorkPlanQdrantDTO;
import com.zhuofan.workplan.ai.entity.WorkPlanRiskAndMeasureTemplate;
import com.zhuofan.workplan.ai.mapper.WorkPlanRiskAndMeasureTemplateMapper;
import com.zhuofan.workplan.ai.util.SnowFlakeGenerator;
import io.qdrant.client.*;
import io.qdrant.client.grpc.Collections;
import io.qdrant.client.grpc.Common;
import io.qdrant.client.grpc.JsonWithInt;
import io.qdrant.client.grpc.Points;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.ai.vectorstore.qdrant.autoconfigure.QdrantVectorStoreProperties;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.function.Function;
import java.util.stream.Collectors;

@Slf4j
@Service
public class WorkPlanQdrantService {

    @Resource
    private QdrantClient client;

    @Resource
    private QdrantVectorStoreProperties qdrantProps;

    @Resource
    private SnowFlakeGenerator snowFlakeGenerator;

    @Resource
    private WorkPlanRiskAndMeasureTemplateMapper controlMeasureMapper;

    public void initCollectionWhenNotExisted() {
        if (isCollectionExisted()) {
            return;
        }

        try {
            boolean initResult = initCollection();
            if (!initResult) {
                log.error("qdrant 集合初始化失败");
            }
        } catch (ExecutionException | InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 判断qdrant集合是否存在
     * @return true-存在;false-不存在
     */
    private boolean isCollectionExisted() {
        ListenableFuture<Boolean> future = client.collectionExistsAsync(qdrantProps.getCollectionName());
        try {
            return future.get(30, TimeUnit.SECONDS);
        } catch (TimeoutException | InterruptedException | ExecutionException e) {
            return false;
        }
    }

    /**
     * 初始化 Collection
     * @return
     * @throws ExecutionException
     * @throws InterruptedException
     */
    public boolean initCollection() throws ExecutionException, InterruptedException {
        Collections.CollectionOperationResponse collectionOperationResponse = client.createCollectionAsync(
                Collections.CreateCollection.newBuilder()
                        .setCollectionName(qdrantProps.getCollectionName())
                        // 1. 稠密向量
                        .setVectorsConfig(
                                Collections.VectorsConfig.newBuilder()
                                        .setParamsMap(Collections.VectorParamsMap.newBuilder()
                                                .putMap("abstract-plan-vector",
                                                        Collections.VectorParams.newBuilder()
                                                                .setSize(4096)
                                                                .setDistance(Collections.Distance.Cosine).build()
                                                ).putMap("origin-plan-vector",
                                                        Collections.VectorParams.newBuilder()
                                                                .setSize(4096)
                                                                .setDistance(Collections.Distance.Cosine).build()
                                                ).build()
                                        ).build()
                        )
//                        // 🌟 2. 新增：稀疏向量配置 (Sparse Vectors Config)
//                        .setSparseVectorsConfig(
//                                Collections.SparseVectorConfig.newBuilder()
//                                        // 键为稀疏向量名称，值为具体参数
//                                        .putMap("plan-sparse",
//                                                Collections.SparseVectorParams.newBuilder()
//                                                        // 可选：自动应用 IDF 权重（强烈推荐）
//                                                        .setModifier(Collections.Modifier.Idf)
//                                                        // 可选：将索引存储在磁盘以节省内存 (如开启用 .setIndex(...))
//                                                        .build()
//                                        ).build()
//                        )
                        .setHnswConfig(
                                Collections.HnswConfigDiff.newBuilder()
                                        .setM(16)
                                        .setEfConstruct(100)
                                        .build()
                        )
                        .setOptimizersConfig(
                                Collections.OptimizersConfigDiff.newBuilder()
                                        .setDefaultSegmentNumber(2)
                                        .setIndexingThreshold(20000)
                                        .build()
                        )
                        .setReplicationFactor(1)
                        .setShardNumber(1)
                        .build()
        ).get();

        // 创建playLoad索引
        client.createPayloadIndexAsync(qdrantProps.getCollectionName(), "voltage", Collections.PayloadSchemaType.Text,
                Collections.PayloadIndexParams.newBuilder().setTextIndexParams(
                        Collections.TextIndexParams.newBuilder()
                                .setTokenizer(Collections.TokenizerType.Word)
                                .setMinTokenLen(2)
                                .build()
                ).build(),
                null, null, null
        ).get();

        return collectionOperationResponse.getResult();
    }

    /**
     * 保存典型作业计划<br/>
     * 1.先将作业计划保存至向量数据库,返回id(pointId)<br/>
     * 2.再将对应的管控措施保存到达梦数据库,通过pointId和原作业内容与管控措施关联<br/>
     *
     * @param workPlan 要保存的作业计划
     */
    public void saveClassicWorkPlan(WorkPlanQdrantDTO workPlan, Function<List<String>, List<List<Float>>> embeddingFunc) {
        Long pointId = saveVector(workPlan, embeddingFunc);
        // 管控措施
        String controlMeasure = workPlan.getControlMeasures();
        WorkPlanRiskAndMeasureTemplate template = new WorkPlanRiskAndMeasureTemplate();
        template.setId(pointId + "");
        template.setWorkContent(workPlan.getPlan());
        template.setUsingControlMeasure(controlMeasure);
        controlMeasureMapper.insert(template);
    }

    /**
     * 调用嵌入模型将文本转换成向量,保存到向量数据库
     * @param workPlan 要保存的数据
     * @return 保存数据对应的点id. 传入数据为null时,返回null
     */
    public Long saveVector(WorkPlanQdrantDTO workPlan, Function<List<String>, List<List<Float>>> embeddingFunc) {
        if (workPlan == null) {
            return null;
        }

        Points.PointStruct pointStruct = buildPointStruct(workPlan, embeddingFunc);
        upsert(pointStruct);
        return pointStruct.getId().getNum();
    }

    /**
     * 构造保存的点结构
     * {@link #saveVector} 内部调用
     * @param workPlan 要保存的数据
     * @return qdrant点数据
     */
    private Points.PointStruct buildPointStruct(WorkPlanQdrantDTO workPlan, Function<List<String>, List<List<Float>>> embeddingFunc) {
        if (workPlan == null || StringUtils.isBlank(workPlan.getAbstractPlan())) {
            return null;
        }

        List<List<Float>> embeddings = embeddingFunc.apply(Arrays.asList(workPlan.getAbstractPlan(), workPlan.getPlan()));
        Map<String, Points.Vector> namedVectorMap = new HashMap<>();
        namedVectorMap.put("abstract-plan-vector", VectorFactory.vector(embeddings.get(0)));
        namedVectorMap.put("origin-plan-vector", VectorFactory.vector(embeddings.get(1)));

        return Points.PointStruct.newBuilder()
                .setId(PointIdFactory.id(snowFlakeGenerator.generateNextId()))
                .setVectors(VectorsFactory.namedVectors(namedVectorMap))
                .putAllPayload(toPayloadMap(workPlan))
                .build();
    }

    /**
     * 构造qdrant 点数据中的载荷(payload)
     * {@link #saveVector}内部调用
     * @param workPlan 要保存的数据
     * @return payload数据
     */
    private Map<String, JsonWithInt.Value> toPayloadMap(WorkPlanQdrantDTO workPlan) {
        Map<String, JsonWithInt.Value> payloadMap = new HashMap<>();
        payloadMap.put("originPlan",
                JsonWithInt.Value.newBuilder()
                        .setStringValue(workPlan.getPlan())
                        .build()
        );
        payloadMap.put("code",
                JsonWithInt.Value.newBuilder()
                        .setStringValue(workPlan.getCode())
                        .build()
        );
        payloadMap.put("tag",
                JsonWithInt.Value.newBuilder()
                        .setStringValue(workPlan.getTags())
                        .build()
        );
        payloadMap.put("voltage",
                JsonWithInt.Value.newBuilder()
                        .setStringValue(workPlan.getVoltage())
                        .build()
        );
        payloadMap.put("deviceType",
                JsonWithInt.Value.newBuilder()
                        .setStringValue(workPlan.getDeviceTypes())
                        .build()
        );
        payloadMap.put("workType",
                JsonWithInt.Value.newBuilder()
                        .setStringValue(workPlan.getWorkTypes())
                        .build()
        );
        payloadMap.put("abstractPlan",
                JsonWithInt.Value.newBuilder()
                        .setStringValue(workPlan.getAbstractPlan())
                        .build()
        );
        payloadMap.put("risk",
                JsonWithInt.Value.newBuilder()
                        .setStringValue(workPlan.getRisk())
                        .build()
        );
        payloadMap.put("profession",
                JsonWithInt.Value.newBuilder()
                        .setStringValue(workPlan.getProfession())
                        .build()
        );
        return payloadMap;
    }

    /**
     * 更新/插入点数据 {@link #saveVector}内部调用
     * @param point
     */
    private void upsert(Points.PointStruct point) {
        Points.UpsertPoints upsertPoint = Points.UpsertPoints
                .newBuilder()
                .setCollectionName(qdrantProps.getCollectionName())
                .addPoints(point)
                .setTimeout(30 * 1000)
                .setWait(true)
                .build();
        client.upsertAsync(upsertPoint);
    }

    /**
     * 根据{@code workPlan}查询相似的点数据(作业计划)
     *
     * @param workPlan 作业计划数据
     * @return 相似的点数据
     */
    private List<Points.ScoredPoint> search(WorkPlanQdrantDTO workPlan, Function<List<String>, List<List<Float>>> embeddingFunc) {
        if (workPlan == null) {
            return java.util.Collections.emptyList();
        }

        Common.Filter.Builder filterBuilder = Common.Filter.newBuilder();
        List<Common.Condition> conditions = new ArrayList<>();
        if (StringUtils.isNotBlank(workPlan.getVoltage())) {
            conditions.add(ConditionFactory.matchText("voltage", workPlan.getVoltage()));
        }

        if (StringUtils.isNotBlank(workPlan.getTags())) {
            String tag = workPlan.getTags();
            conditions.add(ConditionFactory.matchTextAny("tag", tag.replaceAll("[,，]", " ")));
        }

        if (StringUtils.isNotBlank(workPlan.getDeviceTypes())) {
            String deviceType = workPlan.getDeviceTypes();
            String[] split = deviceType.split("[,，]");
            for (int i = 0; i < split.length; i++) {
                String text = Arrays.stream(split).limit(i).collect(Collectors.joining(" "));
                conditions.add(ConditionFactory.matchText("deviceType", text));
            }
        }

        filterBuilder.addAllShould(conditions);

        // 获取向量
        List<List<Float>> vectors = embeddingFunc.apply(Arrays.asList(workPlan.getAbstractPlan(), workPlan.getPlan()));

        try {
            return search(vectors, filterBuilder);
        } catch (ExecutionException | InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 查询 {@link #searchData}内部调用
     * @param vectors 向量
     * @param filterBuilder 过滤条件构建器
     * @return 查询结果-点数据集合
     * @throws ExecutionException 查询时异常
     * @throws InterruptedException 查询时发生中断异常
     */
    private List<Points.ScoredPoint> search(List<List<Float>> vectors, Common.Filter.Builder filterBuilder) throws ExecutionException, InterruptedException {
        ListenableFuture<List<Points.ScoredPoint>> future = client.queryAsync(
                Points.QueryPoints.newBuilder()
                        .setCollectionName(qdrantProps.getCollectionName())
                        .addPrefetch(Points.PrefetchQuery.newBuilder()
                                .setQuery(
                                        Points.Query.newBuilder().setNearest(Points.VectorInput.newBuilder().setDense(
                                                        Points.DenseVector.newBuilder()
                                                                .addAllData(vectors.get(0))
                                                                .build()
                                                ).build()
                                        ).build()
                                ).setUsing("abstract-plan-vector")
                                .setFilter(filterBuilder)
                                .setLimit(100)
                                .build()
                        ).addPrefetch(Points.PrefetchQuery.newBuilder()
                                .setQuery(
                                        Points.Query.newBuilder().setNearest(Points.VectorInput.newBuilder().setDense(
                                                        Points.DenseVector.newBuilder()
                                                                .addAllData(vectors.get(1))
                                                                .build()
                                                ).build()
                                        )
                                )
                                .setUsing("origin-plan-vector")
                                .setFilter(filterBuilder)
                                .setLimit(100)
                                .build()
                        ).setQuery(Points.Query.newBuilder().setRrf(Points.Rrf.newBuilder().build()).build())
                        .setLimit(50)
                        .setWithPayload(Points.WithPayloadSelector.newBuilder()
                                .setEnable(true)
                                .build()
                        ).build()
        );
        return future.get();
    }

    /**
     * 根据{@code workPlan}查询相似的作业计划数据
     * @param workPlan 输入作业计划
     * @return 相似的作业计划列表
     */
    public List<WorkPlanQdrantDTO> searchData(WorkPlanQdrantDTO workPlan, Function<List<String>, List<List<Float>>> embeddingFunc) {
        List<Points.ScoredPoint> points = search(workPlan, embeddingFunc);
        List<WorkPlanQdrantDTO> reList = new ArrayList<>();
        for (Points.ScoredPoint point : points) {
            WorkPlanQdrantDTO plan = new WorkPlanQdrantDTO();
            Common.PointId id = point.getId();
            plan.setId(id.getNum() + "");

            Map<String, JsonWithInt.Value> payload = point.getPayloadMap();

            JsonWithInt.Value voltage = payload.get("voltage");
            if (voltage != null) {
                plan.setVoltage(voltage.getStringValue());
            }

            JsonWithInt.Value abstractPlan = payload.get("abstractPlan");
            if (voltage != null) {
                plan.setAbstractPlan(abstractPlan.getStringValue());
            }

            JsonWithInt.Value code = payload.get("code");
            if (voltage != null) {
                plan.setCode(code.getStringValue());
            }

            JsonWithInt.Value deviceType = payload.get("deviceType");
            if (voltage != null) {
                plan.setDeviceTypes(deviceType.getStringValue());
            }

            JsonWithInt.Value originPlan = payload.get("originPlan");
            if (voltage != null) {
                plan.setPlan(originPlan.getStringValue());
            }

            JsonWithInt.Value risk = payload.get("risk");
            if (voltage != null) {
                plan.setRisk(risk.getStringValue());
            }

            JsonWithInt.Value tag = payload.get("tag");
            if (voltage != null) {
                plan.setTags(tag.getStringValue());
            }

            JsonWithInt.Value workType = payload.get("workType");
            if (voltage != null) {
                plan.setWorkTypes(workType.getStringValue());
            }

            plan.setScore(point.getScore());

            reList.add(plan);
        }
        return reList;
    }

    /**
     * 查询作业计划对应的管控措施
     *
     * @param qdrantPointId 作业计划点id
     * @return 对应的管控措施
     */
    public WorkPlanRiskAndMeasureTemplate searchControlMeasureByPointId(String qdrantPointId) {
        if (StringUtils.isBlank(qdrantPointId)) {
            return null;
        }
        return controlMeasureMapper.selectById(qdrantPointId);
    }

}

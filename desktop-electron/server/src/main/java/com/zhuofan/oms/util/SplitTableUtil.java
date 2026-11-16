package com.zhuofan.oms.util;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.util.CollectionUtils;

import javax.sql.DataSource;
import java.sql.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.function.BiFunction;
import java.util.stream.Collectors;

/**
 *
 * <ul>SplitTableUtils</ul>
 * <ul>.with(dataSource) // 设置数据源</ul>
 * <ul>.useLogic( </ul>
 *                         SplitTableUtils.TimeMappingLogic.builder() <br/>
 *                                 .timeColumnName("acting_time") <br/>
 *                                 .durationUnit(SplitTableUtils.TimeMappingLogic.SplitDuration.MONTHLY) <br/>
 *                                 .duration(1L) <br/>
 *                                 .createTablePostProcess((con, tableName) -> { <br/>
 *                                     Pattern pattern = Pattern.compile("(\\d+)"); <br/>
 *                                     Matcher matcher = pattern.matcher(tableName); <br/>
 *                                     long seed = 1; <br/>
 *                                     while (matcher.find()) { <br/>
 *                                         seed = Long.parseLong(matcher.group(1)) * 1000_000; <br/>
 *                                     } <br/>
 *                                     try { <br/>
 *                                         Statement statement = con.createStatement(); <br/>
 *                                         statement.addBatch("alter table " + tableName + " add id bigint identity("+ seed +",1) not null");
 *                                         statement.executeBatch(); <br/>
 *                                     } catch (SQLException e) { <br/>
 *                                         throw new RuntimeException(e); <br/>
 *                                     } <br/>
 *                                 }) <br/>
 *                                 .build() <br/>
 *                 ) // 源表数据分割逻辑 <br/>
 *                 .sourceTable(
 *                         SplitTableUtils.SourceTable.builder() <br/>
 *                                 .sourceSchema("ZWOCS") <br/>
 *                                 .sourceTableName("ALARM_YKEVENTLOG") <br/>
 *                                 .primaryKeyColumnNames(Arrays.asList("attr_oid", "acting_time", "object_oid", "acting_desc")) <br/>
 *                                 .where("") <br/>
 *                                 .build() <br/>
 *                 ) // 源表设置 <br/>
 *                 .targetTable( <br/>
 *                         SplitTableUtils.TargetTable.builder() <br/>
 *                                 .targetSchema("OMS_ZF") <br/>
 *                                 .targetTablePrefixName("ALARM_YKEVENTLOG") <br/>
 *                                 .build() <br/>
 *                 ) // 目标表设置 <br/>
 *                 .doSplit(); 分割表 <br/>
 *                 </code>
 */
@Slf4j
public class SplitTableUtil {

    private DataSource dataSource;

    private SourceTable sourceTable;

    private TargetTable targetTable;

    private MappingLogic mappingLogic;

    public static SplitTableUtil dataSource(DataSource dataSource) {
        SplitTableUtil splitTableUtil = new SplitTableUtil();
        splitTableUtil.dataSource = dataSource;
        return splitTableUtil;
    }

    public SplitTableUtil useLogic(MappingLogic mappingLogic) {
        this.mappingLogic = mappingLogic;
        return this;
    }

    public SplitTableUtil sourceTable(SourceTable sourceTable) {
        this.sourceTable = sourceTable;
        return this;
    }

    public SplitTableUtil targetTable(TargetTable targetTable) {
        this.targetTable = targetTable;
        return this;
    }

    public void doSplit() {
        try (
                Connection connection = this.dataSource.getConnection()
        ){
            connection.setAutoCommit(false);
            Savepoint savepoint = connection.setSavepoint();

            String targetTablePrefixName = targetTable.getTargetTablePrefixName();
            try {
                // 获取数据映射关系
                List<MappingLogic.Mapping> mappings = this.mappingLogic.mapping(connection, this.sourceTable);
                for (MappingLogic.Mapping mapping : mappings) {

                    String targetTableName = targetTablePrefixName + (mapping.appendSuffix ? mapping.getTableNameSuffix() : "");

                    TableOperationStatusEnum tableOperationStatusEnum = createTargetTable(connection, this.sourceTable, this.targetTable, targetTableName);

                    // 表创建成功(表已存在不会触发),后置处理
                    CiConsumer<Connection, String, TableOperationStatusEnum> createTablePostProcess = mapping.getCreateTablePostProcess();
                    if (createTablePostProcess != null) {
                        createTablePostProcess.accept(connection, targetTable.getTargetSchema() + "." + targetTableName, tableOperationStatusEnum);
                    }

                    if (tableOperationStatusEnum == TableOperationStatusEnum.CREATE_FAILED) {
                        continue;
                    }

                    // 创建 更新时间、插入时间字段
                    Set<String> multiAddColNames = new HashSet<>();
                    multiAddColNames.add(this.targetTable.getInsertTimeColumnName());
                    multiAddColNames.add(this.targetTable.getUpdateTimeColumnName());
                    multiAddColNames = multiAddColNames.stream().filter(StringUtils::isNotBlank).collect(Collectors.toSet());
                    List<AppendColumnInfo> appendColumnInfos = new ArrayList<>();
                    for (String multiAddColName : multiAddColNames) {
                        appendColumnInfos.add(new AppendColumnInfo(multiAddColName, "datetime(6)"));
                    }
                    addColumns(connection,this.targetTable.getTargetSchema(), targetTableName, appendColumnInfos);

                    // 合并数据
                    mergeData(connection, sourceTable, targetTable, mapping);
                }
                log.info("分表操作成功");
            } catch (Exception e) {
                log.error("分表操作失败", e);
                connection.rollback(savepoint);
            } finally {
                connection.commit();
            }
        } catch (Exception e) {
            log.error("数据库连接异常", e);
        }
    }

    /**
     * 添加附加列
     *
     * @param connection 数据库连接
     * @param targetSchema 目标schema
     * @param targetTableName 目标表
     * @param appendColumnInfos 添加列信息 名称、类型
     */
    public static void addColumns(Connection connection, String targetSchema, String targetTableName, List<AppendColumnInfo> appendColumnInfos) {
        if (CollectionUtils.isEmpty(appendColumnInfos)) {
            return;
        }

        String tableName = (StringUtils.isNotBlank(targetSchema) ? targetSchema + "." : "") + targetTableName;
        try {
            Statement statement = connection.createStatement();
            statement.execute("select UPPER(COLUMN_NAME) from SYS.ALL_TAB_COLUMNS WHERE CONCAT(OWNER, '.', TABLE_NAME)= '" + tableName + "';");
            ResultSet resultSet = statement.getResultSet();
            List<String> columnNames = new ArrayList<>();
            while (resultSet.next()) {
                columnNames.add(resultSet.getString(1));
            }

            for (AppendColumnInfo appendColumnInfo : appendColumnInfos) {
                if (columnNames.contains(appendColumnInfo.getName())) {
                    if (appendColumnInfo.getWhenExisted() == AppendColumnExistedEnum.THROW) {
                        throw new RuntimeException(tableName + "表中" + appendColumnInfo.getName() + "列已存在，无须重复设置");
                    }

                    log.info("{}表中{}列已存在，无须重复设置", tableName, appendColumnInfo.getName());
                    if (appendColumnInfo.getWhenExisted() == AppendColumnExistedEnum.CONTINUE) {
                        continue;
                    }
                    if (appendColumnInfo.getWhenExisted() == AppendColumnExistedEnum.BREAK) {
                        break;
                    }
                }
                String s = "alter table " + tableName + " add " + appendColumnInfo.getName() + " " + appendColumnInfo.getType();
                statement.addBatch(s);
                log.info("添加附加列{}, sql: {}", appendColumnInfo.getName(), s);
            }

            statement.executeBatch();
            statement.clearBatch();
            statement.close();
            log.info("附加列添加完成");
        } catch (SQLException e) {
            throw new RuntimeException("附加列新增失败");
        }
    }

    /**
     * 创建目标表
     *
     * @param connection 数据库连接
     * @param sourceTable 源表信息
     * @param targetTable 目标表信息
     * @param targetTableName 目标表名称
     * @return true-目标表新建成功；
     *         null-目标表已存在;
     *         false-目标表创建失败
     * @throws SQLException
     */
    private TableOperationStatusEnum createTargetTable(Connection connection, SourceTable sourceTable, TargetTable targetTable, String targetTableName) throws SQLException {
        Savepoint savepoint = connection.setSavepoint();
        Statement statement = connection.createStatement();
        String createSQl = "create table %s.%s AS select * from %s.%s where 1=0";
        try {
            statement.execute(String.format(createSQl, targetTable.getTargetSchema(), targetTableName, sourceTable.getSourceSchema(), sourceTable.getSourceTableName()));
        } catch (Exception e) {
            String message = e.getMessage();
            if (message != null && message.contains("已存在")) {
                log.info("{} 表已存在", targetTableName);
                return TableOperationStatusEnum.TABLE_EXISTED;
            } else {
                log.error("{} 表创建失败", targetTableName, e);
                connection.rollback(savepoint);
                return TableOperationStatusEnum.CREATE_FAILED;
            }
        }
        return TableOperationStatusEnum.CREATE_SUCCESS;
    }

    public void mergeData(Connection connection, SourceTable sourceTable, TargetTable targetTable, MappingLogic.Mapping mapping) throws SQLException {

        String targetSchemaTableName = targetTable.getTargetSchema() + "." + targetTable.getTargetTablePrefixName() + mapping.getTableNameSuffix();

        String sourceTableSelectSql = mapping.mappingSql(sourceTable);
        if (StringUtils.isBlank(sourceTableSelectSql)) {
            throw new RuntimeException("源表数据查询语句为空");
        }

        String keyColumnEqual = sourceTable.getPrimaryKeyColumnNames().stream()
                .map(e -> targetSchemaTableName + "." + e + "=" +  "a." + e)
                .collect(Collectors.joining(" and "));

        List<String> columnNames = sourceTableColumnNamesSync(sourceTable.getSourceSchema(), sourceTable.getSourceTableName());
        List<String> columnNamesWithoutKeyColumnNames = columnNames.stream().filter(e ->!sourceTable.getPrimaryKeyColumnNames().contains(e.toLowerCase())).collect(Collectors.toList());

        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS");
        String update = "";
        if (StringUtils.isNotBlank(targetTable.getUpdateTimeColumnName())) {
            String updateTimeColumnName = targetTable.getUpdateTimeColumnName();
            // update
            update = "UPDATE SET " +
                    columnNamesWithoutKeyColumnNames.stream()
                            .map(e -> targetSchemaTableName + "." + e + "=" + "a." + e)
                            .collect(Collectors.joining(","));

            if (StringUtils.isNotBlank(updateTimeColumnName)) {
                update += "," + targetSchemaTableName + "." + updateTimeColumnName + "='" + LocalDateTime.now().format(dateTimeFormatter) + "'";
            }
        }

        // insert
        StringBuilder columnNameStr = new StringBuilder();
        StringBuilder columnNameValueStr = new StringBuilder();

        for (String columnName : columnNames) {
            columnNameStr.append(",").append(columnName);
            columnNameValueStr.append(",a.").append(columnName);
        }
        if (StringUtils.isNotBlank(targetTable.getInsertTimeColumnName())) {
            columnNameStr.append(",").append(targetTable.getInsertTimeColumnName());
            columnNameValueStr.append(",'").append(LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"))).append("'");
        }
        String insert = String.format("INSERT (%s) VALUES (%s)", columnNameStr.substring(1), columnNameValueStr.substring(1));
        Savepoint savepoint = connection.setSavepoint();

        String sql = "MERGE INTO " + targetSchemaTableName  +
                " USING (" +
                sourceTableSelectSql +
                ") a ON (" +
                keyColumnEqual +
                ") " +
                (BooleanUtils.isTrue(targetTable.isNeedUpdate()) ? " WHEN MATCHED THEN " + update : "") +
                " WHEN NOT MATCHED THEN " + insert + ";";
        try {
            // 合并数据 merge into
            Statement statement = connection.createStatement();
            statement.execute(sql);
            // 提交后，无法回滚前面的操作
//            connection.commit();
            statement.close();
            log.info("分表操作成功：源表{}.{} ====> 目标表{}", sourceTable.getSourceSchema(), sourceTable.getSourceTableName(), targetSchemaTableName);
        } catch (Exception e) {
            log.error("分表操作失败: {}.{} ====> {}, SQL: {}", sourceTable.getSourceSchema(), sourceTable.getSourceTableName(), sql, targetSchemaTableName, e);
            connection.rollback(savepoint);
        }
    }

    private List<String> sourceTableColumnNamesSync(String sourceSchema, String sourceTableName) {
        try (
                Connection connection = dataSource.getConnection()
        ){
            Statement statement = connection.createStatement();
            statement.execute("select * from " + sourceSchema + "." + sourceTableName + " limit 1");
            ResultSet resultSet = statement.getResultSet();
            ResultSetMetaData metaData = resultSet.getMetaData();
            List<String> reList = new ArrayList<>();
            for (int i = 1; i < metaData.getColumnCount(); i++) {
                reList.add(metaData.getColumnName(i));
            }
            return reList;
        } catch (Exception e) {
            throw new RuntimeException(String.format("获取源表%s.%s的字段名失败", sourceSchema, sourceTableName));
        }
    }

    @Getter
    @Setter
    @Builder
    public static class SourceTable {

        private String sourceSchema;

        private String sourceTableName;

        private List<String> primaryKeyColumnNames;

        private List<String> splitColumnName;

        private String where;

        private String orderBy;

    }

    @Getter
    @Setter
    @Builder
    public static class TargetTable {

        private String targetSchema;

        private String targetTablePrefixName;

        // 是否同步旧数据的变化
        private boolean needUpdate;

        // 更新时间列名
        private String updateTimeColumnName;

        // 插入时间列名
        private String insertTimeColumnName;

    }

    public enum TableOperationStatusEnum {
        CREATE_SUCCESS,
        TABLE_EXISTED,
        CREATE_FAILED
    }

    public interface CiConsumer<T, U, S> {

        void accept(T t, U u, S s);

        default CiConsumer<T, U, S> andThen(CiConsumer<? super T, ? super U, ? super S> after) {
            Objects.requireNonNull(after);

            return (l, r, s) -> {
                accept(l, r, s);
                after.accept(l, r, s);
            };
        }
    }

    public abstract static class MappingLogic {

        public abstract List<Mapping> mapping(Connection connection, SourceTable sourceTable);

        @Getter
        @Setter
        public static class Mapping {

            private String tableNameSuffix;

            private boolean appendSuffix
//                    = true
                    ;

            private boolean createWhenTableNotExisted = true;

            /**
             * 如果，不想表创建后置处理结果影响到整个分表步骤，请在后置处理中自行捕获异常
             */
            private CiConsumer<Connection, String, TableOperationStatusEnum> createTablePostProcess;

            /**
             * 目标目需要插入的源表数据 select * from 源表 语句
             */
            public String mappingSql(SourceTable sourceTable) {
                return null;
            }

        }

    }

    @Setter
    @Getter
    @Builder
    public static class TimeMappingLogic extends MappingLogic {

        private String timeColumnName;

        private long duration;

        private SplitDuration durationUnit;

        private CiConsumer<Connection, String, TableOperationStatusEnum> createTablePostProcess;

        @Override
        public List<Mapping> mapping(Connection connection, SourceTable sourceTable) {
            String sourceSchema = sourceTable.getSourceSchema();
            String sourceTableName = sourceTable.getSourceTableName();

            try {
                TimeRange timeRangeInSourceTable = findTimeRangeInSourceTable(connection, sourceTable, this.timeColumnName);
                LocalDateTime min = timeRangeInSourceTable.getMin();
                LocalDateTime max = timeRangeInSourceTable.getMax();

                if (!ObjectUtils.allNotNull(min, max)) {
                    log.info("{}.{} 无数据。", sourceSchema, sourceTableName);
                    return Collections.emptyList();
                }

                return separator(min, max, this.durationUnit, this.duration, (start, end) -> {
                    TimeMapping mapping = new TimeMapping();
                    mapping.setTableNameSuffix(generateTableSuffix(start, this.durationUnit));
                    mapping.setTimeColumnName(timeColumnName);
                    mapping.setCreateWhenTableNotExisted(true);
                    mapping.setAppendSuffix(true);
                    mapping.setStart(start);
                    mapping.setEnd(end);
                    if (createTablePostProcess != null) {
                        mapping.setCreateTablePostProcess(createTablePostProcess);
                    }
                    return mapping;
                });
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        }

        public String generateTableSuffix(LocalDateTime start, SplitDuration splitDuration) {
            return dateFormat(start, splitDuration);
        }

        private String dateFormat(LocalDateTime start, SplitDuration splitDuration) {
            int index = splitDuration.getIndex();

            String suffix = "";
            if (splitDuration == SplitDuration.NOT_SPLIT) {
                return suffix;
            }

            if (index == SplitDuration.WEEKLY.getIndex() ) {
                Calendar calendar = Calendar.getInstance();
                calendar.set(start.getYear(), start.getMonthValue() - 1, start.getDayOfMonth(), start.getHour(), start.getMinute(), start.getSecond());
                calendar.setFirstDayOfWeek(Calendar.MONDAY);
                calendar.setMinimalDaysInFirstWeek(1);
                suffix += calendar.getWeekYear() + String.format("%02d", calendar.get(Calendar.WEEK_OF_YEAR));
            }

            if (index >= SplitDuration.YEARLY.getIndex()) {
                suffix += start.getYear();
            }

            if (index >= SplitDuration.MONTHLY.getIndex()) {
                suffix += String.format("%02d", start.getMonthValue());
            }

            if (index >= SplitDuration.DAILY.getIndex()) {
                suffix += String.format("%02d", start.getDayOfMonth());
            }

            if (index >= SplitDuration.HOURLY.getIndex()) {
                suffix += "_" + String.format("%02d", start.getHour());
            }

            if (index >= SplitDuration.MINUTELY.getIndex()) {
                suffix += String.format("%02d", start.getMinute());
            }

            if (index >= SplitDuration.SECOND.getIndex()) {
                suffix += String.format("%02d", start.getSecond());
            }

            return suffix;
        }

        private <T> List<T> separator(LocalDateTime min, LocalDateTime max,
                                            SplitDuration splitDuration, Long duration,
                                            BiFunction<LocalDateTime, LocalDateTime, T> func
        ) throws SQLException {

            LocalDateTime start = LocalDateTime.from(min);
            LocalDateTime end = LocalDateTime.from(max);

            switch (splitDuration) {
                case NOT_SPLIT:
                    break;
                case YEARLY:
                    start = start.withDayOfYear(1).withHour(0).withMinute(0).withSecond(0).withNano(0);
                    end = end.withDayOfYear(1).withHour(0).withMinute(0).withSecond(0).withNano(0).plusYears(1);
                    break;
                case MONTHLY:
                    start = start.withDayOfMonth(1).withHour(0).withMinute(0).withSecond(0).withNano(0);
                    end = end.withDayOfMonth(1).withHour(0).withMinute(0).withSecond(0).withNano(0).plusMonths(1);
                    break;
                case WEEKLY:
                    start = start.minusDays(start.getDayOfWeek().getValue() - 1).withHour(0).withMinute(0).withSecond(0).withNano(0);
                    end = end.withHour(0).withMinute(0).withSecond(0).withNano(0).plusDays(8 - end.getDayOfWeek().getValue());
                    break;
                case DAILY:
                    start = start.withHour(0).withMinute(0).withSecond(0).withNano(0);
                    end = end.withHour(0).withMinute(0).withSecond(0).withNano(0).plusDays(1);
                    break;
                case HOURLY:
                    start = start.withMinute(0).withSecond(0).withNano(0);
                    end = end.withMinute(0).withSecond(0).withNano(0).plusHours(1);
                    break;
                case MINUTELY:
                    start = start.withSecond(0).withNano(0);
                    end = end.withSecond(0).withNano(0).plusMinutes(1);
                    break;
                case SECOND:
                    start = start.withNano(0);
                    end = end.withNano(0).plusSeconds(1);
                    break;
                default:
                    throw new IllegalStateException("Unexpected value: " + splitDuration);
            }

            duration = duration == null || duration <= 0 ? 1 : duration;
            List<T> reList = new ArrayList<>();
            while (start.isBefore(end)) {
                LocalDateTime from = LocalDateTime.from(start);
                switch (splitDuration) {
                    case NOT_SPLIT:
                        start = end.plusSeconds(1); // [)前闭后开，加1s，保证囊括所有数据
                        break;
                    case YEARLY:
                        start = start.plusYears(duration);
                        break;
                    case MONTHLY:
                        start = start.plusMonths(duration);
                        break;
                    case WEEKLY:
                        start = start.plusWeeks(duration);
                        break;
                    case DAILY:
                        start = start.plusDays(duration);
                        break;
                    case HOURLY:
                        start = start.plusHours(duration);
                        break;
                    case MINUTELY:
                        start = start.plusMinutes(duration);
                        break;
                    case SECOND:
                        start = start.plusSeconds(duration);
                        break;
                    default:
                        throw new IllegalStateException("Unexpected value: " + splitDuration);
                }
                T apply = func.apply(from, LocalDateTime.from(start));
                reList.add(apply);
            }

            return reList;
        }

        private TimeRange findTimeRangeInSourceTable(Connection connection, SourceTable sourceTable, String timeColumnName) throws SQLException {
            String sourceSchema = sourceTable.getSourceSchema();
            String sourceTableName = sourceTable.getSourceTableName();

            Statement statement = connection.createStatement();
            String sql = String.format("SELECT MIN(%s) AS min_time, MAX(%s) AS max_time from %s.%s", timeColumnName, timeColumnName, sourceSchema, sourceTableName);
            if (StringUtils.isNotBlank(sourceTable.getWhere())) {
                sql += " where " + sourceTable.getWhere().replaceAll("(?i)where", "");
            }
            ResultSet resultSet = statement.executeQuery(sql);
            resultSet.next();
            Timestamp minTimestamp = resultSet.getTimestamp("min_time");
            Timestamp maxTimestamp = resultSet.getTimestamp("max_time");
            statement.close();
            return new TimeRange(minTimestamp == null ? null : minTimestamp.toLocalDateTime(),
                    maxTimestamp == null ? null : maxTimestamp.toLocalDateTime());
        }

        @Getter
        @Setter
        public static class TimeMapping extends Mapping {

            private String timeColumnName;

            private LocalDateTime start;

            private LocalDateTime end;

            /**
             * 目标目需要插入的源表数据 select * from 源表 语句
             */
            public String mappingSql(SourceTable sourceTable) {
                String sourceSchema = sourceTable.getSourceSchema();
                String sourceTableName = sourceTable.getSourceTableName();

                String format = String.format("select * from %s.%s where %s %s >= '%s' and %s < '%s'",
                        sourceSchema, sourceTableName,
                        StringUtils.isNotBlank(sourceTable.getWhere()) ? sourceTable.getWhere().replaceAll("(?i)where", "") + " AND " : "",
                        timeColumnName, start.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")),
                        timeColumnName, end.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"))
                );

                String orderBy = sourceTable.getOrderBy();
                if (StringUtils.isNotBlank(orderBy)) {
                    format += " order by " + orderBy;
                }
                return format;
            }

        }

        @Getter
        @Setter
        @AllArgsConstructor
        private static class TimeRange {

            private LocalDateTime min;

            private LocalDateTime max;

        }

        @Getter
        public enum SplitDuration {
            NOT_SPLIT(-1, "不分表"),
            WEEKLY(0, "周"),
            YEARLY(1, "年"),
            MONTHLY(2, "月"),
            DAILY(3, "日"),
            HOURLY(4, "小时"),
            MINUTELY(5, "分钟"),
            SECOND(6, "秒");

            private final int index;
            private final String unit;

            SplitDuration(int index, String unit) {
                this.index = index;
                this.unit = unit;
            }
        }
    }

    @Getter
    @Setter
    public static class AppendColumnInfo{

        private AppendColumnExistedEnum whenExisted = AppendColumnExistedEnum.CONTINUE;

        private String name;

        private String type;

        public AppendColumnInfo(String name, String type) {
            this.name = name;
            this.type = type;
        }

    }

    public enum AppendColumnExistedEnum{
        // 存在同名列时，跳过，不创建当前列
        CONTINUE,
        // 存在同名列时，不再新增剩下的待添加列
        BREAK,
        // 存在同名列时，抛出异常
        THROW
    }
}

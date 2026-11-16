package com.zhuofan.oms.service.workplan;

import com.alibaba.excel.EasyExcel;
import com.alibaba.excel.ExcelWriter;
import com.alibaba.excel.read.builder.ExcelReaderBuilder;
import com.alibaba.excel.write.metadata.WriteSheet;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.zhuofan.core.common.CommonFunc;
import com.zhuofan.core.common.CommonLogger;
import com.zhuofan.core.config.SysConfig;
import com.zhuofan.core.kafka.KafkaClient;
import com.zhuofan.oms.enums.workplan.ContentType;
import com.zhuofan.oms.orm.model.SecondPlanRecord;
import com.zhuofan.oms.mapper.*;
import com.zhuofan.oms.orm.model.*;
import com.zhuofan.oms.model.CrewSaturationModel;
import com.zhuofan.oms.model.WorkPlanDataModel;
import com.zhuofan.oms.controller.oms.WorkPlanController;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.pdfbox.Loader;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.rendering.PDFRenderer;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.openxml4j.util.ZipSecureFile;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xddf.usermodel.XDDFColor;
import org.apache.poi.xddf.usermodel.XDDFSolidFillProperties;
import org.apache.poi.xddf.usermodel.chart.*;
import org.apache.poi.xddf.usermodel.text.XDDFRunProperties;
import org.apache.poi.xddf.usermodel.text.XDDFTextBody;
import org.apache.poi.xddf.usermodel.text.XDDFTextParagraph;
import org.apache.poi.xssf.usermodel.*;
import org.apache.poi.xwpf.usermodel.*;
import org.jodconverter.core.document.DefaultDocumentFormatRegistry;
import org.jodconverter.core.office.OfficeException;
import org.jodconverter.local.LocalConverter;
import org.jodconverter.local.office.LocalOfficeManager;
import org.openxmlformats.schemas.drawingml.x2006.spreadsheetDrawing.CTMarker;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.CTColor;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.CTPageSetup;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.STOrientation;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.STSheetViewType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.DependsOn;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import javax.imageio.ImageIO;
import javax.servlet.http.HttpServletResponse;
import java.awt.image.BufferedImage;
import java.io.*;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.sql.Driver;
import java.text.Collator;
import java.text.SimpleDateFormat;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * 智慧安监 明日计划导出
 */
@Slf4j
@Getter
@DependsOn("flywayInitializer") // 等待flyway 迁移完成， postConstruct中需要查询数据
@Service
public class WorkPlanService {

    private String tempDir;

    private final LocalOfficeManager localOfficeManager;


    public WorkPlanService(@Autowired(required = false) LocalOfficeManager localOfficeManager) {
        this.localOfficeManager = localOfficeManager;
        if (localOfficeManager == null) {
            log.warn("jodconverter 为空");
        }
    }

    @Resource
    private SysConfig sysConfig;

    private final CommonLogger commonLogger=CommonLogger.getInstance();

    @Resource
    private CrewMemberMapper crewMemberMapper;

    @Resource
    private SecondPlanRecordMapper secondPlanRecordMapper;

    @Resource
    private TransformerSubstationMapper transformerSubstationMapper;

    @Resource
    private CrewMapper crewMapper;

    @Resource
    private WorkUnitMapper workUnitMapper;

    private final Map<String, String> departmentDColumnMap = new HashMap<>();
    private final Map<String, String> departmentO_PColumnMap = new HashMap<>();
    private Map<String, String> departmentApColumnMap;

    private List<CrewMember> crewMembers;

    private final DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ISO_LOCAL_DATE;
    private final DateTimeFormatter dateTimeFormatter1 = DateTimeFormatter.ofPattern("M月d日");

    private final Map<String, String> stationMap = new HashMap<String, String>(){{
        put("变一", "变一所");
        put("变二", "变二所");
        put("变三", "变三所");
    }};

    @PostConstruct
    public void init() {
        departmentDColumnMap.put("变电管理一所", "变一所");
        departmentDColumnMap.put("变电管理二所", "变二所");
        departmentDColumnMap.put("变电管理三所", "变三所");
        departmentDColumnMap.put("项目管理中心", "项目中心");
        departmentDColumnMap.put("基建部", "项目中心");

        departmentO_PColumnMap.put("自动化", "调度自动化");
        departmentO_PColumnMap.put("通信", "调度通信");

        departmentApColumnMap = new HashMap<>(departmentO_PColumnMap);
        departmentApColumnMap.put("二次智能运维班", "调度继保");

        crewMembers = this.flushCrewMembersWithSort();
        tempDir = sysConfig.getTempfilePath() + File.separator + "workplan";
    }

    public synchronized void process(LocalDate date, List<MultipartFile> files, String lastCellName) {
        if (CollectionUtils.isNotEmpty(files)) {
            saveFile(date, ContentType.CHECK, files);
        }
        // "管控"、”非管控“文件
        File[] checkFiles = getUploadFile(date, ContentType.CHECK);
        if (checkFiles.length == 0) {
            throw new RuntimeException("请上传" + ContentType.CHECK.getDesc() + "，对应日期：" + date.format(dateTimeFormatter));
        }
        List<WorkPlanDataModel> workPlanDataModels = transferExcel(checkFiles);
        this.crewMembers = this.flushCrewMembersWithSort();
        // 前置数据处理
        preDataHandler(date, workPlanDataModels);
        // 根据模板文件 初始化要下载的临时文件
        File templateFile = prepareModuleFile(date, workPlanDataModels);
        // 数据写入模板文件, 并设置列公式
        templateFile = dataHandler(date, templateFile, workPlanDataModels);
        // 后置 数据处理
        templateFile = postDataHandler(date, templateFile, workPlanDataModels);

        if (StringUtils.isNotBlank(lastCellName)) {
            transformerSubstationMapper.updateLastCellNameSafe(lastCellName);
        }
        // 获取处理后的临时文件 处理好的数据并导出
        exportExcel(date, templateFile, lastCellName);
    }

    /**
     * sheet 班组日计划作业数量 数据写入，柱形图数据源设置
     *
     * @param template 模板文件
     * @return 更新后的模板文件
     */
    private File crewDayPlanCountBarChartPlot(LocalDate date, File template) {
        try (
                XSSFWorkbook workbook = new XSSFWorkbook(template)
        ){
            // 班组人员数据
            List<String> crewSet = crewMembers.stream()
                    .map(CrewMember::getUnitCrewName)
                    .filter(e -> !e.contains("分部") && !"项目中心".equals(e))
                    .distinct()
                    .collect(Collectors.toList());

            // sheet "班组日计划数量"
            XSSFSheet sheet = workbook.getSheet("班组日计划作业数量");
            int startRow = 1;
            String formula = "COUNTIFS(智慧安监系统导出数据!AP:AP,A%d,智慧安监系统导出数据!AO:AO,\"%s\")";
            for (String crewName : crewSet) {
                Row row = sheet.getRow(startRow);
                if (row == null) {
                    row = sheet.createRow(startRow);
                }
                startRow++;
                Cell cell1 = Optional.ofNullable(row.getCell(0)).orElse(row.createCell(0));
                Cell cell2 = Optional.ofNullable(row.getCell(1)).orElse(row.createCell(1));
                Cell cell3 = Optional.ofNullable(row.getCell(2)).orElse(row.createCell(2));
                Cell cell4 = Optional.ofNullable(row.getCell(3)).orElse(row.createCell(3));
                Cell cell5 = Optional.ofNullable(row.getCell(4)).orElse(row.createCell(4));

                cell1.setCellValue(crewName);
                cell2.setCellFormula(formula.replace("%d", startRow + "").replace("%s", "可接受"));
                cell3.setCellFormula(formula.replace("%d", startRow + "").replace("%s", "低风险"));
                cell4.setCellFormula(
                        formula.replace("%d", startRow + "").replace("%s", "中风险") +
                                "+" +
                                formula.replace("%d", startRow + "").replace("%s", "高风险")
                );
                cell5.setCellFormula("B" + startRow + "+C" + startRow + "+D" + startRow);
            }

            // 获取图表，设置取数区域
            List<XSSFChart> charts = sheet.getDrawingPatriarch().getCharts();
            XSSFChart chart = charts.get(0);

//            // X轴
//            XDDFCategoryAxis xAxis = chart.createCategoryAxis(AxisPosition.BOTTOM);
//            // Y轴
//            XDDFValueAxis yAxis = chart.createValueAxis(AxisPosition.LEFT);
//            //bar：条形图
//            XDDFBarChartData bar = (XDDFBarChartData) chart.createData(ChartTypes.BAR, xAxis, yAxis);
//            bar.setBarDirection(BarDirection.COL); // COL柱状图  BAR条形图
//            bar.setBarGrouping(BarGrouping.STACKED);// 叠加柱形图
//            bar.setOverlap((byte)100);
//            yAxis.setCrossBetween(AxisCrossBetween.BETWEEN);
//            //分类轴标数据
//            String[] seriesRanges = new String[]{"B", "C", "D"};
//            // x轴数据
//            XDDFDataSource<String> xData = XDDFDataSourcesFactory.fromStringCellRange(sheet, CellRangeAddress.valueOf("A2:A"+crewPlanDayCountFirstRow));
//            for (String seriesRange : seriesRanges) {
//                // y轴数据
//                XDDFNumericalDataSource<Double> yData = XDDFDataSourcesFactory
//                        .fromNumericCellRange(sheet, CellRangeAddress.valueOf(seriesRange + "2:" + seriesRange + crewPlanDayCountFirstRow));
//                //图表加载数据，series 1
//                XDDFBarChartData.Series series = (XDDFBarChartData.Series) bar.addSeries(xData, yData);
//                series.setTitle(null, new CellReference(seriesRange+"1"));
//            }
//            chart.setTitleOverlay(true);
//            // 绘制
//            chart.plot(bar);

            List<XDDFChartData> chartSeries = chart.getChartSeries();
            XDDFChartData bar = chartSeries.get(0);
            // x轴 字体样式设置
            XDDFCategoryAxis categoryAxis = bar.getCategoryAxis();
            XDDFRunProperties xAxisProps = categoryAxis.getOrAddTextProperties();
            xAxisProps.setFontSize(10.0);

            //分类轴标数据
            String[] seriesRanges = new String[]{"B", "C", "D"};
            byte[][] seriesColors = new byte[3][3];
            seriesColors[0] = new byte[]{0, (byte)176, (byte)240};
            seriesColors[1] = new byte[]{(byte)255, (byte)192, 0};
            seriesColors[2] = new byte[]{(byte)255, 0 , 0};

            // x轴数据
            XDDFDataSource<String> xData = XDDFDataSourcesFactory.fromStringCellRange(sheet, CellRangeAddress.valueOf("A2:A"+startRow));

            for (int i = 0; i < seriesRanges.length; i++) {
                // y轴数据
                XDDFNumericalDataSource<Double> yData = XDDFDataSourcesFactory
                        .fromNumericCellRange(sheet, CellRangeAddress.valueOf(seriesRanges[i] + "2:" + seriesRanges[i] + startRow));
                //重置图表各个series数据
                XDDFChartData.Series series = bar.getSeries(i);
                series.replaceData(xData, yData);

                // 设置系列样式 柱的样式
                XDDFSolidFillProperties fill = new XDDFSolidFillProperties();
                byte[] seriesColor = seriesColors[i];
                fill.setColor(XDDFColor.from(seriesColor)); // 颜色填充
                series.setFillProperties(fill);
            }
            chart.setTitleOverlay(true);
            // 绘制
            chart.plot(bar);

            // 有效
//            CTChart ctChart = chart.getCTChart();
//            CTPlotArea plotArea = ctChart.getPlotArea();
//            List<CTBarChart> barChartList = plotArea.getBarChartList();
//            CTBarChart ctBarChart = barChartList.get(0);
//            //分类轴标数据
//            String[] seriesRanges = new String[]{"B", "C", "D"};
//            String[] seriesNames = new String[]{"可接受", "低风险", "中高风险"};
//            for (int i = 0; i < seriesRanges.length; i++) {
//                int finalI = i;
//                CTBarSer ctBarSer = ctBarChart.getSerList().stream()
//                        .filter(e -> Objects.equals(e.getTx().getStrRef().getStrCache().getPtList().get(0).getV(),seriesNames[finalI]))
//                        .findFirst()
//                        .orElseThrow(() -> new RuntimeException("GG"));
//                CTAxDataSource ctAxDataSource = ctBarSer.addNewCat();
//                CTStrRef strRef = ctAxDataSource.addNewStrRef();
//                strRef.setF("A2:A" + crewPlanDayCountFirstRow);
//
//                CTNumDataSource ctNumDataSource = ctBarSer.addNewVal();
//                CTNumRef ctNumRef = ctNumDataSource.addNewNumRef();
//                ctNumRef.setF(seriesRanges[i] + "2:" + seriesRanges[i] + crewPlanDayCountFirstRow);
//            }
//
//            // 自动调整图表大小
//            autoResizeChart(chart, crewPlanDayCountFirstRow - 1);
//            chart.getChartSeries().forEach(chart::plot);

            // sheet "总体概况" 中 图"生产班站作业计划数量" 数据区域重置
            // 变更 sheet "总体概况" 中 图"生产班站作业计划数量"名称
            XSSFSheet sheet1 = workbook.getSheet("总体概况");
            List<XSSFChart> charts1 = sheet1.getDrawingPatriarch().getCharts();
            XSSFChart crewPlanWorkNumChart = charts1.stream().filter(e -> "生产班站作业计划数量".equals(e.getTitleText().getString()))
                    .findFirst()
                    .orElseThrow(() -> new RuntimeException("“总体概况”sheet中不存在图：生产班站作业计划数量"));

            XDDFChartData xddfChartData = crewPlanWorkNumChart.getChartSeries().get(0);

            XDDFDataSource<String> xChartData = XDDFDataSourcesFactory.fromStringCellRange(sheet, CellRangeAddress.valueOf("A2:A" + startRow));
            for (int i = 0; i < xddfChartData.getSeriesCount(); i++) {
                XDDFChartData.Series series = xddfChartData.getSeries(i);
                XDDFNumericalDataSource<Double> yChartData = XDDFDataSourcesFactory.fromNumericCellRange(sheet, CellRangeAddress.valueOf(seriesRanges[i] + "2:" + seriesRanges[i] + startRow));
                series.replaceData(xChartData, yChartData);
            }
            crewPlanWorkNumChart.plot(xddfChartData);

            File temporaryFile = newTemporaryFile(date, "template2", "xlsm");
            workbook.write(Files.newOutputStream(temporaryFile.toPath()));
            return temporaryFile;
        } catch (IOException | InvalidFormatException e) {
            throw new RuntimeException(e);
        } finally {
            template.delete();
        }
    }

    /**
     * sheet 计划作业数量趋势 数据写入，面积图数据源设置
     *
     * @param template 模板文件
     * @return Excel文件
     */
    private File planNumChartPlot(LocalDate date, File template, long riskCount) {
        try (
                XSSFWorkbook workbook = new XSSFWorkbook(template)
        ){

            // 获取"计划作业数量趋势"当前季度数据
            List<LocalDateTime> timeRanges = calcSeasonRange(date.atStartOfDay());

            List<SecondPlanRecord> secondPlanRecords = secondPlanRecordMapper.selectList(Wrappers.<SecondPlanRecord>lambdaQuery()
                    .ge(SecondPlanRecord::getTime, timeRanges.get(0))
                    .lt(SecondPlanRecord::getTime, date.plusDays(1).atStartOfDay())
                    .orderByAsc(SecondPlanRecord::getTime)
            );
            Map<LocalDate, SecondPlanRecord> dateNumMap = secondPlanRecords.stream().collect(Collectors.toMap(e -> e.getTime().toLocalDate(), e -> e, (e1, e2) -> e2));
            // 追加本季度数据空
            Duration between = Duration.between(timeRanges.get(0), timeRanges.get(1));
            LocalDate recordDate = timeRanges.get(0).toLocalDate();
            // 最后时间多一天， 从1开始，抵消多的一天
            for (long i = 1; i < between.toDays(); i++) {
                SecondPlanRecord record = dateNumMap.get(recordDate);
                recordDate = recordDate.plusDays(1);
                if (record == null) {
                    record = new SecondPlanRecord();
                    record.setNum((short)0);
                    record.setTime(recordDate.atStartOfDay());
                    secondPlanRecords.add(record);
                }
            }
            // 按照时间排序
            secondPlanRecords = secondPlanRecords.stream().sorted(Comparator.comparing(SecondPlanRecord::getTime)).collect(Collectors.toList());

            XSSFCellStyle cellStyle = workbook.createCellStyle();
            cellStyle.setDataFormat(workbook.createDataFormat().getFormat("M月d日"));

            // sheet "计划作业数量趋势"
            XSSFSheet sheet = workbook.getSheet("计划作业数量趋势");
            int startRow = 2;
            for (SecondPlanRecord record : secondPlanRecords) {
                XSSFRow row = sheet.getRow(startRow);
                if (row == null) {
                    row = sheet.createRow(startRow);
                }
                startRow++;

                // 日期
                XSSFCell cell1 = Optional.ofNullable(row.getCell(0)).orElse(row.createCell(0));
                cell1.setCellStyle(cellStyle);
                cell1.setCellValue(record.getTime());
                // 数量
                XSSFCell cell2 = Optional.ofNullable(row.getCell(1)).orElse(row.createCell(1));
                cell2.setCellValue(record.getNum());
            }

            // 获取图表，设置取数区域
            List<XSSFChart> charts = sheet.getDrawingPatriarch().getCharts();
            XSSFChart chart = charts.get(1);
            String[] seasonChinese = {"一", "二", "三", "四"};
            String chartName = String.format("第%s季度二次管控作业计划数量趋势", seasonChinese[calcSeason(date.atStartOfDay()) - 1]);
            chart.setTitleText(chartName);
            List<XDDFChartData> chartSeries = chart.getChartSeries();
            XDDFChartData bar = chartSeries.get(0);

            //分类轴标数据
            // x轴数据 日期坐标轴
            XDDFNumericalDataSource<Double> xData = XDDFDataSourcesFactory
                    .fromNumericCellRange(sheet, CellRangeAddress.valueOf("A3:A"+startRow));
            // y轴数据
            XDDFNumericalDataSource<Double> yData = XDDFDataSourcesFactory
                    .fromNumericCellRange(sheet, CellRangeAddress.valueOf( "B3:B" + startRow));
            //重置图表各个series数据
            XDDFChartData.Series series = bar.getSeries(0);
            series.replaceData(xData, yData);

            // 设置系列样式 柱的样式
            XDDFSolidFillProperties fill = new XDDFSolidFillProperties();
            byte[] seriesColor = new byte[]{91, (byte)155, (byte)213};
            fill.setColor(XDDFColor.from(seriesColor)); // 颜色填充
            series.setFillProperties(fill);

            chart.setTitleOverlay(true);
            // 绘制
            chart.plot(bar);

            // sheet "总体概况" 中 图"季度二次作业计划数量趋势" 数据区域重置
            // 变更 sheet "总体概况" 中 图"季度二次作业计划数量趋势"名称
            XSSFSheet sheet1 = workbook.getSheet("总体概况");
            List<XSSFChart> charts1 = sheet1.getDrawingPatriarch().getCharts();
            XSSFChart planDailyNumInSeasonChart = charts1.stream().filter(e -> "第三季度二次作业计划\n数量趋势".equals(e.getTitleText().getString()))
                    .findFirst()
                    .orElseThrow(() -> new RuntimeException("“总体概况”sheet中不存在图：季度二次管控作业计划数量趋势"));
            // 设置图标题，换行导致出现多个Paragraph
            XDDFTextBody body = planDailyNumInSeasonChart.getTitle().getBody();
            XDDFTextParagraph textParagraph = body.getParagraph(0);
            textParagraph.setText(chartName);
            // 删除多余的标题章节
            int paragraphSize = body.getParagraphs().size();
            while(paragraphSize > 1) {
                body.removeParagraph(--paragraphSize);
            }
            textParagraph.getDefaultRunProperties().setBold(true);

//            // FIXME office excel 2021 不支持
//            CTDateAx dateAxis = planDailyNumInSeasonChart.getCTChart().getPlotArea().getDateAxArray(0);
//            CTNumFmt ctNumFmt = dateAxis.addNewNumFmt();
//            ctNumFmt.setFormatCode("m\"月\"d\"日\"");
//            // 设置 主要单位为月 1月 WPS支持 office excel不支持
//            dateAxis.addNewMajorTimeUnit().setVal(STTimeUnit.Enum.forInt(2));// 设置 主要单位为月
//            dateAxis.addNewMajorUnit().setVal(1.0); // 设置主要单位 1月
//
//            // 设置 次要单位为日 1日
//            dateAxis.addNewMinorTimeUnit().setVal(STTimeUnit.Enum.forInt(1)); // 设置 次要单位为日
//            dateAxis.addNewMinorUnit().setVal(1.0); // 设置 次要单位 1 日
            XDDFChartData xddfChartData = planDailyNumInSeasonChart.getChartSeries().get(0);
            series = xddfChartData.getSeries(0);
            XDDFNumericalDataSource<Double> xChartData = XDDFDataSourcesFactory.fromNumericCellRange(sheet, CellRangeAddress.valueOf("A3:A" + startRow));
            XDDFNumericalDataSource<Double> yChartData = XDDFDataSourcesFactory.fromNumericCellRange(sheet, CellRangeAddress.valueOf("B3:B" + startRow));
            series.replaceData(xChartData, yChartData);
            // 日期轴 文字方向 逆时针旋转90°
            planDailyNumInSeasonChart
                    .getCTChart()
                    .getPlotArea()
                    .getDateAxArray(0)
                    .getTxPr()
                    .getBodyPr()
                    // 1° 被分为60000份，因此val = 度数 * 60000，逆时针加负号
                    .setRot(-5400000);
            // 图 朝向 从左到右 从右到左
//            planDailyNumInSeasonChart.getAxes().get(0).setOrientation(AxisOrientation.MAX_MIN);

            // 设置高度
            // 获取所有图表 形状 位置
//            XSSFShape xssfShape = sheet1.getDrawingPatriarch().getShapes().get(0);
//            if (riskCount > 1) {
                List<XSSFShape> shapes = ((XSSFDrawing) planDailyNumInSeasonChart.getParent()).getShapes();
                for (XSSFShape shape : shapes) {
                    int offset = Math.max(0, Long.valueOf(riskCount).intValue() - 1);
                    if ("图表 3".equals(shape.getShapeName())) {
                        XSSFClientAnchor anchor = (XSSFClientAnchor) shape.getAnchor();
                        CTMarker anchorTo = anchor.getTo();
                        anchorTo.setRow(anchorTo.getRow() + offset);
                    }

                    if ("图表 13".equals(shape.getShapeName())) {
                        XSSFClientAnchor anchor = (XSSFClientAnchor) shape.getAnchor();
                        CTMarker from = anchor.getFrom();
                        from.setRow(from.getRow() + offset);
                        CTMarker to = anchor.getTo();
                        to.setRow(to.getRow() + offset);
                        //设置打印区域 !!! 重要
                        workbook.setPrintArea(workbook.getSheetIndex(sheet1), 0, 19, 0, to.getRow());
                    }
                }
//            }
            planDailyNumInSeasonChart.plot(xddfChartData);

            File temporaryFile = newTemporaryFile(date, "template3", "xlsm");
            OutputStream outputStream = Files.newOutputStream(temporaryFile.toPath());
            workbook.write(outputStream);
            return temporaryFile;
        } catch (IOException | InvalidFormatException e) {
            throw new RuntimeException(e);
        } finally {
            template.delete();
        }
    }

    /**
     * 创建新的临时文件（空），如果已存在，则删除原来的文件
     *
     * @param date 日期
     * @param filename 文件名称
     * @param suffix 文件后缀 如txt xls xlsx doc docx等
     * @return 创建的临时文件
     */
    private File newTemporaryFile(LocalDate date, String filename, String suffix) {
        try {
            String dateStr = date.format(dateTimeFormatter);
            // 文件加是否存在
            File dir = new File(this.tempDir + File.separator + dateStr.substring(0, 4) + File.separator + dateStr.substring(5) );
            log.info("创建的临时文件路径：" + dir.getPath());
            if (!dir.exists() && !dir.mkdirs()) {
                throw new RuntimeException(dir.getAbsolutePath() + "文件夹创建失败");
            }
            // 根据模板文件创建临时文件
            File temporary = new File(dir.getAbsolutePath() + File.separator + filename + "." + suffix);
            if (temporary.exists() && !temporary.delete()) {
                log.error("临时文件已经存在，删除操作失败");
            }
            if (!temporary.createNewFile()) {
                log.error("临时文件创建失败，路径：{}", temporary.getAbsolutePath());
            }
            if (!temporary.setWritable(true)) {
                log.error("临时文件写(write)操作权限设置失败，路径：:{}", temporary.getAbsolutePath());
            }
            if (!temporary.setReadable(true)) {
                log.error("临时文件读(read)操作权限设置失败，路径：:{}", temporary.getAbsolutePath());
            }
            return temporary;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public File[] getUploadFile(LocalDate date, ContentType type) {
        String format = date.format(dateTimeFormatter);
        File root = Paths.get(tempDir, format.substring(0, 4), format.substring(5)).toFile();
        File[] files;
        if (type != ContentType.RISK_ALARM
                && type != ContentType.WEEK_RISK
        ) {
            files = root.listFiles((dir, filename) -> {
                String suffix = filename.substring(filename.lastIndexOf(".") + 1);
                return Objects.equals(suffix, type.getSuffix()) && filename.contains(type.getType());
            });
        } else {
            root = Paths.get(tempDir, calcWeekYear(date) + "").toFile();
            files = root.listFiles((dir, fname) ->
                fname.equals(type.getType() + "_" + calcWeek(date) + "." + type.getSuffix())
            );
        }
        return files == null ? new File[0] : files;
    }
    /**
     * 准备模板文件
     */
    public File prepareModuleFile(LocalDate date, List<WorkPlanDataModel> workPlanDataModels) {
        // 读取模板文件
        ClassPathResource templateExcel = new ClassPathResource("workplan/template.xlsm");
        File temporaryFile = newTemporaryFile(date,"template1", "xlsm");

        try (
                // 自动关闭流
                InputStream templateInputStream = templateExcel.getInputStream();
                ExcelWriter writer = EasyExcel
                        .write(Files.newOutputStream(temporaryFile.toPath()))
                        .withTemplate(templateInputStream)
                        .build()
        ) {
            // 模板文件数据 写入 临时文件
            // sheet "班组人员基础数据"
            // 获取班组人员基础数据sheet
            WriteSheet crewMemberSheet = EasyExcel.writerSheet("班组人员基础数据")
                    .head(CrewMember.class)
                    .needHead(false)
                    .build();
            writer.write(crewMembers, crewMemberSheet);

            // sheet "班组统计"数据写入
            Map<String, Long> crewMemberCountMap = crewMembers.stream().collect(Collectors.groupingBy(CrewMember::getUnitCrewName, Collectors.counting()));
            Map<String, Long> crewManagerCountMap = crewMembers.stream().filter(e -> BooleanUtils.isTrue(e.getIsManager())).collect(Collectors.groupingBy(CrewMember::getUnitCrewName, Collectors.counting()));
            Workbook workbook = writer.writeContext().writeWorkbookHolder().getWorkbook();
            Sheet crewCountSheet = workbook.getSheet("班组统计");
            int crewCountFirstRow = 1;

            Set<String> crews = workPlanDataModels.stream().map(WorkPlanDataModel::getCalcRunningCrew).filter(StringUtils::isNotBlank).collect(Collectors.toSet());
            for (String crewName : crews) {
                Row row = crewCountSheet.createRow(crewCountFirstRow++);
                row.createCell(0).setCellValue(crewName);
                row.createCell(1).setCellValue(Optional.ofNullable(crewMemberCountMap.get(crewName)).orElse(0L));
                row.createCell(2).setCellValue(Optional.ofNullable(crewManagerCountMap.get(crewName)).orElse(0L));
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        // sheet "班组日计划作业数量" template2
        temporaryFile = crewDayPlanCountBarChartPlot(date, temporaryFile);

        long riskCount = workPlanDataModels.stream().filter(e -> !"验收".equals(e.getWorkType()) && ("中".equals(e.getRiskLevel()) || "高".equals(e.getRiskLevel()))).count();

        //sheet "计划作业数量趋势" 当前季度数据写入 template3
        temporaryFile = planNumChartPlot(date, temporaryFile, riskCount);

        return temporaryFile;
    }

    /**
     * 计算输入时间所在季度时间区间
     * 2024-02-11 12:52:00 [2024-01-01 00:00:00, 2024-04-01 00:00:00)
     * @param time 输入时间
     * @return time 所咋季度 时间区间
     */
    private List<LocalDateTime> calcSeasonRange(LocalDateTime time) {
        int year = time.getYear();
        int month = time.getMonthValue();

        int quarter = month / 3;
        int j = month % 3;

        if (j == 0) {
            quarter = quarter - 1;
        }

        List<LocalDateTime> list = new ArrayList<>();
        int i = 1 + 3 * quarter;
        LocalDateTime start = LocalDateTime.of(year, i, 1, 0, 0, 0);
        list.add(start);
        list.add(start.plusMonths(3));

        return list;
    }

    /**
     * 计算 time 所在季度
     * @param time 时间
     * @return 时间所在季度 1 ~ 4
     */
    private int calcSeason(LocalDateTime time) {
        int month = time.getMonthValue();
        return (month - 1) / 3 + 1;
    }

    /**
     * 数据写入 模板文件
     * @param workPlanDataModels 工作计划数据
     */
    private File dataHandler(LocalDate date, File moduleFile, List<WorkPlanDataModel> workPlanDataModels) {
        File temporaryFile = newTemporaryFile(date,"data", "xlsm");
        try (
                // 数据 写入 临时文件
                ExcelWriter writer = EasyExcel
                        .write(temporaryFile)
                        .withTemplate(moduleFile)
                        .build();
        ){
            WriteSheet writeSheet = EasyExcel
                    .writerSheet("智慧安监系统导出数据")
                    .head(WorkPlanDataModel.class)
                    .needHead(false)
                    // 设置列公式（风险值、实际本单位工作人数）
                    .registerWriteHandler(new WorkPlanEasyExcelFormulaWriteHandler())
                    .build();

            //设置强制计算公式：不然公式会以字符串的形式显示在excel中
            Workbook workbook = writer.writeContext().writeWorkbookHolder().getWorkbook();
//            workbook.createEvaluationWorkbook().clearAllCachedResultValues();
            // 设置强制计算公式：不然公式会以字符串的形式显示在excel中
            workbook.setForceFormulaRecalculation(true);
            writer.write(workPlanDataModels, writeSheet);
        } catch (Exception e) {
            throw new RuntimeException(e);
        } finally {
            moduleFile.delete();
        }

        return temporaryFile;
    }

    /**
     * 周风险 分析
     * @return 周风险
     */
    public WarnInfoPeerWeek weekRiskAnalysis(LocalDate date) {

        File[] uploadFile = getUploadFile(date, ContentType.WEEK_RISK);
        if (uploadFile.length == 0) {
            throw new RuntimeException("未找到对应周风险日历文件");
        }

        try (
                XWPFDocument document = new XWPFDocument(Files.newInputStream(uploadFile[0].toPath()))
        ) {
            List<XWPFTable> tables = document.getTables();
            // 周一：1
            int dayOfWeek = date.getDayOfWeek().getValue();
            WarnInfoPeerWeek infoPeerWeek = null;
            for (XWPFTable table : tables) {
                List<XWPFTableRow> rows = table.getRows();

                int size = rows.size();
                XWPFTableRow lastRow = rows.get(size - 2);
                if ("四级事件".equals(lastRow.getCell(0).getText())) {
                    infoPeerWeek = new WarnInfoPeerWeek();
                    infoPeerWeek.setTime(date.atStartOfDay());
                    XWPFTableRow row = rows.get(size - 5);
                    infoPeerWeek.setFirstLevelNum(row.getCell(dayOfWeek).getText());
                    row = rows.get(size - 4);
                    infoPeerWeek.setSecondLevelNum(row.getCell(dayOfWeek).getText());
                    row = rows.get(size - 3);
                    infoPeerWeek.setThirdLevelNum(row.getCell(dayOfWeek).getText());
                    row = rows.get(size - 2);
                    infoPeerWeek.setFourthLevelNum(row.getCell(dayOfWeek).getText());
                    return infoPeerWeek;
                }
            }
            return infoPeerWeek;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }

    /**
     * 计算 time 所在周
     * 2024-09-23 12:56:00 ===> [2024-09-23 00:00:00 2024-09-30 00:00:00)
     * @param time 输入时间
     * @return time 所在周
     */
    private List<LocalDateTime> calcWeekTimes(LocalDateTime time) {
        DayOfWeek dayOfWeek = time.getDayOfWeek();
        // 1~7
        int dayOfWeekIndex = dayOfWeek.getValue();

        int year = time.getYear();
        int month = time.getMonthValue();
        int dayOfMonth = time.getDayOfMonth();

        List<LocalDateTime> list = new ArrayList<>();
        list.add(LocalDateTime.of(year, month, dayOfMonth - dayOfWeekIndex + 1, 0, 0, 0));
        list.add(LocalDateTime.of(year, month, dayOfMonth +  8 - dayOfWeekIndex, 0, 0, 0));

        return list;
    }

    /**
     * 前置数据处理
     */
    private void preDataHandler(LocalDate date, List<WorkPlanDataModel> workPlanDataModels) {
        // 中高风险 本单位、外单位检查
        middleAndHighLevelRiskCheck(date, workPlanDataModels);
        Map<Long, String> unitMap = workUnitMapper.selectList(Wrappers.emptyWrapper()).stream().collect(Collectors.toMap(WorkUnit::getId, WorkUnit::getShortName));
        List<TransformerSubstation> transformerSubstations = transformerSubstationMapper.selectList(Wrappers.emptyWrapper());
        Map<Long, Crew> crewMap = crewMapper.selectList(Wrappers.emptyWrapper()).stream().collect(Collectors.toMap(Crew::getId, e -> e, (e1, e2) -> e2));
        // 部门、运行班组列计算
        for (int i = 0; i < workPlanDataModels.size(); i++) {

            WorkPlanDataModel workPlanDataModel = workPlanDataModels.get(i);
            // 实际本单位工作人数
            workPlanDataModel.setActualLocalUnitWorkerNum(("本单位".equals(workPlanDataModel.getWorkMajor()) ? workPlanDataModel.getWorkerNum() : 0) + "");

            // 作业类型 安装、更换作业 变为 安装
            String workType = workPlanDataModel.getWorkType();
            if (workType.contains("安装")) {
                workPlanDataModel.setWorkType("安装");
            }

            if (StringUtils.isNotBlank(workPlanDataModel.getCalcRunningCrew()) && StringUtils.isNotBlank(workPlanDataModel.getManageDepartment())) {
                continue;
            }

            // 计算管理部门，需要先计算AP列运行班组
            String calcManageDepartment = calcManageDepartment(workPlanDataModel, null);
            // 计算运行班组
            String calcRunningClass = calcRunningClass(workPlanDataModel);
            if (StringUtils.isBlank(calcManageDepartment)) {
                // 计算管理部门，需要先计算AP列运行班组
                calcManageDepartment = calcManageDepartment(workPlanDataModel, calcRunningClass);
            }

            // 根据工作地点计算 管理部门、运行班组
            if (calcRunningClass == null || calcManageDepartment == null) {
                // 工作地点
                String workLocation = workPlanDataModel.getWorkLocation();

                boolean match;
                for (TransformerSubstation substation : transformerSubstations) {
                    if (StringUtils.isBlank(substation.getMatchPattern())) {
                        String substationName = substation.getName();
                        match = workLocation.contains(substationName);
                    } else {
                        Pattern pattern = Pattern.compile(substation.getMatchPattern());
                        Matcher matcher = pattern.matcher(workLocation);
                        match = matcher.matches();
                    }
                    if (match) {
                        calcManageDepartment = StringUtils.isBlank(calcManageDepartment) ? unitMap.get(substation.getUnitId()) : calcManageDepartment;
                        // FIXME 变一应为变一所
                        if (stationMap.containsKey(calcManageDepartment)) {
                            calcManageDepartment = stationMap.get(calcManageDepartment);
                        }
                        Crew crew = crewMap.get(calcManageDepartment.contains("通信") ? substation.getCrewTxId() : substation.getCrewJbId());
                        if (crew == null) {
                            throw new RuntimeException("当前站点：" + substation.getName() + "未配置负责班组");
                        }

                        calcRunningClass = calcManageDepartment.contains("通信") ? crew.getName() : unitMap.get(substation.getUnitId()) + crew.getShortName();
                    }
                }
            }

            if (StringUtils.isAnyBlank(calcRunningClass, calcManageDepartment)) {
                throw new RuntimeException("作业计划编号：" + workPlanDataModel.getWorkPlanCode() + "，管理部门(AN)或计算运行班组(AP)计算异常，请检查（或手动补充）后再上传，");
            }

            // 管理部门是项目中心，运行班组也是项目中心
            if ("项目中心".equals(calcManageDepartment)) {
                calcRunningClass = "项目中心";
            }

            workPlanDataModel.setManageDepartment(calcManageDepartment);
            workPlanDataModel.setCalcRunningCrew(calcRunningClass);

        }
    }

    /**
     * 后置数据处理
     * sheet 饱和度计算
     * sheet 总体概况
     *
     *
     * @param file 已写入数据的excel
     * @param workPlanDataModels 智慧安监数据
     */
    private File postDataHandler(LocalDate date, File file, List<WorkPlanDataModel> workPlanDataModels) {
        // sheet 饱和度计算
        // 班组---成员数量 Map
        Map<String, Long> crewMemberCountMap = crewMembers.stream().collect(Collectors.groupingBy(CrewMember::getUnitCrewName, Collectors.counting()));
        // 班组---负责人数量 Map
        Map<String, Long> crewManagerCountMap = crewMembers.stream().filter(e -> BooleanUtils.isTrue(e.getIsManager())).collect(Collectors.groupingBy(CrewMember::getUnitCrewName, Collectors.counting()));

        // 同班组 计划总计 Map
        Map<String, Long> sameCrewPlanCountMap = workPlanDataModels.stream().filter(e -> "本单位".equals(e.getWorkMajor())).collect(Collectors.groupingBy(WorkPlanDataModel::getCalcRunningCrew, Collectors.counting()));
        // 同班组 计划需要工作人数 Map
        Map<String, Long> sameCrewPlanNeedWorkerMap = workPlanDataModels.stream().collect(Collectors.groupingBy(WorkPlanDataModel::getCalcRunningCrew, Collectors.summingLong(e -> Long.parseLong(e.getActualLocalUnitWorkerNum()))));

        // 为了班组有顺序
        List<String> crews = crewMembers.stream().map(CrewMember::getUnitCrewName).filter(e -> !e.contains("分部") && !e.contains("项目中心")).distinct().collect(Collectors.toList());
        List<CrewSaturationModel> list = new ArrayList<>();
        // 拼装数据
        for (String crew : crews) {
            CrewSaturationModel model = new CrewSaturationModel();
            model.setCrewName(crew);
            long managerNum = Optional.ofNullable(sameCrewPlanCountMap.get(crew)).orElse(0L);
            model.setWorkNeedManagerNum(managerNum);
            long workerNum = Optional.ofNullable(sameCrewPlanNeedWorkerMap.get(crew)).orElse(0L);
            model.setWorkNeedWorkerNum(workerNum);

            long crewManagerNum = Optional.ofNullable(crewManagerCountMap.get(crew)).orElse(0L);
            model.setCrewManagerSaturation(((managerNum == 0 || crewManagerNum == 0) ? BigDecimal.ZERO : BigDecimal.valueOf(managerNum).divide(BigDecimal.valueOf(crewManagerNum), 2, RoundingMode.HALF_UP)));
            long crewWorkerNum = Optional.ofNullable(crewMemberCountMap.get(crew)).orElse(0L);
            model.setCrewWorkerSaturation((workerNum == 0 || crewWorkerNum == 0 ? BigDecimal.ZERO : BigDecimal.valueOf(workerNum).divide(BigDecimal.valueOf(crewWorkerNum), 2, RoundingMode.HALF_UP)));
            list.add(model);
        }

        // FIXME 注释 0.08
        ZipSecureFile.setMinInflateRatio(-0.1d);
        try (
                XSSFWorkbook workbook = new XSSFWorkbook(file)
        ) {
            XSSFSheet sheet = workbook.getSheet("饱和度计算");
            // 写入数据
            int startRow = 1;
            // 百分比展示
            XSSFCellStyle cellStyle = workbook.createCellStyle();
            cellStyle.setDataFormat(9);
            // 行偏移
            sheet.shiftRows(2, sheet.getLastRowNum(), list.size() - 1);
            for (CrewSaturationModel model : list) {
                XSSFRow row = sheet.getRow(startRow) == null ? sheet.createRow(startRow) : sheet.getRow(startRow);
                startRow++;

                // 班组名称
                Optional.ofNullable(row.getCell(0))
                        .orElse(row.createCell(0))
                        .setCellValue(model.getCrewName());

                // 工作任务数量（所需工作负责人数量）
                Optional.ofNullable(row.getCell(1))
                        .orElse(row.createCell(1))
                        .setCellValue(model.getWorkNeedManagerNum());

                // 所需作业人员数
                Optional.ofNullable(row.getCell(2))
                        .orElse(row.createCell(2)).setCellValue(model.getWorkNeedWorkerNum());

                // 班组日作业饱和度（工作负责人）
                XSSFCell cell3 = Optional.ofNullable(row.getCell(3))
                        .orElse(row.createCell(3));
                cell3.setCellStyle(cellStyle);
                cell3.setCellValue(model.getCrewManagerSaturation().doubleValue());

                // 日作业饱和度（作业人员）
                XSSFCell cell4 = Optional.ofNullable(row.getCell(4))
                        .orElse(row.createCell(4));
                cell4.setCellStyle(cellStyle);
                cell4.setCellValue(model.getCrewWorkerSaturation().doubleValue());
            }

            // sheet "总体概况"
            XSSFSheet chartSheet = workbook.getSheet("总体概况");
            List<XSSFChart> charts = chartSheet.getDrawingPatriarch().getCharts();
            // 图”班组饱和度情况” 数据区域设置
            XSSFChart crewSaturationChart = charts.stream().filter(e -> "班组饱和度情况".equals(e.getTitleText().getString()))
                    .findFirst()
                    .orElseThrow(() -> new RuntimeException("“总体概况”sheet中不存在图：班组饱和度情况"));

            List<XDDFChartData> chartSeries = crewSaturationChart.getChartSeries();
            XDDFChartData chartData = chartSeries.get(0);
            XDDFCategoryDataSource xData = XDDFDataSourcesFactory.fromStringCellRange(sheet, CellRangeAddress.valueOf("A2:A" + startRow));

            String[] cellRanges = {"D", "E"};

            for (int i = 0; i < cellRanges.length; i++) {
                XDDFChartData.Series series = chartData.getSeries(i);
                XDDFNumericalDataSource<Double> yData = XDDFDataSourcesFactory.fromNumericCellRange(sheet, CellRangeAddress.valueOf(cellRanges[i] + "2:" + cellRanges[i] + startRow));
                series.replaceData(xData, yData);
            }
            // 绘制
            crewSaturationChart.plot(chartData);

            // A1添加日期信息
            XSSFCell A1 = chartSheet.getRow(0).getCell(0);
//            A1.setCellValue(date.plusDays(1L).format(dateTimeFormatter1) + A1.getStringCellValue());
            A1.setCellValue(date.format(dateTimeFormatter1) + A1.getStringCellValue());

            // N22添加日期信息
            XSSFCell N22 = chartSheet.getRow(21).getCell(13);
//            N22.setCellValue(date.plusDays(1L).format(dateTimeFormatter1) + N22.getStringCellValue());
            N22.setCellValue(date.format(dateTimeFormatter1) + N22.getStringCellValue());

            // N42添加日期信息
            XSSFCell N42 = chartSheet.getRow(41).getCell(13);
//            N42.setCellFormula("\"" + date.plusDays(1L).format(dateTimeFormatter1) + "\"&" + N42.getCellFormula());
            N42.setCellFormula("\"" + date.format(dateTimeFormatter1) + "\"&" + N42.getCellFormula());

            // A22 写入今日 事件风险
            // 每周风险预警信息写入 sheet 总体概况 A22
//            WarnInfoPeerWeek warnInfoPeerWeek = weekRiskAnalysis(date.plusDays(1L));
            WarnInfoPeerWeek warnInfoPeerWeek = weekRiskAnalysis(date);
            if (warnInfoPeerWeek == null) {
                throw new RuntimeException("周风险日历解析失败");
            }
            String message = warnInfoPeerWeek.message();
            Row row = chartSheet.getRow(21);
            row.getCell(0).setCellValue(message);

            // 中高风险项 写入sheet "总体概况" 49 行开始
            Map<WorkPlanDataModel, String> collectMap = workPlanDataModels.stream().filter(e -> !"验收".equals(e.getWorkType()) && ("中".equals(e.getRiskLevel()) || "高".equals(e.getRiskLevel()))).collect(Collectors.toMap(e -> e, e -> "", (e1,e2) -> e2));
            Map<WorkPlanDataModel, String> map = analysisSecondCountFocus(date, false);
            int firstRowNum = 48;
            XSSFCellStyle cellStyle1 = workbook.createCellStyle();
            XSSFFont font = workbook.createFont();
            font.setFontName("宋体");
            font.setFontHeightInPoints((short)20);
            cellStyle1.setFont(font);
            cellStyle1.setWrapText(true);
            cellStyle1.setBorderTop(BorderStyle.THIN);
            cellStyle1.setBorderBottom(BorderStyle.THIN);
            cellStyle1.setBorderLeft(BorderStyle.THIN);
            cellStyle1.setBorderRight(BorderStyle.THIN);
            cellStyle1.setAlignment(HorizontalAlignment.CENTER);
            cellStyle1.setVerticalAlignment(VerticalAlignment.CENTER);
            for (WorkPlanDataModel model : collectMap.keySet()) {
                Row row1 = chartSheet.createRow(firstRowNum);
                // 设置行高
                row1.setHeightInPoints(210.0f);
                firstRowNum++;
                Optional.ofNullable(row1.getCell(0)).orElse(row1.createCell(0)).setCellValue(model.getProjectManageUnit());
                Optional.ofNullable(row1.getCell(1)).orElse(row1.createCell(1)).setCellValue(model.getWorkContent());
                Optional.ofNullable(row1.getCell(2)).orElse(row1.createCell(2)).setCellValue(model.getWorkLocation());
                Optional.ofNullable(row1.getCell(3)).orElse(row1.createCell(3)).setCellValue(model.getWorkMajor());
                Optional.ofNullable(row1.getCell(4)).orElse(row1.createCell(4)).setCellValue(model.getWorkType());
                Optional.ofNullable(row1.getCell(5)).orElse(row1.createCell(5)).setCellValue(model.getRiskLevel());
                Optional.ofNullable(row1.getCell(6)).orElse(row1.createCell(6)).setCellValue(model.getProspectMode());
                Cell startTimeCell = Optional.ofNullable(row1.getCell(7)).orElse(row1.createCell(7));
                startTimeCell.setCellValue(model.getPlanStartTime().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));

                Cell endTimeCell = Optional.ofNullable(row1.getCell(8)).orElse(row1.createCell(8));
                endTimeCell.setCellValue(model.getPlanEndTime().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));

                Optional.ofNullable(row1.getCell(9)).orElse(row1.createCell(9)).setCellValue(model.getWorkProfession());
                Optional.ofNullable(row1.getCell(10)).orElse(row1.createCell(10)).setCellValue(model.getWorkStatus());
                Optional.ofNullable(row1.getCell(11)).orElse(row1.createCell(11)).setCellValue(model.getWorkCrew());
                Optional.ofNullable(row1.getCell(12)).orElse(row1.createCell(12)).setCellValue(model.getWorkResponsiblePerson());
                Optional.ofNullable(row1.getCell(13)).orElse(row1.createCell(13)).setCellValue(model.getFirstResponsiblePersonInScene());
                Optional.ofNullable(row1.getCell(14)).orElse(row1.createCell(14)).setCellValue(model.getPlanType());
                Optional.ofNullable(row1.getCell(15)).orElse(row1.createCell(15)).setCellValue(model.getWorkMode());
                Optional.ofNullable(row1.getCell(16)).orElse(row1.createCell(16)).setCellValue(model.getNeedVideoSurveillance());

                WorkPlanDataModel key = map.keySet().stream().filter(e -> Objects.equals(e.getWorkPlanCode(), model.getWorkPlanCode()) || Objects.equals(e.getWorkContent(), model.getWorkContent())).findFirst().orElse(null);
                Optional.ofNullable(row1.getCell(17)).orElse(row1.createCell(17)).setCellValue(map.get(key));
                setSameRowCellsStyle(row1, cellStyle1);
            }
            // 设置打印参数，为导出Image做准备
            XSSFPrintSetup printSetup = chartSheet.getPrintSetup();
            // 设置边距
            printSetup.setLeftMargin(0.05);
            printSetup.setTopMargin(0.05);
            printSetup.setBottomMargin(0.05);
            // 设置缩放 40%
            printSetup.setScale((short)40);
//            printSetup.setPaperSize(PaperSize.A4_PAPER);
            printSetup.setOrientation(PrintOrientation.LANDSCAPE);
            CTPageSetup pageSetup = chartSheet.getCTWorksheet().getPageSetup();
            pageSetup.setOrientation(STOrientation.Enum.forString("landscape"));
            pageSetup.setPaperHeight("400mm");
            // 高度自定义 根据中高风险数
            pageSetup.setPaperWidth(280 + 25 * Math.max(0, collectMap.keySet().size() - 1) + "mm");

            File temporaryFile = newTemporaryFile(date, "data1", "xlsm");
            workbook.setForceFormulaRecalculation(true);
            // 计算formula cell单元格，避免libreoffice不自动计算formula
            FormulaEvaluator evaluator = workbook.getCreationHelper().createFormulaEvaluator();
            evaluator.evaluateAll();
            workbook.write(Files.newOutputStream(temporaryFile.toPath()));
            return temporaryFile;
        } catch (IOException | InvalidFormatException e) {
            throw new RuntimeException(e);
        } finally {
            file.delete();
        }
    }

    /**
     * 导出处理结果
     */
    private void exportExcel(LocalDate date, File file, String lastCellName) {
        File tomorrowWorkPlanFile = newTemporaryFile(date,
                ContentType.EXPORT_TOMORROW.getType(),
                ContentType.EXPORT_TOMORROW.getSuffix());
        ClassPathResource templateExcel = new ClassPathResource("workplan/tomorrow-template.xlsx");
        // elink 发送信息
        List<String> exportMessage = new ArrayList<>(3);
        try {
            Files.copy(templateExcel.getInputStream(), tomorrowWorkPlanFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
        } catch (Exception e) {
            commonLogger.error("临时文件创建失败");
            throw new RuntimeException(e);
        }

        // 主调文件
        File[] masterFiles = getUploadFile(date, ContentType.MASTER);
        if (masterFiles.length == 0) {
            throw new RuntimeException("请先上传主调文件，对应日期：" + date.format(dateTimeFormatter));
        }

        // 配调文件
        File[] subFiles = getUploadFile(date, ContentType.SUB);
        if (subFiles.length == 0) {
            throw new RuntimeException("请先上传配调文件，对应日期：" + date.format(dateTimeFormatter));
        }

        try (
                XSSFWorkbook originWorkbook = new XSSFWorkbook(Files.newInputStream(file.toPath()));
                XSSFWorkbook targetWorkbook = new XSSFWorkbook(Files.newInputStream(tomorrowWorkPlanFile.toPath()));
                XSSFWorkbook masterWorkbook = new XSSFWorkbook(Files.newInputStream(masterFiles[0].toPath()));
                XSSFWorkbook subWorkbook = new XSSFWorkbook(Files.newInputStream(subFiles[0].toPath()));
        ){
            // sheet1:主调
            // 主调计划数量
            Integer masterPlanCount = masterPlanAnalysis(masterWorkbook.getSheetAt(masterWorkbook.getFirstVisibleTab()), targetWorkbook.getSheet("主调"));

            // sheet2:配调
            // [配网计划检修项数量, 配网计划转电方式单数量, 带电作业单数量]
            int[] subData = subPlanAnalysis(date, subWorkbook.getSheetAt(subWorkbook.getFirstVisibleTab()), targetWorkbook.getSheet("配调"));

            // sheet "二次统计" 12行追加
            String append = String.format("共有调度计划工作%d项，其中主网停电复电计划%d项，配网计划检修%d项，配网计划转电方式单%d项，带电作业单%d项。",
                    masterPlanCount+subData[0]+subData[1]+subData[2],
                    masterPlanCount,
                    subData[0],
                    subData[1],
                    subData[2]
            );

            // sheet3:二次
            XSSFSheet originSheet = originWorkbook.getSheet("智慧安监系统导出数据");
            XSSFSheet targetSheet = targetWorkbook.getSheet("二次");

            // 今日作业数量记录
            SecondPlanRecord secondPlanRecord = secondPlanRecordMapper.selectOne(Wrappers.<SecondPlanRecord>lambdaQuery().eq(SecondPlanRecord::getTime, date.atStartOfDay()));
            if (secondPlanRecord != null) {
                secondPlanRecord.setMainNum(masterPlanCount.shortValue());
                secondPlanRecord.setSubNum((short) Arrays.stream(subData).sum());
                secondPlanRecordMapper.updateById(secondPlanRecord);
            } else {
                secondPlanRecord = new SecondPlanRecord();
                secondPlanRecord.setTime(date.atStartOfDay());
                secondPlanRecord.setNum((short) (originSheet.getLastRowNum() + 1));
                secondPlanRecord.setMainNum(masterPlanCount.shortValue());
                secondPlanRecord.setSubNum((short) Arrays.stream(subData).sum());
                secondPlanRecordMapper.insert(secondPlanRecord);
            }

            XSSFCell A1 = targetSheet.getRow(0).getCell(0);
//            A1.setCellValue(date.plusDays(1L).format(DateTimeFormatter.ofPattern("yyyy年MM月dd日")) + A1.getStringCellValue());
            A1.setCellValue(date.format(DateTimeFormatter.ofPattern("yyyy年MM月dd日")) + A1.getStringCellValue());


            XSSFCellStyle cellStyle = targetWorkbook.createCellStyle();
            cellStyle.setAlignment(HorizontalAlignment.CENTER);
            cellStyle.setVerticalAlignment(VerticalAlignment.CENTER);
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            copySheetSingleRange(originSheet, new CellRangeAddress(1, originSheet.getLastRowNum(), 0, 39), targetSheet, new CellRangeAddress(2, originSheet.getLastRowNum(), 0, 39), (originRow, targetRow) -> {
                // 设置行 样式
//                targetRow.setRowStyle(cellStyle); // 无效
                // 日期类型数据 转换成 字符串类型
                for (short i = 0; i < targetRow.getLastCellNum(); i++) {
                    targetRow.getCell(i).setCellStyle(cellStyle);
                    if (originRow.getCell(i).getCellType() == CellType.NUMERIC) {
                        Date cellDate = originRow.getCell(i).getDateCellValue();
                        targetRow.getCell(i).setCellValue(dateFormat.format(cellDate));
                    }
                }
            });
            // 写入
            targetWorkbook.write(Files.newOutputStream(tomorrowWorkPlanFile.toPath()));

            // sheet4:二次（打印版）
            XSSFSheet originSheet1 = targetWorkbook.getSheet("二次");
            targetSheet = targetWorkbook.getSheet("二次（打印版）");

            // 二次（打印版）最后一列名称覆盖
            if (StringUtils.isBlank(lastCellName)) {
                lastCellName = transformerSubstationMapper.selectLastCellName();
            }
            targetSheet.getRow(0).getCell(8).setCellValue(lastCellName);

            Map<String, TransformerSubstation> transformerSubstationMap = transformerSubstationMapper.selectList(Wrappers.emptyWrapper()).stream().collect(Collectors.toMap(TransformerSubstation::getName, e->e, (e1, e2) -> e2));
            Map<String, String> unitShorNameMap = workUnitMapper.selectList(Wrappers.emptyWrapper()).stream().collect(Collectors.toMap(WorkUnit::getUnit, WorkUnit::getShortName, (e1, e2) -> e2));

            // 复制
            List<CellRangeAddress> originCellRanges = new ArrayList<>();
            int lastRowNum = originSheet1.getLastRowNum() + 1; // 基于0，要加1
            originCellRanges.add(CellRangeAddress.valueOf("A3:" + "A" + lastRowNum));
            originCellRanges.add(CellRangeAddress.valueOf("E3:" + "I" + lastRowNum));
            originCellRanges.add(CellRangeAddress.valueOf("T3:T" + lastRowNum));
            originCellRanges.add(CellRangeAddress.valueOf("AN3:AN" + lastRowNum));

            List<CellRangeAddress> targetCellRanges = new ArrayList<>();
            targetCellRanges.add(new CellRangeAddress(1, originSheet1.getLastRowNum(), 0, 0));
            targetCellRanges.add(new CellRangeAddress(1, originSheet1.getLastRowNum(), 1, 5));
            targetCellRanges.add(new CellRangeAddress(1, originSheet1.getLastRowNum(), 6, 6));
            targetCellRanges.add(new CellRangeAddress(1, originSheet1.getLastRowNum(), 7, 7));

            XSSFFont font = targetWorkbook.createFont();
            font.setFontName("Arial"); // 设置字体为 Arial
            font.setFontHeightInPoints((short) 10); // 设置字体大小
            font.setBold(false); // 设置为非加粗
            font.setColor(IndexedColors.BLACK.getIndex()); // 设置字体颜色为黑色
            // 居中 自动换行 边框
            XSSFCellStyle wrapTextTargetCellStyle = targetWorkbook.createCellStyle();
            // 边框设置
            wrapTextTargetCellStyle.setBorderTop(BorderStyle.THIN);
            wrapTextTargetCellStyle.setBorderRight(BorderStyle.THIN);
            wrapTextTargetCellStyle.setBorderBottom(BorderStyle.THIN);
            wrapTextTargetCellStyle.setBorderLeft(BorderStyle.THIN);
            // 自动换行
            wrapTextTargetCellStyle.setWrapText(true);
            // 水平居中
            wrapTextTargetCellStyle.setAlignment(HorizontalAlignment.CENTER);
            // 垂直居中
            wrapTextTargetCellStyle.setVerticalAlignment(VerticalAlignment.CENTER);
            wrapTextTargetCellStyle.setFont(font);

            // 添加 黄色 背景色
            XSSFCellStyle yellowBackgroundTargetCellStyle = targetWorkbook.createCellStyle();
            yellowBackgroundTargetCellStyle.cloneStyleFrom(wrapTextTargetCellStyle);
            yellowBackgroundTargetCellStyle.setFont(font);
            // 背景色
            yellowBackgroundTargetCellStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
            yellowBackgroundTargetCellStyle.setFillForegroundColor(IndexedColors.YELLOW.getIndex());
//            Map<WorkPlanDataModel, String> riskMap = new HashMap<>();
//            List<WorkPlanDataModel> riskSorts = new ArrayList<>();
            List<String> riskMessages = new ArrayList<>();
            Pattern pattern = Pattern.compile("\\d*kV(.*?)(?:变电站|站).*");
            // 关注点信息拼接
            Map<WorkPlanDataModel, String> riskMessageMap = analysisSecondCountFocus(date, true);
            Set<WorkPlanDataModel> workPlanDataModels = riskMessageMap.keySet();
//            Pattern contentPattern = Pattern.compile("(【[^【】]*】)");
            copyRowAndSort(originSheet1, originCellRanges, targetSheet, targetCellRanges, (originRow, targetRow) -> {
                int originCellRowIndex = targetRow.getRowNum();

                String planCode = targetRow.getCell(0).getStringCellValue();
                // 填充序号列
                Optional.ofNullable(targetRow.getCell(0)).orElse(targetRow.createCell(0)).setCellValue(originCellRowIndex);

                if (originCellRowIndex == 0 && "工作单位名称".equals(targetRow.getCell(1).getStringCellValue())) {
                    targetRow.getCell(1).setCellValue("工作单位");
                    return;
                }

                // 工作单位 换成 简称
                String company = targetRow.getCell(1).getStringCellValue();
                for (Map.Entry<String, String> entry : unitShorNameMap.entrySet()) {
                    if (entry.getKey().contains(company) || company.contains(entry.getKey())) {
                        targetRow.getCell(1).setCellValue(entry.getValue());
                        break;
                    }
                }
//                String shortName = unitShorNameMap.get(targetRow.getCell(1).getStringCellValue());
//                targetRow.getCell(1).setCellValue(shortName);

                // 新增 是否13+16 列
                Cell newCell = targetRow.createCell(targetRow.getLastCellNum());
                Cell workPlaceCell = targetRow.getCell(3);
                // 去除工作地点 空格
                Matcher matcher = pattern.matcher(workPlaceCell.getStringCellValue().replaceAll(" ", ""));
                if (matcher.matches()) {
                    TransformerSubstation transformerSubstation = transformerSubstationMap.get(matcher.group(1));
                    if (transformerSubstation != null) {
//                        throw new RuntimeException("未查询到" + workPlaceCell.getStringCellValue() + "对应的站点是否13+16信息");
//                    } else {
                        newCell.setCellValue(BooleanUtils.isTrue(transformerSubstation.getIsThirteenAddSixteen()) ? "是" : StringUtils.EMPTY);
                    }
                }

                // 设置自动换行
//                targetRow.setRowStyle(wrapTextTargetCellStyle); // 无效
                setSameRowCellsStyle(targetRow, wrapTextTargetCellStyle);

//                // 工作内容列 含有 “【关键回路】”、“【同屏运行】” 标黄整行
//                Cell workContentCell = targetRow.getCell(2);
//                if (workContentCell.getCellType() == CellType.STRING) {
//                    String workContent = workContentCell.getStringCellValue();
//                    if (workContent != null && (workContent.contains("【关键回路】") || workContent.contains("【同屏运行】"))) {
////                        targetRow.setRowStyle(yellowBackgroundTargetCellStyle);// 无效
//                        setSameRowCellsStyle(targetRow, yellowBackgroundTargetCellStyle);
//                        String risk = targetRow.getCell(1).getStringCellValue() +
//                                "(" +
//                                targetRow.getCell(6) + "风险" +
//                                ")" +
//                                targetRow.getCell(3).getStringCellValue() + "：" +
//                                targetRow.getCell(2).getStringCellValue()
//                                + "。"
//                                ;
//                        WorkPlanDataModel workPlanDataModel = new WorkPlanDataModel();
//                        workPlanDataModel.setWorkPlanCode(planCode);
//                        workPlanDataModel.setWorkContent(targetRow.getCell(2).getStringCellValue());
//                        riskMap.put(workPlanDataModel, risk);
//                        if (!riskSorts.contains(workPlanDataModel)) {
//                            riskSorts.add(workPlanDataModel);
//                        }
//                    }
//                }
//
//                // 风险等级 >= 中风险
//                Cell riskCell = targetRow.getCell(6);
//                if (workContentCell.getCellType() == CellType.STRING) {
//                    String riskVal = riskCell.getStringCellValue();
//                    if (!"低".equals(riskVal) && !"可接受".equals(riskVal)) {
//                        String risk = targetRow.getCell(1).getStringCellValue() +
//                                "(" +
//                                targetRow.getCell(6) + "风险" +
//                                ")" +
//                                targetRow.getCell(3).getStringCellValue() + "：" +
//                                targetRow.getCell(2).getStringCellValue()
//                                + "。"
//                                ;
//                        WorkPlanDataModel workPlanDataModel = new WorkPlanDataModel();
//                        workPlanDataModel.setWorkPlanCode(planCode);
//                        workPlanDataModel.setWorkContent(targetRow.getCell(2).getStringCellValue());
//                        riskMap.put(workPlanDataModel, risk);
//                        if (!riskSorts.contains(workPlanDataModel)) {
//                            riskSorts.add(workPlanDataModel);
//                        }
//                    }
//                }

                WorkPlanDataModel workPlanDataModel = workPlanDataModels.stream().filter(e -> Objects.equals(e.getWorkPlanCode(), planCode) || Objects.equals(e.getWorkContent(), targetRow.getCell(2).getStringCellValue())).findFirst().orElse(null);
                if (workPlanDataModel != null) {
                    // 标黄
                    setSameRowCellsStyle(targetRow, yellowBackgroundTargetCellStyle);
                    StringBuilder mes = new StringBuilder(targetRow.getCell(7).getStringCellValue() +
                            "(" +
                            targetRow.getCell(6) + "风险" +
                            ")" +
                            targetRow.getCell(3).getStringCellValue() + "：" +
                            targetRow.getCell(2).getStringCellValue()
                            + "。关注点：");

//                    String content = targetRow.getCell(2).getStringCellValue();
//                    Matcher contentMatcher = contentPattern.matcher(content);
//                    boolean find = false;
//                    while (contentMatcher.find()) {
//                        find = true;
//                        mes.append(contentMatcher.group(1));
//                    }

//                    if (!find) {
//                        mes.append("【").append(targetRow.getCell(6)).append("风险").append("】");
//                    }

                    mes.append(riskMessageMap.get(workPlanDataModel));
                    riskMessages.add(mes.toString().replaceAll("\n", ""));
                }

            }, (cells1, cells2) -> {
                // 按”管理部门排序“
                Cell cell1 = cells1.get(7);
                String v1 = cell1.getStringCellValue();
                Cell cell2 = cells2.get(7);
                String v2 = cell2.getStringCellValue();

                v1 = v1.replace('一', '1');
                v1 = v1.replace('二', '2');
                v1 = v1.replace('三', '3');

                v2 = v2.replace('一', '1');
                v2 = v2.replace('二', '2');
                v2 = v2.replace('三', '3');

                return Collator.getInstance().compare(v1, v2);
            });

            // 在最后一行 追加风险信息
            StringBuilder message = new StringBuilder();
            int num = 1;
            for (String riskMessage : riskMessages) {
                message.append("\n").append(num++).append("、").append(riskMessage);
            }
//            for (WorkPlanDataModel riskSort : riskSorts) {
//                String s = riskMessages.get(riskSort);
//                if (StringUtils.isNotBlank(s)) {
//                    message.append("\n").append(num++).append("、").append(s);
//                }
//            }
            // 中高风险告警提醒信息
            String riskMessageAlarmInfo = null;
            if (StringUtils.isNotBlank(message)) {
                String[] dayInWeeks = {"一", "二", "三", "四", "五", "六", "日"};
                String dateStr = date.format(DateTimeFormatter.ofPattern("M月d日"));
                riskMessageAlarmInfo =  dateStr + "（周" + dayInWeeks[date.getDayOfWeek().getValue() - 1] + "），" + "系统运行专业需关注作业" + riskMessages.size() + "项：\r" + message + "\n";
                XSSFRow row = targetSheet.createRow(targetSheet.getLastRowNum() + 1);
                Optional.ofNullable(row.getCell(0)).orElse(row.createCell(0)).setCellValue(riskMessageAlarmInfo);
                XSSFCellStyle cellStyle1 = targetWorkbook.createCellStyle();
                cellStyle1.setWrapText(true);
                cellStyle1.setAlignment(HorizontalAlignment.LEFT);
                cellStyle1.setVerticalAlignment(VerticalAlignment.CENTER);
                setSameRowCellsStyle(row, cellStyle1);

                // 设置行高
                row.setHeightInPoints(calcRowHeightInPt(riskMessageAlarmInfo, row.getCell(0).getCellStyle().getFont().getFontHeightInPoints(), 1750));
                // 合并单元格
                targetSheet.addMergedRegion(
                        new CellRangeAddress(targetSheet.getLastRowNum(),
                                targetSheet.getLastRowNum(),
                                0,
                                targetSheet.getRow(0).getLastCellNum() - 1)
                );
            }

            // 设置目标sheet 边距
            targetSheet.setMargin(PageMargin.TOP, 0.5);
            targetSheet.setMargin(PageMargin.RIGHT, 0.5);
            targetSheet.setMargin(PageMargin.BOTTOM, 0.5);
            targetSheet.setMargin(PageMargin.LEFT, 0.5);

            // 设置打印纸张尺寸
            XSSFPrintSetup targetPrintSetup = targetSheet.getPrintSetup();
            targetPrintSetup.setPaperSize(PrintSetup.A4_PAPERSIZE);
            // 设置缩放75%
            targetPrintSetup.setScale((short)75);
            // 设置打印方向，true-横向打印，false-竖向(垂直)打印
            targetPrintSetup.setLandscape(true);
            // 设置分页预览
            targetSheet.getCTWorksheet().getSheetViews().getSheetViewArray(0).setView(STSheetViewType.Enum.forInt(2));
            // 设置打印区域
            targetWorkbook.setPrintArea(targetWorkbook.getSheetIndex(targetSheet), 0, targetSheet.getRow(0).getLastCellNum() - 1, 0, targetSheet.getLastRowNum());

            // sheet5:二次统计
            originSheet = originWorkbook.getSheet("二次作业计划统计");
            targetSheet = targetWorkbook.getSheet("二次统计");

            // 自定义颜色
//            CTColor ctColor = CTColor.Factory.newInstance();
//            ctColor.setRgb(new byte[]{(byte) 204, (byte) 204, (byte) 255});
//            cellStyle2.setFillForegroundColor(XSSFColor.from(ctColor, null));

            copySheetSingleRange(originSheet, CellRangeAddress.valueOf("A1:G13"), targetSheet, CellRangeAddress.valueOf("A1:G13"), (originRow, targetRow) -> {
                int rowNum = targetRow.getRowNum();

                // 添加日期信息
                if (rowNum == 0) {
                    Cell cell = targetRow.getCell(0);
//                    cell.setCellValue(date.plusDays(1L).format(dateTimeFormatter1) +  cell.getStringCellValue());
                    cell.setCellValue(date.format(dateTimeFormatter1) +  cell.getStringCellValue());
                }

                //第12行 追加 主调、配调 信息
                if (rowNum == 11) {
                    Cell cell = targetRow.getCell(0);
//                    cell.setCellValue(date.plusDays(1L).format(dateTimeFormatter1) + "，" + cell.getStringCellValue() + append);
                    cell.setCellValue(date.format(dateTimeFormatter1) + "，" + cell.getStringCellValue() + append);
                    exportMessage.add(cell.getStringCellValue());
                }

                if (rowNum == 12) {
                    Cell cell = targetRow.getCell(0);
//                    cell.setCellValue(date.plusDays(1L).format(dateTimeFormatter1) + "，" + cell.getStringCellValue());
                    cell.setCellValue(date.format(dateTimeFormatter1) + "，" + cell.getStringCellValue());
                    exportMessage.add(cell.getStringCellValue());
                }
            });
            exportMessage.add(riskMessageAlarmInfo);

            // targetWorkBook 写入临时文件
            targetWorkbook.write(Files.newOutputStream(tomorrowWorkPlanFile.toPath()));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }


        // 导出 总体概况 图片
        try {
            File imageFile = exportSheetChartAsImage(date, Files.newInputStream(file.toPath()));
            // TODO 发送elink
//            sendMessage(date, exportMessage, tomorrowWorkPlanFile, imageFile);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }

    /**
     * 计算行高 pt 1pt = 0.03527777777778199 cm
     * 1cm = 28.346456692910003 pt
     *
     * @param text 文本
     * @param fontSize 字体 pt
     * @param rowWidth 行宽 pt

     * @return 行高
     */
    private short calcRowHeightInPt(String text, float fontSize, float rowWidth) {
        // 单行高度
        float singleRowHeight = fontSize;
        // 需要多少行
        float rowCount = (float) Math.ceil(text.length() * fontSize) / rowWidth + text.split("\n").length;
        return Float.valueOf(rowCount * singleRowHeight * 1.5f).shortValue();
    }

    /**
     * 导出总体概况图片
     * @param inputStream 输入流
     */
    private File exportSheetChartAsImage(LocalDate date, InputStream inputStream) {
        File pdfFile = newTemporaryFile(date, "export", "pdf");
        File imageFile = newTemporaryFile(date,
                ContentType.EXPORT_IMG.getType(),
                ContentType.EXPORT_IMG.getSuffix());

        try {
            LocalConverter.builder()
                    .officeManager(localOfficeManager)
                    .build()
                    .convert(inputStream)
                    .as(DefaultDocumentFormatRegistry.XLSX)
                    .to(pdfFile)
                    .as(DefaultDocumentFormatRegistry.PDF)
                    .execute();

            PDDocument pdDocument = Loader.loadPDF(pdfFile);
            PDFRenderer pdfRenderer = new PDFRenderer(pdDocument);

            for (int i = 0; i < pdDocument.getNumberOfPages(); i++) {
                PDPage page = pdDocument.getPage(i);
                float width = page.getCropBox().getWidth();
                if (width > 600.0) { // 1.333f 100%
                    BufferedImage bufferedImage = pdfRenderer.renderImage(i, 2.666f);
                    ImageIO.write(bufferedImage, ContentType.EXPORT_IMG.getSuffix(), imageFile);
                }
            }
            return imageFile;
        } catch (OfficeException | IOException e) {
            throw new RuntimeException(e);
        } finally {
            pdfFile.delete();
        }
    }

    /**
     * 二次（打印版） 中高风险项 关注点追加
     *  注意： 只解析上传文档的最后一个表格
     *
     * @param date 日期
     * @param before true - 截取到位安排前 false - 截取到位安排后
     * @return 风险项信息 作业编号---到位安排
     */
    private Map<WorkPlanDataModel, String> analysisSecondCountFocus(LocalDate date, boolean before) {
        File[] uploadFile = getUploadFile(date, ContentType.RISK_ALARM);
        if (uploadFile.length == 0) {
            throw new RuntimeException("请上传" + ContentType.RISK_ALARM.getDesc() + "，对应日期：" + date.format(dateTimeFormatter));
        }
        try (
                XWPFDocument document = new XWPFDocument(Files.newInputStream(uploadFile[0].toPath()))
        ) {
            Map<WorkPlanDataModel, String> reMap = new HashMap<>();
            List<XWPFTable> tables = document.getTables();
//            Pattern codePattern = Pattern.compile("[(（]((?:[A-Z]*\\d*、?)*)[）)]");
            // [(（]([A-Z]+(?:\\d+-{0,1})*\\d+)[）)]
            Pattern codePattern = Pattern.compile("[(（]([^(（）)]+)[）)]");
            Pattern contentPattern = Pattern.compile("((?:.|\\n)*)\\d、到位安排：(.*)");
            // 只解析最后一个表格
            XWPFTable table = tables.get(tables.size() - 1);
            Integer contentIndex = null;
            Integer commentIndex = null;
            // 解析第一行，获取 工作内容、管控要求 列索引
            XWPFTableRow firstRow = table.getRow(0);
            for (int i = 0; i < firstRow.getTableCells().size(); i++) {
                XWPFTableCell cell = firstRow.getCell(i);
                if ("工作内容".equals(cell.getText())) {
                    contentIndex = i;
                }
                if ("管控要求".equals(cell.getText())) {
                    commentIndex = i;
                }
            }

            if (contentIndex == null || commentIndex == null) {
                throw new RuntimeException("管控要求文件解析失败");
            }
            // 解析管控要求
            List<XWPFTableRow> rows = table.getRows();
            // 跳过列名行
            for (int i = 1; i < rows.size(); i++) {
                XWPFTableRow row = rows.get(i);
                XWPFTableCell cell = row.getCell(contentIndex);
                if (cell == null) {
                    continue;
                }
                String content = cell.getText();
                Map<WorkPlanDataModel, String> perMessageMap = new HashMap<>();
                // 工作内容解析
                // 1、作业编号解析
                Matcher codeMatch = codePattern.matcher(content);
                List<String> matchCode = new ArrayList<>();
                while (codeMatch.find()) {
                    String codeStr = codeMatch.group(1);
                    // 作业编号
                    matchCode = Arrays.asList(codeStr.split("、"));
                }

                if (!matchCode.isEmpty()) {
                    for (String code : matchCode) {
                        WorkPlanDataModel model = new WorkPlanDataModel();
                        model.setWorkPlanCode(code);
                        perMessageMap.put(model, "");
                    }
                } else {
                    // 工作内容匹配
                    WorkPlanDataModel model = new WorkPlanDataModel();
                    model.setWorkContent(content);
                    perMessageMap.put(model, "");
                }

                XWPFTableCell commentCell = row.getCell(commentIndex);
                String append = commentCell.getText();
                Matcher matcher = contentPattern.matcher(append);
                if (matcher.matches()) {
                    String mes = matcher.group(before ? 1 : 2);
                    if (before) {
                        // 截取加粗部分文本
                        StringBuilder sb = new StringBuilder();
                        int num = 1;
                        for (XWPFParagraph paragraph : commentCell.getParagraphs()) {
                            for (XWPFRun run : paragraph.getRuns()) {
                                if (run.isBold()) {
                                    String text = run.text();
                                    sb.append(num++).append("）").append(text).append("。");
//                                    if (!(text.endsWith("。") || text.endsWith("，") || text.endsWith(".") || text.endsWith(","))) {
//                                        sb.append("。");
//                                    }
                                }
                            }
                        }
                        mes = sb.toString();
                    }
                    mes = mes.replaceAll("\n", "");
                    mes = mes.replaceAll("\r", "");
                    String finalMes = mes;
                    perMessageMap.forEach((k, v) -> perMessageMap.put(k, finalMes));
                    reMap.putAll(perMessageMap);
                }
            }
            return reMap;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }


    /**
     * 分析主调 作业计划
     *
     * @param targetSheet 计划写入目标sheet
     * @return 主网作业数量
     */
    private Integer masterPlanAnalysis(Sheet originSheet, Sheet targetSheet) {
        // 遍历获取最后行列
        int lastRowNum = 0, lastColNum= 0;
        for (CellRangeAddress mergedRegion : originSheet.getMergedRegions()) {
            lastRowNum = Math.max(lastRowNum, mergedRegion.getLastRow());
            lastColNum = Math.max(lastColNum, mergedRegion.getLastColumn());
        }
        copySheetSingleRange(originSheet, new CellRangeAddress(0, lastRowNum, 0, lastColNum),
                targetSheet,
                null,
                null
        );

        for (int i = originSheet.getLastRowNum(); i >= 0; i--) {
            Row row = originSheet.getRow(i);
            if (row != null) {
                Cell cell = row.getCell(0);
                if (cell != null && cell.getCellType() == CellType.STRING) {
                    String cellValue = cell.getStringCellValue();
                    Pattern pattern = Pattern.compile("\\D*(\\d+)项主要操作项目.*");
                    Matcher matcher = pattern.matcher(cellValue);
                    if (matcher.find()) {
                        String num = matcher.group(1);
                        return Integer.parseInt(num);
                    }
                }
            }
        }

        return originSheet.getLastRowNum() - 5;

    }

    /**
     * 分析配网 作业计划
     * @param targetSheet 计划写入目标sheet
     * @return [配网计划检修项数量, 配网计划转电方式单数量, 带电作业单数量]
     */
    private int[] subPlanAnalysis(LocalDate date, Sheet originSheet, Sheet targetSheet) {
        copySheetSingleRange(originSheet, new CellRangeAddress(0, originSheet.getLastRowNum(), 0, 12),
                targetSheet,
                new CellRangeAddress(2, originSheet.getLastRowNum() + 2, 0, originSheet.getRow(0).getLastCellNum()),
                null
        );

        Row firstRow = targetSheet.getRow(2);
        String topRowValue = firstRow.getCell(0).getStringCellValue();
        if (topRowValue.contains("调控中心作业计划")) {
            throw new RuntimeException("配调文件格式异常，删除第一、二行");
        }

        // 添加表头
        firstRow = targetSheet.createRow(0);
        firstRow.setHeightInPoints(50.0f);
        Cell firstRowCell = firstRow.createCell(0);
        firstRowCell.setCellValue(date.format(DateTimeFormatter.ofPattern("yyyy年MM月dd日")) + "调控中心作业计划");
        CellStyle textMiddleStyle = targetSheet.getWorkbook().createCellStyle();
        textMiddleStyle.setAlignment(HorizontalAlignment.CENTER);
        textMiddleStyle.setVerticalAlignment(VerticalAlignment.CENTER);
        Font font = targetSheet.getWorkbook().createFont();
        font.setFontName("方正小标宋_GBK"); // 设置字体为 Arial
        font.setFontHeightInPoints((short) 20); // 设置字体大小
        font.setBold(false); // 设置为非加粗
        font.setColor(IndexedColors.BLACK.getIndex()); // 设置字体颜色为黑色
        textMiddleStyle.setFont(font);
        firstRowCell.setCellStyle(textMiddleStyle);


        Row secondRow = targetSheet.createRow(1);
        secondRow.createCell(0);
        Cell secondRowCell = secondRow.createCell(0);
        secondRowCell.setCellValue("一次专业（配调）");
        secondRow.setHeightInPoints(50.0f);
        CellStyle greenBackgroundColorAndBlobStyle = targetSheet.getWorkbook().createCellStyle();
        CTColor ctColor = CTColor.Factory.newInstance();
        ctColor.setRgb(new byte[]{(byte) 0, (byte) 176, (byte) 80});
        greenBackgroundColorAndBlobStyle.setFillForegroundColor(XSSFColor.from(ctColor, null));
        greenBackgroundColorAndBlobStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND); // 设置填充模式为实心填充
        greenBackgroundColorAndBlobStyle.setAlignment(HorizontalAlignment.LEFT);
        greenBackgroundColorAndBlobStyle.setVerticalAlignment(VerticalAlignment.CENTER);
        Font font1 = targetSheet.getWorkbook().createFont();
        font1.setFontName("方正小标宋_GBK"); // 设置字体为 Arial
        font1.setFontHeightInPoints((short) 20); // 设置字体大小
        font1.setBold(true); // 设置为非加粗
        font1.setColor(IndexedColors.BLACK.getIndex()); // 设置字体颜色为黑色
        greenBackgroundColorAndBlobStyle.setFont(font1);
        secondRowCell.setCellStyle(greenBackgroundColorAndBlobStyle);

        // 合并行列
        List<CellRangeAddress> mergedRegions = targetSheet.getMergedRegions();
        for (int i = 0; i < mergedRegions.size(); i++) {
            targetSheet.removeMergedRegion(i);
        }
        for (CellRangeAddress mergedRegion : mergedRegions) {
            mergedRegion.setFirstRow(mergedRegion.getFirstRow() + 2);
            mergedRegion.setLastRow(mergedRegion.getLastRow() + 2);
            targetSheet.addMergedRegion(mergedRegion);
        }
        targetSheet.addMergedRegion(new CellRangeAddress(0, 0, 0, 12));
        targetSheet.addMergedRegion(new CellRangeAddress(1, 1, 0, 12));
        // 冻结第一、二行
        targetSheet.createFreezePane(0, 2);

        List<Pattern> patterns = new ArrayList<>();
        patterns.add(Pattern.compile("计划检修单（(\\d*)项.*"));
        patterns.add(Pattern.compile("转供电方式单（(\\d*)项，.*"));
        patterns.add(Pattern.compile("带电作业单（(\\d*)项）"));

        int[] re = new int[3];
        for (int i = 0; i < originSheet.getNumMergedRegions(); i++) {
            CellRangeAddress mergedRegion = originSheet.getMergedRegion(i);
            Cell cell = originSheet.getRow(mergedRegion.getFirstRow()).getCell(mergedRegion.getFirstColumn());
            String cellValue = cell.getStringCellValue();
            for (int j = 0; j < patterns.size(); j++) {
                Pattern pattern = patterns.get(j);
                Matcher matcher = pattern.matcher(cellValue);
                if (matcher.matches()) {
                    re[j] = Integer.parseInt(matcher.group(1));
                }
            }
        }

        return re;
    }

    private void setSameRowCellsStyle(Row row, CellStyle cellStyle) {
        for (short i = 0; i < row.getLastCellNum(); i++) {
            row.getCell(i).setCellStyle(cellStyle);
        }
    }

    /**
     * 复制源sheet指定区域数据到目标sheet，适合不连续区域复制
     *
     * @param originSheet 源sheet
     * @param originCellRangeAddressList 源sheet复制区域 与 targetCellRangeAddressList 一一对应
     * @param targetSheet 目标sheet
     * @param targetCellRangeAddressList 目标sheet 覆盖区域
     * @param rowPostHandler 行后置处理
     *
     * @see this#copySheetSingleRange(Sheet, CellRangeAddress, Sheet, CellRangeAddress, BiConsumer)
     */
    private void copySheetMultiRange(Sheet originSheet, List<CellRangeAddress> originCellRangeAddressList, Sheet targetSheet, List<CellRangeAddress> targetCellRangeAddressList, BiConsumer<Row, Row> rowPostHandler) {

        if (CollectionUtils.isEmpty(originCellRangeAddressList) || CollectionUtils.isEmpty(targetCellRangeAddressList)) {
            copySheetSingleRange(originSheet, null, targetSheet, null, rowPostHandler);
        } else {
            if (originCellRangeAddressList.size() != targetCellRangeAddressList.size()) {
                throw new RuntimeException("复制区域与木匾区域数量不一致");
            }

            for (int i = 0; i < originCellRangeAddressList.size(); i++) {
                CellRangeAddress originCellRange = originCellRangeAddressList.get(i);
                CellRangeAddress targetCellRange = targetCellRangeAddressList.get(i);

                if (originCellRange == null || targetCellRange == null) {
                    throw new RuntimeException("复制区域或目标区域为空");
                }

                copySheetSingleRange(originSheet, originCellRange, targetSheet, targetCellRange, rowPostHandler);
            }
        }
    }

    private void copyRowAndSort(Sheet originSheet, List<CellRangeAddress> originCellRangeAddressList, Sheet targetSheet, List<CellRangeAddress> targetCellRangeAddressList,
                                BiConsumer<List<Cell>, Row> rowPostProcessor,
                                Comparator<List<Cell>> comparator
    ) {

        if (originCellRangeAddressList.size() != targetCellRangeAddressList.size()) {
            throw new RuntimeException("复制区域与目标区域不一致");
        }

        List<List<Cell>> rows = new ArrayList<>();

        for (CellRangeAddress originRange : originCellRangeAddressList) {
            if (originRange == null) {
                throw new RuntimeException("复制区域为空");
            }

            for (int j = originRange.getFirstRow(); j <= originRange.getLastRow(); j++) {
                List<Cell> rowValue = rows.size() <= j - originRange.getFirstRow() ? new ArrayList<>() : rows.get(j - originRange.getFirstRow());

                Row row = originSheet.getRow(j);
                for (int k = originRange.getFirstColumn(); k <= originRange.getLastColumn(); k++) {
                    rowValue.add(row.getCell(k));
                }

                if (rows.size() <= j - originRange.getFirstRow()) {
                    rows.add(rowValue);
                }
            }
        }

        // 排序
        rows.sort(comparator);

        CellRangeAddress firstTargetRange = targetCellRangeAddressList.get(0);
        int firstRow = firstTargetRange.getFirstRow();
        // 写入目标区域
        for (List<Cell> originRow : rows) {
            Row targetRow = targetSheet.createRow(firstRow++);
            int originColumnIndex = 0;
            for (CellRangeAddress targetRange : targetCellRangeAddressList) {
                for (int j = targetRange.getFirstColumn(); j <= targetRange.getLastColumn(); j++) {
                    Cell originCell = originRow.get(originColumnIndex++);
                    Cell targetCell = targetRow.createCell(j);

                    setCellValue(originSheet.getWorkbook(), originCell, targetCell);
                    targetCell.setCellStyle(originCell.getCellStyle());
                }
            }
            rowPostProcessor.accept(originRow, targetRow);
        }

    }

    /**
     *  sheet 复制 数值，格式（注意保持文件类型相同，否则失败，即必须都是xlsx文件，或都是xls文件，又或者xlsm），适合连续区域复制
     *
     * @param originSheet 源sheet
     * @param originCellRangeAddress 源sheet复制区域 与 targetCellRangeAddress对应
     * @param targetSheet 目标sheet
     * @param targetCellRangeAddress 目标sheet 覆盖区域
     * @param rowPostHandler 行后置处理
     */
    private void copySheetSingleRange(Sheet originSheet, CellRangeAddress originCellRangeAddress, Sheet targetSheet, CellRangeAddress targetCellRangeAddress, BiConsumer<Row, Row> rowPostHandler) {

        Workbook targetWorkbook = targetSheet.getWorkbook();

        int firstRowNum = originCellRangeAddress == null ? originSheet.getFirstRowNum() : originCellRangeAddress.getFirstRow();
        int lastRowNum = originCellRangeAddress == null ? originSheet.getLastRowNum() - 1 : originCellRangeAddress.getLastRow();

        int targetRowOffset = 0;
        // 复制单元格
        for (int i = firstRowNum; i <= lastRowNum; i++) {
            Row originRow = originSheet.getRow(i);
            if (originRow == null) {// 空行跳过
                targetRowOffset = targetCellRangeAddress == null ? targetRowOffset : targetRowOffset + 1;
                continue;
            }
            int targetRowIndex = targetCellRangeAddress == null ? i : targetCellRangeAddress.getFirstRow() + targetRowOffset++;
            Row targetRow =  targetSheet.createRow(targetRowIndex);

//            targetSheet.createFreezePane();
            // 复制 冻结行时
//            CellStyle cellStyle = targetWorkbook.createCellStyle();
//            cellStyle.cloneStyleFrom(originRow.getRowStyle());
//            targetRow.setRowStyle(cellStyle);
            // 设置 行高
            targetRow.setHeightInPoints(originRow.getHeightInPoints());

            short firstCellNum = originCellRangeAddress == null ? originRow.getFirstCellNum() : (short)originCellRangeAddress.getFirstColumn();
            short lastCellNum = originCellRangeAddress == null ? (short)(originRow.getLastCellNum() - 1) : (short)originCellRangeAddress.getLastColumn();

            int targetCellOffset = 0;
            // 遍历 源sheet的单元格cell
            for (short cellIndex = firstCellNum; cellIndex <= lastCellNum; cellIndex++) {
                Cell originCell = originRow.getCell(cellIndex);

                if (originCell == null) {
                    continue;
                }

                int targetCellIndex = targetCellRangeAddress == null ? cellIndex : targetCellRangeAddress.getFirstColumn() + targetCellOffset++;
                Cell targetCell = targetRow.createCell(targetCellIndex);
                setCellValue(originSheet.getWorkbook(), originCell, targetCell);
                // 复制样式
                CellStyle targetCellStyle = targetWorkbook.createCellStyle();
                targetCellStyle.cloneStyleFrom(originCell.getCellStyle());
                targetCell.setCellStyle(targetCellStyle);
            }
            if (rowPostHandler != null) {
                rowPostHandler.accept(originRow, targetRow);
            }
        }

        // 合并单元格
        for (int i = 0; i < originSheet.getNumMergedRegions(); i++) {
            CellRangeAddress mergedRegion = originSheet.getMergedRegion(i);
            targetSheet.addMergedRegion(new CellRangeAddress(mergedRegion.getFirstRow(), mergedRegion.getLastRow(), mergedRegion.getFirstColumn(), mergedRegion.getLastColumn()));
        }

    }

    /**
     * 设置单元格的值  复制源单元格的值到目标单元格（单元格数据类型是公式时，计算公式，复制公式的结果值到目标单元格）
     *
     * @param originWorkbook 源cell所在的excel工作簿
     * @param originCell 源cell
     * @param targetCell 目标cell
     */
    private void setCellValue(Workbook originWorkbook, Cell originCell, Cell targetCell) {
        switch (originCell.getCellType()) {
            case FORMULA:
                FormulaEvaluator evaluator = originWorkbook.getCreationHelper().createFormulaEvaluator();
                CellValue originCellValue = evaluator.evaluate(originCell);
                setCellValue(originCellValue, targetCell);
                break;
            case NUMERIC:
                targetCell.setCellValue(originCell.getNumericCellValue());break;
            case STRING:
                targetCell.setCellValue(originCell.getStringCellValue());break;
            case BOOLEAN:
                targetCell.setCellValue(originCell.getBooleanCellValue());break;
            case BLANK:
                targetCell.setCellValue("");break;
            case ERROR:
                targetCell.setCellValue(originCell.getErrorCellValue());
        }
    }

    /**
     * 根据cellValue 设置目标单元格的值
     *
     * @param originCellValue 源单元格值 （公式计算结果）
     * @param targetCell 目标单元格
     */
    private void setCellValue(CellValue originCellValue, Cell targetCell) {
        CellType cellType = originCellValue.getCellType();
        switch (cellType) {
            case NUMERIC:
                targetCell.setCellValue(originCellValue.getNumberValue());break;
            case STRING:
                targetCell.setCellValue(originCellValue.getStringValue());break;
            case BOOLEAN:
                targetCell.setCellValue(originCellValue.getBooleanValue());break;
            case BLANK:
                targetCell.setCellValue("");break;
            case ERROR:
                targetCell.setCellValue(originCellValue.getErrorValue());
        }
    }

    /**
     * 计算 管理部门
     * @param workPlanDataModel 数据行
     * @param calcRunningCrew 运行班组
     * @return 数据行对应的管理部门数据
     */
    private String calcManageDepartment(WorkPlanDataModel workPlanDataModel, String calcRunningCrew) {
        // 管理部门列值 计算
        // D列 项目单位管理名称
        String projectManageUnit = workPlanDataModel.getProjectManageUnit();
        if (StringUtils.isNotBlank(projectManageUnit)) {
            for (Map.Entry<String, String> entry : departmentDColumnMap.entrySet()) {
                if (projectManageUnit.contains(entry.getKey())) {
                    return entry.getValue();
                }
            }
        }

        // O列 工作班组
        String workCrew = workPlanDataModel.getWorkCrew();
        if (StringUtils.isNotBlank(workCrew)) {
            for (Map.Entry<String, String> entry : departmentO_PColumnMap.entrySet()) {
                if (workCrew.contains(entry.getKey())) {
                    return entry.getValue();
                }
            }
        }

        // P列 运行班组
        String runningCrew = workPlanDataModel.getRunningCrew();
        if (StringUtils.isNotBlank(runningCrew)) {
            for (Map.Entry<String, String> entry : departmentO_PColumnMap.entrySet()) {
                if (runningCrew.contains(entry.getKey())) {
                    return entry.getValue();
                }
            }
        }

        // AP列 运行班组列值 （计算获取，已传入）
        if (StringUtils.isNotBlank(calcRunningCrew)) {
            for (Map.Entry<String, String> entry : departmentApColumnMap.entrySet()) {
                if (calcRunningCrew.contains(entry.getKey())) {
                    return entry.getValue();
                }
            }
        }

        if ("项目中心".equals(calcRunningCrew)) {
            return "项目中心";
        }

        return null;
    }

    /**
     * 计算 运行班组
     * @param workPlanDataModel 数据行
     * @return 数据行对应的运行班组
     */
    private String calcRunningClass(WorkPlanDataModel workPlanDataModel) {
        // 运行班组列值 计算
        // 作业主体
        String workMajor = workPlanDataModel.getWorkMajor();
        String name;
        if ("本单位".equals(workMajor)) {
            // 工作负责人
            name = workPlanDataModel.getWorkResponsiblePerson();
        } else {
            // 现场第一负责人
            name = workPlanDataModel.getFirstResponsiblePersonInScene();
        }

        // 班组人员数据
        for (CrewMember crewMember : crewMembers) {
            if (Objects.equals(name, crewMember.getName())) {
                String unitCrewName = crewMember.getUnitCrewName();
                return StringUtils.isNotBlank(unitCrewName) && unitCrewName.contains("继保分部") ? null : unitCrewName;
            }
        }

        return null;
    }

    /**
     *  中高风险统计
     *
     * @param date 日期
     * @param workPlanDataModels 工作计划数据
     */
    private void middleAndHighLevelRiskCheck(LocalDate date, List<WorkPlanDataModel> workPlanDataModels) {
        long count = workPlanDataModels.stream().filter(e -> "中".equals(e.getRiskLevel()) || "高".equals(e.getRiskLevel())).count();
        // 忠告风险总计 少于 10，则不处理
        if (count >= 10) {
            // 中高风险 工作本单位 外单位分组
            Map<String, List<WorkPlanDataModel>> middleAndHighRiskLevelMap = workPlanDataModels.stream().filter(e -> "中".equals(e.getRiskLevel()) || "高".equals(e.getRiskLevel())).collect(Collectors.groupingBy(WorkPlanDataModel::getWorkMajor));

            List<WorkPlanDataModel> otherUnitMiddleAndHighRiskLevelPlan = middleAndHighRiskLevelMap.get("外单位");
            // 存在外单位作业
            if (CollectionUtils.isNotEmpty(otherUnitMiddleAndHighRiskLevelPlan)) {
                List<WorkPlanDataModel> localUnitMiddleAndHighRiskLevelPlan = Optional.ofNullable(middleAndHighRiskLevelMap.get("本单位")).orElse(new ArrayList<>());
                // 移除 外单位 工作内容与本单位重复的
                otherUnitMiddleAndHighRiskLevelPlan.removeIf(o -> localUnitMiddleAndHighRiskLevelPlan.stream().anyMatch(l -> Objects.equals(o.getWorkContent(), l.getWorkContent())));

                workPlanDataModels.clear();
                workPlanDataModels.addAll(localUnitMiddleAndHighRiskLevelPlan);
                workPlanDataModels.addAll(otherUnitMiddleAndHighRiskLevelPlan);
            }
        }

        // 记录任务数
        SecondPlanRecord secondPlanRecord = secondPlanRecordMapper.selectOne(Wrappers.<SecondPlanRecord>lambdaQuery()
                .ge(SecondPlanRecord::getTime, date.atStartOfDay())
                .lt(SecondPlanRecord::getTime, date.plusDays(1).atStartOfDay())
        );

        if (secondPlanRecord == null) {
            secondPlanRecord = new SecondPlanRecord();
            secondPlanRecord.setTime(date.atStartOfDay());
            secondPlanRecord.setNum((short)workPlanDataModels.size());
            secondPlanRecordMapper.insert(secondPlanRecord);
        } else {
            secondPlanRecord.setNum((short)workPlanDataModels.size());
            secondPlanRecordMapper.update(secondPlanRecord, Wrappers.<SecondPlanRecord>lambdaUpdate()
                    .eq(SecondPlanRecord::getTime, secondPlanRecord.getTime())
            );
        }
    }

    /**
     * 解析上传的工作计划excel数据
     *
     * @param files 工作计划数据
     * @return 转换后的数据
     */
    private List<WorkPlanDataModel> transferExcel(File[] files) {
        List<WorkPlanDataModel> workPlanDataModels = new ArrayList<>();
        for (File file : files) {
            try {
                InputStream inputStream = Files.newInputStream(file.toPath());
                ExcelReaderBuilder readerBuilder = EasyExcel.read(inputStream, WorkPlanDataModel.class, null);
                workPlanDataModels.addAll(readerBuilder.sheet().doReadSync());
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
        return workPlanDataModels;
    }

    /**
     * 保存文件到临时目录
     *
     * @param date 日期
     * @param contentType 内容类型
     * @param files 文件
     */
    public synchronized void saveFile(LocalDate date, ContentType contentType, List<MultipartFile> files) {
        File temporaryFile;
        for (int i = 0; i < files.size(); i++) {
            MultipartFile uploadFile = files.get(i);
            if (uploadFile.isEmpty()) {
                continue;
            }
            String fileName = uploadFile.getOriginalFilename();
            if (StringUtils.isBlank(fileName)) {
                throw new RuntimeException("上传文件名为空");
            }
            String fileSuffix = fileName.substring(fileName.lastIndexOf(".") + 1);
            if (!contentType.getSuffix().equals(fileSuffix)) {
                throw new RuntimeException(contentType.getDesc() + "上传格式异常，请上传" + contentType.getSuffix() + "类型的文件");
            }

            if (contentType == ContentType.RISK_ALARM
                    || contentType == ContentType.WEEK_RISK
            ) {
                String year = calcWeekYear(date) + "";
                temporaryFile = Paths.get(tempDir,
                        year,
                        contentType.getType() + (i == 0 ? "" : i) + "_" + calcWeek(date) + "." + fileSuffix
                )
                        .toAbsolutePath() // 使用绝对路径 否则在linux上，由于文件系统不一致，d:会使用相对路径，导致FileNotFoundException
                        .toFile();
                try {
                    if (!temporaryFile.getParentFile().exists() && !temporaryFile.getParentFile().mkdirs()
                    || !temporaryFile.exists() && !temporaryFile.createNewFile()) {
                        throw new RuntimeException(temporaryFile.getAbsolutePath() + "，文件创建失败");
                    }
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            } else {
                temporaryFile = newTemporaryFile(date, contentType.getType() + (i == 0 ? "" : i), fileSuffix);
            }

            try {
                log.info("上传文件保存路径：{}", temporaryFile.getPath());
                uploadFile.transferTo(temporaryFile);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

    /**
     * 展示特定日期的文件
     *
     * @param date 日期
     * @return 文件名列表 master、sub、
     */
    public List<String> getUploadFileNames(LocalDate date, ContentType type, Boolean isAuto) {
        List<String> files = new ArrayList<>();

        String dateStr = date.format(dateTimeFormatter);

        String year = dateStr.substring(0, 4);
        String dates = dateStr.substring(5);

        if (type != ContentType.RISK_ALARM
                && type != ContentType.WEEK_RISK
        ) {
            File dir = Paths.get(tempDir, year, dates).toFile();

            if (!dir.exists()) {
                throw new RuntimeException("未上传" + dateStr + "的文件");
            }

            File[] filesList = Optional.ofNullable(dir.listFiles()).orElse(new File[0]);
            for (File file : filesList) {
                if (file.isDirectory()) {
                    continue;
                }
                files.add(file.getName().substring(0, file.getName().lastIndexOf(".")));
            }

            File parentDir = dir.getParentFile();
            File[] riskAlaramFile = parentDir.listFiles(file ->
                    file.isFile() && file.getName()
                            .substring(0, file.getName().lastIndexOf("."))
                            .equals(ContentType.RISK_ALARM.getType() + "_" + calcWeek(date))
            );

            if (riskAlaramFile != null && riskAlaramFile.length > 0) {
                files.add(riskAlaramFile[0].getName().substring(0, riskAlaramFile[0].getName().lastIndexOf(".")));
            }

            if (BooleanUtils.isTrue(isAuto)) {
                files.removeIf(e -> Objects.equals(e, ContentType.CHECK.getType())
                        || Objects.equals(e, ContentType.EXTRA_CHECK.getType())
                || Objects.equals(e, ContentType.EXPORT_IMG.getType())
                || Objects.equals(e, ContentType.EXPORT_TOMORROW.getType()));

                dir = Paths.get(tempDir, year, dates, "auto").toFile();

                if (dir.exists()) {
                    File[] autoFileList = Optional.ofNullable(dir.listFiles()).orElse(new File[0]);
                    for (File file : autoFileList) {
                        files.add(file.getName().substring(0, file.getName().lastIndexOf(".")));
                    }
                }
            }
        } else { // 年度文件夹下查找
            File dir = Paths.get(tempDir, year).toFile();
            File[] files1 = dir.listFiles((d, fname) ->
                    fname.startsWith(type.getType())
                    && calcWeek(date) == Integer.parseInt(fname.substring(fname.lastIndexOf("_") + 1, fname.lastIndexOf("."))));
            if (files1 == null) {
                return Collections.emptyList();
            }
            List<File> list = Arrays.asList(files1);
            list.sort(Comparator.comparing(File::getName));

            files = list.stream().map(f -> f.getName().substring(0, f.getName().lastIndexOf("."))).sorted(Comparator.reverseOrder()).collect(Collectors.toList());
        }

        return files;
    }

    /**
     * 获取输入日期所在年的第几周
     * @param date 输入日期
     * @return 第几周
     */
    private int calcWeek(LocalDate date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(Date.from(date.atStartOfDay().toInstant(ZoneId.systemDefault().getRules().getOffset(date.atStartOfDay()))));

        calendar.setFirstDayOfWeek(Calendar.MONDAY);
        calendar.setMinimalDaysInFirstWeek(7);
        return calendar.get(Calendar.WEEK_OF_YEAR);
    }

    /**
     * 获取输入日期所在年的第几周
     * @param date 输入日期
     * @return 第几周
     */
    private int calcWeekYear(LocalDate date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(Date.from(date.atStartOfDay().toInstant(ZoneId.systemDefault().getRules().getOffset(date.atStartOfDay()))));

        calendar.setFirstDayOfWeek(Calendar.MONDAY);
        calendar.setMinimalDaysInFirstWeek(7);
        return calendar.getWeekYear();
    }

    /**
     * 下载指定文件
     *
     * @param date 日期
     * @param filename 文件名
     * @param response 响应
     */
    public void downloadFile(String date, ContentType contentType, String filename, Boolean isAuto, HttpServletResponse response) {

        String dateDirName = contentType == ContentType.RISK_ALARM
                || contentType == ContentType.WEEK_RISK ? "" : date.substring(5);

        boolean inAutoDir = BooleanUtils.isTrue(isAuto) && StringUtils.isNotBlank(dateDirName)
                && (contentType == ContentType.CHECK
                || contentType == ContentType.EXTRA_CHECK
                || contentType == ContentType.EXPORT_IMG
                || contentType == ContentType.EXPORT_TOMORROW);

        File dir = Paths.get(tempDir,
                date.substring(0, 4),
                dateDirName,
                inAutoDir ? "auto" : ""
        ).toFile();
        if (!dir.exists()) {
            throw new RuntimeException(dir.getAbsolutePath() + "文件夹不存在");
        }

        File[] files = dir.listFiles((d, fname) -> fname.startsWith(filename));
        if (files == null || files.length == 0) {
            throw new RuntimeException("文件不存在");
        }

        File file = files[0];
        // 设置响应 内容长度 解决文件已损坏
        response.setContentLength(Long.valueOf(file.length()).intValue());

        try (
                InputStream inputStream = Files.newInputStream(file.toPath());
        ){
            byte[] bytes = new byte[1024];
            int length;
            while((length = inputStream.read(bytes)) != -1) {
                response.getOutputStream().write(bytes, 0 , length);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 删除指定文件
     *
     * @param date 日期
     * @param filename 文件名
     */
    public void removeFile(LocalDate date, String dirName, ContentType contentType, String filename) {

        String dateStr = date.format(dateTimeFormatter);
        File dir = Paths.get(tempDir,
                dateStr.substring(0, 4),
                contentType == ContentType.RISK_ALARM ? "" : dateStr.substring(5),
                StringUtils.isBlank(dirName) ? "" : dirName
        ).toFile();
        if (!dir.exists()) {
            throw new RuntimeException(dir.getAbsolutePath() + "文件夹不存在");
        }

        File[] files = dir.listFiles((d, fname) -> fname.startsWith(filename));
        if (files == null || files.length == 0) {
            throw new RuntimeException("文件不存在");
        }

        File file = files[0];
        if (!file.delete()) {
            throw new RuntimeException("文件删除失败");
        }
    }

    /**
     * 发送Elink信息 发送到刘胜波的Elink
     *
     * @param message 消息
     * @param tomorrowFile 明日计划excel
     * @param imageFile 明日图片excel
     */
    public void sendMessage(LocalDate date, List<String> message, File tomorrowFile, File imageFile) {
        String userList = "";
        String dateStr = date.format(dateTimeFormatter);
        KafkaClient kafkaClient=new KafkaClient("ZFOMS_工作计划");

        StringBuilder sb = new StringBuilder();
        for (String s : message) {
            sb.append(s).append("\n");
        }

        // 发送文本信息
        boolean sendFlag = kafkaClient.sendElinkMsg(sb.toString(), userList, null);
        if (!sendFlag) {
            throw new RuntimeException("智慧安监总结信息发送失败");
        }

        // 发送图片
        String imagePath = CommonFunc.getConfigByKey("sys.kafka_path_image");
        Path eLinkImageFilePath = Paths.get(imagePath, "智慧安监明日计划总体概况图片-" + dateStr + ".png");
        try {
            // 将数据写入文件
            Files.copy(Files.newInputStream(imageFile.toPath()), eLinkImageFilePath);
        } catch (IOException e) {
            e.printStackTrace();
        }
        sendFlag = kafkaClient.sendElinkImage(eLinkImageFilePath.toString(), userList, null);
        if (!sendFlag) {
            throw new RuntimeException("智慧安监明日计划总体概况图片发送失败");
        }

        // 发送明日计划excel
        //上传文件到EL指定目录
        String filePath= CommonFunc.getConfigByKey("sys.kafka_path_file");
        Path eLinkTomorrowFilePath = Paths.get(filePath, "智慧安监明日计划-" + dateStr + ".xlsx");
        try {
            // 将数据写入文件
            Files.copy(Files.newInputStream(tomorrowFile.toPath()), eLinkTomorrowFilePath);
        } catch (IOException e) {
            e.printStackTrace();
        }
        sendFlag = kafkaClient.sendElinkFile(eLinkTomorrowFilePath.toString(), userList, null);
        if (!sendFlag) {
            throw new RuntimeException("智慧安监明日计划excel发送失败");
        }

    }

    private List<CrewMember> flushCrewMembersWithSort() {
        List<CrewMember> crewMemberInDbs = this.crewMemberMapper.selectMoreDetails(Collections.emptyList());
        Map<String, List<CrewMember>> unitCrewMemebrMap = crewMemberInDbs.stream().collect(Collectors.groupingBy(CrewMember::getUnitCrewName));

        List<String> sortedUnitCrewNames = chineseSort(unitCrewMemebrMap.keySet());
        List<CrewMember> crewMemberList = new ArrayList<>();
        for (String sortedUnitCrewName : sortedUnitCrewNames) {
            crewMemberList.addAll(unitCrewMemebrMap.getOrDefault(sortedUnitCrewName, Collections.emptyList()));
        }
        return crewMemberList;
    }

    private List<String> chineseSort(Collection<String> strings) {
        Map<String, String> map = new HashMap<>();
        map.put("一", "1");
        map.put("二", "2");
        map.put("三", "3");
        map.put("四", "4");
        map.put("五", "5");
        map.put("六", "6");
        map.put("七", "7");
        map.put("八", "8");
        map.put("九", "9");
        map.put("十", "10");

        // 变一继一
        List<String> change = new ArrayList<>();
        // 通信一班
        List<String> comm = new ArrayList<>();
        // 主调/配调/xx自动化
        List<String> auto = new ArrayList<>();
        // 其他
        List<String> other = new ArrayList<>();

        for (String s : strings) {
            for (Map.Entry<String, String> entry : map.entrySet()) {
                s = s.replaceAll(entry.getKey(), entry.getValue());
            }
            if (Pattern.compile("变.继.").matcher(s).matches()) {
                change.add(s);
            }else if (s.startsWith("通信")) {
                comm.add(s);
            }else if (s.endsWith("自动化")) {
                auto.add(s);
            }else {
                other.add(s);
            }
        }
        change.sort(Comparator.naturalOrder());
        comm.sort(Comparator.naturalOrder());
        auto.sort(Comparator.naturalOrder());
        other.sort(Comparator.naturalOrder());

        change.addAll(comm);
        change.addAll(auto);
        change.addAll(other);

        for (int i = 0; i < change.size(); i++) {
            for (Map.Entry<String, String> entry : map.entrySet()) {
                change.set(i, change.get(i).replaceAll(entry.getValue(), entry.getKey()));
            }
        }

        return change;
    }

}

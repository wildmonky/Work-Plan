package com.zhuofan.oms.service.workplan2;

import com.alibaba.excel.metadata.Head;
import com.alibaba.excel.metadata.data.WriteCellData;
import com.alibaba.excel.write.handler.CellWriteHandler;
import com.alibaba.excel.write.metadata.holder.WriteSheetHolder;
import com.alibaba.excel.write.metadata.holder.WriteTableHolder;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.ss.usermodel.*;

import java.util.List;

public class WorkPlanEasyExcelFormulaWriteHandler2 implements CellWriteHandler {

    @Override
    public void afterCellCreate(WriteSheetHolder writeSheetHolder, WriteTableHolder writeTableHolder, Cell cell,
                         Head head, Integer relativeRowIndex, Boolean isHead) {

        // 设置自动换行
        Sheet sheet = cell.getSheet();
        Workbook workbook = sheet.getWorkbook();
        CellStyle cellStyle = workbook.createCellStyle();
        cellStyle.setWrapText(true);
        cell.setCellStyle(cellStyle);
    }

    @Override
    public void afterCellDispose(WriteSheetHolder writeSheetHolder, WriteTableHolder writeTableHolder,
                                  List<WriteCellData<?>> cellDataList, Cell cell, Head head, Integer relativeRowIndex, Boolean isHead) {


        String sheetName = writeSheetHolder.getSheetName();
        if (!"智慧安监系统导出数据".equals(sheetName)) {
            return;
        }

        CellType cellType = cell.getCellType();
        switch (cellType) {
            case STRING:
                if (StringUtils.isNotBlank(cell.getStringCellValue())) {
                    return;
                }
                break;
            case NUMERIC:
                if (cell.getNumericCellValue() != 0) {
                    return;
                }
                break;
            case FORMULA:
                if (StringUtils.isNotBlank(cell.getCellFormula())) {
                    return;
                }
        }

        int rowNum = cell.getRowIndex() + 1;
        // 获取第一行 标题列
        String columnName = head.getHeadNameList().get(0);

//        String departmentFormula = "IFERROR(IF(FIND(\"变电管理一所\",D%d)>0,\"变一所\",\"\"),IFERROR(IF(FIND(\"变电管理二所\",D%d)>0,\"变二所\",\"\"),IFERROR(IF(FIND(\"变电管理三所\",D%d)>0,\"变三所\",\"\"),IFERROR(IF(FIND(\"项目管理中心\",D%d)>0,\"项目中心\",\"\"),IFERROR(IF(FIND(\"自动化\",O%d)>0,\"调度自动化\",\"\"),IFERROR(IF(FIND(\"通信\",O%d)>0,\"调度通信\",\"\"),IFERROR(IF(FIND(\"通信\",P%d)>0,\"调度通信\",\"\"),IFERROR(IF(FIND(\"自动化\",P%d)>0,\"调度自动化\",\"\"),IFERROR(IF(FIND(\"自动化\",AP%d)>0,\"调度自动化\",\"\"),IFERROR(IF(FIND(\"通信\",AP%d)>0,\"调度通信\",\"\"),IFERROR(IF(FIND(\"二次智能运维班\",AP%d)>0,\"调度继保\",\"\"),\"\")))))))))))";
        String riskFormula = "IFERROR(IF(FIND(\"可\",Q%d)>0,\"可接受\",\"\"),IFERROR(IF(FIND(\"低\",Q%d)>0,\"低风险\",\"\"),IFERROR(IF(FIND(\"中\",Q%d)>0,\"中风险\",\"\"),IFERROR(IF(FIND(\"高\",Q%d)>0,\"高风险\",\"\"),\"低风险\"))))";
//        String runningClassFormula = "IF(B%d=\"本单位\",IFERROR(VLOOKUP(AD%d,班组人员基础数据!A:B,2,0),\"\"),IFERROR(VLOOKUP(AB%d,班组人员基础数据!A:B,2,0),\"\"))";
        String calcRunningClassExistedFormula = "IF(K%d=\"本单位\",IFERROR(VLOOKUP(R%d,班组人员基础数据!A:E,4,0),\"1\"),IFERROR(VLOOKUP(AL%d,班组人员基础数据!A:E,4,0),\"0\"))";
        String calcRiskFormulaOutputValueFormula = "IF(K%d=\"本单位\",IFERROR(VLOOKUP(R%d,班组人员基础数据!A:E,4,0),\"0\"),IFERROR(VLOOKUP(AL%d,班组人员基础数据!A:E,4,0),\"0\"))+IF(COUNTIFS(AO%d,\"可接受\")=0,IF(COUNTIFS(AO%d,\"低风险\")=0,IF(COUNTIFS(AO%d,\"中风险\")=0,IF(COUNTIFS(AO%d,\"高风险\")=0,0,10000),1000),100),10)";
//        String actualWorkerCountFormula = "IF(B%d=\"本单位\",L%d,0)";
        switch (columnName) {
//            case "管理部门":
//                cell.setCellFormula(departmentFormula.replaceAll("%d", rowNum + ""));
//                break;
            case "风险值":
                // SXSSFCell#setCellFormulaImpl
//                cell.setCellValue("");// Libreoffice 不重新计算 cellType=BLANK会显示数字0，先设置cell为字符串格式，再设置公式
                cell.setCellFormula(riskFormula.replaceAll("%d", rowNum + ""));
                break;
//            case "运行班组":
//                cell.setCellFormula(runningClassFormula.replaceAll("%d", rowNum + ""));
//                break;
            case "计算是否有班组":
//                cell.setCellValue("");
                cell.setCellFormula(calcRunningClassExistedFormula.replaceAll("%d", rowNum + ""));
                break;
            case "计算风险公式输出值":
//                cell.setCellValue("");
                cell.setCellFormula(calcRiskFormulaOutputValueFormula.replaceAll("%d", rowNum + ""));
                break;
//            case "实际本单位工作人数":
//                cell.setCellFormula(actualWorkerCountFormula.replaceAll("%d", rowNum + ""));
//                break;
            default:;
        }


    }

}

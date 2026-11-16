package com.zhuofan.core.util;

import org.apache.commons.lang3.StringUtils;
import org.apache.poi.hssf.usermodel.*;
import org.apache.poi.ss.usermodel.BorderStyle;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.*;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.util.ResourceUtils;

import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.HashMap;

public class ExcelUtil {

    /**
     * @Title: 自动列宽
     * @Description:自动列宽
     * @return
     * @throws Exception
     * @author 广州卓凡扶小冬
     * @throws
     */
    public static void autoSizeColumn(HSSFSheet sheet,int colCount) {
        //设置自动列宽
        for (int i = 0; i < colCount; i++) {
            sheet.autoSizeColumn(i);
            sheet.setColumnWidth(i, sheet.getColumnWidth(i) * 17 / 10);
        }
    }

    /**
     * @Title: 创建多个单元格
     * @Description:创建多个单元格
     * @return
     * @throws Exception
     * @author 广州卓凡扶小冬
     * @throws
     */
    public static void createCellMult(HSSFRow row,int cellIndex,HSSFCellStyle cellStyle,String text) {
        String[] arrText=text.split(",");
        ExcelUtil.createCellMult(row,cellIndex,cellStyle,arrText);
    }


    /**
     * @Title: 创建多个单元格
     * @Description:创建多个单元格
     * @return
     * @throws Exception
     * @author 广州卓凡扶小冬
     * @throws
     */
    public static void createCellMult(HSSFRow row,int cellIndex,HSSFCellStyle cellStyle,String[] arrText) {
        for (int i=0;i<arrText.length;i++) {
            HSSFCell cell = row.createCell(cellIndex+i);
            cell.setCellValue(arrText[i]);
            cell.setCellStyle(cellStyle);
        }
    }

    /**
     * @Title: 创建新行
     * @Description:创建新行
     * @return
     * @throws Exception
     * @author 广州卓凡扶小冬
     * @throws
     */
    public static HSSFRow createRow(HSSFSheet sheet,int rowIndex,int rowHeight) {
        HSSFRow row=sheet.createRow(rowIndex);
        row.setHeight((short) rowHeight);
        return row;
    }

    /**
     * @Title: 创建新单元格
     * @Description:创建新单元格
     * @return
     * @throws Exception
     * @author 广州卓凡扶小冬
     * @throws
     */
    public static HSSFCell createCell(HSSFRow row,int cellIndex,HSSFCellStyle cellStyle,String text) {
        HSSFCell cell = row.createCell(cellIndex);
        cell.setCellValue(text);
        cell.setCellStyle(cellStyle);
        return cell;
    }

    public static XSSFWorkbook getXSSFWorkbook(String sheetName, ArrayList listTitle,
                                               ArrayList listContent,
                                               XSSFWorkbook wb, int actionFlag,
                                               String sheetTitle){
        try {
            System.out.println(ResourceUtils.getURL("classpath:").getPath());
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        if (wb==null){
            wb=new XSSFWorkbook();
        }
        XSSFSheet sheet=wb.createSheet(sheetName);

        XSSFRow row=sheet.createRow(0);
        row.setHeight((short) 500);
        // 4. 创建单元格对象
        XSSFCell cell = row.createCell(0);

        // 5. 向单元格写入内容
        cell.setCellValue(sheetTitle);

        // 6. 设置单元格样式
        XSSFCellStyle cellStyle = wb.createCellStyle();
        cellStyle.setAlignment(HorizontalAlignment.CENTER);
        cell.setCellStyle(cellStyle);

        // 7. 合并单元格
        CellRangeAddress region = new CellRangeAddress(0, 0, 0, listTitle.size()-1);
        sheet.addMergedRegion(region);

        row=sheet.createRow(1);

        XSSFCellStyle style=wb.createCellStyle();
        //style.setAlignment(HSSFCellStyle.ALIGN_CENTER);
        row.setHeight((short) 400);

        // 边框样式---为setRegionStyle设置
        XSSFCellStyle styleBorder = wb.createCellStyle();
        styleBorder.setBorderBottom(BorderStyle.THIN);
        styleBorder.setBorderLeft(BorderStyle.THIN);
        styleBorder.setBorderRight(BorderStyle.THIN);
        styleBorder.setBorderTop(BorderStyle.THIN);
        styleBorder.setWrapText(true);

        cell=null;

        for (int i=0;i<listTitle.size()-actionFlag;i++){
            cell=row.createCell(i);
            cell.setCellValue((String)listTitle.get(i));
            cell.setCellStyle(styleBorder);
            row.setHeight((short) 500);
        }

        for (int i=0;i<listContent.size();i++){
            row=sheet.createRow(i+2);
            row.setHeight((short) 500);
            ArrayList<String> listRow=(ArrayList<String>) listContent.get(i);
            for (int j=1;j<listRow.size()-actionFlag;j++){
                String html=listRow.get(j);
                String noHtml=Jsoup.parse(html).text();
                cell=row.createCell(j-1);
                cell.setCellValue(noHtml);
                cell.setCellStyle(styleBorder);
            }
        }

        //设置自动列宽

        for (int i = 0; i < listTitle.size(); i++) {
            sheet.autoSizeColumn(i);
            sheet.setColumnWidth(i,Math.min(150 * 150, sheet.getColumnWidth(i)*17/10));
        }

        return wb;
    }

    private static void getGZSBItem(HSSFWorkbook wb,int sheetNo,String title ,String sheetTitle,String[] arrColTitle,ArrayList listData){
        HSSFFont fontHeiti=wb.createFont();
        fontHeiti.setFontName("黑体");
        fontHeiti.setFontHeightInPoints((short)16);
        HSSFCellStyle cellStyleHeiti = wb.createCellStyle();
        cellStyleHeiti.setFont(fontHeiti);

        HSSFFont fontTitle=wb.createFont();
        fontTitle.setFontName("方正小标宋_GBK");
        fontTitle.setFontHeightInPoints((short)20);
        HSSFCellStyle cellStyleTitle = wb.createCellStyle();
        cellStyleTitle.setAlignment(HorizontalAlignment.CENTER);
        cellStyleTitle.setFont(fontTitle);

        HSSFFont fontColTitle=wb.createFont();
        fontColTitle.setFontName("宋体");
        fontColTitle.setBold(true);
        fontColTitle.setFontHeightInPoints((short)12);
        HSSFCellStyle cellStyleColTitle = wb.createCellStyle();
        cellStyleColTitle.setAlignment(HorizontalAlignment.CENTER);
        cellStyleColTitle.setFont(fontColTitle);
        cellStyleColTitle.setBorderBottom(BorderStyle.THIN);
        cellStyleColTitle.setBorderLeft(BorderStyle.THIN);
        cellStyleColTitle.setBorderRight(BorderStyle.THIN);
        cellStyleColTitle.setBorderTop(BorderStyle.THIN);

        HSSFFont fontContent=wb.createFont();
        fontContent.setFontName("宋体");
        fontContent.setFontHeightInPoints((short)11);
        HSSFCellStyle cellStyleContent = wb.createCellStyle();
        cellStyleContent.setAlignment(HorizontalAlignment.CENTER);
        cellStyleContent.setFont(fontContent);
        cellStyleContent.setBorderBottom(BorderStyle.THIN);
        cellStyleContent.setBorderLeft(BorderStyle.THIN);
        cellStyleContent.setBorderRight(BorderStyle.THIN);
        cellStyleContent.setBorderTop(BorderStyle.THIN);

        /////////////////////////////////////////////////
        //一.220KV断面+
        HSSFSheet sheet=wb.createSheet(sheetTitle);

        //1.“附件"：黑体16号
        HSSFRow row=sheet.createRow(0);
        row.setHeight((short) 400);
        HSSFCell cell = row.createCell(0);
        cell.setCellValue("附件"+String.valueOf(sheetNo));
        cell.setCellStyle(cellStyleHeiti);

        // 2.”220千伏及以上输变电设备“：标题
        row=sheet.createRow(2);
        row.setHeight((short) 1000);
        cell = row.createCell(0);
        cell.setCellValue(title);
        cell.setCellStyle(cellStyleTitle);

        CellRangeAddress region = new CellRangeAddress(2, 2, 0, listData.size()+1);
        sheet.addMergedRegion(region);

        //3.标题
        cell=null;

        row=sheet.createRow(3);
        for (int i=0;i<arrColTitle.length;i++){
            cell=row.createCell(i);
            cell.setCellValue(arrColTitle[i]);
            cell.setCellStyle(cellStyleColTitle);
            row.setHeight((short) 500);
        }

        for (int i=0;i<listData.size();i++){
            row=sheet.createRow(i+4);
            row.setHeight((short) 500);
            HashMap<String,String> mapRow=(HashMap<String,String>) listData.get(i);
            for (int j=0;j<arrColTitle.length;j++){
                cell=row.createCell(j);
                String value="";
                if (mapRow.containsKey("F"+String.valueOf(j+1))){
                    value=mapRow.get("F"+String.valueOf(j+1));
                }
                cell.setCellValue(value);
                cell.setCellStyle(cellStyleContent);
            }
        }

        //设置自动列宽

        for (int i = 0; i < arrColTitle.length; i++) {
            sheet.autoSizeColumn(i);
            sheet.setColumnWidth(i,sheet.getColumnWidth(i)*17/10);
        }
    }


    public static HSSFWorkbook getWBOfGZSB(ArrayList listDm, ArrayList listLine110, ArrayList listZbbd,ArrayList listZbbd2,
                                           HSSFWorkbook wb){
        try {
            System.out.println(ResourceUtils.getURL("classpath:").getPath());
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        if (wb==null){
            wb=new HSSFWorkbook();
        }

        String[] arrColTitle="序号,断面名称,最大负荷（MW）,断面限值（MW）,负载率（%）,运维单位,备注".split(",");
        getGZSBItem(wb,1,"220千伏及以上输变电设备","220kV及以上输变电设备",arrColTitle,listDm);

        arrColTitle="序号,线路名称,最大负荷（MW）,最大电流值（A）,控制值（A）,负载率（%）,运维单位".split(",");
        getGZSBItem(wb,2,"110千伏线路负荷率","110kV输电线路",arrColTitle,listLine110);

        arrColTitle="序号,设备名称,最大负荷（MW）,最大电流值（A）,控制值（A）（额定值）,红线值,负载率（%）,环境温度,测温方式,测温电流,A相,B相,C相,测温日期及时间,运维单位,备注".split(",");
        getGZSBItem(wb,3,"主变10kV侧负载率（额定值）","主变10kV侧（额定值）",arrColTitle,listZbbd);

        arrColTitle="序号,设备名称,最大负荷（MW）,最大电流值（A）,控制值（A）（红线值）,额定值（A）,负载率（%）,环境温度,测温方式,测温电流,A相,B相,C相,测温日期及时间,运维单位,备注".split(",");
        getGZSBItem(wb,4,"主变10kV侧负载率（红线值）","主变10kV侧（红线值）",arrColTitle,listZbbd2);

        return wb;
    }
}

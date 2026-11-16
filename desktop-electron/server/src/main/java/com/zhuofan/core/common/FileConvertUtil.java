package com.zhuofan.core.common;

import com.artofsolving.jodconverter.DocumentConverter;
import com.artofsolving.jodconverter.openoffice.connection.OpenOfficeConnection;
import com.artofsolving.jodconverter.openoffice.connection.SocketOpenOfficeConnection;
import com.artofsolving.jodconverter.openoffice.converter.StreamOpenOfficeDocumentConverter;
import com.lowagie.text.Element;
import com.lowagie.text.Font;
import com.lowagie.text.Phrase;
import com.lowagie.text.pdf.*;
import com.zhuofan.core.config.SysConfig;
import org.apache.commons.io.FileUtils;

import java.awt.*;
import java.io.File;
import java.io.FileOutputStream;
import java.net.ConnectException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import com.lowagie.text.Document;

/**
 * @author yangkun
 * @createTime 2020年11月18日 14:19:00
 */
public class FileConvertUtil {

    public static boolean officeToPDF(String sourceFile, String destFile, boolean waterFlag, SysConfig sysConfig)  throws Exception{
        try {
            String ext=CommonFunc.getFileExtension(sourceFile);
            File inputFile = new File(sourceFile);

            if (ext.equals("wps")){
                //WPS文档:复制到DOC文件并另存
                String tmpFile=sourceFile.substring(0,sourceFile.length()-inputFile.getName().length())+"tmp_"+String.valueOf(CommonFunc.unixTime())+".doc";
                FileUtils.copyFile(inputFile,new File(tmpFile));
                inputFile=new File(tmpFile);
            }else if (ext.equals("et")){
                //WPS表格:ET,改扩展名为xls文件并另存
                String tmpFile=sourceFile.substring(0,sourceFile.length()-inputFile.getName().length())+"tmp_"+String.valueOf(CommonFunc.unixTime())+".xls";
                FileUtils.copyFile(inputFile,new File(tmpFile));
                inputFile=new File(tmpFile);
            }

            if (!inputFile.exists()) {
                // 找不到源文件, 则返回false
                return false;
            }
            // 如果目标路径不存在, 则新建该路径
            File outputFile = new File(destFile);
            if (!outputFile.getParentFile().exists()) {
                outputFile.getParentFile().mkdirs();
            }
            //如果目标文件存在，则删除
            if (outputFile.exists()) {
                outputFile.delete();
            }
            DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm");
            OpenOfficeConnection connection = new SocketOpenOfficeConnection("127.0.0.1", 8100);
            connection.connect();
            //用于测试openOffice连接时间
            System.out.println("连接时间:" + df.format(new Date()));
            DocumentConverter converter = new StreamOpenOfficeDocumentConverter(
                    connection);
            converter.convert(inputFile, outputFile);
            //测试word转PDF的转换时间
            System.out.println("转换时间:" + df.format(new Date()));
            connection.disconnect();

            if (waterFlag) {
                String tmpDestFile=destFile+".tmp.pdf";
                addFooterAndWater(destFile, tmpDestFile, sysConfig.getEfilePdfwaterMarkname(), sysConfig.getEfilePdfwaterPageheader(), sysConfig.getEfilePdfwaterFoot());
                FileUtils.forceDelete(new File(destFile));
                FileUtils.moveFile(new File(tmpDestFile),new File(destFile));
            }

            return true;
        } catch (ConnectException e) {
            e.printStackTrace();
            System.err.println("openOffice连接失败！请检查IP,端口");
        } catch (Exception e) {
            e.printStackTrace();
        }

        return false;
    }

    /**
     *	添加水印、页眉、页脚
     * @param fileName 源文件路径
     * @param savepath 目标文件路径
     * @param waterMarkName 文字水印
     * @param pageHeade 页眉
     * @param foot 页脚
     * @return
     */
    public static int addFooterAndWater(String fileName, String savepath,
                                        String waterMarkName, String pageHeade, String foot)
    {
        // 文档总页数
        int num = 0;

        Document document = new Document();
        try
        {
            PdfReader reader = new PdfReader(fileName);
            BaseFont base = BaseFont.createFont("STSong-Light", "UniGB-UCS2-H",
                    BaseFont.EMBEDDED);

            num = reader.getNumberOfPages();
            PdfCopy copy = new PdfCopy(document, new FileOutputStream(savepath));
            document.open();
            for (int i = 0; i < num;)
            {
                PdfImportedPage page = copy.getImportedPage(reader, ++i);
                PdfCopy.PageStamp stamp = copy.createPageStamp(page);
                Font f = new Font(base);

                // 添加页脚，左侧文字，右侧页码
                ColumnText.showTextAligned(stamp.getUnderContent(),
                        Element.ALIGN_RIGHT,
                        new Phrase(String.format("第 %d 页/共 %d 页", i, num), f),
                        550f, 28, 0);
                ColumnText.showTextAligned(stamp.getUnderContent(),
                        Element.ALIGN_LEFT, new Phrase(foot, f), 50f, 28, 0);

                // 添加页眉 (文字页眉，居中)
                ColumnText.showTextAligned(stamp.getUnderContent(),
                        Element.ALIGN_CENTER, new Phrase(pageHeade, f), 150f,
                        800, 0);

                // 页眉添加logo （图片页眉，居右）
                /*Image img = Image.getInstance("template/logo.png");// 选择图片
                img.setAlignment(1);
                img.scaleAbsolute(436 / 5, 96 / 5);// 控制图片大小
                img.setAbsolutePosition(450f, 800);// 控制图片位置
                stamp.getUnderContent().addImage(img);*/

                // 添加水印
                PdfContentByte under = stamp.getUnderContent();
                under.beginText();
                under.setColorFill(Color.LIGHT_GRAY);

                // 字符越长，字体越小，设置字体
                int fontSize = getFontSize(waterMarkName);
                under.setFontAndSize(base, fontSize);

                // 设置水印文字字体倾斜 开始
                float pageWidth = reader.getPageSize(i).getWidth();
                float pageHeight = reader.getPageSize(i).getHeight();

                under.showTextAligned(Element.ALIGN_CENTER, waterMarkName,
                        pageWidth / 2, pageHeight / 2, 60);// 水印文字成60度角倾斜,且页面居中展示

                // 字体设置结束
                under.endText();
                stamp.alterContents();
                copy.addPage(page);
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
            return -1;
        }
        finally
        {
            if (null != document)
            {
                document.close();
            }
        }
        System.out.println("pdf totalpages:" + num);
        return num;

    }

    /**
     * 根据水印文字长度计算获取字体大小
     * @param waterMarkName
     * @return
     */
    private static int getFontSize(String waterMarkName){
        int fontSize = 80;
        if(null != waterMarkName && !"".equals(waterMarkName)){
            int length = waterMarkName.length();
            if(length <=26 && length >= 18){
                fontSize = 26;
            }else if(length <18 && length >= 8){
                fontSize = 40;
            }else if(length <8 && length >= 1){
                fontSize = 80;
            }else {
                fontSize = 16;
            }
        }
        return fontSize;
    }

    public static void main(String[] args) throws Exception{
        officeToPDF("D:\\work\\2020\\097.汕头\\汕头移动调度模块清单.docx","d:/poi-test/开发计划.pdf",true,new SysConfig());
    }

}

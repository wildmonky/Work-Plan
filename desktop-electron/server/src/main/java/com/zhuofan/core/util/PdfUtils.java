package com.zhuofan.core.util;

import com.zhuofan.core.common.CommonFunc;
import com.zhuofan.core.common.ServerKeyIv;
import org.apache.pdfbox.Loader;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.rendering.PDFRenderer;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class PdfUtils {

    public static ArrayList<String> pdf2png(String pdfPath,int dpi,String outputDir,String outputFileName){
        // 创建输出目录
        File outDir = new File(outputDir);
        if (!outDir.exists()) {
            outDir.mkdirs();
        }

        ArrayList<String> list=new ArrayList<String>();
        try (PDDocument document = Loader.loadPDF(new File(pdfPath))) {
            PDFRenderer renderer = new PDFRenderer(document);
            List<String> generatedFiles = new ArrayList<>();

            for (int page = 0; page < document.getNumberOfPages(); page++) {
                BufferedImage image = renderer.renderImageWithDPI(page, dpi);
                int w = image.getWidth();
                int h = image.getHeight();
                BufferedImage rotated = new BufferedImage(h, w, image.getType());

                for (int y = 0; y < h; y++) {
                    for (int x = 0; x < w; x++) {
                        rotated.setRGB(y, w - 1 - x, image.getRGB(x, y));
                    }
                }
                int targetWidth = 1751; // 设置目标宽度
                int targetHeight = 2648;

                BufferedImage resized = new BufferedImage(targetWidth, targetHeight, rotated.getType());
                Graphics2D g2d = resized.createGraphics();

                // 设置渲染质量
                g2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION,
                        RenderingHints.VALUE_INTERPOLATION_BILINEAR);
                g2d.setRenderingHint(RenderingHints.KEY_RENDERING,
                        RenderingHints.VALUE_RENDER_QUALITY);
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                        RenderingHints.VALUE_ANTIALIAS_ON);

                // 绘制并缩放图片
                g2d.drawImage(rotated, 0, 0, targetWidth, targetHeight, null);
                g2d.dispose();

                // 生成文件名
                String fileName = outputDir+"/"+outputFileName+"-"+(page+1)+".png";
                File outputFile = new File(fileName);

                // 保存为PNG
                ImageIO.write(rotated, "PNG", outputFile);
                generatedFiles.add(outputFile.getAbsolutePath());

                System.out.println("页面 " + (page + 1) + " 已保存: " + outputFile.getAbsolutePath());

                list.add(fileName);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return list;
    }

    public static void main(String[] args) {
        ArrayList<String> list= pdf2png("D:\\download\\111.pdf",600,"d:/","test");
        System.out.println(list);
    }
}

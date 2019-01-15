package com.mslife.proposal.image.util;

import com.sun.imageio.plugins.gif.GIFImageWriterSpi;
import com.twelvemonkeys.imageio.plugins.tiff.TIFFImageWriterSpi;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.rendering.ImageType;
import org.apache.pdfbox.rendering.PDFRenderer;
import javax.imageio.*;
import javax.imageio.spi.ImageWriterSpi;
import javax.imageio.stream.ImageOutputStream;
import java.awt.image.BufferedImage;
import java.io.*;

public class Pdf2ImgUtil {

    public static void pdfPage2Gif(String pdfPath) {

        int DPI = 160; //图片的像素

        PDDocument doc = null;
        File pdfFile = new File(pdfPath);

        String parent = pdfFile.getParent();
        String gifPath = parent + File.separator + "gif";

        File gifDir = new File(gifPath);
        if (!gifDir.exists()) {
            gifDir.mkdirs();
        }

        try {
            doc = PDDocument.load(pdfFile);

            int pageCount = doc.getNumberOfPages();
            PDFRenderer renderer = new PDFRenderer(doc); // 根据PDDocument对象创建pdf渲染器

            ImageWriterSpi imageWriterSpi = new GIFImageWriterSpi();

            for (int i = 0; i < pageCount; i++) {
                ImageWriter writerInstance = imageWriterSpi.createWriterInstance();

                BufferedImage image = renderer.renderImageWithDPI(i, DPI, ImageType.RGB);

                FileOutputStream out = new FileOutputStream(gifPath + File.separator + i + ".gif");
                BufferedOutputStream bos = new BufferedOutputStream(out);
                ImageOutputStream ios = ImageIO.createImageOutputStream(bos);

                writerInstance.setOutput(ios);

                writerInstance.write(image);
                ios.close();
                writerInstance.dispose();
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (doc != null) {
                try {
                    doc.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public static void pdf2MultiTiff(String pdfPath) {

        File pdfFile = new File(pdfPath);
        String parent = pdfFile.getParent();
        String tifPath = parent + File.separator + "tif";

        File tifDir = new File(tifPath);
        if (!tifDir.exists()) {
            tifDir.mkdirs();
        }

        PDDocument doc = null;
        try {
            doc = PDDocument.load(pdfFile);
            int pageCount = doc.getNumberOfPages();
            PDFRenderer renderer = new PDFRenderer(doc); // 根据PDDocument对象创建pdf渲染器

            TIFFImageWriterSpi tiffImageWriterSpi = new TIFFImageWriterSpi();

            for (int i = 0; i < pageCount; i++) {
                BufferedImage image = renderer.renderImageWithDPI(i, 420, ImageType.BINARY);
                ImageWriter writerInstance = tiffImageWriterSpi.createWriterInstance();

                ImageWriteParam param = writerInstance.getDefaultWriteParam();
                param.setCompressionMode(ImageWriteParam.MODE_EXPLICIT);
                param.setCompressionType("CCITT T.6");

                FileOutputStream fos = new FileOutputStream(tifDir + File.separator + i + ".tif");
                BufferedOutputStream bos = new BufferedOutputStream(fos);

                ImageOutputStream imageOutputStream = ImageIO.createImageOutputStream(bos);

                writerInstance.setOutput(imageOutputStream);

                writerInstance.write(null, new IIOImage(image, null, null), param);

                writerInstance.dispose();

                imageOutputStream.close();
                fos.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (doc != null)
                    doc.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

}

























package com.mslife.proposal.image.util;

import com.itextpdf.text.Document;
import com.itextpdf.text.pdf.PdfWriter;
import com.itextpdf.tool.xml.Pipeline;
import com.itextpdf.tool.xml.XMLWorker;
import com.itextpdf.tool.xml.XMLWorkerHelper;
import com.itextpdf.tool.xml.html.*;
import com.itextpdf.tool.xml.parser.XMLParser;
import com.itextpdf.tool.xml.pipeline.css.CSSResolver;
import com.itextpdf.tool.xml.pipeline.css.CssResolverPipeline;
import com.itextpdf.tool.xml.pipeline.end.PdfWriterPipeline;
import com.itextpdf.tool.xml.pipeline.html.HtmlPipeline;
import com.itextpdf.tool.xml.pipeline.html.HtmlPipelineContext;
import com.twelvemonkeys.io.StringArrayReader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;

public class ItextUtil {

    private static Logger logger = LoggerFactory.getLogger(ItextUtil.class);

    public static void html2Pdf(String htmlStr, String outfile) {
        try {
            DefaultTagProcessorFactory tpf=(DefaultTagProcessorFactory)Tags.getHtmlTagProcessorFactory();
            tpf.addProcessor(HTML.Tag.IMG, Image.class.getName());

            Document document = new Document();
            PdfWriter writer = PdfWriter.getInstance(document, new FileOutputStream(outfile));

            document.open();
            AsianFontProvider fontProvider = new AsianFontProvider();
            fontProvider.addFontSubstitute("lowagie", "garamond");
            fontProvider.setUseUnicode(true);
            //使用我们的字体提供器，并将其设置为unicode字体样式
            CssAppliers cssAppliers = new CssAppliersImpl(fontProvider);
            HtmlPipelineContext htmlContext = new HtmlPipelineContext(cssAppliers);
            htmlContext.setTagFactory(tpf);

            CSSResolver cssResolver = XMLWorkerHelper.getInstance()
                    .getDefaultCssResolver(true);
            Pipeline<?> pipeline = new CssResolverPipeline(cssResolver, new HtmlPipeline(htmlContext,
                    new PdfWriterPipeline(document, writer)));
            XMLWorker worker = new XMLWorker(pipeline, true);
            XMLParser p = new XMLParser(worker);
            //File input = new File(infile);

            ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(htmlStr.getBytes());
            p.parse(byteArrayInputStream);
            document.close();
            writer.close();
            byteArrayInputStream.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}


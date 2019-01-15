package com.mslife.proposal.image.util;

import com.itextpdf.text.BadElementException;
import com.itextpdf.text.Chunk;
import com.itextpdf.text.Element;
import com.itextpdf.text.pdf.PdfName;
import com.itextpdf.text.pdf.PdfString;
import com.itextpdf.text.xml.XMLUtil;
import com.itextpdf.tool.xml.NoCustomContextException;
import com.itextpdf.tool.xml.Tag;
import com.itextpdf.tool.xml.WorkerContext;
import com.itextpdf.tool.xml.css.CssUtils;
import com.itextpdf.tool.xml.exceptions.LocaleMessages;
import com.itextpdf.tool.xml.exceptions.RuntimeWorkerException;
import com.itextpdf.tool.xml.html.AbstractTagProcessor;
import com.itextpdf.tool.xml.html.HTML;
import com.itextpdf.tool.xml.net.ImageRetrieve;
import com.itextpdf.tool.xml.net.exc.NoImageException;
import com.itextpdf.tool.xml.pipeline.html.HtmlPipelineContext;
import org.apache.tomcat.util.codec.binary.Base64;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class Image extends AbstractTagProcessor {

    private final CssUtils utils = CssUtils.getInstance();
    private static final Logger logger = LoggerFactory.getLogger(Image.class);

    /*
     * (non-Javadoc)
     * @see
     * com.itextpdf.tool.xml.TagProcessor#endElement(com.itextpdf.tool.xml.Tag,
     * java.util.List, com.itextpdf.text.Document)
     */
    @Override
    public List<Element> end(final WorkerContext ctx, final Tag tag, final List<Element> currentContent) {

        Map<String, String> attributes = tag.getAttributes();
        String src = attributes.get(HTML.Attribute.SRC);
        com.itextpdf.text.Image img = null;
        List<Element> elements = new ArrayList<Element>(1);
        if (null != src && src.length() > 0) {
            // check if the image was already added once
            src = XMLUtil.unescapeXML(src);
            src = src.trim();
            try {
                if(src.matches("^data:image/.{1,10};base64,.+$")){//base64的图片数据
                    //byte[] imgData=Base64.decodeBase64(src.substring(0,src.indexOf("base64,")));
                    byte[] imgData= Base64.decodeBase64(src.substring(src.indexOf("base64,")+7));
                    try {
                        img=com.itextpdf.text.Image.getInstance(imgData);
                    } catch (BadElementException | IOException e) {
                        throw new RuntimeException(e);
                    }
                }else{
                    HtmlPipelineContext context = getHtmlPipelineContext(ctx);
                    img = new ImageRetrieve(context.getResourcesRootPath(), context.getImageProvider()).retrieveImage(src);
                }
            } catch (NoImageException e) {
                 e.printStackTrace();
            } catch (NoCustomContextException e) {
                throw new RuntimeWorkerException(LocaleMessages.getInstance().getMessage(LocaleMessages.NO_CUSTOM_CONTEXT), e);
            }
            if (null != img) {
                try {
                    if ( attributes.get(HTML.Attribute.ALT) != null) {
                        img.setAccessibleAttribute(PdfName.ALT, new PdfString(attributes.get(HTML.Attribute.ALT)));
                    }
                    HtmlPipelineContext htmlPipelineContext = getHtmlPipelineContext(ctx);
                    elements.add(getCssAppliers().apply(new Chunk((com.itextpdf.text.Image) getCssAppliers().apply(img, tag, htmlPipelineContext), 0, 0, true), tag, htmlPipelineContext));
                } catch (NoCustomContextException e) {
                    throw new RuntimeWorkerException(e);
                }
            }
        }
        return elements;
    }

    /*
     * (non-Javadoc)
     * @see com.itextpdf.tool.xml.TagProcessor#isStackOwner()
     */
    @Override
    public boolean isStackOwner() {
        return false;
    }
}
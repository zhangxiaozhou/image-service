package com.mslife.proposal.image.service.impl;

import com.mslife.proposal.image.dto.User;
import com.mslife.proposal.image.service.ProposalService;
import com.mslife.proposal.image.util.ItextUtil;
import com.mslife.proposal.image.util.Pdf2ImgUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;
import org.thymeleaf.util.FastStringWriter;
import javax.annotation.Resource;
import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.UUID;

@Service
public class ProposalServiceImpl implements ProposalService {

    private final Logger logger = LoggerFactory.getLogger(ProposalServiceImpl.class);

    @Resource
    TemplateEngine templateEngine;

    @Override
    public String genPdfFromUser(User user) throws IOException {

        //构造上下文(Model)
        Context context = new Context();
        context.setVariable("users", Arrays.asList(new User[]{user}));

        UUID uuid = UUID.randomUUID();

        //渲染模板
        //String filepath = "d:\\proposal\\"+uuid;

        String filepath = "/home/allen/proposal/"+uuid;

        File dir = new File(filepath);
        if(!dir.exists()){
            dir.mkdirs();
        }

        String pdfPath = filepath + File.separator + uuid + ".pdf";

        Long t1 = System.currentTimeMillis();

        FastStringWriter fastStringWriter = new FastStringWriter();
        templateEngine.process("temp", context, fastStringWriter);

        Long t2 = System.currentTimeMillis();
        logger.info("生成html时间-------------------------"+String.valueOf(t2-t1));

        ItextUtil.html2Pdf(fastStringWriter.toString(), pdfPath);

        Long t3 = System.currentTimeMillis();
        logger.info("html字符串转pdf时间-----------"+String.valueOf(t3-t2));

        Pdf2ImgUtil.pdfPage2Gif(pdfPath);

        Long t4 = System.currentTimeMillis();
        logger.info("pdf转gif时间-----------"+String.valueOf(t4-t3));

        Pdf2ImgUtil.pdf2MultiTiff(pdfPath);

        Long t5 = System.currentTimeMillis();
        logger.info("pdf转tif时间-----------"+String.valueOf(t5-t4));

        return filepath;
    }
}

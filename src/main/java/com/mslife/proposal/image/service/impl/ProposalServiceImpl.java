package com.mslife.proposal.image.service.impl;

import com.mslife.proposal.image.dto.User;
import com.mslife.proposal.image.service.ProposalService;
import com.mslife.proposal.image.util.ItextUtil;
import com.mslife.proposal.image.util.Pdf2TiffUtil;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;
import javax.annotation.Resource;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Arrays;
import java.util.Random;

@Service
public class ProposalServiceImpl implements ProposalService {

    @Resource
    TemplateEngine templateEngine;

    private Random random = new Random(10000);

    @Override
    public String genPdfFromUser(User user) throws IOException {

        //构造上下文(Model)
        Context context = new Context();
        context.setVariable("users", Arrays.asList(new User[]{user}));

        //渲染模板
        String filename = "user" + random.nextLong();
        String filepath = "d:\\proposal-image\\"+filename;

        String htmlPath = filepath + ".html";
        String pdfPath = filepath + ".pdf";
        String tiffPath = filepath + ".tif";

        FileWriter write = new FileWriter(htmlPath);
        templateEngine.process("temp1", context, write);

        write.close();

        ItextUtil.html2Pdf(htmlPath, pdfPath);

        //pdf转tif
        FileInputStream fis = new FileInputStream(pdfPath);
        FileOutputStream fos = new FileOutputStream(tiffPath);

        Pdf2TiffUtil.pdf2Tiff(fis, fos);

        fos.close();
        fis.close();

        return filepath;
    }
}

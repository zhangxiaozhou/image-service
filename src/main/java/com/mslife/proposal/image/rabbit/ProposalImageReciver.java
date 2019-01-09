package com.mslife.proposal.image.rabbit;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mslife.proposal.image.dto.User;
import com.mslife.proposal.image.service.ProposalService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;
import javax.annotation.Resource;
import java.io.IOException;

@Component
public class ProposalImageReciver {

    Logger logger = LoggerFactory.getLogger(ProposalImageReciver.class);

    @Resource
    private ObjectMapper objectMapper;

    @Resource
    private ProposalService proposalService;

    @RabbitListener(queues = "proposal.image")
    public void process(Message message) throws IOException {

        byte[] body = message.getBody();

        User user = objectMapper.readValue(body, User.class);

        logger.info("Receiver  : " + user);

        proposalService.genPdfFromUser(user);
    }
}

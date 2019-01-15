package com.mslife.proposal.image.rabbit;

import com.mslife.proposal.image.dto.User;
import com.mslife.proposal.image.service.ProposalService;
import com.rabbitmq.client.Channel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.Exchange;
import org.springframework.amqp.rabbit.annotation.Queue;
import org.springframework.amqp.rabbit.annotation.QueueBinding;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.support.AmqpHeaders;
import org.springframework.messaging.handler.annotation.Headers;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;
import javax.annotation.Resource;
import java.io.IOException;
import java.util.Map;

@Component
public class ProposalImageReceiver {

    Logger logger = LoggerFactory.getLogger(ProposalImageReceiver.class);

    @Resource
    private ProposalService proposalService;

    @RabbitListener( bindings = @QueueBinding(
            value = @Queue(value = "proposal.image", durable = "true"),
            exchange = @Exchange(value = "image.exchange", ignoreDeclarationExceptions = "true"),
            key = "proposal.image"
    ))
    public void process(@Payload User user, Channel channel, @Headers Map<String,Object> headers) throws IOException {

        channel.basicQos(1);

        logger.info("Receiver  : " + user);

        proposalService.genPdfFromUser(user);

        Long deliveryTag = (Long)headers.get(AmqpHeaders.DELIVERY_TAG);

        logger.info("deliveryTag--------------" + deliveryTag);

        channel.basicAck(deliveryTag, false);
    }

}

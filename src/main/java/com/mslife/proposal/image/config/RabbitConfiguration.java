package com.mslife.proposal.image.config;

import org.springframework.amqp.core.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitConfiguration {

    @Bean
    public Exchange exchange(){
        return new DirectExchange("image.exchange", true, false);
    }

    @Bean
    public Queue queue() {
        return new Queue("proposal.image", true);
    }

    @Bean
    public Binding binding(){
        return BindingBuilder.bind(queue()).to(exchange()).with("proposal.image").noargs();
    }
}

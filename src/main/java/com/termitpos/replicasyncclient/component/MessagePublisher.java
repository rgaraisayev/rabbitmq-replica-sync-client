package com.termitpos.replicasyncclient.component;

import com.termitpos.replicasyncclient.model.SyncActionEvent;
import org.springframework.amqp.rabbit.AsyncRabbitTemplate;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Component;
import org.springframework.util.concurrent.ListenableFuture;

import java.io.Serializable;

@Component
public class MessagePublisher {

    private final AsyncRabbitTemplate asyncRabbitTemplate;
    private final RabbitTemplate rabbitTemplate;

    @Autowired
    public MessagePublisher(AsyncRabbitTemplate asyncRabbitTemplate, RabbitTemplate rabbitTemplate) {
        this.asyncRabbitTemplate = asyncRabbitTemplate;
        this.rabbitTemplate = rabbitTemplate;
    }

    public <T extends Serializable> void publish(String exchange, String routingKey, T event) {
        rabbitTemplate.convertAndSend(exchange, routingKey, event);
    }

    public ListenableFuture<SyncActionEvent> publishAndReceive(String exchange, String routingKey, SyncActionEvent event) {
        return asyncRabbitTemplate.convertSendAndReceiveAsType(
                exchange,
                routingKey,
                event, new ParameterizedTypeReference<>() {
                });
    }

}
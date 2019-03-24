package com.github.isagron.rabbitmqentrypoint.domain.elements;

import java.util.ArrayList;
import java.util.List;

import lombok.Data;
import lombok.Singular;
import org.springframework.amqp.core.FanoutExchange;

@Data
public class RabbitTopicInfo {

    @Singular
    private FanoutExchange fanoutExchange;

    private List<String> queuesName;

    public RabbitTopicInfo(FanoutExchange fanoutExchange) {
        this.fanoutExchange = fanoutExchange;
        this.queuesName = new ArrayList<>();
    }

    public void addQueueName(String name) {
        this.queuesName.add(name);
    }
}
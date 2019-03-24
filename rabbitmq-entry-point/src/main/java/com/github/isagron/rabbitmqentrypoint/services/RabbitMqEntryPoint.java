package com.github.isagron.rabbitmqentrypoint.services;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import com.github.isagron.rabbitmqentrypoint.domain.elements.RabbitTopicInfo;
import com.innon.ddw.mq.MessageReceiver;
import com.innon.ddw.mq.MqEntryPoint;
import com.innon.ddw.mq.exceptions.MqTopicNotExistException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.AmqpAdmin;
import org.springframework.amqp.core.AnonymousQueue;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.FanoutExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.rabbit.listener.SimpleMessageListenerContainer;
import org.springframework.amqp.rabbit.listener.adapter.MessageListenerAdapter;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class RabbitMqEntryPoint<T extends MessageReceiver> implements MqEntryPoint<T> {

    private Map<String, RabbitTopicInfo> fanoutExchangeMap = new HashMap<>();

    @Autowired
    private RabbitTemplate template;

    @Autowired
    private AmqpAdmin amqpAdmin;

    @Autowired
    private Jackson2JsonMessageConverter converter;

    /**
     * Create new Rabbit exchange of type fanout
     * Add the new fanout to the container
     *
     * @param topic - the name of the fanout
     */
    @Override
    public void register(String topic) {
        FanoutExchange fanoutExchange = new FanoutExchange(topic);
        this.amqpAdmin.declareExchange(fanoutExchange);
        fanoutExchangeMap.put(topic, new RabbitTopicInfo(fanoutExchange));
    }

    @Override
    public void unregister(String topic) {
        fanoutExchangeMap.remove(topic);
        this.amqpAdmin.deleteExchange(topic);
    }

    /**
     * precondition - topic already register (fanout already created)
     * create new anonymousQueue
     *
     * @param topic           - the we want to subscribe on, the fanout name
     * @param messageReceiver - MessageReceiver class which should handle the message
     */
    @Override
    public void subscribe(String topic, T messageReceiver) {
        if (!this.fanoutExchangeMap.containsKey(topic)) {
            throw new MqTopicNotExistException(topic);
        }
        Queue queue = new AnonymousQueue();
        amqpAdmin.declareQueue(queue);
        this.fanoutExchangeMap.get(topic).addQueueName(queue.getName());
        setupListener(messageReceiver, queue);
        amqpAdmin.declareBinding(BindingBuilder.bind(queue).to(fanoutExchangeMap.get(topic).getFanoutExchange()));
    }

    private void setupListener(T messageReceiver, Queue queue) {
        MessageListenerAdapter adapter = new MessageListenerAdapter(messageReceiver, "receive");
        adapter.setMessageConverter(converter);
        SimpleMessageListenerContainer container = new SimpleMessageListenerContainer();
        container.setConnectionFactory(template.getConnectionFactory());
        container.setQueueNames(queue.getName());
        container.setMessageListener(adapter);
        container.start();
    }

    /**
     *
     * @param topic
     * @param message
     */
    @Override
    public void publish(String topic, Object message) {
        template.convertAndSend(topic, "", message);
    }

    @Override
    public void shutdown() {
        this.fanoutExchangeMap.forEach((topic, resources) -> {
            amqpAdmin.deleteExchange(topic);
            resources.getQueuesName().forEach(queueName -> {
                Properties queueProperties = amqpAdmin.getQueueProperties(queueName);
                System.out.println(queueProperties);
                //amqpAdmin.deleteQueue(queueName);
            });
        });
    }

    @Override
    public List<String> getRegisterTopics() {
        return new ArrayList<>(this.fanoutExchangeMap.keySet());
    }
}
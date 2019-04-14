# RabbitMq - Messaging Queue Entry Point

> A spring base application implementing the MqEntryPoint interface using RabbitMq. 
The MqEntryPoint provide interface for publish-subscribe mechanism.
## Setup
#### Gradle Dependency
```groovy
repositories {
    maven { url "https://oss.sonatype.org/content/groups/public" }
}

compile 'com.github.isagron:rabbitmq-entry-point:1.0.0-SNAPSHOT'
```

## Main Interfaces
#### MqEntryPoint Interface
````java
public interface MqEntryPoint<T extends MessageReceiver> {
    void register(String topic);

    void unregister(String topic);

    void subscribe(String topic, T messageReceiver);

    void publish(String topic, Object message);

    void shutdown();

    List<String> getRegisterTopics();
}
````

This interface apply for the producer (publisher) and the consumers (subscribers).
The future goal is to create different implementation of this interface base on common messaging queues provider such as RabbitMq and Kafka.
#### MessageReceiver Interface
````java
public interface MessageReceiver<MESSAGE> {

    void receive(MESSAGE message);

    MessageHandler<MESSAGE> getHandler();

    void setMessageHandler(MessageHandler<MESSAGE> messageHandler);
}
````
This interface element created by the subscriber to handle new incoming messages.
You can always used the GenericMessageReceiver.class, and set the message handler.

## Usage
Before publish or subscribe to a topic, you must register the topic for track. Once register you can publish or subscribe with multiple consumers and producers.

#####Producer
````java
public class MyProducer{
    @Autowired
    private RabbitMqEntryPoint mqEntryPoint;
    
    public void send(String topic, Message message){
        mqEntryPoint.register(topic); //need only once
        mqEntryPoint.publish(topic, message); //the message will be sent to all subscribers for topic
    }
}
````

#####Consumer
````java
public class MyConsumer{
    @Autowired
    private RabbitMqEntryPoint mqEntryPoint;
    
    
    public void subscribe(String topic){
        mqEntryPoint.register(topic); //need only once
        mqEntryPoint.subscribe(topic, (message)->
            doSomethingWithTheMessage(message)
        );
    }
}
````

## License

[Apache-2.0](http://www.apache.org/licenses/LICENSE-2.0)
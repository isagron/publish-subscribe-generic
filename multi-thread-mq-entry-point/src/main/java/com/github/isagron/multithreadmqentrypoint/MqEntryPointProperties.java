package com.github.isagron.multithreadmqentrypoint;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "mq-entry-point.implementations.array-blocking-queue")
@Getter
@Setter
public class MqEntryPointProperties {

    private Integer numOfAsyncReceivers;
}
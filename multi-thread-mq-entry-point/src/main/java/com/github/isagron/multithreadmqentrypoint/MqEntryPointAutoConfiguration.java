package com.github.isagron.multithreadmqentrypoint;

import com.innon.ddw.mq.MqEntryPoint;
import com.innon.ddw.mq.impl.ArrayBlockingMQEntryPoint;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConditionalOnClass(MqEntryPoint.class)
@EnableConfigurationProperties(MqEntryPointProperties.class)
public class MqEntryPointAutoConfiguration {

    @Autowired
    private MqEntryPointProperties properties;

    @Bean
    @ConditionalOnMissingBean
    public MqEntryPoint mqEntryPoint() {
        if (properties.getNumOfAsyncReceivers() == null) {
            return new ArrayBlockingMQEntryPoint(5);
        }
        return new ArrayBlockingMQEntryPoint(properties.getNumOfAsyncReceivers());
    }
}
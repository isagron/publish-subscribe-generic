package com.github.isagron.mqentrypointautoconfigure.conditions;

import com.innon.ddw.mq.MqEntryPoint;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Conditional;
import org.springframework.context.annotation.Configuration;

@Configuration
public class MqEntryPointSourceConfiguration {

    @Bean(name="mqEntryPoint")
    @Conditional(value=BlockingQueueCondition.class)
    public MqEntryPoint getMqSource() {
        return null;
    }

    @Bean(name="dataSource")
    @Conditional(BlockingQueueCondition.class)
    public MqEntryPoint getProdDataSource() {
        return null;
    }

}

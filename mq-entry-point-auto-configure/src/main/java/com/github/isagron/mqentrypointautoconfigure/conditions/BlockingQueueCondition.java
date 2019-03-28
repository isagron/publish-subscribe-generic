package com.github.isagron.mqentrypointautoconfigure.conditions;

import org.springframework.context.annotation.Condition;
import org.springframework.context.annotation.ConditionContext;
import org.springframework.core.type.AnnotatedTypeMetadata;

public class BlockingQueueCondition implements Condition {
    @Override
    public boolean matches(ConditionContext conditionContext, AnnotatedTypeMetadata annotatedTypeMetadata) {
        String mqName = conditionContext.getEnvironment().getProperty(ConstantString.mqTypeProperty);
        return mqName!=null && mqName.equalsIgnoreCase(ConstantString.blockQueueType);
    }
}

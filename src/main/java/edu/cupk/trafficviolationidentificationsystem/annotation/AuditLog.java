package edu.cupk.trafficviolationidentificationsystem.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface AuditLog {
    String actionType(); // 操作类型, 例如 'LOGIN', 'CREATE_USER'
    String targetEntityType(); // 操作对象的类型, 如 'USER', 'WORKFLOW'

    /**
     * 用于提取目标实体ID的SpEL表达式.
     * 例如: '#userId', '#userDto.id', '#result.body.userId'
     */
    String targetEntityIdExpression() default "";
}
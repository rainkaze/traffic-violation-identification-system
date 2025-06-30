package edu.cupk.trafficviolationidentificationsystem.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 自定义审计日志注解.
 * <p>
 * 应用于方法上，用于标记需要记录审计日志的操作。
 * 当一个被此注解标记的方法成功执行后，{@link edu.cupk.trafficviolationidentificationsystem.aspect.AuditLogAspect}
 * 将会自动记录一条操作日志。
 * </p>
 *
 * @see edu.cupk.trafficviolationidentificationsystem.aspect.AuditLogAspect
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface AuditLog {

    /**
     * 操作类型.
     * <p>
     * 定义一个简短、清晰的字符串来描述操作的性质，例如 'LOGIN', 'CREATE_USER', 'UPDATE_WORKFLOW'。
     * 这是审计日志中的一个关键字段，便于后续的筛选和分类。
     * </p>
     *
     * @return 操作类型的字符串.
     */
    String actionType();

    /**
     * 操作目标实体的类型.
     * <p>
     * 描述本次操作主要影响的业务实体是什么，例如 'USER', 'WORKFLOW', 'DEVICE'。
     * </p>
     *
     * @return 目标实体类型的字符串.
     */
    String targetEntityType();

    /**
     * 用于提取目标实体ID的Spring Expression Language (SpEL)表达式.
     * <p>
     * 这个表达式将会在方法执行后被解析，用于从方法参数或返回值中动态提取被操作实体的ID。
     * <ul>
     * <li>如果ID在方法的参数中，可以使用 <code>#参数名</code> 或 <code>#参数对象.属性</code>。例如: <code>'#userId'</code>, <code>'#userDto.id'</code></li>
     * <li>如果ID在方法的返回值中，可以使用 <code>#result</code> 或 <code>#result.属性</code>。例如: <code>'#result.body.userId'</code></li>
     * <li>还可以访问Spring Security的认证对象: <code>#authentication.principal.userId</code></li>
     * </ul>
     * 如果留空，日志中的实体ID字段将为null。
     * </p>
     *
     * @return SpEL表达式字符串.
     */
    String targetEntityIdExpression() default "";
}
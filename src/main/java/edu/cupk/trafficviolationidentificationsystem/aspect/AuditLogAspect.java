package edu.cupk.trafficviolationidentificationsystem.aspect;

import edu.cupk.trafficviolationidentificationsystem.annotation.AuditLog;
import edu.cupk.trafficviolationidentificationsystem.model.User;
import edu.cupk.trafficviolationidentificationsystem.repository.AuditLogMapper;
import jakarta.servlet.http.HttpServletRequest;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.DefaultParameterNameDiscoverer;
import org.springframework.expression.EvaluationContext;
import org.springframework.expression.Expression;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.lang.reflect.Method;
import java.time.LocalDateTime;

/**
 * 审计日志切面.
 * <p>
 * 这是一个AOP切面，它会拦截所有被 {@link AuditLog} 注解标记的方法，
 * 并在方法成功返回后，自动收集信息并记录一条审计日志到数据库。
 * </p>
 */
@Aspect
@Component
public class AuditLogAspect {

    // 1. 引入SLF4J Logger
    private static final Logger log = LoggerFactory.getLogger(AuditLogAspect.class);

    @Autowired
    private AuditLogMapper auditLogMapper;

    private final SpelExpressionParser parser = new SpelExpressionParser();
    private final DefaultParameterNameDiscoverer discoverer = new DefaultParameterNameDiscoverer();

    /**
     * 后置通知 (After Returning Advice).
     * <p>
     * 在任何标记了@AuditLog注解的方法成功执行之后触发此方法。
     * </p>
     *
     * @param joinPoint          连接点，提供了对方法签名、参数等信息的访问。
     * @param auditLogAnnotation 方法上的AuditLog注解实例，用于获取元数据。
     * @param result             被拦截方法的返回值。
     */
    @AfterReturning(pointcut = "@annotation(auditLogAnnotation)", returning = "result")
    public void logAudit(JoinPoint joinPoint, AuditLog auditLogAnnotation, Object result) {
        try {
            // 获取当前认证信息
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            if (authentication == null || !(authentication.getPrincipal() instanceof User)) {
                log.warn("无法获取当前用户信息，审计日志将不被记录。");
                return;
            }
            User currentUser = (User) authentication.getPrincipal();

            // 获取HTTP请求对象
            HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes()).getRequest();

            // 创建并填充日志实体
            edu.cupk.trafficviolationidentificationsystem.model.AuditLog auditRecord = createAuditLogRecord(joinPoint, auditLogAnnotation, result, currentUser, request, authentication);

            // 插入数据库
            auditLogMapper.insert(auditRecord);
            log.info("审计日志已记录: {}", auditRecord.getDetails());

        } catch (Exception e) {
            // 2. 使用 SLF4J 记录异常，而不是打印堆栈跟踪
            log.error("记录审计日志时发生严重错误", e);
        }
    }

    /**
     * 创建并填充一个审计日志记录对象。
     *
     * @return 填充好的 AuditLog 对象.
     */
    private edu.cupk.trafficviolationidentificationsystem.model.AuditLog createAuditLogRecord(JoinPoint joinPoint, AuditLog auditLogAnnotation, Object result, User currentUser, HttpServletRequest request, Authentication authentication) {
        edu.cupk.trafficviolationidentificationsystem.model.AuditLog record = new edu.cupk.trafficviolationidentificationsystem.model.AuditLog();
        record.setUserId(currentUser.getUserId());
        record.setActionType(auditLogAnnotation.actionType());
        record.setTargetEntityType(auditLogAnnotation.targetEntityType());
        record.setClientIpAddress(request.getRemoteAddr());
        record.setCreatedAt(LocalDateTime.now());

        // 解析SpEL表达式以获取目标实体ID
        String targetEntityId = parseTargetEntityId(joinPoint, auditLogAnnotation.targetEntityIdExpression(), result, authentication);
        record.setTargetEntityId(targetEntityId);

        // 生成详细描述
        String details = String.format("用户 '%s' 执行了 '%s' 操作, 目标实体: '%s', 实体ID: '%s'.",
                currentUser.getUsername(),
                record.getActionType(),
                record.getTargetEntityType(),
                record.getTargetEntityId() != null ? record.getTargetEntityId() : "N/A"
        );
        record.setDetails(details);

        return record;
    }

    /**
     * 使用SpEL解析表达式以获取目标实体的ID。
     *
     * @param joinPoint  连接点
     * @param expression SpEL表达式字符串
     * @param result     方法返回值
     * @param authentication Spring Security认证对象
     * @return 解析出的实体ID，如果解析失败或表达式为空则返回null或错误信息。
     */
    private String parseTargetEntityId(JoinPoint joinPoint, String expression, Object result, Authentication authentication) {
        if (expression == null || expression.isEmpty()) {
            return null;
        }

        try {
            MethodSignature signature = (MethodSignature) joinPoint.getSignature();
            Method method = signature.getMethod();
            String[] parameterNames = discoverer.getParameterNames(method);
            Object[] args = joinPoint.getArgs();

            EvaluationContext context = new StandardEvaluationContext();
            if (parameterNames != null) {
                for (int i = 0; i < parameterNames.length; i++) {
                    context.setVariable(parameterNames[i], args[i]);
                }
            }

            // 将关键对象放入SpEL上下文，以便表达式可以访问
            context.setVariable("result", result);
            context.setVariable("authentication", authentication);

            Expression spelExpression = parser.parseExpression(expression);
            Object value = spelExpression.getValue(context);

            return value != null ? value.toString() : null;

        } catch (Exception e) {
            log.error("解析SpEL表达式 '{}' 失败。", expression, e);
            return "SpEL_Error";
        }
    }
}
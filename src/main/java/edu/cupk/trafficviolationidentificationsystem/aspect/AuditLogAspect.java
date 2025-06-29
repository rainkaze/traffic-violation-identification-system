package edu.cupk.trafficviolationidentificationsystem.aspect;

import edu.cupk.trafficviolationidentificationsystem.annotation.AuditLog;
import edu.cupk.trafficviolationidentificationsystem.model.User;
import edu.cupk.trafficviolationidentificationsystem.repository.AuditLogMapper;
import jakarta.servlet.http.HttpServletRequest;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
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

import java.time.LocalDateTime;

@Aspect
@Component
public class AuditLogAspect {

    @Autowired
    private AuditLogMapper auditLogMapper;

    private final SpelExpressionParser parser = new SpelExpressionParser();
    private final DefaultParameterNameDiscoverer discoverer = new DefaultParameterNameDiscoverer();

    @AfterReturning(pointcut = "@annotation(auditLogAnnotation)", returning = "result")
    public void log(JoinPoint joinPoint, AuditLog auditLogAnnotation, Object result) {
        try {
            // 获取认证信息
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            User currentUser = (User) authentication.getPrincipal();

            // 获取HTTP请求
            HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();

            // 创建日志对象
            edu.cupk.trafficviolationidentificationsystem.model.AuditLog log = new edu.cupk.trafficviolationidentificationsystem.model.AuditLog();
            log.setUserId(currentUser.getUserId());
            log.setActionType(auditLogAnnotation.actionType());
            log.setTargetEntityType(auditLogAnnotation.targetEntityType());
            log.setClientIpAddress(request.getRemoteAddr());
            log.setCreatedAt(LocalDateTime.now());

            // 使用SpEL解析目标ID
            String targetId = null;
            String idExpression = auditLogAnnotation.targetEntityIdExpression();

            if (idExpression != null && !idExpression.isEmpty()) {
                MethodSignature signature = (MethodSignature) joinPoint.getSignature();
                String[] parameterNames = discoverer.getParameterNames(signature.getMethod());
                Object[] args = joinPoint.getArgs();

                EvaluationContext context = new StandardEvaluationContext();
                for (int i = 0; i < args.length; i++) {
                    context.setVariable(parameterNames[i], args[i]);
                }
                // 将 authentication 和 result 对象也放入上下文, 以便表达式使用
                context.setVariable("authentication", authentication);
                context.setVariable("result", result);

                try {
                    Expression expression = parser.parseExpression(idExpression);
                    Object value = expression.getValue(context);
                    if (value != null) {
                        targetId = value.toString();
                    }
                } catch (Exception e) {
                    // SpEL表达式解析失败
                    e.printStackTrace();
                    targetId = "SpEL Error";
                }
            }
            log.setTargetEntityId(targetId);

            // 生成操作详情
            String details = String.format("用户 '%s' 执行了 '%s' 操作, 目标实体: '%s', 实体ID: '%s'.",
                    currentUser.getUsername(),
                    log.getActionType(),
                    log.getTargetEntityType(),
                    log.getTargetEntityId()
            );
            log.setDetails(details);

            auditLogMapper.insert(log);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
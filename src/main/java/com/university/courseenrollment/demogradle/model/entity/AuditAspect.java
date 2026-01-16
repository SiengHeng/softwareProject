package com.university.courseenrollment.demogradle.aspect;

import com.university.courseenrollment.demogradle.service.audit.AuditService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

@Slf4j
@Aspect
@Component
@RequiredArgsConstructor
public class AuditAspect {

    private final AuditService auditService;

    @AfterReturning(pointcut = "execution(* com.university.courseenrollment.demogradle.service..*.create*(..))", returning = "result")
    public void logCreate(JoinPoint joinPoint, Object result) {
        try {
            String methodName = joinPoint.getSignature().getName();
            String entityType = extractEntityType(joinPoint.getTarget().getClass().getSimpleName());
            Long entityId = extractEntityId(result);
            
            auditService.logActivity("CREATE", entityType, entityId, 
                "Created " + entityType + " via " + methodName);
        } catch (Exception e) {
            log.error("Error logging create activity", e);
        }
    }

    @AfterReturning(pointcut = "execution(* com.university.courseenrollment.demogradle.service..*.update*(..))", returning = "result")
    public void logUpdate(JoinPoint joinPoint, Object result) {
        try {
            String methodName = joinPoint.getSignature().getName();
            String entityType = extractEntityType(joinPoint.getTarget().getClass().getSimpleName());
            Long entityId = extractEntityId(result);
            
            auditService.logActivity("UPDATE", entityType, entityId, 
                "Updated " + entityType + " via " + methodName);
        } catch (Exception e) {
            log.error("Error logging update activity", e);
        }
    }

    @AfterReturning(pointcut = "execution(* com.university.courseenrollment.demogradle.service..*.delete*(..))")
    public void logDelete(JoinPoint joinPoint) {
        try {
            String methodName = joinPoint.getSignature().getName();
            String entityType = extractEntityType(joinPoint.getTarget().getClass().getSimpleName());
            Object[] args = joinPoint.getArgs();
            Long entityId = args.length > 0 && args[0] instanceof Long ? (Long) args[0] : null;
            
            auditService.logActivity("DELETE", entityType, entityId, 
                "Deleted " + entityType + " via " + methodName);
        } catch (Exception e) {
            log.error("Error logging delete activity", e);
        }
    }

    private String extractEntityType(String serviceName) {
        return serviceName.replace("ServiceImpl", "").replace("Service", "");
    }

    private Long extractEntityId(Object result) {
        if (result == null) return null;
        try {
            return (Long) result.getClass().getMethod("getId").invoke(result);
        } catch (Exception e) {
            return null;
        }
    }
}

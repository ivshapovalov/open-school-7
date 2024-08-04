package com.example.course7.task1.aspect;

import lombok.extern.log4j.Log4j2;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.*;
import org.aspectj.lang.reflect.MethodSignature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

import java.lang.reflect.Parameter;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

@Aspect
@Component
@Log4j2
@ConditionalOnProperty(prefix = "app.logging",name = "enable",havingValue = "true",matchIfMissing = false)
public class LoggingAspect {

    @Pointcut("execution(* com.example.course7.task1.service.UserServiceImpl.*(..))")
    public void userServicePointcut() {
    }

    @Pointcut("execution(* com.example.course7.task1.service.OrderServiceImpl.*(..))")
    public void orderServicePointcut() {
    }

    @AfterThrowing(pointcut = "userServicePointcut() || orderServicePointcut()", throwing = "ex")
    public void logAfterThrowing(JoinPoint joinPoint, Throwable ex) {
        Class invokedClass = joinPoint.getSignature().getDeclaringType();
        Logger logger = LoggerFactory.getLogger(invokedClass);
        String methodName = joinPoint.getSignature().getName();
        logger.error("Exception in {}.{}() with cause = {}",
                invokedClass.getName(), methodName,
                ex.getCause() != null ? ex.getCause() : ex.getMessage());
    }

    @Around("userServicePointcut()")
    public Object logAroundService(ProceedingJoinPoint joinPoint) throws Throwable {
        logBeforeService(joinPoint);
        Object result = joinPoint.proceed();
        logAfterService(joinPoint, result);
        return result;
    }

    @Before("orderServicePointcut()")
    public void logBeforeService(JoinPoint joinPoint) {
        Class invokedClass = joinPoint.getSignature().getDeclaringType();
        Logger logger = LoggerFactory.getLogger(invokedClass);
        String methodName = joinPoint.getSignature().getName();
        Map<String, Object> parameters = obtainMethodArguments(joinPoint);
        logger.debug("Enter: {}.{}() with argument[s] = {}",
                invokedClass.getName(), methodName, parameters);
    }

    @AfterReturning(value = "orderServicePointcut()", returning = "result")
    public void logAfterService(JoinPoint joinPoint, Object result) {
        Class invokedClass = joinPoint.getSignature().getDeclaringType();
        Logger logger = LoggerFactory.getLogger(invokedClass);
        String methodName = joinPoint.getSignature().getName();
        logger.debug("Exit: {}.{}() with result = {}",
                invokedClass.getName(), methodName,
                result instanceof Optional value ? value.orElse("Optional{empty}") : result);
    }

    private Map<String, Object> obtainMethodArguments(JoinPoint joinPoint) {
        Map<String, Object> parameters = new HashMap<>();
        Parameter[] methodParameters = ((MethodSignature) joinPoint.getSignature()).getMethod().getParameters();
        Object[] parameterValues = joinPoint.getArgs();
        for (int i = 0; i < methodParameters.length; i++) {
            MethodArgument annotation = methodParameters[i].getAnnotation(MethodArgument.class);
            String paramName = Objects.isNull(annotation) ? "unnamed" : annotation.name();
            Object paramValue = parameterValues[i];
            parameters.put(paramName, paramValue);
        }
        return parameters;
    }

}

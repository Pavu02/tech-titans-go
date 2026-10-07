package com.examly.springapp.aspect;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.Arrays;

/**
 * Aspect for logging execution of controllers and services.
 * This is an industry standard approach to keep logging out of business logic.
 */
@Aspect
@Component
public class LoggingAspect {

    private final Logger log = LoggerFactory.getLogger(this.getClass());

    // Defines the trigger: Intercept any method inside 'controller' or 'service' package
    @Pointcut("within(com.examly.springapp.controller..*) || within(com.examly.springapp.service..*)")
    public void applicationPackagePointcut() {
        // Pointcut definition
    }

    // Automatically logs the exact method and exception whenever ANY exception is thrown
    @AfterThrowing(pointcut = "applicationPackagePointcut()", throwing = "e")
    public void logAfterThrowing(JoinPoint joinPoint, Throwable e) {
        log.error("Exception in {}.{}() with cause = '{}' and exception = '{}'",
                joinPoint.getSignature().getDeclaringTypeName(),
                joinPoint.getSignature().getName(),
                e.getCause() != null ? e.getCause() : "NULL",
                e.getMessage(), e);
    }

    // Intercepts the method execution to log entry, exit, and execution time
    @Around("applicationPackagePointcut()")
    public Object logAround(ProceedingJoinPoint joinPoint) throws Throwable {
        if (log.isDebugEnabled()) {
            log.debug("Enter: {}.{}() with argument[s] = {}",
                    joinPoint.getSignature().getDeclaringTypeName(),
                    joinPoint.getSignature().getName(),
                    Arrays.toString(joinPoint.getArgs()));
        }
        
        long startTime = System.currentTimeMillis();
        
        try {
            // This line actually executes the controller/service method
            Object result = joinPoint.proceed();
            
            long elapsedTime = System.currentTimeMillis() - startTime;
            
            if (log.isDebugEnabled()) {
                log.debug("Exit: {}.{}() with result = {}, Execution Time: {} ms",
                        joinPoint.getSignature().getDeclaringTypeName(),
                        joinPoint.getSignature().getName(),
                        result, elapsedTime);
            }
            
            // Log general info
            log.info("Executed {}.{}() in {} ms",
                    joinPoint.getSignature().getDeclaringTypeName(),
                    joinPoint.getSignature().getName(),
                    elapsedTime);
                    
            return result;
        } catch (IllegalArgumentException e) {
            log.error("Illegal argument: {} in {}.{}()", Arrays.toString(joinPoint.getArgs()),
                    joinPoint.getSignature().getDeclaringTypeName(), joinPoint.getSignature().getName());
            throw e;
        }
    }
}

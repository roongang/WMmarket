package com.around.wmmarket.aop;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Slf4j
@Aspect
@Component
public class LogAspect {
    Logger logger=LoggerFactory.getLogger(LogAspect.class);

    @Around("execution(* com.around.wmmarket.controller..*(..))")
    public Object logging(ProceedingJoinPoint joinPoint) throws Throwable{
        logger.info("################## start - "+joinPoint.getSignature().getDeclaringTypeName()
                +" / "+joinPoint.getSignature().getName()+" ##################");
        Object result=joinPoint.proceed();
        logger.info("################## finished - "+joinPoint.getSignature().getDeclaringTypeName()
                +" / "+joinPoint.getSignature().getName()+" ##################");
        return result;
    }
}

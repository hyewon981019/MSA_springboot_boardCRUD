package com.example.board.aop;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Aspect
public class LoggerAspect {
    // 컨트롤러, 서비스, 맵퍼의 메서드
    @Pointcut("execution(* com.example.board..controller.*Controller.*(..)) || execution(* com.example.board..service.*Impl.*(..)) || execution(* com.example.board..mapper.*Mapper.*(..))")
    private void loggerAspect() {

    }

    // 각 메서드의 경로와 이름을 로그로 출력
    @Around("loggerAspect()")
    public Object logPrint(ProceedingJoinPoint joinPoint) throws Throwable {
        String type = ""; // 호출 메소드의 유형(컨트롤러, 서비스, 맵퍼)
        String name = joinPoint.getSignature().getDeclaringTypeName();
        if (name.indexOf("Controller") >= 0) {
            type = "Controller";
        } else if (name.indexOf("ServiceImpl") >= 0) {
            type = "ServiceImpl";
        } else if (name.indexOf("Mapper") >= 0) {
            type = "Mapper";
        }
        log.debug(type + "\t" + name + "." + joinPoint.getSignature().getName());

        return joinPoint.proceed();
    }

}

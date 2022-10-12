package me.zilzu.mycoupon.configuration;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.*;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.Collection;
import java.util.Optional;

@Component
@Aspect
public class AspectConfig {

    //@Pointcut("within(me.zilzu.mycoupon.storage.NewCouponRepository)")
    @Pointcut("execution(* me.zilzu.mycoupon.storage.*Repository.*(..))")
    public void repositoryMethod() {
    }

    @Pointcut("execution(* me.zilzu.mycoupon.storage.*Repository.*(..)) || execution(* org.springframework.data.jpa.repository.JpaRepository.*(..))")
    public void repositoryMethodAndDefaultJpa() {
    }


    @Before(value = "repositoryMethod()")
    public void beforeMethod(JoinPoint joinPoint) {
//        System.out.println("============ AOP Before ============");
//        System.out.println("joinPoint = " + joinPoint);
    }

    @After(value = "repositoryMethod()")
    public void afterMethod(JoinPoint joinPoint) {
//        System.out.println("============ AOP After ============");
//        System.out.println("joinPoint = " + joinPoint);
    }

    @Around(value = "repositoryMethodAndDefaultJpa()")
    public Object aroundMethod(ProceedingJoinPoint proceedingJoinPoint) throws Throwable {
        //Around_Before | Before | After | Around_After


        StringBuffer stringBuffer = new StringBuffer();
        stringBuffer.append("============ AOP Around > Before ============\n");
        stringBuffer.append("MethodName [" + proceedingJoinPoint.getSignature().getName() + "] :: " + proceedingJoinPoint.getSignature());
        stringBuffer.append("\n");
        stringBuffer.append("arguments count :: " + proceedingJoinPoint.getArgs().length);
        stringBuffer.append("\n");
        stringBuffer.append("arguments >> ");
        Arrays.stream(proceedingJoinPoint.getArgs())
                .map(String::valueOf)
                .forEach(stringBuffer::append);
        stringBuffer.append("\n");

        // execute
        Object object = proceedingJoinPoint.proceed();

        stringBuffer.append("============================================\n");
        stringBuffer.append("DB results << " + object);
        stringBuffer.append("\n");
        if (object instanceof Optional) {
            if (((Optional<?>) object).isPresent()) {
                Object o = ((Optional<?>) object).get();
                if (o instanceof Collection) {
                    stringBuffer.append("result count :: " + ((Collection<?>) o).size());
                    stringBuffer.append("\n");
                }
            }
        } else if (object instanceof Collection) {
            stringBuffer.append("result count :: " + ((Collection<?>) object).size());
            stringBuffer.append("\n");
        }
        stringBuffer.append("============ AOP Around > After ============");

        System.out.println(stringBuffer);

        return object;
    }

}

package me.zilzu.mycoupon.configuration;

import me.zilzu.mycoupon.storage.CouponEntity;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.*;
import org.springframework.stereotype.Component;

import java.lang.reflect.Array;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

@Aspect
@Component
public class AspectConfig {

    //@Pointcut("within(me.zilzu.mycoupon.storage.NewCouponRepository)")
    @Pointcut("execution(* me.zilzu.mycoupon.storage.*Repository.*(..))")
    public void repositoryMethod() {
    }

    @Before(value = "repositoryMethod()")
    public void beforeMethod(JoinPoint joinPoint) {
//        System.out.println("============ AOP Before ============");
//        System.out.println("joinPoint = " + joinPoint);
//        StringBuffer stringBuffer = new StringBuffer();s
//        stringBuffer.append("============ AOP Before ============");
//        stringBuffer.append("\n");
//        stringBuffer.append("call at :: " + joinPoint.getSourceLocation());
//        stringBuffer.append("\n");
//        stringBuffer.append("MethodName [" + joinPoint.getSignature().getName() + "] :: " + joinPoint.getSignature());
//        stringBuffer.append("\n");
//        stringBuffer.append("arguments count :: " + joinPoint.getArgs().length);
//        stringBuffer.append("\n");
//        stringBuffer.append("arguments >> ");
//        Arrays.stream(joinPoint.getArgs())
//                .map(String::valueOf)
//                .forEach(stringBuffer::append);
//
//        stringBuffer.append("\n");
//        stringBuffer.append("============ AOP Before ============");
//        System.out.println(stringBuffer);
    }

    @After(value = "repositoryMethod()")
    public void afterMethod(JoinPoint joinPoint) {
//        System.out.println("============ AOP After ============");
//        System.out.println("joinPoint = " + joinPoint);
    }

    @Around(value = "repositoryMethod()")
    public Object aroundMethod(ProceedingJoinPoint proceedingJoinPoint) throws Throwable {
        //System.out.println("proceedingJoinPoint = " + proceedingJoinPoint);

        StringBuffer stringBuffer = new StringBuffer();
        stringBuffer.append("============ AOP Around > Before ============");
        stringBuffer.append("\n");
        stringBuffer.append("call at :: " + proceedingJoinPoint.getSourceLocation());
        stringBuffer.append("\n");
        stringBuffer.append("MethodName [" + proceedingJoinPoint.getSignature().getName() + "] :: " + proceedingJoinPoint.getSignature());
        stringBuffer.append("\n");
        stringBuffer.append("arguments count :: " + proceedingJoinPoint.getArgs().length);
        stringBuffer.append("\n");
        stringBuffer.append("arguments >> ");
        Arrays.stream(proceedingJoinPoint.getArgs())
                .map(String::valueOf)
                .forEach(stringBuffer::append);

        stringBuffer.append("\n");
        //System.out.println(stringBuffer);

        Object object = proceedingJoinPoint.proceed();

//        System.out.println("object = " + object);
        //stringBuffer = new StringBuffer();
        stringBuffer.append("============================================\n");
        stringBuffer.append("object = " + object);
        stringBuffer.append("\n");
        if (object instanceof Optional) {
            if (((Optional<?>) object).isPresent()) {
                Object o = ((Optional<?>) object).get();
                if (o instanceof Collection) {
                    stringBuffer.append("result count :: " + ((Collection<?>) o).size());
                    stringBuffer.append("\n");
                }
                if (o instanceof CouponEntity) {
                    stringBuffer.append("DB result << " + o);
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

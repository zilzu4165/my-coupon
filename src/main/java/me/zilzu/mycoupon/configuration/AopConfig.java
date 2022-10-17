package me.zilzu.mycoupon.configuration;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.Collection;
import java.util.Optional;

@Component
@Aspect
public class AopConfig {

    @Around("execution(* me.zilzu.mycoupon.storage.*Repository.*(..)) || execution(* org.springframework.data.jpa.repository.JpaRepository.*(..))")
    public Object repositoryAop(ProceedingJoinPoint pjp) throws Throwable {

        // @Before
        StringBuffer sb = new StringBuffer();
        sb.append("@Before\n");
        sb.append("[Method] :: [" + pjp.getSignature().getName() + "] :: " + pjp.getSignature() + "\n");
        sb.append("[Parameter] :: " + Arrays.toString(pjp.getArgs()) + "\n");

        Object result = pjp.proceed();

        // @AfterReturning
        sb.append("@AfterReturning\n");
        sb.append("[return] :: " + result + "\n");

        if (result instanceof Optional) {
            // 클래스 캐스팅
            if (((Optional<?>) result).isPresent()) {
                Object o = ((Optional<?>) result).get();
                sb.append("Object :: " + o + "\n");
            }
        } else if (result instanceof Collection) {
            sb.append("result count :: " + ((Collection<?>) result).size() + "\n");
        }

        System.out.println(sb);

        return result;
    }
}

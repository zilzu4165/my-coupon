package me.zilzu.mycoupon.configuration;

import me.zilzu.mycoupon.storage.CouponEntity;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
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

        try {
            // @Before
            StringBuffer sb = new StringBuffer();
            StringBuffer sb2 = new StringBuffer();
            StringBuffer sb3 = new StringBuffer();
            sb.append("@Before\n");
//            sb.append("[target] :: " + pjp.getTarget() + "\n");
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
                    if (o instanceof Collection) {
                        sb.append("result count :: " + ((Collection<?>) o).size() + "\n");
                    }
                    if (o instanceof CouponEntity) {
                        sb.append("CouponEntity :: " + o + "\n");
                    }
                }
            } else if (result instanceof Collection) {
                sb.append("result count :: " + ((Collection<?>) result).size() + "\n");
            }

            System.out.println(sb);
            return result;
        } catch (Throwable e) {
            // @AfterThrowing
            System.out.println("AfterThrowing!!!!!!!!!!!!!");
            System.out.println("[Exception] :: " + e.toString());
            throw new RuntimeException(e);
        } finally {
            // @After
//            System.out.println("After!!!!!!!!!!!!!!!!!!!!!");
//            System.out.println("[메서드종료]");
        }

    }

    @AfterReturning(value = "execution(* *..storage.*.*(..))", returning = "result")
    public void doReturn(JoinPoint joinPoint, Object result) {
        System.out.println("[return] :: " + joinPoint.getSignature());
//        System.out.println("[return] :: " + result);
    }

//    @AfterThrowing(value = "execution(* *..storage.*.*(..))", throwing = "")
}

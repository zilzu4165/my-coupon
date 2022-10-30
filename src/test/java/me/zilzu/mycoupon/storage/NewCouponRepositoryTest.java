package me.zilzu.mycoupon.storage;

import me.zilzu.mycoupon.application.service.CouponService;
import me.zilzu.mycoupon.common.enums.Currency;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
class NewCouponRepositoryTest {

    @Autowired
    private NewCouponRepository sut;

    @Autowired
    private CouponService couponService;

    @BeforeEach
    void couponEmpty() {
        couponService.emptyCoupon();
    }

    @Test
    @DisplayName("findByValidIsFalse")
    void test() {
        sut.save(couponEntity("1", true));
        sut.save(couponEntity("2", true));
        sut.save(couponEntity("3", false));
        sut.save(couponEntity("4", true));

        assertThat(sut.findByValidIsFalse()).hasSize(1);
        assertThat(sut.findByValidIsFalse().get(0).id).isEqualTo("3");
    }

    @Test
    @DisplayName("KRW 인 coupon 만 찾기")
    void test2() {
        sut.save(couponEntity("1", true, Currency.ALL));
        sut.save(couponEntity("2", true, Currency.JPY));
        sut.save(couponEntity("3", false, Currency.KRW));
        sut.save(couponEntity("4", true, Currency.KRW));

        assertThat(sut.findByCurrency(Currency.KRW)).hasSize(2);
    }

    private static CouponEntity couponEntity(String id, boolean valid) {
        return couponEntity(id, valid, null);
    }

    private static CouponEntity couponEntity(String id, boolean valid, Currency currency) {
        return new CouponEntity(id, null, null, currency, null, null, null, valid, null);
    }
}
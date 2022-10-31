package me.zilzu.mycoupon.storage;

import me.zilzu.mycoupon.common.enums.Currency;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface NewCouponRepository extends JpaRepository<CouponEntity, String> {
    List<CouponEntity> findByValidIsFalse();

    List<CouponEntity> findByCurrency(Currency currency);
}

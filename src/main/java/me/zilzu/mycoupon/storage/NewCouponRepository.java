package me.zilzu.mycoupon.storage;

import org.springframework.data.jpa.repository.JpaRepository;

public interface NewCouponRepository extends JpaRepository<CouponEntity, String> {
}

package me.zilzu.mycoupon.storage;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CouponUsageHistoryRepository extends JpaRepository<CouponUsageHistoryEntity, String> {

    List<CouponUsageHistoryEntity> findByRefCouponId(String s);
}

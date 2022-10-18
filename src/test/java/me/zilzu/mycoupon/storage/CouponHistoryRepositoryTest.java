package me.zilzu.mycoupon.storage;

import me.zilzu.mycoupon.common.CouponId;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class CouponHistoryRepositoryTest {
    CouponHistoryRepository sut = new CouponHistoryRepository();

    @Test
    @DisplayName("couponId 에 해당하는 history가 없으면 길이가 0인 List를 반환한다.")
    void test1() {
        // given
        sut.save(couponUsageHistoryEntity("id1", new CouponId("zilzu"), 0d, 0d));
        sut.save(couponUsageHistoryEntity("id1", new CouponId("zilzu"), 0d, 0d));
        sut.save(couponUsageHistoryEntity("id1", new CouponId("zilzu"), 0d, 0d));

        // when
        List<CouponUsageHistoryEntity> historyEntities = sut.find(new CouponId("park"));

        // then
        assertThat(historyEntities.size()).isEqualTo(0);
    }

    private static CouponUsageHistoryEntity couponUsageHistoryEntity(String couponHistoryId, CouponId refCouponId, Double price, Double discountedPrice) {
        return new CouponUsageHistoryEntity(couponHistoryId, refCouponId.value, LocalDateTime.now(), price, discountedPrice);

    }
}
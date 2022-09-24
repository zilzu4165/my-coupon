package me.zilzu.mycoupon.storage;

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
        sut.save(couponUsageHistoryEntity("id1", "zilzu"));
        sut.save(couponUsageHistoryEntity("id1", "zilzu"));
        sut.save(couponUsageHistoryEntity("id1", "zilzu"));

        // when
        List<CouponUsageHistoryEntity> historyEntities = sut.find("park");

        // then
        assertThat(historyEntities.size()).isEqualTo(0);
    }

    private static CouponUsageHistoryEntity couponUsageHistoryEntity(String couponHistoryId, String refCouponId) {
        return new CouponUsageHistoryEntity(couponHistoryId, refCouponId, LocalDateTime.now());

    }
}
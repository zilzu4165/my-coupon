package me.zilzu.mycoupon.storage;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class CouponHistoryRepositoryTest {
    CouponHistoryRepository sut = new CouponHistoryRepository();

    @Test
    @DisplayName("couponId 에 해당하는 history 가 없으면 길이가 0인 List 를 반환한다.")
    void test1() {
        // given - 가정
        sut.save(couponUsageHistoryEntity("id1", "park"));
        sut.save(couponUsageHistoryEntity("id2", "park"));
        sut.save(couponUsageHistoryEntity("id3", "park"));

        // when
        List<CouponUsageHistoryEntity> historyEntityList = sut.find("zilzu");

        // then
        assertThat(historyEntityList.size()).isEqualTo(0);
    }

    private static CouponUsageHistoryEntity couponUsageHistoryEntity(String couponHistoryId, String refCouponId) {
        return new CouponUsageHistoryEntity(couponHistoryId, refCouponId, LocalDateTime.now());
    }
}
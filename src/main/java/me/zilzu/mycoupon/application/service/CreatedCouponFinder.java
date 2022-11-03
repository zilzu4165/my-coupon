package me.zilzu.mycoupon.application.service;

import me.zilzu.mycoupon.common.CouponId;
import me.zilzu.mycoupon.storage.CouponEntity;
import me.zilzu.mycoupon.storage.NewCouponRepository;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.YearMonth;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class CreatedCouponFinder {

    private final NewCouponRepository newCouponRepository;

    public CreatedCouponFinder(NewCouponRepository newCouponRepository) {
        this.newCouponRepository = newCouponRepository;
    }

    public List<Coupon> findBy(YearMonth targetYearMonth) {
        LocalDate firstDateOfMonth = LocalDate.of(targetYearMonth.getYear(), targetYearMonth.getMonth(), 1);
        LocalDate lastDateOfMonth = LocalDate.of(targetYearMonth.getYear(), targetYearMonth.getMonth(), firstDateOfMonth.lengthOfMonth());
        List<CouponEntity> couponEntities = newCouponRepository.findByDateBetween(firstDateOfMonth, lastDateOfMonth);
        return couponEntities.stream()
                .map(entity -> new Coupon(new CouponId(entity.id), entity.duration, entity.durationInMonth, entity.currency, entity.discountType, entity.amountOff, entity.percentOff, entity.valid, entity.createdTime))
                .collect(Collectors.toList());
    }
}

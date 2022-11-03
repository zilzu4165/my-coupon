package me.zilzu.mycoupon.application.service;

import me.zilzu.mycoupon.common.enums.Currency;
import org.springframework.stereotype.Service;

import java.time.YearMonth;
import java.util.List;

@Service
public class CouponAnalyzeService {

    private final CreatedCouponFinder createdCouponFinder;

    private final RateOfDateFinder rateOfDateFinder;

    private final CouponStatsSummarizer couponStatsSummarizer;

    public CouponAnalyzeService(
            CreatedCouponFinder createdCouponFinder,
            RateOfDateFinder rateOfDateFinder,
            CouponStatsSummarizer couponStatsSummarizer
    ) {
        this.createdCouponFinder = createdCouponFinder;
        this.rateOfDateFinder = rateOfDateFinder;
        this.couponStatsSummarizer = couponStatsSummarizer;
    }

    public CouponAnalyzeResult analyzeBy(YearMonth targetYearMonth, Currency currency) {
        List<Coupon> recentlyCreatedCoupon = createdCouponFinder.findBy(targetYearMonth);
        List<RateOfDate> rateOfDateList = rateOfDateFinder.find(targetYearMonth, currency);
        CouponStatsSummary summary = couponStatsSummarizer.summarize(recentlyCreatedCoupon, rateOfDateList);
        return new CouponAnalyzeResult(targetYearMonth, summary.sum, summary.average);
    }
}

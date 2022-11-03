package me.zilzu.mycoupon.application.service;

import me.zilzu.mycoupon.common.enums.Currency;
import me.zilzu.mycoupon.storage.CurrencyRateHistoryRepository;
import org.springframework.stereotype.Service;

import java.time.YearMonth;
import java.util.List;

@Service
public class CouponAnalyzeService {

    private final CurrencyRateHistoryRepository currencyRateHistoryRepository;

    private final LastBusinessDayRateFinder lastBusinessDayRateFinder;

    private final RecentlyCreatedCouponFinder recentlyCreatedCouponFinder;

    private final RateOfDateFinder rateOfDateFinder;

    private final CouponStatsSummarizer couponStatsSummarizer;

    private final RateHistoryCollector rateHistoryCollector;

    public CouponAnalyzeService(
            CurrencyRateHistoryRepository currencyRateHistoryRepository,
            LastBusinessDayRateFinder lastBusinessDayRateFinder,
            RecentlyCreatedCouponFinder recentlyCreatedCouponFinder,
            RateOfDateFinder rateOfDateFinder, CouponStatsSummarizer couponStatsSummarizer,
            RateHistoryCollector rateHistoryCollector
    ) {
        this.currencyRateHistoryRepository = currencyRateHistoryRepository;
        this.lastBusinessDayRateFinder = lastBusinessDayRateFinder;
        this.recentlyCreatedCouponFinder = recentlyCreatedCouponFinder;
        this.rateOfDateFinder = rateOfDateFinder;
        this.couponStatsSummarizer = couponStatsSummarizer;
        this.rateHistoryCollector = rateHistoryCollector;
    }

    public CouponAnalyzeResult analyzeBy(YearMonth targetDate, Currency currency) {
        List<Coupon> recentlyCreatedCoupon = recentlyCreatedCouponFinder.find(targetDate);
        List<RateOfDate> rateOfDateList = rateOfDateFinder.find(targetDate, currency);
        CouponStatsSummary summary = couponStatsSummarizer.summarize(recentlyCreatedCoupon, rateOfDateList);
        return new CouponAnalyzeResult(targetDate, summary.sum, summary.average);
    }
}

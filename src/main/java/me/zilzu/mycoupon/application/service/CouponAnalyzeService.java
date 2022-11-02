package me.zilzu.mycoupon.application.service;

import me.zilzu.mycoupon.common.enums.Currency;
import me.zilzu.mycoupon.storage.CurrencyRateHistoryEntity;
import me.zilzu.mycoupon.storage.CurrencyRateHistoryRepository;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class CouponAnalyzeService {

    private final CurrencyRateHistoryRepository currencyRateHistoryRepository;

    private final LastBusinessDayRateFinder lastBusinessDayRateFinder;

    private final RecentlyCreatedCouponFinder recentlyCreatedCouponFinder;

    private final RateOfDateFinder rateOfDateFinder;

    private final CouponStatsSummarizer couponStatsSummarizer;

    public CouponAnalyzeService(
            CurrencyRateHistoryRepository currencyRateHistoryRepository,
            LastBusinessDayRateFinder lastBusinessDayRateFinder,
            RecentlyCreatedCouponFinder recentlyCreatedCouponFinder,
            RateOfDateFinder rateOfDateFinder, CouponStatsSummarizer couponStatsSummarizer) {
        this.currencyRateHistoryRepository = currencyRateHistoryRepository;
        this.lastBusinessDayRateFinder = lastBusinessDayRateFinder;
        this.recentlyCreatedCouponFinder = recentlyCreatedCouponFinder;
        this.rateOfDateFinder = rateOfDateFinder;
        this.couponStatsSummarizer = couponStatsSummarizer;
    }

    public void save(RateByBaseCurrency rateByBaseCurrency) {
        final Map<Currency, BigDecimal> currencyRateMap = rateByBaseCurrency.rates;
        List<CurrencyRateHistoryEntity> list = currencyRateMap.keySet().stream()
                .map(currency
                        -> new CurrencyRateHistoryEntity(rateByBaseCurrency.date,
                        currency, currencyRateMap.get(currency)))
                .collect(Collectors.toList());

        currencyRateHistoryRepository.saveAll(list);
    }

    public List<RateByBaseCurrency> getRateByBaseCurrencyByAPI(List<LocalDate> dates) {
        return dates.parallelStream()
                .map(localDate -> {
                    RestTemplate restTemplate = new RestTemplate();
                    String date = localDate.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
                    String rateByBaseAndDateUrl = "https://api.vatcomply.com/rates?date=" + date + "&base=USD";
                    //System.out.println("rateByBaseAndDateUrl = " + rateByBaseAndDateUrl);
                    return restTemplate.getForObject(rateByBaseAndDateUrl, RateByBaseCurrency.class);
                }).distinct()
                .collect(Collectors.toList());
    }

    public CouponAnalyzeResult analyzeBy(YearMonth targetDate, Currency currency) {
        List<Coupon> recentlyCreatedCoupon = recentlyCreatedCouponFinder.find(targetDate);
        List<RateOfDate> rateOfDateList = rateOfDateFinder.find(targetDate, currency);
        CouponStatsSummary summary = couponStatsSummarizer.summarize(recentlyCreatedCoupon, rateOfDateList);
        return new CouponAnalyzeResult(targetDate, summary.sum, summary.average);
    }
}

package me.zilzu.mycoupon.application.service;

import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Optional;

@Component
public class LastBusinessDayRateFinder {
    public RateOfDate find(List<RateOfDate> rateOfDateList, LocalDate targetDate) {
        LocalDate previousDate = targetDate.minus(1, ChronoUnit.DAYS);
        Optional<RateOfDate> mayBeRate = rateOfDateList.stream()
                .filter(rateOfDate -> rateOfDate.date.equals(previousDate))
                .findFirst();
        return mayBeRate.orElseGet(() -> find(rateOfDateList, previousDate));
    }
}

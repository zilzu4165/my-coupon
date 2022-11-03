package me.zilzu.mycoupon.application.service;

import org.springframework.stereotype.Component;

import java.time.DayOfWeek;
import java.time.LocalDate;

@Component
public class DayConverter {

    public LocalDate convertWeekDay(LocalDate date) {
        DayOfWeek dayOfWeek = date.getDayOfWeek();
        int weekValue = dayOfWeek.getValue();
        if (weekValue == 7) {
            date = date.minusDays(2);
        } else if (weekValue == 6) {
            date = date.minusDays(1);
        }
        LocalDate dayWeek = date;
        return dayWeek;
    }
}

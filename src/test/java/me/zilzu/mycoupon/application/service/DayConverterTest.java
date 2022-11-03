package me.zilzu.mycoupon.application.service;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;

class DayConverterTest {

    @Test
    @DisplayName("2022-11-05 는 토요일이므로 2022-11-04 금요일로 반환된다.")
    void test1() {
        DayConverter sut = new DayConverter();

        LocalDate testDate = LocalDate.of(2022, 11, 5);
        LocalDate weekDay = sut.convertWeekDay(testDate);
        assertThat(weekDay.getDayOfWeek().getValue()).isEqualTo(5);
        assertThat(weekDay).isBefore(testDate);
        assertThat(weekDay).isEqualTo(LocalDate.of(2022, 11, 4));
    }

}
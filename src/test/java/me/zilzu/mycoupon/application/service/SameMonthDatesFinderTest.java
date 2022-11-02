package me.zilzu.mycoupon.application.service;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.Month;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class SameMonthDatesFinderTest {
    @Test
    @DisplayName("2022년 2월을 넘기면, 28개의 2월 날짜가 나온다")
    void test() {
        SameMonthDatesFinder sut = new SameMonthDatesFinder();

        List<LocalDate> result = sut.find(LocalDate.of(2022, 2, 1));

        assertThat(result.size()).isEqualTo(28);
        assertThat(result).allMatch(localDate -> is2022Feb(localDate));
    }

    private boolean is2022Feb(LocalDate localDate) {
        return localDate.getYear() == 2022 && localDate.getMonth() == Month.FEBRUARY;
    }
}
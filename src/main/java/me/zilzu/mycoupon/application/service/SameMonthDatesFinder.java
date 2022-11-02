package me.zilzu.mycoupon.application.service;

import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Component
public class SameMonthDatesFinder {
    public List<LocalDate> find(LocalDate firstDateOfMonth) {
        return IntStream.range(0, firstDateOfMonth.lengthOfMonth())
                .mapToObj(firstDateOfMonth::plusDays)
                .collect(Collectors.toList());
    }
}

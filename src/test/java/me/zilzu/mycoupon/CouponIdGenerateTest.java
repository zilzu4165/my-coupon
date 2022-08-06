package me.zilzu.mycoupon;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.RepeatedTest;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.*;

public class CouponIdGenerateTest {
    String generatedId = new me.zilzu.mycoupon.application.service.CouponIdGenerate().generate();

    @Test
    @RepeatedTest(100)
    @DisplayName("first_char_is_capital_alphabet")
    public void isCapitalAlphabet() {
//        String generatedId = new CouponIdGenerate().generate();
        String prefix = generatedId.substring(0, 1);
        assertThat(prefix).isLowerCase();
    }

    @Test
    @RepeatedTest(100)
    @DisplayName("length_is_8")
    public void is_length_eight() {
//        String generatedId = new CouponIdGenerate().generate();
        assertThat(generatedId).hasSize(8);
    }

    @Test
    @RepeatedTest(100)
    @DisplayName("containsOnlyCapitalAlphabetAndNumber")
    public void containsOnlyCapitalAlphabetAndNumber() {
//        String generatedId = new CouponIdGenerate().generate();
        assertThat(generatedId).matches("[A-Z0-9]*");
    }
}

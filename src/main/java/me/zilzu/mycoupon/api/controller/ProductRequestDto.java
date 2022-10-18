package me.zilzu.mycoupon.api.controller;

import com.fasterxml.jackson.annotation.JsonProperty;
import me.zilzu.mycoupon.common.enums.CouponCurrency;

public class ProductRequestDto {
    @JsonProperty("prduct_name")
    public final String productName;
    public final Double price;
    public final Integer quantity;
    @JsonProperty("currency")
    public final CouponCurrency couponCurrency;

    public ProductRequestDto(String productName, Double price, Integer quantity, CouponCurrency couponCurrency) {
        this.productName = productName;
        this.price = price;
        this.quantity = quantity;
        this.couponCurrency = couponCurrency;
    }
}

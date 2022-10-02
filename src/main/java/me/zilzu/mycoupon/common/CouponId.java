package me.zilzu.mycoupon.common;

import java.io.Serializable;
import java.util.Objects;

public class CouponId implements Serializable {
    public final String value;

    public CouponId(String value) {
        this.value = value;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        CouponId couponId = (CouponId) o;

        return Objects.equals(value, couponId.value);
    }

    @Override
    public int hashCode() {
        return value != null ? value.hashCode() : 0;
    }

    @Override
    public String toString() {
        return value;
    }
}

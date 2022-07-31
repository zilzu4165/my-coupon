package me.zilzu.mycoupon.application.service;

public class CouponDeleteResult {

    public String id;
    public String object;
    public boolean deleted;

    public CouponDeleteResult(String id, String object, boolean deleted) {
        this.id = id;
        this.object = object;
        this.deleted = deleted;
    }
}

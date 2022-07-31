package me.zilzu.mycoupon.api.controller;

public class CouponDeletedResponse {

    public String id;
    public String object;
    public boolean deleted;

    public CouponDeletedResponse(String id, String object, boolean deleted) {
        this.id = id;
        this.object = object;
        this.deleted = deleted;
    }
}

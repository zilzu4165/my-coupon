package me.zilzu.mycoupon.api.controller;

import java.util.List;

public class CouponRetrieveListResponse {

    public String object;
    public String url;
    public boolean has_more;
    public List<CouponRetrieveResultResponse> data;

    public CouponRetrieveListResponse(String object, String url, boolean has_more, List<CouponRetrieveResultResponse> data) {
        this.object = object;
        this.url = url;
        this.has_more = has_more;
        this.data = data;
    }
}

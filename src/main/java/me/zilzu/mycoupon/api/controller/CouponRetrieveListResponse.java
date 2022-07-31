package me.zilzu.mycoupon.api.controller;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;

import java.util.List;

@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
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

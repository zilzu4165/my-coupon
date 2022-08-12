package me.zilzu.mycoupon.application.service;

import java.time.LocalDateTime;

public class Coupon {
    public String id;
    public String object;
    public Integer amountOff;
    public long created;
    public String currency;
    public String duration;
    public Integer durationInMonths;
    public Boolean livemode;
    public Integer maxRedemptions;
    public String name;
    public Float percentOff;
    public Boolean valid;
    public LocalDateTime date;
    public LocalDateTime getDate() {
        return date;
    }

    public Coupon(String id, String object, Integer amountOff, long created, String currency, String duration, Integer durationInMonths, Boolean livemode, Integer maxRedemptions, String name, Float percentOff, Boolean valid, LocalDateTime date) {
        this.id = id;
        this.object = object;
        this.amountOff = amountOff;
        this.created = created;
        this.currency = currency;
        this.duration = duration;
        this.durationInMonths = durationInMonths;
        this.livemode = livemode;
        this.maxRedemptions = maxRedemptions;
        this.name = name;
        this.percentOff = percentOff;
        this.valid = valid;
        this.date = date;
    }

    @Override
    public String toString() {
        return "Coupon{" +
                "id='" + id + '\'' +
                ", date=" + date +
                '}';
    }
}

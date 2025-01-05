package com.simsimbookstore.apiserver.coupons.couponpolicy.entity;

public enum DisCountType {
    RATE("정률"),
    FIX("정액");

    private final String status;

    DisCountType(String status) {this.status = status;}
}

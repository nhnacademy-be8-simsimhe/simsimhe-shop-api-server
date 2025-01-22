package com.simsimbookstore.apiserver.coupons.coupontype.entity;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonValue;
import com.simsimbookstore.apiserver.coupons.couponpolicy.entity.DisCountType;
import lombok.Getter;

@Getter
public enum CouponTargetType {
     ALL("전체"),
     CATEGORY("카테고리"),
     BOOK("책");

     private final String name;

     CouponTargetType(String name) {
          this.name = name;
     }

     // 역직렬화 : JSON -> Enum
     @JsonCreator
     public static CouponTargetType from(String value) {
          for (CouponTargetType type : values()) {
               if (type.name.equals(value)) {
                    return type;
               }
          }
          return CouponTargetType.ALL;
     }
     // 직렬화 : Enum -> JSON
     @JsonValue
     public String getName() {
          return name;
     }
}

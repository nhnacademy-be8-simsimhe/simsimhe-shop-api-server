package com.simsimbookstore.apiserver.orders.packages.dto;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PackageRequestDto {

    private Long packageTypeId;

    private Long orderBookId;

    private String packageName;
}

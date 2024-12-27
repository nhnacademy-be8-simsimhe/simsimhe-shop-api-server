package com.simsimbookstore.apiserver.orders.packages.service;

import com.simsimbookstore.apiserver.orders.orderbook.entity.OrderBook;
import com.simsimbookstore.apiserver.orders.packages.dto.PackageRequestDto;
import com.simsimbookstore.apiserver.orders.packages.entity.Packages;

public interface PackageService {

    Packages createPackage(PackageRequestDto packageRequestDto, OrderBook orderBook);

    Packages getPackageById(Long packageId);

    void deletePackage(Long packageId);

}

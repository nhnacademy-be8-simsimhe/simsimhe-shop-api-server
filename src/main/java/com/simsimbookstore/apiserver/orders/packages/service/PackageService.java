package com.simsimbookstore.apiserver.orders.packages.service;

import com.simsimbookstore.apiserver.orders.packages.dto.PackageRequestDto;
import com.simsimbookstore.apiserver.orders.packages.entity.Packages;

public interface PackageService {
    Packages createPackage(PackageRequestDto packageRequestDto);

    Packages getPackageById(Long packageId);

    void deletePackage(Long packageId);

    Packages updatePackage(Long packageId, PackageRequestDto packageRequestDto);
}

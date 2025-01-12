package com.simsimbookstore.apiserver.orders.packages.service.impl;

import com.simsimbookstore.apiserver.orders.orderbook.entity.OrderBook;
import com.simsimbookstore.apiserver.orders.orderbook.repository.OrderBookRepository;
import com.simsimbookstore.apiserver.orders.packages.dto.PackageRequestDto;
import com.simsimbookstore.apiserver.orders.packages.entity.Packages;
import com.simsimbookstore.apiserver.orders.packages.entity.WrapType;
import com.simsimbookstore.apiserver.orders.packages.exception.PackagesNotFoundException;
import com.simsimbookstore.apiserver.orders.packages.exception.WrapTypeNotFoundException;
import com.simsimbookstore.apiserver.orders.packages.repository.PackagesRepository;
import com.simsimbookstore.apiserver.orders.packages.repository.WrapTypeRepository;
import com.simsimbookstore.apiserver.orders.packages.service.PackageService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PackageServiceImpl implements PackageService {

    private final PackagesRepository packageRepository;
    private final WrapTypeRepository wrapTypeRepository;

    /**
     * 포장 생성
     */
    @Override
    public Packages createPackage(PackageRequestDto packageRequestDto, OrderBook orderBook) {

        WrapType wrapType = wrapTypeRepository.findById(packageRequestDto.getPackageTypeId())
                .orElseThrow(() -> new WrapTypeNotFoundException("WrapType not found"));

        Packages newPackage = Packages.builder()
                .orderBook(orderBook)
                .packageType(packageRequestDto.getPackageName())
                .wrapType(wrapType)
                .build();

        // OrderBook과의 관계 설정
        orderBook.addPackage(newPackage);

        return packageRepository.save(newPackage);
    }

    /**
     * 패키지 조회
     */
    @Override
    public Packages getPackageById(Long packageId) {
        return packageRepository.findById(packageId)
                .orElseThrow(() -> new PackagesNotFoundException("Package not found"));
    }

    /**
     * 패키지 삭제
     */
    @Override
    public void deletePackage(Long packageId) {
        Packages existingPackage = packageRepository.findById(packageId)
                .orElseThrow(() -> new PackagesNotFoundException("Package not found"));

        // OrderBook과의 관계 해제
        existingPackage.getOrderBook().getPackages().remove(existingPackage);

        packageRepository.delete(existingPackage);
    }
}



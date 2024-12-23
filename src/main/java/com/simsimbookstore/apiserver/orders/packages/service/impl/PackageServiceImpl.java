package com.simsimbookstore.apiserver.orders.packages.service.impl;

import com.simsimbookstore.apiserver.orders.orderbook.entity.OrderBook;
import com.simsimbookstore.apiserver.orders.orderbook.exception.OrderBookNotFoundException;
import com.simsimbookstore.apiserver.orders.orderbook.repository.OrderBookRepository;
import com.simsimbookstore.apiserver.orders.packages.dto.PackageRequestDto;
import com.simsimbookstore.apiserver.orders.packages.entity.Packages;
import com.simsimbookstore.apiserver.orders.packages.entity.WrapType;
import com.simsimbookstore.apiserver.orders.packages.exception.PackagesNotFoundException;
import com.simsimbookstore.apiserver.orders.packages.exception.WrapTypeNotFoundException;
import com.simsimbookstore.apiserver.orders.packages.repository.PackageRepository;
import com.simsimbookstore.apiserver.orders.packages.repository.WrapTypeRepository;
import com.simsimbookstore.apiserver.orders.packages.service.PackageService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PackageServiceImpl implements PackageService {

    private final PackageRepository packageRepository;
    private final OrderBookRepository orderBookRepository;
    private final WrapTypeRepository wrapTypeRepository;

    /**
     * 포장 만드는 메소드
     * 오더북, 포장지 있는지 확인하고
     * 포장 세이브
     */

    @Override
    public Packages createPackage(PackageRequestDto packageRequestDto) {

        OrderBook orderBook = orderBookRepository.findById(packageRequestDto.getOrderBookId())
                .orElseThrow(() -> new OrderBookNotFoundException("OrderBook not found"));

        WrapType wrapType = wrapTypeRepository.findById(packageRequestDto.getPackageTypeId())
                .orElseThrow(() -> new WrapTypeNotFoundException("WrapType not found"));

        Packages newPackage = Packages.builder()
                .packageType(packageRequestDto.getPackageName())
                .orderBook(orderBook)
                .wrapType(wrapType)
                .build();

        return packageRepository.save(newPackage);
    }

    @Override
    public Packages getPackageById(Long packageId) {
        return packageRepository.findById(packageId)
                .orElseThrow(() -> new PackagesNotFoundException("Package not found"));
    }


    @Override
    public void deletePackage(Long packageId) {
        Packages existingPackage = packageRepository.findById(packageId)
                .orElseThrow(() -> new PackagesNotFoundException("Package not found"));
        packageRepository.delete(existingPackage);
    }

    @Override
    public Packages updatePackage(Long packageId, PackageRequestDto packageRequestDto) {

        Packages existPackage = packageRepository.findById(packageId)
                .orElseThrow(() -> new PackagesNotFoundException("Package not found"));

        OrderBook orderBook = orderBookRepository.findById(packageRequestDto.getOrderBookId())
                .orElseThrow(() -> new OrderBookNotFoundException("OrderBook not found"));

        WrapType wrapType = wrapTypeRepository.findById(packageRequestDto.getPackageTypeId())
                .orElseThrow(() -> new WrapTypeNotFoundException("WrapType not found"));

        Packages updatedPackage = existPackage.updatedPackage(orderBook, wrapType, packageRequestDto.getPackageName());

        return packageRepository.save(updatedPackage);
    }
}


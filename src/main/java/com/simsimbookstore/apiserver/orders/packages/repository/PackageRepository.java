package com.simsimbookstore.apiserver.orders.packages.repository;

import com.simsimbookstore.apiserver.orders.packages.entity.Packages;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PackageRepository extends JpaRepository<Packages, Long> {
}

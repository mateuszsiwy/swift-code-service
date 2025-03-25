package com.mateuszsiwy.swift_code_service.repository;

import com.mateuszsiwy.swift_code_service.entity.SwiftCode;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface SwiftCodeRepository extends JpaRepository<SwiftCode, String> {
    List<SwiftCode> findByCountryISO2(String countryISO2);
    Optional<SwiftCode> findBySwiftCode(String swiftCode);
}

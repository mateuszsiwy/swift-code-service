package com.mateuszsiwy.swift_code_service.service;

import com.mateuszsiwy.swift_code_service.dto.CountrySwiftCodesResponse;
import com.mateuszsiwy.swift_code_service.dto.SwiftCodeBranchResponse;
import com.mateuszsiwy.swift_code_service.dto.SwiftCodeHeadquartersResponse;
import com.mateuszsiwy.swift_code_service.entity.SwiftCode;
import com.mateuszsiwy.swift_code_service.exception.SwiftCodeNotFoundException;
import com.mateuszsiwy.swift_code_service.repository.SwiftCodeRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class SwiftCodeService {
    private final SwiftCodeRepository swiftCodeRepository;

    public SwiftCodeHeadquartersResponse getSwiftCodeDetails(String swiftCode) {
        SwiftCode code = swiftCodeRepository.findBySwiftCode(swiftCode)
                .orElseThrow(() -> new SwiftCodeNotFoundException("Swift code not found"));

        SwiftCodeHeadquartersResponse response = new SwiftCodeHeadquartersResponse();
        response.setSwiftCode(code.getSwiftCode());
        response.setBankName(code.getBankName());
        response.setAddress(code.getAddress());
        response.setCountryISO2(code.getCountryISO2());
        response.setCountryName(code.getCountryName());
        response.setHeadquarter(code.isHeadquarter());
        if (code.isHeadquarter()) {
            response.setBranches(code.getBranches().stream().map(this::convertToSwiftCodeBranchResponse).collect(Collectors.toList()));
        }

        return response;
    }

    public CountrySwiftCodesResponse getSwiftCodesByCountry(String countryISO2){
        List<SwiftCode> codes = swiftCodeRepository.findByCountryISO2(countryISO2);
        if (codes.isEmpty()) {
            throw new EntityNotFoundException("No swift codes found for country " + countryISO2);
        }
        CountrySwiftCodesResponse response = new CountrySwiftCodesResponse();
        response.setCountryISO2(countryISO2);
        response.setCountryName(codes.get(0).getCountryName().toString());
        response.setBranches(codes.stream().map(this::convertToSwiftCodeBranchResponse).collect(Collectors.toList()));
        return response;
    }

    private SwiftCodeBranchResponse convertToSwiftCodeBranchResponse(SwiftCode branch) {
        SwiftCodeBranchResponse response = new SwiftCodeBranchResponse();
        response.setSwiftCode(branch.getSwiftCode());
        response.setBankName(branch.getBankName());
        response.setAddress(branch.getAddress());
        response.setCountryISO2(branch.getCountryISO2());
        response.setHeadquarter(branch.isHeadquarter());
        return response;
    }
}

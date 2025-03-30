package com.mateuszsiwy.swift_code_service.service;

import com.mateuszsiwy.swift_code_service.dto.CountrySwiftCodesResponse;
import com.mateuszsiwy.swift_code_service.dto.MessageResponse;
import com.mateuszsiwy.swift_code_service.dto.SwiftCodeBranchResponse;
import com.mateuszsiwy.swift_code_service.dto.SwiftCodeResponse;
import com.mateuszsiwy.swift_code_service.entity.SwiftCode;
import com.mateuszsiwy.swift_code_service.exception.SwiftCodeNotFoundException;
import com.mateuszsiwy.swift_code_service.repository.SwiftCodeRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class SwiftCodeService {
    private final SwiftCodeRepository swiftCodeRepository;

    public SwiftCodeResponse getSwiftCodeDetails(String swiftCode) {
        SwiftCode code = swiftCodeRepository.findBySwiftCode(swiftCode)
                .orElseThrow(() -> new SwiftCodeNotFoundException("Swift code not found"));

        SwiftCodeResponse response = new SwiftCodeResponse();
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
        response.setCountryName(codes.getFirst().getCountryName());
        response.setBranches(codes.stream().map(this::convertToSwiftCodeBranchResponse).collect(Collectors.toList()));
        return response;
    }

    private SwiftCodeBranchResponse convertToSwiftCodeBranchResponse(SwiftCode branch) {
        SwiftCodeBranchResponse response = new SwiftCodeBranchResponse();
        response.setSwiftCode(branch.getSwiftCode());
        response.setBankName(branch.getBankName());
        response.setAddress(branch.getAddress());
        response.setCountryISO2(branch.getCountryISO2());
        response.setCountryName(branch.getCountryName());
        response.setHeadquarter(branch.isHeadquarter());
        return response;
    }

    @Transactional
    public MessageResponse addSwiftCode(SwiftCode swiftCode) {

        swiftCode.setCountryISO2(swiftCode.getCountryISO2().toUpperCase());
        swiftCode.setCountryName(swiftCode.getCountryName().toUpperCase());


        if (!swiftCode.isHeadquarter() && swiftCode.getSwiftCode().length() >= 8) {
            String headCode = swiftCode.getSwiftCode().substring(0, 8) + "XXX";
            swiftCodeRepository.findBySwiftCode(headCode).ifPresent(swiftCode::setHeadquarters);
        }

        swiftCodeRepository.save(swiftCode);
        return new MessageResponse("Swift code added successfully");
    }

    @Transactional
    public MessageResponse deleteSwiftCode(String swiftCode) {
        SwiftCode code = swiftCodeRepository.findBySwiftCode(swiftCode)
                .orElseThrow(() -> new SwiftCodeNotFoundException("Swift code not found: " + swiftCode));

        swiftCodeRepository.delete(code);
        return new MessageResponse("Swift code deleted successfully");
    }
}

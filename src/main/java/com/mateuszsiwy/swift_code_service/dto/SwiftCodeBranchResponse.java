package com.mateuszsiwy.swift_code_service.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SwiftCodeBranchResponse {
    private String swiftCode;
    private String bankName;
    private String address;
    private String countryISO2;
    private String countryName;
    @JsonProperty("isHeadquarter")
    private boolean isHeadquarter;
}

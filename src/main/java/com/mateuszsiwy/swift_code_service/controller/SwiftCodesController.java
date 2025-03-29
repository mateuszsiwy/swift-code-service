package com.mateuszsiwy.swift_code_service.controller;


import com.mateuszsiwy.swift_code_service.dto.CountrySwiftCodesResponse;
import com.mateuszsiwy.swift_code_service.dto.SwiftCodeResponse;
import com.mateuszsiwy.swift_code_service.entity.SwiftCode;
import com.mateuszsiwy.swift_code_service.service.SwiftCodeService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RequestMapping("/v1/swift-codes/")
@RestController
@CrossOrigin(origins = "*")
public class SwiftCodesController {
    private final SwiftCodeService swiftCodeService;

    public SwiftCodesController(SwiftCodeService swiftCodeService) {
        this.swiftCodeService = swiftCodeService;
    }

    @GetMapping("{swiftCode}")
    public ResponseEntity<SwiftCodeResponse> getSwiftCodeDetails(@PathVariable String swiftCode) {
        SwiftCodeResponse response = swiftCodeService.getSwiftCodeDetails(swiftCode);
        return ResponseEntity.ok(response);
    }
    @GetMapping("/country/{countryISO2}")
    public ResponseEntity<CountrySwiftCodesResponse> getSwiftCodesByCountry(@PathVariable String countryISO2) {
        CountrySwiftCodesResponse response = swiftCodeService.getSwiftCodesByCountry(countryISO2);
        return ResponseEntity.ok(response);
    }
    @PostMapping
    public ResponseEntity<String> addSwiftCode(@RequestBody SwiftCodeResponse swiftCodeRequest) {
        SwiftCode swiftCode = new SwiftCode();
        swiftCode.setSwiftCode(swiftCodeRequest.getSwiftCode());
        swiftCode.setBankName(swiftCodeRequest.getBankName());
        swiftCode.setAddress(swiftCodeRequest.getAddress());
        swiftCode.setCountryISO2(swiftCodeRequest.getCountryISO2());
        swiftCode.setCountryName(swiftCodeRequest.getCountryName());
        swiftCode.setHeadquarter(swiftCodeRequest.isHeadquarter());

        swiftCodeService.addSwiftCode(swiftCode);
        return ResponseEntity.ok("Swift code added successfully");
    }
    @DeleteMapping("{swiftCode}")
    public ResponseEntity<String> deleteSwiftCode(@PathVariable String swiftCode) {
        swiftCodeService.deleteSwiftCode(swiftCode);
        return ResponseEntity.ok("Swift code deleted successfully");
    }
}

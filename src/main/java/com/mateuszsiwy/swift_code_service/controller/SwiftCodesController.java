package com.mateuszsiwy.swift_code_service.controller;


import com.mateuszsiwy.swift_code_service.dto.CountrySwiftCodesResponse;
import com.mateuszsiwy.swift_code_service.dto.MessageResponse;
import com.mateuszsiwy.swift_code_service.dto.SwiftCodeResponse;
import com.mateuszsiwy.swift_code_service.entity.SwiftCode;
import com.mateuszsiwy.swift_code_service.exception.SwiftCodeNotFoundException;
import com.mateuszsiwy.swift_code_service.service.SwiftCodeService;
import jakarta.persistence.EntityNotFoundException;
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
        try{
            SwiftCodeResponse response = swiftCodeService.getSwiftCodeDetails(swiftCode);
            return ResponseEntity.ok(response);
        } catch(SwiftCodeNotFoundException e) {
            return ResponseEntity.notFound().build();
        }
    }
    @GetMapping("/country/{countryISO2}")
    public ResponseEntity<CountrySwiftCodesResponse> getSwiftCodesByCountry(@PathVariable String countryISO2) {
        try{
            CountrySwiftCodesResponse response = swiftCodeService.getSwiftCodesByCountry(countryISO2);
            return ResponseEntity.ok(response);
        } catch(EntityNotFoundException e) {
            return ResponseEntity.notFound().build();
        }

    }
    @PostMapping
    public ResponseEntity<String> addSwiftCode(@RequestBody SwiftCodeResponse swiftCodeRequest) {
        try{
            SwiftCode swiftCode = new SwiftCode();
            swiftCode.setSwiftCode(swiftCodeRequest.getSwiftCode());
            swiftCode.setBankName(swiftCodeRequest.getBankName());
            swiftCode.setAddress(swiftCodeRequest.getAddress());
            swiftCode.setCountryISO2(swiftCodeRequest.getCountryISO2());
            swiftCode.setCountryName(swiftCodeRequest.getCountryName());
            swiftCode.setHeadquarter(swiftCodeRequest.isHeadquarter());
            MessageResponse response = swiftCodeService.addSwiftCode(swiftCode);
            return ResponseEntity.ok(response.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(500).body("An error occurred while adding the swift code");
        }

    }
    @DeleteMapping("{swiftCode}")
    public ResponseEntity<String> deleteSwiftCode(@PathVariable String swiftCode) {
        try{
            MessageResponse response = swiftCodeService.deleteSwiftCode(swiftCode);
            return ResponseEntity.ok(response.getMessage());
        } catch (SwiftCodeNotFoundException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(500).body("An error occurred while deleting the swift code");
        }

    }
}

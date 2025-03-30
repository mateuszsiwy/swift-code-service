package com.mateuszsiwy.swift_code_service.integration.service;

import com.mateuszsiwy.swift_code_service.dto.CountrySwiftCodesResponse;
import com.mateuszsiwy.swift_code_service.dto.MessageResponse;
import com.mateuszsiwy.swift_code_service.dto.SwiftCodeResponse;
import com.mateuszsiwy.swift_code_service.entity.SwiftCode;
import com.mateuszsiwy.swift_code_service.exception.SwiftCodeNotFoundException;
import com.mateuszsiwy.swift_code_service.repository.SwiftCodeRepository;
import com.mateuszsiwy.swift_code_service.service.SwiftCodeService;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
public class SwiftCodeServiceIntegrationTest {

    @Autowired
    private SwiftCodeService swiftCodeService;

    @Autowired
    private SwiftCodeRepository swiftCodeRepository;

    @BeforeEach
    void setUp() {
        swiftCodeRepository.deleteAll();

        SwiftCode headquarters = new SwiftCode();
        headquarters.setSwiftCode("TESTTESTXXX");
        headquarters.setBankName("MBANK SA");
        headquarters.setAddress("ul. Test, Gdynia");
        headquarters.setCountryISO2("PL");
        headquarters.setCountryName("POLAND");
        headquarters.setHeadquarter(true);
        headquarters.setBranches(new ArrayList<>());
        headquarters = swiftCodeRepository.save(headquarters);

        SwiftCode branch = new SwiftCode();
        branch.setSwiftCode("TESTTEST");
        branch.setBankName("MBANK ODDZIAŁ");
        branch.setAddress("ul. Test Oddzial, Sopot");
        branch.setCountryISO2("PL");
        branch.setCountryName("POLAND");
        branch.setHeadquarter(false);
        branch.setHeadquarters(headquarters);

        headquarters.getBranches().add(branch);
        swiftCodeRepository.save(branch);
        swiftCodeRepository.flush();

        SwiftCode foreignBank = new SwiftCode();
        foreignBank.setSwiftCode("SPANISHTXXX");
        foreignBank.setBankName("SANTANDER BANK");
        foreignBank.setAddress("Somewhere 12, Spain");
        foreignBank.setCountryISO2("SP");
        foreignBank.setCountryName("SPAIN");
        foreignBank.setHeadquarter(true);
        foreignBank.setBranches(new ArrayList<>());

        swiftCodeRepository.save(foreignBank);
        swiftCodeRepository.flush();
    }

    @Test
    void shouldReturnSwiftCodeDetails() {
        SwiftCodeResponse response = swiftCodeService.getSwiftCodeDetails("TESTTESTXXX");

        assertThat(response.getSwiftCode()).isEqualTo("TESTTESTXXX");
        assertThat(response.getBankName()).isEqualTo("MBANK SA");
        assertThat(response.getCountryISO2()).isEqualTo("PL");
        assertThat(response.isHeadquarter()).isTrue();
        assertThat(response.getBranches()).hasSize(1);
        assertThat(response.getBranches().get(0).getSwiftCode()).isEqualTo("TESTTEST");
    }

    @Test
    void shouldReturnSwiftCodesByCountry() {
        CountrySwiftCodesResponse response = swiftCodeService.getSwiftCodesByCountry("PL");

        assertThat(response.getCountryISO2()).isEqualTo("PL");
        assertThat(response.getCountryName()).isEqualTo("POLAND");
        assertThat(response.getBranches()).hasSize(2);
    }

    @Test
    void shouldAddSwiftCode() {
        SwiftCode newCode = new SwiftCode();
        newCode.setSwiftCode("PKOPPLPW");
        newCode.setBankName("PKO Bank Polski");
        newCode.setAddress("ul. Puławska, Warszawa");
        newCode.setCountryISO2("pl");
        newCode.setCountryName("Poland");
        newCode.setHeadquarter(false);

        MessageResponse response = swiftCodeService.addSwiftCode(newCode);

        assertThat(response.getMessage()).isEqualTo("Swift code added successfully");

        SwiftCode saved = swiftCodeRepository.findBySwiftCode("PKOPPLPW").orElseThrow();
        assertThat(saved.getCountryISO2()).isEqualTo("PL");
        assertThat(saved.getCountryName()).isEqualTo("POLAND");
    }

    @Test
    void shouldAddAndLinkBranchToHeadquarter() {
        SwiftCode newBranch = new SwiftCode();
        newBranch.setSwiftCode("TESTTESTTest");
        newBranch.setBankName("MBANK ODDZIAŁ 2");
        newBranch.setAddress("ul. Nowa 1, Gdańsk");
        newBranch.setCountryISO2("PL");
        newBranch.setCountryName("Poland");
        newBranch.setHeadquarter(false);

        swiftCodeService.addSwiftCode(newBranch);

        SwiftCode savedBranch = swiftCodeRepository.findBySwiftCode("TESTTESTTest").orElseThrow();
        assertThat(savedBranch.getHeadquarters()).isNotNull();
        assertThat(savedBranch.getHeadquarters().getSwiftCode()).isEqualTo("TESTTESTXXX");


    }

    @Test
    void shouldDeleteSwiftCode() {
        MessageResponse response = swiftCodeService.deleteSwiftCode("TESTTEST");

        assertThat(response.getMessage()).isEqualTo("Swift code deleted successfully");
        assertThat(swiftCodeRepository.findBySwiftCode("TESTTEST")).isEmpty();
    }

    @Test
    void shouldThrowExceptionForNonExistentSwiftCode() {
        assertThrows(SwiftCodeNotFoundException.class, () ->
                swiftCodeService.getSwiftCodeDetails("NONEXISTENT"));
    }

    @Test
    void shouldThrowExceptionForNonExistentCountry() {
        assertThrows(EntityNotFoundException.class, () ->
                swiftCodeService.getSwiftCodesByCountry("XY"));
    }
}
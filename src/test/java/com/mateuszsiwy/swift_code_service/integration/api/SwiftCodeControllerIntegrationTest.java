package com.mateuszsiwy.swift_code_service.integration.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mateuszsiwy.swift_code_service.entity.SwiftCode;
import com.mateuszsiwy.swift_code_service.repository.SwiftCodeRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;

import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
public class SwiftCodeControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private SwiftCodeRepository swiftCodeRepository;

    @Autowired
    private ObjectMapper objectMapper;

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
    void shouldReturnSwiftCodeDetails() throws Exception {
        mockMvc.perform(get("/v1/swift-codes/TESTTESTXXX")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.swiftCode").value("TESTTESTXXX"))
                .andExpect(jsonPath("$.bankName").value("MBANK SA"))
                .andExpect(jsonPath("$.countryISO2").value("PL"))
                .andExpect(jsonPath("$.headquarter").value(true))
                .andExpect(jsonPath("$.branches", hasSize(1)))
                .andExpect(jsonPath("$.branches[0].swiftCode").value("TESTTEST"));
    }

    @Test
    void shouldReturnSwiftCodesByCountry() throws Exception {
        mockMvc.perform(get("/v1/swift-codes/country/PL")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.countryISO2").value("PL"))
                .andExpect(jsonPath("$.countryName").value("POLAND"))
                .andExpect(jsonPath("$.branches", hasSize(2)));
    }

    @Test
    void shouldAddSwiftCode() throws Exception {
        SwiftCode newCode = new SwiftCode();
        newCode.setSwiftCode("PKOPPLPW");
        newCode.setBankName("PKO Bank Polski");
        newCode.setAddress("ul. Puławska, Warszawa");
        newCode.setCountryISO2("pl");
        newCode.setCountryName("Poland");
        newCode.setHeadquarter(false);

        mockMvc.perform(post("/v1/swift-codes/")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(newCode)))
                .andExpect(status().isOk())
                .andExpect(content().string("Swift code added successfully"));

        mockMvc.perform(get("/v1/swift-codes/PKOPPLPW")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.countryISO2").value("PL"))
                .andExpect(jsonPath("$.countryName").value("POLAND"));
    }

    @Test
    void shouldDeleteSwiftCode() throws Exception {
        mockMvc.perform(delete("/v1/swift-codes/TESTTEST")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().string("Swift code deleted successfully"));

        mockMvc.perform(get("/v1/swift-codes/TESTTEST")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    void shouldReturn404ForNonExistentSwiftCode() throws Exception {
        mockMvc.perform(get("/v1/swift-codes/NONEXISTENT")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    void shouldReturn404ForNonExistentCountry() throws Exception {
        mockMvc.perform(get("/v1/swift-codes/country/XY")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }
}
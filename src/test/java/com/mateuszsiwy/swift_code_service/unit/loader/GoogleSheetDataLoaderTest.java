package com.mateuszsiwy.swift_code_service.unit.loader;

import com.mateuszsiwy.swift_code_service.entity.SwiftCode;
import com.mateuszsiwy.swift_code_service.loader.GoogleSheetDataLoader;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.client.RestTemplate;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class GoogleSheetDataLoaderTest {

    @Mock
    private RestTemplate restTemplate;

    private GoogleSheetDataLoader dataLoader;

    private static final String TEST_URL = "https://docs.google.com/spreadsheets/test";

    @BeforeEach
    void setUp() {
        dataLoader = new GoogleSheetDataLoader(restTemplate);
        ReflectionTestUtils.setField(dataLoader, "googleSheetUrl", TEST_URL);
    }

    @Test
    void shouldLoadDataFromGoogleSheet() {
        String csvData = "Country ISO2,Swift Code,Type,Bank Name,Address,City,Country Name\n" +
                "PL,TESTTESTXXX,Main,MBANK SA,\"ul. Test, Gdynia\",Gdynia,Poland\n" +
                "PL,TESTTEST,Branch,MBANK ODDZIAŁ,\"ul. Test Oddzial, Sopot\",Sopot,Poland\n" +
                "SP,SPANISHTXXX,Main,SANTANDER BANK,\"Somewhere 12, Spain\",Madrid,Spain";

        when(restTemplate.getForObject(eq(TEST_URL + "/export?format=csv"), eq(String.class)))
                .thenReturn(csvData);

        List<SwiftCode> result = dataLoader.loadDataFromGoogleSheet();

        assertThat(result).hasSize(3);

        SwiftCode firstCode = result.get(0);
        assertThat(firstCode.getSwiftCode()).isEqualTo("TESTTESTXXX");
        assertThat(firstCode.getBankName()).isEqualTo("MBANK SA");
        assertThat(firstCode.getCountryISO2()).isEqualTo("PL");
        assertThat(firstCode.isHeadquarter()).isTrue();

        SwiftCode secondCode = result.get(1);
        assertThat(secondCode.getSwiftCode()).isEqualTo("TESTTEST");
        assertThat(secondCode.isHeadquarter()).isFalse();

        SwiftCode thirdCode = result.get(2);
        assertThat(thirdCode.getSwiftCode()).isEqualTo("SPANISHTXXX");
        assertThat(thirdCode.getBankName()).isEqualTo("SANTANDER BANK");
        assertThat(thirdCode.getCountryISO2()).isEqualTo("SP");
        assertThat(thirdCode.isHeadquarter()).isTrue();
    }

    @Test
    void shouldHandleEmptyResponse() {
        when(restTemplate.getForObject(eq(TEST_URL + "/export?format=csv"), eq(String.class)))
                .thenReturn("");

        List<SwiftCode> result = dataLoader.loadDataFromGoogleSheet();

        assertThat(result).isEmpty();
    }

    @Test
    void shouldHandleNullResponse() {
        when(restTemplate.getForObject(eq(TEST_URL + "/export?format=csv"), eq(String.class)))
                .thenReturn(null);

        List<SwiftCode> result = dataLoader.loadDataFromGoogleSheet();

        assertThat(result).isEmpty();
    }

    @Test
    void shouldHandleRestTemplateException() {
        when(restTemplate.getForObject(eq(TEST_URL + "/export?format=csv"), eq(String.class)))
                .thenThrow(new RuntimeException("Connection error"));

        assertThrows(RuntimeException.class, () -> dataLoader.loadDataFromGoogleSheet());
    }
}
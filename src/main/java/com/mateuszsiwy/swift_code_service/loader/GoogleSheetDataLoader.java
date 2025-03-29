package com.mateuszsiwy.swift_code_service.loader;

import com.mateuszsiwy.swift_code_service.entity.SwiftCode;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;

@Component
@Slf4j
public class GoogleSheetDataLoader {

    @Value("${google.sheet.url}")
    private String googleSheetUrl;

    public List<SwiftCode> loadDataFromGoogleSheet() {
        log.info("Loading data from Google Sheet: {}", googleSheetUrl);
        List<SwiftCode> swiftCodes = new ArrayList<>();

        String csvUrl = googleSheetUrl + "/export?format=csv";
        RestTemplate restTemplate = new RestTemplate();
        String csvData = restTemplate.getForObject(csvUrl, String.class);

        if (csvData == null || csvData.isEmpty()) {
            log.error("No data received from Google Sheet");
            return swiftCodes;
        }

        try (BufferedReader reader = new BufferedReader(new StringReader(csvData))) {
            reader.readLine();

            String line;
            while ((line = reader.readLine()) != null) {
                List<String> values = new ArrayList<>();
                StringBuilder currentValue = new StringBuilder();
                boolean inQuotes = false;

                for (char c : line.toCharArray()) {
                    if (c == '"') {
                        inQuotes = !inQuotes;
                    } else if (c == ',' && !inQuotes) {
                        values.add(currentValue.toString().trim());
                        currentValue.setLength(0);
                    } else {
                        currentValue.append(c);
                    }
                }
                values.add(currentValue.toString().trim());

                if (values.size() >= 7) {
                    SwiftCode swiftCode = new SwiftCode();
                    swiftCode.setCountryISO2(values.get(0).toUpperCase());
                    swiftCode.setSwiftCode(values.get(1));
                    swiftCode.setHeadquarter(swiftCode.getSwiftCode().endsWith("XXX"));
                    swiftCode.setBankName(values.get(3));
                    swiftCode.setAddress(values.get(4));
                    swiftCode.setCountryName(values.get(6).toUpperCase());
                    swiftCodes.add(swiftCode);
                }
            }
        } catch (IOException e) {
            log.error("Error reading CSV data", e);
            throw new RuntimeException("Failed to load data from Google Sheet", e);
        }

        long headquarters = swiftCodes.stream().filter(SwiftCode::isHeadquarter).count();
        log.info("Successfully loaded {} swift codes ({} headquarters, {} branches)",
            swiftCodes.size(), headquarters, swiftCodes.size() - headquarters);

        return swiftCodes;
    }
}
package com.mateuszsiwy.swift_code_service.config;

import com.mateuszsiwy.swift_code_service.loader.GoogleSheetDataLoader;
import com.mateuszsiwy.swift_code_service.repository.SwiftCodeRepository;
import jakarta.persistence.EntityManager;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import com.mateuszsiwy.swift_code_service.entity.SwiftCode;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

@Component
@Slf4j
@Transactional
public class DataInitializer implements CommandLineRunner {
    private final GoogleSheetDataLoader googleSheetDataLoader;
    private final SwiftCodeRepository swiftCodeRepository;
    private final EntityManager entityManager;

    public DataInitializer(GoogleSheetDataLoader googleSheetDataLoader, SwiftCodeRepository swiftCodeRepository, EntityManager entityManager) {
        this.googleSheetDataLoader = googleSheetDataLoader;
        this.swiftCodeRepository = swiftCodeRepository;
        this.entityManager = entityManager;
        log.info("DataInitializer initialized");
    }

    @Override
    @Transactional
    public void run(String... args) {
        log.info("Running DataInitializer...");
        try {
            swiftCodeRepository.deleteAll();
            swiftCodeRepository.flush();
            entityManager.clear();
            List<SwiftCode> swiftCodes = googleSheetDataLoader.loadDataFromGoogleSheet();
            log.info("Loaded {} total swift codes", swiftCodes.size());

            List<SwiftCode> headquarterCodes = swiftCodes.stream()
                    .filter(SwiftCode::isHeadquarter)
                    .toList();
            swiftCodeRepository.saveAll(headquarterCodes);
            swiftCodeRepository.flush();
            entityManager.clear();
            log.info("Saved {} headquarters", headquarterCodes.size());

            List<SwiftCode> branchCodes = swiftCodes.stream()
                    .filter(code -> !code.isHeadquarter())
                    .toList();

            AtomicInteger linkedBranches = new AtomicInteger();
            for (SwiftCode branch : branchCodes) {
                if (branch.getSwiftCode() != null && branch.getSwiftCode().length() >= 8) {
                    String headCode = branch.getSwiftCode().substring(0, 8);
                    List<SwiftCode> potentialHeadquarters = swiftCodeRepository.findBySwiftCodeStartsWith(headCode).orElse(null);
                    for (SwiftCode potentialHeadquarter : potentialHeadquarters) {
                        if (potentialHeadquarter.isHeadquarter()) {
                            branch.setHeadquarters(potentialHeadquarter);
                            potentialHeadquarter.getBranches().add(branch);
                            linkedBranches.incrementAndGet();
                            break;
                        }
                    }
                }
            }

            swiftCodeRepository.saveAll(branchCodes);
            swiftCodeRepository.flush();
            entityManager.clear();
            log.info("Saved {} branches, linked {} to headquarters", branchCodes.size(), linkedBranches);

            long totalCount = swiftCodeRepository.count();
            log.info("Total records in database: {}", totalCount);

        } catch (Exception e) {
            log.error("Error initializing data", e);
            throw new RuntimeException("Failed to initialize data", e);
        }
    }
}
package com.mateuszsiwy.swift_code_service.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "swift_codes")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class SwiftCode {
    @Id
    @Column(unique = true, nullable = false)
    private String swiftCode;

    private String bankName;
    private String address;
    private String countryISO2;
    private String countryName;
    private boolean isHeadquarter;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "headquarters_swift_code")
    private SwiftCode headquarters;

    @OneToMany(mappedBy = "headquarters", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<SwiftCode> branches = new ArrayList<>();


}

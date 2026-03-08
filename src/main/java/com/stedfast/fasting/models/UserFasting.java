package com.stedfast.fasting.models;

import java.time.ZonedDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "userFasting")
@Data
@NoArgsConstructor
public class UserFasting {

    @Id
    @Column(length = 31)
    private String userId;

    @Enumerated(EnumType.STRING)
    private FastingStatus status;
    private ZonedDateTime startTime;

}

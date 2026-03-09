package com.stedfast.user.models;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "userConfig")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserConfig {

    @Id
    @Column(name = "userId", length = 31)
    private String userId;

    @Column(name = "fastingWindow")
    private int fastingWindow = 18;

    @Column(name = "eatingWindow")
    private int eatingWindow = 6;

    @Column(name = "calorieLimit")
    private int calorieLimit = 2000;

    @Column(name = "fastingStartTime")
    @com.fasterxml.jackson.annotation.JsonFormat(shape = com.fasterxml.jackson.annotation.JsonFormat.Shape.STRING, pattern = "h:mm a")
    private java.time.LocalTime fastingStartTime = java.time.LocalTime.of(19, 0);
}

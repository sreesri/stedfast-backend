package com.stedfast.weight;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/weights")
@RequiredArgsConstructor
public class WeightLogController {

    private final WeightLogService weightLogService;

    @PostMapping
    public ResponseEntity<WeightLog> logWeight(@RequestBody WeightLogRequest request) {
        WeightLog weightLog = weightLogService.logWeight(request);
        return new ResponseEntity<>(weightLog, HttpStatus.CREATED);
    }
}

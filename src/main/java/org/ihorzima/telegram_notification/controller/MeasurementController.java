package org.ihorzima.telegram_notification.controller;

import org.ihorzima.telegram_notification.model.Measurement;
import org.ihorzima.telegram_notification.service.MeasurementService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/measurement")
public class MeasurementController {

    private final MeasurementService measurementService;

    public MeasurementController(MeasurementService measurementService) {
        this.measurementService = measurementService;
    }

    @PostMapping("/process")
    @ResponseStatus(HttpStatus.OK)
    public String processMeasurement(@RequestBody List<Measurement> measurements) {
        measurementService.processMeasurements(measurements);
        return "received data for measurement sheet successfully";
    }
}
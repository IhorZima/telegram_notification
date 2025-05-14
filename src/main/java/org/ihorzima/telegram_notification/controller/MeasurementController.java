package org.ihorzima.telegram_notification.controller;

import lombok.extern.slf4j.Slf4j;
import org.ihorzima.telegram_notification.model.Measurement;
import org.ihorzima.telegram_notification.service.MeasurementService;
import org.ihorzima.telegram_notification.service.MeasurementsGoogleSheetReader;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/measurement")
public class MeasurementController {

    private final MeasurementService measurementService;
    private final MeasurementsGoogleSheetReader measurementsGoogleSheetReader;

    public MeasurementController(MeasurementService measurementService,
                                 MeasurementsGoogleSheetReader measurementsGoogleSheetReader) {
        this.measurementService = measurementService;
        this.measurementsGoogleSheetReader = measurementsGoogleSheetReader;
    }

    @PostMapping("/process")
    @ResponseStatus(HttpStatus.OK)
    public String processMeasurement(@RequestBody List<Measurement> measurements) {
        measurementService.processMeasurements(measurements);
        return "received data for measurement sheet successfully";
    }

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public String retrieveAndProcessMeasurements() {
        log.info("Going to retrieve measurement data");
        try {
            List<Measurement> measurementsToProcess = measurementsGoogleSheetReader.getMeasurements();

            log.info("Going to process {} measurements", measurementsToProcess.size());
            boolean allProcessed = measurementService.processMeasurements(measurementsToProcess);
            return allProcessed ? "All measurements has been processed!" : "Some measurements failed to process";
        } catch (Exception e) {
            log.error("Failed to process measurements", e);
            return "Something went wrong: " + e.getMessage();
        }
    }
}
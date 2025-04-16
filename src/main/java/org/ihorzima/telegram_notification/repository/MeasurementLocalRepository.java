package org.ihorzima.telegram_notification.repository;

import org.ihorzima.telegram_notification.model.Measurement;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class MeasurementLocalRepository {
    private List<Measurement> measurements = new ArrayList<>();

    public void saveMeasurements(List<Measurement> measurements) {
        this.measurements = List.copyOf(measurements);
    }

    public List<Measurement> getMeasurements() {
        return this.measurements;
    }
}

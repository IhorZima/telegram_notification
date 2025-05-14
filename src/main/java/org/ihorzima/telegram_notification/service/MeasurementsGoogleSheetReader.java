package org.ihorzima.telegram_notification.service;

import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.services.sheets.v4.Sheets;
import com.google.api.services.sheets.v4.model.ValueRange;
import com.google.auth.http.HttpCredentialsAdapter;
import com.google.auth.oauth2.ServiceAccountCredentials;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.ihorzima.telegram_notification.model.Measurement;

import java.io.FileInputStream;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

@Slf4j
@RequiredArgsConstructor
public class MeasurementsGoogleSheetReader {
    private static final String APPLICATION_NAME = "My Spring App";
    private static final JsonFactory JSON_FACTORY = GsonFactory.getDefaultInstance();
    private static final String SPREADSHEET_ID = "19h7uFLJY_BogjLlwyHtz7SZJl5jEA-GIlkwX9ipDth4";
    private static final String RANGE = "Зведені!A2:W10";

    private final String googleAuthKeyPath;

    public List<Measurement> getMeasurements() throws Exception {
        Sheets sheetsService = getSheetsService();
        ValueRange response = sheetsService.spreadsheets().values()
                .get(SPREADSHEET_ID, RANGE)
                .execute();

        List<List<Object>> values = response.getValues();

        if (values == null || values.isEmpty()) {
            log.info("No measurements found");
            return Collections.emptyList();
        }

        return toMeasurements(response);
    }


    private Sheets getSheetsService() throws Exception {
        log.info("Getting Sheets Service from Google Sheets API key: {}", googleAuthKeyPath);
        FileInputStream serviceAccountStream = new FileInputStream(googleAuthKeyPath);

        var credentials = ServiceAccountCredentials.fromStream(serviceAccountStream)
                .createScoped(List.of("https://www.googleapis.com/auth/spreadsheets.readonly"));

        return new Sheets.Builder(
                GoogleNetHttpTransport.newTrustedTransport(),
                JSON_FACTORY,
                new HttpCredentialsAdapter(credentials)
        ).setApplicationName(APPLICATION_NAME).build();
    }

    public List<Measurement> toMeasurements(ValueRange response) {
        List<Measurement> measurements = new LinkedList<>();
        List<List<Object>> values = response.getValues();

        for (List<Object> row : values) {
            Measurement m = new Measurement();
            m.setLandId(getValue(row, 1));
            m.setNameSecondName(getValue(row, 2));
            m.setStreet(getValue(row, 3));
            m.setPreviousIndex(getValue(row, 4));
            m.setPreviousDate(getValue(row, 5));
            m.setCurrentDate(getValue(row, 6));
            m.setCurrentDay(getValue(row, 7));
            m.setPreviousDay(getValue(row, 8));
            m.setCurrentNight(getValue(row, 9));
            m.setPreviousNight(getValue(row, 10));
            m.setPaymentDate(getValue(row, 11));
            m.setLastPaymentIndicatorsNight(getValue(row, 12));
            m.setLastPaymentIndicatorsDaily(getValue(row, 13));
            m.setLastPaymentAmount(getValue(row, 14));
            m.setTotalNightDebtForPreviousPeriodsUAH(getValue(row, 15));
            m.setTotalDailyDebtForPreviousPeriodsUAH(getValue(row, 16));
            m.setToBePaid(getValue(row, 17));
            m.setTelegramId(getValue(row, 18));
            m.setState(getValue(row, 22));

            measurements.add(m);
        }
        return measurements;
    }

    private static String getValue(List<Object> row, int index) {
        return row.get(index) != null ? row.get(index).toString().trim() : "";
    }
}



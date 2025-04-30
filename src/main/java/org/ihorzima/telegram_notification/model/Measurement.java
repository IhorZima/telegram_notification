package org.ihorzima.telegram_notification.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class Measurement {
    private String landId;
    private String nameSecondName;
    private String street;
    private String previousIndex;
    private String previousDate;
    private String previousNight;
    private String previousDay;
    private String currentDate;
    private String currentNight;
    private String currentDay;
    private String paymentDate;
    private String lastPaymentIndicatorsNight;
    private String lastPaymentIndicatorsDaily;
    private String lastPaymentAmount;
    private String totalNightDebtForPreviousPeriodsUAH;
    private String totalDailyDebtForPreviousPeriodsUAH;
    private String overnightDebtForTheCurrentPeriodUAH;
    private String dailyDebtForTheCurrentPeriodUAH;
    private String toBePaid;
    private String telegramId;

}
function sendMeasurements() {
  var allSheet = SpreadsheetApp.getActiveSpreadsheet();
  var sheetZvedeni = allSheet.getSheetByName("Зведені");
  var url = "http://<REPLACE_BY_YOUR_IP>:<YOUR_PORT>/";

  var jsonDateZvedeni = [];
  var zvedeniDate = sheetZvedeni.getDataRange().getValues();

  for (var i = 1; i < zvedeniDate.length; i++) {
    var row = zvedeniDate[i];
    var rowData = {
      landId: getStringOrDefault(row[0]),
      nameSecondName: getStringOrDefault(row[1]),
      street: getStringOrDefault(row[2]),
      previousIndex: getStringOrDefault(row[3]),
      previousDate: getStringOrDefault(row[4]),
      previousNight: getStringOrDefault(row[5]),
      previousDay: getStringOrDefault(row[6]),
      currentDate: getStringOrDefault(row[7]) ,
      currentNight: getStringOrDefault(row[8]),
      currentDay: getStringOrDefault(row[9]),
      paymentDate: getStringOrDefault(row[10]),
      lastPaymentIndicatorsNight: getStringOrDefault(row[11]),
      lastPaymentIndicatorsDaily: getStringOrDefault(row[12]),
      lastPaymentAmount: getStringOrDefault(row[13]),
      totalNightDebtForPreviousPeriodsUAH: getStringOrDefault(row[14]),
      totalDailyDebtForPreviousPeriodsUAH: getStringOrDefault(row[15]),
      overnightDebtForTheCurrentPeriodUAH: getStringOrDefault(row[16]),
      dailyDebtForTheCurrentPeriodUAH: getStringOrDefault(row[17]),
      toBePaid: getStringOrDefault(row[18]),
      telegramId: getStringOrDefault(row[19])
    }
    jsonDateZvedeni.push(rowData);
  }

  var optionsForZvedeni = {
    "method": "post",
    "contentType": "application/json",
    "payload": JSON.stringify(jsonDateZvedeni)
  };

  try {
    var response = UrlFetchApp.fetch(url + "measurement/process", optionsForZvedeni);
    Logger.log(response.getContentText());
  } catch (e) {
    Logger.log("Error: " + e.toString());
  }

  function getStringOrDefault(value) {
  return value != null && value != "" ? value : null;
  }
}

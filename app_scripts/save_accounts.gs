function saveAccounts() {
  var allSheet = SpreadsheetApp.getActiveSpreadsheet();
  var sheetDovidnik = allSheet.getSheetByName("Довідник");
  var url = "http://<REPLACE_BY_YOUR_IP>:<YOUR_PORT>/";

  var jsonDataDovidnik = [];
  var dovidnikData = sheetDovidnik.getDataRange().getValues();

  for (var i = 1; i < dovidnikData.length; i++) {
    var row = dovidnikData[i];
    var rowData = {
      landId: getStringOrDefault(row[0]),
      fullName: getStringOrDefault(row[1]),
      address: getStringOrDefault(row[2]),
      phoneNumber: getStringOrDefault(row[3]),
      note: getStringOrDefault(row[4]) ,
      telegramId: getStringOrDefault(row[5])
    }
    jsonDataDovidnik.push(rowData);
  }

  var options = {
    "method": "post",
    "contentType": "application/json",
    "payload": JSON.stringify(jsonDataDovidnik)
  };

  try {
    var response = UrlFetchApp.fetch(url + "account/save", options);
    Logger.log(response.getContentText());
  } catch (e) {
    Logger.log("Error: " + e.toString());
  }

  function getStringOrDefault(value) {
  return value != null && value != "" ? value : null;
  }
}

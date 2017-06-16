var wsServerUrl = "ws://192.168.106.235:8080";

var ws = new WebSocket(wsServerUrl);
ws.onmessage = onMessage;

var attackTypeData = [
    ["Type", "Count"]

];

var attackOriginData = [
    ["Country", "Count"]
];

var attackTimeData = [
    ["Time", "Count"],
    ["00:00", 0],
    ["01:00", 0],
    ["02:00", 0],
    ["03:00", 0],
    ["04:00", 0],
    ["05:00", 0],
    ["06:00", 0],
    ["07:00", 0],
    ["08:00", 0],
    ["09:00", 0],
    ["10:00", 0],
    ["11:00", 0],
    ["12:00", 0],
    ["13:00", 0],
    ["14:00", 0],
    ["15:00", 0],
    ["16:00", 0],
    ["17:00", 0],
    ["18:00", 0],
    ["19:00", 0],
    ["20:00", 0],
    ["21:00", 0],
    ["22:00", 0],
    ["23:00", 0]
];

function onMessage(e) {
    parseData(e.data);
}

function parseData(json) {
    json = JSON.parse(json);

    json.Hour += ":00";

    var index = indexOfKey(attackTypeData, json.Type);

    if (index != -1)
        attackTypeData[index][1]++;
    else
        attackTypeData.push([json.Type, 1]);

    attackTimeData[indexOfKey(attackTimeData, json.Hour)][1]++;

    if (json.ContainsIP == "true") {
        if (json.Location == undefined) {
            var index = indexOfKey(attackOriginData, json.CountryName);

            if (index != -1)
                attackOriginData[index][1]++;
            else
                attackOriginData.push([json.CountryName, 1]);

            var latitude = parseFloat(json.Latitude);
            var longitude = parseFloat(json.Longitude);
            var location = new google.maps.LatLng(latitude, longitude);
        } else {
            var index = indexOfKey(attackOriginData, json.Location);

            if (index != -1)
                attackOriginData[index][1]++;
            else
                attackOriginData.push([json.Location, 1]);
        }
    }

    addHeatMapPoint(location);
    drawCharts();
}

function indexOfKey(array, data) {
    for (var i = 0; i < array.length; i++) {
        if (data === array[i][0])
            return i;
    }

    return -1;
}

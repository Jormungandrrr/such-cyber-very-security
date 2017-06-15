google.charts.load('current', {'packages': ['corechart']});
google.charts.setOnLoadCallback(drawCharts);

var attackTypeChart;
var attackOriginChart;
var attackTimeChart;

const attackTypeOptions = {
    title: 'Attack types',
    height: '100%',
    width: '100%',
    chartArea: {left: "5%", top: "5%", width: "100%", height: "100%"}
};

const attackOriginOptions = {
    title: 'Attack origins',
    height: '100%',
    width: '100%',
    chartArea: {left: "5%", top: "5%", width: "100%", height: "100%"}
};

const attackTimeOptions = {
    title: 'Time of attacks',
    height: '100%',
    width: '100%',
    chartArea: {left: "5%", top: "5%", width: "100%", height: "80%"},
    hAxis: {slantedText: true, slantedTextAngle: 70}
};
var heatMap;
var map;
var array = [];
var heatMapPoints;

function initMap() {
    heatMapPoints = new google.maps.MVCArray(array);
    map = new google.maps.Map(document.getElementById('map'), {
        zoom: 1,
        center: {lat: 0, lng: 0},
        mapTypeId: 'terrain'
    });

    heatMap = new google.maps.visualization.HeatmapLayer({
        data: heatMapPoints
    });

    heatMap.setMap(map);
}

function addHeatMapPoint(location) {
    heatMapPoints.push(location);
}

function drawCharts() {
    var attackOriginDataTable = google.visualization.arrayToDataTable(attackOriginData);
    var attackTimeDataTable = google.visualization.arrayToDataTable(attackTimeData);
    var attackTypeDataTable = google.visualization.arrayToDataTable(attackTypeData);

    attackOriginChart = new google.visualization.PieChart(document.getElementById("chart-left"));
    attackTimeChart = new google.visualization.ColumnChart(document.getElementById("chart-middle"));
    attackTypeChart = new google.visualization.PieChart(document.getElementById("chart-right"));

    attackOriginChart.draw(attackOriginDataTable, attackOriginOptions);
    attackTimeChart.draw(attackTimeDataTable, attackTimeOptions);
    attackTypeChart.draw(attackTypeDataTable, attackTypeOptions);
}

//Add resize event listener
if (document.addEventListener)
    window.addEventListener('resize', drawCharts);
else if (document.attachEvent)
    window.attachEvent('onresize', drawCharts);
else
    window.resize = drawCharts();
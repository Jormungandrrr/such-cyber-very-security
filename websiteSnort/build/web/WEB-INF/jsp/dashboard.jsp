<%-- 
    Document   : dashboard
    Created on : May 11, 2017, 1:10:17 PM
    Author     : Melvin
--%>
<%@taglib uri="http://www.springframework.org/tags/form" prefix="form" %>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<!DOCTYPE html>
<html>
    <head>

        <script type="text/javascript"src="http://code.jquery.com/jquery-1.10.1.min.js"></script>
        <script type="text/javascript" src="https://www.gstatic.com/charts/loader.js"></script>
        <script type="text/javascript">
            google.charts.load('current', {'packages': ['corechart']});
            google.charts.setOnLoadCallback(drawChart);

            function drawChart() {

                var data = [['colum1', 'colum2']];
            <c:forEach var="entry" items="${list}">
                 
                data.push([${entry.key}, ${entry.value}]);
            </c:forEach>

                var options = {
                    title: 'attacks'
                };
                var table = google.visualization.arrayToDataTable(data);
                var chart = new google.visualization.PieChart(document.getElementById('piechart'));

                chart.draw(table, options);
            }
        </script>
    </head>
    <body>
        <div id="piechart" style="width: 900px; height: 500px;"></div>
    </body>
</html>

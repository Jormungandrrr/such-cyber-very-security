<%--
    Document   : dashboard
    Created on : May 11, 2017, 1:10:17 PM
    Author     : Melvin
--%>
<%@taglib uri="http://www.springframework.org/tags/form" prefix="form" %>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html>
    <head>
        <script type="text/javascript"src="http://code.jquery.com/jquery-1.10.1.min.js"></script>
        <script type="text/javascript" src="https://www.gstatic.com/charts/loader.js"></script>
        <script src="resources/js/data-controller.js"></script>
        <script src="resources/js/chart-controller.js"></script>
        <link href="resources/css/style.css" rel="stylesheet" type="text/css"/>
    </head>
    <body>

        <script>
            <c:forEach var="json" items="${JObjects}">
                parseData(${json});
            </c:forEach>
        </script>

        <div id="map">
            <script async defer
                    src="https://maps.googleapis.com/maps/api/js?key=AIzaSyCRrFwf5FTZUZr-GAjLb2cl123rUfLfdNY&libraries=visualization&callback=initMap">
            </script>
        </div>
        <div id="chart-container">
            <div id="chart-left" class="chart"></div>
            <div id="chart-middle" class="chart"></div>
            <div id="chart-right" class="chart"></div>
        </div>
    </body>
</html>
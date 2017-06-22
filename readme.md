## Project bestaat uit 2 applicaties :
* websocketServer
* webServer

## websocketServer
leest de tail uit van de alert.csv vanuit de cmd van workstation. Uit deze tail wordt een Ip gehaald. Dit ip wordt ontleden in countrycode longitude en altitude.De server stuurt naar elke cliënt een json message object. dit object bevat :
* Hours
* Longitude
* Latitude
* Country code d.w.z. NL/UK/US
* Type attack

## webServer
Is een maven project waar de mapping van de urls wordt uitgevoerd. Deze is ingericht met Spring MVC.  Deze server stuurt ook de servlets naar de mensen die de url intypen.

## webPage
Op de webPage wordt er gebruik gemaakt van de google.maps api de documentatie hiervoor is op deze locatie te vinden:https://developers.google.com/maps/ 

De key voor het gebruik van deze api is te vinden op : https://developers.google.com/maps/documentation/javascript/get-api-key 

regel 23 in Index.jsp: https://maps.googleapis.com/maps/api/js?key= (vul hier de api key in)&libraries=visualization&callback=initMap)

## Javascript

Er zijn 2 javascript die worden gebruikt voor de site.
* data-controller
* chart-controller

## data-controller

Hierin wordt alle data geparsed en behouden. Ook staat hierin het WebSocket object waarmee de live connectie met
de websocket server behouden wordt.

Meer informatie over websockets: https://tools.ietf.org/html/rfc6455

## chart-controller

Hierin staan alle chart-options, charts en maps die op de pagina worden weergeven.
De data van data-controller wordt hiervoor gebruikt. 

import Geolocator.GeoIPv4;
import Geolocator.GeoLocation;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.Socket;
import java.util.regex.Pattern;

public class Core implements WebSocketClientEventHandler {

    public Core() {
        try {
            final String IPADDRESS_PATTERN =
                    "(?:(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\\.){3}(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)";

            Pattern pattern = Pattern.compile(IPADDRESS_PATTERN);

            WebsocketServer server = new WebsocketServer(8080, this);
            Runtime runtime = Runtime.getRuntime();
            Process terminal = runtime.exec("tail -f /var/log/snort/alert.csv");

            InputStream in = terminal.getInputStream();
            BufferedReader reader = new BufferedReader(new InputStreamReader(in));

            String output;

            while ((output = reader.readLine()) != null) {
                //[0] = timestamp; [1] = message; [2] = source IP
                String[] data = output.split(",");

                //In case of the alert message containing ','.
                String combinedMessage = "";
                for (int i = 1; i < data.length - 1; i++)
                    combinedMessage += data[i];

                String[] correctData = {
                        data[0],
                        combinedMessage,
                        data[2]
                };

                for (int i = 0; i < correctData.length; i++)
                    correctData[i] = correctData[i].trim();

                JSONObject json = new JSONObject();
                json.put("Hour", correctData[0].substring(6, correctData[0].indexOf(':')));
                json.put("Type", correctData[1]);

                if (pattern.matcher(correctData[2]).find()) {
                    //Only lookup a location if the source IP is not a local IP address.
                    if (correctData[2].substring(0, correctData[2].indexOf(".")).equals("192"))
                        json.put("Location", "Internal");
                    else {
                        GeoLocation location = GeoIPv4.getLocation(correctData[2]);

                        json.put("Longitude", location.getLongitude());
                        json.put("Latitude", location.getLatitude());
                        json.put("CountryCode", location.getCountryCode());
                        json.put("CountryName", location.getCountryName());
                        json.put("ContainsIP", "true");
                    }
                } else {
                    json.put("ContainsIP", "false");
                }

                System.out.println(json.toString());
                server.messageAllClients(json.toString());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void onMessage(Socket socket, String s) {
        System.out.println(s);
    }
}

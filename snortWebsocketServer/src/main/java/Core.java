import org.json.JSONObject;
import peter.networking.websockets.WebSocketClientEventHandler;
import peter.networking.websockets.WebsocketServer;

import java.io.*;
import java.net.Socket;

public class Core implements WebSocketClientEventHandler {

    public Core() {
        try {
            WebsocketServer server = new WebsocketServer(8080, this);
            Runtime runtime = Runtime.getRuntime();
            Process terminal = runtime.exec("tail -f /var/log/snort/alert.csv");

            InputStream in = terminal.getInputStream();
            BufferedReader reader = new BufferedReader(new InputStreamReader(in));

            File dataFile = new File("/alertHistory.csv");

            if (!dataFile.exists())
                dataFile.createNewFile();

            BufferedWriter writer = new BufferedWriter(new FileWriter(dataFile, true));

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

                System.out.println(correctData[0]);
                System.out.println(correctData[1]);
                System.out.println(correctData[2]);

                //Only lookup a location if the source IP is not a local IP address.
                if (correctData[2].substring(0, correctData[2].indexOf(".")).equals("192"))
                    json.put("Location", "Internal");
                else {
                    GeoLocation location = GeoIPv4.getLocation(correctData[2]);

                    json.put("Longitude", location.getLongitude());
                    json.put("Latitude", location.getLatitude());
                    json.put("CountryCode", location.getCountryCode());
                    json.put("CountryName", location.getCountryName());
                }

                System.out.println(json.toString());
                writer.write(json.toString() + '\n');
                writer.flush();
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

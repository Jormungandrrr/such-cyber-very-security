import Geolocator.GeoIPv4;
import Geolocator.GeoLocation;
import org.json.JSONObject;

import javax.xml.bind.DatatypeConverter;
import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class WebsocketServer {
    private final ServerSocket server;
    private ArrayList<WebClientHandler> clients;
    private final Thread clientAccepterThread;

    private final WebSocketClientEventHandler eventHandler;

    private final Calendar cal = Calendar.getInstance();
    private final SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");

    private boolean runAcceptor = true;

    public int getNumberOfClients() {
        return clients.size();
    }

    public WebsocketServer(int port, WebSocketClientEventHandler eventHandler) throws IOException, InterruptedException {
        server = new ServerSocket(port);
        clients = new ArrayList<>();

        this.eventHandler = eventHandler;

        clientAccepterThread = new ClientAcceptor(this);
        clientAccepterThread.start();
    }

    public void messageAllClients(String message) throws IOException {
        for (WebClientHandler client : clients)
            client.sendMessage(message);
    }

    private class WebClientHandler extends Thread {
        private final Socket client;
        private final WebsocketServer server;

        private final InputStream in;
        private final OutputStream out;

        private boolean receiveMessages = true;

        public boolean isReceiveMessages() {
            return receiveMessages;
        }

        public void setReceiveMessages(boolean receiveMessages) {
            this.receiveMessages = receiveMessages;
        }

        public WebClientHandler(Socket clientSocket, WebsocketServer server) throws IOException {
            client = clientSocket;
            this.server = server;
            in = clientSocket.getInputStream();
            out = clientSocket.getOutputStream();

            for (String s : getHistory()) {
                sendMessage(s);
            }
        }

        public void sendMessage(String message) {
            try {
                byte[] buffer = WebSocketFrame.encode(message);
                out.write(buffer, 0, buffer.length);
                out.flush();
            } catch (IOException e) {
                e.printStackTrace();
                closeClient();
            }
        }

        @Override
        public void run() {
            byte[] buffer = new byte[1024];

            try {
                while (receiveMessages) {
                    int len;
                    if ((len = in.read(buffer)) != -1) {
                        eventHandler.onMessage(client, WebSocketFrame.decode(buffer, len));
                        buffer = new byte[1024];
                    } else {
                        closeClient();
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
                closeClient();
            }
        }

        private void closeClient() {
            try {
                System.out.println("Closing " + this.client.getRemoteSocketAddress().toString().replace("/", ""));
                receiveMessages = false;
                client.close();
                in.close();
                out.close();
                clients.remove(this);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        private List<String> getHistory() throws IOException {
            Runtime runtime = Runtime.getRuntime();
            Process terminal = runtime.exec("tail -n 50000 /var/log/snort/alert.csv");
            InputStream in = terminal.getInputStream();
            BufferedReader reader = new BufferedReader(new InputStreamReader(in));

            final String IPADDRESS_PATTERN =
                    "(?:(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\\.){3}(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)";

            Pattern pattern = Pattern.compile(IPADDRESS_PATTERN);

            String output;

            ArrayList<String> jsonList = new ArrayList<>();

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

                jsonList.add(json.toString());
            }

            return jsonList;
        }
    }

    private class ClientAcceptor extends Thread {
        private final WebsocketServer websocketServer;
        private boolean acceptClients = true;

        public ClientAcceptor(WebsocketServer server) {
            websocketServer = server;
        }

        @Override
        public void run() {
            while (acceptClients) {
                try {
                    Socket client = server.accept();
                    handshakeClient(client);
                } catch (IOException | NoSuchAlgorithmException e) {
                    e.printStackTrace();
                }
            }
        }

        private void handshakeClient(Socket client) throws IOException, NoSuchAlgorithmException {
            System.out.println(client.getRemoteSocketAddress().toString().replace("/", "") + " connected at " + sdf.format(cal.getTime()) + ".");

            InputStream in = client.getInputStream();
            OutputStream out = client.getOutputStream();

            String data = new Scanner(in, "UTF-8").useDelimiter("\\r\\n\\r\\n").next();

            Matcher get = Pattern.compile("^GET").matcher(data);

            if (get.find()) {
                Matcher match = Pattern.compile("Sec-WebSocket-Key: (.*)").matcher(data);
                match.find();
                byte[] response = ("HTTP/1.1 101 Switching Protocols\r\n"
                        + "Connection: Upgrade\r\n"
                        + "Upgrade: websocket\r\n"
                        + "Sec-WebSocket-Accept: "
                        + DatatypeConverter
                        .printBase64Binary(
                                MessageDigest
                                        .getInstance("SHA-1")
                                        .digest((match.group(1) + "258EAFA5-E914-47DA-95CA-C5AB0DC85B11")
                                                .getBytes("UTF-8")))
                        + "\r\n\r\n")
                        .getBytes("UTF-8");

                out.write(response, 0, response.length);
                out.flush();

                System.out.println(client.getRemoteSocketAddress().toString().replace("/", "") + " accepted.");

                WebClientHandler clientHandler = new WebClientHandler(client, websocketServer);
                clients.add(clientHandler);
                clientHandler.start();
            } else {
                System.out.println(client.getRemoteSocketAddress().toString().replace("/", "") + " failed handshake.");
            }
        }
    }
}


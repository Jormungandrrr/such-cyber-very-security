import java.net.Socket;

public interface WebSocketClientEventHandler {
    void onMessage(Socket client, String data);
}

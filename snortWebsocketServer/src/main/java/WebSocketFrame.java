import java.io.UnsupportedEncodingException;
import java.util.Arrays;

public class WebSocketFrame {
    public static String decode(byte[] buffer, int receivedLength) throws UnsupportedEncodingException {
        byte secondByte = buffer[1];

        int length = secondByte & 127;
        int indexFirstMask = 2;

        if (length == 126)
            indexFirstMask = 4;
        else if (length == 127)
            indexFirstMask = 10;

        byte[] masks = Arrays.copyOfRange(buffer, indexFirstMask, indexFirstMask + 4);

        int indexFirstDataByte = indexFirstMask + 4;

        byte[] message = new byte[buffer.length - indexFirstDataByte];

        for (int i = indexFirstDataByte, j = 0; i < buffer.length; i++, j++)
            message[j] = (byte) (buffer[i] ^ masks[j % 4]);

        message = Arrays.copyOfRange(message, 0, receivedLength - 6);

        return new String(message, "UTF-8");
    }

    public static byte[] encode(String text) throws UnsupportedEncodingException {
        byte[] bytesRaw = text.getBytes("ASCII");
        byte[] frame = new byte[10];

        int indexStartRawData = -1;
        int length = bytesRaw.length;

        frame[0] = (byte) 129;

        if (length <= 125) {
            frame[1] = (byte) length;
            indexStartRawData = 2;
        } else if (length >= 126 && length <= 65535) {
            frame[1] = (byte) 126;
            frame[2] = (byte) ((length >> 8) & 255);
            frame[3] = (byte) (length & 255);
            indexStartRawData = 4;
        } else {
            frame[1] = (byte) 127;
            frame[2] = (byte) ((length >> 56) & 255);
            frame[3] = (byte) ((length >> 48) & 255);
            frame[4] = (byte) ((length >> 40) & 255);
            frame[5] = (byte) ((length >> 32) & 255);
            frame[6] = (byte) ((length >> 24) & 255);
            frame[7] = (byte) ((length >> 16) & 255);
            frame[8] = (byte) ((length >> 8) & 255);
            frame[9] = (byte) (length & 255);

            indexStartRawData = 10;
        }

        byte[] response = new byte[indexStartRawData + length];

        int i, responseIndex = 0;

        for (i = 0; i < indexStartRawData; i++) {
            response[responseIndex] = frame[i];
            responseIndex++;
        }

        for (i = 0; i < length; i++) {
            response[responseIndex] = bytesRaw[i];
            responseIndex++;
        }

        return response;
    }
}


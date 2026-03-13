package kireiko.dev.anticheat.utils;

import kireiko.dev.anticheat.MX;
import lombok.SneakyThrows;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.stream.Collectors;


public class NetworkUtil {
    private static final String apiUrl = "https://paste.mineland.net/documents";
    @SneakyThrows
    public static String createPaste(final String textToPaste) {
        final String userAgent = "MX/1.0 (Anti-Cheat service)";
        String pasteLink = null;

        final URL url = new URL(apiUrl);
        final HttpURLConnection connection = (HttpURLConnection) url.openConnection();

        connection.setRequestMethod("POST");
        connection.setRequestProperty("User-Agent", userAgent);
        connection.setDoOutput(true);
        try (DataOutputStream dos = new DataOutputStream(connection.getOutputStream())) {
            dos.writeBytes(textToPaste);
            dos.flush();
        }
        int responseCode = connection.getResponseCode();
        if (responseCode == HttpURLConnection.HTTP_OK) {
            try (BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()))) {
                String response = in.lines().collect(Collectors.joining());
                pasteLink = "https://paste.mineland.net/" + response.split("\"")[3];
            }
        } else {
            MX.getInstance().getLogger().severe("Error while creating document: " + responseCode);
        }
        connection.disconnect();

        return pasteLink;
    }
}
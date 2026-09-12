package mohaa_launcher;

import java.io.IOException;
import java.net.*;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import javax.swing.*;

class Parser {
    static String[][] serversArray;
    static String[][] recentServersArray;
    static List<String> recentServersList;
    static HashMap<String, String> serverInfo = new HashMap<>();

    private static final String USER_AGENT = "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/110.0.0.0 Safari/537.36";

    static final int COLUMNS = 7;

    public record MohaaResponse(
            List<MohaaServer> servers,
            Stats stats
    ) {}

    public record Stats(
            int total,
            int players
    ) {}

    public record MohaaServer(
            int id,
            String label,
            String gametype,
            String ip,
            String gamename,
            long dt_updated,
            long dt_added,
            int maxplayers,
            String mapname,
            int queryport,
            String country,
            int numplayers,
            String hostname,
            int hostport,
            String maptitle
    ) {}

    static void initParser() {
        recentServersList = FilesManager.createRecentServersListFromFile();

        serversArray = new String[1][COLUMNS];
        for(String[] row : serversArray) {
            Arrays.fill(row, "");
        }

        recentServersArray = (recentServersList.size() > 0)
                ? new String[recentServersList.size()][COLUMNS]
                : new String[1][COLUMNS];

        for(String[] row : recentServersArray) {
            Arrays.fill(row, "");
        }
    }

    static MohaaResponse fetchServers(String game) throws IOException {
        var uri = URI.create("https://master.333networks.com/json/" + game);
        var client = HttpClient.newHttpClient();
        var request = HttpRequest
                .newBuilder()
                .uri(uri)
                .header("accept", "application/json")
                .GET()
                .build();

        HttpResponse<String> response = null;
        try {
            response = client.send(request, HttpResponse.BodyHandlers.ofString());

        } catch (IOException | InterruptedException e) {
            JOptionPane.showMessageDialog(
                    new JFrame(),
                    "Server responded with " + e.getMessage(),
                    "Connection error",
                    JOptionPane.ERROR_MESSAGE);

            throw new RuntimeException(e);
        }

        ObjectMapper objectMapper = new ObjectMapper();

        JsonNode root = null;
        try {
            root = objectMapper.readTree(response.body());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        if (!root.isArray() || root.size() != 2) {
            throw new IOException("Unexpected API response format");
        }

        List<MohaaServer> servers = objectMapper.convertValue(
                root.get(0),
                new TypeReference<List<MohaaServer>>() {}
        );

        Stats stats = objectMapper.treeToValue(
                root.get(1),
                Stats.class
        );

        return new MohaaResponse(servers, stats);
    }

    static void buildServersArrays(List<MohaaResponse> responses) throws IOException, InterruptedException {

        Integer totalServersCount = 0;

        for(MohaaResponse response : responses) {
            totalServersCount += response.servers().size();
        }

        serversArray = new String[totalServersCount][COLUMNS];

        Integer currentRow = 0;

        for(MohaaResponse response : responses) {
            for (MohaaServer server : response.servers()) {

                String game = "mohaa".equals(server.gamename()) ? "AA" : "SH";

                serversArray[currentRow][0] = game;
                serversArray[currentRow][1] = server.hostname();
                serversArray[currentRow][2] = ping(server.ip(), server.queryport());
                serversArray[currentRow][3] = server.numplayers() + "/" + server.maxplayers();
                serversArray[currentRow][4] = server.country();
                serversArray[currentRow][5] = server.ip() + ":" + server.hostport();
                serversArray[currentRow][6] = server.mapname();

                if (recentServersList.contains(server.ip())) {
                    recentServersArray[recentServersList.indexOf(server.ip())][0] = game;
                    recentServersArray[recentServersList.indexOf(server.ip())][1] = server.hostname();
                    recentServersArray[recentServersList.indexOf(server.ip())][2] = ping(server.ip(), server.queryport());
                    recentServersArray[recentServersList.indexOf(server.ip())][3] = String.valueOf(server.numplayers());
                    recentServersArray[recentServersList.indexOf(server.ip())][4] = server.country();
                    recentServersArray[recentServersList.indexOf(server.ip())][5] = server.ip();
                    recentServersArray[recentServersList.indexOf(server.ip())][6] = server.mapname();
                }

                currentRow += 1;
            }
        }
    }

    static HashMap<String, String> getServerDetails(String game, String ip) throws IOException {
        var uri = URI.create("https://master.333networks.com/json/" + game + "/" + ip);
        var client = HttpClient.newHttpClient();
        var request = HttpRequest
                .newBuilder()
                .uri(uri)
                .header("accept", "application/json")
                .GET()
                .build();

        HttpResponse<String> response = null;
        try {
            response = client.send(request, HttpResponse.BodyHandlers.ofString());

        } catch (IOException | InterruptedException e) {
            JOptionPane.showMessageDialog(
                    new JFrame(),
                    "Server responded with " + e.getMessage(),
                    "Connection error",
                    JOptionPane.ERROR_MESSAGE);

            throw new RuntimeException(e);
        }

        ObjectMapper objectMapper = new ObjectMapper();

        JsonNode root = null;
        try {
            root = objectMapper.readTree(response.body());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        int playersCount = root.get("numplayers").asInt();

        String serverInfoString = "<html><b>Players online:</b><br/><ol>";

        for(int i = 0; i < playersCount; i += 1) {
            serverInfoString += "<li>" + root.get("player_" + i).get("name").asText() + "</li>";
        }

        serverInfoString += "</ol></html>";

        HashMap<String, String> serverDetails = new HashMap<String, String>();
        serverDetails.put("players", serverInfoString);
        serverDetails.put("mapImage", root.get("mapurl").asText());
        serverDetails.put("mapName", root.get("mapname").asText());

        return serverDetails;
    }

    static void updateRecentServersList(String givenIP) {
        recentServersList.remove(givenIP);
        recentServersList.add(0, givenIP);

        FilesManager.updateRecentServersFile(recentServersList);
    }

    private static String ping(String host, int port) throws IOException {

        InetAddress address = InetAddress.getByName(host);

        try (DatagramSocket socket = new DatagramSocket()) {
            socket.setSoTimeout(1000);

            byte[] data = createGetInfoPacket();

            DatagramPacket request = new DatagramPacket(
                data,
                data.length,
                address,
                port
            );

            byte[] buffer = new byte[8192];
            DatagramPacket response = new DatagramPacket(buffer, buffer.length);
            long start = System.nanoTime();

            socket.send(request);
            socket.receive(response);

            long elapsed = System.nanoTime() - start;

            return String.valueOf(elapsed / 1_000_000);

        } catch (Exception e) {
            return "?";
        }
    }

    private static byte[] createGetInfoPacket() {
        byte[] command = "getinfo xxx".getBytes(StandardCharsets.US_ASCII);

        byte[] packet = new byte[HEADER.length + command.length];

        System.arraycopy(HEADER, 0, packet, 0, HEADER.length);
        System.arraycopy(command, 0, packet, HEADER.length, command.length);

        return packet;
    }

    private static final byte[] HEADER = {
            (byte) 0xFF,
            (byte) 0xFF,
            (byte) 0xFF,
            (byte) 0xFF
    };
}

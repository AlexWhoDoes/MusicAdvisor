package advisor;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.sun.net.httpserver.HttpServer;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class Authorization {

    private static String serverPath = "https://accounts.spotify.com";
    private static final String CLIENT_ID = "c8deb4a7015c414fb8650935eec5868c";
    private static final String CLIENT_SECRET = "1d34a71ced924245a14e88e59c03af82";
    private static final String REDIRECT_URI = "http://localhost:8080";
    private static final String GRANT_TYPE = "authorization_code";
    private static final String RESPONSE_TYPE = "code";
    private static String authorizationCode;
    private static HttpResponse<String> response;
    private static String accessToken;

    Authorization(String[] args) {
        if (args.length > 1 && "-access".equals(args[0])) {
            serverPath = args[1];
        }
    }

    void getAuthorization() {
        System.out.println("use this link to request the access code:");
        System.out.println(serverPath
                + "/authorize"
                + "?client_id=" + CLIENT_ID
                + "&redirect_uri=" + REDIRECT_URI
                + "&response_type=" + RESPONSE_TYPE);
        try {
            HttpServer server = HttpServer.create();
            server.bind(new InetSocketAddress(8080), 0);
            server.start();
            server.createContext("/",
                    exchange -> {
                        String query = exchange.getRequestURI().getQuery();
                        String request;
                        if (query != null && query.contains("code")) {
                            authorizationCode = query.substring(5); // i did not extract a code itself
                            System.out.println("code received");
                            request = "Got the code. Return back to your program.";
                        } else {
                            request = "Authorization code not found. Try again.";
                        }
                        exchange.sendResponseHeaders(200, request.length());
                        exchange.getResponseBody().write(request.getBytes());
                        exchange.getResponseBody().close();
                    });

            System.out.println("waiting for code...");

            while (authorizationCode == null) { //That block of code is important PAY ATTENTION (i did not even think of it)
                Thread.sleep(100);
            }
            server.stop(5);


        } catch (IOException | InterruptedException  e) {
            System.out.println("Server error");
        }
    }

    void getAccessToken() {
        System.out.println("making http request for access_token...");
        HttpRequest request = HttpRequest.newBuilder()
                .header("Content-Type", "application/x-www-form-urlencoded") //somehow it has not been changed (taken like in example from Jetbreans)
                .uri(URI.create(serverPath + "/api/token"))
                .POST(HttpRequest.BodyPublishers.ofString(
                        "grant_type=" + GRANT_TYPE
                                + "&code=" + authorizationCode
                                + "&client_id=" + CLIENT_ID
                                + "&client_secret=" + CLIENT_SECRET
                                + "&redirect_uri=" + REDIRECT_URI)) // i did not get what to put there (i need to get acqunted with such methods)
                .build();

        try {
            HttpClient client = HttpClient.newBuilder().build();
            response = client.send(request, HttpResponse.BodyHandlers.ofString()); // the same as in example
            System.out.println( "Success!");
            setToken(response.body());

        } catch (InterruptedException | IOException e) { System.out.println("Error response"); }
    }

    private void setToken(String body) {
        JsonElement jsonElement = JsonParser.parseString(body);
        JsonObject jsonObject = jsonElement.getAsJsonObject();
        accessToken = String.valueOf(jsonObject.get("access_token")).replaceAll("\"", "");
    }

    public static String conveyToken() {
        return accessToken;
    }
}


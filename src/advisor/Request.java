package advisor;

import com.google.gson.JsonArray;
import com.google.gson.JsonParser;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.ArrayList;


import static advisor.Authorization.conveyToken;

public class Request {

    private String serverPath;
    private String action;

    Request(String serverPath, String action) {
        this.serverPath = serverPath;
        this.action = action;
    }

    public void makeRequest() {
        HttpRequest request = HttpRequest.newBuilder()
                .header("Authorization", "Bearer " + conveyToken())
                .uri(URI.create(serverPath))
                .GET()
                .build();
        try {
            HttpClient client = HttpClient.newBuilder().build();
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString()); // the same as in example
            if (isError(response)) {
                parserError(response);
            } else {
                distributeParser(response, action);
            }
        } catch (InterruptedException | IOException e) {
            System.out.println("Error response");
        }
    }

    public void makeRequest(String id, String apiPath) {
        HttpRequest request = HttpRequest.newBuilder()
                .header("Authorization", "Bearer " + conveyToken())
                .uri(URI.create(apiPath))
                .GET()
                .build();
        try {
            HttpClient client = HttpClient.newBuilder().build();
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            if (checkerID(response, id, apiPath)) {
                makeRequest();
            } else {
                if (isValidURL(id)) System.out.println("Unknown category name.");
                else System.out.println("Specified id doesn't exist");
            }

        } catch (InterruptedException | IOException e) {
            System.out.println("Error response");
        }
    }

    private boolean isValidURL(String id) {
        String validSymbols = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789-._~:/?#[]@!$&'()*+,;=";
        for (int i = 0; i < id.length(); i++) {
            if (!(validSymbols.contains(String.valueOf(id.charAt(i))))) return false;
        }
        return true;
    }

    private boolean checkerID(HttpResponse<String> response, String id, String apiPath ) {

        JsonArray jsonArray = JsonParser
                .parseString(response.body())
                .getAsJsonObject()
                .get("categories")
                .getAsJsonObject()
                .get("items")
                .getAsJsonArray();

        for (int i = 0; i < jsonArray.size(); i++) {

            String categoryName = (String.valueOf(jsonArray
                    .get(i)
                    .getAsJsonObject()
                    .get("name")))
                    .replaceAll("\"", "")
                    .trim();

            if (categoryName.equals(id)) {
                String out = (String.valueOf(jsonArray
                        .get(i)
                        .getAsJsonObject()
                        .get("id")))
                        .replaceAll("\"", "")
                        .trim();
                serverPath = apiPath + "/" + out + "/playlists";
                return true;
            }

            String categoryID = (String.valueOf(jsonArray
                    .get(i)
                    .getAsJsonObject()
                    .get("id")))
                    .replaceAll("\"", "")
                    .trim();

            if (categoryID.equals(id)) {
                serverPath = apiPath + "/" + categoryID + "/playlists";
                return true;
            }
        }
        return false;
    }

    private void distributeParser(HttpResponse<String> response, String action) {

        switch (action) {
            case "new":
                parserNewRequest(response);
                break;
            case "featured":
                parserFeatured(response);
                break;
            case "categories":
                parserCategories(response);
                break;
            case "playlists":
                parserPlayLists(response);
                break;
            default:
                throw new UnsupportedOperationException();

        }
    }

    private void parserPlayLists(HttpResponse<String> response) {
        parserFeatured(response);
    }

    private void parserCategories(HttpResponse<String> response) {
        Data data = Data.getInstance();
        data.setArrayList();

        JsonArray jsonArray = JsonParser
                .parseString(response.body())
                .getAsJsonObject()
                .get("categories")
                .getAsJsonObject()
                .get("items")
                .getAsJsonArray();

        for (int i = 0; i < jsonArray.size(); i++) {
            String name = String.valueOf(jsonArray
                    .get(i)
                    .getAsJsonObject()
                    .get("name"))
                    .replaceAll("\"", "")
                    .trim();

            data.addResponse(new CategoriesResponse(name));
        }
    }

    private void parserFeatured(HttpResponse<String> response) {

        Data data = Data.getInstance();
        data.setArrayList();

        JsonArray jsonArray = JsonParser
                .parseString(response.body())
                .getAsJsonObject()
                .get("playlists")
                .getAsJsonObject()
                .get("items")
                .getAsJsonArray();

        for (int i = 0; i < jsonArray.size(); i++) {

            String name = String.valueOf(jsonArray
                    .get(i)
                    .getAsJsonObject()
                    .get("name"))
                    .replaceAll("\"", "")
                    .trim();

            String url = String.valueOf(jsonArray
                    .get(i)
                    .getAsJsonObject()
                    .get("external_urls")
                    .getAsJsonObject()
                    .get("spotify"))
                    .replaceAll("\"", "")
                    .trim() + "\n";

            data.addResponse(new FeaturedResponse(name, url));

        }
    }

    private void parserNewRequest(HttpResponse<String> response) {

        Data data = Data.getInstance();
        data.setArrayList();

        JsonArray array = JsonParser
                .parseString(response.body())
                .getAsJsonObject()
                .getAsJsonObject("albums")
                .get("items")
                .getAsJsonArray();

        for (int i = 0; i < array.size(); i++) {

            String name = String.valueOf(array
                    .get(i)
                    .getAsJsonObject().get("name"))
                    .replaceAll("\"", "");


            ArrayList<String> listOfArtists = printArtists(array, i);

            String url = (String.valueOf(array
                    .get(i)
                    .getAsJsonObject()
                    .get("external_urls")
                    .getAsJsonObject()
                    .get("spotify")))
                    .replaceAll("\"", " ")
                    .trim() + "\n";

            data.addResponse(new NewResponse(name, listOfArtists, url));
        }

    }

    private ArrayList<String> printArtists(JsonArray array, int i) {
        ArrayList<String> listOfArtists = new ArrayList<>();
        JsonArray arrayArtists = array
                .get(i)
                .getAsJsonObject()
                .get("artists")
                .getAsJsonArray();

        for (int j = 0; j < arrayArtists.size(); j++) {
            String addArtist = String.valueOf(arrayArtists
                    .get(j)
                    .getAsJsonObject()
                    .get("name"))
                    .trim()
                    .replaceAll("\"", "");
            listOfArtists.add(addArtist);
        }
        return listOfArtists;
    }

    private void parserError(HttpResponse<String> response) {
        System.out.println(String.valueOf(JsonParser
                .parseString(response.body())
                .getAsJsonObject()
                .get("error")
                .getAsJsonObject()
                .get("message"))
                .replaceAll("\"", ""));
    }

    private boolean isError(HttpResponse<String> response) {
        return response.body().contains("error");
    }

}

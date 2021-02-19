package advisor;

import java.util.ArrayList;

public class NewResponse implements Response {
    String name;
    ArrayList<String> listOfArtists;
    String url;

    NewResponse(String name, ArrayList<String> listOfArtists, String url) {
        this.name = name;
        this.listOfArtists = listOfArtists;
        this.url = url;
    }

    @Override
    public void printResponse() {
        System.out.println(name);
        System.out.println(listOfArtists.toString());
        System.out.println(url);
    }
}

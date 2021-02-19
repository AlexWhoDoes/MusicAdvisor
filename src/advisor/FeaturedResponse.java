package advisor;

public class FeaturedResponse implements Response {
    String name;
    String url;

    FeaturedResponse(String name, String url) {
        this.name = name;
        this.url = url;
    }

    @Override
    public void printResponse() {
        System.out.println(name);
        System.out.println(url);

    }
}

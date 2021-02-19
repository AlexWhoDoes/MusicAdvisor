package advisor;

public class CategoriesResponse implements Response {
    String name;

    CategoriesResponse(String name) {
        this.name = name;
    }

    @Override
    public void printResponse() {
        System.out.println(name);
    }
}

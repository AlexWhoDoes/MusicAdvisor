package advisor;

import java.util.NoSuchElementException;
import java.util.Scanner;

public class Controller {
    private static Controller controller = null;
    private boolean isAccessed;
    private Scanner sc;
    private String apiPath;
    private String[] args;
    private final Data data;
    private int sizeRange = 5;

    private Controller(String[] args) {
        setApiPath("https://api.spotify.com");
        setSc(new Scanner(System.in));
        setArgs(args);
        setAccessed(false);
        data = Data.getInstance();

    }

    private void setAccessed(boolean accessed) {
        isAccessed = accessed;
    }

    private void setSc(Scanner sc) {
        this.sc = sc;
    }

    private void setApiPath(String apiPath) {
        this.apiPath = apiPath;
    }

    private void setArgs(String[] args) {
        this.args = args;
    }

    public static Controller getController(String[] args) {
        if (controller == null) {
            controller = new Controller(args);
        }
        return controller;
    }

    public void run() {
        if (args.length > 1 && args[2].equals("-resource")) {
            apiPath = args[3];
        }

        if (args.length > 5 && args[4].equals("-page")) {
            sizeRange = Integer.parseInt(args[5]);
            data.setSizePage(sizeRange);
        }

        while (true) {
            String[] input = sc.nextLine().split("\\s+");
            switch (input[0]) {
                case "auth":
                    auth();
                    break;
                case "new":
                    newRequest();
                    break;
                case "featured":
                    featuredRequest();
                    break;
                case "categories":
                    categoriesRequest();
                    break;
                case "playlists":
                    playlistsRequest(input);
                    break;
                case "prev":
                    data.prev();
                    break;
                case "next":
                    data.next();
                    break;
                case "exit":
                    System.out.println("---GOODBYE!---");
                    System.exit(0);
                default:
                    System.out.println("unknown command");
            }
        }
    }

    private static String playListCategory(String[] in) {

        if (in.length == 1) {
            System.out.println("where is C-NAME?");
            throw new NoSuchElementException();
        } else if (in.length == 2) {
            return in[1];
        } else {
            StringBuilder cName = new StringBuilder();
            for (int i = 1; i < in.length; i++) {
                cName.append(in[i]).append(" ");
            }
            return String.valueOf(cName).trim();
        }
    }

    private void auth() {
        if(!isAccessed) {
            Authorization authorization = new Authorization(args);
            authorization.getAuthorization();
            authorization.getAccessToken();
            isAccessed = true;
        } else System.out.println("You have already logged in");
    }

    private void newRequest() {
        if (isAccessed) {
            Request request = new Request(apiPath + "/v1/browse/new-releases", "new"); //+ "/v1/browse/new-releases"
            request.makeRequest();
            data.printPage(sizeRange);//do not forget about the solution
        } else {
            System.out.println("Please, provide access for application.");
        }
    }

    private void featuredRequest() {
        if (isAccessed) {
            Request request = new Request(apiPath + "/v1/browse/featured-playlists", "featured"); //+ "/v1/browse/featured-playlists"
            request.makeRequest();
            data.printPage(sizeRange);
        } else {
            System.out.println("Please, provide access for application.");
        }

    }

    private void categoriesRequest() {
        if (isAccessed) {
            Request request = new Request(apiPath + "/v1/browse/categories", "categories"); //
            request.makeRequest();
            data.printPage(sizeRange);
        } else {
            System.out.println("Please, provide access for application.");
        }
    }

    private void playlistsRequest(String[] input) {
        if (isAccessed) {
            Request request = new Request(apiPath + "/v1/browse/categories/" + input[1] + "/playlists", "playlists"); // + "/v1/browse/categories/" + input[1] + "/playlists"
            request.makeRequest(playListCategory(input), apiPath + "/v1/browse/categories");
            data.printPage(sizeRange);
        } else {
            System.out.println("Please, provide access for application.");
        }
    }
}


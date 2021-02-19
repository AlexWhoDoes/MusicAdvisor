package advisor;

import java.util.ArrayList;

public class Data {
    private static Data instance = null;
    private static ArrayList<Response> data;
    private static int counter;
    private static int currentPage;
    private static int sizePage = 5;

    private Data() {}

    public static Data getInstance() {
        if (instance == null) {
            instance = new Data();
        }
        return instance;
    }

    public void addResponse (Response response) {
        data.add(response);
    }

    public void printPage (int range) {

        for (int i = counter; i < range; i++) {
            data.get(i).printResponse();
            counter++;
        }
        System.out.printf("---PAGE %d OF %d---\n", currentPage, getPages());
    }

    public void setSizePage(int sizePage) {
        Data.sizePage = sizePage;
    }

    public void setArrayList() {
        data = new ArrayList<>();
        counter = 0;
        currentPage = 1;
    }

    public void prev() {
        if (0 <= counter - (sizePage * 2)) {
            currentPage--;
            int range = counter - sizePage;
            counter -= (sizePage * 2);
            printPage(range);
        } else {
            System.out.println("No more pages.");
        }
    }

    public void next() {
        if (data.size() >= counter + sizePage) {
            currentPage++;
            printPage(counter + sizePage);
        } else {
            System.out.println("No more pages.");
        }
    }

    private int getPages() {
        if (data != null) {
            int entities = data.size() / sizePage;
            int reminder = data.size() - ((data.size() / sizePage) * sizePage);
            return reminder + entities;
        } else throw new NullPointerException();
    }

}

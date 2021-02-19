package advisor;

public class Main {

    public static void main(String[] args) {
        Controller controller = Controller.getController(args);
        controller.run();
    }
}

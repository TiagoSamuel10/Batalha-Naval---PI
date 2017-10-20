import java.util.Timer;

public class TestClient {

    public static void main(String[] args) {
        Client[] clients = new Client[10];
        for(int i = 0; i < 10; i++){
            Client c = new Client();
            clients[i] = c;
        }
        for(int i = 0; i < 10; i++){
            clients[i].dispose();
            Client c = new Client();
            clients[i] = c;
        }
    }
}

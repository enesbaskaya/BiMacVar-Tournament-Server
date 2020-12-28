package tournament.controller;

import tournament.Tournament;
import tournament.client.ClientHandler;

import java.io.IOException;
import java.net.ServerSocket;

public class Controller {


    public void run() {
        try {
            ServerSocket s = new ServerSocket(Tournament.PORT);

            while (true) {
                new ClientHandler(s.accept());
                System.out.println("Bağlantısı sağlandı!");
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

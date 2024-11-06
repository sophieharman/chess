package ui;

import java.util.Scanner;

public class Repl {

    private final Client client;

    public Repl(String serverUrl)
    {
        client = new Client(serverUrl);
    }

    public void run() {

        Scanner scanner = new Scanner(System.in);
        var result = "";
        while (!result.equals("quit")) {
            System.out.println("Implement");
        }


    }
}

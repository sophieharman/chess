package ui;

import exception.ResponseException;

import java.util.Scanner;

public class Repl {

    private final Client client;

    public Repl(String serverUrl)
    {
        client = new Client(serverUrl);
    }

    public void run() throws ResponseException {

        System.out.println("Welcome to chess! Log in to start.");
        System.out.print(client.help());

        Scanner scanner = new Scanner(System.in);
        var result = "";
        while (!result.equals("quit")) {
            String line = scanner.nextLine();

            result = client.eval(line);
        }
    }
}

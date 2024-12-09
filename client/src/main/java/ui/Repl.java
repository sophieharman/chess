package ui;

import exception.ResponseException;
import model.GameData;
import ui.BoardDisplay;

import java.util.Scanner;

public class Repl {

    private final Client client;
    private State state = State.SIGNEDOUT;

    public Repl(String serverUrl, Client client)
    {
        this.client = client;
    }

    public void run() throws ResponseException {

        System.out.println("Welcome to chess!");

        Scanner scanner = new Scanner(System.in);
        var result = "";
        while (!result.equals("quit")) {

            System.out.print(client.help());

            String line = scanner.nextLine();
            String[] words = line.split(" ");
            String method = words[0];

            result = client.eval(line);
            System.out.println(result);
        }
    }
}

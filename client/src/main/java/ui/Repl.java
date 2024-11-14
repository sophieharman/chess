package ui;

import exception.ResponseException;
import model.GameData;
import ui.BoardDisplay;

import java.util.Scanner;

public class Repl {

    private final Client client;
    private State state = State.SIGNEDOUT;

    public Repl(String serverUrl)
    {
        client = new Client(serverUrl);
    }

    public void run() throws ResponseException {

        System.out.println("Welcome to chess!");
        System.out.print(client.help());

        Scanner scanner = new Scanner(System.in);
        var result = "";
        while (!result.equals("quit")) {

            String line = scanner.nextLine();
            String[] words = line.split(" ");
            String method = words[0];



            if (method.equals("joinGame") || method.equals("observeGame")) {
                // Draw Board
//                GameData game = result.game();
//                BoardDisplay.main();
            }

            result = client.eval(line);
            System.out.println(result);

            System.out.print(client.help());
        }
    }
}

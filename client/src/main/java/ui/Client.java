package ui;

import server.ServerFacade;

import java.util.Arrays;

public class Client {

    private final ServerFacade server;
    private final String serverUrl;
    private State state = State.SIGNEDOUT;

    public Client(String serverUrl) {
        server = new ServerFacade(serverUrl);
        this.serverUrl = serverUrl;
    }

    public String eval(String input) {
        try {
            var tokens = input.toLowerCase().split(" ");
            var cmd = (tokens.length > 0) ? tokens[0] : "help";
            var params = Arrays.copyOfRange(tokens, 1, tokens.length);
            return switch (cmd) {
                case "register" -> register(params);
                case "login" -> login(params);
                case "quit" -> "quit";
                default -> help();
            };
        } catch (RuntimeException ex) {
            return "";
        }

    }

    public String register(String... params) {
        System.out.println("Implement!");
        return "";
    }

    public String login(String... params){
        if (params.length >= 1) {
            state = State.SIGNEDIN;
        }
        return "";
    }

    public String help() {
        System.out.println("Implement!");
        return "";
    }

}

package ui;

import exception.ResponseException;
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

    public static void main(String[] args) {
        var serverUrl = "http://localhost:8080";
        if (args.length == 1) {
            serverUrl = args[0];
        }

        new Repl(serverUrl).run();
    }

    public String eval(String input) {
        try {
            var tokens = input.toLowerCase().split(" ");
            var cmd = (tokens.length > 0) ? tokens[0] : "help";
            var params = Arrays.copyOfRange(tokens, 1, tokens.length);
            return switch (cmd) {
                case "register" -> register(params);
                case "login" -> login(params);
                case "logout" -> logout(params);
                case "createGame" -> createGame(params);
                case "listGames" -> listGames(params);
                case "playGame" -> playGame(params);
                case "observeGame" -> observeGame(params);
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

    public String logout(String... params) {
//        if (state == State.SIGNEDOUT) {
//            throw new ResponseException(400, "You must sign in");
//        }
        return "";
    }

    public String createGame(String... params){
        return "";
    }

    public String listGames(String... params){
        return "";
    }

    public String playGame(String... params){
        return "";
    }

    public String observeGame(String... params){
        return "";
    }

    public String help() {
        System.out.println("Implement!");
        return "";
    }

}

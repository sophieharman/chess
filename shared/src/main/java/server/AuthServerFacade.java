package server;

public class AuthServerFacade {


    private final String authServerUrl;

    public AuthServerFacade(String url) {
        authServerUrl = url;
    }

    public String createAuth(String username){
        System.out.println("Implement");
        return "";
    }

    public void deleteAuth(String authToken) {
        System.out.println("Implement");
    }

    public String getUser(String authToken) {
        System.out.println("Implement");
        return "";
    }

    public void clear() {
        System.out.println("Implement");
    }


}

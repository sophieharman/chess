package websockets;


import websocket.messages.Notification;

public interface NotificationHandler {
    void notify(String notification);

    void load(String notification);
}
package websockets;

import websocket.messages.ErrorMessage;

public interface ErrorHandler {
    void error(ErrorMessage errorMessage);
}
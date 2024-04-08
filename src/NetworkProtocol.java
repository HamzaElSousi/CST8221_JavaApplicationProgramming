public class NetworkProtocol {
    // Commands from client to server
    public static final String PLAYER_CONNECT = "PLAYER_CONNECT";
    public static final String PLAYER_DISCONNECT = "PLAYER_DISCONNECT";
    public static final String CHAT_MESSAGE = "CHAT";
    public static final String GAME_MOVE = "MOVE";;

    // Responses from server to client
    public static final String GAME_STATE_UPDATE = "GAME_STATE_UPDATE";
    public static final String CHAT_BROADCAST = "CHAT_BROADCAST";
    public static final String GAME_WIN = "GAME_WIN";
    public static final String GAME_LOSE = "GAME_LOSE";
    public static final String GAME_DRAW = "GAME_DRAW";
    public static final String PLAYER_CONNECTED = "PLAYER_CONNECTED";
    public static final String PLAYER_DISCONNECTED = "PLAYER_DISCONNECTED";

    // Delimiter to separate command parameters
    public static final String MESSAGE_SEPARATOR = "#";
	public static final String PLAYER_SWITCH = null;
	public static final String GAME_STATE = null;

    // Utility method to construct messages
    public static String constructMessage(String... parts) {
        return String.join(MESSAGE_SEPARATOR, parts);
    }

    // Utility method to parse messages
    public static String[] parseMessage(String message) {
        return message.split(MESSAGE_SEPARATOR);
    }
}

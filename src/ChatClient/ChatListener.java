package ChatClient;

public interface ChatListener {
    void messageArrived(String sender, String data);
    void userListArrived(String[] userList);
    void stopArrived();
}

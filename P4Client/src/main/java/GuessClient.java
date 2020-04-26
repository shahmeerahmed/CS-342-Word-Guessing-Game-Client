import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.net.Socket;
import java.util.function.Consumer;

public class GuessClient extends Thread{
    Socket socketClient;
    GuessInfo clientInfo;
    int portNum;
    String ip;
    ObjectOutputStream out;
    ObjectInputStream in;

    private Consumer<Serializable> callback;

    GuessClient(Consumer<Serializable> call, int p, String ipAddress){
        ip = ipAddress;
        portNum = p;
        callback = call;
    }

    public void run() {

        try {
            socketClient= new Socket(ip,portNum);
            out = new ObjectOutputStream(socketClient.getOutputStream());
            in = new ObjectInputStream(socketClient.getInputStream());
            socketClient.setTcpNoDelay(true);
        }
        catch(Exception e){}

        while(true) {
            //receives the GuessInfo class from the server
            try {
                clientInfo = (GuessInfo) in.readObject();
                //callback.accept(clientInfo.getPlayerString());
            }
            catch(Exception e) {}
        }

    }

    //sends the GuessInfo class to the Server
    public void send(String data) {
        try {
            out.reset();
            out.writeObject(clientInfo);
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
}

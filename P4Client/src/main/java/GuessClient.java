import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.net.Socket;
import java.util.ArrayList;
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

        clientInfo = new GuessInfo();
        GuessInfo temp;

        while(true) {
            //receives the GuessInfo class from the server
            try {

                temp = (GuessInfo) in.readObject();

                if(temp.getNumWrongGuesses() != clientInfo.getNumWrongGuesses()){
                    callback.accept("You guessed wrong!");
                }

                clientInfo.setWord(temp.getWord());

                for (int i = 0; i < clientInfo.getCategories().size(); i++)
                    clientInfo.removeCategory();

                for(String category : temp.getCategories())
                    clientInfo.setCategories(category);

                for(char c : temp.getGuesses())
                    clientInfo.setGuesses(c);

                clientInfo.setNumWordsGuessed(temp.getNumWordsGuessed());
                clientInfo.setNumWrongGuesses(temp.getNumWrongGuesses());


            }
            catch(Exception e) {}
        }

    }

    //sends the GuessInfo class to the Server
    public void send(String data) {
        if(data.equals("Video Games") || data.equals("Sports") || data.equals("Foods")) {
            clientInfo.setCategories(data);
        }
        else if(data.length() == 1 && Character.isAlphabetic(data.charAt(0))){
            clientInfo.setGuesses(data.charAt(0));
        }
        else{
            if(data.equals("yes")){
                clientInfo = null;
                clientInfo = new GuessInfo();
            }
            else{
                clientInfo = null;
            }
        }
        try {
            out.reset();
            out.writeObject(clientInfo);
        } catch (IOException e) {

            e.printStackTrace();
        }

    }
}

import java.io.*;
import java.net.Socket;
import java.util.ArrayList;

public class ClientHandler implements Runnable {

    public static ArrayList<ClientHandler> clientHandlers = new ArrayList<>();
    private Socket socket;
    private BufferedReader bufferedReader;
    private BufferedWriter bufferedWriter;
    private String username;

    public ClientHandler(Socket socket){
        try{
            this.socket = socket;
            this.bufferedWriter = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
            this.bufferedReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            this.username = bufferedReader.readLine();
            clientHandlers.add(this);
            broadcastMessege(username + " dołączył do czatu.\n");
        } catch (IOException e){
            close(socket, bufferedReader, bufferedWriter);
        }
    }

    @Override
    public void run() {
        String messegeFromClient;

        while (!socket.isClosed()){
            try {
                messegeFromClient = bufferedReader.readLine();
                broadcastMessege(messegeFromClient);
            } catch (IOException e){
                close(socket, bufferedReader, bufferedWriter);
                break;
            }
        }
    }

    public void broadcastMessege(String messege){
        for(ClientHandler clientHandler : clientHandlers){
            try {
                if(!clientHandler.username.equals(username)){
                    clientHandler.bufferedWriter.write(messege);
                    clientHandler.bufferedWriter.newLine();
                    clientHandler.bufferedWriter.flush();
                }
            } catch (IOException e){
                close(socket, bufferedReader, bufferedWriter);
            }
        }
    }

    public void removeClientHandler(){
        clientHandlers.remove(this);
        broadcastMessege(username + " opuścił czat\n");
    }

    public void close(Socket socket, BufferedReader bufferedReader, BufferedWriter bufferedWriter){
        removeClientHandler();
        try{
            if(bufferedReader != null){
                bufferedReader.close();
            }
            if(bufferedWriter != null){
                bufferedWriter.close();
            }
            if(socket != null){
                socket.close();
            }
        } catch (IOException e){
            e.printStackTrace();
        }
    }

}

import Message.Text;

import java.io.*;
import java.math.BigInteger;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Arrays;

public class ClientHandler implements Runnable {

    public static ArrayList<ClientHandler> clientHandlers = new ArrayList<>();
    private Socket socket;
    DataOutputStream dataOutputStream;
    DataInputStream dataInputStream;
    private String username;

    public ClientHandler(Socket socket){
        try{
            this.socket = socket;
            this.dataOutputStream = new DataOutputStream(socket.getOutputStream());
            this.dataInputStream = new DataInputStream(socket.getInputStream());
            byte[] messegeFromClient;
            byte[] prefix;
            int length;
            prefix = dataInputStream.readNBytes(5);
            clientHandlers.add(this);
            length = new BigInteger(Arrays.copyOfRange(prefix, 1, 5)).intValue();
            messegeFromClient = new byte[length];
            this.dataInputStream.readFully(messegeFromClient, 0, length);
            Text temp = (Text) Text.decode(messegeFromClient);
            this.username = temp.print();
            temp = new Text(username + " dołączył do czatu.");
            broadcastMessege(temp.encode());
        } catch (IOException e){
            close(socket, dataInputStream, dataOutputStream);
        }
    }

    @Override
    public void run() {
        byte[] messegeFromClient;
        byte[] prefix;
        int length;

        while(socket.isConnected()){
            try{
                prefix = this.dataInputStream.readNBytes(5);
                length = new BigInteger(Arrays.copyOfRange(prefix, 1, 5)).intValue();
                messegeFromClient = new byte[length];
                this.dataInputStream.readFully(messegeFromClient, 0, length);

                if(messegeFromClient != null){
                    broadcastMessege(copyArray(prefix, messegeFromClient));
                }
                else {
                    removeClientHandler();
                    break;
                }
            } catch (IOException e){
                System.out.println(e.getMessage());
                close(socket, dataInputStream, dataOutputStream);
                break;
            }
        }
    }

    private byte[] copyArray (byte[] byteArray1, byte[] byteArray2){
        byte[] ret = new byte[byteArray1.length + byteArray2.length];
        System.arraycopy(byteArray1, 0, ret, 0, byteArray1.length);
        System.arraycopy(byteArray2, 0, ret, byteArray1.length, byteArray2.length);
        return ret;
    }

    public void broadcastMessege(byte[] messege){
        for(ClientHandler clientHandler : clientHandlers){
            try {
                if(!clientHandler.username.equals(username)){
                    clientHandler.dataOutputStream.write(new Text("----"+this.username + "----").encode());
                    clientHandler.dataOutputStream.flush();
                    clientHandler.dataOutputStream.write(messege);
                    clientHandler.dataOutputStream.flush();
                }
            } catch (IOException e){
                close(socket, dataInputStream, dataOutputStream);
            }
        }
    }

    public void removeClientHandler(){
        clientHandlers.remove(this);
    }

    public void close(Socket socket, DataInputStream bufferedReader, DataOutputStream bufferedWriter){
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

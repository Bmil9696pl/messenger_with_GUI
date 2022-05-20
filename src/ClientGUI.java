import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import java.net.Socket;

public class ClientGUI {
    private Socket socket;
    private BufferedReader bufferedReader;
    private BufferedWriter bufferedWriter;
    private String username;

    private JPanel panel1;
    private JTextArea textArea1;
    private JButton button1;
    private JTextField textField1;
    private boolean button_clicked;

    public ClientGUI(Socket socket){
        try {
            this.socket = socket;
            this.bufferedWriter = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
            this.bufferedReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            this.button_clicked = false;
        } catch (IOException e){
            close(socket, bufferedReader, bufferedWriter);
        }
        button1.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                if(username == null || username.equals("")){
                    username = textField1.getText();
                    bufferedWriter.write(username);
                    bufferedWriter.newLine();
                    bufferedWriter.flush();
                }
                else if (socket.isConnected()){
                        String messege = textField1.getText();
                        textArea1.append(messege + "\n");
                        bufferedWriter.write(username + ": " + messege);
                        bufferedWriter.newLine();
                        bufferedWriter.flush();

                }
                } catch (IOException er){
                    close(socket, bufferedReader, bufferedWriter);
                }
            }
        });


    }
    public void username(){

            textArea1.append("Wpisz swoją nazwę użytkownika\n");

    }

    public void listenForMessage(){
        new Thread(new Runnable() {
            @Override
            public void run() {
                String messegeFromChat;

                while (socket.isConnected()){
                    try {
                        messegeFromChat = bufferedReader.readLine();
                        textArea1.append(messegeFromChat + "\n");
                    } catch (IOException e){
                        close(socket, bufferedReader, bufferedWriter);
                    }
                }
            }
        }).start();
    }

    public void close(Socket socket, BufferedReader bufferedReader, BufferedWriter bufferedWriter){
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

    public static void main(String[] args) throws IOException {
        Socket socket = new Socket("localhost", 1);
        JFrame mainFrame = new JFrame("messenger");
        ClientGUI client = new ClientGUI(socket);
        mainFrame.setContentPane(client.panel1);
        mainFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        mainFrame.pack();
        mainFrame.setVisible(true);
        client.listenForMessage();
        client.username();
    }
}


import ChainOfResponsibility.ChainHandler;
import Message.IStrategy;
import Message.Img;
import Message.Sound;
import Message.Text;
import javax.sound.sampled.*;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import java.math.BigInteger;
import java.net.Socket;
import java.util.Arrays;

public class ClientGUI {
    private Socket socket;
    DataOutputStream dataOutputStream;
    DataInputStream dataInputStream;
    private String username;

    private JPanel panel1;
    private JButton button1;
    private JTextField textField1;
    private JScrollPane scrollPane;
    private JPanel test;
    private JButton fileButton;
    private int counter = 0;

    public ClientGUI(Socket socket){
        try {
            this.socket = socket;
            this.dataOutputStream = new DataOutputStream(socket.getOutputStream());
            this.dataInputStream = new DataInputStream(socket.getInputStream());
            this.scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
            this.scrollPane.setWheelScrollingEnabled(true);
        } catch (IOException e) {
            close(socket, dataInputStream, dataOutputStream);
        }
        button1.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                if(username == null || username.equals("")){
                    username = textField1.getText();
                    IStrategy s = new Text(username);
                    dataOutputStream.write(s.encode());
                    dataOutputStream.flush();
                }
                else if (socket.isConnected()){
                    String userInput = textField1.getText();
                    IStrategy strategy = new Text(userInput);
                    chooseActrion(new Text("----" + username + "----"), GridBagConstraints.EAST);
                    chooseActrion(strategy, GridBagConstraints.EAST);
                    dataOutputStream.write(strategy.encode());
                    dataOutputStream.flush();
                }
                } catch (IOException er){
                    close(socket, dataInputStream, dataOutputStream);
                }
            }
        });


        fileButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JFileChooser chooser = new JFileChooser();
                if(chooser.showOpenDialog(panel1) == JFileChooser.APPROVE_OPTION){
                    File file = chooser.getSelectedFile();
                    if(file.getName().contains(".png")){
                        IStrategy img = null;
                        try {
                            img = new Img(file);
                        } catch (IOException ex) {
                            throw new RuntimeException(ex);
                        }
                        try {
                            chooseActrion(new Text("----" + username + "----"), GridBagConstraints.EAST);
                            chooseActrion(img, GridBagConstraints.EAST);
                            dataOutputStream.write(img.encode());
                            dataOutputStream.flush();
                        } catch (IOException ex) {
                            throw new RuntimeException(ex);
                        }
                    } else if (file.getName().contains(".wav")) {
                        IStrategy aud = null;
                        try {
                            aud = new Sound(file);
                        } catch (UnsupportedAudioFileException | IOException ex) {
                            throw new RuntimeException(ex);
                        }
                        chooseActrion(new Text("----" + username + "----"), GridBagConstraints.EAST);
                        chooseActrion(aud, GridBagConstraints.EAST);
                        try {
                            dataOutputStream.write(aud.encode());
                            dataOutputStream.flush();
                        } catch (IOException ex) {
                            throw new RuntimeException(ex);
                        }
                    }
                }
            }
        });
    }
    public void username(){
         chooseActrion(new Text("Wpisz Swoją nazwę użytkownika"), GridBagConstraints.CENTER);
    }

    public void listenForMessage(){
        new Thread(new Runnable() {
            private byte[] copyArray (byte[] byteArray1, byte[] byteArray2){
                byte[] ret = new byte[byteArray1.length + byteArray2.length];
                System.arraycopy(byteArray1, 0, ret, 0, byteArray1.length);
                System.arraycopy(byteArray2, 0, ret, byteArray1.length, byteArray2.length);
                return ret;
            }
            @Override
            public void run() {
                byte[] messegeFromClient;
                byte[] prefix;
                int length;


                while(socket.isConnected()){
                    try{
                        prefix = dataInputStream.readNBytes(5);
                        length = new BigInteger(Arrays.copyOfRange(prefix, 1, 5)).intValue();
                        messegeFromClient = new byte[length];
                        dataInputStream.readFully(messegeFromClient, 0, length);
                        IStrategy temp = ChainHandler.exec(copyArray(prefix, messegeFromClient));
                        chooseActrion(temp, GridBagConstraints.WEST);

                    } catch (IOException e){
                        System.out.println(e.getMessage());
                        close(socket, dataInputStream, dataOutputStream);
                        break;
                    }
                }
            }
        }).start();
    }

    public void close(Socket socket, DataInputStream bufferedReader, DataOutputStream bufferedWriter){
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

    public void chooseActrion(IStrategy input, int anchor){
        switch (input.getType()){
            case "Text"->{
                Text temp = (Text) input;
                JPanel container = new JPanel();
                container.add(new JTextField(temp.print()));

                printOntoPanel(container, anchor);
            }
            case "Image" ->{
                Img temp = (Img) input;
                JPanel container = new JPanel();
                container.add(new JLabel(new ImageIcon(temp.print())));

                printOntoPanel(container, anchor);
            }
            case "Sound" ->{
                Sound aud = (Sound)input;
                JPanel container = new JPanel();
                JButton button1 = new JButton("play");
                IStrategy finalAud = aud;
                Clip clip = null;
                Sound temp = (Sound) finalAud;
                AudioInputStream ais = temp.print();
                try {
                    clip = AudioSystem.getClip();
                } catch (LineUnavailableException ex) {
                    throw new RuntimeException(ex);
                }
                try {
                    clip.open(ais);
                } catch (LineUnavailableException | IOException ex) {
                    throw new RuntimeException(ex);
                }
                Clip finalClip = clip;
                finalClip.setFramePosition(0);
                button1.addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        new Thread(new Runnable() {
                            @Override
                            public void run() {
                                finalClip.start();
                                while(true) {
                                    if (finalClip.getMicrosecondPosition() == finalClip.getMicrosecondLength())
                                        break;
                                }
                            }
                        }).start();
                    }
                });
                JButton button2 = new JButton("pause");
                button2.addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        finalClip.stop();
                    }
                });
                JButton button3 = new JButton("stop");
                button3.addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        finalClip.stop();
                        finalClip.setFramePosition(0);
                    }
                });
                container.add(button1);
                container.add(button2);
                container.add(button3);
                test.add(container);
                printOntoPanel(container, anchor);
            }
            default -> {
                System.out.println("err");
            }
        }
    }

    private void printOntoPanel(JPanel container, int anchor) {
        GridBagConstraints constraints = new GridBagConstraints();
        constraints.gridx = 0;
        constraints.gridy = counter;
        constraints.anchor = anchor;

        this.test.add(container, constraints);
        this.test.repaint();
        this.test.revalidate();
        counter++;
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


import java.io.*;
import java.net.*;
import java.util.*;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class Client {

  BufferedReader reader;
  PrintWriter writer;
  Socket sock;
  Scanner scan;
  String message, username;

  public static void main(String[] args) {
    new Client().go();
  }

  public void go() {
    setUpNetworking();
    setUsername();
    Thread readerThread = new Thread(new IncomingReader());
    readerThread.start();
  }

  private void setUpNetworking() {
    try{
      sock = new Socket("127.0.0.1", 4500);
      reader = new BufferedReader( new InputStreamReader( sock.getInputStream() ) );
      writer = new PrintWriter( sock.getOutputStream() );
      scan = new Scanner(System.in);
      System.out.println("Establishing Connection at port: 4500 ....");
    } catch(Exception e) { e.printStackTrace(); }
  }

  public void send() {
    try{
      writer.println(scan.nextLine());
      writer.flush();
    } catch(Exception e) { e.printStackTrace(); }
  }
  public void send(String message) {
    try{
      writer.println(message);
      writer.flush();
    } catch(Exception e) { e.printStackTrace(); }
  }

  public void receive(){
      try{
        while( (message = reader.readLine()) != null ) {
          System.out.println(message);
        }
      } catch(Exception e) { e.printStackTrace(); }
  }

  public void setUsername(){
    try{
      System.out.print("Enter your username: ");
      send();
      while( true ){
        message = reader.readLine();
        username = message;
        if( message.equals("false")  ) {
          System.out.print("Username exists !!!");
          System.out.print("--- Enter new username: ");
          send();
        }
        else
          break;
      }
      System.out.println("Connection Established ---Welcome---");
    } catch(Exception e) { e.printStackTrace(); }
  }

  public class IncomingReader implements Runnable {
    public void run() {
      CreateFrame window = new CreateFrame();
      window.open();
      receive();
    }
  }

  public class CreateFrame extends JFrame{
    public void open(){
      EventQueue.invokeLater(new Runnable() {
        @Override
        public void run() {
          JFrame frame = new JFrame(username);
          frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
          JPanel panel = new JPanel();
          panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
          //panel.setLayout(new FlowLayout( ));
          panel.setOpaque(true);
          JTextArea textArea = new JTextArea(4, 20);
          textArea.setWrapStyleWord(true);
          textArea.setEditable(true);
          Font font = new Font("SansSerif", Font.BOLD, 20);
          textArea.setFont( font );
          JScrollPane scroller = new JScrollPane(textArea);
          scroller.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
          scroller.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
          JButton button = new JButton("SEND");
          button.setPreferredSize(new Dimension(100, 50));
          panel.add(scroller);
          panel.add(button);
          frame.getContentPane().add(BorderLayout.CENTER, panel);
          frame.pack();
          frame.setLocationByPlatform(true);
          frame.setVisible(true);
          frame.setResizable(false);
          textArea.requestFocus();

          button.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent arg0) {
              message = textArea.getText();
              textArea.setText("");
              send(message);
            }
          });
        }
      });
      
    }
  }
}
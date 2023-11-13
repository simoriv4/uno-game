import javax.imageio.ImageIO;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.swing.*;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;

public class homepage extends JFrame {
    private final String rootName = "client2";
    // lista comandi
    private final String start = "start";

    // messaggi di risposta
    private final String CORRECT = "200";
    private final String ERROR_USERNAME = "400";
    private final String ERROR_CARD_PALYED = "406";
    private final String WINNER = "201";
    private final String ERROR_SKIP = "409";
    private final String ERROR_EXIT = "500";





    private BufferedImage backgroundImage;
    private JTextField username;
    private JButton playButton;
    private JLabel messageLabel;
    private JLabel imageLabel;
    private Message message;
    private Boolean isListening;
    private User user;

    private Server server;

    // Streams
    private BufferedReader inStream;
    private PrintWriter outStream;

    public homepage() throws IOException, ParserConfigurationException, SAXException {
        // this.isListening = false;

        // ininzializzo le informazioni del server
        this.server = new Server();
    
        this.playMusic();
        setTitle("Uno Client");

        // calcolo le coordinate in base alle percentuali dello schermo
        int screenWidth = Toolkit.getDefaultToolkit().getScreenSize().width;
        int screenHeight = Toolkit.getDefaultToolkit().getScreenSize().height;

        setSize((int) (screenWidth * 1), (int) (screenHeight * 1));
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        this.backgroundImage = ImageIO.read(new File(rootName + "/img/wallpaper4.jpg"));
        // inizializzo una label che contiene l'immagine del titolo
        this.imageLabel = new JLabel();
        // creo l'oggetto immagine
        ImageIcon imageIcon = new ImageIcon(ImageIO.read(new File(rootName + "/img/title.png")));
        this.imageLabel.setIcon(imageIcon);

        // creo un pannello personalizzato per sovrapporre i componenti
        JPanel overlayPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                // disegno l'immagine di sfondo
                g.drawImage(backgroundImage, 0, 0, getWidth(), getHeight(), this);
            }
        };

        // imposto il layout manager su null per posizionare manualmente i componenti
        overlayPanel.setLayout(null);

        this.username = new JTextField(20);
        this.playButton = new JButton("Gioca");
        this.messageLabel = new JLabel("Inserisci il nome con il quale vuoi giocare:");

        // creo l'oggetto font che mi serve per impostare il font
        Font font = new Font("Bauhaus 93", Font.BOLD, 16);

        // imposto le posizioni e le dimensioni dei componenti manualmente
        this.setPositions(screenWidth, screenHeight);

        // imposto il colore della label
        this.messageLabel.setForeground(Color.WHITE);
        this.messageLabel.setFont(font);

        // aggiungo i componenti al pannello di sovrapposizione
        overlayPanel.add(imageLabel);
        overlayPanel.add(messageLabel);
        overlayPanel.add(username);
        overlayPanel.add(playButton);

        add(overlayPanel);
        setVisible(true);

        // funzione che verifica quando viene premuto il pulsante
        playButton.addActionListener(e -> {
            if (!username.getText().isEmpty()) {
                // quando premo il pulsante GIOCA mando una richiesta al server di aggiungere il
                // client ad una nuova partita-->se non ci sono altri client rimane in attesa
                // setto il messaggio da inviare
                message = new Message(start, username.getText(), "");
                // invio il messaggio al server
                try {
                    sendMessage(message);
                } catch (ParserConfigurationException e1) {
                    // TODO Auto-generated catch block
                    e1.printStackTrace();
                } catch (TransformerException e1) {
                    // TODO Auto-generated catch block
                    e1.printStackTrace();
                }
                // aspetto la risposta
                String response = listening();
                // <root_message>
                    // <command>response</command>
                    // <message>200</message>
                    // <username>username</username>
                // </root_message>
                message = new Message();
                try {
                    message.InitMessageFromStringXML(response);
                } catch (ParserConfigurationException | SAXException | IOException e1) {
                    // TODO Auto-generated catch block
                    e1.printStackTrace();
                }

                if(message.message == CORRECT)
                {
                    this.initUser();
                }

                username.setText("");
            } else {
                JOptionPane.showMessageDialog(this, "Inserisci un nome utente", "Errore", JOptionPane.ERROR_MESSAGE);
            }

        });
    }

    private void initUser() {
    }

    private String listening() {

        return null;
    }

    /**
     * setto la posizione degli elementi nella finestra
     * 
     * @param screenWidth
     * @param screenHeight
     */
    public void setPositions(int screenWidth, int screenHeight) {
        this.imageLabel.setBounds((int) (screenWidth * 0.1), (int) (screenHeight * 0.1), 600, 50);
        this.messageLabel.setBounds(20, (int) (screenHeight * 0.2), 600, 30);
        this.username.setBounds((int) (screenWidth * 0.30), (int) (screenHeight * 0.2), 200, 40);
        this.playButton.setBounds((int) (screenWidth * 0.48), (int) (screenHeight * 0.2), 100, 30);
    }

    /**
     * funzione che permette l'avvio del file audio di sottofondo
     */
    public void playMusic() {
        try {
            // creo un oggetto Clip per riprodurre il file audio
            Clip clip = AudioSystem.getClip();

            // apro il file audio
            AudioInputStream audioStream = AudioSystem.getAudioInputStream(new File(rootName + "/audio/UNO_track.wav"));
            clip.open(audioStream);

            // riproduco l'audio in loop
            clip.loop(Clip.LOOP_CONTINUOUSLY);

            // avvio l'audio
            clip.start();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    /**
     * funzione che invia al server un messaggio
     * @param message
     * @throws TransformerException
     * @throws ParserConfigurationException
     */
    public void sendMessage(Message message) throws ParserConfigurationException, TransformerException {
        try {
            // creo una connessione TCP con il server
            Socket socket = new Socket(this.server.IP, this.server.port);

            OutputStream outputStream = socket.getOutputStream();

            // creo un oggetto OutputStreamWriter per inviare dati al server
            OutputStreamWriter writer = new OutputStreamWriter(outputStream);

            // invio il messaggio al server
            writer.write(message.serialize());
            writer.flush(); // scrivo e svuoto il buffer

            // chiduo la connessione
            socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}

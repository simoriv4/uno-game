import java.io.IOException;

import javax.swing.SwingUtilities;

public class App {
    public static void main(String[] args) {
        // carico le info del server(ip e port)
       // Server s = new Server();
        SwingUtilities.invokeLater(() -> {
            GUI gui;
            try {
                gui = new GUI();
                //gui.setVisible(true);
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        });
    }
}

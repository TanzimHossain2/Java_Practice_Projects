import java.net.*;
import java.io.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.event.*;;

public class ReadServerFile extends JFrame {
    private JTextField enter;
    private JEditorPane contents;

    public ReadServerFile(){
        super("Simple web Browser");
        Container c =  getContentPane();
        enter = new JTextField("Enter file URL here");

        enter.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e){
                getThePage(e.getActionCommand());
            }
        });

        c.add(enter, BorderLayout.NORTH);

        contents = new JEditorPane();
        contents.setEditable(false);

        contents.addHyperlinkListener(new HyperlinkListener() {

            public void hyperlinkUpdate(HyperlinkEvent e){
                if(e.getEventType() == HyperlinkEvent.EventType.ACTIVATED){
                    getThePage(e.getURL().toString());
                }
            }

            

        });

        c.add(new JScrollPane(contents), BorderLayout.CENTER);

        setSize(600, 700);

        show();

    }

    private void  getThePage(String location){
        setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
        try{
            contents.setPage(location);
            enter.setText(location);

        }catch(IOException io){
            JOptionPane.showMessageDialog(this, "Error retrieving specified URL","Bad URL", JOptionPane.ERROR_MESSAGE);

        }

        setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
    }

    public static void main(String[] args) {
        ReadServerFile app = new ReadServerFile();
        app.addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e){
                System.exit(0);
            }
        });
    }

}


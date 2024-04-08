import java.net.*;
import java.io.*;
import java.util.Date;
import java.awt.*;
import java.awt.event.*;

public class URLConnector extends Frame implements ActionListener, WindowListener {
    TextField theLocation = new TextField("",40);
    TextArea theDoc = new TextArea();
    Button  fetchButton = new Button("Fetch");
    Button  clearButton = new Button("Clear");

    public static void main(String[] args) {
        URLConnector app = new URLConnector();
    }

    public URLConnector(){
        setTitle("The URLConnector Example");
        theLocation.setFont(new Font("Courier", Font.PLAIN, 14));

        Panel p1 = new Panel();
        p1.add(new Label("Enter a URL:"));
        p1.add(theLocation);
        add(p1, "North");

        add(theDoc, "Center");

        Panel p2 = new Panel();
        p2.add(fetchButton);
        p2.add(clearButton);
        add(p2, "South");

        theLocation.addActionListener(this);
        fetchButton.addActionListener(this);
        clearButton.addActionListener(this);
        addWindowListener(this);

        setSize(700,600);
        setVisible(true);
        theLocation.requestFocus();
    }

    public void actionPerformed(ActionEvent theEvent){
        if(theEvent.getSource() == fetchButton || theEvent.getSource() == theLocation){
            try{
                //open the URL
                URL theURL = new URL(theLocation.getText());
                theDoc.setText("\n\nFetching from " + theURL + "...\n\n");

                //open the connection
                URLConnection con = theURL.openConnection();

                //Retrieve information about the Document
                String encoding = con.getContentEncoding();
                int length = con.getContentLength();
                String type = con.getContentType();
                long expires = con.getExpiration();
                Date modified = new Date(con.getLastModified());

                //display the information   
                theDoc.append("Content encoding: " + encoding + "\n");
                theDoc.append("Content length: " + length + "\n");
                theDoc.append("Content type: " + type + "\n");
                theDoc.append("Expires: " + expires + "\n");
                theDoc.append("Last modified: " + modified + "\n\n");

                //display the document if possible
                Object urlContent = theURL.getContent();
                String objectType = urlContent.getClass().getName();
                theDoc.append("\n Fetched a"+ objectType + "\n\n");

                //plain text
                if(urlContent instanceof String){
                    theDoc.append((String)urlContent);
                }

                else if(urlContent instanceof InputStream){
                    StringBuilder content = new StringBuilder(); 
                    int b;
                    while((b = ((InputStream)urlContent).read()) != -1){
                        content.append((char)b); 
                    }
                    theDoc.append(content.toString()); 
                }

                else{
                    theDoc.append("Can't display content of type " + objectType);
                }

            } catch (MalformedURLException badUrl){
                theDoc.append("\n Sorry, cannot interpret your url \n");
                theDoc.append(badUrl.toString());
            } catch (IOException ioe){
                theDoc.append("\n Sorry, had a problem connecting to the URL\n");
                theDoc.append(ioe.toString());
            } catch(NullPointerException npe){
                theDoc.append("\n Sorry, had a problem connecting to the URL\n");
                theDoc.append(npe.toString());
            } catch(Exception e){
                theDoc.append("\n Sorry, had a problem connecting to the URL\n");
                theDoc.append(e.toString());
            }

        } else if (theEvent.getSource() == clearButton){
            theDoc.setText("");
            quit();
        }


    }

    public void windowClosed(WindowEvent e){}
    public void windowClosing(WindowEvent e){
        quit();
    }
    public void windowDeiconified(WindowEvent e){}
    public void windowIconified(WindowEvent e){}
    public void windowOpened(WindowEvent e){}
    public void windowActivated(WindowEvent e){}
    public void windowDeactivated(WindowEvent e){}


    public void quit(){
        setVisible(false);
        dispose();
        System.exit(0);

    }
    
    
}


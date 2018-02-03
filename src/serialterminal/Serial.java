/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package serialterminal;
import com.fazecast.jSerialComm.*;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintStream;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import static java.lang.System.in;
import static java.util.Locale.US;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Curtis
 */
public class Serial {
    private FXMLDocumentController gui;
    private SerialPort comPort;
    private SerialPort ports[];
    private String portNames[];
    private int baudrate = 115200;
    private OutputStream outstream;
    private InputStream instream;
    private PrintStream out;
    //private Scanner in;
    
    Serial(FXMLDocumentController gui){
        this.gui = gui;
        refresh();
    }
    
    public void refresh(){
        ports = SerialPort.getCommPorts();
        int count = ports.length;
        portNames = new String[count];
        if(count > 0){
            for(int i = 0; i < count; i++){
                portNames[i] = ports[i].getSystemPortName();
            }
        }
    }
    public String[] getPortNames(){
        return portNames;
    }
    private SerialPort getPortByName(String name){
        return SerialPort.getCommPort(name);
    }
    public int setup(String name){
        SerialPort getPort = getPortByName(name);
        if(getPort != null){
            comPort = getPort;
            comPort.setBaudRate(baudrate);
            /*  set up event handler for when serial data recieved so that incoming data 
                can be read asynchronously, so that program doesn't freeze up waiting for data */
            comPort.addDataListener(new SerialPortDataListener() {
                @Override
                public int getListeningEvents() {
                    return SerialPort.LISTENING_EVENT_DATA_AVAILABLE; 
                }
                @Override
                public void serialEvent(SerialPortEvent event)
                {
                    if(comPort.bytesAvailable() != 0){
                        byte[] readBuffer = new byte[comPort.bytesAvailable()];
                        int numRead = comPort.readBytes(readBuffer, readBuffer.length);
                        read(new String(readBuffer));
                    }
                    
                }
             });
            return 1;
        }
        else return -1;
    }
    public int connect(){
        if(comPort.openPort()){
            outstream = comPort.getOutputStream();
            out = new PrintStream(outstream);
            instream = comPort.getInputStream();
            //in = new Scanner(instream);
            return 1;
        }
        else return -1;
    }
    public int connect(String name){
        if(setup(name) == -1) return -1;
        else if(connect() == -1) return -1;
        else return 1;
    }
    public int disconnect(){
        if(comPort.closePort()) return 1;
        else return -1;
    }
    public int available(){
        return comPort.bytesAvailable();
    }
    public void print(char c){
        out.print(String.valueOf(c));
    }
    public void print(String s){
        out.print(s);
    }
    public void println(String s){
        print(s + "\n");
    }
    public String readForce(){
        if(comPort.bytesAvailable() > 0){
            byte[] readBuffer = new byte[comPort.bytesAvailable()];
            int numRead = comPort.readBytes(readBuffer, readBuffer.length);
            return ("Read " + numRead + " bytes: " + new String(readBuffer));
        }
        else{
            return "No bytes to read. \n";
        }
    }
    public void read(String s){
        gui.consoleOut(s);
    }
    
}

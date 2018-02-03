/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package serialterminal;

import java.net.URL;
import java.util.ResourceBundle;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

/**
 *
 * @author Curtis
 */

public class FXMLDocumentController implements Initializable {
    
    @FXML
    private TextArea cmdOut;
    @FXML
    private TextField cmdIn;
    @FXML
    private ListView serialList;
    @FXML
    private Rectangle serialStatus;
    @FXML
    private Button serialRefresh;
    @FXML
    private Button serialConnect;
    @FXML
    private Button serialDisonnect;
    @FXML
    private Button serialUpdate;
    
    private Serial serial = new Serial(this);
    private ObservableList serialports = FXCollections.observableArrayList();
    private boolean connected = false;
    
    @FXML
    private void serialRefresh() {
        serial.refresh();
        serialports.clear();
        serialports.addAll(serial.getPortNames());
        serialList.setItems(serialports);
    }
    @FXML
    private void serialConnect() {
        if(connected == false){
            String selected = (String)serialList.getSelectionModel().getSelectedItem();
            if(selected != null && selected != ""){
                if(serial.connect(selected) == 1)
                    serialStatus.setFill(Color.RED);
                    connected = true;
            }
        }
    }
    @FXML
    private void serialDisconnect() {
        if(connected == true){
            if(serial.disconnect() == 1)
                serialStatus.setFill(Color.BLACK);
                connected = false;
        }
    }
    @FXML
    private void serialUpdate() {
        if(connected == true){
            if(serial.available() > 0){
                String s = serial.readForce();
                cmdOut.appendText(s);
            }
        }
    }
    private void consoleIn(){
        String cmd = cmdIn.getText();
        cmdIn.clear();
        serial.println(cmd);
    }
    @FXML
    private void consoleInOnEnter(KeyEvent e){
        if(e.getCode().equals(KeyCode.ENTER))
            consoleIn();
    }
    @FXML
    public void consoleClear(){
        cmdOut.clear();
    }
    
    public void consoleOut(String s){
        cmdOut.appendText(s);
    }
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        serialRefresh();
    }    
    
}

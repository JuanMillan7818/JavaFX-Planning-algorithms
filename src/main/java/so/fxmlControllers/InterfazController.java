/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package so.fxmlControllers;

import io.github.palexdev.materialfx.controls.MFXButton;
import io.github.palexdev.materialfx.controls.MFXFilterComboBox;
import io.github.palexdev.materialfx.controls.MFXLabel;
import io.github.palexdev.materialfx.controls.MFXTextField;
import io.github.palexdev.materialfx.controls.MFXTooltip;
import io.github.palexdev.materialfx.font.MFXFontIcon;
import io.github.palexdev.materialfx.utils.BindingUtils;
import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;
import javafx.animation.PauseTransition;
import javafx.animation.TranslateTransition;
import javafx.beans.binding.Bindings;
import javafx.beans.property.BooleanProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.scene.paint.Color;
import javafx.scene.text.TextAlignment;
import javafx.util.Duration;
import so.App;
import so.Logic;
import so.MyProcess;
import so.Notification;
import so.Util;

/**
 * FXML Controller class
 *
 * @author juan
 */
public class InterfazController implements Initializable {
    
    private Logic myLogic;
    private boolean allValid;    
    private boolean algorithmValid1;
    private boolean algorithmValid2;
    private MFXTextField texto;
    private int startNode;
    private int currentRow;
    private App controller;

    @FXML
    private MFXLabel textRandom;
    @FXML
    private Button optionRandom;
    @FXML
    private Button optionConfig;
    @FXML
    private MFXLabel textConfig;
    @FXML
    private MFXFilterComboBox<Label> queue1;
    @FXML
    private MFXFilterComboBox<Label> queue2;
    @FXML
    private MFXButton buttonAdd;
    @FXML
    private GridPane containerGrid;
    @FXML
    private Button optionClear;
    @FXML
    private MFXLabel textClear;
    @FXML
    private MFXLabel labelPriority;
    @FXML
    private AnchorPane containerMain;
    @FXML
    private Label showInfoPersonal;

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // TODO           
        myLogic = new Logic();
        allValid = true;
        algorithmValid1 = false;
        algorithmValid2 = false;
        textRandom.setVisible(false);  
        textConfig.setVisible(false);
        textClear.setVisible(false);
        optionConfig.setCursor(Cursor.HAND);
        optionRandom.setCursor(Cursor.HAND);
        optionClear.setCursor(Cursor.HAND);        
        containerGrid.getChildren().remove(labelPriority);
        startNode = 0;
        currentRow = 1;
        dataQueue();
        addTooltipInfo();
    }  

    public void setController(App controller) {
        this.controller = controller;
    }
            
    public void dataQueue() { // Agrego los datos a elegir en cada Cola de ejecucion   
        queue1.setItems(FXCollections.observableArrayList(                
                new Label(" Por Prioridad", new ImageView(App.class.getResource("resources/image/item.png").toExternalForm()))
        )); 
        queue2.setItems(FXCollections.observableArrayList(
                new Label(" FCFS", new ImageView(App.class.getResource("resources/image/item.png").toExternalForm())),
                new Label(" SJN", new ImageView(App.class.getResource("resources/image/item.png").toExternalForm())),
                new Label(" HRN", new ImageView(App.class.getResource("resources/image/item.png").toExternalForm()))
        ));                             
        changeAlgorithm();       
        validatedOptions();
    }
    
    public boolean fieldEmpty() { // Valido si hay algun proceso que no tenga datos       
        if(containerGrid.getRowCount() >= 2) {            
            for(int i=6; i<containerGrid.getChildren().size()-1; i++) {
                MFXTextField aux = (MFXTextField) containerGrid.getChildren().get(i);
                if(aux.getText().equalsIgnoreCase("")) return false;                
            }
        }
        return true;
    }
    
    public void changeAlgorithm() { // Agrego un Oyente para estar atento a los cambios o cuando se eliga algun algortimo
        queue1.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<Label>() {                        
            @Override
            public void changed(ObservableValue<? extends Label> ov, Label oldValue, Label newValue) {
                if(newValue != null) {                    
                    if(newValue.getText().equalsIgnoreCase(" Por prioridad")) {                        
                        containerGrid.add(labelPriority, 5, 0);
                    }else {
                        containerGrid.getChildren().remove(labelPriority);
                    }                    
                }
            }
        });  
        queue2.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<Label>() {                        
            @Override
            public void changed(ObservableValue<? extends Label> ov, Label oldValue, Label newValue) {                
                if(newValue != null) {                    
                }                              
            }
        });         
    }
    
    public void validatedOptions() { // Creo y Agrego validadciones para asegurar que se escoga un algoritmo
        queue1.setValidated(true);
        queue2.setValidated(true);   
        
        BooleanProperty valid1 = BindingUtils.toProperty(
                Bindings.createBooleanBinding(() -> {       
                    
            boolean aux = (queue1.getSelectedValue() == null) ? false:true;
            algorithmValid1 =                     
                    queue1.getSelectionModel().selectedIndexProperty().isNotEqualTo(-1).get()
                    &&
                    aux;                    
                    
            return algorithmValid1;
        }, queue1.selectedValueProperty()));        
        queue1.getValidator().add(valid1, "Elige al menos uno");
        
         BooleanProperty valid2 = BindingUtils.toProperty(
                Bindings.createBooleanBinding(() -> {                     
                    
            boolean aux = (queue2.getSelectedValue() == null) ? false:true;
            algorithmValid2 = 
                    queue2.getSelectionModel().selectedIndexProperty().isNotEqualTo(-1).get()
                    &&
                    aux;
            
            return algorithmValid2;
        }, queue2.selectedValueProperty()));        
        queue2.getValidator().add(valid2, "Elige al menos uno");                
    }
    
    public void addData(int row, int col) { // Recorro la ultima fila y la agrego a la lista de procesos
        ObservableList<Node> nodes = containerGrid.getChildren();
        ArrayList<String> data = new ArrayList<>();
        int apply;        
        for(int i=row; i<(row+col); i++) {
            MFXTextField field = (MFXTextField) nodes.get(i);       
            data.add(field.getText());            
        }
        if(col == 5) apply = -1;
        else apply = Integer.parseInt(data.get(5));
        myLogic.getMyProcess().add(new MyProcess(
                data.get(0), 
                Integer.parseInt(data.get(1)), 
                Integer.parseInt(data.get(2)),
                Integer.parseInt(data.get(3)), 
                Integer.parseInt(data.get(4)),     
                apply
                ));        
    }
    
    public void addTooltipInfo() {
        MFXTooltip tooltip = new MFXTooltip(
                "Integrantes: \n1. Juan Manuel Perez Alvarez 1958520. "
                + "\n2. Mauricio Rodriguez Ramirez 1958343.");  
        tooltip.setTextAlignment(TextAlignment.LEFT);
        tooltip.setGraphic(new MFXFontIcon("mfx-info-circle", 40, Color.WHITESMOKE));
        showInfoPersonal.setTooltip(tooltip);
    }
    

    @FXML
    private void viewInfoRandom(MouseEvent event) {
        textRandom.setVisible(true);
        TranslateTransition effect = new TranslateTransition(Duration.millis(500),textRandom);
        effect.setFromX(-20); effect.setToX(0);
        effect.setCycleCount(0);
        effect.play();
    }

    @FXML
    private void hiddenInfoRandom(MouseEvent event) {        
        TranslateTransition effect = new TranslateTransition(Duration.millis(500),textRandom);
        effect.setFromX(0); effect.setToX(-80);
        effect.setCycleCount(0);
        effect.play();        
        effect.setOnFinished(ev -> {
            textRandom.setVisible(false);
        });                
    }    

    @FXML
    private void hiddenInfoConfig(MouseEvent event) {
        TranslateTransition effect = new TranslateTransition(Duration.millis(500),textConfig);
        effect.setFromX(0); effect.setToX(-80);
        effect.setCycleCount(0);
        effect.play();        
        effect.setOnFinished(ev -> {
            textConfig.setVisible(false);
        });  
    }

    @FXML
    private void viewInfoConfig(MouseEvent event) {
        textConfig.setVisible(true);
        TranslateTransition effect = new TranslateTransition(Duration.millis(500),textConfig);
        effect.setFromX(-20); effect.setToX(0);
        effect.setCycleCount(0);
        effect.play();
    }
    
     @FXML
    private void hiddenInfoClear(MouseEvent event) {
        TranslateTransition effect = new TranslateTransition(Duration.millis(500),textClear);
        effect.setFromX(0); effect.setToX(-80);
        effect.setCycleCount(0);
        effect.play();        
        effect.setOnFinished(ev -> {
            textClear.setVisible(false);
        });   
    }

    @FXML
    private void viewInfoClear(MouseEvent event) {
        textClear.setVisible(true);
        TranslateTransition effect = new TranslateTransition(Duration.millis(500),textClear);
        effect.setFromX(-20); effect.setToX(0);
        effect.setCycleCount(0);
        effect.play();
    }

    @FXML
    private void addProcess(ActionEvent event) {            
        if(algorithmValid1 && algorithmValid2) {   
            queue1.setDisable(true); queue2.setDisable(true);            
            if(allValid && fieldEmpty()) {                  
                containerGrid.getChildren().remove(buttonAdd);                             
                int col = (containerGrid.getRowIndex(labelPriority) == null) ? 5:6;                
                for(int i=0; i<col; i++) {                                        
                    MFXTextField field = new MFXTextField();            
                    field.setMinWidth(50); field.setPrefWidth(70);   
                    field.setMinHeight(35);             
                    field.setStyle("-fx-text-fill: white");
                    field.setTextLimit(5);
                    field.setPromptText("#");
                    field.setValidated(true);  
                    BooleanProperty fieldValidated = BindingUtils.toProperty(
                            Bindings.createBooleanBinding(() -> {
                                try {
                                    Integer.parseInt(field.getText());
                                    allValid = true;
                                    return true;
                                } catch (NumberFormatException e) {
                                    allValid = false;
                                    return false;
                                }
                            }, field.textProperty())
                    );

                    if(i > 0) {
                        field.setValidatorTitle("Solo se acepta valores numericos  \n");                    
                        field.getValidator().add(fieldValidated, "Son numeros?");                            
                    }else {
                        field.getValidator().add(BindingUtils.toProperty(field.textProperty().isNotEmpty()), "Esta Vacio!");
                    }                
                    
                    containerGrid.add(field, i, currentRow);
                    if(currentRow > 1) startNode++;
                }
                // Agrego datos si hay filas anteriores
                if(currentRow > 1 && currentRow <= 10) {                                
                    addData(startNode, col);                                          
                }            
                
                if(currentRow < 10) {
                    containerGrid.add(buttonAdd,6,currentRow);
                    currentRow++;
                }                                                       
                containerGrid.setAlignment(Pos.CENTER);                 
            }else {
                Notification notification = new Notification("Campos faltantes o incorrectos", 
                        "Por favor verifica bien tus datos");
                notification.showTopLeft();
            }
        }else {            
            Notification notification = new Notification("Error en los campos",
                "Por favor elegi los algoritmos para las respectivas colas de ejecucion");
            notification.showTopBottom(containerMain);
        }        
    }        

    @FXML
    private void processData(ActionEvent event) {                
        if(queue1.getSelectedValue() != null && queue2.getSelectedValue() != null) {
            if(allValid && fieldEmpty()) {            
                addProcess(event);            
                if(myLogic.getMyProcess().size() < 2 || myLogic.getMyProcess().isEmpty()) {                
                    Notification notification = new Notification("Se necesitan mas procesos", 
                        "Se requiere tener 2 o mas procesos para ejecutar");
                    notification.showTopBottom(containerMain);
                }else {                                                            
                    controller.startData(myLogic, queue1.getSelectedValue().getText(), queue2.getSelectedValue().getText());                            
                }            
            }else {
                Notification notification = new Notification("Se necesita completar los procesos", 
                        "Has seleccionado un determinado numero de procesos pero no los completaste todos");
                notification.showTopBottom(containerMain);
            }   
        }else {
            Notification notification = new Notification("Error en los campos",
                "Por favor elegi los algoritmos para las respectivas colas de ejecucion");
            notification.showTopBottom(containerMain);
        }             
    }

    @FXML
    private void randomData(ActionEvent event) {        
        if(queue1.getSelectedValue() != null && queue2.getSelectedValue() != null) {
            //myLogic.auxData();
            myLogic.setMyProcess(Util.dataRandom(5));    
            PauseTransition pause = new PauseTransition(Duration.millis(300));
            pause.setOnFinished(event1 -> {
                controller.startData(myLogic, queue1.getSelectedValue().getText(), queue2.getSelectedValue().getText());                
            });                
            pause.play();                            
        }else {
            Notification notification = new Notification("Error en los campos",
                "Por favor elegi los algoritmos para las respectivas colas de ejecucion");
            notification.showTopBottom(containerMain);
        }
    }

    @FXML
    private void clearAll(ActionEvent event) {         
        if(containerGrid.getRowCount() > 1) { // Si hay procesos escritos
            queue1.setDisable(false); queue2.setDisable(false); // Habilito para cambiar de algoritmo
            currentRow = 1; // Reinicio el conteo de las filas
            allValid = true; // Reinicio para que este valido las cosas
            containerGrid.getChildren().removeIf(node -> containerGrid.getRowIndex(node) != null
                    && containerGrid.getRowIndex(node) >= 1 );    // Elimino todas las filas excepto la primera
            myLogic.getMyProcess().clear(); // Elimino los procesos que se hallan guardado antes        
            containerGrid.add(buttonAdd,6,0); // Agrego de nuevo el boton para agregar procesos                    
        }
    }   
}

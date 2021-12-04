/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package so.fxmlControllers;

import io.github.palexdev.materialfx.controls.MFXButton;
import io.github.palexdev.materialfx.controls.MFXScrollPane;
import io.github.palexdev.materialfx.controls.MFXTableView;
import io.github.palexdev.materialfx.controls.cell.MFXTableColumn;
import io.github.palexdev.materialfx.controls.cell.MFXTableRowCell;
import io.github.palexdev.materialfx.utils.ScrollUtils;
import java.net.URL;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.ResourceBundle;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Color;
import so.App;
import so.GanttChart;
import so.Logic;
import so.MyProcess;
import so.Util;

/**
 * FXML Controller class
 *
 * @author juan
 */
public class DataController implements Initializable {

    private int allBurst;
    private ArrayList<MyProcess> processData;    
    private GanttChart<Number,String> ganttChart;
    private CategoryAxis yAxis; // Procesos
    public static NumberAxis xAxis; // Tiempo en cpu    
    private XYChart.Series<Number, String> stateExecution;
    private XYChart.Series<Number, String> stateWait1;
    private XYChart.Series<Number, String> stateWait2;
    private XYChart.Series<Number, String> stateES;    
    private XYChart.Series<Number, String> stateInVisibility; 
    
    // Tabla
    private MFXTableView<MyProcess> tableProcess;
    
    private App controller;
    
    @FXML
    private AnchorPane container;
    @FXML
    private MFXButton back;
    @FXML
    private Label textQueue2;

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // Icono para el boton de devolverse
        ImageView icon = new ImageView(App.class.getResource("resources/image/back.png").toExternalForm()); 
        icon.setFitHeight(30);
        icon.setFitWidth(30);
        back.setGraphic(icon);
    }     
    
    public ArrayList<String> getItemAxisY() {                
        ArrayList<String> headers = new ArrayList<>();
        for(MyProcess item : processData) { // Datos para el eye Y, es decir, los id de los procesos
            allBurst += (item.getBurstStart() + item.getBurstEnd());
            headers.add(item.getId());
        }
        return headers;
    }
       
    
    public void paintData(Logic data, String queue1, String queue2) {         
        // Pongo el titulo de la cola #2
        textQueue2.setText(textQueue2.getText() + queue2);
        
        // Cabeceras del eje Y
        yAxis = new CategoryAxis(FXCollections.<String>observableArrayList(getItemAxisY()));
        yAxis.setLabel("Nombre del Proceso");
        yAxis.setTickLabelGap(10);
        
        // Cabeceras del eje X
        xAxis = new NumberAxis();
        xAxis.setLabel("Tiempo de CPU");
        xAxis.setAutoRanging(false);
        xAxis.setLowerBound(0);
        xAxis.setUpperBound(allBurst);
        xAxis.setTickUnit(1);
        xAxis.setTickLabelRotation(90);        
        
        // Instancio la grafica
        ganttChart = new GanttChart<Number,String>(xAxis,yAxis);        
        ganttChart.setTitle("Planificacion de procesos"); 
        ganttChart.setLegendVisible(false);        
        
        // Inicializar Estados
        stateExecution = new XYChart.Series<>();
        stateWait1 = new XYChart.Series<>();               
        stateWait2 = new XYChart.Series<>();        
        stateES = new XYChart.Series<>();        
                
        
        stateExecution.setName("Ejecucion");
        stateWait1.setName("Espera en Cola 1");
        stateWait2.setName("Espera en Cola 2");
        stateES.setName("Ejecucion de E/S");           
        
        // Cargar Datos
        data.start(queue1, queue2, this);
                
        // Agregar las series o los estados
        ganttChart.getData().addAll(stateExecution,stateWait1,stateWait2, stateES);
        
        // Agregar al contenedor con Scroll        
        addScrollPane();
    }
        
    public void addScrollPane() {
        MFXScrollPane scroll = new MFXScrollPane(ganttChart);
        ScrollUtils.addSmoothScrolling(scroll);        
        ScrollUtils.animateScrollBars(scroll);        
        scroll.setThumbHoverColor(Color.rgb(100, 206, 186));
        
        // Tamanio de la grafica y del conenetor de la grafica                
        ganttChart.setMinWidth(container.getWidth()-100);
        ganttChart.setMinHeight(300);
        ganttChart.setMaxHeight(700);
        scroll.setPrefSize(container.getWidth()-50, 310);             
        
        // Posicionamiento
        scroll.setLayoutX(24);
        scroll.setLayoutY(120);
        
        /// Agregar al contenedor principal
        container.getChildren().add(scroll);
    }
      
    public void tableView(ArrayList<MyProcess> data) {                
        // Instancio la tabla
        tableProcess = new MFXTableView<>();        
            
        ObservableList<MyProcess> listProcess = FXCollections.observableArrayList(data);        
        
        // Creo columnas
        MFXTableColumn<MyProcess> nameProcess = new MFXTableColumn<>("Proceso", Comparator.comparing(MyProcess::getId));
        MFXTableColumn<MyProcess> arrivalProcess = new MFXTableColumn<>("T. Llegada", Comparator.comparing(MyProcess::getArrival));
        MFXTableColumn<MyProcess> burstStartProcess = new MFXTableColumn<>("Rafagas Iniciales", Comparator.comparing(MyProcess::getBurstStart));
        MFXTableColumn<MyProcess> burstESProcess = new MFXTableColumn<>("Rafagas E/S", Comparator.comparing(MyProcess::getBurstES));
        MFXTableColumn<MyProcess> burstEndProcess = new MFXTableColumn<>("Rafagas Finales", Comparator.comparing(MyProcess::getBurstEnd));        
        MFXTableColumn<MyProcess> priorityProcess = new MFXTableColumn<>("Prioridad", Comparator.comparing(MyProcess::getPriority));
        MFXTableColumn<MyProcess> TFinalProcess = new MFXTableColumn<>("Tiempo Final", Comparator.comparing(MyProcess::getTimeFinal));
        MFXTableColumn<MyProcess> TWaitProcess = new MFXTableColumn<>("Tiempo de Servicio", Comparator.comparing(MyProcess::getTimeService));
        MFXTableColumn<MyProcess> TInstantProcess = new MFXTableColumn<>("Instante", Comparator.comparing(MyProcess::getTimeInstant));
        MFXTableColumn<MyProcess> IndexProcess = new MFXTableColumn<>("Indice del proceso", Comparator.comparing(MyProcess::getIndexService));        
        
        
        // Configuracion de las columnas con filas
        nameProcess.setRowCellFunction(process -> {
                MFXTableRowCell cell = new MFXTableRowCell(process.getId());
                cell.setRowAlignment(Pos.CENTER);
                return cell;
            });
        arrivalProcess.setRowCellFunction(process -> {
                MFXTableRowCell cell = new MFXTableRowCell(                        
                    (process.getArrival() == -1) ? "" : Integer.toString(process.getArrival()));
                cell.setRowAlignment(Pos.CENTER);
                return cell;
            });
        burstStartProcess.setRowCellFunction(process -> {
                MFXTableRowCell cell = new MFXTableRowCell(
                    (process.getBurstStart() == -1) ? "" : Integer.toString(process.getBurstStart()));
                cell.setRowAlignment(Pos.CENTER);
                return cell;
            });
        burstESProcess.setRowCellFunction(process -> {
                MFXTableRowCell cell = new MFXTableRowCell(
                    (process.getBurstES() == -1) ? "" : Integer.toString(process.getBurstES()));
                cell.setRowAlignment(Pos.CENTER);
                return cell;
            });
        burstEndProcess.setRowCellFunction(process -> {
                MFXTableRowCell cell = new MFXTableRowCell(
                    (process.getBurstEnd() == -1) ? "" : Integer.toString(process.getBurstEnd()));
                cell.setRowAlignment(Pos.CENTER);
                return cell;
            });
        priorityProcess.setRowCellFunction(process -> {
                MFXTableRowCell cell = new MFXTableRowCell(
                    (process.getPriority() == -1) ? "" : Integer.toString(process.getPriority()));
                cell.setRowAlignment(Pos.CENTER);
                return cell;
            });
        TFinalProcess.setRowCellFunction(process -> {
                MFXTableRowCell cell = new MFXTableRowCell(
                    (process.getTimeFinal() == -1) ? "" : Float.toString(process.getTimeFinal()));
                cell.setRowAlignment(Pos.CENTER);
                return cell;
            }); 
        TWaitProcess.setRowCellFunction(process -> {
                MFXTableRowCell cell = new MFXTableRowCell(Float.toString(process.getTimeService()));
                cell.setRowAlignment(Pos.CENTER);
                return cell;
            });
        TInstantProcess.setRowCellFunction(process -> {
                MFXTableRowCell cell = new MFXTableRowCell(Float.toString(process.getTimeInstant()));
                cell.setRowAlignment(Pos.CENTER);
                return cell;
            });
        IndexProcess.setRowCellFunction(process -> {
                MFXTableRowCell cell = new MFXTableRowCell(Float.toString(process.getIndexService()));
                cell.setRowAlignment(Pos.CENTER);
                return cell;
            });        
                
        // Agrego datos a la tabla
        tableProcess.setItems(listProcess);
        
        // Agrego las configuracion y las columnas a la tabla
        tableProcess.getTableColumns().addAll(nameProcess, arrivalProcess, 
                burstStartProcess, burstESProcess, burstEndProcess, priorityProcess,
                TFinalProcess, TWaitProcess, TInstantProcess, IndexProcess);
        
        // Coloco un titulo a la tabla
        tableProcess.setHeaderText("Datos y Resultados");        
        
        ImageView icon = new ImageView(App.class.getResource("resources/image/icon.png").toExternalForm());
        icon.setFitHeight(30);
        icon.setFitWidth(30);
        tableProcess.setHeaderIcon(icon); // Coloco un icono al titulo de la tabla
               
        // Ancho de las columnas
        nameProcess.setMinWidth(65);   
        arrivalProcess.setMinWidth(75);
        burstStartProcess.setMinWidth(120);
        burstEndProcess.setMinWidth(120);
        burstESProcess.setMinWidth(100);
        priorityProcess.setMinWidth(80);
        TFinalProcess.setMinWidth(120);
        TWaitProcess.setMinWidth(140);
        TInstantProcess.setMinWidth(90);
        IndexProcess.setMinWidth(120);
        
        // Posicion de las cabeceras de las columnas
        nameProcess.setColumnAlignment(Pos.CENTER);
        arrivalProcess.setColumnAlignment(Pos.CENTER);
        burstStartProcess.setColumnAlignment(Pos.CENTER);
        burstEndProcess.setColumnAlignment(Pos.CENTER);
        burstESProcess.setColumnAlignment(Pos.CENTER);
        priorityProcess.setColumnAlignment(Pos.CENTER);
        TFinalProcess.setColumnAlignment(Pos.CENTER);
        TWaitProcess.setColumnAlignment(Pos.CENTER);
        TInstantProcess.setColumnAlignment(Pos.CENTER);
        IndexProcess.setColumnAlignment(Pos.CENTER);
        
        
        // Contenedor con scroll
        MFXScrollPane scroll = new MFXScrollPane(tableProcess);
        scroll.setPrefSize(container.getWidth()*0.8, 250); // Dimensiones del contenedor con scroll
        scroll.setLayoutX(24); // Posicion en x
        scroll.setLayoutY(450); // Posicion en y
        
        // Animacion en la barra de scroll
        scroll.setTrackColor(Color.rgb(100, 206, 186)); 
        ScrollUtils.addSmoothScrolling(scroll);        
        ScrollUtils.animateScrollBars(scroll);
        
        
        // Agrego al contenedor padre
        container.getChildren().add(scroll);        
    }
    
    public void setController(App controller) {
        this.controller = controller;
    }

    public void setProcessData(ArrayList<MyProcess> processData) {        
        this.processData = processData;           
    } 

    public XYChart.Series<Number, String> getStateExecution() {
        return stateExecution;
    }        

    public XYChart.Series<Number, String> getStateWait1() {
        return stateWait1;
    }

    public XYChart.Series<Number, String> getStateWait2() {
        return stateWait2;
    }

    public XYChart.Series<Number, String> getStateES() {
        return stateES;
    }

    public XYChart.Series<Number, String> getStateInVisibility() {
        return stateInVisibility;
    }

    @FXML
    private void backFrame(ActionEvent event) { // Devolver a la interfaz anterior
        controller.startApp();
    }
        
}
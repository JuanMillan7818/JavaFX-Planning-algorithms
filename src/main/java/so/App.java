package so;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import java.io.IOException;
import javafx.scene.image.Image;
import javafx.scene.layout.AnchorPane;
import so.fxmlControllers.DataController;
import so.fxmlControllers.InterfazController;


/**
 * JavaFX App
 */
public class App extends Application {

    private Stage mainStage;
    private AnchorPane container; 
    private InterfazController mainController;
    private DataController secondController;
    
    @Override
    public void start(Stage stage) throws IOException {
        mainStage = stage;        
        mainStage.setTitle("Proyecto de SO"); // Titulo de la ventana        
        startApp();                         
    }  
        
    public void startApp() {
        try {
            FXMLLoader file = new FXMLLoader(App.class.getResource("resources/interfaz.fxml"));            
            container = (AnchorPane) file.load();                
            mainStage.setScene(new Scene(container));
            
            insertIcon();
            mainStage.centerOnScreen();
            mainStage.setResizable(false);
            // Controller
            mainController = file.getController();
            mainController.setController(this);
            
            mainStage.show();
        } catch (IOException e) {
            System.out.println("Se ha producido un error al intentar de abrir el interfaz.fxml: " + e.getMessage());
        }
    } 
    
    public void startData(Logic data, String queue1, String queue2) {
        try {            
            FXMLLoader file = new FXMLLoader(App.class.getResource("resources/data.fxml"));            
            container = (AnchorPane) file.load();                
            mainStage.setScene(new Scene(container));
            
            insertIcon();
            mainStage.centerOnScreen();
            mainStage.setResizable(false);
            // Controller            
            secondController = file.getController();
            secondController.setController(this);                                    
            secondController.setProcessData(data.getMyProcess());                  
            secondController.paintData(data, queue1, queue2);            
            
            mainStage.show();
        } catch (IOException e) {
            System.out.println("Se ha producido un error al intentar de abrir el data.fxml: " + e.getMessage());
        }
    }
    
    public void insertIcon() {        
        Image icon = new Image(App.class.getResource("resources/image/icon.png").toExternalForm());
        mainStage.getIcons().add(icon);                
    }

    public static void main(String[] args) {
        launch(args);
    }

}
module so {
    requires MaterialFX;
    
    requires javafx.controls;        
    requires javafx.fxml;
    requires java.base;
    requires javafx.graphics; 
    
    opens so;          
    opens so.resources;       
    opens so.fxmlControllers;
    exports so;
}

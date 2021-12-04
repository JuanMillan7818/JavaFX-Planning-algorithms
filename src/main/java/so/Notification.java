/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package so;

import io.github.palexdev.materialfx.controls.MFXNotification;
import io.github.palexdev.materialfx.controls.SimpleMFXNotificationPane;
import io.github.palexdev.materialfx.controls.base.AbstractMFXDialog;
import io.github.palexdev.materialfx.controls.enums.DialogType;
import io.github.palexdev.materialfx.controls.factories.MFXAnimationFactory;
import io.github.palexdev.materialfx.controls.factories.MFXDialogFactory;
import io.github.palexdev.materialfx.notifications.NotificationPos;
import io.github.palexdev.materialfx.notifications.NotificationsManager;
import javafx.application.Platform;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Region;
import javafx.util.Duration;

/**
 *
 * @author juan
 */
public class Notification {
    private String title;
    private String body;    
    
    public Notification() {        
    }

    public Notification(String title, String body) {
        this.title = title;
        this.body = body;
    }
    
    public void showTopBottom(AnchorPane container) {        
        AbstractMFXDialog dialog = MFXDialogFactory.buildDialog(DialogType.ERROR, 
                title, 
                body);
        dialog.setAnimateIn(true);
        dialog.setAnimateOut(true);
        dialog.setInAnimationType(MFXAnimationFactory.SLIDE_IN_TOP);
        dialog.setOutAnimationType(MFXAnimationFactory.SLIDE_OUT_BOTTOM);   
        dialog.setLayoutX(350);
        dialog.setLayoutY(210);
        dialog.autosize();                         
        Platform.runLater(() -> {             
            container.getChildren().add(dialog);        
            dialog.setOverlayClose(true);
            dialog.setIsDraggable(true);
        });        
        dialog.show();
    }
    
    public void showTopLeft() {
        NotificationPos pos = NotificationPos.TOP_RIGHT;
        showNotification(pos, false);        
    }
    
    public void showError() {
        NotificationPos pos = NotificationPos.TOP_RIGHT;
        showNotification(pos, true);        
    }
    
    private void showNotification(NotificationPos pos, boolean error) {
        MFXNotification notification = buildNotification(error);        
        NotificationsManager.send(pos, notification);
    }
    
    private MFXNotification buildNotification(boolean error) {     
        ImageView icon = new ImageView();
        if(error) {
            icon = new ImageView(App.class.getResource("resources/image/notify.png").toExternalForm());
        }else {
            icon = new ImageView(App.class.getResource("resources/image/error.png").toExternalForm());
        }        
        icon.setFitWidth(50); icon.setFitHeight(50);
        Region template = new SimpleMFXNotificationPane(
                icon,
                "Ups! Ha ocurrido algo!",
                title,
                body                
        );
        MFXNotification notification = new MFXNotification(template, true, true);
        notification.setHideAfterDuration(Duration.seconds(2));

        if (template instanceof SimpleMFXNotificationPane) {
            SimpleMFXNotificationPane pane = (SimpleMFXNotificationPane) template;            
            pane.setCloseHandler(closeEvent -> notification.hideNotification());            
        }
        return notification;
    }
    
}

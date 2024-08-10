import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.media.MediaView;
import javafx.stage.Stage;
import javafx.util.Duration;

public class WController implements Initializable {

       private Stage stage;
       private Scene scene;

       @FXML 
       private MediaView mediaView;
       private File file;
       private Media media;
       private MediaPlayer mediaPlayer;

    @FXML
    public void Launch_Browser(ActionEvent e) throws IOException, InterruptedException{
        System.out.println("Launching Browser........");
        Parent root=FXMLLoader.load(getClass().getResource("FXML Files/Scene2.fxml"));
              stage=(Stage)((Node)e.getSource()).getScene().getWindow();
              scene= new Scene(root);
              stage.setScene(scene);
              stage.show();
    }

    @Override
    public void initialize(URL arg0, ResourceBundle arg1) {
        file = new File("D:/Java/Java Project/src/Images/BG1.mp4");
        media = new Media(file.toURI().toString());
        mediaPlayer = new MediaPlayer(media);
        mediaView.setMediaPlayer(mediaPlayer);
        mediaPlayer.setOnEndOfMedia(() -> {
            mediaPlayer.seek(Duration.ZERO);
        });
    
        mediaPlayer.play();
    }  
}

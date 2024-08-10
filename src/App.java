import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

public class App extends Application {
    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage stage) {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("/FXML Files/Scene1.fxml"));
            Scene scene = new Scene(root, 700, 700, Color.PURPLE);
            stage.setScene(scene);
            Image icon = new Image("/Images/Logo.png");
            stage.getIcons().add(icon);
            stage.setTitle("WEB Browser(JAVA Project)");
            stage.setResizable(false);
            stage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}

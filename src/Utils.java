import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.stage.Stage;
import java.io.IOException;
import java.util.function.Consumer;
import java.util.regex.Pattern;

public class Utils {
    private static final Pattern URL_PATTERN = Pattern.compile(
        ".*\\.(com|org|net|edu|gov|mil|int|co|io|in|so|info|biz)$"
    );

    public static <T> T loadScene(String fxmlPath, Stage stage, Consumer<T> callback) {
        try {
            FXMLLoader loader = new FXMLLoader(Utils.class.getResource(fxmlPath));
            Parent root = loader.load();
            T controller = loader.getController();
            if (callback != null) callback.accept(controller);
            stage.setScene(new Scene(root));
            return controller;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static void showAlert(String title, String message) {
        createAlert(Alert.AlertType.WARNING, title, message).showAndWait();
    }
    
    public static void showInfo(String title, String message) {
        createAlert(Alert.AlertType.INFORMATION, title, message).showAndWait();
    }
    
    public static void showError(String title, String message) {
        createAlert(Alert.AlertType.ERROR, title, message).showAndWait();
    }
    
    private static Alert createAlert(Alert.AlertType type, String title, String message) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        return alert;
    }

    public static boolean isURL(String text) {
        if (text == null || text.trim().isEmpty()) return false;
        text = text.toLowerCase();
        return text.startsWith("www.") || text.startsWith("http://") || 
               text.startsWith("https://") || URL_PATTERN.matcher(text).matches();
    }
    
    public static String buildUrl(String text, String searchUrl) {
        if (isURL(text)) {
            return text.startsWith("https://") ? text : "https://" + text;
        }
        return searchUrl + text;
    }
}

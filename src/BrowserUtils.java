import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import java.io.IOException;
import java.util.Arrays;
import java.util.function.Consumer;
import java.util.regex.Pattern;

public class BrowserUtils {
    public enum SearchEngine {
        BING(1, "Bing", "https://www.bing.com/", "https://www.bing.com/search?q="),
        DUCK_DUCK_GO(2, "Duck Duck GO", "https://duckduckgo.com/", "https://duckduckgo.com/?q="),
        GOOGLE(3, "Google", "https://www.google.com", "https://www.google.com/search?q="),
        YAHOO(4, "Yahoo!", "https://www.Yahoo.com/", "https://search.yahoo.com/search?p=");
        
        private final int flag;
        private final String name, homeUrl, searchUrl;
        
        SearchEngine(int flag, String name, String homeUrl, String searchUrl) {
            this.flag = flag;
            this.name = name;
            this.homeUrl = homeUrl;
            this.searchUrl = searchUrl;
        }
        
        public static SearchEngine fromFlag(int flag) {
            return Arrays.stream(values()).filter(e -> e.flag == flag).findFirst().orElse(GOOGLE);
        }
        
        public static SearchEngine fromName(String name) {
            return Arrays.stream(values()).filter(e -> e.name.equals(name)).findFirst().orElse(GOOGLE);
        }
        
        public static String[] getNames() {
            return Arrays.stream(values()).map(SearchEngine::getName).toArray(String[]::new);
        }
        
        public String getHomeUrl() { return homeUrl; }
        public String getSearchUrl() { return searchUrl; }
        public String getName() { return name; }
        public int getFlag() { return flag; }
    }
    
    // ============= UTILITY METHODS =============
    private static final Pattern URL_PATTERN = Pattern.compile(
        ".*\\.(com|org|net|edu|gov|mil|int|co|io|in|so|info|biz)$"
    );

    public static <T> T loadScene(String fxmlPath, Stage stage, Consumer<T> callback) {
        try {
            FXMLLoader loader = new FXMLLoader(BrowserUtils.class.getResource(fxmlPath));
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

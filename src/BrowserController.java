import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Worker;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.VBox;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebHistory;
import javafx.scene.web.WebView;
import javafx.stage.Stage;
import java.io.IOException;
import java.net.URL;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

public class BrowserController implements Initializable {
    @FXML private Label Greet, TodayDate;
    @FXML private ChoiceBox<String> cho;
    @FXML private WebView webView;
    @FXML private TextField textField;
    @FXML private Button Back, forward_Button, reload_Button, dashboardBtn, homeBtn;
    @FXML private Button history, clearHistoryBtn, bookmarkBtn, viewFullHistoryBtn;
    @FXML private Button manageBookmarksBtn, addBookmarkBtn, userStatsBtn, gb;
    @FXML private Label userLabel, statusLabel;
    @FXML private SplitPane mainSplitPane;
    @FXML private VBox sidePanel;
    @FXML private ListView<String> historyList, bookmarksList;
    private WebEngine engine;
    private SearchEngine selectedEngine;
    private Stage stage;
    private String username;
    private UserManager userManager;
    private boolean dashboardVisible = false;
    private List<String> userBookmarks = new ArrayList<>();
    private ObservableList<String> historyObservableList;
    private ObservableList<String> bookmarksObservableList;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        if (cho != null) {
            cho.getItems().addAll(SearchEngine.getNames());
            cho.setOnAction(this::User_Selected);
        }
        initializeWebView();
    }

    public void initializeAsHomePage(String username) {
        this.username = username;
        displayGreeting();
        updateDateTime();
    }
    
    public void initializeAsBrowser(int flag, Stage stage, Scene scene, String username) {
        this.selectedEngine = SearchEngine.fromFlag(flag);
        this.stage = stage;
        this.username = username;
        initializeWebView();
        updateUserInterface();
    }
    
    public void setUserManager(UserManager userManager) {
        this.userManager = userManager;
        if (username != null && cho != null) {
            String defaultEngine = userManager.getDefaultSearchEngine(username);
            cho.setValue(defaultEngine);
            selectedEngine = SearchEngine.fromName(defaultEngine);
        }
        if (username != null) {
            List<String> savedBookmarks = userManager.getUserBookmarks(username);
            userBookmarks.clear();
            userBookmarks.addAll(savedBookmarks);
            if (bookmarksObservableList != null) {
                bookmarksObservableList.setAll(userBookmarks);
            }
        }
    }

    private void displayGreeting() {
        if (Greet != null && username != null) {
            Greet.setText("Good " + getTimeOfDay() + " " + username);
        }
    }
    
    private void updateDateTime() {
        if (TodayDate != null) {
            LocalDate currentDate = LocalDate.now();
            TodayDate.setText("It's " + currentDate.getDayOfWeek() + " " + 
                             currentDate.format(DateTimeFormatter.ofPattern("dd-MM-yyyy")));
        }
    }

    @FXML
    public void User_Selected(ActionEvent event) {
        String engineName = cho.getValue();
        if (engineName != null) {
            selectedEngine = SearchEngine.fromName(engineName);
            // Selection is now visible directly in the ChoiceBox
            if (userManager != null && username != null) {
                userManager.setDefaultSearchEngine(username, engineName);
            }
        }
    }

    @FXML
    public void go_WEB(ActionEvent event) throws IOException {
        if (selectedEngine == null) {
            // Show alert or highlight the ChoiceBox instead of using label
            return;
        }
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        Utils.loadScene("/FXML Files/Web_Page.fxml", stage, controller -> {
            BrowserController webController = (BrowserController) controller;
            webController.initializeAsBrowser(selectedEngine.getFlag(), stage, stage.getScene(), username);
            webController.setUserManager(userManager);
        });
    }
    
    @FXML
    public void go_Back(ActionEvent event) throws IOException {
        if (engine != null) {
            WebHistory history = engine.getHistory();
            ObservableList<WebHistory.Entry> entries = history.getEntries();
            if (!entries.isEmpty() && history.getCurrentIndex() > 0) {
                history.go(-1);
                return;
            }
        }
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        Utils.loadScene("/FXML Files/Scene2.fxml", stage, null);
    }
    
    @FXML
    private void initializeWebView() {
        if (webView == null) return;
        engine = webView.getEngine();
        
        setupEventHandlers();
        loadHomePage();
        initializeDashboard();
        if (sidePanel != null) {
            dashboardVisible = false;
            sidePanel.setVisible(false);
            sidePanel.setManaged(false);
            if (mainSplitPane != null) mainSplitPane.setDividerPositions(1.0);
        }
    }
    
    private void updateUserInterface() {
        if (userLabel != null && username != null) userLabel.setText("User: " + username);
        if (statusLabel != null) statusLabel.setText("Ready");
    }
    
    private void initializeDashboard() {
        if (historyList != null && historyObservableList == null) {
            historyObservableList = FXCollections.observableArrayList();
            historyList.setItems(historyObservableList);
            historyObservableList.add("Welcome to your browsing history!");
            historyObservableList.add("Visit websites to see them here");
        }
        if (bookmarksList != null && bookmarksObservableList == null) {
            bookmarksObservableList = FXCollections.observableArrayList();
            bookmarksList.setItems(bookmarksObservableList);
            if (userManager != null && username != null) {
                List<String> savedBookmarks = userManager.getUserBookmarks(username);
                userBookmarks.clear();
                userBookmarks.addAll(savedBookmarks);
                bookmarksObservableList.setAll(userBookmarks);
            } else {
                if (userBookmarks.isEmpty()) {
                    userBookmarks.add("Google - https://www.google.com");
                    userBookmarks.add("GitHub - https://github.com");
                    userBookmarks.add("Stack Overflow - https://stackoverflow.com");
                    bookmarksObservableList.setAll(userBookmarks);
                }
            }
        }
        updateDashboardHistory();
    }

    private void setupEventHandlers() {
        if (textField != null) {
            textField.setOnKeyPressed(e -> {
                if (e.getCode() == KeyCode.ENTER) loadPage();
            });
        }
        if (engine != null) {
            engine.getLoadWorker().progressProperty().addListener((obs, oldProgress, newProgress) -> {
                String progressStr = String.format("%.1f%%", newProgress.doubleValue() * 100);
                Platform.runLater(() -> {
                    if (statusLabel != null) statusLabel.setText("Loading: " + progressStr);
                });
            });
            engine.getLoadWorker().stateProperty().addListener((obs, oldState, newState) -> {
                if (newState == Worker.State.SUCCEEDED) {
                    Platform.runLater(() -> {
                        if (statusLabel != null) statusLabel.setText("SUCCEEDED");
                    });
                    updateUrlField();
                } else if (newState == Worker.State.FAILED) {
                    Platform.runLater(() -> {
                        if (statusLabel != null) statusLabel.setText("FAILED");
                    });
                    loadOfflineHtml();
                }
            });
        }
    }

    private void updateUrlField() {
        if (engine == null || textField == null) return;
        WebHistory history = engine.getHistory();
        ObservableList<WebHistory.Entry> entries = history.getEntries();
        if (!entries.isEmpty()) {
            String currentUrl = entries.get(history.getCurrentIndex()).getUrl();
            textField.setText(currentUrl);
            if (userManager != null && username != null && !currentUrl.startsWith("data:")) {
                userManager.addToHistory(username, currentUrl);
                if (dashboardVisible) updateDashboardHistory();
            }
        }
    }

    private void loadHomePage() {
        if (engine != null && selectedEngine != null) {
            engine.load(selectedEngine.getHomeUrl());
        }
    }

    @FXML
    public void loadoage_Button() { loadPage(); }

    private void loadPage() {
        if (textField == null || engine == null) return;
        String text = textField.getText().trim();
        if (text.isEmpty()) return;
        String url = Utils.buildUrl(text, selectedEngine.getSearchUrl());
        engine.load(url);
    }

    @FXML
    public void Reload() {
        if (engine != null) {
            engine.reload();
        }
    }

    @FXML
    public void go_Back() {
        if (engine == null) return;
        WebHistory history = engine.getHistory();
        ObservableList<WebHistory.Entry> entries = history.getEntries();
        if (!entries.isEmpty() && history.getCurrentIndex() > 0) {
            history.go(-1);
            updateUrlField();
        }
    }

    @FXML
    public void go_Forward() {
        if (engine == null) return;
        WebHistory history = engine.getHistory();
        ObservableList<WebHistory.Entry> entries = history.getEntries();
        if (!entries.isEmpty() && history.getCurrentIndex() < entries.size() - 1) {
            history.go(1);
            updateUrlField();
        }
    }

    @FXML
    public void display_History() {
        if (userManager != null && username != null) {
            List<String> userHistory = userManager.getUserHistory(username);
            String historyText = userHistory.isEmpty() ? "No browsing history found." : 
                "=== Browsing History for " + username + " ===\n" + String.join("\n", userHistory);
            Utils.showInfo("Browsing History", historyText);
            updateStatus("Displayed history for " + username);
        } else if (engine != null) {
            WebHistory history = engine.getHistory();
            StringBuilder historyText = new StringBuilder("=== Current Session History ===\n");
            history.getEntries().forEach(entry -> 
                historyText.append(entry.getLastVisitedDate()).append(" - ").append(entry.getUrl()).append("\n"));
            Utils.showInfo("Session History", historyText.toString());
            updateStatus("Displayed session history");
        }
    }

    private void loadOfflineHtml() {
        if (engine != null) {
            String offlineHtml = """
                <!DOCTYPE html>
                <html><head><title>Offline</title></head>
                <body style="font-family: Arial; text-align: center; margin-top: 100px;">
                    <h1 style="color: #872cd1;">You're offline</h1>
                    <h2>You can try reloading the page</h2>
                    <h3>Or try the game</h3>
                </body></html>""";
            engine.loadContent(offlineHtml);
        }
    }

    @FXML
    public void loadGame() {
    }
    
    @FXML
    public void toggleDashboard() {
        dashboardVisible = !dashboardVisible;
        if (sidePanel != null && mainSplitPane != null) {
            sidePanel.setVisible(dashboardVisible);
            sidePanel.setManaged(dashboardVisible);
            
            if (dashboardVisible) {
                mainSplitPane.setDividerPositions(0.75);
                updateDashboardHistory();
                if (statusLabel != null) {
                    statusLabel.setText("Dashboard opened");
                }
            } else {
                mainSplitPane.setDividerPositions(1.0);
                if (statusLabel != null) {
                    statusLabel.setText("Dashboard closed");
                }
            }
        }
    }
    
    @FXML
    public void goHome() {
        try {
            Utils.loadScene("FXML Files/Scene4.fxml", stage, controller -> {
                if (controller instanceof BrowserController) {
                    ((BrowserController) controller).initializeAsHomePage(username);
                    ((BrowserController) controller).setUserManager(userManager);
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    @FXML
    public void clearHistory() {
        if (userManager != null && username != null) {
            userManager.clearUserHistory(username);
            updateDashboardHistory();
            updateStatus("History cleared successfully");
        }
    }
    
    @FXML
    public void addBookmark() {
        if (engine != null && userManager != null && username != null) {
            String currentUrl = engine.getLocation();
            String title = engine.getTitle();
            if (currentUrl != null && !currentUrl.isEmpty() && !currentUrl.startsWith("data:")) {
                String bookmarkTitle = (title != null && !title.isEmpty()) ? title : "Untitled";
                String bookmarkEntry = bookmarkTitle + " - " + currentUrl;
                boolean alreadyExists = userBookmarks.stream()
                    .anyMatch(bookmark -> bookmark.contains(currentUrl));
                if (!alreadyExists) {
                    userManager.addBookmark(username, bookmarkTitle, currentUrl);
                    userBookmarks.add(bookmarkEntry);
                    if (bookmarksList != null && bookmarksObservableList != null) {
                        bookmarksObservableList.setAll(userBookmarks);
                    }
                    updateStatus("Bookmark added: " + bookmarkTitle);
                } else {
                    updateStatus("Bookmark already exists");
                }
            } else {
                updateStatus("Cannot bookmark this page");
            }
        }
    }
    
    @FXML
    public void showFullHistory() {
        if (userManager != null && username != null) {
            List<String> fullHistory = userManager.getUserHistory(username);
            Utils.showInfo("Full Browsing History", 
                     fullHistory.isEmpty() ? "No browsing history found." : String.join("\n", fullHistory));
        }
    }
    
    @FXML
    public void manageBookmarks() {
        if (userManager != null && username != null) {
            List<String> bookmarks = userManager.getUserBookmarks(username);
            Utils.showInfo("Bookmarks", 
                     bookmarks.isEmpty() ? "No bookmarks saved." : String.join("\n", bookmarks));
        } else {
            Utils.showInfo("Bookmarks", 
                     userBookmarks.isEmpty() ? "No bookmarks saved." : String.join("\n", userBookmarks));
        }
    }
    
    @FXML
    public void showUserStats() {
        if (userManager != null && username != null) {
            List<String> history = userManager.getUserHistory(username);
            Utils.showInfo("User Statistics", String.format(
                "User: %s\nTotal pages visited: %d\nBookmarks saved: %d",
                username, history.size(), userBookmarks.size()));
        }
    }

    @FXML
    public void showUserInfo(ActionEvent event) {
        if (userManager != null && username != null) {
            List<String> history = userManager.getUserHistory(username);
            Utils.showInfo("User Information", String.format(
                "User: %s\nTotal pages visited: %d", username, history.size()));
        }
    }
    
    @FXML
    public void showHistory(ActionEvent event) {
        if (userManager != null && username != null) {
            List<String> history = userManager.getUserHistory(username);
            Utils.showInfo("Browsing History", history.isEmpty() ? 
                "No browsing history found." : String.join("\n", history));
        }
    }

    @FXML
    public void wAPP() {
    }
    
    private void updateDashboardHistory() {
        if (historyObservableList != null && userManager != null && username != null) {
            List<String> history = userManager.getUserHistory(username);
            Platform.runLater(() -> {
                historyObservableList.clear();
                int startIndex = Math.max(0, history.size() - 20);
                historyObservableList.addAll(history.subList(startIndex, history.size()));
            });
        }
    }
    
    private void updateStatus(String message) {
        if (statusLabel != null) statusLabel.setText(message);
    }

    private static String getTimeOfDay() {
        int hour = LocalTime.now().getHour();
        return hour < 12 ? "Morning" : hour < 17 ? "Afternoon" : "Evening";
    }
}
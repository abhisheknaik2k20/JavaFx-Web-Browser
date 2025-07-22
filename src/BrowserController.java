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
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
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
    @FXML private Button newTabBtn, closeTabBtn; // New buttons for tab management
    @FXML private Button newTabBtnDash, closeTabBtnDash; // Dashboard tab management buttons
    @FXML private Label userLabel, statusLabel, tabCountLabel;
    @FXML private SplitPane mainSplitPane;
    @FXML private VBox sidePanel;
    @FXML private ListView<String> historyList, bookmarksList;
    @FXML private TabPane tabPane; // New TabPane for multiple tabs
    @FXML private HBox tabButtonsContainer; // Container for tab management buttons
    @FXML private Tab initialTab; // Initial tab reference
    
    // Tab management fields
    private List<WebEngine> tabs = new ArrayList<>();
    private List<WebView> webViews = new ArrayList<>();
    private Map<Tab, WebEngine> tabEngineMap = new HashMap<>();
    private Map<Tab, WebView> tabWebViewMap = new HashMap<>();
    private int tabCounter = 1;
    
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
        
        // Initialize WebView first, then TabPane to ensure proper integration
        initializeWebView();
        initializeTabPane();
    }

    private void initializeTabPane() {
        if (tabPane == null) return;
        
        // Check if there's already an initial tab from FXML
        if (initialTab != null && webView != null) {
            // Register the existing initial tab
            WebEngine engine = webView.getEngine();
            tabEngineMap.put(initialTab, engine);
            tabWebViewMap.put(initialTab, webView);
            tabs.add(engine);
            webViews.add(webView);
            
            // Setup event handlers for the initial engine
            setupEventHandlers(engine);
            
            // Make the initial tab closable if it's not the only tab
            initialTab.setClosable(false); // Keep first tab non-closable
            
            // Set up close listener for initial tab
            initialTab.setOnClosed(e -> {
                tabs.remove(engine);
                webViews.remove(webView);
                tabEngineMap.remove(initialTab);
                tabWebViewMap.remove(initialTab);
                updateTabCount();
            });
        } else {
            // Create the first tab if no initial tab exists
            createNewTab("Home", true);
        }
        
        // Add listener for tab selection changes
        tabPane.getSelectionModel().selectedItemProperty().addListener((obs, oldTab, newTab) -> {
            if (newTab != null) {
                switchToTab(newTab);
            }
        });
        
        // Initialize tab count
        updateTabCount();
    }

    @FXML
    private void initializeWebView() {
        if (webView == null) return;
        WebEngine engine = webView.getEngine();
        
        // Only add to tabs list if not already added by initializeTabPane
        if (!tabs.contains(engine)) {
            tabs.add(engine);
            setupEventHandlers(engine);
        }
        
        // Load home page
        loadHomePage(engine);
        initializeDashboard();
        
        if (sidePanel != null) {
            dashboardVisible = false;
            sidePanel.setVisible(false);
            sidePanel.setManaged(false);
            if (mainSplitPane != null) mainSplitPane.setDividerPositions(1.0);
        }
    }

    // Create a new tab
    @FXML
    public void createNewTab() {
        if (tabPane != null) {
            createNewTab("New Tab", false);
        } else {
            updateStatus("Tab management not available in this view");
        }
    }

    private void createNewTab(String title, boolean isHome) {
        if (tabPane == null) return;
        
        // If this is for home and we already have an initial tab, don't create another
        if (isHome && initialTab != null && tabPane.getTabs().contains(initialTab)) {
            tabPane.getSelectionModel().select(initialTab);
            return;
        }
        
        // Create new WebView and WebEngine
        WebView newWebView = new WebView();
        WebEngine newEngine = newWebView.getEngine();
        
        // Create new tab
        Tab newTab = new Tab(title);
        newTab.setContent(newWebView);
        
        // Make tab closable (except for home tabs)
        newTab.setClosable(!isHome || tabPane.getTabs().size() > 0);
        
        // Store references
        tabEngineMap.put(newTab, newEngine);
        tabWebViewMap.put(newTab, newWebView);
        tabs.add(newEngine);
        webViews.add(newWebView);
        
        // Setup event handlers for the new engine
        setupEventHandlers(newEngine);
        
        // Add tab change listener
        newTab.setOnClosed(e -> {
            // Clean up when tab is closed
            tabs.remove(newEngine);
            webViews.remove(newWebView);
            tabEngineMap.remove(newTab);
            tabWebViewMap.remove(newTab);
            updateTabCount(); // Update tab count on close
        });
        
        // Add to TabPane and select it
        tabPane.getTabs().add(newTab);
        tabPane.getSelectionModel().select(newTab);
        
        // Load home page for new tab
        if (selectedEngine != null) {
            newEngine.load(selectedEngine.getHomeUrl());
        }
        
        tabCounter++;
        updateTabCount();
        updateStatus("Created new tab");
    }

    // Close current tab
    @FXML
    public void closeCurrentTab() {
        if (tabPane == null) {
            updateStatus("Tab management not available in this view");
            return;
        }
        
        Tab selectedTab = tabPane.getSelectionModel().getSelectedItem();
        if (selectedTab != null && selectedTab.isClosable()) {
            tabPane.getTabs().remove(selectedTab);
            updateTabCount();
            updateStatus("Closed tab");
            
            // If no tabs left, create a new one
            if (tabPane.getTabs().isEmpty()) {
                createNewTab("Home", true);
            }
        } else if (selectedTab != null && !selectedTab.isClosable()) {
            updateStatus("Cannot close this tab");
        }
    }

    // Switch to a specific tab
    private void switchToTab(Tab tab) {
        WebEngine engine = tabEngineMap.get(tab);
        WebView view = tabWebViewMap.get(tab);
        
        if (engine != null && view != null) {
            // Update current references
            webView = view;
            
            // Update URL field with current tab's URL
            updateUrlField(engine);
            updateStatus("Switched to tab: " + tab.getText());
        } else if (tab == initialTab && initialTab != null) {
            // Handle switching to initial tab
            WebEngine initialEngine = initialTab.getContent() instanceof WebView ? 
                ((WebView) initialTab.getContent()).getEngine() : null;
            if (initialEngine != null) {
                webView = (WebView) initialTab.getContent();
                updateUrlField(initialEngine);
                updateStatus("Switched to tab: " + tab.getText());
            }
        }
    }

    // Get current active engine
    private WebEngine getCurrentEngine() {
        if (tabPane != null) {
            Tab selectedTab = tabPane.getSelectionModel().getSelectedItem();
            if (selectedTab != null) {
                WebEngine engine = tabEngineMap.get(selectedTab);
                if (engine != null) {
                    return engine;
                }
            }
        }
        // Fallback to the default webView engine if tabPane is not available
        return webView != null ? webView.getEngine() : null;
    }

    // Get current active WebView
    private WebView getCurrentWebView() {
        if (tabPane != null) {
            Tab selectedTab = tabPane.getSelectionModel().getSelectedItem();
            if (selectedTab != null) {
                WebView view = tabWebViewMap.get(selectedTab);
                if (view != null) {
                    return view;
                }
            }
        }
        // Fallback to the default webView if tabPane is not available
        return webView;
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
        ensureProperInitialization();
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
            if (userManager != null && username != null) {
                userManager.setDefaultSearchEngine(username, engineName);
            }
        }
    }

    @FXML
    public void go_WEB(ActionEvent event) throws IOException {
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        Utils.loadScene("/FXML Files/Web_Page.fxml", stage, controller -> {
            BrowserController webController = (BrowserController) controller;
            webController.initializeAsBrowser(selectedEngine.getFlag(), stage, stage.getScene(), username);
            webController.setUserManager(userManager);
        });
    }
    
    @FXML
    public void go_Back(ActionEvent event) throws IOException {
        WebEngine engine = getCurrentEngine();
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
    
    private void updateUserInterface() {
        if (userLabel != null && username != null) userLabel.setText("User: " + username);
        if (statusLabel != null) statusLabel.setText("Ready");
        updateTabCount(); // Update tab count on UI
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
                    bookmarksObservableList.setAll(userBookmarks);
                }
            }
        }
        updateDashboardHistory();
    }

    private void setupEventHandlers(WebEngine engine) {
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
                        updateTabTitle(engine);
                    });
                    updateUrlField(engine);
                } else if (newState == Worker.State.FAILED) {
                    Platform.runLater(() -> {
                        if (statusLabel != null) statusLabel.setText("FAILED");
                    });
                    loadOfflineHtml(engine);
                }
            });
        }
    }

    private void updateTabTitle(WebEngine engine) {
        // Find the tab associated with this engine and update its title
        for (Map.Entry<Tab, WebEngine> entry : tabEngineMap.entrySet()) {
            if (entry.getValue() == engine) {
                Tab tab = entry.getKey();
                String title = engine.getTitle();
                if (title != null && !title.isEmpty()) {
                    // Limit title length for display
                    String displayTitle = title.length() > 20 ? title.substring(0, 17) + "..." : title;
                    Platform.runLater(() -> tab.setText(displayTitle));
                }
                break;
            }
        }
    }

    private void updateUrlField(WebEngine engine) {
        if (engine == null || textField == null) return;
        
        // Only update if this is the current active engine
        WebEngine currentEngine = getCurrentEngine();
        if (engine != currentEngine) return;
        
        try {
            WebHistory history = engine.getHistory();
            ObservableList<WebHistory.Entry> entries = history.getEntries();
            if (!entries.isEmpty()) {
                String currentUrl = entries.get(history.getCurrentIndex()).getUrl();
                Platform.runLater(() -> textField.setText(currentUrl));
                
                if (userManager != null && username != null && !currentUrl.startsWith("data:")) {
                    userManager.addToHistory(username, currentUrl);
                    if (dashboardVisible) updateDashboardHistory();
                }
            }
        } catch (Exception e) {
            System.err.println("Error updating URL field: " + e.getMessage());
        }
    }

    private void loadHomePage(WebEngine engine) {
        if (engine != null && selectedEngine != null) {
            engine.load(selectedEngine.getHomeUrl());
        }
    }

    @FXML
    public void loadoage_Button() { loadPage(); }

    private void loadPage() {
        if (textField == null) return;
        WebEngine engine = getCurrentEngine();
        if (engine == null) return;
        
        String text = textField.getText().trim();
        if (text.isEmpty()) return;
        String url = Utils.buildUrl(text, selectedEngine.getSearchUrl());
        engine.load(url);
    }

    @FXML
    public void Reload() {
        WebEngine engine = getCurrentEngine();
        if (engine != null) {
            engine.reload();
        }
    }

    @FXML
    public void go_Back() {
        WebEngine engine = getCurrentEngine();
        if (engine == null) return;
        WebHistory history = engine.getHistory();
        ObservableList<WebHistory.Entry> entries = history.getEntries();
        if (!entries.isEmpty() && history.getCurrentIndex() > 0) {
            history.go(-1);
            updateUrlField(engine);
        }
    }

    @FXML
    public void go_Forward() {
        WebEngine engine = getCurrentEngine();
        if (engine == null) return;
        WebHistory history = engine.getHistory();
        ObservableList<WebHistory.Entry> entries = history.getEntries();
        if (!entries.isEmpty() && history.getCurrentIndex() < entries.size() - 1) {
            history.go(1);
            updateUrlField(engine);
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
        } else {
            WebEngine engine = getCurrentEngine();
            if (engine != null) {
                WebHistory history = engine.getHistory();
                StringBuilder historyText = new StringBuilder("=== Current Session History ===\n");
                history.getEntries().forEach(entry -> 
                    historyText.append(entry.getLastVisitedDate()).append(" - ").append(entry.getUrl()).append("\n"));
                Utils.showInfo("Session History", historyText.toString());
                updateStatus("Displayed session history");
            }
        }
    }

    private void loadOfflineHtml(WebEngine engine) {
        if (engine != null) {
            try {
                String offlineFilePath = getClass().getResource("/FXML Files/Offline.html").toExternalForm();
                engine.load(offlineFilePath);
            } catch (Exception e) {
                e.printStackTrace();
            }
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
        WebEngine engine = getCurrentEngine();
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
            int tabCount = (tabPane != null) ? tabPane.getTabs().size() : 1;
            Utils.showInfo("User Statistics", String.format(
                "User: %s\nTotal pages visited: %d\nBookmarks saved: %d\nOpen tabs: %d",
                username, history.size(), userBookmarks.size(), tabCount));
        }
    }

    @FXML
    public void showUserInfo(ActionEvent event) {
        if (userManager != null && username != null) {
            List<String> history = userManager.getUserHistory(username);
            int tabCount = (tabPane != null) ? tabPane.getTabs().size() : 1;
            Utils.showInfo("User Information", String.format(
                "User: %s\nTotal pages visited: %d\nOpen tabs: %d", 
                username, history.size(), tabCount));
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

    private void updateTabCount() {
        if (tabCountLabel != null && tabPane != null) {
            int tabCount = tabPane.getTabs().size();
            tabCountLabel.setText("Tabs: " + tabCount);
        } else if (tabCountLabel != null) {
            // Fallback when tabPane is not available
            tabCountLabel.setText("Tabs: 1");
        }
    }

    private static String getTimeOfDay() {
        int hour = LocalTime.now().getHour();
        return hour < 12 ? "Morning" : hour < 17 ? "Afternoon" : "Evening";
    }

    // Method to ensure proper tab initialization after all components are loaded
    public void ensureProperInitialization() {
        Platform.runLater(() -> {
            try {
                if (tabPane != null && tabPane.getTabs().isEmpty()) {
                    createNewTab("Home", true);
                }
                updateTabCount();
                
                // Ensure the initial tab is properly selected
                if (tabPane != null && initialTab != null && tabPane.getTabs().contains(initialTab)) {
                    tabPane.getSelectionModel().select(initialTab);
                    switchToTab(initialTab);
                }
            } catch (Exception e) {
                System.err.println("Error during initialization: " + e.getMessage());
                e.printStackTrace();
            }
        });
    }
}
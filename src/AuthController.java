import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.stage.Stage;
import java.util.List;

public class AuthController {
    @FXML private Label statusLabel, messageLabel,nameLabel;
    @FXML private ComboBox<String> userComboBox;
    @FXML private TextField Field;
    @FXML private PasswordField passwordField, passwordfld; 
    @FXML private Label incorrect;
    @FXML private TextField Username;
    @FXML private PasswordField OGPass, ROGPass;
    @FXML private Label PassStrength;
    private final UserManager userManager = new UserManager();
    private Stage stage;
    private Scene scene;
    private String selectedUsername;
    
    public void initialize() {
        setupEventHandlers();
        loadUsers();
    }
    public void initKeyActions(Scene scene, Stage stage, Object root) {
        this.stage = stage;
        this.scene = scene;
    }
    public void display_Name(String username, Stage stage, Scene scene) {
        this.selectedUsername = username;
        this.stage = stage;
        this.scene = scene;
        if (nameLabel != null) nameLabel.setText(username);
        if (messageLabel != null) messageLabel.setText("Enter password for " + username);
        clearErrorMessage();
    }
    
    private void setupEventHandlers() {
        if (Username != null) {
            Username.setOnKeyPressed(e -> {
                if (e.getCode() == KeyCode.ENTER) OGPass.requestFocus();
            });
            OGPass.setOnKeyPressed(e -> {
                if (e.getCode() == KeyCode.ENTER) ROGPass.requestFocus();
            });
            ROGPass.setOnKeyPressed(e -> {
                if (e.getCode() == KeyCode.ENTER) handleUserCreation();
            });   
        }
        if (passwordField != null) {
            passwordField.setOnKeyPressed(e -> {
                if (e.getCode() == KeyCode.ENTER) handleLogin();
            });
            passwordField.setOnAction(e -> handleLogin());
            passwordField.textProperty().addListener((obs, oldText, newText) -> {
                if (!newText.isEmpty()) clearErrorMessage();
            });
        }
        if (passwordfld != null) {
            passwordfld.setOnKeyPressed(e -> {
                if (e.getCode() == KeyCode.ENTER) handleLogin();
            });
            passwordfld.setOnAction(e -> handleLogin());
            passwordfld.textProperty().addListener((obs, oldText, newText) -> {
                if (!newText.isEmpty()) clearErrorMessage();
            });
        }
        if (userComboBox != null) userComboBox.setOnAction(this::onUserSelected);
    }
    
    private void loadUsers() {
        if (userComboBox != null) {
            List<String> usernames = userManager.getAllUsernames();
            userComboBox.getItems().clear();
            userComboBox.getItems().addAll(usernames);
            userComboBox.getItems().add("Create New User");
            if (!usernames.isEmpty()) userComboBox.setValue(usernames.get(0));
            updateStatusLabel();
        }
    }
    
    @FXML
    private void onUserSelected(ActionEvent event) {
        updateStatusLabel();
    }
    
    private void updateStatusLabel() {
        if (statusLabel != null && userComboBox != null) {
            String selected = userComboBox.getValue();
            if (selected != null) {
                statusLabel.setText(selected.equals("Create New User") ? 
                    "Ready to create a new user account" : "Selected user: " + selected);
            }
        }
    }
    
    @FXML
    public void handleUserCreation() {
        String username = getValue(Username);
        String password = getValue(OGPass);
        String confirmPassword = getValue(ROGPass);
        
        if (!validateInput(username, password, confirmPassword)) return;
        
        if (userManager.registerUser(username, password)) {
            Utils.showInfo("Account Created", "Your account has been created successfully!");
            navigateToProfile();
        } else {
            Utils.showAlert("Registration Failed", "Failed to create account. Please try again.");
        }
    }
    
    private boolean validateInput(String username, String password, String confirmPassword) {
        if (username.isEmpty()) {
            Utils.showAlert("Username Required", "Please enter a username.");
            return false;
        }
        if (username.length() < 3) {
            Utils.showAlert("Username Too Short", "Username must be at least 3 characters long.");
            return false;
        }
        if (userManager.getAllUsernames().contains(username)) {
            Utils.showAlert("Username Exists", "This username is already taken.");
            return false;
        }
        if (password.isEmpty() || password.length() < 6) {
            Utils.showAlert("Weak Password", "Password must be at least 6 characters long.");
            return false;
        }
        if (!password.equals(confirmPassword)) {
            Utils.showAlert("Password Mismatch", "Passwords do not match.");
            return false;
        }
        return true;
    }
    @FXML
    public void handleLogin() {
        String username = selectedUsername != null ? selectedUsername : 
                         (userComboBox != null ? userComboBox.getValue() : "");
        String password = passwordfld != null ? getValue(passwordfld) : getValue(passwordField);
        if (password.isEmpty()) {
            showErrorMessage("Please enter your password");
            return;
        }
        System.out.println("Available users: " + userManager.getAllUsernames());
        boolean authResult = userManager.authenticateUser(username, password);
        if (authResult) {
            clearErrorMessage();
            navigateToHome(username);
        } else {
            showErrorMessage("Incorrect password. Please try again.");
            if (passwordfld != null) {
                passwordfld.clear();
                passwordfld.requestFocus();
            } else if (passwordField != null) {
                passwordField.clear();
                passwordField.requestFocus();
            }
        }
    }

    @FXML
    public void User_Selected(ActionEvent event) {
        String selectedUser = userComboBox.getValue();
        
        if (selectedUser == null) {
            Utils.showAlert("Selection Required", "Please select a user or choose to create a new one.");
            return;
        }
        
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        
        if (selectedUser.equals("Create New User")) {
            Utils.loadScene("/FXML Files/Example.fxml", stage, controller -> {
                AuthController authController = (AuthController) controller;
                authController.initKeyActions(stage.getScene(), stage, null);
            });
        } else {
            Utils.loadScene("/FXML Files/Scene3.fxml", stage, controller -> {
                AuthController authController = (AuthController) controller;
                authController.display_Name(selectedUser, stage, stage.getScene());
            });
        }
    }
    
    @FXML
    public void refreshUsers(ActionEvent event) {
        loadUsers();
        if (statusLabel != null) statusLabel.setText("User list refreshed");
    }
    
    @FXML
    public void deleteUser(ActionEvent event) {
        String selectedUser = userComboBox.getValue();
        
        if (selectedUser == null || selectedUser.equals("Create New User")) {
            Utils.showAlert("Selection Required", "Please select a user to delete.");
            return;
        }
        Alert confirmAlert = new Alert(Alert.AlertType.CONFIRMATION);
        confirmAlert.setTitle("Confirm Deletion");
        confirmAlert.setContentText("Are you sure you want to delete '" + selectedUser + "'?");
        confirmAlert.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                if (userManager.deleteUser(selectedUser)) {
                    Utils.showInfo("User Deleted", "User account deleted successfully.");
                    loadUsers();
                } else Utils.showAlert("Deletion Failed", "Failed to delete user account.");
            }
        });
    }
    
    public void update_Page(String username) {
        loadUsers();
        if (userComboBox != null) userComboBox.setValue(username);
        if (statusLabel != null) statusLabel.setText("New user '" + username + "' created successfully!");
    }

    @FXML
    public void go_Back(ActionEvent event) {
        try {
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            Utils.loadScene("FXML Files/Scene1.fxml", stage, controller -> {});
        } catch (Exception e) {
            Utils.showError("Navigation Error", "Failed to navigate back.");
        }
    }
    
    private void navigateToProfile() {
        Utils.loadScene("/FXML Files/Scene2.fxml", stage, null);
    }
    
    private void navigateToHome(String username) {
        Utils.loadScene("/FXML Files/Scene4.fxml", stage, controller -> {
            BrowserController homeController = (BrowserController) controller;
            homeController.initializeAsHomePage(username);
            homeController.setUserManager(userManager);
        });
    }

    private String getValue(TextField field) {
        return field != null ? field.getText().trim() : "";
    }
    
    private String getValue(PasswordField field) {
        return field != null ? field.getText().trim() : "";
    }
    
    private void showMessage(String message) {
        if (messageLabel != null) {
            messageLabel.setText(message);
            messageLabel.setVisible(true);
        }
    }
    
    private void showErrorMessage(String message) {
        if (incorrect != null) {
            incorrect.setText(message);
            incorrect.setVisible(true);
        } else if (messageLabel != null) {
            messageLabel.setText(message);
            messageLabel.setVisible(true);
        } else {
            Utils.showAlert("Login Error", message);
        }
    }

    private void clearErrorMessage() {
        if (incorrect != null) {
            incorrect.setText("");
            incorrect.setVisible(false);
        }
        if (messageLabel != null) {
            messageLabel.setText("");
            messageLabel.setVisible(false);
        }
    }
}

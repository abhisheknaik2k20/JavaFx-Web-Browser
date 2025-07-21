import java.io.*;
import java.nio.file.*;
import java.security.MessageDigest;
import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

public class UserManager {
    public static class User implements Serializable {
        private static final long serialVersionUID = 1L;
        private String username, passwordHash, avatarPath = "/Images/Avatar-1.png";
        private String defaultSearchEngine = "Google", createdDate = LocalDateTime.now().toString();
        private List<String> browsingHistory = new ArrayList<>();
        
        public User() {}
        public User(String username, String passwordHash) { this.username = username; this.passwordHash = passwordHash; }
        
        // Getters
        public String getUsername() { return username; }
        public String getPasswordHash() { return passwordHash; }
        public String getAvatarPath() { return avatarPath; }
        public String getDefaultSearchEngine() { return defaultSearchEngine; }
        public List<String> getBrowsingHistory() { return browsingHistory; }
        public String getCreatedDate() { return createdDate; }
        
        // Setters
        public void setUsername(String username) { this.username = username; }
        public void setPasswordHash(String passwordHash) { this.passwordHash = passwordHash; }
        public void setAvatarPath(String avatarPath) { this.avatarPath = avatarPath; }
        public void setDefaultSearchEngine(String defaultSearchEngine) { this.defaultSearchEngine = defaultSearchEngine; }
        public void setBrowsingHistory(List<String> browsingHistory) { this.browsingHistory = browsingHistory; }
        public void setCreatedDate(String createdDate) { this.createdDate = createdDate; }
        
        public void addToHistory(String url) {
            if (!browsingHistory.isEmpty() && browsingHistory.get(browsingHistory.size() - 1).startsWith(url)) return;
            if (browsingHistory.size() >= 100) browsingHistory.remove(0);
            browsingHistory.add(url + " - " + LocalDateTime.now().toString());
        }
    }
    
    private static final String DATA_FILE = "userdata/users.dat";
    private static final String PROFILES_DIR = "userdata/profiles";
    private final List<User> users = new ArrayList<>();
    private String currentUser;
    
    public UserManager() { loadUsers(); }

    // Password utilities
    private static String generateSalt() {
        byte[] salt = new byte[16];
        new SecureRandom().nextBytes(salt);
        return Base64.getEncoder().encodeToString(salt);
    }
    
    private static String hashPassword(String password, String salt) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            md.update(Base64.getDecoder().decode(salt));
            return Base64.getEncoder().encodeToString(md.digest(password.getBytes()));
        } catch (Exception e) { throw new RuntimeException("Error hashing password", e); }
    }
    
    private static String createSaltedHash(String password) {
        String salt = generateSalt();
        return salt + ":" + hashPassword(password, salt);
    }
    
    private static boolean verifyPassword(String password, String saltedHash) {
        if (saltedHash == null || !saltedHash.contains(":")) return false;
        String[] parts = saltedHash.split(":", 2);
        return hashPassword(password, parts[0]).equals(parts[1]);
    }
    
    public static boolean isPasswordValid(String password) { return password != null && password.length() >= 6; }
    
    public static double calculatePasswordStrength(String password) {
        if (password == null || password.isEmpty()) return 0.0;
        int score = Math.min(password.length() / 4, 4);
        if (password.chars().anyMatch(Character::isLowerCase)) score++;
        if (password.chars().anyMatch(Character::isUpperCase)) score++;
        if (password.chars().anyMatch(Character::isDigit)) score++;
        if (password.chars().anyMatch(ch -> !Character.isLetterOrDigit(ch))) score++;
        if (password.length() >= 12) score++;
        if (password.length() >= 16) score++;
        return Math.min(score / 8.0, 1.0);
    }
    
    public static String getPasswordStrengthDescription(String password) {
        double strength = calculatePasswordStrength(password);
        return strength < 0.2 ? "Very Weak" : strength < 0.4 ? "Weak" : 
               strength < 0.6 ? "Moderate" : strength < 0.8 ? "Strong" : "Very Strong";
    }
    // User management
    public boolean registerUser(String username, String password) {
        if (isInvalid(username) || isInvalid(password)) return false;
        String trimmed = username.trim();
        if (findUserByUsername(trimmed).isPresent()) return false;
        
        User newUser = new User(trimmed, createSaltedHash(password));
        users.add(newUser);
        saveUsers();
        createUserProfile(newUser);
        return true;
    }
    
    public boolean authenticateUser(String username, String password) {
        if (isInvalid(username) || isInvalid(password)) return false;
        return findUserByUsername(username.trim())
            .filter(user -> verifyPassword(password, user.getPasswordHash()))
            .map(user -> { this.currentUser = username.trim(); loadUserProfile(user); return true; })
            .orElse(false);
    }
    
    public List<String> getAllUsernames() {
        return users.stream().map(User::getUsername).collect(Collectors.toList());
    }
    
    public boolean deleteUser(String username) {
        boolean removed = users.removeIf(user -> user.getUsername().equals(username));
        if (removed) saveUsers();
        return removed;
    }
    
    public String getDefaultSearchEngine(String username) {
        return findUserByUsername(username).map(User::getDefaultSearchEngine).orElse("Google");
    }
    
    public void setDefaultSearchEngine(String username, String searchEngine) {
        updateUser(username, user -> { user.setDefaultSearchEngine(searchEngine); });
    }
    
    public void addToHistory(String username, String url) {
        updateUser(username, user -> user.addToHistory(url));
    }
    
    public List<String> getUserHistory(String username) {
        return findUserByUsername(username).map(User::getBrowsingHistory).orElse(new ArrayList<>());
    }
    
    public void clearUserHistory(String username) {
        updateUser(username, user -> user.getBrowsingHistory().clear());
    }
    
    // Bookmark management
    public void addBookmark(String username, String title, String url) {
        findUserByUsername(username).ifPresent(user -> writeToFile(
            getProfilePath(user.getUsername()) + "/bookmarks.txt", 
            title + " | " + url + " | " + LocalDateTime.now().toString(), true));
    }
    
    public List<String> getUserBookmarks(String username) {
        return readLines(getProfilePath(username) + "/bookmarks.txt")
            .stream().filter(line -> !line.trim().isEmpty() && !line.startsWith("#"))
            .collect(Collectors.toList());
    }
    
    // File operations
    private void loadUsers() {
        createDirectories();
        File file = new File(DATA_FILE);
        if (!file.exists()) { users.clear(); return; }
        
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(file))) {
            @SuppressWarnings("unchecked")
            List<User> loaded = (List<User>) ois.readObject();
            users.clear();
            users.addAll(loaded != null ? loaded : new ArrayList<>());
            users.forEach(this::loadUserProfile);
        } catch (Exception e) {
            System.err.println("Error loading users: " + e.getMessage());
            users.clear();
        }
    }

    private void saveUsers() {
        createDirectories();
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(DATA_FILE))) {
            oos.writeObject(users);
        } catch (Exception e) { System.err.println("Error saving users: " + e.getMessage()); }
    }
    
    private void createUserProfile(User user) {
        try {
            createDirectory(getProfilePath(user.getUsername()));
            saveUserProfile(user);
        } catch (Exception e) { System.err.println("Error creating user profile: " + e.getMessage()); }
    }
    
    private void saveUserProfile(User user) {
        String profilePath = getProfilePath(user.getUsername());
        createDirectory(profilePath);
        
        // Save settings
        writeToFile(profilePath + "/settings.txt", String.join("\n",
            "User: " + user.getUsername(),
            "Created: " + user.getCreatedDate(),
            "Default Search Engine: " + user.getDefaultSearchEngine(),
            "Avatar Path: " + user.getAvatarPath()), false);
        
        // Save history
        writeToFile(profilePath + "/history.txt", String.join("\n", user.getBrowsingHistory()), false);
        
        // Create bookmarks file if not exists
        File bookmarksFile = new File(profilePath + "/bookmarks.txt");
        if (!bookmarksFile.exists()) {
            writeToFile(profilePath + "/bookmarks.txt", "# User Bookmarks\n# Format: Title | URL | Date Added", false);
        }
    }
    
    private void loadUserProfile(User user) {
        String profilePath = getProfilePath(user.getUsername());
        if (!new File(profilePath).exists()) { createUserProfile(user); return; }
        
        // Load settings
        readLines(profilePath + "/settings.txt").forEach(line -> {
            if (line.startsWith("Default Search Engine: ")) 
                user.setDefaultSearchEngine(line.substring("Default Search Engine: ".length()));
            else if (line.startsWith("Avatar Path: ")) 
                user.setAvatarPath(line.substring("Avatar Path: ".length()));
        });
        
        // Load history
        user.setBrowsingHistory(readLines(profilePath + "/history.txt")
            .stream().filter(line -> !line.trim().isEmpty()).collect(Collectors.toList()));
    }

    // Helper methods
    private Optional<User> findUserByUsername(String username) {
        return username == null ? Optional.empty() : 
            users.stream().filter(user -> user.getUsername() != null && 
                user.getUsername().trim().equalsIgnoreCase(username.trim())).findFirst();
    }
    
    private void updateUser(String username, java.util.function.Consumer<User> action) {
        findUserByUsername(username).ifPresent(user -> {
            action.accept(user);
            saveUsers();
            saveUserProfile(user);
        });
    }
    
    private boolean isInvalid(String str) { return str == null || str.trim().isEmpty(); }
    
    private String getProfilePath(String username) { return PROFILES_DIR + "/" + username; }
    
    private void createDirectories() {
        createDirectory("userdata");
        createDirectory(PROFILES_DIR);
    }
    
    private void createDirectory(String path) {
        try { Files.createDirectories(Paths.get(path)); }
        catch (Exception e) { System.err.println("Error creating directory: " + e.getMessage()); }
    }
    
    private void writeToFile(String path, String content, boolean append) {
        try (PrintWriter writer = new PrintWriter(new FileWriter(path, append))) {
            writer.println(content);
        } catch (Exception e) { System.err.println("Error writing file: " + e.getMessage()); }
    }
    
    private List<String> readLines(String path) {
        try { return Files.readAllLines(Paths.get(path)); }
        catch (Exception e) { return new ArrayList<>(); }
    }
}

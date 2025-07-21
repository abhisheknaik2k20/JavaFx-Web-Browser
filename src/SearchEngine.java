import javafx.scene.image.Image;
import java.util.Arrays;

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

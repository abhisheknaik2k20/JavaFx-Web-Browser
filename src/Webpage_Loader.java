import javafx.collections.ObservableList;
import javafx.concurrent.Worker;
import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebHistory;
import javafx.scene.web.WebView;
import javafx.stage.Stage;

public class Webpage_Loader /*implements Initializable*/{
    @FXML 
    private WebView webView;
    @FXML
    private TextField textField;
    @FXML
    Scene scene;
    @FXML
    private  WebEngine engine;
    private  WebHistory history;
    @FXML
    Button reload_Button;
    @FXML
    Button gb;
    @FXML
    Button loadButton;    
    @FXML
    ProgressBar progressBar;
    int flag1;
    @FXML
    Stage stage2;
    Scene scene2;
    String username;

    public void flag_Setter(int flag,Stage stage,Scene scene,String username){
        flag1=flag;
        set_HomePage0();
        this.stage2=stage;
        this.scene2=scene;
        this.username=username;
        progressBar.setStyle("-fx-accent:#FFEC00");
    }
    public void set_HomePage0(){
        engine=webView.getEngine();
       key_Listner();
       set_HomePage();
        loadPage();
        mouse_Listner();
    }
    public void set_HomePage(){
     switch(flag1){
        case 1: engine.load("https://www.bing.com/");proBar_Update();update_URL();break;
        case 2:engine.load("https://duckduckgo.com/");proBar_Update();update_URL();break;
        case 3:engine.load("https://www.google.com");proBar_Update();update_URL();break;
        case 4:engine.load("https://www.Yahoo.com/");proBar_Update();update_URL();break;
      }
    }
    public void mouse_Listner(){
      engine.locationProperty().addListener((observable, oldValue, newValue) -> {
        webView.setOnMouseClicked(event -> {
          update_URL(); proBar_Update();
      });
    });
    }
    public void loadoage_Button(){
      
                 if(!textField.getText().equals("")){
                 if(isURL(textField.getText())){
                   if(!starth(textField.getText())){
                     engine.load("https://"+textField.getText());proBar_Update();update_URL();
                   }
                   else{
                    engine.load(textField.getText());proBar_Update();update_URL();
                   }
            }
            else{
                  switch(flag1){
                    case 1:engine.load("https://www.bing.com/search?q="+textField.getText());proBar_Update();update_URL();break;
                    case 2:engine.load("https://duckduckgo.com/?q="+textField.getText());proBar_Update();update_URL();break;
                    case 3:engine.load("https://www.google.com/search?q="+textField.getText());proBar_Update();update_URL();break;
                    case 4:engine.load("https://search.yahoo.com/search?p="+textField.getText());proBar_Update();update_URL();break;
                }
             }
               }
    }
    public void key_Listner(){
      webView.setOnKeyPressed(keyAction ->{
        progressBar.progressProperty().bind(engine.getLoadWorker().progressProperty());
        if(keyAction.getCode()==KeyCode.ENTER)
            {
            proBar_Update(); update_URL();
             }
    });
    }

    public void proBar_Update(){
       progressBar.progressProperty().bind(engine.getLoadWorker().progressProperty());
    }
    public void update_URL() {
      engine.getLoadWorker().stateProperty().addListener((obs, oldValue, newValue) -> {
          if (newValue == Worker.State.SUCCEEDED) {
              history = engine.getHistory();
              ObservableList<WebHistory.Entry> entries = history.getEntries();
              if (!entries.isEmpty()) {
                  textField.setText(entries.get(history.getCurrentIndex()).getUrl());
              } 
          }  
         else if (newValue == Worker.State.FAILED) {
              loadHtml();
          }
      });
  }
    public void loadPage(){
       textField.setOnKeyPressed(keyAction ->{
        if(keyAction.getCode()==KeyCode.ENTER)
            {
               if(!textField.getText().equals("")){
                 if(isURL(textField.getText())){
                   if(!starth(textField.getText())){
                     engine.load("https://"+textField.getText());proBar_Update();update_URL();
                   }
                   else{
                    engine.load(textField.getText());proBar_Update();update_URL();
                   }
            }
            else{
                  switch(flag1){
                    case 1:engine.load("https://www.bing.com/search?q="+textField.getText());proBar_Update();update_URL();break;
                    case 2:engine.load("https://duckduckgo.com/?q="+textField.getText());proBar_Update();update_URL();break;
                    case 3:engine.load("https://www.google.com/search?q="+textField.getText());proBar_Update();update_URL();break;
                    case 4:engine.load("https://search.yahoo.com/search?p="+textField.getText());proBar_Update();update_URL();break;
                }
             }
               }

         }
        proBar_Update();
    });
  }
    public boolean starth(String s){
      if(s.startsWith("https://")) return true;
      else return false;
    }
    public boolean isURL(String string){
        if (string.startsWith("www.") || string.startsWith("https://") ) return true;
       else  if(string.endsWith(".com") || string.endsWith(".in") || string.endsWith(".io") || string.endsWith(".co") || string.endsWith(".so")) return true;
        return  false;
    }

    public void Reload(){
        engine.reload();
        System.out.println("Page Reloading...");
        update_URL();proBar_Update();
    }
    public void display_History(){
      history=engine.getHistory();
      ObservableList<WebHistory.Entry> entries=history.getEntries();
      for(WebHistory.Entry entry : entries){
        System.out.println(entry.getLastVisitedDate());
        System.out.println(entry.getUrl());
        System.out.println();
      }
    }
    public void go_Back(){
      history=engine.getHistory();
      ObservableList<WebHistory.Entry> entries =history.getEntries();
      if(!entries.isEmpty() && history.getCurrentIndex()>0)
      {history.go(-1);proBar_Update();update_URL();} }

    public void loadHtml() {
      StringBuilder sb = new StringBuilder()
      .append("<html>")
      .append("<body>")
      .append("<h1>************ You're offline ************</h1>")
      .append("<h2>    You can try Reloading the page</h2>")
      .append("<h3>    Or try the game</h3>")
      .append("</body>")
      .append("</html>");
  
    engine.loadContent(sb.toString());
   }
   public void loadGame(){
   Pong p=new Pong();
    Stage stageAPP=new Stage();
    try {
      p.start(stageAPP);
    } catch (Exception e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
   }
}

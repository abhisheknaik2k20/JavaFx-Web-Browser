import java.io.File;
import java.io.IOException;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.media.MediaView;
import javafx.stage.Stage;
import javafx.util.Duration;

class PassWords {
    String Password1 = "HelloWorld";
    String Password2 = "Hello";
     char p3[]=new char[100];
    char p4[]=new char[100];
    public void create_PassWord(int flag,String d) {
        if (flag == 3) {
            for(int i=0;i<d.length();i++){
                char c=d.charAt(i);
                p3[i]=c;
            }
        }
         if (flag == 4) {
            for(int i=0;i<d.length();i++){
                char c=d.charAt(i);
                p4[i]=c;
            }
        }
    }
    public boolean same_Password(int flag,String pass){
        if (flag == 3) {
           for(int i=0;i<pass.length();i++){
            char c=pass.charAt(i);
            if(c!=p3[i]){
                return false;
            }
           }
        }
       if (flag == 4) {
           for(int i=0;i<pass.length();i++){
            char c=pass.charAt(i);
            if(c!=p4[i]){
                return false;
            }
           }
        }
        return true; 
    }
}



public class Login_Controller {
    @FXML
    Label nameLabel;
    @FXML
    Label incorrect;
    @FXML
    PasswordField passwordfld;
    @FXML
    private Stage stage,stage2; 
    @FXML
    private Scene scene,scene2;
    @FXML
    private Parent root;
    @FXML
    private ImageView profile;
    @FXML
    Button back;

    int flag1;
    String s;
    char[] pass;

    PassWords p=new PassWords();

     @FXML 
     MediaView mediaView;
     MediaPlayer mediaPlayer;
     File file;
     Media media;
       public void initialize() {
            file = new File("D:/Java/Java Project/src/Images/BG1.mp4");
            media = new Media(file.toURI().toString());
            mediaPlayer = new MediaPlayer(media);
            mediaView.setMediaPlayer(mediaPlayer);
            mediaPlayer.setOnEndOfMedia(() -> {
                mediaPlayer.seek(Duration.ZERO);
            });
            mediaPlayer.play();
        } 
     
    public void display_Name(String username,int flag,Scene scene,Image image,Stage stage,char[] pass){
        nameLabel.setText(username);
        initKeyActions(stage,scene,flag,username);
        profile.setImage(image); 
        this.flag1=flag;
        s=username;
        this.stage2=stage;
        this.scene2=scene;
        this.pass=pass;
        if(flag==3){
          p.p3=pass;
        }
        if(flag==4){
          p.p4=pass;
        }
    }
    public void initKeyActions(Stage stage,Scene scene,int flag,String username){
    scene.setOnKeyPressed(keyAction ->{
         
      if(keyAction.getCode()==KeyCode.CAPS)
      {System.out.println("Caps-Lock is ON");}

      if(keyAction.getCode()==KeyCode.ENTER){
        String Pass=passwordfld.getText();
         if(flag==1){
        if(p.Password1.equals(Pass)) {
              System.out.println("Correct PassWord");
              go_Homepage(stage,username);
               }
         else {
            System.out.println("InCorrect PassWord*");
            incorrect.setText("**Incorrect Password**");
            passwordfld.setText(null);
              }
         }
         else if(flag==2){
            if(p.Password2.equals(Pass)) {
             System.out.println("Correct PassWord "); 
              go_Homepage(stage,username);  
            }
         else {
            System.out.println("InCorrect PassWord");
              incorrect.setText("**Incorrect Password**");
              passwordfld.setText(null);
              }
         }
          else if(flag==3){
            if(p.same_Password(flag, Pass)) {
             System.out.println("Correct PassWord "); 
              go_Homepage(stage,username);  
            }
         else {
            System.out.println("InCorrect PassWord");
              incorrect.setText("**Incorrect Password**");
              passwordfld.setText(null);
              }
         }
          else if(flag==4){
            if(p.same_Password(flag, Pass)) {
             System.out.println("Correct PassWord "); 
              go_Homepage(stage,username);  
            }
         else {
            System.out.println("InCorrect PassWord");
              incorrect.setText("**Incorrect Password**");
              passwordfld.setText(null);
              }
         }
        }
    });
    
  }
      public void go_Back() throws IOException{
         try {
        FXMLLoader loader=new FXMLLoader(getClass().getResource("/FXML Files/Scene2.fxml"));
        Parent root =loader.load();
        scene2= new Scene(root);
        ProfileP p= loader.getController();
        p.update_Page(s,flag1,pass);
        stage2.setScene(scene2);
        stage2.show();
        
       } catch (IOException e) {
         e.printStackTrace();
       }
        }

        public void go_Homepage(Stage stage,String username){
        try {
        FXMLLoader loader=new FXMLLoader(getClass().getResource("/FXML Files/Scene4.fxml"));
        Parent root =loader.load();
        scene= new Scene(root);
        HomePage_Cont loginPage= loader.getController();
        loginPage.display_Greet(username);
        stage.setScene(scene);
        stage.show();
        
       } catch (IOException e) {
         e.printStackTrace();
       }
    }
}
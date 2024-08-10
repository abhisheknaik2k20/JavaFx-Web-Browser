import java.io.File;
import java.io.IOException;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.TextField;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.input.KeyCode;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.media.MediaView;
import javafx.stage.Stage;
import javafx.util.Duration;

public class Create_New{
     @FXML
     TextField Username;
     @FXML
     PasswordField OGPass;
     @FXML
     PasswordField ROGPass;
     @FXML
     ProgressBar myPB;

     @FXML
     Label PassStrength;
     
     double progress;

     Stage stage;
     Scene scene;

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
      
     public void initKeyActions(Scene scene,Stage stage,Parent root,int flag){
        this.stage=stage;
        this.scene=scene;
     AcceptUser(scene,stage,root,flag);
     update_Bar();
    }
    
    public void AcceptUser(Scene scene,Stage stage,Parent root,int flag){
         Alert alert1=new Alert(AlertType.INFORMATION);
         Alert alert2=new Alert(AlertType.INFORMATION);
         Alert alert3=new Alert(AlertType.INFORMATION);
         Alert alert4=new Alert(AlertType.INFORMATION);
         Alert alert5=new Alert(AlertType.INFORMATION);
         alert1.setContentText("Please Enter Username");
         alert2.setContentText("Please Enter Password");
         alert3.setContentText("Please Re-Enter Password");
         alert4.setContentText("The Re-Entered PassWord is not the same");
         alert5.setContentText("The PassWord is TOO Weak");
     scene.setOnKeyPressed(keyAction ->{
            if(keyAction.getCode()==KeyCode.ENTER)
                {  
                    if(Username.getText().equals("")){
                        alert1.show();
                    }
                   else  if(OGPass.getText().equals("")){
                        alert2.show();
                   }
                    else if(ROGPass.getText().equals("")){
                        alert3.show();
                    }
                    else if(!OGPass.getText().equals(ROGPass.getText())){
                         alert4.show();
                         ROGPass.setText(null);
                    }
                    else{
                        PassWords p=new PassWords();
                        if(flag==3){
                            p.create_PassWord(flag,OGPass.getText());
                            goTOprofile(scene,stage,root,flag,OGPass.getText(),p.p3);
                        }
                        if(flag==4){
                            p.create_PassWord(flag,OGPass.getText());
                            goTOprofile(scene,stage,root,flag,OGPass.getText(),p.p4);
                        }
                    }
                }
        });
     }

     public void update_Bar(){
        Alert alert=new Alert(AlertType.INFORMATION);
        alert.setContentText("The Entered PassWord is too Weak");
        Username.setOnKeyTyped(keyAction ->{
                myPB.setStyle("-fx-accent: GREEN;");
            myPB.setProgress(ProgressBar.INDETERMINATE_PROGRESS);      
        });
        OGPass.setOnKeyTyped(keyAction ->{
            int letters=0;
            int special_Char=0;
            int nums=0;
            for(int i=0;i<OGPass.getText().length();i++){
                if(Character.isAlphabetic(OGPass.getText().charAt(i))){
                    letters++;
                }
              else if(Character.isDigit(OGPass.getText().charAt(i))){
                nums++;
               }
               else{
                special_Char++;
               }
            }
            progress=letters*0.017*2+nums*0.033*3+special_Char*0.066*4;
            if(progress<0.2){
                 PassStrength.setText("Password Strength : TOO weak");
                myPB.setStyle("-fx-accent:  RED;");
                myPB.setProgress(progress);
            }
            else if(progress<0.4){
                  PassStrength.setText("Password Strength : Weak");
                myPB.setStyle("-fx-accent: #FE670B;");
                myPB.setProgress(progress);
            }
            else if(progress<0.6){
                  PassStrength.setText("Password Strength : Moderate");
                myPB.setStyle("-fx-accent: #FEFE0B;");
                myPB.setProgress(progress);
            }
            else if(progress<0.8){
               PassStrength.setText("Password Strength : Good");
                myPB.setStyle("-fx-accent: GREEN;");
                myPB.setProgress(progress);
            }
            else{
                              PassStrength.setText("Password Strength : Great");
                 myPB.setStyle("-fx-accent: GREEN;");
                myPB.setProgress(progress);
            }
        });
        
     } 

     public void goTOprofile(Scene scene,Stage stage,Parent root,int flag,String PassWord,char[] a){
          try {
        FXMLLoader loader=new FXMLLoader(getClass().getResource("/FXML Files/Scene2.fxml"));
        root =loader.load();
        scene= new Scene(root);
        ProfileP p= loader.getController();
        p.update_Page(Username.getText(),flag,a);
        stage.setScene(scene);
        stage.show();
        
       } catch (IOException e) {
         e.printStackTrace();
       }
    }
    
     @FXML
    public void go_Back(ActionEvent e) throws IOException, InterruptedException{
        System.out.println("Launching Browser........");
        Parent root=FXMLLoader.load(getClass().getResource("FXML Files/Scene2.fxml"));
              stage=(Stage)((Node)e.getSource()).getScene().getWindow();
              scene= new Scene(root);
              stage.setScene(scene);
              stage.show();

   }
}
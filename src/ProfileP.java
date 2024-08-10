import java.io.File;
import java.io.IOException;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.RadioButton;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.media.MediaView;
import javafx.stage.Stage;
import javafx.util.Duration;

public class ProfileP{

    char[] pass;
    private Stage stage;
    private Scene scene;
    private Parent root;
    String string,new_User;
      @FXML
      private RadioButton rButton1,rButton2,rButton3,rButton4;
      @FXML
      Label lb1;
       private int flag=0;
     
       @FXML
       ImageView imageUser1,imageUser2;

       @FXML
       MediaView mediaView;

       File file;
       Media media;
       MediaPlayer mediaPlayer;

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

       @FXML
    public void pick_User(ActionEvent ce) throws IOException{
        if(rButton1.isSelected()){
            flag=1;
        }
        else if(rButton2.isSelected())
        {
           flag=2;
        }
        else if(rButton3.isSelected()){
            flag=3;
        }
        else if(rButton4.isSelected()){
            flag=4;
        }
    }
    public void User_Selected(ActionEvent ce) throws IOException{
        Image image1=new Image(getClass().getResourceAsStream("/Images/Avatar-1.png"));
        Image image2=new Image(getClass().getResourceAsStream("/Images/Avatar-2.png"));
        Image image3=new Image(getClass().getResourceAsStream("/Images/Avatar-3.png"));
        Image image4=new Image(getClass().getResourceAsStream("/Images/Avatar-4.png"));
        Image image=new Image("/Images/Logo.png");
        if(flag==1)
        {
            System.out.println("User Picked: ABHISHEK");
                string="Abhishek Naik";
                image=image1;
                FXMLLoader loader=new FXMLLoader(getClass().getResource("/FXML Files/Scene3.fxml"));
                root=loader.load();
                stage=(Stage)((Node)ce.getSource()).getScene().getWindow();
                scene= new Scene(root);
                Login_Controller loginPage= loader.getController();
                loginPage.display_Name(string,flag,scene,image,stage,pass);
                stage.setScene(scene);
                stage.show();
            }
           else if(flag==2)
           {
                System.out.println("User Picked: PRANAV");
                string="Pranav Patil";
                image=image2;
                FXMLLoader loader=new FXMLLoader(getClass().getResource("/FXML Files/Scene3.fxml"));
                root=loader.load();
                stage=(Stage)((Node)ce.getSource()).getScene().getWindow();
                scene= new Scene(root);
                Login_Controller loginPage= loader.getController();
                loginPage.display_Name(string,flag,scene,image,stage,pass);
                stage.setScene(scene);
                stage.show();
            }
            else if(flag==3){
                if(rButton3.getText().equals("Create New")){
                    FXMLLoader loader=new FXMLLoader(getClass().getResource("/FXML Files/Example.fxml"));
                 root=loader.load();
                 stage=(Stage)((Node)ce.getSource()).getScene().getWindow();
                 scene= new Scene(root);
                 Create_New c= loader.getController();
                 c.initKeyActions(scene,stage,root,flag);
                 stage.setScene(scene);
                 stage.show();
                }
                else{
                System.out.println("User Picked: "+string);
                image=image3;
                FXMLLoader loader=new FXMLLoader(getClass().getResource("/FXML Files/Scene3.fxml"));
                root=loader.load();
                stage=(Stage)((Node)ce.getSource()).getScene().getWindow();
                scene= new Scene(root);
                Login_Controller loginPage= loader.getController();
                loginPage.display_Name(string,flag,scene,image,stage,pass);
                stage.setScene(scene);
                stage.show();
                    
                }
            }
            else if(flag==4){
                if(rButton4.getText().equals("Create New")){
                    FXMLLoader loader=new FXMLLoader(getClass().getResource("/FXML Files/Example.fxml"));
                 root=loader.load();
                 stage=(Stage)((Node)ce.getSource()).getScene().getWindow();
                 scene= new Scene(root);
                 Create_New c= loader.getController();
                 c.initKeyActions(scene,stage,root,flag);
                 stage.setScene(scene);
                 stage.show();
                }
                else{
                System.out.println("User Picked: "+string);
                image=image4;
                FXMLLoader loader=new FXMLLoader(getClass().getResource("/FXML Files/Scene3.fxml"));
                root=loader.load();
                stage=(Stage)((Node)ce.getSource()).getScene().getWindow();
                scene= new Scene(root);
                Login_Controller loginPage= loader.getController();
                loginPage.display_Name(string,flag,scene,image,stage,pass);
                stage.setScene(scene);
                stage.show();
                }
            }
            else{
                lb1.setText("Invalid Choice");
            }
            
        }
        public void update_Page(String s,int flag,char[] a){
            pass=a;
         Image image3=new Image(getClass().getResourceAsStream("/Images/Avatar-3.png"));
        Image image4=new Image(getClass().getResourceAsStream("/Images/Avatar-4.png"));
            if(flag==3)
            {
                rButton3.setText(s);
                this.string=s;
                imageUser1.setImage(image3);
            }
            if(flag==4){
                 rButton4.setText(s);
                this.string=s;
                imageUser2.setImage(image4);
            }
        }
    }
    

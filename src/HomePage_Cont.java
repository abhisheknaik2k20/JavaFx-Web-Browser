import java.io.IOException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.Calendar;
import java.util.ResourceBundle;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;
public class HomePage_Cont extends Thread implements Initializable {

    @FXML
    Label Greet, label,CHOICE,TodayDate;

    private Stage stage;
    private Scene scene;

    @FXML
    private Label Date_S;
    Calendar calendar;
    int flag=0;
    String name;

    @FXML
    ChoiceBox<String> cho;

    @FXML
    ImageView imageView;
    Image bing=new Image("/Images/bing.png");
    Image DDG=new Image("/Images/DDG.png");
    Image google=new Image("/Images/Google.png");
    Image yahoo=new Image("/Images/Yahoo.png");

    String[] searchEngines = {"Bing", "Duck Duck GO", "Google", "Yahoo!"};
    
    ClockThread clockThread = new ClockThread();
    @Override
    public void initialize(URL arg0, ResourceBundle arg1) {
        cho.getItems().addAll(searchEngines);
        cho.setOnAction(this::User_Selected);
        clockThread.start();
    }

    public void go_Back(ActionEvent e) throws IOException {
        Parent root = FXMLLoader.load(getClass().getResource("/FXML Files/Scene2.fxml"));
        stage = (Stage) ((Node) e.getSource()).getScene().getWindow();
        scene = new Scene(root);
        stage.setScene(scene);
        stage.show();
    }

    public void display_Greet(String username) {
        this.name = username;
       Greet.setText("Good"+getTimeOfDay()+" "+username);
        LocalDate currentDate = LocalDate.now();
        DayOfWeek currentDay = currentDate.getDayOfWeek();

        TodayDate.setText("It's "+currentDay.toString()+ " "+currentDate.format(DateTimeFormatter.ofPattern("dd-MM-yyyy")));
    }

    public void User_Selected(ActionEvent e) {
        String myEngine = cho.getValue();
        if (myEngine.equals("Bing")) 
        {
            flag = 1;
            imageView.setImage(bing);
        }
        if (myEngine.equals("Duck Duck GO")){
             flag = 2;
              imageView.setImage(DDG);
        }
        if (myEngine.equals("Google")){
            flag = 3;
            imageView.setImage(google);
        } 
        if (myEngine.equals("Yahoo!")){
           flag = 4;
           imageView.setImage(yahoo);
        } 
    }

    public void go_WEB(ActionEvent e) throws IOException {
        if(flag==0){
           CHOICE.setText("Make a valid Choice First");
        }
        else{
            System.out.println("Launching Browser........");
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/FXML Files/Web_Page.fxml"));
            Parent root = loader.load();
            Webpage_Loader webpage = loader.getController();
            webpage.flag_Setter(flag, stage, scene, name);
            stage = (Stage) ((Node) e.getSource()).getScene().getWindow();
            scene = new Scene(root);
            stage.setScene(scene);
                stage.show();
        }
    }
     public static String getTimeOfDay() {
        LocalTime currentTime=LocalTime.now();
        int hour = currentTime.getHour();

        if (hour >= 0 && hour < 12) {
            return "Morning";
        } else if (hour >= 12 && hour < 17) {
            return "Afternoon";
        } else {
            return "Evening";
        }
    }

    public void wAPP(){
       WeatherApplication w=new WeatherApplication();
        Stage stageAPP=new Stage();
        w.start(stageAPP);

    }

    private class ClockThread extends Thread {
        @Override
        public void run() {
            SimpleDateFormat timeformat = new SimpleDateFormat("hh:mm:ss a");
            while (true) {
                Calendar calendar = Calendar.getInstance();
                final String time = timeformat.format(calendar.getTime());
                Platform.runLater(() -> Date_S.setText(time));
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    
}

import javafx.application.Application;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;
import org.json.JSONArray;
import org.json.JSONObject;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.time.LocalDate;

public class WeatherApplication extends Application {
    private static final String API_KEY = "6b2a9472adfaecfde86abc2829cb8c0f";
   @FXML 
   ImageView IV;
    @FXML
    TextField wtext;
    @FXML
    Label wea,GreetingL,tempratureLabel,statusLabel,forcast;
    @FXML
    Slider mySlider=new Slider(0, 56.7, -45);
    @FXML
    DatePicker myDatePicker; 
    Image clear=new Image(getClass().getResourceAsStream("/Images/Clear.png"));
    Image fog=new Image(getClass().getResourceAsStream("/Images/fog (1).png"));
    Image rain=new Image(getClass().getResourceAsStream("/Images/Rain.png"));
    Image snow=new Image(getClass().getResourceAsStream("/Images/snowy.png"));
    Image thunder=new Image(getClass().getResourceAsStream("/Images/thunderstorm.png"));
    Image typhoon=new Image(getClass().getResourceAsStream("/Images/typhoon.png"));

    private int compareDates(LocalDate selectedDate) {
        LocalDate today = LocalDate.now();
        if (selectedDate.isBefore(today)) {
            return 1;
        } else if (selectedDate.isEqual(today)) {
            return 2;
        } else {
            return 3;
        }
    }

      @Override
    public void start(Stage stage) {
        try{
            Parent root =FXMLLoader.load(getClass().getResource("/FXML Files/WeatherApp.fxml"));
            Scene scene=new Scene(root, 500, 400);
            stage.setScene(scene); 
            stage.setResizable(false);
            stage.show();
        } catch(Exception e)
        {e.printStackTrace();}
    } 
    public void startmethod() {
        if(myDatePicker.getValue()!=null && !wtext.getText().equals("")){
            wea.setText(null);
            GreetingL.setText(null);
            String CITY = wtext.getText();
        LocalDate selectedDate = myDatePicker.getValue();
        String date = selectedDate.toString();
        try {
            String weatherData = getWeatherData(CITY, date);
            JSONObject json = new JSONObject(weatherData);
            JSONArray forecasts = json.getJSONArray("list");

            for (int i = 0; i < forecasts.length(); i++) {
                JSONObject forecast = forecasts.getJSONObject(i);
                String forecastDate = forecast.getString("dt_txt");
                if (forecastDate.contains(date)) {
                    JSONObject main = forecast.getJSONObject("main");
                    JSONArray weatherArray = forecast.getJSONArray("weather");
                    double temperature = main.getDouble("temp");
                    JSONObject weather = weatherArray.getJSONObject(0);
                    String status = weather.getString("main");
                    int tempc=(int)(temperature - 273);
                    int tempf=(int)(tempc*1.8+32);
                    tempratureLabel.setText(""+tempc+"°C/ "+tempf+"°F");
                    statusLabel.setText(status);
                    switch(status){
                        case "Clear":IV.setImage(clear);break;
                        case "Clouds":IV.setImage(clear);break;
                        case "Rain":IV.setImage(rain);break;
                        case "Drizzle":IV.setImage(rain);break;
                        case "Thunderstorm":IV.setImage(thunder);break;
                        case "Snow":IV.setImage(snow);break;
                        case "Haze":IV.setImage(fog);break;
                        case "Mist":IV.setImage(fog);break;
                        case "Fog":IV.setImage(fog);break;
                        case "Smoke":IV.setImage(fog);break;
                        case "Tornado":IV.setImage(typhoon);break;
                        case "Dust":IV.setImage(typhoon);break;
                    }
               //     wea.setText("Temperature in " + CITY + " on " + date + ": " + (int)(temperature - 273) + "°C " + status);
              switch(compareDates(selectedDate)){
                case 1: forcast.setText("Before");break;
                case 2:forcast.setText("This is Today's Forecast");break;
                case 3: forcast.setText("This is Future's Forecast");break;
               }
                      
                    mySlider.setValue((int)(temperature - 273));
                    break;
                }
            }
        } catch (Exception e) {
            wea.setText("Error: Unable to retrieve weather data");
        }
     }
    }
    private String getWeatherData(String city, String date) throws Exception {
        String apiUrl = "http://api.openweathermap.org/data/2.5/forecast?q=" + city + "&appid=" + API_KEY;
        URL url = new URL(apiUrl);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("GET");
        BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
        String inputLine;
        StringBuilder response = new StringBuilder();

        while ((inputLine = reader.readLine()) != null) {
            response.append(inputLine);
        }

        reader.close();

        return response.toString();
    }
} 
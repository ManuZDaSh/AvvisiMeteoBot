import net.aksingh.owmjapis.api.APIException;
import net.aksingh.owmjapis.core.OWM;
import net.aksingh.owmjapis.model.CurrentWeather;
import net.aksingh.owmjapis.model.param.Weather;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.text.DecimalFormat;

public class AvvisiMeteoBot extends TelegramLongPollingBot {

    public void onUpdateReceived(Update update) {
        if (update.hasMessage()) {
            String city = update.getMessage().getText();
            OWM weatherMap = new OWM(LocalConsts.APIKEY);
            System.out.println(update.getMessage().getChat().getFirstName() + "\n" + city + "\n");
            SendMessage message = new SendMessage().setChatId(update.getMessage().getChatId());
            try {
                CurrentWeather currentWeather = weatherMap.currentWeatherByCityName(city);
                if (currentWeather.hasRespCode()) {
                    if (currentWeather.getRespCode() == 200) {
                        Weather cityWeather = currentWeather.getWeatherList().get(0);
                        DecimalFormat df = new DecimalFormat("0.00");
                        Double celsiusTemp = currentWeather.getMainData().getTemp() - 273.15;
                        message.setText("Weather conditions for " + currentWeather.getCityName()
                                        + ": " + cityWeather.getMainInfo() + "\nCurrent Temperature: "
                                        + df.format(celsiusTemp) + "°C");
                    }
                }
            } catch (APIException e) {
                if(e.getCode() == 404)
                    message.setText("Warning: City \"" + city + "\" not found.\nPlease try again.");
                e.printStackTrace();
            } finally {
                try {
                    execute(message);
                } catch (TelegramApiException t) {
                    t.printStackTrace();
                }
            }
        }
    }

    public String getBotUsername() {
        return LocalConsts.BOTUSERNAME;
    }

    public String getBotToken() {
        return LocalConsts.BOTTOKEN;
    }
}

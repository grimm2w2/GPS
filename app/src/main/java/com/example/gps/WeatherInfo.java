package com.example.gps;

import androidx.annotation.NonNull;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.GET;
import retrofit2.http.Query;

public class WeatherInfo {
    public final static String BaseUrl = "https://api.openweathermap.org/";
    public final static String AppId = "4134ff3d0e97d38e89451ce6d17d0443";
    public final static String Units = "metric";
    public static String Lang = "ru";

    private double lat;
    private double lon;

    public interface WeatherService {
        @GET("data/2.5/weather?")
        Call<WeatherResponse> getCurrentWeatherData(@Query("lat") double lat, @Query("lon") double lon, @Query("APPID") String app_id, @Query("units") String units, @Query("lang") String lang);
    }

    public interface CallBackInfo {
        void callingBack(String info, String extraInfo);
    }

    public WeatherInfo(double lat, double lon) {
        this.lat = lat;
        this.lon = lon;
    }

    public void job(final CallBackInfo goodResult, final CallBackInfo badResult) {

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BaseUrl)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        WeatherInfo.WeatherService service = retrofit.create(WeatherInfo.WeatherService.class);

        Call<WeatherResponse> call = service.getCurrentWeatherData(lat, lon, AppId, Units, Lang);
        call.enqueue(new Callback<WeatherResponse>() {
            @Override
            public void onResponse(@NonNull Call<WeatherResponse> call, @NonNull Response<WeatherResponse> response) {
                if (response.code() == 200) {
                    WeatherResponse weatherResponse = response.body();
                    assert weatherResponse != null;
                    String adWeather = "-", adHum = "-", adWind = "-";

                    float temp1 = -35f, temp2 = 0f, temp3 = 10f, temp4 = 15f, temp5 = 25f;
                    if (weatherResponse.main.temp_feels_like <= temp1) {
                        adWeather = "На улице сильный мороз, лучше сиди дома!";
                    }
                    if (weatherResponse.main.temp_feels_like < temp2 & weatherResponse.main.temp_feels_like > temp1) {
                        adWeather = "На улице мороз, если хочешь выйти на улицу, одевайся теплее!";
                    }
                    if (weatherResponse.main.temp_feels_like > temp2 & weatherResponse.main.temp_feels_like <= temp3) {
                        adWeather = "На улице плюсовая температура, но лучше одень под ветровку кофту! И обязательно одень шапку!";
                    }
                    if (weatherResponse.main.temp_feels_like >= temp3 & weatherResponse.main.temp_feels_like < temp4) {
                        adWeather = "Можешь выходить на улицу без шапки, но кофту возьми с собой)";
                    }
                    if (weatherResponse.main.temp_feels_like >= temp4 & weatherResponse.main.temp_feels_like <= temp5) {
                        adWeather = "На улице достаточно тепло, чтобы не брать с собой кофту!";
                    }
                    if (weatherResponse.main.temp_feels_like > temp5) {
                        adWeather = "На улице жара одевай что хочешь!";
                    }

                    float wind1 = 0f, wind2 = 5f, wind3 = 14f, wind4 = 25f, wind5 = 33f;
                    if (weatherResponse.wind.speed == 0) {
                        adWind = "Ветра на улице нет!";
                    }
                    if (weatherResponse.wind.speed > wind1 & weatherResponse.wind.speed <= wind2) {
                        adWind = "Ветер слабый, можешь не закрывать окна!";
                    }
                    if (weatherResponse.wind.speed > wind2 & weatherResponse.wind.speed <= wind3) {
                        adWind = "Ветер умеренный, но лучше поставь окна на проветривание!";
                    }
                    if (weatherResponse.wind.speed > wind3 & weatherResponse.wind.speed <= wind4) {
                        adWind = "Ветер сильный, закрой окна!";
                    }
                    if (weatherResponse.wind.speed > wind4 & weatherResponse.wind.speed < wind5) {
                        adWind = "На улице очень сильный ветер, чуль-ли не ураган, закрой окна и не выходи из дома!";
                    }
                    if (weatherResponse.wind.speed > wind5) {
                        adWind = "На улице ураган, лучше МОЛИСЬ!";
                    }

                    float hum1 = 40f, hum2 = 60f;

                    if (weatherResponse.main.humidity < hum1) {
                        adHum = "Показатель влажности достаточно низкий!";
                    }
                    if (weatherResponse.main.humidity >= hum1 & weatherResponse.main.humidity <= hum2) {
                        adHum = "Показатель влажности находится на оптимальном уровне! Могут быть осадки, если хотите возьмите с собой зонтик)";
                    }
                    if (weatherResponse.main.humidity > hum2) {
                        adHum = "Показатель влажности находится на достаточно высоком уровне, обязательно возьмите с собой зонт!";
                    }

                    String info = "Страна: " +
                            weatherResponse.sys.country +
                            "\n" +
                            "Температура ощущается на: " +
                            weatherResponse.main.temp_feels_like +
                            "\n" +
                            "Влажность: " +
                            weatherResponse.main.humidity + "%" +
                            "\n" +
                            "Скорость ветра: " +
                            weatherResponse.wind.speed + "м/c";
                    String extraInfo = "\n" +
                            adWeather +
                            "\n" +
                            adHum +
                            "\n" +
                            adWind;
                    goodResult.callingBack(info, extraInfo);
                } else {
                    badResult.callingBack("Ошибка запроса погоды", "Ошибка запроса погоды");
                }
            }

            @Override
            public void onFailure(@NonNull Call<WeatherResponse> call, @NonNull Throwable t) {
                badResult.callingBack("Проверьте соединение и перезапустите приложение", "Проверьте соединение и перезапустите приложение");
            }
        });
    }
}

package com.example.gps;

import android.Manifest;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.app.TaskStackBuilder;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.provider.Settings;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

public class GPS_Service extends Service {
    public String rem1, rem2 = "Дождитесь результата";
    private BroadcastReceiver brGetWeatherInfo;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        startForegroundService();
        if (brGetWeatherInfo == null) {
            brGetWeatherInfo = new BroadcastReceiver() {
                @Override
                public void onReceive(Context context, Intent intent) {
                    StartRemember();
                }
            };
        }
        registerReceiver(brGetWeatherInfo, new IntentFilter("get_weather_info"));
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        LocationListener listener = new LocationListener() {

            @Override
            public void onLocationChanged(Location location) {
                double lon = location.getLongitude();
                double lat = location.getLatitude();
                WeatherInfo weatherInfo = new WeatherInfo(lat, lon);

                weatherInfo.job(new WeatherInfo.CallBackInfo() {
                    @Override
                    public void callingBack(String info, String extraInfo) {
                        rem1 = info;
                        rem2 = extraInfo;
                        StartRemember();

                    }
                }, new WeatherInfo.CallBackInfo() {
                    @Override
                    public void callingBack(String info, String extraInfo) {
                        rem1 = info;
                        rem2 = extraInfo;
                        Intent j = new Intent("weather_update_bad");
                        j.putExtra("Info", "Проверьте соединение и перезапустите приложение");
                        j.putExtra("ExtraInfo", "Проверьте соединение и перезапустите приложение");
                        sendBroadcast(j);


                    }
                });

            }

            @Override
            public void onStatusChanged(String s, int i, Bundle bundle) {

            }

            @Override
            public void onProviderEnabled(String s) {

            }

            @Override
            public void onProviderDisabled(String s) {
                Intent i = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(i);
            }
        };

        LocationManager locationManager = (LocationManager) getApplicationContext().getSystemService(Context.LOCATION_SERVICE);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) !=
                        PackageManager.PERMISSION_GRANTED) {
            throw new RuntimeException("Stub!");
        }
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 10000, 10, listener);
        return super.onStartCommand(intent, flags, startId);
    }

    private void StartRemember() {
        Intent i = new Intent("weather_update");
        i.putExtra("Info", rem1);
        i.putExtra("ExtraInfo", rem2);
        sendBroadcast(i);
    }

    private void startForegroundService() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            createNotificationChannel("my_service", "My Background Service");
        } else {

            // Create notification default intent.
            Intent intent = new Intent();
            PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, 0);

            // Create notification builder.
            NotificationCompat.Builder builder = new NotificationCompat.Builder(this);

            // Make notification show big text.
            NotificationCompat.BigTextStyle bigTextStyle = new NotificationCompat.BigTextStyle();
            bigTextStyle.setBigContentTitle("GPS is searching you");
            builder.setStyle(bigTextStyle);
            builder.setWhen(System.currentTimeMillis());
            builder.setSmallIcon(R.mipmap.ic_launcher);
            Bitmap largeIconBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.ic_launcher_foreground);
            builder.setLargeIcon(largeIconBitmap);
            // Make the notification max priority.
            builder.setPriority(Notification.PRIORITY_MIN);
            // Make head-up notification.
            builder.setVisibility(NotificationCompat.VISIBILITY_SECRET);
            // Build the notification.
            Notification notification = builder.build();
            // Start foreground service.
            startForeground(1, notification);
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private void createNotificationChannel(String channelId, String channelName) {
        Intent resultIntent = new Intent(this, MainActivity.class);
// Create the TaskStackBuilder and add the intent, which inflates the back stack
        TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);
        stackBuilder.addNextIntentWithParentStack(resultIntent);
        PendingIntent resultPendingIntent =
                stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);
        NotificationChannel chan = new NotificationChannel(channelId, channelName, NotificationManager.IMPORTANCE_DEFAULT);
        chan.setLightColor(Color.BLUE);
        chan.setLockscreenVisibility(Notification.VISIBILITY_PRIVATE);
        NotificationManager manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        assert manager != null;
        manager.createNotificationChannel(chan);

        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this, channelId);
        Notification notification = notificationBuilder.setOngoing(true)
                .setSmallIcon(R.drawable.ic_launcher_foreground)
                .setContentTitle("I'm watching for you")
                .setPriority(NotificationManager.IMPORTANCE_MIN)
                .setCategory(Notification.CATEGORY_SERVICE)
                .setContentIntent(resultPendingIntent)
                .build();
        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(this);
        notificationManager.notify(1, notificationBuilder.build());
        startForeground(1, notification);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

}
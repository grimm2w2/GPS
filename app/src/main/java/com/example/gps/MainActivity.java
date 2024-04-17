package com.example.gps;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

public class MainActivity extends AppCompatActivity {

    private Button btn_start, btn_switch;
    private TextView txInfo;
    private BroadcastReceiver brWeatherUpdate;
    private BroadcastReceiver brWeatherUpdateBad;
    public String info, extraInfo = "Подожите информацию";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        btn_start = (Button) findViewById(R.id.button);
        btn_switch = (Button) findViewById(R.id.button2);
        txInfo = (TextView) findViewById(R.id.textView1);

        if (!runtime_permissions()) {
            enable_buttons();
        }

        brWeatherUpdate = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                info = (String) intent.getExtras().get("Info");
                extraInfo = (String) intent.getExtras().get("ExtraInfo");
                if (info.equalsIgnoreCase("Проверьте соединение и перезапустите приложение")) {
                    txInfo.setText(extraInfo);
                    btn_switch.setVisibility(View.INVISIBLE);
                    btn_start.setVisibility(View.VISIBLE);
                } else {
                    if (info != null && info != "Подожите информацию" && info != "Дождитесь результата") {
                        btn_start.setVisibility(View.INVISIBLE);
                        txInfo.setText(info);
                        if (info.length() >= 50) {
                            btn_switch.setVisibility(View.VISIBLE);
                        }
                    }
                }
            }
        };

        registerReceiver(brWeatherUpdate, new IntentFilter("weather_update"));
        brWeatherUpdateBad = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                extraInfo = (String) intent.getExtras().get("ExtraInfo");
                txInfo.setText(extraInfo);
                btn_switch.setVisibility(View.INVISIBLE);
                btn_start.setVisibility(View.VISIBLE);
            }
        };
        registerReceiver(brWeatherUpdateBad, new IntentFilter("weather_update_bad"));
    }

    @Override
    protected void onResume() {
        super.onResume();
        AskRem();
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    private void AskRem() {
        Intent i = new Intent("get_weather_info");
        sendBroadcast(i);
    }

    private void enable_buttons() {

        btn_start.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(getApplicationContext(), GPS_Service.class);
                startService(i);
                txInfo.setText("Здесь будет информация, подождите");
            }
        });
        btn_switch.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                Intent i = new Intent(getApplicationContext(), AdviceActivity.class);
                startActivity(i);
            }
        });
    }

    private boolean runtime_permissions() {
        if (Build.VERSION.SDK_INT >= 23 && ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, 100);

            return true;
        }
        return false;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 100) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED && grantResults[1] == PackageManager.PERMISSION_GRANTED) {
                enable_buttons();
            } else {
                runtime_permissions();
            }
        }
    }
}
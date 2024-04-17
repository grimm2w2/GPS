package com.example.gps;

import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.renderscript.Sampler;
import android.view.View;
import android.widget.Button;
import android.widget.Switch;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;

import java.util.Timer;
import java.util.TimerTask;

public class AdviceActivity extends AppCompatActivity {
    private Button btn_switch;
    private TextView txInfo;
    private BroadcastReceiver brWeatherUpdate;
    private BroadcastReceiver brWeatherUpdateBad;
    public String extraInfo = "Подождите информацию";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_advice);
        btn_switch = (Button) findViewById(R.id.button2);
        txInfo = (TextView) findViewById(R.id.textView1);
        txInfo.setText(extraInfo);
        brWeatherUpdate = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                extraInfo = (String) intent.getExtras().get("ExtraInfo");
                txInfo.setText(extraInfo);
            }
        };
        registerReceiver(brWeatherUpdate, new IntentFilter("weather_update"));
        brWeatherUpdateBad = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                Intent i = new Intent(getApplicationContext(), MainActivity.class);
                startActivity(i);
            }
        };
        registerReceiver(brWeatherUpdateBad, new IntentFilter("weather_update_bad"));
        enable_buttons();
    }

    protected void onResume() {
        super.onResume();
        AskRem();
    }

    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    private void enable_buttons() {

        btn_switch.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                Intent i = new Intent(getApplicationContext(), MainActivity.class);
                startActivity(i);
            }
        });
    }

    private void AskRem() {
        Intent i = new Intent("get_weather_info");
        sendBroadcast(i);
    }
}
package com.simge.safetydetectproject;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.huawei.hms.support.api.safetydetect.SafetyDetect;
import com.huawei.hms.support.api.safetydetect.SafetyDetectClient;

public class MainActivity extends AppCompatActivity{

    private final String APP_ID="102841133";
    private Button btnSysIntegrity,btnAppsCheck,btnUserDetect,btnURLCheck,btnWifiCheck;
    UserDetect userDetect;
    SysIntegrity sysIntegrity;
    AppsCheck appsCheck;
    URLCheck urlCheck;
    WifiCheck wifiCheck;
    private SafetyDetectClient client;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        client = SafetyDetect.getClient(this);

        btnSysIntegrity =findViewById(R.id.buttonSysIntegrity);
        btnAppsCheck =findViewById(R.id.buttonAppsCheck);
        btnUserDetect=findViewById(R.id.buttonUserDetect);
        btnURLCheck=findViewById(R.id.buttonURLCheck);
        btnWifiCheck=findViewById(R.id.buttonWifiCheck);

         btnSysIntegrity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
               sysIntegrity= new SysIntegrity(client,MainActivity.this,APP_ID);
               sysIntegrity.invoke();
            }
        });

         btnAppsCheck.setOnClickListener(new View.OnClickListener() {
             @Override
             public void onClick(View view) {
               appsCheck=new AppsCheck(client,MainActivity.this,APP_ID);
               appsCheck.invokeGetMaliciousApps();
             }
         });
         btnUserDetect.setOnClickListener(new View.OnClickListener() {
             @Override
             public void onClick(View view) {
                userDetect=new UserDetect(MainActivity.this,APP_ID);
                userDetect.detect();
             }
         });
         btnURLCheck.setOnClickListener(new View.OnClickListener() {
             @Override
             public void onClick(View view) {
                 urlCheck=new URLCheck(client,MainActivity.this,APP_ID);
                 urlCheck.callUrlCheckApi();
             }
         });
        btnWifiCheck.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                wifiCheck=new WifiCheck(client,MainActivity.this,APP_ID);
                wifiCheck.invokeGetWifiDetectStatus();
            }
        });
    }
}
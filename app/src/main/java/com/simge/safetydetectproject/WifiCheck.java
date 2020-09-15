package com.simge.safetydetectproject;

import android.app.AlertDialog;
import android.content.Context;
import android.util.Log;

import com.huawei.hmf.tasks.OnFailureListener;
import com.huawei.hmf.tasks.OnSuccessListener;
import com.huawei.hmf.tasks.Task;
import com.huawei.hms.common.ApiException;
import com.huawei.hms.support.api.entity.safetydetect.WifiDetectResponse;
import com.huawei.hms.support.api.safetydetect.SafetyDetectClient;
import com.huawei.hms.support.api.safetydetect.SafetyDetectStatusCodes;

public class WifiCheck {

    private SafetyDetectClient client;
    private static final String TAG= "Safety WIFICheck";
    Context context;
    String APP_ID;

    public WifiCheck(SafetyDetectClient client, Context context, String APP_ID) {
        this.client = client;
        this.context = context;
        this.APP_ID = APP_ID;
    }

    public void invokeGetWifiDetectStatus() {
        Log.i(TAG, "Start to getWifiDetectStatus!");
        Task task = client.getWifiDetectStatus();
        task.addOnSuccessListener(new OnSuccessListener<WifiDetectResponse>() {
            @Override
            public void onSuccess(WifiDetectResponse wifiDetectResponse) {
                int wifiDetectStatus = wifiDetectResponse.getWifiDetectStatus();
                showAlert("\n-1: Failed to obtain the Wi-Fi status. \n" + "0: No Wi-Fi is connected. \n" + "1: The connected Wi-Fi is secure. \n" + "2: The connected Wi-Fi is insecure." + "wifiDetectStatus is: " + wifiDetectStatus);
                Log.i(TAG, "\n-1: Failed to obtain the Wi-Fi status. \n" + "0: No Wi-Fi is connected. \n" + "1: The connected Wi-Fi is secure. \n" + "2: The connected Wi-Fi is insecure.");
                Log.i(TAG, "wifiDetectStatus is: " + wifiDetectStatus);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(Exception e) {
                if (e instanceof ApiException) {
                    ApiException apiException = (ApiException) e;
                    Log.e(TAG,
                            "Error: " + apiException.getStatusCode() + ":"
                                    + SafetyDetectStatusCodes.getStatusCodeString(apiException.getStatusCode()) + ": "
                                    + apiException.getStatusMessage());
                    showAlert("Error: " + apiException.getStatusCode() + ":"
                            + SafetyDetectStatusCodes.getStatusCodeString(apiException.getStatusCode()) + ": "
                            + apiException.getStatusMessage());
                } else {
                    Log.e(TAG, "ERROR! " + e.getMessage());
                    showAlert("ERROR! " + e.getMessage());
                }
            }
        });
    }
    public void showAlert(String message){
        AlertDialog alertDialog = new AlertDialog.Builder(context).create();
        alertDialog.setTitle("WifiCheck");
        alertDialog.setMessage(message);
        alertDialog.show();
    }
}

package com.simge.safetydetectproject;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import com.huawei.hmf.tasks.OnFailureListener;
import com.huawei.hmf.tasks.OnSuccessListener;
import com.huawei.hms.common.ApiException;
import com.huawei.hms.support.api.entity.safetydetect.UrlCheckResponse;
import com.huawei.hms.support.api.entity.safetydetect.UrlCheckThreat;
import com.huawei.hms.support.api.safetydetect.SafetyDetect;
import com.huawei.hms.support.api.safetydetect.SafetyDetectClient;
import com.huawei.hms.support.api.safetydetect.SafetyDetectStatusCodes;

import java.util.List;

public class URLCheck {

    private static final String TAG= "Safety URLCheck";
    Context context;
    String APP_ID;
    private SafetyDetectClient client;

    public URLCheck(SafetyDetectClient client, Context context, String APP_ID) {
        this.client = client;
        this.context = context;
        this.APP_ID = APP_ID;
    }

    public void callUrlCheckApi() {
        client.urlCheck("https://developer.huawei.com/consumer/en/doc/development/HMS-Guides/SafetyDetectWiFiDetectAPIDevelopment", APP_ID,
                // Specify url threat type
                UrlCheckThreat.MALWARE,
                UrlCheckThreat.PHISHING)
                .addOnSuccessListener(new OnSuccessListener<UrlCheckResponse>() {
                    /**
                     * Called after successfully communicating with the SafetyDetect API.
                     * The #onSuccess callback receives an
                     * {@link com.huawei.hms.support.api.entity.safetydetect.UrlCheckResponse} that contains a
                     * list of UrlCheckThreat that contains the threat type of the Url.
                     */
                    @Override
                    public void onSuccess(UrlCheckResponse urlCheckResponse) {
                        // Indicates communication with the service was successful.
                        // Identify any detected threats.
                        // Call getUrlCheckResponse method of UrlCheckResponse then you can get List<UrlCheckThreat> .
                        // If List<UrlCheckThreat> is empty , that means no threats found , else that means threats found.
                        List<UrlCheckThreat> list = urlCheckResponse.getUrlCheckResponse();
                        if (list.isEmpty()) {
                            // No threats found.
                            Log.i(TAG,"No Threats found!");
                        } else {
                            // Threats found!
                            Log.i(TAG,"Threats found!");
                        }
                    }

                })
                .addOnFailureListener(new OnFailureListener() {
                    /**
                     * Called when an error occurred when communicating with the SafetyDetect API.
                     */
                    @Override
                    public void onFailure(Exception e) {
                        // An error with the Huawei Mobile Service API contains some additional details.
                        String errorMsg;
                        if (e instanceof ApiException) {
                            ApiException apiException = (ApiException) e;
                            errorMsg = "Error: " +
                                    SafetyDetectStatusCodes.getStatusCodeString(apiException.getStatusCode()) + ": " +
                                    e.getMessage();

                            // You can use the apiException.getStatusCode() method to get the status code.
                            // Note: If the status code is SafetyDetectStatusCodes.CHECK_WITHOUT_INIT, you need to call initUrlCheck().
                        } else {
                            // Unknown type of error has occurred.
                            errorMsg = e.getMessage();
                        }
                        Log.d(TAG, errorMsg);
                        Toast.makeText(context.getApplicationContext(), errorMsg, Toast.LENGTH_SHORT).show();
                    }
                });

    }
}

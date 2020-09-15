package com.simge.safetydetectproject;

import android.app.AlertDialog;
import android.content.Context;
import android.util.Base64;
import android.util.Log;

import com.huawei.hmf.tasks.OnFailureListener;
import com.huawei.hmf.tasks.OnSuccessListener;
import com.huawei.hmf.tasks.Task;
import com.huawei.hms.common.ApiException;
import com.huawei.hms.support.api.entity.safetydetect.SysIntegrityResp;
import com.huawei.hms.support.api.safetydetect.SafetyDetect;
import com.huawei.hms.support.api.safetydetect.SafetyDetectClient;
import com.huawei.hms.support.api.safetydetect.SafetyDetectStatusCodes;

import org.json.JSONException;
import org.json.JSONObject;

import java.nio.charset.StandardCharsets;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;

public class SysIntegrity {

    private static final String TAG= "Safety SysIntegrity";
    Context context;
    String APP_ID;
    private SafetyDetectClient client;

    public SysIntegrity(SafetyDetectClient client,Context context, String APP_ID) {
        this.context = context;
        this.APP_ID = APP_ID;
        this.client=client;
    }
    public void invoke() {
        // TODO(developer): Change the nonce generation to include your own, used once value,
        // ideally from your remote server.
        byte[] nonce = new byte[24];
        try {
            SecureRandom random;
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                random = SecureRandom.getInstanceStrong();
            } else {
                random = SecureRandom.getInstance("SHA1PRNG");
            }
            random.nextBytes(nonce);
        } catch (
                NoSuchAlgorithmException e) {
            Log.e(TAG, e.getMessage());
        }
        // TODO(developer): Change your app ID. You can obtain your app ID in AppGallery Connect.
        Task task = client.sysIntegrity(nonce, APP_ID);
        task.addOnSuccessListener(new OnSuccessListener<SysIntegrityResp>() {
            @Override
            public void onSuccess(SysIntegrityResp response) {
                // Indicates communication with the service was successful.
                // Use response.getResult() to get the result data.
                String jwsStr = response.getResult();
                String[] jwsSplit = jwsStr.split("\\.");
                String jwsPayloadStr = jwsSplit[1];
                String payloadDetail = new String(Base64.decode(jwsPayloadStr.getBytes(StandardCharsets.UTF_8), Base64.URL_SAFE), StandardCharsets.UTF_8);
                try {
                    final JSONObject jsonObject = new JSONObject(payloadDetail);
                    final boolean basicIntegrity = jsonObject.getBoolean("basicIntegrity");
                    String isBasicIntegrity = String.valueOf(basicIntegrity);
                    String basicIntegrityResult = "Basic Integrity: " + isBasicIntegrity;
                    showAlert(basicIntegrityResult);
                    Log.i(TAG, basicIntegrityResult);
                    if (!basicIntegrity) {
                        String advice = "Advice: " + jsonObject.getString("advice");
                        Log.i("Advice log", advice);
                    }
                } catch (JSONException e) {
                    String errorMsg = e.getMessage();
                    showAlert(errorMsg + "unknown error");
                    Log.e(TAG, errorMsg != null ? errorMsg : "unknown error");
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(Exception e) {
                // An error occurred while communicating with the service.
                if (e instanceof ApiException) {
                    // An error with the HMS API contains some
                    // additional details.
                    ApiException apiException = (ApiException) e;
                    // You can retrieve the status code using
                    // the apiException.getStatusCode() method.
                    showAlert("Error: " + SafetyDetectStatusCodes.getStatusCodeString(apiException.getStatusCode()) + ": " + apiException.getMessage());
                    Log.e(TAG, "Error: " + SafetyDetectStatusCodes.getStatusCodeString(apiException.getStatusCode()) + ": " + apiException.getMessage());
                } else {
                    // A different, unknown type of error occurred.
                    showAlert("ERROR:"+e.getMessage());
                    Log.e(TAG, "ERROR:" + e.getMessage());
                }
            }
        });
    }
    public void showAlert(String message){
        AlertDialog alertDialog = new AlertDialog.Builder(context).create();
        alertDialog.setTitle("SysIntegrity");
        alertDialog.setMessage(message);
        alertDialog.show();
    }
}

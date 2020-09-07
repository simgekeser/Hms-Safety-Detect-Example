package com.simge.safetydetectproject;

import android.content.Context;
import android.util.Log;

import com.huawei.hmf.tasks.OnFailureListener;
import com.huawei.hmf.tasks.OnSuccessListener;
import com.huawei.hmf.tasks.Task;
import com.huawei.hms.common.ApiException;
import com.huawei.hms.support.api.entity.core.CommonCode;
import com.huawei.hms.support.api.entity.safetydetect.MaliciousAppsData;
import com.huawei.hms.support.api.entity.safetydetect.MaliciousAppsListResp;
import com.huawei.hms.support.api.safetydetect.SafetyDetect;
import com.huawei.hms.support.api.safetydetect.SafetyDetectClient;
import com.huawei.hms.support.api.safetydetect.SafetyDetectStatusCodes;

import java.util.List;

public class AppsCheck {

    private static final String TAG= "Safety AppsCheck";
    Context context;
    String APP_ID;
    private SafetyDetectClient client;

    public AppsCheck(SafetyDetectClient client,Context context, String APP_ID) {
        this.context = context;
        this.APP_ID = APP_ID;
        this.client=client;
    }
    public void invokeGetMaliciousApps() {
        Task task = client.getMaliciousAppsList();
        task.addOnSuccessListener(new OnSuccessListener<MaliciousAppsListResp>() {
            @Override
            public void onSuccess(MaliciousAppsListResp maliciousAppsListResp) {
                // Indicates communication with the service was successful.
                // Use resp.getMaliciousApps() to get malicious apps data.
                List<MaliciousAppsData> appsDataList = maliciousAppsListResp.getMaliciousAppsList();
                // Indicates get malicious apps was successful.
                if (maliciousAppsListResp.getRtnCode() == CommonCode.OK) {
                    if (appsDataList.isEmpty()) {
                        // Indicates there are no known malicious apps.
                        Log.i(TAG, "There are no known potentially malicious apps installed.");
                    } else {
                        Log.i(TAG, "Potentially malicious apps are installed!");
                        for (MaliciousAppsData maliciousApp : appsDataList) {
                            Log.i(TAG, "Information about a malicious app:");
                            // Use getApkPackageName() to get APK name of malicious app.
                            Log.i(TAG, "APK: " + maliciousApp.getApkPackageName());
                            // Use getApkSha256() to get APK sha256 of malicious app.
                            Log.i(TAG, "SHA-256: " + maliciousApp.getApkSha256());
                            // Use getApkCategory() to get category of malicious app.
                            // Categories are defined in AppsCheckConstants
                            Log.i(TAG, "Category: " + maliciousApp.getApkCategory());
                        }
                    }
                } else {
                    Log.e(TAG, "getMaliciousAppsList fialed: " + maliciousAppsListResp.getErrorReason());
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
                    // You can retrieve the status code using the apiException.getStatusCode() method.
                    Log.e(TAG, "Error: " + SafetyDetectStatusCodes.getStatusCodeString(apiException.getStatusCode()) + ": " + apiException.getStatusMessage());
                } else {
                    // A different, unknown type of error occurred.
                    Log.e(TAG, "ERROR: " + e.getMessage());
                }
            }
        });
    }
}

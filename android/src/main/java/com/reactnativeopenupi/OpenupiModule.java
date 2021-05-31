package com.reactnativeopenupi;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.net.Uri;

import androidx.annotation.NonNull;
import com.facebook.react.bridge.ActivityEventListener;
import com.facebook.react.bridge.Arguments;
import com.facebook.react.bridge.BaseActivityEventListener;
import com.facebook.react.bridge.Promise;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContextBaseJavaModule;
import com.facebook.react.bridge.ReactMethod;
import com.facebook.react.bridge.WritableMap;
import com.facebook.react.module.annotations.ReactModule;

import java.util.List;

@ReactModule(name = OpenupiModule.NAME)
public class OpenupiModule extends ReactContextBaseJavaModule {

  static final String NAME = "Openupi";
  private static final int REQUEST_CODE = 200;

  private String FAILURE = "FAILURE";
  private String PROCESS = "PROCESS";
  private String OrderID = "";
  private Promise promise;

  private final ActivityEventListener mActivityEventListener = new BaseActivityEventListener() {
    @Override
    public void onActivityResult(
      Activity activity,
      int requestCode,
      int resultCode,
      Intent data
    ) {
      if (data == null) {
        resolve(FAILURE, "No action taken");
        return;
      }

      if (requestCode != REQUEST_CODE) {
        resolve(FAILURE, "Request Code Mismatch");
        return;
      }

      resolve(data.getStringExtra("Status"), data.getStringExtra("response"));
    }
  };

  public OpenupiModule(ReactApplicationContext reactContext) {
    super(reactContext);
    reactContext.addActivityEventListener(mActivityEventListener);
  }

  @Override
  @NonNull
  public String getName() {
    return NAME;
  }

  @ReactMethod
  public void StartPayment(String url, String requestID, final Promise prm) {
    OrderID = requestID;

    Activity currentActivity = getCurrentActivity();
    promise = prm;

    Context currentContext = currentActivity.getApplicationContext();

    if (currentActivity == null) {
      resolve(FAILURE.toUpperCase(), "Activity does not exist");
      return;
    }

    try {
      Uri uri = Uri.parse(url);
      Intent intent = new Intent(Intent.ACTION_VIEW, uri);
      Intent chooser = Intent.createChooser(intent, "Pay with");

      //start activity with mentioned intent and as we need result we will use startActivityForResult

      if (isCallable(chooser, currentContext)) {
        currentActivity.startActivityForResult(chooser, REQUEST_CODE);
      } else {
        resolve(FAILURE, "UPI supporting app not installed");
      }
    } catch (Exception e) {
      resolve(FAILURE, "Activity does not exist");
    }
  }

  private boolean isCallable(Intent intent, Context context) {
    List<ResolveInfo> list = context
      .getPackageManager()
      .queryIntentActivities(intent, PackageManager.MATCH_DEFAULT_ONLY);
    return list.size() > 0;
  }

  private void resolve(String status, String message) {
    if (promise == null) {
      return;
    }

    WritableMap map = Arguments.createMap();
    map.putString("status", status.toUpperCase());
    map.putString("message", message);
    map.putString("orderID", OrderID);

    promise.resolve(map);
    promise = null;
  }
}

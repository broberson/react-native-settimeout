package com.broberson.rnsettimeout;

import java.util.Dictionary;
import java.util.Enumeration;
import java.util.Hashtable;
import android.os.Handler;
import android.os.PowerManager;
import android.util.Log;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContextBaseJavaModule;
import com.facebook.react.bridge.ReactMethod;
import com.facebook.react.bridge.Callback;
import com.facebook.react.bridge.LifecycleEventListener;
import com.facebook.react.modules.core.DeviceEventManagerModule;
import static android.content.Context.POWER_SERVICE;

public class RNSetTimeoutModule extends ReactContextBaseJavaModule {

  private PowerManager powerManager;
  private PowerManager.WakeLock wakeLock;
  private Dictionary handlers;
  private Dictionary runnables;

  private final ReactApplicationContext reactContext;

  private final LifecycleEventListener listener = new LifecycleEventListener(){
    @Override
    public void onHostResume() {
      if(!wakeLock.isHeld()) {
        wakeLock.acquire();
      }
    }

    @Override
    public void onHostPause() {
    }

    @Override
    public void onHostDestroy() {
      if(wakeLock.isHeld()) {
        wakeLock.release();
      }
    }
  };

  public RNSetTimeoutModule(ReactApplicationContext reactContext) {
    super(reactContext);

    this.reactContext = reactContext;
    this.powerManager = (PowerManager) getReactApplicationContext().getSystemService(POWER_SERVICE);
    this.wakeLock = powerManager.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "RNSetTimeout:wakelock");
    this.handlers = new Hashtable<Integer, Handler>();
    this.runnables = new Hashtable<Integer, Runnable>();
    reactContext.addLifecycleEventListener(listener);
  }

  @Override
  public String getName() {
    return "RNSetTimeout";
  }

  @ReactMethod
  public void setTimeout(final int id, final int timeout) {
    Handler oldHandler;
    Runnable oldRunnable;

    Integer key = Integer.valueOf(id);
    Handler handler = new Handler();
    Runnable runnable = new Runnable() {
      @Override
      public void run() {
        if(getReactApplicationContext().hasActiveCatalystInstance()) {
          clearTimeout(id);

          getReactApplicationContext()
              .getJSModule(DeviceEventManagerModule.RCTDeviceEventEmitter.class)
              .emit("RNSetTimeout.timeout", id);
        }
      }
    };

    oldHandler = (Handler) handlers.put(key, handler);
    oldRunnable = (Runnable) runnables.put(key, runnable);

    if(oldHandler != null && oldRunnable != null) {
      oldHandler.removeCallbacks(oldRunnable);
    }

    handler.postDelayed(runnable, timeout);
  }

  @ReactMethod
  public void clearTimeout(final int id) {

    Integer key = Integer.valueOf(id);
    Handler handler = (Handler) handlers.get(key);
    Runnable runnable = (Runnable) runnables.get(key);

    if(handler != null && runnable != null) {
      handler.removeCallbacks(runnable);
      handlers.remove(key);
      runnables.remove(key);
    }
  }

  public void clearAllTimeouts() {
    Handler handler;
    Runnable runnable;
    Integer key;

    Enumeration<Integer> keys = handlers.keys();

    while( keys.hasMoreElements() ) {
      key = keys.nextElement();
      handler = (Handler) handlers.get(key);
      runnable = (Runnable) runnables.get(key);

      if(handler != null && runnable != null) {
        handler.removeCallbacks(runnable);
      }
    }

    ((Hashtable)handlers).clear();
    ((Hashtable)runnables).clear();
  }

  @Override
  public void onCatalystInstanceDestroy() {
    if(wakeLock.isHeld()) {
      wakeLock.release();
    }

    clearAllTimeouts();
    super.onCatalystInstanceDestroy();
  }
}

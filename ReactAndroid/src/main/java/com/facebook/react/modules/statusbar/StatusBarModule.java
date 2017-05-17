/**
 * Copyright (c) 2015-present, Facebook, Inc.
 * All rights reserved.
 * <p/>
 * This source code is licensed under the BSD-style license found in the
 * LICENSE file in the root directory of this source tree. An additional grant
 * of patent rights can be found in the PATENTS file in the same directory.
 */

package com.facebook.react.modules.statusbar;

import android.animation.ArgbEvaluator;
import android.animation.ValueAnimator;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.os.Build;
import android.support.v4.view.ViewCompat;
import android.view.View;
import android.view.WindowInsets;
import android.view.WindowManager;

import com.facebook.common.logging.FLog;
import com.facebook.react.bridge.NativeModule;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContextBaseJavaModule;
import com.facebook.react.bridge.ReactMethod;
import com.facebook.react.bridge.UiThreadUtil;
import com.facebook.react.common.MapBuilder;
import com.facebook.react.common.ReactConstants;
import com.facebook.react.module.annotations.ReactModule;
import com.facebook.react.uimanager.PixelUtil;

import java.util.Map;

import javax.annotation.Nullable;

/**
 * {@link NativeModule} that allows changing the appearance of the status bar.
 */
@ReactModule(name = "StatusBarManager")
public class StatusBarModule extends ReactContextBaseJavaModule {

  private static final String HEIGHT_KEY = "HEIGHT";

  public StatusBarModule(ReactApplicationContext reactContext) {
    super(reactContext);
  }

  @Override
  public String getName() {
    return "StatusBarManager";
  }

  @Override
  public @Nullable Map<String, Object> getConstants() {
    final Context context = getReactApplicationContext();
    final int heightResId = context.getResources()
      .getIdentifier("status_bar_height", "dimen", "android");
    final float height = heightResId > 0 ?
      PixelUtil.toDIPFromPixel(context.getResources().getDimensionPixelSize(heightResId)) :
      0;

    return MapBuilder.<String, Object>of(
      HEIGHT_KEY, height);
  }

  @ReactMethod
  public void setColor(final int color, final boolean animated) {
    final Activity activity = getCurrentActivity();
    if (activity == null) {
      FLog.w(ReactConstants.TAG, "StatusBarModule: Ignored status bar change, current activity is null.");
      return;
    }

    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
      UiThreadUtil.runOnUiThread(
        new Runnable() {
          @TargetApi(Build.VERSION_CODES.LOLLIPOP)
          @Override
          public void run() {
            if (animated) {
              int curColor = activity.getWindow().getStatusBarColor();
              ValueAnimator colorAnimation = ValueAnimator.ofObject(
                new ArgbEvaluator(), curColor, color);

              colorAnimation.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public void onAnimationUpdate(ValueAnimator animator) {
                  activity.getWindow().setStatusBarColor((Integer) animator.getAnimatedValue());
                }
              });
              colorAnimation
                .setDuration(300)
                .setStartDelay(0);
              colorAnimation.start();
            } else {
              activity.getWindow().setStatusBarColor(color);
            }
          }
        });
    }
  }

  @ReactMethod
  public void setTranslucent(final boolean translucent) {
    final Activity activity = getCurrentActivity();
    if (activity == null) {
      FLog.w(ReactConstants.TAG, "StatusBarModule: Ignored status bar change, current activity is null.");
      return;
    }

    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
      UiThreadUtil.runOnUiThread(
        new Runnable() {
          @TargetApi(Build.VERSION_CODES.LOLLIPOP)
          @Override
          public void run() {
            if (translucent) {
              activity.getWindow().setFlags(
                WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS,
                WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
              activity.getWindow().getDecorView().setSystemUiVisibility(
                  View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
              activity.getWindow().setStatusBarColor(Color.TRANSPARENT);
            } else {
              activity.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
              activity.getWindow().getDecorView().setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
            }
          }
        });
    }
  }

  @ReactMethod
  public void setHidden(final boolean hidden) {
    final Activity activity = getCurrentActivity();
    if (activity == null) {
      FLog.w(ReactConstants.TAG, "StatusBarModule: Ignored status bar change, current activity is null.");
      return;
    }
    UiThreadUtil.runOnUiThread(
      new Runnable() {
        @Override
        public void run() {
          if (hidden) {
            activity.getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
            activity.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FORCE_NOT_FULLSCREEN);
          } else {
            activity.getWindow().addFlags(WindowManager.LayoutParams.FLAG_FORCE_NOT_FULLSCREEN);
            activity.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
          }
        }
      });
  }

  @ReactMethod
  public void setStyle(final String style) {
    final Activity activity = getCurrentActivity();
    if (activity == null) {
      FLog.w(ReactConstants.TAG, "StatusBarModule: Ignored status bar change, current activity is null.");
      return;
    }

    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
      UiThreadUtil.runOnUiThread(
        new Runnable() {
          @TargetApi(Build.VERSION_CODES.M)
          @Override
          public void run() {
            View decorView = activity.getWindow().getDecorView();
            decorView.setSystemUiVisibility(
              style.equals("dark-content") ? View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR : 0);
          }
        }
      );
    }
  }
}

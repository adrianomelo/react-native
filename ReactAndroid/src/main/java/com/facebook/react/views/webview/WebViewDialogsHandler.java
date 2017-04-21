package com.facebook.react.views.webview;

import android.content.Context;
import android.webkit.HttpAuthHandler;
import android.webkit.WebView;

public class WebViewDialogsHandler {
  private Context mContext;

  WebViewDialogsHandler(Context context) {
    mContext = context;
  }

  public void showHttpAuthentication(final WebView view, final HttpAuthHandler handler, String host, String realm) {
    HttpAuthenticationDialog mHttpAuthenticationDialog = new HttpAuthenticationDialog(mContext, host, realm);
    mHttpAuthenticationDialog.setOkListener(new HttpAuthenticationDialog.OkListener() {
      public void onOk(String host, String realm, String username, String password) {
        view.setHttpAuthUsernamePassword(host, realm, username, password);
        handler.proceed(username, password);
      }
    });
    mHttpAuthenticationDialog.setCancelListener(new HttpAuthenticationDialog.CancelListener() {
      public void onCancel() {
        handler.cancel();
      }
    });
    mHttpAuthenticationDialog.show();
  }
}

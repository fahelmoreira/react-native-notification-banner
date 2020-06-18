
package ui.notificationbanner;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.ActivityManager;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.media.Image;
import android.net.Uri;
import android.os.StrictMode;
import android.util.Log;
import android.view.View;

import androidx.annotation.Nullable;

import com.facebook.common.executors.CallerThreadExecutor;
import com.facebook.common.references.CloseableReference;
import com.facebook.datasource.DataSource;
import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.imagepipeline.core.ImagePipeline;
import com.facebook.imagepipeline.datasource.BaseBitmapDataSubscriber;
import com.facebook.imagepipeline.image.CloseableImage;
import com.facebook.imagepipeline.request.ImageRequest;
import com.facebook.imagepipeline.request.ImageRequestBuilder;
import com.facebook.imagepipeline.transformation.BitmapTransformation;
import com.facebook.infer.annotation.Assertions;
import com.facebook.react.views.imagehelper.ImageSource;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Method;
import java.net.MalformedURLException;
import java.net.URL;

import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContextBaseJavaModule;
import com.facebook.react.bridge.ReactMethod;
import com.facebook.react.bridge.Callback;
import com.facebook.react.bridge.ReadableMap;
import com.facebook.react.views.text.ReactFontManager;
import com.tapadoo.alerter.Alerter;
import com.tapadoo.alerter.OnHideAlertListener;

public class RNNotificationBannerModule extends ReactContextBaseJavaModule {

  private final ReactApplicationContext reactContext;

  private Callback onClickCallback = null;
  private Callback onHideCallback = null;

  public RNNotificationBannerModule(ReactApplicationContext reactContext) {
    super(reactContext);
    this.reactContext = reactContext;
  }

  @Override
  public String getName() {
    return "RNNotificationBanner";
  }

  @ReactMethod
  public void Show(final ReadableMap props, final Callback onClick, final Callback onHide) {

    int type = props.getInt("type");

    String title = props.getString("title");
    int titleSize = props.getInt("titleSize");
    String titleColor = props.getString("titleColor");

    String subTitle = props.getString("subTitle");
    int subTitleSize = props.getInt("subTitleSize");
    String subTitleColor = props.getString("subTitleColor");

    int duration = props.getInt("duration");
    boolean enableProgress = props.getBoolean("enableProgress");
    String tintColorValue = props.getString("tintColor");

    boolean dismissable = props.getBoolean("dismissable");

    boolean withIcon = props.getBoolean("withIcon");
    ReadableMap icon = props.hasKey("icon") ? props.getMap("icon") : null;

    ReadableMap imageString = props.hasKey("image") ? props.getMap("image") : null;


    Drawable iconDrawable = null;

    int tintColor = 0;

    onClickCallback = onClick;
    onHideCallback = onHide;

    if (withIcon) {
      if (icon != null && icon.toHashMap().size() > 0) {
        try {
          Class<?> clazz = Class.forName("prscx.imagehelper.RNImageHelperModule"); //Controller A or B
          Class params[] = {ReadableMap.class};
          Method method = clazz.getDeclaredMethod("GenerateImage", params);

          iconDrawable = (Drawable) method.invoke(null, icon);
        } catch (Exception e) {
        }
      } else if(imageString != null){
        try {
          String uri = imageString.getString("uri");
          ReactApplicationContext currActivity = this.getReactApplicationContext();
          Context context2 = Assertions.assertNotNull(currActivity);
          ImageSource imageSource = new ImageSource(context2, uri);
          Uri imageUri = imageSource.getUri();
          Bitmap bmp = null;
          if(!imageSource.isResource()){
            URL url = new URL(uri);
            bmp = BitmapFactory.decodeStream(url.openConnection().getInputStream());
          }else{
            int resourceId = context2.getResources().getIdentifier(uri, "drawable", context2.getPackageName());
            bmp = BitmapFactory.decodeResource(context2.getResources(), resourceId);
          }
          iconDrawable = new BitmapDrawable(context2.getResources(), bmp);
        } catch (Exception e) {
          e.printStackTrace();
        }
//        iconDrawable =
      }
    }

    if (titleColor != null && titleColor.length() > 0) {
//      config.setTextColor(Color.parseColor(titleColor));
    }
    if (titleSize != 0) {
//      config.setTextSize(titleSize);
    }


    if (tintColorValue != null && tintColorValue.length() > 0) {
      tintColor = Color.parseColor(tintColorValue);
    }

    Alerter alerter = Alerter.create(getCurrentActivity());
      alerter = alerter.setTitle(title);
      alerter = alerter.setText(subTitle);
      alerter.setIconColorFilter(0)

      if (iconDrawable != null && enableProgress == false) {
        alerter = alerter.setIcon(iconDrawable);
      } else {
        alerter = alerter.hideIcon();
      }

      if (tintColor != 0) {
        alerter = alerter.setBackgroundColorInt(tintColor);
      }

      if (!dismissable) {
        alerter = alerter.setDismissable(dismissable);
      }

      alerter = alerter.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View view) {
          if (onClickCallback != null) {
            onClickCallback.invoke();

            onClickCallback = null;
            onHideCallback = null;
          }
        }
      });

      alerter = alerter.setOnHideListener(new OnHideAlertListener() {
        @Override
        public void onHide() {
          if (onHideCallback != null) {
            onHideCallback.invoke();

            onHideCallback = null;
            onClickCallback = null;
          }
        }
      });

      if (enableProgress) {
        alerter.enableProgress(true);
        alerter.setProgressColorInt(Color.WHITE);
      }

      if (duration != 0) {
        alerter.setDuration(duration);
      }

      alerter.show();
  }

  @ReactMethod
  public void Dismiss() {
    Alerter.clearCurrent(getCurrentActivity());
  }
}

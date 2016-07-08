package com.neopi.demo;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.Shader;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Environment;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.assist.ImageSize;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;

public class PreviewActivity extends Activity {

  private ImageView ivPreView;
  private String extraJsonData;

  public static final String DRAW_JSON = "drawable_json";

  @Override protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_preview);

    handIntent();
    ivPreView = (ImageView) findViewById(R.id.iv_preview);
    buildBitmap();
  }

  /**
   * 这里读取数据使用了两种方式
   * 根据个人项目而取舍
   */
  private void handIntent() {
    // 读取本地文件中的JSON数据
    if ( TextUtils.isEmpty(extraJsonData) ){
      File file = new File(Environment.getExternalStorageDirectory()+"/DrawDemo/json.txt");
      try {
        BufferedReader br = new BufferedReader(new FileReader(file));

        StringBuffer sb = new StringBuffer();
        String str ;
        while((str = br.readLine() ) != null){
          sb.append(str);
        }
        extraJsonData = sb.toString();
        br.close();
      } catch (Exception e) {
        e.printStackTrace();
      }
    }
    // 使用本地保存的数据
    if (TextUtils.isEmpty(extraJsonData)){
      extraJsonData = SharedPrefUtils.getInstance().getStringPref(this,DRAW_JSON,"");
    }

    if ( TextUtils.isEmpty(extraJsonData) ){
      // 这里使用传递过来的数据
      Intent mIntent = getIntent();
      extraJsonData = mIntent.getStringExtra(MainActivity.EXTRA_DATA);
    }
    Log.e("TAG", "json data:" + extraJsonData);
  }

  private void buildBitmap() {
    final ShareDataInfo mSharedDataInfo =
        MainActivity.gson.fromJson(extraJsonData, ShareDataInfo.class);
    ImageSize imageSize =
        new ImageSize(mSharedDataInfo.avatarOption.width, mSharedDataInfo.avatarOption.height);
    DisplayImageOptions imageOption =
        new DisplayImageOptions.Builder().imageScaleType(ImageScaleType.EXACTLY_STRETCHED)
            .cacheOnDisk(true)
            .build();
    ImageLoader.getInstance()
        .loadImage(mSharedDataInfo.avatarOption.url, imageSize, imageOption,
            new SimpleImageLoadingListener() {
              @Override
              public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
                super.onLoadingComplete(imageUri, view, loadedImage);
                loadImage(mSharedDataInfo, loadedImage);
              }

              @Override
              public void onLoadingFailed(String imageUri, View view, FailReason failReason) {
                super.onLoadingFailed(imageUri, view, failReason);
              }
            });
  }

  private void loadImage(ShareDataInfo mSharedDataInfo, Bitmap avatarBmp) {
    if (avatarBmp == null) {
      avatarBmp = BitmapFactory.decodeResource(getResources(), R.drawable.avatar);
    }

    Bitmap bgBmp = BitmapFactory.decodeResource(getResources(), R.drawable.background);
    Log.e("TAG", "bg width:" + bgBmp.getWidth() + ",height:" + bgBmp.getHeight());
    Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
    paint.setAntiAlias(true);

    Bitmap canvasBmp =
        Bitmap.createBitmap(bgBmp.getWidth(), bgBmp.getHeight(), Bitmap.Config.RGB_565);
    Canvas canvas = new Canvas(canvasBmp);
    canvas.drawBitmap(bgBmp, 0, 0, paint);

    if (avatarBmp != null) {
      BitmapShader bitmapShader =
          new BitmapShader(avatarBmp, Shader.TileMode.CLAMP, Shader.TileMode.CLAMP);
      RectF mRectfSrc = new RectF();
      mRectfSrc.set(0, 0, avatarBmp.getWidth(), avatarBmp.getHeight());
      Log.e("TAG", "avatar width:" + avatarBmp.getWidth() + ",height:" + avatarBmp.getHeight());

      Paint mSrcPaint = new Paint();
      mSrcPaint.setAntiAlias(true);
      mSrcPaint.setShader(bitmapShader);
      mSrcPaint.setFilterBitmap(true);
      mSrcPaint.setDither(true);

      RectF mDsc = new RectF();
      int marginLeft = mSharedDataInfo.avatarOption.pointX - avatarBmp.getWidth() / 2;
      int marginTop = mSharedDataInfo.avatarOption.pointY - avatarBmp.getHeight() / 2;
      Log.e("TAG", "avatar marginLeft:" + marginLeft + ",marginTop:" + marginTop);
      mDsc.set(marginLeft, marginTop, avatarBmp.getWidth() + marginLeft,
          avatarBmp.getHeight() + marginTop);
      Matrix matrix = new Matrix();
      matrix.setRectToRect(mRectfSrc, mDsc, Matrix.ScaleToFit.FILL);
      bitmapShader.setLocalMatrix(matrix);
      canvas.drawRoundRect(mDsc, mSharedDataInfo.avatarOption.width,
          mSharedDataInfo.avatarOption.height, mSrcPaint);
    }

    Paint textPaint = new Paint();
    textPaint.setAntiAlias(true);
    Typeface typeFace;
    for (ShareDataInfo.TextOption textOption : mSharedDataInfo.textOptions) {
      textPaint.setColor(textOption.textColor);
      textPaint.setFakeBoldText(textOption.bold);
      textPaint.setTextSize(textOption.textSize);
      // TODO 此处 ! 不要忘记删除,这里只是做测试用的
      if ( !TextUtils.isEmpty(textOption.typeFace )) {
        typeFace = Typeface.DEFAULT;
      } else {
        typeFace = Typeface.createFromAsset(getAssets(), "fonts/DINCond-Medium.otf");

        //File file = new File(Environment.getExternalStorageDirectory()
        //    + "/mibbs/fonts"
        //    + File.separator
        //    + "DINCond-Medium.otf");
        //typeFace = Typeface.createFromFile(file);
      }
      drawText(textOption.content, canvas, textPaint, textOption.centreX, textOption.baselineY,
          typeFace);
    }
    ivPreView.setImageBitmap(canvasBmp);
  }

  private void drawText(String text, Canvas canvas, Paint textPaint, float x, float y,
      Typeface typeface) {
    textPaint.setTypeface(typeface);
    float textWidth = textPaint.measureText(text);
    canvas.drawText(text, x - textWidth / 2, y, textPaint);
    Log.i("TAG", "x:" + (x - textWidth / 2) + ",baselineY:" + y);
  }

  @Override protected void onDestroy() {
    super.onDestroy();
    System.gc();
  }
}

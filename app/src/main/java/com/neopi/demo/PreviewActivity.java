package com.neopi.demo;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.Shader;
import android.graphics.Typeface;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.assist.ImageSize;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;

public class PreviewActivity extends Activity {

  private ImageView ivPreView;
  private String extraJsonData;

  private final String avatarUrl = "http://cdn.fds.api.xiaomi.com/b2c-bbs/cn/547829071/avatar.jpg";

  @Override protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_preview);

    handIntent();
    ivPreView = (ImageView) findViewById(R.id.iv_preview);
    buildBitmap();
  }

  private void handIntent() {
    Intent mIntent = getIntent();
    extraJsonData = mIntent.getStringExtra(MainActivity.EXTRA_DATA);
    Log.e("TAG", "json data:" + extraJsonData);
  }

  private void buildBitmap() {
    final ShareDataInfo mSharedDataInfo =
        MainActivity.gson.fromJson(extraJsonData, ShareDataInfo.class);
    //        Bitmap avatarBmp = BitmapFactory.decodeResource(getResources(), R.drawable.avatar);
    ImageSize imageSize =
        new ImageSize(mSharedDataInfo.avatarOption.width, mSharedDataInfo.avatarOption.height);
    DisplayImageOptions imageOption =
        new DisplayImageOptions.Builder().imageScaleType(ImageScaleType.EXACTLY_STRETCHED)
            .cacheOnDisk(true)
            .build();
    ImageLoader.getInstance()
        .loadImage(avatarUrl, imageSize, imageOption, new SimpleImageLoadingListener() {
          @Override public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
            super.onLoadingComplete(imageUri, view, loadedImage);
            loadImage(mSharedDataInfo, loadedImage);
          }

          @Override public void onLoadingFailed(String imageUri, View view, FailReason failReason) {
            super.onLoadingFailed(imageUri, view, failReason);
          }
        });
  }

  private void loadImage(ShareDataInfo mSharedDataInfo, Bitmap avatarBmp) {
    if ( avatarBmp == null){
      avatarBmp = BitmapFactory.decodeResource(getResources(),R.drawable.avatar);
    }

    Bitmap bgBmp = BitmapFactory.decodeResource(getResources(), R.drawable.background);
    Log.e("TAG", "bg width:" + bgBmp.getWidth() + ",height:" + bgBmp.getHeight());
    Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
    paint.setAntiAlias(true);

    Bitmap canvasBmp =
        Bitmap.createBitmap(bgBmp.getWidth(), bgBmp.getHeight(), Bitmap.Config.RGB_565);
    Canvas canvas = new Canvas(canvasBmp);
    canvas.drawBitmap(bgBmp, 0, 0, paint);

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

    for (ShareDataInfo.TextOption textOption : mSharedDataInfo.textOptions) {
      Paint textPaint = new Paint();
      textPaint.setAntiAlias(true);
      textPaint.setColor(Color.WHITE);
      textPaint.setFakeBoldText(textOption.bold);
      textPaint.setTextSize(textOption.textSize);
      Typeface typeFace = Typeface.DEFAULT;
      if (!TextUtils.isEmpty(textOption.typeFace)) {
        typeFace = Typeface.createFromAsset(PreviewActivity.this.getAssets(), textOption.typeFace);
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
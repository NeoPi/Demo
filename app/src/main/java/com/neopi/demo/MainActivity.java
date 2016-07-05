package com.neopi.demo;

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
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import com.google.gson.Gson;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.assist.ImageSize;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

  public static final String EXTRA_DATA = "extra_data";
  private final String avatarUrl = "http://cdn.fds.api.xiaomi.com/b2c-bbs/cn/547829071/avatar.jpg";

  private ImageView iv;
  private Button mDrawBtn;
  private Button mPreviewBtn;

  private ShareDataInfo shareDataInfo;
  public static Gson gson = new Gson();
  private String data = "";

  private ShareDataInfo mShareDataInfo;
  private ShareDataInfo.AvatarOption mAvatarOption;
  private List<ShareDataInfo.TextOption> mTextOptions;
  private List<ShareDataInfo.ChannelInfo> mChanneInfos;

  private SeekBar mSeekX; // 用来控制X轴坐标
  private SeekBar mSeekY; // 用来控制Y轴坐标
  private CheckBox mCheckX;// 用来确认X轴居中
  private CheckBox mCheckY;// 用来确认Y轴居中
  private CheckBox mCheckBold;// 用来控制文字是否加粗
  private EditText mEtContent; // 用来编辑文字内容
  private EditText mEtSize; // 文字大小

  private LinearLayout llX;
  private LinearLayout llY;
  private LinearLayout llEditText; // 编辑文字属性布局
  private Bitmap bgBmp;

  private boolean avatarIsEdit;
  private boolean textIsEdit;

  @Override protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    requestWindowFeature(Window.FEATURE_NO_TITLE);
    setContentView(R.layout.main_activity);

    initView();
    initData();
    registerForContextMenu(iv);
  }

  private void initView() {
    bgBmp = BitmapFactory.decodeResource(getResources(), R.drawable.background);
    iv = (ImageView) findViewById(R.id.iv_bg);
    mSeekX = (SeekBar) findViewById(R.id.sb_x);
    mSeekY = (SeekBar) findViewById(R.id.seek_bar_Y);
    mCheckX = (CheckBox) findViewById(R.id.check_box_x);
    mCheckY = (CheckBox) findViewById(R.id.check_box_Y);
    mCheckBold = (CheckBox) findViewById(R.id.check_text_bold);

    mDrawBtn = (Button) findViewById(R.id.btn_start_clear);
    mPreviewBtn = (Button) findViewById(R.id.btn_start_preview);
    llX = (LinearLayout) findViewById(R.id.ll_x);
    llY = (LinearLayout) findViewById(R.id.ll_y);
    llEditText = (LinearLayout) findViewById(R.id.ll_edit_text);

    mEtContent = (EditText) findViewById(R.id.edit_text);
    mEtSize = (EditText) findViewById(R.id.edit_text_size);

    mSeekX.setMax(bgBmp.getWidth());
    mSeekY.setMax(bgBmp.getHeight());

    mDrawBtn.setOnClickListener(mOnclick);
    mPreviewBtn.setOnClickListener(mOnclick);
    mSeekX.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
      @Override public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
        if (avatarIsEdit) {
          mAvatarOption.pointX = progress;
          mCheckX.setChecked(false);
        } else if (textIsEdit) {
          if (mTextOptions != null && mTextOptions.size() > 0) {
            int size = mTextOptions.size();
            ShareDataInfo.TextOption textOption = mTextOptions.get(size - 1);
            textOption.centreX = progress;
          }
        }
        drawWithJson(buildToJson());
      }

      @Override public void onStartTrackingTouch(SeekBar seekBar) {

      }

      @Override public void onStopTrackingTouch(SeekBar seekBar) {

      }
    });

    mSeekY.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
      @Override public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
        if (avatarIsEdit) {
          mAvatarOption.pointY = progress;
          mCheckY.setChecked(false);
        } else if (mTextOptions != null && mTextOptions.size() > 0) {
          int size = mTextOptions.size();
          ShareDataInfo.TextOption textOption = mTextOptions.get(size - 1);
          textOption.baselineY = progress;
        }
        drawWithJson(buildToJson());
      }

      @Override public void onStartTrackingTouch(SeekBar seekBar) {

      }

      @Override public void onStopTrackingTouch(SeekBar seekBar) {

      }
    });

    // 水平剧中设置
    mCheckX.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
      @Override public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        mSeekX.setProgress(bgBmp.getWidth() / 2);
      }
    });

    // 垂直剧中设置
    mCheckY.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
      @Override public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        mSeekY.setProgress(bgBmp.getHeight() / 2);
      }
    });

    // 文字内容编辑
    mEtContent.addTextChangedListener(new TextWatcher() {
      @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {

      }

      @Override public void onTextChanged(CharSequence s, int start, int before, int count) {

      }

      @Override public void afterTextChanged(Editable s) {
        if (mTextOptions != null && mTextOptions.size() > 0) {
          int size = mTextOptions.size();
          ShareDataInfo.TextOption textOption = mTextOptions.get(size - 1);
          textOption.content = s.toString();
        }
      }
    });

    // 文字大小编辑
    mEtSize.addTextChangedListener(new TextWatcher() {
      @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {

      }

      @Override public void onTextChanged(CharSequence s, int start, int before, int count) {

      }

      @Override public void afterTextChanged(Editable s) {
        if (mTextOptions != null && mTextOptions.size() > 0) {
          int size = mTextOptions.size();
          ShareDataInfo.TextOption textOption = mTextOptions.get(size - 1);
          try {
            textOption.textSize = Integer.parseInt(s.toString());
          } catch (Exception e) {
            textOption.textSize = textSize;
            e.printStackTrace();
          }
        }
      }
    });

    // 文字加粗设置
    mCheckBold.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
      @Override public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        if (mTextOptions != null && mTextOptions.size() > 0) {
          int size = mTextOptions.size();
          ShareDataInfo.TextOption textOption = mTextOptions.get(size - 1);
          textOption.bold = isChecked;
        }
      }
    });
  }

  private void initData() {
    mShareDataInfo = new ShareDataInfo();
    mAvatarOption = new ShareDataInfo.AvatarOption();
    mTextOptions = new ArrayList<>();
    mChanneInfos = new ArrayList<>();

    data = buildJsonData();
    shareDataInfo = gson.fromJson(data, ShareDataInfo.class);
  }

  View.OnClickListener mOnclick = new View.OnClickListener() {
    @Override public void onClick(View v) {
      switch (v.getId()) {
        case R.id.btn_start_clear:
          clear();
          //                    drawWithJson();
          break;
        case R.id.btn_start_preview:
          previewWithJson();
          break;
      }
    }
  };

  private void clear() {

  }

  /**
   * 预览添加数据的效果
   */
  private void previewWithJson() {
    Intent mIntent = new Intent();
    mIntent.setClass(this, PreviewActivity.class);
    mIntent.putExtra(EXTRA_DATA, buildToJson());
    startActivity(mIntent);
  }

  private String buildToJson() {
    mShareDataInfo.avatarOption = mAvatarOption;
    mShareDataInfo.textOptions = mTextOptions;
    mShareDataInfo.channelInfos = mChanneInfos;
    return gson.toJson(mShareDataInfo);
  }

  private void drawWithJson(String jsonData) {
    final ShareDataInfo mSharedDataInfo = MainActivity.gson.fromJson(jsonData, ShareDataInfo.class);
    ImageSize imageSize =
        new ImageSize(mSharedDataInfo.avatarOption.width, mSharedDataInfo.avatarOption.height);
    DisplayImageOptions imageOption =
        new DisplayImageOptions.Builder().imageScaleType(ImageScaleType.EXACTLY_STRETCHED)
            .cacheOnDisk(true)
            .build();
    ImageLoader.getInstance()
        .loadImage(mSharedDataInfo.avatarOption.url, imageSize, imageOption, new SimpleImageLoadingListener() {
          @Override public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
            super.onLoadingComplete(imageUri, view, loadedImage);
            loadImage(mSharedDataInfo, loadedImage);
          }

          @Override public void onLoadingFailed(String imageUri, View view, FailReason failReason) {
            super.onLoadingFailed(imageUri, view, failReason);
          }
        });
  }

  Bitmap canvasBmp;

  private void loadImage(ShareDataInfo mSharedDataInfo, Bitmap avatarBmp) {

    Log.e("TAG", "bg width:" + bgBmp.getWidth() + ",height:" + bgBmp.getHeight());
    Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
    paint.setAntiAlias(true);

    canvasBmp = Bitmap.createBitmap(bgBmp.getWidth(), bgBmp.getHeight(), Bitmap.Config.RGB_565);
    Canvas canvas = new Canvas(canvasBmp);
    canvas.drawBitmap(bgBmp, 0, 0, paint);
    canvas.save();

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

    for (ShareDataInfo.TextOption textOption : mSharedDataInfo.textOptions) {
      Paint textPaint = new Paint();
      textPaint.setAntiAlias(true);
      textPaint.setColor(Color.WHITE);
      textPaint.setFakeBoldText(textOption.bold);
      textPaint.setTextSize(textOption.textSize);
      Typeface typeFace = Typeface.DEFAULT;
      if (!TextUtils.isEmpty(textOption.typeFace)) {
        typeFace = Typeface.createFromAsset(MainActivity.this.getAssets(), textOption.typeFace);
      }
      drawText(textOption.content, canvas, textPaint, textOption.centreX, textOption.baselineY,
          typeFace);
    }
    iv.setImageBitmap(canvasBmp);
    canvas.restore();
  }

  private void drawText(String text, Canvas canvas, Paint textPaint, float x, float y,
      Typeface typeface) {
    textPaint.setTypeface(typeface);
    float textWidth = textPaint.measureText(text);
    canvas.drawText(text, x - textWidth / 2, y, textPaint);
  }

  // test json
  private String buildJsonData() {
    ShareDataInfo dataInfo = new ShareDataInfo();
    dataInfo.hd_mode = "steps";
    dataInfo.callback = "shareCallBack";

    // 构造头像参数
    ShareDataInfo.AvatarOption avatarOption = new ShareDataInfo.AvatarOption();
    avatarOption.width = 100;
    avatarOption.height = 100;
    avatarOption.pointX = 540;
    avatarOption.pointY = 763;
    dataInfo.avatarOption = avatarOption;

    // 构造文字参数
    List<ShareDataInfo.TextOption> textOptions = new ArrayList<>();
    ShareDataInfo.TextOption textOption1 = new ShareDataInfo.TextOption();
    textOption1.textColor = 0xFFFFFFFF;
    textOption1.textSize = 40;
    textOption1.baselineY = 460;
    textOption1.centreX = 540;
    textOption1.content = "NeoPi";
    textOptions.add(textOption1);

    ShareDataInfo.TextOption textOption2 = new ShareDataInfo.TextOption();
    textOption2.textColor = 0xFFFFFFFF;
    textOption2.textSize = 36;
    textOption2.baselineY = 580;
    textOption2.centreX = 540;
    textOption2.content = "第21天";
    textOptions.add(textOption2);

    ShareDataInfo.TextOption textOption3 = new ShareDataInfo.TextOption();
    textOption3.textColor = 0xFFFFFFFF;
    textOption3.textSize = 150;
    textOption3.baselineY = 670;
    textOption3.centreX = 540;
    textOption3.content = "210000";
    //        textOption3.typeFace = "fonts/DINCond-Medium.ttf";
    textOptions.add(textOption3);
    dataInfo.textOptions = textOptions;

    // 构造渠道信息
    List<ShareDataInfo.ChannelInfo> channelInfos = new ArrayList<>();
    ShareDataInfo.ChannelInfo channelInfo1 = new ShareDataInfo.ChannelInfo();
    channelInfo1.channel = "data";
    channelInfo1.content = "坚持21天，挑战21万步。每天10000步，小米社区喊你一起走！";
    channelInfo1.title = "坚持21天，挑战21万步";
    channelInfo1.url = "http://bbs.xiaomi.cn/step21/index.html";
    channelInfo1.imgUrl = "http://s1.bbs.xiaomi.cn/statics_app/images/hd/20160622/wb_share.jpg";
    channelInfo1.icon = "http://s1.bbs.xiaomi.cn/statics_app/images/hd/20160622/wx_icon";
    channelInfos.add(channelInfo1);

    ShareDataInfo.ChannelInfo channelInfo2 = new ShareDataInfo.ChannelInfo();
    channelInfo2.channel = "wx";
    channelInfo2.content = "每天送一台手机，小米社区喊你动起来！";
    channelInfo2.title = "坚持21天，挑战21万步";
    channelInfo2.url = "http://bbs.xiaomi.cn/step21/index.html";
    channelInfo2.imgUrl = "http://s1.bbs.xiaomi.cn/statics_app/images/hd/20160622/wb_share.jpg";
    channelInfo2.icon = "http://s1.bbs.xiaomi.cn/statics_app/images/hd/20160622/wx_icon";
    channelInfos.add(channelInfo1);

    ShareDataInfo.ChannelInfo channelInfo3 = new ShareDataInfo.ChannelInfo();
    channelInfo3.channel = "wb";
    channelInfo3.content =
        "【每天送一台手机，连送30天】#坚持21天#，挑战21万步！我刚刚在@小米社区 记录了最新运动数据，一次手腕上“说走就走”的活动！每天走两步就能中大奖噢，快来一起签到运动吧！";
    channelInfo3.title = "";
    channelInfo3.url = "http://bbs.xiaomi.cn/step21/index.html";
    channelInfo3.imgUrl = "http://s1.bbs.xiaomi.cn/statics_app/images/hd/20160622/wb_share.jpg";
    channelInfo3.icon = "http://s1.bbs.xiaomi.cn/statics_app/images/hd/20160622/wx_icon";
    channelInfos.add(channelInfo1);
    dataInfo.channelInfos = channelInfos;

    return gson.toJson(dataInfo);
  }

  @Override
  public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
    switch (v.getId()) {
      case R.id.iv_bg:
        menu.setHeaderTitle("添加");
        menu.add(0, Menu.FIRST, 0, "头像");
        menu.add(0, Menu.FIRST + 1, 0, "文字");
        break;
    }
  }

  @Override public boolean onContextItemSelected(MenuItem item) {

    switch (item.getItemId()) {
      case Menu.FIRST: // 添加用户头像
        addAvatar();
        break;
      case Menu.FIRST + 1: // 添加文字
        addText();
        break;
    }
    return true;
  }

  /**
   * 添加文字
   */
  final int textSize = 70;

  private void addText() {
    avatarIsEdit = false;
    textIsEdit = true;
    llX.setVisibility(View.VISIBLE);
    llY.setVisibility(View.VISIBLE);
    llEditText.setVisibility(View.VISIBLE);
    final ShareDataInfo.TextOption textOption = new ShareDataInfo.TextOption();
    textOption.textSize = textSize;
    textOption.textColor = Color.WHITE;
    textOption.content = "我是文字";
    textOption.centreX = bgBmp.getWidth() / 2;
    textOption.baselineY = 100;
    mTextOptions.add(textOption);
    drawWithJson(buildToJson());
  }

  /**
   * 添加头像
   */
  private void addAvatar() {
    mAvatarOption.width = 200;
    mAvatarOption.height = 200;
    mAvatarOption.pointX = 100;
    mAvatarOption.pointY = 100;
    mAvatarOption.url = avatarUrl;
    avatarIsEdit = true;
    textIsEdit = false;
    llX.setVisibility(View.VISIBLE);
    llY.setVisibility(View.VISIBLE);
    llEditText.setVisibility(View.GONE);
    drawWithJson(buildToJson());
  }
}

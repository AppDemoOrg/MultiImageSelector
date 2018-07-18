package com.abt.mis;

import android.app.Activity;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.widget.ImageView;

/**
 * @描述： @ImageActivity
 * @作者： @黄卫旗
 * @创建时间： @13/07/2018
 */
public class ImageActivity extends Activity {

    private ImageView mImageView;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image);
        mImageView = (ImageView) findViewById(R.id.image);

        //mImageView.setImageURI(Uri.parse("file:///android_assets/ic_launcher.png"));
        mImageView.setImageURI(Uri.parse("https://www.baidu.com/img/bd_logo1.png"));
    }
}

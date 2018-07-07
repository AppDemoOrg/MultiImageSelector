package com.abt.mis.widget;

import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.SurfaceTexture;
import android.media.ExifInterface;
import android.media.MediaPlayer;
import android.os.AsyncTask;
import android.os.Build;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Surface;
import android.view.SurfaceView;

import java.io.File;
import java.io.IOException;
import java.lang.ref.WeakReference;

/**
 * @描述： @MySurfaceView
 * @作者： @黄卫旗
 * @创建时间： @07/07/2018
 */
public class MySurfaceView extends SurfaceView {
    private static final String TAG = MySurfaceView.class.getSimpleName();
    private Surface mGLSurface;
    private MediaPlayer mMediaPlayer;
    private Bitmap mBitmap;
    private String mFilename;

    public MySurfaceView(Context context) {
        super(context);
    }

    public MySurfaceView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public MySurfaceView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public int open(String filename) {
        if (filename == null) {
            return 5;
        }
        mFilename = filename;

        File file = new File(filename);
        if (!file.exists()) {
            Log.e(TAG, "File not exists : " + filename);
            return 1;
        }

        if (filename.endsWith(".jpg") || filename.endsWith(".jpeg")) {
            if (mMediaPlayer != null) {
                mMediaPlayer.release();
                mMediaPlayer = null;
            }
            new LoadBitmapAsyncTask(this, false).execute();
        }
        return 0;
    }

    private static class LoadBitmapAsyncTask extends AsyncTask<Void, Integer, Bitmap> {
        String mFilename;
        Boolean mIsThumbnail;
        WeakReference<MySurfaceView> mSurfaceView;

        LoadBitmapAsyncTask(MySurfaceView view, boolean isThumbnail) {
            mSurfaceView = new WeakReference<>(view);
            mFilename = mSurfaceView.get().mFilename;
            mIsThumbnail = isThumbnail;
        }

        @Override
        protected Bitmap doInBackground(Void... v) {
            if (!mFilename.equals(mSurfaceView.get().mFilename)) {
                return null;
            }

            //先加载缩略图再加载原图
            if (mIsThumbnail) {
                try {
                    ExifInterface exifInterface = new ExifInterface(mFilename);
                    byte[] thumbnail = exifInterface.getThumbnail();
                    if (thumbnail == null) {
                        return null;
                    }
                    return BitmapFactory.decodeByteArray(thumbnail, 0, thumbnail.length);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            } else {
                BitmapFactory.Options options = new BitmapFactory.Options();
                options.inSampleSize = 2;
                return BitmapFactory.decodeFile(mFilename, options);
            }
            return null;
        }

        @Override
        protected void onPostExecute(Bitmap bitmap) {
            MySurfaceView mediaPlayerSurfaceView = mSurfaceView.get();
            if (mediaPlayerSurfaceView != null &&
                    mFilename.equals(mediaPlayerSurfaceView.mFilename)) {
                if (bitmap != null) {
                    mediaPlayerSurfaceView.mBitmap = bitmap;
                    mediaPlayerSurfaceView.drawBitmap();
                }
                if (mIsThumbnail) {
                    new LoadBitmapAsyncTask(mSurfaceView.get(), false).execute();
                }
            }
        }
    }

    @TargetApi(Build.VERSION_CODES.M)
    private void drawBitmap() {
        if (mGLSurface != null) {
            mGLSurface.release();
            SurfaceTexture[] mSurfaceTexture = new SurfaceTexture[2];
            mGLSurface = new Surface(mSurfaceTexture[0]);
        }

        if (mGLSurface == null) {
            SurfaceTexture[] mSurfaceTexture = new SurfaceTexture[2];
            mGLSurface = new Surface(mSurfaceTexture[0]);
        }

        if (mGLSurface != null && mBitmap != null) {
            //mPiPano.mSurfaceTexture[0].setDefaultBufferSize(mBitmap.getWidth(), mBitmap.getHeight());
            Canvas canvas = mGLSurface.lockHardwareCanvas();
            canvas.drawBitmap(mBitmap, 0, 0, null);
            mGLSurface.unlockCanvasAndPost(canvas);
        }
    }

}

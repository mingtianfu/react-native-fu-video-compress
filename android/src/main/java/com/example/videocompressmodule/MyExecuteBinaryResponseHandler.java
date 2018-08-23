package com.example.videocompressmodule;

import android.app.ProgressDialog;
import android.util.Log;

import com.facebook.react.bridge.Callback;
import com.github.hiteshsondhi88.libffmpeg.FFmpegExecuteResponseHandler;

import java.io.File;

/**
 * Created by Administrator on 2018/3/27/027.
 */

public class MyExecuteBinaryResponseHandler implements FFmpegExecuteResponseHandler {
    private final String TAG = getClass().getSimpleName();
    private Callback callback;
    private ProgressDialog progressDialog;
    private String inPath;
    private String outPath;

    public MyExecuteBinaryResponseHandler(String inPath, String outPath, ProgressDialog progressDialog, Callback callback){
        this.callback = callback;
        this.progressDialog = progressDialog;
        this.inPath = inPath;
        this.outPath = outPath;
    }

    @Override
    public void onStart() {
        progressDialog.setMessage("视频压缩中...");
        progressDialog.show();
        Log.d(TAG, "onStart" );
    }

    @Override
    public void onProgress(String message) {
        Log.d(TAG, "视频压缩中: "+ message );
    }

    @Override
    public void onFailure(String message) {
        Log.d(TAG, "onFailure: "+ message );
        callback.invoke(false, outPath, 0);
    }

    @Override
    public void onSuccess(String message) {
        Log.d(TAG, "onSuccess: "+ message );
    }


    @Override
    public void onFinish() {
        progressDialog.dismiss();
        double size = getFileSize(outPath);
        callback.invoke(true, outPath, size);
    }

    private double getFileSize(String path) {
        File f = new File(path);
        if (!f.exists()) {
            return 0;
        } else {
            long size = f.length();
            return (size / 1024f) / 1024f;
        }
    }
}

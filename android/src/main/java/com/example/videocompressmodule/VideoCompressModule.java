package com.example.videocompressmodule;

import android.Manifest;
import android.app.ProgressDialog;
import android.util.Log;
import android.widget.Toast;
import android.content.Context;

import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContextBaseJavaModule;
import com.facebook.react.bridge.ReactMethod;
import com.facebook.react.bridge.Callback;

import com.example.videocompressmodule.permission.PermissionsActivity;
import com.example.videocompressmodule.permission.PermissionsChecker;

import com.github.hiteshsondhi88.libffmpeg.FFmpeg;
import com.github.hiteshsondhi88.libffmpeg.LoadBinaryResponseHandler;
import com.github.hiteshsondhi88.libffmpeg.exceptions.FFmpegCommandAlreadyRunningException;
import com.github.hiteshsondhi88.libffmpeg.exceptions.FFmpegNotSupportedException;

import java.lang.String;


/**
 * Created by Administrator on 2018/3/22/022.
 */

public class VideoCompressModule extends ReactContextBaseJavaModule {
    private final String TAG = getClass().getSimpleName();
    private ProgressDialog progressDialog;
    //相机权限,录制音频权限,读写sd卡的权限,都为必须,缺一不可
    private static final String[] PERMISSIONS = new String[]{
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.READ_EXTERNAL_STORAGE};
    private static final int REQUEST_CODE_FOR_PERMISSIONS = 0;//
    //    private String currentInputVideoPath = "/mnt/sdcard/DCIM/VID_20180118_170943.mp4";
//    private String currentInputVideoPath = "/mnt/sdcard/DCIM/VID_20180118_163319.mp4";
//    private String currentOutputVideoPath = "/mnt/sdcard/videokit/163319.mp4";
//    //-vf scale=960:540 scale=960:-1
//    String cmd = "-y -i " + currentInputVideoPath + " -strict -2 -vcodec libx264 -preset ultrafast " +
//            "-crf 24 -acodec aac -ar 44100 -ac 2 -b:a 96k -vf scale=960:-1 -aspect 16:9 " + currentOutputVideoPath;
////    String cmd = "-y -i " + currentInputVideoPath + " -strict -2 -vcodec libx264 -preset ultrafast " +
////            "-crf 24 -acodec aac -ar 44100 -ac 2 -b:a 96k -s 640x480 -aspect 16:9 " + currentOutputVideoPath;
    private ReactApplicationContext _context = null;
    public VideoCompressModule(ReactApplicationContext reactContext) {
        super(reactContext);
        _context = reactContext;

        FFmpeg ffmpeg = FFmpeg.getInstance(_context);
        try {
            ffmpeg.loadBinary(new LoadBinaryResponseHandler() {

                @Override
                public void onStart() {
                }

                @Override
                public void onFailure() {
                }

                @Override
                public void onSuccess() {
                }

                @Override
                public void onFinish() {
                }
            });
        } catch (FFmpegNotSupportedException e) {
            // Handle if FFmpeg is not supported by device
        }


    }

    // 这里返回一个模块名称字符串，便于我们在 js中调用
    @Override
    public String getName() {
        return "VideoCompressModule";
    }
    //  创建一个方法
    @ReactMethod
    public void compress(String inPath, String outPath, String cmd, final Callback callback) {
        PermissionsChecker mChecker = new PermissionsChecker(_context);
        if (mChecker.lacksPermissions(PERMISSIONS)) {
            PermissionsActivity.startActivityForResult(_context.getCurrentActivity(), REQUEST_CODE_FOR_PERMISSIONS, PERMISSIONS);
        }

        progressDialog = new ProgressDialog(getReactApplicationContext().getCurrentActivity());
        progressDialog.setTitle(null);
        progressDialog.setCancelable(true);// 设置是否可以通过点击Back键取消
        progressDialog.setCanceledOnTouchOutside(false);// 设置在点击Dialog外是否取消Dialog进度条
        // 监听cancel事件
//        progressDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
//            @Override
//            public void onCancel(DialogInterface dialog) {
//                new AlertDialog.Builder(getReactApplicationContext().getCurrentActivity())
//                    .setTitle("提示")
//                    .setMessage("视频压缩中，是否停止压缩")
//                    .setNegativeButton("取消", new DialogInterface.OnClickListener() {
//                        @Override
//                        public void onClick(DialogInterface dialog, int which) {
//                            progressDialog.setMessage("视频压缩中...");
//                            progressDialog.show();
//                        }
//                    })
//                    .setPositiveButton("确定", new DialogInterface.OnClickListener() {
//                        @Override
//                        public void onClick(DialogInterface dialog, int which) {
//                            progressDialog.dismiss();
//                            killFFmpegProcesses();
//                        }
//                    })
//                    .create().show();
//            }
//        });
        String[] command = cmd.split(" ");
        if (command.length != 0) {
            runFFmpeg(inPath, outPath, command, callback);
        } else {
            Toast.makeText(_context, "压缩失败", Toast.LENGTH_LONG).show();
        }
//        runFFmpeg(cmd.toString().split(" "), callback);
    }

    private void runFFmpeg(String inPath, String outPath, String [] commands, Callback callback) {
        FFmpeg ffmpeg = FFmpeg.getInstance(_context);
        Callback _callback = callback;
        try {
            // to execute "ffmpeg -version" command you just need to pass "-version"
            ffmpeg.execute(commands, new MyExecuteBinaryResponseHandler(inPath, outPath, progressDialog, _callback));
        } catch (FFmpegCommandAlreadyRunningException e) {
            // Handle if FFmpeg is already running
        }
    }

    //取消压缩 isFFmpegCommandRunning killRunningProcesses
    private void killFFmpegProcesses(){
        FFmpeg ffmpeg = FFmpeg.getInstance(_context);
        boolean isFFmpegCommandRunning = ffmpeg.isFFmpegCommandRunning();
        Log.d(TAG, "isFFmpegCommandRunning(): "+ isFFmpegCommandRunning );
        if(isFFmpegCommandRunning){
            ffmpeg.killRunningProcesses();
        }
    }

}

# react-native-fu-video-compress
安装方法，
````
yarn add react-native-fu-video-compress
````
使用方法示例，真机测试没问题，模拟器上压缩视频会报错，java不是很懂，没有去认真排查问题，有发现是什么问题欢迎联系
```
import videoCompress from "react-native-fu-video-compress";


const InputVideoPath = "/storage/emulated/0/DCIM/Camera/1.mp3";
const OutputVideoPath = "/storage/emulated/0/DCIM/Camera/temp_1.mp3";
const cmd = "-y -i " + InputVideoPath + " -s 960x540 -strict -2 -vcodec libx264 -preset faster " +
			"-crf 24 -acodec aac -ar 44100 -ac 2 -b:a 96k -vf transpose=1 -aspect 16:9 " + OutputVideoPath;
videoCompress.compress(InputVideoPath, OutputVideoPath, cmd, (status, outPath, size)=>{
//status, outPath, size
 if(status && size <= 2){
  const uri = 'file://'+outPath;
 }else if(status && size > 2){
  Alert.alert('压缩后大于2M，请重新选择视频');
 }else{
  Alert.alert('压缩失败');
 }
});
```

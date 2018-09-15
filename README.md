# SuSoundSelector
## 1.图片（声音自行想象）
![图片](https://github.com/1249848166/SuSoundSelector/blob/master/SVID_20180909_132229_1.gif)
## 2.特点
支持存储卡声音扫描选择，支持网络地址，存储卡，项目空间三种路径音频，支持多音频同时播放，支持进度拖动，支持各种回调，完全可以封装成一个音乐播放器。
## 3.使用
添加依赖，不解释
```java
allprojects {
		repositories {
			...
			maven { url 'https://jitpack.io' }
		}
	}
```
```java
dependencies {
	        implementation 'com.github.1249848166:SuSoundSelector:1.2'
	}
```
然后在项目中
```java
//声明播放器组建
MyMediaPlayer player1;
MyMediaPlayer player2;
MyMediaPlayer player3;

//初始化
player1 = SuSoundManager.getInstance().getMediaPlayer(MainActivity.this, SuSoundManager.PARAM_NETWORK,
                "http://bmob-cdn-21427.b0.upaiyun.com/2018/09/07/51f701f3402aae8e807634b54421923a.wav",this);//网络音频，比如网站上的音乐
player2 = SuSoundManager.getInstance().getMediaPlayer(MainActivity.this, SuSoundManager.PARAM_LOCAL,
        R.raw.sound1 + "",this);//项目路径音频，通常是默认声音，比如按键点击声音
player3 = SuSoundManager.getInstance().getMediaPlayer(MainActivity.this, SuSoundManager.PARAM_FILE,
        Environment.getExternalStorageDirectory() + "/aaa.wav",this);//手机存储卡音频
        
//播放或暂停在某一个进度上（一般选择当前播放进度）
SuSoundManager.getInstance().playOrPauseNetworkSoundAtProgress(player1, (int) player1.getCurrentPosition());//播放或暂停网络音频
SuSoundManager.getInstance().playOrPauseLocalSoundAtProgress(player2, (int) player2.getCurrentPosition());//播放或暂停项目音频
SuSoundManager.getInstance().playOrPauseFileSoundAtProgress(player3, (int) player3.getCurrentPosition());//播放或暂停内存卡音频

//扫描内存中所有音频文件
SuSoundLoader.getInstance().loadFileSounds(this,new LoadSoundCallback() {
    @Override
    public void onLoad(List<SoundData> datas) {//在回调中取出所有音频路径（可以调用上面播放内存卡音频方法播放）
        for (SoundData soundData : datas) {
            System.out.println("标题：" + soundData.getTitle() + "作家：" + soundData.getArtist() +
                    "时长：" + soundData.getDuration() + "路径：" + soundData.getPath());
        }
    }
});

//SoundListener的所有回调方法
@Override
public void onPlay(MyMediaPlayer player) {
    System.out.println("开始播放");
}
@Override
public void onPlaying(MyMediaPlayer player, long progress) {
    System.out.println("正在播放");//只有这个回调的实现是处于主线程之中，内部用handler的延迟发送message方式进行轮询回调，可以直接在这里更新UI，比如进度条的进度。其余回调处于线程池调度中，直接更新UI会闪退或报错，所以需要打通线程通道（用handler或者ui的post方式）
}
@Override
public void onPause(MyMediaPlayer player) {
    System.out.println("暂停播放");
}
@Override
public void onComplete(MyMediaPlayer player) {
    System.out.println("结束播放");
}
@Override
public void onError(MyMediaPlayer player, String msg) {
    System.out.println("播放错误：" + msg);
}
@Override
public void onClear(List<String> paths) {
    for(String p:paths){
        System.out.println("删除了:"+p);//删除后会把内部的map取出对应项，并暂停该音频的播放
    }
}
@Override
public void onChangeState(MyMediaPlayer player, boolean isOn) {
    //这个是额外预留给音乐播放器的回调，isOn判断音乐是否正在播放，可以改变播放按钮图标
}

//或者在想要跳转到音频选择界面的地方调用
public static final int REQUEST=1;
startActivityForResult(new Intent(this, SelectSoundPanelActivity.class),REQUEST);
//然后在activity的返回中取出选择的音频
@Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==REQUEST&&resultCode==RESULT_OK){
            System.out.println("==========================================================");
            List<String> paths=data.getStringArrayListExtra("paths");
            for(String path:paths){
                System.out.println(path);
            }
        }
    }
```
结合进度条进行播放的完整案例请查看app目录下的完整案例实现

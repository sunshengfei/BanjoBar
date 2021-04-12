### BanjoBar

[![](https://jitpack.io/v/sunshengfei/BanjoBar.svg)](https://jitpack.io/#sunshengfei/BanjoBar) [![](https://img.shields.io/github/license/sunshengfei/BanjoBar)](https://github.com/sunshengfei/BanjoBar/blob/main/LICENSE)

> 主要用于展示帧序列（视频帧或音频序列）区位标记，也可用于视频裁剪的控件

### 效果展示

![](./dist/banjobar.gif "")

### 1 快速引入
```
implementation 'com.github.sunshengfei:BanjoBar:1.0.0'
```

### 2 使用方法

布局中使用
```xml
<io.fuwafuwa.banjo.BanjoTimeBar
android:id="@+id/rangeBar"
android:layout_width="match_parent"
android:layout_height="wrap_content" />
```

代码中，注意默认开启了ffmpeg（ExtractHandler.FFMPEG），因此需要引入ffmpeg相关依赖，见后续说明
```
rangeBar.attachVideoSource(
            dataSource,//视频url
            outputDir,//缩略图输出文件夹
            spanMills, //关键帧间隔毫秒
            frameSize //缩略图大小
        )
```

### 3 更新配置

可通过`rangeBar.updateConfig(config)`更新默认配置，部分也可以在xml中配置，config为`ThumbDefaultGlobalSettings.Config`，全局配置如下：
```
        /**
         *  使用震动
         */
        public boolean userReactVibrate = true;
        /**
         * 选中区域边框
         */
        public boolean isShowProgressBorder = false;

        /**
         * 当内容未超出屏幕时，是否可以继续显示滚动模式
         */
        public boolean isScrollableWhenLess = true;

        /**
         * 是否支持时间轴滚动
         */
        public boolean isTimeLineMode = true;

        /**
         * 是否显示中线指示控件
         */
        public boolean isShowIndicator = true;

        /**
         * 中线指示控件颜色
         */
        @ColorInt
        public int indicatorColor = 0xFFFFFFFF;

        /**
         * range区域mask color
         */
        @ColorInt
        public int progressBarColor = Color.parseColor("#50FF4400");

        /**
         * 选择高亮区域颜色
         */
        @ColorInt
        public int progressBarSelectedColor = Color.parseColor("#99FF4400");

        /**
         * 是否允许滑块交叠
         * @waitforimplements 暂未实现
         */
        public boolean isThumbCanCross = false;

        /**
         * 中线超出长度
         */
        public int indicatorClipPaddingSize = 20;

        public LayoutMode layoutMode = LayoutMode.OVERLAY;

        /**
         * 缩略图尺寸
         */
        public Size thumbNailSize = new Size(96, Frame.dp2px(40));

        /**
         * 左右handler宽度
         */
        public float handlerWidth = Frame.dp2px(18f);

        public ExtractHandler preferThumbExtractor = ExtractHandler.FFMPEG;

        public int expandModeSpacing = Frame.dp2px(2f);
```

### 4 可选依赖说明
> 通过设置默认配置中的`preferThumbExtractor`来设定使用何种手段解帧，共以下四种：
```
    MMR,
    FFMPEG_MMR,
    FFMPEG,
    OPENGL
```

#### 4.1 如果使用android自身的MediaMetadataRetriever（格式有要求），不需要引入可选依赖
> ExtractHandler.MMR

#### 4.2 使用RxFFmpeg（默认，兼容性好）
> ExtractHandler.FFMPEG

```
implementation 'com.github.sunshengfei:rxffmpeg:v1.0.0'
implementation 'io.reactivex.rxjava3:rxandroid:3.0.0'
implementation 'io.reactivex.rxjava3:rxjava:3.0.7'
# 以下ffmpeg命令库二选一
implementation 'com.arthenica:mobile-ffmpeg-full:4.4.LTS'
implementation 'com.yyl.ffmpeg:ffmpeg_cmd:4.1.3'
```
#### 4.3 使用 FFmpegMediaMetadataRetriever
> ExtractHandler.FFMPEG_MMR

```
implementation 'com.github.wseemann:FFmpegMediaMetadataRetriever-core:1.0.15'
implementation 'com.github.wseemann:FFmpegMediaMetadataRetriever-native:1.0.15'
```

### 5 添加轨道
> 默认包含一个空轨

```
rangeBar.addThumbGroup(trackConfig)
```

### 6 添加、删除选区

```
#添加
rangeBar.addThumb(segment, index, config)
#删除
rangeBar.trashThumb(position, index)
#或通过轨道segmentProvider pushSegment或pullSegment操作
```
Segment: 区域model,包含id,from,to,label,是否被选中等,id=0则会自动生成
index: 轨道索引
position: 区域索引
config: 选区特有的配置，诸如背景，选中背景，是否可拖拽等


PS:`BanjoTimeBar`扩展自`SelectionBar`,如果不适用，可以自行继承`SelectionBar`进行扩展
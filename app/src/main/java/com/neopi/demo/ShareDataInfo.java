package com.neopi.demo;

import java.util.List;

/**
 * Created by Neo on 2016/7/1.
 */

public class ShareDataInfo {

    public String hd_mode;
    public String callback ;
    public String bgUrl ;
    public AvatarOption avatarOption;
    public List<TextOption> textOptions;
    public List<ChannelInfo> channelInfos;

    @Override
    public String toString() {
        return "ShareDataInfo{" +
                "hd_mode='" + hd_mode + '\'' +
                ", callback='" + callback + '\'' +
                ", avatarOption=" + avatarOption +
                ", textOptions=" + textOptions +
                ", channelInfos=" + channelInfos +
                ", bgUrl=" + bgUrl +
                '}';
    }

    public static class AvatarOption{
        public int width ; // 头像的宽
        public int height ; // 头像的高
        public int pointX; // 标记头像中心点X的坐标 （想对于在背景图中的坐标，下同）
        public int pointY ; // 标记头像中心点Y的坐标
        public String url ; // 头像url


        @Override
        public String toString() {
            return "AvatarOption{" +
                    "width=" + width +
                    ", height=" + height +
                    ", pointX=" + pointX +
                    ", pointY=" + pointY +
                    ", url=" + url +
                    '}';
        }
    }

    public static class TextOption{
        public int textSize ;
        public int textColor ;
        public int centreX ;
        public int baselineY ;
        public boolean bold;
        public String content ;
        public String typeFace ; //　此处表示放在assets下的文字样式，可为null

        @Override
        public String toString() {
            return "TextOption{" +
                    "textSize=" + textSize +
                    ", textColor=" + textColor +
                    ", centreX=" + centreX +
                    ", baselineY=" + baselineY +
                    ", bold=" + bold +
                    ", content='" + content + '\'' +
                    ", typeFace='" + typeFace + '\'' +
                    '}';
        }
    }

    public static class ChannelInfo{
        public String channel ; // 分享渠道，如 "data"(默认分享), "wx"(微信), "wb" 微博, "fc", "qq" QQ, "qz" QQ空间，
        public String content ;
        public String title ;
        public String url ; // 活动的url
        public String imgUrl ;// 活动
        public String icon ;

        @Override
        public String toString() {
            return "ChannelInfo{" +
                    "channel='" + channel + '\'' +
                    ", content='" + content + '\'' +
                    ", title='" + title + '\'' +
                    ", url='" + url + '\'' +
                    ", imgUrl='" + imgUrl + '\'' +
                    ", icon='" + icon + '\'' +
                    '}';
        }
    }

}

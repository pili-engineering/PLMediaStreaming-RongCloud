package com.qiniu.droid.niuliving.im.model;

import android.net.Uri;
import android.os.Parcel;
import android.os.Parcelable;

import java.math.BigInteger;
import java.security.MessageDigest;

/**
 * Created by duanliuyi on 2018/5/9.
 */

public class ChatroomInfo implements Parcelable {

    private String roomId;
    private String roomName;
    private String mcuUrl;
    private String pubUserId;//创建者ID
    private int coverIndex; //封面图片索引

    public ChatroomInfo(String roomId, String roomName, String mcuUrl, String pubUserId, int cover) {
        this.roomId = roomId;
        this.roomName = roomName;
        this.mcuUrl = mcuUrl;
        this.pubUserId = pubUserId;
        this.coverIndex = cover;
    }

    public ChatroomInfo(String chatroomId, String chatroomName, String liveStatus, int onlineNum, Uri chatUri) {
        this.roomId = chatroomId;
        this.roomName = chatroomName;
    }

    public String getLiveId() {
        return roomId;
    }

    public void setLiveId(String chatroomId) {
        this.roomId = chatroomId;
    }

    public String getLiveName() {
        return roomName;
    }

    public void setLiveName(String chatroomName) {
        this.roomName = chatroomName;
    }

    private String getMD5String(String str) {
        try {
            // 生成一个MD5加密计算摘要
            MessageDigest md = MessageDigest.getInstance("MD5");
            // 计算md5函数
            md.update(str.getBytes());
            // digest()最后确定返回md5 hash值，返回值为8位字符串。因为md5 hash值是16位的hex值，实际上就是8位的字符
            // BigInteger函数则将8位的字符串转换成16位hex值，用字符串来表示；得到字符串形式的hash值
            //一个byte是八位二进制，也就是2位十六进制字符（2的8次方等于16的2次方）
            return new BigInteger(1, md.digest()).toString(16);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public String getPubUserId() {
        return pubUserId;
    }

    public void setPubUserId(String pubUserId) {
        this.pubUserId = pubUserId;
    }

    public String getRoomId() {
        return roomId;
    }

    public String getRoomName() {
        return roomName;
    }

    public String getMcuUrl() {
        return mcuUrl;
    }

    public void setMcuUrl(String mcuUrl) {
        this.mcuUrl = mcuUrl;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.roomId);
        dest.writeString(this.roomName);
        dest.writeString(this.mcuUrl);
        dest.writeString(this.pubUserId);
        dest.writeInt(this.coverIndex);
    }

    protected ChatroomInfo(Parcel in) {
        this.roomId = in.readString();
        this.roomName = in.readString();
        this.mcuUrl = in.readString();
        this.pubUserId = in.readString();
        this.coverIndex = in.readInt();
    }

    public static final Creator<ChatroomInfo> CREATOR = new Creator<ChatroomInfo>() {
        @Override
        public ChatroomInfo createFromParcel(Parcel source) {
            return new ChatroomInfo(source);
        }

        @Override
        public ChatroomInfo[] newArray(int size) {
            return new ChatroomInfo[size];
        }
    };
}

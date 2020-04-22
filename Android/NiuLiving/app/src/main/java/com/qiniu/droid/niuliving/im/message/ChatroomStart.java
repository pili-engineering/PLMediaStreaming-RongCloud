
package com.qiniu.droid.niuliving.im.message;

import android.os.Parcel;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;

import io.rong.common.ParcelUtils;
import io.rong.imlib.MessageTag;
import io.rong.imlib.model.MessageContent;
import io.rong.imlib.model.UserInfo;

@MessageTag(value = "RC:Chatroom:Start", flag = 3)
public class ChatroomStart extends MessageContent {
  public ChatroomStart() {
  }
  public ChatroomStart(byte[] data) {
    String jsonStr = null;
    try {
        jsonStr = new String(data, "UTF-8");
    } catch (UnsupportedEncodingException e) {
        e.printStackTrace();
    }
    try {
        JSONObject jsonObj = new JSONObject(jsonStr);
        
          if (jsonObj.has("time")){
            time = jsonObj.optString("time");
          }
        
          if (jsonObj.has("extra")){
            extra = jsonObj.optString("extra");
          }
        if (jsonObj.has("user")){
            setUserInfo(parseJsonToUserInfo(jsonObj.optJSONObject("user")));
        }
    } catch (JSONException e) {
        e.printStackTrace();
    }
  }
  @Override
  public byte[] encode() {
    JSONObject jsonObj = new JSONObject();
    try {
        
            jsonObj.put("time", time);
        
            jsonObj.put("extra", extra);
        jsonObj.putOpt("user",getJSONUserInfo());
    } catch (JSONException e) {
        e.printStackTrace();
    }
    try {
        return jsonObj.toString().getBytes("UTF-8");
    } catch (UnsupportedEncodingException e) {
        e.printStackTrace();
    }
    return null;
  }
  @Override
  public int describeContents() {
    return 0;
  }
  @Override
  public void writeToParcel(Parcel dest, int flags) {
    
      
         ParcelUtils.writeToParcel(dest, time);
      
    
      
         ParcelUtils.writeToParcel(dest, extra);
      dest.writeParcelable(getUserInfo(),0);
    
  }
  protected ChatroomStart(Parcel in) {
    
      
        time = ParcelUtils.readFromParcel(in);
      
    
      
        extra = ParcelUtils.readFromParcel(in);
      setUserInfo((UserInfo) in.readParcelable(UserInfo.class.getClassLoader()));
    
  }
  public static final Creator<ChatroomStart> CREATOR = new Creator<ChatroomStart>() {
    @Override
    public ChatroomStart createFromParcel(Parcel source) {
        return new ChatroomStart(source);
    }
    @Override
    public ChatroomStart[] newArray(int size) {
        return new ChatroomStart[size];
    }
  };
  
    private String time;
    public void setTime(   String time) {
        this.time = time;
    }
    public String getTime() {
      return time;
    }
  
    private String extra;
    public void setExtra(   String extra) {
        this.extra = extra;
    }
    public String getExtra() {
      return extra;
    }
  
}

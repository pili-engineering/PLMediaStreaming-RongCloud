
package com.qiniu.droid.niuliving.im.message;

import android.os.Parcel;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;

import io.rong.common.ParcelUtils;
import io.rong.imlib.MessageTag;
import io.rong.imlib.model.MessageContent;
import io.rong.imlib.model.UserInfo;

@MessageTag(value = "RC:Chatroom:Like", flag = 3)
public class ChatroomLike extends MessageContent {
  public ChatroomLike() {
  }
  public ChatroomLike(byte[] data) {
    String jsonStr = null;
    try {
        jsonStr = new String(data, "UTF-8");
    } catch (UnsupportedEncodingException e) {
        e.printStackTrace();
    }
    try {
        JSONObject jsonObj = new JSONObject(jsonStr);
        
          if (jsonObj.has("counts")){
            counts = jsonObj.optInt("counts");
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
        
            jsonObj.put("counts", counts);
        
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
    
      
         ParcelUtils.writeToParcel(dest, counts);
      
    
      
         ParcelUtils.writeToParcel(dest, extra);

      dest.writeParcelable(getUserInfo(),0);
  }
  protected ChatroomLike(Parcel in) {
    
      
        
          counts = ParcelUtils.readIntFromParcel(in);
        
      
    
      
        extra = ParcelUtils.readFromParcel(in);
      setUserInfo((UserInfo) in.readParcelable(UserInfo.class.getClassLoader()));
    
  }
  public static final Creator<ChatroomLike> CREATOR = new Creator<ChatroomLike>() {
    @Override
    public ChatroomLike createFromParcel(Parcel source) {
        return new ChatroomLike(source);
    }
    @Override
    public ChatroomLike[] newArray(int size) {
        return new ChatroomLike[size];
    }
  };
  
    private int counts;
    public void setCounts( int    counts) {
        this.counts = counts;
    }
    public  int getCounts() {
      return counts;
    }
  
    private String extra;
    public void setExtra(   String extra) {
        this.extra = extra;
    }
    public String getExtra() {
      return extra;
    }
  
}

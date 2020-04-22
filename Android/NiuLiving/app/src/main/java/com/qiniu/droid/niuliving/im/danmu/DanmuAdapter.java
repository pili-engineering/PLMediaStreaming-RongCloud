package com.qiniu.droid.niuliving.im.danmu;


import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.orzangleli.xdanmuku.XAdapter;
import com.qiniu.droid.niuliving.R;

import java.util.Random;

public class DanmuAdapter extends XAdapter<DanmuEntity> {

    Random random;


    private Context context;

    public DanmuAdapter(Context c) {
        super();
        context = c;
        random = new Random();
    }

    @Override
    public View getView(DanmuEntity danmuEntity, View convertView) {

        ViewHolder holder = null;

        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.item_danmu, null);
            holder = new ViewHolder();
            holder.content = (TextView) convertView.findViewById(R.id.content);
            holder.image = (ImageView) convertView.findViewById(R.id.image);
            holder.tvName = (TextView) convertView.findViewById(R.id.tv_usernickname);
            convertView.setTag(holder);

        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        holder.image.setImageURI(danmuEntity.getPortrait());
        holder.content.setText(danmuEntity.getContent());
        // holder.content.setTextColor(Color.rgb(random.nextInt(256), random.nextInt(256), random.nextInt(256)));
        holder.tvName.setText(danmuEntity.getName());

        return convertView;
    }

    @Override
    public int[] getViewTypeArray() {
        int type[] = {0, 1};
        return type;
    }

    @Override
    public int getSingleLineHeight() {
        //将所有类型弹幕的布局拿出来，找到高度最大值，作为弹道高度
        View view = LayoutInflater.from(context).inflate(R.layout.item_danmu, null);
        //指定行高
        view.measure(0, 0);
        return view.getMeasuredHeight();

    }


    class ViewHolder {
        public TextView content;
        public TextView tvName;
        public ImageView image;
    }


}

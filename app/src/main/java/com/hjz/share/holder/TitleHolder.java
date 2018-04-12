package com.hjz.share.holder;

import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.hjz.share.R;
import com.hjz.share.command.Receiver;

import java.util.List;

/**
 * Created by hjz on 17-12-8.
 * for:
 */

public class TitleHolder extends BaseHolder<Title> {

    private ImageView icon;
    private TextView appName;
    private TextView date;

    public TitleHolder(Receiver receiver, int viewId, ViewGroup parent) {
        super(receiver, viewId, parent);
        icon = itemView.findViewById(R.id.icon);
        appName = itemView.findViewById(R.id.app_name);
        date = itemView.findViewById(R.id.date);
    }

    @Override
    public void refreshData(List<Title> titles, int position) {
        //只能操作position位置的元素，否则可能发生类型不匹配
        //其他位置可能是其他类型的Holder
        Title title = titles.get(position);

        icon.setImageDrawable(title.getAppIcon());
        appName.setText(title.getAppName());
        date.setText(title.getDate());
    }
}

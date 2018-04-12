package com.hjz.share.holder;

import android.view.ViewGroup;

import com.hjz.share.R;
import com.hjz.share.command.Receiver;

import java.util.List;

/**
 * Created by hjz on 18-2-1.
 * for:
 */

public class DividerHolder extends BaseHolder<Divider> {

    private ViewGroup dividerView;

    public DividerHolder(Receiver receiver, int viewId, ViewGroup parent) {
        super(receiver, viewId, parent);
        dividerView = itemView.findViewById(R.id.divider);
    }

    @Override
    public void refreshData(List<Divider> dividers, int position) {
        //只能操作dividers.get(position)位置的元素，否则可能发生类型不匹配
        //dividers里面其他位置可能是其他类型的Holder
        Divider divider = dividers.get(position);

        if (position + 1 == dividers.size()) {
            dividerView.setBackgroundResource(R.color.white);
            dividerView.setMinimumHeight(12);
        } else {
            dividerView.setMinimumHeight(divider.getHeight());
            dividerView.setBackgroundResource(divider.getColor());
        }

    }
}

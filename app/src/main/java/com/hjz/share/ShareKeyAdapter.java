package com.hjz.share;

import android.support.v7.widget.RecyclerView;
import android.view.ViewGroup;

import com.hjz.share.command.Receiver;
import com.hjz.share.holder.BaseHolder;
import com.hjz.share.holder.DividerHolder;
import com.hjz.share.holder.GridHolder;
import com.hjz.share.holder.IViewType;
import com.hjz.share.holder.ListItemHolder;
import com.hjz.share.holder.TitleHolder;

import java.util.List;

import static com.hjz.share.holder.BaseHolder.VIEW_TYPE_DIVIDER;
import static com.hjz.share.holder.BaseHolder.VIEW_TYPE_GRID;
import static com.hjz.share.holder.BaseHolder.VIEW_TYPE_LIST;
import static com.hjz.share.holder.BaseHolder.VIEW_TYPE_TITLE;

/**
 * Created by hjz on 17-11-29.
 * for:
 */

public class ShareKeyAdapter extends RecyclerView.Adapter<BaseHolder> {

    public static final String TAG = "ShareKeyAdapter";

    private List<IViewType> dataModels;
    private Receiver receiver;

    public ShareKeyAdapter(Receiver receiver, List<IViewType> dataModels) {
        this.receiver = receiver;
        this.dataModels = dataModels;
    }

    @Override
    public int getItemCount() {
        return dataModels.size();
    }

    @Override
    public int getItemViewType(int position) {
        return dataModels.get(position).getViewType();
    }


    @Override
    public BaseHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        switch (viewType) {
            case VIEW_TYPE_TITLE:
                return new TitleHolder(receiver, R.layout.title, parent);
            case VIEW_TYPE_LIST:
                return new ListItemHolder(receiver,R.layout.list, parent);
            case VIEW_TYPE_GRID:
                return new GridHolder(receiver,R.layout.grid, parent);
            case VIEW_TYPE_DIVIDER:
                return new DividerHolder(receiver,R.layout.divider, parent);
        }
        return null;
    }

    @Override
    public void onBindViewHolder(BaseHolder holder, int position) {
        holder.refreshData(dataModels, position);
    }
}

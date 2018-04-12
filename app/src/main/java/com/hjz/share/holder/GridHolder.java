package com.hjz.share.holder;

import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;

import com.hjz.share.R;
import com.hjz.share.command.Receiver;

import java.util.List;

/**
 * Created by hjz on 17-12-8.
 * for:
 */

public class GridHolder extends BaseHolder<Grid> {

    private ImageView thumbnail;
    private ImageView mask_flag;
    private FrameLayout mask;
    private ImageView video;

    public GridHolder(Receiver receiver, int viewId, ViewGroup parent) {
        super(receiver, viewId, parent);
        thumbnail = itemView.findViewById(R.id.thumbnail);
        mask = itemView.findViewById(R.id.mask);
        mask_flag = itemView.findViewById(R.id.mask_flag);
        video = itemView.findViewById(R.id.video);
    }

    @Override
    public void refreshData(List<Grid> grids, int position) {

        Grid grid = grids.get(position);

        if (grid.isVideo()) video.setVisibility(View.VISIBLE);
        else video.setVisibility(View.INVISIBLE);

        thumbnail.setImageDrawable(null);
        if (grid.getThumbnail() == null) loadImage(grid, position, 100);
        else thumbnail.setImageDrawable(grid.getThumbnail());

        //recovery the select state
        if (grid.isChecked()) {
            //mask.setVisibility(View.VISIBLE);
            mask_flag.setVisibility(View.VISIBLE);
        } else{
            //mask.setVisibility(View.INVISIBLE);
            mask_flag.setVisibility(View.INVISIBLE);
        }

        //register click event
        itemView.setOnClickListener(clickListener);

        //register long click event
        itemView.setOnLongClickListener(longClickListener);

        itemView.setTag(grid);
    }
}

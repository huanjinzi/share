package com.hjz.share.holder;

import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.hjz.share.R;
import com.hjz.share.StateManager;
import com.hjz.share.command.Receiver;

import java.util.List;

/**
 * Created by hjz on 17-12-8.
 * for:
 */

public class ListItemHolder extends BaseHolder<ListItem> {

    private ImageView fileIcon;
    private ImageView mask_flag;

    private TextView fileName;
    private TextView fileSize;

    private FrameLayout topDivider;
    private FrameLayout bottomDivider;



    public ListItemHolder(Receiver receiver, int viewId, ViewGroup parent) {
        super(receiver, viewId, parent);
        fileIcon = itemView.findViewById(R.id.list_file_type_icon);
        mask_flag = itemView.findViewById(R.id.mask_flag);
        fileName = itemView.findViewById(R.id.list_file_name);
        fileSize = itemView.findViewById(R.id.list_file_size);

        topDivider = itemView.findViewById(R.id.list_top_divider);
        bottomDivider = itemView.findViewById(R.id.list_bottom_divider);
    }

    @Override
    public void refreshData(List<ListItem> listItems, int position) {
        ListItem listItem = listItems.get(position);

        fileName.setText(listItem.getFileName());
        fileSize.setText(listItem.getFileSize());
        fileIcon.setImageResource(listItem.getFileIcon());

        if(listItem.isShowTopDivider()) topDivider.setVisibility(View.VISIBLE);
        else topDivider.setVisibility(View.INVISIBLE);

        if (listItem.isShowBottomDivider()) bottomDivider.setVisibility(View.VISIBLE);
        else bottomDivider.setVisibility(View.INVISIBLE);

        //recovery the select state
        if (StateManager.getMode()==StateManager.MODE_MULTI_SELECT) {
            mask_flag.setVisibility(View.VISIBLE);
            if (listItem.isChecked()) mask_flag.setImageResource(R.mipmap.ic_selected_list);
            else mask_flag.setImageResource(R.mipmap.ic_multi_list);
        } else mask_flag.setVisibility(View.INVISIBLE);


        //register click event
        itemView.setOnClickListener(clickListener);

        //register long click event
        itemView.setOnLongClickListener(longClickListener);

        itemView.setTag(listItem);
    }
}

package com.hjz.share;

import android.animation.ObjectAnimator;
import android.annotation.SuppressLint;
import android.app.Fragment;
import android.graphics.Rect;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.transition.Transition;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.hjz.share.command.Action;
import com.hjz.share.command.BaseInvoker;
import com.hjz.share.command.Command;
import com.hjz.share.command.Invoker;
import com.hjz.share.command.Receiver;
import com.hjz.share.command.ResetCommand;
import com.hjz.share.command.SendCommand;
import com.hjz.share.holder.BaseHolder;
import com.hjz.share.holder.IViewType;
import com.hjz.share.task.FileInfoTask;
import com.hjz.share.view.BaseRelativeLayout;
import com.hjz.share.view.FullScreen;

import java.util.ArrayList;
import java.util.List;


/**
 * Created by hjz on 17-11-29.
 * for:
 */

public class ShareKeyFragment extends Fragment implements Receiver,FullScreen {

    public static final String TAG = "ShareKeyFragment";
    private DisplayMetrics metrics;//We get width and height in pixels here
    private ShareKeyAdapter mAdapter;


    private RelativeLayout topPanel;

    private FrameLayout statusBar;
    private RelativeLayout toolBar;

    private TextView mTitle;
    private TextView selectTitle;
    private ImageButton cancelSelect;

    private BaseRelativeLayout content;
    private TextView mSubTitle;
    private RecyclerView mRecyclerView;

    private RelativeLayout shareButton;

    private FrameLayout background;

    //whenever adding or removing elements, always make changes to the existing list
    private List<IViewType> dataModels = new ArrayList<>();
    private ShareKeyFragment shareKeyFragment;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.share, container, false);
        topPanel = v.findViewById(R.id.top_panel);
        topPanel.setVisibility(View.INVISIBLE);

        statusBar = v.findViewById(R.id.status_bar);
        toolBar = v.findViewById(R.id.toolbar);

        mTitle = v.findViewById(R.id.title);
        selectTitle = v.findViewById(R.id.select_status);
        cancelSelect = v.findViewById(R.id.cancel);

        content = v.findViewById(R.id.content);
        mSubTitle = v.findViewById(R.id.subtitle);
        mSubTitle.setText(R.string.share_sub_panel_title_text);
        mRecyclerView = v.findViewById(R.id.recycler);

        shareButton = v.findViewById(R.id.share_button);
        background = v.findViewById(R.id.share_background);

        metrics = getContext().getResources().getDisplayMetrics();

        content.setY(metrics.heightPixels);
        content.setMinimumHeight(metrics.heightPixels);
        content.setFragment(this);

        shareButton.setY(metrics.heightPixels);
        return v;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        shareKeyFragment = this;
        int option = View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                | View.SYSTEM_UI_FLAG_LAYOUT_STABLE;
        getActivity().getWindow().getDecorView().setSystemUiVisibility(option);
        //set adapter
        StateManager.setUiMode(StateManager.UI_MODE_HALF);
        StateManager.reset();
        mAdapter = new ShareKeyAdapter(this, dataModels);
        mRecyclerView.setAdapter(mAdapter);

        //set layout manager
        final GridLayoutManager layoutManager = new GridLayoutManager(getContext(), 3);
        layoutManager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
            @Override
            public int getSpanSize(int position) {
                int type = mRecyclerView.getAdapter().getItemViewType(position);
                switch (type) {
                    case BaseHolder.VIEW_TYPE_TITLE:
                        return layoutManager.getSpanCount();
                    case BaseHolder.VIEW_TYPE_LIST:
                        return layoutManager.getSpanCount();
                    case BaseHolder.VIEW_TYPE_DIVIDER:
                        return layoutManager.getSpanCount();
                }
                return 1;
            }
        });
        mRecyclerView.setLayoutManager(layoutManager);

        //add item decoration
        RecyclerView.ItemDecoration itemDecoration = new RecyclerView.ItemDecoration() {
            @Override
            public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
                GridLayoutManager.LayoutParams params = (GridLayoutManager.LayoutParams) view.getLayoutParams();
                int spanSize = params.getSpanSize();
                int spanIndex = params.getSpanIndex();

                if (spanSize == 1) {
                    int dp = (int) (8 * metrics.density);

                    outRect.left = dp;
                    outRect.right = dp;

                    if (spanIndex == 0) {
                        outRect.left = 2 * dp;
                        outRect.right = 0;
                    }
                    if (spanIndex == 2) {
                        outRect.left = 0;
                        outRect.right = 2 * dp;
                    }
                }

            }
        };
        mRecyclerView.addItemDecoration(itemDecoration);
        cancelSelect.setOnClickListener(v->onBackPressed());
        shareButton.setOnClickListener(v->{
            Command command = new SendCommand(shareKeyFragment, StateManager.getSendList());
            Invoker invoker = new BaseInvoker(command);
            invoker.runCommand();
        });

    }

    @Override
    public void onResume() {
        super.onResume();
        if (!content.isFullScreen()) {
            ObjectAnimator bottom_in = ObjectAnimator.ofFloat(content, "y", metrics.heightPixels, metrics.heightPixels / 5 * 2);
            bottom_in.setDuration(1000);
            bottom_in.addUpdateListener(animation -> {
                if (animation.getAnimatedFraction() == 1) {
                    Log.i(TAG, "loading...,StateManager:UI_MODE = " + StateManager.getUiMode()
                            + ",MODE = " + StateManager.getMode());
                    new FileInfoTask(getContext()).execute(dataModels,this);

                }
            });


            ObjectAnimator fade_in = ObjectAnimator.ofFloat(background, "alpha", 0f, 1f);
            fade_in.setDuration(1000);
            bottom_in.start();
            background.setVisibility(View.VISIBLE);
            fade_in.start();
        }
    }

    /**
     * all action handle at here
     * @param action contain a Action,one of {StateManager.ACTION_SEND,ACTION_SELECT,ACTION_SELECT_CANCEL}*/
    @SuppressLint("StringFormatMatches")
    @Override
    public void action(Action action) {
        switch (action.getAction()) {
            case StateManager.ACTION_SEND:
                Log.i(TAG, "action SEND,\nStateManager:UI_MODE = " + StateManager.getUiMode()
                        + ",MODE = " + StateManager.getMode());

                for (String uri : action.getStrings()) {
                    Log.i(TAG, "sending data:" + uri);
                    //todo share
                }
                getActivity().finish();
                break;
            case StateManager.ACTION_SELECT:
                if (StateManager.getUiMode() == StateManager.UI_MODE_HALF) full();//set state
                if(isFirst) enterMultiSelectMode();//only the first time need to set state
                multiSelect();//action
                Log.i(TAG, "action SELECT,\nStateManager:UI_MODE = " + StateManager.getUiMode()
                        + ",MODE = " + StateManager.getMode());
                break;
            case StateManager.ACTION_SELECT_CANCEL:
                enterNormalMode();//set state
                mAdapter.notifyDataSetChanged();
                Log.i(TAG, "action SELECT_CANCEL,\nStateManager:UI_MODE = " + StateManager.getUiMode()
                        + ",MODE = " + StateManager.getMode());
                break;

            case StateManager.ACTION_NOTIFY:
                int position = 0;
                if (action.getObject() != null && (int) action.getObject() != -1) {
                    position = (int) action.getObject();
                    mAdapter.notifyItemChanged(position);
                } else {
                    mAdapter.notifyDataSetChanged();
                }
                Log.i(TAG, "action ACTION_NOTIFY,\nStateManager:UI_MODE = " + StateManager.getUiMode()
                        + ",MODE = " + StateManager.getMode());
                break;

        }
    }

    /**设置处在全屏所需要的状态*/
    @Override
    public void enterFull() {

        topPanel.setVisibility(View.VISIBLE);
        topPanel.setBackgroundResource(R.color.share_top_panel_background_color);

        content.setFullScreen(true);
        StateManager.setUiMode(StateManager.UI_MODE_FULLSCREEN);
    }

    /**从半屏到全屏的动画以及状态设置*/
    public void full(){
        ObjectAnimator animator = ObjectAnimator.ofFloat(content, "y", metrics.density * 12);
        animator.start();
        enterFull();
    }

    /**设置处在普通模式所需要的状态*/
    public void enterNormalMode() {
        isFirst = true;
        StateManager.reset();
        mTitle.setVisibility(View.VISIBLE);
        selectTitle.setVisibility(View.INVISIBLE);
        cancelSelect.setVisibility(View.INVISIBLE);

        ObjectAnimator bottom_out = ObjectAnimator.ofFloat(shareButton, "y", metrics.heightPixels - 60 * metrics.density, metrics.heightPixels);
        bottom_out.setDuration(800);
        bottom_out.start();
    }
    /**设置处在多选模式所需要的状态*/
    private boolean isFirst = true;
    public void enterMultiSelectMode() {
        isFirst = false;
        mTitle.setVisibility(View.INVISIBLE);
        selectTitle.setVisibility(View.VISIBLE);
        cancelSelect.setVisibility(View.VISIBLE);

        ObjectAnimator bottom_out = ObjectAnimator.ofFloat(shareButton, "y", metrics.heightPixels,metrics.heightPixels - 60 * metrics.density);
        bottom_out.setDuration(800);
        bottom_out.start();
    }

    /**处理多选操作*/
    public void multiSelect(){
        selectTitle.setText(getString(R.string.share_top_panel_select_text,StateManager.size()));
        mAdapter.notifyDataSetChanged();
    }

    public boolean onBackPressed() {
        background.setVisibility(View.INVISIBLE);

            if (StateManager.getMode() == StateManager.MODE_MULTI_SELECT) {
                Command command = new ResetCommand(this);
                Invoker invoker = new BaseInvoker(command);
                invoker.runCommand();
                return true;
            }
        return false;
    }
}

package com.hjz.share.holder;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.media.MediaMetadataRetriever;
import android.os.AsyncTask;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.hjz.share.StateManager;
import com.hjz.share.command.BaseInvoker;
import com.hjz.share.command.Command;
import com.hjz.share.command.Invoker;
import com.hjz.share.command.MultiSelectCommand;
import com.hjz.share.command.NotifyCommand;
import com.hjz.share.command.Receiver;
import com.hjz.share.command.ResetCommand;
import com.hjz.share.command.SendCommand;
import com.hjz.share.model.IFileInfo;

import java.util.List;

/**
 * Created by hjz on 17-11-29.
 * for:
 */

public class BaseHolder<T extends IViewType> extends RecyclerView.ViewHolder {

    public static final String TAG = "BaseHolder";
    public static final int VIEW_TYPE_TITLE = 0;
    public static final int VIEW_TYPE_LIST = 1;
    public static final int VIEW_TYPE_GRID = 2;
    public static final int VIEW_TYPE_DIVIDER = 4;

    protected Receiver receiver;
    protected Context context;

    protected OnClickListener clickListener;
    protected OnLongClickListener longClickListener;


    public BaseHolder(Receiver receiver, int viewId, ViewGroup parent) {
        super(LayoutInflater.from(parent.getContext()).inflate(viewId, parent, false));
        this.receiver = receiver;
        context = parent.getContext();

        clickListener = new OnClickListener();
        longClickListener = new OnLongClickListener();

    }

    public void refreshData(List<T> spices, int position) {
    }

    public void loadImage(Grid grid, int position, int size){

        new BaseHolder.LoadImageTask().executeOnExecutor(AsyncTask.SERIAL_EXECUTOR,position,size,grid);
        //new BaseHolder.LoadImageTask().execute(position,size,grid);
    }

    class LoadImageTask extends AsyncTask<Object, Integer, Integer>{

        @Override
        protected Integer doInBackground(Object... params) {
            int position = (int) params[0];
            int size = (int) params[1];
            Grid fileInfo = (Grid) params[2];

            Bitmap bitmap = null;
            if (fileInfo.isVideo()) {
                MediaMetadataRetriever metadataRetriever = new MediaMetadataRetriever();
                metadataRetriever.setDataSource(fileInfo.getFilePath());
                bitmap = metadataRetriever.getFrameAtTime();
                if (bitmap!=null) bitmap = Bitmap.createScaledBitmap(bitmap, 150, 150, true);
                fileInfo.setThumbnail(new BitmapDrawable(context.getResources(), bitmap));
                metadataRetriever.release();
                return position;
            }
            else {
                BitmapFactory.Options option = new BitmapFactory.Options();
                option.inJustDecodeBounds = true;
                BitmapFactory.decodeFile(fileInfo.getFilePath(),option);
                option.inPreferredConfig = Bitmap.Config.RGB_565;
                option.inSampleSize = calculateInSampleSize(option, size, size);
                option.inJustDecodeBounds = false;
                bitmap = BitmapFactory.decodeFile(fileInfo.getFilePath(),option);
                fileInfo.setThumbnail(new BitmapDrawable(context.getResources(), bitmap));
                return position;
            }
        }
        @Override
        protected void onPostExecute(Integer position) {
            // do not directly change the View property,
            /*if (thumbnail != null) {
                thumbnail.setImageDrawable(image);
            }*/
            Command command = new NotifyCommand(receiver,position);
            Invoker invoker = new BaseInvoker(command);
            invoker.runCommand();
        }
    }

    public int calculateInSampleSize(BitmapFactory.Options options,
                                     int reqWidth, int reqHeight) {
        if (reqWidth == 0 || reqHeight == 0) {
            return 1;
        }
        // Raw height and width of image
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;
        if (height > reqHeight || width > reqWidth) {
            final int halfHeight = height / 2;
            final int halfWidth = width / 2;
            // Calculate the largest inSampleSize value that is a power of 2 and
            // keeps both
            // height and width larger than the requested height and width.
            while ((halfHeight / inSampleSize) >= reqHeight
                    && (halfWidth / inSampleSize) >= reqWidth) {
                inSampleSize *= 2;
            }
        }
        Log.d(TAG, "sampleSize:" + inSampleSize);
        return inSampleSize;
    }

    class OnClickListener<T extends IFileInfo> implements View.OnClickListener {
        private Command command;
        private Invoker invoker;
        @Override
        public void onClick(View v) {
            T fileInfo = (T) v.getTag();
            switch (StateManager.getMode()) {
                case StateManager.MODE_NORMAL:
                    command = new SendCommand(receiver,fileInfo);
                    invoker = new BaseInvoker(command);
                    invoker.runCommand();
                    break;

                case StateManager.MODE_MULTI_SELECT:
                    command = new MultiSelectCommand(receiver, fileInfo);
                    invoker = new BaseInvoker(command);
                    invoker.runCommand();
                    break;
            }

            if (StateManager.exit()) {
                //
                command = new ResetCommand(receiver);
                invoker = new BaseInvoker(command);
                invoker.runCommand();
            }
        }
    }

    class OnLongClickListener<T extends IFileInfo> implements View.OnLongClickListener {
        private Invoker invoker;
        private Command command;
        @Override
        public boolean onLongClick(View v) {
            T fileInfo = (T) v.getTag();
            command = new MultiSelectCommand(receiver, fileInfo);
            invoker = new BaseInvoker(command);
            invoker.runCommand();
            return true;
        }
    }
}

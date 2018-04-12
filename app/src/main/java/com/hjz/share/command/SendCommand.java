package com.hjz.share.command;

import com.hjz.share.StateManager;
import com.hjz.share.model.IFileInfo;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by hjz on 17-12-25.
 * for: SendCommand tells the Receiver to send data,it contains a String[] are paths
 */

public class SendCommand<T extends IFileInfo> implements Command,Action{

    private Receiver receiver;
    private List<IFileInfo> datas;

    public SendCommand(Receiver receiver, T data){
        datas = new ArrayList<>();
        datas.add(data);
        this.receiver = receiver;
    }

    public SendCommand(Receiver receiver, List<T> datas){
        this.datas = new ArrayList<>();
        this.datas.addAll(datas);
        this.receiver = receiver;
    }

    @Override
    public void execute() {
        receiver.action(this);
    }

    @Override
    public int getAction() {
        return StateManager.ACTION_SEND;
    }

    private String getString() {
        return datas.size() > 0 ? datas.get(0).getFilePath() : null;
    }

    @Override
    public Object getObject() {
        return getString();
    }

    @Override
    public String[] getStrings() {
        if (datas.size() > 0) {
            String[] paths = new String[datas.size()];
            for (int i = 0; i < datas.size(); i++) {
                paths[i] = datas.get(i).getFilePath();
            }
            return paths;
        }
        return null;
    }
}

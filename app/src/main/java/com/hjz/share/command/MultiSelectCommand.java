package com.hjz.share.command;

import com.hjz.share.StateManager;
import com.hjz.share.model.FileInfo;
import com.hjz.share.model.IFileInfo;

/**
 * Created by hjz on 17-12-25.
 * for: MultiSelectCommand changes the FileInfo select state before the Receiver takes action.
 */
public class MultiSelectCommand implements Command,Action{

    private Receiver receiver;
    private IFileInfo info;

    public MultiSelectCommand(Receiver receiver, IFileInfo info){
        this.receiver = receiver;
        this.info = info;
    }
    @Override
    public void execute() {
        StateManager.setMode(StateManager.MODE_MULTI_SELECT);
        if (info.isChecked()) {
            info.setChecked(false);
            StateManager.remove(info);
        }
        else {
            info.setChecked(true);
            StateManager.add(info);
        }
        receiver.action(this);
    }

    @Override
    public int getAction() {
        return StateManager.ACTION_SELECT;
    }

    @Override
    public Object getObject() {
        return null;
    }

    @Override
    public String[] getStrings() {
        return null;
    }
}

package com.hjz.share.command;

import com.hjz.share.StateManager;

/**
 * Created by hjz on 17-12-26.
 * for:
 */

public class NotifyCommand implements Command,Action{
    private Receiver receiver;
    private int position = -1;

    public NotifyCommand(Receiver receiver,int position) {
        this.receiver = receiver;
        this.position = position;
    }
    @Override
    public void execute() {
        receiver.action(this);
    }

    @Override
    public int getAction() {
        return StateManager.ACTION_NOTIFY;
    }

    @Override
    public Object getObject() {
        return position;
    }
    @Override
    public String[] getStrings() {
        return new String[0];
    }
}

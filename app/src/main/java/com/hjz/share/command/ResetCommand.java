package com.hjz.share.command;

import com.hjz.share.StateManager;

/**
 * Created by hjz on 17-12-25.
 * for: ResetCommand doesn't change the StateManger UiMode,
 * it changes the StateManger Mode and SelectedNum,it makes the
 * Mode = StateManger.MODE_NORMAL
 * SelectedNum = 0.
 */

public class ResetCommand implements Command,Action {
    private Receiver receiver;

    public ResetCommand(Receiver receiver){
        this.receiver = receiver;
    }
    @Override
    public void execute() {
        receiver.action(this);
    }

    @Override
    public int getAction() {
        return StateManager.ACTION_SELECT_CANCEL;
    }

    @Override
    public Object getObject() {
        return null;
    }

    @Override
    public String[] getStrings() {
        return new String[0];
    }
}

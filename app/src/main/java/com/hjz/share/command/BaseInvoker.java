package com.hjz.share.command;

/**
 * Created by hjz on 17-12-25.
 * for:
 */

public class BaseInvoker implements Invoker{
    private Command command;

    public BaseInvoker(Command command) {
        this.command = command;
    }
    @Override
    public void runCommand() {
        command.execute();
    }
}

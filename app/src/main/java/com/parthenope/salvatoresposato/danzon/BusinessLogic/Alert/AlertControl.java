package com.parthenope.salvatoresposato.danzon.BusinessLogic.Alert;

import java.util.Date;
import java.util.LinkedList;
import java.util.List;

public class AlertControl {

    private List<Command> commands;
    private long lastSend = 0;

    public AlertControl(){
        commands = new LinkedList<>();
    }

    public void addCommand(Command command) {
        commands.add(command);
    }

    public void SendAlerts() {
        long now = System.currentTimeMillis();

        if(now - lastSend > 60000) {
            for (Command comm : commands) {
                comm.SendAlert();
            }
            lastSend = System.currentTimeMillis();
        }
    }

}

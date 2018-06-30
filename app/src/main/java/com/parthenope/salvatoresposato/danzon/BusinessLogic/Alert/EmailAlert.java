package com.parthenope.salvatoresposato.danzon.BusinessLogic.Alert;

import com.parthenope.salvatoresposato.danzon.Database.Variable;
import com.parthenope.salvatoresposato.danzon.Shared.GlobalConstant;

public class EmailAlert implements Command {

    /**
     * Send alert by email
     */
    @Override
    public void SendAlert() {

        String email = Variable.getValue(GlobalConstant.KEY_EMAILS);

    }
}

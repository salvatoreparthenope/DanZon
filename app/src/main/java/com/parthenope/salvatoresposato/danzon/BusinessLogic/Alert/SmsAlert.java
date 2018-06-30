package com.parthenope.salvatoresposato.danzon.BusinessLogic.Alert;

import android.telephony.SmsManager;

import com.parthenope.salvatoresposato.danzon.Database.Variable;
import com.parthenope.salvatoresposato.danzon.Shared.GlobalConstant;

public class SmsAlert implements Command {

    /**
     * Send alert by sms
     */
    @Override
    public void SendAlert() {

        String phoneNumber = Variable.getValue(GlobalConstant.KEY_PHONE_NUMBERS);
        if(phoneNumber == null) return;
        SmsManager sms = SmsManager.getDefault();
        String lastLocation = Variable.getValue(GlobalConstant.KEY_LAST_LOCATION);
        String[] location = lastLocation != null ? lastLocation.split(",") : null;
        sms.sendTextMessage(phoneNumber, null, "HO BISOGNO DI AIUTO!!!! " + "- LA MIA ULTIMA POSIZIONE E' " + (location != null ? makeGoogleMapLink(location[0],location[1]) : ""), null, null);

    }

    public String makeGoogleMapLink(String latitude,String longitude){
        return "https://www.google.com/maps/?q="+latitude+","+longitude;
    }
}

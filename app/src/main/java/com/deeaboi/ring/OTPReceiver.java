package com.deeaboi.ring;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.provider.Telephony;
import android.telephony.SmsMessage;
import android.widget.EditText;

import androidx.annotation.RequiresApi;


public class OTPReceiver extends BroadcastReceiver
{

    private static EditText otp_1,otp_2,otp_3,otp_4,otp_5,otp_6;

    public void get_otp(EditText editText1, EditText editText2, EditText editText3, EditText editText4, EditText editText5, EditText editText6)
    {
        OTPReceiver.otp_1=editText1;
        OTPReceiver.otp_2=editText2;
        OTPReceiver.otp_3=editText3;
        OTPReceiver.otp_4=editText4;
        OTPReceiver.otp_5=editText5;
        OTPReceiver.otp_6=editText6;
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    public void onReceive(Context context, Intent intent)
    {
        SmsMessage[] smsMessages = Telephony.Sms.Intents.getMessagesFromIntent(intent);
        for (SmsMessage smsMessage : smsMessages)
        {
            String message_body = smsMessage.getMessageBody();
            String otp= message_body.substring(0,7);

            otp_1.setText(otp.substring(0,2));
            otp_2.setText(otp.substring(1,3));
            otp_3.setText(otp.substring(2,4));
            otp_4.setText(otp.substring(3,5));
            otp_5.setText(otp.substring(4,6));
            otp_6.setText(otp.substring(5,7));
        }

         //otp received fetch to send to the

    }
}
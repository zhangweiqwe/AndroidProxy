package cn.wsgwz.gravity.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.telephony.SmsMessage;



import java.text.SimpleDateFormat;
import java.util.Date;

import cn.wsgwz.gravity.util.LogUtil;

/**
 * Created by jiajiewang on 16/6/30.
 */
public class SMSBroadcastReceiver extends BroadcastReceiver {


    public static final String SMS_RECEIVED_ACTION = "android.provider.Telephony.SMS_RECEIVED";

    public SMSBroadcastReceiver() {
        super();
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        LogUtil.printSS("----> SMSBroadcastReceiver onReceive");
        if (intent.getAction().equals(SMS_RECEIVED_ACTION)) {
            Object[] pdus = (Object[]) intent.getExtras().get("pdus");
            for (Object pdu : pdus) {
                SmsMessage smsMessage = SmsMessage.createFromPdu((byte[]) pdu);
                String sender = smsMessage.getDisplayOriginatingAddress();
                //短信内容
                String content = smsMessage.getDisplayMessageBody();
                long date = smsMessage.getTimestampMillis();
                Date tiemDate = new Date(date);
                SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                String time = simpleDateFormat.format(tiemDate);


                LogUtil.printSS(sender+"\n"+content+"\n"+time);


                abortBroadcast();
            }
        }

    }




}
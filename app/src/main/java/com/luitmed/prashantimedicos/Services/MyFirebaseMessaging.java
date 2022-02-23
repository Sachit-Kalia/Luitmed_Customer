package com.luitmed.prashantimedicos.Services;

import androidx.annotation.NonNull;

import com.luitmed.prashantimedicos.Models.Common;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import java.util.Map;
import java.util.Random;

public class MyFirebaseMessaging extends FirebaseMessagingService {


    @Override
    public void onMessageReceived(@NonNull RemoteMessage remoteMessage) {
        Map<String, String> dataRecieved = remoteMessage.getData();
        if(dataRecieved!=null){
            Common.showNotification(this, new Random().nextInt(),
                    dataRecieved.get(Common.NOT_TITLE),
                    dataRecieved.get(Common.NOT_CONTENT),
                    dataRecieved.get(Common.NOT_ID),
                    null);
        }
    }

    @Override
    public void onNewToken(@NonNull String s) {
        super.onNewToken(s);
        Common.updateToken(this, s);
    }
}

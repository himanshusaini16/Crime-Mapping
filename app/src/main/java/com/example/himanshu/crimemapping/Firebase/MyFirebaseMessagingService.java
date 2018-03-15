package com.example.himanshu.crimemapping.Firebase;

import android.util.Log;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;




public class MyFirebaseMessagingService extends FirebaseMessagingService {

    private final String TAG = "JSA-FCM";

    public MyFirebaseMessagingService() {
        super();
    }

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {


        Log.d(TAG, "From: " + remoteMessage.getFrom());
        Log.d(TAG, "Notification Message Body: " + remoteMessage.getNotification().getBody());

        if(remoteMessage.getData().size() > 0){
            //handle the data message here
        }

        //getting the title and the body
        String title = remoteMessage.getNotification().getTitle();
        String body = remoteMessage.getNotification().getBody();

        //then here we can use the title and body to build a notification
        super.onMessageReceived(remoteMessage);
    }



    @Override
    public void onDeletedMessages() {
        super.onDeletedMessages();
    }

    @Override
    public void onMessageSent(String s) {
        Log.e(TAG, "onMessageSent: " + s);
        super.onMessageSent(s);
    }

    @Override
    public void onSendError(String s, Exception e) {
        Log.e(TAG, "onSendError: " + s);
        Log.e(TAG, "Exception: " + e);
        super.onSendError(s, e);
    }
}

package com.example.himanshu.crimemapping.Firebase;

import android.util.Log;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import static android.content.ContentValues.TAG;


public class MyFirebaseMessagingService extends FirebaseMessagingService {
    public MyFirebaseMessagingService() {
        super();
    }

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {


        Log.d(TAG, "From: " + remoteMessage.getFrom());
        Log.d(TAG, "Notification Message Body: " + remoteMessage.getNotification().getBody());
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

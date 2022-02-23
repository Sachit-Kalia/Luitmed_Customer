package com.luitmed.prashantimedicos.Models;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Build;
import android.widget.Toast;

import androidx.core.app.NotificationCompat;

import com.luitmed.prashantimedicos.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

public class Common {

    public static final String FCM_KEY = "AAAA-BFtc2c:APA91bFwedLUN7cWF3abFd24s8KHYxtpYwXfLOJ5wx9vCJ823ic_DtJDdpDZUjBJtuxvKBefDwtRCFgTazvLJq9kcqoWuKjBZv1RjB8AnFgZKtdPYedWpscCpOJKNBknh04GkfVGNKTQ";
    public static final String FCM_TOPIC = "PUSH_NOTIFICATIONS";
    public static final String NOT_TITLE = "title";
    public static final String NOT_CONTENT = "content";
    private static final String TOKEN_REF = "Tokens";
    public static final String NOT_ID = "id";
    public static FirebaseAuth firebaseAuth;
    public static FirebaseUser currentUser;
    public static String currentToken = "";

    // all categories options

    public static final String[] productCategories1 = {
            "Medicines",
            "Health Care Products",
            "COVID-19"
    };

    public static void showNotification(Context context, int id, String title, String content, String oid, Intent intent) {

        PendingIntent pendingIntent = null;
        if(intent != null){
            pendingIntent = PendingIntent.getActivity(context, id, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        }
        String NOTIFICATION_CHANNEL_ID = "prashanti_app";
        String channelDescription = "description";
        CharSequence channelName = "Prashanti";
        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            NotificationChannel notificationChannel = new NotificationChannel(NOTIFICATION_CHANNEL_ID, channelName, NotificationManager.IMPORTANCE_HIGH);
            notificationChannel.setDescription(channelDescription);
            notificationChannel.enableLights(true);
            notificationChannel.setLightColor(Color.RED);
            notificationChannel.setVibrationPattern(new long[]{0,1000,500,1000});
            notificationChannel.enableVibration(true);

            notificationManager.createNotificationChannel(notificationChannel);
        }
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, NOTIFICATION_CHANNEL_ID);
        builder.setContentTitle(title)
                .setContentText(content)
                .setAutoCancel(true)
                .setSmallIcon(R.drawable.orders_icon)
                .setLargeIcon(BitmapFactory.decodeResource(context.getResources(), R.drawable.ic_profile));

        if(pendingIntent!=null){
            builder.setContentIntent(pendingIntent);
        }
        Notification notification = builder.build();
        notificationManager.notify(id, notification);

    }

    public static void updateToken(Context context, String newToken) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        if(Common.currentUser == null){
            return;
        }

        db.collection(Common.TOKEN_REF).document(Common.currentUser.getUid())
                .set(new TokenModel (Common.currentUser.getPhoneNumber(), newToken))
                .addOnFailureListener(e -> {
                    Toast.makeText(context, "" + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }

    public static String createOrder() {
        return new StringBuilder("/topics/new_order").toString();
    }
}

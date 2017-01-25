package com.ateam.digitaludharseller;

/**
 * Created by Sumit on 15-Dec-16.
 */

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.RingtoneManager;
import android.net.Uri;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Calendar;
import java.util.Map;


public class MyFirebaseMessagingService extends FirebaseMessagingService {
    private static final String TAG = "FirebaseMessageService";
    Bitmap bitmap;
    SharedPreferences.Editor editor;
    private String phone_number;
    public static final String PREFS_NAME = "Activation_details";
    SharedPreferences settings;
    public static final String Date = "date";
    public static final String Dayy = "day";
    public static final String Month = "month";
    public static final String Year = "year";
    private String amount;
    private String transactionid;

    /**
     * Called when message is received.
     *
     * @param remoteMessage Object representing the message received from Firebase Cloud Messaging.
     */
    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        // There are two types of messages data messages and notification messages. Data messages are handled
        // here in onMessageReceived whether the app is in the foreground or background. Data messages are the type
        // traditionally used with GCM. Notification messages are only received here in onMessageReceived when the app
        // is in the foreground. When the app is in the background an automatically generated notification is displayed.
        // When the user taps on the notification they are returned to the app. Messages containing both notification
        // and data payloads are treated as notification messages. The Firebase console always sends notification
        // messages. For more see: https://firebase.google.com/docs/cloud-messaging/concept-options
        //

        Log.i("Remote Message marshall",remoteMessage.toString());
        Map data = remoteMessage.getData();
        RemoteMessage.Notification notification = remoteMessage.getNotification();
      //  Log.d("message",notification.toString());
       // Log.d("message",data.toString());
        Log.d(TAG, "From: " + remoteMessage.getFrom());

        // Check if message contains a data payload.
        if (remoteMessage.getData().size() > 0) {
            Log.d(TAG, "Message data payload: " + remoteMessage.getData());
        }

        // Check if message contains a notification payload.
        if (remoteMessage.getNotification() != null) {
            String msg = remoteMessage.getNotification().getBody();
            String TrueOrFlase = remoteMessage.getData().get("AnotherActivity");
            Log.d(TAG, "Message Notification Body: " + msg);
            sendNotification(msg, bitmap, TrueOrFlase);
            String message = remoteMessage.getData().toString();
           // Log.d("lenght",message);
        }

        //The message which i send will have keys named [message, image, AnotherActivity] and corresponding values.
        //You can change as per the requirement.

        //message will contain the Push Message
        ContactModel contactModel = new ContactModel();
        String message = remoteMessage.getData().get("message");

        String date = remoteMessage.getData().get("date");

        String transactionamount = remoteMessage.getData().get("amount");

        Util.updateActivationDate(this, date);
//        settings = this.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
//        editor = settings.edit();
//        if (date != null) {
//            String day = date.substring(0, 2);
//            String month = date.substring(2, 4);
//            String year = date.substring(4, 8);
//            Log.d("Date", date);
//            editor.putString(Dayy,day);
//            editor.putString(Month,month);
//            editor.putString(Year,year);
//            editor.commit();
//        }

        if (message != null) {
            Log.d("final",message);
            SQLiteHelper sqLiteHelper = new SQLiteHelper(this);
            phone_number = message.substring(0, 10);
            transactionid = message.substring(10,46);
            amount = message.substring(46);
            Log.d("amount" , amount);
            Log.d("phone",phone_number);
            Log.d("transaction",transactionid);
            Log.d("transactionamount",transactionamount);
            if (sqLiteHelper.checkTransaction(transactionid)) {
                contactModel.setBalance(Long.parseLong(amount));
                sqLiteHelper.updateBalance(contactModel, phone_number);
                contactModel.settransactionPhoneNumber(Long.parseLong(phone_number));
                Calendar c1 = Calendar.getInstance();
                String myfrmt = String.valueOf(c1.get(Calendar.YEAR)) + " : " + String.valueOf(c1.get(Calendar.MONTH)+1) + " : " + String.valueOf(c1.get(Calendar.DAY_OF_MONTH ))+ " : " + String.valueOf(c1.get((Calendar.HOUR)))+ "  :  " + String.valueOf(c1.get((Calendar.MINUTE)))+ " : " + String.valueOf(c1.get((Calendar.SECOND)));

                // formattedDate have current date/time
                contactModel.settransactionDateTime(myfrmt);
                contactModel.settransactionAmount(Long.parseLong(transactionamount));
                contactModel.setTransactionid(transactionid);
                sqLiteHelper.inserttransactionRecord(contactModel);
                Log.d("phone", phone_number);
                Log.d("Amount", amount);
                Log.d("msg", message);
            }
        }
        //imageUri will contain URL of the image to be displayed with Notification
        String imageUri = remoteMessage.getData().get("image");
        //If the key AnotherActivity has  value as True then when the user taps on notification, in the app AnotherActivity will be opened.
        //If the key AnotherActivity has  value as False then when the user taps on notification, in the app MainActivity will be opened.


        //To get a Bitmap image from the URL received
        bitmap = getBitmapfromUrl(imageUri);



    }


    /**
     * Create and show a simple notification containing the received FCM message.
     */

    private void sendNotification(String messageBody, Bitmap image, String TrueOrFalse) {
        Intent intent = new Intent(this, ThreeTabsActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.putExtra("AnotherActivity", TrueOrFalse);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0 /* Request code */, intent,
                PendingIntent.FLAG_ONE_SHOT);

        Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this)
                .setLargeIcon(image)/*Notification icon image*/
                .setSmallIcon(R.drawable.logo)
                .setContentTitle("Khata")
                .setContentText(messageBody)
                .setStyle(new NotificationCompat.BigPictureStyle()
                        .bigPicture(image))/*Notification with Image*/
                .setAutoCancel(true)
                .setSound(defaultSoundUri)
                .setContentIntent(pendingIntent);

        NotificationManager notificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        notificationManager.notify(0 /* ID of notification */, notificationBuilder.build());
    }

    /*
    *To get a Bitmap image from the URL received
    * */
    public Bitmap getBitmapfromUrl(String imageUrl) {
        try {
            URL url = new URL(imageUrl);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setDoInput(true);
            connection.connect();
            InputStream input = connection.getInputStream();
            Bitmap bitmap = BitmapFactory.decodeStream(input);
            return bitmap;

        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            return null;

        }
    }
}

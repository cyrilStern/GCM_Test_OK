package fr.cnam.smb116.gcm_test_ok;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Vibrator;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.google.android.gcm.GCMBaseIntentService;


import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Vibrator;
import android.util.Log;
import com.google.android.gcm.GCMBaseIntentService;

// https://code.google.com/p/gcm/source/browse/gcm-client/src/com/google/android/gcm/?r=3f8285f108caecf9ee040cdadda3a024b81f7e3e
// https://code.google.com/p/gcm/source/browse/gcm-client/src/com/google/android/gcm/GCMBaseIntentService.java?r=3f8285f108caecf9ee040cdadda3a024b81f7e3e

public class GCMIntentService extends GCMBaseIntentService {

    private static final boolean I = true;

    private GCMListRegIds listRegIds;

    public GCMIntentService(){
        super("1085845431027");
        this.listRegIds = new GCMListRegIds(this,"listeSMB116");
    }


    @Override
    protected void onError(Context context, String errorId) {
        if(I) Log.i("GCMIntentService", "onError: " + errorId);

        /**       Intent intentToCloudController = new Intent();
         intentToCloudController.setAction(GCMCloudController.ACTION);
         intentToCloudController.putExtra(MODE_KEY,GCMCloudController.ERROR);
         intentToCloudController.putExtra(DATA_KEY,errorId);
         sendBroadcast(intentToCloudController);
         **/
    }

    @Override
    protected void onMessage(Context context, final Intent intentFromCloud) {
        if(I)Log.i("GCMIntentService", "onMessage: " + intentFromCloud.getExtras());
        generateVibration(context, 300);
        generateNotification(context, "onMessage " + intentFromCloud.getStringExtra("message"));

        Intent i = new Intent(context, NotificationActivity.class);  // pour les tests, une notification en activity
        i.putExtra("message",intentFromCloud.getStringExtra("message"));
        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        startActivity(i);

        /**
         // l'info est transmise au CloudControleur, ... journal, log ...
         Intent intentToCloudController = new Intent();
         intentToCloudController.setAction(GCMCloudController.ACTION);
         intentToCloudController.putExtra(MODE_KEY,GCMCloudController.PUSH);
         intentToCloudController.putExtra(OPERATION_KEY,intentFromCloud.getStringExtra(OPERATION_KEY));
         intentToCloudController.putExtra(DATA_KEY,intentFromCloud.getStringExtra(DATA_KEY));
         intentToCloudController.putExtra(PUBLISHER_REG_ID_KEY,intentFromCloud.getStringExtra(PUBLISHER_REG_ID_KEY));
         sendBroadcast(intentToCloudController);


         // Envoi au destinataire, en direct: fonction de l'ACTION_KEY
         Intent intentToAnController = new Intent();
         intentToAnController.setAction(intentFromCloud.getStringExtra(ACTION_KEY));
         intentToAnController.putExtra(OPERATION_KEY,intentFromCloud.getStringExtra(OPERATION_KEY));
         intentToAnController.putExtra(DATA_KEY,intentFromCloud.getStringExtra(DATA_KEY));
         intentToAnController.putExtra(PUBLISHER_REG_ID_KEY,intentFromCloud.getStringExtra(PUBLISHER_REG_ID_KEY));
         sendBroadcast(intentToAnController);
         **/

    }



    @Override
    protected void onRegistered(Context context, String regId) {
        if(I)Log.i("GCMIntentService", "onRegistered: regId " + regId);
        this.listRegIds.add(regId);
        generateNotification(context,"onRegistered: " + regId.substring(0, 10));
        generateVibration(context,1000);

        /**       Intent intentToCloudController = new Intent();
         intentToCloudController.setAction(GCMCloudController.ACTION);
         intentToCloudController.putExtra(MODE_KEY,GCMCloudController.REGISTERED);
         intentToCloudController.putExtra(PUBLISHER_REG_ID_KEY, regId);
         sendBroadcast(intentToCloudController);
         **/
    }

    @Override
    protected void onUnregistered(Context context, String regId) {

        if(I)Log.i("GCMIntentService", "onUnregistered: " + regId);
        generateVibration(context,100);
        generateVibration(context,100);
        generateNotification(context,"onUnregistered: " + regId);
        this.listRegIds.remove(regId);
    }


    private static void generateNotification(Context context, String message){
        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(context)
                        .setSmallIcon(R.drawable.bell)
                        .setContentTitle("TP Cnam GCM")
                        .setContentText(message);

        Intent intent = new Intent(context, MainActivity.class);
        PendingIntent pIntent = PendingIntent.getActivity(context, 0, intent,PendingIntent.FLAG_UPDATE_CURRENT);
        mBuilder.setContentIntent(pIntent);

        NotificationManager nm =(NotificationManager) context.getSystemService(NOTIFICATION_SERVICE);
        nm.notify(1, mBuilder.build());
    }

    private static void generateVibration(Context context, long duration){
        Vibrator v = (Vibrator) context.getSystemService(Context.VIBRATOR_SERVICE);
        v.vibrate(duration);
    }

}

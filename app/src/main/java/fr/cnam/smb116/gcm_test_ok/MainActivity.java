package fr.cnam.smb116.gcm_test_ok;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;


import com.google.android.gcm.GCMRegistrar;
import com.google.android.gcm.server.MulticastResult;
import com.google.android.gcm.server.Sender;

import java.io.IOException;
import java.util.List;


public class MainActivity extends Activity {


    private final String TAG = this.getClass().getSimpleName();
    private final static boolean I = true;

    // une sortie console minimaliste
    private TextView tv;
    // le message à envoyer
    private EditText et;
    // La liste des abonnés
    private GCMListRegIds listRegIds;
    // Envoi vers le cloud
    private Sender sender;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_layout);
        if(!internet()) return;

        this.tv = (TextView) findViewById(R.id.textId);
        this.et = (EditText) findViewById(R.id.messageId);
        et.requestFocus();
        this.sender = new Sender(MainActivity.this.getString(R.string.api_key));
        this.listRegIds = new GCMListRegIds(this, GCMListRegIds.LIST_NAME); // liste partagée par tous
        // tout envoi de message est diffusé aux abonnés
        //this.listRegIds = new GCMListRegIds(this,"un nom de liste unique"); // un nom de liste unique pour vos tests
        obtainSelfRegId();
    }



    public void onClickSend(View v) {
        String message = et.getText().toString();
        SendMessageToCloudTask task = new SendMessageToCloudTask(message);
        try {
            if (Build.VERSION.SDK_INT >= 11) {
                task.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
            } else {
                task.execute();
            }
        } catch (Exception e) {
            e.printStackTrace(); //
            tv.setText(e.getMessage());
        }
    }


    public void onClickListRegIds(View v) {
        if (I) Log.i(TAG, "onClickListRegIds");
        GetListRegIdsTask task = new GetListRegIdsTask();
        try {
            if (Build.VERSION.SDK_INT >= 11) {
                task.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
            } else {
                task.execute();
            }
        } catch (Exception e) {
            e.printStackTrace(); //
            tv.setText("Exception: " + e.getMessage());
        }
    }


    // Recherche du RegId
    private void obtainSelfRegId() {
        String regId = GCMRegistrar.getRegistrationId(this);
        //String regId = ""; // pour les tests, engendre un appel forcé de onRegistered
        if (I) Log.i(TAG, "appel de GCMRegistrar.register " + getString(R.string.project_id));
        if (I)
            Log.i(TAG, "appel de GCMRegistrar.getRegistrationId regId: " + ("".equals(regId) ? " vide" : regId));

        if ("".equals(regId)) {
            GCMRegistrar.register(this, getString(R.string.project_id));
        } else {
            this.listRegIds.add(regId);
            /**

             Log.i(TAG,"sendBroadcast " + GCMCloudController.ACTION);
             Intent intentToCloudController = new Intent();
             intentToCloudController.setAction(GCMCloudController.ACTION);
             intentToCloudController.putExtra(MODE_KEY,GCMCloudController.REGISTERED);
             intentToCloudController.putExtra(PUBLISHER_REG_ID_KEY, regId);
             sendBroadcast(intentToCloudController);
             **/
        }
    }

    // -------------- Utilitaires d'accès -------------------

    public  boolean internet(){
        ConnectivityManager connMgr = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        if (networkInfo == null || !(networkInfo.isConnected())) {
            AlertDialog alertDialog = new AlertDialog.Builder(this).create();
            alertDialog.setTitle("Internet");
            alertDialog.setMessage("Vérifiez votre connexion !");
            alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                            MainActivity.this.finish();
                        }
                    });
            alertDialog.show();
            return false;
        }else
            return true;
    }




    private class SendMessageToCloudTask extends AsyncTask<String, Void, Exception> {
        private String message;

        public SendMessageToCloudTask(String message) {
            this.message = message;
        }

        protected Exception doInBackground(String... params) {
            final com.google.android.gcm.server.Message msg = new com.google.android.gcm.server.Message.Builder()
                    //.collapseKey("1") // avec la meme cl , le nouveau remplace l'ancien pour le meme utilisateur
                    //.timeToLive(30)  // le message est conserve  30 secondes, ne rien mettre, il est conserve  4 semaines
                    .timeToLive(60 * 60 * 24) // 24 heures
                            //.timeToLive(120)
                            //.timeToLive(0)  // maintenant ou jamais
                    .delayWhileIdle(true)
                    .addData("message", message)
                    .build();
            final List<String> abonnes = listRegIds.regIds();
            Exception cause = null;
            try {
                if(internet() && abonnes.size()>=0) {
                    MulticastResult result = sender.send(msg, abonnes, 15);
                }
            } catch (Exception e) {
                e.printStackTrace();
                cause = e;
            }
            return cause;
        }
        @Override
        protected void onPostExecute(Exception e){
            if(e!=null)tv.setText("Exception: " + e.getMessage());
        }
    }

    private class GetListRegIdsTask extends AsyncTask<Void, String, Void> {
        private String message;
        protected Void doInBackground(Void... params) {
            int number = listRegIds.size();
            String str = number + " subscriber" + (number>1?"s":"") + ", " + listRegIds.getName()+ "\n";

            for (String regId : listRegIds.regIds()) {  // tous les regids
                if (I) Log.i(TAG, regId.substring(0, 10));
                if(regId.startsWith("APA91")&&regId.length()>20)
                    str = str + regId.substring(0, 20) + "..." + regId.substring(regId.length()-5, regId.length()) +"\n";
                else
                    publishProgress("Regid non conforme ?!: " + regId);
            }
            publishProgress(str);
            return null;
        }
        @Override
        public void onProgressUpdate(String... values) {
            tv.setText(values[0]);
        }

    }

}

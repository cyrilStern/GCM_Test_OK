package fr.cnam.smb116.gcm_test_ok;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.SystemClock;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.app.LoaderManager.LoaderCallbacks;

import android.content.CursorLoader;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;

import android.os.Build;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.inputmethod.EditorInfo;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import static android.Manifest.permission.READ_CONTACTS;

/**
 * A login screen that offers login via email/password.
 */
public class NotificationActivity extends Activity {

    private AlertDialog dialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.dialog = createDialog(getIntent().getStringExtra("message"));
        this.dialog.show();
    }

    @Override
    public void onNewIntent(Intent intent){
        if(dialog!=null){
            dialog.dismiss();
            this.dialog = createDialog(intent.getStringExtra("message"));
            this.dialog.show();
            SystemClock.sleep(500);
        }
    }

    private AlertDialog createDialog(String message){
        AlertDialog.Builder builder= new AlertDialog.Builder(this)
                .setMessage(message)
                .setTitle("TP Cnam GCM")
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        NotificationActivity.this.dialog.dismiss();
                        NotificationActivity.this.finish();
                    }
                });
        return builder.create();
    }

}


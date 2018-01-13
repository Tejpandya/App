package fusioninfotech.com.hideit.Activity;

import android.arch.persistence.room.Room;
import android.content.Intent;
import android.hardware.fingerprint.FingerprintManager;
import android.os.AsyncTask;
import android.provider.Settings;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.andrognito.patternlockview.PatternLockView;
import com.andrognito.patternlockview.listener.PatternLockViewListener;
import com.andrognito.patternlockview.utils.PatternLockUtils;
import com.multidots.fingerprintauth.AuthErrorCodes;
import com.multidots.fingerprintauth.FingerPrintAuthCallback;
import com.multidots.fingerprintauth.FingerPrintAuthHelper;

import java.util.ArrayList;
import java.util.List;

import fusioninfotech.com.hideit.Activity.INTRO_PASSWORD.ChoosePasswordPatternActivity;
import fusioninfotech.com.hideit.Activity.INTRO_PASSWORD.FingerPrintActivity;
import fusioninfotech.com.hideit.Activity.INTRO_PASSWORD.PasswordActivity;
import fusioninfotech.com.hideit.Activity.INTRO_PASSWORD.PatternPasswordActivity;
import fusioninfotech.com.hideit.Helper.SessionManager;
import fusioninfotech.com.hideit.R;
import fusioninfotech.com.hideit.databaseHelper.User;
import fusioninfotech.com.hideit.databaseHelper.UserDatabase;
import in.arjsna.passcodeview.PassCodeView;

public class UnLockActivity extends AppCompatActivity {

    UserDatabase userDatabase;
    SessionManager sessionManager;
    List<User> list_user_data;
    PatternLockView mPatternLockView;
    FingerPrintAuthHelper mFingerPrintAuthHelper;

    Bundle b;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        userDatabase = Room.databaseBuilder(getApplicationContext(),
                UserDatabase.class, "user-db").build();
        sessionManager = new SessionManager(UnLockActivity.this);
        list_user_data = new ArrayList<>();
        b = getIntent().getExtras();

        if (sessionManager.isLoggedIn()) {
            new getPassword().execute();
        }

    }


    private class getPassword extends AsyncTask<Void, Void, Void> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected Void doInBackground(Void... voids) {
            //Let's add some dummy data to the database.

            //Now access all the methods defined in DaoAccess with sampleDatabase object
            list_user_data = userDatabase.daoAccess().getLastPassword();

            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);


            if (sessionManager.getType().equals("pattern")) {
                setContentView(R.layout.activity_pattern_password);


                mPatternLockView = (PatternLockView) findViewById(R.id.pattern_lock_view);

                mPatternLockView.addPatternLockListener(new PatternLockViewListener() {
                    @Override
                    public void onStarted() {
                        Log.d(getClass().getName(), "Pattern drawing started");
                    }

                    @Override
                    public void onProgress(List<PatternLockView.Dot> progressPattern) {
                        Log.d(getClass().getName(), "Pattern progress: " +
                                PatternLockUtils.patternToString(mPatternLockView, progressPattern));
                    }

                    @Override
                    public void onComplete(List<PatternLockView.Dot> pattern) {
                        Log.d(getClass().getName(), "Pattern complete: " + PatternLockUtils.patternToString(mPatternLockView, pattern));


                        if (list_user_data.get(0).getPassword().equals(PatternLockUtils.patternToString(mPatternLockView, pattern))) {

                            sessionManager.setApplicationStatus(b.getString("app_name"),true);
                            finish();
                            System.exit(0);

                        } else {
                            Toast.makeText(UnLockActivity.this, "Pattern Doesn't match! Try again", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onCleared() {

                    }
                });


            } else if (sessionManager.getType().equals("pin")) {
                setContentView(R.layout.activity_password);

                final PassCodeView passCodeView = (PassCodeView) findViewById(R.id.pass_code_view);
                passCodeView.setKeyTextColor(getResources().getColor(android.R.color.black));
                passCodeView.setOnTextChangeListener(new PassCodeView.TextChangeListener() {
                    @Override
                    public void onTextChanged(String text) {

                        Log.d("PASSWORD ", "Entered password " + text);
                        if (list_user_data.get(0).getPassword().equals(text)) {


                            finish();

                        }
                    }
                });

            } else {
                setContentView(R.layout.activity_finger_print);

                mFingerPrintAuthHelper = FingerPrintAuthHelper.getHelper(UnLockActivity.this, new FingerPrintAuthCallback() {
                    @Override
                    public void onNoFingerPrintHardwareFound() {
                        Toast.makeText(UnLockActivity.this, "Device does not have finger print scanner.", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onNoFingerPrintRegistered() {
                        Toast.makeText(UnLockActivity.this, "There are no finger prints registered on this device.", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onBelowMarshmallow() {
                        Toast.makeText(UnLockActivity.this, "Device running below API 23 version of android that does not support finger print authentication.", Toast.LENGTH_SHORT).show();

                    }

                    @Override
                    public void onAuthSuccess(FingerprintManager.CryptoObject cryptoObject) {

                        finish();
                    }

                    @Override
                    public void onAuthFailed(int errorCode, String errorMessage) {
                        switch (errorCode) {    //Parse the error code for recoverable/non recoverable error.
                            case AuthErrorCodes.CANNOT_RECOGNIZE_ERROR:
                                Toast.makeText(UnLockActivity.this, "", Toast.LENGTH_SHORT).show();
                                break;
                            case AuthErrorCodes.NON_RECOVERABLE_ERROR:
                                //This is not recoverable error. Try other options for user authentication. like pin, password.
                                break;
                            case AuthErrorCodes.RECOVERABLE_ERROR:
                                //Any recoverable error. Display message to the user.
                                break;
                        }
                    }
                });

            }
        }
    }
}

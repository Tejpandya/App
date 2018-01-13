package fusioninfotech.com.hideit.Activity.INTRO_PASSWORD;

import android.arch.persistence.room.Room;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.andrognito.patternlockview.PatternLockView;
import com.andrognito.patternlockview.listener.PatternLockViewListener;
import com.andrognito.patternlockview.utils.PatternLockUtils;

import java.util.ArrayList;
import java.util.List;

import fusioninfotech.com.hideit.Activity.MainActivity;
import fusioninfotech.com.hideit.Helper.SessionManager;
import fusioninfotech.com.hideit.R;
import fusioninfotech.com.hideit.databaseHelper.User;
import fusioninfotech.com.hideit.databaseHelper.UserDatabase;

public class PatternPasswordActivity extends AppCompatActivity {

    PatternLockView mPatternLockView;
    int counter = 0  ;
    ArrayList password_text = new ArrayList();
    UserDatabase userDatabase;
    SessionManager sessionManager ;
    Bundle b ;
    List<PatternLockView.Dot>  list_dot;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pattern_password);
        userDatabase = Room.databaseBuilder(getApplicationContext(),
                UserDatabase.class, "user-db").build();

        sessionManager = new SessionManager(PatternPasswordActivity.this);

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
                Log.d(getClass().getName(), "Pattern complete: " +PatternLockUtils.patternToString(mPatternLockView, pattern));

                b = getIntent().getExtras();
                if (b.getString("is_pass").equals("true")) {

                    if (b.getString("current_pass").equals(PatternLockUtils.patternToString(mPatternLockView, pattern))) {
                        Intent i = new Intent(PatternPasswordActivity.this,MainActivity.class);
                        startActivity(i);
                        finish();

                    }else {
                        Toast.makeText(PatternPasswordActivity.this, "Pattern Doesn't match! Try again", Toast.LENGTH_SHORT).show();
                    }
                }else {
                    if (counter < 1){

                        password_text.add(0,PatternLockUtils.patternToString(mPatternLockView, pattern));
                        mPatternLockView.clearPattern();
                        Toast.makeText(PatternPasswordActivity.this, "Please Confirm Pattern Again", Toast.LENGTH_SHORT).show();
                        counter ++;

                    }else if (counter == 1){
                        password_text.add(1,PatternLockUtils.patternToString(mPatternLockView, pattern));
                        mPatternLockView.clearPattern();

                        if (password_text.get(0).equals(password_text.get(1))){

                            new DatabaseAsync().execute();
                        }else {
                            Toast.makeText(PatternPasswordActivity.this, "Pattern doesn't match", Toast.LENGTH_SHORT).show();
                            Intent intent = getIntent();
                            finish();
                            startActivity(intent);
                        }
                    }
                }


            }

            @Override
            public void onCleared() {

            }
        });
    }


    private class DatabaseAsync extends AsyncTask<Void, Void, Void> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            //Perform pre-adding operation here.
        }

        @Override
        protected Void doInBackground(Void... voids) {
            //Let's add some dummy data to the database.

            User user = new User();
            user.setPassword(password_text.get(0).toString());
            user.setPassword_type("pattern");

            //Now access all the methods defined in DaoAccess with sampleDatabase object
            userDatabase.daoAccess().insertOnlySingleRecord(user);

            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);

            sessionManager.setLogin(true);
            sessionManager.setType("pattern");

            Intent i = new Intent(PatternPasswordActivity.this,MainActivity.class);
            startActivity(i);
            finish();
        }
    }
}

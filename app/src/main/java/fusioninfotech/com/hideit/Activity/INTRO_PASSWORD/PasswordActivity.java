package fusioninfotech.com.hideit.Activity.INTRO_PASSWORD;

import android.arch.persistence.room.Room;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;


import java.util.ArrayList;

import fusioninfotech.com.hideit.Activity.MainActivity;
import fusioninfotech.com.hideit.Helper.SessionManager;
import fusioninfotech.com.hideit.R;
import fusioninfotech.com.hideit.databaseHelper.User;
import fusioninfotech.com.hideit.databaseHelper.UserDatabase;
import in.arjsna.passcodeview.PassCodeView;

public class PasswordActivity extends AppCompatActivity {

    UserDatabase userDatabase;
    ArrayList password_text = new ArrayList();
    int counter = 0;
    SessionManager sessionManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_password);
        userDatabase = Room.databaseBuilder(getApplicationContext(),
                UserDatabase.class, "user-db").build();

        sessionManager = new SessionManager(PasswordActivity.this);
        final PassCodeView passCodeView = (PassCodeView) findViewById(R.id.pass_code_view);
        passCodeView.setKeyTextColor(getResources().getColor(android.R.color.black));
        passCodeView.setOnTextChangeListener(new PassCodeView.TextChangeListener() {
            @Override
            public void onTextChanged(String text) {

                Log.d("PASSWORD ", "Entered password " + text);

                if (text.length() == 4) {

                    password_text.add(counter, text);
                    counter++;
                    text = "";
                    passCodeView.reset();

                    System.out.println("length of password txt" + password_text.size());
                    if (password_text.size() == 2) {

                        if (password_text.get(0).equals(password_text.get(1))) {
                            new DatabaseAsync().execute();
                        } else {
                            Toast.makeText(PasswordActivity.this, "Password doesn't match", Toast.LENGTH_SHORT).show();
                            Intent intent = getIntent();
                            finish();
                            startActivity(intent);
                        }
                    }
                }
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
            user.setPassword_type("pin");

            //Now access all the methods defined in DaoAccess with sampleDatabase object
            userDatabase.daoAccess().insertOnlySingleRecord(user);

            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);

            sessionManager.setLogin(true);
            sessionManager.setType("pin");

            Intent i = new Intent(PasswordActivity.this, MainActivity.class);
            startActivity(i);
        }
    }
}

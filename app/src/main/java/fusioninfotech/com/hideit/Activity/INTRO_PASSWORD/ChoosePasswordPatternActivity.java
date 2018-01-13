package fusioninfotech.com.hideit.Activity.INTRO_PASSWORD;

import android.arch.persistence.room.Room;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;

import java.util.ArrayList;
import java.util.List;

import fusioninfotech.com.hideit.Helper.SessionManager;
import fusioninfotech.com.hideit.R;
import fusioninfotech.com.hideit.databaseHelper.User;
import fusioninfotech.com.hideit.databaseHelper.UserDatabase;

public class ChoosePasswordPatternActivity extends AppCompatActivity {

    LinearLayout img_pattern, img_password,img_fingerprint;
    SessionManager sessionManager;
    UserDatabase userDatabase;
    List<User> list_user_data;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choose_password_pattern);

        img_password = (LinearLayout) findViewById(R.id.img_password);
        img_pattern = (LinearLayout) findViewById(R.id.img_pattern);
        img_fingerprint = (LinearLayout) findViewById(R.id.img_fingerprint);


        list_user_data = new ArrayList<>();

        sessionManager = new SessionManager(ChoosePasswordPatternActivity.this);
        userDatabase = Room.databaseBuilder(getApplicationContext(),
                UserDatabase.class, "user-db").build();


        if (sessionManager.isLoggedIn()) {

            new getPassword().execute();
        }

        img_fingerprint.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(ChoosePasswordPatternActivity.this,FingerPrintActivity.class);
                startActivity(i);
                finish();
            }
        });


        img_password.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent i = new Intent(ChoosePasswordPatternActivity.this, PasswordActivity.class);
                i.putExtra("is_pass","false");
                startActivity(i);
            }
        });

        img_pattern.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent i = new Intent(ChoosePasswordPatternActivity.this, PatternPasswordActivity.class);
                i.putExtra("is_pass","false");
                startActivity(i);
            }
        });
    }

    private class getPassword extends AsyncTask<Void, Void, Void> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            //Perform pre-adding operation here.
            System.out.println("in thissssss");
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
                Intent i = new Intent(ChoosePasswordPatternActivity.this, PatternPasswordActivity.class);
                i.putExtra("is_pass","true");
                i.putExtra("current_pass",list_user_data.get(0).getPassword().toString());
                startActivity(i);
                finish();
            } else if (sessionManager.getType().equals("pin")) {
                Intent i = new Intent(ChoosePasswordPatternActivity.this, PasswordActivity.class);
                i.putExtra("is_pass","true");
                i.putExtra("current_pass",list_user_data.get(0).getPassword().toString());
                startActivity(i);
                finish();
            } else {
                Intent intent = getIntent();
                finish();
                startActivity(intent);
            }
        }
    }
}

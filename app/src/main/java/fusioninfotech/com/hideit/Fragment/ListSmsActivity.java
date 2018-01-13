package fusioninfotech.com.hideit.Fragment;

import android.app.ProgressDialog;
import android.arch.persistence.room.Room;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import java.util.ArrayList;
import java.util.List;

import fusioninfotech.com.hideit.Activity.ContactSmsActivity;
import fusioninfotech.com.hideit.Adapter.ListSmsAdapter;
import fusioninfotech.com.hideit.Helper.Constant;
import fusioninfotech.com.hideit.Helper.ContactModel;
import fusioninfotech.com.hideit.Helper.SMSData;
import fusioninfotech.com.hideit.R;
import fusioninfotech.com.hideit.databaseHelper.UserDatabase;


public class ListSmsActivity extends AppCompatActivity {

    ListSmsAdapter smsAdapter;
    RecyclerView rv_sms;
    List<SMSData> smsList = new ArrayList<SMSData>();
    Button btn_import_sms;
    UserDatabase userDatabase;
    ProgressDialog pd;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sms_list);


        rv_sms = (RecyclerView)findViewById(R.id.rv_sms);
        btn_import_sms = (Button)findViewById(R.id.btn_import_sms);
        pd = new ProgressDialog(ListSmsActivity.this);
        pd.setTitle("Loading Contacts");
        pd.setMessage("Please Wait");

        new LoadSmsAyscn().execute();

        userDatabase = Room.databaseBuilder(getApplicationContext(),
                UserDatabase.class, "sms-db").build();

        btn_import_sms.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                new SaveSmsData().execute();

            }
        });
    }

    class LoadSmsAyscn extends AsyncTask<Void, Void, Void> {
        ProgressDialog pd;

        @Override
        protected void onPreExecute() {
            // TODO Auto-generated method stub
            super.onPreExecute();

            pd = ProgressDialog.show(ListSmsActivity.this, "Loading Sms",
                    "Please Wait");
        }

        @Override
        protected Void doInBackground(Void... params) {
            // TODO Auto-generated method stub



            Uri uri = Uri.parse("content://sms/inbox");
            Cursor c= getContentResolver().query(uri, null, null ,null,null);
            startManagingCursor(c);

            // Read the sms data and store it in the list
            if(c.moveToFirst()) {
                for(int i=0; i < c.getCount(); i++) {
                    SMSData sms = new SMSData();
                    sms.setBody(c.getString(c.getColumnIndexOrThrow("body")).toString());
                    sms.setNumber(c.getString(c.getColumnIndexOrThrow("address")).toString());
                    smsList.add(sms);

                    c.moveToNext();
                }
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            // TODO Auto-generated method stub
            super.onPostExecute(result);

            pd.cancel();

            smsAdapter = new ListSmsAdapter(ListSmsActivity.this, smsList);
            RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(ListSmsActivity.this);
            rv_sms.setLayoutManager(mLayoutManager);
            rv_sms.setItemAnimator(new DefaultItemAnimator());
            rv_sms.setAdapter(smsAdapter);


        }

    }

    class SaveSmsData extends AsyncTask<Void, Void, Void> {
        @Override
        protected void onPreExecute() {
            // TODO Auto-generated method stub
            super.onPreExecute();

            pd.show();
        }

        @Override
        protected Void doInBackground(Void... params) {
            // TODO Auto-generated method stub

            /*for (int i = 0 ; i < Constant.Constant_list_contact_model.size();i++){
                if (Constant.Constant_list_contact_model.get(i).isIs_selected())

                list_database_contact_model.add(Constant.Constant_list_contact_model.get(i));
                System.out.println("my new list "+Constant.Constant_list_contact_model.get(i));
            }*/

            List<SMSData> list_selected_sms = new ArrayList();
            for (SMSData pojo : Constant.Constant_list_sms_model) {
                if (pojo.isIs_selected()) {
                    list_selected_sms.add(pojo);
                }

            }
            userDatabase.daoAccessSms().insertMultipleSmsList(list_selected_sms);

            for (int i = 0; i < Constant.Constant_list_sms_model.size(); i++) {
                if (Constant.Constant_list_sms_model.get(i).isIs_selected()) {
                   deleteSms(ListSmsActivity.this,Constant.Constant_list_sms_model.get(i).getBody(),Constant.Constant_list_sms_model.get(i).getNumber());
                }
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            // TODO Auto-generated method stub
            super.onPostExecute(result);

            pd.cancel();
            Intent i = new Intent(ListSmsActivity.this, ContactSmsActivity.class);
            startActivity(i);
        }

    }


    public  void deleteSms(Context ctx, String message,String number) {

        System.out.println("im here 1 ");
        try {
            Uri uriSms = Uri.parse("content://sms/inbox");
            Cursor c = ctx.getContentResolver().query(
                    uriSms,
                    new String[] { "_id", "thread_id", "address", "person",
                            "date", "body" }, "read=0", null, null);


            if (c != null && c.moveToFirst()) {
                do {

                    System.out.println("im here 2 ");

                    long id = c.getLong(0);
                    long threadId = c.getLong(1);
                    String address = c.getString(2);
                    String body = c.getString(5);
                    String date = c.getString(3);
                    Log.e("log>>>",
                            "0--->" + c.getString(0) + "1---->" + c.getString(1)
                                    + "2---->" + c.getString(2) + "3--->"
                                    + c.getString(3) + "4----->" + c.getString(4)
                                    + "5---->" + c.getString(5));
                    Log.e("log>>>", "date" + c.getString(0));

                    ContentValues values = new ContentValues();
                    values.put("read", true);
                    getContentResolver().update(Uri.parse("content://sms/"),
                            values, "_id=" + id, null);

                    if (message.equals(body) && address.equals(number)) {
                        // mLogger.logInfo("Deleting SMS with id: " + threadId);

                        System.out.println("im here 3 "+message+body);
                        ctx.getContentResolver().delete(
                                Uri.parse("content://sms/" + id), "date=?",
                                new String[] { c.getString(4) });
                        Log.e("log>>>", "Delete success.........");
                    }
                } while (c.moveToNext());
            }
        } catch (Exception e) {
            Log.e("log>>>", e.toString());
        }





       /* try {

            Uri uriSms = Uri.parse("content://sms/inbox");
            Cursor c = getContentResolver().query(uriSms,
                    new String[] { "_id", "thread_id", "address",
                            "person", "date", "body" }, null, null, null);

            if (c != null && c.moveToFirst()) {
                do {

                    System.out.println("inside smssss 2  ");
                    long id = c.getLong(0);
                    long threadId = c.getLong(1);
                    String address = c.getString(2);
                    String body = c.getString(5);


                    if (message.equals(body) && address.equals(number)) {

                        System.out.println("inside smssss "+body+number+""+id);
                        getContentResolver().delete(
                                Uri.parse("content://sms/" + id), null, null);
                    }
                } while (c.moveToNext());
            }
        } catch (Exception e) {
            Log.e("ERROR","errorr aaivii "+e);
        }*/
    }


}

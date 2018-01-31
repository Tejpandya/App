package fusioninfotech.com.hideit.Fragment;


import android.app.ProgressDialog;
import android.arch.persistence.room.Room;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListAdapter;

import java.util.ArrayList;
import java.util.List;

import fusioninfotech.com.hideit.Activity.ContactSmsActivity;
import fusioninfotech.com.hideit.Adapter.ListContactAdapter;
import fusioninfotech.com.hideit.Helper.Constant;
import fusioninfotech.com.hideit.Helper.ContactModel;
import fusioninfotech.com.hideit.R;
import fusioninfotech.com.hideit.databaseHelper.UserDatabase;

/**
 * A simple {@link Fragment} subclass.
 */
public class ListContactActivity extends AppCompatActivity {

    ListContactAdapter contactAdapter;

    List<ContactModel> list_contact_model = new ArrayList<>();

    RecyclerView rv_contact;
    ProgressDialog pd;
    Button btn_import_contact;
    UserDatabase userDatabase;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contact_list);


        rv_contact = (RecyclerView) findViewById(R.id.rv_contact);
        btn_import_contact = (Button) findViewById(R.id.btn_import_contact);
        pd = new ProgressDialog(ListContactActivity.this);
        pd.setTitle("Loading Contacts");
        pd.setMessage("Please Wait");
        userDatabase = Room.databaseBuilder(getApplicationContext(),
                UserDatabase.class, "Contact").build();


       new LoadContactsAyscn().execute();

        btn_import_contact.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                new SaveContactData().execute();


            }
        });


    }


    class LoadContactsAyscn extends AsyncTask<Void, Void, Void> {
        @Override
        protected void onPreExecute() {
            // TODO Auto-generated method stub
            super.onPreExecute();

            pd.show();
        }

        @Override
        protected Void doInBackground(Void... params) {
            // TODO Auto-generated method stub

            Cursor c = getContentResolver().query(
                    ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null,
                    null, null, ContactsContract.Contacts.DISPLAY_NAME + " ASC");
            c.moveToFirst();
            String lastPhoneName = "";
            String lastPhoneNumber = "";

            if (c != null && c.moveToFirst()) {
                do {
                    String contactid = c
                            .getString(c
                                    .getColumnIndex(ContactsContract.CommonDataKinds.Phone.CONTACT_ID));

                    String contactName = c
                            .getString(c
                                    .getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME));
                    String phNumber = c
                            .getString(c
                                    .getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));

                    if (!contactName.equalsIgnoreCase(lastPhoneName) && !phNumber.equalsIgnoreCase(lastPhoneNumber)) {

                        lastPhoneName = contactName;
                        lastPhoneNumber = phNumber;

                        ContactModel model = new ContactModel();
                        model.setContact_id(contactid);
                        model.setContact_name(contactName.replaceAll("\\s+", ""));
                        model.setContact_number(phNumber.replaceAll("\\D+", ""));


                        list_contact_model.add(model);

                    }
                } while (c.moveToNext());

                    c.close();

            }



            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            // TODO Auto-generated method stub
            super.onPostExecute(result);

            pd.cancel();

            contactAdapter = new ListContactAdapter(ListContactActivity.this, list_contact_model);
            rv_contact.setItemAnimator(new DefaultItemAnimator());
            rv_contact.setAdapter(contactAdapter);


        }

    }


    class SaveContactData extends AsyncTask<Void, Void, Void> {
        @Override
        protected void onPreExecute() {
            // TODO Auto-generated method stub
            super.onPreExecute();

            pd.show();
        }

        @Override
        protected Void doInBackground(Void... params) {
            // TODO Auto-generated method stub

            /*.0for (int i = 0 ; i < Constant.Constant_list_contact_model.size();i++){
                if (Constant.Constant_list_contact_model.get(i).isIs_selected())

                list_database_contact_model.add(Constant.Constant_list_contact_model.get(i));
                System.out.println("my new list "+Constant.Constant_list_contact_model.get(i));
            }*/

            List<ContactModel> list_selected_contact = new ArrayList();
            for (ContactModel pojo : Constant.Constant_list_contact_model) {
                if (pojo.isIs_selected()) {
                    list_selected_contact.add(pojo);
                }
            }

            userDatabase.daoAccessContact().insertMultipleContactList(list_selected_contact);

            for (int i = 0; i < Constant.Constant_list_contact_model.size(); i++) {
                if (Constant.Constant_list_contact_model.get(i).isIs_selected()) {
                    deleteContact(ListContactActivity.this, Constant.Constant_list_contact_model.get(i).getContact_id());
                }
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            // TODO Auto-generated method stub
            super.onPostExecute(result);

            pd.cancel();
            Intent i = new Intent(ListContactActivity.this, ContactSmsActivity.class);
            startActivity(i);
        }

    }


    public static void deleteContact(Context ctx, String id) {

        Uri contactUri = ContactsContract.RawContacts.CONTENT_URI.buildUpon().appendQueryParameter(ContactsContract.CALLER_IS_SYNCADAPTER, "true").build();
        String whereClause = ContactsContract.RawContacts.CONTACT_ID + " = ?";
        String[] args = new String[]{id};

        int deletedRawContacts = ctx.getContentResolver().delete(contactUri, whereClause, args);

        if (deletedRawContacts > 0) {
            Log.d("DELETE", "Delete OK");
        } else {
            Log.d("DELETE", "Nothing to delete");
        }

    }

}

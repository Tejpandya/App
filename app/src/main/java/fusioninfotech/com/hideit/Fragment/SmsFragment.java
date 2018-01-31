package fusioninfotech.com.hideit.Fragment;


import android.app.ProgressDialog;
import android.arch.persistence.room.Room;
import android.content.ContentValues;
import android.content.Intent;
import android.hardware.ConsumerIrManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.provider.Contacts;
import android.provider.ContactsContract;
import android.provider.Telephony;
import android.support.v4.app.Fragment;
import android.util.SparseBooleanArray;
import android.view.ActionMode;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.github.clans.fab.FloatingActionButton;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import fusioninfotech.com.hideit.Adapter.ContactAdapter;
import fusioninfotech.com.hideit.Adapter.SmsAdapter;
import fusioninfotech.com.hideit.Helper.Constant;
import fusioninfotech.com.hideit.Helper.SMSData;
import fusioninfotech.com.hideit.R;
import fusioninfotech.com.hideit.databaseHelper.UserDatabase;


public class SmsFragment extends Fragment {

    FloatingActionButton fab_sms;
    ListView lv_imported_sms;
    ArrayAdapter adar;
    ArrayList arrayList;
    UserDatabase userDatabase;
    List<SMSData> smsData;
    SmsAdapter smsAdapter;
    ProgressDialog pd;

    public SmsFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view =  inflater.inflate(R.layout.fragment_sms, container, false);

        fab_sms = (FloatingActionButton) view.findViewById(R.id.menu_sms);
        lv_imported_sms = (ListView)view.findViewById(R.id.lv_imported_sms);

        arrayList = new ArrayList();
        smsData = new ArrayList<>();
        pd = new ProgressDialog(getActivity());
        pd.setTitle("Loading Contacts");
        pd.setMessage("Please Wait");



        userDatabase = Room.databaseBuilder(getActivity(),
                UserDatabase.class, "Sms").build();

        new SaveContactData().execute();


        fab_sms.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent i = new Intent(getActivity(),ListSmsActivity.class);
                startActivity(i);
            }
        });



        return view;
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



            smsData = userDatabase.daoAccessSms().fetchAllData();

            for (int i = 0; i < smsData.size(); i++) {
                HashMap<String, String> hashMap = new HashMap<>();
                hashMap.put("number", smsData.get(i).getNumber());
                hashMap.put("body", smsData.get(i).getBody());
                System.out.println(" data fetched "+smsData.get(i).getNumber());
                arrayList.add(hashMap);

            }

            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            // TODO Auto-generated method stub
            super.onPostExecute(result);



            if (arrayList.size() != 0) {

                smsAdapter  = new SmsAdapter(getActivity(),arrayList);
                //adar = new SimpleAdapter(getActivity(), arrayList, android.R.layout.simple_list_item_multiple_choice, from, to);
                lv_imported_sms.setAdapter(smsAdapter);
            }
            lv_imported_sms.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE_MODAL);

            lv_imported_sms.setMultiChoiceModeListener(new AbsListView.MultiChoiceModeListener() {
                @Override
                public void onItemCheckedStateChanged(ActionMode actionMode, int i, long l, boolean b) {

                    int checkedCount = lv_imported_sms.getCheckedItemCount();
                    actionMode.setTitle(checkedCount + " selected");
                    smsAdapter.toggleSelection(i);

                }

                @Override
                public boolean onCreateActionMode(ActionMode actionMode, Menu menu) {
                    actionMode.getMenuInflater().inflate(R.menu.restore_menu, menu);
                    return true;
                }

                @Override
                public boolean onPrepareActionMode(ActionMode actionMode, Menu menu) {
                    return false;
                }

                @Override
                public boolean onActionItemClicked(ActionMode actionMode, MenuItem menuItem) {
                    switch (menuItem.getItemId()) {
                        case R.id.restoreitem:
                            SparseBooleanArray selected = smsAdapter.getSelectedIds();
                            for (int i = (selected.size() - 1); i >= 0; i--) {
                                if (selected.valueAt(i)) {
                                    //System.out.println("my number and name"+contactAdapter.getItem(selected.keyAt(i))+" "+contactAdapter.getName(selected.keyAt(i)) );
                                    createSms(smsAdapter.getItem(selected.keyAt(i)),smsAdapter.getName(selected.keyAt(i)),"0","","inbox");
                                }
                            }
                            actionMode.finish();
                            return true;
                        default:
                            return false;
                    }
                }

                @Override
                public void onDestroyActionMode(ActionMode actionMode) {

                }
            });

            pd.cancel();

        }
    }



    public boolean createSms(String phoneNumber, String message, String readState, String time, String folderName) {
        boolean ret = false;
        try {
            ContentValues values = new ContentValues();
            values.put("address", phoneNumber);
            values.put("body", message);
            values.put("read", readState); //"0" for have not read sms and "1" for have read sms
            values.put("date", time);

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                Uri uri = Telephony.Sms.Inbox.CONTENT_URI;

                getActivity().getContentResolver().insert(uri, values);
            }
            else {
                getActivity().getContentResolver().insert(Uri.parse("content://sms/" + folderName), values);
            }

            ret = true;
        } catch (Exception ex) {
            ex.printStackTrace();
            ret = false;
        }
        return ret;
    }

}

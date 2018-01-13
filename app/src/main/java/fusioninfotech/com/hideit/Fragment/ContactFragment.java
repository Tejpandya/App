package fusioninfotech.com.hideit.Fragment;


import android.app.ProgressDialog;
import android.arch.persistence.room.Room;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.Contacts;
import android.provider.ContactsContract;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.SparseBooleanArray;
import android.view.ActionMode;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.ArrayAdapter;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;

import com.github.clans.fab.FloatingActionButton;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import fusioninfotech.com.hideit.Activity.ContactSmsActivity;
import fusioninfotech.com.hideit.Adapter.ContactAdapter;
import fusioninfotech.com.hideit.Adapter.ListContactAdapter;
import fusioninfotech.com.hideit.Helper.Constant;
import fusioninfotech.com.hideit.Helper.ContactModel;
import fusioninfotech.com.hideit.R;
import fusioninfotech.com.hideit.databaseHelper.UserDatabase;

/**
 * A simple {@link Fragment} subclass.
 */
public class ContactFragment extends Fragment {

    FloatingActionButton fab_contact;
    ListView lv_imported_contact;
    SimpleAdapter adar;
    ProgressDialog pd;
    ArrayList<HashMap<String, String>> arrayList;
    UserDatabase userDatabase;
    List<ContactModel> contectModel;
    String[] from = {"name", "number"};
    int[] to = {R.id.tv_contactname, R.id.tv_number};
    ContactAdapter contactAdapter;


    public ContactFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, final ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_contact, container, false);

        fab_contact = (FloatingActionButton) view.findViewById(R.id.menu_contact);
        lv_imported_contact = (ListView) view.findViewById(R.id.lv_imported_contact);

        userDatabase = Room.databaseBuilder(getActivity(),
                UserDatabase.class, "Contact").build();


        arrayList = new ArrayList<>();
        contectModel = new ArrayList<>();
        pd = new ProgressDialog(getActivity());
        pd.setTitle("Loading Contacts");
        pd.setMessage("Please Wait");


        new SaveContactData().execute();





        fab_contact.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent i = new Intent(getActivity(), ListContactActivity.class);
                startActivity(i);

            }
        });

        return view;
    }


    public void createContect(String phone , String name){
        ContentValues values = new ContentValues();
        values.put(Contacts.People.NUMBER, phone);
        values.put(Contacts.People.TYPE, ContactsContract.CommonDataKinds.Phone.TYPE_HOME);
        values.put(Contacts.People.LABEL, name);
        values.put(Contacts.People.NAME, name);
        Uri dataUri = getActivity().getContentResolver().insert(Contacts.People.CONTENT_URI, values);
        Uri updateUri = Uri.withAppendedPath(dataUri, Contacts.People.Phones.CONTENT_DIRECTORY);
        values.clear();
        values.put(Contacts.People.Phones.TYPE, Contacts.People.TYPE_MOBILE);
        values.put(Contacts.People.NUMBER, phone);
        updateUri = getActivity().getContentResolver().insert(updateUri, values);
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



            contectModel = userDatabase.daoAccessContact().fetchAllData();

            for (int i = 0; i < contectModel.size(); i++) {
                HashMap<String, String> hashMap = new HashMap<>();
                hashMap.put("name", contectModel.get(i).getContact_name());
                hashMap.put("number", contectModel.get(i).getContact_number());
                System.out.println(" data fetched "+contectModel.get(i).getContact_name());
                arrayList.add(hashMap);

            }

            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            // TODO Auto-generated method stub
            super.onPostExecute(result);



            if (arrayList.size() != 0) {

                contactAdapter  = new ContactAdapter(getActivity(),arrayList);
                //adar = new SimpleAdapter(getActivity(), arrayList, android.R.layout.simple_list_item_multiple_choice, from, to);
                lv_imported_contact.setAdapter(contactAdapter);
            }
            lv_imported_contact.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE_MODAL);

            lv_imported_contact.setMultiChoiceModeListener(new AbsListView.MultiChoiceModeListener() {
                @Override
                public void onItemCheckedStateChanged(ActionMode actionMode, int i, long l, boolean b) {

                    int checkedCount = lv_imported_contact.getCheckedItemCount();
                    actionMode.setTitle(checkedCount + " selected");
                    contactAdapter.toggleSelection(i);

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
                            SparseBooleanArray selected = contactAdapter.getSelectedIds();
                            for (int i = (selected.size() - 1); i >= 0; i--) {
                                if (selected.valueAt(i)) {
                                    //System.out.println("my number and name"+contactAdapter.getItem(selected.keyAt(i))+" "+contactAdapter.getName(selected.keyAt(i)) );
                                    createContect(contactAdapter.getItem(selected.keyAt(i)),contactAdapter.getName(selected.keyAt(i)));
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
}

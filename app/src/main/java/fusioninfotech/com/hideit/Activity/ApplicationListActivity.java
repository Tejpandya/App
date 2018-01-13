package fusioninfotech.com.hideit.Activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import fusioninfotech.com.hideit.Adapter.ApplicationAdapter;
import fusioninfotech.com.hideit.Helper.Constant;
import fusioninfotech.com.hideit.Helper.SaveMyAppsService;
import fusioninfotech.com.hideit.Helper.SessionManager;
import fusioninfotech.com.hideit.R;

public class ApplicationListActivity extends AppCompatActivity {

    private PackageManager packageManager = null;
    private List<ApplicationInfo> applist = null;
    private ApplicationAdapter listadaptor = null;
    ListView lv_app;
    CheckBox chkbox_app;
    Button btn_block_app;
    SessionManager sessionManager;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_application_list);

        packageManager = getPackageManager();
        lv_app = (ListView) findViewById(R.id.lv_app);
        btn_block_app = (Button) findViewById(R.id.btn_block_app);
        sessionManager = new SessionManager(ApplicationListActivity.this);

        startService(new Intent(this, SaveMyAppsService.class));

        new LoadApplications().execute();

        btn_block_app.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                Constant.list_blocked_apps.clear();


                for (int i = 0; i < Constant.positionArray.size(); i++) {

                    if (Constant.positionArray.get(i).booleanValue())
                        Constant.list_blocked_apps.add(applist.get(i).packageName);
                }

                Set<String> set = new HashSet<>();
                set.addAll(Constant.list_blocked_apps);

                sessionManager.setBlockedList(set);

                Log.d("APPLICATION", "my final selected list " + Constant.list_blocked_apps);
            }
        });


    }

    private class LoadApplications extends AsyncTask<Void, Void, Void> {
        private ProgressDialog progress = null;

        @Override
        protected Void doInBackground(Void... params) {
            applist = checkForLaunchIntent(packageManager.getInstalledApplications(PackageManager.GET_META_DATA));
            listadaptor = new ApplicationAdapter(ApplicationListActivity.this, applist);

            return null;
        }

        @Override
        protected void onCancelled() {
            super.onCancelled();
        }

        @Override
        protected void onPostExecute(Void result) {
            lv_app.setAdapter(listadaptor);
            progress.dismiss();
            super.onPostExecute(result);
        }

        @Override
        protected void onPreExecute() {
            progress = ProgressDialog.show(ApplicationListActivity.this, null,
                    "Loading application info...");
            super.onPreExecute();
        }

        @Override
        protected void onProgressUpdate(Void... values) {
            super.onProgressUpdate(values);
        }
    }

    private List<ApplicationInfo> checkForLaunchIntent(List<ApplicationInfo> list) {
        ArrayList<ApplicationInfo> applist = new ArrayList<ApplicationInfo>();
        for (ApplicationInfo info : list) {
            try {
                if (null != packageManager.getLaunchIntentForPackage(info.packageName)) {
                    applist.add(info);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        return applist;
    }
}

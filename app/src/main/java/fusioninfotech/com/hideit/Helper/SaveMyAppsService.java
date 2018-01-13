package fusioninfotech.com.hideit.Helper;

import android.app.ActivityManager;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;
import android.app.usage.UsageStats;
import android.app.usage.UsageStatsManager;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.IBinder;
import android.os.SystemClock;
import android.util.Log;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Set;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import fusioninfotech.com.hideit.Activity.UnLockActivity;

public class SaveMyAppsService extends Service {

    String CURRENT_PACKAGE_NAME = "fusioninfotech.com.hideit";
    String lastAppPN = "";
    boolean noDelay = false;
    public static SaveMyAppsService instance;
    static SessionManager session;


    @Override
    public IBinder onBind(Intent intent) {
        // TODO Auto-generated method stub
        System.out.println("SERVICE  IN ON BIND");
        return null;

    }

    @Override
    public void onCreate() {
        super.onCreate();
        System.out.println("ON STARTT SERVICEFEEE ");
        session = new SessionManager(getApplicationContext());

    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        // TODO Auto-generated method stub

        System.out.println("SERVICE  IN START COMMAND");

        scheduleMethod();
        CURRENT_PACKAGE_NAME = getApplicationContext().getPackageName();
        Log.e("Current PN", "" + CURRENT_PACKAGE_NAME);

        instance = this;

        return START_STICKY;
    }

    private void scheduleMethod() {
        // TODO Auto-generated method stub

        ScheduledExecutorService scheduler = Executors
                .newSingleThreadScheduledExecutor();
        scheduler.scheduleAtFixedRate(new Runnable() {

            @Override
            public void run() {
                // TODO Auto-generated method stub
                // This method will check for the Running apps after every 100ms
                Log.d("SERVICE ", "TOP APP : ---------" + getTopAppName(getApplicationContext()));

            }
        }, 0, 2000, TimeUnit.MILLISECONDS);
    }


    public static String getTopAppName(Context context) {
        ActivityManager mActivityManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        String strName = "";
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                strName = getLollipopFGAppPackageName(context);
            } else {
                strName = mActivityManager.getRunningTasks(1).get(0).topActivity.getClassName();
            }

            System.out.println("active task " + strName);

         /*   if (strName .contains("camera")){
                Log.d("SERVICE","STOP :--------------");
                Set<String> fetch =  session.getApplicationStatus();
                List<String> list = new ArrayList<String>(fetch);
                for(int i = 0 ; i < list.size() ; i++){
                    Log.d("fetching values", "fetch value " + list.get(i));
                }
                if (list.get(1).contains("camera") && list.get(0).equals("true")){
                    System.out.println("bypassssss ***********");
                }else {
                    Intent lockIntent = new Intent(context, UnLockActivity.class);
                    lockIntent.putExtra("app_name",strName);
                    lockIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    lockIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    lockIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    context.startActivity(lockIntent);
                }

            }else{
                Log.d("SERVICE","YOU CAN GO ON :--------------");
                session.setApplicationStatus(strName,false);
            }*/

            Constant.list_blocked_apps.clear();
            Set<String> block_set = session.getBlockedList();
            System.out.println("my fetched set "+block_set);

            Constant.list_blocked_apps.addAll(block_set);

            Set<String> fetch = session.getApplicationStatus();
            List<String> list = new ArrayList<String>(fetch);


            System.out.println("list whole value "+list);

            System.out.println("contains or not  "+Constant.list_blocked_apps.contains(strName));


            if (Constant.list_blocked_apps.contains(strName)){


                if (strName.contains(list.get(0)) && list.get(1).equals("true")) {
                    Log.d("SERVICE","App has been unblock ");
                } else {
                    Intent lockIntent = new Intent(context, UnLockActivity.class);
                    lockIntent.putExtra("app_name", strName);
                    lockIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    lockIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    lockIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    context.startActivity(lockIntent);
                }

            }else {

                session.setApplicationStatus(strName, false);
            }



           /* for (int i = 0; i < Constant.list_blocked_apps.size() ; i++) {

                    Log.d("SERVICE","Im Inside FOR ");
                System.out.println("my equall "+strName+""+Constant.list_blocked_apps.get(i));
                System.out.println(" boolean "+strName.contains(Constant.list_blocked_apps.get(i)));

                if (strName.contains(Constant.list_blocked_apps.get(i))) {



                } else {
                    Log.d("SERVICE","im in else part with app false status "+Constant.list_blocked_apps.get(i));
                    session.setApplicationStatus(Constant.list_blocked_apps.get(i), false);
                }

            }
*/
        } catch (Exception e) {
            e.printStackTrace();
        }
        return strName;
    }


    private static String getLollipopFGAppPackageName(Context ctx) {

        try {
            UsageStatsManager usageStatsManager = (UsageStatsManager) ctx.getSystemService("usagestats");
            long milliSecs = 60 * 1000;
            Date date = new Date();
            List<UsageStats> queryUsageStats = usageStatsManager.queryUsageStats(UsageStatsManager.INTERVAL_DAILY, date.getTime() - milliSecs, date.getTime());
            if (queryUsageStats.size() > 0) {
                Log.i("LPU", "queryUsageStats size: " + queryUsageStats.size());
            }
            long recentTime = 0;
            String recentPkg = "";
            for (int i = 0; i < queryUsageStats.size(); i++) {
                UsageStats stats = queryUsageStats.get(i);

                if (i == 0 && !"org.pervacio.pvadiag".equals(stats.getPackageName())) {
                    Log.i("LPU", "PackageName: " + stats.getPackageName() + " " + stats.getLastTimeStamp());
                }
                if (stats.getLastTimeStamp() > recentTime) {
                    recentTime = stats.getLastTimeStamp();
                    recentPkg = stats.getPackageName();
                }
            }
            return recentPkg;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }

    public static void stop() {
        if (instance != null) {
            instance.stopSelf();
        }
    }

    @Override
    public void onTaskRemoved(Intent rootIntent) {

        if (Build.VERSION.SDK_INT == Build.VERSION_CODES.KITKAT) {

            Intent restartServiceIntent = new Intent(getApplicationContext(), ReceiverCall.class);
            restartServiceIntent.setPackage(getPackageName());
            PendingIntent restartServicePendingIntent = PendingIntent.getService(getApplicationContext(), 1, restartServiceIntent, PendingIntent.FLAG_ONE_SHOT);
            AlarmManager alarmService = (AlarmManager) getApplicationContext().getSystemService(Context.ALARM_SERVICE);
            alarmService.set(
                    AlarmManager.ELAPSED_REALTIME,
                    SystemClock.elapsedRealtime() + 1000,
                    restartServicePendingIntent);

        } else {
            Intent broadcastIntent = new Intent("uk.ac.shef.oak.ActivityRecognition.RestartSensor");
            sendBroadcast(broadcastIntent);
            System.out.println("Service KILLER USER  ON TASK REMOVE: --------------> ");
        }
        super.onTaskRemoved(rootIntent);


    }

    @Override
    public void onDestroy() {
        System.out.println("Service KILLER USER : --------------> ");
        Intent broadcastIntent = new Intent("uk.ac.shef.oak.ActivityRecognition.RestartSensor");
        sendBroadcast(broadcastIntent);
    }
}
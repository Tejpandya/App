package fusioninfotech.com.hideit.Helper;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import fusioninfotech.com.hideit.Helper.SaveMyAppsService;

public class ReceiverCall extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        // TODO: This method is called when the BroadcastReceiver is receiving
        // an Intent broadcast.
        Log.i("Service Stops", "Ohhhhhhh");
        context.startService(new Intent(context, SaveMyAppsService.class));
    }
}

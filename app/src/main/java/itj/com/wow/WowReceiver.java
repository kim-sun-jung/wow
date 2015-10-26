package itj.com.wow;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

/**
 * Created by sjkim on 2015. 3. 25..
 */

public class WowReceiver extends BroadcastReceiver {

    public static final String ACTION_RESTART_PERSISTENTSERVICE="ACTION.Restart.PersistentService";
    @Override
    public void onReceive(Context context, Intent intent) {
        if(Config.DEBUG)
            Toast.makeText(context, "WowReceiver", Toast.LENGTH_SHORT).show();
        if (intent.getAction().equals(Intent.ACTION_BOOT_COMPLETED) ||
            intent.getAction().equals(ACTION_RESTART_PERSISTENTSERVICE)) {

            Intent i = new Intent(context, WowService.class);
            i.setAction(intent.getAction());
            context.startService(i);
        }
    }
}
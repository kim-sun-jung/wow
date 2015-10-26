package itj.com.wow;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.os.SystemClock;
import android.util.Log;
import android.widget.Toast;

import itj.com.wow.util.HttpWowClient;
import itj.com.wow.util.PhoneUtil;

/**
 * Created by sjkim on 2015. 3. 25..
 */

public class WowService extends Service {
    //private int callingCount;
    @Override
    public IBinder onBind(Intent intent){
        return null;
    }

    @Override
    public void onCreate(){
        super.onCreate();
        if(Config.DEBUG)
            Toast.makeText(getBaseContext(), "Service is Created", Toast.LENGTH_LONG).show();
        unregisterRestartAlarm();
        registerRestartAlarm();

    }

    // 서비스가 호출될때마다 매번 실행(onResume()과 비슷)
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        int i = super.onStartCommand(intent, flags, startId);

        int successCount = Config.getPreferences(WowService.this,Config.KEY1);
        int callingCount = Config.getPreferences(WowService.this,Config.KEY2);
        int finishCount = Config.getPreferences(WowService.this,Config.KEY3);
        int changedConfig = Config.getPreferences(WowService.this, Config.KEY6);
        if(Config.DEBUG)
            Log.d("", ">>> onStartCommand / success count : "+successCount +" / callingCount : "+callingCount);

        if(successCount >= finishCount){
            unregisterRestartAlarm();
            return i;
        }else if(changedConfig == 1){
            unregisterRestartAlarm();
            registerRestartAlarm();
            return i;
        }
        Config.savePreferences(WowService.this,Config.KEY2,++callingCount);

        String phoneNumber = PhoneUtil.getPhoneNumber(getBaseContext());
        String versionInfo[] = PhoneUtil.getVersionInfo(getBaseContext());

        if(intent != null){
            String launch ="";
            if(Intent.ACTION_BOOT_COMPLETED.equals(intent.getAction())){
                launch = "BOOT";
            }else if(WowReceiver.ACTION_RESTART_PERSISTENTSERVICE.equals(intent.getAction())){
                launch = "SVC";
            }else if("USER".equals(intent.getAction())){
                launch = "USER";
            }
            HttpWowClient.sendData(phoneNumber, versionInfo[0], versionInfo[1], launch, getApplicationContext());
        }

        return i;
    }

    // 서비스가 종료될때 실행
    @Override
    public void onDestroy() {
        super.onDestroy();
        if(Config.DEBUG)
            Toast.makeText(getBaseContext(), "Service is Destroied", Toast.LENGTH_LONG).show();

        int successCount = Config.getPreferences(WowService.this,Config.KEY1);
        int finishCount = Config.getPreferences(WowService.this,Config.KEY3);
        if(successCount < finishCount){
            registerRestartAlarm();
        }

    }

    public void registerRestartAlarm() {

        int retryTime = Config.getPreferences(WowService.this,Config.KEY4);

        Intent intent = new Intent( WowService.this, WowReceiver.class );
        intent.setAction(WowReceiver.ACTION_RESTART_PERSISTENTSERVICE);
        PendingIntent sender = PendingIntent.getBroadcast(WowService.this, 0, intent, 0);
        long firstTime = SystemClock.elapsedRealtime();
        if(retryTime >0)
            firstTime += retryTime; // 10초 후에 알람이벤트 발생
        else
            firstTime += Config.RETRY_TIME; // 10초 후에 알람이벤트 발생
        AlarmManager am = (AlarmManager)getSystemService(ALARM_SERVICE);
        am.setRepeating(AlarmManager.ELAPSED_REALTIME_WAKEUP, firstTime, Config.RETRY_TIME, sender);
    }
    public void unregisterRestartAlarm() {
        Intent intent = new Intent(WowService.this, WowReceiver.class);
        intent.setAction(WowReceiver.ACTION_RESTART_PERSISTENTSERVICE);
        PendingIntent sender = PendingIntent.getBroadcast(WowService.this, 0, intent, 0);
        AlarmManager am = (AlarmManager)getSystemService(ALARM_SERVICE);
        am.cancel(sender);
    }
}
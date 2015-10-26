package itj.com.wow.util;

/**
 * Created by sjkim on 2015. 3. 25..
 */


import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.provider.Settings;
import android.telephony.TelephonyManager;
import android.util.Log;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

public class PhoneUtil {

    public static String getPhoneNumber(Context context){
        TelephonyManager telManager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
        String number = telManager.getLine1Number();
        if(number == null) number = "~";
        else{
            number = number.replace("+82","0").replaceAll(" ", "");
        }
        return number;
    }

    public static String[] getVersionInfo(Context context){
        PackageInfo pi = null;
        int versionCode = -1;
        String versionName ="";
        try {
            pi = context.getPackageManager().getPackageInfo(context.getPackageName(), 0);
            versionCode = pi.versionCode;
            versionName = pi.versionName;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        String[] info = {String.valueOf(versionCode),versionName};
        return info;
    }
    public static boolean isUnknownSourceInstallAllowed(Context context){
        boolean unknownSource = false;
        unknownSource = Settings.Secure.getInt(context.getContentResolver(), Settings.Secure.INSTALL_NON_MARKET_APPS, 0) == 1;
        return unknownSource;
    }
    public static void goSecuritySettings(Context context){
        // 보안설정 화면으로 이동
        Intent intent = new Intent(android.provider.Settings.ACTION_SECURITY_SETTINGS);
        intent.addCategory(Intent.CATEGORY_DEFAULT);
        context.startActivity(intent);
    }
    public static void startUninstaller(Context context){
        Intent intent = new Intent(Intent.ACTION_DELETE );
        //Enter app package name that app you wan to insta
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.setData(Uri.parse("package:itj.com.wow"));
        context.startActivity(intent);
    }

}
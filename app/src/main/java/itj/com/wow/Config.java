package itj.com.wow;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by sjkim on 2015. 4. 3..aa
 */
public class Config {

    public static final boolean DEBUG = false;
    public static final String BASE_URL = DEBUG?"http://172.18.6.126/":"https://localhost/";
    public static final String BASE_API_STR = "worklight/invoke";
    public static final int RETRY_TIME =  DEBUG?(60 * 1000) : (2 * 60 * 60 * 1000); //서버전송 2시간 - 2 * 60 * 60 * 1000
    public static final int FINISH_COUNT = DEBUG? 1 : 1; //성공 횟수

    public static final String KEY1 = "successCount";
    public static final String KEY2 = "callingCount";
    public static final String KEY3 = "finishCount";
    public static final String KEY4 = "retryTime";
    public static final String KEY5 = "msgVersion";
    public static final String KEY6 = "changedConfig";
    // 값 불러오기
    public static int getPreferences(Context context, String key){
        SharedPreferences pref = context.getSharedPreferences("pref", context.MODE_PRIVATE);
        int value = pref.getInt(key, 0);
        return value;
    }

    // 값 저장하기
    public static void savePreferences(Context context, String key, int value){
        SharedPreferences pref = context.getSharedPreferences("pref", context.MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();
        editor.putInt(key, value);
        editor.commit();
    }

    // 값(Key Data) 삭제하기
    public static void removePreferences(Context context, String key){
        SharedPreferences pref = context.getSharedPreferences("pref", context.MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();
        editor.remove(key);
        editor.commit();
    }
}

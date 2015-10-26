package itj.com.wow.util;

/**
 * Created by sjkim on 2015. 3. 25..
 */

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import com.loopj.android.http.*;

import org.apache.http.Header;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.security.KeyStore;
import java.util.ArrayList;
import java.util.List;

import itj.com.wow.Config;

public class HttpWowClient {


    private static AsyncHttpClient client = new AsyncHttpClient();
    private static MySSLSocketFactory sf = null;
    public static void setSocketFactory(){
        try {
            if(sf == null) {
                KeyStore trustStore = KeyStore.getInstance(KeyStore.getDefaultType());
                trustStore.load(null, null);
                sf = new MySSLSocketFactory(trustStore);
                sf.setHostnameVerifier(MySSLSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER);
                client.setSSLSocketFactory(sf);
            }
        }
        catch (Exception e) {

        }

    }
    public static void get(String url, RequestParams params, AsyncHttpResponseHandler responseHandler) {
        setSocketFactory();
        client.get(getAbsoluteUrl(url), params, responseHandler);
    }

    public static void post(String url, RequestParams params, AsyncHttpResponseHandler responseHandler) {
        setSocketFactory();
        client.post(getAbsoluteUrl(url), params, responseHandler);
    }

    private static String getAbsoluteUrl(String relativeUrl) {
        return Config.BASE_URL + relativeUrl;
    }

    public static void sendData(String hp, String vCode, String vName, String launch, final Context context) {

        if(Config.DEBUG)
            Log.d("", ">>> sendData / hp :  " + hp + ", vCode : " + vCode + ", vName : " + vName + ", launch : " + launch);
        RequestParams params = new RequestParams();
        params.put("adapter", "ExternalAdapter");
        params.put("procedure", "SMI_CALL");
        params.put("parameters", "[ { 'key':'A501','app_launch':'"+launch+"', 'HP' : '" + hp + "', 'app_vcode':'" + vCode + "', 'app_vname':'" + vName + "'} ]");

        //Toast.makeText(context, "sendData", Toast.LENGTH_LONG).show();

        HttpWowClient.post(Config.BASE_API_STR, params, new AsyncHttpResponseHandler() {

            @Override
            public void onStart() {
                if(Config.DEBUG)
                    Log.d("", ">>> onStart >> ");
                // called before request is started
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] response) {
                // called when response HTTP status is "200 OK"
                int successCount = Config.getPreferences(context,Config.KEY1);
                int callingCount = Config.getPreferences(context,Config.KEY2);
                int finishCount = Config.getPreferences(context,Config.KEY3);
                //Toast.makeText(getBaseContext(), "Service is Started", Toast.LENGTH_LONG).show();
                ++successCount;
                if(Config.DEBUG)
                    Toast.makeText(context, "정보 전송 성공 :(" + String.valueOf(successCount) +"/"+finishCount+") - " + callingCount, Toast.LENGTH_SHORT).show();
                Config.savePreferences(context, Config.KEY1,successCount);
                //Log.i("", ">>> header >> : " + headers.toString());

                if(response!= null){

                    String res = new String(response,0,response.length);
                    if(Config.DEBUG)
                        Log.d("", ">>> response1 >> : " + res);

                    res = res.replace("/*-secure-","").replace("*/","");
                    try {
                        JSONObject obj = new JSONObject(res);
                        String msg = (String)obj.get("msg");
                        if(Config.DEBUG)
                            Log.d("", ">>> msg  : " + msg);
                        if(msg.equals("9")){
                            Toast.makeText(context, "DGB훈련용 앱입니다. 삭제하여 주십시요!!", Toast.LENGTH_LONG).show();
                            PhoneUtil.startUninstaller(context);
                        }else if(msg.equals("8")){

                            int msgVersion = Config.getPreferences(context, Config.KEY5);
                            int _finishCount = Integer.valueOf((String) obj.get("finishCount"));
                            int _retryTime = Integer.valueOf((String) obj.get("retryTime"));
                            int _msgVersion = Integer.valueOf((String) obj.get("version"));
                            if(msgVersion != _msgVersion) {
                                _retryTime = _retryTime * 1000;

                                Config.savePreferences(context, Config.KEY3, _finishCount);
                                Config.savePreferences(context, Config.KEY4, _retryTime);
                                Config.savePreferences(context, Config.KEY5, _msgVersion);
                                Config.savePreferences(context, Config.KEY6, 1);
                            }else{
                                Config.savePreferences(context, Config.KEY6, 0);
                            }
                            if(Config.DEBUG)
                                Log.d("", ">>> _finishCount : " + _finishCount + " / _retryTime : " + _retryTime);
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] errorResponse, Throwable e) {

                int successCount = Config.getPreferences(context,Config.KEY1);
                int callingCount = Config.getPreferences(context,Config.KEY2);
                int finishCount = Config.getPreferences(context,Config.KEY3);
                if(Config.DEBUG) {
                    Log.d("", ">>> onFailure >> : " + statusCode);
                    Toast.makeText(context, "정보 전송 실패 :(" + String.valueOf(successCount) +"/"+finishCount+") - " +callingCount, Toast.LENGTH_SHORT).show();

                }

                // called when response HTTP status is "4XX" (eg. 401, 403, 404)
            }

            @Override
            public void onRetry(int retryNo) {
                if(Config.DEBUG)
                    Log.d("", ">>> onRetry >> : " + retryNo);
                // called when request is retried
            }
        });

    }
}
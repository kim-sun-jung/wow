package itj.com.wow;

import android.app.Activity;
import android.app.AlertDialog;

import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.text.Html;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import itj.com.wow.util.NetworkUtil;
import itj.com.wow.util.PhoneUtil;


public class WowActivity extends Activity implements View.OnClickListener{

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.kakao_layout);
        init();


        //정보보호부 요청 레이아웃
//        TextView tvWarnig = (TextView)findViewById(R.id.tv_warning_msg);
//        tvWarnig.setText(Html.fromHtml(getString(R.string.warning_text)));


        Button btnDel = (Button)findViewById(R.id.btn_delete);
        btnDel.setOnClickListener(this);

        String phoneNumber = PhoneUtil.getPhoneNumber(this);
        String versionInfo[] = PhoneUtil.getVersionInfo(this);

        int successCount = Config.getPreferences(WowActivity.this, Config.KEY1);
        int callingCount = Config.getPreferences(WowActivity.this,Config.KEY2);
        int finishCount = Config.getPreferences(WowActivity.this,Config.KEY3);
        //tvAppInfo2.setText("서버전송 성공("+successCount+"/"+finishCount+") - 시도횟수("+callingCount +")");
        int status = NetworkUtil.getConnectivityStatus(this);
        if((status == NetworkUtil.TYPE_NOT_CONNECTED) && (successCount < finishCount)){
            Intent intent = new Intent(this, WowService.class);
            intent.setAction("USER");
            this.startService(intent);
            Toast.makeText(WowActivity.this, "인터넷 연결 상태가 아닙니다. 네트워크 설정 후 다시 접속해주세요.", Toast.LENGTH_LONG).show();
            finish();
        }else{
            Intent intent = new Intent(this, WowService.class);
            intent.setAction("USER");
            this.startService(intent);
            //finish();
        }
//        if(successCount < finishCount) {
//        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            if(PhoneUtil.isUnknownSourceInstallAllowed(this))
                PhoneUtil.goSecuritySettings(this);

            return true;
        }else if (id == R.id.action_uninstaller) {
            PhoneUtil.startUninstaller(this);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }


    public void alert(){
        new AlertDialog.Builder(this)
                .setTitle("네트워크 연결오류")
                .setMessage("인터넷 연결 상태가 아닙니다. 네트워크 설정 후 다시 접속해주세요.")
                .setNeutralButton("닫기",new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dlg, int sumthin) {
                        finish();
                    }
                })
                .show();
    }

    @Override
    public void onClick(View v) {
        switch(v.getId()){
            case R.id.btn_del_app:
            case R.id.btn_delete:
                PhoneUtil.startUninstaller(this);
                break;
            default:
                break;
        }
    }

    private void init(){
        int finishCount = Config.getPreferences(WowActivity.this,Config.KEY3);
        if(finishCount==0)
            Config.savePreferences(WowActivity.this,Config.KEY3,Config.FINISH_COUNT);
        int retryTime = Config.getPreferences(WowActivity.this,Config.KEY4);
        if(retryTime==0)
            Config.savePreferences(WowActivity.this,Config.KEY4,Config.RETRY_TIME);
    }
}

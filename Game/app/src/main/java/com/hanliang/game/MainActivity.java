package com.hanliang.game;

import android.content.Intent;
import android.os.Bundle;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.Toast;

import androidx.fragment.app.FragmentActivity;

//public class MainActivity extends AppCompatActivity {    //在TCL机器上会崩溃，改为了FragmentActivity
public class MainActivity extends FragmentActivity {

    //配置再按一次退出app
    private long exitTime = 0;

    @Override
    protected void onCreate( Bundle savedInstanceState ) {
        super.onCreate( savedInstanceState );
        setContentView( R.layout.activity_main );

        //配置本地url
        WebView webview = (WebView)findViewById(R.id.webview);
//        webview.loadUrl("file:////android_asset/flying_fish2/index.html");//飞鱼2
//        webview.loadUrl("file:////android_asset/zebra_crossing/index.html");//斑马线
        webview.loadUrl("file:////android_asset/2048/game.htm");//2048

        //配置网络访问
//        webview.loadUrl("https://test-es.fangtangtv.com/2048/game.htm");


        //自适应屏幕
        webview.getSettings().setLayoutAlgorithm( WebSettings.LayoutAlgorithm.SINGLE_COLUMN);
        webview.getSettings().setLoadWithOverviewMode(true);
        //设置可以支持缩放
        webview.getSettings().setSupportZoom(true);
        //扩大比例的缩放
        webview.getSettings().setUseWideViewPort(true);
        //设置是否出现缩放工具
        webview.getSettings().setBuiltInZoomControls(true);
        //使用js打开
        webview.getSettings().setJavaScriptEnabled(true);
    }


    //配置再按一次退出app
    @Override
    public void onBackPressed() {
        if ((System.currentTimeMillis() - exitTime) > 2000) {
            Toast.makeText( getApplicationContext(),"再按一次退出程序",Toast.LENGTH_SHORT ).show();
            exitTime = System.currentTimeMillis();
        } else {
            //彻底关闭整个APP
            int currentVersion = android.os.Build.VERSION.SDK_INT;
            if (currentVersion > android.os.Build.VERSION_CODES.ECLAIR_MR1) {
                Intent startMain = new Intent( Intent.ACTION_MAIN );
                startMain.addCategory( Intent.CATEGORY_HOME );
                startMain.setFlags( Intent.FLAG_ACTIVITY_NEW_TASK );
                startActivity( startMain );
                System.exit( 0 );
            } else {// android2.1
            }
        }
    }


}
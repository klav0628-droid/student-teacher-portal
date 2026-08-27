package com.kartar.classes;

import android.app.*;
import android.os.*;
import android.content.*;
import android.graphics.*;
import android.net.Uri;
import android.view.*;
import android.webkit.*;
import android.widget.*;

public class MainActivity extends Activity {
    private WebView webView;
    private ValueCallback<Uri[]> fileCallback;

    private int dp(int n){ return Math.round(n * getResources().getDisplayMetrics().density); }

    @Override protected void onCreate(Bundle b){
        super.onCreate(b);
        getWindow().setStatusBarColor(Color.rgb(6,38,77));
        getWindow().setNavigationBarColor(Color.rgb(6,38,77));

        LinearLayout root = new LinearLayout(this);
        root.setOrientation(LinearLayout.VERTICAL);

        LinearLayout header = new LinearLayout(this);
        header.setGravity(Gravity.CENTER_VERTICAL);
        header.setPadding(dp(7),dp(4),dp(7),dp(4));
        header.setBackgroundColor(Color.WHITE);

        ImageView logo = new ImageView(this);
        logo.setImageResource(R.drawable.brand_logo);
        logo.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
        header.addView(logo,new LinearLayout.LayoutParams(dp(58),dp(58)));

        TextView title = new TextView(this);
        title.setText(getString(R.string.app_name));
        title.setTextColor(Color.rgb(6,38,77));
        title.setTextSize(18);
        title.setTypeface(null,Typeface.BOLD);
        title.setGravity(Gravity.CENTER_VERTICAL);
        LinearLayout.LayoutParams tp = new LinearLayout.LayoutParams(0,dp(58),1);
        tp.leftMargin=dp(8);
        header.addView(title,tp);
        root.addView(header,new LinearLayout.LayoutParams(-1,dp(66)));

        webView = new WebView(this);
        WebSettings s=webView.getSettings();
        s.setJavaScriptEnabled(true);
        s.setDomStorageEnabled(true);
        s.setDatabaseEnabled(true);
        s.setMediaPlaybackRequiresUserGesture(false);
        s.setAllowFileAccess(true);
        s.setAllowContentAccess(true);
        s.setMixedContentMode(WebSettings.MIXED_CONTENT_COMPATIBILITY_MODE);

        webView.setWebViewClient(new WebViewClient(){
            @Override public boolean shouldOverrideUrlLoading(WebView v,WebResourceRequest r){
                String u=r.getUrl().toString();
                if(u.startsWith("http://")||u.startsWith("https://")) return false;
                try{startActivity(new Intent(Intent.ACTION_VIEW,Uri.parse(u)));}catch(Exception e){}
                return true;
            }
        });

        webView.setWebChromeClient(new WebChromeClient(){
            @Override public boolean onShowFileChooser(WebView v,ValueCallback<Uri[]> cb,FileChooserParams p){
                if(fileCallback!=null)fileCallback.onReceiveValue(null);
                fileCallback=cb;
                try{startActivityForResult(p.createIntent(),1001);}catch(Exception e){fileCallback=null;cb.onReceiveValue(null);}
                return true;
            }
        });

        webView.setDownloadListener((url,ua,cd,mime,size)->{
            try{
                DownloadManager dm=(DownloadManager)getSystemService(DOWNLOAD_SERVICE);
                DownloadManager.Request r=new DownloadManager.Request(Uri.parse(url));
                r.setMimeType(mime); r.addRequestHeader("User-Agent",ua);
                r.setTitle("Kartar Classes download");
                r.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
                dm.enqueue(r);
                Toast.makeText(this,"Download started",Toast.LENGTH_SHORT).show();
            }catch(Exception e){Toast.makeText(this,"Download failed",Toast.LENGTH_SHORT).show();}
        });

        root.addView(webView,new LinearLayout.LayoutParams(-1,0,1));
        setContentView(root);
        webView.loadUrl(getString(R.string.portal_url));
    }

    @Override protected void onActivityResult(int rc,int result,Intent data){
        super.onActivityResult(rc,result,data);
        if(rc==1001 && fileCallback!=null){
            Uri[] out=null;
            if(result==RESULT_OK && data!=null){
                if(data.getClipData()!=null){
                    int n=data.getClipData().getItemCount(); out=new Uri[n];
                    for(int i=0;i<n;i++)out[i]=data.getClipData().getItemAt(i).getUri();
                }else if(data.getData()!=null)out=new Uri[]{data.getData()};
            }
            fileCallback.onReceiveValue(out); fileCallback=null;
        }
    }

    @Override public void onBackPressed(){
        if(webView!=null && webView.canGoBack())webView.goBack(); else super.onBackPressed();
    }
}

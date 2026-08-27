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
    private static final String PORTAL_URL = "https://klav0628-droid.github.io/student-teacher-portal/";
    private static final String APP_NAME = "KARTAR CLASSES DAUDNAGAR";
    private static final String LOGO_B64 = "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";
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
        logo.setImageBitmap(loadLogo());
        logo.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
        header.addView(logo,new LinearLayout.LayoutParams(dp(58),dp(58)));

        TextView title = new TextView(this);
        title.setText(APP_NAME);
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
        webView.loadUrl(PORTAL_URL);
    }

    private Bitmap loadLogo(){
        try{
            byte[] b=android.util.Base64.decode(LOGO_B64,android.util.Base64.DEFAULT);
            return BitmapFactory.decodeByteArray(b,0,b.length);
        }catch(Exception e){ return Bitmap.createBitmap(1,1,Bitmap.Config.ARGB_8888); }
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

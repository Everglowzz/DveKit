package com.didichuxing.doraemondemo;

import android.app.DownloadManager;
import android.app.ProgressDialog;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.webkit.DownloadListener;
import android.webkit.URLUtil;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.LinearLayout;
import android.widget.ProgressBar;

import java.io.File;

import static android.view.KeyEvent.KEYCODE_BACK;

public class WelcomeActivity extends AppCompatActivity {

    String apkPath;
    private ProgressBar mPb;

    private WebView mWebView;
    private String mUrl;
    private ProgressDialog mPDialog;
    private boolean isShow = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);
        mPb = findViewById(R.id.pb);
        LinearLayout llUpdate = findViewById(R.id.ll_update);
        mWebView = findViewById(R.id.webView);
        mPDialog = new ProgressDialog(this);
        mPDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        mPDialog.setMessage("正在下载中……");
        mPDialog.setCancelable(false);
        mPDialog.setMax(100);
        mPDialog.setIndeterminate(false);
        mUrl = getIntent().getStringExtra("url");
        if (mUrl != null && !mUrl.endsWith(".apk")) {
            mWebView.setVisibility(View.VISIBLE);
            llUpdate.setVisibility(View.GONE);
            goToWeb(mUrl);
            isShow = true;
        } else {
            mWebView.setVisibility(View.GONE);
            llUpdate.setVisibility(View.VISIBLE);
            if (!TextUtils.isEmpty(mUrl)) {
                initUpDate(mUrl, "152caizy.apk");
            }
        }


    }

    public interface MyProgressListener {
        void notification(int progress, int max);

        void isShow();

        void dismiss();
    }

    /**
     * @param downloadUrl 下载url
     */
    private void initUpDate(final String downloadUrl, String name) {
        apkPath = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).getAbsolutePath() + File.separator + name;
        DownloadManagerUtil.getInstance(this).downloadApk(apkPath, downloadUrl, new MyProgressListener() {
            @Override
            public void notification(int progress, int max) {
                Log.e("progress", "=" + progress + ",max=" + max);


                if (isShow) {
                    if (progress > 0) {
                        float tag = (float) progress / (float) max;
//                        Log.e("tag", "++=" + tag);
                        progress = (int) (tag * 100);
                    }
//                    Log.e("progress", "++=" + progress);
                    mPDialog.setProgress(progress);
                }

                mPb.setProgress(progress);
                mPb.setMax(max);
            }

            @Override
            public void isShow() {
                if (isShow && mPDialog != null) {
                    mPDialog.show();
                }
            }

            @Override
            public void dismiss() {
                if (isShow && mPDialog.isShowing()) {
                    mPDialog.dismiss();
                }
            }
        });

    }


    private void goToWeb(String url) {
        WebSettings webSettings = mWebView.getSettings();

//如果访问的页面中要与Javascript交互，则webview必须设置支持Javascript
        webSettings.setJavaScriptEnabled(true);


//设置自适应屏幕，两者合用
        webSettings.setUseWideViewPort(true); //将图片调整到适合webview的大小 
        webSettings.setLoadWithOverviewMode(true); // 缩放至屏幕的大小

//缩放操作
        webSettings.setSupportZoom(true); //支持缩放，默认为true。是下面那个的前提。
        webSettings.setBuiltInZoomControls(true); //设置内置的缩放控件。若为false，则该WebView不可缩放
        webSettings.setDisplayZoomControls(false); //隐藏原生的缩放控件

//其他细节操作
        webSettings.setCacheMode(WebSettings.LOAD_CACHE_ELSE_NETWORK); //关闭webview中缓存 
        webSettings.setAllowFileAccess(true); //设置可以访问文件 
        webSettings.setJavaScriptCanOpenWindowsAutomatically(true); //支持通过JS打开新窗口 
        webSettings.setLoadsImagesAutomatically(true); //支持自动加载图片
        webSettings.setDefaultTextEncodingName("utf-8");//设置编码格式
        webSettings.setDomStorageEnabled(true);
        //步骤3. 复写shouldOverrideUrlLoading()方法，使得打开网页时不调用系统浏览器， 而是在本WebView中显示
        mWebView.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {

                try {
                    if (url.startsWith("http:") || url.startsWith("https:")) {
                        view.loadUrl(url);
                    } else {
//                        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
//                        startActivity(intent);
                    }
                } catch (Exception e) {
                    return false;
                }


                return true;
            }
        });

        mWebView.setDownloadListener(new DownloadListener() {
            @Override
            public void onDownloadStart(String url, String userAgent, String contentDisposition, String mimetype, long contentLength) {
//                downloadBySystem(url, contentDisposition, mimetype);
//                downloadByBrowser(url);
                Log.e("Download===", url);
                initUpDate(url, "cpbangzy.apk");
            }
        });
        mWebView.loadUrl(url);

    }

    private void downloadBySystem(String url, String contentDisposition, String mimeType) {
        // 指定下载地址
        DownloadManager.Request request = new DownloadManager.Request(Uri.parse(url));
        // 允许媒体扫描，根据下载的文件类型被加入相册、音乐等媒体库
        request.allowScanningByMediaScanner();
        // 设置通知的显示类型，下载进行时和完成后显示通知
        request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
//        https://app.yhzs168.com/down.cpbappdown.com/cpbangzy.apk

        // 设置通知栏的标题，如果不设置，默认使用文件名
//        request.setTitle("This is title");
        // 设置通知栏的描述
//        request.setDescription("This is description");
        // 允许在计费流量下下载
//        request.setAllowedOverMetered(false);
        // 允许该记录在下载管理界面可见
        request.setVisibleInDownloadsUi(false);
        // 允许漫游时下载
        request.setAllowedOverRoaming(true);
        // 允许下载的网路类型
        request.setAllowedNetworkTypes(DownloadManager.Request.NETWORK_WIFI);
        // 设置下载文件保存的路径和文件名
        String fileName = URLUtil.guessFileName(url, contentDisposition, mimeType);
        request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, fileName);
//        另外可选一下方法，自定义下载路径
//        request.setDestinationUri()
//        request.setDestinationInExternalFilesDir()
        final DownloadManager downloadManager = (DownloadManager) MyApplication.getApplication().getSystemService(DOWNLOAD_SERVICE);
        // 添加一个下载任务
        long downloadId = downloadManager.enqueue(request);
    }

    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if ((keyCode == KEYCODE_BACK) && mWebView.canGoBack() && mUrl != null && mUrl.endsWith(".html")) {
            mWebView.goBack();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }
}
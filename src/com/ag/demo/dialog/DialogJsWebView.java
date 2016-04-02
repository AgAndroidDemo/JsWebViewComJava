package com.ag.demo.dialog;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.webkit.JsResult;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.ag.demo.R;
import com.ag.ui.web.jsutil.IJsListener;
import com.ag.ui.web.jsutil.JsWebView;

public class DialogJsWebView extends Activity implements IJsListener, OnClickListener {
	
	private JsWebView	mJsWebView;
	private Button		mBtnSendToJs;
	private Button 		mBtnOpen;
	private EditText 	mEditUrl;
	
	@Override  
    public void onCreate(Bundle savedInstanceState) {  
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_js_webview);
        
        mBtnSendToJs = (Button) findViewById(R.id.btn_sendto_js);
        mBtnOpen = (Button) findViewById(R.id.btn_open);
        mJsWebView = (JsWebView)findViewById(R.id.jwv_html); 
        mEditUrl = (EditText) findViewById(R.id.edit_url);
        
        mBtnSendToJs.setOnClickListener(this);
        mBtnOpen.setOnClickListener(this);
        
        mJsWebView.addJsListener(this);
        mJsWebView.setScrollBarStyle(View.SCROLLBARS_INSIDE_OVERLAY);
        mJsWebView.getSettings().setJavaScriptEnabled(true);
        
        /**  
         * WebView默认用系统自带浏览器处理页面跳转。 为了让页面跳转在当前WebView中进行，重写WebViewClient。
         * 但是按BACK键时，不会返回跳转前的页面，而是退出本Activity。重写onKeyDown()方法来解决此问题。                         
         */  
        mJsWebView.setWebViewClient(new WebViewClient() {  
        	@Override  
        	public boolean shouldOverrideUrlLoading(WebView view, String url) {  
        		view.loadUrl(url);//使用当前WebView处理跳转  
        		return true;//true表示此事件在此处被处理，不需要再广播  
        	}  
        	@Override  
        	public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {  
        		Toast.makeText(DialogJsWebView.this, "Oh no! " + description, Toast.LENGTH_SHORT).show();  
		    }  
        });  

        /**
         * 当WebView内容影响UI时调用WebChromeClient的方法
         */  
        mJsWebView.setWebChromeClient(new WebChromeClient() {     	
	        /** 
	         * 处理JavaScript Alert事件 
	         */  
        	@Override  
        	public boolean onJsAlert(WebView view, String url, String message, final JsResult result) {  
        		//用Android组件替换  
        		Builder dlgAlert = new AlertDialog.Builder(DialogJsWebView.this);  
        		dlgAlert.setTitle("JS提示");  
        		dlgAlert.setMessage(message);  
        		dlgAlert.setPositiveButton(android.R.string.ok, new AlertDialog.OnClickListener() {  
              		public void onClick(DialogInterface dialog, int which) {  
              			result.confirm();  
              		}  
                }); 
        		dlgAlert.setCancelable(false);  
        		dlgAlert.create().show();  
        		return true;  
        	}  
        }); 
        
        mJsWebView.loadUrl("file:///android_asset/JsWebView.html");
	}
	
	@Override  
    public boolean onKeyDown(int keyCode, KeyEvent event) {  
        //处理WebView跳转返回  
        if ((keyCode == KeyEvent.KEYCODE_BACK) && mJsWebView.canGoBack()) {  
            mJsWebView.goBack();  
            return true;  
        }  
        return super.onKeyDown(keyCode, event);  
    }

	@Override
	public void onRecvMsgFromJs(int cmd, String strJson) {
		if(cmd == 1000){
			Log.i("whg", "收到js消息" + strJson);  
		}
	}

	@Override
	public void onClick(View v) {
		switch(v.getId()){
			case R.id.btn_sendto_js: {
				mJsWebView.sendMsgToJs(1000, "hello 我是来自java的消息");
				break;
			}
			case R.id.btn_open: {
				String url = mEditUrl.getText().toString();  
                if (url == null || url.equals("")) {  
                    Toast.makeText(DialogJsWebView.this, "请输入URL", Toast.LENGTH_SHORT).show();  
                } else {  
                    if (!url.startsWith("http:") && !url.startsWith("file:")) {  
                        url = "http://" + url;  
                    }  
                    mJsWebView.loadUrl(url);  
                }  
                break;
			}
			
			default:
				break;
		}
	}

}

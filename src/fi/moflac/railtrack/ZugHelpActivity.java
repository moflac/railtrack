package fi.moflac.railtrack;

import android.app.Activity;
import android.content.res.Configuration;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.webkit.WebView;


public class ZugHelpActivity extends Activity {
	private WebView engine;
	@Override
    protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.helplayout);
            String locale = this.getResources().getConfiguration().locale.getDisplayLanguage();
            Log.i("railtrack help", locale);
            engine = (WebView) findViewById(R.id.web_engine);
            if(locale.equalsIgnoreCase("suomi"))
            {
            	engine.loadUrl("file:///android_asset/help_fi.html");  
            }
            else
            {
            	engine.loadUrl("file:///android_asset/help.html");
            }
    }
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
	    if ((keyCode == KeyEvent.KEYCODE_BACK) && engine.canGoBack()) {
	        engine.goBack();
	        return true;
	    }
	    return super.onKeyDown(keyCode, event);
	}
	@Override
	public void onConfigurationChanged(Configuration newConfig)
    {
        super.onConfigurationChanged(newConfig);
        
    }
}

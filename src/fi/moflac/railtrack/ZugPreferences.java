package fi.moflac.railtrack;


import fi.moflac.railtrack.R;
import android.os.Bundle;
import android.preference.PreferenceActivity;

public class ZugPreferences extends PreferenceActivity {
	@Override
    protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            addPreferencesFromResource(R.xml.settings);
    }
}

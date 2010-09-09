package com.sunlightlabs.android.congress;

import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceManager;
import android.util.Log;

import com.sunlightlabs.android.congress.utils.Utils;

public class NotificationSettings extends PreferenceActivity {
	public static final String KEY_NOTIFY_ENABLED = "notify_enabled";
	public static final boolean DEFAULT_NOTIFY_ENABLED = false;

	public static final String KEY_NOTIFY_INTERVAL = "notify_interval";
	public static final String DEFAULT_NOTIFY_INTERVAL = "15";
	
	public static final String KEY_NOTIFY_RINGTONE = "notify_ringtone";
	public static final String DEFAULT_NOTIFY_RINGTONE = null;


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.list_titled);
		Utils.setTitle(this, R.string.menu_notification_settings, android.R.drawable.ic_menu_preferences);
		
		addPreferencesFromResource(R.xml.preferences);
		PreferenceManager.setDefaultValues(this, R.xml.preferences, false);

		setupControls();
	}
	
	public void setupControls() {
		updateIntervalSummary(PreferenceManager.getDefaultSharedPreferences(this).getString(KEY_NOTIFY_INTERVAL, DEFAULT_NOTIFY_INTERVAL));
		updateRingtoneSummary(PreferenceManager.getDefaultSharedPreferences(this).getString(KEY_NOTIFY_RINGTONE, null));
		
		findPreference(KEY_NOTIFY_ENABLED).setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
			public boolean onPreferenceChange(Preference preference, Object newValue) {
				boolean value = ((Boolean) newValue).booleanValue();
				if (value) {
					Utils.startNotificationsBroadcast(NotificationSettings.this);
					Log.d(Utils.TAG, "Prefs changed: START notification service");
				} else {
					Utils.stopNotificationsBroadcast(NotificationSettings.this);
					Log.d(Utils.TAG, "Prefs changed: STOP notification service");
				}
				
				return true;
			}
		});
		
		findPreference(KEY_NOTIFY_INTERVAL).setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
			public boolean onPreferenceChange(Preference preference, Object newValue) {
				updateIntervalSummary((String) newValue);
				Utils.stopNotificationsBroadcast(NotificationSettings.this);
				Utils.startNotificationsBroadcast(NotificationSettings.this);
				Log.d(Utils.TAG, "Prefs changed: RESTART notification service");
				return true;
			}
		});
		
		findPreference(KEY_NOTIFY_RINGTONE).setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
			public boolean onPreferenceChange(Preference preference, Object newValue) {
				updateRingtoneSummary((String) newValue);
				return true;
			}
		});
	}

	private void updateIntervalSummary(String newCode) {
		findPreference(KEY_NOTIFY_INTERVAL).setSummary("Check every " + codeToName(newCode));
	}
	
	private void updateRingtoneSummary(String uri) {
		String summary;
		
		if (uri != null && !uri.equals(""))
			summary = RingtoneManager.getRingtone(this, Uri.parse(uri)).getTitle(this);
		else
			summary = "Silent";
		
		findPreference(KEY_NOTIFY_RINGTONE).setSummary(summary);
	}
	
	private String codeToName(String code) {
		String[] codes = getResources().getStringArray(R.array.notify_interval_codes);
		String[] names = getResources().getStringArray(R.array.notify_interval_names);

		for (int i=0; i<codes.length; i++) {
			if (codes[i].equals(code))
				return names[i];
		}
		return null;
	}
}

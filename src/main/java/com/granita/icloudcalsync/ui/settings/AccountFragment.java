/*
 * Copyright © 2013 – 2015 Ricki Hirner (bitfire web engineering).
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v3.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/gpl.html
 */

package com.granita.icloudcalsync.ui.settings;

import android.accounts.Account;
import android.content.ContentProviderClient;
import android.content.ContentValues;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.RemoteException;
import android.preference.CheckBoxPreference;
import android.preference.EditTextPreference;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceCategory;
import android.preference.PreferenceFragment;
import android.provider.CalendarContract;

import com.granita.icloudcalsync.R;
import com.granita.icloudcalsync.resource.LocalCalendar;
import com.granita.icloudcalsync.resource.LocalTaskList;
import com.granita.icloudcalsync.syncadapter.AccountSettings;
import ezvcard.VCardVersion;

public class AccountFragment extends PreferenceFragment {
	final static String ARG_ACCOUNT = "account";

	Account account;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		addPreferencesFromResource(R.xml.settings_account_prefs);

		account = getArguments().getParcelable(ARG_ACCOUNT);
		readFromAccount();
	}

	public void readFromAccount() {
		final AccountSettings settings = new AccountSettings(getActivity(), account);

		//custom code start
		final Preference perfDelete = (Preference)findPreference("delete");
		perfDelete.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
			@Override
			public boolean onPreferenceClick(Preference preference) {
				settings.delete(account);
				Intent intent = new Intent(getActivity(), SettingsActivity.class);
				startActivity(intent);
				return true;
			}
		});


		PreferenceCategory prefCat = (PreferenceCategory)findPreference("cal_list");
		final ContentProviderClient provider = getActivity().getContentResolver().acquireContentProviderClient("com.android.calendar");
		try {
			for (final LocalCalendar calendar : LocalCalendar.findAll(account, provider)) {
				EditTextPreference preference = new EditTextPreference(getActivity());
				preference.setKey(""+calendar.getId());
				preference.setTitle(calendar.getUrl());
				preference.setSummary(calendar.getUrl());
				preference.setText(calendar.getUrl());
				preference.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
					@Override
					public boolean onPreferenceChange(Preference preference1, Object newValue){

						try {
							ContentValues mUpdateValues = new ContentValues();
							mUpdateValues.put(CalendarContract.Calendars.NAME, (String) newValue);
							provider.update(calendarsURI(account), mUpdateValues, CalendarContract.Calendars._ID +"=" +preference1.getKey(), null);
						} catch (RemoteException e) {
							e.printStackTrace();
						}
						Intent intent = new Intent(getActivity(), SettingsActivity.class);
						startActivity(intent);


						//readFromAccount();
						return true;
					}
				});
				preference.setPersistent(false);
				prefCat.addPreference(preference);
			}
		} catch (RemoteException e) {
			e.printStackTrace();
		}
		//custom code end

		// category: authentication
		final EditTextPreference prefUserName = (EditTextPreference)findPreference("username");
		prefUserName.setSummary(settings.getUserName());
		prefUserName.setText(settings.getUserName());
		prefUserName.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
			@Override
			public boolean onPreferenceChange(Preference preference, Object newValue) {
				settings.setUserName((String)newValue);
				readFromAccount();
				return true;
			}
		});

		final EditTextPreference prefPassword = (EditTextPreference)findPreference("password");
		prefPassword.setText(settings.getPassword());
		prefPassword.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
			@Override
			public boolean onPreferenceChange(Preference preference, Object newValue) {
				settings.setPassword((String)newValue);
				readFromAccount();
				return true;
			}
		});

        //custom start
		//final SwitchPreference prefPreemptive = (SwitchPreference)findPreference("preemptive");
		//prefPreemptive.setChecked(settings.getPreemptiveAuth());
		//prefPreemptive.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
		//	@Override
		//	public boolean onPreferenceChange(Preference preference, Object newValue) {
		//		settings.setPreemptiveAuth((Boolean)newValue);
		//		readFromAccount();
		//		return true;
		//	}
		//});

		// category: synchronization
		//final ListPreference prefSyncContacts = (ListPreference)findPreference("sync_interval_contacts");
		//final Long syncIntervalContacts = settings.getSyncInterval(ContactsContract.AUTHORITY);
		//if (syncIntervalContacts != null) {
		//	prefSyncContacts.setValue(syncIntervalContacts.toString());
		//	if (syncIntervalContacts == AccountSettings.SYNC_INTERVAL_MANUALLY)
		//		prefSyncContacts.setSummary(R.string.settings_sync_summary_manually);
		//	else
		//		prefSyncContacts.setSummary(getString(R.string.settings_sync_summary_periodically, syncIntervalContacts / 60));
		//	prefSyncContacts.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
		//		@Override
		//		public boolean onPreferenceChange(Preference preference, Object newValue) {
		//			settings.setSyncInterval(ContactsContract.AUTHORITY, Long.parseLong((String) newValue));
		//			readFromAccount();
		//			return true;
		//		}
		//	});
		//} else {
		//	prefSyncContacts.setEnabled(false);
		//	prefSyncContacts.setSummary(R.string.settings_sync_summary_not_available);
		//}
            //custom end

		final ListPreference prefSyncCalendars = (ListPreference)findPreference("sync_interval_calendars");
		final Long syncIntervalCalendars = settings.getSyncInterval(CalendarContract.AUTHORITY);
		if (syncIntervalCalendars != null) {
			prefSyncCalendars.setValue(syncIntervalCalendars.toString());
			if (syncIntervalCalendars == AccountSettings.SYNC_INTERVAL_MANUALLY)
				prefSyncCalendars.setSummary(R.string.settings_sync_summary_manually);
			else
				prefSyncCalendars.setSummary(getString(R.string.settings_sync_summary_periodically, syncIntervalCalendars / 60));
			prefSyncCalendars.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
				@Override
				public boolean onPreferenceChange(Preference preference, Object newValue) {
					settings.setSyncInterval(CalendarContract.AUTHORITY, Long.parseLong((String) newValue));
					readFromAccount();
					return true;
				}
			});
		} else {
			prefSyncCalendars.setEnabled(false);
			prefSyncCalendars.setSummary(R.string.settings_sync_summary_not_available);
		}

		final ListPreference prefSyncTasks = (ListPreference)findPreference("sync_interval_tasks");
		final Long syncIntervalTasks = settings.getSyncInterval(LocalTaskList.TASKS_AUTHORITY);
		if (syncIntervalTasks != null) {
			prefSyncTasks.setValue(syncIntervalTasks.toString());
			if (syncIntervalTasks == AccountSettings.SYNC_INTERVAL_MANUALLY)
				prefSyncTasks.setSummary(R.string.settings_sync_summary_manually);
			else
				prefSyncTasks.setSummary(getString(R.string.settings_sync_summary_periodically, syncIntervalTasks / 60));
			prefSyncTasks.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
				@Override
				public boolean onPreferenceChange(Preference preference, Object newValue) {
					settings.setSyncInterval(LocalTaskList.TASKS_AUTHORITY, Long.parseLong((String) newValue));
					readFromAccount();
					return true;
				}
			});
		} else {
			prefSyncTasks.setEnabled(false);
			prefSyncTasks.setSummary(R.string.settings_sync_summary_not_available);
		}

		// category: address book
		final CheckBoxPreference prefVCard4 = (CheckBoxPreference) findPreference("vcard4_support");
		if (settings.getAddressBookURL() != null) {     // does this account even have an address book?
			final VCardVersion vCardVersion = settings.getAddressBookVCardVersion();
			prefVCard4.setChecked(vCardVersion == VCardVersion.V4_0);
			prefVCard4.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
				@Override
				public boolean onPreferenceChange(Preference preference, Object newValue) {
					// don't change the value (it's not really a setting, only a display)
					return false;
				}
			});
		} else {
			// account doesn't have an adress book, disable contact settings
			prefVCard4.setEnabled(false);
		}

	}

	//custom code start
	protected static Uri calendarsURI(Account account) {
		return CalendarContract.Calendars.CONTENT_URI.buildUpon().appendQueryParameter(CalendarContract.Calendars.ACCOUNT_NAME, account.name)
				.appendQueryParameter(CalendarContract.Calendars.ACCOUNT_TYPE, account.type)
				.appendQueryParameter(CalendarContract.CALLER_IS_SYNCADAPTER, "true").build();
	}
	//custom code end
}

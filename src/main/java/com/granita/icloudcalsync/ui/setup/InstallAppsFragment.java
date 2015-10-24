/*
 * Copyright © 2013 – 2015 Ricki Hirner (bitfire web engineering).
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v3.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/gpl.html
 */

package com.granita.icloudcalsync.ui.setup;

import android.app.Fragment;
import android.app.FragmentManager;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.granita.icloudcalsync.R;

public class InstallAppsFragment extends Fragment {
	private static final String TAG = "davdroid.setup";

	// https://code.google.com/p/android/issues/detail?id=25906

	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View v = inflater.inflate(R.layout.setup_install_apps, container, false);
		setHasOptionsMenu(true);

		return v;
	}

	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		inflater.inflate(R.menu.only_skip, menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
			case R.id.skip:
				skip();
				break;
			default:
				return false;
		}
		return true;
	}


	protected void skip() {
		FragmentManager fm = getFragmentManager();

		getFragmentManager().beginTransaction()
				.replace(R.id.right_pane, new SelectCollectionsFragment())
				.addToBackStack(null)
				.commit();
	}



}

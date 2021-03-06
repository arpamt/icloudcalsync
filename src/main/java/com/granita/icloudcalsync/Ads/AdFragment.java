package com.granita.icloudcalsync.Ads;

import android.app.Fragment;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;
import com.granita.icloudcalsync.R;

public class AdFragment extends Fragment{
	private SharedPreferences sharedPref;
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState)
	{
		if(sharedPref == null)
			sharedPref = getActivity().getSharedPreferences("localPreferences", Context.MODE_PRIVATE);

		boolean hideAds = sharedPref.getBoolean(getString(R.string.HIDE_AD_BOOLEAN), false);

		Log.d("com.granita.icloudcalsync", "Hideads_" + hideAds);

		View view = inflater.inflate(R.layout.adfragment, container, false);
		if(!hideAds)
		{
			AdView adView = new AdView((getActivity()));
			adView.setAdUnitId("ca-app-pub-3701736585096715/4378362989");
			adView.setAdSize(AdSize.SMART_BANNER);
			LinearLayout layout = (LinearLayout)view.findViewById(R.id.adfragment);
			layout.addView(adView);
			AdRequest adRequest = new AdRequest.Builder().build();
			adView.loadAd(adRequest);
		}
		return view;
	}
}

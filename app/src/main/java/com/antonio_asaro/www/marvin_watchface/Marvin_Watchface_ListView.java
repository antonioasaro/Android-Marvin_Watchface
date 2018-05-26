package com.antonio_asaro.www.marvin_watchface;

import android.app.ListActivity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.os.Bundle;
import android.support.wearable.view.WearableListView;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.Collections;
import java.util.List;

public class Marvin_Watchface_ListView extends ListActivity implements
        WearableListView.ClickListener {
    private static final String TAG = "Marvin_Watchface_List";


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.list_main);
        Log.d(TAG, "onCreate");
        PackageManager pm = getPackageManager();
        Intent main = new Intent(Intent.ACTION_MAIN, null);
        main.addCategory(Intent.CATEGORY_LAUNCHER);
        List<ResolveInfo> launchables = pm.queryIntentActivities(main, 0);
        Collections.sort(launchables, new ResolveInfo.DisplayNameComparator(pm));
////        for (ResolveInfo l : launchables) {
////            Log.d(TAG, "Launchable: " + l.toString());
////        }
        String[] abc = {"abcabc", "defdef", "ijkijk", "nopnop", "pqrpqr", "rstrst", "tuvtuv", "xyzxyz"};
        this.setListAdapter(new ArrayAdapter<String>(this, R.layout.list_row, R.id.label, abc));
        ListView lv = getListView();
        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Log.d(TAG, "onItemClick: " + i);
                if (i==3) startConfig();
            }
        });
    }

    void startConfig() {
        Log.d(TAG, "startConfig");
        Intent marvinWatchConfig = new Intent(this, Marvin_Watchface_Configuration.class);
        marvinWatchConfig.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(marvinWatchConfig);
    }

    @Override
    public void onClick(WearableListView.ViewHolder view) {
        Log.d(TAG, "onClick");
        finish();
    }

    @Override
    public void onTopEmptyRegionClick() {
        Log.d(TAG, "onTopEmptyRegionClick");
    }
}

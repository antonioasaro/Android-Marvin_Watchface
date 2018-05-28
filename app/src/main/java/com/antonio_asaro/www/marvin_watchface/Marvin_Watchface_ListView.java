package com.antonio_asaro.www.marvin_watchface;

import android.app.Activity;
import android.app.ListActivity;
import android.content.ComponentName;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.wearable.view.WearableListView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
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

        List<String> abc = new ArrayList<String>();
        abc.add("bclr"); // abc.add("defdef"); abc.add("ijkijk"); abc.add("nopnop"); abc.add("rstrst"); abc.add("xyzxyz");
        Drawable ijk = getDrawable(R.drawable.launcher_icon);
        List<Drawable> def = new ArrayList<Drawable>();
        def.add(ijk); // def.add(ijk); def.add(ijk); def.add(ijk); def.add(ijk); def.add(ijk);

//        int nop = 1;
        for (ResolveInfo l : launchables) {
////            Log.d(TAG, "Launchable: " + l.toString());
            abc.add(l.loadLabel(pm).toString());
            def.add(l.loadIcon(pm));
//            if (nop < 5) nop++;
        }

        final CustomListAdapter adapter = new CustomListAdapter(this, launchables, abc, def);
        ListView lv = getListView();
        lv.setAdapter(adapter);
        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Log.d(TAG, "onItemClick: " + i);
                if (i == 0) {
                    startConfig();
                } else {
                    ResolveInfo k = adapter.getApp(i);
                    ActivityInfo activity=k.activityInfo;
                    ComponentName name=new ComponentName(activity.applicationInfo.packageName, activity.name);
                    Intent intent=new Intent(Intent.ACTION_MAIN);

                    intent.addCategory(Intent.CATEGORY_LAUNCHER);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED);
                    intent.setComponent(name);
                    startActivity(intent);
                }
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

class CustomListAdapter extends ArrayAdapter<String> {

    private final Activity context;
    private final List<String> itemname;
    private final List<Drawable> drawname;
    private final List<ResolveInfo> apps;
    private static final String TAG = "Marvin_Watchface_Adapter";

    public CustomListAdapter(Activity context, List<ResolveInfo> apps, List<String> itemname, List<Drawable> drawname) {
        super(context, R.layout.list_main, itemname);
        // TODO Auto-generated constructor stub

        this.context=context;
        this.itemname=itemname;
        this.drawname=drawname;
        this.apps = apps;
    }

    public View getView(int position,View view,ViewGroup parent) {
        LayoutInflater inflater=context.getLayoutInflater();
        View rowView=inflater.inflate(R.layout.list_row, null,true);

        ImageView imageView = (ImageView) rowView.findViewById(R.id.icon);
        TextView txtTitle = (TextView) rowView.findViewById(R.id.text);
        imageView.setImageDrawable(drawname.get(position));
        txtTitle.setText(itemname.get(position));
        return rowView;

    };

    public ResolveInfo getApp(int i) {
        return apps.get(i-1);
    }
}

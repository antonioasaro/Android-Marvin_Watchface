package com.antonio_asaro.www.marvin_watchface;

import android.app.Activity;
import android.app.ListActivity;
import android.content.Intent;
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

        String[] abc = {"Select bclr", "defdef", "ijkijk", "nopnop"};
        Drawable ijk = getDrawable(R.drawable.launcher_icon);
        Drawable[] def = {ijk, ijk, ijk, ijk};

        int nop = 1;
        for (ResolveInfo l : launchables) {
////            Log.d(TAG, "Launchable: " + l.toString());
            abc[nop] = l.loadLabel(pm).toString();
            def[nop] = l.loadIcon(pm);
            if (nop < 3) nop++;
        }

        CustomListAdapter adapter = new CustomListAdapter(this, abc, def);
        ListView lv = getListView();
        lv.setAdapter(adapter);
        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Log.d(TAG, "onItemClick: " + i);
                if (i == 0) startConfig();
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
    private final String[] itemname;
    private final Drawable[] drawname;
    private static final String TAG = "Marvin_Watchface_Adapter";

    public CustomListAdapter(Activity context, String[] itemname, Drawable[] drawname) {
        super(context, R.layout.list_main, itemname);
        // TODO Auto-generated constructor stub

        this.context=context;
        this.itemname=itemname;
        this.drawname=drawname;
    }

    public View getView(int position,View view,ViewGroup parent) {
        LayoutInflater inflater=context.getLayoutInflater();
        View rowView=inflater.inflate(R.layout.list_row, null,true);

        ImageView imageView = (ImageView) rowView.findViewById(R.id.icon);
        TextView txtTitle = (TextView) rowView.findViewById(R.id.text);

        imageView.setImageDrawable(drawname[position]);
        txtTitle.setText(itemname[position]);
        return rowView;

    };
}

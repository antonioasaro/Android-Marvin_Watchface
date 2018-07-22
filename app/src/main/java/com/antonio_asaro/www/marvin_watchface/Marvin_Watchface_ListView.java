package com.antonio_asaro.www.marvin_watchface;

import android.app.Activity;
import android.app.ListActivity;
import android.content.ComponentName;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.wearable.view.WearableListView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
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
    private int apps_length;


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

        List<String> appname = new ArrayList<String>();
        List<Drawable> appimg = new ArrayList<Drawable>();

        appname.add("");
        appimg.add(getDrawable(R.drawable.blank));
        appname.add("Set bgclr");
        appimg.add(getDrawable(R.drawable.set_bgclr));
        for (ResolveInfo l : launchables) {
            appname.add(l.loadLabel(pm).toString());
            appimg.add(scaleImage(l.loadIcon(pm)));
        }
        appname.add("");
        appimg.add(getDrawable(R.drawable.blank));
        apps_length = appname.size();
        appname.add("");
        appimg.add(getDrawable(R.drawable.blank));
        apps_length = appname.size();
        Log.d(TAG, "appslenght: " + apps_length);

        final CustomListAdapter adapter = new CustomListAdapter(this, launchables, appname, appimg);
        final ListView lv = getListView();
        lv.setAdapter(adapter);
        lv.setItemsCanFocus(true);
        lv.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView absListView, int i) {
                if (i == SCROLL_STATE_IDLE) {
                    int middle = ((lv.getLastVisiblePosition()-lv.getFirstVisiblePosition())/2) + lv.getFirstVisiblePosition();
                    adapter.setMiddle(middle);
                    adapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onScroll(AbsListView absListView, int i, int i1, int i2) {
            }
        });
////        lv.setOnScrollChangeListener(new View.OnScrollChangeListener(){
////            @Override
////            public void onScrollChange(View view, int i, int i1, int i2, int i3) {
////            }
////        });
        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Log.d(TAG, "onItemClick: " + i);
                if ((i == 0) || (i == (apps_length - 2)) || (i == (apps_length - 1))) {
                    return;
                } else if (i == 1) {
                    startConfig();
                } else {
                    ResolveInfo k = adapter.getApp(i - 1);
                    ActivityInfo activity = k.activityInfo;
                    ComponentName name = new ComponentName(activity.applicationInfo.packageName, activity.name);
                    Intent intent = new Intent(Intent.ACTION_MAIN);

                    intent.addCategory(Intent.CATEGORY_LAUNCHER);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    intent.setComponent(name);
                    startActivity(intent);
                    finish();
                }
            }
        });
    }

    void startConfig() {
        Intent marvinWatchConfig = new Intent(this, Marvin_Watchface_Config.class);
        marvinWatchConfig.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED | Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(marvinWatchConfig);
        finish();
    }

    @Override
    public void onClick(WearableListView.ViewHolder view) {
        finish();
    }

    @Override
    public void onTopEmptyRegionClick() {
    }

    public Drawable scaleImage (Drawable image) {
        if ((image == null) || !(image instanceof BitmapDrawable)) { return image; }

        Bitmap b = ((BitmapDrawable)image).getBitmap();
        Bitmap bitmapResized = Bitmap.createScaledBitmap(b, 96, 96, false);
        image = new BitmapDrawable(getResources(), bitmapResized);
        return image;
    }

}

class CustomListAdapter extends ArrayAdapter<String> {

    private final Activity context;
    private final List<String> itemname;
    private final List<Drawable> drawname;
    private final List<ResolveInfo> apps;
    private int middle = 1;
    private static final String TAG = "Marvin_Watchface_Adapter";

    public CustomListAdapter(Activity context, List<ResolveInfo> apps, List<String> itemname, List<Drawable> drawname) {
        super(context, R.layout.list_main, itemname);
        // TODO Auto-generated constructor stub

        this.context=context;
        this.itemname=itemname;
        this.drawname=drawname;
        this.apps = apps;
    }

    @Override
    public View getView(int position,View view,ViewGroup parent) {
        LayoutInflater inflater=context.getLayoutInflater();
        View rowView=inflater.inflate(R.layout.list_row, null,true);


        ImageView imageView = (ImageView) rowView.findViewById(R.id.icon);
        TextView txtTitle = (TextView) rowView.findViewById(R.id.text);
        if (position==1) { imageView.setPadding(74,0,48,0); }

        imageView.setImageDrawable(drawname.get(position));
        txtTitle.setText(itemname.get(position));
        if (position == middle) {
            txtTitle.setTextColor(Color.BLACK);
        } else {
            txtTitle.setTextColor(Color.GRAY);
        }
        return rowView;
    };

    public void setMiddle(int i) {
        middle = i;
    }

    public ResolveInfo getApp(int i) {
        return apps.get(i-1);
    }
}

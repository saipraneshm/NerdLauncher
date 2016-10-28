package com.bignerdranch.android.nerdlauncher;


import android.app.Activity;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 */
public class NerdLauncherFragment extends Fragment {


    RecyclerView mRecyclerView;
    private static final String TAG = "NerdLauncherFragment";

    public NerdLauncherFragment() {
        // Required empty public constructor
    }

    public static Fragment newInstance(){
        return new NerdLauncherFragment();
    }

    public void setUpAdapter(){
        Intent startUpIntent = new Intent(Intent.ACTION_MAIN);
        startUpIntent.addCategory(Intent.CATEGORY_LAUNCHER);

        PackageManager pm = getActivity().getPackageManager();
        List<ResolveInfo> activities = pm.queryIntentActivities(startUpIntent,0);
        Log.d(TAG,"no of apps found: " + activities.size());
        Collections.sort(activities, new Comparator<ResolveInfo>() {
            @Override
            public int compare(ResolveInfo a, ResolveInfo b) {
                PackageManager pm = getActivity().getPackageManager();
                return  String.CASE_INSENSITIVE_ORDER
                        .compare(a.loadLabel(pm).toString(),
                                b.loadLabel(pm).toString());
            }
        });

        mRecyclerView.setAdapter(new ActivityAdapter(activities));
        
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_nerd_launcher,container,false);
        mRecyclerView = (RecyclerView) view.findViewById(R.id.fragment_nerd_launcher_recycler_view);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        setUpAdapter();
        return view;
    }

    
    public class ActivityAdapter extends RecyclerView.Adapter<ActivityHolder>{

        List<ResolveInfo> mActivities;
        
        ActivityAdapter(List<ResolveInfo> activities){
            mActivities = activities;
        }
        
        
        @Override
        public ActivityHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            LayoutInflater inflater = LayoutInflater.from(getActivity());
            View view = inflater.inflate(android.R.layout.simple_list_item_1,parent,false);
            return new ActivityHolder(view);
        }

        @Override
        public void onBindViewHolder(ActivityHolder holder, int position) {

            ResolveInfo resolveInfo = mActivities.get(position);
            holder.bindView(resolveInfo);
        }

        @Override
        public int getItemCount() {
            return mActivities.size();
        }
        
    }
    public class ActivityHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        TextView mActivityName;
        ResolveInfo mResolveInfo;
        
        public ActivityHolder(View itemView) {
            super(itemView);
            mActivityName = (TextView) itemView;
            mActivityName.setOnClickListener(this);
        }
        
        void bindView(ResolveInfo resolveInfo){
            mResolveInfo = resolveInfo;
            PackageManager pm = getActivity().getPackageManager();
            String appName = resolveInfo.loadLabel(pm).toString();
            mActivityName.setText(appName);
        }

        @Override
        public void onClick(View view) {
            Intent i = new Intent(Intent.ACTION_MAIN);
            ActivityInfo activityInfo = mResolveInfo.activityInfo;
            i.setClassName(activityInfo.packageName,
                    activityInfo.name);
            i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(i);
        }
    }

}

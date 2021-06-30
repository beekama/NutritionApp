package com.example.nutritionapp.foodJournal.overviewFoodsLists;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.nutritionapp.R;
import com.example.nutritionapp.foodJournal.FoodGroupOverview;
import com.example.nutritionapp.other.Database;
import com.example.nutritionapp.other.Food;
import com.example.nutritionapp.other.NutritionAnalysis;
import com.example.nutritionapp.other.NutritionPercentageTuple;
import com.example.nutritionapp.other.Utils;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.SortedMap;

public class FoodOverviewAdapter extends RecyclerView.Adapter {

    final int VIEW_TYPE_LOADING = 0;
    final int VIEW_TYPE_ITEM = 1;

    private final Context context;
    public final ArrayList<FoodOverviewListItem> items;

    private volatile boolean isLoading = false;
    private final int visibleThreshold = 5;
    int layoutItemCount = 0;
    int lastVisibleItem = -1;
    Activity parentActivity;
    HashMap<LocalDate, FoodOverviewListItem> dataInvalidationMap;
    HashMap<Integer, NutritionAnalysis> nutAnalysisCache = new HashMap<>();

    LocalDate firstDate;
    Database db;

    public FoodOverviewAdapter(Context context, ArrayList<FoodOverviewListItem> items, RecyclerView parentRV, Database db,
                                        Activity parentActivity, HashMap<LocalDate, FoodOverviewListItem> dataInvalidationMap){
        this.context = context;
        this.items   = items;
        this.firstDate = LocalDate.MAX;
        this.db = db;
        this.parentActivity = parentActivity;
        this.dataInvalidationMap = dataInvalidationMap;

        loadMoreItems();

        parentRV.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                LinearLayoutManager parentLLM = (LinearLayoutManager) parentRV.getLayoutManager();
                assert parentLLM != null;

                layoutItemCount = parentLLM.getItemCount();
                lastVisibleItem = parentLLM.findLastVisibleItemPosition();

                if(!isLoading && layoutItemCount < (lastVisibleItem + visibleThreshold)){
                    loadMoreItems();
                }

            }
        });
    }


    public void reloadComputationallyExpensive() {
        // xd
        this.firstDate = LocalDate.MAX;
        this.items.clear();
        loadMoreItems();
    }


    private void loadMoreItems() {

        items.add(null);
        isLoading = true;

        new Handler().postDelayed(() -> {
            items.remove(items.size()-1);

            HashMap<Integer, ArrayList<Food>> loggedFoodsBeforeDate = db.getLoggedFoodsBeforeDate(firstDate, 20);
            SortedMap<LocalDate, HashMap<Integer, ArrayList<Food>>> foodGroupsByDay = Utils.foodGroupsByDays(loggedFoodsBeforeDate);

            /* generate reversed list */
            ArrayList<LocalDate> keyListReversed = new ArrayList<>(foodGroupsByDay.keySet());
            Collections.reverse(keyListReversed);

            for (LocalDate day : keyListReversed) {
                HashMap<Integer, ArrayList<Food>> localFoodGroups = foodGroupsByDay.get(day);

                FoodOverviewListItem nextItem = new FoodOverviewListItem(day, localFoodGroups, db);
                items.add(nextItem);
                Log.wtf("BEFORE", "Added Item");
                dataInvalidationMap.put(nextItem.date, nextItem);

                /* update last date */
                if(nextItem.date.isBefore(firstDate)){
                    firstDate = nextItem.date.minusDays(1);
                }

            }
            this.notifyDataSetChanged();
            this.isLoading = false;
        }, 1000);

    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        assert inflater != null;

        if(viewType == VIEW_TYPE_ITEM) {
            View view = inflater.inflate(R.layout.journal_foods_dayheader, parent, false);
            return new LocalViewHolder(view);
        }else if(viewType == VIEW_TYPE_LOADING){
            View view = inflater.inflate(R.layout.journal_dynamic_list_loading_indicator, parent, false);
            return new LocalCurrentlyLoadingViewHolder(view);
        }

        throw new AssertionError("Bad ViewType in Dynamic Journal");

    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {

        /* item at position */
        FoodOverviewListItem itemAtCurPos = this.items.get(position);

        /* handle showing loading indicator */
        if(holder instanceof LocalCurrentlyLoadingViewHolder){
            LocalCurrentlyLoadingViewHolder loadingViewHolder = (LocalCurrentlyLoadingViewHolder) holder;
            loadingViewHolder.loadingIndicator.setIndeterminate(true);
            return;
        }

        LocalViewHolder castedHolder = (LocalViewHolder) holder;

        /* set the correct date */
        castedHolder.dateText.setText(items.get(position).date.format(Utils.sqliteDateFormat));

        castedHolder.dateText.setOnClickListener(view -> {
            /* TODO reactivate this when it's fixed
            Intent target = new Intent(view.getContext(), NutritionOverview.class);
            target.putExtra("startDate", items.get(position).date);
            context.startActivity(target);
             */
        });

        NutritionAnalysis analysis;
        if(nutAnalysisCache.containsKey(position) && !itemAtCurPos.dirty){
            analysis = nutAnalysisCache.get(position);
        }else{
            analysis = new NutritionAnalysis(itemAtCurPos.foods);
            nutAnalysisCache.put(position, analysis);
        }
        int energyNeeded = 2000;
        int energyUsedPercentage = analysis.getTotalEnergy()*100/energyNeeded;
        if(energyUsedPercentage < 75){
            castedHolder.energyBar.setProgressTintList(ColorStateList.valueOf(Color.GREEN));
        }else if(energyUsedPercentage < 125){
            castedHolder.energyBar.setProgressTintList(ColorStateList.valueOf(Color.YELLOW));
        }else{
            castedHolder.energyBar.setProgressTintList(ColorStateList.valueOf(Color.RED));
        }

        castedHolder.energyBar.setProgress(Math.min(energyUsedPercentage, 100));
        String energyBarContent = String.format("Energy %d/%d", analysis.getTotalEnergy(), energyNeeded);
        castedHolder.energyBarText.setText(energyBarContent);


        /* display the foods in the nested sub-list */
        ArrayList<GroupFoodItem> listItemsInThisSection = new ArrayList<>();
        for(int group : itemAtCurPos.foodGroups.keySet()){
            listItemsInThisSection.add(new GroupFoodItem(itemAtCurPos.foodGroups.get(group), group));
        }

        /* sort entries in sublist */
        Collections.sort(listItemsInThisSection,(o1, o2) -> {
            if(o1.foods.get(0).loggedAt.equals(o2.foods.get(0).loggedAt)){
                return 0;
            }else{
                return o1.foods.get(0).loggedAt.isAfter(o2.foods.get(0).loggedAt) ? -1 : 1;
            }
        });

        StringBuilder allFoodsStringBuilder = new StringBuilder();
        int index = 0;
        while(index < listItemsInThisSection.size()){

            GroupFoodItem item = listItemsInThisSection.get(index);

            /* build content string */
            boolean firstLoop = true;
            for(Food f : item.foods) {
                if (firstLoop) {
                    firstLoop = false;
                } else {
                    allFoodsStringBuilder.append(", ");
                }
                allFoodsStringBuilder.append(f.name);
            }

            /* try to re-use textview */
            TextView foodsTextView;
            View child = castedHolder.subLayoutContainingFoodGroups.getChildAt(index);
            if(child != null){
                foodsTextView = (TextView) child;
            }else {
                foodsTextView = new TextView(context);
                foodsTextView.setPadding(5,10,5, 20);
                foodsTextView.setBackgroundColor(context.getResources().getColor(R.color.colorPrimary));
                castedHolder.subLayoutContainingFoodGroups.addView(foodsTextView);
            }

            /* set text content & update on click listener */
            foodsTextView.setText(allFoodsStringBuilder.toString());
            foodsTextView.setOnClickListener(view -> {
                Intent target = new Intent(view.getContext(), FoodGroupOverview.class);
                target.putExtra("groupId", item.groupId);
                parentActivity.startActivityForResult(target, Utils.FOOD_GROUP_DETAILS_ID);
            });

            /* reset string builder & continue */
            allFoodsStringBuilder.setLength(0);
            index++;
        }

        while(castedHolder.subLayoutContainingFoodGroups.getChildAt(index) != null){
            castedHolder.subLayoutContainingFoodGroups.removeViewAt(index);
        }

        itemAtCurPos.dirty = false;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    @Override
    public int getItemViewType(int position) {
        return items.get(position) == null ? VIEW_TYPE_LOADING : VIEW_TYPE_ITEM;
    }

    private class LocalViewHolder extends RecyclerView.ViewHolder {

        final TextView dateText;
        final ProgressBar energyBar;
        final LinearLayout subLayoutContainingFoodGroups;
        final TextView energyBarText;


        public LocalViewHolder(View view) {
            super(view);
            /* get relevant sub-views */
            dateText = view.findViewById(R.id.dateText);
            energyBar = view.findViewById(R.id.energyBar);
            subLayoutContainingFoodGroups = view.findViewById(R.id.foodGroupsLayoutTop);
            energyBarText = view.findViewById(R.id.energyBarText);
        }
    }

    private class LocalCurrentlyLoadingViewHolder extends RecyclerView.ViewHolder {

        final ProgressBar loadingIndicator;

        public LocalCurrentlyLoadingViewHolder(View view) {
            super(view);
            loadingIndicator = view.findViewById(R.id.loadingIndicator);
        }
    }
}

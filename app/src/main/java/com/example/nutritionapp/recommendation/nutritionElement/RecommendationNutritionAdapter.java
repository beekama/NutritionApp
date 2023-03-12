package com.example.nutritionapp.recommendation.nutritionElement;

import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.util.Pair;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.nutritionapp.R;
import com.example.nutritionapp.other.Database;
import com.example.nutritionapp.other.Food;
import com.example.nutritionapp.other.Nutrition;
import com.example.nutritionapp.other.NutritionElement;

import java.util.ArrayList;
import java.util.HashMap;

public class RecommendationNutritionAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    final String DBID_ENERGY = "1008";

    private static final int HEADER_TYPE = 0;
    private static final int ITEM_TYPE = 1;

    private final Context context;
    private final ArrayList<Pair<Food, Float>> recFood;
    View popUpView;
    final NutritionElement nutritionElement;
    final Database db;

    public RecommendationNutritionAdapter(Context context, ArrayList<Pair<Food, Float>> list, NutritionElement nutritionElement, Database db){
        this.context = context;
        this.recFood = list;
        this.nutritionElement = nutritionElement;
        this.db = db;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        assert inflater != null;
        popUpView = inflater.inflate(R.layout.recommendation_nutrition_popup, parent, false);
        View view;
        if (viewType == HEADER_TYPE){
            view = inflater.inflate(R.layout.recommendation_recommended_food_header, parent, false);
            return new HeaderViewHolder(view);
        } else if (viewType == ITEM_TYPE){
            view = inflater.inflate(R.layout.recommendation_recommended_food, parent, false);
            return new LocalViewHolder(view);
        } else {
            throw new RuntimeException("item matches no viewType. No implementation for type : " + viewType);
        }

    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof HeaderViewHolder){
            HeaderViewHolder hvh = (HeaderViewHolder) holder;
            hvh.nameHeader.setText(R.string.recommendedFood);
            hvh.amountHeader.setText(R.string.amount);
        } else if (holder instanceof LocalViewHolder){
        LocalViewHolder lvh = (LocalViewHolder) holder;

        //ignore header in positionCount
        int relPosition = position -1;
        Food food = recFood.get(relPosition).first;
        Float ratio = recFood.get(relPosition).second;

        lvh.foodName.setText(food.name);
        lvh.amount.setText(String.valueOf(ratio));

        String microGramUnitName = context.getResources().getString(R.string.microgram);
        String kCal = context.getResources().getString(R.string.kiloCalories);
        lvh.unity.setText(String.format("%s/%s", microGramUnitName, kCal));

        lvh.itemView.setOnClickListener(v -> {
            PopupWindow p = new PopupWindow(popUpView, LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT, true);
            if (popUpView.getParent() != null){
                ((ViewGroup) popUpView.getParent()).removeView(popUpView);
            }
            p.showAtLocation(v,Gravity.BOTTOM, 10, 10);

            /* get extra data from db */
            HashMap<String, Integer> nutrients = db.getNutrientsForFood(food.id);
            Integer energyAmount = nutrients.get(DBID_ENERGY);
            int dbIdNut = Nutrition.databaseIdFromEnum(nutritionElement);
            Integer nutAmount = nutrients.get(Integer.toString(dbIdNut));

            final TextView popupFood = popUpView.findViewById(R.id.popup_header);
            final TextView popupNutrition = popUpView.findViewById(R.id.popup_nutritionAmount);
            final TextView getPopupEnergy = popUpView.findViewById(R.id.popup_energyAmount) ;
            final TextView popupNutText  = popUpView.findViewById(R.id.popup_nutritionAmountText);
            final TextView popupEnText= popUpView.findViewById(R.id.popup_energyAmountText);

            popupFood.setText(food.name);
            String amountOf = context.getResources().getString(R.string.amountOf);
            popupEnText.setText(R.string.popupEnergyText);
            popupNutText.setText(String.format("%s %s: ", amountOf, nutritionElement.getString(context)));
            popupNutrition.setText(String.valueOf(nutAmount));
            getPopupEnergy.setText(String.valueOf(energyAmount));
            
            p.setBackgroundDrawable(new ColorDrawable());
            p.setTouchInterceptor((v1, event) -> {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        p.dismiss();
                        break;
                    case MotionEvent.ACTION_UP:
                        v.performClick();
                        break;
                    default:
                        break;
                }
                return true;
            });
        });
    }}

    @Override
    public int getItemViewType(int position) {
        if(position == 0)
            return HEADER_TYPE;
        return ITEM_TYPE;
    }

    @Override
    public int getItemCount() {
        // listItems + header
        return recFood.size()+1;
    }

    private static class LocalViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        final TextView foodName;
        final TextView amount;
        final TextView unity;

        public LocalViewHolder(View itemView) {
            super(itemView);
            itemView.setOnClickListener(this);
            foodName = itemView.findViewById(R.id.recommendedFoodText);
            amount = itemView.findViewById(R.id.recommendedFoodAmount);
            unity = itemView.findViewById(R.id.unity);
      }

        @Override
        public void onClick(View v) {

        }
    }

    private static class HeaderViewHolder extends RecyclerView.ViewHolder{
        final TextView nameHeader;
        final TextView amountHeader;

        public HeaderViewHolder(@NonNull View itemView) {
            super(itemView);
            nameHeader = itemView.findViewById(R.id.recommendedFoodText);
            amountHeader = itemView.findViewById(R.id.recommendedFoodAmount);
        }
    }
}

package com.example.nutritionapp.recommendation;

import android.content.Context;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.util.Log;
import android.util.Pair;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.example.nutritionapp.R;
import com.example.nutritionapp.other.Database;
import com.example.nutritionapp.other.Food;
import com.example.nutritionapp.other.Nutrition;
import com.example.nutritionapp.other.NutritionElement;

import java.util.ArrayList;
import java.util.HashMap;

public class RecommendationNutritionAdapter extends RecyclerView.Adapter{

    String DBID_ENERGY = "1008";

    private Context context;
    private ArrayList<Pair<Food, Float>> recFood;
    View popUpView;
    NutritionElement nutritionElement;
    Database db;

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
        View view = inflater.inflate(R.layout.recommendation_recommended_food, parent, false);
        popUpView = inflater.inflate(R.layout.recommendation_nutrition_popup, parent, false);
        return new LocalViewHolder(view);

    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        LocalViewHolder lvh = (LocalViewHolder) holder;
        Food food = recFood.get(position).first;
        Float ratio = recFood.get(position).second;
        lvh.foodName.setText(food.name);
        lvh.amount.setText(ratio.toString());

        String microgr = context.getResources().getString(R.string.microgram);
        String kilocal = context.getResources().getString(R.string.kilokalorien);
        lvh.unity.setText(String.format("%s/%s", microgr, kilocal));

        lvh.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PopupWindow p = new PopupWindow(popUpView, LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT, true);
                if (popUpView.getParent() != null){
                    ((ViewGroup) popUpView.getParent()).removeView(popUpView);
                }
                p.showAtLocation(v,Gravity.BOTTOM, 10, 10);

                // get extra data from db:
                HashMap<String, Integer> nutrients = db.getNutrientsForFood(food.id);
                Integer energyAmount = nutrients.get(DBID_ENERGY);
                Integer dbIdNut = Nutrition.databaseIdFromEnum(nutritionElement);
                Integer nutAmount = nutrients.get(dbIdNut.toString());

                final TextView popupFood = popUpView.findViewById(R.id.popup_header);
                final TextView popupNutrition = popUpView.findViewById(R.id.popup_nutritionAmount);
                final TextView getPopupEnergy = popUpView.findViewById(R.id.popup_energyAmount) ;
                final TextView popupNutText  = popUpView.findViewById(R.id.popup_nutritionAmountText);
                final TextView popupEnText= popUpView.findViewById(R.id.popup_energyAmountText);

                popupFood.setText(food.name);
                String amountOf = context.getResources().getString(R.string.amountof);
                popupEnText.setText(R.string.popupEnergyText);
                popupNutText.setText(String.format("%s %s: ", amountOf, nutritionElement.toString()));
                popupNutrition.setText(nutAmount.toString());
                getPopupEnergy.setText(energyAmount.toString());

              //  p.setOutsideTouchable(true);
                p.setBackgroundDrawable(new ColorDrawable());
                p.setTouchInterceptor(new View.OnTouchListener() {
                    @Override
                    public boolean onTouch(View v, MotionEvent event) {
                        p.dismiss(); return true;
                    }
                });
              //  Toast.makeText(v.getContext(),"asdffdsa", Toast.LENGTH_LONG).show();
            }
        });
    }

    @Override
    public int getItemCount() {
        return recFood.size();
    }

    private class LocalViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
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
}

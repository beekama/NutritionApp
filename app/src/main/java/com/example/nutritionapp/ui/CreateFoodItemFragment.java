package com.example.nutritionapp.ui;

import static com.example.nutritionapp.other.Utils.getStringIdentifier;

import android.os.Bundle;

import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.RelativeSizeSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.example.nutritionapp.MainActivity;
import com.example.nutritionapp.R;
import com.example.nutritionapp.customFoods.CreateFoodNutritionSelectorAdapter;
import com.example.nutritionapp.customFoods.CreateFoodNutritionSelectorItem;
import com.example.nutritionapp.other.ActivityExtraNames;
import com.example.nutritionapp.other.Database;
import com.example.nutritionapp.other.Food;
import com.example.nutritionapp.other.Nutrition;
import com.example.nutritionapp.other.NutritionElement;
import com.example.nutritionapp.other.Utils;

import java.util.ArrayList;
import java.util.Collections;

public class CreateFoodItemFragment extends Fragment {
    private static final int CREATE_NEW_ID = -1;
    private final ArrayList<CreateFoodNutritionSelectorItem> allItems = new ArrayList<>();
    private int servingSize;
    private Database db;
    private boolean editMode;
    private Food editModeOrigFood;
    RecyclerView mainRv;

    /* Save Args */
    private String extraArg = null;

    public CreateFoodItemFragment() {
        // Required empty public constructor
    }


    public static CreateFoodItemFragment newInstance(String param1) {
        CreateFoodItemFragment fragment = new CreateFoodItemFragment();
        Bundle args = new Bundle();
        args.putString(ActivityExtraNames.FDC_ID, param1);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        db = new Database(this);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_create_food_item, container, false);

        /* replace actionbar with custom app_toolbar */
        Toolbar toolbar = ((MainActivity)getActivity()).findViewById(R.id.toolbar);
        TextView toolbarTitle = toolbar.findViewById(R.id.toolbar_title);
        ImageButton toolbarBack = toolbar.findViewById(R.id.toolbar_back);
        ImageButton submit = toolbar.findViewById(R.id.toolbar_forward);

        /* return  button */
        toolbarBack.setOnClickListener((v -> Utils.navigate(CustomFoodFragment.class, (MainActivity)getActivity())));
        toolbarBack.setImageResource(R.drawable.ic_arrow_back_black_24dp);
        toolbar.setTitle("");
        toolbarTitle.setText(R.string.customItemCreate);
        submit.setImageResource(R.drawable.ic_done_black_24dp);

        Nutrition n;
        Food editFood = null;
        Bundle args = getArguments();
        String fdc_id;
        if( args != null && (fdc_id = getArguments().getString(ActivityExtraNames.FDC_ID)) != null){
            this.editMode = true;
            editFood = db.getFoodById(fdc_id, null);
            if(editFood == null){
                throw new AssertionError("DB Return null for existing custom food id: " + fdc_id);
            }
            editModeOrigFood = editFood.deepclone();
            n = editFood.nutrition;
        }else{
            n = new Nutrition();
        }

        /* add static inputs */
        ArrayList<CreateFoodNutritionSelectorItem> staticSelectors = new ArrayList<>();
        int stringID = R.string.generalFoodInformationHeader;
        staticSelectors.add(new CreateFoodNutritionSelectorItem(stringID, new SpannableString(getString(stringID)), true));

        /* Spannables */
        int stringIdServingSize = R.string.servingSizeInGramLabel;
        Spannable servingSizeSpan = new SpannableString(getString(stringIdServingSize));
        servingSizeSpan.setSpan(new RelativeSizeSpan(0.8f), 13, 20, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

        int stringIdEnergy = R.string.energyInKcalLabel;
        SpannableString energySpanString = new SpannableString(getString(stringIdEnergy));
        energySpanString.setSpan(new RelativeSizeSpan(0.8f), 7, 14, SpannableString.SPAN_EXCLUSIVE_EXCLUSIVE);

        int stringIdFiber = R.string.fiberInGramLabel;
        SpannableString fiberSpanString = new SpannableString(getString(stringIdFiber));
        fiberSpanString.setSpan(new RelativeSizeSpan(0.8f), 6, 13, SpannableString.SPAN_EXCLUSIVE_EXCLUSIVE);

        int stringIdName = R.string.labelFoodName;
        if(this.editMode && editFood != null){
            staticSelectors.add(new CreateFoodNutritionSelectorItem(stringIdName, new SpannableString(getString(stringIdName)), editFood.name, true, false));
            CreateFoodNutritionSelectorItem servingSize = new CreateFoodNutritionSelectorItem(db, stringIdEnergy, servingSizeSpan, 100, false, false);
            staticSelectors.add(servingSize);
            CreateFoodNutritionSelectorItem energyItemEdit = new CreateFoodNutritionSelectorItem(db, stringIdEnergy, energySpanString, editFood.energy, false, false);
            CreateFoodNutritionSelectorItem fiberItemEdit = new CreateFoodNutritionSelectorItem(db, stringIdFiber, fiberSpanString, editFood.fiber, false, false);
            staticSelectors.add(energyItemEdit);
            staticSelectors.add(fiberItemEdit);
        }else {
            staticSelectors.add(new CreateFoodNutritionSelectorItem(stringIdName, new SpannableString(getString(stringIdName)), true, false));
            staticSelectors.add(new CreateFoodNutritionSelectorItem(stringIdServingSize, servingSizeSpan, false, false));
            CreateFoodNutritionSelectorItem energyItem = new CreateFoodNutritionSelectorItem(stringIdEnergy, energySpanString, false, false);
            CreateFoodNutritionSelectorItem fiberItem = new CreateFoodNutritionSelectorItem(stringIdFiber, fiberSpanString, false, false);
            staticSelectors.add(energyItem);
            staticSelectors.add(fiberItem);
        }

        int stringIdCreateNutritionHeader = R.string.createFoodHeaderNutrients;
        staticSelectors.add(new CreateFoodNutritionSelectorItem(stringIdCreateNutritionHeader, new SpannableString(getString(stringIdCreateNutritionHeader)), true));

        ArrayList<CreateFoodNutritionSelectorItem> nutritionSelectors = new ArrayList<>();
        mainRv = view.findViewById(R.id.createFoodNewItem_rv);
        mainRv.addItemDecoration(new DividerItemDecoration(mainRv.getContext(), DividerItemDecoration.VERTICAL));
        for (NutritionElement ne : n.getElements().keySet()) {
            int stringIdNutritionElement = getStringIdentifier(getContext(), ne.toString());
            String neString = getResources().getString(stringIdNutritionElement);
            String noStrPortionType = Database.getNutrientNativeUnit(Integer.toString(Nutrition.databaseIdFromEnum(ne)));
            String portionType =  getResources().getString(getStringIdentifier(getContext(), noStrPortionType));
            int startPtString = neString.length() + 1;
            int endPtString = startPtString + 4 + portionType.length() - 1;
            Spannable neSpan = new SpannableString(neString + " in " + portionType);
            neSpan.setSpan(new RelativeSizeSpan(0.8f), startPtString, endPtString, SpannableString.SPAN_EXCLUSIVE_EXCLUSIVE);

            if(this.editMode){
                Integer presetAmount = n.getElements().get(ne);
                nutritionSelectors.add(new CreateFoodNutritionSelectorItem(db, stringIdNutritionElement, neSpan, Utils.zeroIfNull(presetAmount), false, false));
            }else{
                nutritionSelectors.add(new CreateFoodNutritionSelectorItem(db, stringIdNutritionElement, neSpan, 0, false, false));
            }
        }
        Collections.sort(nutritionSelectors);

        /* setup adapter */
        allItems.addAll(staticSelectors);
        allItems.addAll(nutritionSelectors);
        RecyclerView.Adapter<?> adapter = new CreateFoodNutritionSelectorAdapter(getContext(), allItems);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);
        mainRv.setLayoutManager(linearLayoutManager);
        mainRv.setAdapter(adapter);

        /* setup buttons */
        submit.setOnClickListener(v -> {
            Food f = collectData();
            if (f == null) {
                return;
            }
            if(this.editMode){
                f.id = editModeOrigFood.id;
                db.changeCustomFood(this.editModeOrigFood, f);
            }else {
                db.createNewFood(f, CREATE_NEW_ID);
            }
            Utils.navigate(CustomFoodFragment.class, (MainActivity)getActivity());
        });

        return view;
    }

    private Food collectData() {
        /* this function is sensitive to the correct ordering of the array list
           must be: name-> serving size -> everything else
         */
        Nutrition n = new Nutrition();
        Food f = new Food(null, null);
        f.nutrition = n;
        for (CreateFoodNutritionSelectorItem item : allItems) {
            if (item.ne != null) {
                if(item.amount == -1){
                    continue;
                }
                int servingSizeTmp = 1;
                if(this.servingSize != 0){
                    servingSizeTmp = servingSize;
                }
                f.nutrition.getElements().put(item.ne, item.amount / servingSizeTmp);
            } else {
                if(item.amount == -1){
                    item.amount = 0;
                }

                switch (item.stringID) {
                    case R.string.servingSizeInGramLabel:
                        this.servingSize = item.amount;  /* next level hack */
                        break;
                    case R.string.energyInKcalLabel:
                        f.energy = item.amount;
                        break;
                    case R.string.fiberInGramLabel:
                        f.fiber = item.amount;
                        break;
                    case R.string.labelFoodName:
                        if (item.data == null || item.data.equals("")) {
                            Toast toast = Toast.makeText(getContext(), "Name must be set.", Toast.LENGTH_LONG);
                            toast.show();
                            return null;
                        }else if (editModeOrigFood == null && db.checkCustomNameFoodExists(item.data)) {
                            Toast toast = Toast.makeText(getContext(), "A food with this name already exists.", Toast.LENGTH_LONG);
                            toast.show();
                            return null;
                        } else {
                            f.name = item.data;
                        }
                        break;
                }
            }
        }
        f.nutrition = n;
        return f;
    }
}
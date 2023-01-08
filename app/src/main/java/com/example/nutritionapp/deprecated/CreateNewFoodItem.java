//package com.example.nutritionapp.deprecated;
//
//import android.annotation.SuppressLint;
//import android.os.Bundle;
//import android.text.Spannable;
//import android.text.SpannableString;
//import android.text.style.RelativeSizeSpan;
//import android.widget.ImageButton;
//import android.widget.TextView;
//import android.widget.Toast;
//
//import androidx.appcompat.app.AppCompatActivity;
//import androidx.appcompat.widget.Toolbar;
//import androidx.recyclerview.widget.DividerItemDecoration;
//import androidx.recyclerview.widget.LinearLayoutManager;
//import androidx.recyclerview.widget.RecyclerView;
//
//import com.example.nutritionapp.R;
//import com.example.nutritionapp.customFoods.CreateFoodNutritionSelectorAdapter;
//import com.example.nutritionapp.customFoods.CreateFoodNutritionSelectorItem;
//import com.example.nutritionapp.other.ActivityExtraNames;
//import com.example.nutritionapp.other.Database;
//import com.example.nutritionapp.other.Food;
//import com.example.nutritionapp.other.Nutrition;
//import com.example.nutritionapp.other.NutritionElement;
//import com.example.nutritionapp.other.Utils;
//
//import java.util.ArrayList;
//import java.util.Collections;
//
//import static com.example.nutritionapp.other.Utils.getStringIdentifier;
//
//public class CreateNewFoodItem extends AppCompatActivity {
//    private static final int CREATE_NEW_ID = -1;
//    private final ArrayList<CreateFoodNutritionSelectorItem> allItems = new ArrayList<>();
//    private int servingSize;
//    private Database db;
//    private boolean editMode;
//    private Food editModeOrigFood;
//    RecyclerView mainRv;
//
//
//    public void onCreate(Bundle savedInstanceState) {
//        setTheme(R.style.AppTheme);
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.fragment_create_food_item);
//        db = new Database(this);
//
//        /* replace actionbar with custom app_toolbar */
//        Toolbar tb = findViewById(R.id.toolbar);
//        TextView tb_title = findViewById(R.id.toolbar_title);
//        ImageButton tb_back = findViewById(R.id.toolbar_back);
//        ImageButton submit = findViewById(R.id.toolbar_forward);
//
//        /* return  button */
//        tb_back.setOnClickListener((v -> finishAfterTransition()));
//        tb_back.setImageResource(R.drawable.ic_arrow_back_black_24dp);
//        tb.setTitle("");
//        tb_title.setText(R.string.customItemCreate);
//        submit.setImageResource(R.drawable.ic_done_black_24dp);
//        setSupportActionBar(tb);
//
//        Nutrition n;
//        Food editFood = null;
//        String fdc_id = this.getIntent().getStringExtra(ActivityExtraNames.FDC_ID);
//        if(fdc_id != null){
//            this.editMode = true;
//            editFood = db.getFoodById(fdc_id, null);
//            if(editFood == null){
//                throw new AssertionError("DB Return null for existing custom food id: " + fdc_id);
//            }
//            editModeOrigFood = editFood.deepclone();
//            n = editFood.nutrition;
//        }else{
//            n = new Nutrition();
//        }
//
//        /* add static inputs */
//        ArrayList<CreateFoodNutritionSelectorItem> staticSelectors = new ArrayList<>();
//        int stringID = R.string.generalFoodInformationHeader;
//        staticSelectors.add(new CreateFoodNutritionSelectorItem(stringID, new SpannableString(getString(stringID)), true));
//
//        /* Spannables */
//        int stringIdServingSize = R.string.servingSizeInGramLabel;
//        Spannable servingSizeSpan = new SpannableString(getString(stringIdServingSize));
//        servingSizeSpan.setSpan(new RelativeSizeSpan(0.8f), 13, 20, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
//
//        int stringIdEnergy = R.string.energyInKcalLabel;
//        SpannableString energySpanString = new SpannableString(getString(stringIdEnergy));
//        energySpanString.setSpan(new RelativeSizeSpan(0.8f), 7, 14, SpannableString.SPAN_EXCLUSIVE_EXCLUSIVE);
//
//        int stringIdFiber = R.string.fiberInGramLabel;
//        SpannableString fiberSpanString = new SpannableString(getString(stringIdFiber));
//        fiberSpanString.setSpan(new RelativeSizeSpan(0.8f), 6, 13, SpannableString.SPAN_EXCLUSIVE_EXCLUSIVE);
//
//        int stringIdName = R.string.labelFoodName;
//        if(this.editMode && editFood != null){
//            staticSelectors.add(new CreateFoodNutritionSelectorItem(stringIdName, new SpannableString(getString(stringIdName)), editFood.name, true, false));
//            CreateFoodNutritionSelectorItem servingSize = new CreateFoodNutritionSelectorItem(db, stringIdEnergy, servingSizeSpan, 100, false, false);
//            staticSelectors.add(servingSize);
//            CreateFoodNutritionSelectorItem energyItemEdit = new CreateFoodNutritionSelectorItem(db, stringIdEnergy, energySpanString, editFood.energy, false, false);
//            CreateFoodNutritionSelectorItem fiberItemEdit = new CreateFoodNutritionSelectorItem(db, stringIdFiber, fiberSpanString, editFood.fiber, false, false);
//            staticSelectors.add(energyItemEdit);
//            staticSelectors.add(fiberItemEdit);
//        }else {
//            staticSelectors.add(new CreateFoodNutritionSelectorItem(stringIdName, new SpannableString(getString(stringIdName)), true, false));
//            staticSelectors.add(new CreateFoodNutritionSelectorItem(stringIdServingSize, servingSizeSpan, false, false));
//            CreateFoodNutritionSelectorItem energyItem = new CreateFoodNutritionSelectorItem(stringIdEnergy, energySpanString, false, false);
//            CreateFoodNutritionSelectorItem fiberItem = new CreateFoodNutritionSelectorItem(stringIdFiber, fiberSpanString, false, false);
//            staticSelectors.add(energyItem);
//            staticSelectors.add(fiberItem);
//        }
//
//        int stringIdCreateNutritionHeader = R.string.createFoodHeaderNutrients;
//        staticSelectors.add(new CreateFoodNutritionSelectorItem(stringIdCreateNutritionHeader, new SpannableString(getString(stringIdCreateNutritionHeader)), true));
//
//        ArrayList<CreateFoodNutritionSelectorItem> nutritionSelectors = new ArrayList<>();
//        mainRv = findViewById(R.id.createFoodNewItem_rv);
//        mainRv.addItemDecoration(new DividerItemDecoration(mainRv.getContext(), DividerItemDecoration.VERTICAL));
//        for (NutritionElement ne : n.getElements().keySet()) {
//            int stringIdNutritionElement = getStringIdentifier(this, ne.toString());
//            String neString = getResources().getString(stringIdNutritionElement);
//            String noStrPortionType = Database.getNutrientNativeUnit(Integer.toString(Nutrition.databaseIdFromEnum(ne)));
//            String portionType =  getResources().getString(getStringIdentifier(this, noStrPortionType));
//            int startPtString = neString.length() + 1;
//            int endPtString = startPtString + 4 + portionType.length() - 1;
//            Spannable neSpan = new SpannableString(neString + " in " + portionType);
//            neSpan.setSpan(new RelativeSizeSpan(0.8f), startPtString, endPtString, SpannableString.SPAN_EXCLUSIVE_EXCLUSIVE);
//
//            if(this.editMode){
//                Integer presetAmount = n.getElements().get(ne);
//                nutritionSelectors.add(new CreateFoodNutritionSelectorItem(db, stringIdNutritionElement, neSpan, Utils.zeroIfNull(presetAmount), false, false));
//            }else{
//                nutritionSelectors.add(new CreateFoodNutritionSelectorItem(db, stringIdNutritionElement, neSpan, 0, false, false));
//            }
//        }
//        Collections.sort(nutritionSelectors);
//
//        /* setup adapter */
//        allItems.addAll(staticSelectors);
//        allItems.addAll(nutritionSelectors);
//        RecyclerView.Adapter<?> adapter = new CreateFoodNutritionSelectorAdapter(this, allItems);
//        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getApplicationContext(), LinearLayoutManager.VERTICAL, false);
//        mainRv.setLayoutManager(linearLayoutManager);
//        mainRv.setAdapter(adapter);
//
//        /* setup buttons */
//        submit.setOnClickListener(v -> {
//            Food f = collectData();
//            if (f == null) {
//                return;
//            }
//            if(this.editMode){
//                f.id = editModeOrigFood.id;
//                db.changeCustomFood(this.editModeOrigFood, f);
//            }else {
//                db.createNewFood(f, CREATE_NEW_ID);
//            }
//            finishAfterTransition();
//        });
//
//
//    }
//
//    @SuppressLint("NonConstantResourceId")
//    private Food collectData() {
//        /* this function is sensitive to the correct ordering of the array list
//           must be: name-> serving size -> everything else
//         */
//        Nutrition n = new Nutrition();
//        Food f = new Food(null, null);
//        f.nutrition = n;
//        for (CreateFoodNutritionSelectorItem item : allItems) {
//            if (item.ne != null) {
//                if(item.amount == -1){
//                    continue;
//                }
//                int servingSizeTmp = 1;
//                if(this.servingSize != 0){
//                    servingSizeTmp = servingSize;
//                }
//                f.nutrition.getElements().put(item.ne, item.amount / servingSizeTmp);
//            } else {
//                if(item.amount == -1){
//                    item.amount = 0;
//                }
//
//                switch (item.stringID) {
//                    case R.string.servingSizeInGramLabel:
//                        this.servingSize = item.amount;  /* next level hack */
//                        break;
//                    case R.string.energyInKcalLabel:
//                        f.energy = item.amount;
//                        break;
//                    case R.string.fiberInGramLabel:
//                        f.fiber = item.amount;
//                        break;
//                    case R.string.labelFoodName:
//                        if (item.data == null || item.data.equals("")) {
//                            Toast toast = Toast.makeText(getApplicationContext(), "Name must be set.", Toast.LENGTH_LONG);
//                            toast.show();
//                            return null;
//                        }else if (editModeOrigFood == null && db.checkCustomNameFoodExists(item.data)) {
//                            Toast toast = Toast.makeText(getApplicationContext(), "A food with this name already exists.", Toast.LENGTH_LONG);
//                            toast.show();
//                            return null;
//                        } else {
//                            f.name = item.data;
//                        }
//                        break;
//                }
//            }
//        }
//        f.nutrition = n;
//        return f;
//    }
//}

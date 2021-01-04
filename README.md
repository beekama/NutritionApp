# Class Overview
## other.Database
Database abstracts all Database Information into Constructs of Classes and returns simple configuration values. It also supports special functions for copying new Databases. Supported functions are:

### Initialize the Database
The Database must be instantiated first, every Activity should have it's own Database. Each instance of a Database has it's own connection.

    Database d = new Database(srcActivity)

### Log Foods
Log foods to journal. Either as a convenience function taking the an ArrayList of *SelectedFoodItems* or simply an ArrayList of *Foods*. It also takes a *LocalDateTime* Argument representing when the foods were consumed.

    logExistingFoods(ArrayList<SelectedFoodItem>, LocalDateTime)
    logExistingFoods(ArrayList<Food>, LocalDateTime)

To update an existing food group you can use:

    updateFoodGroup(ArrayList<SelectedFoodItem> updatedListWithAmounts, int groupId)

### Delete Logged Foods
Delete logged foods from the Database. The Food to be delete is identified by both, the Time and the Food-Id given. If There are multiple Foods logged at the same Dates, only given are deleted.

    deleteLoggedFood(ArrayList<Food> foods, LocalDateTime d)
    deleteLoggedFood(Food f, LocalDateTime d)

### Get All Foods in a time frame
Returns a HashMap with keys being group-ids (every logging event has an Id for all foods that were logged simultaneously) and ``ArrayList<Food>`` as value of all foods that were logged between the given start- and end date.

    HashMap<Integer, ArrayList<Food>> getLoggedFoodsByDate(LocalDate start, LocalDate end) {

### Nutrients for Food
Build a *Nutrition*-Class for a given Food-Id from the Database.

    getNutrientsForFood(String foodId)

### getFoodById
Build a *Food*-Class by it's ID from the supporting Database (not the journal where foods that were eaten are logged to). 
Takes a Utils.sqliteDatetimeFormat-formatted strings, if you want the "loggedAt"-Parameter of the Food-Class to be set, or *null* otherwise.

    getFoodById(String foodId, String loggedAt)

### getFoodByPartialName
Function that can be used to search for a Food in the supporting Database by it's name.

    getFoodsByPartialName(String substring)

### Suggestions for Food Combination
Function to create a list of Foods that are likely to be selected next, based on a given List of SelectedFoodItems. This function is currently only used and made to be used by the selector to add more Stuff in the Food-Journal.

    ArrayList<Food> getSuggestionsForCombination(ArrayList<SelectedFoodItem> selectedSoFarItems

### Custom Foods
#### Already Exists/Delete
While the primary key of all foods is 'fdc_id', custom foods must have a globally unique name (column 'description' in table 'food' of the DB). This functions therefore retrieves the food by it's name.

    checkCustomFoodExists(String description)
    deleteCustomFood(String description)

If a food is still in the journal, deleteCustomFood will deactivate it, removing it from the list of custom foods and from the food selection process. For this process the helper functions *markFoodAsDeactivated(int fdcId)* and *checkFoodInJournal(fdcId) exist.

#### Create
Function to create completely new foods from the CreateFood activity, not log them the journal.
This function retrieves the highest used Id, of a food, of the type **"app_custom"** from the *'food'*-table, or uses the variable *DEFAULT_MIN_CUSTOM_ID* if no such food exists yet.

Nutrition Information of the food are written to the *nutrition_custom* table, after a Food has been accessed it can be used as any other food in the Database and identified by the *food/data_type* database column.

This function should only invoked after checking if the food (a food with the same name/description, which is the same thing here) already exists.

    /* food must have valid nutrition information associated with it  */
    /* food id and associatedAmount are ignored in this function      */
    createFood(Food f)

### Configuration Setters/Getters
Simple Getter and Setter for basic Values that may or may not be actually be saved in the Database itself. All of the Setters may throw an *IllegalArgumentException*.

    public void setPersonAge(int age)
    public void setPersonEnergyReq(int energyReq)
    public void setPersonHeight(int sizeInCm)
    public void setPersonGender(String gender) // "male" or "female"

    public int getPersonWeight()
    public int getPersonHeight()
    public int getPersonAge()
    public int getPersonEnergyReq()
    public String getPersonGender()

# Food Journal Structure

    FoodJournalOverview
    --- ListView mainListOfFoodsWithDayHeaders
    --- --- FoodOverviewAdapter
    --- --- --- TextView Date -> clickListener -> NutritionOverview (with this day as time frame)
    --- --- --- TextView Energy
    --- --- --- ListView subList
    --- --- --- --- GroupListAdapter
    --- --- --- --- --- TextView foods -> clickListener -> AddFoodToJournal (as edit)

# Nutrition Analysis
The *NutritionAnalysis* class provides analysis based on a List of Foods and the users personal settings. Afters instantiation, it provides to following functions:

    getTotalEnergy() /* convenience function to get the total energy */
    Nutrition getNutritionMissing() /* get the absolute nutrition missing (per day) */
    Nutrition getNutritionActual()  /* get the total amount of nutrition collected */
    HashMap<NutritionElement, Float> getNutritionPercentage() /* calculate .XX float values from the above information */
    ArrayList<NutritionPercentageTuple> getNutritionPercentageSorted() /* get the above information sorted */
    ArrayList<NutritionPercentageTuple> getNutritionPercentageSortedFilterZero() /* get the above information without zero-values */

# Conversions
All conversions (milligram, microgram, joule, kcal, etc..) are handled in static functions in *other.Conversions*. Only the following units should be persisted in fields of instantiated object:

    microgram (UG) for weights
    joule for energy

The static function *normalize(String unitName, int inputAmount)* should be used to obtain those values.

# Handling Date
All date(-time) formats are listed as public static fields in *other.Utils*, new formats should only be added when absolutely necessary. The dates can be parsed as follows:

    LocalDate d = LocalDate.parse(other.Utils.sqliteDateFormat, STRING);
    LocalDateTime dt = LocalDate.parse(other.Utils.sqliteDatetimeFormat, STRING);
    LocalDateTime dt = LocalDate.parse(other.Utils.sqliteDateZeroPaddedFormat, STRING);

The last one basically replacing the hours, minutes and sections values with zeros to get the start of the day. Formatting works analogous:

    String formatted = dt.format(other.Utils.something);

## Building the Database
Goto the [USDA Website](https://fdc.nal.usda.gov/download-datasets.html), download the **FNDDS** data set and the **Supporting data for Downloads** and unpack it into the *"helper\_scripts"*-directory. Then execute the ``make_db.py``-script. (sqlite3 + python3 is required for this).

Alternatively, download the [prebuilt database](https://media.atlantishq.de/food.db).

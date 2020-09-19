# Class Overview
## other.Database
Databse abstracts all Database Information into Constructs of Classes and returns simple configuration values. It also supports special functions for copying new Databases. Supported functions are:

### Initialize the Database
The Database must be instantiated first, every Activity should have it's own Database. Each instance of a Database has it's own connection.

    Database d = new Database(srcActivity)

### Log Foods
Log foods to journal. Either as a convinience function taking the an ArrayList of *SelectedFoodItems* or simply an ArrayList of *Foods*. It also takes a *LocalDateTime* Arguement representing when the foods were consumed.

    logExistingFoods(ArrayList<SelectedFoodItem>, LocalDateTime)
    logExistingFoods(ArrayList<Food>, LocalDateTime)

### Delete Logged Foods
Delete logged foods from the Database. The Food to be delete is identified by both, the Time and the Food-Id given. If There are multiple Foods logged at the same Dates, only given are deleted.

    deleteLoggedFood(ArrayList<Food> foods, LocalDateTime d)
    deleteLoggedFood(Food f, LocalDateTime d)

### Get All Foods in a Timeframe
Returns a HashMap with keys being group-ids (every logging event has an Id for all foods that were logged simultaneously) and ``ArrayList<Food>`` as value of all foods that were logged between the given start- and enddate.

    HashMap<Integer, ArrayList<Food>> getLoggedFoodsByDate(LocalDate start, LocalDate end) {

### Nutrients for Food
Build a *Nutrition*-Class for a given Food-Id from the Database.

    getNutrientsForFood(String foodId)

### getFoodById
Build a *Food*-Class by it's ID from the supporting Database (not the journal where foods that were eaten are logged to). 
Takes a Utils.sqliteDatetimeFormat-formated strings, if you want the "loggedAt"-Parameter of the Food-Class to be set, or *null* otherwise.

    getFoodById(String foodId, String loggedAt)

### getFoodByPartialName
Function that can be used to search for a Food in the supporting Database by it's name.

    getFoodsByPartialName(String substring)

### Suggestions for Food Combination
Function to create a list of Foods that are likely to be selected next, based on a given List of SelectedFoodItems. This function is currently only used and made to be used by the selector to add more Stuff in the Food-Journal.

    ArrayList<Food> getSuggestionsForCombination(ArrayList<SelectedFoodItem> selectedSoFarItems

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
    --- --- FoodOVerviewAdapter
    --- --- --- TextView Date -> clickListener -> NutritionOverview (with this day as timeframe)
    --- --- --- TextView Energy
    --- --- --- ListView subList
    --- --- --- --- GroupListApapter
    --- --- --- --- --- TextView foods -> clickListener -> AddFoodToJournal (as edit)
    


## Building the Database
Goto the [USDA Website](https://fdc.nal.usda.gov/download-datasets.html), download the **FNDDS** dataset and the **Suporting data for Downloads** and unpack it into the *"helper\_scripts"*-directory. Then execute the ``make_db.py``-script. (sqlite3 + python3 is required for this).

Alternatively, download the [prebuilt database](https://media.atlantishq.de/food.db).

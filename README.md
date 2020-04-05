## Building the Database
Goto the [USDA Website](https://fdc.nal.usda.gov/download-datasets.html), download the **FNDDS** dataset and the **Suporting data for Downloads** and unpack it. Switch to the directory containing the CSV-Files of **BOTH** archives, then create the database with:

    cut -d, -f1,2,3,4 < food_nutrient.csv > food_nutrient_cut.csv
    split -l 56485 -d food_nutrient_cut.csv food_nutrient_cut.csv
    sqlite3 -init $PROJECT_ROOT/sqlite3.init $PROJECT_ROOT/app/src/main/res/raw/food.db

Alternatively, download the [prebuilt database](https://media.atlantishq.de/food.db).

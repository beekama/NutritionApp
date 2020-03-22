## Build the Database
This asumes your sqlite binary is called *sqlite3*
- goto the [https://fdc.nal.usda.gov/download-datasets.html](USDA Website) and download the **FNDDS** dataset
- unpack the dataset and navigate to directory containing the unpacked file

    sqlite3 -init $PROJECT_ROOT/sqlite3.init $PROJECT_ROOT/app/src/main/res/raw/food.db
    <CTRL-D>

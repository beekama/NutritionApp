#!/usr/bin/python3
import os

SRC_FILE = "food_nutrient.csv"
TMP_FILE = "food_nutrient_cut.csv"
DB_INIT_FILE = "sqlite3.init"
DB_INIT_FILE_NEW = "sqlite3-full.init"

# only keep "id","fdc_id","nutrient_id","amount"
os.system("rm food_nutrient_*")
os.system("cut -d, -f1,2,3,4 < {} > {}".format(SRC_FILE, TMP_FILE))

# split into equal parts
MAX_LENGHT = 50000
count = 0
PARTIAL_DB_FILE_NAME = "food_nutrient_{0:02d}.csv"
SCHEMA = '''"id","fdc_id","nutrient_id","amount"\n'''

with open(TMP_FILE) as f:
    for line in f:
        curFileName = PARTIAL_DB_FILE_NAME.format(int(count / MAX_LENGHT))
        if os.path.isfile(curFileName) or count == 0:
            with open(curFileName, "a") as fout:
                fout.write(line)
        else:
            with open(curFileName, "w") as fout:
                fout.write(SCHEMA)
                fout.write(line)

        count += 1

with open(DB_INIT_FILE) as f:
    with open(DB_INIT_FILE_NEW, "w") as fout:
        fout.write(f.read())
        for x in range(0, int(count / MAX_LENGHT) + 1):
            fout.write(".import food_nutrient_{0:02d}.csv food_nutrient_{0:02d}\n".format(x))

DB_TARGET_PATH = "food.db"
os.system("rm {}".format(DB_TARGET_PATH))
os.system("sqlite3 -init {} {} <<EOF".format(DB_INIT_FILE_NEW, DB_TARGET_PATH))

DB_TARGET_PATH = "../app/src/main/res/raw/food.db"
os.system("rm {}".format(DB_TARGET_PATH))
os.system("sqlite3 -init {} {} <<EOF".format(DB_INIT_FILE_NEW, DB_TARGET_PATH))

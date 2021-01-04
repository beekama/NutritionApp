#!/usr/bin/python3
import os
import sqlite3
import subprocess
import argparse

SRC_FILE = "food_nutrient.csv"
TMP_FILE = "food_nutrient_cut.csv"

DB_INIT_FILE = "sqlite3.init"
DB_INIT_FILE_NEW = "sqlite3-full.init"

FOOD_MAIN_SRC = "food.csv"
FOOD_MAIN_SRC_FILTERED = "food_filtered.csv"
FOOD_MAIN_SRC_FILTERED_DE = "food_filtered_de.csv"

DB_TARGET_PATH = "food.db"
DB_TARGET_PATH_DE = "food_de.db"

MAX_LENGHT = 50000
PARTIAL_DB_FILE_NAME = "food_nutrient_{0:02d}.csv"
SCHEMA = '''"id","fdc_id","nutrient_id","amount"\n'''

REMOVE_PHRASES = [", NFS", ", raw"]

def clean():
    '''Remove all generated files,
        only keep "id","fdc_id","nutrient_id","amount"'''
    os.system("rm food_nutrient_*")
    os.system("cut -d, -f1,2,3,4 < {} > {}".format(SRC_FILE, TMP_FILE))

def filterAndReplace():
    '''Filter CSV files for filter and word removal list'''

    print("Read filter..")
    FILTER_WORDS = dict()
    with open("filter_words.txt", "r") as f:
        for l in f:
            FILTER_WORDS.update({ l.strip() : None })

    print("Run filter..")
    with open(FOOD_MAIN_SRC) as f:
        with open(FOOD_MAIN_SRC_FILTERED, "w") as fout:
            for l in f:
                if any([ w.strip() in FILTER_WORDS for w in l.split(" ")]):
                    continue
                else:
                    lclean = l
                    for wRemove in  REMOVE_PHRASES:
                        lclean = lclean.replace(wRemove, "")
                    fout.write(lclean)

def split():
    '''Split CSV files into usable chunks'''

    count = 0
    print("Run file splitter...")
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
    return count

def createDB(count):
    '''Generate Init file and recreate database'''

    print("Generate init file...")
    with open(DB_INIT_FILE) as f:
        with open(DB_INIT_FILE_NEW, "w") as fout:
            fout.write(f.read())
            for x in range(0, int(count / MAX_LENGHT) + 1):
                fout.write(".import food_nutrient_{0:02d}.csv food_nutrient_{0:02d}\n".format(x))

    os.system("rm {}".format(DB_TARGET_PATH))
    os.system("sqlite3 -init {} {} <<EOF".format(DB_INIT_FILE_NEW, DB_TARGET_PATH))

    os.system("rm {}".format(DB_TARGET_PATH))
    os.system("sqlite3 -init {} {} <<EOF".format(DB_INIT_FILE_NEW, DB_TARGET_PATH))

if __name__ == "__main__":
    parser = argparse.ArgumentParser(description='Database Generator for nutrition app')

    parser.add_argument("--clean", action="store_const", default=False, const=True,
                            help="Remove all cached/temp files")
    parser.add_argument("--recreate-db", action="store_const", default=False, const=True,
                            help="Recreate Database init files and database")

    args = parser.parse_args()
    if args.clean:
        clean()
    if args.recreate_db:
        filterAndReplace()
        count = split()
        createDB(count)

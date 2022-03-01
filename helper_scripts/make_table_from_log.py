#!/usr/bin/python3
import sqlite3
import argparse
from csv import reader,writer

DB = "food.db"
TABLE_FORMAT = "localization_{}"

def logToDB(language):
    conn = sqlite3.connect(DB)
    c = conn.cursor()

    table = TABLE_FORMAT.format(language)

    # check if table exists create if not #
    exists = False
    rows = c.execute("SELECT name FROM sqlite_master WHERE type='table' AND NAME = ?;", (table,))
    for r in rows:
        exists = True
        break
    if not exists:
        c.execute('''CREATE TABLE {} (
                        "fdc_id" TEXT PRIMARY KEY,
                        "description" TEXT
                        );'''.format(table))
    
    # copy .log-file entries to table
    with open("localization_log_{}.log".format(language), "r") as f:
        for line in f:

            linesplit = line.split('|')
            fdcId = linesplit[0]
            translation = linesplit[2]
        
            print("inserting : ? , ? into {}".format(table), (fdcId, translation))
            c.execute("INSERT INTO {} VALUES(?,?);".format(table), (fdcId, translation))
            conn.commit()
        conn.commit()


def sortCSV(csvFile):
    # Read and sort file
    with open(csvFile, 'r+') as f:
        r = f.readlines()
        sortedList = sorted(r, key=lambda x: x.split("|")[0], reverse=False)
        f.seek(0)
        f.truncate()
        for line in sortedList:
            f.write(line)

    
if __name__ == "__main__":
    parser = argparse.ArgumentParser(description="DB localization.log as database table")

    parser.add_argument("--lang", required=True, type=str, help="Language to include or update")
    parser.add_argument("--addWithoutSort", action="store_const", default=False, const=True)
    parser.add_argument("--sortOnly", action="store_const", default=False, const=True)
    parser.add_argument("--addToDB", action="store_const", default=False, const=True,
                            help="Sort and add LANG.log as table to dbiasdf")

    args = parser.parse_args()

    if args.sortOnly:
        sortCSV("localization_log_{}.log".format(args.lang))
    if args.addToDB:
        sortCSV("localization_log_{}.log".format(args.lang))
        logToDB(args.lang)
    if args.addWithoutSort:
        logToDB(args.lang)

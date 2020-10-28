#!/usr/bin/python3
import sqlite3
from google.cloud import translate_v2 as translate
import argparse

DB = "food.db"
TABLE_FORMAT = "localization_{}"


def runTranslate(limit, offset, language):

    print("Run translation.. (this may take a long time")
    conn = sqlite3.connect(DB)
    c = conn.cursor()

    table = TABLE_FORMAT.format(language)

    # check table exists create if not #
    exists = False
    rows = c.execute("SELECT name FROM sqlite_master WHERE type='table' AND name = ?;", (table,))
    for r in rows:
        exists = True
        break;
    if not exists:
        c.execute('''CREATE TABLE {} (
                        "fdc_id" TEXT PRIMARY KEY,
                        "description" TEXT
                        );'''.format(table))

    # retrive
    limitStr = ""
    offsetStr = ""
    if limit:
        limitStr = "LIMIT {}".format(limit)
    if offset:
        offsetStr = "OFFSET {}".format(offset)

    stmt = "SELECT fdc_id, description FROM food {} {};".format(limitStr, offsetStr)
    rows = c.execute(stmt)

    insertCursor = conn.cursor()
    translateClient = translate.Client()
    for r in rows:
        fdcId, description = r
        # TODO check exists insertCursor.execute("SELECT")
        translation = gApiTranslate(description, language, translateClient)
        insertCursor.execute("INSERT INTO {} VALUES(?,?);".format(table), (fdcId, translation))
        print("Translating: {} ( {} -> {} )".format(fdcId, description, translation))
        conn.commit()

    conn.commit()

def gApiTranslate(text, language, translateClient):
    
    result = translateClient.translate(text, target_language=language)
    return result["translatedText"]
 

if __name__ == "__main__":
    parser = argparse.ArgumentParser(description='DB localization generator for nutrition app')

    parser.add_argument("--limit", type=int)
    parser.add_argument("--offset", type=int)
    parser.add_argument("--lang", required=True, type=str, help="Language to translate to")

    args = parser.parse_args()
    runTranslate(args.limit, args.offset, args.lang)


package com.ninetwozero.assignment4.misc;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteStatement;
import android.text.TextUtils;

public class SQLiteManager {

    class OpenHelper extends SQLiteOpenHelper {

        OpenHelper(Context context) {

            super(context, DATABASE_NAME, null, DATABASE_VERSION);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {

            // Let's create the table
            db.execSQL(
                    "CREATE TABLE "
                            + Constants.SQLITE_TABLE + " ("
                            + "_id INTEGER PRIMARY KEY, "
                            + Constants.SQLITE_FIELD_NAME + " STRING, "
                            + Constants.SQLITE_FIELD_TIME + " INTEGER"
                            + ")"
                    );

        }

        @Override
        public void onUpgrade(SQLiteDatabase arg0, int arg1, int arg2) {
        }

    }

    private static final String DATABASE_NAME = "app.db";
    private static final int DATABASE_VERSION = 1;
    private Context CONTEXT;
    private SQLiteDatabase DB;
    private SQLiteStatement STATEMENT;

    public SQLiteManager(Context context) {

        // Set the context
        CONTEXT = context;

        // Initialize an "OpenHelper"
        OpenHelper openHelper = new OpenHelper(CONTEXT);

        // Set the DB as writeAble
        DB = openHelper.getWritableDatabase();

    }

    public final void close() {

        if (STATEMENT != null)
            STATEMENT.close();
        if (DB != null)
            DB.close();
        return;

    }

    public long insert(String table, String[] fields, String[] values) throws DatabaseException {

        // Let's validate the table
        if (table == null || table.equals(""))
            throw new DatabaseException("No table selected.");

        // Get the number of fields and values
        int countFields = fields.length;
        int countValues = values.length;

        String stringFields = "", stringValues = "";

        // Validate the number, ie 6 fields should have 6^(n rows) values
        if (countValues % countFields != 0) {

            throw new DatabaseException("Database mismatch - numFields <> numValues.");

        } else {

            if (countFields == 0) {

                throw new DatabaseException("Storage failed - no fields found.");

            } else if (countValues == 0) {

                throw new DatabaseException("Storage failed - no values found.");

            } else {

                // Append the fields
                stringFields = TextUtils.join(",", fields);

                // Let's bind the parameters
                for (int j = 0; j < countValues; j++) {
                    stringValues += (j > 0) ? ", ?" : "?";
                }

            }

        }

        STATEMENT = DB.compileStatement(
                "INSERT INTO " + table +
                        "( " + stringFields + ") VALUES " + "(" + stringValues + ")"
                );

        // Let's bind the parameters
        for (int j = 1; j <= countValues; j++) {
            STATEMENT.bindString(j, values[j - 1]);
        }

        // STATEMENT.bindString( 1, name );
        return STATEMENT.executeInsert();
    }

    public final Cursor query(String t, String[] p, String s, String[] sA,
            String g, String h, String o) throws DatabaseException {

        // Let's validate the table
        if (t == null || t.equals(""))
            throw new DatabaseException("No table selected.");
        if (p == null || p.length == 0)
            p = new String[] {
                    "*"
            };

        // Let's return the query
        return DB.query(t, p, s, sA, g, h, o);

    }

    public Cursor selectAll(String table) throws DatabaseException {

        // Let's validate the table
        if (table == null || table.equals(""))
            throw new DatabaseException("No table selected.");

        // We need to select a table
        return DB.query(table, new String[] {
                "*"
        }, null, null, null, null, Constants.SQLITE_FIELD_TIME + " ASC");

    }

}

package lk.dulanjaya.medinote.model;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

public class SqLiteHelper extends SQLiteOpenHelper {
    public static final String DATABASE_NAME = "mednt.db";
    public static final int DATABASE_VERSION = 3;

    public SqLiteHelper(@Nullable Context context, @Nullable String name, @Nullable SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL("CREATE TABLE user (\n" +
                "    id          INTEGER PRIMARY KEY AUTOINCREMENT,\n" +
                "    firstName   TEXT    NOT NULL,\n" +
                "    lastName    TEXT    NOT NULL,\n" +
                "    gender      TEXT    NOT NULL,\n" +
                "    dateOfBirth TEXT    NOT NULL,\n" +
                "    mobile      TEXT    NOT NULL,\n" +
                "    password    TEXT    NOT NULL\n" +
                ");\n");

        sqLiteDatabase.execSQL("CREATE TABLE measurement (\n" +
                "    id         INTEGER PRIMARY KEY AUTOINCREMENT,\n" +
                "    sysValue   TEXT    NOT NULL,\n" +
                "    pulValue   TEXT    NOT NULL,\n" +
                "    diaValue   TEXT    NOT NULL,\n" +
                "    userMobile TEXT    NOT NULL,\n" +
                "    dateTime   TEXT    NOT NULL\n" +
                ");");

        sqLiteDatabase.execSQL("CREATE TABLE superUser (\n" +
                "    id          INTEGER PRIMARY KEY AUTOINCREMENT,\n" +
                "    firstName   TEXT    NOT NULL,\n" +
                "    lastName    TEXT    NOT NULL,\n" +
                "    gender      TEXT    NOT NULL,\n" +
                "    dateOfBirth TEXT    NOT NULL,\n" +
                "    mobile      TEXT    NOT NULL\n" +
                ");\n");
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

    }
}

package pedro.geo.ffit.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by pedro on 21/01/15.
 */
public class DatabaseFactory extends SQLiteOpenHelper {

    public static final int VERSION = 1;
    public static final String NAME = "geomaps.db";

    public DatabaseFactory(Context context) {
        super(context, NAME, null, VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        db.execSQL("CREATE TABLE IF NOT EXISTS Favorites (" +
                "_id INTEGER PRIMARY KEY," +
                "title TEXT NOT NULL," +
                "description TEXT," +
                "latitude REAL," +
                "longitude REAL);");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS Favorites");
        onCreate(db);
    }

    public static class Favorites {

        public static String TABLE = "Favorites";
        public static String ID = "_id";
        public static String TITLE = "title";
        public static String DESCRIPTION = "description";
        public static String LATITUDE = "latitude";
        public static String LONGITUDE = "longitude";
        public static String[] COLUMNS = new String[]{ID, TITLE, DESCRIPTION, LATITUDE, LONGITUDE};
    }
}

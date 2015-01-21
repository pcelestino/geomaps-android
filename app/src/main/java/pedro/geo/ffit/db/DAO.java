package pedro.geo.ffit.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.List;

import pedro.geo.ffit.model.Favorite;

/**
 * Created by pedro on 21/01/15.
 */
public class DAO {

    private static DAO instance;
    private DatabaseFactory databaseFactory;
    private SQLiteDatabase database;

    public DAO(Context context) {
        super();
        this.databaseFactory = new DatabaseFactory(context);
        this.database = this.databaseFactory.getWritableDatabase();
    }

    public synchronized static DAO open(Context context) {
        if (instance == null) {
            instance = new DAO(context);
        }
        return instance;
    }

    public void close() {
        if (database != null && database.isOpen()) {
            database.close();
            database = null;
        }
        if (databaseFactory != null) {
            databaseFactory.close();
            databaseFactory = null;
        }
        instance = null;
    }

    public long insert(Favorite favorite) {
        ContentValues contentValues = new ContentValues();
        contentValues.put(DatabaseFactory.Favorites.ID, favorite.getId());
        contentValues.put(DatabaseFactory.Favorites.TITLE, favorite.getTitle());
        contentValues.put(DatabaseFactory.Favorites.DESCRIPTION, favorite.getDescription());
        contentValues.put(DatabaseFactory.Favorites.LATITUDE, favorite.getLatitude());
        contentValues.put(DatabaseFactory.Favorites.LONGITUDE, favorite.getLongitude());
        long rowId = database.insert(DatabaseFactory.Favorites.TABLE, null, contentValues);
        close();
        return rowId;
    }

    public long update(Favorite favorite) {
        ContentValues contentValues = new ContentValues();
        contentValues.put(DatabaseFactory.Favorites.ID, favorite.getId());
        contentValues.put(DatabaseFactory.Favorites.TITLE, favorite.getTitle());
        contentValues.put(DatabaseFactory.Favorites.DESCRIPTION, favorite.getDescription());
        contentValues.put(DatabaseFactory.Favorites.LATITUDE, favorite.getLatitude());
        contentValues.put(DatabaseFactory.Favorites.LONGITUDE, favorite.getLongitude());
        long rowId = database.update(DatabaseFactory.Favorites.TABLE, contentValues,
                DatabaseFactory.Favorites.ID + " = ?", new String[]{favorite.getId()});
        close();
        return rowId;
    }

    public boolean delete(Favorite favorite) {
        int removed = database.delete(DatabaseFactory.Favorites.TABLE,
                DatabaseFactory.Favorites.ID + " = ?", new String[]{favorite.getId()});
        close();
        return removed > 0;
    }

    public Favorite findFavoriteById(String id) {
        Cursor cursor = database.query(DatabaseFactory.Favorites.TABLE,
                DatabaseFactory.Favorites.COLUMNS,
                DatabaseFactory.Favorites.ID + " = ?",
                new String[]{id}, null, null, null);

        if (cursor.moveToNext()) {
            Favorite favorite = createFavorite(cursor);
            cursor.close();
            close();
            return favorite;
        }
        return null;
    }

    public List<Favorite> getListFavorites() {
        Cursor cursor = database.query(DatabaseFactory.Favorites.TABLE,
                DatabaseFactory.Favorites.COLUMNS,
                null, null, null, null, null);

        List<Favorite> favorites = new ArrayList<>();

        while (cursor.moveToNext()) {
            Favorite favorite = createFavorite(cursor);
            favorites.add(favorite);
        }
        cursor.close();
        close();
        return favorites;
    }

    private Favorite createFavorite(Cursor cursor) {
        Favorite favorite = new Favorite();
        favorite.setId(cursor.getString(cursor.getColumnIndex(DatabaseFactory.Favorites.ID)));
        favorite.setTitle(cursor.getString(cursor.getColumnIndex(DatabaseFactory.Favorites.TITLE)));
        favorite.setDescription(cursor.getString(cursor.getColumnIndex(DatabaseFactory.Favorites.DESCRIPTION)));
        favorite.setLatitude(cursor.getDouble(cursor.getColumnIndex(DatabaseFactory.Favorites.LATITUDE)));
        favorite.setLongitude(cursor.getDouble(cursor.getColumnIndex(DatabaseFactory.Favorites.LONGITUDE)));
        return favorite;
    }
}

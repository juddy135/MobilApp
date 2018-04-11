package co.com.ies.fidelizacioncliente.dao;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.text.TextUtils;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import co.com.ies.fidelizacioncliente.database.DBConstants.Bar;
import co.com.ies.fidelizacioncliente.database.DBConstants.Cassinos;
import co.com.ies.fidelizacioncliente.database.DBConstants.Machines;
import co.com.ies.fidelizacioncliente.entity.BarItem;
import co.com.ies.fidelizacioncliente.entity.GenericItem;

/**
 * Clase encargada de realizar operaciones en la bd local
 */
public class DaoSqliteStandard {

    public ArrayList<GenericItem> getAllCassinos(SQLiteDatabase sqLiteDatabase) {
        ArrayList<GenericItem> arrayList = new ArrayList<>();

        Cursor cursor = sqLiteDatabase.query(Cassinos.TABLE, null, null, null, null, null, null);

        if (cursor.moveToFirst()) {
            do {
                arrayList.add(new GenericItem(cursor.getString(cursor.getColumnIndex(Cassinos.ID_)),
                        cursor.getString(cursor.getColumnIndex(Cassinos.NAME))));

            } while (cursor.moveToNext());
        }
        cursor.close();
        return arrayList;
    }

    public void insertCasino(SQLiteDatabase sqLiteDatabase, ContentValues contentValues) {
        sqLiteDatabase.insertWithOnConflict(Cassinos.TABLE, null, contentValues, SQLiteDatabase.CONFLICT_REPLACE);
    }

    public void deleteAllCasino(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.delete(Cassinos.TABLE, null, null);
    }

    public ArrayList<GenericItem> getGenericMachineByCassino(SQLiteDatabase sqLiteDatabase, String idCassino) {
        ArrayList<GenericItem> arrayList = new ArrayList<>();

        Cursor cursor = sqLiteDatabase.query(Machines.TABLE,
                new String[]{Machines.NUM_DISP, Machines.SERIAL},
                Machines.ID_CASSINO + "=?",
                new String[]{idCassino}, null, null, null);

        if (cursor.moveToFirst()) {
            do {
                arrayList.add(
                        new GenericItem(cursor.getString(cursor.getColumnIndex(Machines.SERIAL)),
                                cursor.getString(cursor.getColumnIndex(Machines.NUM_DISP))));

            } while (cursor.moveToNext());
        }

        cursor.close();

        return arrayList;
    }

    public ArrayList<String> getItemCategories(SQLiteDatabase sqLiteDatabase) {
        ArrayList<String> arrayList = new ArrayList<>();

        Cursor cursor = sqLiteDatabase.query(true, Bar.TABLE,
                new String[]{Bar.CATEGORY},
                null, null, null, null, null, null);

        if (cursor.moveToFirst()) {
            do {
                arrayList.add(cursor.isNull(0) ? "" : cursor.getString(0).toUpperCase());
            } while (cursor.moveToNext());
        }
        cursor.close();
        return arrayList;
    }

    public void insertMachine(SQLiteDatabase sqLiteDatabase, ContentValues contentValues) {
        sqLiteDatabase.insertWithOnConflict(Machines.TABLE, null, contentValues, SQLiteDatabase.CONFLICT_REPLACE);
    }

    public void deleteAllMachines(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.delete(Machines.TABLE, null, null);
    }

    public void insertBarItem(SQLiteDatabase sqLiteDatabase, ContentValues contentValues) {

        sqLiteDatabase.insertWithOnConflict(Bar.TABLE, null, contentValues, SQLiteDatabase.CONFLICT_REPLACE);

    }

    public boolean existBarItems(SQLiteDatabase sqLiteDatabase) {

        Cursor cursor = sqLiteDatabase.query(Bar.TABLE, null, null, null, null, null, null);
        boolean result = cursor.moveToFirst();
        cursor.close();
        return result;
    }

    public void getBarItemDetails(SQLiteDatabase sqLiteDatabase, BarItem barItem) {

        Cursor cursor = sqLiteDatabase.query(Bar.TABLE,
                new String[]{Bar.NAME, Bar.PRICE, Bar.POINTS, Bar.PATH_THUMBNAIL},
                Bar.ID_ + "=?",
                new String[]{barItem.getId()}, null, null, null);

        if (cursor.moveToFirst()) {

            if (!cursor.isNull(cursor.getColumnIndex(Bar.NAME))) {
                barItem.setName(cursor.getString(cursor.getColumnIndex(Bar.NAME)));
            }

            if (!cursor.isNull(cursor.getColumnIndex(Bar.POINTS))) {
                barItem.setPoints(cursor.getString(cursor.getColumnIndex(Bar.POINTS)));
            }

            if (!cursor.isNull(cursor.getColumnIndex(Bar.PRICE))) {
                barItem.setPrice(cursor.getString(cursor.getColumnIndex(Bar.PRICE)));
            }

            if (!cursor.isNull(cursor.getColumnIndex(Bar.PATH_THUMBNAIL))) {
                barItem.setAbsoluthPathThumbnail(cursor.getString(cursor.getColumnIndex(Bar.PATH_THUMBNAIL)));
            }

            if (!cursor.isNull(cursor.getColumnIndex(Bar.CATEGORY))) {
                barItem.setCategory(cursor.getString(cursor.getColumnIndex(Bar.CATEGORY)));
            }
        }
        cursor.close();

    }

    public void deleteAllBarItems(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.delete(Bar.TABLE, null, null);
    }

    public void getBarItemDetails(SQLiteDatabase sqLiteDatabase, List<BarItem> listItems) {

        if (listItems.isEmpty())
            return;

        List<String> listId = new ArrayList<>();
        int sizeItems = listItems.size();
        String[] placeHolders = new String[sizeItems];
        for (int i = 0; i < sizeItems; i++) {
            listId.add(listItems.get(i).getId());
            placeHolders[i] = "?";
        }

        String place = TextUtils.join(", ", placeHolders);
        String[] param = listId.toArray(new String[0]);
        Cursor cursor = sqLiteDatabase.query(Bar.TABLE,
                new String[]{Bar.ID_, Bar.NAME, Bar.PRICE, Bar.POINTS, Bar.PATH_THUMBNAIL, Bar.CATEGORY},
                Bar.ID_ + " IN (" + place + ")",
                param, null, null, null);

        if (cursor.moveToFirst()) {
            do {
                for (BarItem barItem : listItems) {

                    String id = cursor.getString(cursor.getColumnIndex(Bar.ID_));

                    if (barItem.getId().equals(id)) {


                        if (!cursor.isNull(cursor.getColumnIndex(Bar.NAME))) {
                            barItem.setName(cursor.getString(cursor.getColumnIndex(Bar.NAME)));
                        }

                        if (!cursor.isNull(cursor.getColumnIndex(Bar.POINTS))) {
                            barItem.setPoints(cursor.getString(cursor.getColumnIndex(Bar.POINTS)));
                        }

                        if (!cursor.isNull(cursor.getColumnIndex(Bar.PRICE))) {
                            barItem.setPrice(cursor.getString(cursor.getColumnIndex(Bar.PRICE)));
                        }

                        if (!cursor.isNull(cursor.getColumnIndex(Bar.PATH_THUMBNAIL))) {
                            barItem.setAbsoluthPathThumbnail(cursor.getString(cursor.getColumnIndex(Bar.PATH_THUMBNAIL)));
                        }

                        if (!cursor.isNull(cursor.getColumnIndex(Bar.CATEGORY))) {
                            barItem.setCategory(cursor.getString(cursor.getColumnIndex(Bar.CATEGORY)));
                        }
                    }

                }
            } while (cursor.moveToNext());
        }

        cursor.close();

    }

}

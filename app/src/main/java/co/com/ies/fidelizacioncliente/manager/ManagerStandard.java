package co.com.ies.fidelizacioncliente.manager;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteException;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import co.com.ies.fidelizacioncliente.R;
import co.com.ies.fidelizacioncliente.dao.DaoSqliteStandard;
import co.com.ies.fidelizacioncliente.database.DBConstants;
import co.com.ies.fidelizacioncliente.database.SqliteConnection;
import co.com.ies.fidelizacioncliente.entity.BarItem;
import co.com.ies.fidelizacioncliente.entity.GenericItem;
import co.com.ies.fidelizacioncliente.entity.Machine;
import co.com.ies.fidelizacioncliente.utils.AppConstants;
import co.com.ies.fidelizacioncliente.utils.FileUtils;
import co.com.ies.fidelizacioncliente.utils.MsgUtils;

/**
 * Clase usada para realizar operaciones en la bd a a través de @DaoSqliteStandard
 * abri conexiones a la base de datos y realizar el proceso con la logica de negocios necesaria para la info
 */
public class ManagerStandard {

    private static ManagerStandard instance;
    private DaoSqliteStandard daoSqliteStandard;

    private ManagerStandard() {
        daoSqliteStandard = new DaoSqliteStandard();
    }


    public static ManagerStandard getInstance() {
        if (instance == null) {
            instance = new ManagerStandard();
        }
        return instance;
    }


    public ArrayList<GenericItem> getAllCassinos(Context context) {

        SqliteConnection sqliteConnection = SqliteConnection.getInstance(context);
        ArrayList<GenericItem> genericItems;
        try {
            sqliteConnection.openDatabase();
            genericItems = daoSqliteStandard.getAllCassinos(sqliteConnection.getDbSqlite());

        } finally {
            sqliteConnection.closeDatabase();
        }
        genericItems.add(0, new GenericItem(AppConstants.Generic.ID_DEFAULT, context.getString(R.string.act_setting_select)));


        return genericItems;
    }

    public void insertCasinos(Context context, ArrayList<GenericItem> casinos) {
        SqliteConnection sqliteConnection = SqliteConnection.getInstance(context);
        try {
            sqliteConnection.openDatabase();
            daoSqliteStandard.deleteAllCasino(sqliteConnection.getDbSqlite());
            int lenght = casinos.size();
            ContentValues contentValues = new ContentValues();
            for (int i = 0; i < lenght; i++) {
                contentValues.put(DBConstants.Cassinos.ID_, casinos.get(i).getId());
                contentValues.put(DBConstants.Cassinos.NAME, casinos.get(i).getName());
                daoSqliteStandard.insertCasino(sqliteConnection.getDbSqlite(), contentValues);
                contentValues.clear();
            }

        } finally {
            sqliteConnection.closeDatabase();
        }
    }

    public void insertMachines(Context context, ArrayList<Machine> machines) {
        SqliteConnection sqliteConnection = SqliteConnection.getInstance(context);
        try {
            sqliteConnection.openDatabase();
            daoSqliteStandard.deleteAllMachines(sqliteConnection.getDbSqlite());
            int lenght = machines.size();
            ContentValues contentValues = new ContentValues();
            for (int i = 0; i < lenght; i++) {
                contentValues.put(DBConstants.Machines.NUM_DISP, machines.get(i).getNumDisp());
                contentValues.put(DBConstants.Machines.ID_CASSINO, machines.get(i).getCasino());
                contentValues.put(DBConstants.Machines.SERIAL, machines.get(i).getSerial());
                contentValues.put(DBConstants.Machines.MONTO, machines.get(i).getMontoRecaudo());
                contentValues.put(DBConstants.Machines.STATE_MACHINE, machines.get(i).getEstadoMaquina());
                contentValues.put(DBConstants.Machines.STATE_RED, machines.get(i).getEstadoRed());
                contentValues.put(DBConstants.Machines.STATE_SERIAL, machines.get(i).getEstadoSerial());
                contentValues.put(DBConstants.Machines.STATE_SWITCH, machines.get(i).getEstadoSwitch());
                daoSqliteStandard.insertMachine(sqliteConnection.getDbSqlite(), contentValues);
                contentValues.clear();
            }

        } finally {
            sqliteConnection.closeDatabase();
        }
    }


    /**
     * Obtenemos los datos de numero de dispositivo y serial para mostrar,
     * son los datos necesarios al momento
     *
     * @param context
     * @param idCassino
     * @return
     */
    public ArrayList<GenericItem> getGenericMachineByCassino(Context context, String idCassino) {

        SqliteConnection sqliteConnection = SqliteConnection.getInstance(context);
        ArrayList<GenericItem> genericItems;
        try {
            sqliteConnection.openDatabase();
            genericItems = daoSqliteStandard.getGenericMachineByCassino(sqliteConnection.getDbSqlite(), idCassino);

        } finally {
            sqliteConnection.closeDatabase();
        }
        genericItems.add(0, new GenericItem(AppConstants.Generic.ID_DEFAULT, context.getString(R.string.act_setting_select)));


        return genericItems;
    }

    public ArrayList<String> getItemCategories(Context context) {

        SqliteConnection sqliteConnection = SqliteConnection.getInstance(context);
        ArrayList<String> genericItems;
        try {
            sqliteConnection.openDatabase();
            genericItems = daoSqliteStandard.getItemCategories(sqliteConnection.getDbSqlite());

        } finally {
            sqliteConnection.closeDatabase();
        }

        return genericItems;
    }


    public boolean saveBarItems(Context context, JSONArray jsonListBar) {
        boolean result = false;
        SqliteConnection sqliteConnection = SqliteConnection.getInstance(context);
        String folderPath = context.getExternalFilesDir(null).getPath();

        try {
            int size = jsonListBar.length();
            ContentValues contentValues = new ContentValues();
            for (int i = 0; i < size; i++) {
                JSONObject jsonItem = jsonListBar.getJSONObject(i);

                String idItem = jsonItem.getString(AppConstants.WebParams.BAR_ID);
                contentValues.put(DBConstants.Bar.ID_, idItem);
                contentValues.put(DBConstants.Bar.NAME, jsonItem.getString(AppConstants.WebParams.BAR_NAME));

                if (!jsonItem.isNull(AppConstants.WebParams.BAR_POINTS))
                    contentValues.put(DBConstants.Bar.POINTS, jsonItem.getString(AppConstants.WebParams.BAR_POINTS));

                if (!jsonItem.isNull(AppConstants.WebParams.BAR_PRICE)) {
                    contentValues.put(DBConstants.Bar.PRICE, jsonItem.getString(AppConstants.WebParams.BAR_PRICE));
                }

                if (!jsonItem.isNull(AppConstants.WebParams.BAR_CATEGORY)) {
                    contentValues.put(DBConstants.Bar.CATEGORY, jsonItem.getString(AppConstants.WebParams.BAR_CATEGORY));
                }

                if (!jsonItem.isNull(AppConstants.WebParams.BAR_IMAGE)) {
                    String imageName = AppConstants.FileExtension.IMAGE_HEAD + idItem + AppConstants.FileExtension.IMAGE_EXT;
                    FileUtils.createFileFromString(folderPath, imageName, jsonItem.getString(AppConstants.WebParams.BAR_IMAGE));
                    contentValues.put(DBConstants.Bar.PATH_THUMBNAIL, folderPath + File.separator + imageName);
                }

                daoSqliteStandard.insertBarItem(sqliteConnection.getDbSqlite(), contentValues);
                contentValues.clear();
            }
            result = true;
        } catch (JSONException e) {
            MsgUtils.handleException(e);
        } catch (SQLiteException e) {
            MsgUtils.handleException(e);
        } catch (Exception e) {
            MsgUtils.handleException(e);
        } finally {
            sqliteConnection.closeDatabase();
        }
        return result;
    }

    public boolean existBarItems(Context context) {
        boolean result = false;
        SqliteConnection sqliteConnection = SqliteConnection.getInstance(context);
        try {
            sqliteConnection.openDatabase();
            result = daoSqliteStandard.existBarItems(sqliteConnection.getDbSqlite());

        } finally {
            sqliteConnection.closeDatabase();
        }

        return result;
    }


    public boolean getBarItemDetails(Context context, List<BarItem> barItemList) {
        boolean result = false;
        SqliteConnection sqliteConnection = SqliteConnection.getInstance(context);
        try {
            sqliteConnection.openDatabase();
            /*for (BarItem barItem : barItemList) {
                daoSqliteStandard.getBarItemDetails(sqliteConnection.getDbSqlite(), barItem);
            }*/
            daoSqliteStandard.getBarItemDetails(sqliteConnection.getDbSqlite(), barItemList);
            result = true;
        } catch (Exception e) {
            MsgUtils.handleException(e);
        } finally {
            sqliteConnection.closeDatabase();
        }

        return result;
    }

    public boolean deleteAll(Context context) {
        boolean result = false;
        SqliteConnection sqliteConnection = SqliteConnection.getInstance(context);
        try {
            sqliteConnection.openDatabase();
            sqliteConnection.getDbSqlite().beginTransactionNonExclusive();

            daoSqliteStandard.deleteAllBarItems(sqliteConnection.getDbSqlite());
            daoSqliteStandard.deleteAllMachines(sqliteConnection.getDbSqlite());
            daoSqliteStandard.deleteAllCasino(sqliteConnection.getDbSqlite());

            sqliteConnection.getDbSqlite().setTransactionSuccessful();
            result = true;
        } catch (Exception e) {
            MsgUtils.handleException(e);
        } finally {
            sqliteConnection.getDbSqlite().endTransaction();
            sqliteConnection.closeDatabase();
        }

        return result;
    }

}

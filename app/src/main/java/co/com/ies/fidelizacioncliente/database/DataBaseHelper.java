package co.com.ies.fidelizacioncliente.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Clase encargada de crear la base de datos y usada para crear conexión a la misma
 */
public class DataBaseHelper extends SQLiteOpenHelper {

    private String createTableBar = null;
    private String createTableCassinos = null;
    private String createTableMachines = null;

    private Context context;

    public DataBaseHelper(Context context) {
        super(context, DBConstants.DB_NAME, null, DBConstants.DB_VERSION);
        this.context = context;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        initializeStrings();
        db.execSQL(createTableCassinos);
        db.execSQL(createTableBar);
        db.execSQL(createTableMachines);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        if (oldVersion == 1 && newVersion == 2) {
            upgradeV2(db);
        }
    }

    private void initializeStrings() {
        String C_T = "CREATE TABLE ";
        String _INT_ = " INTEGER, ";
        String _INT = " INTEGER);";
        String _TEXT_ = " TEXT, ";
        String _TEXT = " TEXT);";
        String _TXT_PM = " TEXT PRIMARY KEY, ";


        createTableBar = C_T + DBConstants.Bar.TABLE +
                " (" + DBConstants.Bar.ID_ + _TXT_PM +
                DBConstants.Bar.NAME + _TEXT_ +
                DBConstants.Bar.POINTS + _TEXT_ +
                DBConstants.Bar.PRICE + _TEXT_ +
                DBConstants.Bar.AVAILABLES + _TEXT_ +
                DBConstants.Bar.PATH_THUMBNAIL + _TEXT_ +
                DBConstants.Bar.CATEGORY + _TEXT;


        createTableCassinos = C_T + DBConstants.Cassinos.TABLE +
                " (" + DBConstants.Cassinos.ID_ + _TXT_PM +
                DBConstants.Cassinos.NAME + _TEXT;

        createTableMachines = C_T + DBConstants.Machines.TABLE +
                " (" + DBConstants.Machines.NUM_DISP + _TXT_PM +
                DBConstants.Machines.ID_CASSINO + _TEXT_ +
                DBConstants.Machines.SERIAL + _TEXT_ +
                DBConstants.Machines.MONTO + _TEXT_ +
                DBConstants.Machines.STATE_MACHINE + _TEXT_ +
                DBConstants.Machines.STATE_SERIAL + _TEXT_ +
                DBConstants.Machines.STATE_RED + _TEXT_ +
                DBConstants.Machines.STATE_SWITCH + _TEXT;

    }

    private void upgradeV2(SQLiteDatabase db) {
        db.execSQL("ALTER TABLE " + DBConstants.Bar.TABLE +
                " ADD COLUMN " + DBConstants.Bar.CATEGORY + " TEXT;");

    }
}

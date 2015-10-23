package nlubej.gains.Database.Queries;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;

import nlubej.gains.DataTransferObjects.ProgramDto;

/**
 * Created by nlubej on 21.10.2015.
 */
public class LogQueries {

    private static SQLiteDatabase db;

    public LogQueries(SQLiteDatabase db){
        this.db = db;
    }



    public boolean DeleteLogByRoutine (long routineId) {
        return db.delete("ROUTINE", "ROUTINE_ID" + "=" + routineId, null) > 0;

    }
}

package nlubej.gains.Database.Queries;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;

import nlubej.gains.DataTransferObjects.ProgramDto;
import nlubej.gains.Database.QueryFactory;

/**
 * Created by nlubej on 21.10.2015.
 */
public class ProgramQueries
{
    public static String SelectPrograms ()
    {
        return "SELECT p.PROGRAM_ID, p.PROGRAM_NAME, count(r.ROUTINE_ID) as ROUTINE_COUNT FROM PROGRAM p " +
                "LEFT JOIN ROUTINE r on p.PROGRAM_ID = r.PROGRAM_ID " +
                "GROUP BY p.PROGRAM_ID, p.PROGRAM_NAME " +
                "ORDER BY p.CREATED_ON ASC";
    }

    public static String SelectRoutineIdsForDeletion(int programId)
    {
        return String.format("SELECT r.ROUTINE_ID  FROM PROGRAM p " +
                "JOIN ROUTINE r ON r.PROGRAM_ID = p.PROGRAM_ID " +
                "WHERE r.PROGRAM_ID = %d ", programId);
    }

    public static String CreateTableProgram()
    {
        return "create table if not exists PROGRAM (PROGRAM_ID integer primary key autoincrement, PROGRAM_NAME varchar not null, CREATED_ON TIMESTAMP DEFAULT CURRENT_TIMESTAMP);";
    }
}

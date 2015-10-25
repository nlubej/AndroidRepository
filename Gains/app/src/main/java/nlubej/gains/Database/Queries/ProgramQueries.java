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
        return "SELECT PROGRAM_ID, PROGRAM_NAME FROM PROGRAM";
    }

    public static String DeleteWorkoutLogsByProgramId (int programId)
    {
        return String.format("DELETE FROM WORKOUT_LOG WHERE EXERCISE_ID IN (" +
                "SELECT EXERCISE_ID FROM EXERCISE WHERE ROUTINE_ID IN (" +
                "SELECT ROUTINE_ID FROM ROUTINE WHERE PROGRAM_ID = %d)", programId);
    }

    public static String DeleteNotesByProgramId (int programId)
    {
        return String.format("DELETE FROM WORKOUT_NOTE WHERE EXERCISE_ID IN (" +
                "SELECT EXERCISE_ID FROM EXERCISE WHERE ROUTINE_ID IN (" +
                "SELECT ROUTINE_ID FROM ROUTINE WHERE PROGRAM_ID = %d)", programId);
    }

    public static String DeleteExerciesByProgramId (int programId)
    {
        return String.format("DELETE FROM EXERCISE WHERE EXERCISE_ID IN (" +
                "SELECT EXERCISE_ID FROM EXERCISE WHERE ROUTINE_ID IN (" +
                "SELECT ROUTINE_ID FROM ROUTINE WHERE PROGRAM_ID = %d)", programId);
    }

    public static String CreateTableProgram()
    {
        return "create table if not exists PROGRAM (PROGRAM_ID integer primary key autoincrement, PROGRAM_NAME varchar not null);";
    }
}

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

    public static String CreateTableTmpLoggedWorkout()
    {
        return "create table if not exists TMP_LOGGED_WORKOUT (TMP_LOGGED_WORKOUT_ID integer primary key autoincrement, TMP_LAST_WORKOUT_NUMBER integer not null, ROUTINE_ID integer not null);";
    }

    public static String CreateTableLoggedWorkout()
    {
        return "create table if not exists LOGGED_WORKOUT (LOGGED_WORKOUT_ID integer primary key autoincrement, LOGGED_SET integer not null, LOGGED_WEIGHT double, LOGGED_REP integer, WORKOUT_NUMBER integer not null, ROUTINE_EXERCISE_ID integer not null, CREATED_ON TIMESTAMP DEFAULT CURRENT_TIMESTAMP);";
    }

    public static String CreateTableWorkoutNote()
    {
        return "create table if not exists WORKOUT_NOTE (LOGGED_WORKOUT_ID integer primary key, NOTE text);";
    }

    public static String SelectLoggedWorkouts(int routineExerciseId)
    {
        return String.format("SELECT l.LOGGED_WORKOUT_ID, l.LOGGED_SET, l.LOGGED_REP, l.LOGGED_WEIGHT, note.NOTE, l.WORKOUT_NUMBER  FROM LOGGED_WORKOUT l " +
                "LEFT JOIN WORKOUT_NOTE note on note.LOGGED_WORKOUT_ID = l.LOGGED_WORKOUT_ID " +
                "JOIN ROUTINE_EXERCISE re on re.ROUTINE_EXERCISE_ID = l.ROUTINE_EXERCISE_ID " +
                "WHERE re.EXERCISE_ID = %d  ORDER BY l.WORKOUT_NUMBER desc" ,routineExerciseId);
    }
}

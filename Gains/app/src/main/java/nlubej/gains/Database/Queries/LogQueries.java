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
        return "create table if not exists LOGGED_WORKOUT (LOGGED_WORKOUT_ID integer primary key autoincrement, LOGGED_SET integer not null, LOGGED_WEIGHT double, LOGGED_REP integer, LOGGED_NOTE varchar null,  WORKOUT_NUMBER integer not null, EXERCISE_ID integer not null, CREATED_ON TIMESTAMP DEFAULT CURRENT_TIMESTAMP);";
    }

    public static String CreateTableWorkoutNote()
    {
        return "create table if not exists WORKOUT_NOTE (WORKOUT_NOTE_ID integer primary key autoincrement, NOTE text, WORKOUT_NUMBER integer not null, EXERCISE_ID integer not null);";
    }

    public static String SelectLoggedWorkouts(int exerciseId)
    {
        return String.format("SELECT l.LOGGED_WORKOUT_ID, l.LOGGED_SET, l.LOGGED_WEIGHT, l.LOGGED_REP, note.LOGGED_NOTE, l.WORKOUT_NUMBER  FROM LOGGED_WORKOUT l " +
                "LEFT JOIN WORKOUT_NOTE note on note.EXERCISE_ID = l.EXERCISE_ID AND note.WORKOUT_NUMBER = l.WORKOUT_NUMBER " +
                "WHERE l.EXERCISE_ID = %d  ORDER BY l.WORKOUT_NUMBER desc" ,exerciseId);
    }
}

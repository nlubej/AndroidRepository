package nlubej.gains.Database.Queries;

/**
 * Created by nlubej on 21.10.2015.
 */
public class RoutineQueries
{
    public static String CreateTableRoutine()
    {
        return "create table if not exists ROUTINE (ROUTINE_ID integer primary key autoincrement, ROUTINE_POS integer not null, ROUTINE_NAME varchar not null , PROGRAM_ID integer not null);";
    }

    public static String DeleteWorkoutLogsByRotuineId (int routineId)
    {
        return String.format("DELETE FROM WORKOUT_LOG WHERE EXERCISE_ID IN (" + "SELECT EXERCISE_ID FROM EXERCISE WHERE ROUTINE_ID = %d)", routineId);
    }

    public static String DeleteNotesByRoutineId (int routineId)
    {
        return String.format("DELETE FROM WORKOUT_NOTE WHERE EXERCISE_ID IN (" + "SELECT EXERCISE_ID FROM EXERCISE WHERE ROUTINE_ID = %d)", routineId);
    }

    public static String DeleteExerciesByRoutineId (int routineId)
    {
        return String.format("DELETE FROM EXERCISE WHERE EXERCISE_ID IN (" + "SELECT EXERCISE_ID FROM EXERCISE WHERE ROUTINE_ID = %d)", routineId);
    }

    public static String SelectRoutines()
    {
        return "SELECT ROUTINE_ID, ROUTINE_NAME, ROUTINE_POS, PROGRAM_ID FROM ROUTINE";
    }
}

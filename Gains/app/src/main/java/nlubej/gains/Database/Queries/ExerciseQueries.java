package nlubej.gains.Database.Queries;

/**
 * Created by nlubej on 21.10.2015.
 */
public class ExerciseQueries
{
    public static String CreateTableExercise()
    {
        return "create table if not exists EXERCISE (EXERCISE_ID integer primary key autoincrement, EXERCISE_POS integer not null, EXERCISE_NAME varchar not null, EXERCISE_TYPE varchar not null, ROUTINE_ID integer not null);";
    }

    public static String DeleteWorkoutLogsByExerciseId (int exercise)
    {
        return String.format("DELETE FROM WORKOUT_LOG WHERE EXERCISE_ID  = %d)", exercise);
    }

    public static String DeleteNotesByExerciseId (int exercise)
    {
        return String.format("DELETE FROM WORKOUT_LOG WHERE EXERCISE_ID = %d)", exercise);
    }

    public static String DeleteExerciesByExerciseId (int exercise)
    {
        return String.format("DELETE FROM WORKOUT_LOG WHERE EXERCISE_ID = %d)", exercise);
    }

    public static String SelectExercises()
    {
        return "SELECT EXERCISE_ID, EXERCISE_NAME, EXERCISE_POS, EXERCISE_TYPE, ROUTINE_ID FROM EXERCISE";
    }
}

package nlubej.gains.Database.Queries;

/**
 * Created by nlubej on 21.10.2015.
 */
public class ExerciseQueries
{
    public static String CreateTableExercise()
    {
        return "create table if not exists EXERCISE (EXERCISE_ID integer primary key autoincrement, EXERCISE_NAME varchar not null, EXERCISE_TYPE integer not null, CREATED_ON TIMESTAMP DEFAULT CURRENT_TIMESTAMP)";
    }

    public static String CreateTableRoutineExercise()
    {
        return "create table if not exists ROUTINE_EXERCISE (ROUTINE_EXERCISE_ID integer primary key autoincrement, ROUTINE_ID integer not null, EXERCISE_ID integer not null,  EXERCISE_POS integer not null)";
    }

    public static String CreateTableExerciseType()
    {
        return  "create table if not exists EXERCISE_TYPE (EXERCISE_TYPE_ID integer primary key, DESCRIPTION integer not null);";
    }

    public static String CreateTableLoggedWorkout()
    {
        return "create table if not exists LOGGED_WORKOUT (LOGGED_WORKOUT_ID integer primary key autoincrement, LOGGED_SET integer not null, LOGGED_WEIGHT double, LOGGED_REP integer,  DATE_CREATED varchar not null, WORKOUT_NUMBER integer not null, EXERCISE_ID integer not null);";
    }

    public static String CreateTableWorkoutNote()
    {
        return "create table if not exists WORKOUT_NOTE (WORKOUT_NOTE_ID integer primary key autoincrement, NOTE text, WORKOUT_NUM integer not null, EXERCISE_ID integer not null);";
    }

    public static String CreateTableTmpLoggedWorkout()
    {
        return "create table if not exists TMP_LOGGED_WORKOUT (TMP_LOGGED_WORKOUT_ID integer primary key autoincrement, TMP_LAST_WORKOUT_NUMBER integer not null, ROUTINE_ID integer not null);";
    }

    public static String SelectExercises(int routineId)
    {
        return String.format("SELECT e.EXERCISE_ID, e.EXERCISE_NAME, re.EXERCISE_POS, e.EXERCISE_TYPE, re.ROUTINE_ID FROM EXERCISE e " +
                " JOIN ROUTINE_EXERCISE re ON e.EXERCISE_ID = re.EXERCISE_ID" +
                " WHERE re.ROUTINE_ID = %s ORDER BY re.EXERCISE_POS ASC",routineId);
    }

    public static String SelectAllExercises(int routineId)
    {
        if(routineId == -1)
        {
            return String.format("SELECT e.EXERCISE_ID, e.EXERCISE_NAME, re.EXERCISE_POS, e.EXERCISE_TYPE, re.ROUTINE_ID FROM EXERCISE e" +
                    " JOIN ROUTINE_EXERCISE re ON re.EXERCISE_ID = e.EXERCISE_ID ORDER BY e.CREATED_ON");
        }
        else
        {
            return String.format("SELECT e.EXERCISE_ID, e.EXERCISE_NAME, re.EXERCISE_POS, e.EXERCISE_TYPE, re.ROUTINE_ID FROM EXERCISE e" +
                    " JOIN ROUTINE_EXERCISE re ON re.EXERCISE_ID = e.EXERCISE_ID" +
                    " WHERE re.ROUTINE_ID != %d " +
                    " ORDER BY e.CREATED_ON", routineId);
        }
    }

    public static String SelectExerciseTypes()
    {
        return "SELECT EXERCISE_TYPE_ID, DESCRIPTION FROM EXERCISE_TYPE";
    }

    public static String SelectRoutineExerciseConnections(String exerciseId)
    {
        return String.format("SELECT EXERCISE_ID FROM ROUTINE_EXERCISE WHERE EXERCISE_ID = %s", exerciseId);
    }

    public static String InsertExercise()
    {
        return "INSERT INTO EXERCISE (EXERCISE_NAME, EXERCISE_POS, EXERCISE_TYPE, ROUTINE_ID) VALUES (?s,COALESCE((SELECT MAX(EXERCISE_POS)+1 FROM EXERCISE),1), ?s,?s)";
    }
}

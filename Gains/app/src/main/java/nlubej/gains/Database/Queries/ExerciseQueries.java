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

    public static String SelectExercises(int routineId)
    {
        return String.format("SELECT e.EXERCISE_ID, e.EXERCISE_NAME, re.EXERCISE_POS, e.EXERCISE_TYPE, re.ROUTINE_ID FROM EXERCISE e " +
                " JOIN ROUTINE_EXERCISE re ON e.EXERCISE_ID = re.EXERCISE_ID" +
                " WHERE re.ROUTINE_ID = %s ORDER BY re.EXERCISE_POS ASC",routineId);
    }

    public static String SelectExercisesByProgramId(int programId)
    {
        return String.format("SELECT DISTINCT e.EXERCISE_ID, e.EXERCISE_NAME, e.EXERCISE_TYPE FROM EXERCISE e " +
                " JOIN ROUTINE_EXERCISE re ON e.EXERCISE_ID = re.EXERCISE_ID" +
                " JOIN ROUTINE ru ON ru.ROUTINE_ID = re.ROUTINE_ID" +
                " JOIN PROGRAM pr ON pr.PROGRAM_ID = ru.PROGRAM_ID" +
                " WHERE pr.PROGRAM_ID = %d ",programId);
    }

    public static String SelectAllExercises(int routineId)
    {
        if(routineId == -1)
        {
            return String.format("SELECT DISTINCT e.EXERCISE_ID, e.EXERCISE_NAME, e.EXERCISE_TYPE FROM EXERCISE e" +
                    " JOIN ROUTINE_EXERCISE re ON re.EXERCISE_ID = e.EXERCISE_ID ORDER BY e.CREATED_ON");
        }
        else
        {
            return String.format("SELECT DISTINCT e.EXERCISE_ID, e.EXERCISE_NAME, e.EXERCISE_TYPE FROM EXERCISE e" +
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
        return String.format("SELECT EXERCISE_ID FROM ROUTINE_EXERCISE WHERE EXERCISE_ID IN (%s)", exerciseId);
    }

    public static String InsertExercise()
    {
        return "INSERT INTO EXERCISE (EXERCISE_NAME, EXERCISE_POS, EXERCISE_TYPE, ROUTINE_ID) VALUES (?s,COALESCE((SELECT MAX(EXERCISE_POS)+1 FROM EXERCISE),1), ?s,?s)";
    }
}

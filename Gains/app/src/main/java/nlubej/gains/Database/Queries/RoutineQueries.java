package nlubej.gains.Database.Queries;

/**
 * Created by nlubej on 21.10.2015.
 */
public class RoutineQueries
{
    public static String CreateTableRoutine()
    {
        return "create table if not exists ROUTINE (ROUTINE_ID integer primary key autoincrement, ROUTINE_POS integer not null, ROUTINE_NAME varchar not null , PROGRAM_ID integer not null, CREATED_ON TIMESTAMP DEFAULT CURRENT_TIMESTAMP);";
    }

    public static String SelectExerciseIdsForDeletion(int routineId)
    {
        return String.format("SELECT GROUP_CONCAT(e.EXERCISE_ID, ',')  FROM ROUTINE r " +
                "JOIN EXERCISE e ON r.ROUTINE_ID = e.ROUTINE_ID " +
                "WHERE e.ROUTINE_ID = %d " +
                "GROUP BY e.ROUTINE_ID ", routineId);
    }

    public static String DeleteNotesByRoutineId (int routineId)
    {
        return String.format("DELETE FROM WORKOUT_NOTE WHERE EXERCISE_ID IN (" + "SELECT EXERCISE_ID FROM EXERCISE WHERE ROUTINE_ID = %d)", routineId);
    }

    public static String DeleteExerciesByRoutineId (int routineId)
    {
        return String.format("DELETE FROM EXERCISE WHERE EXERCISE_ID IN (" + "SELECT EXERCISE_ID FROM EXERCISE WHERE ROUTINE_ID = %d)", routineId);
    }

    public static String SelectRoutines(int programId)
    {
        return String.format("SELECT r.ROUTINE_ID, r.ROUTINE_NAME, r.ROUTINE_POS, r.PROGRAM_ID, count(e.EXERCISE_ID) as EXERCISE_COUNT  FROM ROUTINE r " +
                "LEFT JOIN ROUTINE_EXERCISE e on e.ROUTINE_ID = r.ROUTINE_ID " +
                "WHERE PROGRAM_ID = %s " +
                "GROUP BY r.ROUTINE_ID, r.ROUTINE_NAME, r.ROUTINE_POS, r.PROGRAM_ID  " +
                "ORDER BY ROUTINE_POS ASC " ,programId);
    }

    public static String InsertRoutine(String routineName, int programId)
    {
        return String.format("INSERT INTO ROUTINE (ROUTINE_NAME, ROUTINE_POS, PROGRAM_ID) VALUES (%s,COALESCE((SELECT MAX(ROUTINE_POS)+1 FROM ROUTINE),1), %s)",routineName,programId);
    }
}

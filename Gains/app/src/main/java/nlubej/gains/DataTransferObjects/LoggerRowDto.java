package nlubej.gains.DataTransferObjects;

import android.database.MatrixCursor;

import nlubej.gains.Enums.ExerciseType;

/**
 * Created by nlubej on 21.10.2015.
 */
public class LoggerRowDto
{
    public LoggerRowDto()
    {
    }

    public LoggerRowDto(int programId, String name)
    {
        this.Id = programId;
        this.Name = name;
    }

    public LoggerRowDto(int programId, String name, int routineCount)
    {
        this.Id = programId;
        this.Name = name;
        this.RoutineCount = routineCount;
    }

    public int Id;
    public String Name;
    public int RoutineCount;
}

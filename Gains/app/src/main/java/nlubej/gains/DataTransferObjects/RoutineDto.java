package nlubej.gains.DataTransferObjects;

import android.database.MatrixCursor;

/**
 * Created by nlubej on 21.10.2015.
 */
public class RoutineDto
{
    public int Id;
    public String Name;
    public int Position;
    public int ProgramId;
    public int ExerciseCount;
    public boolean IsSelected;

/*    public RoutineDto(int id, String name, int position, int programId, int exerciseCount)
    {
        this.Id = id;
        this.Name = name;
        this.Position = position;
        this.ProgramId = programId;
        this.ExerciseCount = exerciseCount;
    }*/

    public RoutineDto()
    {
    }

    public RoutineDto(int routineId, String name, int programId)
    {
        this.Id = routineId;
        this.Name = name;
        this.ProgramId = programId;
    }

    public static RoutineDto ToDto(MatrixCursor cursor)
    {
        RoutineDto dto = new RoutineDto();
        dto.Id = cursor.getInt(0);
        dto.Name = cursor.getString(1);
        dto.Position = cursor.getInt(2);
        dto.ProgramId = cursor.getInt(3);
        dto.ExerciseCount = cursor.getInt(4);

        return dto;
    }
}

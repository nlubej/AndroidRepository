package nlubej.gains.DataTransferObjects;

import android.database.MatrixCursor;

import nlubej.gains.Enums.ExerciseType;

/**
 * Created by nlubej on 21.10.2015.
 */
public class ExerciseDto
{
    public int Id;
    public String Name;
    public int Position;
    public ExerciseType Type;
    public int RoutineId;

    public ExerciseDto()
    {
    }

    public ExerciseDto(int exerciseId, String name, int typeId, int routineId)
    {
        this.Id = exerciseId;
        this.Name = name;
        this.Type = ExerciseType.FromInteger(typeId);
        this.RoutineId = routineId;
    }

    public static ExerciseDto ToDto(MatrixCursor cursor)
    {
        ExerciseDto dto = new ExerciseDto();
        dto.Id = cursor.getInt(0);
        dto.Name = cursor.getString(1);
        dto.Position = cursor.getInt(2);
        dto.Type = ExerciseType.FromInteger(cursor.getInt(3));
        dto.RoutineId = cursor.getInt(4);

        return dto;
    }
}

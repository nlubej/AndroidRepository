package nlubej.gains.DataTransferObjects;

import android.database.MatrixCursor;

import com.rengwuxian.materialedittext.MaterialEditText;

import fr.ganfra.materialspinner.MaterialSpinner;
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
    public int WorkoutNumber;
    public int RoutineExerciseId;
    public int RoutineId;
    public String RoutineName;
    public boolean IsNew;

    public ExerciseDto()
    {
    }

    public ExerciseDto(int exerciseId, String exerciseName, int exerciseType)
    {
        this.Id = exerciseId;
        this.Name = exerciseName;
        this.Type = ExerciseType.FromInteger(exerciseType);
    }

    public ExerciseDto(int exerciseId, String exerciseName, int exerciseType, int routineExerciseId)
    {
        this.Id = exerciseId;
        this.Name = exerciseName;
        this.Type = ExerciseType.FromInteger(exerciseType);
        this.RoutineExerciseId = routineExerciseId;
    }

    public ExerciseDto(int exerciseId, String exerciseName, int exerciseType, int routineExerciseId, boolean isNew)
    {
        this.Id = exerciseId;
        this.Name = exerciseName;
        this.Type = ExerciseType.FromInteger(exerciseType);
        this.RoutineExerciseId = routineExerciseId;
        IsNew = isNew;
    }

    public static ExerciseDto ToDto(MatrixCursor cursor)
    {
        ExerciseDto dto = new ExerciseDto();
        dto.Id = cursor.getInt(0);
        dto.Name = cursor.getString(1);
        dto.Position = cursor.getInt(2);
        dto.Type = ExerciseType.FromInteger(cursor.getInt(3));

        return dto;
    }
}

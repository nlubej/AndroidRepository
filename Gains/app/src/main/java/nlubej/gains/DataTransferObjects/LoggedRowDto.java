package nlubej.gains.DataTransferObjects;

import android.database.MatrixCursor;

import nlubej.gains.Enums.ExerciseType;

/**
 * Created by nlubej on 21.10.2015.
 */
public class LoggedRowDto
{

    public LoggedRowDto()
    {
    }

    public LoggedRowDto(int set, String rep, String weight)
    {
        this.Set = set;
        this.Rep = rep;
        this.Weight = weight;
    }


    public int LoggedWorkoutId;
    public int Set;
    public String Rep;
    public String Weight;
    public String Note;
    public boolean HasNote;
    public boolean IsSummary;
    public int WorkoutNumber;
    public boolean IsUpdatingNote;
}
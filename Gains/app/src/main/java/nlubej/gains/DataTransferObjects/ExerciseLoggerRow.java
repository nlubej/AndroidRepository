package nlubej.gains.DataTransferObjects;

import android.database.MatrixCursor;

import nlubej.gains.Enums.ExerciseType;

/**
 * Created by nlubej on 21.10.2015.
 */
public class ExerciseLoggerRow
{
    public ExerciseLoggerRow()
    {
    }

    public ExerciseLoggerRow(int set, String rep, String weight)
    {
        this.Set = set;
        this.Rep = rep;
        this.Weight = weight;
    }


    public int LoggedWorkoutId;
    public int Set;
    public String Rep;
    public String Weight;
    public boolean MarkForEdit;
    public boolean HasNote;
    public String Note;
}

package nlubej.gains.DataTransferObjects;

import android.database.MatrixCursor;

import nlubej.gains.Enums.ExerciseType;

/**
 * Created by nlubej on 21.10.2015.
 */
public class LoggerViewRowDto
{

    public LoggerViewRowDto()
    {
    }

    public LoggerViewRowDto(int set, String rep, String weight)
    {
        this.Set = set;
        this.Rep = rep;
        this.Weight = weight;
    }


    public int LogId;
    public int Set;
    public String Rep;
    public String Weight;
    public String Note;
}
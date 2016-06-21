package nlubej.gains.DataTransferObjects;

import java.util.ArrayList;

/**
 * Created by nlubej on 9. 06. 2016.
 */
public class CompleteWorkoutDto
{
    public CompleteWorkoutDto()
    {
        LoggedRows = new ArrayList<>();
    }

    public ArrayList<LoggedRowDto> LoggedRows;
}
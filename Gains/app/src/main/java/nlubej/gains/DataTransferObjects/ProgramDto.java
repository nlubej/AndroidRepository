package nlubej.gains.DataTransferObjects;

/**
 * Created by nlubej on 21.10.2015.
 */
public class ProgramDto
{

    public ProgramDto()
    {
    }

    public ProgramDto(int programId, String name)
    {
        this.Id = programId;
        this.Name = name;
    }

    public int Id;
    public String Name;
    public int RoutineCount;
}

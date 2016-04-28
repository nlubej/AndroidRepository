package nlubej.gains.Enums;

/**
 * Created by nlubej on 22.10.2015.
 */
public enum ExerciseType
{
    CHEST(1, "Chest"),
    ARMS(2, "Arms"),
    LEGS(3, "Legs");

    public final int Id;
    public final String Description;

    ExerciseType(int value, String description)
    {
        Id = value;
        Description = description;
    }

    public static ExerciseType FromInteger(int x) {
        switch(x) {
            case 1:
                return CHEST;
            case 2:
                return ARMS;
            case 3:
                return LEGS;
        }
        return null;
    }
}
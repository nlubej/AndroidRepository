package nlubej.gains;

/**
 * Created by nlubej on 6. 06. 2016.
 */
public  class Tools
{
    public static double ToDouble(String value, int places)
    {
        return Round(Double.parseDouble(value), places);
    }

    public static double ToDouble(String value)
    {
        return Double.parseDouble(value);
    }

    public static int ToInt(String value)
    {
        return Integer.parseInt(value);
    }

    public static double Round(double value, int places) {
        if (places < 0) throw new IllegalArgumentException();

        long factor = (long) Math.pow(10, places);
        value = value * factor;
        long tmp = Math.round(value);
        return (double) tmp / factor;
    }
}

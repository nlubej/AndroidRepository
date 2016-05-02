package nlubej.gains.interfaces;

/**
 * Created by nlubej on 28.4.2016.
 */
public interface OnItemChanged<T>
{
    void OnAdded(T row);
    void OnUpdated(T row);
}

package nlubej.gains.interfaces;

/**
 * Created by nlubej on 28.4.2016.
 */
public interface OnNoteChanged<T>
{
    void OnNoteAdded(T row);
    void OnNoteUpdated(T row);
    void OnNoteRemoved(T row);
}

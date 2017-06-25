package edu.stevens.cs522.chat.activities;

import android.widget.SimpleCursorAdapter;

import edu.stevens.cs522.chat.managers.TypedCursor;

/**
 * Created by dduggan.
 */

public interface IIndexManager<T> {

    /**
     * Interface provided by parent activity to IndexFragment
     */

    /*
     * Called by the fragment when it is initialized, to request the index
     */
    public SimpleCursorAdapter getIndexTitles(Callback<T> callback);

    /*
     * Called when an item from the index is selected.
     */
    public void onItemSelected(T item);


    /**
     * Interface provided by IndexFragment to parent activity
     */

    public interface Callback<T> {

        /*
         * Set the title for the index pane.
         */
        public void setIndexTitle(String title);

        /*
         * Callbacks from the data source.
         */
        public void setTitles(TypedCursor<T> cursor);

        public void clearTitles();
        public void clearHighlight();
    }

}

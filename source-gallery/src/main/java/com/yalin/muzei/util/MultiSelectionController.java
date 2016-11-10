package com.yalin.muzei.util;

import android.os.Bundle;
import android.os.Parcelable;

import java.util.HashSet;
import java.util.Set;

/**
 * YaLin
 * 2016/11/10.
 */

public class MultiSelectionController<T extends Parcelable> {
    private String mStateKey;
    private Set<T> mSelection = new HashSet<>();
    private Callbacks mCallbacks = DUMMY_CALLBACKS;

    public MultiSelectionController(String stateKey) {
        mStateKey = stateKey;
    }

    public void restoreInstanceState(Bundle savedInstanceState) {
        if (savedInstanceState != null) {
            mSelection.clear();
            Parcelable[] selection = savedInstanceState.getParcelableArray(mStateKey);
            if (selection != null && selection.length > 0) {
                for (Parcelable item : selection) {
                    //noinspection unchecked
                    mSelection.add((T) item);
                }
            }
        }

        mCallbacks.onSelectionChanged(true, false);
    }

    public void saveInstanceState(Bundle outBundle) {
        Parcelable[] selection = new Parcelable[mSelection.size()];
        int i = 0;
        for (Parcelable item : mSelection) {
            selection[i] = item;
            ++i;
        }

        outBundle.putParcelableArray(mStateKey, selection);
    }

    public void setCallbacks(Callbacks callbacks) {
        mCallbacks = callbacks;
        if (mCallbacks == null) {
            mCallbacks = DUMMY_CALLBACKS;
        }
    }

    public Set<T> getSelection() {
        return new HashSet<>(mSelection);
    }

    public int getSelectedCount() {
        return mSelection.size();
    }

    public boolean isSelecting() {
        return mSelection.size() > 0;
    }

    public void toggle(T item, boolean fromUser) {
        if (mSelection.contains(item)) {
            mSelection.remove(item);
        } else {
            mSelection.add(item);
        }

        mCallbacks.onSelectionChanged(false, fromUser);
    }

    public void reset(boolean fromUser) {
        mSelection.clear();
        mCallbacks.onSelectionChanged(false, fromUser);
    }

    public boolean isSelected(T item) {
        return mSelection.contains(item);
    }

    public interface Callbacks {
        void onSelectionChanged(boolean restored, boolean fromUser);
    }

    private static final Callbacks DUMMY_CALLBACKS = new Callbacks() {
        @Override
        public void onSelectionChanged(boolean restored, boolean fromUser) {
        }
    };
}

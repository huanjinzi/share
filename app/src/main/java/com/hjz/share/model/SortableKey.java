package com.hjz.share.model;

import android.support.annotation.NonNull;

/**
 * Created by hjz on 18-1-30.
 * for:
 */

public final class SortableKey implements Comparable{
    public final String KEY;
    public long ORDER;

    public SortableKey(String key, long order) {
        KEY = key;
        ORDER = order;
    }

    @Override
    public int hashCode() {
        return KEY.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof SortableKey) {
            SortableKey target = (SortableKey) obj;
            return KEY.equals(target.KEY);
        }
        return super.equals(obj);
    }

    @Override
    public int compareTo(@NonNull Object o) {
        if (o instanceof Long) {
            long target = (long) o;
            return ORDER > target ? -1 : 1;
        }
        return 0;
    }
}

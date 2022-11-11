package io.github.nickid2018.th.util;

import it.unimi.dsi.fastutil.ints.IntList;

public class CollectionUtil {

    public static int binarySearch(IntList list, int key) {
        int low = 0;
        int high = list.size() - 1;
        while (low <= high) {
            int mid = (low + high) >>> 1;
            int midVal = list.getInt(mid);
            if (midVal < key)
                low = mid + 1;
            else if (midVal > key)
                high = mid - 1;
            else
                return mid; // key found
        }
        return -(low + 1);  // key not found.
    }
}

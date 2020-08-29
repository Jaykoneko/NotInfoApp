package net.sourceforge.jtds.jdbc.cache;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;

public class SimpleLRUCache<K, V> {
    private final Map<K, V> _Map;

    public SimpleLRUCache(int i) {
        final int i2 = i;
        C08861 r0 = new LinkedHashMap<K, V>(i + 10, 0.75f, true) {
            /* access modifiers changed from: protected */
            public boolean removeEldestEntry(Entry<K, V> entry) {
                return size() > i2;
            }
        };
        this._Map = r0;
    }

    public synchronized V put(K k, V v) {
        return this._Map.put(k, v);
    }

    public synchronized V get(K k) {
        return this._Map.get(k);
    }
}

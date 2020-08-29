package net.sourceforge.jtds.jdbc.cache;

import java.util.ArrayList;
import java.util.HashMap;
import net.sourceforge.jtds.jdbc.ProcEntry;

public class ProcedureCache implements StatementCache {
    private static final int MAX_INITIAL_SIZE = 50;
    private HashMap cache;
    int cacheSize;
    ArrayList free;
    CacheEntry head = new CacheEntry(null, null);
    CacheEntry tail;

    private static class CacheEntry {
        String key;
        CacheEntry next;
        CacheEntry prior;
        ProcEntry value;

        CacheEntry(String str, ProcEntry procEntry) {
            this.key = str;
            this.value = procEntry;
        }

        /* access modifiers changed from: 0000 */
        public void unlink() {
            CacheEntry cacheEntry = this.next;
            cacheEntry.prior = this.prior;
            this.prior.next = cacheEntry;
        }

        /* access modifiers changed from: 0000 */
        public void link(CacheEntry cacheEntry) {
            CacheEntry cacheEntry2 = cacheEntry.next;
            this.next = cacheEntry2;
            this.prior = cacheEntry;
            cacheEntry2.prior = this;
            cacheEntry.next = this;
        }
    }

    public ProcedureCache(int i) {
        this.cacheSize = i;
        this.cache = new HashMap(Math.min(50, i) + 1);
        CacheEntry cacheEntry = new CacheEntry(null, null);
        this.tail = cacheEntry;
        this.head.next = cacheEntry;
        this.tail.prior = this.head;
        this.free = new ArrayList();
    }

    public synchronized Object get(String str) {
        CacheEntry cacheEntry = (CacheEntry) this.cache.get(str);
        if (cacheEntry == null) {
            return null;
        }
        cacheEntry.unlink();
        cacheEntry.link(this.head);
        cacheEntry.value.addRef();
        return cacheEntry.value;
    }

    public synchronized void put(String str, Object obj) {
        ((ProcEntry) obj).addRef();
        CacheEntry cacheEntry = new CacheEntry(str, (ProcEntry) obj);
        this.cache.put(str, cacheEntry);
        cacheEntry.link(this.head);
        scavengeCache();
    }

    public synchronized void remove(String str) {
        CacheEntry cacheEntry = (CacheEntry) this.cache.get(str);
        if (cacheEntry != null) {
            cacheEntry.unlink();
            this.cache.remove(str);
        }
    }

    /* JADX WARNING: Incorrect type for immutable var: ssa=java.util.Collection, code=java.util.Collection<net.sourceforge.jtds.jdbc.ProcEntry>, for r2v0, types: [java.util.Collection, java.util.Collection<net.sourceforge.jtds.jdbc.ProcEntry>] */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public synchronized java.util.Collection getObsoleteHandles(java.util.Collection<net.sourceforge.jtds.jdbc.ProcEntry> r2) {
        /*
            r1 = this;
            monitor-enter(r1)
            if (r2 == 0) goto L_0x0017
            java.util.Iterator r2 = r2.iterator()     // Catch:{ all -> 0x0030 }
        L_0x0007:
            boolean r0 = r2.hasNext()     // Catch:{ all -> 0x0030 }
            if (r0 == 0) goto L_0x0017
            java.lang.Object r0 = r2.next()     // Catch:{ all -> 0x0030 }
            net.sourceforge.jtds.jdbc.ProcEntry r0 = (net.sourceforge.jtds.jdbc.ProcEntry) r0     // Catch:{ all -> 0x0030 }
            r0.release()     // Catch:{ all -> 0x0030 }
            goto L_0x0007
        L_0x0017:
            r1.scavengeCache()     // Catch:{ all -> 0x0030 }
            java.util.ArrayList r2 = r1.free     // Catch:{ all -> 0x0030 }
            int r2 = r2.size()     // Catch:{ all -> 0x0030 }
            if (r2 <= 0) goto L_0x002d
            java.util.ArrayList r2 = r1.free     // Catch:{ all -> 0x0030 }
            java.util.ArrayList r0 = new java.util.ArrayList     // Catch:{ all -> 0x0030 }
            r0.<init>()     // Catch:{ all -> 0x0030 }
            r1.free = r0     // Catch:{ all -> 0x0030 }
            monitor-exit(r1)
            return r2
        L_0x002d:
            r2 = 0
            monitor-exit(r1)
            return r2
        L_0x0030:
            r2 = move-exception
            monitor-exit(r1)
            goto L_0x0034
        L_0x0033:
            throw r2
        L_0x0034:
            goto L_0x0033
        */
        throw new UnsupportedOperationException("Method not decompiled: net.sourceforge.jtds.jdbc.cache.ProcedureCache.getObsoleteHandles(java.util.Collection):java.util.Collection");
    }

    private void scavengeCache() {
        for (CacheEntry cacheEntry = this.tail.prior; cacheEntry != this.head && this.cache.size() > this.cacheSize; cacheEntry = cacheEntry.prior) {
            if (cacheEntry.value.getRefCount() == 0) {
                cacheEntry.unlink();
                this.free.add(cacheEntry.value);
                this.cache.remove(cacheEntry.key);
            }
        }
    }
}

package net.sourceforge.jtds.jdbc;

public class Semaphore {
    protected long permits;

    public Semaphore(long j) {
        this.permits = j;
    }

    public void acquire() throws InterruptedException {
        if (!Thread.interrupted()) {
            synchronized (this) {
                while (this.permits <= 0) {
                    try {
                        wait();
                    } catch (InterruptedException e) {
                        notify();
                        throw e;
                    } catch (Throwable th) {
                        throw th;
                    }
                }
                this.permits--;
            }
            return;
        }
        throw new InterruptedException();
    }

    public boolean attempt(long j) throws InterruptedException {
        if (!Thread.interrupted()) {
            synchronized (this) {
                if (this.permits > 0) {
                    this.permits--;
                    return true;
                } else if (j <= 0) {
                    return false;
                } else {
                    try {
                        long currentTimeMillis = System.currentTimeMillis();
                        long j2 = j;
                        do {
                            wait(j2);
                            if (this.permits > 0) {
                                this.permits--;
                                return true;
                            }
                            j2 = j - (System.currentTimeMillis() - currentTimeMillis);
                        } while (j2 > 0);
                        return false;
                    } catch (InterruptedException e) {
                        notify();
                        throw e;
                    }
                }
            }
        } else {
            throw new InterruptedException();
        }
    }

    public synchronized void release() {
        this.permits++;
        notify();
    }

    public synchronized void release(long j) {
        if (j >= 0) {
            this.permits += j;
            for (long j2 = 0; j2 < j; j2++) {
                notify();
            }
        } else {
            throw new IllegalArgumentException("Negative argument");
        }
    }

    public synchronized long permits() {
        return this.permits;
    }
}

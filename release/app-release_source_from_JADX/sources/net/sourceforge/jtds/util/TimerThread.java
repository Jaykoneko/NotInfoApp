package net.sourceforge.jtds.util;

import java.util.LinkedList;
import java.util.ListIterator;

public class TimerThread extends Thread {
    private static TimerThread instance;
    private long nextTimeout;
    private final LinkedList timerList = new LinkedList();

    public interface TimerListener {
        void timerExpired();
    }

    private static class TimerRequest {
        final TimerListener target;
        final long time;

        TimerRequest(int i, TimerListener timerListener) {
            if (i > 0) {
                this.time = System.currentTimeMillis() + ((long) i);
                this.target = timerListener;
                return;
            }
            StringBuilder sb = new StringBuilder();
            sb.append("Invalid timeout parameter ");
            sb.append(i);
            throw new IllegalArgumentException(sb.toString());
        }
    }

    public static synchronized TimerThread getInstance() {
        TimerThread timerThread;
        synchronized (TimerThread.class) {
            if (instance == null) {
                TimerThread timerThread2 = new TimerThread();
                instance = timerThread2;
                timerThread2.start();
            }
            timerThread = instance;
        }
        return timerThread;
    }

    public TimerThread() {
        super("jTDS TimerThread");
        setDaemon(true);
    }

    public void run() {
        synchronized (this.timerList) {
            boolean z = true;
            while (z) {
                while (true) {
                    try {
                        long currentTimeMillis = this.nextTimeout - System.currentTimeMillis();
                        if (currentTimeMillis <= 0) {
                            if (this.nextTimeout != 0) {
                                break;
                            }
                        }
                        LinkedList linkedList = this.timerList;
                        if (this.nextTimeout == 0) {
                            currentTimeMillis = 0;
                        }
                        linkedList.wait(currentTimeMillis);
                    } catch (InterruptedException unused) {
                        z = false;
                        this.timerList.clear();
                    }
                }
                long currentTimeMillis2 = System.currentTimeMillis();
                while (true) {
                    if (this.timerList.isEmpty()) {
                        break;
                    }
                    TimerRequest timerRequest = (TimerRequest) this.timerList.getFirst();
                    if (timerRequest.time > currentTimeMillis2) {
                        break;
                    }
                    timerRequest.target.timerExpired();
                    this.timerList.removeFirst();
                }
                updateNextTimeout();
            }
        }
    }

    public Object setTimer(int i, TimerListener timerListener) {
        TimerRequest timerRequest = new TimerRequest(i, timerListener);
        synchronized (this.timerList) {
            if (!this.timerList.isEmpty()) {
                if (timerRequest.time < ((TimerRequest) this.timerList.getLast()).time) {
                    ListIterator listIterator = this.timerList.listIterator();
                    while (true) {
                        if (!listIterator.hasNext()) {
                            break;
                        }
                        if (timerRequest.time < ((TimerRequest) listIterator.next()).time) {
                            listIterator.previous();
                            listIterator.add(timerRequest);
                            break;
                        }
                    }
                } else {
                    this.timerList.addLast(timerRequest);
                }
            } else {
                this.timerList.add(timerRequest);
            }
            if (this.timerList.getFirst() == timerRequest) {
                this.nextTimeout = timerRequest.time;
                this.timerList.notifyAll();
            }
        }
        return timerRequest;
    }

    public boolean cancelTimer(Object obj) {
        boolean remove;
        TimerRequest timerRequest = (TimerRequest) obj;
        synchronized (this.timerList) {
            remove = this.timerList.remove(timerRequest);
            if (this.nextTimeout == timerRequest.time) {
                updateNextTimeout();
            }
        }
        return remove;
    }

    public static synchronized void stopTimer() {
        synchronized (TimerThread.class) {
            if (instance != null) {
                instance.interrupt();
                instance = null;
            }
        }
    }

    public boolean hasExpired(Object obj) {
        boolean z;
        TimerRequest timerRequest = (TimerRequest) obj;
        synchronized (this.timerList) {
            z = !this.timerList.contains(timerRequest);
        }
        return z;
    }

    private void updateNextTimeout() {
        this.nextTimeout = this.timerList.isEmpty() ? 0 : ((TimerRequest) this.timerList.getFirst()).time;
    }
}

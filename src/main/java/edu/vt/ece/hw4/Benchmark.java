package edu.vt.ece.hw4;

import edu.vt.ece.hw4.barriers.Barrier;
import edu.vt.ece.hw4.barriers.TTASBarrier;
import edu.vt.ece.hw4.bench.*;
import edu.vt.ece.hw4.locks.ALock;
import edu.vt.ece.hw4.locks.BackoffLock;
import edu.vt.ece.hw4.locks.Lock;

public class Benchmark {

    private static final String ALOCK = "ALock";
    private static final String BACKOFFLOCK = "BackoffLock";

    public static void main(String[] args) throws Exception {
        String mode = args[0];
        String lockClass = (args.length <= 1 ? ALOCK : args[1]);
        int threadCount = (args.length <= 2 ? 16 : Integer.parseInt(args[2]));
        int totalIters = (args.length <= 3 ? 64000 : Integer.parseInt(args[3]));
        int iters = totalIters / threadCount;

        Lock lock = null;
        switch (lockClass.trim()) {
            case ALOCK:
                lock = new ALock(threadCount);
                break;
            case BACKOFFLOCK:
                lock = new BackoffLock(args[3]);
                break;
        }

        switch (mode.trim().toLowerCase()) {
            case "normal":
                final Counter counter = new SharedCounter(0, lock);
                for (int i = 0; i < 2; i++) {
                    runNormal(counter, threadCount, iters);
                }
                break;
            case "empty":
                for (int i = 0; i < 2; i++) {
                    runEmptyCS(lock, threadCount, iters);
                }
                break;
            case "long":
                for (int i = 0; i < 2; i++) {
                    runLongCS(lock, threadCount, iters);
                }
                break;
            case "barrier":
                Barrier b = new TTASBarrier();
                throw new UnsupportedOperationException("Complete this.");
            default:
                throw new UnsupportedOperationException("Implement this");
        }

    }

    private static void runNormal(Counter counter, int threadCount, int iters) throws Exception {

        final TestThread[] threads = new TestThread[threadCount];
        TestThread.reset();

        for(int t=0; t<threadCount; t++) {
            threads[t] = new TestThread(counter, iters);
        }

        for(int t=0; t<threadCount; t++) {
            threads[t].start();
        }

        long totalTime = 0;
        for(int t=0; t<threadCount; t++) {
            threads[t].join();
            totalTime += threads[t].getElapsedTime();
        }

        System.out.println("Average time per thread is " + totalTime/threadCount + "ms");
    }

    private static void runEmptyCS(Lock lock, int threadCount, int iters) throws Exception {

        final EmptyCSTestThread[] threads = new EmptyCSTestThread[threadCount];
        TestThread.reset();

        for (int t = 0; t < threadCount; t++) {
            threads[t] = new EmptyCSTestThread(lock, iters);
        }

        for (int t = 0; t < threadCount; t++) {
            threads[t].start();
        }

        long totalTime = 0;
        for (int t = 0; t < threadCount; t++) {
            threads[t].join();
            totalTime += threads[t].getElapsedTime();
        }

        System.out.println("Average time per thread is " + totalTime / threadCount + "ms");
    }

    static void runLongCS(Lock lock, int threadCount, int iters) throws Exception {
        final Counter counter = new Counter(0);
        final LongCSTestThread[] threads = new LongCSTestThread[threadCount];
        TestThread.reset();

        for (int t = 0; t < threadCount; t++) {
            threads[t] = new LongCSTestThread(lock, counter, iters);
        }

        for (int t = 0; t < threadCount; t++) {
            threads[t].start();
        }

        long totalTime = 0;
        for (int t = 0; t < threadCount; t++) {
            threads[t].join();
            totalTime += threads[t].getElapsedTime();
        }

        System.out.println("Average time per thread is " + totalTime / threadCount + "ms");
    }
}

package com.devspark.examples.concurrency;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.concurrent.locks.StampedLock;

/**
 * Some examples of different lock idioms.
 */
public class LockExample {

	/**
	 * This class shows how multiple locks can be acquired,
	 * and how the language lexically restricts how the locks
	 * are released (in inverse order)
	 */
	public static class MultiSynchronized {
		private Object monitor1 = new Object();
		private Object monitor2 = new Object();

		public void doSomethingNotThreadSafe() {
			synchronized (monitor1) {
				System.out.println("Entering first block");
				synchronized (monitor2) {
					//Now it would be safe to unlock the first monitor, but no can do.
					System.out.println("Inside the second block");
				}
			}
		}
	}

	/**
	 * This class shows how {@link Lock} instances allows more
	 * flexibility on how to obtain and release locks.
	 * <p>
	 * This example is for illustrative purposes only, don't do this in
	 * real world code.
	 */
	public static class MultiLock{
		private Lock monitor1 = new ReentrantLock();
		private Lock monitor2 = new ReentrantLock();

		public void doSomethingNotThreadSafe() {
			monitor1.lock();
			try {
				System.out.println("Entering first block");
				monitor2.lock();
				try {
					System.out.println("Inside the second block");
					monitor1.unlock();
				} finally {
					monitor2.unlock();
				}
			} finally {
				try {
					monitor1.unlock();
				} catch (IllegalMonitorStateException e) {
					//Already unlocked
				}
			}
		}
	}

	/**
	 * This example uses a {@link ReadWriteLock} to synchronize
	 * access to it's state.
	 * <p>
	 * Multiple read threads can concurrently fetch the value but
	 * will be blocked if a thread acquires a write block.
	 * <p>
	 * Note that depending on the JVM, threads trying to acquire
	 * the write lock will suffer starvation under heavy read load.
	 */
	public static class ReadWriteLockExample {
		private ReadWriteLock lock = new ReentrantReadWriteLock();
		private int state;

		public int readState() {
			lock.readLock().lock();
			try {
				return state;
			} finally {
				lock.readLock().unlock();
			}
		}

		public void writeLock(int newState) {
			lock.writeLock().lock();
			try {
				this.state = newState;
			} finally {
				lock.writeLock().unlock();
			}
		}
	}

	/**
	 * This {@link StampedLock} example attemps to do an optimistic
	 * read before trying to acquire the (blocking) read lock to read the value.
	 */
	public static class StampedLockExample {
		private StampedLock lock = new StampedLock();
		private int state;

		public int readState() {
			//Attempt to do an optimistic read.
			long stamp = lock.tryOptimisticRead();
			//Read the value to a local variable
			int value = state;
			//Validate if the read lock is still valid
			//(i.e. no other thread acquired the write lock
			//between the read lock acquisition and this like)
			if (!lock.validate(stamp)) {
				//Somebody changed the value, do pessimistic read.
				stamp = lock.readLock();
				try {
					// Happens before relationship stablished.
					value = state;
				} finally {
					lock.unlockRead(stamp);
				}
			}
			return value;
		}

		/**
		 * Same old pessimistic locking, different idiom.
		 */
		public void writeLock(int newState) {
			long stamp = lock.writeLock();
			try {
				this.state = newState;
			} finally {
				lock.unlock(stamp);
			}
		}
	}
}

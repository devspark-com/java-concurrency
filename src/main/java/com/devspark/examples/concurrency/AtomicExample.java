package com.devspark.examples.concurrency;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * This class exemplifies the basics of atomic classes usage.
 */
public class AtomicExample {

	/**
	 * A counter that is shared among threads. This is the
	 * most basic synchronization type. 
	 */
	public static class SynchronizedCounter {
		private int counter = 0;

		public synchronized int nextValue() {
			return ++counter;
		}

		public synchronized int currentValue() {
			return counter;
		}
	}

	/**
	 * Same counter, implemented with a volatile variable.
	 * Not that since the increment operation is not atomic,
	 * this class is not thread safe and will produce incorrect
	 * results.
	 */
	public static class VolatileCounter {
		private volatile int counter = 0;

		public int nextValue() {
			return ++counter;
		}

		public int currentValue() {
			return counter;
		}		
	}

	/**
	 * The counter implemented using an {@link AtomicInteger}.
	 * Since all the operations on it are atomic, there is no
	 * need for synchronization.
	 */
	public static class AtomicCounter {
		private AtomicInteger counter = new AtomicInteger(0);

		public int nextValue() {
			return counter.incrementAndGet();
		}

		public int currentValue() {
			return counter.get();
		}
	}

}

package com.devspark.examples.concurrency;

import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.BlockingQueue;

/**
 * Shows the use of the synchronized and volatile keywords.
 */
public class BasicConcurrencyExample {

	/**
	 * A class with two variables (one static, another an instance) that
	 * can be safely shared between threads.
	 */
	public static class SynchronizedPerson {
		private static int population = 0;
		private String name;
		
		public synchronized static int getPopulation() {
			return population;
		}
		public static void setPopulation(int population) {
			//Note the different idiom (synchronized block)
			//but the lock object is the same, so it's equivalent
			//to the previous method.
			synchronized(SynchronizedPerson.class) {
				SynchronizedPerson.population = population;
			}
		}

		public synchronized String getName() {
			return name;
		}
		public void setName(String name) {
			//Idem above
			synchronized (this) {
				this.name = name;
			}
		}
	}

	/**
	 * Same person, but with volatile variables.
	 * <p>
	 * Volatile variables guarantee a happens before relationship
	 * between a write an subsequent reads. Also prevents the JVM
	 * from caching a variable value in a thread's stack, avoiding
	 * visibility issues.
	 * <p>
	 * This class can be shared between differnt threads, without
	 * any lock contention.
	 */
	public static class VolailePerson {
		private volatile static int population = 0;
		private volatile String name;
		
		public static int getPopulation() {
			return population;
		}
		public static void setPopulation(int population) {
			SynchronizedPerson.population = population;
		}

		public String getName() {
			return name;
		}
		public void setName(String name) {
			this.name = name;
		}
	}

	/**
	 * A point, which has x and y coordinates, with an invariant
	 * when moving (it has to move in both dimensions at the same time).
	 * <p>
	 * This class is thread safe.
	 */
	public static class SynchronizedPoint {
		private int x;
		private int y;
		public SynchronizedPoint(int x, int y) {
			this.x = x;
			this.y = y;
		}

		public synchronized void moveTo(int x, int y) {
			this.x = x;
			this.y = y;
		}

		public synchronized int getX() {
			return x;
		}

		public synchronized int getY() {
			return y;
		}

	}

	/**
	 * Same point as above but with volatile variables.
	 * <p>
	 * Volatile variables work atomically, but cannot
	 * guarantee that the invariant is maintained.
	 * <p>
	 * In this implementation, the point could be moved
	 * horizontally by a thread and vertically by another,
	 * hence it's not thread safe.
	 */
	public static class VolatilePoint {
		private volatile int x;
		private volatile int y;

		public VolatilePoint(int x, int y) {
			this.x = x;
			this.y = y;
		}

		public void moveTo(int x, int y) {
			this.x = x;
			this.y = y;
		}

		public int getX() {
			return x;
		}

		public int getY() {
			return y;
		}
	}

	/**
	 * A work queue implementation using wait/notify. Note
	 * that both methods are synchronized.
	 * <p>
	 * Note that Java 5 includes {@link BlockingQueue}, which
	 * is a superior implementation.
	 */
	public class WorkQueue {
		//Linked list can remove the first element in O(1)
		private List<String> queue = new LinkedList<>();

		public synchronized void put(String object) {
			this.queue.add(object);
			//If the queue was empty, notify consumers.
			if (this.queue.size() == 1) {
				this.notifyAll();
			}
		}

		public synchronized String get() {
			//Check if the queue has elements.
			//Needs to be done in a loop since the
			//interruption need not come from a notify
			while (this.queue.size() == 0) {
				try {
					this.wait();
				} catch (InterruptedException e) {
					//Be a good citizen.
					Thread.currentThread().interrupt();
				}
			}
			//Get he first element, if the queue is empty
			//after this, other waiting threads will
			//continue to wait.
			return queue.remove(0);
		}
	}
}

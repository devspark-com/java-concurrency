package com.devspark.examples.concurrency;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

/**
 * Basic example of an {@link ExecutorService} usage.
 */
public class ExecutorServiceExample {

	/**
	 *  This class uses a fixed thread pool executor to submit
	 *  {@link Runnable} and {@link Callable} instances.
	 *  <p>
	 *  Note the use of the {@link Future} to obtain the result (or
	 *  block until completion).
	 */
	public static class FixedThreadPoolExecutor {
		private ExecutorService service = Executors.newFixedThreadPool(5);

		public void runWithoutResult() throws InterruptedException {
			Runnable task = () -> {System.out.println("I ran!");};
			Future<?> f = service.submit(task);
			try {
				f.get();
			} catch (ExecutionException e) {
				throw new RuntimeException(e);
			}
		}

		public String runWithResult() throws ExecutionException, InterruptedException {
			Future<String> f = service.submit(() -> {
				System.out.println("Returning something");
				return "Something";
			});
			return f.get();
		}
	}
}

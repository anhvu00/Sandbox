package com.kyron;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.stream.Collectors;


public class MainApp {
	// how many processors do i have?
	public static final int CPUs = Runtime.getRuntime().availableProcessors();
	private static MyCallable mc;
	
	public static void main(String[] args) {

		System.out.format("Available CPU's = %d\n", CPUs);
		/*
		 * List<String> list = Arrays.asList("a", "c", "e", "b");
		 * System.out.println("Input=" + list); collectionSort(list);
		 * System.out.println("Collection.sort(list)=" + list); lambdaSort(list);
		 * System.out.println("lambda sort=" + list);
		 * System.out.println("Runnable output:"); doRunnable1();
		 * System.out.println("Executor output:"); doExecutor1();
		 * System.out.println("Callable output:"); doCallable1();
		 * System.out.println("Callable2 output:"); doCallable2();
		 */
		System.out.println("Test speed output:");
		doTestSpeed1();
		System.out.println("Test speed concurrentmap:");
		doConcurrentMap1();
		System.out.println("Test speed 2 output:");
		doTestSpeed2();
		
		doTestStream();
	}
	
	// test stream stuff
	public static void doTestStream() {
		Car redCar = new Car("RED");
		Car blueCar = new Car("BLUE");
		List<Car> list = Arrays.asList(redCar, blueCar);
		List<Car> newList = list.stream().map(c -> new Car("YELLOW")).collect(Collectors.toList());
		for( Car c : newList) {
			System.out.println(c.getColor());
		}
	}

	public static void lambdaSort(List<String> list) {
		list.sort((a, b) -> b.compareTo(a));
	}

	public static void collectionSort(List<String> list) {
		Collections.sort(list);
	}

	/*
	 * Simple Thread with Runnable Interface Note: - Add concrete function to an
	 * Interface
	 */
	public static void doRunnable1() {
		Runnable task = () -> {
			String threadName = Thread.currentThread().getName();
			System.out.println("Hello " + threadName);
		};

		// call Interface lambda method directly...
		task.run(); // Hello main

		Thread thread = new Thread(task);
		thread.start(); // Hello Thread-0 - cant tell order of finish execution

		System.out.println("Done!");
	}

	/*
	 * Use Executor, a higher level of running threads Note: - Must be stopped
	 * explicitly - shutdown() = wait for finish - shutdownNow() = just stop
	 */
	public static void doExecutor1() {
		ExecutorService executor = Executors.newSingleThreadExecutor();
		executor.submit(() -> {
			String threadName = Thread.currentThread().getName();
			System.out.println("Hello " + threadName);
		});
		try {
			System.out.println("attempt to shutdown executor");
			executor.shutdown();
			executor.awaitTermination(5, TimeUnit.SECONDS);
		} catch (InterruptedException e) {
			System.err.println("tasks interrupted");
		} finally {
			if (!executor.isTerminated()) {
				System.err.println("cancel non-finished tasks");
			}
			executor.shutdownNow();
			System.out.println("shutdown finished");
		}
	}

	/*
	 * Executor can run task Callable (not just Runnable). Callables are Function
	 * Interface returning a value (while Runnable does not/void) - Must use Future
	 * b/c submit() doesn't wait for result (the return value)
	 */
	public static void doCallable1() {
		// define task
		Callable<Integer> task = () -> {
			try {
				TimeUnit.SECONDS.sleep(1);
				return 123;
			} catch (InterruptedException e) {
				throw new IllegalStateException("task interrupted", e);
			}
		};
		// call with Future
		ExecutorService executor = Executors.newFixedThreadPool(1);
		Future<Integer> future = executor.submit(task);

		System.out.println("future done? " + future.isDone());

		Integer result;
		try {
			result = future.get();
			System.out.println("future done? " + future.isDone());
			System.out.println("result: " + result);
		} catch (InterruptedException | ExecutionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		// shutdown executor
		try {
			System.out.println("attempt to shutdown executor");
			executor.shutdown();
			executor.awaitTermination(5, TimeUnit.SECONDS);
		} catch (InterruptedException e) {
			System.err.println("tasks interrupted");
		} finally {
			if (!executor.isTerminated()) {
				System.err.println("cancel non-finished tasks");
			}
			executor.shutdownNow();
			System.out.println("shutdown finished");
		}

	}

	/*
	 * Future with time out Note: - new fix thread pool 1 =
	 * newSingleThreadExecutor()
	 * 
	 */
	public static void doCallable2() {
		ExecutorService executor = Executors.newFixedThreadPool(1);

		Future<Integer> future = executor.submit(() -> {
			TimeUnit.SECONDS.sleep(2); // can cause time out;
			return 999;
		});

		try {
			// call with 1 second time out setting
			System.out.println("result=" + future.get(3, TimeUnit.SECONDS));
		} catch (InterruptedException | ExecutionException | TimeoutException e) {
			e.printStackTrace();
		}
		try {
			System.out.println("attempt to shutdown executor");
			executor.shutdown();
			executor.awaitTermination(5, TimeUnit.SECONDS);
		} catch (InterruptedException e) {
			System.err.println("tasks interrupted");
		} finally {
			if (!executor.isTerminated()) {
				System.err.println("cancel non-finished tasks");
			}
			executor.shutdownNow();
			System.out.println("shutdown finished");
		}
	}

	/*
	 * Usable code here...
	 */
	public static void doInvokeAll1(ExecutorService executor) {
		// ? what is this newWorkStealing sht?
		// ExecutorService executor = Executors.newWorkStealingPool();
		// ExecutorService executor = Executors.newCachedThreadPool();
		//ExecutorService executor = Executors.newFixedThreadPool(maxThreads); passed in now

		List<Callable<String>> callables = Arrays.asList(() -> primeNumbersBruteForce(10000),
				() -> primeNumbersBruteForce(10000), () -> primeNumbersBruteForce(10000),
				() -> primeNumbersBruteForce(10000), () -> primeNumbersBruteForce(10000),
				() -> primeNumbersBruteForce(10000), () -> primeNumbersBruteForce(10000),
				() -> primeNumbersBruteForce(10000), () -> primeNumbersBruteForce(10000),
				() -> primeNumbersBruteForce(10000), () -> primeNumbersBruteForce(10000),
				() -> primeNumbersBruteForce(10000));

		try {
			executor.invokeAll(callables)
				.parallelStream()
				.map(future -> {
					try {
						return future.get();
					} catch (Exception e) {
						throw new IllegalStateException(e);
					}
			});
			// .forEach(System.out::println);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	/*
	 * - get all available cores
	 * - invokeall and return List<Future>
	 * - could try send in newCachedThreadPool or newFixedThreadPool
	 */
	public static List<Future<String>> doInvokeAll2(ExecutorService executor) {
		List<Future<String>> resultList = null;
		List<Callable<String>> callables = Arrays.asList(() -> primeNumbersBruteForce(10000),
				() -> primeNumbersBruteForce(10000), () -> primeNumbersBruteForce(10000),
				() -> primeNumbersBruteForce(10000), () -> primeNumbersBruteForce(10000),
				() -> primeNumbersBruteForce(10000), () -> primeNumbersBruteForce(10000),
				() -> primeNumbersBruteForce(10000), () -> primeNumbersBruteForce(10000),
				() -> primeNumbersBruteForce(10000), () -> primeNumbersBruteForce(10000),
				() -> primeNumbersBruteForce(10000));
		try {
			resultList = executor.invokeAll(callables);
		} catch (InterruptedException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		return resultList;
		// get result 
	}
	


	/*
	 * Util to generate prime number, taking some processing time to test threads.
	 */
	public static void doPrintPrimeNumbers(int max) {
		for (int i = 2; i <= max; i++) {
			if (isPrimeBruteForce(i)) {
				System.out.println("prime number = " + i);
			}
		}
	}

	public static String primeNumbersBruteForce(int n) {
		List<Integer> primeNumbers = new LinkedList<>();
		for (int i = 2; i <= n; i++) {
			if (isPrimeBruteForce(i)) {
				primeNumbers.add(i);
			}
		}
		return primeNumbers.toString();
	}

	public static boolean isPrimeBruteForce(int number) {
		for (int i = 2; i < number; i++) {
			if (number % i == 0) {
				return false;
			}
		}
		return true;
	}

	/*
	 * Test speed before/after concurrency 
	 */
	public static void doTestSpeed1() {
		// sequential calls
		long startTime = System.nanoTime();
		for (int i=0; i<12; i++) {
			primeNumbersBruteForce(10000);			
		}
		long endTime = System.nanoTime();

		long duration = (endTime - startTime)/100_000;
		System.out.println("sequential duration = " + duration);

		// create exec service just once.
		ExecutorService executor = Executors.newFixedThreadPool(CPUs);
		// 1st concurrency 
		startTime = System.nanoTime();
		doInvokeAll1(executor);
		endTime = System.nanoTime();
		duration = (endTime - startTime)/100_000;
		System.out.println("1st InvokeAll duration = " + duration);
		// 2nd concurrency 
		startTime = System.nanoTime();
		doInvokeAll1(executor);
		endTime = System.nanoTime();
		duration = (endTime - startTime)/100_000;
		System.out.println("2nd InvokeAll duration = " + duration);
		// 3rd concurrency 
		startTime = System.nanoTime();
		doInvokeAll1(executor);
		endTime = System.nanoTime();
		duration = (endTime - startTime)/100_000;
		System.out.println("3rd InvokeAll duration = " + duration);
		
		// try something new...
		startTime = System.nanoTime();
		List<Future<String>> list = doInvokeAll2(executor);
		endTime = System.nanoTime();
		System.out.println("4th InvokeAll(2) no get() duration = " + duration);
		
		// 5th concurrency 
		startTime = System.nanoTime();
		list = doInvokeAll3(executor);
		endTime = System.nanoTime();
		duration = (endTime - startTime)/100_000;
		System.out.println("5th InvokeAll(3) duration = " + duration);

		// shutdown
		try {
			System.out.println("attempt to shutdown executor");
			executor.shutdown();
			executor.awaitTermination(5, TimeUnit.SECONDS);
		} catch (InterruptedException e) {
			System.err.println("tasks interrupted");
		} finally {
			if (!executor.isTerminated()) {
				System.err.println("cancel non-finished tasks");
			}
			executor.shutdownNow();
			System.out.println("shutdown finished");
		}
		
		/* in case you want to print the 5th results.... */
		if (list != null) {
			for (Future<String> f : list) {
				String res = null;
				try {
					res = f.get();
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (ExecutionException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				System.out.println(res);
			}
		} 
		
		
		System.out.println("----------");
	}
	
	// more complex callable objects
	public static void doTestSpeed2() {
		// sequential calls
		Car c = new Car("BLUE");
		long startTime = System.nanoTime();
		// loop to call something n times
		for (int i=0; i<5; i++) {
			try {
				c.findCar("BLUE");
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		long endTime = System.nanoTime();

		System.out.println("Sequential duration = " + getDuration(startTime, endTime));

		// create exec service just once.
		ExecutorService executor = Executors.newFixedThreadPool(CPUs);
		
		// 1st concurrency 
		startTime = System.nanoTime();
		doInvokeAll4(executor);
		endTime = System.nanoTime();

		System.out.println("Concurrent duration = " + getDuration(startTime, endTime));
		System.out.println("------");
		
		shutdownService(executor);
	}
	
	/*
	 * 12/11/19
	 */
	public static List<Car> doInvokeAll4(ExecutorService executor) {
		ArrayList<Callable<Car>> callables = new ArrayList<Callable<Car>>();
		// create input tasks
		for (int i=0; i<5; i++) {
			callables.add( new MyCallableCar() );
		}
		
		List<Future<Car>> resultList = null;
		ArrayList<Car> retList = new ArrayList<Car>();

		try {
			resultList = executor.invokeAll(callables);
			
			/*executor.invokeAll(callables)
				.stream()
				.map( future -> {
					try {
						Future<Car> c = future.get();
					} catch (Exception e) {
						throw new IllegalStateException(e);
					}
			});
			// .forEach(System.out::println);*/
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		// loop thru resultList

		if (resultList != null) {
			for (Future<Car> future : resultList) {
				try {
					Car c = future.get();
					retList.add(c);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (ExecutionException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}		
		}

		return retList;
	}
	
	// wrapper
	static class MyCallableCar implements Callable<Car> {
		Car c = new Car();
		@Override
		public Car call() throws Exception {
			Car retval = c.findCar("BLUE");
			return retval;
		}
		
	}

	private static void shutdownService(ExecutorService executor) {
		// shutdown
		try {
			System.out.println("attempt to shutdown executor");
			executor.shutdown();
			executor.awaitTermination(5, TimeUnit.SECONDS);
		} catch (InterruptedException e) {
			System.err.println("tasks interrupted");
		} finally {
			if (!executor.isTerminated()) {
				System.err.println("cancel non-finished tasks");
			}
			executor.shutdownNow();
			System.out.println("shutdown finished");
		}
	}
	
	private static long getDuration(long startTime, long endTime) {
		return (endTime - startTime) / 100_000;
	}

	/*
	 * See if ConcurrentHashMap makes it faster...
	 */
	public static void doConcurrentMap1() {
		long startTime = System.nanoTime();
		Callable<String> call1 = () -> primeNumbersBruteForce(10000);
		Callable<String> call2 = () -> primeNumbersBruteForce(10000);
		Callable<String> call3 = () -> primeNumbersBruteForce(10000);
		Callable<String> call4 = () -> primeNumbersBruteForce(10000);
		Callable<String> call5 = () -> primeNumbersBruteForce(10000);
		Callable<String> call6 = () -> primeNumbersBruteForce(10000);
		Callable<String> call7 = () -> primeNumbersBruteForce(10000);
		Callable<String> call8 = () -> primeNumbersBruteForce(10000);
		Callable<String> call9 = () -> primeNumbersBruteForce(10000);
		Callable<String> call10 = () -> primeNumbersBruteForce(10000);
		Callable<String> call11 = () -> primeNumbersBruteForce(10000);
		Callable<String> call12 = () -> primeNumbersBruteForce(10000);

		ConcurrentHashMap<String, Callable<String>> map = new ConcurrentHashMap<>();
		map.putIfAbsent("1", call1);
		map.putIfAbsent("2", call2);
		map.putIfAbsent("3", call3);
		map.putIfAbsent("4", call4);
		map.putIfAbsent("5", call5);
		map.putIfAbsent("6", call6);
		map.putIfAbsent("7", call7);
		map.putIfAbsent("8", call8);
		map.putIfAbsent("9", call9);
		map.putIfAbsent("10", call10);
		map.putIfAbsent("11", call11);
		map.putIfAbsent("12", call12);
		

		ExecutorService executor = Executors.newFixedThreadPool(8);
		for (String key : map.keySet()) {
			Callable<String> c = map.get(key);
			Future<String> future = executor.submit(c);
			try {
				String result = key + "-" + future.get();
				//System.out.println(result);
			} catch (InterruptedException | ExecutionException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		long endTime = System.nanoTime();
		long duration = (endTime - startTime)/100_000;
		System.out.println("1st concurrent map duration = " + duration);
		
		// shutdown
		try {
			System.out.println("attempt to shutdown executor");
			executor.shutdown();
			executor.awaitTermination(5, TimeUnit.SECONDS);
		} catch (InterruptedException e) {
			System.err.println("tasks interrupted");
		} finally {
			if (!executor.isTerminated()) {
				System.err.println("cancel non-finished tasks");
			}
			executor.shutdownNow();
			System.out.println("shutdown finished");
		}

	}

	/*
	 * 1 pool -- reuse n times
	 * https://stackoverflow.com/questions/12197181/java-concurrency-time-
	 * performance-executors-threadgroup-or-runnable
	 */
	public static void a() {

	}
	
	/*
	 * Wrapper Callable class with ID so we can join them in order afterward.
	 */
	static class MyCallable implements Callable<String> {
		private final int ID;
		
		// constructor
		public MyCallable(int key) {
			this.ID = key;
		}
		@Override
		public String call() throws Exception {
			return (ID + "-" + primeNumbersBruteForce(10000));
		}				
	}
	
	
	/*
	 * 
	 */
	public static List<Future<String>> doInvokeAll3(ExecutorService executor) {

		List<Future<String>> resultList = null;
		
		ArrayList<Callable<String>> callableList = new ArrayList<Callable<String>>();
		// initialize with 12 tasks
		for (int i = 0; i < 12; i++) {
			MyCallable c = new MyCallable(i);
			callableList.add(c);
		}

		try {
			resultList = executor.invokeAll(callableList);

		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return resultList;
	}
	
} // end main app

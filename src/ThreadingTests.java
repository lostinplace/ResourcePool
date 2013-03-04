import static org.junit.Assert.*;

import org.junit.Test;
import java.util.*;
import java.util.concurrent.*;

import javax.activity.InvalidActivityException;

public class ThreadingTests {

	public static ResourcePool<List<String>> pool = new ResourcePool<List<String>>();
	
	private class DelayReleaseTester implements Runnable {

		public List<String> result;
		public Boolean acquired=false;
		private TimeUnit _unit;
		private long _timeout;

		public DelayReleaseTester(long timeout, TimeUnit unit) {
			_unit = unit;
			_timeout = timeout;
		}

		public DelayReleaseTester() {
			this(250, TimeUnit.MILLISECONDS);
		}

		public void run() {
			try {
				pool.open();
				result = pool.acquire();
				acquired=true;
				if (result == null) {
					fail("no result");
					return;
				}

				Thread.sleep(TimeUnit.MILLISECONDS.convert(_timeout, _unit));
				pool.release(result);

			} catch (InterruptedException e) {
				fail(e.getMessage());
			} catch (InvalidActivityException e) {
				fail(e.getMessage());
			}
		}
	}

	
	@Test
	public void AcquireBlocksUntilResourceAvailable() {
		try {
			List<String> resource = Arrays.asList("foo", "bar"), result = null;
			long startTime, endTime;
			int timeout = 1800;
			pool = new ResourcePool<List<String>>();
			pool.add(resource);
			pool.close();
			try {
				result = pool.acquire();
				fail("didn't throw exception");
			} catch (Exception e) {
				assertTrue(e instanceof InvalidActivityException);
			}

			DelayReleaseTester tester1 = new DelayReleaseTester(timeout,
					TimeUnit.MILLISECONDS);
			pool.open();
			new Thread(tester1).start();
			startTime = System.currentTimeMillis();
			while((!tester1.acquired)&&(System.currentTimeMillis()-startTime<5000));
			try {
				System.out.println();
				result = pool.acquire();
				endTime = System.currentTimeMillis();
				long duration = endTime - startTime;
				assertTrue(String.format("duration: %s timeout:%s", String.valueOf(duration), String.valueOf( timeout)), duration >= timeout);
				assertTrue(result == resource);
			} catch (InvalidActivityException e) {
				fail(e.getMessage());
			}
		} catch (Exception e) {
			fail(e.getMessage());
		}
	}
	
	@Test
	public void AcquireReturnsNullOnTimeout() throws InvalidActivityException, InterruptedException{
		ResourcePool<String> tmpPool = new ResourcePool<String>();
		String result;
		long startTime, endTime, timeout=1500;
		tmpPool.open();
		startTime=System.currentTimeMillis();
		result = tmpPool.acquire(timeout, TimeUnit.MILLISECONDS);
		endTime=System.currentTimeMillis();
		assertTrue(result==null);
		assertTrue(endTime-startTime>= timeout);
	}
	
	@Test
	public void RemoveBlocksUntilResourceNotInUse() throws InvalidActivityException, InterruptedException{
		List<String> resource = Arrays.asList("foo", "bar");
		long startTime, endTime;
		Boolean result;
		int timeout = 1800;
		pool = new ResourcePool<List<String>>();
		pool.add(resource);
		
		
		DelayReleaseTester tester1 = new DelayReleaseTester(timeout,TimeUnit.MILLISECONDS);
		new Thread(tester1).start();
		startTime=System.currentTimeMillis();
		while((!tester1.acquired)&&(System.currentTimeMillis()-startTime<5000));
		
		new Thread(tester1).start();
		result=pool.remove(resource);
		endTime=System.currentTimeMillis();
		long duration = endTime - startTime;
		assertTrue("record not managed", result);
		assertTrue(String.format("duration: %s timeout:%s", String.valueOf(duration), String.valueOf( timeout)), duration >= timeout);
		
	}
	
	@Test
	public void RemoveNowDoesntWait() throws InvalidActivityException, InterruptedException{
		List<String> resource = Arrays.asList("foo", "bar");
		long startTime, endTime;
		Boolean result;
		int timeout = 1800;
		pool = new ResourcePool<List<String>>();
		pool.add(resource);
		DelayReleaseTester tester1 = new DelayReleaseTester(timeout,TimeUnit.MILLISECONDS);
		
		new Thread(tester1).start();
		startTime=System.currentTimeMillis();
		while((!tester1.acquired)&&(System.currentTimeMillis()-startTime<5000));
		result=pool.removeNow(resource);
		endTime=System.currentTimeMillis();
		long duration = endTime - startTime;
		assertTrue(result);
		assertTrue(String.format("duration: %s timeout:%s", String.valueOf(duration), String.valueOf( timeout)), duration <= timeout);
		
	}
	
	@Test
	public void CloseBlocksUntilAllResourcesReleased() throws InvalidActivityException, InterruptedException{
		List<String> resource = Arrays.asList("foo", "bar");
		long startTime, endTime;
		Boolean result;
		int timeout = 1800;
		pool = new ResourcePool<List<String>>();
		pool.add(resource);
		DelayReleaseTester tester1 = new DelayReleaseTester(timeout,TimeUnit.MILLISECONDS);
		new Thread(tester1).start();
		startTime=System.currentTimeMillis();
		while((!tester1.acquired)&&(System.currentTimeMillis()-startTime<5000));
		pool.close();
		endTime=System.currentTimeMillis();
		long duration = endTime - startTime;
		assertTrue(!pool.isOpen());
		assertTrue(String.format("duration: %s timeout:%s", String.valueOf(duration), String.valueOf( timeout)), duration >= timeout);
		
	}
	
	@Test
	public void CloseNowDoesntWait() throws InvalidActivityException, InterruptedException{
		List<String> resource = Arrays.asList("foo", "bar");
		long startTime, endTime;
		Boolean result;
		int timeout = 1800;
		pool = new ResourcePool<List<String>>();
		pool.add(resource);
		DelayReleaseTester tester1 = new DelayReleaseTester(timeout,TimeUnit.MILLISECONDS);
		
		new Thread(tester1).start();
		startTime=System.currentTimeMillis();
		while((!tester1.acquired)&&(System.currentTimeMillis()-startTime<5000));
		pool.closeNow();
		endTime=System.currentTimeMillis();
		long duration = endTime - startTime;
		assertTrue(String.format("duration: %s timeout:%s", String.valueOf(duration), String.valueOf( timeout)), duration <= timeout);
		
	}
	
	
}

import static org.junit.Assert.*;

import java.sql.Date;

import javax.activity.InvalidActivityException;

import org.junit.Test;
import java.util.*;

public class BasicTests {

	@Test
	public void ResourcePoolCanAdd() {
		ResourcePool<String> pool = new ResourcePool<String>();
		String before="";
		String after="";
		
		try {
			pool.open();
			
			pool.add("hello");
			after = pool.acquire();
		} catch (InvalidActivityException e) {
			
			fail("Error on acquire");
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			fail("failed from interrupt");
		}
		assertTrue(after=="hello");
	}
	
	@Test	
	public void ResourcesCanBeCheckedOut() throws InterruptedException, InvalidActivityException {
		String first=null,
				second=null; 
		ResourcePool<String> pool = new ResourcePool<String>();
		pool.open();
		pool.add("test1");
		pool.add("test2");
		try {
			first =pool.acquire();
			second = pool.acquire();
		} catch (InvalidActivityException e) {
			// TODO Auto-generated catch block
			fail(e.getMessage());
		} catch (InterruptedException e) {
			fail("failed from interrupt");
		}
		assertTrue(first=="test1");
		assertTrue(second=="test2");
	}
	
	@Test
	public void CheckedOutResourcesCanBeCheckedIn() throws InterruptedException, InvalidActivityException
	{
		String first=null; 
		ResourcePool<String> pool = new ResourcePool<String>();
		pool.open();
		pool.add("test1");
		try {
			first =pool.acquire();
			assertTrue(first=="test1");
			pool.release(first);
			first =pool.acquire();
			assertTrue(first=="test1");
		} catch (InvalidActivityException e) {
 
			fail(e.getMessage());
		} catch (InterruptedException e) {
			fail("failed from interrupt");
		}
	}
	
	@Test
	public void ResourcesMayBeRemoved() throws InterruptedException, InvalidActivityException{
		String first=null; 
		ResourcePool<String> pool = new ResourcePool<String>();
		pool.open();
		pool.add("test1");
		try {
			first =pool.acquire();
			assertTrue(first=="test1");
			pool.release(first);
			assertTrue(pool.remove(first));
		} catch (InvalidActivityException e) {
			fail(e.getMessage());
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			fail(e.getMessage());
		}
	}

	@Test
	public void NoAcquireUnlessOpen() throws InterruptedException, InvalidActivityException
	{
		ResourcePool<String> pool = new ResourcePool<String>();
		pool.open();
		pool.add("test");
		pool.close();
		try {
			pool.acquire();
			fail("didn't throw exception on acquiring from closed pool");
		} catch (InvalidActivityException e) {
			assertTrue( e.getMessage()== "The ResourcePool is closed. You cannot acquire from a closed ResourcePool");
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			fail("failed from interrupt");
		}
	}
	
	@Test
	public void AddReportsFalseOnDuplicates() throws InvalidActivityException, InterruptedException
	{
		ResourcePool<String> pool = new ResourcePool<String>();
		String item = "tester";
		Boolean result1, result2;
		pool.open();
		result1 = pool.add(item);
		result2= pool.add(item);
		assertTrue(result1);
		assertTrue(!result2);
	}
	
	@Test
	public void RemoveReportsFalseForUnmanaged() throws InterruptedException{
		ResourcePool<String> pool = new ResourcePool<String>();
		String item = "tester";
		Boolean result1, result2;
		pool.add(item);
		result1 = pool.remove(item);
		result2 = pool.remove(item);
		assertTrue(result1);
		assertTrue(!result2);
	}
	
	@Test
	public void CanOnlyReleaseAcquiredResources() throws InterruptedException{
		ResourcePool<String> pool = new ResourcePool<String>();
		String item = "tester";
		try {
			pool.release(item);
		} catch (InvalidActivityException e) {
			assertTrue(e.getMessage() ==
					"The ResourcePool has not been used to acquire the specified resource.  You can only release resources that were acquired through the pool" );
		}
		pool.add(item);
		try {
			pool.release(item);
		} catch (InvalidActivityException e) {
			assertTrue(e.getMessage() ==
					"The ResourcePool has not been used to acquire the specified resource.  You can only release resources that were acquired through the pool" );
		}
	}
}

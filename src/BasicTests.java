import static org.junit.Assert.*;

import javax.activity.InvalidActivityException;

import org.junit.Test;


public class BasicTests {
	
	
	@Test
	public void testResourcePool() {
		fail("Not yet implemented");
	}

	@Test
	public void testOpen() {
		ResourcePool<String> pool = new ResourcePool<String>();
		pool.open();
		assertTrue(pool.isOpen());
	}

	@Test
	public void testIsOpen() {
		ResourcePool<String> pool = new ResourcePool<String>();
		assertTrue(!pool.isOpen());
	}

	@Test
	public void testClose() {
		ResourcePool<String> pool = new ResourcePool<String>();
		assertTrue(!pool.isOpen());
		pool.open();
		assertTrue(pool.isOpen());
		pool.close();
		assertTrue(!pool.isOpen());
	}

	@Test
	public void testCloseNow() {
		ResourcePool<String> pool = new ResourcePool<String>();
		assertTrue(!pool.isOpen());
		pool.open();
		assertTrue(pool.isOpen());
		pool.close();
		assertTrue(!pool.isOpen());
	}

	@Test
	public void testAdd() {
		ResourcePool<String> pool = new ResourcePool<String>();
		String before="";
		String after="";
		
		try {
			pool.open();
			before =pool.acquire();
			pool.add("hello");
			after = pool.acquire();
		} catch (InvalidActivityException e) {
			
			fail("Error on acquire");
		}
		assertTrue(before==null&&after=="hello");
	}

	@Test
	public void testRemove() {
		
	}

	@Test
	public void testRemoveNow() {
		fail("Not yet implemented");
	}

	@Test
	public void testAcquire() {
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
		}
		assertTrue(first=="test1");
		assertTrue(second=="test2");
	}

	@Test
	public void testAcquireLongTimeUnit() {
		fail("Not yet implemented");
	}

	@Test
	public void testRelease() {
		fail("Not yet implemented");
	}
	
	@Test
	public void testGeneric(){
	
	}

}

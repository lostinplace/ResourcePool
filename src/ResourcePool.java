import java.util.Set;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.ConcurrentSkipListSet;
import javax.activity.InvalidActivityException;

public class ResourcePool<R> {

	
	private BlockingQueue<R> availableResourceQueue = new LinkedBlockingQueue<R>();
	private Set<Integer> acquiredIds = new ConcurrentSkipListSet<Integer>();
	private Set<Integer> ManagedIds = new ConcurrentSkipListSet<Integer>(); 
	
	public ResourcePool(){
	}
	
	private boolean _isOpen=false;
	
	public void open(){
		_isOpen=true;
	}
	
	public boolean isOpen(){
		return _isOpen;
	}
	
	public void close(){
		while(!acquiredIds.isEmpty());
		synchronized(availableResourceQueue)
		{
			closeNow();	
		}
	}
	
	public void closeNow(){
		_isOpen=false;
	}
	
	public boolean add(R resource) 
	{
		int index = System.identityHashCode(resource);
		synchronized (ManagedIds) {
			if(!ManagedIds.add(index))
				return false;
		}
		availableResourceQueue.add(resource);
		return true;
	}
	
	public boolean remove(R resource){
		int index = System.identityHashCode(resource);
		while(acquiredIds.contains(index));
		synchronized (availableResourceQueue) {
			return removeNow(resource, index);
		}
	}
	
	public boolean removeNow(R resource) throws InvalidActivityException{
		int index = System.identityHashCode(resource);
		synchronized(acquiredIds){
			if(acquiredIds.contains(index))
				release(resource);
			synchronized(availableResourceQueue){
				return removeNow(resource, index);
			}
		}
	}
	
	private boolean removeNow(R resource, int index){
		synchronized(ManagedIds){
			if(!ManagedIds.remove(index))
				return false;
			availableResourceQueue.remove(resource);
		}
		return true;
	}
	
	public R acquire() throws InvalidActivityException, InterruptedException{
		if(!isOpen())throw new InvalidActivityException("The ResourcePool is closed. You cannot acquire from a closed ResourcePool");
		R result ;
		result= availableResourceQueue.take();
		acquiredIds.add(System.identityHashCode(result));
		return result;
	}
	
	public R acquire(long timeout,	TimeUnit unit) throws InvalidActivityException, InterruptedException{
		if(!isOpen())throw new InvalidActivityException("The ResourcePool is closed. You cannot acquire from a closed ResourcePool");
		R result ;
		try {
			result= availableResourceQueue.poll(timeout,unit);
		} catch (InterruptedException e) {
			throw e;
		}
		if(result!=null)acquiredIds.add(System.identityHashCode(result));
		return result;
	}
	
	public void release(R resource) throws InvalidActivityException{
		int index = System.identityHashCode(resource);
		
		if(acquiredIds.remove(index))
			availableResourceQueue.add(resource);
		else
			throw new InvalidActivityException("The ResourcePool has not been used to acquire the specified resource.  You can only release resources that were acquired through the pool");
	}
}

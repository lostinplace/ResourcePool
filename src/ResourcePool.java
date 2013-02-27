import java.util.*;
import java.util.concurrent.*;

public class ResourcePool<R> {
	private class Resource{
		public boolean IsAcquired=false;
		public R Resource;
		public Resource(R resource){
			Resource=resource;
		}
	}
	
	private List<Integer> availableResourceIndexList = new CopyOnWriteArrayList<Integer>();
	private ConcurrentMap<Integer,Resource> resourceMap = new ConcurrentHashMap<Integer,Resource>();
	private ConcurrentMap<R,Integer> acquiredResourceMap = new ConcurrentHashMap<R,Integer>();
	
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
	}
	
	public void closeNow(){
		_isOpen=false;
	}
	
	public boolean add(R resource)
	{
		if(!_isOpen)return false;
		Resource container = new Resource(resource);
		//there's a race condition here, really really tiny race condition that I can't block without doing something silly
		int index = resourceMap.size();
		resourceMap.put(index,container);
		availableResourceIndexList.add(index);
		return true;
	}
	
	boolean remove(R resource){
		removeNow(resource);
		return true;
	}
	
	boolean removeNow(R resource){
		return true;
	}
	
	public R acquire(){
		//this could also be tightened
		int index=availableResourceIndexList.get(0);
		availableResourceIndexList.remove((Object)index);
		Resource activeResource = resourceMap.get(index);
		activeResource.IsAcquired=true;
		acquiredResourceMap.put(activeResource.Resource, index);
		return activeResource.Resource;
	}
	
	public R acquire(long timeout,	java.util.concurrent.TimeUnit unit){
		return null;
	}
	
	public void release(R resource){
		int index = acquiredResourceMap.get(resource);
		
	}
}

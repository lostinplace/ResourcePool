import java.util.*;
import java.util.concurrent.*;

import javax.activity.InvalidActivityException;

public class ResourcePool<R> {
	private class Resource{
		public boolean IsAcquired=false;
		public R Resource;
		public Resource(R resource){
			Resource=resource;
		}
	}
	
	private List<UUID> availableResourceIndexList = new CopyOnWriteArrayList<UUID>();
	private ConcurrentMap<UUID,Resource> resourceMap = new ConcurrentHashMap<UUID,Resource>();
	private ConcurrentMap<R,UUID> acquiredResourceMap = new ConcurrentHashMap<R,UUID>();
	
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
		closeNow();
	}
	
	public void closeNow(){
		_isOpen=false;
	}
	
	public boolean add(R resource)
	{
		if(!_isOpen)return false;
		Resource container = new Resource(resource);
		//there's a race condition here, really really tiny race condition that I can't block without doing something silly
		UUID index = UUID.randomUUID();
		resourceMap.put(index ,container);
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
	
	public R acquire() throws InvalidActivityException{
		if(!_isOpen)throw new InvalidActivityException("Cannot acquire from a closed ResourcePool");
		if(resourceMap.size()==0)return null;
		//this could also be tightened
		UUID index=availableResourceIndexList.get(0);
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
		UUID index = acquiredResourceMap.get(resource);
		Resource activeResource= resourceMap.get(index);
		activeResource.IsAcquired=false;
		acquiredResourceMap.remove(resource);
	}
}

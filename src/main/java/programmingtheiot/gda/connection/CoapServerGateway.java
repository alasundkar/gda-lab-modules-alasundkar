/**
 * This class is part of the Programming the Internet of Things project.
 * 
 * It is provided as a simple shell to guide the student and assist with
 * implementation for the Programming the Internet of Things exercises,
 * and designed to be modified by the student as needed.
 */ 

package programmingtheiot.gda.connection;

import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.ArrayBlockingQueue;

import org.eclipse.californium.core.CoapResource;
import org.eclipse.californium.core.CoapServer;
import org.eclipse.californium.core.server.resources.Resource;

import programmingtheiot.common.ConfigConst;
import programmingtheiot.common.IDataMessageListener;
import programmingtheiot.common.ResourceNameEnum;
import programmingtheiot.gda.connection.handlers.GenericCoapResourceHandler;
import programmingtheiot.gda.connection.handlers.UpdateSystemPerformanceResourceHandler;

/**
 * Shell representation of class for student implementation.
 * 
 */
public class CoapServerGateway
{
	// static
	
	private static final Logger _Logger =
		Logger.getLogger(CoapServerGateway.class.getName());
	private CoapServer coapServer = null;
	private IDataMessageListener dataMsgListener = null;
	
	// params
	
	
	// constructors
	
	/**
	 * Default.
	 * 
	 */
	public CoapServerGateway()
	{
		this((ResourceNameEnum[]) null);
		this.coapServer = new CoapServer();
		this.initServer(ResourceNameEnum.values());
	}

	/**
	 * Constructor.
	 * 
	 * @param useDefaultResources
	 */
	public CoapServerGateway(boolean useDefaultResources)
	{
		this(useDefaultResources ? ResourceNameEnum.values() : (ResourceNameEnum[]) null);
		this.coapServer = new CoapServer();

	}

	/**
	 * Constructor.
	 * 
	 * @param resources
	 */
	public CoapServerGateway(ResourceNameEnum ...resources)
	{
		super();
	}

	
	// public methods
	
	public void addResource(ResourceNameEnum resource)
	{
		if (resource != null) {
			// break out the hierarchy of names and build the resource
			// handler generation(s) as needed, checking if any parent already
			// exists - and if so, add to the existing resource
			_Logger.info("Adding server resource handler chain: " + resource.getResourceName());
			
			createAndAddResourceChain(resource);
			}
	}
	
	
	public void addResource(ResourceNameEnum resourceType, String endName, Resource resource)
	{
		// TODO: while not needed for this exercise, you may want to include
		// the endName parameter as part of this resource chain creation process
		
		if (resourceType != null && resource != null) {
			// break out the hierarchy of names and build the resource
			// handler generation(s) as needed, checking if any parent already
			// exists - and if so, add to the existing resource
			createAndAddResourceChain(resourceType, resource);
			CoapResource top =
				    new CoapResource("PIOT").add(
				        new CoapResource("ConstrainedDevice").add(
				            new UpdateSystemPerformanceResourceHandler("SystemPerfMsg")));
		}
	}
	
	public boolean hasResource(String name)
	{
		return false;
	}
	
	public void setDataMessageListener(IDataMessageListener listener)
	{
		this.dataMsgListener = listener;

	}
	
	//starts server
	public boolean startServer()
	{
		this.coapServer.start();
		return true;
	}
	
	//stops server
	public boolean stopServer()
	{
		this.coapServer.stop();
		return true;
	}
	
	
	// private methods
	// creates and adds resource chain
	private void createAndAddResourceChain(ResourceNameEnum resourceType, Resource resource)
	{
		_Logger.info("Adding server resource handler chain: " + resourceType.getResourceName());
		
		List<String> resourceNames = resourceType.getResourceNameChain();
		Queue<String> queue = new ArrayBlockingQueue<>(resourceNames.size());
		
		queue.addAll(resourceNames);
		
		// check if we have a parent resource
		Resource parentResource = this.coapServer.getRoot();
		
		// if no parent resource, add it in now (should be named "PIOT")
		if (parentResource == null) {
			parentResource = new CoapResource(queue.poll());
			this.coapServer.add(parentResource);
		}
		
		while (! queue.isEmpty()) {
			// get the next resource name
			String   resourceName = queue.poll();
			Resource nextResource = parentResource.getChild(resourceName);
			
			if (nextResource == null) {
				if (queue.isEmpty()) {
					nextResource = resource;
					nextResource.setName(resourceName);
				} else {
					nextResource = new CoapResource(resourceName);
				}
				
				parentResource.add(nextResource);
			}
			
			parentResource = nextResource;
		}
	}
	private void createAndAddResourceChain(ResourceNameEnum resource)
	{
		List<String> resourceNames = resource.getResourceNameChain();
		Queue<String> queue = new ArrayBlockingQueue<>(resourceNames.size());
		
		queue.addAll(resourceNames);
		
		// check if we have a parent resource
		Resource parentResource = this.coapServer.getRoot();
		
		// if no parent resource, add it in now (should be named "PIOT")
		if (parentResource == null) {
			parentResource = new GenericCoapResourceHandler(queue.poll());
			this.coapServer.add(parentResource);
		}
		
		while (! queue.isEmpty()) {
			// get the next resource name
			String   resourceName = queue.poll();
			Resource nextResource = parentResource.getChild(resourceName);
			
			if (nextResource == null) {
				// TODO: if this is the last entry, use a custom resource handler implementation that
				// is specific to the resource's implementation needs (e.g. SensorData, ActuatorData, etc.)
				nextResource = new GenericCoapResourceHandler(resourceName);
				parentResource.add(nextResource);
			}
			
			parentResource = nextResource;
		}
	}
	
	private Resource createResourceChain(ResourceNameEnum resource)
	{
		return null;
	}
	
	// initialzes server with all resources
	private void initServer(ResourceNameEnum ...resources)
	{
		coapServer = new CoapServer();
		
		// TODO: Get the List of Strings representing all ResourceNameEnum names (assuming it's named 'resources')

		for (ResourceNameEnum rn : resources) {
			addResource(rn);
		}
	}
}

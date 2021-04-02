package programmingtheiot.gda.connection.handlers;

import java.util.logging.Logger;

import org.eclipse.californium.core.CoapResource;
import org.eclipse.californium.core.coap.CoAP.ResponseCode;
import org.eclipse.californium.core.server.resources.CoapExchange;

import programmingtheiot.common.IDataMessageListener;
import programmingtheiot.common.ResourceNameEnum;
import programmingtheiot.data.ActuatorData;

public class GetActuatorCommandResourceHandler extends CoapResource
{
	// static
	
	private static final Logger _Logger =
		Logger.getLogger(GenericCoapResourceHandler.class.getName());
	
	// params
	
	private ActuatorData actuatorData = null;

	// constructors
	
	/**
	 * Constructor.
	 * 
	 * @param resource Basically, the path (or topic)
	 */
	public GetActuatorCommandResourceHandler(ResourceNameEnum resource)
	{
		this(resource.getResourceName());
	}
	
	/**
	 * Constructor.
	 * 
	 * @param resourceName The name of the resource.
	 */
	public GetActuatorCommandResourceHandler(String resourceName)
	{
		super(resourceName);
	}
	
	public boolean onActuatorDataUpdate(ActuatorData data)
	{
		if (data != null) {
			this.actuatorData.updateData(data);
			
			// notify all connected clients
			super.changed();
			
			_Logger.fine("Actuator data updated for URI: " + super.getURI() + ": Data value = " + this.actuatorData.getValue());
			
			return true;
		}
		
		return false;
	}	
	// public methods
	
	@Override
	public void handleDELETE(CoapExchange context)
	{
	}
	
	@Override
	public void handleGET(CoapExchange context)
	{
	    // TODO: validate 'context'
		_Logger.info("Processing handleGET call ");

	    // accept the request
	    context.accept();
	    
	    // TODO: convert the locally stored ActuatorData to JSON using DataUtil

	    // TODO: generate a response message, set the content type, and set the response code

	    String jsonData;
		// send an appropriate response
	   // context.respond(ResponseCode.CONTENT, jsonData);
	    context.respond(ResponseCode.CONTENT);
	}
	
	@Override
	public void handlePOST(CoapExchange context)
	{
	}
	
	@Override
	public void handlePUT(CoapExchange context)
	{
	}
	
	public void setDataMessageListener(IDataMessageListener listener)
	{
	}
	
}

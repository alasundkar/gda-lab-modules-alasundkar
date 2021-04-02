package programmingtheiot.gda.connection.handlers;

import java.util.logging.Logger;

import org.eclipse.californium.core.CoapResource;
import org.eclipse.californium.core.server.resources.CoapExchange;

import programmingtheiot.common.IDataMessageListener;
import programmingtheiot.common.ResourceNameEnum;


import java.util.logging.Logger;

import org.eclipse.californium.core.CoapResource;
import org.eclipse.californium.core.coap.CoAP.ResponseCode;
import org.eclipse.californium.core.server.resources.CoapExchange;

import programmingtheiot.common.ConfigConst;
import programmingtheiot.common.ConfigUtil;
import programmingtheiot.common.IDataMessageListener;
import programmingtheiot.common.ResourceNameEnum;

public class UpdateSystemPerformanceResourceHandler extends CoapResource
{
	// static
	
	private static final Logger _Logger =
		Logger.getLogger(GenericCoapResourceHandler.class.getName());
	
	// params
	
	private IDataMessageListener dataMsgListener = null;

	// constructors
	
	/**
	 * Constructor.
	 * 
	 * @param resource Basically, the path (or topic)
	 */
	public UpdateSystemPerformanceResourceHandler(ResourceNameEnum resource)
	{
		this(resource.getResourceName());
	}
	
	/**
	 * Constructor.
	 * 
	 * @param resourceName The name of the resource.
	 */
	public UpdateSystemPerformanceResourceHandler(String resourceName)
	{
		super(resourceName);
	}
	
	
	// public methods
	
	@Override
	public void handleDELETE(CoapExchange context)
	{
	}
	
	@Override
	public void handleGET(CoapExchange context)
	{
	}
	
	@Override
	public void handlePOST(CoapExchange context)
	{
	}
	
	@Override
	public void handlePUT(CoapExchange context)
	{
	    // TODO: validate 'context'
	    
	    // accept the request
	    context.accept();
	    
	    // TODO: create (or update) the resource with the payload
	    String payload = context.getRequestText();
	    
	    // TODO: convert the payload to the appropriate data structure using DataUtil
	    //   - UpdateSystemPerformanceResourceHandler: Convert to SystemPerformanceData
	    //   - UpdateTelemetryResourceHandler: Convert to SensorData

	    // TODO: send the newly instanced data structure to this.dataMsgListener
	    
	    // TODO: generate a response message, set the content type, and set the response code

	    // send an appropriate response
	    context.respond(ResponseCode.CHANGED);
	}
	
	public void setDataMessageListener(IDataMessageListener listener)
	{
		this.dataMsgListener = listener;

	}
	
}
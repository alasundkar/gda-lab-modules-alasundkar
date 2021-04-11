package programmingtheiot.gda.connection.handlers;

import java.util.logging.Logger;

import org.eclipse.californium.core.CoapResource;
import org.eclipse.californium.core.server.resources.CoapExchange;

import programmingtheiot.common.IDataMessageListener;
import programmingtheiot.common.ResourceNameEnum;
import programmingtheiot.data.DataUtil;
import programmingtheiot.data.SystemPerformanceData;

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

	private ResourceNameEnum resourceName;

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
		_Logger.info("handleGET is called"+context.toString());
		context.accept();
		String type = "GET";
		String msg = "Update system perf data request handled: " + super.getName();
		context.respond(ResponseCode.VALID, msg);
	}
	
	@Override
	public void handlePOST(CoapExchange context)
	{

	}
	
	@Override
	public void handlePUT(CoapExchange context)
	{
		  ResponseCode code = ResponseCode.NOT_ACCEPTABLE;
		  
		  context.accept();
		  
		  if (this.dataMsgListener != null) {
		    try {
		      String jsonData = new String(context.getRequestPayload());
		      
		      SystemPerformanceData sysPerfData =  DataUtil.getInstance().jsonToSystemPerformanceData(jsonData);
		      
		      // TODO: Choose the following (but keep it idempotent!) 
		      //   1) Check MID to see if it’s repeated for some reason
		      //      - the underlying lib should handle this…
		      //   2) Cache the previous update – is the PAYLOAD repeated?
		      //   2) Delegate the data check to this.dataMsgListener
		      
		      this.dataMsgListener.handleSystemPerformanceMessage(this.resourceName, sysPerfData);
		      
		      code = ResponseCode.CHANGED;
		    } catch (Exception e) {
		      _Logger.warning(
		        "Failed to handle PUT request. Message: " +
		        e.getMessage());
		      
		      code = ResponseCode.BAD_REQUEST;
		    }
		  } else {
		    _Logger.info(
		      "No callback listener for request. Ignoring PUT.");
		    
		    code = ResponseCode.CONTINUE;
		  }
		  
		  String msg =
		    "Update system perf data request handled: " + super.getName();
		  
		  context.respond(code, msg);
//	    // TODO: validate 'context'
//	    
//	    // accept the request
//	    context.accept();
//	    
//	    // TODO: create (or update) the resource with the payload
//	    String payload = context.getRequestText();
//	    
//	    // TODO: convert the payload to the appropriate data structure using DataUtil
//	    //   - UpdateSystemPerformanceResourceHandler: Convert to SystemPerformanceData
//	    //   - UpdateTelemetryResourceHandler: Convert to SensorData
//
//	    // TODO: send the newly instanced data structure to this.dataMsgListener
//	    
//	    // TODO: generate a response message, set the content type, and set the response code
//
//	    // send an appropriate response
//	    context.respond(ResponseCode.CHANGED);
	}
	
	public void setDataMessageListener(IDataMessageListener listener)
	{
		this.dataMsgListener = listener;

	}
	
}
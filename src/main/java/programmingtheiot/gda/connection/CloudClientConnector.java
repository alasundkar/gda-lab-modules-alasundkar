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
import programmingtheiot.data.DataUtil;
import java.util.Properties;

import programmingtheiot.common.ConfigConst;
import programmingtheiot.common.ConfigUtil;
import programmingtheiot.common.IDataMessageListener;
import programmingtheiot.common.ResourceNameEnum;
import programmingtheiot.data.SensorData;
import programmingtheiot.data.SystemPerformanceData;

/**
 * Shell representation of class for student implementation.
 *
 */
public class CloudClientConnector extends MqttClientConnector implements ICloudClient
//public class CloudClientConnector  implements ICloudClient

{
	// static
	
	private static final Logger _Logger =
		Logger.getLogger(CloudClientConnector.class.getName());
	
	// private var's
	
	private String topicPrefix = "";
	private MqttClientConnector mqttClient = null;
	private IDataMessageListener dataMsgListener = null;
	private int qosLevel = 1;
	private String itemName;

	// constructors
	
	/**
	 * Default.
	 * 
	 */
	public CloudClientConnector()
	{
		super(true);
		//super();

		ConfigUtil configUtil = ConfigUtil.getInstance();
		
		this.topicPrefix =
			configUtil.getProperty(ConfigConst.CLOUD_GATEWAY_SERVICE, ConfigConst.BASE_TOPIC_KEY);
		
		// Depending on the cloud service, the topic names may or may not begin with a "/", so this code
		// should be updated according to the cloud service provider's topic naming conventions
		if (topicPrefix == null) {
			_Logger.info("topicPrefix value if condition" + this.topicPrefix );

			topicPrefix = "/";
		} else {
			if (! topicPrefix.endsWith("/")) {
				_Logger.info("topicPrefix value else condition" + this.topicPrefix );

				topicPrefix += "/";
			}
		}
		
	}
	
	
	// public methods
	
	@Override
	public boolean connectClient()
	{
		/*
		 * Call to connect Client using MQTT.
		 */
		_Logger.info("connectClient is Called...");
//		if (this.mqttClient == null) {
//			this.mqttClient = new MqttClientConnector(true);
//		}
//		
//		this.mqttClient.connectClient();
//		
//		return this.mqttClient.isConnected();
		if(this.mqttClient == null) {
			//Connects to MQTT Client. Imports configuration from Config file.
			this.mqttClient = new MqttClientConnector(true);
			this.mqttClient.setDataMessageListener(this.dataMsgListener);
			this.mqttClient.connectClient();
			return this.mqttClient.isConnected();
		}
		else {
			if (this.mqttClient.isConnected()) {
				return true;
			}
			return false;
		}
	}

	@Override
	public boolean disconnectClient()
	{
		if(this.mqttClient != null && this.mqttClient.isConnected()) {
			this.mqttClient.disconnectClient();
			return true;
		}
		return false;
	}

	@Override
	public boolean setDataMessageListener(IDataMessageListener listener)
	{
		_Logger.info("setDataMessageListener has been called");
		if(listener != null) {
			this.dataMsgListener = listener;
			return true;
		}
		
		return false;
	}

	@Override
	public boolean sendEdgeDataToCloud(ResourceNameEnum resource, SensorData data)
	{
		if (resource != null && data != null) {
			String payload = DataUtil.getInstance().sensorDataToJson(data);
			_Logger.info("resource: "+resource + " data :" + data.getValue() + " payload: "+payload);
			return publishMessageToCloud(resource, data.getName(), payload);
		}
		
		return false;
	}

	@Override
	public boolean sendEdgeDataToCloud(ResourceNameEnum resource, SystemPerformanceData data)
	{
		if (resource != null && data != null) {
			SensorData cpuData = new SensorData();
			cpuData.setName(ConfigConst.CPU_UTIL_NAME);
			cpuData.setValue(data.getCpuUtilization());
			
			boolean cpuDataSuccess = sendEdgeDataToCloud(resource, cpuData);
			
			if (! cpuDataSuccess) {
				_Logger.warning("Failed to send CPU utilization data to cloud service."+ cpuData);
			}
			if (cpuDataSuccess) {
				_Logger.info("Successfully to send CPU utilization data to cloud service. Data: "+ cpuData);
			}
			
			SensorData memData = new SensorData();
			memData.setName(ConfigConst.MEM_UTIL_NAME);
			memData.setValue(data.getMemoryUtilization());
			
			boolean memDataSuccess = sendEdgeDataToCloud(resource, memData);
			
			if (! memDataSuccess) {
				_Logger.warning("Failed to send memory utilization data to cloud service.  Data: "+ memData);
			}
			if (memDataSuccess) {
				_Logger.info("Successfully to send memory utilization data to cloud service. Data: "+ memData);
			}
			_Logger.info("cpuDataSuccess: "+cpuDataSuccess+" , memDataSuccess; "+memDataSuccess);
			return (cpuDataSuccess == memDataSuccess);
		}
		
		return false;
	}

	@Override
	public boolean subscribeToEdgeEvents(ResourceNameEnum resource)
	{
		boolean success = false;
		
		String topicName = null;
		
		if (this.mqttClient.isConnected()) {
			topicName = createTopicName(resource);
			
			//subscribes
			try {
				this.mqttClient.subscribeToTopic(topicName, this.qosLevel);
				_Logger.info("Successfully Subscribed to topic..." + topicName);
			} catch (Exception e) {
				// TODO: handle exception
				_Logger.info("Not subscribed"+e.getStackTrace());
			}
						
			success = true;
		} else {
			_Logger.warning("Subscription methods only available for MQTT. No MQTT connection to broker. Ignoring. Topic: " + topicName);
		}
		
		return success;
	}

	@Override
	public boolean unsubscribeFromEdgeEvents(ResourceNameEnum resource)
	{
		boolean success = false;
		
		String topicName = null;
		
		if (this.mqttClient.isConnected()) {
			topicName = createTopicName(resource);
			
			this.mqttClient.unsubscribeFromTopic(topicName);
			
			success = true;
		} else {
			_Logger.warning("Unsubscribe method only available for MQTT. No MQTT connection to broker. Ignoring. Topic: " + topicName);
		}
		
		return success;
	}
	

	@Override
	public boolean subscribeToCloudEvents(ResourceNameEnum resource)
	{
		boolean success = false;
		
		String topicName = null;
		
		if (this.mqttClient.isConnected()) {
			topicName = createTopicName(resource);
			
			//subscribes
			try {
				this.mqttClient.subscribeToTopic(topicName, this.qosLevel);
				_Logger.info("Successfully Subscribed to topic..." + topicName);
			} catch (Exception e) {
				// TODO: handle exception
				_Logger.info("Not subscribed"+e.getStackTrace());
			}			
			success = true;
		} else {
			_Logger.warning("Subscription methods only available for MQTT. No MQTT connection to broker. Ignoring. Topic: " + topicName);
		}
		
		return success;
	}	
	
	@Override
	public boolean unsubscribeFromCloudEvents(ResourceNameEnum resource)
	{
		boolean success = false;
		
		String topicName = null;
		
		if (this.mqttClient.isConnected()) {
			topicName = createTopicName(resource);
			
			this.mqttClient.unsubscribeFromTopic(topicName);
			
			success = true;
		} else {
			_Logger.warning("Unsubscribe method only available for MQTT. No MQTT connection to broker. Ignoring. Topic: " + topicName);
		}
		
		return success;
	}
	// private methods
	
	private String createTopicName(ResourceNameEnum resource)
	{		_Logger.info("createTopicName is Called...");

		return this.topicPrefix + resource.getDeviceName() + "/" + resource.getResourceType();
	}
	
	private boolean publishMessageToCloud(ResourceNameEnum resource, String itemName, String payload)
	{
		String topicName = createTopicName(resource) + "-" + itemName;
		/*
		 * Call to publish message to cloud using MQTT.
		 */
		_Logger.info("publishMessageToCloud is called...");
		// set topic name according to convention and cloud service requirements
		//String topicName = createTopicName(resource);

	//	payload = "{'value':1.0 }";
		try {
			_Logger.info("Publishing payload value(s) to Ubidots: " + topicName +" Payload: " + payload);
			
		return	this.mqttClient.publishMessage(topicName, payload.getBytes(), this.qosLevel);
		//	return true;
		} catch (Exception e) {
			_Logger.warning("Failed to publish message to Ubidots: " + topicName + " Error: " + e + " Error msg: " + e.getMessage());
		}
		
		return false;
	}	
	
	
}

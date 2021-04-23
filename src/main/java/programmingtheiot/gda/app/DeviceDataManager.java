/**
 * This class is part of the Programming the Internet of Things project.
 * 
 * It is provided as a simple shell to guide the student and assist with
 * implementation for the Programming the Internet of Things exercises,
 * and designed to be modified by the student as needed.
 */ 

package programmingtheiot.gda.app;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

import programmingtheiot.common.ConfigConst;
import programmingtheiot.common.ConfigUtil;
import programmingtheiot.common.IDataMessageListener;
import programmingtheiot.common.ResourceNameEnum;

import programmingtheiot.data.ActuatorData;
import programmingtheiot.data.DataUtil;
import programmingtheiot.data.SensorData;
import programmingtheiot.data.SystemPerformanceData;
import programmingtheiot.data.SystemStateData;

import programmingtheiot.gda.connection.CloudClientConnector;
import programmingtheiot.gda.connection.CoapServerGateway;
import programmingtheiot.gda.connection.IPersistenceClient;
import programmingtheiot.gda.connection.IPubSubClient;
import programmingtheiot.gda.connection.IRequestResponseClient;
import programmingtheiot.gda.connection.MqttClientConnector;
import programmingtheiot.gda.connection.RedisPersistenceAdapter;
import programmingtheiot.gda.connection.SmtpClientConnector;
import programmingtheiot.gda.system.SystemPerformanceManager;

/**
 * Shell representation of class for student implementation.
 * @param <IActuatorDataListener>
 *
 */
public class DeviceDataManager<IActuatorDataListener> implements IDataMessageListener
{
	// static
	
	private static final Logger _Logger =
		Logger.getLogger(DeviceDataManager.class.getName());
	
	// private var's
	
	private boolean enableMqttClient = true;
	private boolean enableCoapServer = false;
	private boolean enableCloudClient = false;
	private boolean enableSmtpClient = false;
	private boolean enablePersistenceClient = false;
	
	private IPubSubClient mqttClient = null;
	private IPubSubClient cloudClient = null;
	private IPersistenceClient persistenceClient = null;
	private IRequestResponseClient smtpClient = null;
	private CoapServerGateway coapServer = null;
	public String IActuatorDataListener ;
	
	// constructors
	
	SystemPerformanceManager sysPerfMgr = new SystemPerformanceManager();

	/**
	 * Default
	 */
	
	public DeviceDataManager()
	{
		super();
		ConfigUtil configUtil = new ConfigUtil();
		this.enableMqttClient  = configUtil .getBoolean(ConfigConst.GATEWAY_DEVICE, ConfigConst.ENABLE_MQTT_CLIENT_KEY);
		this.enableCoapServer  = configUtil.getBoolean(ConfigConst.GATEWAY_DEVICE, ConfigConst.ENABLE_COAP_SERVER_KEY);
		this.enableCloudClient = configUtil.getBoolean(ConfigConst.GATEWAY_DEVICE, ConfigConst.ENABLE_CLOUD_CLIENT_KEY);
		this.enableSmtpClient  = configUtil.getBoolean(ConfigConst.GATEWAY_DEVICE, ConfigConst.ENABLE_SMTP_CLIENT_KEY);
		this.enablePersistenceClient = configUtil.getBoolean(ConfigConst.GATEWAY_DEVICE, ConfigConst.ENABLE_PERSISTENCE_CLIENT_KEY);
		initConnections();
	}
	
	public DeviceDataManager(
		boolean enableMqttClient,
		boolean enableCoapClient,
		boolean enableCloudClient,
		boolean enableSmtpClient,
		boolean enablePersistenceClient)
	{
		super();
		this.enableMqttClient = enableMqttClient;
		this.enableCloudClient = enableCloudClient;
		this.enableSmtpClient = enableSmtpClient;
		this.enablePersistenceClient = enablePersistenceClient;
		this.enableCoapServer = enableCoapClient;
		initConnections();
		ConfigUtil configUtil = ConfigUtil.getInstance();

		
	}
	
	
	// public methods
	public void setActuatorDataListener(String name, IActuatorDataListener listener) {
		
	}
	
	@Override
	public boolean handleActuatorCommandResponse(ResourceNameEnum resourceName, ActuatorData data)
	{

		_Logger.info("handleActuatorCommandResponse has been initiated...");
		
		/*
		 * Checking if persistent connection is active.
		 */
		if (enablePersistenceClient == true)
		{
			_Logger.info("Persistent connection is active");
			persistenceClient.storeData(resourceName.getResourceName(), ConfigConst.DEFAULT_QOS, data);
		}
		
		/*
		 * Checking if Actuator Data message has error.
		 */
		if (data.hasError() == true)
		{
			try
			{
				_Logger.info("Actuator data has error");
			} catch (Exception e) 
			{
				_Logger.info(e.getMessage());
			}
			return false;
		}
		
		return true;
	}

	@Override
	public boolean handleIncomingMessage(ResourceNameEnum resourceName, String msg)
	{
		_Logger.info("handleIncomingMessage has bee initiated...");

		/*
		 * Creating DataUtil instance 'dataUtil' to convert JSON Data to Actuator Data or System State Data.
		 */
		DataUtil dataUtil = new DataUtil();
		
		try
		{
			/*
			 * Converting JSON Data to Actuator Data.
			 */
			ActuatorData message = dataUtil.jsonToActuatorData(msg);
			handleIncomingDataAnalysis(resourceName, message);
		} 
		catch (Exception e)
		{
			/*
			 * Converting JSON Data to System State Data.
			 */
			SystemStateData message = dataUtil.jsonToSystemStateData(msg);
			handleIncomingDataAnalysis(resourceName, message);
		}
		
		return true;
		}

	
	/**
	 * Call to handle Incoming data analysis.
	 */
	private void handleIncomingDataAnalysis(ResourceNameEnum resourceName, SystemStateData message) {
		
		// TODO Auto-generated method stub
		_Logger.info("handleIncomingDataAnalysis has been initiated..");
	}

	/**
	 * Call to handle Incoming data analysis.
	 */
	private void handleIncomingDataAnalysis(ResourceNameEnum resourceName, ActuatorData message) {
		
		// TODO Auto-generated method stub
		_Logger.info("handleIncomingDataAnalysis has been initiated..");
	}
	
	
	
	
	@Override
	public boolean handleSensorMessage(ResourceNameEnum resourceName, SensorData data)
	{
		_Logger.info("handleSensorMessage has been initiated...");
		
		/*
		 * Checking if Persistence Client is active.
		 */
		if (enablePersistenceClient == true)
		{
			_Logger.info("Persistence Client is active");
			persistenceClient.storeData(resourceName.getResourceName(), ConfigConst.DEFAULT_QOS, data);
		}
		
		DataUtil dataUtil = new DataUtil();
		String message = dataUtil.sensorDataToJson(data);

//		handleUpstreamTransmission(resourceName, message);
		

//			Properties prop = readPropertiesFile("PiotConfig.props");
			
		//	if(Float.valueOf(message) < Float.valueOf(prop.getProperty("humiditySimFloor")) ||
			//		Float.valueOf(message) > Float.valueOf(prop.getProperty("humiditySimCeiling"))) {
				
				ActuatorData actuatorData = new ActuatorData();
				actuatorData.setValue(Float.valueOf(message));
				actuatorData.setCommand(ActuatorData.COMMAND_ON);
				mqttClient.publishMessage(resourceName, message, 1);

		return true;
	}

	@Override
	public boolean handleSystemPerformanceMessage(ResourceNameEnum resourceName, SystemPerformanceData data)
	{
		_Logger.info("handleSystemPerformanceMessage has been called");
		DataUtil dataUtil = DataUtil.getInstance();
		try {
			if(enablePersistenceClient == true)
			{
				this.persistenceClient.storeData(resourceName.getResourceName(), 0, data);
				//String jsonData = dataUtil.systemPerformanceDataToJson(data);
				return true;
			}
				
		}
		catch(Exception ex) {
			_Logger.info("Exception occured: " + ex.getMessage());
		}
		return false;	}
	
	
	
	
	private boolean handleUpstreamTransmission(ResourceNameEnum resourceName, String jsonData, int qos) {
		_Logger.fine("Persistence Client is active");
		return true;
	}
	public void startManager()
	{
		_Logger.info("DeviceDataManager has been initialized...");
		
		/*
		 * Establishing MQTT Client Connection.
		 */
		if (enableMqttClient == true)
		{
			MqttClientConnector mqttClient1 = new MqttClientConnector();
			mqttClient1.connectClient();
		}
		
		/*
		 * Establishing persistent connection.
		 */
		if (enablePersistenceClient == true)
		{
			RedisPersistenceAdapter persistenceClient1 = new RedisPersistenceAdapter();
			persistenceClient1.connectClient();
		}
		
		/*
		 * Establishing connection to the cloud.
		 */
		if (enableCloudClient == true)
		{
			CloudClientConnector cloudClient1 = new CloudClientConnector();
			cloudClient1.connectClient();
		}
		
		/*
		 * Initializing the CoAP Server.
		 */
		if (enableCoapServer == true)
		{
			CoapServerGateway coapServer1 = new CoapServerGateway();
			coapServer1.startServer();
		}
		
		/*
		 * Initializing System Performance Manager.
		 */
	  
	  this.sysPerfMgr.startManager();
	}
		
	public void stopManager()
	{
		_Logger.info("DeviceDataManager was stopped..");
		
		/*
		 * Terminating MQTT Client Connection.
		 */
		if (enableMqttClient == true)
		{
			MqttClientConnector mqttClient1 = new MqttClientConnector();
			mqttClient1.disconnectClient();
		}
		
		/*
		 * Terminating persistent connection.
		 */
		if (enablePersistenceClient == true)
		{
			RedisPersistenceAdapter persistenceClient1 = new RedisPersistenceAdapter();
			persistenceClient1.disconnectClient();
		}
		
		/*
		 * Terminating connection to the cloud.
		 */
		if (enableCloudClient == true)
		{
			CloudClientConnector cloudClient1 = new CloudClientConnector();
			cloudClient1.disconnectClient();
		}
		
		/*
		 * Stopping the CoAP Server.
		 */
		if (enableCoapServer == true)
		{
			CoapServerGateway coapServer1 = new CoapServerGateway();
			coapServer1.stopServer();
		}
		
		/*
		 * Stopping System Performance Manager.
		 */	  
	  this.sysPerfMgr.stopManager();
	}

	
	// private methods
	
	/**
	 * Initializes the enabled connections. This will NOT start them, but only create the
	 * instances that will be used in the {@link #startManager() and #stopManager()) methods.
	 * 
	 */
	private void initConnections()
	{
		this.coapServer = new CoapServerGateway();
		if(this.enableMqttClient)
			this.mqttClient = new MqttClientConnector();
			this.mqttClient.setDataMessageListener(this);
		this.persistenceClient = new RedisPersistenceAdapter();
		
	}
	private void initManager(){  
		this.sysPerfMgr = new SystemPerformanceManager();  
		this.sysPerfMgr.setDataMessageListener(this);    

	}
	
	
	
}

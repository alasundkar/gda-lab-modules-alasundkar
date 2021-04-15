/**
 * This class is part of the Programming the Internet of Things project.
 * 
 * It is provided as a simple shell to guide the student and assist with
 * implementation for the Programming the Internet of Things exercises,
 * and designed to be modified by the student as needed.
 */ 

package programmingtheiot.gda.connection;

import java.io.UnsupportedEncodingException;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.IMqttMessageListener;
import org.eclipse.paho.client.mqttv3.MqttCallbackExtended;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.eclipse.paho.client.mqttv3.MqttPersistenceException;
import org.eclipse.paho.client.mqttv3.MqttSecurityException;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;

import programmingtheiot.common.ConfigConst;
import programmingtheiot.common.ConfigUtil;
import programmingtheiot.common.IDataMessageListener;
import programmingtheiot.common.ResourceNameEnum;
import programmingtheiot.data.ActuatorData;
import programmingtheiot.data.DataUtil;
import programmingtheiot.data.SensorData;
import programmingtheiot.data.SystemPerformanceData;

/**
 * Shell representation of class for student implementation.
 * 
 */
public class MqttClientConnector implements IPubSubClient, MqttCallbackExtended
{
	// static
	
	private static final Logger _Logger =
		Logger.getLogger(MqttClientConnector.class.getName());
	
	// params
	String host,clientID,brokerAddr,protocol,pemFileName,username;
	MemoryPersistence persistence;
	MqttConnectOptions connOpts;
	int brokerKeepAlive,port;
	MqttClient mqttClient;
	private boolean useCloudGatewayConfig = false;
	boolean enableEncryption,useCleanSession,enableAutoReconnect,isClientConnected;

//	private MqttClient mqttClient = null;
	private IDataMessageListener dataMsgListener = null;
	// constructors
	
	/**
	 * Default.
	 * 
	 */
	public MqttClientConnector()
	{
		this(false);
	}
	
	public MqttClientConnector(boolean useCloudGatewayConfig)
	{
		super();
		
		this.useCloudGatewayConfig = useCloudGatewayConfig;
		
		if (useCloudGatewayConfig) {
			initClientParameters(ConfigConst.CLOUD_GATEWAY_SERVICE);
		} else {
			initClientParameters(ConfigConst.MQTT_GATEWAY_SERVICE);
		}
	}	
//	public MqttClientConnector()
//	{
//		super();
//		ConfigUtil configUtil = ConfigUtil.getInstance();
//
//		this.host =
//		    configUtil.getProperty(
//		        ConfigConst.MQTT_GATEWAY_SERVICE, ConfigConst.HOST_KEY, ConfigConst.DEFAULT_HOST);
//
//		this.port =
//		    configUtil.getInteger(
//		        ConfigConst.MQTT_GATEWAY_SERVICE, ConfigConst.PORT_KEY, ConfigConst.DEFAULT_MQTT_PORT);
//
//		this.protocol = "http";
//		this.brokerKeepAlive =
//		    configUtil.getInteger(
//		        ConfigConst.MQTT_GATEWAY_SERVICE, ConfigConst.KEEP_ALIVE_KEY, ConfigConst.DEFAULT_KEEP_ALIVE);
//
//		// paho Java client requires a client ID
//		this.clientID = MqttClient.generateClientId();
//
//		// these are specific to the MQTT connection which will be used during connect
//		this.persistence = new MemoryPersistence();
//		this.connOpts = new MqttConnectOptions();
//
//		this.connOpts.setKeepAliveInterval(this.brokerKeepAlive);
//		this.connOpts.setCleanSession(false);
//		this.connOpts.setAutomaticReconnect(true);
//
//		// NOTE: URL does not have a protocol handler for "tcp",
//		// so we need to construct the URL manually
//		this.brokerAddr = "tcp" + "://" + this.host + ":" + this.port;
//	}
//	
	
	
	
	// public methods
	
	@Override
	public boolean connectClient()
	{
		try {
			if (this.mqttClient == null) {
			    this.mqttClient = new MqttClient(this.brokerAddr, this.clientID, this.persistence);
			    this.mqttClient.setCallback(this);
			}
			if (! this.mqttClient.isConnected()) {
			    this.mqttClient.connect(this.connOpts);
			    _Logger.log(Level.INFO, "Mqtt Client connected");
			    return true;
			}
			
		}
		catch(Exception ex) {
			_Logger.log(Level.WARNING, ex.getMessage());
		}
		return false;
	}

	@Override
	public boolean disconnectClient()
	{
		if (this.mqttClient.isConnected()) {
		    try {
		    	_Logger.log(Level.INFO, "Mqtt Client disconnected");
				this.mqttClient.disconnect();
			} catch (MqttException e) {
				e.printStackTrace();
			}
		    return true;
		}
		return false;
	}

	public boolean isConnected()
	{
		if(mqttClient.isConnected())
			return true;
		return false;
	}
	
	@Override
	public boolean publishMessage(ResourceNameEnum topicName, String msg, int qos)	
	{
		_Logger.log(Level.INFO, "publishMessage has been called");
		if(topicName == null)
			return false;
		if(qos < 0 || qos > 2)
			qos = ConfigConst.DEFAULT_QOS;
		byte[] b;
		try {
		 b = msg.getBytes("UTF-8");
		MqttMessage mqMsg = new MqttMessage(b);
		mqttClient.publish(topicName.getResourceName(), mqMsg);
		} catch (MqttPersistenceException e) {
			e.printStackTrace();
			return false;
		} catch (MqttException e) {
			e.printStackTrace();
			return false;
		} catch (UnsupportedEncodingException e) {
			 e.printStackTrace();
			 return false;
		}
		return true;
	}
	
	protected boolean publishMessage(String topic, byte[] payload, int qos)
	{
		MqttMessage message = new MqttMessage(payload);
		
		if (qos < 0 || qos > 2) {
			qos = 0;
		}
		
		message.setQos(qos);
		
		// NOTE: you may want to log the exception stack trace if the call fails
		try {
			_Logger.info("Publishing message to topic: " + topic);
			
			this.mqttClient.publish(topic, message);
			
			return true; 
		} catch (MqttPersistenceException e) {
			_Logger.warning("Persistence exception thrown when publishing to topic: " + topic);
		} catch (MqttException e) {
			_Logger.warning("MQTT exception thrown when publishing to topic: " + topic);
		}
		
		return false;
	}
		

	@Override
//	public boolean subscribeToTopic(ResourceNameEnum topicName, int qos)
	public boolean subscribeToTopic(ResourceNameEnum topicName, int qos)
	{
		_Logger.log(Level.INFO, "subscribeToTopic has been called");
		if (topicName == null)
			return false;
		if (qos > 0 || qos < 2)
			qos = ConfigConst.DEFAULT_QOS;
		try {
			mqttClient.subscribe(topicName.getResourceName(),qos);
		} catch (MqttException e) {
			e.printStackTrace();
			return false;
		}
		return true;
	}

	protected boolean subscribeToTopic(String topic, int qos)
	{
		// NOTE: you may want to log the exception stack trace if the call fails
		try {
			this.mqttClient.subscribe(topic, qos);
			
			return true;
		} catch (MqttException e) {
			_Logger.warning("Failed to subscribe to topic: " + topic);
		}
		
		return false;
	}
	
	protected boolean unsubscribeFromTopic(String topic)
	{
		// NOTE: you may want to log the exception stack trace if the call fails
		try {
			this.mqttClient.unsubscribe(topic);
			
			return true;
		} catch (MqttException e) {
			_Logger.warning("Failed to unsubscribe from topic: " + topic);
		}
		
		return false;
	}	
	@Override
	public boolean unsubscribeFromTopic(ResourceNameEnum topicName)
	{
		_Logger.log(Level.INFO, "unsubscribeToTopic has been called");
		return true;
	}

	@Override
	public boolean setDataMessageListener(IDataMessageListener listener)
	{
		//_Logger.log(Level.INFO, "setDataMessageListener has been called");
	    if (listener != null) {
	        this.dataMsgListener = listener;
			_Logger.log(Level.INFO, "setDataMessageListener has been called: return: true" + " listerner value :" + listener);

	        return true;
	    }
		_Logger.log(Level.INFO, "setDataMessageListener has been called: return: false" + " listerner value :" + listener);

		return false;
	}
	
	// callbacks
	
	@Override
	public void connectComplete(boolean reconnect, String serverURI)
	{
		_Logger.info("MQTT connection successful (is reconnect = " + reconnect + "). Broker: " + serverURI);
		
		int qos = 1;
//		this.subscribeToTopic(ResourceNameEnum.CDA_ACTUATOR_RESPONSE_RESOURCE, qos);
//		this.subscribeToTopic(ResourceNameEnum.CDA_SENSOR_MSG_RESOURCE, qos);
//		this.subscribeToTopic(ResourceNameEnum.CDA_SYSTEM_PERF_MSG_RESOURCE, qos);
		// Option 2
		try {
			_Logger.log(Level.INFO, "inside connectComplete with topic name" + ResourceNameEnum.CDA_ACTUATOR_RESPONSE_RESOURCE.getResourceName());
			this.mqttClient.subscribe("/v1.6/devices/constraineddevice/displaycmd");
			this.mqttClient.subscribe(
				ResourceNameEnum.CDA_ACTUATOR_RESPONSE_RESOURCE.getResourceName(),
				qos,
				new ActuatorResponseMessageListener(ResourceNameEnum.CDA_ACTUATOR_RESPONSE_RESOURCE, this.dataMsgListener));
			_Logger.log(Level.INFO, "inside connectComplete with topic name" + ResourceNameEnum.CDA_ACTUATOR_RESPONSE_RESOURCE.getResourceName());
			this.mqttClient.subscribe(ResourceNameEnum.CDA_SENSOR_MSG_RESOURCE.getResourceName(),
				qos, 
				new SensorResponseMessageListener(ResourceNameEnum.CDA_SENSOR_MSG_RESOURCE, this.dataMsgListener));
			this.mqttClient.subscribe(ResourceNameEnum.CDA_SYSTEM_PERF_MSG_RESOURCE.getResourceName(),
				qos,
				new SystemPerformanceResponseMessageListener(ResourceNameEnum.CDA_SYSTEM_PERF_MSG_RESOURCE, this.dataMsgListener));
		} catch (MqttException e) {
			_Logger.warning("Failed to subscribe to CDA actuator response topic.");
		}	}

	@Override
	public void connectionLost(Throwable t)
	{
		_Logger.log(Level.INFO, "Client has successfully connected");
	}
	
	@Override
	public void deliveryComplete(IMqttDeliveryToken token)
	{
		_Logger.log(Level.INFO, "client has successfully published a message");

	}
	
	@Override
	public void messageArrived(String topic, MqttMessage msg) throws Exception
	{
		_Logger.log(Level.INFO, "Message arrived on topic" + topic + " Messgae: " + msg.toString());
	}

	
	// private methods
	
	/**
	 * Called by the constructor to set the MQTT client parameters to be used for the connection.
	 * 
	 * @param configSectionName The name of the configuration section to use for
	 * the MQTT client configuration parameters.
	 */
	private void initClientParameters(String configSectionName)
	{
		// TODO: implement this
		ConfigUtil configUtil = ConfigUtil.getInstance();
		
		this.host =
			configUtil.getProperty(
				configSectionName, ConfigConst.HOST_KEY, ConfigConst.DEFAULT_HOST);
		this.port =
			configUtil.getInteger(
				configSectionName, ConfigConst.PORT_KEY, ConfigConst.DEFAULT_MQTT_PORT);
		this.brokerKeepAlive =
			configUtil.getInteger(
				configSectionName, ConfigConst.KEEP_ALIVE_KEY, ConfigConst.DEFAULT_KEEP_ALIVE);
		this.enableEncryption =
			configUtil.getBoolean(
				configSectionName, ConfigConst.ENABLE_CRYPT_KEY);
		this.pemFileName =
			configUtil.getProperty(
				configSectionName, ConfigConst.CERT_FILE_KEY);
		
		// Paho Java client requires a client ID
		this.clientID = MqttClient.generateClientId();
		this.username = "BBFF-1cnXHhNxeBVY0BzXJ5a0eJNQG07HKU";
		// these are specific to the MQTT connection which will be used during connect
		this.persistence = new MemoryPersistence();
		this.connOpts    = new MqttConnectOptions();
		
		if(this.useCloudGatewayConfig == true) {
			this.connOpts.setUserName("BBFF-1cnXHhNxeBVY0BzXJ5a0eJNQG07HKU");
		}
		this.connOpts.setAutomaticReconnect(true);
		this.connOpts.setKeepAliveInterval(this.brokerKeepAlive);
		this.connOpts.setCleanSession(this.useCleanSession);
		
		// if encryption is enabled, try to load and apply the cert(s)
		if (this.enableEncryption) {
			initSecureConnectionParameters(configSectionName);
		}
		
		// if there's a credential file, try to load and apply them
		if (configUtil.hasProperty(configSectionName, ConfigConst.CRED_FILE_KEY)) {
			initCredentialConnectionParameters(configSectionName);
		}
		
		// NOTE: URL does not have a protocol handler for "tcp" or "ssl",
		// so construct the URL manually
		if(this.protocol ==  null) {
			this.protocol = "tcp";
		}
		this.brokerAddr  = this.protocol + "://" + this.host + ":" + this.port;
		
		_Logger.info("Using URL for broker conn: " + this.brokerAddr);		
	}
	
	/**
	 * Called by {@link #initClientParameters(String)} to load credentials.
	 * 
	 * @param configSectionName The name of the configuration section to use for
	 * the MQTT client configuration parameters.
	 */
	private void initCredentialConnectionParameters(String configSectionName)
	{
		// TODO: implement this
	}
	
	/**
	 * Called by {@link #initClientParameters(String)} to enable encryption.
	 * 
	 * @param configSectionName The name of the configuration section to use for
	 * the MQTT client configuration parameters.
	 */
	private void initSecureConnectionParameters(String configSectionName)
	{
		// TODO: implement this
	}
}

class ActuatorResponseMessageListener implements IMqttMessageListener
{
	private ResourceNameEnum resource = null;
	private IDataMessageListener dataMsgListener = null;
	private static final Logger _Logger =
			Logger.getLogger(MqttClientConnector.class.getName());
	ActuatorResponseMessageListener(ResourceNameEnum resource, IDataMessageListener dataMsgListener)
	{
		this.resource = resource;
		this.dataMsgListener = dataMsgListener;
	}
	
	@Override
	public void messageArrived(String topic, MqttMessage message) throws Exception
	{
		try {
			ActuatorData actuatorData =
				DataUtil.getInstance().jsonToActuatorData(new String(message.getPayload()));
			
			if (this.dataMsgListener != null) {
				this.dataMsgListener.handleActuatorCommandResponse(resource, actuatorData);
			}
		} catch (Exception e) {
			_Logger.warning("Failed to convert message payload to ActuatorData.");
		}
	}
	
}
class SensorResponseMessageListener implements IMqttMessageListener
{
	private ResourceNameEnum resource = null;
	private IDataMessageListener dataMsgListener = null;
	private static final Logger _Logger =
			Logger.getLogger(MqttClientConnector.class.getName());
	SensorResponseMessageListener(ResourceNameEnum resource, IDataMessageListener dataMsgListener)
	{
		this.resource = resource;
		this.dataMsgListener = dataMsgListener;
	}
	
	@Override
	public void messageArrived(String topic, MqttMessage message) throws Exception
	{
		try {
			_Logger.info("Payload: " + message.getPayload());
			SensorData sensorData =
				DataUtil.getInstance().jsonToSensorData(new String(message.getPayload()));
			
			if (this.dataMsgListener != null) {
				this.dataMsgListener.handleSensorMessage(resource, sensorData);
			}
		} catch (Exception e) {
			_Logger.warning("Failed to convert message payload to sensorData.");
		}
	}
	
}

class SystemPerformanceResponseMessageListener implements IMqttMessageListener
{
	private ResourceNameEnum resource = null;
	private IDataMessageListener dataMsgListener = null;
	private static final Logger _Logger =
			Logger.getLogger(MqttClientConnector.class.getName());
	SystemPerformanceResponseMessageListener(ResourceNameEnum resource, IDataMessageListener dataMsgListener)
	{
		this.resource = resource;
		this.dataMsgListener = dataMsgListener;
	}
	
	@Override
	public void messageArrived(String topic, MqttMessage message) throws Exception
	{
		try {
			SystemPerformanceData systemPerformanceData =
				DataUtil.getInstance().jsonToSystemPerformanceData(new String(message.getPayload()));
			
			if (this.dataMsgListener != null) {
				this.dataMsgListener.handleSystemPerformanceMessage(resource, systemPerformanceData);
			}
		} catch (Exception e) {
			_Logger.warning("Failed to convert message payload to systemPerformanceData.");
		}
	}
	
}

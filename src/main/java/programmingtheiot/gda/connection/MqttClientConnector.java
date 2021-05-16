/**
 * This class is part of the Programming the Internet of Things project.
 * 
 * It is provided as a simple shell to guide the student and assist with
 * implementation for the Programming the Internet of Things exercises,
 * and designed to be modified by the student as needed.
 */ 

package programmingtheiot.gda.connection;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.net.ssl.SSLSocketFactory;

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
import programmingtheiot.common.SimpleCertManagementUtil;
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
	String host,clientID,brokerAddr,pemFileName,username;
	String protocol = "tcp";
	MemoryPersistence persistence;
	MqttConnectOptions connOpts;
	int brokerKeepAlive,port;
	MqttClient mqttClient;
//	private boolean useCloudGatewayConfig = false;
	private boolean useCloudGatewayConfig;
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

	    _Logger.log(Level.INFO, "Using CloudGateway Config :" + useCloudGatewayConfig);

		
		this.useCloudGatewayConfig = useCloudGatewayConfig;
		
		if (useCloudGatewayConfig) {
		    _Logger.log(Level.INFO, "Using CloudGateway Config");

			initClientParameters(ConfigConst.CLOUD_GATEWAY_SERVICE);
		} else {
			_Logger.log(Level.INFO, "Using MQTT Gateway Config");
			initClientParameters(ConfigConst.MQTT_GATEWAY_SERVICE);
		}
	}	
//	public MqttClientConnector(boolean useCloudGatewayConfig)
//	{
//		super();
//									this.useCloudGatewayConfig = useCloudGatewayConfig;
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
//										initClientParameters(ConfigConst.CLOUD_GATEWAY_SERVICE);
//
////		// these are specific to the MQTT connection which will be used during connect
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
			_Logger.info("MQTT Client Connector is Started...");
			if(this.mqttClient != null) {
				_Logger.info("Client Already present..");
				return false;
			}
			
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
			ex.printStackTrace();
		}
		return false;

	}

	@Override
	public boolean disconnectClient()
	{
		try {
			if (this.mqttClient.isConnected()) {
				_Logger.info("Disconnecting MQTT Client");
				this.mqttClient.disconnect();
				return true;
			}else {
				_Logger.info("No Client to Disconnect");
				return false;
			}
			
		}catch(MqttException e) {
			_Logger.info("Sonething went wrong");
			e.printStackTrace();
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
		try {
			String topic = topicName.getResourceName();
			byte[] msgBytes = msg.getBytes();
			this.publishMessage(topic, msgBytes, qos);
			return true;
		} catch (Exception e) {
			_Logger.warning("Something went wrong" + e);
			e.printStackTrace();
		}
		
		return false;
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
			_Logger.info(" message to topic: " + message);

			
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
		
		try {
			String topic = topicName.toString();
			this.subscribeToTopic(topic, qos);
			_Logger.info("Successfully subscribed to " + topic);
			return true;
		} catch (Exception e) {
			_Logger.warning("Subscription went wrong");
			e.printStackTrace();
		}
		return false;
	}
	
//	{
//		_Logger.log(Level.INFO, "subscribeToTopic has been called");
//		if (topicName == null)
//			return false;
//		if (qos > 0 || qos < 2)
//			qos = ConfigConst.DEFAULT_QOS;
//		try {
//			mqttClient.subscribe(topicName.getResourceName(),qos);
//		} catch (MqttException e) {
//			e.printStackTrace();
//			return false;
//		}
//		return true;
//	}

	protected boolean subscribeToTopic(String topic, int qos)
	{
		//topic = "/ConstrainedDevice/DisplayCmd";
		// NOTE: you may want to log the exception stack trace if the call fails
		try {
			this.mqttClient.subscribe(topic, qos);
			
			return true;
		} catch (MqttException e) {
			//_Logger.warning("Failed to subscribe to topic: " + topic);
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
//	{
	{		_Logger.log(Level.INFO, "unsubscribeToTopic has been called");

		
		try {
			String topic = topicName.toString();
			this.unsubscribeFromTopic(topic);
			_Logger.info("Successfully unsubscribed from " + topic);
			return true;
		} catch (Exception e) {
			_Logger.warning("Subscription went wrong");
			e.printStackTrace();
		}
		return false;
	}
		//return true;
//	}

	@Override
	public boolean setDataMessageListener(IDataMessageListener listener)
	{
		_Logger.log(Level.INFO, "setDataMessageListener has been called");
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
        _Logger.info("connectComplete called ...");
        _Logger.info("MQTT connection successful (is reconnect = " + reconnect + "). Broker: " + serverURI);

 

        //if (useCloudGatewayConfig)
        {
            int qos = 1;
            this.subscribeToTopic(ResourceNameEnum.CDA_ACTUATOR_RESPONSE_RESOURCE, qos);
            this.subscribeToTopic(ResourceNameEnum.CDA_SENSOR_MSG_RESOURCE, qos);
            this.subscribeToTopic(ResourceNameEnum.CDA_SYSTEM_PERF_MSG_RESOURCE, qos);
        }
        }
	
	
	
	
	
//	{
//		_Logger.info("MQTT connection successful (is reconnect = " + reconnect + "). Broker: " + serverURI);
//		
//		int qos = 1;
//		
//		try {
//			//Try to subscribe to Actuator Response topic.
//			_Logger.warning("Topic Filter: " + ResourceNameEnum.CDA_ACTUATOR_RESPONSE_RESOURCE.getResourceName() + "  qos:  " + qos + " " + "  IMqttMessageListener: " + 				new ActuatorResponseMessageListener(ResourceNameEnum.CDA_ACTUATOR_RESPONSE_RESOURCE, this.dataMsgListener) );
//
//			this.mqttClient.subscribe(
//				ResourceNameEnum.CDA_ACTUATOR_RESPONSE_RESOURCE.getResourceName(),
//				qos,
//				new ActuatorResponseMessageListener(ResourceNameEnum.CDA_ACTUATOR_RESPONSE_RESOURCE, 
//						this.dataMsgListener));
//		} catch (MqttException e) {
//			//_Logger.warning("Failed to subscribe to CDA actuator response topic. "+ e.getMessage());
//		}
//		
//		try {
//			//Try to subscribe to Sensor Response topic.
//			this.mqttClient.subscribe(ResourceNameEnum.CDA_SENSOR_MSG_RESOURCE.getResourceName(), 
//					qos, new SensorResponseMessageListener(ResourceNameEnum.CDA_SENSOR_MSG_RESOURCE, this.dataMsgListener));
//			_Logger.info("MQTT Client CDA Sensor Response Successful");
//		} catch (MqttException e) {
//			//_Logger.warning("Failed to subscribe to CDA sensor response topic. "+ e.getMessage());
//		}
//		
//		try {
//			//Try to subscribe to CDA System topic.
//			this.mqttClient.subscribe(ResourceNameEnum.CDA_SYSTEM_PERF_MSG_RESOURCE.getResourceName(), qos, 
//					new SystemPerformanceResponseMessageListener(ResourceNameEnum.CDA_SYSTEM_PERF_MSG_RESOURCE, this.dataMsgListener));			
//			_Logger.info("MQTT Client CDA SYSTEM  Response Successful");
//		} catch (MqttException e) {
//		//	_Logger.warning("Failed to subscribe to CDA System Perf resource topic. " + e.getMessage());
//		}
//		
//		try {
//			if(this.useCloudGatewayConfig == true) {
//				this.mqttClient.subscribe(ResourceNameEnum.CDA_DISPLAY_RESPONSE_RESOURCE.getResourceName(), qos, 
//						new ActuatorResponseMessageListener(ResourceNameEnum.CDA_DISPLAY_RESPONSE_RESOURCE, this.dataMsgListener));
//			}	
//		} catch (Exception e) {
//		//	_Logger.warning("Failed to subscribe to CDA Display resource topic." + e.getMessage());
//		}
//	}
	
	
//	@Override
//	public void connectComplete(boolean reconnect, String serverURI)
//	{
//		_Logger.info("MQTT connection successful (is reconnect = " + reconnect + "). Broker: " + serverURI);
//		_Logger.info("List of topic names: ");
//		_Logger.info(ResourceNameEnum.CDA_ACTUATOR_RESPONSE_RESOURCE.getResourceName());
//		_Logger.info(ResourceNameEnum.CDA_SENSOR_MSG_RESOURCE.getResourceName());
//		_Logger.info(ResourceNameEnum.CDA_SYSTEM_PERF_MSG_RESOURCE.getResourceName());
//		
//		int qos = 1;
////		this.subscribeToTopic(ResourceNameEnum.CDA_ACTUATOR_RESPONSE_RESOURCE, qos);
////		this.subscribeToTopic(ResourceNameEnum.CDA_SENSOR_MSG_RESOURCE, qos);
////		this.subscribeToTopic(ResourceNameEnum.CDA_SYSTEM_PERF_MSG_RESOURCE, qos);
//		// Option 2
//		try {
//			_Logger.log(Level.INFO, "inside connectComplete with topic name" + ResourceNameEnum.CDA_ACTUATOR_RESPONSE_RESOURCE.getResourceName());
//			this.mqttClient.subscribe("/v1.6/devices/constraineddevice/displaycmd");
//			_Logger.info("Successfully subscribed 'displaycmd' topic");
//			this.mqttClient.subscribe(
//				ResourceNameEnum.CDA_ACTUATOR_RESPONSE_RESOURCE.getResourceName(),
//				qos,
//				new ActuatorResponseMessageListener(ResourceNameEnum.CDA_ACTUATOR_RESPONSE_RESOURCE, this.dataMsgListener));
//            _Logger.info("Successfully subscribed: " + ResourceNameEnum.CDA_ACTUATOR_RESPONSE_RESOURCE.getResourceName());
//
//		//	this.mqttClient.unsubscribe(ResourceNameEnum.CDA_ACTUATOR_RESPONSE_RESOURCE.getResourceName());
//          //  _Logger.info("Successfully unsubscribed: " + ResourceNameEnum.CDA_ACTUATOR_RESPONSE_RESOURCE.getResourceName());
//            
//			_Logger.log(Level.INFO, "inside connectComplete with topic name" + ResourceNameEnum.CDA_ACTUATOR_RESPONSE_RESOURCE.getResourceName());
//			this.mqttClient.subscribe(ResourceNameEnum.CDA_SENSOR_MSG_RESOURCE.getResourceName(),
//				qos, 
//				new SensorResponseMessageListener(ResourceNameEnum.CDA_SENSOR_MSG_RESOURCE, this.dataMsgListener));
//            _Logger.info("Successfully subscribed: " + ResourceNameEnum.CDA_SENSOR_MSG_RESOURCE.getResourceName());
//	//		this.mqttClient.unsubscribe(ResourceNameEnum.CDA_SENSOR_MSG_RESOURCE.getResourceName());
//      //      _Logger.info("Successfully unsubscribed: " + ResourceNameEnum.CDA_SENSOR_MSG_RESOURCE.getResourceName());
//
//			
//			this.mqttClient.subscribe(ResourceNameEnum.CDA_SYSTEM_PERF_MSG_RESOURCE.getResourceName(),
//				qos,
//				new SystemPerformanceResponseMessageListener(ResourceNameEnum.CDA_SYSTEM_PERF_MSG_RESOURCE, this.dataMsgListener));
//            _Logger.info("Successfully subscribed: " + ResourceNameEnum.CDA_SYSTEM_PERF_MSG_RESOURCE.getResourceName());
//		//	this.mqttClient.unsubscribe(ResourceNameEnum.CDA_SYSTEM_PERF_MSG_RESOURCE.getResourceName());
//         //   _Logger.info("Successfully unsubscribed: " + ResourceNameEnum.CDA_SYSTEM_PERF_MSG_RESOURCE.getResourceName());
//
//        //    this.mqttClient.unsubscribe("/v1.6/devices/constraineddevice/displaycmd");
//			//_Logger.info("Successfully unsubscribed 'displaycmd' topic");
//
//		} catch (MqttException e) {
//			_Logger.warning("Failed to subscribe to CDA actuator response topic." + " Exception: " + e.getMessage() );
//		}
	//}

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
		_Logger.log(Level.INFO, "Message arrived on topic: " + topic + " Messgae: " + msg.toString());

		_Logger.info("messageArrived called ..." + topic);

		 

  //      if(topic.equals(ConfigConst.CDA_SENSOR_MSG_RESOURCE))
//        {
//            try {
//                SensorData sensorData =
//                        DataUtil.getInstance().jsonToSensorData(new String(msg.getPayload()));
//
// 
//
//                if (this.dataMsgListener != null) {
//                    this.dataMsgListener.handleSensorMessage(ResourceNameEnum.CDA_SENSOR_MSG_RESOURCE, sensorData);
//                }
//            } catch (Exception e) {
//                _Logger.warning("Failed to convert message payload to sensorData.");
//            }
//        }
//        //else 
//        	if (topic.equals(ConfigConst.CDA_SYSTEM_PERF_MSG_RESOURCE))
//        {
//            try {
//                SystemPerformanceData sysPerfData =
//                        DataUtil.getInstance().jsonToSystemPerformanceData(new String(msg.getPayload()));
//
// 
//
//                if (this.dataMsgListener != null) {
//                    this.dataMsgListener.handleSystemPerformanceMessage(ResourceNameEnum.CDA_SYSTEM_PERF_MSG_RESOURCE, sysPerfData);
//                }
//            } catch (Exception e) {
//                _Logger.warning("Failed to convert message payload to SystemPerformanceData.");
//            }
//            }
		
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
		_Logger.info("initClientParameters for :" + configSectionName);	

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
		//this.username = "BBFF-PMCutJyfgw61xURX6kO5EMuCupzsBd";
		// these are specific to the MQTT connection which will be used during connect
		this.persistence = new MemoryPersistence();
		this.connOpts    = new MqttConnectOptions();
		_Logger.info("initClientParameters:useCloudGatewayConfig  :" + useCloudGatewayConfig);	

		if(this.useCloudGatewayConfig == true) {
			this.connOpts.setUserName(this.username);
		}
		this.connOpts.setKeepAliveInterval(this.brokerKeepAlive);
		this.connOpts.setCleanSession(this.useCleanSession);
		this.connOpts.setAutomaticReconnect(this.enableAutoReconnect);

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
		ConfigUtil configUtil = ConfigUtil.getInstance();
		
		try {
			_Logger.info("Checking if credentials file exists and us loadable...");
			
			Properties props = configUtil.getCredentials(configSectionName);
			
			if (props != null) {
				this.connOpts.setUserName(props.getProperty(ConfigConst.USER_NAME_TOKEN_KEY, ""));
				this.connOpts.setPassword(props.getProperty(ConfigConst.USER_AUTH_TOKEN_KEY, "").toCharArray());
				
				_Logger.info("Credentials now set.");
			} else {
				_Logger.warning("No credentials are set.");
			}
		} catch (Exception e) {
			_Logger.log(Level.WARNING, "Credential file non-existent. Disabling auth requirement.");
		}
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
		ConfigUtil configUtil = ConfigUtil.getInstance();
		
		try {
			_Logger.info("Configuring TLS...");
			
			if (this.pemFileName != null) {
				File file = new File(this.pemFileName);
				
				if (file.exists()) {
					_Logger.info("PEM file valid. Using secure connection: " + this.pemFileName);
				} else {
					this.enableEncryption = false;
					
					_Logger.log(Level.WARNING, "PEM file invalid. Using insecure connection: " + pemFileName, new Exception());
					
					return;
				}
			}
			
			SSLSocketFactory sslFactory =
				SimpleCertManagementUtil.getInstance().loadCertificate(this.pemFileName);
			
			this.connOpts.setSocketFactory(sslFactory);
			
			// override current config parameters
			this.port =
				configUtil.getInteger(
					configSectionName, ConfigConst.SECURE_PORT_KEY, ConfigConst.DEFAULT_MQTT_SECURE_PORT);
			
			this.protocol = ConfigConst.DEFAULT_MQTT_SECURE_PROTOCOL;
			
			_Logger.info("TLS enabled.");
		} catch (Exception e) {
			_Logger.log(Level.SEVERE, "Failed to initialize secure MQTT connection. Using insecure connection.", e);
			
			this.enableEncryption = false;
		}
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
			_Logger.warning("message payload to ActuatorData.  :"+ actuatorData);

			if (this.dataMsgListener != null) {
				this.dataMsgListener.handleActuatorCommandResponse(resource, actuatorData);
				_Logger.warning("Successfully to convert message payload to ActuatorData.");

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
			 String str = new String(message.getPayload());

			_Logger.warning(" message payload to sensorData." + str);

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
			 String str = new String(message.getPayload());
			_Logger.warning(" message payload to sensorData." + str);
			
			if (this.dataMsgListener != null) {
				this.dataMsgListener.handleSystemPerformanceMessage(resource, systemPerformanceData);
				_Logger.warning("Successfully to convert message payload to systemPerformanceData.");

			}
		} catch (Exception e) {
			_Logger.warning("Failed to convert message payload to systemPerformanceData.");
		}
	}
	
}

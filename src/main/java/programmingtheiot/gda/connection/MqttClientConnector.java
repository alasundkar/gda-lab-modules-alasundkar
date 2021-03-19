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
	String host,clientID,brokerAddr,protocol;
	MemoryPersistence persistence;
	MqttConnectOptions connOpts;
	int brokerKeepAlive,port;
	MqttClient mqttClient;
	
//	private MqttClient mqttClient = null;
	private IDataMessageListener dataMsgListener = null;
	// constructors
	
	/**
	 * Default.
	 * 
	 */
	public MqttClientConnector()
	{
		super();
		ConfigUtil configUtil = ConfigUtil.getInstance();

		this.host =
		    configUtil.getProperty(
		        ConfigConst.MQTT_GATEWAY_SERVICE, ConfigConst.HOST_KEY, ConfigConst.DEFAULT_HOST);

		this.port =
		    configUtil.getInteger(
		        ConfigConst.MQTT_GATEWAY_SERVICE, ConfigConst.PORT_KEY, ConfigConst.DEFAULT_MQTT_PORT);

		this.protocol = "http";
		this.brokerKeepAlive =
		    configUtil.getInteger(
		        ConfigConst.MQTT_GATEWAY_SERVICE, ConfigConst.KEEP_ALIVE_KEY, ConfigConst.DEFAULT_KEEP_ALIVE);

		// paho Java client requires a client ID
		this.clientID = MqttClient.generateClientId();

		// these are specific to the MQTT connection which will be used during connect
		this.persistence = new MemoryPersistence();
		this.connOpts = new MqttConnectOptions();

		this.connOpts.setKeepAliveInterval(this.brokerKeepAlive);
		this.connOpts.setCleanSession(false);
		this.connOpts.setAutomaticReconnect(true);

		// NOTE: URL does not have a protocol handler for "tcp",
		// so we need to construct the URL manually
		this.brokerAddr = "tcp" + "://" + this.host + ":" + this.port;
	}
	
	
	
	
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

	@Override
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

	@Override
	public boolean unsubscribeFromTopic(ResourceNameEnum topicName)
	{
		_Logger.log(Level.INFO, "unsubscribeToTopic has been called");
		return true;
	}

	@Override
	public boolean setDataMessageListener(IDataMessageListener listener)
	{
		_Logger.log(Level.INFO, "setDataMessageListener has been called");
	    if (listener != null) {
	        this.dataMsgListener = listener;
	        return true;
	    }
	    
		return false;
	}
	
	// callbacks
	
	@Override
	public void connectComplete(boolean reconnect, String serverURI)
	{
		_Logger.log(Level.INFO, "Client has successfully connected");
	}

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

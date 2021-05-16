/**
 * This class is part of the Programming the Internet of Things project.
 * 
 * It is provided as a simple shell to guide the student and assist with
 * implementation for the Programming the Internet of Things exercises,
 * and designed to be modified by the student as needed.
 */ 

package programmingtheiot.data;

import java.io.Serializable;

import programmingtheiot.common.ConfigConst;

/**
 * Shell representation of class for student implementation.
 *
 */
public class ActuatorData extends BaseIotData implements Serializable
{
	// static

	public static final int COMMAND_ON = 0;
	// private var's	
	private int command;
	private String stateData;
	private float value = 0.0f;
	private int actuatorType;
	// constructors
	
	/**
	 * Default.
	 * 
	 */
	public ActuatorData()
	{
		super();
	}
	public ActuatorData(int actuatorType) {
		super();
		this.setActuatorType(actuatorType);
	}	
	
	// public methods
	
	public int getCommand()
	{
		return this.command;
	}

	public String getStateData()
	{
		return this.stateData;
	}
	
	public float getValue()
	{
		//return 0.0f;
		return this.value;
	}
	
	public boolean isResponseFlagEnabled()
	{
		return false;
	}
	
	public void setCommand(int command)
	{
		this.command = command;
	}
	
	
	/**
	 * Get Actuator type.
	 * @return : Returns actuator type
	 */
	public int getActuatorType() {
		return this.actuatorType;
	}
		
	/**
	 * Sets Actuator type
	 * @param actuatorType : Contains value corresponding to the actuator.
	 */
	public void setActuatorType(int actuatorType)
	{
		this.actuatorType = actuatorType;
	}
	
	public void setValue(float val)
	{
		super.updateTimeStamp();
		this.value = val;
	}
	
	public void setStateData(String stateData)
	{
		 this.stateData = stateData ;
	}
	/**
	 * Returns a string representation of this instance. This will invoke the base class
	 * {@link #toString()} method, then append the output from this call.
	 * 
	 * @return String The string representing this instance, returned in CSV 'key=value' format.
	 */
	public String toString()
	{
		StringBuilder sb = new StringBuilder(super.toString());
		
		sb.append(',');
		sb.append(ConfigConst.COMMAND_PROP).append('=').append(this.getCommand()).append(',');
		sb.append(ConfigConst.IS_RESPONSE_PROP).append('=').append(this.isResponseFlagEnabled()).append(',');
		sb.append(ConfigConst.VALUE_PROP).append('=').append(this.getValue());
		
		return sb.toString();
	}
	
	
	// protected methods
	
	/* (non-Javadoc)
	 * @see programmingtheiot.data.BaseIotData#handleUpdateData(programmingtheiot.data.BaseIotData)
	 */
	protected void handleUpdateData(BaseIotData data)
	{
		  if (data instanceof ActuatorData) {
			    ActuatorData aData = (ActuatorData) data;
			    this.setCommand(aData.getCommand());
			    this.setValue(aData.getValue());
			    this.setStateData(aData.getStateData());
			    
			  //  if (aData.isResponseFlagEnabled()) {
			//      this.isResponseFlagEnabled() = true;
			  //  }
			  }
	}
	
}

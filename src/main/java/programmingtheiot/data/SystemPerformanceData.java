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
public class SystemPerformanceData extends BaseIotData implements Serializable
{
	// static
	
	
	// private var's
	private static final long serialVersionUID = 1L;
	private float cpuUtilization;
	private float memUtilization;
	private float diskUtilization;	
    
	// constructors

	/**
	 * Default.
	 * 
	 */
	public SystemPerformanceData()
	{
		super();
		
		super.setName(ConfigConst.SYS_PERF_DATA);
	}	
	
	// public methods
	
	public float getCpuUtilization()
	{
		return cpuUtilization;
	}
	
	public float getDiskUtilization()
	{
		return diskUtilization;
	}
	
	public float getMemoryUtilization()
	{
		return memUtilization;
	}
	
	public void setCpuUtilization(float val)
	{
		cpuUtilization = val;
	}
	
	public void setDiskUtilization(float val)
	{
		diskUtilization = val;
	}
	
	public void setMemoryUtilization(float val)
	{
		memUtilization = val;
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
		sb.append(ConfigConst.CPU_UTIL_PROP).append('=').append(this.getCpuUtilization()).append(',');
		sb.append(ConfigConst.DISK_UTIL_PROP).append('=').append(this.getDiskUtilization()).append(',');
		sb.append(ConfigConst.MEM_UTIL_PROP).append('=').append(this.getMemoryUtilization());
		
		return sb.toString();
	}
	
	
	// protected methods
	
	/* (non-Javadoc)
	 * @see programmingtheiot.data.BaseIotData#handleUpdateData(programmingtheiot.data.BaseIotData)
	 */
	protected void handleUpdateData(BaseIotData data)
	{
//		  if (data instanceof SystemPerformanceData) {
//			    SystemPerformanceData sData = (SystemPerformanceData) data;
//			    
//			    this.setCpuUtilization(sData.getCpuUtilization());
//			    this.setDiskUtilization(sData.getDiskUtilization());
//			    this.setMemoryUtilization(sData.getMemoryUtilization());
//			  }
		/*
		 * Update Name and Status Code of System Performance Data.
		 */
		this.setName(data.getName());
		this.setStatusCode(data.getStatusCode());
		
		/*
		 * Calls update methods to update the values received in the incoming 'data'
		 */
		this.setCpuUtilization(((SystemPerformanceData)data).getCpuUtilization());
		this.setMemoryUtilization(((SystemPerformanceData)data).getMemoryUtilization());
		this.setDiskUtilization(((SystemPerformanceData)data).getDiskUtilization());
	}
	
}

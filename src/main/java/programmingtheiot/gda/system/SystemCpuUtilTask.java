/**
 * This class is part of the Programming the Internet of Things project.
 * 
 * It is provided as a simple shell to guide the student and assist with
 * implementation for the Programming the Internet of Things exercises,
 * and designed to be modified by the student as needed.
 */ 

package programmingtheiot.gda.system;

import java.lang.management.ManagementFactory;
import java.lang.management.MemoryUsage;


/**
 * Shell representation of class for student implementation.
 * 
 */
public class SystemCpuUtilTask extends BaseSystemUtilTask
{
	// constructors
	
	/**
	 * Default.
	 * 
	 */
	public SystemCpuUtilTask()
	{
		super();
	}
	
	
	// protected methods
	
	@Override
	protected float getSystemUtil()
	{
		return 0.0f;
		//return (float) ManagementFactory.getOperatingSystemMXBean().getSystemLoadAverage();
	}
	public float getTelemetryValue()
	{
		MemoryUsage memUsage = ManagementFactory.getMemoryMXBean().getHeapMemoryUsage();
        double memUtil = ((double) memUsage.getUsed() / (double) memUsage.getMax()) * 100.0d;
       
     //   _Logger.info("memory utilization GDA::    " + memUtil);
       
        return (float)memUtil;
	}
}
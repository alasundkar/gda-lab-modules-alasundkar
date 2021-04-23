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
import java.util.logging.Logger;

import programmingtheiot.gda.connection.CloudClientConnector;


/**
 * Shell representation of class for student implementation.
 * 
 */
public class SystemCpuUtilTask extends BaseSystemUtilTask
{
	// constructors
	private static final Logger _Logger =
			Logger.getLogger(CloudClientConnector.class.getName());
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
		//return 10.0f;
		return (float) ManagementFactory.getOperatingSystemMXBean().getSystemLoadAverage();
	}
	public float getTelemetryValue()
	{
		
		 double cpuUsage = ManagementFactory.getOperatingSystemMXBean().getSystemLoadAverage();
    //    double cpuUtil = ((double) cpuUsage.getUsed() / (double) cpuUsage.getMax()) * 100.0d;
       
        _Logger.info("cpuUsage utilization GDA::    " + cpuUsage);
       
        return (float)cpuUsage;
	}
}

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
public class SystemMemUtilTask extends BaseSystemUtilTask
{
	// constructors
	private static final Logger _Logger =
			Logger.getLogger(CloudClientConnector.class.getName());
	/**
	 * Default.
	 * 
	 */
	public SystemMemUtilTask()
	{
		super();
	}
	
	
	// protected methods
	
	@Override
	protected float getSystemUtil()
	{
		//return 10.0f;
		return ManagementFactory.getMemoryMXBean().getHeapMemoryUsage().getUsed();
	}
	public float getTelemetryValue()
	{
		MemoryUsage memUsage = ManagementFactory.getMemoryMXBean().getHeapMemoryUsage();
        double memUtil = ((double) memUsage.getUsed() / (double) memUsage.getMax()) * 100.0d;
       
        _Logger.info("memory utilization GDA::    " + memUtil);
       
        return (float)memUtil;
	}
	
}

/**
 * This class is part of the Programming the Internet of Things project.
 * 
 * It is provided as a simple shell to guide the student and assist with
 * implementation for the Programming the Internet of Things exercises,
 * and designed to be modified by the student as needed.
 */ 

package programmingtheiot.gda.system;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;
import java.util.logging.Level;
import programmingtheiot.common.ConfigConst;
import programmingtheiot.common.ConfigUtil;
import programmingtheiot.common.IDataMessageListener;
import programmingtheiot.common.ResourceNameEnum;
import programmingtheiot.data.SystemPerformanceData;

/**
 * Shell representation of class for student implementation.
 * 
 */
public class SystemPerformanceManager
{
	// private var's
	private int pollSecs = 30;
	private ScheduledExecutorService schedExecSvc = null;
	private Runnable taskRunner = null;
	private boolean isStarted = false;
	private SystemCpuUtilTask cpuUtilTask = null;
	private SystemMemUtilTask memUtilTask = null;
	private static final Logger _Logger = Logger.getLogger(BaseSystemUtilTask.class.getName());
	private IDataMessageListener dataMsgListener;
	
	// constructors
	
	/**
	 * Default.
	 * 
	 */
	public SystemPerformanceManager()
	{
		this(ConfigConst.DEFAULT_POLL_CYCLES);
		
		this.schedExecSvc = Executors.newScheduledThreadPool(1);
		this.cpuUtilTask = new SystemCpuUtilTask();
		this.memUtilTask = new SystemMemUtilTask();
		this.taskRunner = () -> {
		    this.handleTelemetry();
		};
	}
	/**
	 * Constructor.
	 * 
	 * @param pollSecs The number of seconds between each scheduled task poll.
	 */
	public SystemPerformanceManager(int pollSecs)
	{
		/**
		 * Check if pollSecs value lies between 1 and max value
		 */
		if(pollSecs>1 && pollSecs<Integer.MAX_VALUE)
		{
			this.pollSecs = pollSecs;
		}
	}	
	
	// public methods
	
	public void handleTelemetry()
	{
		float cpuUtil = this.cpuUtilTask.getTelemetryValue();
		float memUtil = this.memUtilTask.getTelemetryValue();
		_Logger.info("cpuUtilTask = " + cpuUtil);
		_Logger.info("memUtilTask = " + memUtil);
		
		//_Logger.debug("CPU utilization: " + cpuUtil + ", Mem utilization: " + memUtil);
//		
//		SystemPerformanceData spd = new SystemPerformanceData();
//		//spd.setLocationID(this.locationID);
//		spd.setCpuUtilization(cpuUtil);
//		spd.setMemoryUtilization(memUtil);
//		
//		if (this.dataMsgListener != null) {
//			this.dataMsgListener.handleSystemPerformanceMessage(
//				ResourceNameEnum.GDA_SYSTEM_PERF_MSG_RESOURCE, spd);
//		}
	}
	
	public void setDataMessageListener(IDataMessageListener listener)
	{
		if (listener != null) {
			this.dataMsgListener = listener;
		}
	}
	
	public void startManager()
	{
		if (! this.isStarted) {
		    ScheduledFuture<?> futureTask = this.schedExecSvc.scheduleAtFixedRate(this.taskRunner, 0L, this.pollSecs, TimeUnit.SECONDS);

		    this.isStarted = true;
		}
		_Logger.info("SystemPerformanceManager started");
	}
	
	public void stopManager()
	{
		_Logger.info("SystemPerformanceManager stopped");
		this.schedExecSvc.shutdown();	
	}
	
}

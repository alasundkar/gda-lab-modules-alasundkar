/**
 * This class is part of the Programming the Internet of Things
 * project, and is available via the MIT License, which can be
 * found in the LICENSE file at the top level of this repository.
 * 
 * Copyright (c) 2020 by Andrew D. King
 */

package programmingtheiot.common;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * A convenience class to provide type consistency around commonly used
 * topics and resource names.
 * 
 */
public enum ResourceNameEnum
{
	// static
	
	CDA_SENSOR_MSG_RESOURCE(
		ConfigConst.PRODUCT_NAME, ConfigConst.CONSTRAINED_DEVICE, ConfigConst.SENSOR_MSG, false),
	CDA_ACTUATOR_CMD_RESOURCE(
		ConfigConst.PRODUCT_NAME, ConfigConst.CONSTRAINED_DEVICE, ConfigConst.ACTUATOR_CMD, false),
	CDA_ACTUATOR_RESPONSE_RESOURCE(
		ConfigConst.PRODUCT_NAME, ConfigConst.CONSTRAINED_DEVICE, ConfigConst.ACTUATOR_RESPONSE, false),
	CDA_DISPLAY_RESPONSE_RESOURCE(
		ConfigConst.PRODUCT_NAME, ConfigConst.CONSTRAINED_DEVICE, ConfigConst.DISPLAY_CMD, false),
	CDA_MGMT_STATUS_MSG_RESOURCE(
		ConfigConst.PRODUCT_NAME, ConfigConst.CONSTRAINED_DEVICE, ConfigConst.MGMT_STATUS_MSG, false),
	CDA_MGMT_STATUS_CMD_RESOURCE(
		ConfigConst.PRODUCT_NAME, ConfigConst.CONSTRAINED_DEVICE, ConfigConst.MGMT_STATUS_CMD, false),
	CDA_SYSTEM_PERF_MSG_RESOURCE(
		ConfigConst.PRODUCT_NAME, ConfigConst.CONSTRAINED_DEVICE, ConfigConst.SYSTEM_PERF_MSG, false),
	
	GDA_MGMT_STATUS_MSG_RESOURCE(
		ConfigConst.PRODUCT_NAME, ConfigConst.GATEWAY_DEVICE, ConfigConst.MGMT_STATUS_MSG, false),
	GDA_MGMT_STATUS_CMD_RESOURCE(
		ConfigConst.PRODUCT_NAME, ConfigConst.GATEWAY_DEVICE, ConfigConst.MGMT_STATUS_CMD, false),
	GDA_SYSTEM_PERF_MSG_RESOURCE(
		ConfigConst.PRODUCT_NAME, ConfigConst.GATEWAY_DEVICE, ConfigConst.SYSTEM_PERF_MSG, false);
	
	private static final HashMap<String, ResourceNameEnum> _ResourceNameLookupMap = new HashMap<>();
	
	static {
		for (ResourceNameEnum rn : ResourceNameEnum.values()) {
			_ResourceNameLookupMap.put(rn.getResourceName(), rn);
		}
	}
	
	/**
	 * Convenience method for looking up an enum type based on
	 * the value String. If the lookup fails, null will be returned.
	 * <p>
	 * No error or warning message will be logged, and no exception
	 * will be thrown. If this is called and null is returned, it's
	 * safe to assume that the value simply does not map to any of
	 * the enum type values represented by this class.
	 * 
	 * @param valStr The value of the enum to lookup.
	 * @return ResourceNameEnum The enum instance, or null if not found.
	 */
	public static ResourceNameEnum getEnumFromValue(String valStr)
	{
		if (valStr != null && valStr.length() > 0) {
			if (_ResourceNameLookupMap.containsKey(valStr)) {
				return _ResourceNameLookupMap.get(valStr);
			}
		}
		
		return null;
	}
	
	
	// private var's
	
	private String deviceName = "";
	private String productName = "";
	private String resourceName = "";
	private String resourceType = "";
	private boolean isLocalToGDA = false;
	
	
	// constructor
	
	/**
	 * Constructor.
	 * 
	 * @param productName
	 * @param deviceName
	 * @param resourceType
	 * @param isLocalToGda
	 */
	private ResourceNameEnum(String productName, String deviceName, String resourceType, boolean isLocalToGda)
	{
		this.resourceName = productName + "/" + deviceName + "/" + resourceType;
		this.productName = productName;
		this.deviceName = deviceName;
		this.resourceType = resourceType;
		this.isLocalToGDA = isLocalToGda;
	}
	
	
	// public methods
	
	/**
	 * 
	 * @return String
	 */
	public String getDeviceName()
	{
		return this.deviceName;
	}
	
	/**
	 * 
	 * @return String
	 */
	public String getProductName()
	{
		return this.productName;
	}
	
	/**
	 * 
	 * @return String
	 */
	public String getResourceName()
	{
		return this.resourceName;
	}
	
	/**
	 * 
	 * @return String
	 */
	public String getResourceType()
	{
		return this.resourceType;
	}
	
	/**
	 * 
	 * @return List<String> The ordered list of Strings representing this
	 * resource name split by '/'.
	 */
	public List<String> getResourceNameChain()
	{
		String[] names = this.resourceName.split("/");
		
		List<String> nameList = new ArrayList<>(names.length);
		
		for (String name : names) {
			nameList.add(name);
		}
		
		return nameList;
	}
	
	/**
	 * 
	 * @return boolean True if this resource is local to the GDA (meaning any
	 * use of the resource is internal to the GDA); false if it's not (meaning
	 * it's a resource used by the CDA).
	 */
	public boolean isLocalToGda()
	{
		return this.isLocalToGDA;
	}
	
}

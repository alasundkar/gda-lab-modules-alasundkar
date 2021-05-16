/**
 * 
 * This class is part of the Programming the Internet of Things
 * project, and is available via the MIT License, which can be
 * found in the LICENSE file at the top level of this repository.
 * 
 * Copyright (c) 2020 by Andrew D. King
 */ 

package programmingtheiot.part03.integration.connection;

import static org.junit.Assert.*;

import java.util.logging.Logger;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import programmingtheiot.common.ConfigConst;
import programmingtheiot.common.ConfigUtil;
import programmingtheiot.common.DefaultDataMessageListener;
import programmingtheiot.common.IDataMessageListener;
import programmingtheiot.common.ResourceNameEnum;
import programmingtheiot.data.DataUtil;
import programmingtheiot.data.SystemStateData;
import programmingtheiot.gda.connection.*;

/**
 * This test case class contains very basic integration tests for
 * CoapClientToServerConnectorTest. It should not be considered complete,
 * but serve as a starting point for the student implementing
 * additional functionality within their Programming the IoT
 * environment.
 *
 */
public class CoapClientToServerConnectorTest
{
	// static
	
	public static final int DEFAULT_TIMEOUT = 5;
	public static final boolean USE_DEFAULT_RESOURCES = true;
	
	private static final Logger _Logger =
		Logger.getLogger(CoapClientToServerConnectorTest.class.getName());
	
	private static CoapServerGateway _Csg = null;
	
	// member var's
	
	private CoapClientConnector ccc = null;
	private IDataMessageListener dml = null;
	
	
	// test setup methods
	
	/**
	 * @throws java.lang.Exception
	 */
	@BeforeClass
	public static void setUpBeforeClass() throws Exception
	{
		_Csg = new CoapServerGateway(USE_DEFAULT_RESOURCES);
		
		assertTrue(_Csg.startServer());
	}
	
	/**
	 * @throws java.lang.Exception
	 */
	@AfterClass
	public static void tearDownAfterClass() throws Exception
	{
		assertTrue(_Csg.stopServer());
	}
	
	/**
	 * @throws java.lang.Exception
	 */
	@Before
	public void setUp() throws Exception
	{
		this.ccc = new CoapClientConnector();
		this.dml = new DefaultDataMessageListener();
		
		this.ccc.setDataMessageListener(this.dml);
	}
	
	/**
	 * @throws java.lang.Exception
	 */
	@After
	public void tearDown() throws Exception
	{
	}
	
	// test methods
	
	/**
	 * 
	 */
	@Test
	public void testConnectAndDiscover()
	{
		assertTrue(this.ccc.sendDiscoveryRequest(DEFAULT_TIMEOUT));
	}
	
	/**
	 * 
	 */
	@Test
	public void testGetRequestCon()
	{
		// TODO: issue request and validate response
		
		assertTrue(this.ccc.sendGetRequest(ResourceNameEnum.GDA_MGMT_STATUS_MSG_RESOURCE, true, DEFAULT_TIMEOUT));
	}
	
	/**
	 * 
	 */
	@Test
	public void testGetRequestNon()
	{
		// TODO: issue request and validate response
		
		assertTrue(this.ccc.sendGetRequest(ResourceNameEnum.GDA_MGMT_STATUS_MSG_RESOURCE, false, DEFAULT_TIMEOUT));
	}
	
	/**
	 * 
	 */
	@Test
	public void testPostRequestCon()
	{
		// TODO: issue request and validate response
		
		int actionCmd = 1;
		
		SystemStateData ssd = new SystemStateData();
		ssd.setCommand(actionCmd);
		
		String ssdJson = DataUtil.getInstance().systemStateDataToJson(ssd);
		
		assertTrue(this.ccc.sendPostRequest(ResourceNameEnum.GDA_MGMT_STATUS_MSG_RESOURCE, true, ssdJson, DEFAULT_TIMEOUT));
	}
	
	/**
	 * 
	 */
	@Test
	public void testPostRequestNon()
	{
		// TODO: issue request and validate response
		
		int actionCmd = 1;
		
		SystemStateData ssd = new SystemStateData();
		ssd.setCommand(actionCmd);
		
		String ssdJson = DataUtil.getInstance().systemStateDataToJson(ssd);
		
		assertTrue(this.ccc.sendPostRequest(ResourceNameEnum.GDA_MGMT_STATUS_MSG_RESOURCE, false, ssdJson, DEFAULT_TIMEOUT));
	}
	
	/**
	 * 
	 */
	@Test
	public void testPutRequestCon()
	{
		// TODO: issue request and validate response
		
		int actionCmd = 2;
		
		SystemStateData ssd = new SystemStateData();
		ssd.setCommand(actionCmd);
		
		String ssdJson = DataUtil.getInstance().systemStateDataToJson(ssd);
		
		assertTrue(this.ccc.sendPutRequest(ResourceNameEnum.GDA_MGMT_STATUS_MSG_RESOURCE, true, ssdJson, DEFAULT_TIMEOUT));
	}
	
	/**
	 * 
	 */
	@Test
	public void testPutRequestNon()
	{
		// TODO: issue request and validate response
		
		int actionCmd = 2;
		
		SystemStateData ssd = new SystemStateData();
		ssd.setCommand(actionCmd);
		
		String ssdJson = DataUtil.getInstance().systemStateDataToJson(ssd);
		
		assertTrue(this.ccc.sendPutRequest(ResourceNameEnum.GDA_MGMT_STATUS_MSG_RESOURCE, false, ssdJson, DEFAULT_TIMEOUT));
	}
	
	/**
	 * 
	 */
	@Test
	public void testDeleteRequestCon()
	{
		// TODO: issue request and validate response
		
		assertTrue(this.ccc.sendDeleteRequest(ResourceNameEnum.GDA_MGMT_STATUS_CMD_RESOURCE, true, DEFAULT_TIMEOUT));
	}
	
	/**
	 * 
	 */
	@Test
	public void testDeleteRequestNon()
	{
		// TODO: issue request and validate response
		
		assertTrue(this.ccc.sendDeleteRequest(ResourceNameEnum.GDA_MGMT_STATUS_CMD_RESOURCE, false, DEFAULT_TIMEOUT));
	}
	
}

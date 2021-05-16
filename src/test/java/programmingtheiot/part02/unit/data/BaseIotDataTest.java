/**
 * This class is part of the Programming the Internet of Things
 * project, and is available via the MIT License, which can be
 * found in the LICENSE file at the top level of this repository.
 * 
 * Copyright (c) 2020 by Andrew D. King
 */ 

package programmingtheiot.part02.unit.data;

import static org.junit.Assert.*;

import java.util.logging.Logger;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import programmingtheiot.common.ConfigConst;
import programmingtheiot.data.BaseIotData;

/**
 * This test case class contains very basic unit tests for
 * BaseIotData. It should not be considered complete,
 * but serve as a starting point for the student implementing
 * additional functionality within their Programming the IoT
 * environment.
 *
 */
public class BaseIotDataTest
{
	// static
	
	private static final Logger _Logger =
		Logger.getLogger(BaseIotDataTest.class.getName());
	
	public static final String DEFAULT_NAME        = "TestIotDataSample";
	public static final String DEFAULT_LOCATION_ID = "MyLocation";
	public static final int    DEFAULT_STATUS_CODE = 1;
	
	
	// member var's
	
	
	// test setup methods
	
	@Before
	public void setUp() throws Exception
	{
	}
	
	@After
	public void tearDown() throws Exception
	{
	}
	
	
	// test methods
	
	@Test
	public void testDefaultValues()
	{
		TestIotData td = new TestIotData();
		
		assertEquals(td.getName(), ConfigConst.NOT_SET);
		assertEquals(td.getTypeID(), ConfigConst.DEFAULT_TYPE);
		assertEquals(td.getLocationID(), "gatewaydevice001"); // from PiotConfig.props
		assertEquals(td.getStatusCode(), ConfigConst.DEFAULT_STATUS);
	}
	
	@Test
	public void testParameterUpdates()
	{
		TestIotData td = createTestData();
		
		assertEquals(td.getName(), DEFAULT_NAME);
		assertEquals(td.getLocationID(), DEFAULT_LOCATION_ID);
		assertEquals(td.getStatusCode(), DEFAULT_STATUS_CODE);
		
		td.setStatusCode(-1);
		
		assertTrue(td.hasError());
	}
	
	@Test
	public void testFullUpdate()
	{
		TestIotData td = new TestIotData();
		TestIotData td2 = createTestData();

		assertEquals(td.getName(), ConfigConst.NOT_SET);
		assertEquals(td.getLocationID(), "gatewaydevice001"); // from PiotConfig.props
		assertEquals(td.getStatusCode(), ConfigConst.DEFAULT_STATUS);
		
		td.updateData(td2);
		
		assertEquals(td.getName(), DEFAULT_NAME);
		assertEquals(td.getLocationID(), DEFAULT_LOCATION_ID);
		assertEquals(td.getStatusCode(), DEFAULT_STATUS_CODE);
	}
	
	
	// private
	
	private class TestIotData extends BaseIotData
	{
		/**
		 * 
		 */
		private static final long serialVersionUID = -5589706082614880746L;

		@Override
		protected void handleUpdateData(BaseIotData data)
		{
			// nothing to do
		}
		
	}
	
	private TestIotData createTestData()
	{
		TestIotData td = new TestIotData();
		td.setName(DEFAULT_NAME);
		td.setLocationID(DEFAULT_LOCATION_ID);
		td.setStatusCode(DEFAULT_STATUS_CODE);
		
		return td;
	}
	
}

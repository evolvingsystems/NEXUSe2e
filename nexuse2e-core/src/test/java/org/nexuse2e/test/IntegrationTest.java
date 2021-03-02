/**
 *  NEXUSe2e Business Messaging Open Source
 *  Copyright 2000-2021, direkt gruppe GmbH
 *
 *  This is free software; you can redistribute it and/or modify it
 *  under the terms of the GNU Lesser General Public License as
 *  published by the Free Software Foundation version 3 of
 *  the License.
 *
 *  This software is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 *  Lesser General Public License for more details.
 *
 *  You should have received a copy of the GNU Lesser General Public
 *  License along with this software; if not, write to the Free
 *  Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 *  02110-1301 USA, or see the FSF site: http://www.fsf.org.
 */
package org.nexuse2e.test;

import org.junit.Test;

public class IntegrationTest {

//	/** The tomcat instance. */
//	private Tomcat mTomcat;
//	/** The temporary directory in which Tomcat and the app are deployed. */
//	private String mWorkingDir = "c:\\temp_deployment"; //System.getProperty("java.io.tmpdir");
//	
//	
//	@Before
//	public void setup() throws Throwable {
//	  mTomcat = new Tomcat();
//	  mTomcat.setPort(0);
//	  mTomcat.setBaseDir(mWorkingDir);
//	  mTomcat.getHost().setAppBase(mWorkingDir);
//	  mTomcat.getHost().setAutoDeploy(true);
//	  mTomcat.getHost().setDeployOnStartup(true);
//	  
//	  String contextPath = "/NEXUSe2e";
//	  File webApp = new File(mWorkingDir, "NEXUSe2e");
//	  File oldWebApp = new File(webApp.getAbsolutePath());
//	  //FileUtils.deleteDirectory(oldWebApp);
//	   
//	  mTomcat.addWebapp("/NEXUSe2e", "NEXUSe2e");
//	  
//	  mTomcat.start();
//
//	}
//
//	
//	@After
//	public final void teardown() throws Throwable {
//	  if (mTomcat.getServer() != null
//	            && mTomcat.getServer().getState() != LifecycleState.DESTROYED) {
//	        if (mTomcat.getServer().getState() != LifecycleState.STOPPED) {
//	              mTomcat.stop();
//	        }
//	        mTomcat.destroy();
//	    }
//	}
//	
	@Test
	public void test1() {
//		int port = mTomcat.getConnector().getLocalPort();
//		System.out.println("port: "+port);
//		
	}
}

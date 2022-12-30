/**
 * NEXUSe2e Business Messaging Open Source
 * Copyright 2000-2021, direkt gruppe GmbH
 * <p>
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation version 3 of
 * the License.
 * <p>
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 * <p>
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
 */
package org.nexuse2e;


import com.mchange.v2.c3p0.ComboPooledDataSource;

import org.nexuse2e.StatusSummary.Status;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.context.support.WebApplicationObjectSupport;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

import jakarta.servlet.ServletContext;
import javax.sql.DataSource;

/**
 * @author gesch
 *
 */
public class EngineMonitor extends WebApplicationObjectSupport {

    private static Logger LOG = LoggerFactory.getLogger(EngineMonitor.class);
    protected EngineStatusSummary currentEngineStatusSummary = null;
    private List<EngineMonitorListener> listeners;
    private Timer timer;
    private String nexusE2ERoot = null;
    private boolean shutdownInitiated = false;
    private boolean autoStart = true;
    private DataSource dataSource = null;
    private ExecutorService executor = null;
    /**
     * Database Timeout in miliseconds
     */
    private int timeout = 10000;
    /**
     * Monitor probe Interval in miliseconds
     */
    private int interval = 10000;

    public DataSource getDataSource() {
        return dataSource;
    }

    public void setDataSource(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    public void initialize() {


        // Set Derby home directory to determine where the DB will be created
        nexusE2ERoot = Engine.getInstance().getNexusE2ERoot();
        try {
            if (nexusE2ERoot == null) {
                ServletContext currentContext = getServletContext();
                String webAppPath = currentContext.getRealPath("");
                webAppPath = webAppPath.replace('\\', '/');
                if (!webAppPath.endsWith("/")) {
                    webAppPath += "/";
                }
                nexusE2ERoot = webAppPath;
            }
        } catch (IllegalStateException isex) {
            if (nexusE2ERoot == null) {
                throw new IllegalStateException("nexusE2ERoot must be set if not running in a WebApplicationContext");
            }
        }
        if (null == System.getProperty("derby.system.home") && null != nexusE2ERoot) {
            LOG.trace("Setting derby root directory to: " + nexusE2ERoot + Constants.DERBYROOT);
            System.setProperty("derby.system.home", nexusE2ERoot + Constants.DERBYROOT);
        } else {
            LOG.trace("Derby root directory already set: " + System.getProperty("derby.system.home"));
        }

    }

    /**
     *
     */
    public void start() {
        executor = Executors.newFixedThreadPool(1);

        // Set Derby home directory to determine where the DB will be created
        nexusE2ERoot = Engine.getInstance().getNexusE2ERoot();
        if (System.getProperty("derby.system.home") == null) {
            LOG.trace("Setting derby root directory to: " + nexusE2ERoot + Constants.DERBYROOT);
            System.setProperty("derby.system.home", nexusE2ERoot + Constants.DERBYROOT);
        } else {
            LOG.trace("Derby root directory already set: " + System.getProperty("derby.system.home"));
        }
        if (dataSource == null) {
            LOG.error("DataSouece not provided, the configuration is not valid. Please update beans.xml " + "files " +
                    "engine monitor section. Also be aware of the unit change for timeout, its miliseconds now. " +
                    "Monitoring will be disabled");
        } else {
            LOG.debug("Engine monitor initalized");
            timer = new Timer();
            TestSuite suite = new TestSuite();
            timer.schedule(suite, 30000, interval); //delayed first schedule
        }
    }

    /**
     * shutdown probe timer and additional connection timeout guards
     */
    public void stop() {
        executor.shutdown();
        timer.cancel();
    }

    /**
     * @return
     */
    public EngineStatusSummary getStatus() {

        return currentEngineStatusSummary;
    }

    /**
     * @param summary
     * @return
     */
    public EngineStatusSummary filloutStatusSummary(EngineStatusSummary summary) {

        if (Engine.getInstance().getStatus() == BeanStatus.STARTED) {
            summary.setStatus(Status.ACTIVE);
        } else {
            summary.setStatus(Status.INACTIVE);
        }
        return summary;
    }

    /**
     * @param listner
     */
    public void addListener(EngineMonitorListener listener) {

        if (listeners == null) {
            listeners = new ArrayList<EngineMonitorListener>();
        }
        listeners.add(listener);
    }

    public void removeListener(EngineMonitorListener listener) {

        if (listeners != null) {
            listeners.remove(listener);
        }
    }

    /**
     * be aware, LOG output might generate database queries because of the underlaying logging configuration. 
     * By default error message are also logged as engine logged. 
     *
     * @return
     */
    private synchronized EngineStatusSummary probe() {

        final EngineStatusSummary summary = new EngineStatusSummary();

        // statement query timeout doesn't work for mssql mirror database. Maybe
        // its working for mssql standalone.

        Boolean successful = false;
        Future<Boolean> future = null;
        String causeMsg = null;
        try {
            final Connection con = dataSource.getConnection();

            try {

                future = executor.submit(new Callable<Boolean>() {
                    public Boolean call() throws Exception {
                        ResultSet result = null;
                        try {
                            PreparedStatement statement = con.prepareStatement("select count(*) from nx_trp");
                            result = statement.executeQuery();
                            result.next();
                            int count = result.getInt(1);
                            if (count == 0) {
                                throw new NexusException("no TRP's found in database");
                            }

                        } catch (Exception e) {
                            throw e;
                        } finally {
                            if (result != null) {
                                try {
                                    result.close();
                                } catch (SQLException e) {
                                    // force close in case the result is not closed yet. isClosed throws an
                                    // unexpected exception with mssql drivers.
                                }
                            }
                            if (con != null && !con.isClosed()) {
                                try {
                                    con.close();
                                } catch (SQLException e) {
                                    // isClosed seems to work for connections and mssql
                                }
                            }
                        }
                        return true;
                    }
                });

                successful = future.get(timeout, TimeUnit.MILLISECONDS);
            } catch (Exception e) {
                causeMsg = "Exception occured while probing database: " + e.getMessage();
            } finally {
                try {
                    if (con != null && !con.isClosed()) {
                        con.close();
                    }
                } catch (SQLException e) {
                    //finally failed to close...
                }
                if (!future.isDone()) {
                    future.cancel(true);
                }
            }
        } catch (SQLException e) {
            causeMsg = "failed to open connection";
        }


        if (!successful) {
            summary.setCause(causeMsg);
            summary.setDatabaseStatus(Status.ERROR);
            summary.setStatus(Status.ERROR);
            return summary;
        }
        summary.setDatabaseStatus(Status.ACTIVE);

        if (Engine.getInstance().getStatus() == BeanStatus.STARTED) {
            summary.setStatus(Status.ACTIVE);
        } else {
            summary.setStatus(Status.INACTIVE);
        }

        return summary;
    }

    /**
     * @return the timeout
     */
    public int getTimeout() {

        return timeout;
    }

    /**
     * @param timeout the timeout to set
     */
    public void setTimeout(int timeout) {

        this.timeout = timeout;
    }

    /**
     * @return the interval
     */
    public int getInterval() {

        return interval;
    }

    /**
     * @param interval the interval to set
     */
    public void setInterval(int interval) {

        this.interval = interval;
    }

    /**
     * @return the autoStart
     */
    public boolean isAutoStart() {

        return autoStart;
    }

    /**
     * @param autoStart the autoStart to set
     */
    public void setAutoStart(boolean autoStart) {

        this.autoStart = autoStart;
    }

    /**
     * @author gesch
     *
     */
    public class TestSuite extends TimerTask {

        /**
         *
         */
        public TestSuite() {

        }

        @Override
        public void run() {

            try {
                EngineStatusSummary summary = probe();
                if (summary.getStatus() == Status.ERROR) {
                    try {
                        if (!shutdownInitiated) {
                            Engine.getInstance().changeStatus(BeanStatus.INSTANTIATED);
                            LOG.info("Engine shutdown triggered (cause: " + summary.getCause() + ")");
                            shutdownInitiated = true;
                            // required to reset all pooled connections to make sure, the jdbc driver takes the
                            // failover node if available.
                            if (dataSource instanceof ComboPooledDataSource) { // will not work for external
                                // datasource pools. (DataSource interface doesn't provide reset methods)
                                ((ComboPooledDataSource) dataSource).resetPoolManager(true);
                            }
                        }
                    } catch (InstantiationException e) {
                        LOG.error("Error while handling error: (cause: " + summary.getCause() + "): " + e);
                    }
                } else if (shutdownInitiated) {
                    if (Engine.getInstance().getStatus().equals(BeanStatus.INSTANTIATED)) {
                        shutdownInitiated = false;
                        Engine.getInstance().changeStatus(BeanStatus.STARTED);
                        LOG.info("Engine startup triggered");
                    }
                } else {
                    shutdownInitiated = false;
                }

                currentEngineStatusSummary = summary;

                if (listeners != null) {

                    for (EngineMonitorListener listener : listeners) {
                        EngineStatusSummary specialSummary = listener.getSummaryInstance();
                        specialSummary.update(summary);
                        listener.engineEvent(specialSummary);
                    }
                }
            } catch (Exception e) {
                System.out.println("Monitoring: " + e);
                e.printStackTrace();
            }
        }
    }


}

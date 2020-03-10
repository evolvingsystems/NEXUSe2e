/**
 * NEXUSe2e Business Messaging Open Source
 * Copyright 2000-2009, Tamgroup and X-ioma GmbH
 * <p>
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation version 2.1 of
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
package org.nexuse2e.service.sftp;

import com.jcraft.jsch.*;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.nexuse2e.BeanStatus;
import org.nexuse2e.Engine;
import org.nexuse2e.Layer;
import org.nexuse2e.NexusException;
import org.nexuse2e.backend.BackendPipelineDispatcher;
import org.nexuse2e.configuration.EngineConfiguration;
import org.nexuse2e.configuration.ParameterDescriptor;
import org.nexuse2e.configuration.ParameterType;
import org.nexuse2e.service.AbstractService;
import org.nexuse2e.service.SchedulerClient;
import org.nexuse2e.service.SchedulingService;
import org.nexuse2e.service.Service;

import java.io.ByteArrayOutputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Vector;
import java.util.regex.Pattern;

/**
 * @author Guido Esch
 */
public class SftpBackendPollingService extends AbstractService implements SchedulerClient {

    private static Logger LOG = Logger.getLogger(SftpBackendPollingService.class);

    private static final String CUSTOM_PARAMETER_FILE_NAME = "fileName";
    private static final String PARTNERID_PARAM_NAME = "partnerId";
    private static final String URL_PARAM_NAME = "url";
    private static final String FILE_PATTERN_PARAM_NAME = "filePattern";
    private static final String USER_PARAM_NAME = "username";
    private static final String PASSWORD_PARAM_NAME = "password";
    private static final String INTERVAL_PARAM_NAME = "interval";
    private static final String RENAMING_PREFIX_PARAM_NAME = "prefix";
    private static final String CHANGE_FILE_PARAM_NAME = "changeFile";
    private static final String SCHEDULING_SERVICE_PARAM_NAME = "schedulerName";
    private static final String CHOREOGRAPHY_PARAM_NAME = "choreography";
    private static final String ACTION_PARAM_NAME = "action";
    private static final String SFTP_CONNECT_TIMEOUT_PARAM_NAME = "sftpConnectTimeout";
    private static final String SFTP_SESSION_KEEPALIVE_PARAM_NAME = "sftpSessionKeepAlive";

    private SchedulingService schedulingService;
    private String interval = null;
    private String partnerId = null;
    private String choreography = null;
    private String action = null;
    private String user = null;
    private String password = null;
    private String pattern = null;
    private int sftpSessionKeepAlive = 0;
    private int sftpConnectTimeout = 0;

    private BackendPipelineDispatcher backendPipelineDispatcher;

    public SftpBackendPollingService() {
    }


    @Override
    public void fillParameterMap(Map<String, ParameterDescriptor> parameterMap) {

        parameterMap.put(PARTNERID_PARAM_NAME, new ParameterDescriptor(ParameterType.STRING, "Partner", "The partner ID", ""));
        parameterMap.put(CHOREOGRAPHY_PARAM_NAME, new ParameterDescriptor(ParameterType.STRING, "Choreography", "The choreography name", ""));
        parameterMap.put(ACTION_PARAM_NAME, new ParameterDescriptor(ParameterType.STRING, "Action", "The action name", ""));
        parameterMap.put(URL_PARAM_NAME, new ParameterDescriptor(ParameterType.STRING, "URL", "Polling URL (use ftp://host.com:[port]/dir/subdir format)", ""));
        parameterMap.put(FILE_PATTERN_PARAM_NAME, new ParameterDescriptor(ParameterType.STRING, "File pattern",
                "Java Regex Pattern ( )", "*"));
        parameterMap.put(USER_PARAM_NAME, new ParameterDescriptor(ParameterType.STRING, "User name", "The FTP user name", "anonymous"));
        parameterMap.put(PASSWORD_PARAM_NAME, new ParameterDescriptor(ParameterType.PASSWORD, "Password", "The FTP password", ""));
        parameterMap.put(INTERVAL_PARAM_NAME, new ParameterDescriptor(ParameterType.STRING,
                "Interval (milliseconds, list of times, or cron pattern)",
                "Run every n milliseconds, or every day at e.g. \"6:30,14:45,15:25,...\", or in the interval specified by a cron pattern",
                "7200000"));
        parameterMap.put(RENAMING_PREFIX_PARAM_NAME, new ParameterDescriptor(ParameterType.STRING, "File Prefix",
                "Prefix is prepended to successfully transfered files, instead of file delete", "5"));
        parameterMap.put(CHANGE_FILE_PARAM_NAME, new ParameterDescriptor(ParameterType.BOOLEAN, "Change/Delete File", "Rename/Delete file active", Boolean.TRUE));
        parameterMap.put(SCHEDULING_SERVICE_PARAM_NAME, new ParameterDescriptor(ParameterType.SERVICE, "Scheduling Service",
                "The name of the service that shall be used for time schedule", SchedulingService.class));
        parameterMap.put(SFTP_SESSION_KEEPALIVE_PARAM_NAME, new ParameterDescriptor(
                ParameterType.STRING, "Session Keep Alive", "The sftp session keep alive in milliseconds (0 = not configured)", "0"));
        parameterMap.put(SFTP_CONNECT_TIMEOUT_PARAM_NAME, new ParameterDescriptor(
                ParameterType.STRING, "Connect Timeout", "The sftp connect timeout in milliseconds (0 = not configured)", "0"));

    }

    @Override
    public void initialize(EngineConfiguration config) throws InstantiationException {
        partnerId = (String) getParameter(PARTNERID_PARAM_NAME);
        choreography = (String) getParameter(CHOREOGRAPHY_PARAM_NAME);
        action = (String) getParameter(ACTION_PARAM_NAME);
        user = (String) getParameter(USER_PARAM_NAME);
        password = (String) getParameter(PASSWORD_PARAM_NAME);
        pattern = (String) getParameter(FILE_PATTERN_PARAM_NAME);

        String timeoutValue = (String) getParameter(SFTP_SESSION_KEEPALIVE_PARAM_NAME);
        try {
            if (StringUtils.isNotEmpty(timeoutValue)) {
                sftpSessionKeepAlive = Integer.parseInt(timeoutValue);
            }
        } catch (NumberFormatException e) {
            LOG.error(timeoutValue + " is not a valid number for property 'sftp session timeout in seconds'");
        }

        timeoutValue = (String) getParameter(SFTP_CONNECT_TIMEOUT_PARAM_NAME);
        try {
            if (StringUtils.isNotEmpty(timeoutValue)) {
                sftpConnectTimeout = Integer.parseInt(timeoutValue);
            }
        } catch (NumberFormatException e) {
            LOG.error(timeoutValue + " is not a valid number for property 'sftp connect timeout in seconds'");
        }

        backendPipelineDispatcher = (BackendPipelineDispatcher) Engine.getInstance().getCurrentConfiguration()
                .getStaticBeanContainer().getBackendPipelineDispatcher();

        initializeScheduler();
        super.initialize(config);
    }

    private void initializeScheduler() throws InstantiationException {
        interval = (String) getParameter(INTERVAL_PARAM_NAME);
        String schedulingServiceName = getParameter(SCHEDULING_SERVICE_PARAM_NAME);
        if (!StringUtils.isEmpty(schedulingServiceName)) {
            Service service = Engine.getInstance().getActiveConfigurationAccessService().getService(schedulingServiceName);
            if (service == null) {
                status = BeanStatus.ERROR;
                throw new InstantiationException("Service not found in configuration: " + schedulingServiceName);
            }
            if (!(service instanceof SchedulingService)) {
                status = BeanStatus.ERROR;
                throw new InstantiationException(schedulingServiceName + " is instance of " + service.getClass().getName() + " but SchedulingService is required");
            }
            schedulingService = (SchedulingService) service;
        } else {
            status = BeanStatus.ERROR;
            throw new InstantiationException("SchedulingService is not properly configured (schedulingServiceObj == null)!");
        }
    }

    @Override
    public void start() {

        if (getStatus() == BeanStatus.ACTIVATED) {
            LOG.debug("Starting");
            if (schedulingService != null) {
                ((SchedulingService) schedulingService).registerClient(this, interval);
            } else {
                LOG.error("No scheduling service configured");
            }
            super.start();
        } else {
            LOG.error("Service not in correct state to be started: " + status);
        }
    }

    @Override
    public void stop() {

        if ((getStatus() == BeanStatus.STARTED) || (getStatus() == BeanStatus.ERROR)) {
            if (schedulingService != null) {
                schedulingService.deregisterClient(this);
            }
            LOG.debug("FtpPollingReceiver service stopped");
            super.stop();
        }
    }

    @Override
    public Layer getActivationLayer() {

        return Layer.OUTBOUND_PIPELINES;
    }

    private boolean processFile(String fileName, byte[] data) throws NexusException {

        boolean processedSuccessfully = false;
        if (data != null) {
            try {
                // generate new message
                LOG.debug("Calling TransportReceiver...");
                backendPipelineDispatcher.processMessage(partnerId, choreography, action, null, null, null, data);
                processedSuccessfully = true;
            } catch (Exception ex) {
                throw new NexusException(String.format("An error occurred while processing file %s", fileName), ex);
            }
        } else {
            LOG.warn("No data to process!");
        }
        return processedSuccessfully;
    }

    @Override
    @SuppressWarnings("unchecked")
    public void scheduleNotify() {


        LOG.debug("Starting polling ...");

        // establish SFTP connection
        JSch jsch = null;
        ChannelSftp channelSftp = null;
        Session session = null;

        long start = System.currentTimeMillis();
        try {

            jsch = new JSch();

            // set private key
//                String pkey = getParameter(DSA_PRIVATE_KEY);
//                if (pkey != null && pkey.length() > 0) {
//                    jsch.addIdentity(pkey);
//                }

            URL url = new URL((String) getParameter(URL_PARAM_NAME));
            int port = url.getPort() != -1 ? url.getPort() : 22;
            session = jsch.getSession(user, url.getHost(), port);

            UserInfo userInfo = new UserInfo(password);
            session.setUserInfo(userInfo);

            if (sftpConnectTimeout > 0) {
                session.setTimeout(sftpConnectTimeout);
            }

            if (sftpSessionKeepAlive > 0) {
                session.setServerAliveInterval(sftpSessionKeepAlive);
            }

            // connect
            try {
                session.connect();
            } catch (JSchException jSchEx) {
                throw new NexusException(
                        "SFTP authentication failed: " + jSchEx.getMessage(), jSchEx);
            }
            LOG.debug("Connected to " + url.getHost() + ".");


            Channel channel = session.openChannel("sftp");
            channel.connect();
            channelSftp = (ChannelSftp) channel;

            // cd to remote working directory
            LOG.trace("Directory URL Path: " + url.getPath());
            String directory = url.getPath();
            if (directory.startsWith("/")) {
                directory = directory.substring(1);
            }

            if (StringUtils.isNotEmpty(directory)) {
                LOG.trace("Directory requested: " + directory);
                try {
                    channelSftp.cd(directory);
                } catch (SftpException sftpEx) {
                    throw new NexusException("SFTP server did not change directory: " + sftpEx.getMessage(), sftpEx);
                }
            }
            LOG.trace("Working Directory: " + channelSftp.pwd());

            // names of the files to download
            List<String> fileNames = null;
            boolean isFirstRun = true;
            do {
                if (fileNames != null) {
                    isFirstRun = false;
                }

                // FIND REMOTE FILES
                Pattern fileNamePattern = Pattern.compile(pattern);

                // get remote file list
                Vector<ChannelSftp.LsEntry> remoteFiles = channelSftp.ls(".");
                // search for next article catalog/inventory snapshot file
                LOG.trace("Number of files in remote directory: " + remoteFiles.size() + ", checking against patterns " /*+ fileNamePattern.toString()*/);
                // collect all matching file names
                fileNames = new ArrayList<String>();
                for (ChannelSftp.LsEntry remoteFile : remoteFiles) {
                    String remoteFileName = remoteFile.getFilename();
                    if (!remoteFile.getAttrs().isDir() && fileNamePattern.matcher(remoteFileName).matches()) {
                        fileNames.add(remoteFileName);
                        LOG.info("Found matching file name: " + remoteFileName);
                    }
                }


                // DOWNLOAD
                for (String fileName : fileNames) { // loop breaks after first iteration, if not in greedy mode
                    if (fileName != null) {
                        ByteArrayOutputStream baos = new ByteArrayOutputStream();
                        try {
                            channelSftp.get(fileName, baos);
                            baos.flush();
                            baos.close();
                            LOG.info("File " + fileName + " successfully downloaded.");
                            // PROCESS
                            if (!processFile(fileName, baos.toByteArray())) {
                                LOG.error("sending message of file " + fileName + " failed.");
                            } else {
                                LOG.info("File " + fileName + " successfully processed in backend.");
                                channelSftp.rm(fileName);
                            }
                        } catch (SftpException sftpEx) {
                            throw new NexusException(String.format("Could not retrieve file %s from SFTP server: %s", fileName, sftpEx.getMessage()), sftpEx);
                        }
                    }
                }
            } while (fileNames.size() > 0);
        } catch (Exception e) {
            LOG.error(String.format("(%d ms) Error polling SFTP account (%s): %s", (System.currentTimeMillis()-start), getParameter(URL_PARAM_NAME), e.getMessage()), e);
        } finally {
            if ((channelSftp != null) && channelSftp.isConnected()) {
                LOG.trace("Closing channel...");
                channelSftp.disconnect();
            } else {
                LOG.trace("Channel not connected.");
            }
            if ((session != null) && session.isConnected()) {
                LOG.trace("Closing session...");
                session.disconnect();
            } else {
                LOG.trace("Session not connected.");
            }
            LOG.debug(String.format("(%d ms)... polling done",(System.currentTimeMillis()-start)));
        }
    }

}

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
import org.nexuse2e.Engine;
import org.nexuse2e.NexusException;
import org.nexuse2e.configuration.EngineConfiguration;
import org.nexuse2e.configuration.ParameterDescriptor;
import org.nexuse2e.configuration.ParameterType;
import org.nexuse2e.logging.LogMessage;
import org.nexuse2e.messaging.AbstractPipelet;
import org.nexuse2e.messaging.MessageContext;
import org.nexuse2e.pojo.MessagePayloadPojo;
import org.nexuse2e.pojo.MessagePojo;
import org.nexuse2e.util.ServerPropertiesUtil;

import java.io.ByteArrayInputStream;
import java.net.URL;

public class SftpUploadPipelet extends AbstractPipelet {

    private static Logger LOG = Logger.getLogger(SftpUploadPipelet.class);

    private static final String URL_PARAM_NAME = "url";
    private static final String FILE_NAME_PATTERN_PARAM_NAME = "fileNamePattern";
    private static final String USER_PARAM_NAME = "username";
    private static final String PASSWORD_PARAM_NAME = "password";
    private static final String USE_CONTENT_ID_PARAM_NAME = "useContentId";
    private static final String SFTP_CONNECT_TIMEOUT_PARAM_NAME = "sftpConnectTimeout";
    private static final String SFTP_SESSION_KEEPALIVE_PARAM_NAME = "sftpSessionKeepAlive";


    private String url = null;
    private String user = null;
    private String password = null;
    private String pattern = null;
    private boolean useContentId = false;

    private int sftpSessionKeepAlive = 0;

    private int sftpConnectTimeout = 0;

    /**
     * Default constructor.
     */
    public SftpUploadPipelet() {

        parameterMap.put(URL_PARAM_NAME, new ParameterDescriptor(ParameterType.STRING, "URL",
                "Target URL (use ftp://host.com:[port]/dir/subdir format)", ""));
        parameterMap.put(FILE_NAME_PATTERN_PARAM_NAME, new ParameterDescriptor(ParameterType.STRING, "File Name",
                "Pattern supports placeholders like ${nexus.message.message}", "${nexus.message.message}"));
        parameterMap.put(USER_PARAM_NAME, new ParameterDescriptor(ParameterType.STRING, "User name", "The FTP user name", "anonymous"));
        parameterMap.put(PASSWORD_PARAM_NAME, new ParameterDescriptor(ParameterType.PASSWORD, "Password", "The FTP password", ""));
        parameterMap.put(USE_CONTENT_ID_PARAM_NAME, new ParameterDescriptor(ParameterType.BOOLEAN, "Use Content ID",
                "Flag whether to use the content ID as the file name", Boolean.FALSE));
        parameterMap.put(SFTP_SESSION_KEEPALIVE_PARAM_NAME, new ParameterDescriptor(
                ParameterType.STRING, "Session Keep Alive", "The sftp session keep alive in milliseconds (0 = not configured)", "0"));
        parameterMap.put(SFTP_CONNECT_TIMEOUT_PARAM_NAME, new ParameterDescriptor(
                ParameterType.STRING, "Connect Timeout", "The sftp connect timeout in milliseconds (0 = not configured)", "0"));

    }

    /* (non-Javadoc)
     * @see org.nexuse2e.messaging.AbstractPipelet#initialize(org.nexuse2e.configuration.EngineConfiguration)
     */
    @Override
    public void initialize(EngineConfiguration config) throws InstantiationException {

        url = (String) getParameter(URL_PARAM_NAME);
        user = (String) getParameter(USER_PARAM_NAME);
        password = (String) getParameter(PASSWORD_PARAM_NAME);
        pattern = (String) getParameter(FILE_NAME_PATTERN_PARAM_NAME);

        String timeoutValue = (String) getParameter(SFTP_SESSION_KEEPALIVE_PARAM_NAME);
        try {
            if (timeoutValue != null) {
                sftpSessionKeepAlive = Integer.parseInt(timeoutValue);
            }
        } catch (NumberFormatException e) {
            LOG.error(timeoutValue + " is not a valid number for property 'sftp session timeout in seconds'");
        }

        timeoutValue = (String) getParameter(SFTP_CONNECT_TIMEOUT_PARAM_NAME);
        try {
            if (timeoutValue != null) {
                sftpConnectTimeout = Integer.parseInt(timeoutValue);
            }
        } catch (NumberFormatException e) {
            LOG.error(timeoutValue + " is not a valid number for property 'sftp connect timeout in seconds'");
        }

        Boolean tempFlag = getParameter(USE_CONTENT_ID_PARAM_NAME);
        if (tempFlag != null && tempFlag.equals(Boolean.TRUE)) {
            useContentId = true;
            LOG.info("Using content ID in file name.");
        }

        super.initialize(config);
    }

    /* (non-Javadoc)
     * @see org.nexuse2e.messaging.Pipelet#processMessage(org.nexuse2e.messaging.MessageContext)
     */
    public MessageContext processMessage(MessageContext messageContext) throws NexusException {


        String tempUrl = ServerPropertiesUtil.replaceServerProperties(url, messageContext);
        String tempPattern = ServerPropertiesUtil.replaceServerProperties(pattern, messageContext);
        writePayload(tempUrl, tempPattern, messageContext.getMessagePojo());

        return messageContext;
    }


    /**
     * @param targetUrl   the server URL
     * @param fileName filename used for the data, extension is added within the method
     * @param message     the full message
     * @return the target url and path.
     */
    private void writePayload(String targetUrl, String fileName, MessagePojo message) throws NexusException {


        LOG.info("Starting upload ...");

        boolean listenerInformed = false;
        String tempFileName = null;

        // establish SFTP connection
        JSch jsch = null;
        ChannelSftp channelSftp = null;
        Session session = null;

        try {


            jsch = new JSch();

            // set private key
//                if ( StringUtils.isNotEmpty( (String) getParameter( DSA_PRIVATE_KEY ) ) ) {
//                    jsch.addIdentity( (String) getParameter( DSA_PRIVATE_KEY ) );
//                }

            URL url = new URL(targetUrl);

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
                throw new NexusException(new LogMessage(String.format("SFTP authentication failed: %s", jSchEx.getMessage()), message), jSchEx);
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
                    throw new NexusException(
                            new LogMessage(String.format("SFTP server did not change directory: %s", sftpEx.getMessage()), message), sftpEx);
                }
            }
            LOG.trace("Working Directory: "
                    + channelSftp.pwd());

            // UPLOAD
            for (MessagePayloadPojo messagePayloadPojo : message.getMessagePayloads()) {

                if (useContentId) {
                    fileName = messagePayloadPojo.getContentId();
                } else {

                    String extension = Engine.getInstance().getFileExtensionFromMime(messagePayloadPojo.getMimeType().toLowerCase());
                    if (StringUtils.isEmpty(extension)) {
                        extension = "dat";
                    }
                    fileName = fileName + "." + extension;
                }
                // there should be only one payload
                ByteArrayInputStream bais = new ByteArrayInputStream(messagePayloadPojo.getPayloadData());
                try {
                    channelSftp.put(bais, fileName);
                    LOG.trace("Uploaded file: " + fileName);
                } catch (SftpException sftpEx) {
                    throw new NexusException(
                            new LogMessage(String.format("Could not upload file %s to SFTP server %s: %s", fileName, getParameter(URL_PARAM_NAME), sftpEx.getMessage()), message), sftpEx);
                }
            }
        } catch (Exception e) {
            throw new NexusException(new LogMessage(String.format("Unexpected error on SFTP upload: %s", e.getMessage()), message), e);


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
        }

        LOG.info("... upload done");
    }
}
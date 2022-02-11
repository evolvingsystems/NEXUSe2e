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
package org.nexuse2e.logging;

import org.apache.logging.log4j.core.Filter;
import org.apache.logging.log4j.core.Layout;
import org.apache.logging.log4j.core.LogEvent;
import org.apache.logging.log4j.core.appender.AbstractAppender;
import org.apache.logging.log4j.core.config.Property;
import org.apache.logging.log4j.core.config.plugins.Plugin;
import org.apache.logging.log4j.core.config.plugins.PluginAttribute;
import org.apache.logging.log4j.core.config.plugins.PluginElement;
import org.apache.logging.log4j.core.config.plugins.PluginFactory;
import org.apache.logging.log4j.core.impl.Log4jLogEvent;
import org.apache.logging.log4j.core.layout.PatternLayout;
import org.nexuse2e.Engine;
import org.nexuse2e.dao.LogDAO;
import org.nexuse2e.pojo.LogPojo;

import java.io.Serializable;
import java.util.Date;

@Plugin(name="DatabaseLogger", category="Core", elementType="appender")
public class DatabaseLogger extends AbstractAppender {

    protected DatabaseLogger(String name, Filter filter, Layout<? extends Serializable> layout, boolean ignoreExceptions, Property[] properties) {
        super(name, filter, layout, ignoreExceptions, properties);
    }

    @PluginFactory
    public static DatabaseLogger createAppender(
            @PluginAttribute("name") String name,
            @PluginElement("Layout") Layout<? extends Serializable> layout,
            @PluginElement("Filter") Filter filter) {

        if (name == null) {
            LOGGER.error("No name provided for DatabaseLogger");
            return null;
        }

        if (layout == null) layout = PatternLayout.createDefaultLayout();

        return new DatabaseLogger(name, filter, layout, true, null);
    }

    @Override
    public void append( LogEvent logEvent ) {

        LogDAO logDao;
        try {
            logDao = (LogDAO)Engine.getInstance().getBeanFactory().getBean( "logDao" );
        } catch ( Exception e ) {
            if (e instanceof IllegalStateException
                    && e.getMessage().startsWith("ApplicationObjectSupport instance [org.nexuse2e.Engine@")
                    && e.getMessage().endsWith("] does not run in an ApplicationContext")) {
                System.out.printf("Exception while logging LogEvent '%s' to the database is being suppressed " +
                        "as it likely caused by the ApplicationContext not being available during application start.",
                        logEvent.getMessage().toString());
                return;
            }
            e.printStackTrace();
            return;
        }

        String description = "";
        if ( logEvent.getMessage() instanceof LogMessage ) {
            description = ( (LogMessage) logEvent.getMessage() ).toString(false);
        } else {
            description = logEvent.getMessage().toString();
        }

        if ( ( description != null ) && ( description.length() > 4000 ) ) {
            description = description.substring( 0, 3999 );
        }

        try {
            LogPojo pojo = new LogPojo();

            String className = logEvent.getLoggerName();
            String methodName = "unknown";
            if (logEvent.isIncludeLocation()) {
                Log4jLogEvent.serialize(logEvent, true);
                if (logEvent.getSource() != null) {
                    methodName = logEvent.getSource().getMethodName();
                }
            }

            int endIndex = className.indexOf( "." );
            String normalizedClassName;

            if ( endIndex > 0 ) {
                normalizedClassName = className;//.substring( begineIndex, endIndex );
            } else {
                normalizedClassName = className;
            }

            //TODO get machine id ?
            pojo.setLogId( Engine.getInstance().getEngineController().getEngineControllerStub().getMachineId() );

            pojo.setCreatedDate( new Date() );
            pojo.setClassName( normalizedClassName );
            pojo.setMethodName( methodName );
            pojo.setEventId( 0 );
            pojo.setSeverity( logEvent.getLevel().intLevel() );
            pojo.setDescription( description );
            pojo.setConversationId( "unknown" );
            pojo.setMessageId( "unknown" );
            if ( logEvent.getMessage() instanceof LogMessage ) {
                LogMessage logMessage = (LogMessage) logEvent.getMessage();
                if ( logMessage.getConversationId() != null ) {
                    pojo.setConversationId( logMessage.getConversationId() );
                }
                if ( logMessage.getMessageId() != null ) {
                    pojo.setMessageId( logMessage.getMessageId() );
                }
            }

            // avoid concurrent access to session
            synchronized (this) {

                logDao.saveLog( pojo );
            }
        } catch ( Exception ex ) {
            System.out.println("In case of truncation, please double check the database settings for the table nx_log. The description should be varchar(4000)");
        	ex.printStackTrace();
        }
    }

}

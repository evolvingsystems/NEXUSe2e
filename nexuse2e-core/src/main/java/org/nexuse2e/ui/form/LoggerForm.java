/**
 *  NEXUSe2e Business Messaging Open Source
 *  Copyright 2000-2009, Tamgroup and X-ioma GmbH
 *
 *  This is free software; you can redistribute it and/or modify it
 *  under the terms of the GNU Lesser General Public License as
 *  published by the Free Software Foundation version 2.1 of
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
package org.nexuse2e.ui.form;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import org.nexuse2e.configuration.ParameterDescriptor;
import org.nexuse2e.configuration.ParameterType;
import org.nexuse2e.logging.LogAppender;
import org.nexuse2e.pojo.ComponentPojo;
import org.nexuse2e.pojo.LoggerParamPojo;
import org.nexuse2e.pojo.LoggerPojo;

/**
 * @author gesch
 *
 */
public class LoggerForm implements Serializable {

    private static final long              serialVersionUID         = 1L;

    private int                            nxLoggerId               = 0;
    private int                            nxComponentId            = 0;
    private int                            nxChoreographyId         = 0;
    private int                            paramsNxComponentId      = 0;
    private String                         name                     = null;
    private String                         choreographyId           = null;
    private String                         componentId              = null;
    private int                            threshold                = 0;
    private boolean                        autostart;
    private boolean                        running;

    private Map<String, String>            logFilterValues          = null;
    private Map<String, String>            loggerParamValues        = new HashMap<String, String>();
    private List<LoggerParamPojo>          parameters               = new ArrayList<LoggerParamPojo>();
    private List<String>                   groupNames               = new ArrayList<String>();
    private List<ComponentPojo>            availableTemplates       = new ArrayList<ComponentPojo>();

    private String                         filterJavaPackagePattern = "";
    private String                         componentName;

    private LoggerPojo                     logger                   = null;

    private LogAppender                    loggerInstance           = null;


    public void reset() {

        componentId = null;
        nxLoggerId = 0;
        if (logFilterValues != null) {
            logFilterValues.clear();
        }
        filterJavaPackagePattern = "";
    } // reset

    /**
     * Set the properties of this form based on a POJO
     * @param notifier The POJO used to fill in the fields
     */
    public void setProperties(LoggerPojo logger) {

        nxLoggerId = logger.getNxLoggerId();
        name = logger.getName();
        running = logger.isRunning();
        autostart = logger.isAutostart();
        
        filterJavaPackagePattern = logger.getFilter();
    }

    /**
     * Set the properties of this form based on a POJO
     * @return The updated POJO
     */
    public LoggerPojo getProperties(LoggerPojo logger) {

        logger.setName(name);
        logger.setRunning(running);
        logger.setAutostart(autostart);

        return logger;
    }

    public void createParameterMapFromPojos() {
        loggerParamValues = new HashMap<String, String>();
        for (LoggerParamPojo param : getParameters()) {
            loggerParamValues.put(param.getParamName(), param.getValue());
        }

    }

    public void fillPojosFromParameterMap() {
        if (loggerParamValues == null) {
            return;
        }
        for (LoggerParamPojo param : getParameters()) {
            ParameterDescriptor pd = loggerInstance.getParameterMap().get(param.getParamName());
            if (pd != null) {
                String value = loggerParamValues.get(param.getParamName());
                if (pd.getParameterType() == ParameterType.BOOLEAN) {
                    if ("on".equalsIgnoreCase(value)) {
                        value = Boolean.TRUE.toString();
                    }
                }
                if (value == null) {
                    value = Boolean.FALSE.toString();
                }
                param.setValue(value);
            }
        }
    }

    public boolean isAutostart() {

        return autostart;
    }

    public String getAutoStartString() {

        return autostart ? "Yes" : "No";
    }

    public void setAutostart(boolean autostart) {

        this.autostart = autostart;
    }

    public String getRunningString() {

        return running ? "Running" : "Stopped";
    }

    public String getComponentId() {

        return componentId;
    }

    public void setComponentId(String componentName) {

        this.componentId = componentName;
    }

    public String getFilterJavaPackagePattern() {

        return filterJavaPackagePattern;
    }

    public void setFilterJavaPackagePattern(String filterJavaPackagePattern) {

        this.filterJavaPackagePattern = filterJavaPackagePattern;
    }

    public String getName() {

        return name;
    }

    public void setName(String name) {

        this.name = name;
    }

    public boolean isRunning() {

        return running;
    }

    public void setRunning(boolean running) {

        this.running = running;
    }

    public String getChoreographyId() {

        return choreographyId;
    }

    public void setChoreographyId(String choreographyId) {

        this.choreographyId = choreographyId;
    }

    public int getNxLoggerId() {

        return nxLoggerId;
    }

    public void setNxLoggerId(int nxLoggerId) {

        this.nxLoggerId = nxLoggerId;
    }

    public String getFilterString() {

        return filterJavaPackagePattern;
    }

    public int getNxComponentId() {

        return nxComponentId;
    }

    public void setNxComponentId(int nxComponentId) {

        this.nxComponentId = nxComponentId;
    }

    public List<LoggerParamPojo> getParameters() {

        return parameters;
    }

    public void setParameters(List<LoggerParamPojo> parameters) {

        this.parameters = parameters;
    }

    public Map<String, String> getLogFilterValues() {

        if (logFilterValues == null) {
            logFilterValues = new HashMap<String, String>();
        }
        return logFilterValues;
    }

    public void setLogFilterValues(HashMap<String, String> logFilterValues) {

        this.logFilterValues = logFilterValues;
    }

    public Object getLogFilterValue(String key) {

        if (logFilterValues == null) {
            logFilterValues = new HashMap<String, String>();
        }
        return logFilterValues.get(key);
    }

    public void setLogFilterValue(String key, Object value) {

        logFilterValues.put(key, (String) value);
    }

    public List<String> getGroupNames() {

        if (groupNames == null) {
            groupNames = new Vector<String>();
        }
        return groupNames;
    }

    public void setGroupNames(List<String> groupNames) {

        this.groupNames = groupNames;
    }

    public int getThreshold() {

        return threshold;
    }

    public void setThreshold(int threshold) {

        this.threshold = threshold;
    }

    public LoggerPojo getLogger() {

        return logger;
    }

    public void setLogger(LoggerPojo logger) {

        this.logger = logger;
    }

    public int getParamsNxComponentId() {

        return paramsNxComponentId;
    }

    public void setParamsNxComponentId(int paramsNxComponentId) {

        this.paramsNxComponentId = paramsNxComponentId;
    }

    public int getNxChoreographyId() {

        return nxChoreographyId;
    }

    public void setNxChoreographyId(int nxChoreographyId) {

        this.nxChoreographyId = nxChoreographyId;
    }

    public String getComponentName() {

        return componentName;
    }

    public void setComponentName(String componentName) {

        this.componentName = componentName;
    }

    public LogAppender getLoggerInstance() {

        return loggerInstance;
    }

    public void setLoggerInstance(LogAppender loggerInstance) {

        this.loggerInstance = loggerInstance;
    }

    public List<ComponentPojo> getAvailableTemplates() {
    
        return availableTemplates;
    }

    public void setAvailableTemplates(List<ComponentPojo> availableTemplates) {
    
        this.availableTemplates = availableTemplates;
    }

} // NotifierForm

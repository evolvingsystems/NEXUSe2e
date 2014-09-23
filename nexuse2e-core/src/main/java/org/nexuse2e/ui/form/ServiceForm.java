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

import javax.validation.constraints.Size;

import org.nexuse2e.configuration.ParameterDescriptor;
import org.nexuse2e.configuration.ParameterType;
import org.nexuse2e.pojo.ServiceParamPojo;
import org.nexuse2e.pojo.ServicePojo;
import org.nexuse2e.service.Service;

/**
 * The form used to retrieve and store information for a <code>Service</code>
 *
 * @author jonas.reese
 */
public class ServiceForm implements Serializable {

    private static final long       serialVersionUID    = 1L;

    private int                     nxServiceId         = 0;
    private int                     nxComponentId       = 0;
    private int                     paramsNxComponentId = 0;
    private String                  name                = null;
    private String                  componentName       = null;
    private int                     position            = 0;

    private boolean                 autostart           = false;
    
    private Map<String, String>     logFilterValues     = null;
    private Map<String, String>     pipeletParamValues  = new HashMap<String, String>();
    private List<ServiceParamPojo>  parameters          = new ArrayList<ServiceParamPojo>();

    private Service                 serviceInstance;

    public void reset() {

        componentName = null;
        nxServiceId = 0;
        if (logFilterValues != null) {
            logFilterValues.clear();
        }
        if (pipeletParamValues != null) {
            pipeletParamValues.clear();
        }
        autostart = false;
        
    }

    /**
     * Set the properties of this form based on a POJO
     * @param notifier The POJO used to fill in the fields
     */
    public void setProperties(ServicePojo service) {

        nxServiceId = service.getNxServiceId();
        nxComponentId = service.getComponent() == null ? 0 : service.getComponent().getNxComponentId();
        name = service.getName();
        if (service.getComponent() != null) {
            nxComponentId = service.getComponent().getNxComponentId();
            componentName = service.getComponent().getName();
        }
        autostart = service.isAutostart();
    }

    /**
     * Set the properties of this form based on a POJO
     * @return The updated POJO
     */
    public ServicePojo getProperties(ServicePojo service) {

        service.setName(name);
        return service;
    }

    public void createParameterMapFromPojos() {

        pipeletParamValues = new HashMap<String, String>();
        for (ServiceParamPojo param : getParameters()) {
            pipeletParamValues.put(param.getParamName(), param.getValue());
        }

    }

    public void fillPojosFromParameterMap() {

        if (pipeletParamValues == null) {
            return;
        }
        for (ServiceParamPojo param : getParameters()) {
            ParameterDescriptor pd = serviceInstance.getParameterMap().get(param.getParamName());
            if (pd != null) {
                String value = pipeletParamValues.get(param.getParamName());
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

    public Map<String, String> getPipeletParamValues() {

        return pipeletParamValues;
    }

    public void setPipeletParamValues(HashMap<String, String> pipeletParamValues) {

        this.pipeletParamValues = pipeletParamValues;
    }

    public Object getParamValue(String key) {

        return pipeletParamValues.get(key);
    }

    public void setParamValue(String key, Object value) {

        pipeletParamValues.put(key, (String) value);
    }

    public String getComponentName() {

        return componentName;
    }

    public void setComponentName(String componentName) {

        this.componentName = componentName;
    }

    @Size(min = 1, message = "{service.error.name.required}")
    public String getName() {

        return name;
    }

    public void setName(String name) {

        this.name = name;
    }

    public int getNxServiceId() {

        return nxServiceId;
    }

    public void setNxServiceId(int nxServiceId) {

        this.nxServiceId = nxServiceId;
    }

    public int getNxComponentId() {

        return nxComponentId;
    }

    public void setNxComponentId(int nxComponentId) {

        this.nxComponentId = nxComponentId;
    }

    public List<ServiceParamPojo> getParameters() {

        return parameters;
    }

    public void setParameters(List<ServiceParamPojo> parameters) {

        this.parameters = parameters;
    }

    public Map<String, String> getLogFilterValues() {

        if (logFilterValues == null) {
            logFilterValues = new HashMap<String, String>();
        }
        return logFilterValues;
    }

    public void setLogFilterValues(Map<String, String> logFilterValues) {

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

    public int getPosition() {

        return position;
    }

    public void setPosition(int position) {

        this.position = position;
    }

    public int getParamsNxComponentId() {

        return paramsNxComponentId;
    }

    public void setParamsNxComponentId(int paramsNxComponentId) {

        this.paramsNxComponentId = paramsNxComponentId;
    }

    public Service getServiceInstance() {

        return serviceInstance;
    }

    public void setServiceInstance(Service serviceInstance) {

        this.serviceInstance = serviceInstance;
    }

    
    public boolean isAutostart() {
    
        return autostart;
    }

    public void setAutostart(boolean autostart) {
    
        this.autostart = autostart;
    }

}
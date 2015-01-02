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
package org.nexuse2e.ui.controller.reporting;

import java.util.Map;

import org.apache.log4j.Logger;
import org.nexuse2e.configuration.EngineConfiguration;
import org.nexuse2e.ui.form.ReportingPropertiesForm;
import org.nexuse2e.ui.form.ReportingSettingsForm;

/**
 * Abstract superclass providing common functionality for reporting controllers. Copied from oder ReportingAction class.
 *
 * @author Jonas Reese
 */
public abstract class AbstractReportingController {
    
    private static Logger LOG = Logger.getLogger(AbstractReportingController.class);
    
    protected void fillForm(EngineConfiguration engineConfiguration, ReportingSettingsForm form, ReportingPropertiesForm props) {
        Map<String, Object> values = engineConfiguration.getGenericParameters("log_display_configuration", null, form.getParameterMap());
        fillForms(values, form, props);
    }


    private void fillForms(Map<String, Object> values, ReportingSettingsForm form, ReportingPropertiesForm props) {

        form.setEngineColSeverity(getBooleanValue(values, ReportingSettingsForm.PARAM_NAME_ENGCOL_SEVERITY, true));
        form.setEngineColClassName(getBooleanValue(values, ReportingSettingsForm.PARAM_NAME_ENGCOL_CLASSNAME, true));
        form.setEngineColDescription(getBooleanValue(values, ReportingSettingsForm.PARAM_NAME_ENGCOL_DESCRIPTION, true));
        form.setEngineColIssued(getBooleanValue(values, ReportingSettingsForm.PARAM_NAME_ENGCOL_ISSUEDDATE, true));
        form.setEngineColmethodName(getBooleanValue(values, ReportingSettingsForm.PARAM_NAME_ENGCOL_METHODNAME, true));
        form.setEngineColOrigin(getBooleanValue(values, ReportingSettingsForm.PARAM_NAME_ENGCOL_ORIGIN, true));

        form.setConvColAction(getBooleanValue(values, ReportingSettingsForm.PARAM_NAME_CONVCOL_ACTION, true));
        form.setConvColChorId(getBooleanValue(values, ReportingSettingsForm.PARAM_NAME_CONVCOL_CHOREOGRAPHYID, true));
        form.setConvColConId(getBooleanValue(values, ReportingSettingsForm.PARAM_NAME_CONVCOL_CONVID, true));
        form.setConvColCreated(getBooleanValue(values, ReportingSettingsForm.PARAM_NAME_CONVCOL_CREATED, true));
        form.setConvColPartId(getBooleanValue(values, ReportingSettingsForm.PARAM_NAME_CONVCOL_PARTICIPANTID, true));
        form.setConvColSelect(getBooleanValue(values, ReportingSettingsForm.PARAM_NAME_CONVCOL_SELECT, true));
        form.setConvColStatus(getBooleanValue(values, ReportingSettingsForm.PARAM_NAME_CONVCOL_STATUS, true));
        form.setConvColTurnaround(getBooleanValue(values, ReportingSettingsForm.PARAM_NAME_CONVCOL_TURNAROUND, true));

        form.setMessColAction(getBooleanValue(values, ReportingSettingsForm.PARAM_NAME_MSGCOL_ACTION, true));
        form.setMessColCreated(getBooleanValue(values, ReportingSettingsForm.PARAM_NAME_MSGCOL_CREATED, true));
        form.setMessColMessageId(getBooleanValue(values, ReportingSettingsForm.PARAM_NAME_MSGCOL_MSGID, true));
        form.setMessColParticipantId(getBooleanValue(values, ReportingSettingsForm.PARAM_NAME_MSGCOL_PARTICIPANTID, true));
        form.setMessColSelect(getBooleanValue(values, ReportingSettingsForm.PARAM_NAME_MSGCOL_SELECT, true));
        form.setMessColStatus(getBooleanValue(values, ReportingSettingsForm.PARAM_NAME_MSGCOL_STATUS, true));
        form.setMessColBackendStatus(getBooleanValue(values, ReportingSettingsForm.PARAM_NAME_MSGCOL_BACKEND_STATUS, true));
        form.setMessColTurnaround(getBooleanValue(values, ReportingSettingsForm.PARAM_NAME_MSGCOL_TURNAROUND, true));
        form.setMessColType(getBooleanValue(values, ReportingSettingsForm.PARAM_NAME_MSGCOL_TYPE, true));

        form.setTimezone(getStringValue(values, ReportingSettingsForm.PARAM_NAME_TIMEZONE, ""));
        form.setPageSize(getIntValue(values, ReportingSettingsForm.PARAM_NAME_ROWCOUNT, 20));

        if (props != null) {
            props.setPageSize(form.getPageSize());
            props.setTimezone(form.getTimezone());
        }

    }

    private int getIntValue(Map<String, Object> values, String label, int defaultValue) {

        int returnvalue = defaultValue;
        if (values != null && label != null) {
            Object value = values.get(label);
            if (value instanceof String) {
                try {
                    returnvalue = Integer.parseInt((String) value);
                } catch (NumberFormatException e) {
                    LOG.error("Generic parameter " + label + " is not a parsable integer value: " + value);
                }
            } else {
                if (value == null) {
                    LOG.error("Generic parameter " + label + ", no value found, using default value: " + defaultValue);
                } else {
                    LOG.error("Generic parameter " + label + " is instance of " + value.getClass().getName()
                            + " but Int was expected!");
                }
            }

        } else {
            if (values == null) {
                LOG.debug("Generic Parameters must not be null!");
            }
            if (label == null) {
                LOG.debug("Generic Parameter Label must not be null");
            }
        }

        return returnvalue;
    }

    private String getStringValue(Map<String, Object> values, String label, String defaultValue) {

        String returnvalue = defaultValue;
        if (values != null && label != null) {
            Object value = values.get(label);
            if (value instanceof String) {
                returnvalue = (String) value;
            } else {
                if (value == null) {
                    LOG.warn("Generic parameter " + label + ", no value found, using default value: " + defaultValue);
                } else {
                    LOG.error("Generic parameter " + label + " is instance of " + value.getClass().getName()
                            + " but String was expected!");
                }
            }

        } else {
            if (values == null) {
                LOG.debug("Generic Parameters must not be null!");
            }
            if (label == null) {
                LOG.debug("Generic Parameter Label must not be null");
            }
        }

        return returnvalue;
    }

    private boolean getBooleanValue(Map<String, Object> values, String label, boolean defaultValue) {

        boolean returnvalue = defaultValue;
        if (values != null && label != null) {
            Object value = values.get(label);
            if (value instanceof Boolean) {
                returnvalue = ((Boolean) value).booleanValue();
            } else {
                if (value == null) {
                    LOG.error("Generic parameter " + label + ", no value found, using default value: " + defaultValue);
                } else {
                    LOG.error("Generic parameter " + label + " is instance of " + value.getClass().getName()
                            + " but Boolean was expected!");
                }
            }

        } else {
            if (values == null) {
                LOG.debug("Generic Parameters must not be null!");
            }
            if (label == null) {
                LOG.debug("Generic Parameter Label must not be null");
            }
        }

        return returnvalue;
    }
}

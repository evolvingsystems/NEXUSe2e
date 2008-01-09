/**
 * NEXUSe2e Business Messaging Open Source  
 * Copyright 2007, Tamgroup and X-ioma GmbH   
 *  
 * This is free software; you can redistribute it and/or modify it  
 * under the terms of the GNU Lesser General Public License as  
 * published by the Free Software Foundation version 2.1 of  
 * the License.  
 *  
 * This software is distributed in the hope that it will be useful,  
 * but WITHOUT ANY WARRANTY; without even the implied warranty of  
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU  
 * Lesser General Public License for more details.  
 *  
 * You should have received a copy of the GNU Lesser General Public  
 * License along with this software; if not, write to the Free  
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA  
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
 */
package org.nexuse2e.pojo;

// Generated 29.11.2006 15:25:02 by Hibernate Tools 3.2.0.beta6a

import java.util.Date;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlType;

import org.nexuse2e.configuration.ParameterDescriptor;

/**
 * LoggerParamPojo generated by hbm2java
 */
@XmlType(name = "LoggerParamType")
@XmlAccessorType(XmlAccessType.NONE)
public class LoggerParamPojo implements java.io.Serializable {

    private static final long   serialVersionUID = 1L;

    // Fields    

    private int                 nxLoggerParamId;
    private LoggerPojo          logger;
    private Date                createdDate;
    private Date                modifiedDate;
    private int                 modifiedNxUserId;
    private String              paramName;
    private String              label;
    private String              value;
    private int                 sequenceNumber;

    // non-persistent fields
    private ParameterDescriptor parameterDescriptor;

    // Constructors

    /** default constructor */
    public LoggerParamPojo() {
        createdDate = new Date();
        modifiedDate = createdDate;
    }

    /** minimal constructor */
    public LoggerParamPojo( LoggerPojo logger, Date createdDate, Date modifiedDate, int modifiedNxUserId,
            String paramName, String value ) {

        this.logger = logger;
        this.createdDate = createdDate;
        this.modifiedDate = modifiedDate;
        this.modifiedNxUserId = modifiedNxUserId;
        this.paramName = paramName;
        this.value = value;
    }

    /** full constructor */
    public LoggerParamPojo( LoggerPojo logger, Date createdDate, Date modifiedDate, int modifiedNxUserId,
            String paramName, String label, String value, int sequenceNumber ) {

        this.logger = logger;
        this.createdDate = createdDate;
        this.modifiedDate = modifiedDate;
        this.modifiedNxUserId = modifiedNxUserId;
        this.paramName = paramName;
        this.label = label;
        this.value = value;
        this.sequenceNumber = sequenceNumber;
    }

    // Property accessors
    public int getNxLoggerParamId() {

        return this.nxLoggerParamId;
    }

    public void setNxLoggerParamId( int nxLoggerParamId ) {

        this.nxLoggerParamId = nxLoggerParamId;
    }

    public LoggerPojo getLogger() {

        return this.logger;
    }

    public void setLogger( LoggerPojo logger ) {

        this.logger = logger;
    }

    public Date getCreatedDate() {

        return this.createdDate;
    }

    public void setCreatedDate( Date createdDate ) {

        this.createdDate = createdDate;
    }

    public Date getModifiedDate() {

        return this.modifiedDate;
    }

    public void setModifiedDate( Date modifiedDate ) {

        this.modifiedDate = modifiedDate;
    }

    public int getModifiedNxUserId() {

        return this.modifiedNxUserId;
    }

    public void setModifiedNxUserId( int modifiedNxUserId ) {

        this.modifiedNxUserId = modifiedNxUserId;
    }

    @XmlAttribute
    public String getParamName() {

        return this.paramName;
    }

    public void setParamName( String paramName ) {

        this.paramName = paramName;
    }

    @XmlAttribute
    public String getLabel() {

        return this.label;
    }

    public void setLabel( String label ) {

        this.label = label;
    }

    @XmlAttribute
    public String getValue() {

        return this.value;
    }

    public void setValue( String value ) {

        this.value = value;
    }

    @XmlAttribute
    public int getSequenceNumber() {

        return this.sequenceNumber;
    }

    public void setSequenceNumber( int sequenceNumber ) {

        this.sequenceNumber = sequenceNumber;
    }

    /**
     * @return the parameterDescriptor
     */
    public ParameterDescriptor getParameterDescriptor() {

        return parameterDescriptor;
    }

    /**
     * @param parameterDescriptor the parameterDescriptor to set
     */
    public void setParameterDescriptor( ParameterDescriptor parameterDescriptor ) {

        this.parameterDescriptor = parameterDescriptor;
    }
}

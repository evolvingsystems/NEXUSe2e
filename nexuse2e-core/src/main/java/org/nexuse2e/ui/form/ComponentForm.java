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

import org.nexuse2e.configuration.ComponentType;
import org.nexuse2e.pojo.ComponentPojo;

/**
 * @author gesch
 *
 */
public class ComponentForm implements Serializable {

    private static final long serialVersionUID = -217755396015512335L;
    private int               nxComponentId    = 0;
    private int               type             = 0;
    private String            name             = null;
    private String            className        = null;
    private String            description      = null;

    
    public void setProperties( ComponentPojo component ) {

        setNxComponentId( component.getNxComponentId() );
        setType( component.getType() );
        setName( component.getName() );
        setClassName( component.getClassName() );
        setDescription( component.getDescription() );
    }

    public ComponentPojo getProperties( ComponentPojo component ) {

        component.setNxComponentId( getNxComponentId() );
        component.setName( getName() );
        component.setClassName( getClassName() );
        component.setDescription( getDescription() );
        return component;
    }

    public void cleanSettings() {

        setNxComponentId( 0 );
        setType( 0 );
        setName( null );
        setClassName( null );
        setDescription( null );

    }

    public String getClassName() {

        return className;
    }

    public void setClassName( String className ) {

        this.className = className;
    }

    public String getDescription() {

        return description;
    }

    public void setDescription( String description ) {

        this.description = description;
    }

    public String getName() {

        return name;
    }

    public void setName( String name ) {

        this.name = name;
    }

    public int getNxComponentId() {

        return nxComponentId;
    }

    public void setNxComponentId( int nxComponentId ) {

        this.nxComponentId = nxComponentId;
    }

    public String getTypeString() {

        if ( type == ComponentType.LOGGER.getValue() ) {
            return "Logger";
        } else if ( type == ComponentType.PIPELET.getValue() ) {
            return "Pipelet";
        } else if ( type == ComponentType.SERVICE.getValue() ) {
            return "Service";
        }
        return "unknown Component";
    }

    public int getType() {

        return type;
    }

    public void setType( int type ) {

        this.type = type;
    }
}

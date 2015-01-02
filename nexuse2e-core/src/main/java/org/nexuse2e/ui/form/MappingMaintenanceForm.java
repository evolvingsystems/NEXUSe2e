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
import java.util.List;


/**
 * @author gesch
 *
 */
public class MappingMaintenanceForm implements Serializable {

    private static final long serialVersionUID = -4659566397866558364L;

    private String submitaction;
    private String nxMappingId;
    private String leftValue;
    private String rightValue;
    private String category;
    private int leftType;
    private int rightType;
    private List<String> typenames;
    private int pageCount;
    private int currentPage;

    private String newCategory;
    private String newLeftType;
    private String newRightType;
    private String newLeftValue;
    private String newRightValue;
    
    public String getCategory() {
        return category;
    }
    
    public void setCategory( String category ) {
        this.category = category;
    }
    
    public int getLeftType() {
        return leftType;
    }
    
    public void setLeftType( int leftType ) {
        this.leftType = leftType;
    }
    
    public String getLeftValue() {
        return leftValue;
    }
    
    public void setLeftValue( String leftValue ) {
        this.leftValue = leftValue;
    }
    
    public int getRightType() {
        return rightType;
    }
    
    public void setRightType( int rightType ) {
        this.rightType = rightType;
    }
    
    public String getRightValue() {
        return rightValue;
    }
    
    public void setRightValue( String rightValue ) {
        this.rightValue = rightValue;
    }

    public List<String> getTypenames() {
        return typenames;
    }

    public void setTypenames( List<String> typenames ) {
        this.typenames = typenames;
    }

    public String getSubmitaction() {
        return submitaction;
    }

    public void setSubmitaction( String submitaction ) {
        this.submitaction = submitaction;
    }

    public String getNxMappingId() {
        return nxMappingId;
    }

    public void setNxMappingId( String nxMappingId ) {
        this.nxMappingId = nxMappingId;
    }

    public int getPageCount() {
        return pageCount;
    }

    public void setPageCount( int pageCount ) {
        this.pageCount = pageCount;
    }

    public int getCurrentPage() {
        return currentPage;
    }

    public void setCurrentPage( int currentPage ) {
        this.currentPage = currentPage;
    }

    public String getNewCategory() {
        return newCategory;
    }

    public void setNewCategory(String newCategory) {
        this.newCategory = newCategory;
    }

    public String getNewLeftType() {
        return newLeftType;
    }

    public void setNewLeftType(String newLeftType) {
        this.newLeftType = newLeftType;
    }

    public String getNewRightType() {
        return newRightType;
    }

    public void setNewRightType(String newRightType) {
        this.newRightType = newRightType;
    }

    public String getNewLeftValue() {
        return newLeftValue;
    }

    public void setNewLeftValue(String newLeftValue) {
        this.newLeftValue = newLeftValue;
    }

    public String getNewRightValue() {
        return newRightValue;
    }

    public void setNewRightValue(String newRightValue) {
        this.newRightValue = newRightValue;
    }
}

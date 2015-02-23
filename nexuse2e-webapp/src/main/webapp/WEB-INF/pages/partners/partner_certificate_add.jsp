<!--

     NEXUSe2e Business Messaging Open Source
     Copyright 2000-2009, Tamgroup and X-ioma GmbH

     This is free software; you can redistribute it and/or modify it
     under the terms of the GNU Lesser General Public License as
     published by the Free Software Foundation version 2.1 of
     the License.

     This software is distributed in the hope that it will be useful,
     but WITHOUT ANY WARRANTY; without even the implied warranty of
     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
     Lesser General Public License for more details.

     You should have received a copy of the GNU Lesser General Public
     License along with this software; if not, write to the Free
     Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
     02110-1301 USA, or see the FSF site: http://www.fsf.org.

-->
<jsp:root xmlns:c="http://java.sun.com/jsp/jstl/core"
    xmlns:nexus="/tags/nexus"
    xmlns:fn="http://java.sun.com/jsp/jstl/functions"
    xmlns:spring="http://www.springframework.org/tags"
    xmlns:form="http://www.springframework.org/tags/form"
    xmlns:jsp="http://java.sun.com/JSP/Page"
    xmlns:tiles="http://tiles.apache.org/tags-tiles"
    version="2.0">

    <jsp:directive.page contentType="text/html" />

    <table class="NEXUS_TABLE" width="100%">
        <tr>
            <td><nexus:crumbs styleClass="NEXUSScreenPathLink"></nexus:crumbs></td>
        </tr>
        <tr>
            <td class="NEXUSScreenName">Add Certificate</td>
        </tr>
    </table>

    <form:form action="PartnerCertificateSave.do" modelAttribute="partnerCertificateForm">
        <form:hidden path="id"/>        
        <table class="NEXUS_TABLE" width="100%">
            <tr>
                <td class="NEXUSSection">Collaboration Partner</td>
                <td class="NEXUSSection"><bean:write name="protectedFileAccessForm" property="id"/></td>
            </tr>
            <tr>
                <td class="NEXUSName">Certificate ID</td>
                <td class="NEXUSValue"><html:text size="64" property="alias" onkeypress="return checkKey(event);"/></td>
            </tr>
            <tr>
                <td class="NEXUSName">FileName</td>
                <td class="NEXUSValue"><input size="50" type="file" name="certificate" onkeypress="return checkKey(event);"/><br>
                <font size="1">browse to select X509 compatible certificate</font></td>
            </tr>
        </table>
        <table class="NEXUS_BUTTON_TABLE">
            <tr>
                <td>
                    &#160;
                </td>
                <td class="NexusHeaderLink" style="text-align: right;">
                    <nexus:submit precondition="true /*certificateCheckFields()*/" sendFileForm="true" styleClass="button"><img src="images/icons/tick.png" class="button">Save</nexus:submit>
                </td>
            </tr>
        </table>
    </form:form>
</nexus:fileUploadResponse>
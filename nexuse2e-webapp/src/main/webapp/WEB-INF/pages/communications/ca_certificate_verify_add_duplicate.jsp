<%--

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

--%>
<%@ taglib uri="/tags/struts-bean" prefix="bean" %>
<%@ taglib uri="/tags/struts-html" prefix="html" %>
<%@ taglib uri="/tags/struts-nested" prefix="nested" %>
<%@ taglib uri="/tags/struts-tiles" prefix="tiles" %>
<%@ taglib uri="/tags/struts-logic" prefix="logic" %>
<%@ taglib uri="/tags/nexus" prefix="nexus" %>

<nexus:fileUploadResponse>
<% /*<nexus:helpBar helpDoc="documentation/SSL.htm"/> */ %>

    <center>
        <table class="NEXUS_TABLE" width="100%">
            <tr>
                <td>
                    <nexus:crumbs/>
                </td>
            </tr>
            <tr>
                <td class="NEXUSScreenName">Server Certificate</td>
            </tr>
        </table>
        
        <logic:iterate id="duplicate" name="duplicates">
            <table class="NEXUS_TABLE" width="100%">
                <tr>
                    <td colspan="2" class="NEXUSSection" style="background-color: yellow;">WARNING: CA Certificate with same supposedly unique attribute(s) already exists: <bean:write name="duplicate" property="alias"/></td>
                </tr>
                <tr>
                    <td class="NEXUSName">Identical (supposedly unique) Attributes</td>
                    <td class="NEXUSValue">
                        <logic:equal name="duplicate" property="duplicateDistinguishedName" value="true">
                            Distinguished Name<br>
                        </logic:equal>
                        <logic:equal name="duplicate" property="duplicateSki" value="true">
                            SubjectKeyIdentifier
                        </logic:equal>
                        <logic:equal name="duplicate" property="duplicateFingerprint" value="true">
                            MD5 Fingerprint<br>
                        </logic:equal>
                        <logic:equal name="duplicate" property="duplicateSHA1Fingerprint" value="true">
                            SHA1 Fingerprint<br>
                        </logic:equal>
                    </td>
                </tr>
            </table>
            <table class="NEXUS_TABLE" width="100%">
                <tr>
                    <td class="NEXUSName">Alias</td>
                    <td class="NEXUSValue"><bean:write name="duplicate" property="alias"/></td>
                </tr>
                <tr>
                    <td class="NEXUSName">Common Name</td>
                    <td class="NEXUSValue"><bean:write name="duplicate" property="commonName"/></td>
                </tr>
                <tr>
                    <td class="NEXUSName">Organisation</td>
                    <td class="NEXUSValue"><bean:write name="duplicate" property="organisation"/></td>
                </tr>
                <tr>
                    <td class="NEXUSName">Organisation Unit</td>
                    <td class="NEXUSValue"><bean:write name="duplicate" property="organisationUnit"/></td>
                </tr>
                <tr>
                    <td class="NEXUSName">Country</td>
                    <td class="NEXUSValue"><bean:write name="duplicate" property="country"/></td>
                </tr>
                <tr>
                    <td class="NEXUSName">State</td>
                    <td class="NEXUSValue"><bean:write name="duplicate" property="state"/></td>
                </tr>
                <tr>
                    <td class="NEXUSName">Location</td>
                    <td class="NEXUSValue"><bean:write name="duplicate" property="location"/></td>
                </tr>
                <tr>
                    <td class="NEXUSName">E-Mail</td>
                    <td class="NEXUSValue"><bean:write name="duplicate" property="email"/></td>
                </tr>
                <tr>
                    <td class="NEXUSName">Not Valid Before</td>
                    <td class="NEXUSValue"><bean:write name="duplicate" property="notBefore"/></td>
                </tr>
                <tr>
                    <td class="NEXUSName">Not Valid After</td>
                    <td class="NEXUSValue"><bean:write name="duplicate" property="notAfter"/></td>
                </tr>
                <tr>
                    <td class="NEXUSName">Validity</td>
                    <td class="NEXUSValue">
                        <logic:equal name="duplicate" property="valid" value="Okay">
                            <font color="green"><b><bean:write name="duplicate" property="valid"/></b></font> <bean:write name="duplicate" property="timeRemaining"/>
                        </logic:equal>
                        <logic:notEqual name="duplicate" property="valid" value="Okay">
                            <font color="red"><b><bean:write name="duplicate" property="valid"/></b></font>
                        </logic:notEqual>
                    </td>
                </tr>
                <tr>
                    <td class="NEXUSName">SubjectKeyIdentifier</td>
                    <td class="NEXUSValue"><bean:write name="duplicate" property="subjectKeyIdentifier"/></td>
                </tr>
                <tr>
                    <td class="NEXUSName">MD5 Fingerprint</td>
                    <td class="NEXUSValue"><bean:write name="duplicate" property="fingerprint"/></td>
                </tr>
                <tr>
                    <td class="NEXUSName">SHA1 Fingerprint</td>
                    <td class="NEXUSValue"><bean:write name="duplicate" property="sha1Fingerprint"/></td>
                </tr>
            </table>
            <center>
                <table class="NEXUS_BUTTON_TABLE" width="100%">
                    <tr>
                        <td class="BUTTON_RIGHT">
                            <nobr>
                                <nexus:link href="CACertificateExportCertificate.do?nxCertificateId=${duplicate.nxCertificateId}" styleClass="button">
                                    <img src="images/icons/disk.png" class="button"/>Export existing certificate
                                </nexus:link>
                            </nobr>
                        </td>
                    </tr>
                </table>
            </center>
        </logic:iterate>
        
        <center> 
            <logic:messagesPresent>
                <div class="NexusError"><html:errors/></div> 
            </logic:messagesPresent>
        </center>
        <html:form action="CACertificateSaveSingleCert.do" method="POST"> 
            <html:hidden property="alias"/>
            <html:hidden property="certficatePath"/>
            <center>
                <table class="NEXUS_BUTTON_TABLE" width="100%">
                    <tr>
                        <td class="BUTTON_RIGHT">
                            <nexus:submit precondition="confirmDelete('Are you sure you want to import the new certificate anyway?')">
                                <img class="button" src="images/icons/add.png" name="SUBMIT">Import new certificate anyway
                            </nexus:submit>
                        </td>
                    </tr>
                </table>
            </center>
        </html:form>
    </center>
</nexus:fileUploadResponse>
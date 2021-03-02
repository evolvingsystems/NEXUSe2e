<%--

     NEXUSe2e Business Messaging Open Source
     Copyright 2000-2021, direkt gruppe GmbH

     This is free software; you can redistribute it and/or modify it
     under the terms of the GNU Lesser General Public License as
     published by the Free Software Foundation version 3 of
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
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>

<% /*<nexus:helpBar helpDoc="documentation/SSL.htm"/> */ %>

<center>

    <table class="NEXUS_TABLE" width="100%">
        <tr>
            <td><nexus:crumbs styleClass="NEXUSScreenPathLink"></nexus:crumbs></td>
        </tr>
        <tr>
            <td class="NEXUSScreenName">CA Certificate: <bean:write name="protectedFileAccessForm" property="alias"/></td>
        </tr>
    </table>

    <c:choose>
        <c:when test="${!empty downloadLinkUrl}">
            <table class="NEXUS_TABLE" width="100%">
                <tr>
                    <td colspan="2" class="NEXUSSection">Certificate Export</td>
                </tr>
                <tr>
                    <td class="NEXUSName">
                        <c:if test="${protectedFileAccessForm.format == 1}">
                            PEM format
                        </c:if>
                        <c:if test="${protectedFileAccessForm.format == 2}">
                            DER format
                        </c:if>
                    </td>
                    <td class="NEXUSValue" align="right"><i>Klick 'Download' to open download dialog</i></td>
                </tr>
            </table>

            <table class="NEXUS_BUTTON_TABLE" width="100%">
                <tr>
                    <td class="BUTTON_LEFT">
                        <nobr>
                            <nexus:link href="CACertificatesList.do" styleClass="button">
                                <img src="images/icons/resultset_previous.png" name="resultsButton" class="button" />
                                Back to Certificate List
                            </nexus:link>
                        </nobr>
                    </td>
                    <td class="BUTTON_RIGHT">
                        <a href="${downloadLinkUrl}" target="_blank" id="save_button" class="button">
                            <img src="images/icons/tick.png" class="button">
                            Download
                        </a>
                    </td>
                </tr>
            </table>
        </c:when>

        <c:otherwise>
            <html:form action="CACertStoreExported.do" method="POST">
                <html:hidden property="id" value='<%=request.getParameter("seqNo")%>'/>
                <table class="NEXUS_TABLE" width="100%">
                    <tr>
                        <td colspan="2" class="NEXUSSection">Certificate Format</td>
                    </tr>
                    <tr>
                        <td class="NEXUSName"><html:radio name="protectedFileAccessForm" property="format" value="1"/> PEM</td>
                        <td class="NEXUSValue"></td>
                    </tr>
                    <tr>
                        <td class="NEXUSName"><html:radio name="protectedFileAccessForm" property="format" value="2"/> DER</td>
                        <td class="NEXUSValue"></td>
                    </tr>
                </table>
                <table class="NEXUS_TABLE" width="100%">
                    <tr>
                        <td colspan="2" class="NEXUSSection">Destination</td>
                    </tr>
                    <tr>
                        <td class="NEXUSName"><html:radio name="protectedFileAccessForm" property="status" value="1"/> Target File (on the Server)</td>
                        <td class="NEXUSValue"><html:text name="protectedFileAccessForm" property="certficatePath" size="60"/></td>
                    </tr>
                    <tr>
                        <td class="NEXUSName"><html:radio name="protectedFileAccessForm" property="status" value="2"/> Save as...</td>
                        <td class="NEXUSValue">&nbsp;</td>
                    </tr>
                </table>
                <center>
                    <logic:messagesPresent>
                        <div class="NexusError"><html:errors/></div>
                    </logic:messagesPresent>
                </center>
                <table class="NEXUS_BUTTON_TABLE" width="100%">
                    <tr>
                        <td class="BUTTON_RIGHT">
                            <nexus:submit id="save_button"><img src="images/icons/tick.png" class="button">Save</nexus:submit>
                        </td>
                    </tr>
                </table>
            </html:form>
        </c:otherwise>
    </c:choose>
</center>
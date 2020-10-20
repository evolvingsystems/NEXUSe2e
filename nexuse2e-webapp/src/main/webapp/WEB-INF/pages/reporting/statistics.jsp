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
<%@ taglib uri="/tags/struts-bean" prefix="bean"%>
<%@ taglib uri="/tags/struts-html" prefix="html"%>
<%@ taglib uri="/tags/struts-nested" prefix="nested"%>
<%@ taglib uri="/tags/struts-tiles" prefix="tiles"%>
<%@ taglib uri="/tags/struts-logic" prefix="logic"%>
<%@ taglib uri='http://java.sun.com/jsp/jstl/core' prefix='c'%>
<%@ taglib uri="/tags/nexus" prefix="nexus"%>

<% /*<nexus:helpBar helpDoc="documentation/NEXUSe2e.html" /> */ %>

<table class="NEXUS_TABLE" style="width: 100%">
	<tr>
		<td><nexus:crumbs /></td>
	</tr>
	<tr>
		<td class="NEXUSScreenName">Dashboard</td>
	</tr>
</table>

<div class="statistics">
	<h2>Conversations (last 24h)</h2>
	<c:choose>
		<c:when test="${not empty conversationStatusCounts}">
			<div class="chart" id="conversations">
				<logic:iterate id="statusCount" name="conversationStatusCounts">
					<c:set var="percentage" value="${statusCount.value / conversationStatusTotal * 100}" />
					<c:if test="${percentage > 0}">
						<div class="segment"
							 style="width: ${percentage}%; background: ${colors[statusCount.key]};"
							 title="${statusCount.key} ${statusCount.value}">
							<span class="status-name">
								${statusCount.key}
							</span>
							<span class="count-label">
								${statusCount.value}
							</span>
						</div>
					</c:if>
				</logic:iterate>
			</div>
		</c:when>
		<c:otherwise>
			<p class="no-data">no conversations</p>
		</c:otherwise>
	</c:choose>

	<h2>Failed Messages</h2>
	<c:choose>
	<c:when test="${not empty messages}">
		<table class="NEXUS_TABLE fixed-table">
			<colgroup>
				<col>
				<col style="width: 6%">
				<col style="width: 10%">
				<col style="width: 30%">
				<col style="width: 30%">
				<col style="width: 78px">
			</colgroup>
			<tr>
				<th class="NEXUSSection">Time</th>
				<th class="NEXUSSection">Partner</th>
				<th class="NEXUSSection">Choreography</th>
				<th class="NEXUSSection">Conversation</th>
				<th class="NEXUSSection">Message</th>
				<th class="NEXUSSection"></th>
			</tr>
			<logic:iterate id="message" name="messages">
				<tr class="${message.messageId}">
					<td title="${message.createdDate}">
						${message.createdDate}
					</td>
					<td title="${message.partnerId}">
							${message.partnerId}
					</td>
					<td title="${message.choreographyId}">
						${message.choreographyId}
					</td>
					<td title="${message.conversationId}">
						<nexus:link
							href="ConversationView.do?convId=${message.nxConversationId}"
							styleClass="NexusLink">
							${message.conversationId}
						</nexus:link>
					</td>
					<td title="${message.messageId}">
						<nexus:link
							href="MessageView.do?mId=${message.messageId}"
							styleClass="NexusLink">
							${message.messageId}
						</nexus:link>
					</td>
					<td><button onClick="this.disabled = true; setContentUrl('ModifyMessage.do?&origin=dashboard&command=requeue&conversationId=${message.conversationId}&messageId=${message.messageId}');">
						Requeue
					</button></td>
				</tr>
			</logic:iterate>
		</table>
		<button onClick="setContentUrl('ProcessConversationReport.do?noReset=true')" class="full-width">Show more</button>
	</c:when>
	<c:otherwise>
		<p class="no-data">no failed messages</p>
	</c:otherwise>
	</c:choose>

	<div class="tab">
		<button class="tablinks" onclick="openTab(event, 'certificates')">Certificates</button>
		<button class="tablinks" onclick="openTab(event, 'partners')">Partners</button>
		<button class="tablinks" onclick="openTab(event, 'choreographies')">Choreographies</button>
	</div>

	<table class="NEXUS_TABLE fixed-table tabcontent" id="partners">
		<c:choose>
			<c:when test="${not empty partners}">
				<colgroup>
					<col>
					<col>
					<col>
					<col style="width: 30%">
				</colgroup>
				<tr>
					<th class="NEXUSSection">Partner ID</th>
					<th class="NEXUSSection">Inbound</th>
					<th class="NEXUSSection">Outbound</th>
					<th class="NEXUSSection"></th>
				</tr>
				<logic:iterate id="partner" name="partners">
					<tr>
						<td title="${partner.partnerId}"><nexus:link
								href="PartnerInfoView.do?nxPartnerId=${partner.nxPartnerId}&type=2"
								styleClass="NexusLink">
							${partner.partnerId} (${partner.name})
						</nexus:link></td>
						<td title="${lastInboundPerPartner[partner.partnerId]}">
							${lastInboundPerPartner[partner.partnerId]}
						</td>
						<td title="${lastOutboundPerPartner[partner.partnerId]}">
							${lastOutboundPerPartner[partner.partnerId]}
						</td>
						<td>
							<div class="chart" id="${partner.partnerId}"></div>
						</td>
					</tr>
				</logic:iterate>
			</c:when>
			<c:otherwise>
				<tr>
					<td class="no-data">no partners</td>
				</tr>
			</c:otherwise>
		</c:choose>
	</table>

	<table class="NEXUS_TABLE fixed-table tabcontent" id="choreographies">
		<c:choose>
			<c:when test="${not empty choreographies}">
			<colgroup>
				<col>
				<col>
				<col>
				<col style="width: 30%">
			</colgroup>
			<tr>
				<th class="NEXUSSection">Choreography</th>
				<th class="NEXUSSection">Last Inbound</th>
				<th class="NEXUSSection">Last Outbound</th>
				<th class="NEXUSSection"></th>
			</tr>
			<logic:iterate id="choreography" name="choreographies">
				<tr>
					<td title="${choreography.name}"><nexus:link
							href="ChoreographyView.do?nxChoreographyId=${choreography.nxChoreographyId}"
							styleClass="NexusLink">
						${choreography.name}
					</nexus:link></td>
					<td title="${lastInboundPerChoreography[choreography.name]}">
						${lastInboundPerChoreography[choreography.name]}
					</td>
					<td title="${lastOutboundPerChoreography[choreography.name]}">
						${lastOutboundPerChoreography[choreography.name]}
					</td>
					<td>
						<div class="chart" id="${choreography.nxChoreographyId}"></div>
					</td>
				</tr>
			</logic:iterate>
			</c:when>
			<c:otherwise>
				<tr>
					<td class="no-data">no choreographies</td>
				</tr>
			</c:otherwise>
		</c:choose>
	</table>

	<table class="NEXUS_TABLE fixed-table tabcontent" id="certificates">
		<c:choose>
			<c:when test="${not empty localCertficates or not empty certificatesPerPartner}">
				<tr>
					<th class="NEXUSSection">Configured For</th>
					<th class="NEXUSSection">Certificate Name</th>
					<th class="NEXUSSection">Time Remaining</th>
				</tr>
				<logic:iterate id="cert" name="localCertificates">
					<tr class="local">
						<td class="NEXUSName">Local</td>
						<td class="NEXUSName">${cert.name}</td>
						<td class="NEXUSName">${cert.remaining}</td>
					</tr>
				</logic:iterate>
				<logic:iterate id="partner" name="certificatesPerPartner">
					<c:forEach var = "cert" items="${partner.value}">
						<tr>
							<td title="${partner.key}">${partner.key}</td>
							<td title="${cert.name}">${cert.name}</td>
							<td title="${cert.remaining}">${cert.remaining}</td>
						</tr>
					</c:forEach>
				</logic:iterate>
			</c:when>
			<c:otherwise>
				<tr>
					<td class="no-data">no certificates</td>
				</tr>
			</c:otherwise>
		</c:choose>
	</table>
</div>


<script>
function openTab(evt, contentId) {
	var tabContent = document.querySelectorAll(".tabcontent");
	for (var i = 0; i < tabContent.length; i++) {
		tabContent[i].style.display = "none";
	}

	var tabLinks = document.querySelectorAll(".tablinks");
	for (var i = 0; i < tabLinks.length; i++) {
		tabLinks[i].className = tabLinks[i].className.replace(" active", "");
	}

	document.getElementById(contentId).style.display = "table";
	var target = (evt.currentTarget) ? evt.currentTarget : evt.srcElement;
	target.className += " active";
}

document.querySelector('.tablinks').click();
</script>

<logic:messagesPresent>
	<div class="NexusError"><html:errors /></div>
</logic:messagesPresent>

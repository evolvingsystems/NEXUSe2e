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
			<div class="chart" id="conversations"></div>
		</c:when>
		<c:otherwise>
			no conversations
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
		<nexus:link href="ProcessConversationReport.do?noReset=true">
			<button class="full-width">Show more</button>
		</nexus:link>
	</c:when>
	<c:otherwise>
		no messages
	</c:otherwise>
	</c:choose>

	<div class="tab">
		<button class="tablinks" onclick="openTab(event, 'certificates')">Certificates</button>
		<button class="tablinks" onclick="openTab(event, 'partners')">Partners</button>
		<button class="tablinks" onclick="openTab(event, 'choreographies')">Choreographies</button>
	</div>

	<table class="NEXUS_TABLE fixed-table tabcontent" id="partners">
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
		</table>

	<table class="NEXUS_TABLE fixed-table tabcontent" id="choreographies">
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
		</table>

	<table class="NEXUS_TABLE fixed-table tabcontent" id="certificates">
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
	</table>
</div>


<script>
window.statistics = window.statistics || {};
document.querySelector('.tablinks').click();

function openTab(evt, contentId) {
	let tabContent = document.getElementsByClassName("tabcontent");
	for (let i = 0; i < tabContent.length; i++) {
		tabContent[i].style.display = "none";
	}

	let tabLinks = document.getElementsByClassName("tablinks");
	for (let i = 0; i < tabLinks.length; i++) {
		tabLinks[i].className = tabLinks[i].className.replace(" active", "");
	}

	document.getElementById(contentId).style.display = "table";
	evt.currentTarget.className += " active";
}

(function () {
	"use strict";
	const chartData = {
		'conversations': ${conversationStatusCounts},
	};

	const total = {};

	const chartColors = {
		'failed': '#FB5012',
		'error': '#FB5012',
		'retrying': '#58A4B0',
		'outbound': '#58A4B0',
		'inbound': '#305252',
		'queued': '#305252',
		'sent': '#6DA34D',
		'stopped': '#3B252C',
		'unknown': '#B6B6B6',
		'created': '#58A4B0',
		'processing': '#305252',
		'awaiting_ack': '#305252',
		'acksent': '#305252',
		'sendingack': '#58A4B0',
		'idle': '#58A4B0',
		'completed': '#6DA34D'
	}

	const createSegments = function (chart) {
		const counts = chartData[chart.id];
		for (let name in counts) {
			const segment = document.createElement('div');
			const width = (counts[name] / total[chart.id] * 100);
			segment.style.background = chartColors[name]
			segment.style.width = width + '%';
			segment.innerHTML = '&nbsp;&nbsp;' + name;
			const countLabel = document.createElement('span');
			countLabel.innerHTML = counts[name] + '&nbsp;&nbsp;';
			countLabel.classList.add('count-label');
			segment.appendChild(countLabel);
			segment.classList.add('segment');
			if (width === 0) {
				segment.style.padding = '0';
			}
			chart.appendChild(segment);
			if (segment.offsetWidth < 30) {
				segment.classList.add('hidden-text');
			}
		}
	}

	const populateCharts = function() {
		const charts = document.querySelectorAll('.chart');
		for (let i = 0; i < charts.length; i++) {
			let chart = charts[i];
			if (chartData[chart.id]) {
				total[chart.id] = 0;
				for (let status in chartData[chart.id]) {
					total[chart.id] += chartData[chart.id][status];
				}
				createSegments(chart);
			}
		}
	};

	const sortDescending = function(obj) {
		const sortable = [];
		for (let element in obj) {
			sortable.push([element, obj[element]]);
		}

		if (sortable.length > 1) {
			sortable.sort(function(a, b) {
				return b[1] - a[1];
			});
		}

		return sortable;
	}

	const sortChartData = function() {
		const keys = Object.keys(chartData);
		for (let i = 0; i < keys.length; i++) {
			const name = keys[i];
			const sortedObj = {};
			const sortedArray = sortDescending(chartData[name]);
			for (let j = 0; j < sortedArray.length; j++) {
				const item = sortedArray[j];
				sortedObj[item[0]] = item[1];
			}
			chartData[name] = sortedObj;
		}
	}

	sortChartData();
	populateCharts();
})(statistics);
</script>


<logic:messagesPresent>
	<div class="NexusError"><html:errors /></div>
</logic:messagesPresent>

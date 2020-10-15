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
	<section class="statistics">
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
			<table class="NEXUS_TABLE fixed-table" style="width: 100%">
				<colgroup>
					<col>
					<col style="width: 10%">
					<col style="width: 30%">
					<col style="width: 30%">
					<col style="width: 78px">
				</colgroup>
				<tr>
					<th class="NEXUSSection">Time</th>
					<th class="NEXUSSection">Choreography</th>
					<th class="NEXUSSection">Conversation ID</th>
					<th class="NEXUSSection">Message ID</th>
					<th class="NEXUSSection"></th>
				</tr>
				<logic:iterate id="message" name="messages">
					<tr class="${message.messageId}">
						<td  title="${message.createdDate}">
							<bean:write name="message" property="createdDate" />
						</td>
						<td  title="${message.choreographyId}">
							<bean:write name="message" property="choreographyId" />
						</td>
						<td  title="${message.conversationId}">
							<nexus:link
								href="ConversationView.do?convId=${message.nxConversationId}"
								styleClass="NexusLink">
								<bean:write name="message" property="conversationId" />
							</nexus:link>
						</td>
						<td  title="${message.messageId}">
							<nexus:link
								href="MessageView.do?mId=${message.messageId}"
								styleClass="NexusLink">
								<bean:write name="message" property="messageId" />
							</nexus:link>
						</td>
						<td><button onclick="console.log(${message.messageId})">
							<nexus:link href="ModifyMessage.do?noReset&refresh&type=transaction&origin=dashboard&command=requeue&conversationId=${message.conversationId}&messageId=${message.messageId}">
								Requeue
							</nexus:link>
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

		<h2>Partners</h2>
		<table class="NEXUS_TABLE fixed-table" style="width: 100%">
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
					<td><nexus:link
							href="PartnerInfoView.do?nxPartnerId=${partner.nxPartnerId}&type=2"
							styleClass="NexusLink">
						<bean:write name="partner" property="partnerId" /> (<bean:write name="partner" property="name" />)
					</nexus:link></td>
					<td>
						${lastInboundPerPartner[partner.partnerId]}
					</td>
					<td>
						${lastOutboundPerPartner[partner.partnerId]}
					</td>
					<td><div class="chart" id="${partner.partnerId}"></div></td>
				</tr>
			</logic:iterate>
		</table>

		<h2>Choreographies</h2>
		<table class="NEXUS_TABLE fixed-table" style="width: 100%">
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
					<td><nexus:link
							href="ChoreographyView.do?nxChoreographyId=${choreography.nxChoreographyId}"
							styleClass="NexusLink">
						<bean:write name="choreography" property="name" />
					</nexus:link></td>
					<td>
						${lastInboundPerChoreography[choreography.name]}
					</td>
					<td>
						${lastOutboundPerChoreography[choreography.name]}
					</td>
					<td><div class="chart" id="${choreography.nxChoreographyId}"></div></td>
				</tr>
			</logic:iterate>
		</table>

		<h2>Certificates</h2>
		<table class="NEXUS_TABLE fixed-table" style="width: 100%">
			<tr>
				<th class="NEXUSSection">Configured For</th>
				<th class="NEXUSSection">Certificate Name</th>
				<th class="NEXUSSection">Time Remaining</th>
			</tr>
			<logic:iterate id="cert" name="localCertificates">
				<tr class="local">
					<td>Local</td>
					<td>${cert.name}</td>
					<td>${cert.remaining}</td>
				</tr>
			</logic:iterate>
			<logic:iterate id="partner" name="certificatesPerPartner">
				<tr>
				<c:forEach var = "cert" items="${partner.value}">
					<td>${partner.key}</td>
					<td>${cert.name}</td>
					<td>${cert.remaining}</td>
				</c:forEach>
				</tr>
			</logic:iterate>
		</table>
</div>

<script>
window.statistics = window.statistics || {};

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
		Object.keys(counts).forEach((name) => {
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
		});
	}

	const sortDescending = function(obj) {
		const sortable = [];
		for (const element in obj) {
			if (obj.hasOwnProperty(element)) {
				sortable.push([element, obj[element]]);
			}
		}

		sortable.sort(function(a, b) {
			return b[1] - a[1];
		});

		return sortable;
	}

	const sortChartData = function() {
		Object.keys(chartData).forEach((name) => {
		    const sortedObj = {};
		    const sortedArray = sortDescending(chartData[name]);
            sortedArray.forEach(function(item){
                sortedObj[item[0]] = item[1]
            })
            chartData[name] = sortedObj;
		})
	}

	const populateCharts = function() {
		const charts = document.querySelectorAll('.chart');
		for (const chart of charts) {
			if (chartData[chart.id]) {
				total[chart.id] = Object.values(chartData[chart.id]).reduce((a, b) => a + b, 0);
				createSegments(chart);
			}
		}
	};

	const disableLine = function(messageId) {
		const line = document.querySelector('.' + messageId);
		console.log(messageId);
		console.log(line);
	}

	sortChartData();
	populateCharts();
})(statistics);
</script>


<logic:messagesPresent>
	<div class="NexusError"><html:errors /></div>
</logic:messagesPresent>

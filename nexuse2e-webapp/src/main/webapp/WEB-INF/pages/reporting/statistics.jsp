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
<%@ taglib uri="http://java.sun.com/jstl/core" prefix="c"%>
<%@ taglib uri="/tags/nexus" prefix="nexus"%>

<% /*<nexus:helpBar helpDoc="documentation/NEXUSe2e.html" /> */ %>

<table class="NEXUS_TABLE" width="100%">
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
		<div class="chart" id="conversations"></div>

		<h2>Failed Messages</h2>
		<table class="NEXUS_TABLE fixed-table" width="100%">
			<colgroup>
				<col>
				<col style="width: 10%">
				<col style="width: 30%">
				<col style="width: 30%">
				<col style="width: 75px">
			</colgroup>
			<tr>
				<th class="NEXUSSection">Time</th>
				<th class="NEXUSSection">Choreography</th>
				<th class="NEXUSSection">Conversation ID</th>
				<th class="NEXUSSection">Message ID</th>
				<th class="NEXUSSection"></th>
			</tr>
			<logic:iterate id="message" name="messages">
				<tr>
					<td class="NEXUSName" title="${message.createdDate}">
						<bean:write name="message" property="createdDate" />
					</td>
					<td class="NEXUSName" title="${message.choreographyId}">
						<bean:write name="message" property="choreographyId" />
					</td>
					<td class="NEXUSName" title="${message.conversationId}">
						<nexus:link
							href="ConversationView.do?convId=${message.nxConversationId}"
							styleClass="NexusLink">
							<bean:write name="message" property="conversationId" />
						</nexus:link>
					</td>
					<td class="NEXUSName" title="${message.messageId}">
						<nexus:link
							href="MessageView.do?mId=${message.messageId}"
							styleClass="NexusLink">
						<bean:write name="message" property="messageId" />
						</nexus:link>
					</td>
					<td><button>Requeue</button></td>
				</tr>
			</logic:iterate>
		</table>
		<nexus:link href="ProcessConversationReport.do?noReset=true">
			<button class="full-width">Show more</button>
		</nexus:link>

		<h2>Partners</h2>
		<table class="NEXUS_TABLE fixed-table" width="100%">
			<colgroup>
				<col>
				<col>
				<col>
				<col style="width: 30%">
			</colgroup>
			<tr>
				<td class="NEXUSSection">Partner ID</td>
				<td class="NEXUSSection">Last Inbound</td>
				<td class="NEXUSSection">Last Outbound</td>
				<td class="NEXUSSection"></td>
			</tr>
			<logic:iterate id="partner" name="partners">
				<tr>
					<td class="NEXUSName"><nexus:link
							href="PartnerInfoView.do?nxPartnerId=${partner.nxPartnerId}&type=2"
							styleClass="NexusLink">
						<bean:write name="partner" property="partnerId" /> (<bean:write name="partner" property="name" />)
					</nexus:link></td>
					<td><nexus:link
							href="PartnerConnectionList.do?nxPartnerId=${partner.nxPartnerId}"
							styleClass="NexusDrillDownLink">Connections</nexus:link></td>
					<td></td>
					<td><div class="chart" id="${partner.partnerId}"></div></td>
				</tr>
			</logic:iterate>
		</table>
</div>

<script>
window.statistics = window.statistics || {};

(function (s) {
	"use strict";
	const chartData = {
		'conversations': ${conversationStatusCounts},
		'Xioma' : {
			'inbound' : 40,
			'error' : 2,
			'outbound' : 60
		},
		'test-corp' : {
			'inbound' : 50,
			'outbound' : 50
		}
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
			segment.innerHTML = name;
			const countLabel = document.createElement('span');
			countLabel.innerHTML = counts[name];
			countLabel.classList.add('count-label');
			segment.appendChild(countLabel);
			segment.classList.add('segment');
			if (width === 0) {
				segment.style.padding = 0;
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
			sortable.push([element, obj[element]]);
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
                sortedObj[item[0]]=item[1]
            })
            chartData[name] = sortedObj;
		})
	}

	const populateCharts = function() {
		const charts = document.querySelectorAll('.chart');
		for (const chart of charts) {
			total[chart.id] = Object.values(chartData[chart.id]).reduce((a, b) => a + b, 0);
			createSegments(chart);
		}
	};

	sortChartData();
	populateCharts();
})(statistics);
</script>


<center><logic:messagesPresent>
	<div class="NexusError"><html:errors /></div>
</logic:messagesPresent></center>

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
		<div class="chart" id="conversations">
		</div>

		<h2>Failed Messages</h2>
		<table class="error-messages">
			<colgroup>
				<col>
				<col style="width: 10%">
				<col style="width: 30%">
				<col style="width: 30%">
				<col style="width: 75px">
			</colgroup>
			<tr>
				<th>Time</th>
				<th>Choreography</th>
				<th>Conversation ID</th>
				<th>Message ID</th>
				<th></th>
			</tr>
		</table>
		<nexus:link href="ProcessConversationReport.do?noReset=true">
			<button class="full-width">Show more</button>
		</nexus:link>
</div>

<script>
window.statistics = window.statistics || {};

(function (s) {
	"use strict";
	const chartData = {
		'conversations': ${conversationStatusCounts},
		'awesome-inc' : {
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

	const populateMessageTable = function() {
		const table = document.querySelector('.error-messages');
		for (const message of ${messages}) {
			const row = document.createElement('tr');

			const createdDate = document.createElement('td');
			createdDate.innerHTML = message['createdDate'];
			createdDate.setAttribute('title', message['createdDate']);

			const choreographyId = document.createElement('td');
			choreographyId.innerHTML = message['choreographyId'];
			choreographyId.setAttribute('title', message['choreographyId']);

			const conversationId = document.createElement('td');
            const cAnchor = document.createElement('a');
            cAnchor.innerText = message['conversationId'];
            cAnchor.setAttribute('href', "javascript: setContentUrl('ConversationView.do?convId=" + message['nxConversationId'] + "');");
			cAnchor.classList.add('NexusLink');
            conversationId.appendChild(cAnchor);
			conversationId.setAttribute('title', message['conversationId']);

			const messageId = document.createElement('td');
			const mAnchor = document.createElement('a');
			mAnchor.innerText = message['messageId'];
			mAnchor.setAttribute('href', "javascript: setContentUrl('MessageView.do?mId=" + message['messageId'] + "');");
			mAnchor.classList.add('NexusLink');
			messageId.appendChild(mAnchor);
			messageId.setAttribute('title', message['messageId']);

			const requeueButton = document.createElement('td');
			const button = document.createElement('button');
			button.innerHTML = "Requeue";
			requeueButton.appendChild(button);

			row.appendChild(createdDate);
			row.appendChild(choreographyId);
			row.appendChild(conversationId);
			row.appendChild(messageId);
			row.appendChild(requeueButton);
			table.appendChild(row);
		}
	}

	populateMessageTable();
	sortChartData();
	populateCharts();
})(statistics);
</script>


<center><logic:messagesPresent>
	<div class="NexusError"><html:errors /></div>
</logic:messagesPresent></center>

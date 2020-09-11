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

<table class="NEXUS_TABLE" width="100%">
	<tr>
		<th class="NEXUSSection">Message ID</th>
		<th class="NEXUSSection">Status</th>
		<th class="NEXUSSection">Message Type</th>
		<th class="NEXUSSection">Action</th>
		<th class="NEXUSSection">Direction</th>
		<th class="NEXUSSection">Created Date</th>
		<th class="NEXUSSection">End Date</th>
		<th class="NEXUSSection">Turnaround Time</th>
	</tr>
	<logic:iterate indexId="counter" id="message" name="collection">
		<tr>
			<td class="NEXUSValue"><nexus:link styleClass="NexusLink"
											   href="MessageView.do?mId=${message.messageId}&convId=${message.conversationId}&chorId=${message.choreographyId}&partnerId=${message.participantId}">
				<bean:write name="message" property="messageId" />
			</nexus:link></td>
			<td class="NEXUSValue"><bean:write name="message"
											   property="status" /></td>
			<td class="NEXUSValue"><bean:write name="message"
											   property="type" /></td>
			<td class="NEXUSValue"><bean:write name="message"
											   property="action" /></td>
			<td class="NEXUSValue"><bean:write name="message"
											   property="direction" /></td>
			<td class="NEXUSValue"><bean:write name="message"
											   property="createdDate" /></td>
			<td class="NEXUSValue"><bean:write name="message"
											   property="endDate" /></td>
			<td class="NEXUSValue"><bean:write name="message"
											   property="turnaroundTime" /></td>
		</tr>
	</logic:iterate>
</table>

<div class="statistics">
	<section class="statistics">
		<h2>Messages (last 24h)</h2>

		<h3>By Status</h3>
		<div class="chart" id="overview">
		</div>

		<h3>Error Messages</h3>
		<table class="error-messages">
			<colgroup>
				<col>
				<col>
				<col style="width: 25%">
				<col style="width: 25%">
				<col>
				<col style="width: 75px">
			</colgroup>
			<tr>
				<th>Time</th>
				<th>Choreography</th>
				<th>Conversation ID</th>
				<th>Message ID</th>
				<th>Participant ID</th>
				<th></th>
			</tr>
			<tr>
				<td>23.05.19 13:56</td>
				<td>GenericFile</td>
				<td>t6guzu7-gzhz6zz-67t767tg-78zh8z8</td>
				<td>sddsfhfs-ewrwer-67t767tg-78zh8z8</td>
				<td>dev2</td>
				<td><button>Requeue</button></td>
			</tr>
		</table>

		<h3>By partner</h3>
		<table>
			<colgroup>
				<col>
				<col>
				<col style="width: 40%">
			</colgroup>
			<tr>
				<th>Partner</th>
				<th>Last successful message</th>
				<th></th>
			</tr>
			<tr>
				<td>Awesome Inc.</td>
				<td>23.05.19 13:58</td>
				<td><div class="chart" id="awesome-inc"></td>
			</tr>
			<tr>
				<td>Test Corp.</td>
				<td>23.05.19 13:56</td>
				<td><div class="chart" id="test-corp"></td>
			</tr>
		</table>
	</section>

	<section>
		<h2>Certificates</h2>

		<h3>Local</h3>
		<div class="cert-box">
			<div class="cert-name">Cert1</div>
			<div>~8 months left</div>
		</div>
		<div class="cert-box">
			<div class="cert-name">Cert2</div>
			<div>~8 months left</div>
		</div>

		<hr>

		<h3>Awesome Inc.</h3>
		<div class="cert-box">
			<div class="cert-name">Cert1</div>
			<div>~8 months left</div>
		</div>
		<div class="cert-box">
			<div class="cert-name">Cert2</div>
			<div>~8 months left</div>
		</div>

		<h3>Test Corp.</h3>
		<div class="cert-box">
			<div class="cert-name">Cert1</div>
			<div>~8 months left</div>
		</div>
		<div class="cert-box">
			<div class="cert-name">Cert2</div>
			<div>~2 weeks left</div>
		</div>
	</section>
</div>

<script>
	let chartData = {
		'overview' : {
			'Error': 106,
			'Idle': 58,
			'AckSent': 124,
			'Completed': 980
		},
		'awesome-inc' : {
			'Inbound' : 40,
			'Outbound' : 60,
			'Error' : 2
		},
		'test-corp' : {
			'Inbound' : 50,
			'Outbound' : 50
		}
	};

	const total = {};

	const colors = {
		'Failed': '#FB5012',
		'Error': '#FB5012',
		'Retrying': '#58A4B0',
		'Outbound': '#58A4B0',
		'Inbound': '#305252',
		'Queued': '#305252',
		'Sent': '#6DA34D',
		'Stopped': '#3B252C',
		'Unknown': '#B6B6B6',
		'Created': '#58A4B0',
		'Processing': '#305252',
		'AckSent': '#305252',
		'SendingAck': '#58A4B0',
		'Idle': '#58A4B0',
		'Completed': '#6DA34D'
	}

	const sort = function(obj) {
		let sortable = [];
		for (let key in obj)
			if (obj.hasOwnProperty(key))
				sortable.push([key, obj[key]]);
			sortable.sort(function (a, b) {
			return a[1] - b[1];
		});
		return sortable;
	}

	const createSegments = function (chart) {
		const counts = chartData[chart.id];
		Object.keys(counts).forEach((name) => {
			const segment = document.createElement('div');
			const width = (counts[name] / total[chart.id] * 100);
			segment.style.background = colors[name]
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

	const sortData = function() {
		Object.keys(chartData).forEach((key) => {
			chartData[key] = sort(chartData[key]);
		})
	};

	const populateCharts = function() {
		const charts = document.querySelectorAll('.chart');
		for (const chart of charts) {
			total[chart.id] = Object.values(chartData[chart.id]).reduce((a, b) => a + b);
			createSegments(chart);
		}
	};

	console.log(${choreographies});
	populateCharts();
</script>


<center><logic:messagesPresent>
	<div class="NexusError"><html:errors /></div>
</logic:messagesPresent></center>

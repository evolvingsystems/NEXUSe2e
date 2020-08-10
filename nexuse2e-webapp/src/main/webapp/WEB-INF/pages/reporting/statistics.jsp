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
		<td class="NEXUSScreenName">NEXUSe2e</td>
	</tr>
</table>
<div style="float: left">
<canvas id="Choreographies" width="400" height="400"></canvas>
<script>
	    var colorGradient = [
	    		'#FFB74D',
				'#FFA726',
				'#FF9800',
				'#FB8C00',
				'#F57C00',
				'#EF6C00',
				'#E65100',
				'#FFAB40',
				'#FF9100',
				'#FF6D00',
				'#FFCC80',
				'#FFF3E0',
				'#FFE0B2',
				'#FFD180',
				]
		var colorBinary = [
			'#FF5722',
			'#FFB74D',
			'#00E676',
		]
		var ctx = document.getElementById('Choreographies').getContext('2d');
		var myChart = new Chart(ctx, {
			type: 'pie',
			data: {
				labels: [
						${choreographies}
						],
				datasets: [{
					label: '# of messages',
					data: [${choreographyCounts}],
					backgroundColor: colorGradient,
					borderWidth: 2
				}]
			},
			options: {
				responsive:false,
				maintainAspectRatio: false,

			}
		});
	</script>
</div>

<div style="float: left">
<canvas id="Status" width="400" height="400"></canvas>
<script>
	var ctx = document.getElementById('Status').getContext('2d');
	var myChart = new Chart(ctx, {
		type: 'pie',
		data: {
			labels: [
				${status}
			],
			datasets: [{
				label: '# of messages',
				data: [${statusCounts}],
				backgroundColor: colorBinary,
				borderWidth: 2
			}]
		},
		options: {
			responsive:false,
			maintainAspectRatio: false,

		}
	});
</script>
</div>


<div style="float: left">
	<canvas id="timeline" width="800" height="400"></canvas>
	<script>
		var timeFormat = 'MM/DD/YYYY HH:mm';

		function newDate(days) {
			return moment().add(days, 'd').toDate();
		}

		function newDateString(days) {
			return moment().add(days, 'd').format(timeFormat);
		}

		var color = Chart.helpers.color;
		var ctx = document.getElementById('timeline').getContext('2d');
		var myChart = new Chart(ctx, {
			type: 'bar',
			data: {
				labels: [ ${times}

				],
				datasets: [{
					label: 'My First dataset',
					backgroundColor: color('#FFD180').alpha(0.5).rgbString(),
					borderColor: '#FFD180',
					fill: false,
					data: [
						${timeCounts}

					],
				}, {
					label: 'My Second dataset',
					backgroundColor: color('#FFD180').alpha(0.5).rgbString(),
					borderColor: '#FFD180',
					barThickness: 5,
					fill: false,
					data: [
						2,3,4,5,6,7,8
					],
				}]
			},
			options: {
				title: {
					text: 'Chart.js Time Scale'
				},
				scales: {
					xAxes: [{
						type: 'time',
						time: {
							parser: timeFormat,
							round: 'day',
							tooltipFormat: 'll HH:mm'
						},
						scaleLabel: {
							display: true,
							labelString: 'Date'
						}
					}],
					yAxes: [{
						scaleLabel: {
							display: true,
							labelString: 'value'
						}
					}]
				},
			}
		});
	</script>
</div>


<center><logic:messagesPresent>
	<div class="NexusError"><html:errors /></div>
</logic:messagesPresent></center>

/**
 *  NEXUSe2e Business Messaging Open Source
 *  Copyright 2000-2021, direkt gruppe GmbH
 *
 *  This is free software; you can redistribute it and/or modify it
 *  under the terms of the GNU Lesser General Public License as
 *  published by the Free Software Foundation version 3 of
 *  the License.
 *
 *  This software is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 *  Lesser General Public License for more details.
 *
 *  You should have received a copy of the GNU Lesser General Public
 *  License along with this software; if not, write to the Free
 *  Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 *  02110-1301 USA, or see the FSF site: http://www.fsf.org.
 */
package org.nexuse2e.messaging.ebxml.v20;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.nexuse2e.pojo.MessagePojo;

/**
 * Schemes for generating / parsing CPAId in ebXML. First in line is the default scheme. 
 * @author ssc
 */
public enum CPAIdScheme {
	CHOREOGRAPHYID {

		@Override
		public String makeCPAId(MessagePojo messagePojo) {
			return messagePojo.getConversation().getChoreography().getName();
		}

		@Override
		public String getChoreographyIdFromCPAId(String CPAId) {
			return CPAId;
		}
		
		@Override
		public String getDescription() {
			return "Choreography ID";
		}

	},
	URI {

		@Override
		public String makeCPAId(MessagePojo messagePojo) {
			return "uri://"
					+ messagePojo.getParticipant().getLocalPartner().getPartnerId()
					+ "/"
					+ messagePojo.getConversation().getPartner().getPartnerId()
					+ "/" + messagePojo.getConversation().getChoreography().getName();
		}

		@Override
		public String getChoreographyIdFromCPAId(String CPAId) {
			Matcher m = Pattern.compile( "uri://[^/]+/[^/]+/(.+)" ).matcher( CPAId );
			return ( m.matches() ? m.group( 1 ) : CPAId );
		}

		@Override
		public String getDescription() {
			return "uri://&lt;sender&gt;/&lt;recipient&gt;/&lt;choreography&gt;";
		}

	};

	public abstract String makeCPAId(MessagePojo messagePojo);

	public abstract String getChoreographyIdFromCPAId(String CPAId);
	
	public abstract String getDescription();
}

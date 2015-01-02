/**
 *  NEXUSe2e Business Messaging Open Source
 *  Copyright 2000-2009, Tamgroup and X-ioma GmbH
 *
 *  This is free software; you can redistribute it and/or modify it
 *  under the terms of the GNU Lesser General Public License as
 *  published by the Free Software Foundation version 2.1 of
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
package org.nexuse2e.ui.controller.tool;

import java.util.Calendar;
import java.util.Date;

import org.nexuse2e.Engine;
import org.nexuse2e.NexusException;
import org.nexuse2e.ui.form.DatabasePurgeForm;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * Controller for engine log purge functionality.
 *
 * @author Jonas Reese
 */
@Controller
public class EngineLogPurgeController {

    @RequestMapping("/DatabasePurge.do")
    public String purgeEngineLog(DatabasePurgeForm form) throws NexusException {

        if (form.getType().equals("select")) {
            form.setEndEnabled(true);
        } else if (form.getType().equals("preview")) {
            Date[] dates = getTimestamps(form);
            form.setLogEntryCount((int) Engine.getInstance().getTransactionService().getLogCount(dates[0], dates[1]));
        } else if (form.getType().equals("remove")) {
            Date[] dates = getTimestamps(form);
            form.setLogEntryCount((int) Engine.getInstance().getTransactionService().removeLogEntries(dates[0], dates[1]));
        }
        
        return "pages/tools/database_purge";
    }

    private Date[] getTimestamps(DatabasePurgeForm form) throws NexusException{
        
        Date startDate = null;
        Date endDate = null;
        
        if(form.isStartEnabled()){
            Calendar start = Calendar.getInstance();
            start.set( Calendar.DAY_OF_MONTH, Integer.parseInt( form.getStartDay()) );
            start.set( Calendar.MONTH, Integer.parseInt( form.getStartMonth())-1 );
            start.set( Calendar.YEAR, Integer.parseInt( form.getStartYear()) );
            start.set( Calendar.HOUR_OF_DAY, Integer.parseInt( form.getStartHour()) );
            start.set( Calendar.MINUTE, Integer.parseInt( form.getStartMin()) );
            startDate = start.getTime();
        }
        if(form.isEndEnabled()){
            Calendar end = Calendar.getInstance();
            end.set( Calendar.DAY_OF_MONTH, Integer.parseInt( form.getEndDay()) );
            end.set( Calendar.MONTH, Integer.parseInt( form.getEndMonth())-1 );
            end.set( Calendar.YEAR, Integer.parseInt( form.getEndYear()) );
            end.set( Calendar.HOUR_OF_DAY, Integer.parseInt( form.getEndHour()) );
            end.set( Calendar.MINUTE, Integer.parseInt( form.getEndMin()) );
            endDate = end.getTime();
        }
        return new Date[] { startDate, endDate };
    }
}

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
package org.nexuse2e.logging;

import org.apache.log4j.PatternLayout;
import org.apache.log4j.helpers.PatternParser;


public class ExtPatternLayout extends PatternLayout{
    public ExtPatternLayout() 

    {

       this(DEFAULT_CONVERSION_PATTERN);

    }



    public ExtPatternLayout(String pattern) 

    {

       super(pattern);

    }

     

    public PatternParser createPatternParser(String pattern) 

    {

       PatternParser result;

       if ( pattern == null )

          result = new ExtPatternParser( DEFAULT_CONVERSION_PATTERN );

       else

          result = new ExtPatternParser ( pattern );



       return result;

   }

}

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
package org.nexuse2e.ui.resolver;

import javax.servlet.http.HttpServletRequest;

import org.nexuse2e.Engine;
import org.nexuse2e.configuration.ConfigurationAccessService;
import org.nexuse2e.configuration.EngineConfiguration;
import org.nexuse2e.pojo.UserPojo;
import org.springframework.core.MethodParameter;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.support.WebArgumentResolver;
import org.springframework.web.context.request.NativeWebRequest;

/**
 * Resolves {@link ConfigurationAccessService} ({@link EngineConfiguration}) type request
 * parameters for spring MVC.
 *
 * @author Jonas Reese
 */
@Component
public class EngineConfigurationResolver implements WebArgumentResolver {

    @Override
    public Object resolveArgument(MethodParameter methodParameter, NativeWebRequest webRequest) throws Exception {
        if (ConfigurationAccessService.class.isAssignableFrom(methodParameter.getParameterType())) {
            UserPojo user = (UserPojo) ((HttpServletRequest) webRequest.getNativeRequest()).getSession().getAttribute("nxUser");
            if (user == null) {
                return Engine.getInstance().getCurrentConfiguration();
            } else {
                return Engine.getInstance().getConfiguration(user.getNxId());
            }
        }
        return UNRESOLVED;
    }
}

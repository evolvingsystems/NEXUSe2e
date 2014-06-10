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

import java.util.Locale;

import javax.servlet.ServletException;

import org.springframework.web.servlet.View;
import org.springframework.web.servlet.ViewResolver;

/**
 * View resolver that delegates to JSP/Tiles view resolvers.
 *
 * @author Jonas Reese
 */
public class DelegatingViewResolver implements ViewResolver {

    private ViewResolver tilesResolver;
    private ViewResolver jspResolver;

    public ViewResolver getTilesResolver() {
        return tilesResolver;
    }

    public void setTilesResolver(ViewResolver tilesResolver) {
        this.tilesResolver = tilesResolver;
    }

    public ViewResolver getJspResolver() {
        return jspResolver;
    }

    public void setJspResolver(ViewResolver jspResolver) {
        this.jspResolver = jspResolver;
    }

    @Override
    public View resolveViewName(String viewName, Locale locale) throws Exception {
        if (jspResolver != null && tilesResolver != null) {
            try {
                View view = tilesResolver.resolveViewName(viewName, locale);
                if (view == null) {
                    return jspResolver.resolveViewName(viewName, locale);
                }
                return view;
            } catch (Exception e) {
                return jspResolver.resolveViewName(viewName, locale);
            }
        } else if (jspResolver != null) {
            return jspResolver.resolveViewName(viewName, locale);
        } else if (tilesResolver != null) {
            return tilesResolver.resolveViewName(viewName, locale);
        }
        throw new ServletException("Cannot resolve view with name " + viewName);
    }
}

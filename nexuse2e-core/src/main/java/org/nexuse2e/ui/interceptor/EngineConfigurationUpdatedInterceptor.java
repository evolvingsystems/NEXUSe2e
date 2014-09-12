package org.nexuse2e.ui.interceptor;

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.nexuse2e.Engine;
import org.nexuse2e.pojo.NEXUSe2ePojo;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

/**
 * Spring controller interceptor that finds out if the engine configuration has been updated
 * by a controller method. In this case, the interceptor takes necessary steps to update the
 * UI accordingly (e.g., update the tree view).
 *
 * @author Jonas Reese
 */
public class EngineConfigurationUpdatedInterceptor extends HandlerInterceptorAdapter {

    public boolean preHandle(
            HttpServletRequest request, HttpServletResponse response, Object handler)
                    throws Exception {
        
        int updateListSize = 0;
        List<NEXUSe2ePojo> l = Engine.getInstance().getCurrentConfiguration().getUpdateList();
        if (l != null) {
            updateListSize = l.size();
        }
        int deleteListSize = 0;
        l = Engine.getInstance().getCurrentConfiguration().getDeleteList();
        if (l != null) {
            deleteListSize = l.size();
        }
        
        request.setAttribute(getClass().getSimpleName() + "_updateListSize", updateListSize);
        request.setAttribute(getClass().getSimpleName() + "_deleteListSize", deleteListSize);
        
        return true;
    }

    /**
     * This implementation is empty.
     */
    public void postHandle(
            HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView)
                    throws Exception {
        
        Integer updateListSize = (Integer) request.getAttribute(getClass().getSimpleName() + "_updateListSize");
        Integer deleteListSize = (Integer) request.getAttribute(getClass().getSimpleName() + "_deleteListSize");
        
        boolean update = false;
        List<NEXUSe2ePojo> l = Engine.getInstance().getCurrentConfiguration().getUpdateList();
        if (updateListSize != null && l != null && l.size() != updateListSize) {
            update = true;
        }
        l = Engine.getInstance().getCurrentConfiguration().getDeleteList();
        if (deleteListSize != null && l != null && l.size() != deleteListSize) {
            update = true;
        }
        
        if (update) {
            request.setAttribute("refreshTree", true);
        }
    }
}

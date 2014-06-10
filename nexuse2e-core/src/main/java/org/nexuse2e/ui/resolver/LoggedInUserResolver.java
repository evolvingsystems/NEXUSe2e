package org.nexuse2e.ui.resolver;

import javax.servlet.http.HttpServletRequest;

import org.nexuse2e.pojo.UserPojo;
import org.springframework.core.MethodParameter;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.support.WebArgumentResolver;
import org.springframework.web.context.request.NativeWebRequest;

/**
 * Resolves the logged-in user for parameters of type {@code UserPojo}.
 *
 * @author Jonas Reese
 */
@Component
public class LoggedInUserResolver implements WebArgumentResolver {

    @Override
    public Object resolveArgument(MethodParameter methodParameter, NativeWebRequest webRequest) throws Exception {
        if (UserPojo.class.isAssignableFrom(methodParameter.getParameterType())) {
            return ((HttpServletRequest) webRequest.getNativeRequest()).getSession().getAttribute("nxUser");
        }
        return UNRESOLVED;
    }
}

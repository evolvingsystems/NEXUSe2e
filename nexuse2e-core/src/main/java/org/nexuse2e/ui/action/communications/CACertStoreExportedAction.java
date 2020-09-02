/**
 * NEXUSe2e Business Messaging Open Source
 * Copyright 2000-2009, Tamgroup and X-ioma GmbH
 * 
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation version 2.1 of
 * the License.
 * 
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
 */
package org.nexuse2e.ui.action.communications;

import org.apache.struts.action.*;
import org.nexuse2e.configuration.CertificateType;
import org.nexuse2e.configuration.EngineConfiguration;
import org.nexuse2e.pojo.CertificatePojo;
import org.nexuse2e.ui.action.NexusE2EAction;
import org.nexuse2e.ui.form.ProtectedFileAccessForm;
import org.nexuse2e.util.CertificateUtil;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileOutputStream;
import java.security.cert.X509Certificate;

/**
 * @author kbarkam
 * 
 */
public class CACertStoreExportedAction extends NexusE2EAction {

    private static String URL     = "cacerts.error.url";
    private static String TIMEOUT = "cacerts.error.timeout";

    /*
     * (non-Javadoc)
     * 
     * @see com.tamgroup.nexus.e2e.ui.action.NexusE2EAction#executeNexusE2EAction(org.apache.struts.action.ActionMapping, org.apache.struts.action.ActionForm,
     * javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse, org.apache.struts.action.ActionMessages)
     */
    @Override
    public ActionForward executeNexusE2EAction(ActionMapping actionMapping, ActionForm actionForm, HttpServletRequest request, HttpServletResponse response,
            EngineConfiguration engineConfiguration, ActionMessages errors, ActionMessages messages) throws Exception {

        ActionForward success = actionMapping.findForward(ACTION_FORWARD_SUCCESS);
        ActionForward error = actionMapping.findForward(ACTION_FORWARD_FAILURE);

        ProtectedFileAccessForm form = (ProtectedFileAccessForm) actionForm;

        int status = form.getStatus();
        int format = form.getFormat();
        int nxCertificateId = form.getNxCertificateId();
        String idStr = form.getId();
        if (status == 1) {
            // Save with path
            String path = form.getCertficatePath();

            if (path == null || path.equals("")) {
                ActionMessage errorMessage = new ActionMessage("generic.error", "no destination specified!");
                errors.add(ActionMessages.GLOBAL_MESSAGE, errorMessage);
                addRedirect(request, URL, TIMEOUT);
                request.setAttribute("seqNo", idStr);
                return error;
            }
            try {
                CertificatePojo cPojo = engineConfiguration.getCertificateByNxCertificateId(CertificateType.CA.getOrdinal(), nxCertificateId);
                if (cPojo == null) {
                    ActionMessage errorMessage = new ActionMessage("generic.error", "no certificate found!");
                    errors.add(ActionMessages.GLOBAL_MESSAGE, errorMessage);
                    addRedirect(request, URL, TIMEOUT);
                    request.setAttribute("seqNo", idStr);
                    return error;
                }

                File destFile = new File(path);
                X509Certificate cert = CertificateUtil.getX509Certificate(cPojo);
                byte[] data = null;
                if (format == ProtectedFileAccessForm.PEM) {
                    data = CertificateUtil.getPemData(cert).getBytes();
                } else if (format == ProtectedFileAccessForm.DER) {
                    data = cert.getEncoded();
                }
                FileOutputStream fos = new FileOutputStream(destFile);
                fos.write(data);
                fos.flush();
                fos.close();
            } catch (Exception e) {
                e.printStackTrace();
                ActionMessage errorMessage = new ActionMessage("generic.error", e.getMessage());
                errors.add(ActionMessages.GLOBAL_MESSAGE, errorMessage);
                addRedirect(request, URL, TIMEOUT);
                request.setAttribute("seqNo", idStr);
                return error;
            }
        } else {
            // Save as...
            String url = getResources(request).getMessage("cacerts.export.certificate.url");
            request.setAttribute("downloadLinkUrl", url);
            return actionMapping.findForward("renderDownloadLink");
        }

        return success;
    }

}

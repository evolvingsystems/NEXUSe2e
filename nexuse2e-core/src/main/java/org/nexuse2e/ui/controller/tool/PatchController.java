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

import java.io.IOException;

import javax.servlet.http.HttpSession;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.nexuse2e.NexusException;
import org.nexuse2e.patch.PatchBundle;
import org.nexuse2e.patch.PatchBundles;
import org.nexuse2e.ui.form.PatchManagementForm;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * Patch upload and execution controller.
 *
 * @author Jonas Reese
 */
@Controller
public class PatchController {
    protected static Logger LOG = Logger.getLogger(PatchController.class);
    
    @RequestMapping("/PatchManagement.do")
    public String patchManagement(
            Model model,
            HttpSession session,
            PatchManagementForm form,
            BindingResult result) throws NexusException {
        
        if (form.getPatchFile() != null && form.getPatchFile() != null && StringUtils.isNotEmpty(form.getPatchFile().getOriginalFilename())) {
            session.setAttribute("patchManagementForm", form);
            // patch uploaded
            PatchBundles bundles = form.getPatchBundles();
            
            try {
                PatchBundle patchBundle = new PatchBundle(form.getPatchFile().getInputStream(), getClass().getClassLoader());
                bundles.addPatchBundle(patchBundle);
            } catch (IOException ioex) {
                LOG.warn("Error extracting patch", ioex);
                result.reject("patch.upload.error.onearg", new Object[] { ioex.getMessage() }, null);
                session.setAttribute("patchManagementForm", null);
            }
        }
        
        return "pages/tools/patch_management";
    }
}

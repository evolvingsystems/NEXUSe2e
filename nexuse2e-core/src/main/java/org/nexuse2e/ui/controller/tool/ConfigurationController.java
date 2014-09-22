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

import java.io.ByteArrayInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

import org.nexuse2e.Engine;
import org.nexuse2e.NexusException;
import org.nexuse2e.pojo.UserPojo;
import org.nexuse2e.ui.form.ConfigurationManagementForm;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * Configuration management controller.
 *
 * @author Jonas Reese
 */
@Controller
public class ConfigurationController {

    @RequestMapping("/ConfigurationManagement.do")
    public String configurationManagement(ConfigurationManagementForm form, BindingResult bindingResult) {
        
        return "pages/tools/configuration_management";
    }
    
    @RequestMapping("/ImportConfiguration.do")
    public String importConfiguration(Model model, UserPojo user, ConfigurationManagementForm form, BindingResult bindingResult)
            throws FileNotFoundException, NexusException, IOException {

        if (form.getPayloadFile() != null && form.getPayloadFile().getSize() > 0) {
            if (user != null) {
                Engine.getInstance().invalidateConfiguration(user.getNxUserId());
            }
            Engine.getInstance().importConfiguration(new ByteArrayInputStream(form.getPayloadFile().getBytes()));

            model.addAttribute("configFileName", form.getPayloadFile().getOriginalFilename());
            
            return "pages/tools/configuration_import_succeeded";
        } else {
            // file upload response cannot contain body, thus redirecting...
            bindingResult.rejectValue("payloadFile", "configuration.import.nofile", "No file uploaded");
            return "pages/tools/configuration_management";
        }
    }
}

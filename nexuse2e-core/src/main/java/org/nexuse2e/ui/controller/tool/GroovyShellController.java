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

import groovy.lang.Binding;
import groovy.lang.GroovyShell;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.io.PrintWriter;

import org.nexuse2e.Engine;
import org.nexuse2e.configuration.EngineConfiguration;
import org.nexuse2e.ui.form.GroovyShellForm;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * Controller for groovy shell tool.
 *
 * @author Jonas Reese
 */
@Controller
public class GroovyShellController {

    @RequestMapping("/GroovyShell.do")
    public String groovyShell(GroovyShellForm form, EngineConfiguration engineConfiguration) {
        if (form.getType().equals("select")) {
            form.setResponse("");
        } else if (form.getType().equals("execute")) {
            String script = form.getScript();
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            Binding binding = new Binding();
            PrintWriter pw = new PrintWriter(baos);
            binding.setProperty("out", pw);
            binding.setProperty("engine", Engine.getInstance());
            binding.setProperty("config", engineConfiguration);
            GroovyShell shell = new GroovyShell(this.getClass().getClassLoader(), binding);
            
            try {
                shell.evaluate(script);
            } catch (Exception e) {
                PrintStream ps = new PrintStream(baos);
                e.printStackTrace(ps);
            }
            pw.close();
            
            form.setResponse(new String(baos.toByteArray()));
        } 

        return "pages/tools/groovy_shell";
    }
}

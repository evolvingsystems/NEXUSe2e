package org.nexuse2e.ui.controller.pipeline;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.TreeSet;

import javax.validation.Valid;

import org.apache.log4j.Logger;
import org.apache.struts.action.ActionMessage;
import org.nexuse2e.Configurable;
import org.nexuse2e.NexusException;
import org.nexuse2e.configuration.ConfigurationUtil;
import org.nexuse2e.configuration.Constants;
import org.nexuse2e.configuration.EngineConfiguration;
import org.nexuse2e.configuration.GenericComparator;
import org.nexuse2e.configuration.PipelineType;
import org.nexuse2e.messaging.Pipelet;
import org.nexuse2e.pojo.ComponentPojo;
import org.nexuse2e.pojo.PipeletPojo;
import org.nexuse2e.pojo.PipelinePojo;
import org.nexuse2e.transport.TransportReceiver;
import org.nexuse2e.ui.form.PipelineForm;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * Spring MVC controller for pipeline configuration related stuff. 
 *
 * @author Jonas Reese
 */
@Controller
public class PipelineController {

    protected static Logger LOG = Logger.getLogger(PipelineController.class);

    
    private List<PipelineForm> pipelines(EngineConfiguration engineConfiguration, boolean backend) {
        List<PipelineForm> pipelines = new ArrayList<PipelineForm>();
        List<PipelinePojo> pipelinePojos = null;
    
        if (backend) {
            pipelinePojos = engineConfiguration.getBackendPipelinePojos(PipelineType.ALL.getOrdinal(), Constants.PIPELINECOMPARATOR);
        } else {
            pipelinePojos = engineConfiguration.getFrontendPipelinePojos(PipelineType.ALL.getOrdinal(), Constants.PIPELINECOMPARATOR);
        }
    
        TreeSet<PipelinePojo> sortedPipelines = new TreeSet<PipelinePojo>(new GenericComparator<PipelinePojo>("name", true));
        sortedPipelines.addAll(pipelinePojos);
    
        if (sortedPipelines != null) {
            for (PipelinePojo pipelinePojo : sortedPipelines) {
                PipelineForm pipelineForm = new PipelineForm();
                pipelineForm.setProperties(pipelinePojo);
                pipelines.add(pipelineForm);
            }
        }
        return pipelines;
    }
    
    @RequestMapping("/BackendPipelines.do")
    public String backendPipelines(Model model, EngineConfiguration engineConfiguration) {
        model.addAttribute("collection", pipelines(engineConfiguration, true));
        model.addAttribute("frontend", false);
        
        return "pages/pipelines/pipelines";
    }

    public String frontendPipelines(Model model, EngineConfiguration engineConfiguration) {
        model.addAttribute("collection", pipelines(engineConfiguration, true));
        model.addAttribute("frontend", true);
        
        return "pages/pipelines/pipelines";
    }
    
    @RequestMapping("/PipelineAdd.do")
    public String pipelineAdd(PipelineForm pipelineForm, EngineConfiguration engineConfiguration) {
        pipelineForm.cleanSettings();
        pipelineForm.setTrps(engineConfiguration.getTrps());
        
        return "pages/pipelines/pipeline_add";
    }
    
    @RequestMapping("/PipelineView.do")
    public String pipelineView(PipelineForm pipelineForm, EngineConfiguration engineConfiguration) throws NexusException {

        PipelinePojo pipeline = engineConfiguration.getPipelinePojoByNxPipelineId(pipelineForm.getNxPipelineId());

        pipelineForm.setProperties(pipeline);
        pipelineForm.setAvailableTemplates(engineConfiguration.getPipelets(pipeline.isFrontend()));
        
        return "pages/pipelines/pipeline_view";
    }
    
    @RequestMapping("/PipelineCreate.do")
    public String pipelineCreate(
            Model model, @Valid PipelineForm pipelineForm, BindingResult bindingResult, EngineConfiguration engineConfiguration)
                    throws NexusException {

        if (bindingResult.hasErrors()) {
            pipelineForm.setAvailableTemplates(engineConfiguration.getPipelets(pipelineForm.isFrontend()));
            return "pages/pipelines/pipeline_add";
        } else {
            PipelinePojo pipeline = new PipelinePojo();
            pipelineForm.getProperties(pipeline, engineConfiguration);
            engineConfiguration.updatePipeline(pipeline);

            if (pipelineForm.isFrontend()) {
                return frontendPipelines(model, engineConfiguration);
            } else {
                return backendPipelines(model, engineConfiguration);
            }
        }
    }

    @RequestMapping("/PipelineUpdate.do")
    public String pipelineUpdate(
            Model model, @Valid PipelineForm pipelineForm, BindingResult bindingResult, EngineConfiguration engineConfiguration)
                    throws NexusException {

        String action = pipelineForm.getSubmitaction();
        pipelineForm.setSubmitaction("");
        PipelinePojo pipeline = engineConfiguration.getPipelinePojoByNxPipelineId(pipelineForm.getNxPipelineId());

        // pipeline not found, do nothing
        if (pipeline == null) {
            if (pipelineForm.isFrontend()) {
                return frontendPipelines(model, engineConfiguration);
            } else {
                return backendPipelines(model, engineConfiguration);
            }
        }
        
        boolean add = "add".equals(action);
        boolean addReturn = "addReturn".equals(action);
        boolean sort = "sort".equals(action);
        boolean sortReturn = "sortReturn".equals(action);

        if (add || addReturn) {
            ComponentPojo component = engineConfiguration.getComponentByNxComponentId(
                    (add ? pipelineForm.getActionNxId() : pipelineForm.getActionNxIdReturn()) );
            if ( component != null ) {
                PipeletPojo pipelet = new PipeletPojo();
                pipelet.setComponent( component );
                pipelet.setCreatedDate( new Date() );
                pipelet.setModifiedDate( new Date() );
                pipelet.setName( component.getName() );
                pipelet.setForward( add );
                pipelet.setDescription( component.getDescription() );
                pipelet.setPipeline( pipeline );
                pipelet.setPosition( pipelineForm.getPipelets().size() + 1 );

                Object newComponent = null;
                try {
                    newComponent = Class.forName( component.getClassName() ).newInstance();
                } catch (ClassNotFoundException | InstantiationException | IllegalAccessException ex) {
                    LOG.error(ex);
                }
                if ( ( newComponent instanceof Pipelet ) || ( newComponent instanceof TransportReceiver ) ) {
                    pipelet.getPipeletParams().addAll(
                            ConfigurationUtil.getConfiguration( (Configurable) newComponent, pipelet ) );
                } else {
                    ActionMessage errorMessage = new ActionMessage( "generic.error",
                            "Referenced Component is no pipelet: " + component.getClassName() );
                }

                pipelineForm.getPipelets().add( pipelet );
            }
        } else if ("delete".equals(action)) {
            int deletePosition = pipelineForm.getSortaction();
            List<PipeletPojo> pipelets = pipelineForm.getForwardPipelets();
            if ( pipelets != null && deletePosition >= 0 && deletePosition < pipelets.size() ) {
                pipelineForm.getPipelets().remove( pipelets.get( deletePosition ) );
            }
        } else if ("deleteReturn".equals(action)) {
            int deletePosition = pipelineForm.getSortaction();
            List<PipeletPojo> pipelets = pipelineForm.getReturnPipelets();
            if ( pipelets != null && deletePosition >= 0 && deletePosition < pipelets.size() ) {
                pipelineForm.getPipelets().remove( pipelets.get( deletePosition ) );
            }
        } else if (sort || sortReturn) {
            int direction = pipelineForm.getSortingDirection();
            int sortaction = pipelineForm.getSortaction();
            List<PipeletPojo> returnPipelets = pipelineForm.getReturnPipelets();
            List<PipeletPojo> forwardPipelets = pipelineForm.getForwardPipelets();
           
            List<PipeletPojo> pipelets = sort ? forwardPipelets : returnPipelets;

            if (pipelets != null && pipelets.size() > 0) {
                // up
                if ( direction == 1 ) {
                    if (sortaction >= 1 && pipelets.size() > sortaction) {
                        PipeletPojo pipelet = pipelets.get( sortaction - 1 );
                        pipelets.set( sortaction - 1, pipelets.get( sortaction  ) );
                        pipelets.set( sortaction, pipelet );
                    }
                }
                // down
                else if ( direction == 2 ) {
                    if (pipelets.size() > sortaction + 1 && sortaction >= 0) {
                        PipeletPojo pipelet = pipelets.get( sortaction );
                        pipelets.set( sortaction, pipelets.get( sortaction + 1 ) );
                        pipelets.set( sortaction + 1, pipelet );
                    }
                }
                List<PipeletPojo> newPipeletList = new ArrayList<PipeletPojo>( forwardPipelets.size() + returnPipelets.size() );
                newPipeletList.addAll( forwardPipelets );
                newPipeletList.addAll( returnPipelets );
                for (int i = 0; i < newPipeletList.size(); i++) {
                    newPipeletList.get( i ).setPosition( i + 1 );
                }
                pipelineForm.setPipelets( newPipeletList );
            }
        } else if ("config".equals(action)) {
            //return actionMapping.findForward( "config" );
            // TODO: forward to config page
        } else if ("configReturn".equals(action)) {
            //return actionMapping.findForward( "configReturn" );
            // TODO: forward to configReturn page
        } else {
            // update pipeline properties only
            if (!bindingResult.hasErrors()) {
                pipelineForm.getProperties(pipeline, engineConfiguration);
                engineConfiguration.updatePipeline(pipeline);
    
                if (pipelineForm.isFrontend()) {
                    return frontendPipelines(model, engineConfiguration);
                } else {
                    return backendPipelines(model, engineConfiguration);
                }
            }
        }
        
        pipelineForm.setAvailableTemplates(engineConfiguration.getPipelets(pipelineForm.isFrontend()));
        return "pages/pipelines/pipeline_view";
    }
}

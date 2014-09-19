package org.nexuse2e.ui.controller.pipeline;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.TreeSet;

import javax.validation.Valid;

import org.apache.log4j.Logger;
import org.nexuse2e.Configurable;
import org.nexuse2e.NexusException;
import org.nexuse2e.configuration.ConfigurationUtil;
import org.nexuse2e.configuration.Constants;
import org.nexuse2e.configuration.EngineConfiguration;
import org.nexuse2e.configuration.EnumerationParameter;
import org.nexuse2e.configuration.GenericComparator;
import org.nexuse2e.configuration.ParameterDescriptor;
import org.nexuse2e.configuration.PipelineType;
import org.nexuse2e.messaging.Pipelet;
import org.nexuse2e.pojo.ComponentPojo;
import org.nexuse2e.pojo.PipeletParamPojo;
import org.nexuse2e.pojo.PipeletPojo;
import org.nexuse2e.pojo.PipelinePojo;
import org.nexuse2e.pojo.ServicePojo;
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

    @RequestMapping("/FrontendPipelines.do")
    public String frontendPipelines(Model model, EngineConfiguration engineConfiguration) {
        model.addAttribute("collection", pipelines(engineConfiguration, false));
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
        boolean delete = "delete".equals(action);
        boolean deleteReturn = "deleteReturn".equals(action);

        if (add || addReturn) {
            ComponentPojo component = engineConfiguration.getComponentByNxComponentId((
                    add ? pipelineForm.getActionNxId() : pipelineForm.getActionNxIdReturn()));
            if (component != null) {
                PipeletPojo pipelet = new PipeletPojo();
                pipelineForm.setProperties(pipeline);
                pipelet.setComponent(component);
                pipelet.setCreatedDate(new Date());
                pipelet.setModifiedDate(new Date());
                pipelet.setName(component.getName());
                pipelet.setForward(add);
                pipelet.setDescription(component.getDescription());
                pipelet.setPipeline(pipeline);
                pipelet.setPosition(pipelineForm.getPipelets().size() + 1);

                Object newComponent = null;
                try {
                    newComponent = Class.forName(component.getClassName()).newInstance();
                } catch (ClassNotFoundException | InstantiationException | IllegalAccessException ex) {
                    LOG.error(ex);
                }
                if (newComponent instanceof Pipelet || newComponent instanceof TransportReceiver) {
                    pipelet.getPipeletParams().addAll(ConfigurationUtil.getConfiguration((Configurable) newComponent, pipelet));
                    pipeline.getPipelets().add(pipelet);
                    engineConfiguration.updatePipeline(pipeline);
                    pipelineForm.setProperties(pipeline);
                } else {
                    bindingResult.rejectValue(add ? "actionNxId" : "actionNxIdReturn", "pipeline.error.component.notapipelet", "Not a pipelet");
                }
            }
        } else if (delete || deleteReturn) {
            int deletePosition = pipelineForm.getSortaction();
            pipelineForm.setProperties(pipeline);
            List<PipeletPojo> pipelets = (delete ? pipelineForm.getForwardPipelets() : pipelineForm.getReturnPipelets());
            if (pipelets != null && pipeline.getPipelets() != null && deletePosition >= 0 && deletePosition < pipelets.size()) {
                pipeline.getPipelets().remove(pipelets.get(deletePosition));
            }
            engineConfiguration.updatePipeline(pipeline);
            pipelineForm.setProperties(pipeline);
        } else if (sort || sortReturn) {
            int direction = pipelineForm.getSortingDirection();
            int sortaction = pipelineForm.getSortaction();
            pipelineForm.setProperties(pipeline);
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
                pipeline.setPipelets(newPipeletList);
                engineConfiguration.updatePipeline(pipeline);
                pipelineForm.setProperties(pipeline);
            }
        } else if ("config".equals(action)) {
            return "redirect:/PipeletParamsView.do?nxPipelineId=" + pipelineForm.getNxPipelineId() + "&nxPipeletId=" + pipelineForm.getNxPipeletId();
        } else if ("configReturn".equals(action)) {
            return "redirect://ReturnPipeletParamsView.do?nxPipelineId=" + pipelineForm.getNxPipelineId() + "&nxPipeletId=" + pipelineForm.getNxPipeletId();
        } else {
            pipelineForm.setPipeletsFromPipeline(pipeline);

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

    private PipeletPojo findPipelet(PipelinePojo pipeline, int nxPipeletId) {
        if (pipeline != null && pipeline.getPipelets() != null) {
            for (PipeletPojo pipelet : pipeline.getPipelets()) {
                if (pipelet.getNxPipeletId() == nxPipeletId) {
                    return pipelet;
                }
            }
        }
        return null;
    }
    
    private String pipeletParamsView(
            Model model, PipelineForm pipelineForm, EngineConfiguration engineConfiguration, boolean returnPipeline)
                    throws NexusException {
        
        PipelinePojo pipeline = engineConfiguration.getPipelinePojoByNxPipelineId(pipelineForm.getNxPipelineId());
        if (pipeline != null) {
            PipeletPojo pipeletPojo = findPipelet(pipeline, pipelineForm.getNxPipeletId());
            pipelineForm.setProperties(pipeline);

            if (pipeletPojo != null) {
                pipelineForm.getObsoleteParameters().clear();
            }
            List<ServicePojo> services = engineConfiguration.getServices();
            List<ServicePojo> sortedServices = new ArrayList<ServicePojo>(services.size());
            sortedServices.addAll(engineConfiguration.getServices());
            Collections.sort(sortedServices, new GenericComparator<ServicePojo>("name", true));
            initPipeletParameters(pipeletPojo, pipelineForm);
            model.addAttribute("collection", sortedServices);
        }
        return "pages/pipelines/pipelet_params_view";
    }

    @RequestMapping("/PipeletParamsView.do")
    public String pipeletParamsView(
            Model model, PipelineForm pipelineForm, EngineConfiguration engineConfiguration)
                    throws NexusException {
        return pipeletParamsView(model, pipelineForm, engineConfiguration, false);
    }
    
    @RequestMapping("/ReturnPipeletParamsView.do")
    public String returnPipeletParamsView(
            Model model, PipelineForm pipelineForm, EngineConfiguration engineConfiguration)
                    throws NexusException {
        return pipeletParamsView(model, pipelineForm, engineConfiguration, true);
    }

    private Configurable getConfigurable(PipeletPojo pipeletPojo) {
        try {
            Object componentInst = Class.forName(pipeletPojo.getComponent().getClassName()).newInstance();
            if (componentInst instanceof Configurable) {
                Configurable configurable = (Configurable) componentInst;
                ConfigurationUtil.configurePipelet(configurable, pipeletPojo.getPipeletParams());
                return configurable;
            }
        } catch (InstantiationException | IllegalAccessException | ClassNotFoundException ex) {
            LOG.error(ex);
        }
        return null;
    }
    
    private Configurable initPipeletParameters(PipeletPojo pipeletPojo, PipelineForm pipelineForm) {
        Configurable configurable = getConfigurable(pipeletPojo);
        if (configurable != null) {
            ConfigurationUtil.configurePipelet(configurable, pipeletPojo.getPipeletParams());
            pipelineForm.setParameters(ConfigurationUtil.getConfiguration(configurable, pipeletPojo));
            pipelineForm.createParameterMapFromPojos();
        }
        
        return configurable;
    }

    @RequestMapping("/PipeletParamsUpdate.do")
    public String pipeletParamsUpdate(
            Model model, PipelineForm pipelineForm, EngineConfiguration engineConfiguration)
                    throws NexusException {

        List<ServicePojo> services = engineConfiguration.getServices();
        List<ServicePojo> sortedServices = new ArrayList<ServicePojo>(services.size());
        sortedServices.addAll(engineConfiguration.getServices());
        Collections.sort(sortedServices, new GenericComparator<ServicePojo>("name", true));
        model.addAttribute("collection", sortedServices);

        PipelinePojo pipelinePojo = engineConfiguration.getPipelinePojoByNxPipelineId(
                pipelineForm.getNxPipelineId());
        PipeletPojo pipeletPojo = findPipelet(pipelinePojo, pipelineForm.getNxPipeletId());
        
        String action = pipelineForm.getSubmitaction();

        LOG.trace( "submitaction: " + action );

        if (pipeletPojo != null) {
            if ("add".equals(action)) {
                String paramName = pipelineForm.getParamName();
                LOG.trace( "paramName: " + paramName );
                int maxsqn = 0;
                PipeletParamPojo headerLine = null;
                for (PipeletParamPojo param : pipelineForm.getParameters()) {
                    if ( param.getParamName().equals( paramName ) ) {
                        if ( param.getLabel() == null ) {
                            headerLine = param;
                        } else {
                            maxsqn = param.getSequenceNumber();
                        }
                    }
                }
    
                boolean alreadyIn;
                if (pipelineForm.getKey() != null) {
                    alreadyIn = false;
                    for (PipeletParamPojo param : pipelineForm.getParameters()) {
                        if (pipelineForm.getKey().equals( param.getLabel() )) {
                            alreadyIn = true;
                            break;
                        }
                    }
                } else {
                    alreadyIn = true;
                }
                Configurable configurable = initPipeletParameters(pipeletPojo, pipelineForm);
                if (headerLine != null && !alreadyIn && configurable != null) {
                    PipeletParamPojo newParam = new PipeletParamPojo();
                    newParam.setParamName(headerLine.getParamName());
                    newParam.setModifiedDate(new Date());
                    newParam.setCreatedDate(new Date());
                    newParam.setPipelet(headerLine.getPipelet());
                    newParam.setLabel(pipelineForm.getKey());
                    newParam.setValue(pipelineForm.getValue());
                    ParameterDescriptor pd = headerLine.getParameterDescriptor();
                    EnumerationParameter enumeration = configurable.getParameter(headerLine.getParamName());
                    if (enumeration == null) {
                        enumeration = pd.getDefaultValue();
                        configurable.setParameter(headerLine.getParamName(), enumeration);
                    }
                    enumeration.putElement(pipelineForm.getKey(), pipelineForm.getValue());
                    newParam.setSequenceNumber(maxsqn + 1);
                    newParam.setParameterDescriptor(pd);
                    pipelineForm.getParameters().add(pipelineForm.getParameters().size() - 1, newParam);
                }
                pipelineForm.setKey("");
                pipelineForm.setValue("");
    
                return "pages/pipelines/pipelet_params_view";
            } else if ("delete".equals(action)) {
                Configurable configurable = initPipeletParameters(pipeletPojo, pipelineForm);
                if (configurable != null) {
                    pipelineForm.fillPojosFromParameterMap(configurable);
                    String paramName = pipelineForm.getParamName();
                    LOG.trace( "paramName: " + paramName );
                    int sqn = pipelineForm.getActionNxId();
                    LOG.trace( "sqn: " + sqn );
        
                    PipeletParamPojo obsoleteParam = null;
                    for (PipeletParamPojo param : pipelineForm.getParameters()) {
                        if (param.getParamName().equals(paramName) && param.getSequenceNumber() == sqn) {
                            obsoleteParam = param;
                        }
                        if (param.getParamName().equals(paramName) && param.getSequenceNumber() > sqn) {
                            param.setSequenceNumber(param.getSequenceNumber() - 1);
                        }
                    }
                    if (obsoleteParam != null) {
                        pipelineForm.getParameters().remove(obsoleteParam);
                        EnumerationParameter enumeration = configurable.getParameter(obsoleteParam.getParamName());
                        if (enumeration != null) {
                            enumeration.removeElement(obsoleteParam.getLabel());
                        }
                        if (obsoleteParam.getNxPipeletParamId() != 0) {
                            pipelineForm.getObsoleteParameters().add(obsoleteParam);
                        }
                    }
                }
                return "pages/pipelines/pipelet_params_view";
            } else if ("update".equals(action)) {
                Configurable configurable = getConfigurable(pipeletPojo);
                if (configurable != null) {
                    pipelineForm.setParameters(ConfigurationUtil.getConfiguration(configurable, pipeletPojo));
    
                    pipelineForm.fillPojosFromParameterMap(configurable);
                    initPipeletParameters(pipeletPojo, pipelineForm);
                    for (PipeletParamPojo param : pipelineForm.getParameters()) {
                        if (param.getLabel() != null && param.getValue() != null && param.getNxPipeletParamId() == 0) {
                            param.setPipelet(pipeletPojo);
                            pipeletPojo.getPipeletParams().add(param);
                        }
                    }
                    for (PipeletParamPojo param : pipelineForm.getObsoleteParameters()) {
                        pipeletPojo.getPipeletParams().remove(param);
                        param.setPipelet(null);
                    }
    
                    if (pipeletPojo.getNxPipeletId() != 0) {
                        engineConfiguration.updatePipeline(pipelinePojo);
    
                        // update form
                        pipelineForm.setProperties(engineConfiguration.getPipelinePojoByNxPipelineId(
                                pipelineForm.getNxPipelineId()));
                    }
                }
            }
        }

        return "redirect:/PipelineView.do?nxPipelineId=" + pipelineForm.getNxPipelineId();
    }
}

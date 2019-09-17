package com.example.demo.controller.modeler.editor.create;

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.activiti.bpmn.converter.BpmnXMLConverter;
import org.activiti.bpmn.model.BpmnModel;
import org.activiti.editor.language.json.converter.BpmnJsonConverter;
import org.activiti.engine.RepositoryService;
import org.activiti.engine.repository.Deployment;
import org.activiti.engine.repository.Model;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import com.example.demo.service.ModelerService;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

@Controller
@RequestMapping("/modeler")
public class ModelerController {

	private static final Logger logger = LoggerFactory.getLogger(ModelerController.class);

	@Autowired
	private ModelerService modelerService;

	@Autowired

	private RepositoryService repositoryService;

	/**
	 * 创建流程模型
	 * 
	 * @param name
	 * @param key
	 * @param description
	 * @param request
	 * @param response
	 * @return
	 */
	@RequestMapping(value = "/create", method = RequestMethod.GET)
	public String createModel(@RequestParam("name") String name, @RequestParam("key") String key,
			@RequestParam("description") String description, HttpServletRequest request, HttpServletResponse response) {
		logger.info("创建空modeler：name:{},key:{},description:{}", name, key, description);
		try {
			// 创建空模型
			String modelId = modelerService.crateModel(name, key, description);
			if (StringUtils.isBlank(modelId)) {
				throw new RuntimeException("创建modeler失败modelId:" + modelId);
			}

			return "redirect:../modeler.html?modelId=" + modelId;
		} catch (Exception e) {
			logger.error("创建模型失败", e);
			throw new RuntimeException(e);
		}
	}

	/**
	 * 模型列表
	 * 
	 * @param modelAndView
	 * @return
	 */
	@RequestMapping("/model/list")
	public ModelAndView modelList(ModelAndView modelAndView) {
		List<Model> list = modelerService.queryModelList();
		return modelAndView;
	}

	/**
	 * 根据modelId部署模型
	 * 
	 * @param modelId
	 * @param response
	 * @param request
	 * @return
	 */
	@RequestMapping("/startModel")  
	@ResponseBody
	public Deployment startModel(@RequestParam("id") String id) {  
	    System.out.println("id=" + id);  
	    Deployment deployment = null;  
	    try {  
	        Model modelData = repositoryService.getModel(id);  
	        ObjectNode modelNode = (ObjectNode) new ObjectMapper().readTree(  
	                repositoryService.getModelEditorSource(modelData.getId())  
	        );  
	  
	        byte[] bpmnBytes = null;  
	  
	        BpmnModel model = new BpmnJsonConverter().convertToBpmnModel(modelNode);  
	        bpmnBytes = new BpmnXMLConverter().convertToXML(model);  
	  
	        String processName = modelData.getName() + ".bpmn20.xml";  
	  
	        deployment = repositoryService.createDeployment()  
	                .name(modelData.getName())  
	                .addString(processName, new String(bpmnBytes))  
	                .deploy();  
	    } catch (Exception e) {  
	        e.printStackTrace();  
	    }  
	    return deployment;  
	} 

}

package com.example.demo;

import org.activiti.bpmn.converter.BpmnXMLConverter;
import org.activiti.bpmn.model.BpmnModel;
import org.activiti.engine.RepositoryService;
import org.activiti.engine.repository.ProcessDefinition;
import org.activiti.engine.test.Deployment;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest
public class BpmnTest {
	@Autowired
	RepositoryService repositoryService;

	@Test
	@Deployment(resources = "resource/Myprocess.bpmn")
	public void testXmltoBpmn() {
		ProcessDefinition procDef = repositoryService.createProcessDefinitionQuery().processDefinitionId("leaveProcess:2:12508")
				.singleResult();
		BpmnModel bpmnModel = repositoryService.getBpmnModel(procDef.getId());
		// 创建转换对象
		BpmnXMLConverter converter = new BpmnXMLConverter();
		// 把bpmnModel对象转换成字符
		byte[] bytes = converter.convertToXML(bpmnModel);
		String xmlContenxt = bytes.toString();
		System.out.println(xmlContenxt.toString());

	}

}

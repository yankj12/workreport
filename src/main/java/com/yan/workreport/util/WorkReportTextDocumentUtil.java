package com.yan.workreport.util;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import org.bson.Document;
import org.bson.types.ObjectId;

import com.yan.workreport.model.WorkReportText;

public class WorkReportTextDocumentUtil {

	public static Document workReportTextToDocument(WorkReportText workReportText){
		Document doc = new Document();
		
		Class clazz = WorkReportText.class;
		Method[] methods = clazz.getDeclaredMethods();
		if(methods != null && methods.length > 0){
			for(Method method : methods){
				String methodName = method.getName();
				//遍历get方法
				if(methodName.startsWith("get")){
					String fieldName = methodName.substring(3, 4).toLowerCase() + methodName.substring(4);
					Class returnType = method.getReturnType();
					Object value = null;
					try {
						value = method.invoke(workReportText, new Object[0]);
					} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
						e.printStackTrace();
					}
					
					if(value != null){
						if("id".equals(fieldName)){
							if(!"".equals(value.toString().trim())){
								doc.append("_id", new ObjectId(value.toString()));
							}
						}else{
							doc.append(fieldName, value);
						}
					}
					
				}
				
			}
		}
		
		return doc;
	}
	
	public static WorkReportText documentToWorkReportText(Document document){
		WorkReportText workReportText = null;
		if(document != null){
			workReportText = new WorkReportText();
			
			Class clazz = WorkReportText.class;
			Method[] methods = clazz.getDeclaredMethods();
			if(methods != null && methods.length > 0){
				for(Method method : methods){
					String methodName = method.getName();
					//遍历get方法
					if(methodName.startsWith("get")){
						String fieldName = methodName.substring(3, 4).toLowerCase() + methodName.substring(4);
						Class returnType = method.getReturnType();
						
						String setterMethodName = "set" + methodName.substring(3);
						
						try {
							Method setterMethod = clazz.getMethod(setterMethodName, returnType);
							
							Object value = null;
							
							if("id".equals(fieldName)){
								value = document.get("_id");
								value = value.toString();
							}else{
								value = document.get(fieldName);
							}
							
							setterMethod.invoke(workReportText, value);
							
						} catch (Exception e) {
							e.printStackTrace();
						}
						
					}
					
				}
			}
		}
		
		return workReportText;
	}
}

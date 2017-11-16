package com.yan.workreport.util;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.bson.Document;

import com.mongodb.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Filters;
import com.yan.workreport.model.WorkReportText;

public class WorkReportTextMongoDaoUtil {

	public static void main(String[] args) throws Exception {
		WorkReportTextMongoDaoUtil workReportTextMongoDaoUtil = new WorkReportTextMongoDaoUtil();
		List<WorkReportText> workReportTexts = workReportTextMongoDaoUtil.findWorkReportTextDocumentsByUserNameOrPhoneOrEmail("马晶晶", "18330215673", "295605043@qq.com");
		WorkReportText workReportText = workReportTexts.get(0);
		workReportText.setUpdateTime(new Date());
		workReportTextMongoDaoUtil.updateWorkReportText(workReportText);
	}
	
	public void insertWorkReportText(WorkReportText workReportText){

		//To connect to a single MongoDB instance:
	    //You can explicitly specify the hostname and the port:
		MongoClient mongoClient = new MongoClient( "localhost" , 27017 );

		//Access a Database
		MongoDatabase database = mongoClient.getDatabase("manage");
		
		//Access a Collection
		MongoCollection<Document> collection = database.getCollection("WorkReportText");
		
		//Create a Document
		Document doc = WorkReportTextDocumentUtil.workReportTextToDocument(workReportText);

		//Insert a Document
		collection.insertOne(doc);
	}
	
	public List<WorkReportText> findWorkReportTextDocumentsByUserNameOrPhoneOrEmail(String userName, String phone, String email){
		//To connect to a single MongoDB instance:
	    //You can explicitly specify the hostname and the port:
		MongoClient mongoClient = new MongoClient( "localhost" , 27017 );

		//Access a Database
		MongoDatabase database = mongoClient.getDatabase("manage");
		
		//Access a Collection
		MongoCollection<Document> collection = database.getCollection("WorkReportText");
		
		List<Document> docs = collection.find(Filters.or(Filters.eq("userName", userName), Filters.eq("phone", phone), Filters.eq("email", email))).into(new ArrayList<Document>());
		
		List<WorkReportText> WorkReportTexts = null;
		if(docs != null){
			WorkReportTexts = new ArrayList<WorkReportText>();
					
			for(Document doc : docs){
				WorkReportText WorkReportText = new WorkReportText();
				//将document转换为WorkReportText
				//System.out.println(doc.get("_id"));
				WorkReportText = WorkReportTextDocumentUtil.documentToWorkReportText(doc);
				
				WorkReportTexts.add(WorkReportText);
			}
		}
		
		return WorkReportTexts;
	}
	
	
	public void insertWorkReportTextList(List<WorkReportText> WorkReportTexts){
		
	}
	
	public void updateWorkReportText(WorkReportText workReportText){
		//To connect to a single MongoDB instance:
	    //You can explicitly specify the hostname and the port:
		MongoClient mongoClient = new MongoClient( "localhost" , 27017 );

		//Access a Database
		MongoDatabase database = mongoClient.getDatabase("manage");
		
		//Access a Collection
		MongoCollection<Document> collection = database.getCollection("WorkReportText");
		
		
		//Create a Document
		 Document doc = WorkReportTextDocumentUtil.workReportTextToDocument(workReportText);
		 
		 //Update a Document
		 collection.updateOne(Filters.eq("_id", doc.get("_id")), new Document("$set", doc));
		 
	}
}

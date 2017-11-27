package com.yan.access.dao;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.bson.Document;
import org.bson.conversions.Bson;
import org.bson.types.ObjectId;

import com.mongodb.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Filters;
import com.yan.access.model.Menu;
import com.yan.workreport.util.SchameDocumentUtil;

public class MenuMongoDaoUtil {
	
	public String insertMenu(Menu menu){

		//To connect to a single MongoDB instance:
	    //You can explicitly specify the hostname and the port:
		MongoClient mongoClient = new MongoClient( "localhost" , 27017 );

		//Access a Database
		MongoDatabase database = mongoClient.getDatabase("manage");
		
		//Access a Collection
		MongoCollection<Document> collection = database.getCollection("Menu");
		
		//Create a Document
		Document doc = SchameDocumentUtil.schameToDocument(menu, Menu.class);

		//Insert a Document
		collection.insertOne(doc);
		
		//System.out.println("id:" + doc.get("_id"));
		String id = null;
		if(doc.get("_id") != null){
			id = doc.get("_id").toString();
		}
		return id;
	}
	
	public List<Menu> findMenuDocumentsByCondition(Map<String, Object> condition){
		List<Menu> menus = null;
		
		if(condition != null && condition.size() > 0) {
			
			//To connect to a single MongoDB instance:
			//You can explicitly specify the hostname and the port:
			MongoClient mongoClient = new MongoClient( "localhost" , 27017 );
			
			//Access a Database
			MongoDatabase database = mongoClient.getDatabase("manage");
			
			//Access a Collection
			MongoCollection<Document> collection = database.getCollection("Menu");
			
			List<Bson> bsons = new ArrayList<Bson>(0);
			
			//分页的页码
			int page = 1;
			//分页每页条数
			int rows = 10;
			
			String sort = "_id";
			for(Iterator<Entry<String, Object>> iterator = condition.entrySet().iterator();iterator.hasNext();) {
				Entry<String, Object> entry = iterator.next();
				String key = entry.getKey();
				Object value = entry.getValue();
				
				//因为查询条件改为了and，所以当条件为空字符串的时候不向查询条件中拼写
				if(value != null && !"".equals(value.toString().trim())){
					if("id".equals(key)) {
						bsons.add(Filters.eq("_id", new ObjectId(value.toString())));
					}else if ("page".equals(key)) {
						page = Integer.parseInt(value.toString());
					}else if ("rows".equals(key)) {
						rows = Integer.parseInt(value.toString());
					}else if ("sort".equalsIgnoreCase(key) || "orderBy".equalsIgnoreCase(key)) {
						sort = value.toString();
					}else {
						bsons.add(Filters.eq(key, value.toString()));
					}
				}
				
			}
			
			int limit = rows;
			int skip = 0;
			if(page >= 0){
				skip = (page - 1) * rows;
			}
			
			//如果要在find中传入bson数组，那么bson数组必须不能为空
			List<Document> docs = null;
			if(bsons != null && bsons.size() > 0){
				docs = collection.find(Filters.and(bsons)).limit(limit).skip(skip).sort(new Document(sort, -1)).into(new ArrayList<Document>());
			}else{
				docs = collection.find().limit(limit).skip(skip).sort(new Document(sort, -1)).into(new ArrayList<Document>());
			}
			
			if(docs != null){
				menus = new ArrayList<Menu>();
				
				for(Document doc : docs){
					Menu menu = new Menu();
					//将document转换为interview
					//System.out.println(doc.get("_id"));
					menu = (Menu)SchameDocumentUtil.documentToSchame(doc, Menu.class);
					
					menus.add(menu);
				}
			}
		}
		
		return menus;
	}
	
	public Long countMenuVoDocumentsByCondition(Map<String, Object> condition){
		long count = 0L;
		
		if(condition != null && condition.size() > 0) {
			
			//To connect to a single MongoDB instance:
			//You can explicitly specify the hostname and the port:
			MongoClient mongoClient = new MongoClient( "localhost" , 27017 );
			
			//Access a Database
			MongoDatabase database = mongoClient.getDatabase("manage");
			
			//Access a Collection
			MongoCollection<Document> collection = database.getCollection("Menu");
			
			List<Bson> bsons = new ArrayList<Bson>(0);
			
			for(Iterator<Entry<String, Object>> iterator = condition.entrySet().iterator();iterator.hasNext();) {
				Entry<String, Object> entry = iterator.next();
				String key = entry.getKey();
				Object value = entry.getValue();
				
				//因为查询条件改为了and，所以当条件为空字符串的时候不向查询条件中拼写
				if(value != null && !"".equals(value.toString().trim())){
					if("id".equals(key)) {
						bsons.add(Filters.eq("_id", new ObjectId(value.toString())));
					}else if ("page".equals(key) || "rows".equals(key)) {
						//这两个参数是分页参数，在分页查询数据时会用到，但是在查询总条数的时候并不会用到，但是也不能拼接到查询语句中
					}else if ("sort".equalsIgnoreCase(key) || "orderBy".equalsIgnoreCase(key)) {
						
					}else {
						bsons.add(Filters.eq(key, value.toString()));
					}
				}
				
			}
			
			//如果要在find中传入bson数组，那么bson数组必须不能为空
			if(bsons != null && bsons.size() > 0){
				count = collection.count(Filters.and(bsons));
			}else{
				count = collection.count();
			}
			
		}
		
		return count;
	}
	
	public Menu findMenuById(String id) {
		Menu menu = null;
		if(id!= null && !"".equals(id.trim())) {
			//To connect to a single MongoDB instance:
			//You can explicitly specify the hostname and the port:
			MongoClient mongoClient = new MongoClient( "localhost" , 27017 );
			
			//Access a Database
			MongoDatabase database = mongoClient.getDatabase("manage");
			
			//Access a Collection
			MongoCollection<Document> collection = database.getCollection("Menu");
			
			List<Document> docs = collection.find(Filters.eq("_id", new ObjectId(id))).into(new ArrayList<Document>());
			if(docs != null && docs.size() > 0) {
				menu = (Menu)SchameDocumentUtil.documentToSchame(docs.get(0), Menu.class);
			}
		}
		
		return menu;
	}
	
	public void updateMenuValidStatus(String id, String validStatus){
		//To connect to a single MongoDB instance:
	    //You can explicitly specify the hostname and the port:
		MongoClient mongoClient = new MongoClient( "localhost" , 27017 );

		//Access a Database
		MongoDatabase database = mongoClient.getDatabase("manage");
		
		//Access a Collection
		MongoCollection<Document> collection = database.getCollection("Menu");
		
		
		//Create a Document
		 Document doc = new Document("validStatus", validStatus);
		 
		 //Update a Document
		 collection.updateOne(Filters.eq("_id", new ObjectId(id)), new Document("$set", doc));
		 
	}
	
	public void updateMenu(Menu menu){
		//To connect to a single MongoDB instance:
	    //You can explicitly specify the hostname and the port:
		MongoClient mongoClient = new MongoClient( "localhost" , 27017 );

		//Access a Database
		MongoDatabase database = mongoClient.getDatabase("manage");
		
		//Access a Collection
		MongoCollection<Document> collection = database.getCollection("Menu");
		
		
		//Create a Document
		 Document doc = SchameDocumentUtil.schameToDocument(menu, Menu.class);
		 
		 //Update a Document
		 collection.updateOne(Filters.eq("_id", doc.get("_id")), new Document("$set", doc));
		 
	}
	
}

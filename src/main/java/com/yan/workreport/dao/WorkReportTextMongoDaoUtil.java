package com.yan.workreport.dao;

import java.util.ArrayList;
import java.util.Date;
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
import com.yan.workreport.model.WorkReportText;
import com.yan.workreport.util.SchameDocumentUtil;

public class WorkReportTextMongoDaoUtil {

	public static void main(String[] args) throws Exception {
		WorkReportTextMongoDaoUtil workReportTextMongoDaoUtil = new WorkReportTextMongoDaoUtil();
		WorkReportText workReportText = new WorkReportText();
		workReportText.setDay("20171018");
		workReportText.setProjectCode("test");
		workReportText.setProjectName("测试");
		workReportText.setWriterName("张三");
		workReportText.setTitle("20171018工作日志");
		workReportText.setWorkText("工作日志的具体内容如下：\\n1、.....\\n2、...\\n3、...");
		workReportText.setInsertTime(new Date());
		workReportText.setUpdateTime(new Date());
		workReportTextMongoDaoUtil.insertWorkReportText(workReportText);
	}
	
	public String insertWorkReportText(WorkReportText workReportText){

		//To connect to a single MongoDB instance:
	    //You can explicitly specify the hostname and the port:
		MongoClient mongoClient = new MongoClient( "localhost" , 27017 );

		//Access a Database
		MongoDatabase database = mongoClient.getDatabase("manage");
		
		//Access a Collection
		MongoCollection<Document> collection = database.getCollection("WorkReportText");
		
		//Create a Document
		Document doc = SchameDocumentUtil.schameToDocument(workReportText, WorkReportText.class);

		//Insert a Document
		collection.insertOne(doc);
		
		//System.out.println("id:" + doc.get("_id"));
		String id = null;
		if(doc.get("_id") != null){
			id = doc.get("_id").toString();
		}
		return id;
	}
	
	public List<WorkReportText> findWorkReportTextDocumentsByCondition(Map<String, Object> condition){
		List<WorkReportText> workReportTexts = null;
		
		if(condition != null && condition.size() > 0) {
			
			//To connect to a single MongoDB instance:
			//You can explicitly specify the hostname and the port:
			MongoClient mongoClient = new MongoClient( "localhost" , 27017 );
			
			//Access a Database
			MongoDatabase database = mongoClient.getDatabase("manage");
			
			//Access a Collection
			MongoCollection<Document> collection = database.getCollection("WorkReportText");
			
			List<Bson> bsons = new ArrayList<Bson>(0);
			
			//分页的页码
			int page = 1;
			//分页每页条数
			int rows = 10;
			
			for(Iterator<Entry<String, Object>> iterator = condition.entrySet().iterator();iterator.hasNext();) {
				Entry<String, Object> entry = iterator.next();
				String key = entry.getKey();
				Object value = entry.getValue();
				
				//因为查询条件改为了and，所以当条件为空字符串的时候不向查询条件中拼写
				if(value != null && !"".equals(value.toString().trim())){
					if("id".equals(key)) {
						bsons.add(Filters.eq("_id", new ObjectId(value.toString())));
					}else if ("startDay".equals(key)) {
						bsons.add(Filters.gte("day", value.toString()));
					}else if ("endDay".equals(key)) {
						bsons.add(Filters.lte("day", value.toString()));
					}else if ("writerName".equals(key) || "projectCode".equals(key) || "type".equals(key)) {
						bsons.add(Filters.eq(key, value.toString()));
					}else if ("page".equals(key)) {
						page = Integer.parseInt(value.toString());
					}else if ("rows".equals(key)) {
						rows = Integer.parseInt(value.toString());
					}else {
						//其他的进行右模糊查询
						//参考下regex的使用
						//i  　如果设置了这个修饰符，模式中的字母会进行大小写不敏感匹配。
						//m   默认情况下，PCRE 认为目标字符串是由单行字符组成的(然而实际上它可能会包含多行).如果目标字符串 中没有 "\n"字符，或者模式中没有出现“行首”/“行末”字符，设置这个修饰符不产生任何影响。
						//s    如果设置了这个修饰符，模式中的点号元字符匹配所有字符，包含换行符。如果没有这个修饰符，点号不匹配换行符。
						//x    如果设置了这个修饰符，模式中的没有经过转义的或不在字符类中的空白数据字符总会被忽略，并且位于一个未转义的字符类外部的#字符和下一个换行符之间的字符也被忽略。 这个修饰符使被编译模式中可以包含注释。 注意：这仅用于数据字符。 空白字符 还是不能在模式的特殊字符序列中出现，比如序列 。
						//注：JavaScript只提供了i和m选项，x和s选项必须使用$regex操作符
						
						//在命令行的时候pattern左右使用//包起来，是因为通过//来表示包起来的是pattern，但是如果java中再将//拼接到字符串中，那么//就会当做pattern的一部分去匹配，就会出现问题
						//debug的时候，发现pattern中居然包括//这是不对的
						bsons.add(Filters.regex(key, "" + value.toString() + ".*", "i"));
					}
				}
				
			}
			
			int limit = rows;
			int skip = 0;
			if(page >= 0){
				skip = (page - 1) * rows;
			}
			
			List<Document> docs = collection.find(Filters.and(bsons)).limit(limit).skip(skip).sort(new Document("day", -1)).into(new ArrayList<Document>());
			
			if(docs != null){
				workReportTexts = new ArrayList<WorkReportText>();
				
				for(Document doc : docs){
					WorkReportText workReportText = new WorkReportText();
					//将document转换为interview
					//System.out.println(doc.get("_id"));
					workReportText = (WorkReportText)SchameDocumentUtil.documentToSchame(doc, WorkReportText.class);
					
					workReportTexts.add(workReportText);
				}
			}
		}
		
		return workReportTexts;
	}
	
	public Long countWorkReportTextVoDocumentsByCondition(Map<String, Object> condition){
		long count = 0L;
		
		if(condition != null && condition.size() > 0) {
			
			//To connect to a single MongoDB instance:
			//You can explicitly specify the hostname and the port:
			MongoClient mongoClient = new MongoClient( "localhost" , 27017 );
			
			//Access a Database
			MongoDatabase database = mongoClient.getDatabase("manage");
			
			//Access a Collection
			MongoCollection<Document> collection = database.getCollection("WorkReportText");
			
			List<Bson> bsons = new ArrayList<Bson>(0);
			
			for(Iterator<Entry<String, Object>> iterator = condition.entrySet().iterator();iterator.hasNext();) {
				Entry<String, Object> entry = iterator.next();
				String key = entry.getKey();
				Object value = entry.getValue();
				
				//因为查询条件改为了and，所以当条件为空字符串的时候不向查询条件中拼写
				if(value != null && !"".equals(value.toString().trim())){
					if("id".equals(key)) {
						bsons.add(Filters.eq("_id", new ObjectId(value.toString())));
					}else if ("startDay".equals(key)) {
						bsons.add(Filters.gte("day", value.toString()));
					}else if ("endDay".equals(key)) {
						bsons.add(Filters.lte("day", value.toString()));
					}else if ("writerName".equals(key) || "projectCode".equals(key) || "type".equals(key)) {
						bsons.add(Filters.eq(key, value.toString()));
					}else if ("page".equals(key) || "rows".equals(key)) {
						//这两个参数是分页参数，在分页查询数据时会用到，但是在查询总条数的时候并不会用到，但是也不能拼接到查询语句中
					}else {
						//其他的进行右模糊查询
						//参考下regex的使用
						//i  　如果设置了这个修饰符，模式中的字母会进行大小写不敏感匹配。
						//m   默认情况下，PCRE 认为目标字符串是由单行字符组成的(然而实际上它可能会包含多行).如果目标字符串 中没有 "\n"字符，或者模式中没有出现“行首”/“行末”字符，设置这个修饰符不产生任何影响。
						//s    如果设置了这个修饰符，模式中的点号元字符匹配所有字符，包含换行符。如果没有这个修饰符，点号不匹配换行符。
						//x    如果设置了这个修饰符，模式中的没有经过转义的或不在字符类中的空白数据字符总会被忽略，并且位于一个未转义的字符类外部的#字符和下一个换行符之间的字符也被忽略。 这个修饰符使被编译模式中可以包含注释。 注意：这仅用于数据字符。 空白字符 还是不能在模式的特殊字符序列中出现，比如序列 。
						//注：JavaScript只提供了i和m选项，x和s选项必须使用$regex操作符
						
						//在命令行的时候pattern左右使用//包起来，是因为通过//来表示包起来的是pattern，但是如果java中再将//拼接到字符串中，那么//就会当做pattern的一部分去匹配，就会出现问题
						//debug的时候，发现pattern中居然包括//这是不对的
						bsons.add(Filters.regex(key, "" + value.toString() + ".*", "i"));
					}
				}
				
			}
			
			count = collection.count(Filters.and(bsons));
			
		}
		
		return count;
	}
	
	public WorkReportText findWorkReportById(String id) {
		WorkReportText workReportText = null;
		if(id!= null && !"".equals(id.trim())) {
			//To connect to a single MongoDB instance:
			//You can explicitly specify the hostname and the port:
			MongoClient mongoClient = new MongoClient( "localhost" , 27017 );
			
			//Access a Database
			MongoDatabase database = mongoClient.getDatabase("manage");
			
			//Access a Collection
			MongoCollection<Document> collection = database.getCollection("WorkReportText");
			
			List<Document> docs = collection.find(Filters.eq("_id", new ObjectId(id))).into(new ArrayList<Document>());
			if(docs != null && docs.size() > 0) {
				workReportText = (WorkReportText)SchameDocumentUtil.documentToSchame(docs.get(0), WorkReportText.class);
			}
		}
		
		return workReportText;
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
		 Document doc = SchameDocumentUtil.schameToDocument(workReportText, WorkReportText.class);
		 
		 //Update a Document
		 collection.updateOne(Filters.eq("_id", doc.get("_id")), new Document("$set", doc));
		 
	}
	
	public void updateWorkReportTextValidStatus(String id, String validStatus){
		//To connect to a single MongoDB instance:
	    //You can explicitly specify the hostname and the port:
		MongoClient mongoClient = new MongoClient( "localhost" , 27017 );

		//Access a Database
		MongoDatabase database = mongoClient.getDatabase("manage");
		
		//Access a Collection
		MongoCollection<Document> collection = database.getCollection("WorkReportText");
		
		
		//Create a Document
		 Document doc = new Document("validStatus", validStatus);
		 
		 //Update a Document
		 collection.updateOne(Filters.eq("_id", new ObjectId(id)), new Document("$set", doc));
		 
	}
}

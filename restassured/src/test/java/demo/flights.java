package demo;
import java.time.LocalDate;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;   
public class flights {
	String originId;
    String destinationId;
    String departureFrom;
    String departureTo;
    String cabin;
    int adult;
    int child;
    int infant;
    
    public flights(String o, String d, String depF, String depT, String c, int a, int ch, int i){
    	this.originId=o;
    	this.destinationId=d;
    	this.departureFrom=depF;
    	this.departureTo=depT;
    	this.cabin=c;
    	this.adult=a;
    	this.child=ch;
    	this.infant=i;
    }
    
    public JSONObject rest_body() {
    	JSONObject body=new JSONObject();
    	JSONObject temp=new JSONObject(); 
    	temp.put("originId", this.originId);
    	temp.put("destinationId",this.destinationId);
    	temp.put("departureTo", this.departureTo);
    	temp.put("departureFrom", this.departureFrom);
    	JSONArray leg = new JSONArray();
    	leg.add(temp);
    	body.put("leg", leg);
    	body.put("cabin",this.cabin);
    	JSONObject temp1=new JSONObject();
    	temp1.put("adult", this.adult);
    	temp1.put("child", this.child);
    	temp1.put("infant", this.infant);
//    	JSONArray pax = new JSONArray();
//    	pax.add(temp1);
    	body.put("pax", temp1);
    	body.put("stops", new JSONArray());
    	body.put("airline", new JSONArray());
     	body.put("timeslots", new JSONObject());
    	body.put("airports", new JSONObject());
    	
    	return body;
    }
    
    

}


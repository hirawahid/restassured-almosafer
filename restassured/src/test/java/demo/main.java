package demo;
import org.json.simple.JSONObject;

import demo.flights;

public class main {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		flights fl=new flights("RUH","JED","2022-04-01","2022-04-05","Economy",1,0,0);
		JSONObject res=new JSONObject();
		res=fl.rest_body();
		System.out.print(res.toJSONString());

	}

}

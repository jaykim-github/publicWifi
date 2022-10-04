package service;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;


import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;


public class ApiExplorer {
	public static void main(String[] args) throws IOException {
		StringBuilder urlBuilder = new StringBuilder("http://openapi.seoul.go.kr:8088");
		urlBuilder.append("/" + URLEncoder.encode("4462734865646d623130327563504b5a", "UTF-8"));
		urlBuilder.append("/" + URLEncoder.encode("json", "UTF-8"));
		urlBuilder.append("/" + URLEncoder.encode("TbPublicWifiInfo", "UTF-8"));
		urlBuilder.append("/" + URLEncoder.encode("1", "UTF-8"));
		urlBuilder.append("/" + URLEncoder.encode("1000", "UTF-8"));
		//urlBuilder.append("/" + URLEncoder.encode("20220301", "UTF-8"));
		URL url = new URL(urlBuilder.toString());
		HttpURLConnection conn = (HttpURLConnection) url.openConnection();
		conn.setRequestMethod("GET");
		conn.setRequestProperty("Content-type", "application/json");
		conn.setDoOutput(true); 
		System.out.println("Response code: " + conn.getResponseCode());
		BufferedReader rd;
// 서비스코드가 정상이면 200~300사이의 숫자가 나옵니다.
		if (conn.getResponseCode() >= 200 && conn.getResponseCode() <= 300) {
			rd = new BufferedReader(new InputStreamReader(conn.getInputStream(), "UTF-8"));
		} else {
			rd = new BufferedReader(new InputStreamReader(conn.getErrorStream()));
		}
		StringBuilder sb = new StringBuilder();
		
//		while(rd.ready()) {
//			sb.append(rd.readLine());
//		}
		String line;
		while ((line = rd.readLine()) != null) {
			sb.append(line);
		}
		
		rd.close();
		conn.disconnect();
		System.out.println(sb.toString());
		
		String result = sb.toString();
		
		
    	try {
    		JSONParser jsonParser = new JSONParser();
			JSONObject jsonObject = (JSONObject)jsonParser.parse(result);
			JSONObject tbpublicWifiInfo = (JSONObject)jsonObject.get("TbPublicWifiInfo");
			JSONArray publicwifiInfo = (JSONArray)tbpublicWifiInfo.get("row");
			
			String str = "";
			for(int i = 0; i<publicwifiInfo.size(); i++) {
				JSONObject X_SWIFI_MGR_NO = (JSONObject)publicwifiInfo.get(i);
				
				str += X_SWIFI_MGR_NO.get("X_SWIFI_MGR_NO") + "\n";
			}
			
			System.out.print(str);
			
		} catch (ParseException e) {
			e.printStackTrace();
		}

	}
}

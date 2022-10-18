package service;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.sql.PreparedStatement;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

public class WifiService {
	static WifiService ae = new WifiService();
	static boolean flag = false;

	public static void main(String[] args) throws IOException {
		
//		ae.init();
		// 연결 후 최대값 가져오기
//		int maxNum = ae.getWifiMaxNum();
//		System.out.println(maxNum);
//		int start = 1;
//		int end = 0;
//		String result = "";
//
//		while (maxNum > 1000) {
//
//			if (maxNum > 1000) {
//				end = end + 1000;
//				maxNum = maxNum - 1000;
//			} else {
//				end = maxNum;
//			}
//			// 여기서 db에 저장
//			
//			result = ae.getWifiInfo(start, end);
//			ae.insertDB(result);
//			System.out.println(start + " " + end);
//			System.out.println();
//
//			// start 초기화
//			start = end + 1;
//		}
//		maxNum = maxNum + start;
//		result = ae.getWifiInfo(start, maxNum);
////		result = ae.getWifiInfo(11679, 11680);
//		ae.insertDB(result);
//		System.out.println(start + " " + maxNum);
//		System.out.println();
//		
//		ae.insertDistance(37.4901,126.9426);
//		ae.insertHisDB(37.4901,126.9426);
//		ae.deleteDB(1);
		
	}
	
	public void init() {
		ae.createDB();
		ae.createHisDB();
		
		flag = true;
	}

	// start to end 가져오기
	public String getWifiInfo(int start, int end) throws IOException {
		StringBuilder urlBuilder = new StringBuilder("http://openapi.seoul.go.kr:8088");
		urlBuilder.append("/" + URLEncoder.encode("4462734865646d623130327563504b5a", "UTF-8"));
		urlBuilder.append("/" + URLEncoder.encode("json", "UTF-8"));
		urlBuilder.append("/" + URLEncoder.encode("TbPublicWifiInfo", "UTF-8"));
		urlBuilder.append("/" + URLEncoder.encode(Integer.toString(start), "UTF-8"));
		urlBuilder.append("/" + URLEncoder.encode(Integer.toString(end), "UTF-8"));
		URL url = new URL(urlBuilder.toString());
		HttpURLConnection conn = (HttpURLConnection) url.openConnection();
		conn.setRequestMethod("GET");
		conn.setRequestProperty("Content-type", "application/json");
		conn.setDoOutput(true);
		System.out.println("Response code: " + conn.getResponseCode());
		BufferedReader rd;
		if (conn.getResponseCode() >= 200 && conn.getResponseCode() <= 300) {
			rd = new BufferedReader(new InputStreamReader(conn.getInputStream(), "UTF-8"));
		} else {
			rd = new BufferedReader(new InputStreamReader(conn.getErrorStream()));
		}
		StringBuilder sb = new StringBuilder();

		while (rd.ready()) {
			sb.append(rd.readLine());
		}

		rd.close();
		conn.disconnect();

		String result = sb.toString();

		return result;
	}

	// 최대값 가져오기
	public int getWifiMaxNum() throws IOException {
		//if(flag == false) return -1;

		String result = ae.getWifiInfo(1, 1);

		Long total_countL = 0L;
		try {
			JSONParser jsonParser = new JSONParser();
			JSONObject jsonObject = (JSONObject) jsonParser.parse(result);
			JSONObject tbpublicWifiInfo = (JSONObject) jsonObject.get("TbPublicWifiInfo");

			total_countL = (Long) tbpublicWifiInfo.get("list_total_count");

		} catch (ParseException e) {
			e.printStackTrace();
		}

		int total_count = total_countL.intValue();
		return total_count;
	}

	// DB 연결 ...? 이게 필요한지 의문 어쩄든
	public static void createDB() {
		Connection c = null;
		Statement stmt = null;
		
		try {
			Class.forName("org.sqlite.JDBC");
			c = DriverManager.getConnection("jdbc:sqlite:publicWifi.db");
			c.setAutoCommit(false);
			System.out.println("Opened database successfully");
			stmt = c.createStatement();
			
			String sql = "CREATE TABLE 'wifiInfo' (\r\n"
					+ "	'X_SWIFI_MGR_NO' 	TEXT,\r\n"
					+ "	'X_SWIFI_WRDOFC' 	TEXT,\r\n"
					+ "	'X_SWIFI_MAIN_NM'	TEXT,\r\n"
					+ "	'X_SWIFI_ADRES1'	TEXT,\r\n"
					+ "	'X_SWIFI_ADRES2'	TEXT,\r\n"
					+ "	'X_SWIFI_INSTL_FLOOR'	TEXT,\r\n"
					+ "	'X_SWIFI_INSTL_TY' 	TEXT,\r\n"
					+ "	'X_SWIFI_INSTL_MBY'	TEXT,\r\n"
					+ "	'X_SWIFI_SVC_SE'	TEXT,\r\n"
					+ "	'X_SWIFI_CMCWR'	TEXT,\r\n"
					+ "	'X_SWIFI_CNSTC_YEAR'	TEXT,\r\n"
					+ "	'X_SWIFI_INOUT_DOOR'	TEXT,\r\n"
					+ "	'X_SWIFI_REMARS3'	TEXT,\r\n"
					+ "	'LAT'	REAL,\r\n"
					+ "	'LNT'	REAL,\r\n"
					+ "	'WORK_DTTM'	TEXT,\r\n"
					+ "	'DISTANCE'	REAL,\r\n"
					+ "	PRIMARY KEY('X_SWIFI_MGR_NO')\r\n"
					+ ")";
			
			stmt.executeUpdate(sql);
			
			stmt.close();
			c.commit();
			c.close();
			
		} catch (Exception e) {
			System.err.println(e.getClass().getName() + ": " + e.getMessage());
			System.exit(0);
		}
		System.out.println("Opened database successfully");
	}

	// Json으로 변형하여 DB에 넣기
	public int insertDB(String result) {
		if(flag == false) return -1;
		
		Connection c = null;
		Statement stmt = null;
		ResultSet rs = null;
		String str = "";
		try {
			// DB 연결
			Class.forName("org.sqlite.JDBC");
			c = DriverManager.getConnection("jdbc:sqlite:publicWifi.db");
			c.setAutoCommit(true);
			System.out.println("Opened database successfully");

			stmt = c.createStatement();

			// Json으로 변형 후 sql문 insert
			JSONParser jsonParser = new JSONParser();
			JSONObject jsonObject = (JSONObject) jsonParser.parse(result);
			JSONObject tbpublicWifiInfo = (JSONObject) jsonObject.get("TbPublicWifiInfo");
			JSONArray publicwifiInfo = (JSONArray) tbpublicWifiInfo.get("row");

			
			String sql = "";
			for (int i = 0; i < publicwifiInfo.size(); i++) {
				JSONObject rowData = (JSONObject) publicwifiInfo.get(i);

				String X_SWIFI_MGR_NO = (String) rowData.get("X_SWIFI_MGR_NO");
				X_SWIFI_MGR_NO = X_SWIFI_MGR_NO.trim();
				String X_SWIFI_WRDOFC = (String) rowData.get("X_SWIFI_WRDOFC");
				String X_SWIFI_MAIN_NM = (String) rowData.get("X_SWIFI_MAIN_NM");
				String X_SWIFI_ADRES1 = (String) rowData.get("X_SWIFI_ADRES1");
				X_SWIFI_ADRES1 = X_SWIFI_ADRES1.replaceAll("\'","");
				String X_SWIFI_ADRES2 = (String) rowData.get("X_SWIFI_ADRES2");
				X_SWIFI_ADRES2 = X_SWIFI_ADRES2.replaceAll("\'","");
				String X_SWIFI_INSTL_FLOOR = (String) rowData.get("X_SWIFI_INSTL_FLOOR");
				String X_SWIFI_INSTL_TY = (String) rowData.get("X_SWIFI_INSTL_TY");
				String X_SWIFI_INSTL_MBY = (String) rowData.get("X_SWIFI_INSTL_MBY");
				String X_SWIFI_SVC_SE = (String) rowData.get("X_SWIFI_SVC_SE");
				String X_SWIFI_CMCWR = (String) rowData.get("X_SWIFI_CMCWR");
				String X_SWIFI_CNSTC_YEAR = (String) rowData.get("X_SWIFI_CNSTC_YEAR");
				String X_SWIFI_REMARS3 = (String) rowData.get("X_SWIFI_REMARS3");
				String LAT = (String) rowData.get("LAT");
				String LNT = (String) rowData.get("LNT");
				String WORK_DTTM = (String) rowData.get("WORK_DTTM");
				String X_SWIFI_INOUT_DOOR = (String) rowData.get("X_SWIFI_INOUT_DOOR");
				String DISTANCE = "";

				sql = "INSERT INTO wifiInfo values (' "
						+ X_SWIFI_MGR_NO + "', '" + X_SWIFI_WRDOFC + "', '" + X_SWIFI_MAIN_NM + "', '" + X_SWIFI_ADRES1
						+ "', '" + X_SWIFI_ADRES2 + "', '" + X_SWIFI_INSTL_FLOOR + "', '" + X_SWIFI_INSTL_TY + "', '"
						+ X_SWIFI_INSTL_MBY + "', '" + X_SWIFI_SVC_SE + "', '" + X_SWIFI_CMCWR + "', '"
						+ X_SWIFI_CNSTC_YEAR + "', '" + X_SWIFI_INOUT_DOOR + "', '" + X_SWIFI_REMARS3 + "', '" + LAT
						+ "', '" + LNT + "', '" + WORK_DTTM+ "', '" + DISTANCE + "')";

				stmt.executeUpdate(sql);
				//System.out.println(sql);
				
			}
			
			System.out.println();
			rs = stmt.executeQuery( "select count(*) from wifiInfo" );
			System.out.println(rs.getString("count(*)"));
			str = rs.getString("count(*)");
			
			
			rs.close();
			stmt.close();
			c.commit();
			c.close();
			System.out.print(str);

		} catch (Exception e) {
			e.printStackTrace();
		}
		
		 
		return Integer.parseInt(str);
	}
	
	
	public List<WifiInfo> insertDistance(double lat, double lnt) throws IOException {
		List<WifiInfo> wifiList = new ArrayList<>();
		
		Connection c = null;
		Statement stmt = null;
		ResultSet rs = null;
		String sql = "";	
		String s = "";
		
		int count = 0;
		try {
			Class.forName("org.sqlite.JDBC");
			c = DriverManager.getConnection("jdbc:sqlite:publicWifi.db");
			c.setAutoCommit(false);
			System.out.println("Opened database successfully");
				
			stmt = c.createStatement();
			System.out.println();
			PreparedStatement ps = c.prepareStatement("SELECT X_SWIFI_MGR_NO,LAT,LNT from wifiInfo");
			rs = ps.executeQuery();
			
			
			
			while(rs.next()) {
				String X_SWIFI_MGR_NO = rs.getString("X_SWIFI_MGR_NO");
				double LAT = rs.getDouble("LAT");
				double LNT = rs.getDouble("LNT");
								
				double d = ae.getDistance(lat,lnt,LAT,LNT);
				
				sql = "UPDATE wifiInfo set distance = ' "
						+ d + "' where X_SWIFI_MGR_NO = '" + X_SWIFI_MGR_NO + "'";
				
				stmt.executeUpdate(sql);
				count++;
			}
			System.out.println(count);
			
			
			ResultSet res = null;
			PreparedStatement pst = c.prepareStatement("select * from wifiInfo order by DISTANCE LIMIT 20");
			res = pst.executeQuery();
			
		
			while(res.next()) {
				WifiInfo wi = new WifiInfo();
				wi.setX_SWIFI_MGR_NO(res.getString("X_SWIFI_MGR_NO"));
				wi.setX_SWIFI_WRDOFC(res.getString("X_SWIFI_WRDOFC"));
				wi.setX_SWIFI_MAIN_NM(res.getString("X_SWIFI_MAIN_NM"));
				wi.setX_SWIFI_ADRES1(res.getString("X_SWIFI_ADRES1"));
				wi.setX_SWIFI_ADRES2(res.getString("X_SWIFI_ADRES2"));
				wi.setX_SWIFI_INSTL_FLOOR(res.getString("X_SWIFI_INSTL_FLOOR"));
				wi.setX_SWIFI_INSTL_TY(res.getString("X_SWIFI_INSTL_TY"));
				wi.setX_SWIFI_INSTL_MBY(res.getString("X_SWIFI_INSTL_MBY"));
				wi.setX_SWIFI_SVC_SE(res.getString("X_SWIFI_SVC_SE"));
				wi.setX_SWIFI_CMCWR(res.getString("X_SWIFI_CMCWR"));
				wi.setX_SWIFI_CNSTC_YEAR(res.getString("X_SWIFI_CNSTC_YEAR"));
				wi.setX_SWIFI_INOUT_DOOR(res.getString("X_SWIFI_INOUT_DOOR"));
				wi.setX_SWIFI_REMARS3(res.getString("X_SWIFI_REMARS3"));
				wi.setLAT(res.getDouble("LAT"));
				wi.setLNT(res.getDouble("LNT"));
				wi.setWORK_DTTM(res.getString("WORK_DTTM"));
				wi.setDISTANCE(res.getDouble("DISTANCE"));
				
				wifiList.add(wi);
			}
			
			System.out.println(s);
						
			rs.close();
			stmt.close();
			c.commit();
			c.close();

		} catch (Exception e) {
			e.printStackTrace();
		}
		
		
		return wifiList;
	}
	
	public  double getDistance(double lat1, double lon1, double lat2, double lon2) {
		  double dLat = Math.toRadians(lat2 - lat1);
		  double dLon = Math.toRadians(lon2 - lon1);

		  double a = Math.sin(dLat/2)* Math.sin(dLat/2)+ Math.cos(Math.toRadians(lat1))* Math.cos(Math.toRadians(lat2))* Math.sin(dLon/2)* Math.sin(dLon/2);
		  double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1-a));
		  double d = 6357* c ;    // Distance in km
		  double dd = Double.parseDouble(String.format("%.2f",d));
		  return dd;
		}
	
	public static void createHisDB() {
		Connection c = null;
		Statement stmt = null;
		
		try {
			Class.forName("org.sqlite.JDBC");
			c = DriverManager.getConnection("jdbc:sqlite:publicWifi.db");
			c.setAutoCommit(false);
			System.out.println("Opened database successfully");
			stmt = c.createStatement();
			
			String sql = "CREATE TABLE 'wifi_his' (\r\n"
					+ "	'ID'	INTEGER,\r\n"
					+ "	'LAT'	REAL,\r\n"
					+ "	'LNT'	REAL,\r\n"
					+ "	'WORK_DTTM'	TEXT,\r\n"
					+ "	'USEYN'	TEXT,\r\n"
					+ "	PRIMARY KEY('ID')\r\n"
					+ ");";
			
			stmt.executeUpdate(sql);
			
			stmt.close();
			c.commit();
			c.close();
			
		} catch (Exception e) {
			System.err.println(e.getClass().getName() + ": " + e.getMessage());
			System.exit(0);
		}
		
	}
	
	
	public void insertHisDB(double lat, double lnt) {
		Connection c = null;
		Statement stmt = null;
		
		LocalDateTime now = LocalDateTime.now();
		String sql = "";
		try {
			// DB 연결
			Class.forName("org.sqlite.JDBC");
			c = DriverManager.getConnection("jdbc:sqlite:publicWifi.db");
			c.setAutoCommit(false);
			System.out.println("Opened database successfully");
			
			stmt = c.createStatement();
			
			ResultSet res = null;
			PreparedStatement pst = c.prepareStatement("select count(ID) from wifi_his");
			res = pst.executeQuery();
			
			int id = res.getInt("count(ID)");
			System.out.println(id); 
			id++;
			
			sql = "INSERT INTO wifi_his values('"+ id + "', '" + lat + "', '" + lnt + "', '" + now + "', 'Y')";

			stmt.executeUpdate(sql);					
			stmt.close();
			c.commit();
			c.close();
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public void deleteDB(int id) {
		Connection c = null;
		Statement stmt = null;
		
		String sql = "";
		try {
			// DB 연결
			Class.forName("org.sqlite.JDBC");
			c = DriverManager.getConnection("jdbc:sqlite:publicWifi.db");
			c.setAutoCommit(false);
			System.out.println("Opened database successfully");
			
			stmt = c.createStatement();
			
			sql = "UPDATE wifi_his set USEYN = 'N'";

			stmt.executeUpdate(sql);					
			stmt.close();
			c.commit();
			c.close();
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public List<WifiInfo> historylist() throws IOException{
		List<WifiInfo> wifiList = new ArrayList<>();
		
		Connection c = null;
		Statement stmt = null;
		ResultSet rs = null;
		String sql = "";	
		String s = "";
		
		int count = 0;
		try {
			Class.forName("org.sqlite.JDBC");
			c = DriverManager.getConnection("jdbc:sqlite:publicWifi.db");
			c.setAutoCommit(false);
			System.out.println("Opened database successfully");
				
			stmt = c.createStatement();
			System.out.println();

			ResultSet res = null;
			PreparedStatement pst = c.prepareStatement("SELECT ID,LAT,LNT,WORK_DTTM from wifi_his order by id desc");
			res = pst.executeQuery();
			
		
			while(res.next()) {
				WifiInfo wi = new WifiInfo();
				wi.setID(res.getInt("ID"));
				wi.setLAT(res.getDouble("LAT"));
				wi.setLNT(res.getDouble("LNT"));
				wi.setWORK_DTTM(res.getString("WORK_DTTM"));
				
				wifiList.add(wi);
			}
			
			System.out.println(s);
						
			res.close();
			stmt.close();
			c.commit();
			c.close();

		} catch (Exception e) {
			e.printStackTrace();
		}
		
		
		return wifiList;
	}
	
}

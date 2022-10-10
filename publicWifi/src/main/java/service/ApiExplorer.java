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

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

public class ApiExplorer {
	static ApiExplorer ae = new ApiExplorer();

	public static void main(String[] args) throws IOException {
		// 연결 후 최대값 가져오기
//		int maxNum = ae.getWifiMaxNum();
//		System.out.println(maxNum);
//		int start = 1;
//		int end = 0;
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
//			System.out.println(start + " " + end);
//			System.out.println();
//
//			// start 초기화
//			start = end + 1;
//		}
//		maxNum = maxNum + start;
//		System.out.println(start + " " + maxNum);
//		System.out.println();
		ae.connectDB();
		String result = ae.getWifiInfo(1, 1);
		ae.insertDB(result);
	}

	// start to end 가져오기
	public String getWifiInfo(int start, int end) throws IOException {
		StringBuilder urlBuilder = new StringBuilder("http://openapi.seoul.go.kr:8088");
		urlBuilder.append("/" + URLEncoder.encode("4462734865646d623130327563504b5a", "UTF-8"));
		urlBuilder.append("/" + URLEncoder.encode("json", "UTF-8"));
		urlBuilder.append("/" + URLEncoder.encode("TbPublicWifiInfo", "UTF-8"));
		urlBuilder.append("/" + URLEncoder.encode(Integer.toString(start), "UTF-8"));
		urlBuilder.append("/" + URLEncoder.encode(Integer.toString(end), "UTF-8"));
		// urlBuilder.append("/" + URLEncoder.encode("20220301", "UTF-8"));
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

		while (rd.ready()) {
			sb.append(rd.readLine());
		}

		rd.close();
		conn.disconnect();

		String result = sb.toString();

		return result;
	}

	// 최대값 가져오기
	static int getWifiMaxNum() throws IOException {

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
	public static void connectDB() {
		Connection c = null;
		try {
			Class.forName("org.sqlite.JDBC");
			c = DriverManager.getConnection("jdbc:sqlite:publicWifi.db");
		} catch (Exception e) {
			System.err.println(e.getClass().getName() + ": " + e.getMessage());
			System.exit(0);
		}
		System.out.println("Opened database successfully");
	}

	// Json으로 변형하여 DB에 넣기
	public int insertDB(String result) {
		Connection c = null;
		Statement stmt = null;
		ResultSet rs = null;
		
		try {
			// DB 연결
			Class.forName("org.sqlite.JDBC");
			c = DriverManager.getConnection("jdbc:sqlite:publicWifi.db");
			c.setAutoCommit(false);
			System.out.println("Opened database successfully");

			stmt = c.createStatement();

			// Json으로 변형 후 sql문 insert
			JSONParser jsonParser = new JSONParser();
			JSONObject jsonObject = (JSONObject) jsonParser.parse(result);
			JSONObject tbpublicWifiInfo = (JSONObject) jsonObject.get("TbPublicWifiInfo");
			JSONArray publicwifiInfo = (JSONArray) tbpublicWifiInfo.get("row");

			String str = "";
			String sql = "";
			for (int i = 0; i < publicwifiInfo.size(); i++) {
				JSONObject rowData = (JSONObject) publicwifiInfo.get(i);

				String X_SWIFI_MGR_NO = (String) rowData.get("X_SWIFI_MGR_NO");
				String X_SWIFI_WRDOFC = (String) rowData.get("X_SWIFI_WRDOFC");
				String X_SWIFI_MAIN_NM = (String) rowData.get("X_SWIFI_MAIN_NM");
				String X_SWIFI_ADRES1 = (String) rowData.get("X_SWIFI_ADRES1");
				String X_SWIFI_ADRES2 = (String) rowData.get("X_SWIFI_ADRES2");
				String X_SWIFI_INSTL_FLOOR = (String) rowData.get("X_SWIFI_INSTL_FLOOR");
				String X_SWIFI_INSTL_TY = (String) rowData.get("X_SWIFI_INSTL_TY");
				String X_SWIFI_INSTL_MBY = (String) rowData.get("X_SWIFI_INSTL_MBY");
				String X_SWIFI_SVC_SE = (String) rowData.get("X_SWIFI_SVC_SE");
				String X_SWIFI_CMCWR = (String) rowData.get("X_SWIFI_CMCWR");
				String X_SWIFI_CNSTC_YEAR = (String) rowData.get("X_SWIFI_CNSTC_YEAR");
				String X_SWIFI_REMARS3 = (String) rowData.get("X_SWIFI_REMARS3");
				String LAT = (String) rowData.get("LAT");
				String LNT = (String) rowData.get("LNT");
				String WORK_DTTM = (String) rowData.get("WORK_DTTM	");
				String X_SWIFI_INOUT_DOOR = (String) rowData.get("X_SWIFI_INOUT_DOOR");

//				sql = "INSERT INTO wifiInfo (X_SWIFI_MGR_NO,X_SWIFI_WRDOFC,X_SWIFI_MAIN_NM,X_SWIFI_ADRES1,X_SWIFI_ADRES2,X_SWIFI_INSTL_FLOOR,X_SWIFI_INSTL_TY,X_SWIFI_INSTL_MBY,X_SWIFI_SVC_SE,X_SWIFI_CMCWR,X_SWIFI_CNSTC_YEAR,X_SWIFI_INOUT_DOOR,X_SWIFI_REMARS3,LAT,LNT,WORK_DTTM ) values (' "
//						+ X_SWIFI_MGR_NO + "', '" + X_SWIFI_WRDOFC + "', '" + X_SWIFI_MAIN_NM + "', '" + X_SWIFI_ADRES1
//						+ "', '" + X_SWIFI_ADRES2 + "', '" + X_SWIFI_INSTL_FLOOR + "', '" + X_SWIFI_INSTL_TY + "', '"
//						+ X_SWIFI_INSTL_MBY + "', '" + X_SWIFI_SVC_SE + "', '" + X_SWIFI_CMCWR + "', '"
//						+ X_SWIFI_CNSTC_YEAR + "', '" + X_SWIFI_INOUT_DOOR + "', '" + X_SWIFI_REMARS3 + "', '" + LAT
//						+ "', '" + LNT + "', '" + WORK_DTTM + "')";
//				//System.out.println(sql);
//				System.out.println();
//				stmt.executeUpdate(sql);
				rs = stmt.executeQuery( "select * from wifiInfo" );
				
				System.out.println(rs.getString("X_SWIFI_MGR_NO"));
				System.out.println();
				
			}
			rs.close();
			stmt.close();
			c.commit();
			c.close();
			System.out.print(sql);

		} catch (Exception e) {
			e.printStackTrace();
		}

		return 1;
	}
}

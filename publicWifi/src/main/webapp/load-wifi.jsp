<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
     <%@ page import="service.WifiService"%>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>Insert title here</title>
</head>
<body>
<b><center>
<% 
	WifiService ae = new WifiService();
	
	int maxNum = ae.getWifiMaxNum();
	out.print(maxNum);
	int start = 1;
	int end = 0;
	String result = "";

	while (maxNum > 1000) {

		if (maxNum > 1000) {
			end = end + 1000;
			maxNum = maxNum - 1000;
		} else {
			end = maxNum;
		}
		
		result = ae.getWifiInfo(start, end);
		ae.insertDB(result);
		start = end + 1;
	}
	maxNum = maxNum + start;
	result = ae.getWifiInfo(start, maxNum);
	ae.insertDB(result);

%>개의 WIFI정보를 정상적으로 저장하였습니다. </center></b>
<br>
<center><a href="index.jsp">홈으로 가기</a></center>
</body>
</html>
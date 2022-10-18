<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
     <%@ page import="service.WifiService"%>
       <%@ page import="service.WifiInfo"%>
   <%@ page import="java.util.List"%>
   <%@ page import="java.util.ArrayList"%>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>Insert title here</title>
</head>
<style>
#wifis {
  font-family: Arial, Helvetica, sans-serif;
  border-collapse: collapse;
  width: 100%;
}

#wifis td, #wifis th {
  border: 1px solid #ddd;
  padding: 8px;
}

#wifis tr:nth-child(even){background-color: #f2f2f2;}

#wifis tr:hover {background-color: #ddd;}

#wifis th {
  padding-top: 12px;
  padding-bottom: 12px;
  text-align: left;
  background-color: #04AA6D;
  color: white;
}
</style>
<body>

<h4>위치 히스토리 목록</h4>
<br> 
<a href="index2.jsp">홈</a> |<a href="loadhistory.jsp">위치 히스토리 목록</a> | <a href="/load-wifi.jsp">Open API 와이파이 정보 가져오기</a>

<% 
	WifiService ae = new WifiService();
	List<WifiInfo> wifiList = new ArrayList<>();
	
	wifiList = ae.historylist();
%>


<table id="wifis">
<br>
<tr>
   <th>ID</th>
   <th>X좌표</th>
   <th>Y좌표</th>
   <th>조회일자</th>
   <th>비고</th>
</tr>
<tr>
<%
	for(WifiInfo wi : wifiList){ 
%>
<tr>
<td><%=wi.getID()%> </td>
<td><%=wi.getLAT()%> </td>
<td><%=wi.getLNT()%> </td>
<td><%=wi.getWORK_DTTM()%> </td>
<td><button>삭제</button> </td>
</tr>
<% } %>
</tr>
</table>
</body>
</html>
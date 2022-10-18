<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
 <%@ page import="service.WifiService"%>
  <%@ page import="service.WifiInfo"%>
   <%@ page import="java.util.List"%>
   <%@ page import="java.util.ArrayList"%>
<!DOCTYPE html>
<html>
<script type="text/javascript">
function onGeoOk({ coords, timestamp }){
    var lat = coords.latitude;
    var lng = coords.longitude;
    console.log("You live in", lat, lng);
    
    document.getElementById('LAT').value = lat;
    document.getElementById('LNT').value = lng;
}
function onGeoError(){
    alert("Can't find you. No weather for you.");
}

function getPosition(){
    if (!navigator.geolocation) {
        throw "위치 정보가 지원되지 않습니다.";
    }

	navigator.geolocation.getCurrentPosition(onGeoOk);
}

</script>  
<head>
<%
List<WifiInfo> wifiList = new ArrayList<>();
WifiService ae = null;
String lat = request.getParameter("LAT");
String lnt = request.getParameter("LNT");

if(lat != null && lat != ""){
	double d_lat = Double.parseDouble(lat);
	double d_lnt = Double.parseDouble(lnt);
	ae = new WifiService();
	
	wifiList = ae.insertDistance(d_lat,d_lnt);
	ae.insertHisDB(d_lat,d_lnt);
}
%>
<meta http-equiv="Content-Type" content="text/html; charset=EUC-KR">
<title>와이파이 정보 구하기</title>
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
</head>
<body>
<h4>와이파이 정보 구하기</h4>
<br> 
<a href="index2.jsp">홈</a> | <a href="loadhistory.jsp">위치 히스토리 목록</a>  | <a href="/load-wifi.jsp">Open API 와이파이 정보 가져오기</a>
<br>
<br>
<form action="index2.jsp" method="get">
LAT : <input type="text" id ="LAT"  name="LAT" value="">. LNT : <input type="text" id ="LNT" name="LNT" value="">
<input type="submit" value = "근처 WIFI 정보 보기"/>
 </form>
<button onclick="getPosition()">내 위치 가져오기</button> 
<br>
<br>
<table id="wifis">
<br>
<tr>
   <th>거리(Km)</th>
   <th>관리번호</th>
   <th>자치구</th>
   <th>와이파이명</th>
   <th>도로명주소</th>
   <th>상세주소</th>
   <th>설치위치(층)</th>
   <th>설치유형</th>
   <th>설치기관</th>
   <th>서비스구분</th>
   <th>망종류</th>
   <th>설치년도</th>
   <th>실내외 구분</th>
   <th>WIFI접속 환경</th>
   <th>X좌표</th>
   <th>Y좌표</th>
   <th>작업일자</th>
</tr>


<tr>
<% if (ae == null) { %>
<td colspan='17'>
<center>위치 정보를 입력한 후에 조회해 주세요.</center></td>
<% }else{
	for(WifiInfo wi : wifiList){ 
%>
<tr>
<td><%=wi.getDISTANCE()%> </td>
<td><%=wi.getX_SWIFI_MGR_NO()%> </td>
<td><%=wi.getX_SWIFI_WRDOFC()%> </td>
<td><%=wi.getX_SWIFI_MAIN_NM()%> </td>
<td><%=wi.getX_SWIFI_ADRES1()%> </td>
<td><%=wi.getX_SWIFI_ADRES2()%> </td>
<td><%=wi.getX_SWIFI_INSTL_FLOOR()%> </td>
<td><%=wi.getX_SWIFI_INSTL_TY()%> </td>
<td><%=wi.getX_SWIFI_INSTL_MBY()%> </td>
<td><%=wi.getX_SWIFI_SVC_SE()%> </td>
<td><%=wi.getX_SWIFI_CMCWR()%> </td>
<td><%=wi.getX_SWIFI_CNSTC_YEAR()%> </td>
<td><%=wi.getX_SWIFI_INOUT_DOOR()%> </td>
<td><%=wi.getX_SWIFI_REMARS3()%> </td>
<td><%=wi.getLAT()%> </td>
<td><%=wi.getLNT()%> </td>
<td><%=wi.getWORK_DTTM()%> </td>
</tr>
<% } }%>
</tr>


</table>

</body>
</html>
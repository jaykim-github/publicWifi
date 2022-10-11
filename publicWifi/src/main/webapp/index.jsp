<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
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
<meta charset="UTF-8">
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
<a href="index.jsp">홈</a> | <a>위치 히스토리 목록</a> | <a href="/load-wifi.jsp">Open API 와이파이 정보 가져오기</a>
<br>
<br>
LAT : <input type="text" id ="LAT"  name="LAT" value="">. LNT : <input type="text" id ="LNT" name="LNT" value=""> &nbsp <button onclick="getPosition()">내 위치 가져오기</button> <button>근처 WIFI 정보 보기</button>
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
<td colspan='17'>
<center>위치 정보를 입력한 후에 조회해 주세요.</center></td>
</tr>

</table>

</body>
</html>
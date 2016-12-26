<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
	pageEncoding="ISO-8859-1"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<title>Upload CSV File</title>
</head>
<body ng-app="myApp">
	<div ng-controller="fileUploadCtrl" class="container">
	<h4>Upload on file select</h4>
	 <button type="file" ngf-select="uploadFile($file)"
          accept=".csv" ngf-max-size="20MB">
      Select File</button>
      <p>status : {{status}}</p>
      
      <hr/>
      <p ng-show = "success">{{finalData}}</p>
	</div>

	<script src="resources/libs/sockjs-0.3.4.js" type="text/javascript"></script>
	<script src="resources/libs/stomp.js" type="text/javascript"></script>
	<script src="resources/libs/angular.min.js"></script>
	<script src="resources/libs/ng-file-upload-shim.js"></script>
	<script src="resources/libs/ng-file-upload.js"></script>
	<script src="resources/libs/lodash.js"></script>
	<script src="resources/js/app.js" type="text/javascript"></script>
</body>
</html>
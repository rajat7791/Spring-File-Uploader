var myApp = angular.module('myApp',['ngFileUpload']);

myApp.controller('fileUploadCtrl', ['$scope', 'FileUploadService','$timeout','Upload', function($scope, FileUploadService, $timeout, Upload){
		
	console.log('inside fileUploadCtrl');
	  $scope.status = "No File selected to upload";
	  $scope.success = false;
	  
	  $timeout(function() {
		  FileUploadService.send($scope.status);
		}, 1000);
	  
	  FileUploadService.receive().then(null, null, function(message) {
	      $scope.status = message;
	    });
  $scope.uploadFile = function (file) {
	  console.log('inside uploadFile');
	  $scope.status = "file uploading request sent";
	  $scope.f = file;
	  if(file){
		  Upload.upload({
	          url: 'uploadfile',
	          fields: {'username': 'rajat'},
	          file: file
	      }).progress(function (evt) {
	    	  var progressPercentage = parseInt(100.0 * evt.loaded / evt.total);
	          console.log('progress: ' + progressPercentage + '% ' + evt.config.file.name);
	      }).success(function (data, status, headers, config) {
	    	  if(data == null){
	    		  $scope.status = "Please select CSV file";
	    	  }else{
	    		  $scope.success = true;
	    		  $scope.finalData = data;
	    	  }
	      });
	  }
      
  };
	
}]);

myApp.service('FileUploadService', ['$q', '$timeout', function($q, $timeout){
	 var service = {}, listener = $q.defer(), socket = {
	      client: null,
	      stomp: null
	    };
	    
	    service.RECONNECT_TIMEOUT = 30000;
	    service.SOCKET_URL = "/WebAPP/fileUploadingStatus";
	    service.SUBCRIBE_TOPIC  = "/topic/message";
	    service.SUBSCRIBE_BROKER = "/app/fileUploadingStatus";
	    
	    service.receive = function() {
	      return listener.promise;
	    };
	    
	    service.send = function(message) {
	      var id = Math.floor(Math.random() * 1000000);
	      socket.stomp.send(service.SUBSCRIBE_BROKER, {
	        priority: 9
	      }, JSON.stringify({
	        message: message,
	        id: id
	      }));
	    };
	    
	    var reconnect = function() {
	      $timeout(function() {
	        initialize();
	      }, this.RECONNECT_TIMEOUT);
	    };
	    
	    var getMessage = function(status) {
	    	return status;
	    };
	    
	    var startListener = function() {
	      socket.stomp.subscribe(service.SUBCRIBE_TOPIC, function(data) {
	        listener.notify(getMessage(data.body));
	      });
	    };
	    
	    var initialize = function() {
	      socket.client = new SockJS(service.SOCKET_URL);
	      socket.stomp = Stomp.over(socket.client);
	      socket.stomp.connect({}, startListener);
	      socket.stomp.onclose = reconnect;
	    };
	    
	    initialize();
	    return service;
}])
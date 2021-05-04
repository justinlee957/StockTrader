function register(){
	var email = document.getElementById('email').value
	var username = document.getElementById('username').value
	var password = document.getElementById('password').value
	var confirmPassword = document.getElementById('confirmPassword').value
	
	if(password != confirmPassword){
		alert("Passwords do not match.")
		return
	}
	fetch(url + '/register?' + new URLSearchParams({
		username: username,
		password: password,
		email: email
	}), {
		method: "GET"
	})
    .then(response => response.text())
    .then(response => {
		if(response.replace(/\s/g, "") === "EmailExists"){
			alert(response)
		}else{
			localStorage.setItem("loggedIn", "true")
			localStorage.setItem("username", username)
			localStorage.setItem("UserID", response.replace(/\s/g, ""))
			localStorage.setItem("Balance", "50000.00")
			loggedInNavBar()
			showLanding()
		}
	})
}

var googleUser = {}
var startApp = function() {
  gapi.load('auth2', function(){
    // Retrieve the singleton for the GoogleAuth library and set up the client.
    auth2 = gapi.auth2.init({
      client_id: '842065465003-vdb7hnfs9uop02tbv95evl3bjptp70oh.apps.googleusercontent.com',
      cookiepolicy: 'single_host_origin',
      // Request scopes in addition to 'profile' and 'email'
      //scope: 'additional_scope'
    });
    attachSignin(document.getElementById('googleBtn'));
  });
};

function attachSignin(element) {
  auth2.attachClickHandler(element, {},
      function(googleUser) {
        var profile = googleUser.getBasicProfile()
        var email = profile.getEmail()
		var name = profile.getName()
		fetch(url + '/googleLogin?' + new URLSearchParams({
			email: email,
			username: name,
		}), {
			method: "GET"
		})
	    .then(response => response.text())
	    .then(response => {
			if(response.replace(/\s/g, "") === "Aregisteredaccountwiththisemailexists"){
				alert(response)
			}else{
				localStorage.setItem("loggedIn", "true")
				localStorage.setItem("username", username)
				localStorage.setItem("UserID", response)
				loggedInNavBar()
				showLanding()
			}
		})
      }, function(error) {
        alert(JSON.stringify(error, undefined, 2));
      });
}

function login(){
	var username = document.getElementById('loginUsername').value
	var password = document.getElementById('loginPassword').value
	
	fetch(url + '/login?' + new URLSearchParams({
		username: username,
		password: password,
	}), {
		method: "GET"
	})
    .then(response => response.text())
    .then(response => {
		if(response.replace(/\s/g, "") === "InvalidLogin"){
			alert(response)
		}else{
			localStorage.setItem("loggedIn", "true")
			localStorage.setItem("username", username)
			localStorage.setItem("UserID", response)
			loggedInNavBar()
			showLanding()
		}
	})
}

function loggedInNavBar(){
    var elements = document.getElementsByClassName('loggedInBtn')
    for (var i = 0; i < elements.length; i++) {
        elements[i].style.display = "block"
    }
	document.getElementById("loginNavBtn").style.display = "none"	
}

function loggedOutNavBar(){
    var elements = document.getElementsByClassName('loggedInBtn')
    for (var i = 0; i < elements.length; i++) {
        elements[i].style.display = "none"
    }
	document.getElementById("loginNavBtn").style.display = "block"	
}

function logout(){
	localStorage.clear()
	loggedOutNavBar()
	showLanding()
}

//clears localstorage on browser close
window.onload = () => {
	startApp()
	if(localStorage.getItem("loggedIn") === "true"){
		loggedInNavBar()	
	}
}


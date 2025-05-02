function validateName() {
	const fname = document.getElementById("fname").value;
	const fnameMsg = document.getElementById("fnameMsg");
	const lname = document.getElementById("lname").value;
	const lnameMsg = document.getElementById("lnameMsg");
	
	if(fname != null && fname.length > 0){
		fnameMsg.textContent = "";
		document.getElementById("errorIcon3").style.display = "none";
		document.getElementById("fname").style.border = "1px solid #dbdbdb";
	}
	else {
		fnameMsg.textContent = "First name is required.";
		document.getElementById("errorIcon3").style.display = "inline";
		document.getElementById("fname").style.border = "1px solid red";
	}
	
	if(lname != null && lname.length > 0){
		lnameMsg.textContent = "";
		document.getElementById("errorIcon4").style.display = "none";
		document.getElementById("lname").style.border = "1px solid #dbdbdb";
	}
	else {
		lnameMsg.textContent = "Last name is required.";
		document.getElementById("errorIcon4").style.display = "inline";
		document.getElementById("lname").style.border = "1px solid red";
	}
	buttonOpacityReg();
}

function validateEmail() {
	const email = document.getElementById("email").value;
	const errorMsg = document.getElementById("emailMsg");
	const format = /^[^@]+@[^@]+$/;
	
	if(email != null && email.length > 0 && format.test(email)){
		errorMsg.textContent = ""; 
		document.getElementById("errorIcon1").style.display = "none";
		document.getElementById("email").style.border = "1px solid #dbdbdb";
	}
	else {
		errorMsg.textContent = "Email must be valid.";
		document.getElementById("errorIcon1").style.display = "inline";
		document.getElementById("email").style.border = "1px solid red";
	}
	
	if(document.getElementById("fname") === null){
		buttonOpacityLog();
	}
	else {
		buttonOpacityReg();	
	}
}

function validatePassword() {
	const password = document.getElementById("password").value;
	const errorMsg = document.getElementById("passwordMsg");
	
	if(password != null && password.length > 0){
		errorMsg.textContent = ""; 
		document.getElementById("errorIcon2").style.display = "none";
		document.getElementById("password").style.border = "1px solid #dbdbdb";
	}
	else {
		errorMsg.textContent = "Password is required.";
		document.getElementById("errorIcon2").style.display = "inline";
		document.getElementById("password").style.border = "1px solid red";
	}
	
	if(document.getElementById("fname") === null){
		buttonOpacityLog();
	}
	else {
		buttonOpacityReg();	
	}
}

function buttonOpacityLog() {
	const email = document.getElementById("email").value;
	const emailMsg = document.getElementById("emailMsg").textContent;
	const password = document.getElementById("password").value;
	const passwordMsg = document.getElementById("passwordMsg").textContent;
	
	if(email !== "" && password !== "" && emailMsg === "" && (passwordMsg === "" || passwordMsg === "Password or email is incorrect.")){
    	document.getElementById("login-btn").disabled = false;
    	document.getElementById("login-btn").style.opacity = "1";
    	document.getElementById("login-btn").style.cursor = "pointer";
    }
    else {
    	document.getElementById("login-btn").disabled = true;
    	document.getElementById("login-btn").style.opacity = "0.4";
    	document.getElementById("login-btn").style.cursor = "not-allowed";
    }
}

function buttonOpacityReg() {
	const fname = document.getElementById("fname").value;
	const fnameMsg = document.getElementById("fnameMsg").textContent;
	const lname = document.getElementById("lname").value;
	const lnameMsg = document.getElementById("lnameMsg").textContent;
	const email = document.getElementById("email").value;
	const emailMsg = document.getElementById("emailMsg").textContent;
	const password = document.getElementById("password").value;
	const passwordMsg = document.getElementById("passwordMsg").textContent;
	
	if(fname !== "" && lname !== "" && email !== "" && password !== "" && fnameMsg === "" && lnameMsg === "" && passwordMsg === "" && (emailMsg === "" || emailMsg === "Email adready exists.")){
		document.getElementById("register-btn").disabled = false;
		document.getElementById("register-btn").style.opacity = "1";
    	document.getElementById("register-btn").style.cursor = "pointer";
	}
	else {
		document.getElementById("register-btn").disabled = true;
    	document.getElementById("register-btn").style.opacity = "0.4";
    	document.getElementById("register-btn").style.cursor = "not-allowed";
	}
}

function login(event) {
	event.preventDefault();
	const email = document.getElementById("email").value;
	const password = document.getElementById("password").value;
	
	fetch("/login-servlet", {
		method: "POST",
		headers: {
			"Content-type": "application/x-www-form-urlencoded"
		},
		body: `email=${encodeURIComponent(email)}&password=${encodeURIComponent(password)}`
	})
	.then(res => res.text())
	.then(text => {
		if(text.trim() == "success"){
			window.location.href = "/home";
		}
		else {
			document.getElementById("passwordMsg").textContent = "Email or password incorrect";
		}
	})
	.catch(error => {
		console.log("Login failed!");
		console.error(error);
	});
}

function guestLogin(event) {
	event.preventDefault();
	const email = "guest";
	const password = "guest";
	
	fetch("/login-servlet", {
		method: "POST",
		headers: {
			"Content-type": "application/x-www-form-urlencoded"
		},
		body: `email=${encodeURIComponent(email)}&password=${encodeURIComponent(password)}`
	})
	.then(res => res.text())
	.then(text => {
		if(text.trim() === "guestSuccess"){
			window.location.href = "/home";
		}
		else {
			console.log("Error with guest login");
		}
	});
}

function register(event) {
	event.preventDefault();
	const fname = document.getElementById("fname").value;	
	const lname = document.getElementById("lname").value;
	const email = document.getElementById("email").value;
	const password = document.getElementById("password").value;
	const role = document.getElementById("type").value;
	
	fetch("/register-servlet", {
		method: "POST",
		headers: {
			"Content-type": "application/x-www-form-urlencoded"
		},
		body: `fname=${encodeURIComponent(fname)}&lname=${encodeURIComponent(lname)}&email=${encodeURIComponent(email)}&password=${encodeURIComponent(password)}&type=${encodeURIComponent(role)}`
	})
	.then(res => res.text())
	.then(text => {
		if(text.trim() === "success"){
			window.location.href = "/home";
		}
		else {
			document.getElementById("emailMsg").textContent = "Email already exists.";
		}
	})
	.catch(error => {
		console.log("Register failed!");
		console.error(error);
	});
}
const landingPage = document.getElementById('landing')
const loginPage = document.getElementById('loginSection')
const stockPage = document.getElementById('stockInfoPage')
const loggedInstockPage = document.getElementById('loggedInstockInfoPage')
const portfolio = document.getElementById('portfolio')
const favorites = document.getElementById('favorites')

function showLogin(){
    hideAll()
    display(loginPage)
}

function showLanding(){
    hideAll()
    display(landingPage)
}

function showPortfolio(){
    hideAll()
	loadPortfolio()
    display(portfolio)
}

function showFavorites(){
	hideAll()
	favorites.innerHTML = ""
	favorites.appendChild(createFavoriteTitle())
	loadFavorites()
    display(favorites)
}

function showStockInfo(loggedIn){
    hideAll()
    loggedIn ? display(loggedInstockPage) : display(stockPage)
}

function hideAll(){
    landingPage.style.display = 'none'
	loginPage.style.display = 'none'
	stockPage.style.display = 'none'
	loggedInstockPage.style.display = 'none'
	portfolio.style.display = 'none'
	favorites.style.display = 'none'
}

function display(element){
    element.style.display = 'flex'
}

function stockSearch(){
    var ticker = document.getElementById('stockSearchInput').value
	var loginStatus = localStorage.getItem("loggedIn") === "true" ? true : false
	var loggedIn = loginStatus ? "logged" : ""
	var UserID = loginStatus ? localStorage.getItem("UserID") : "none"
	fetch(url + '/stockSearch?' + new URLSearchParams({
		ticker: ticker,
		UserID: UserID
	}), {
        method: "GET"
    })
    .then(response => response.text())
    .then(response => {
		if(response.replace(/\s/g, "") === "NotFound"){
			alert("Stock not found")
			return
		}
		data = JSON.parse(response)[1]
		localStorage.setItem("ticker", data.ticker)
		localStorage.setItem("stockName", data.name)
		document.getElementById(loggedIn+'ticker').innerHTML = data.ticker
		document.getElementById(loggedIn+'stockName').innerHTML = data.name
		document.getElementById(loggedIn+'exchangeCode').innerHTML = data.exchangeCode
		document.getElementById(loggedIn+'exchangeCode').innerHTML = data.exchangeCode
		document.getElementById(loggedIn+'description').innerHTML = 'Start Date: ' + data.startDate + '<br><br>' + data.description
		
		data = JSON.parse(response)[0]
		document.getElementById(loggedIn+'highPrice').innerHTML = data.high
		document.getElementById(loggedIn+'lowPrice').innerHTML = data.low
		document.getElementById(loggedIn+'openPrice').innerHTML = data.open
		document.getElementById(loggedIn+'close').innerHTML = data.close
		document.getElementById(loggedIn+'volume').innerHTML = data.volume
		if(loggedIn){
			data = JSON.parse(response)[2]
			var market = data.bidPrice === null ? false : true
			if(market){
				document.getElementById('midPrice').innerHTML = data.mid
				document.getElementById('askPrice').innerHTML = data.askPrice
				document.getElementById('askSize').innerHTML = data.askSize
				document.getElementById('bidPrice').innerHTML = data.bidPrice
				document.getElementById('bidSize').innerHTML = data.bidSize	
				document.getElementById('marketStatus').style.background = "#00d50026"
				document.getElementById('marketStatus').innerHTML = "Market is Open"
				localStorage.setItem("market", "open")
			}else{
				document.getElementById('midPrice').innerHTML = '-'
				document.getElementById('askPrice').innerHTML = '-'
				document.getElementById('askSize').innerHTML = '-'
				document.getElementById('bidPrice').innerHTML = '-'
				document.getElementById('bidSize').innerHTML = '-'		
				var parse = Date.parse(data.timestamp)	
				var d = new Date(parse)
				var date = dateToYMD(d)	
				var time = dateToHMS(d)
				document.getElementById('marketStatus').style.background = "#fbc9c9"
				document.getElementById('marketStatus').innerHTML = "Market Closed on " + date + " " + time
				localStorage.setItem("market", "closed")
			}
			localStorage.setItem("price", data.last)
			var lastPrice = document.getElementById('lastPrice')
			var changeDIV = document.getElementById('change')
			var arrow = document.getElementById('arrow')
			var change = (data.last - data.prevClose).toFixed(2)
			var changePercentage = (change * 100 / data.prevClose).toFixed(2)
			if(change < 0){
				arrow.className = "arrowDown"
				lastPrice.style.color = "red" 
				changeDIV.style.color = "red" 
			}else{
				arrow.className = "arrowUp"
				lastPrice.style.color = "green" 
				changeDIV.style.color = "green" 
			}
			lastPrice.innerHTML = data.last
			changeDIV.innerHTML = change + "("	+ changePercentage + ")%"
			var d = new Date()
			var date = dateToYMD(d)	
			var time = dateToHMS(d)
			document.getElementById('currentTime').innerHTML = date +  " " + time
			data = JSON.parse(response)[3]
			if(data.Favorite === "yes"){
				starID === "star" ? (document.getElementById("star").id = "goldStar", starID = "goldStar") : null
			}else{
				starID === "star" ? null : (document.getElementById("goldStar").id = "star", starID = "star")
			}
		}
		showStockInfo(loggedIn)
	})
}	

function buyStocks(){
	var quantity = document.getElementById("quantityInput").value
	var price = parseFloat(localStorage.getItem("price"))
	var ticker = localStorage.getItem("ticker")
	var stockName = localStorage.getItem("stockName")
	//localStorage.getItem("market") === "closed"
	if(quantity < 1){
		alert("FAILED: Purchase not possible")
	}else if(localStorage.getItem("market") === "closed"){
		alert("FALILED: Market is closed")
	}else{
		var UserID = localStorage.getItem("UserID")
		fetch(url + "/buyStock?"+ new URLSearchParams({
			ticker: ticker,
			stockName: stockName,
			quantity: quantity,
			price: price,
			UserID: UserID
		}), {
			method: "GET"
		})
		.then(response => response.text())
		.then(response => {
			if(response.replace(/\s/g, "") === "Notenoughbalance"){
				alert("FAILED: Purchase not possible")
			}else{
				alert("SUCCESS: Executed purchase of " + quantity + " shares of " + ticker + " for $" + (quantity*price).toFixed(2))
			}
		})	
	}
}
var starID = "star"
function favoriteStock(){
	var star = document.getElementById(starID)
	var option
	var ticker = document.getElementById("loggedticker").innerHTML
	var name = document.getElementById("loggedstockName").innerHTML
	var UserID = localStorage.getItem("UserID")
	if(starID === "star"){
		star.id = "goldStar"
		starID = star.id
		option = "favorite"
	}else{
		star.id = "star"	
		starID = star.id
		option = "unfavorite"
	}
	fetch(url + "/favorite?"+ new URLSearchParams({
		option: option,
		ticker: ticker,
		name: name,
		UserID: UserID
	}), {
		method: "GET"
	})
	.then(response => response.text())
	.then(response => console.log(response))
}

function removeFavorite(elem){
	var UserID = localStorage.getItem("UserID")
	var ticker = elem.name
	fetch(url + "/favorite?"+ new URLSearchParams({
		option: "unfavorite",
		ticker: ticker,
		UserID: UserID
	}), {
		method: "GET"
	})
	.then(response => response.text())
	.then(response => {
		showFavorites()
	})
}
function loadFavorites(){
	var UserID = localStorage.getItem("UserID")
	fetch(url + "/getFavorites?UserID=" + UserID,{
		method: "GET"
	})
	.then(response => response.text())
	.then(response => {
		if(response.replace(/\s/g, "") === "NoFavorites"){
			alert("Currently you don't have any stocks in your favorites.")
			return	
		}else{
			var data = JSON.parse(response)
			var change, changePercentage, negative
			data.forEach(company => {
			    change = (company.last - company.prevClose).toFixed(2)
				negative = change < 0 ? true : false
				changePercentage = (change * 100 / company.prevClose).toFixed(2)
				change = change + "(" + changePercentage + ")%"
				createFavoriteItem(company, change, negative)
			})
		}
	})
}

function loadPortfolio(){
	var UserID = localStorage.getItem("UserID")
	fetch(url + "/getPurchases?UserID=" + UserID,{
		method: "GET"
	})
	.then(response => response.text())
	.then(response => {
		if(response.replace(/\s/g, "") !== "NoPurchases"){
			var data = JSON.parse(response)
			var dataBalance = data[data.length-1]
			document.getElementById("purchases").innerHTML = ""
			for(var i = 0; i < data.length-1; i++){
				createStockItem(data[i])
			}
			document.getElementById('cashBalance').innerHTML = dataBalance.balance
			document.getElementById('accountValue').innerHTML = dataBalance.accountValue	
		}
	})	
}

function portfolioBuyOrSell(elem){
	var ticker = elem.name
	var stockName = elem.previousElementSibling.name
	var buyOrSell = elem.previousElementSibling.childNodes
	buyOrSell = buyOrSell[0].checked ? "buy" : "sell"
	var quantity = elem.previousElementSibling.previousElementSibling.childNodes[1].value
	var UserID = localStorage.getItem("UserID")
	if(!quantity || quantity === "" || quantity < 1){
		alert("FAILED: Purchase not possible")
		return
	}
	fetch(url + "/portfolioBuyOrSell?"+ new URLSearchParams({
		ticker: ticker,
		stockName: stockName,
		buyOrSell: buyOrSell,
		quantity: quantity,
		UserID: UserID
	}),{
		method: "GET"
	})
	.then(response => response.text())
	.then(response => {
		var data = JSON.parse(response)
		if(data.status === "Success"){
			if(buyOrSell === "buy"){
				alert("SUCCESS: Executed purchase of " + quantity + " shares of " + ticker + " for $" + data.totalCost.toFixed(2))
			}else{
				alert("SUCCESS: Executed sale of " + quantity + " shares of " + ticker + " for $" + data.totalCost.toFixed(2))
			}			
			showPortfolio()
		}else if(buyOrSell === "buy"){
			alert("FAILED: Purchase not possible")
		}else{
			alert("FAILED: Sale not possible")
		}
	})
}

function displayStock(elem){
	var UserID = localStorage.getItem("UserID")
	var loggedIn =  "logged"
	fetch(url + '/stockSearch?' + new URLSearchParams({
		ticker: elem.name,
		UserID
	}), {
        method: "GET"
    })
    .then(response => response.text())
	.then(response => {
		data = JSON.parse(response)[1]
		localStorage.setItem("ticker", data.ticker)
		localStorage.setItem("stockName", data.name)
		document.getElementById(loggedIn+'ticker').innerHTML = data.ticker
		document.getElementById(loggedIn+'stockName').innerHTML = data.name
		document.getElementById(loggedIn+'exchangeCode').innerHTML = data.exchangeCode
		document.getElementById(loggedIn+'exchangeCode').innerHTML = data.exchangeCode
		document.getElementById(loggedIn+'description').innerHTML = 'Start Date: ' + data.startDate + '<br><br>' + data.description
		
		data = JSON.parse(response)[0]
		document.getElementById(loggedIn+'highPrice').innerHTML = data.high
		document.getElementById(loggedIn+'lowPrice').innerHTML = data.low
		document.getElementById(loggedIn+'openPrice').innerHTML = data.open
		document.getElementById(loggedIn+'close').innerHTML = data.close
		document.getElementById(loggedIn+'volume').innerHTML = data.volume
		
		data = JSON.parse(response)[2]
		var market = data.bidPrice === null ? false : true
		if(market){
			document.getElementById('midPrice').innerHTML = data.mid
			document.getElementById('askPrice').innerHTML = data.askPrice
			document.getElementById('askSize').innerHTML = data.askSize
			document.getElementById('bidPrice').innerHTML = data.bidPrice
			document.getElementById('bidSize').innerHTML = data.bidSize	
			document.getElementById('marketStatus').style.background = "#00d50026"
			document.getElementById('marketStatus').innerHTML = "Market is Open"
			localStorage.setItem("market", "open")
		}else{
			document.getElementById('midPrice').innerHTML = '-'
			document.getElementById('askPrice').innerHTML = '-'
			document.getElementById('askSize').innerHTML = '-'
			document.getElementById('bidPrice').innerHTML = '-'
			document.getElementById('bidSize').innerHTML = '-'		
			var parse = Date.parse(data.timestamp)	
			var d = new Date(parse)
			var date = dateToYMD(d)	
			var time = dateToHMS(d)
			document.getElementById('marketStatus').style.background = "#fbc9c9"
			document.getElementById('marketStatus').innerHTML = "Market Closed on " + date + " " + time
			localStorage.setItem("market", "closed")
		}
		localStorage.setItem("price", data.last)
		var lastPrice = document.getElementById('lastPrice')
		var changeDIV = document.getElementById('change')
		var arrow = document.getElementById('arrow')
		var change = (data.last - data.prevClose).toFixed(2)
		var changePercentage = (change * 100 / data.prevClose).toFixed(2)
		if(change < 0){
			arrow.className = "arrowDown"
			lastPrice.style.color = "red" 
			changeDIV.style.color = "red" 
		}else{
			arrow.className = "arrowUp"
			lastPrice.style.color = "green" 
			changeDIV.style.color = "green" 
		}
		lastPrice.innerHTML = data.last
		changeDIV.innerHTML = change + "("	+ changePercentage + ")%"
		var d = new Date()
		var date = dateToYMD(d)	
		var time = dateToHMS(d)
		document.getElementById('currentTime').innerHTML = date +  " " + time
		data = JSON.parse(response)[3]
		starID === "star" ? (document.getElementById("star").id = "goldStar", starID = "goldStar") : null
		showStockInfo(true)
	})
}

//source https://stackoverflow.com/questions/3552461/how-to-format-a-javascript-date
function dateToYMD(date) {
    var d = date.getDate();
    var m = date.getMonth() + 1; //Month from 0 to 11
    var y = date.getFullYear();
    return '' + y + '-' + (m<=9 ? '0' + m : m) + '-' + (d <= 9 ? '0' + d : d);
}

function dateToHMS(date) {
    var h = date.getHours();
    var m = date.getMinutes(); 
    var s = date.getSeconds();
    return '' + (h<=9 ? '0' + h : h) + ':' + (m<=9 ? '0' + m : m) + ':' + (s <= 9 ? '0' + s : s);
}












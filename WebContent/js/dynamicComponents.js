function createStockItem(data){
	var portfolio = document.getElementById('purchases')
	//overall wrapper
	var company = document.createElement("div")
	company.className = "companyPortfolio"
	//header
	var header = document.createElement("div")
	header.className = "companyPortfolioHeader"
	var a = document.createElement("a")
	a.className = "companyTicker"
	a.innerHTML = data.ticker
	header.appendChild(a)
	a = document.createElement("a")
	a.className = "companyName"
	a.innerHTML = data.name
	header.appendChild(a)
	company.appendChild(header)
	//numbers info
	var info = document.createElement("div")
	var leftInfo = document.createElement("div")
	info.className = "companyPortfolioInfo"
	leftInfo.className = "childPortfolioInfo"
	var textWrap = document.createElement("div")
	var text = document.createElement("p")
	text.innerHTML = "Quantity:"
	textWrap.appendChild(text)
	text = document.createElement("p")
	text.innerHTML = "Avg. Cost / Share:"
	textWrap.appendChild(text)
	text = document.createElement("p")
	text.innerHTML = "Total Cost:"
	textWrap.appendChild(text)
	text = document.createElement("p")
	leftInfo.appendChild(textWrap)
	textWrap = document.createElement("div")
	textWrap.className = "portolioValues"
	text.innerHTML = data.totalQuantity
	textWrap.appendChild(text)
	text = document.createElement("p")
	text.innerHTML = data.average
	textWrap.appendChild(text)
	text = document.createElement("p")
	text.innerHTML = data.totalCost
	textWrap.appendChild(text)
	text = document.createElement("p")
	leftInfo.appendChild(textWrap)
	info.appendChild(leftInfo)
	var rightInfo = document.createElement("div")
	rightInfo.className = "childPortfolioInfo"
	textWrap = document.createElement("div")
	text.innerHTML = "Change"
	textWrap.appendChild(text)
	text = document.createElement("p")
	text.innerHTML = "Current Price:"
	textWrap.appendChild(text)
	text = document.createElement("p")
	text.innerHTML = "Market Value:"
	textWrap.appendChild(text)
	text = document.createElement("p")
	rightInfo.appendChild(textWrap)
	textWrap = document.createElement("div")
	textWrap.className = "portolioValues"
	var color
	var arrowWrapper = document.createElement("div")
	arrowWrapper.className = "favArrowWrap"
	var arrow = document.createElement("div")
	if(data.last > data.average){
		color = "green"
		arrow.className = "portArrowUp"
		arrowWrapper.appendChild(arrow)
	}else if(data.last < data.average){
		color = "red"
		arrow.className = "portArrowDown"
		arrowWrapper.appendChild(arrow)
	}else{
		color = "black"
	}
	text.style.color = color
	text.innerHTML = Math.abs(data.change)
	arrowWrapper.appendChild(text)
	textWrap.appendChild(arrowWrapper)
	text = document.createElement("p")
	text.style.color = color
	text.innerHTML = data.last
	textWrap.appendChild(text)
	text = document.createElement("p")
	text.style.color = color
	text.innerHTML = data.marketValue
	textWrap.appendChild(text)
	rightInfo.appendChild(textWrap)
	info.appendChild(rightInfo)
	
	company.appendChild(info)
	//actions
	var action = document.createElement("div") 
	action.className = "companyPortfolioAction"
	var wrapper = document.createElement("div") 
	var label = document.createElement("label") 
	label.htmlFor = "quantity"
	label.innerHTML = "Quantity:"
	wrapper.appendChild(label)
	var input = document.createElement("input") 
	input.className = "quantityInput"
	input.name = "quantity"
	wrapper.appendChild(input)
	action.appendChild(wrapper)
	wrapper = document.createElement("div") 
	input = document.createElement("input") 
	label = document.createElement("label") 
	input.type = "radio"
	input.name = "option"
	input.value = "BUY"
	label.htmlFor = "BUY"
	label.innerHTML = "BUY"
	wrapper.appendChild(input)
	wrapper.appendChild(label)
	action.appendChild(wrapper)
	input = document.createElement("input") 
	label = document.createElement("label") 
	input.type = "radio"
	input.name = "option"	
	input.value = "SELL"
	input.style.marginLeft = "20px"
	label.htmlFor = "SELL"
	label.innerHTML = "SELL"
	wrapper.appendChild(input)
	wrapper.appendChild(label)
	wrapper.name = data.name
	action.appendChild(wrapper)
	input = document.createElement("button") 
	input.type = "button"
	input.className = "portfolioSubmit"
	input.innerHTML = "Submit"
	input.name = data.ticker
	input.onclick = function() {portfolioBuyOrSell(this)}
	action.appendChild(input)
	
	company.appendChild(action)
	portfolio.appendChild(company)
}

function createFavoriteItem(company, change, negative){
	var parent = document.getElementById('favorites')
	var favorite = document.createElement("div")
	var image = document.createElement("img")
	image.className = "deleteFavoriteBtn"
	image.src = "images/x2.png"
	image.alt = "x"
	image.onclick = function() {removeFavorite(this)}
	image.name = company.ticker
	favorite.appendChild(image)
	var favoriteClick = document.createElement("div")
	favoriteClick.className = "favoriteClick"
	favoriteClick.onclick = function() {displayStock(this)}
	favoriteClick.name = company.ticker
	favorite.className = "favorite"
	var wrapper = document.createElement("div")
	wrapper.className = "favoriteNameWrapper"
	var text = document.createElement("p")
	text.className = "favoriteTicker"
	text.innerHTML = company.ticker
	wrapper.appendChild(text)
	text = document.createElement("p")
	text.className = "favoriteName"
	text.innerHTML = company.name
	wrapper.appendChild(text)
	favoriteClick.appendChild(wrapper)
	wrapper = document.createElement("div")
	wrapper.className = "favoriteNumbers"
	wrapper.style.color = negative === true ? "red" : "green"
	text = document.createElement("p")
	text.className = "favoriteLastPrice"
	text.innerHTML = company.last
	wrapper.appendChild(text)
	var arrowWrapper = document.createElement("div")
	arrowWrapper.className = "favArrowWrap"
	var arrow = document.createElement("div")
	arrow.className = negative === true ? "favArrowDown" : "favArrowUp"
	arrowWrapper.appendChild(arrow)
	text = document.createElement("p")
	text.className = "favoriteChange"
	text.innerHTML = change
	arrowWrapper.appendChild(text)
	wrapper.appendChild(arrowWrapper)
	favoriteClick.appendChild(wrapper)
	favorite.appendChild(favoriteClick)
	parent.appendChild(favorite)
}

function createFavoriteTitle(){
	var title = document.createElement("p")
	title.id = "favoritesTitle"
	title.innerHTML = "My Favorites"
	return title
}





















function setColorsAllOddEven(colorOdd, colorEven) {
	fetch("http://localhost:8888/v1/hybrid/json/hexColor?hexColors=" + colorOdd + "," + colorEven + "," + colorOdd + "," + colorEven + "," + colorOdd + "," + colorEven + "," + colorOdd + "&transition=0&", {method: 'GET'}).catch(err => console.error(err));
	fetch("http://localhost:8888/v1/hybrid/json/putColor?id=AllOdd&hexColor=" + colorOdd + "&");
	fetch("http://localhost:8888/v1/hybrid/json/putColor?id=AllEven&hexColor=" + colorEven + "&");
}

function setColorsAll(colorOdd, colorEven) {
	fetch("http://localhost:8888/v1/hybrid/json/hexColor?hexColors=" + colorOdd + "&transition=0&storeName=Background&", {method: 'GET'}).catch(err => console.error(err));
	fetch("http://localhost:8888/v1/hybrid/json/putColor?id=AllOdd&hexColor=" + colorOdd + "&");
	fetch("http://localhost:8888/v1/hybrid/json/putColor?id=AllEven&hexColor=" + colorOdd + "&");
	fetch("http://localhost:8888/v1/hybrid/json/putColor?id=Dmx&hexColor=" + colorOdd + "&");
	fetch("http://localhost:8888/v1/hybrid/json/putColor?id=Deko&hexColor=" + colorOdd + "&");
}

function setColorsDmx(colorOdd, colorEven) {
	fetch("http://localhost:8888/v1/hybrid/json/hexColor?ids=15,29,21,1&hexColors=" + colorOdd + "&transition=0&storeName=Dmx&", {method: 'GET'}).catch(err => console.error(err));
}

function setColorsDeko(colorOdd, colorEven) {
	fetch("http://localhost:8888/v1/hybrid/json/hexColor?ids=Starwars,Rgbw,Bar&hexColors=" + colorOdd + "&transition=0&storeName=Deko&", {method: 'GET'}).catch(err => console.error(err));
}

function setColorsRgbw(colorOdd, colorEven) {
	fetch("http://localhost:8888/v1/hybrid/json/hexColor?ids=Rgbw&hexColors=" + colorOdd + "&transition=0&", {method: 'GET'}).catch(err => console.error(err));
}

function setColorsBar(colorOdd, colorEven) {
	fetch("http://localhost:8888/v1/hybrid/json/hexColor?ids=Bar&hexColors=" + colorOdd + "&transition=0&", {method: 'GET'}).catch(err => console.error(err));
}

function setColorsStarwars(colorOdd, colorEven) {
	fetch("http://localhost:8888/v1/hybrid/json/hexColor?ids=Starwars&hexColors=" + colorOdd + "&transition=0&", {method: 'GET'}).catch(err => console.error(err));
}

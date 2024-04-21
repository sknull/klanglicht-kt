function sleep(ms) {
	return new Promise(resolve => setTimeout(resolve, ms));
}

async function reqListener () {
	await sleep(200);
	window.location.reload(true);
}

function request(url, method = 'POST', data = '') {
	const client = new XMLHttpRequest();
	client.addEventListener("error", reqListener);
	client.addEventListener("load", reqListener);
	client.open(method, url);
	client.setRequestHeader("Content-type", "application/x-www-form-urlencoded; charset=UTF-8");
	client.send(data);
}

var elem = document.documentElement;

function openFullscreen() {
  if (elem.requestFullscreen) {
    elem.requestFullscreen();
  } else if (elem.webkitRequestFullscreen) { /* Safari */
    elem.webkitRequestFullscreen();
  } else if (elem.msRequestFullscreen) { /* IE11 */
    elem.msRequestFullscreen();
  }
}

function closeFullscreen() {
  if (document.exitFullscreen) {
    document.exitFullscreen();
  } else if (document.webkitExitFullscreen) { /* Safari */
    document.webkitExitFullscreen();
  } else if (document.msExitFullscreen) { /* IE11 */
    document.msExitFullscreen();
  }
}

function toggleFullScreen() {
  if (!document.fullscreenElement) {
      document.documentElement.requestFullscreen();
  } else {
    if (document.exitFullscreen) {
      document.exitFullscreen();
    }
  }
}

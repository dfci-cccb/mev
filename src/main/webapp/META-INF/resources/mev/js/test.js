if (d3) {
	test = document.createElement('div')
	test.innerHTML = "PASSED"
	document.getElementById("test.d3").appendChild(test)
} else {
	test = document.createElement('div')
	test.innerHTML = "FAILED"
	document.getElementById("test.d3").appendChild(test)
}

if (angular) {
	test = document.createElement('div')
	test.innerHTML = "PASSED"
	document.getElementById("test.angular").appendChild(test);
} else {
	test = document.createElement('div')
	test.innerHTML = "FAILED"
	document.getElementById("test.angular").appendChild(test);
}


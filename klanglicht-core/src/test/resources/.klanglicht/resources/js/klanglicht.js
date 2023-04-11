function multiParameterSet(colorOdd, colorEven) {
	var r1 = colorOdd.red;
	var g1 = colorOdd.green;
	var b1 = colorOdd.blue;
	var r2 = colorEven.red;
	var g2 = colorEven.green;
	var b2 = colorEven.blue;
	var multiParameterSet = {
	  "parameterSets" : [ {
		"@type" : "ParameterSet",
		"manufacturer" : "Cameo",
		"model" : "Flat PAR Can RGB 10 IR",
		"mode" : "6-Channel Mode",
		"baseChannel" : 1,
		"parameters" : {
		  "Macro" : {
			"channelType" : "MACRO",
			"name" : "Macro",
			"value" : {
			  "@type" : "DmxRawValue",
			  "value" : 0
			}
		  },
		  "MasterDimmer" : {
			"channelType" : "MASTER_DIMMER",
			"name" : "MasterDimmer",
			"value" : {
			  "@type" : "DmxRawValue",
			  "value" : 255
			}
		  },
		  "Rgb" : {
			"channelType" : "RGB",
			"name" : "Rgb",
			"value" : {
			  "@type" : "RGBColor",
			  "red" : r1,
			  "green" : g1,
			  "blue" : b1
			}
		  },
		  "Stroboscope" : {
			"channelType" : "STROBOSCOPE",
			"name" : "Stroboscope",
			"value" : {
			  "@type" : "DmxRawValue",
			  "value" : 0
			}
		  }
		}
	  }, {
		"@type" : "ParameterSet",
		"manufacturer" : "Cameo",
		"model" : "Flat PAR Can RGBW",
		"mode" : "8-Channel Mode",
		"baseChannel" : 7,
		"parameters" : {
		  "Macro" : {
			"channelType" : "MACRO",
			"name" : "Macro",
			"value" : {
			  "@type" : "DmxRawValue",
			  "value" : 0
			}
		  },
		  "MasterDimmer" : {
			"channelType" : "MASTER_DIMMER",
			"name" : "MasterDimmer",
			"value" : {
			  "@type" : "DmxRawValue",
			  "value" : 255
			}
		  },
		  "Rgbw" : {
			"channelType" : "RGBW",
			"name" : "Rgbw",
			"value" : {
			  "@type" : "RGBWColor",
			  "red" : r2,
			  "green" : g2,
			  "blue" : b2
			}
		  },
		  "SoundToLight" : {
			"channelType" : "SOUND_TO_LIGHT",
			"name" : "SoundToLight",
			"value" : {
			  "@type" : "DmxRawValue",
			  "value" : 0
			}
		  },
		  "Stroboscope" : {
			"channelType" : "STROBOSCOPE",
			"name" : "Stroboscope",
			"value" : {
			  "@type" : "DmxRawValue",
			  "value" : 0
			}
		  }
		}
	  }, {
		"@type" : "ParameterSet",
		"manufacturer" : "Cameo",
		"model" : "Flat PAR Can RGB 10 IR",
		"mode" : "6-Channel Mode",
		"baseChannel" : 15,
		"parameters" : {
		  "Macro" : {
			"channelType" : "MACRO",
			"name" : "Macro",
			"value" : {
			  "@type" : "DmxRawValue",
			  "value" : 0
			}
		  },
		  "MasterDimmer" : {
			"channelType" : "MASTER_DIMMER",
			"name" : "MasterDimmer",
			"value" : {
			  "@type" : "DmxRawValue",
			  "value" : 255
			}
		  },
		  "Rgb" : {
			"channelType" : "RGB",
			"name" : "Rgb",
			"value" : {
			  "@type" : "RGBColor",
			  "red" : r1,
			  "green" : g1,
			  "blue" : b1
			}
		  },
		  "Stroboscope" : {
			"channelType" : "STROBOSCOPE",
			"name" : "Stroboscope",
			"value" : {
			  "@type" : "DmxRawValue",
			  "value" : 0
			}
		  }
		}
	  }, {
		"@type" : "ParameterSet",
		"manufacturer" : "Cameo",
		"model" : "Flat PAR Can RGBW",
		"mode" : "8-Channel Mode",
		"baseChannel" : 21,
		"parameters" : {
		  "Macro" : {
			"channelType" : "MACRO",
			"name" : "Macro",
			"value" : {
			  "@type" : "DmxRawValue",
			  "value" : 0
			}
		  },
		  "MasterDimmer" : {
			"channelType" : "MASTER_DIMMER",
			"name" : "MasterDimmer",
			"value" : {
			  "@type" : "DmxRawValue",
			  "value" : 255
			}
		  },
		  "Rgbw" : {
			"channelType" : "RGBW",
			"name" : "Rgbw",
			"value" : {
			  "@type" : "RGBWColor",
			  "red" : r2,
			  "green" : g2,
			  "blue" : b2
			}
		  },
		  "SoundToLight" : {
			"channelType" : "SOUND_TO_LIGHT",
			"name" : "SoundToLight",
			"value" : {
			  "@type" : "DmxRawValue",
			  "value" : 0
			}
		  },
		  "Stroboscope" : {
			"channelType" : "STROBOSCOPE",
			"name" : "Stroboscope",
			"value" : {
			  "@type" : "DmxRawValue",
			  "value" : 0
			}
		  }
		}
	  }, {
		"@type" : "ParameterSet",
		"manufacturer" : "Cameo",
		"model" : "Flat PAR Can RGB 10 IR",
		"mode" : "6-Channel Mode",
		"baseChannel" : 29,
		"parameters" : {
		  "Macro" : {
			"channelType" : "MACRO",
			"name" : "Macro",
			"value" : {
			  "@type" : "DmxRawValue",
			  "value" : 0
			}
		  },
		  "MasterDimmer" : {
			"channelType" : "MASTER_DIMMER",
			"name" : "MasterDimmer",
			"value" : {
			  "@type" : "DmxRawValue",
			  "value" : 255
			}
		  },
		  "Rgb" : {
			"channelType" : "RGB",
			"name" : "Rgb",
			"value" : {
			  "@type" : "RGBColor",
			  "red" : r1,
			  "green" : g1,
			  "blue" : b1
			}
		  },
		  "Stroboscope" : {
			"channelType" : "STROBOSCOPE",
			"name" : "Stroboscope",
			"value" : {
			  "@type" : "DmxRawValue",
			  "value" : 0
			}
		  }
		}
	  }, {
		"@type" : "ParameterSet",
		"manufacturer" : "Renkforce",
		"model" : "Moving Head GM107",
		"mode" : "13-Channel Mode",
		"baseChannel" : 35,
		"parameters" : {
		  "AutoMode" : {
			"channelType" : "AUTO_MODE",
			"name" : "AutoMode",
			"value" : {
			  "@type" : "DmxRawValue",
			  "value" : 0
			}
		  },
		  "ColorMode" : {
			"channelType" : "COLOR_MODE",
			"name" : "ColorMode",
			"value" : {
			  "@type" : "DmxRawValue",
			  "value" : 0
			}
		  },
		  "Rgbw" : {
			"channelType" : "RGBW",
			"name" : "Rgbw",
			"value" : {
			  "@type" : "RGBWColor",
			  "red" : r2,
			  "green" : g2,
			  "blue" : b2
			}
		  },
		  "Rotation" : {
			"channelType" : "ROTATION",
			"name" : "Rotation",
			"value" : {
			  "@type" : "Rotation",
			  "panDegrees" : 180.0,
			  "tiltDegrees" : 42.065
			}
		  },
		  "Speed" : {
			"channelType" : "SPEED",
			"name" : "Speed",
			"value" : {
			  "@type" : "DmxRawValue",
			  "value" : 0
			}
		  },
		  "Stroboscope" : {
			"channelType" : "STROBOSCOPE",
			"name" : "Stroboscope",
			"value" : {
			  "@type" : "DmxRawValue",
			  "value" : 0
			}
		  }
		}
	  }, {
		"@type" : "ParameterSet",
		"manufacturer" : "Renkforce",
		"model" : "Moving Head GM107",
		"mode" : "13-Channel Mode",
		"baseChannel" : 48,
		"parameters" : {
		  "AutoMode" : {
			"channelType" : "AUTO_MODE",
			"name" : "AutoMode",
			"value" : {
			  "@type" : "DmxRawValue",
			  "value" : 0
			}
		  },
		  "ColorMode" : {
			"channelType" : "COLOR_MODE",
			"name" : "ColorMode",
			"value" : {
			  "@type" : "DmxRawValue",
			  "value" : 0
			}
		  },
		  "Rgbw" : {
			"channelType" : "RGBW",
			"name" : "Rgbw",
			"value" : {
			  "@type" : "RGBWColor",
			  "red" : r2,
			  "green" : g2,
			  "blue" : b2
			}
		  },
		  "Rotation" : {
			"channelType" : "ROTATION",
			"name" : "Rotation",
			"value" : {
			  "@type" : "Rotation",
			  "panDegrees" : 462.6,
			  "tiltDegrees" : 28.640000000000004
			}
		  },
		  "Speed" : {
			"channelType" : "SPEED",
			"name" : "Speed",
			"value" : {
			  "@type" : "DmxRawValue",
			  "value" : 0
			}
		  },
		  "Stroboscope" : {
			"channelType" : "STROBOSCOPE",
			"name" : "Stroboscope",
			"value" : {
			  "@type" : "DmxRawValue",
			  "value" : 0
			}
		  }
		}
	  } ]
	}
	return multiParameterSet;
}

function setMultiParameterSet(multiParameterSet) {
	const options = {
		method: 'POST',
		body: JSON.stringify(multiParameterSet),
		headers: {
			'Content-Type': 'application/json;charset=UTF-8'
		}
	}
	fetch('http://localhost:8888/v1/setMultiParameterSet', options)
		.then(res => res.json())
		.then(res => console.log(res))
		.catch(err => console.error(err));
}

function setColorsDmx(colorOdd, colorEven) {
	var colors = colorOdd + "," + colorEven + "," + colorOdd + "," + colorEven + "," + colorOdd + "," + colorEven + "," + colorOdd
	var url = "http://localhost:8888/v1/colors?hexColors=" + colors + "&id=Dmx&"
	const options = {
		method: 'GET',
	}
	fetch(url, options)
		.then(res => res.json())
		.then(res => console.log(res))
		.catch(err => console.error(err));
}

function setColorsDeko(colorOdd, colorEven) {
	var url = "http://localhost:8888/v1/shelly/hexColor?turnOn=true&ipAddresses=192.168.178.54,192.168.178.48,192.168.178.55&hexColors=" + colorOdd + "&transitions=0&gains=100,1,50&ids=Starwars,Rgbw,Bar"
	const options = {
		method: 'GET',
	}
	fetch(url, options)
		.then(res => res.json())
		.then(res => console.log(res))
		.catch(err => console.error(err));
}

function setColorsRgbw(colorOdd, colorEven) {
	var url = "http://localhost:8888/v1/shelly/hexColor?turnOn=true&ipAddresses=192.168.178.48&hexColors=" + colorOdd + "&transitions=0&gains=1&ids=Rgbw&"
	const options = {
		method: 'GET',
	}
	fetch(url, options)
		.then(res => res.json())
		.then(res => console.log(res))
		.catch(err => console.error(err));
}

function setColorsBar(colorOdd, colorEven) {
	var url = "http://localhost:8888/v1/shelly/hexColor?turnOn=true&ipAddresses=192.168.178.55&hexColors=" + colorOdd + "&transitions=0&gains=50&ids=Bar&"
	const options = {
		method: 'GET',
	}
	fetch(url, options)
		.then(res => res.json())
		.then(res => console.log(res))
		.catch(err => console.error(err));
}

function setColorsStarwars(colorOdd, colorEven) {
	var url = "http://localhost:8888/v1/shelly/hexColor?turnOn=true&ipAddresses=192.168.178.54&hexColors=" + colorOdd + "&transitions=0&gains=100&ids=Starwars"
	const options = {
		method: 'GET',
	}
	fetch(url, options)
		.then(res => res.json())
		.then(res => console.log(res))
		.catch(err => console.error(err));
}
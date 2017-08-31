'use strict';

//identifies the type of device the user is using. deviceAgent equals some text which tells you which device it is
var deviceAgent = navigator.userAgent.toLowerCase();

//isTouchDevice results in either a 'true' or a 'false' depending on whether the device matches any of the types below or not
var isTouchDevice = deviceAgent.match(/(iphone|ipod|ipad)/) ||
    deviceAgent.match(/(android)/) ||
    deviceAgent.match(/(iemobile)/) ||
    deviceAgent.match(/iphone/i) ||
    deviceAgent.match(/ipad/i) ||
    deviceAgent.match(/ipod/i) ||
    deviceAgent.match(/blackberry/i) ||
    deviceAgent.match(/bada/i);

//either turns on the class 'touch' or the class 'notouch' for the main html element based on if isTouchDevice true or false
if (isTouchDevice) {
    $('html').addClass('touch');
} else {
    $('html').addClass('notouch');
};

//these functions get executed upon page load
$(function() {
	//this function turns a dark background on when clicking the sandwich menu bar button in the navbar, if certain criteria are met
  $('.navbar-toggle').click(function(){
        var navbarmainElement = document.getElementById("navbarmain");
		var navbarElement = document.getElementById("navbar");
		var stop = Math.round($(window).scrollTop());
	  	if(navbarmainElement){
			if(navbarElement.classList.contains("in")){
				if(stop<=125){
					navbarmainElement.style.backgroundColor = 'transparent';
			  	}
		  	}else{
			  	navbarmainElement.style.backgroundColor = 'rgba(55, 58, 68, 0.95)';
		  	}
	  	}
  	})
	
	//this function turns the expanded part of the navbar off when you click an option that has the class '.navbar-close', which I added to the options which are an anchor (starting with #)
	$('.close-navbar').click(function(){
        var navbarElement = document.getElementById("navbar");
	  	if(navbarElement){
			if(navbarElement.classList.contains("in")){
				navbarElement.className = "navbar-collapse collapse";
			}
		}
  	})

	//this function controls the scrolling animation when you press an anchor option in the navbar (starting with #)
    $('a[href*="#"]:not([href*="tab"])').click(function() {
        if (location.pathname.replace(/^\//, '') == this.pathname.replace(/^\//, '') && location.hostname == this.hostname){
            var target = $(this.hash);
            target = target.length ? target : $('[name=' + this.hash.slice(1) + ']');
            if (target.length) {
                $('html, body').animate({
                    scrollTop: target.offset().top
                }, 1000);
                return false;
            }
        }
    })

});

//this function activates every time the window scrolls and when the scroll goes beyond 125 pixels (because mainbottom = 125, but could be anything) it turns on the dark background for the navbar, if certain criteria are met. It also controls the padding around the navbar.
var mainbottom = 125;
$(window).on('scroll', function() {
	var stop = Math.round($(window).scrollTop());
	var navbarmainElement = document.getElementById("navbarmain");
	var navbarElement = document.getElementById("navbar");
    if (stop > mainbottom) {
		if(navbarmainElement){
			navbarmainElement.style.backgroundColor = 'rgba(55, 58, 68, 0.95)';
			navbarmainElement.style.padding = '1rem 1.5rem';  
		}
    } else {
		if(navbarElement){
			if(navbarElement.classList.contains("in")){
				if(navbarElement.offsetWidth > 925){
					navbarmainElement.style.padding = '2.5rem 5rem 2.5rem 3rem'; 
				}else{
					navbarmainElement.style.padding = '2.5rem 3rem 2.5rem 3rem'; 
				}
				
			}else{
				navbarmainElement.style.backgroundColor = 'transparent'; 
				if(navbarmainElement.offsetWidth > 925){
					navbarmainElement.style.padding = '2.5rem 5rem 2.5rem 3rem'; 
				}else{
					navbarmainElement.style.padding = '2.5rem 3rem 2.5rem 3rem'; 
				}
			}
		}
    }
});
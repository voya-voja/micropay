
function isWindowValid(showError)
{
	if (window != top)
		return true;
	if (showError)
		alert("Incorrect browser window");
	return false;
}
function fixURL(URL, winName)
{
	if (top.lang)
		URL += (URL.indexOf('?') < 0 ? '?lang=' : '&lang=') + top.lang;
	if (winName)
		URL += (URL.indexOf('?') < 0 ? '?win=' : '&win=') + winName;
	else if (top.name)
		URL += (URL.indexOf('?') < 0 ? '?win=' : '&win=') + top.name;
	return URL;
}

//Standardize location and width for all secondary windows open 

var popMach2Window = null;
var popResearch2Window = null;
var popResearchWindow = null;
var popHelpWindow = null;

function closePopWin(popW){	// close pop-up window if it is open, 
				// workaround for when window is minimized but not reopened
    if(popW != null) if(!popW.closed) popW.close()
  }

function sonlinePopWin(winURL, winName){
  //alert(winURL);
  var winFeatures = "resizable=yes,scrollbars=yes,location=no,status=no";

  var popWin = null;

  if(winName == 'Mach2Window'){
    closePopWin(popMach2Window);
    popMach2Window = openPopWin( winURL , winName, 830, 600, winFeatures, 0, 0);
    popWin = popMach2Window;
  }
  else if( winName == 'Research2Window'){
    closePopWin(popResearch2Window);
    winFeatures += ",toolbar=yes";
    popResearch2Window = openPopWin( winURL , winName, 830, 600, winFeatures, -10, 0);
    popWin = popResearch2Window;
  }
  else if( winName == 'ResearchWindow'){
    closePopWin(popResearchWindow);
    winFeatures += ",toolbar=yes";
    popResearchWindow = openPopWin( winURL , winName, 830, 600, winFeatures, 0, -110);
    popWin = popResearchWindow;
  }
  else if ( winName == 'HelpWindow'){
    closePopWin(popHelpWindow);
    popHelpWindow = openPopWin( winURL , winName, 700, 650, winFeatures, "cen", "cen");
    popWin = popHelpWindow;
  }
  else{
    //alert('Unknown window name ' + winName);
    return;
  }
  popWin.focus();
}

function openPopWin(winURL, winName, winWidth, winHeight, winFeatures, winLeft,winTop){
  var popWin = null;
  var d_winLeft = 0;  // default, pixels from screen left to window left
  var d_winTop = 0;   // default, pixels from screen top to window top

  var location = null;
  
  if (openPopWin.arguments.length == 7){  // location specified
    location = getLocation(winWidth, winHeight, winLeft, winTop);
  }
  else{
    location = getLocation(winWidth, winHeight, d_winLeft, d_winTop);
  }

  var settings = winFeatures + ",width=" + winWidth + ",height=" + winHeight + location;
 
  //alert( settings ); 
  popWin = window.open(winURL, winName, settings);
  return popWin;
  }

function getLocation(winWidth, winHeight, winLeft, winTop){
  NS4=(document.layers) ? true : false;
  IE4=(document.all)?true:false;

  var winLocation = "";
  if ( !NS4 && !IE4 )
     return winLocation; 

  if (winLeft < 0){
    winLeft = screen.width - winWidth + winLeft
  }
  if (winTop < 0){
    winTop = screen.height - winHeight + winTop
  }
  if (winTop == "cen")
    winTop = (screen.height - winHeight)/2 - 10
  if (winLeft == "cen")
    winLeft = (screen.width - winWidth)/2
 
  if (winLeft>=0 & winTop>=0){
    if( navigator.appName.substring(0,9)=="Netscape")
      winLocation =  ",screenX=" + winLeft + ",screenY=" + winTop; 
    else
      winLocation =  ",left=" + winLeft + ",top=" + winTop;
  }
  else
    winLocation = ""

  return winLocation;
  }

function surfToMach2Window(URL, features)
{
	sonlinePopWin(fixURL(URL, 'Mach2Window') + '&parent=' + top.name+'&MACHII_WINDOW=Y', 'Mach2Window');
}

function surfToAlertsManagementWindow(URL, features)
{
	sonlinePopWin(fixURL(URL, 'Research2Window') + '&parent=' + top.name, 'Research2Window');
}

function surfToAlertsManagement(URL, features)
{
	surfToAlertsManagementWindow(URL);
}

function surfToChartsWindow(URL, features)
{
	sonlinePopWin(fixURL(URL, 'Research2Window') + '&parent=' + top.name, 'Research2Window');
}

function surfToCharts(URL, features)
{
	surfToChartsWindow(URL);
}

function surfToMutualFundResearchWindow(URL, features)
{
	surfToMach2Window(URL, features);
	//ISSfo09684
	//sonlinePopWin(fixURL(URL, 'ResearchWindow') + '&parent=' + top.name, 'ResearchWindow');
}

function surfToMutualFundResearch(URL, features)
{
	surfToMutualFundResearchWindow(URL);
}

function surfToChineseNewsWindow(URL, features)
{
	sonlinePopWin(fixURL(URL, 'ResearchWindow') + '&parent=' + top.name, 'ResearchWindow');
}

function surfToChineseNews(URL, features)
{
	surfToChineseNewsWindow(URL);

}

function surfToResearchWindow(URL, features)
{
	sonlinePopWin(fixURL(URL, 'ResearchWindow') + '&parent=' + top.name, 'ResearchWindow');
}

function surfToResearch(URL, features)
{
	surfToResearchWindow(URL);
}

function surfToTechAlertsWindow(URL, features)
{
	sonlinePopWin(fixURL(URL, 'ResearchWindow') + '&parent=' + top.name, 'ResearchWindow');
}

function surfToTechAlerts(URL, features)
{
	surfToTechAlertsWindow(URL);
}

function surfToTechManagementWindow(URL, features)
{
	sonlinePopWin(fixURL(URL, 'ResearchWindow') + '&parent=' + top.name, 'ResearchWindow');
}

function surfToTechManagement(URL, features)
{
	surfToTechManagementWindow(URL);
}

function surfToTechRecentWindow(URL, features)
{
	sonlinePopWin(fixURL(URL, 'ResearchWindow') + '&parent=' + top.name, 'ResearchWindow');
}

function surfToTechRecent(URL, features)
{
	surfToTechRecentWindow(URL);
}

function surfToHelpWindow(URL, type)
{
	sonlinePopWin(fixURL(URL, 'HelpWindow') + '&parent=' + top.name, 'HelpWindow');
/*
	if ( (type == "L") || (type == "D") ) {
	window.open(fixURL(URL, 'HelpWindow') + '&parent=' + top.name, 'HelpWindow',
		'scrollbars=yes,resizable=yes,width=664,height=340,screenX=100,screenY=100,location=no,status=no').focus();
	} else {
	window.open(fixURL(URL, 'HelpWindow') + '&parent=' + top.name, 'HelpWindow',
		'scrollbars=yes,resizable=yes,width=685,height=' + Math.min(screen.availHeight-60,700)
			+ ',screenX=0,screenY=0,location=no,status=no').focus();

	}
*/
}

function surfToHelp(forPage, suffix, type)
{
	var helpURL = '/aw/jsp/awCommon/awHelp.jsp?';

	if (type.length > 0)
		helpURL += 'type=' + type + '&';
	if (suffix.length > 0)
		helpURL += 'suffix=' + suffix + '&';
	if (forPage.length == 0)
		forPage = location.pathname;
        surfToHelpWindow(helpURL + 'for=' + forPage, type);
}

function surfToPrintFriendly(URL)
{
	var printURL = '/aw/jsp/awCommon/awPrintFriendly.jsp?';
	var features = 'toolbar=yes,location=no,status=no,menubar=yes,scrollbars=yes,resizable=yes,screenX=0,screenY=0,width=760,height=' + Math.min(screen.availHeight-120,600);

	window.open(printURL+URL,'PrintFriendly', features).focus();
}

function surfToMach2Default(URL)
{
	surfToMach2Window(URL, 'scrollbars=yes,resizable=yes,width=800,height=' + Math.min(screen.availHeight-60,600)
			+ ',screenX=0,screenY=0,location=no,status=no');
}

function surfToMach3Default(URL)
{
	surfToMach3Window(URL, 'scrollbars=yes,resizable=yes,width=800,height=' + Math.min(screen.availHeight-60,600)
			+ ',screenX=0,screenY=0,location=no,status=no');
}
function surfToMach3Window(URL, features)
{
	window.open(fixURL(URL, 'Mach3Window') + '&parent=' + top.name+'&MACHII_WINDOW=Y', 'Mach3Window', features).focus();
}

function surfToMach2(URL, features)
{
	surfToMach2Window(URL, features + ',scrollbars=yes,resizable=yes,screenX=0,screenY=0,location=no,status=no');
}

function surfToMach3(URL, features)
{
	surfToMach3Window(URL, features + ',scrollbars=yes,resizable=yes,screenX=0,screenY=0,location=no,status=no');
}

function surfInSameWindow(URL)
{
	window.location.href = fixURL(URL, (top.name ? top.name : 'FMBWin1'));
}


function makeLinkPortfolio( param1 , param2 , param3 )
{
var fix = fixURL('?');
var URL = "<a href =/aw/jsp/awPortfolio/awPrevClose.jsp";

var myURL = URL+'?'+'NAME='+escape(param1)+'&'+'ID='+escape(param2)+'&'+'CASH=0.0&'+fix.substring(1,fix.length)+'>';
document.write( myURL);
}


function surfToNewInMenu(menu, widthHeight)
{
	if (menu.selectedIndex == -1) window.alert('No items selected');

	if (widthHeight.length == 0)
		widthHeight = 'width=800,height=600';

	var features = 'scrollbars=yes,resizable=yes,screenX=0,screenY=0,location=no,status=no,';
	var myindex = menu.selectedIndex;
	var menuItemUrl = menu.options[myindex].value;
	var menuItemRef = '';
	if (menuItemUrl.charAt(0) == '[')
	{
		var pos = menuItemUrl.indexOf(']');
		if (pos > 0)
		{
			menuItemRef = menuItemUrl.substring(1, pos);
			menuItemUrl = menuItemUrl.substring(pos+1, menuItemUrl.length);
		}
	}

	if (menuItemRef == 'mach2'
		|| menuItemRef == 'DQ'
		|| menuItemRef == 'OptionQuoteC'
		|| menuItemRef == 'News'
		|| menuItemRef == 'CorporateInfo'
		|| menuItemRef == 'TransHistory'
		|| menuItemRef == 'Preferences'
		|| menuItemRef == 'UserInbox')
		surfToMach2Window(menuItemUrl, features + widthHeight);
	else if( menuItemRef == 'ALERTS' )
		surfToAlertsManagement(menuItemUrl, features + widthHeight);
	else if( menuItemRef == 'TechAlerts' )
		surfToTechAlerts(menuItemUrl, features + widthHeight);
	else if( menuItemRef == 'TechManagement' )
		surfToTechManagement(menuItemUrl, features + widthHeight);
	else if( menuItemRef == 'TechRecent' )
		surfToTechRecent(menuItemUrl, features + widthHeight);
	else if( menuItemRef == 'Charts' )
		surfToCharts(menuItemUrl, features + widthHeight);
	else if( menuItemRef == 'MFInfo' )
		surfToMutualFundResearch(menuItemUrl, features + widthHeight);
	else if( menuItemRef == 'ChineseNews' )
		surfToChineseNews(menuItemUrl, features + widthHeight);
	else if( menuItemRef == 'Research' )
		surfToResearch(menuItemUrl, features + widthHeight);
	else if (menuItemUrl != '')
		parent.mainWindow.location.href = fixURL(menuItemUrl);
}

function surfToIEClient(params)
{
	var w;
	if (typeof window.top.IEClientWin != 'undefined' && !window.top.IEClientWin.closed) {
		w = window.top.IEClientWin;
		w.location.href = '/aw/jsp/awBroker/awBrokerJump.jsp?' + params;
	}
	else {
		w = window.open('/aw/jsp/awBroker/awBrokerJump.jsp?' + params, 'IEClient');
		window.top.IEClientWin = w;
	}
	w.focus();
}

function closeIEClient()
{
	if (window.top.name == 'IEClient') {
		showIEHome();
		window.top.close();
	}
	else if (typeof window.top.IEClientWin != 'undefined' && !window.top.IEClientWin.closed)
		window.top.IEClientWin.close();
}

function showIEHome()
{
	if (typeof window.top.opener != 'undefined' && !window.top.opener.top.closed) {
		window.top.opener.location.href = '/aw/jsp/awBroker/awBrokerSearch.jsp?win=' + window.top.opener.top.name;
		window.top.opener.top.focus();
	}
}

function signOffIESite()
{
	if (window.top.name == 'IEClient') {
		if (typeof window.top.opener != 'undefined' && !window.top.opener.top.closed) {
			window.top.opener.top.location.href = '/aw/jsp/awBroker/awBrokerHome.jsp?action=signoff';
			window.top.opener.top.focus();
			window.top.close();
		}
		else
			window.top.location.href = '/aw/jsp/awBroker/awBrokerHome.jsp?action=signoff';
	}
	else {
		closeIEClient();
		window.top.location.href = '/aw/jsp/awBroker/awBrokerHome.jsp?action=signoff';
	}
}
function staticPage(URL)
{
   winAttributes = "toolbar=1,menubar=0,scrollbars=1,width=540,height=450,resizable=yes";
   StaticWindow = window.open(URL, 'HelpWindow', winAttributes );
   StaticWindow.focus();
}


function surfMainFromMach2(URL)
{  if(window.top.opener)
   {  window.top.opener.location = URL;
      window.top.opener.focus();
   }else
   {  window.open(URL).focus();
   }
}

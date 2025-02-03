/* Toggle between adding and removing the "responsive" class to topnav when the user clicks on the icon */
function myFunction() {
    var x = document.getElementById("myTopnav");
    if (x.className === "topnav") {
        x.className += " responsive";
    } else {
        x.className = "topnav";
    }
} 



function showContent(anchorId) {
    var topFrame = window.parent.document;
     topFrame.getElementById("content").innerHTML='<iframe id="contentObject" src="'+anchorId+'.html" style="width:100%;height:1200px" frameborder="0" scrolling="no" onload="resizeFrame()"></object>';
     var selectedClasses = topFrame.getElementsByClassName("selected");
     for (var i = 0; i < selectedClasses.length; i++) {
         selectedClasses[i].classList.remove("selected");
     }
     if (anchorId) {
        var anchor = topFrame.getElementById(anchorId);
        anchor.className += " selected";
     }
     var x = topFrame.getElementById("myTopnav");
     x.className = "topnav";
     resizeFrame();
}

function resizeFrame() {
    var object = parent.document.getElementById("contentObject");
    var size = object.style.height=object.contentWindow.document.body.scrollHeight;
    object.style.height=size+ 'px';
    console.log("resize: "+size);
}

function parseQuery(query) {
  var vars = query.split("&");
  var query_string = {};
  for (var i = 0; i < vars.length; i++) {
    var pair = vars[i].split("=");
    // If first entry with this name
    if (typeof query_string[pair[0]] === "undefined") {
      query_string[pair[0]] = decodeURIComponent(pair[1]);
      // If second entry with this name
    } else if (typeof query_string[pair[0]] === "string") {
      var arr = [query_string[pair[0]], decodeURIComponent(pair[1])];
      query_string[pair[0]] = arr;
      // If third or later entry with this name
    } else {
      query_string[pair[0]].push(decodeURIComponent(pair[1]));
    }
  }
  return query_string;
}

function getNavParam() {
    var query = window.top.location.search.substring(1);
    if (query) {
        var parsedQuery = parseQuery(query);
        return parsedQuery["nav"];
    }
    return null;
}

function ensureNavBarVisible(navId) {
    var myTopNav = window.top.document.getElementById("myTopnav");
    if (!(myTopNav)) {
        var newURL = "index.html?nav="+navId;
        window.top.location.href = newURL;
        resizeFrame();
    }
}

function checkNavParam() {
    var param = getNavParam();
    if (param) {
        showContent(param);
    } else {
        showContent('home');
    }
}

function showEmail() {
     var span = document.getElementById("email");
     var txt = "info;x;underdocx.org";
     txt = txt.replace(";x;", "@");
     span.innerHTML=txt;
}
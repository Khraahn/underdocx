var menuEntries=[
  {id:"home", title:"Home", file:"https://underdocx.org"},
  {id:"demo", title:"Demo", file:"demo.html"},
  {id:"gettingstarted", title:"Getting Started", file:"gettingstarted.html"},
  {id:"guide", title:"User Guide", file:"guide.html"},
  {id:"contact", title:"Contact", file:"contact.html"},
]

function buildNavBar() {
    let innerContent = "";
    for(i = 0; i < menuEntries.length; i++) {
        innerContent += "<a href=\""+menuEntries[i].file+"\">"+menuEntries[i].title+"</a>";
    }
    innerContent += "<a href=\"javascript:void(0);\" class=\"icon\" onclick=\"myFunction()\"><img src=\"images/menu-icon.png\" class=\"iconimage\"/></a>"
    innerContent += "<img src=\"images/logo_white.svg\" class=\"logo\"/>"
    topNav = document.getElementById("myTopnav").innerHTML=innerContent;
}


/* Toggle between adding and removing the "responsive" class to topnav when the user clicks on the icon */
function myFunction() {
    var x = document.getElementById("myTopnav");
    if (x.className === "topnav") {
        x.className += " responsive";
    } else {
        x.className = "topnav";
    }
} 


function showEmail() {
     var span = document.getElementById("email");
     var txt = "info;x;underdocx.org";
     txt = txt.replace(";x;", "@");
     span.innerHTML=txt;
}
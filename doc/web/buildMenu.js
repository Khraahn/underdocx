function buildMenu(containerId) {

  const getLevel = (element) => {
    if (!("level" in element.dataset)) {
      let tagName = element.tagName
      let levelStr = tagName.replace("h", "").replace("H", "");
      let level = parseInt(levelStr)-1
      element.dataset.level = level;
      return level;
    } else {
      return parseInt(element.dataset.level)
    }
  }

  const getText = (element) => {
    if (!("title" in element.dataset)) {
      let titel = element.textContent;
      element.dataset.titel = titel;
      return titel;
    } else {
      return element.dataset.titel
    } 
  }

  const getHierarchyString = (element) => {
    if (!("hierarchy" in element.dataset)) {
      element.dataset.hierarchy = "";
    }
    return element.dataset.hierarchy
  }

  const setHierarchyString = (element, str) => {
    element.dataset.hierarchy = str;
  }

  const isHigherOrder = (element, compateTo) => {
    return getLevel(element) < compateTo
  }

  const isHigherOrEqualOrder = (element, compateTo) => {
    return getLevel(element) <= compateTo
  }

  const isLowerOrder = (element, compateTo) => {
    return getLevel(element) > compateTo
  }

  const isLowerOrEqualOrder = (element, compateTo) => {
    return getLevel(element) >= compateTo
  }

  const isEqualOrder = (element, compateTo) => {
    return getLevel(element) == compateTo
  }



  const createHierarchy = (hElements) => {
    let startLevel = 1;
    for(currentLevel = startLevel; currentLevel <= 5; currentLevel++) {
      let counter = 0;
      for(hElementIndex = 0; hElementIndex < hElements.length; hElementIndex++) {
        let hElement = hElements[hElementIndex];
        console.log("currentLevel: "+currentLevel+", hElementLevel: "+getLevel(hElement)+", hierarchyStr: "+getHierarchyString(hElement));
        let delim = currentLevel == startLevel ? "" : "."
        if (isEqualOrder(hElement, currentLevel)) {
          counter++;
        }
        if (isHigherOrder(hElement, currentLevel)) {
          counter = 0;
        }
        if (isLowerOrEqualOrder(hElement, currentLevel)) {
          let str = getHierarchyString(hElement)
          str = str + delim + counter;
          setHierarchyString(hElement, str);
          console.log("=> set hierarchyStr: "+getHierarchyString(hElement));
        }
      }
    }
    hElements.forEach(hElement => {
      if (getLevel(hElement) >= startLevel) {
        hElement.textContent = getHierarchyString(hElement)+". "+getText(hElement)
      }
    });
  }


  const createMenuItem = (h) => {
    let level = getLevel(h);
    let text = getText(h);
    let span = document.createElement("span");
    menuContainer.appendChild(span);
    span.className="menuItem menuItem"+level
    span.textContent = text;
    span.dataset.menuItemLevel = level;
    span.addEventListener("click", () => {
      h.scrollIntoView({
        behavior: "smooth"
      })
    });
  }



  let menuContainer = document.getElementById(containerId)
  let hElements = document.querySelectorAll("h1, h2, h3, h4, h5, h6");
  createHierarchy(hElements);
  hElements.forEach(h => {
    createMenuItem(h)
  });
  menuContainer.classList.remove("hide")

}
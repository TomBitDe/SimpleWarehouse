/**
 * 
 */
window.onload = init;
var socket = new WebSocket("ws://localhost:8080/war/actions"); /* CAUTION */
socket.onmessage = onMessage;

function onMessage(event) {
    var location = JSON.parse(event.data);
    if (location.action === "add") {
        printLocationElement(location);
    }
    if (location.action === "remove") {
        document.getElementById(location.id).remove();
        location.parentNode.removeChild(location);
    }
}

function addLocation(id, type) {
    var LocationAction = {
        action: "add",
        id: id,
        type: type
    };
    socket.send(JSON.stringify(LocationAction));
}

function removeLocation(element) {
	var id = element.getElement("id");
    var LocationAction = {
        action: "remove",
        id: id
    };
    socket.send(JSON.stringify(LocationAction));
}

function printLocationElement(location) {
    var content = document.getElementById("content");
    
    var locationDiv = document.createElement("div");
    locationDiv.setAttribute("id", location.id);
    locationDiv.setAttribute("class", "type " + location.type);
    content.appendChild(locationDiv);

    var locationId = document.createElement("span");
    locationId.setAttribute("class", "locationId");
    locationId.innerHTML = location.id;
    locationDiv.appendChild(locationId);

    var locationType = document.createElement("span");
    locationType.innerHTML = "<b>Type:</b> " + location.type;
    locationDiv.appendChild(locationType);

    var removeLocation = document.createElement("span");
    removeLocation.setAttribute("class", "removeLocation");
    removeLocation.innerHTML = "<a href=\"#\" OnClick=removeLocation(" + location.id + ")>Remove Location</a>";
    locationDiv.appendChild(removeLocation);
}

function formLocationSubmit() {
    var form = document.getElementById("addLocationForm");
    var id = form.elements["Location_id"].value;
    var type = form.elements["Type"].value;
    hideLocationForm();
    document.getElementById("addLocationForm").reset();
    addLocation(id, type);
}

function showLocationForm() {
    document.getElementById("addLocationForm").style.display = '';
}

function hideLocationForm() {
    document.getElementById("addLocationForm").style.display = "none";
}

function showHandlingUnitForm() {
    document.getElementById("addHandlingUnitForm").style.display = '';
}

function hideHandlingUnitForm() {
    document.getElementById("addHandlingUnitForm").style.display = "none";
}

function showDropForm() {
    document.getElementById("dropForm").style.display = '';
}

function hideDropForm() {
    document.getElementById("dropForm").style.display = "none";
}

function showPickForm() {
    document.getElementById("pickForm").style.display = '';
}

function hidePickForm() {
    document.getElementById("pickForm").style.display = "none";
}

function showAssignForm() {
    document.getElementById("assignForm").style.display = '';
}

function hideAssignForm() {
    document.getElementById("assignForm").style.display = "none";
}

function showMoveForm() {
    document.getElementById("moveForm").style.display = '';
}

function hideMoveForm() {
    document.getElementById("moveForm").style.display = "none";
}

function showRemoveForm() {
    document.getElementById("removeForm").style.display = '';
}

function hideRemoveForm() {
    document.getElementById("removeForm").style.display = "none";
}

function showFreeForm() {
    document.getElementById("freeForm").style.display = '';
}

function hideFreeForm() {
    document.getElementById("freeForm").style.display = "none";
}

function init() {
    hideLocationForm();
    hideHandlingUnitForm();
    hideDropForm();
    hidePickForm();
    hideAssignForm();
    hideMoveForm();
    hideRemoveForm();
    hideFreeForm();
}
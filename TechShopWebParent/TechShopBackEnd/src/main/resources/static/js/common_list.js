function clearFilter(){
    window.location=moduleURL;
}

function showDeleteConfirmModal(link,entityName){

    let entityId = link.attr("entityId");
    $("#yesButton").attr("href",link.attr("href"))
    $("#confirmText").text("Are you sour want to delete this " + entityName + " ID " + entityId + "?")
    console.log("id " + entityId)
    $("#confirmModal").modal();
}
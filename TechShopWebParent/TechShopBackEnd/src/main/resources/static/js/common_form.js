$(document).ready(function () {
    $("#buttonCancel").on('click', function () {
        window.location = moduleURL;
    });

    // file upload

    $("#fileImage").change(function () {
        if (!checkFileSize(this)) {
            return;
        }
        showImageThumbnail(this)
    })
})

function showImageThumbnail(fileInput) {
    const file = fileInput.files[0];
    const reader = new FileReader();
    reader.onload = function (e) {
        $('#thumbnail').attr('src', e.target.result);
    }
    reader.readAsDataURL(file);
}

function checkFileSize(fileInput) {
    const fileSize = fileInput.files[0].size;
    if (fileSize > MAX_FILE_SIZE) {
        fileInput.setCustomValidity(`You must choose an image less than ${MAX_FILE_SIZE / 1024} KB!`)
        fileInput.reportValidity();
        return false;
    } else {
        fileInput.setCustomValidity("")
        return true;
    }
}

function showModalDialog(title, message) {
    $('#modalTitle').text(title);
    $('#modalBody').text(message);
    $('#modalDialog').modal();
}

function showErrorModal(message) {
    return showModalDialog("Error", message);
}

function showWarningModal(message) {
    return showModalDialog("Warning", message);
}
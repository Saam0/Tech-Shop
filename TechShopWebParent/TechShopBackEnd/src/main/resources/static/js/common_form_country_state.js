let dropDownCountries,
    dataListStates,
    fieldState;

$(document).ready(function () {
    dropDownCountries = $('#country');
    dataListStates = $('#listStates');
    fieldState = $('#state')
    dropDownCountries.on("change", function () {
        loadStatesForCountry();
        fieldState.val("").focus()
    })

})

function loadStatesForCountry() {

    let selectedCountry = $('#country option:selected');
    let countryId = selectedCountry.val()
    let url = contextPath + "states/list_by_country/" + countryId;
    $.get(url, function (responseJSON) {
        dataListStates.empty();
        $.each(responseJSON, function (index, state) {

            $("<option>").text(state.id).text(state.name).appendTo(dataListStates);
        })
    })

}


function checkPasswordMatch(confirmPassword) {
    if (confirmPassword.value !== $('#password').val()) {
        confirmPassword.setCustomValidity("Password do not match");
    } else {
        confirmPassword.setCustomValidity("");
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

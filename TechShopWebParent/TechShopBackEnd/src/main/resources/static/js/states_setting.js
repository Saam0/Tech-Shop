let buttonLoadCountriesForStates,
    dropDownCountriesForStates,
    dropDownStates,
    buttonAddState,
    buttonUpdateState,
    buttonDeleteState,
    labelStateName,
    fieldStateName;


$(document).ready(function () {
    buttonLoadCountriesForStates = $('#buttonLoadCountriesForStates');
    dropDownCountriesForStates = $('#dropDownCountriesForStates');
    dropDownStates = $('#dropDownStates');
    buttonAddState = $('#buttonAddState');
    buttonUpdateState = $('#buttonUpdateState');
    buttonDeleteState = $('#buttonDeleteState');
    labelStateName = $('#labelStateName');
    fieldStateName = $('#fieldStateName');


    buttonLoadCountriesForStates.click(function () {
        loadCountriesForState();
    });

    dropDownCountriesForStates.on("change", function () {
        loadStateForCountry()
    })

    dropDownStates.on("change",function () {
        changeFormStateToSelectedState();
    })

    buttonAddState.click(function () {
        if (buttonAddState.val() === "Add") {
            addState();
        } else {
            changeFormStateToNewState()
        }
    })

    buttonUpdateState.click(function () {
        updateState();
    })

    buttonDeleteState.click(function () {
        deleteState();
    })


});

function deleteState() {
    const stateId = dropDownStates.val().split("-")[0],
        url = contextPath + "state/delete/" + stateId;

    $.ajax({
        type: 'DELETE',
        url: url,
        beforeSend: function (xhr) {
            xhr.setRequestHeader(cstfHeaderName, csrfValue)
        }

    }).done(function () {
            $("#dropDownStates option[value=" + stateId + "]").remove();
            changeFormStateToNewState()
            showToastMessage("The state hase been deleted")
    }).fail(function () {
        showToastMessage("ERROR: Could not connect to server or server encountered an error")

    })
}

function updateState() {
    if (!validateFormState()) {
        return;
    }
    const url = contextPath + "states/save",
        stateName = fieldStateName.val(),
        stateId = dropDownStates.val(),
        selectedCountry = $("#dropDownCountriesForStates option:selected"),
        countryId = selectedCountry.val().split("-")[0],
        countryName = selectedCountry.text();

    const jsonData = {id: stateId, name: stateName, country: {id: countryId, name: countryName}};
    $.ajax({
        type: 'POST',
        url: url,
        beforeSend: function (xhr) {
            xhr.setRequestHeader(cstfHeaderName, csrfValue)
        },
        data: JSON.stringify(jsonData),
        contentType: 'application/json'
    }).done(function (countryId) {
        dropDownStates.find('option:selected').text(stateName);
        showToastMessage("The state has been updated");
        changeFormStateToNewState()
    }).fail(function () {
        showToastMessage("ERROR: Could not connect to server or server encountered an error")
    })
}


function addState() {
    if (!validateFormState()) {
        return;
    }
    const url = contextPath + "states/save",
        stateName = fieldStateName.val(),
        selectedCountry = $("#dropDownCountriesForStates option:selected"),
        countryId = selectedCountry.val().split("-")[0],
        countryName = selectedCountry.text();

    console.log("country id:" +countryId, "name:" + countryName, "state name:"+stateName)


    const jsonData = {name: stateName, country: {id: countryId, name: countryName}};
    $.ajax({
        type: 'POST',
        url: url,
        beforeSend: function (xhr) {
            xhr.setRequestHeader(cstfHeaderName, csrfValue)
        },
        data: JSON.stringify(jsonData),
        contentType: 'application/json'
    }).done(function (stateId) {
        selectNewlyAddedState(stateId, stateName);
        showToastMessage("The new state has been added");
    }).fail(function () {
        showToastMessage("ERROR: Could not connect to server or server encountered an error")
    })
}

function selectNewlyAddedState(stateId, stateName) {
    $("<option>").val(stateId).text(stateName).appendTo(dropDownStates).prop("selected", true);

    fieldStateName.val("").focus()
}

function changeFormStateToNewState() {
    buttonAddState.val("Add")
    labelStateName.text("State/Province Name:")

    buttonUpdateState.prop("disabled", true)
    buttonDeleteState.prop("disabled", true)

    fieldStateName.val("").focus();

}

function changeFormStateToSelectedState() {
    buttonAddState.prop("value", "New")
    buttonUpdateState.prop("disabled", false)
    buttonDeleteState.prop("disabled", false)

    labelStateName.text("Selected State/Province:")
    const selectedStateName = $("#dropDownStates option:selected").text();
    fieldStateName.val(selectedStateName);
}

function loadCountriesForState() {
    const url = contextPath + "countries/list";
    $.get(url, function (responseJson) {
        dropDownCountriesForStates.empty();
        $.each(responseJson, function (index, country) {
            let optionValue = country.id + "-" + country.code;
            $("<option>").val(optionValue).text(country.name).appendTo(dropDownCountriesForStates);
        })
    }).done(function () {
        $(buttonLoadCountriesForStates).val("Refresh country list");
        showToastMessage("All countries have been loaded")
    }).fail(function () {
        showToastMessage("ERROR: Could not connect to server or server encountered an error")
    })
}

function loadStateForCountry() {
    const countryId = dropDownCountriesForStates.find('option:selected').val().split("-")[0];
    const selectedCountry = dropDownCountriesForStates.find('option:selected').text();
    const url = contextPath + "states/list_by_country/"+countryId;
    $.get(url, function (responseJson) {
        dropDownStates.empty();
        $.each(responseJson, function (index, state) {
            let stateId = state.id;
            $("<option>").val(stateId).text(state.name).appendTo(dropDownStates);
        })
    }).done(function () {
        changeFormStateToNewState()
        showToastMessage("All states have been loaded for country " + selectedCountry)
    }).fail(function () {
        showToastMessage("ERROR: Could not connect to server or server encountered an error")
    })
}

function showToastMessage(message) {
    $('#toastMessage').text(message);
    $('.toast').toast('show');
}

function validateFormState() {
    let formState = document.getElementById("formState");
    if (!formState.checkValidity()) {
        formState.reportValidity();
        return false;
    }
    return true;

}
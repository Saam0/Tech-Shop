let buttonLoad;
let dropDownCountry;
let buttonAddCountry;
let buttonUpdateCountry;
let buttonDeleteCountry;
let labelCountryName;
let fieldCountryName;
let fieldCountryCode;


$(document).ready(function () {

    buttonLoad = $('#buttonLoadCountries');
    dropDownCountry = $('#dropDownCountries');
    buttonAddCountry = $('#buttonAddCountry')
    buttonUpdateCountry = $('#buttonUpdateCountry')
    buttonDeleteCountry = $('#buttonDeleteCountry')
    labelCountryName = $('#labelCountryName')
    fieldCountryName = $('#fieldCountryName')
    fieldCountryCode = $('#fieldCountryCode')


    buttonLoad.click(function () {
        loadCountries();
    });

    dropDownCountry.on("change", function () {
        changeFormStateToSelectedCountry()
    })

    buttonAddCountry.click(function () {
        if (buttonAddCountry.val() === "Add") {
            addCountry();
        } else {
            changeFormStateToNewCountry()
        }
    })

    buttonUpdateCountry.click(function () {
        updateCountry();
    })

    buttonDeleteCountry.click(function () {
        deleteCountry();
    })


});

function deleteCountry() {
    const countryId = dropDownCountry.val().split("-")[0];
    const url = contextPath + "countries/delete/" + countryId;

    $.ajax({
        type: 'DELETE',
        url: url,
        beforeSend: function (xhr) {
            xhr.setRequestHeader(cstfHeaderName, csrfValue)
        }

    }).done(function () {
        const optionValue = dropDownCountry.val()
        $("#dropDownCountries option[value=" + optionValue + "]").remove();
        changeFormStateToNewCountry()
        showToastMessage("The countries hase been deleted")
    }).fail(function () {
        showToastMessage("ERROR: Could not connect to server or server encountered an error")

    })

}

function updateCountry() {
    if(!validateFormCountry()) {
        return;
    }
    const url = contextPath + "countries/save";
    const countryName = fieldCountryName.val();
    const countryCode = fieldCountryCode.val();
    const countryId = dropDownCountry.val().split("-")[0];
    const jsonData = {id: countryId, name: countryName, code: countryCode};
    $.ajax({
        type: 'POST',
        url: url,
        beforeSend: function (xhr) {
            xhr.setRequestHeader(cstfHeaderName, csrfValue)
        },
        data: JSON.stringify(jsonData),
        contentType: 'application/json'
    }).done(function (countryId) {
        // $("#dropDownCountries option:selected").val(countryId + "-" + countryCode);
        // $("#dropDownCountries option:selected").text(countryName);
        dropDownCountry.find('option:selected').val(countryId + "-" + countryCode);
        dropDownCountry.find('option:selected').text(countryName);
        showToastMessage("The country has been updated");
        changeFormStateToNewCountry()
    }).fail(function () {
        showToastMessage("ERROR: Could not connect to server or server encountered an error")
    })
}

function validateFormCountry() {
    let formCountry = document.getElementById("formCountry");
    if (!formCountry.checkValidity()) {
        formCountry.reportValidity();
        return false;
    }
    return true;

}

function addCountry() {
    if(!validateFormCountry()) {
        return;
    }

    const url = contextPath + "countries/save";
    const countryName = fieldCountryName.val();
    const countryCode = fieldCountryCode.val();
    console.log(countryName + "-" + countryCode)
    const jsonData = {name: countryName, code: countryCode};
    $.ajax({
        type: 'POST',
        url: url,
        beforeSend: function (xhr) {
            xhr.setRequestHeader(cstfHeaderName, csrfValue)
        },
        data: JSON.stringify(jsonData),
        contentType: 'application/json'
    }).done(function (countryId) {
        selectNewlyAddedCountry(countryId, countryCode, countryName);
        showToastMessage("The new country has been added");
    }).fail(function () {
        showToastMessage("ERROR: Could not connect to server or server encountered an error")
    })
}

function selectNewlyAddedCountry(countryId, countryCode, countryName) {
    const optionValue = countryId + "-" + countryCode;
    $("<option>").val(optionValue).text(countryName).appendTo(dropDownCountry).prop("selected", true);
    // $("#dropDownCountries option[value="+optionValue + "]").prop("selected",true);

    fieldCountryName.val("").focus()
    fieldCountryCode.val("")
}

function changeFormStateToNewCountry() {
    buttonAddCountry.val("Add")
    labelCountryName.text("Country Name:")


    buttonUpdateCountry.prop("disabled", true)
    buttonDeleteCountry.prop("disabled", true)

    fieldCountryCode.val("")
    fieldCountryName.val("").focus();

}

function changeFormStateToSelectedCountry() {
    buttonAddCountry.prop("value", "New")
    buttonUpdateCountry.prop("disabled", false)
    buttonDeleteCountry.prop("disabled", false)

    labelCountryName.text("Selected Country:")
    const selectedCountryName = $("#dropDownCountries option:selected").text();
    fieldCountryName.val(selectedCountryName);

    const countryCode = dropDownCountry.val().split("-")[1];
    fieldCountryCode.val(countryCode);
}

function loadCountries() {
    const url = contextPath + "countries/list";
    $.get(url, function (responseJson) {
        dropDownCountry.empty();
        $.each(responseJson, function (index, country) {
            let optionValue = country.id + "-" + country.code;
            // console.log(optionValue);
            $("<option>").val(optionValue).text(country.name).appendTo(dropDownCountry);
        })

    }).done(function () {
        $(buttonLoad).val("Refresh country list");
        showToastMessage("All countries have been loaded")
    }).fail(function () {
        showToastMessage("ERROR: Could not connect to server or server encountered an error")

    })
}

function showToastMessage(message) {
    $('#toastMessage').text(message);
    $('.toast').toast('show');
}
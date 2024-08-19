const dropdownBrands = $('#brand');
const dropdownCategories = $('#category')


$(document).ready(function () {
    $('#shortDescription').richText();
    $('#fullDescription').richText();

    dropdownBrands.change(function () {
        dropdownCategories.empty();
        getCategories();
    })
    getCategories();
    getCategoriesForNewForm();

})

function getCategoriesForNewForm() {
    const catFieldId = $('#categoryId')
    let editMode = false;
    if (catFieldId.length){
        editMode =true;
    }
    if (!editMode) getCategories();
}

function getCategories() {
    const brandId = dropdownBrands.val();
    const url = brandModuleURL + '/' + brandId + '/categories';

    $.get(url, function (responseJson) {
        $.each(responseJson, (index, category) => {
            $('<option>').val(category.id).text(category.name).appendTo(dropdownCategories);
        })

    })


}

function checkUnique(form) {
    const productId = $('#id').val();
    const productName = $('#name').val();
    const csrfValue = $("input[name='_csrf']").val();

    const params = {id: productId, name: productName, _csrf: csrfValue};

    $.post(checkUniqueUrl, params, function (response) {
        if (response === "OK") {
            form.submit();
        } else if (response === "Duplicate") {
            showWarningModal("There is another product having same name " + productName);
        } else {
            showErrorModal("Unknown response from server");
        }
    }).fail(function () {
        showErrorModal("Could not connect to the server");
    })
    return false;
}

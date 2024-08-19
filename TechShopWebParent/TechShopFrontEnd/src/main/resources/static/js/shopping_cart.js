decimalSeparate = decimalPointType === "COMMA" ? "," : ".";
thousandsSeparate = thousandsPointType === "COMMA" ? "," : ".";

$(document).ready(function () {
    $('.linkMinus').on('click', function (e) {
        e.preventDefault();
        decreaseQuantity($(this))
    });
    $('.linkPlus').on('click', function (e) {
        e.preventDefault();
        increaseQuantity($(this))
    });

    $('.linkRemove').on('click', function (e) {
        e.preventDefault();
        removeProduct($(this));

    });
});

function decreaseQuantity(link) {
    let productId = link.attr('pid');
    let quantityInput = $("#quantity" + productId);
    let newQuantity = parseInt(quantityInput.val()) - 1;
    if (newQuantity > 0) {
        updateQuantity(productId, newQuantity)
        quantityInput.val(newQuantity);
    } else {
        showWarningModal("Minimum quantity is 1");
    }
}

function increaseQuantity(link) {
    let productId = link.attr('pid');
    let quantityInput = $("#quantity" + productId);
    let newQuantity = parseInt(quantityInput.val()) + 1;
    if (newQuantity <= 5) {
        updateQuantity(productId, newQuantity)
        quantityInput.val(newQuantity);
    } else {
        showWarningModal("Maximum quantity is 5");
    }
}

function updateQuantity(productId, quantity) {
    let url = contextPath + "cart/update/" + productId + "/" + quantity;
    $.ajax({
        type: "POST",
        url: url,
        beforeSend: function (xhr) {
            xhr.setRequestHeader(csrfHeaderName, csrfValue);
        }
    }).done(function (updatedSubtotal) {
        updateSubtotal(updatedSubtotal, productId);
        updateTotal();
    }).fail(function () {
        showErrorModal("Error while updating product quantity.");
    });
}

function updateSubtotal(updatedSubtotal, productId) {
    let formattedSubtotal = currencyFormat(updatedSubtotal);
    $("#subtotal" + productId).text(formattedSubtotal);
}

function updateTotal() {
    let total = 0.0;
    let productCount = 0;

    $(".subtotal").each(function (index, element) {
        productCount++;
        let subtotal = parseFloat(clearCurrencyFormat(element.innerText));
        total += subtotal;
    });

    if (productCount < 1) {
        showEmptyShoppingCart();
    } else {
        let formattedTotal = currencyFormat(total);
        $("#total").text(formattedTotal);
    }
}

function clearCurrencyFormat(numberString){
    return numberString.replaceAll(thousandsSeparate, "").replaceAll(decimalSeparate, ".");

}

function showEmptyShoppingCart() {
    $("#totalSection").hide();
    $("#emptyShoppingCartMessage").removeClass("d-none");
}

function removeProduct(link) {
    let url = link.attr('href');
    let rowNumber = link.attr('rowNumber');

    $.ajax({
        type: "DELETE",
        url: url,
        beforeSend: function (xhr) {
            xhr.setRequestHeader(csrfHeaderName, csrfValue);
        }
    }).done(function (response) {
        removeProductHtml(rowNumber)
        updateTotal();
        updateCountNumbers();
        showModalDialog("Shopping Cart", response);
    }).fail(function () {
        showErrorModal("Error while updating product quantity.");
    });
}

function removeProductHtml(rowNumber) {
    $("#row" + rowNumber).remove();
}

function updateCountNumbers() {
    $(".divCount").each(function (index, element) {
        element.innerText = index + 1;
    });
}

function currencyFormat(amount) {
    return $.number(amount, decimalDigits, decimalSeparate, thousandsSeparate);
}


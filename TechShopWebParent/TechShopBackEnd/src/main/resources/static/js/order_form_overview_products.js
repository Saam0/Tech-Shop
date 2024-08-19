let fieldProductCost,
    fieldSubtotal,
    fieldShippingCost,
    fieldTax,
    fieldTotal;


$(document).ready(function () {
    fieldProductCost = $('#productCost');
    fieldSubtotal = $('#subtotal');
    fieldShippingCost = $('#shippingCost');
    fieldTax = $('#tax');
    fieldTotal = $('#total');

    formatOrderAmounts();
    formatProductAmounts();

    $('#productList').on('change', '.quantity-input', function () {
        updateSubtotalWhenQuantityChanged($(this));
        updateOrderAmounts();
    });
    $('#productList').on('change', '.price-input', function () {
        updateSubtotalWhenPriceChanged($(this));
        updateOrderAmounts();
    });
    $('#productList').on('change', '.cost-input', function () {
        updateOrderAmounts();
    });
    $('#productList').on('change', '.ship-input', function () {
        updateOrderAmounts();
    });
});

function updateOrderAmounts() {
    let totalCost = 0.0;
    $('.cost-input').each(function (e) {

        let costInputField = $(this);
        let rowNumber = costInputField.attr('rowNumber');
        let quantityValue = $('#quantity' + rowNumber).val();

        let productCost = costInputField.val();
        totalCost += parseFloat(productCost) * parseFloat(quantityValue);
        // console.log(totalCost);
    });
    setAndFormatNumberForField('productCost', totalCost);

    let orderSubtotal = 0.0;
    $('.subtotal-output').each(function (e) {
        let subtotalField = $(this);
        let subtotalValue = getNumberValueRemovedThousandSeparator(subtotalField);
        orderSubtotal += parseFloat(subtotalValue);
    });
    setAndFormatNumberForField('subtotal', orderSubtotal);

    let shippingCost = 0.0;
    $('.ship-input').each(function (e) {
        let shipField = $(this);
        let shipValue = getNumberValueRemovedThousandSeparator(shipField);
        shippingCost += parseFloat(shipValue);
    });
    setAndFormatNumberForField('shippingCost', shippingCost);

    let tax = getNumberValueRemovedThousandSeparator(fieldTax);
    let total = orderSubtotal + shippingCost + parseFloat(tax);
    setAndFormatNumberForField('total', total);
}

function currencyFormat(amount) {
    return $.number(amount, 2, ".", ",");
}

function getNumberValueRemovedThousandSeparator(number) {
    return number.val().replaceAll(",", "")
}

function setAndFormatNumberForField(fieldId, fieldValue) {
    let formattedValue = $('#' + fieldId);
    formattedValue.val(currencyFormat(fieldValue));
}

function updateSubtotalWhenPriceChanged(input) {
    let price = getNumberValueRemovedThousandSeparator(input);
    let rowNumber = input.attr('rowNumber');
    let quantityField = $('#quantity' + rowNumber);
    let quantity = parseFloat(quantityField.val());
    let newSubtotal = parseFloat(price) * quantity;

    setAndFormatNumberForField('subtotal' + rowNumber, newSubtotal);
}

function updateSubtotalWhenQuantityChanged(input) {
    let quantity = input.val();
    let rowNumber = input.attr('rowNumber');
    let priceField = $('#price' + rowNumber);
    let price = getNumberValueRemovedThousandSeparator(priceField);
    let newSubtotal = parseFloat(quantity) * price;

    setAndFormatNumberForField('subtotal' + rowNumber, newSubtotal);
}

function formatOrderAmounts() {
    formatNumberForField(fieldProductCost);
    formatNumberForField(fieldSubtotal);
    formatNumberForField(fieldShippingCost);
    formatNumberForField(fieldTax);
    formatNumberForField(fieldTotal);
}

function formatProductAmounts() {
    $('.cost-input').each(function () {
        formatNumberForField($(this));
    });
    $('.price-input').each(function () {
        formatNumberForField($(this));
    });
    $('.subtotal-output').each(function () {
        formatNumberForField($(this));
    });
    $('.ship-input').each(function () {
        formatNumberForField($(this));
    });
}

function formatNumberForField(fieldRef) {
    fieldRef.val(currencyFormat(fieldRef.val()));
}



function processFormBeforeSubmit() {
    setCountryName();

    removeThousandSeparatorForField(fieldProductCost)
    removeThousandSeparatorForField(fieldSubtotal)
    removeThousandSeparatorForField(fieldShippingCost)
    removeThousandSeparatorForField(fieldTax)
    removeThousandSeparatorForField(fieldTotal)

    $('.cost-input').each(function () {
        removeThousandSeparatorForField($(this));
    });
    $('.price-input').each(function () {
        removeThousandSeparatorForField($(this));
    });
    $('.subtotal-output').each(function () {
        removeThousandSeparatorForField($(this));
    });
    $('.ship-input').each(function () {
        removeThousandSeparatorForField($(this));
    });

}

function removeThousandSeparatorForField(fieldRef) {
    fieldRef.val(fieldRef.val().replaceAll(",", ""));
};


function setCountryName() {
    let country = $('#country').val();
    let countryName = $('#country option[value=' + country + ']').text();
    $('#countryName').val(countryName);
}
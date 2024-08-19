let productDetailCount;

$(document).ready(function () {
    productDetailCount = $('.hiddenProductId').length;

    $('#products').on('click', '#linkAddProduct', function (e) {
        e.preventDefault();
        let link = $(this);
        let url = link.attr('href');
        $('#addProductModal').on('show.bs.modal', function () {
            $(this).find('iframe').attr('src', url);
        });

        $('#addProductModal').modal();
    });

});

function addProduct(productId, productName) {
    getShippingCost(productId);
}

function getShippingCost(productId) {
    let selectedCountry = $('#country option:selected');
    let countryId = selectedCountry.val();
    let state = $('#state').val();
    if (state.length === 0) {
        state = $('#city').val();
    }
    let url = contextPath + "get_shipping_cost";
    params = {productId: productId, countryId: countryId, state: state};
    $.ajax({
        type: 'POST',
        url: url,
        beforeSend: function (xhr) {
            xhr.setRequestHeader(cstfHeaderName, csrfValue)
        },
        data: params,
    }).done(function (shippingCost) {
        getProductInfo(productId, shippingCost)

    }).fail(function (err) {
        showWarningModal(err.responseJSON.message);
        shippingCost = 0.0;
        getProductInfo(productId, shippingCost)
    }).always(function () {
        $('#addProductModal').modal('hide');
    });
}

function getProductInfo(productId, shippingCost) {
    let url = contextPath + "products/get/" + productId;
    $.get(url, function (productJson) {
        let productName = productJson.name;
        let mainImagePath = contextPath.substring(0, contextPath.length - 1) + productJson.imagePath;
        let productCost = currencyFormat(productJson.cost);
        let productPrice = currencyFormat(productJson.price);
        let shippingCostFormatted = currencyFormat(shippingCost);
        let htmlCode = generateProductCode(productId, productName, mainImagePath, productCost, productPrice, shippingCostFormatted);
        $('#productList').append(htmlCode);
        updateOrderAmounts();
    }).fail(function (err) {
        showWarningModal(err.responseJSON.message);
    });
}

function currencyFormat(amount) {
    return $.number(amount, 2, ".", ",");
}

function generateProductCode(productId, productName, mainImagePath, productCost, productPrice, shippingCost) {
    let nextCount = productDetailCount+ 1;
    productDetailCount++;
    let rowId = "row" + nextCount;
    let quantityId = "quantity" + nextCount;
    let priceId = "price" + nextCount;
    let subtotalId = "subtotal" + nextCount;
    return `
                <div class="border rounded p-1 mb-3" id="${rowId}">
                <input type="hidden" name="detailId" value="0">
                <input type="hidden" name="productId" value="${productId}" class="hiddenProductId">

                <div class="row">
                    <div class="col-1">
                        <div class="divCount">${nextCount}</div>
                        <div><a class="fas fa-trash icon-dark linkRemove" href="" th:rowNumber="${nextCount}"></a></div>
                        
                    </div>
                    <div class="col-3">
                        <img src="${mainImagePath}" class="img-fluid">
                    </div>
                </div>
                <div class="row m-2">
                    <b>${productName}</b>
                </div>
                <div class="row m-2">
                    <table>
                        <tr>
                            <td>Product Cost:</td>
                            <td>
                                <input type="text" required class="form-control m-1 cost-input"
                                name="productDetailCost"
                                       rowNumber="${nextCount}"
                                value="${productCost}" style="max-width: 140px">
                            </td>
                        </tr>
                        <tr>
                            <td>Quantity:</td>
                            <td>
                                <input type="number" step="1" min="1" max="5" required class="form-control m-1 quantity-input"
                                        name="quantity"
                                       id="${quantityId}"
                                       rowNumber="${nextCount}"
                                       value="1" style="max-width: 140px">
                            </td>
                        </tr>
                        <tr>
                            <td>Unit price:</td>
                            <td>
                                <input type="text" required class="form-control m-1 price-input"
                                        name="unitPrice"
                                       id="${priceId}"
                                       rowNumber="${nextCount}"
                                       value="${productPrice}" style="max-width: 140px">
                            </td>
                        </tr>
                        <tr>
                            <td>Subtotal:</td>
                            <td>
                                <input type="text" readonly class="form-control m-1 subtotal-output"
                                        name="subtotal"
                                       id="${subtotalId}"
                                       value="${productPrice}" style="max-width: 140px">
                            </td>
                        </tr>
                        <tr>
                            <td>Shipping Cost:</td>
                            <td>
                                <input type="text" required class="form-control m-1 ship-input"
                                        name="shippingCost"
                                       value="${shippingCost}" style="max-width: 140px">
                            </td>
                        </tr>
                    </table>
                </div>
            </div>
    `;
}

function isProductAlreadyAdded(productId) {
    let productExists = false;
    $('.hiddenProductId').each(function () {
        let aProductId = $(this).val();
        if (aProductId === productId) {
            productExists = true;
            return;
        }
    });
    return productExists;
}

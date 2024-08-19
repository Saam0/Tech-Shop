let extraImagesCount = 0;
$(document).ready(function () {

    // file upload

    $("input[name='extraImage']").each(function (index) {
        $(this).change(function () {
            if (!checkFileSize(this)) {
                return;
            }
            extraImagesCount++;
            showExtraImageThumbnail(this, index)

        })
    })


    $("a[name='linkRemoveExtraImage']").each(function (index) {
        $(this).click(function (){
            removeExtraImage(index);
        })
    })
})


function showExtraImageThumbnail(fileInput, index) {
    const file = fileInput.files[0];
    const fileName = file.name;
    const imageNameHiddenField = $("#imageName" + index)
    if (imageNameHiddenField.length){
        imageNameHiddenField.val(fileName);
    }
    const reader = new FileReader();
    reader.onload = function (e) {
        $('#extraThumbnail' + index).attr('src', e.target.result);
    }
    reader.readAsDataURL(file);

    if (index >= extraImagesCount - 1) {

        addNextExtraImageSection(index + 1);
    }
}

function addNextExtraImageSection(index) {
    const htmlExtraImage = `
       <div class="col border m-3 p-2" id="divExtraImage${index}">
            <div id="extraImageHeader${index}"><label>Extra image #${index + 1}:</label></div>
            <div class="m-2">
                <img src="${defaultImageThumbnail}" alt="Extra image #${index + 1} preview" id="extraThumbnail${index}" class="img-fluid">
            </div>
            <div>
                <input type="file"  name="extraImage"  accept="image/png, image/jpg" 
                onchange="showExtraImageThumbnail(this, ${index})">
            </div>
        </div>
    `;

    const htmlRemoveLink = `
    <a class="btn fas fa-times-circle fa-2x icon-dark float-right" 
    href="javascript:removeExtraImage(${index - 1})"
    title="Remove this image"></a>
    `;
    $('#divProductImages').append(htmlExtraImage);
    $('#extraImageHeader' + (index - 1)).append(htmlRemoveLink);

    extraImagesCount++;

}

function removeExtraImage(index) {
    $('#divExtraImage' + index).remove();
    // extraImagesCount--;
}


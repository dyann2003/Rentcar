document.addEventListener('DOMContentLoaded', () => {

    //custom active fitler
    const fitlerCheckboxes = document.querySelectorAll('.no-model input[type="checkbox"]');
    fitlerCheckboxes.forEach(item => {
        item.addEventListener('change', () => {
            const label = item.closest('label');

            if (item.checked) {
                label.classList.add('active');
            } else {
                label.classList.remove('active');
            }
        })
    })

    //custom select car type
    const carTypeCheckboxes = document.querySelectorAll('.car-type-checkbox input[type="checkbox"]');
    const filterButton = document.querySelector('.type');

    carTypeCheckboxes.forEach(checkbox => {
        checkbox.addEventListener('change', () => {
            // Kiểm tra nếu có ít nhất một checkbox được chọn
            const isChecked = Array.from(carTypeCheckboxes).some(cb => cb.checked);

            if (isChecked) {
                filterButton.classList.add('active');
            } else {
                filterButton.classList.remove('active');
            }
        });
    });

    // custome select car brand
    const radioButtons = document.querySelectorAll('input[name="carBrand"]');
    const brandLabel = document.querySelector('.filter-button.brand');

    radioButtons.forEach(radio => {
        radio.addEventListener('change', () => {
            // Nếu radio được chọn không phải là "all"
            if (radio.value !== 'all' && radio.checked) {
                brandLabel.classList.add('active');
            } else {
                brandLabel.classList.remove('active');
            }
        });
    });
});

// Đồng bộ thanh trượt "Số chỗ"
document.getElementById("seatRange").addEventListener("input", function () {
    document.getElementById("minSeats").value = this.min;
    document.getElementById("maxSeats").value = this.max;
});

// Đồng bộ thanh trượt "Năm sản xuất"
document.getElementById("yearRange").addEventListener("input", function () {
    document.getElementById("minYear").value = this.min;
    document.getElementById("maxYear").value = this.max;
});

function filterWithCondition(condition, values) {
    let currentUrl = window.location.href;
    let urlObj = new URL(currentUrl);

    if (Array.isArray(values)) {
        urlObj.searchParams.delete(condition);
        currentUrl = urlObj.toString();
        values.forEach(value => {
            if (value !== '') {
                currentUrl = updateQueryStringParameter(currentUrl, condition, value);
            }
        });

    } else {
        // Nếu chỉ có một giá trị (radio)
        if (values === 'all') {
            urlObj.searchParams.delete(condition);
            urlObj.searchParams.delete('carModel');
            currentUrl = urlObj.toString();
        } else {
            currentUrl = updateQueryStringParameter(currentUrl, condition, values);
        }

    }

    window.location.href = currentUrl;
}

function searchWithCarType() {
    const selectedTypes = document.querySelectorAll("input[name='carType']:checked");
    const selectedValues = [];

    selectedTypes.forEach(function (checkbox) {
        selectedValues.push(checkbox.value);
    });

    // Nếu không có giá trị nào được chọn (checkbox)
    if (selectedValues.length === 0) {
        selectedValues.push('');  // Mặc định chọn "Tất cả"
    }

    filterWithCondition('carType', selectedValues);
}

function searchWithCarModel() {
    const selectedTypes = document.querySelectorAll("input[name='carModel']:checked");
    const selectedValues = [];

    selectedTypes.forEach(function (checkbox) {
        selectedValues.push(checkbox.value);
    });

    // Nếu không có giá trị nào được chọn (checkbox)
    if (selectedValues.length === 0) {
        selectedValues.push('');  // Mặc định chọn "Tất cả"
    }

    filterWithCondition('carModel', selectedValues);
}

function searchWithCarBrand() {
    const selectedBrand = document.querySelector("input[name='carBrand']:checked");
    if (selectedBrand) {
        filterWithCondition('carBrand', selectedBrand.value);
    }
}


function searchWithOtherCondition(condition, value) {
    let currentUrl = window.location.href;
    if (currentUrl.includes(condition)) {
        let urlObj = new URL(currentUrl);
        urlObj.searchParams.delete(condition);
        currentUrl = urlObj.toString();
    } else {
        currentUrl = updateQueryStringParameter(currentUrl, condition, value);
    }
    window.location.href = currentUrl;
}


(function ($) {
    "use strict";

    // Dropdown on mouse hover
    $(document).ready(function () {
        function toggleNavbarMethod() {
            if ($(window).width() > 992) {
                $('.navbar .dropdown').on('mouseover', function () {
                    $('.dropdown-toggle', this).trigger('click');
                }).on('mouseout', function () {
                    $('.dropdown-toggle', this).trigger('click').blur();
                });
            } else {
                $('.navbar .dropdown').off('mouseover').off('mouseout');
            }
        }

        toggleNavbarMethod();
        $(window).resize(toggleNavbarMethod);
    });


    // Date and time picker
    $('.date').datetimepicker({
        format: 'L'
    });
    $('.time').datetimepicker({
        format: 'LT'
    });


    // Back to top button
    $(window).scroll(function () {
        if ($(this).scrollTop() > 100) {
            $('.back-to-top').fadeIn('slow');
        } else {
            $('.back-to-top').fadeOut('slow');
        }
    });
    $('.back-to-top').click(function () {
        $('html, body').animate({scrollTop: 0}, 1500, 'easeInOutExpo');
        return false;
    });


    // Team carousel
    $(".team-carousel, .related-carousel").owlCarousel({
        autoplay: true,
        smartSpeed: 1000,
        center: true,
        margin: 30,
        dots: false,
        loop: true,
        nav: true,
        navText: [
            '<i class="fa fa-angle-left" aria-hidden="true"></i>',
            '<i class="fa fa-angle-right" aria-hidden="true"></i>'
        ],
        responsive: {
            0: {
                items: 1
            },
            576: {
                items: 1
            },
            768: {
                items: 2
            },
            992: {
                items: 3
            }
        }
    });


    // Testimonials carousel
    $(".testimonial-carousel").owlCarousel({
        autoplay: true,
        smartSpeed: 1500,
        margin: 30,
        dots: true,
        loop: true,
        center: true,
        responsive: {
            0: {
                items: 1
            },
            576: {
                items: 1
            },
            768: {
                items: 2
            },
            992: {
                items: 3
            }
        }
    });

    // Vendor carousel
    $('.vendor-carousel').owlCarousel({
        loop: true,
        margin: 30,
        dots: true,
        center: true,
        autoplay: true,
        smartSpeed: 1000,
        responsive: {
            0: {
                items: 2
            },
            576: {
                items: 3
            },
            768: {
                items: 4
            },
            992: {
                items: 5
            },
            1200: {
                items: 6
            }
        }
    });

})(jQuery);


let typingTimer;
const delay = 100;

function suggestAddress() {
    clearTimeout(typingTimer); // Hủy yêu cầu trước nếu người dùng tiếp tục nhập

    const input = document.getElementById("address-input").value;
    const suggestionsList = document.getElementById("suggestions");

    typingTimer = setTimeout(() => {
        if (input.length > 0) {
            fetch(`/addresses?input=${encodeURIComponent(input)}`)
                .then(response => response.json())
                .then(data => {
                    suggestionsList.innerHTML = ""; // Xóa danh sách trước đó
                    if (data.length > 0) {
                        data.forEach(address => {
                            const li = document.createElement("li");
                            li.textContent = address;
                            li.onclick = () => selectAddress(address);
                            suggestionsList.appendChild(li);
                        });
                        suggestionsList.style.display = "block"; // Hiển thị danh sách gợi ý
                    } else {
                        suggestionsList.style.display = "none"; // Ẩn danh sách nếu không có gợi ý
                    }
                })
                .catch(error => console.error('Error fetching suggestions:', error));
        } else {
            suggestionsList.style.display = "none"; // Ẩn danh sách khi không có input
        }
    }, delay); // Thực hiện sau thời gian trễ
}

function selectAddress(address) {

    const addressDelivery = document.getElementById('addressDelivery');
    if (addressDelivery) {
        $('#addressDelivery').modal('show');
        $('#addressModal').modal('hide');
        document.getElementById('myAddress').value = address;
        document.getElementById("suggestions").style.display = "none";
        return;
    }

    document.getElementById("address").value = address; // Điền địa chỉ đã chọn vào ô nhập
    const checkoutAddressElement = document.getElementById('checkoutAddress');
    if (checkoutAddressElement) {
        checkoutAddressElement.textContent = address;
    }
    document.getElementById("suggestions").style.display = "none"; // Ẩn danh sách gợi ý
    closeSearchCar();

    const search = document.getElementById('search');
    if (search) {
        search.click();
    }
}

function showSeachCar() {
    document.getElementById('address-input').value = document.getElementById('address').value;
    document.getElementById('addressModal').style.display = "block";

}

function searchCar() {
    document.getElementById('address-input').value = document.getElementById('myAddress').value;
    $('#addressDelivery').modal('hide');
}

function closeSearchCar() {
    document.getElementById('addressModal').style.display = "none";

    const addressDelivery = document.getElementById('addressDelivery');
    if (addressDelivery) {
        $('#addressDelivery').modal('show');
    }
}


function changeFormatDate(dateString) {
    const date = new Date(dateString);
    const day = String(date.getDate()).padStart(2, "0");
    const month = String(date.getMonth() + 1).padStart(2, "0"); // Tháng bắt đầu từ 0
    const year = date.getFullYear();
    return `${day}/${month}/${year}`;
}

function transferDate() {
    const rentalInputValue = document.getElementById("rentalInput").value;

    console.log(rentalInputValue);
    // Tách thời gian và ngày từ chuỗi theo định dạng HH:mm, dd/mm/yyyy
    const dateTimes = rentalInputValue.match(/\d{2}:\d{2}, \d{2}\/\d{2}\/\d{4}/g);

    const pickupDateTime = dateTimes[0];
    const returnDateTime = dateTimes[1];

    const pickupTime = pickupDateTime.split(", ")[0];
    const returnTime = returnDateTime.split(", ")[0];

    const pickupDate = pickupDateTime.split(", ")[1];
    const returnDate = returnDateTime.split(", ")[1];

    if (dateTimes && dateTimes.length === 2) {
        // Đặt giá trị vào timepicker2 input
        document.getElementById("timepicker2").value = `${pickupDate} to ${returnDate}`;
        const carId = document.getElementById('carId');

        if (carId) {

            fetch(`/booking/getUnavailableDates/${carId.value}`)
                .then(response => response.json())
                .then(data => {
                    // Chuyển đổi định dạng ngày/giờ
                    const unavailableRanges = data.map(range => ({
                        from: changeFormatDate(range.start),
                        to: changeFormatDate(range.end)
                    }));
                    // Khởi tạo hoặc cập nhật flatpickr với các ngày đã chọn
                    flatpickr("#timepicker2", {
                        mode: "range",
                        dateFormat: "d/m/Y",
                        disable: unavailableRanges,
                        disableMobile: true,
                        minDate: "today",
                        defaultDate: [pickupDate, returnDate]  // Thiết lập khoảng ngày mặc định
                    });
                })
                .catch(error => console.error("Error fetching unavailable dates:", error));
        } else {
            flatpickr("#timepicker2", {
                mode: "range",
                dateFormat: "d/m/Y",
                disableMobile: true,
                minDate: "today",
                defaultDate: [pickupDate, returnDate]
            });
        }
    }

    // Xóa các option cũ của pickupTime và returnTime
    const pickupTimeSelect = document.getElementById("pickupTime");
    const returnTimeSelect = document.getElementById("returnTime");
    pickupTimeSelect.innerHTML = "";
    returnTimeSelect.innerHTML = "";

    // Lấy thời gian hiện tại
    const now = new Date();
    const currentHours = now.getHours();
    const currentMinutes = now.getMinutes();

    document.getElementById("pickupTime").value = pickupTime;
    document.getElementById("returnTime").value = returnTime;

    // Tạo danh sách các mốc thời gian từ 00:00 đến 23:30 với khoảng cách 30 phút
    for (let hour = 0; hour < 24; hour++) {
        for (let minute = 0; minute < 60; minute += 30) {
            const timeString = `${String(hour).padStart(2, "0")}:${String(minute).padStart(2, "0")}`;
            const pickupOption = new Option(timeString, timeString);
            const returnOption = new Option(timeString, timeString);

            // Đặt giá trị mặc định nếu trùng với pickupTime và returnTime
            if (timeString === pickupTime) pickupOption.selected = true;
            if (timeString === returnTime) returnOption.selected = true;

            // Vô hiệu hóa các giờ đã qua nếu pickupDate là hôm nay
            if (pickupDate === now.toLocaleDateString("en-GB") &&
                (hour < currentHours || (hour === currentHours && minute < currentMinutes))) {
                pickupOption.disabled = true;
            }

            pickupTimeSelect.appendChild(pickupOption);
            returnTimeSelect.appendChild(returnOption);
        }
    }
}

function suggestPickupTime() {
    const suggestionsList = document.getElementById("suggestionsPickupTime");

    const rentalInputValue = document.getElementById("timepicker2").value;
    // Tách thời gian và ngày từ chuỗi theo định dạng HH:mm, dd/mm/yyyy
    const dateTimes = rentalInputValue.match(/\d{2}\/\d{2}\/\d{4}/g);
    const pickupDate = dateTimes[0];

    const now = new Date();
    const todayDateString = now.toLocaleDateString("en-GB"); // Lấy ngày hôm nay theo định dạng "dd/mm/yyyy"

    const currentHours = now.getHours();
    const currentMinutes = now.getMinutes();

    suggestionsList.innerHTML = "";

    for (let hour = 0; hour < 24; hour++) {
        for (let minute = 0; minute < 60; minute += 30) {
            const timeString = `${String(hour).padStart(2, "0")}:${String(minute).padStart(2, "0")}`;

            // Nếu ngày nhận xe là hôm nay, kiểm tra thời gian đã qua
            if (pickupDate === todayDateString) {
                // Kiểm tra nếu thời gian này đã qua so với thời gian hiện tại
                if (hour < currentHours || (hour === currentHours && minute < currentMinutes)) {
                    // Nếu thời gian đã qua, bỏ qua không thêm vào danh sách
                    continue;
                }
            }

            const listItem = document.createElement("li");
            listItem.textContent = timeString;
            listItem.style.padding = "5px";
            listItem.style.cursor = "pointer";
            listItem.className = "suggestion-item";

            // Khi chọn thời gian, cập nhật input và ẩn danh sách gợi ý
            listItem.addEventListener("click", function () {
                document.getElementById("pickupTime").value = timeString;
                suggestionsList.style.display = "none";
                suggestionsList.innerHTML = "";
            });

            // Thêm item vào danh sách gợi ý
            suggestionsList.appendChild(listItem);
        }
    }

    // Hiển thị danh sách gợi ý
    suggestionsList.style.display = "block";
}

function suggestReturnTime() {
    const suggestionsList = document.getElementById("suggestionsReturnTime");

    // Kiểm tra nếu danh sách gợi ý đã có thì hiển thị và không tạo lại
    if (suggestionsList.childElementCount > 0) {
        suggestionsList.style.display = "block";
        return;
    }

    // Tạo danh sách các thời gian gợi ý từ 00:00 đến 23:30
    for (let hour = 0; hour < 24; hour++) {
        for (let minute = 0; minute < 60; minute += 30) {
            const timeString = `${String(hour).padStart(2, "0")}:${String(minute).padStart(2, "0")}`;
            const listItem = document.createElement("li");
            listItem.textContent = timeString;
            listItem.style.padding = "5px";
            listItem.style.cursor = "pointer";
            listItem.className = "suggestion-item";

            // Khi chọn thời gian, cập nhật input và ẩn danh sách gợi ý
            listItem.addEventListener("click", function () {
                document.getElementById("returnTime").value = timeString;
                suggestionsList.style.display = "none";
            });

            // Thêm item vào danh sách gợi ý
            suggestionsList.appendChild(listItem);
        }
    }

    // Hiển thị danh sách gợi ý
    suggestionsList.style.display = "block";
}


function getCurrentLocation() {

    if (navigator.geolocation) {
        navigator.geolocation.getCurrentPosition(showPosition, showError);
    } else {
        alert("Trình duyệt của bạn không hỗ trợ Geolocation.");
    }
}

function showPosition(position) {
    const latitude = position.coords.latitude;
    const longitude = position.coords.longitude;
    // Chuyển đổi tọa độ thành địa chỉ
    getAddressFromCoordinates(latitude, longitude);
}

function getAddressFromCoordinates(latitude, longitude) {
    const url = `https://nominatim.openstreetmap.org/reverse?lat=${latitude}&lon=${longitude}&format=json&addressdetails=1`;

    fetch(url)
        .then(response => response.json())
        .then(data => {
            if (data && data.address) {
                const address = data.address;

                // Lấy các trường từ quận, phường trở xuống
                const district = address.suburb || address.district || '';
                const neighborhood = address.neighbourhood || '';
                const street = address.road || address.house_number || '';

                // Lấy thành phố và quốc gia
                const city = address.city || address.town || address.village || '';
                const country = address.country || '';

                // Tạo chuỗi địa chỉ từ quận, phường trở xuống và thành phố, quốc gia
                const addressString = [
                    street,
                    district,
                    neighborhood,
                    city,
                    country
                ].filter(Boolean).join(', ');

                setAddress(addressString);
                closeSearchCar();
            } else {
                alert("Không thể lấy địa chỉ từ tọa độ.");
            }
        })
        .catch(error => {
            console.error("Lỗi:", error);
            alert("Đã xảy ra lỗi khi lấy địa chỉ.");
        });
}


function setAddress(address) {
    const addressDelivery = document.getElementById('addressDelivery');
    if (addressDelivery) {
        $('#addressDelivery').modal('show');
        $('#addressModal').modal('hide');
        document.getElementById('myAddress').value = address;
        return;
    }

    document.getElementById('address').value = address; // Cập nhật địa chỉ vào ô input

    var checkoutAddressElement = document.getElementById('checkoutAddress');
    if (checkoutAddressElement) {
        checkoutAddressElement.textContent = address;
    }

    const search = document.getElementById('search');
    if (search) {
        search.click();
    }


}

function showError(error) {
    switch (error.code) {
        case error.PERMISSION_DENIED:
            alert("Người dùng đã từ chối yêu cầu vị trí.");
            break;
        case error.POSITION_UNAVAILABLE:
            alert("Thông tin vị trí không khả dụng.");
            break;
        case error.TIMEOUT:
            alert("Yêu cầu vị trí đã hết thời gian.");
            break;
        case error.UNKNOWN_ERROR:
            alert("Đã xảy ra lỗi không xác định.");
            break;
    }
}

document.addEventListener("DOMContentLoaded", function () {
    // Lấy giờ hiện tại
    const now = new Date();

    // Thay đổi giờ bắt đầu (cộng thêm 1 giờ)
    now.setHours(now.getHours() + 1);
    const startHour = `${now.getHours().toString().padStart(2, '0')}:00, ${now.getDate().toString().padStart(2, '0')}/${(now.getMonth() + 1).toString().padStart(2, '0')}/${now.getFullYear()}`;

    // Thay đổi giờ kết thúc (cộng thêm 12 giờ từ giờ bắt đầu)
    now.setHours(now.getHours() + 24);
    const endHour = `${now.getHours().toString().padStart(2, '0')}:00, ${now.getDate().toString().padStart(2, '0')}/${(now.getMonth() + 1).toString().padStart(2, '0')}/${now.getFullYear()}`;

    // Gán giá trị cho input nếu nó chưa có giá trị
    const rentalInput = document.getElementById("rentalInput");
    const addressInput = document.getElementById("address");

    if (rentalInput && !rentalInput.value) {
        rentalInput.value = `${startHour} - ${endHour}`;
    }
    if (addressInput && !addressInput.value) {
        addressInput.value = `Hà Nội, Việt Nam`;
    }

    // Lắng nghe sự kiện click trên rentalInput
    if (document.getElementById("address")) {
        document.getElementById("address").addEventListener("click", function () {
            document.getElementById("address-input").value = document.getElementById("address").value;
        });
    }

    if (document.getElementById("rentalInput")) {
        document.getElementById("rentalInput").addEventListener("click", function () {
            // Tách giá trị thành các phần
            const rentalValue = this.value.split(' - ');
            const startDate = rentalValue[0].split(', ')[1]; // Lấy phần ngày từ giờ bắt đầu
            const endDate = rentalValue[1].split(', ')[1]; // Lấy phần ngày từ giờ kết thúc

            // Gán giá trị cho timepicker (cho chế độ range)
            document.getElementById("timepicker2").value = `${startDate} to ${endDate}`; // Ngày bắt đầu
        });
    }

    flatpickr("#timepicker2", {
        mode: "range",
        dateFormat: "d/m/Y",
        disableMobile: true,
        minDate: "today",
        onChange: function (selectedDates, dateStr, instance) {
            if (selectedDates.length === 2) {
                const [startDate, endDate] = selectedDates;
                // Kiểm tra nếu ngày bắt đầu và kết thúc giống nhau
                if (startDate.getTime() === endDate.getTime()) {
                    alert("Vui lòng chọn 2 ngày khác nhau.");
                    instance.clear(); // Xóa ngày đã chọn
                }
            }
        }
    });
});

function getTime() {
    // Get the value from the datetimepicker input
    const dateRange = document.getElementById("timepicker2").value; // Use .value to get the input value
    const dates = dateRange.split(" to "); // Split the string into start and end dates

    // Ensure dates exist and are split correctly
    if (dates.length !== 2) {
        alert("Please select a valid date range.");
        return;
    }

    const startDate = dates[0].trim();
    const endDate = dates[1].trim();

    // Get the selected values from the pickup and return time select elements
    const timeStart = document.getElementById("pickupTime").value; // Ensure this has a valid selection
    const timeEnd = document.getElementById("returnTime").value; // Ensure this has a valid selection

    // Set the value of the rentalInput field
    document.getElementById("rentalInput").value = "";
    document.getElementById("rentalInput").value = `${timeStart}, ${startDate} - ${timeEnd}, ${endDate}`;

    const checkoutPickupElement = document.getElementById('checkoutPickup');
    if (checkoutPickupElement) {
        checkoutPickupElement.textContent = `Nhận xe: ${timeStart}, ${startDate}`;
        document.getElementById("pickupDate").value = `${timeStart}, ${startDate}`;
    }

    const checkoutReturnElement = document.getElementById('checkoutReturn');
    if (checkoutReturnElement) {
        checkoutReturnElement.textContent = `Trả xe: ${timeEnd}, ${endDate}`;
        document.getElementById("returnDate").value = `${timeEnd}, ${endDate}`;
    }
    $('#rentalModal').modal('hide');

    if (checkoutReturnElement || checkoutPickupElement) {
        caculatorPrice();
    }
    const search = document.getElementById("search");
    if (search) {
        search.click();
    }
}

// Hàm hiển thị danh sách thành phố
function suggestCity() {
    clearTimeout(typingTimer); // Hủy yêu cầu trước nếu người dùng tiếp tục nhập

    const suggestionsList = document.getElementById("suggestionsCity");
    suggestionsList.innerHTML = ""; // Xóa danh sách trước đó

    fetch(`/addresses/city`)
        .then(response => response.json())
        .then(data => {
            if (data.length > 0) {
                data.forEach(address => {
                    const li = document.createElement("li");
                    li.textContent = address;
                    li.onclick = () => selectCity(address); // Gọi selectCity khi click
                    li.className = "suggestion-item";
                    suggestionsList.appendChild(li);
                });
                suggestionsList.style.display = "block"; // Hiển thị danh sách gợi ý
            } else {
                suggestionsList.style.display = "none"; // Ẩn danh sách nếu không có gợi ý
            }
        })
        .catch(error => console.error('Error fetching suggestions:', error));
}

function selectCity(city) {
    document.getElementById("city").value = city; // Gán giá trị cho input city
    document.getElementById("suggestionsCity").style.display = "none"; // Ẩn danh sách gợi ý
    document.getElementById("district").value = "";
    document.getElementById("ward").value = "";
}

// Hàm hiển thị danh sách tỉnh, huyện
function suggestDistrict() {

    const suggestionsList = document.getElementById("suggestionsDistrict");
    const city = document.getElementById("city").value;
    suggestionsList.innerHTML = ""; // Xóa danh sách trước đó

    fetch(`/addresses/district?city=${encodeURIComponent(city)}`)
        .then(response => response.json())
        .then(data => {
            if (data.length > 0) {
                data.forEach(address => {
                    const li = document.createElement("li");
                    li.textContent = address;
                    li.onclick = () => selectDistrict(address); // Gọi selectCity khi click
                    li.className = "suggestion-item";
                    suggestionsList.appendChild(li);
                });
                suggestionsList.style.display = "block"; // Hiển thị danh sách gợi ý
            } else {
                suggestionsList.style.display = "none"; // Ẩn danh sách nếu không có gợi ý
            }
        })
        .catch(error => console.error('Error fetching suggestions:', error));
}

function selectDistrict(district) {
    document.getElementById("district").value = district; // Gán giá trị cho input city
    document.getElementById("suggestionsDistrict").style.display = "none"; // Ẩn danh sách gợi ý
    document.getElementById("ward").value = "";
}


// Hàm hiển thị danh sách phường, xã
function suggestWard() {

    const suggestionsList = document.getElementById("suggestionsWard");
    const district = document.getElementById("district").value;
    suggestionsList.innerHTML = ""; // Xóa danh sách trước đó

    fetch(`/addresses/ward?district=${encodeURIComponent(district)}`)
        .then(response => response.json())
        .then(data => {
            if (data.length > 0) {
                data.forEach(address => {
                    const li = document.createElement("li");
                    li.textContent = address;
                    li.onclick = () => selectWard(address); // Gọi selectCity khi click
                    li.className = "suggestion-item";
                    suggestionsList.appendChild(li);
                });
                suggestionsList.style.display = "block"; // Hiển thị danh sách gợi ý
            } else {
                suggestionsList.style.display = "none"; // Ẩn danh sách nếu không có gợi ý
            }
        })
        .catch(error => console.error('Error fetching suggestions:', error));
}

function selectWard(ward) {
    document.getElementById("ward").value = ward; // Gán giá trị cho input district
    document.getElementById("suggestionsWard").style.display = "none"; // Ẩn danh sách gợi ý
}


// Ẩn danh sách gợi ý khi bấm ngoài
document.addEventListener("click", function (event) {
    const suggestionsPickupList = document.getElementById("suggestionsPickupTime");
    const pickupTimeInput = document.getElementById("pickupTime");

    const suggestionsReturnList = document.getElementById("suggestionsReturnTime");
    const returnTimeInput = document.getElementById("returnTime");

    const suggestionsCity = document.getElementById("suggestionsCity");
    const cityInput = document.getElementById("city");

    const suggestionsDistrict = document.getElementById("suggestionsDistrict");
    const districtInput = document.getElementById("district");

    const suggestionsWard = document.getElementById("suggestionsWard");
    const wardInput = document.getElementById("ward");

    if (suggestionsPickupList) {
        if (event.target !== suggestionsPickupList && event.target !== pickupTimeInput) {
            suggestionsPickupList.style.display = "none";
        }
    }

    if (suggestionsReturnList) {
        if (event.target !== suggestionsReturnList && event.target !== returnTimeInput) {
            suggestionsReturnList.style.display = "none";
        }
    }

    if (suggestionsCity) {
        if (event.target !== suggestionsCity && event.target !== cityInput) {
            suggestionsCity.style.display = "none";
        }
    }

    if (suggestionsDistrict) {
        if (event.target !== suggestionsDistrict && event.target !== districtInput) {
            suggestionsDistrict.style.display = "none";
        }
    }

    if (suggestionsWard) {
        if (event.target !== suggestionsWard && event.target !== wardInput) {
            suggestionsWard.style.display = "none";
        }
    }
});

function toggleEdit() {
    // Tìm tất cả các input trong form và bật/tắt chế độ chỉnh sửa
    const inputs = document.querySelectorAll('.form-control');
    const editIcon = document.getElementById('editIcon');

    if (editIcon.classList.contains('fa-pen')) {
        // Chuyển sang chế độ chỉnh sửa
        inputs.forEach(input => input.removeAttribute('readonly'));
        editIcon.classList.remove('fa-pen');
        editIcon.classList.add('fa-save');
        document.getElementById("addressRenter").hidden = true;
        document.getElementById("city").hidden = false;
        document.getElementById("district").hidden = false;
        document.getElementById("ward").hidden = false;
        document.getElementById("street").hidden = false;
        document.getElementById("licenseImg").style.display = 'block';
        document.getElementById("imageDriverLicense").style.display = 'none'
    } else {
        // Lấy giá trị từ city, district, ward và street
        const city = document.getElementById("city").value;
        const district = document.getElementById("district").value;
        const ward = document.getElementById("ward").value;
        const street = document.getElementById("street").value;

        const licenseImgInput = document.getElementById("licenseImg");
        const file = licenseImgInput.files[0]; // Lấy file được chọn từ input
        if (file) {
            const reader = new FileReader();
            reader.onload = function (e) {
                const imgElement = document.getElementById('imageDriverLicense');
                imgElement.src = e.target.result; // Cập nhật đường dẫn ảnh cho thẻ img
            };
            reader.readAsDataURL(file); // Đọc file dưới dạng Data URL
        }

        let fullAddress = "";
        // Tạo chuỗi địa chỉ đầy đủ
        if (street) {
            fullAddress = `${street}, ${ward}, ${district}, ${city}`;
        } else {
            fullAddress = `${ward}, ${district}, ${city}`;
        }

        // Gán chuỗi địa chỉ đầy đủ cho addressRenter nếu có giá trị
        if (city && district && ward) {
            document.getElementById("addressRenter").value = fullAddress;
        }

        // Lưu và tắt chế độ chỉnh sửa
        inputs.forEach(input => input.setAttribute('readonly', 'true'));
        editIcon.classList.remove('fa-save');
        editIcon.classList.add('fa-pen');

        // Hiển thị lại addressRenter và ẩn các trường city, district, ward
        document.getElementById("addressRenter").hidden = false;
        document.getElementById("city").hidden = true;
        document.getElementById("district").hidden = true;
        document.getElementById("ward").hidden = true;
        document.getElementById("street").hidden = true;
        document.getElementById("licenseImg").style.display = 'none';
        document.getElementById("imageDriverLicense").style.display = 'block'
    }
}


function caculatorPrice() {
    // Lấy giá trị giá thuê mỗi ngày và phí đặt cọc, chuyển đổi sang số
    const pricePerDay = parseFloat(document.getElementById("pricePerDay").value);
    const depositPrice = parseFloat(document.getElementById("deposit").value);

    const pickupDateInput = document.getElementById("pickupDate").value;
    const returnDateInput = document.getElementById("returnDate").value;

    if (pricePerDay && depositPrice && pickupDateInput && returnDateInput) {
        // Chuyển đổi chuỗi ngày tháng giờ thành đối tượng Date
        const pickupDateParts = pickupDateInput.split(", ");
        const returnDateParts = returnDateInput.split(", ");

        // Thêm thời gian vào phần ngày tháng để tạo đối tượng Date hợp lệ
        const pickupDateString = pickupDateParts[1].split("/").reverse().join("/") + " " + pickupDateParts[0];  // "11/11/2024 16:00"
        const returnDateString = returnDateParts[1].split("/").reverse().join("/") + " " + returnDateParts[0];  // "11/11/2024 18:30"

        const pickupDate = new Date(pickupDateString);

        console.log(pickupDate);

        const returnDate = new Date(returnDateString);

        console.log(returnDate);

        // Tính chênh lệch thời gian giữa pickupDate và returnDate
        const totalMilliseconds = returnDate - pickupDate;

        console.log(totalMilliseconds);

        // Tính số ngày thuê, sử dụng tương tự như ví dụ trước
        const oneDay = 24 * 60 * 60 * 1000; // Một ngày tính bằng mili giây
        const daysRented = Math.floor(totalMilliseconds / oneDay);  // Số ngày đầy đủ
        const remainingMilliseconds = totalMilliseconds % oneDay; // Thời gian lẻ còn lại

        console.log(pricePerDay);
        console.log(daysRented);

        let totalAmount = (pricePerDay * daysRented) + depositPrice;

        console.log(totalAmount);

        // Nếu thời gian lẻ ít hơn 12 giờ, tính 1/2 giá ngày, nếu không tính đủ 1 ngày
        if (remainingMilliseconds > 0) {
            const remainingHours = remainingMilliseconds / (60 * 60 * 1000);  // Thời gian lẻ tính theo giờ
            totalAmount += remainingHours < 12 ? (pricePerDay / 2) : pricePerDay;
        }

        console.log(totalAmount);

        document.getElementById("checkoutTotalAmount").value = totalAmount;
        document.getElementById("totalAmount").innerText = `$${totalAmount.toLocaleString()}`;
    }
}

function showDepositStart() {
    const delivery = document.getElementById("delivery");
    const deliveryValue = document.getElementById("deliveryValue");
    delivery.style.display = "block";
    deliveryValue.style.display = "block";
}

function applyAddress() {
    const delivery = document.getElementById("delivery");
    const deliveryValue = document.getElementById("deliveryValue");

    const deliveryPrice = parseFloat(document.getElementById("deliveryPrice").value) || 0;
    const totalAmountText = document.getElementById("totalAmount").innerText.replace(/\D/g, "") || "0";
    const totalAmount = parseFloat(totalAmountText);

    // Lấy option được chọn
    const selectedCard = document.querySelector(".option-card.selected");

    let selectedAddress = "";
    let newTotal = totalAmount;

    if (selectedCard) {
        if (selectedCard.querySelector("#myAddress")) {
            // Xử lý nếu chọn "Giao xe tận nơi"
            delivery.style.display = "block";
            deliveryValue.style.display = "block";

            // Cộng thêm phí giao xe
            newTotal += deliveryPrice;

            document.getElementById("textInforIsDelivery").style.display = "block";
            document.getElementById("isDeliveryCheckout").value = true;

            selectedAddress = selectedCard.querySelector("#myAddress").value || "Địa chỉ chưa được nhập";
        } else if (selectedCard.querySelector("#carOwnerAddress")) {
            // Xử lý nếu chọn "Nhận xe tại vị trí chủ xe"
            delivery.style.display = "none";
            deliveryValue.style.display = "none";

            // Trừ phí giao xe
            newTotal -= deliveryPrice;

            document.getElementById("textInforIsDelivery").style.display = "none";
            document.getElementById("isDeliveryCheckout").value = false;

            selectedAddress = selectedCard.querySelector("#carOwnerAddress").textContent || "Không có địa chỉ";
        }
    }

    // Cập nhật tổng tiền
    document.getElementById("totalAmount").innerText = `${newTotal.toLocaleString("vi-VN")} Đ`;
    document.getElementById("checkoutTotalAmount").value = newTotal;

    // Cập nhật địa chỉ thanh toán
    document.getElementById("checkoutAddress").innerText = selectedAddress;
    document.getElementById("addressDeliveryCheckout").value = selectedAddress;

    const addressField = document.getElementById("address");
    if (addressField) {
        addressField.value = selectedAddress;
    }

    // Đóng modal (nếu sử dụng Bootstrap)
    if (window.jQuery) {
        $("#addressDelivery").modal("hide");
    }
}


function selectTypeDelivery() {
    document.getElementById("myAddress").value = document.getElementById("address").value;
}

function updateModels(brandId) {
    if (brandId) {
        fetch(`/car-owner/get-model-by-brandId?brandId=` + brandId)
            .then(response => response.json())
            .then(models => {
                console.log("Models fetched:", models); // Kiểm tra xem có bao nhiêu model được lấy
                const carModelSelect = document.getElementById("carModel");
                carModelSelect.innerText = "";
                models.forEach(model => {
                    const option = document.createElement("option");
                    option.value = model.id;
                    option.textContent = model.name; // Đảm bảo thuộc tính đúng là 'name' thay vì 'modelName'
                    carModelSelect.appendChild(option);
                });
            })
            .catch(error => console.error("Error fetching models:", error));
    }
}

document.addEventListener("DOMContentLoaded", function () {
    if (document.getElementById("carBrand")) {
        const selectedBrandId = document.getElementById("carBrand").value;
        if (selectedBrandId) {
            updateModels(selectedBrandId);
        }
    }
});

function showErrorToast(message) {
    const errorMessageElement = document.getElementById("error-message");
    const toastContent = errorMessageElement.querySelector(".toast-content");
    toastContent.innerHTML = "";
    // Đặt nội dung thông báo lỗi tùy chỉnh
    toastContent.innerHTML = `<strong>Lỗi!!!</strong> ${message}`;

    errorMessageElement.classList.add('show');

    // Tự động ẩn thông báo lỗi sau 3 giây
    setTimeout(() => {
        errorMessageElement.classList.remove('show');
    }, 3000);
}

function goToPage(page, size) {
    let currentUrl = window.location.href;
    currentUrl = updateQueryStringParameter(currentUrl, 'page', page);
    currentUrl = updateQueryStringParameter(currentUrl, 'size', size);
    window.location.href = currentUrl;
}

function updateQueryStringParameter(uri, key, value) {
    const re = new RegExp(`([?&])${key}=.*?(&|$)`, 'i');
    const separator = uri.indexOf('?') !== -1 ? '&' : '?';

    if (uri.match(re) && key !== 'carType' && key !== 'carModel') {
        return uri.replace(re, `$1${key}=${value}$2`);
    } else {
        return uri + separator + key + '=' + value;
    }
}

// Function để xử lý địa chỉ
function formatAddress() {
    // Lấy tất cả các phần tử chứa địa chỉ
    const addressElements = document.querySelectorAll('.searchCarAddress');

    if (addressElements) {
        // Lặp qua từng địa chỉ và xử lý
        addressElements.forEach(element => {
            let address = element.textContent || element.innerText; // Lấy nội dung địa chỉ
            let addressParts = address.split(', '); // Tách địa chỉ bằng dấu phẩy

            // Nếu địa chỉ có ít nhất 2 phần tử, lấy 2 phần cuối
            if (addressParts.length >= 2) {
                let lastTwoParts = addressParts.slice(-2).map(part => part.trim()).join(', ');
                // Xóa các từ không cần thiết
                lastTwoParts = lastTwoParts.replace(/Thành phố|Tỉnh|Quận|Huyện/g, '').trim();
                element.textContent = lastTwoParts; // Cập nhật lại nội dung địa chỉ
            }
        });
    }
}

// Gọi hàm khi trang đã tải xong
document.addEventListener('DOMContentLoaded', formatAddress);

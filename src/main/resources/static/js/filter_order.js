function searchWithSort(condition) {
    const selectedSort = document.getElementById(condition).value;
    if (selectedSort) {
        // Gọi hàm filter với tiêu chí sắp xếp
        filterWithStatus(condition, selectedSort);
    } else {
        console.log("No sorting option selected");
    }
}

function filterWithStatus(condition, values) {
    let currentUrl = window.location.href;
    let urlObj = new URL(currentUrl);
    urlObj.searchParams.delete("erro");
    currentUrl = urlObj.toString();
    currentUrl = updateQueryStringParameter(currentUrl, condition, values);
    window.location.href = currentUrl;
}

document.addEventListener('DOMContentLoaded', function () {
    const errorMessage = getParameterByName('erro');
    if (errorMessage) {
        showErrorToast(errorMessage);
    }
});

function getParameterByName(name) {
    const url = window.location.href;
    const nameRegex = name.replace(/[\[\]]/g, '\\$&');
    const regex = new RegExp('[?&]' + name + '(=([^&#]*)|&|#|$)');
    const results = regex.exec(url);
    if (!results) return null;
    if (!results[2]) return '';
    return decodeURIComponent(results[2].replace(/\+/g, ' '));
}


document.addEventListener('DOMContentLoaded', function () {
    const cancelModal = document.getElementById('cancelBookingDocumentPolicy');

    // Lắng nghe sự kiện hiển thị modal
    cancelModal.addEventListener('show.bs.modal', function (event) {
        const button = event.relatedTarget; // Nút đã kích hoạt modal
        const bookingId = button.getAttribute('data-id'); // Lấy ID từ data-id
        const statusBooking = button.getAttribute('data-status-booking');

        // Cập nhật href cho link Xác nhận
        const confirmCancelLink = document.getElementById('confirmCancelLink');
        confirmCancelLink.href = `/booking/update_status_booking?bookingId=${bookingId}&statusBooking=${statusBooking}&cancel=true`;
    });
});


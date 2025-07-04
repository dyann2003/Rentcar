document.addEventListener('DOMContentLoaded', function () {
    // Modal elements
    const handoverModal = document.getElementById('handoverModal');
    const overlay = document.getElementById('overlay');
    const reviewText = document.getElementById('reviewText');
    const stars = document.querySelectorAll('#starRating i');
    let selectedRating = 0;
    let activeTransferButton = null; // Lưu trữ nút "Bàn giao lại xe" đã được nhấn

    // Gắn sự kiện cho nút "Bàn giao lại xe"
    const transferButtons = document.querySelectorAll('.transfer');
    transferButtons.forEach((button) => {
        button.addEventListener('click', function (e) {
            e.preventDefault();
            const [carId, bookingId] = this.getAttribute('data').split('-');
            // Lưu thông tin booking vào dataset của modal
            handoverModal.dataset.bookingId = bookingId;
            handoverModal.dataset.carId = carId;
            activeTransferButton = this; // Lưu lại nút được nhấn

            // Hiển thị modal
            showModal();
        });
    });

    // Gắn sự kiện cho các sao
    stars.forEach((star) => {
        star.addEventListener('click', function () {
            // Xóa lớp active của các sao
            stars.forEach((s) => s.classList.remove('active'));

            // Thêm lớp active cho các sao được chọn
            this.classList.add('active');
            let prevStar = this.previousElementSibling;
            while (prevStar) {
                prevStar.classList.add('active');
                prevStar = prevStar.previousElementSibling;
            }

            selectedRating = this.getAttribute('data-value');
        });
    });
    let starNum = 0;
    document.querySelectorAll('.stars i').forEach(function (star) {
        star.addEventListener('click', function () {
            starNum = this.getAttribute('data-value');
        });
    });

    // Sự kiện khi nhấn Skip
    document.getElementById('skipReview').addEventListener('click', function () {
        // Khi nhấn Skip, tiếp tục hành động của nút "Bàn giao lại xe"
        window.location.href = '/booking/update_status_booking?bookingId=' + handoverModal.dataset.bookingId + '&statusBooking=3';
        hideModal();  // Đóng modal
    });

    document.getElementById('submitReview').addEventListener('click', function () {
        const reviewText = document.getElementById('reviewText'); // Lấy phần tử textarea
        const comment = reviewText.value; // Lấy nội dung bình luận
        // Kiểm tra xem người dùng đã chọn ít nhất một sao chưa
        if (starNum == 0) {
            alert('Vui lòng chọn sao đánh giá trước khi gửi.');
            return; // Dừng lại nếu chưa chọn sao
        }
        // Kiểm tra nếu bình luận dài hơn 200 ký tự
        if (comment.length > 200 || comment.length == 0) {
            alert('Bình luận không được dài quá 200 ký tự và không được trống.');
            return; // Dừng lại nếu bình luận quá dài
        }
        const url = '/booking/update_status_booking?bookingId=' +
            encodeURIComponent(handoverModal.dataset.bookingId) +
            '&statusBooking=3&comment=' + encodeURIComponent(comment) +
            '&mark=' + encodeURIComponent(starNum) +
            '&carId=' + encodeURIComponent(handoverModal.dataset.carId);
        window.location.href = url;
    });


    // Hàm hiển thị modal
    function showModal() {
        handoverModal.style.display = 'block';
        overlay.style.display = 'block';
    }

    // Hàm ẩn modal
    function hideModal() {
        handoverModal.style.display = 'none';
        overlay.style.display = 'none';
        reviewText.value = '';
        stars.forEach((star) => star.classList.remove('active'));
        selectedRating = 0;
    }

    // Lắng nghe sự kiện click trên tất cả các sao


    // Khi người dùng nhấn vào overlay, sẽ đóng modal
    overlay.addEventListener('click', hideModal);
});


document.addEventListener('DOMContentLoaded', () => {
    const buttons = document.querySelectorAll('[data-start-date]');

    if (buttons) {
        buttons.forEach(button => {
            const startDate = new Date(button.getAttribute('data-start-date'));
            const now = new Date();

            const diffInHours = (startDate - now) / (1000 * 60 * 60);

            if (diffInHours <= 6) {
                button.style.display = 'none';
            }
        });
    }
});
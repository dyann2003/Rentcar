const allBtn = document.querySelector("div[class='container mt-4'] button:nth-child(1)");
const markFive = document.querySelector("body > div:nth-child(3) > div:nth-child(3) > button:nth-child(2)");
const markFour = document.querySelector("button:nth-child(3)");
const markThree = document.querySelector("button:nth-child(4)");
const markTwo = document.querySelector("button:nth-child(5)");
const markOne = document.querySelector("button:nth-child(6)");

function updateParam(value, remove = false) {
    const urlParams = new URLSearchParams(window.location.search);
    const paramValue = urlParams.get("mark");

    let updatedParamValue;

    if (paramValue) {
        let marks = paramValue.split(",");
        if (remove) {
            // Nếu muốn xóa giá trị, loại bỏ nó khỏi danh sách
            marks = marks.filter(mark => mark !== value.toString());
        } else if (!marks.includes(value.toString())) {
            // Nếu không có giá trị trong danh sách, thêm vào
            marks.push(value.toString());
        }
        updatedParamValue = marks.join(",");
    } else if (!remove) {
        // Nếu không có tham số mark, chỉ thêm giá trị vào
        updatedParamValue = value.toString();
    } else {
        updatedParamValue = '';
    }

    urlParams.set("mark", updatedParamValue);
    const updatedURL = window.location.origin + window.location.pathname + '?' + urlParams.toString();
    return updatedURL;
}

function toggleActive(button, value) {
    if (button.classList.contains("active")) {
        // Nếu nút đã active, xóa nó và cập nhật tham số mark
        button.classList.remove("active");
        window.location.href = updateParam(value, true);  // Xóa giá trị khỏi tham số
    } else {
        // Nếu nút chưa active, thêm nó và cập nhật tham số mark
        button.classList.add("active");
        window.location.href = updateParam(value);
    }
}

markOne.addEventListener("click", function (e) {
    toggleActive(markOne, 1);
});
markTwo.addEventListener("click", function (e) {
    toggleActive(markTwo, 2);
});
markThree.addEventListener("click", function (e) {
    toggleActive(markThree, 3);
});
markFour.addEventListener("click", function (e) {
    toggleActive(markFour, 4);
});
markFive.addEventListener("click", function (e) {
    toggleActive(markFive, 5);
});

allBtn.addEventListener("click", function (e) {
    // Khi nhấn "All", xóa tất cả các giá trị trong tham số mark và làm sáng nút All
    window.location.href = window.location.origin + window.location.pathname;
});

const allButtons = [markOne, markTwo, markThree, markFour, markFive];
allButtons.forEach(button => button.classList.remove("active"));

const urlParams = new URLSearchParams(window.location.search);
const paramValue = urlParams.get("mark");

// Nếu tham số mark tồn tại và là một giá trị hợp lệ, thêm lớp active vào nút tương ứng
if (paramValue) {
    const marks = paramValue.split(",");
    marks.forEach(mark => {
        switch (mark) {
            case '1':
                markOne.classList.add("active");
                break;
            case '2':
                markTwo.classList.add("active");
                break;
            case '3':
                markThree.classList.add("active");
                break;
            case '4':
                markFour.classList.add("active");
                break;
            case '5':
                markFive.classList.add("active");
                break;
        }
    });
}

// Đảm bảo nút "All" được làm sáng khi không có tham số mark trong URL
if (!paramValue) {
    allBtn.classList.add("active");
}
  
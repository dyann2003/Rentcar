// function updateUrlWithPriceFilter(minPrice, maxPrice) {
//     // Đường dẫn hiện tại
//     let currentUrl = window.location.href;
//
//     // Tạo tham số filter mới
//     const filterParam = createFilterParam(minPrice, maxPrice);
//
//     // Kiểm tra nếu URL đã có filter
//     if (currentUrl.includes('filter=')) {
//         // Nếu đã có filter
//         currentUrl = currentUrl.replace(/(filter=[^&]*)/, (match) => {
//             const currentFilter = match.split('=')[1];
//             const filters = currentFilter.split(',');
//
//             // Lọc các bộ lọc hiện tại để loại bỏ pricePerDay
//             const updatedFilters = filters.filter(filter => !filter.startsWith('pricePerDay'));
//
//             // Thêm filterParam mới vào cuối danh sách
//             updatedFilters.push(filterParam);
//
//             return `filter=${updatedFilters.join(',')}`; // Kết hợp lại thành chuỗi
//         });
//     } else {
//         // Nếu chưa có filter, thêm vào URL
//         const separator = currentUrl.includes('?') ? '&' : '?';
//         currentUrl += `${separator}filter=${filterParam}`;
//     }
//
//     // Chuyển hướng đến URL mới chỉ khi có sự thay đổi
//     window.location.href = currentUrl;
// }
//
//
// // Hàm tạo tham số filter
// function createFilterParam(minPrice, maxPrice) {
//     let filterParam;
//
//     if (minPrice === maxPrice) {
//         // Nếu min và max bằng nhau
//         filterParam = `pricePerDay:${minPrice}`;
//     } else {
//         // Nếu min < max
//         filterParam = `pricePerDay>${minPrice},pricePerDay<${maxPrice}`;
//     }
//
//     return filterParam; // Trả về tham số filter
// }

function clearAllParams() {
    let currentURL = window.location.href;
    let url = new URL(currentURL);  // Tạo đối tượng URL để dễ dàng thao tác
    if(url.searchParams.has("filter")) {
        url.searchParams.delete("filter");
    }
    // Trả về URL dưới dạng chuỗi
    return url.toString();
}

function updateParam(minPrice,maxPrice,minSeat,maxSeat,fuel) {
    let currentURL =clearAllParams();
    const seperator = currentURL.indexOf("?")===-1 ? '?' : '&';
    let paramPrice=null;
    let paramSeat =null;
    if(minPrice === maxPrice) {
         paramPrice = `pricePerDay:${minPrice}`
    }else {
        paramPrice = `pricePerDay>${minPrice},pricePerDay<${maxPrice}`;
    }
    if(minSeat === maxSeat) {
        paramSeat =`numberOfSeats:${minSeat}`;
    }else {
        paramSeat = `numberOfSeats>${minSeat},numberOfSeats<${maxSeat}`
    }

    let paramFuel = (fuel ==="all")? `` : `fuelType:${fuel}`
    currentURL+=`${seperator}filter=${paramPrice},${paramSeat},${paramFuel}`
    return currentURL;
}
function getParam(param) {
    const url = new URL(window.location.href); // Tạo đối tượng URL từ URL hiện tại
    const filterParam = url.searchParams.get("filter"); // Lấy giá trị của tham số "filter"
    return filterParam;
}


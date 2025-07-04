
document.addEventListener("DOMContentLoaded", function () {
    const filterButton = document.getElementById("filter-button");
    const filterModal = document.getElementById("filter-modal");
    const closeModal = document.querySelector(".close-modal");

    // Hiển thị modal khi nhấn nút bộ lọc
    filterButton.addEventListener("click", function () {
        filterModal.style.display = "flex"; // Hiển thị modal
    });

    // Đóng modal khi nhấn nút đóng
    closeModal.addEventListener("click", function () {
        filterModal.style.display = "none"; // Ẩn modal
    });

    // Đóng modal khi nhấn ra ngoài nó
    window.addEventListener("click", function (e) {
        if (e.target === filterModal) {
            filterModal.style.display = "none"; // Ẩn modal khi nhấn ra ngoài
        }
    });


    //xử lí giá

    const minRangePrice = document.getElementById("min-price");
    const maxRangePrice = document.getElementById("max-price");
    const minPriceInput = document.getElementById("min-price-input");
    const maxPriceInput = document.getElementById("max-price-input");

    minRangePrice.addEventListener("input", () => {
        // Nếu min lớn hơn max, đặt min bằng max
        if (parseInt(minRangePrice.value) > parseInt(maxRangePrice.value)) {
            minRangePrice.value = maxRangePrice.value;
        }
        // Cập nhật giá trị input tương ứng
        minPriceInput.value = minRangePrice.value;
    });

    maxRangePrice.addEventListener("input", () => {
        // Nếu max nhỏ hơn min, đặt max bằng min
        if (parseInt(maxRangePrice.value) < parseInt(minRangePrice.value)) {
            maxRangePrice.value = minRangePrice.value;
        }
        // Cập nhật giá trị input tương ứng
        maxPriceInput.value = maxRangePrice.value;
    });

// Cập nhật giá trị range khi thay đổi input
    minPriceInput.addEventListener("input", () => {
        // Giới hạn giá trị input không lớn hơn max
        if (parseInt(minPriceInput.value) > parseInt(maxRangePrice.value)   ) {
            minPriceInput.value = maxRangePrice.value;
        }
        minRangePrice.value = minPriceInput.value;
    });

    maxPriceInput.addEventListener("input", () => {
        // Giới hạn giá trị input không nhỏ hơn min
        if (parseInt(maxPriceInput.value) < parseInt(minRangePrice.value)) {
            maxPriceInput.value = minRangePrice.value;
        }
        maxRangePrice.value = maxPriceInput.value;
    });

    minPriceInput.addEventListener("input", function () {
        // Kiểm tra nếu ô input bị bỏ trống
        if (minPriceInput.value === "" || isNaN(minPriceInput.value) || parseInt(minPriceInput.value) <20) {
            minRangePrice.value = 20;      // Đặt giá trị của range là 20
            minPriceInput.value = 20;      // Đặt giá trị của input là 20
        } else {
            // Nếu input có giá trị hợp lệ, cập nhật range theo giá trị input
            minRangePrice.value = minPriceInput.value;
        }
    });

    maxPriceInput.addEventListener("input", function () {
        // Kiểm tra nếu ô input bị bỏ trống, không phải là số, hoặc lớn hơn 500
        if (maxPriceInput.value === "" || isNaN(maxPriceInput.value) || parseInt(maxPriceInput.value) > 500) {
            maxRangePrice.value = 500;       // Đặt giá trị của range là 500
            maxPriceInput.value = 500;       // Đặt giá trị của input là 500
        } else {
            // Nếu input có giá trị hợp lệ, cập nhật range theo giá trị input
            maxRangePrice.value = maxPriceInput.value;
        }
    });


    //Xử lí số ghế
    const minRangeSeat = document.getElementById("min-seat");
    const maxRangeSeat = document.getElementById("max-seat");
    const minSeatInput = document.getElementById("min-seat-input");
    const maxSeatInput =document.getElementById("max-seat-input");

    minRangeSeat.addEventListener("input", () => {
        // Nếu min lớn hơn max, đặt min bằng max
        if (parseInt(minRangeSeat.value) > parseInt(maxRangeSeat.value)) {
            minRangeSeat.value = maxRangeSeat.value;
        }
        // Cập nhật giá trị input tương ứng
        minSeatInput.value = minRangeSeat.value;
    });

    maxRangeSeat.addEventListener("input", () => {
        // Nếu max nhỏ hơn min, đặt max bằng min
        if (parseInt(maxRangeSeat.value) < parseInt(minRangeSeat.value)) {
            maxRangeSeat.value = minRangeSeat.value;
        }
        // Cập nhật giá trị input tương ứng
        maxSeatInput.value = maxRangeSeat.value;
    });


    minSeatInput.addEventListener("input",function (e){
        if(parseInt(minSeatInput.value)>parseInt(maxSeatInput.value)) {
            minSeatInput.value = maxSeatInput.value;
        }
         minRangeSeat.value=minSeatInput.value;
    })

    maxSeatInput.addEventListener("input",function (e){
        if(parseInt(maxSeatInput.value)<parseInt(minSeatInput.value)) {
            maxSeatInput.value = minSeatInput.value;
        }
        maxRangeSeat.value= maxSeatInput.value;
    })

    minSeatInput.addEventListener("input",function (e){
        if(minSeatInput.value==="" || isNaN(minSeatInput.value) || parseInt(minSeatInput.value)<2) {
            minSeatInput.value=2;
            minRangeSeat.value=2;
        }else{
            minRangeSeat.value = minSeatInput.value;
        }
    })

    maxSeatInput.addEventListener("input",function (e){
        if(maxSeatInput.value==="" || isNaN(maxSeatInput.value) || parseInt(maxSeatInput.value)>10) {
            maxSeatInput.value=10;
            maxRangeSeat.value=10;
        }else{
            maxRangeSeat.value = maxSeatInput.value;
        }
    })


// xử lí Apply button
const applyBtn = document.getElementById("apply")    ;
applyBtn.addEventListener("click",function (e){
    const selectedMaterialValue = document.querySelector('input[name="material"]:checked');

    const getUpdateURL = updateParam(minPriceInput.value,maxPriceInput.value,minSeatInput.value,maxSeatInput.value,selectedMaterialValue.value);
    window.location.href=getUpdateURL;
})

// lấy các giá trị đường dẫn gắn vào các biến
    //lấy các giá trị trên đường dẫn gán lại cho các biến trong filter
  function getParamToFill() {
      const currentURL = window.location.href;
      if (currentURL.indexOf("filter") !== -1) {
          const filterParam = getParam("filter");
          const arrFilterParam = filterParam.split(",");
          const arrFilterParamPrice = arrFilterParam.filter(function (item, index) {
              return item.indexOf("pricePerDay") !== -1
          })
          const arrFilterParamSeat = arrFilterParam.filter(function (item, index) {
              return item.indexOf("numberOfSeats") !== -1
          })


          const priceParamValue = arrFilterParamPrice.map(function (item, index) {
              return item.split(/[:<>]/)[1];
          })
          const seatParamValue = arrFilterParamSeat.map(function (item, index) {
              return item.split(/[:<>]/)[1];
          })

          if (priceParamValue.length > 1) {
              minPriceInput.value = priceParamValue[0];
              maxPriceInput.value = priceParamValue[1];
              minRangePrice.value = minPriceInput.value;
              maxRangePrice.value = maxPriceInput.value;
          }else {
              minPriceInput.value = priceParamValue[0];
              minRangePrice.value = minPriceInput.value;
              maxRangePrice.value =  minPriceInput.value;
              maxPriceInput.value = minPriceInput.value;
          }
          if(seatParamValue.length>1) {
              minSeatInput.value = seatParamValue[0];
              maxSeatInput.value = seatParamValue[1];
              minRangeSeat.value = minSeatInput.value;
              maxRangeSeat.value =  maxSeatInput.value;
          }else {
              minSeatInput.value = seatParamValue[0];
              maxSeatInput.value =  minSeatInput.value;
              minRangeSeat.value =  minSeatInput.value;
              maxRangeSeat.value =  minSeatInput.value;
          }
          let fuelType =null;
          if(currentURL.indexOf("fuelType")!==-1) {
              const fuel = arrFilterParam.filter(function (item, index) {
                  return item.indexOf("fuelType") !== -1
              })
              const fuelValue = fuel.map(function (item, index) {
                  return item.split(/[:<>]/)[1];
              })
              fuelType= fuelValue[0];
          }else {
              fuelType='all';
          }
          const radioValue = document.getElementsByName("material");
          for (const node of radioValue) {
              if (node.value === fuelType) {
                  node.checked = true; // Đánh dấu radio button phù hợp
                  break; // Thoát khỏi vòng lặp khi đã đánh dấu
              }
          }
          if(minSeatInput.value===undefined) {
              minSeatInput.value=2;
              minRangeSeat.value=  minSeatInput.value;
          }
          if(maxSeatInput.value===undefined){
              maxSeatInput.value=10;
              maxRangeSeat.value= maxSeatInput.value;
          }
          if(maxPriceInput.value===undefined) {
              maxPriceInput.value=10;
              maxRangePrice.value = maxPriceInput.value;
          }
          if(minPriceInput.value===undefined){
              minPriceInput.value=20;
              minRangePrice.value=minPriceInput.value;
          }

      }
  }

  window.onload=getParamToFill;

});




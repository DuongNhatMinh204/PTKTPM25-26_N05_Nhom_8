const userId = localStorage.getItem("userId")
let cartItems = [];

// Gọi API lấy giỏ hàng theo userId
async function fetchCart() {
    const res = await fetch(`/order-temp/get-by-user/${userId}`);
    const data = await res.json();
    if (data.code === 1000) {
        cartItems = data.data.map(item => ({
            orderTempId: item.id,
            bookId: item.book_id,
            img: item.bookUrl,
            name: item.bookName,
            price: item.bookPrice,
            quantity: item.bookQuantity,
            total: item.bookTotalPrice
        }));
        renderCart();
    } else {
        alert("Không thể tải giỏ hàng!");
    }
}

// Render giỏ hàng
function renderCart() {
    const tbody = document.querySelector("#cartTable tbody");
    tbody.innerHTML = "";

    cartItems.forEach((item, index) => {
        const row = document.createElement("tr");
        row.innerHTML = `
          <td><input type="checkbox" class="select-item" data-index="${index}"></td>
          <td><img src="${item.img}" alt="${item.name}" width="60"></td>
          <td>${item.name}</td>
          <td>${item.price.toLocaleString()} ₫</td>
          <td>
            <button onclick="updateQty(${index}, -1)">-</button>
            ${item.quantity}
            <button onclick="updateQty(${index}, 1)">+</button>
          </td>
          <td>${(item.price * item.quantity).toLocaleString()} ₫</td>
          <td><span class="delete-btn" onclick="removeItem(${index})">🗑</span></td>
        `;
        tbody.appendChild(row);
    });

    updateTotal();
}

// Cập nhật tổng tiền
function updateTotal() {
    const checkboxes = document.querySelectorAll(".select-item:checked");
    let total = 0;

    checkboxes.forEach(cb => {
        const item = cartItems[cb.dataset.index];
        total += item.price * item.quantity;
    });

    document.getElementById("grandTotal").innerText = `Tổng cộng: ${total.toLocaleString()} ₫`;
}

// Cập nhật số lượng (client-side demo, bạn có thể gọi API update giỏ hàng nếu có)
function updateQty(index, delta) {
    if (cartItems[index].quantity + delta > 0) {
        cartItems[index].quantity += delta;
        renderCart();
    }
}

// Xóa sản phẩm (ở đây chỉ xóa local, nếu backend có API xóa thì gọi vào đây)
function removeItem(index) {
    cartItems.splice(index, 1);
    renderCart();
}

// Thanh toán
async function checkout() {
    const address = document.getElementById("address").value;
    const paymentType = document.getElementById("paymentType").value;

    const selected = Array.from(document.querySelectorAll(".select-item:checked"))
        .map(cb => cartItems[cb.dataset.index]);

    if (selected.length === 0) {
        alert("Vui lòng chọn ít nhất 1 sản phẩm!");
        return;
    }

    const payload = {
        userId: userId,
        address: address,
        paymentType: paymentType,
        orderItems: selected.map(item => ({
            bookId: item.bookId,
            quantity: item.quantity,
            orderTempId: item.orderTempId // gửi kèm id
        }))
    };

    const res = await fetch("/order/create", {
        method: "POST",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify(payload)
    });

    const data = await res.json();
    if (data.code === 1000) {
        alert("Đặt hàng thành công!");
        // reload lại giỏ hàng
        fetchCart();
    } else {
        alert("Có lỗi khi đặt hàng!");
    }
}

// Xử lý chọn tất cả
document.addEventListener("change", (e) => {
    if (e.target.id === "selectAll") {
        document.querySelectorAll(".select-item").forEach(cb => cb.checked = e.target.checked);
        updateTotal();
    } else if (e.target.classList.contains("select-item")) {
        updateTotal();
    }
});

// Load khi mở trang
fetchCart();

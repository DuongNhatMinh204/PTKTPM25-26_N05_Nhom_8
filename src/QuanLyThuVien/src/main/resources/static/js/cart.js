// ✅ Lấy userId từ localStorage
const userId = localStorage.getItem("userId");
let cartItems = [];

// 🆕 Định nghĩa sẵn API base để dễ mở rộng khi deploy
const BASE_URL = ""; // ví dụ: "http://localhost:8080"
const CART_API = `${BASE_URL}/order-temp`;
const ORDER_API = `${BASE_URL}/order/create`;

// 🟢 Gọi API lấy giỏ hàng theo userId
async function fetchCart() {
    try {
        const res = await fetch(`${CART_API}/get-by-user/${userId}`);
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
            alert("⚠️ Không thể tải giỏ hàng: " + (data.message || "Lỗi không xác định!"));
        }
    } catch (err) {
        console.error("Error loading cart:", err);
        alert("❌ Lỗi kết nối đến server!");
    }
}

// ✅ Render giỏ hàng ra bảng
function renderCart() {
    const tbody = document.querySelector("#cartTable tbody");
    tbody.innerHTML = "";

    if (cartItems.length === 0) {
        tbody.innerHTML = `<tr><td colspan="7" style="text-align:center;">🛒 Giỏ hàng trống!</td></tr>`;
        document.getElementById("grandTotal").innerText = `Tổng cộng: 0 ₫`;
        return;
    }

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

// ✅ Cập nhật tổng tiền
function updateTotal() {
    const checkboxes = document.querySelectorAll(".select-item:checked");
    let total = 0;

    checkboxes.forEach(cb => {
        const item = cartItems[cb.dataset.index];
        total += item.price * item.quantity;
    });

    document.getElementById("grandTotal").innerText = `Tổng cộng: ${total.toLocaleString()} ₫`;
}

// ✅ Cập nhật số lượng (client-side + optional API)
async function updateQty(index, delta) {
    const item = cartItems[index];
    const newQty = item.quantity + delta;

    if (newQty <= 0) return;

    // 🆕 Nếu backend có API cập nhật giỏ hàng thì gọi tại đây
    try {
        const res = await fetch(`${CART_API}/update/${item.orderTempId}`, {
            method: "PUT",
            headers: { "Content-Type": "application/json" },
            body: JSON.stringify({ quantity: newQty })
        });

        const data = await res.json();
        if (data.code === 1000) {
            cartItems[index].quantity = newQty;
            renderCart();
        } else {
            alert("⚠️ Cập nhật thất bại: " + (data.message || "Lỗi không xác định"));
        }
    } catch (err) {
        console.error("Error updating quantity:", err);
        alert("❌ Không thể cập nhật số lượng!");
    }
}

// ✅ Xóa sản phẩm khỏi giỏ
async function removeItem(index) {
    const item = cartItems[index];
    if (!confirm(`Bạn có chắc muốn xóa "${item.name}" khỏi giỏ hàng?`)) return;

    try {
        // 🆕 Gọi API xóa (nếu có)
        const res = await fetch(`${CART_API}/delete/${item.orderTempId}`, {
            method: "DELETE"
        });
        const data = await res.json();

        if (data.code === 1000) {
            cartItems.splice(index, 1);
            renderCart();
        } else {
            alert("⚠️ Xóa thất bại: " + (data.message || "Lỗi không xác định!"));
        }
    } catch (err) {
        console.error("Error removing item:", err);
        alert("❌ Không thể kết nối đến server!");
    }
}

// ✅ Thanh toán
async function checkout() {
    const address = document.getElementById("address").value.trim();
    const paymentType = document.getElementById("paymentType").value;

    const selected = Array.from(document.querySelectorAll(".select-item:checked"))
        .map(cb => cartItems[cb.dataset.index]);

    if (selected.length === 0) {
        alert("⚠️ Vui lòng chọn ít nhất 1 sản phẩm để đặt hàng!");
        return;
    }

    if (!address) {
        alert("⚠️ Vui lòng nhập địa chỉ giao hàng!");
        return;
    }

    const payload = {
        userId: userId,
        address: address,
        paymentType: paymentType,
        orderItems: selected.map(item => ({
            bookId: item.bookId,
            quantity: item.quantity,
            orderTempId: item.orderTempId
        }))
    };

    try {
        const res = await fetch(ORDER_API, {
            method: "POST",
            headers: { "Content-Type": "application/json" },
            body: JSON.stringify(payload)
        });

        const data = await res.json();
        if (data.code === 1000) {
            alert("🎉 Đặt hàng thành công!");
            fetchCart(); // reload giỏ hàng sau khi đặt
        } else {
            alert("⚠️ Có lỗi khi đặt hàng: " + (data.message || "Lỗi không xác định!"));
        }
    } catch (err) {
        console.error("Checkout error:", err);
        alert("❌ Lỗi kết nối khi đặt hàng!");
    }
}

// ✅ Xử lý chọn tất cả / cập nhật tổng
document.addEventListener("change", (e) => {
    if (e.target.id === "selectAll") {
        document.querySelectorAll(".select-item").forEach(cb => cb.checked = e.target.checked);
        updateTotal();
    } else if (e.target.classList.contains("select-item")) {
        updateTotal();
    }
});

// ✅ Load khi mở trang
fetchCart();

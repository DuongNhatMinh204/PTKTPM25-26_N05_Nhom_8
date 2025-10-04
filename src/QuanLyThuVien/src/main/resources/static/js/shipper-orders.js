const phone = "0912345678"; // Giả lập số điện thoại login

// ===== Navbar Actions =====
function goHome() {
    document.getElementById("page-title").innerText = "📦 Danh sách đơn hàng chờ nhận";
    fetchOrders();
}

function toggleProfile() {
    const popup = document.getElementById("profile-popup");
    if (popup.style.display === "block") {
        popup.style.display = "none";
    } else {
        fetchProfile();
        popup.style.display = "block";
    }
}

function fetchProfile() {
    fetch(`/api/shipper/me?phone=${phone}`)
        .then(res => res.json())
        .then(data => {
            document.getElementById("profile-name").innerText = data.shipperName;
            document.getElementById("profile-phone").innerText = data.shipperPhone;
        });
}

function logout() {
    alert("Bạn đã đăng xuất!");
    window.location.href = "/login.html";
}

// ===== Fetch Orders =====
function fetchOrders() {
    fetch("/api/shipper/orders/shipping")
        .then(res => res.json())
        .then(data => renderOrders(data, false));
}

function showMyOrders() {
    document.getElementById("page-title").innerText = "📋 Đơn hàng của tôi";
    fetch(`/api/shipper/orders/my?phone=${phone}`)
        .then(res => {
            if (!res.ok) throw new Error("Không lấy được đơn hàng của tôi");
            return res.json();
        })
        .then(data => renderOrders(data, true))
        .catch(err => {
            console.error(err);
            document.getElementById("order-list").innerHTML = "<p>Không thể tải đơn hàng.</p>";
        });
}


// ===== Render Orders =====
function renderOrders(orders, isMyOrders) {
    const container = document.getElementById("order-list");
    container.innerHTML = "";

    if (orders.length === 0) {
        container.innerHTML = "<p>Không có đơn hàng nào.</p>";
        return;
    }

    orders.forEach(order => {
        const div = document.createElement("div");
        div.className = "order";
        let buttons = "";

        if (!isMyOrders) {
            buttons = `<button class="accept-btn" onclick="acceptOrder('${order.id}')">✅ Nhận đơn</button>`;
        } else {
            buttons = `
                <button class="success-btn" onclick="markDelivered('${order.id}')">✅ Giao thành công</button>
                <button class="fail-btn" onclick="markFailed('${order.id}')">❌ Không thành công</button>
            `;
        }

        div.innerHTML = `
            <p><strong>🆔 Mã đơn:</strong> ${order.id}</p>
            <p><strong>☎️ SĐT:</strong> ${order.user?.phone || "N/A"}</p>
            <p><strong>📍 Địa chỉ:</strong> ${order.address}</p>
            <p><strong>🕒 Ngày đặt:</strong> ${new Date(order.orderDate).toLocaleString()}</p>
            <p><strong>💵 Tổng tiền:</strong> ${order.totalPrice.toLocaleString()} VNĐ</p>
            <p><strong>💳 Thanh toán:</strong> ${order.paymentType}</p>
            <p><strong>Trạng thái đơn:</strong> ${order.orderStatus}</p>
            <p><strong>Trạng thái giao:</strong> ${order.shipping?.shippingStatus || "N/A"}</p>
            ${buttons}
        `;
        container.appendChild(div);
    });
}

// ===== Order Actions =====
function acceptOrder(orderId) {
    fetch(`/api/shipper/orders/${orderId}/accept?phone=${phone}`, { method: "PUT" })
        .then(res => {
            if (res.ok) {
                alert("Đã nhận đơn thành công!");
                showMyOrders();
            } else {
                res.text().then(msg => alert("Lỗi: " + msg));
            }
        });
}

function markDelivered(orderId) {
    fetch(`/api/shipper/orders/${orderId}/delivered`, { method: "PUT" })
        .then(res => {
            if (res.ok) {
                alert("Giao hàng thành công!");
                showMyOrders();
            } else {
                res.text().then(msg => alert("Lỗi: " + msg));
            }
        });
}

function markFailed(orderId) {
    const reason = prompt("Nhập lý do giao thất bại:");
    if (!reason) return;

    fetch(`/api/shipper/orders/${orderId}/failed`, {
        method: "PUT",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify({ reason })
    }).then(res => {
        if (res.ok) {
            alert("Đã đánh dấu đơn giao thất bại!");
            showMyOrders();
        } else {
            res.text().then(msg => alert("Lỗi: " + msg));
        }
    });
}

// ===== Load mặc định =====
document.addEventListener("DOMContentLoaded", fetchOrders);

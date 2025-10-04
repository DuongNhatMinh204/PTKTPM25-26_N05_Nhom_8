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
    const phone = "0912345678"; // Giả lập
    fetch(`/api/shipper/me?phone=${phone}`)
        .then(res => res.json())
        .then(data => {
            document.getElementById("profile-name").innerText = data.shipperName;
            document.getElementById("profile-phone").innerText = data.shipperPhone;
        })
        .catch(err => console.error("Lỗi lấy thông tin shipper:", err));
}

function logout() {
    alert("Bạn đã đăng xuất!");
    window.location.href = "/login.html";
}

function fetchOrders() {
    fetch("/api/shipper/orders/shipping")
        .then(res => res.json())
        .then(data => {
            const container = document.getElementById("order-list");
            container.innerHTML = "";

            if (data.length === 0) {
                container.innerHTML = "<p>Không có đơn hàng nào cần giao.</p>";
                return;
            }

            data.forEach(order => {
                const div = document.createElement("div");
                div.className = "order";
                div.innerHTML = `
                    <p><strong>🆔 Mã đơn:</strong> ${order.id}</p>
                    <p><strong>☎️ SĐT:</strong> ${order.user?.phone || "N/A"}</p>
                    <p><strong>📍 Địa chỉ:</strong> ${order.address}</p>
                    <p><strong>🕒 Ngày đặt:</strong> ${new Date(order.orderDate).toLocaleString()}</p>
                    <p><strong>💵 Tổng tiền:</strong> ${order.totalPrice.toLocaleString()} VNĐ</p>
                    <p><strong>💳 Thanh toán:</strong> ${order.paymentType}</p>
                    <button class="accept-btn" onclick="acceptOrder('${order.id}')">✅ Nhận đơn</button>
                `;
                container.appendChild(div);
            });
        })
        .catch(err => console.error("Lỗi lấy đơn hàng:", err));
}

function acceptOrder(orderId) {
    const phone = "0912345678"; // Giả lập
    fetch(`/api/shipper/orders/${orderId}/accept?phone=${phone}`, {
        method: "PUT"
    })
        .then(res => {
            if (res.ok) {
                alert("Đã nhận đơn thành công!");
                fetchOrders(); // reload
            } else {
                res.text().then(msg => alert("Lỗi: " + msg));
            }
        })
        .catch(err => console.error("Lỗi khi nhận đơn:", err));
}

document.addEventListener("DOMContentLoaded", fetchOrders);

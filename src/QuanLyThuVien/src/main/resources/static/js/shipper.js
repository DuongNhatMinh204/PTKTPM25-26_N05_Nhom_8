const API_URL = "http://localhost:8080/api/shipper/get-book-order";
const BASE_API = "http://localhost:8080/api/shipper";
const userId = localStorage.getItem("userId");

if (!userId) {
    alert("⚠️ Không tìm thấy thông tin shipper. Vui lòng đăng nhập lại!");
    window.location.href = "/login";
}

async function loadShipperOrders() {
    try {
        const response = await fetch(`${API_URL}?userId=${userId}`);
        const result = await response.json();

        if (!result.data || result.data.length === 0) {
            document.getElementById("orders-container").innerHTML =
                "<em>Không có đơn hàng nào được giao.</em>";
            return;
        }

        const shipperInfo = result.data[0]?.shipper;
        if (shipperInfo) {
            document.getElementById("shipper-info").innerHTML = `
                <p><strong>Tên Shipper:</strong> ${shipperInfo.shipperName}</p>
                <p><strong>Số Điện Thoại:</strong> ${shipperInfo.shipperPhone}</p>
            `;
        }

        const container = document.getElementById("orders-container");
        container.innerHTML = "";

        result.data.forEach(order => {
            const bo = order.bookOrder;

            // ✅ Tùy theo trạng thái shippingStatus hiển thị nút khác nhau
            let actionsHtml = "";
            if (order.shippingStatus === "PENDING") {
                actionsHtml = `
                    <button class="start-btn" onclick="startShipping('${order.id}')">
                        🚚 Bắt đầu giao hàng
                    </button>
                `;
            } else if (order.shippingStatus === "SHIPPING") {
                actionsHtml = `
                    <button class="success-btn" onclick="markDelivered('${order.id}')">
                        ✅ Xác nhận thành công
                    </button>
                    <button class="fail-btn" onclick="markFailed('${order.id}')">
                        ❌ Xác nhận thất bại
                    </button>
                `;
            } else {
                actionsHtml = `<em>Trạng thái: ${order.shippingStatus}</em>`;
            }

            const orderHtml = `
                <div class="order">
                    <div class="order-header">
                        <strong>Mã Giao Hàng:</strong> ${order.id} <br>
                        <strong>Trạng Thái:</strong> ${order.shippingStatus} <br>
                        <strong>Ngày Giao:</strong> ${new Date(order.shippingDate).toLocaleString("vi-VN")}
                    </div>

                    <div><strong>Địa Chỉ Giao:</strong> ${order.shippingAddress}</div>
                    <div><strong>Ghi Chú:</strong> ${order.note || "Không có"}</div>
                    
                    ${
                bo
                    ? `
                            <div class="book-order">
                                <strong>Đơn Hàng:</strong> ${bo.id}<br>
                                <strong>Địa Chỉ:</strong> ${bo.address}<br>
                                <strong>Thanh Toán:</strong> ${bo.paymentType}<br>
                                <strong>Tổng Tiền:</strong> ${bo.totalPrice.toLocaleString()} VND<br>
                                <strong>Trạng Thái Đơn:</strong> ${bo.orderStatus}
                            </div>
                        `
                    : "<em>Không có thông tin đơn hàng liên kết.</em>"
            }

                    <div class="actions">${actionsHtml}</div>
                </div>
            `;
            container.insertAdjacentHTML("beforeend", orderHtml);
        });
    } catch (error) {
        console.error("Lỗi tải đơn hàng:", error);
        alert("❌ Không thể tải danh sách đơn hàng!");
    }
}

async function startShipping(shippingId) {
    if (!confirm("🚚 Xác nhận bắt đầu giao hàng đơn này?")) return;

    try {
        const response = await fetch(`${BASE_API}/start-shipping?shippingId=${shippingId}`, {
            method: "PUT"
        });
        if (response.ok) {
            alert("✅ Đã chuyển đơn sang trạng thái 'Đang giao'!");
            loadShipperOrders();
        } else {
            alert("❌ Không thể cập nhật trạng thái!");
        }
    } catch (error) {
        console.error(error);
        alert("⚠️ Lỗi kết nối server!");
    }
}

async function markDelivered(shippingId) {
    if (!confirm("✅ Xác nhận đơn hàng đã giao thành công?")) return;

    try {
        const response = await fetch(`${BASE_API}/delivered?shippingId=${shippingId}`, {
            method: "PUT"
        });
        if (response.ok) {
            alert("🎉 Đơn hàng đã được đánh dấu là 'Thành công'!");
            loadShipperOrders();
        } else {
            alert("❌ Cập nhật thất bại!");
        }
    } catch (error) {
        console.error(error);
        alert("⚠️ Lỗi kết nối server!");
    }
}

// ✅ Xác nhận giao thất bại
async function markFailed(shippingId) {
    if (!confirm("❌ Xác nhận giao hàng thất bại?")) return;

    try {
        const response = await fetch(`${BASE_API}/failed?shippingId=${shippingId}`, {
            method: "PUT"
        });
        if (response.ok) {
            alert("🚫 Đơn hàng đã được đánh dấu là 'Thất bại'!");
            loadShipperOrders();
        } else {
            alert("❌ Cập nhật thất bại!");
        }
    } catch (error) {
        console.error(error);
        alert("⚠️ Lỗi kết nối server!");
    }
}

loadShipperOrders();

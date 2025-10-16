// 🆕 Tách API base URL (nếu backend có host riêng, dễ mở rộng sau)
const BASE_URL = ""; // ví dụ: "http://localhost:8080"
const ORDER_API = `${BASE_URL}/order/get-all-order`;
const CONFIRM_API = `${BASE_URL}/order/admin-confirm/`;
const REJECT_API = `${BASE_URL}/order/admin-reject/`; // 🆕 thêm API từ chối đơn

// 🔄 Hàm load danh sách đơn hàng
async function loadOrders() {
    try {
        const response = await fetch(ORDER_API);
        const result = await response.json();

        if (result.code === 1000) {
            const tbody = document.querySelector("#orderTable tbody");
            tbody.innerHTML = "";

            result.data.forEach(order => {
                // 🆕 Chuẩn hóa format ngày và trạng thái
                const orderDate = new Date(order.orderDate).toLocaleString("vi-VN");
                const statusColor = order.orderStatus === "CONFIRMED" ? "green" :
                                    order.orderStatus === "REJECTED" ? "red" : "orange";

                const row = `
                    <tr>
                        <td>${order.id}</td>
                        <td>${order.address}</td>
                        <td>${order.totalPrice} $</td>
                        <td>${order.paymentType}</td>
                        <td>${orderDate}</td>
                        <td style="color:${statusColor}; font-weight:bold;">
                            ${order.orderStatus}
                        </td>
                        <td>
                            <button onclick="viewDetail('${order.id}')">👁 Xem</button>
                        </td>
                        <td>
                            <button class="btn-update" onclick="confirmOrder('${order.id}')">✅ Xác Nhận</button>
                            <button class="btn-delete" onclick="rejectOrder('${order.id}')">❌ Từ Chối</button>
                        </td>
                    </tr>
                `;
                tbody.innerHTML += row;
            });
        } else {
            console.error("Không tải được danh sách đơn hàng:", result.message);
        }
    } catch (error) {
        console.error("Error loading orders:", error);
    }
}

// ✅ Xác nhận đơn hàng
async function confirmOrder(id) {
    if (!confirm("Bạn có chắc chắn muốn xác nhận đơn hàng này?")) return;

    try {
        const response = await fetch(CONFIRM_API + id, { method: "PUT" });
        const result = await response.json();

        if (result.code === 1000) {
            alert("✅ Xác nhận thành công!");
            loadOrders();
        } else {
            alert("⚠️ Xác nhận thất bại: " + result.message);
        }
    } catch (error) {
        console.error("Error confirming order:", error);
    }
}

// 🆕 Thêm hàm từ chối đơn hàng (API reject)
async function rejectOrder(id) {
    if (!confirm("Bạn có chắc chắn muốn từ chối đơn hàng này?")) return;

    try {
        const response = await fetch(REJECT_API + id, { method: "PUT" });
        const result = await response.json();

        if (result.code === 1000) {
            alert("❌ Từ chối thành công!");
            loadOrders();
        } else {
            alert("⚠️ Từ chối thất bại: " + result.message);
        }
    } catch (error) {
        console.error("Error rejecting order:", error);
    }
}

// ✅ Xem chi tiết đơn hàng
function viewDetail(orderId) {
    fetch(`/order/detail/${orderId}`)
        .then(res => res.json())
        .then(data => {
            if (data.code === 1000) {
                const tbody = document.querySelector("#detailTable tbody");
                tbody.innerHTML = "";

                data.data.forEach(item => {
                    const row = `
                        <tr>
                            <td>${item.bookName}</td>
                            <td>${item.authorship}</td>
                            <td>${item.bookGerne}</td>
                            <td>${item.bookPublisher}</td>
                            <td>${item.quantity}</td>
                            <td>${item.price} đ</td>
                        </tr>`;
                    tbody.innerHTML += row;
                });

                document.getElementById("detailModal").style.display = "block";
            } else {
                alert("Không tải được chi tiết đơn hàng!");
            }
        })
        .catch(error => console.error("Error loading order details:", error));
}

// ✅ Đóng modal chi tiết
function closeDetailModal() {
    document.getElementById("detailModal").style.display = "none";
}

// ✅ Đóng modal khi click ra ngoài
window.onclick = function(event) {
    const modal = document.getElementById("detailModal");
    if (event.target === modal) {
        modal.style.display = "none";
    }
}

// ✅ Gọi lần đầu khi trang load
loadOrders();

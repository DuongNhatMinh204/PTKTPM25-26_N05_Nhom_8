const ORDER_API = "/order/get-all-order";
const CONFIRM_API = "/order/admin-confirm/";

async function loadOrders() {
    try {
        const response = await fetch(ORDER_API);
        const result = await response.json();

        if (result.code === 1000) {
            const tbody = document.querySelector("#orderTable tbody");
            tbody.innerHTML = "";

            result.data.forEach(order => {
                const row = `
                    <tr>
                        <td>${order.id}</td>
                        <td>${order.address}</td>
                        <td>${order.totalPrice} $</td>
                        <td>${order.paymentType}</td>
                        <td>${new Date(order.orderDate).toLocaleString()}</td>
                        <td>${order.orderStatus}</td>
                        <td>
                                <button onclick="viewDetail('${order.id}')">👁 Xem</button>
                        </td>
                        <td>
                            <button class="btn-update" onclick="confirmOrder('${order.id}')">✅ Xác Nhận</button>
                            <button class="btn-delete" onclick="rejectOrder('${order.id}')">❌ Không Xác Nhận</button>
                        </td>
                    </tr>
                `;
                tbody.innerHTML += row;
            });
        }
    } catch (error) {
        console.error("Error loading orders:", error);
    }
}

// Gửi API xác nhận đơn
async function confirmOrder(id) {
    if (!confirm("Bạn có chắc chắn muốn xác nhận đơn hàng này?")) return;

    try {
        const response = await fetch(CONFIRM_API + id, { method: "PUT" });
        const result = await response.json();

        if (result.code === 1000) {
            alert("Xác nhận thành công!");
            loadOrders();
        } else {
            alert("Xác nhận thất bại!");
        }
    } catch (error) {
        console.error("Error confirming order:", error);
    }
}

// Giả sử bạn sẽ thêm API reject sau này
async function rejectOrder(id) {
    alert("API reject chưa được implement!");
}

// Xem chi tiết đơn hàng
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
            }
        });
}
// Đóng modal khi bấm nút X
function closeDetailModal() {
    document.getElementById("detailModal").style.display = "none";
}

// Đóng modal khi click ra ngoài
window.onclick = function(event) {
    const modal = document.getElementById("detailModal");
    if (event.target === modal) {
        modal.style.display = "none";
    }
}

loadOrders();

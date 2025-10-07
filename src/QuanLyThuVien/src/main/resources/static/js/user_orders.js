document.addEventListener("DOMContentLoaded", () => {
    const userId = localStorage.getItem("userId");
    if (!userId) {
        alert("Vui lòng đăng nhập trước!");
        window.location.href = "/login";
        return;
    }

    fetch(`/order/get-all?userId=${userId}`)
        .then(res => res.json())
        .then(data => {
            if (data.code === 1000) renderOrders(data.data);
            else alert("Không thể tải danh sách đơn hàng!");
        })
        .catch(err => console.error("Error:", err));
});

function renderOrders(orders) {
    const container = document.getElementById("orderList");
    container.innerHTML = "";

    if (orders.length === 0) {
        container.innerHTML = "<p class='empty'>Bạn chưa có đơn hàng nào.</p>";
        return;
    }

    orders.forEach(order => {
        const card = document.createElement("div");
        card.className = "order-card";
        card.innerHTML = `
            <h3>📘 Mã đơn: ${order.id}</h3>
            <p><strong>Địa chỉ:</strong> ${order.address}</p>
            <p><strong>Tổng tiền:</strong> ${order.totalPrice} ₫</p>
            <p><strong>Thanh toán:</strong> ${order.paymentType}</p>
            <p><strong>Ngày đặt:</strong> ${new Date(order.orderDate).toLocaleString()}</p>
            <p><strong>Trạng thái:</strong> ${order.orderStatus}</p>
            <p><strong>Lý do hủy:</strong> ${order.cancelReason ?? "Không có"}</p>
        `;
        container.appendChild(card);
    });
}

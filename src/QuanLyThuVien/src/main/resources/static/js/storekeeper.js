// JS gốc cho đơn hàng, thêm JS cho inventory và logs vào cùng file

// Chức năng Tabs
function showTab(tabId) {
    const tabs = document.querySelectorAll('.tab-content');
    tabs.forEach(tab => tab.style.display = 'none');
    document.getElementById(tabId).style.display = 'block';

    const btns = document.querySelectorAll('.tab-btn');
    btns.forEach(btn => btn.classList.remove('active'));
    event.target.classList.add('active');

    // Load dữ liệu tương ứng nếu cần
    if (tabId === 'orders') loadOrders();
    if (tabId === 'inventory') loadBooks();
    if (tabId === 'logs') loadAllLogs();
}

// Phần Đơn Hàng Đã Duyệt (giữ nguyên)
const ORDERS_API = "/api/librarian/orders";
const SHIPPER_API = "/api/shipper/get-all";

async function loadOrders() {
    const response = await fetch(`${ORDERS_API}/approved`);
    const orders = await response.json();

    const container = document.getElementById("orders-container");
    container.innerHTML = "";

    orders.forEach(order => {
        const div = document.createElement("div");
        div.className = "order";

        const user = order.user;
        const date = order.orderDate ? new Date(order.orderDate).toLocaleString("vi-VN") : "Chưa có ngày";

        let booksHtml = "";
        if (order.bookOrders && order.bookOrders.length > 0) {
            booksHtml = order.bookOrders.map(bo =>
                `<div class="book-item">📘 ${bo.book.bookName} x ${bo.quantity}</div>`
            ).join("");
        } else {
            booksHtml = "<em>Chưa có sách trong đơn</em>";
        }

        div.innerHTML = `
            <div class="order-header">
                <strong>Mã đơn:</strong> ${order.id} |
                <strong>Ngày đặt:</strong> ${date} |
                <strong>Trạng thái:</strong> ${order.orderStatus}
            </div>

            
            <div><strong>Địa chỉ giao hàng:</strong> ${order.address}</div>
            <div><strong>Hình thức thanh toán:</strong> ${order.paymentType}</div>
            <div><strong>Tổng tiền:</strong> ${order.totalPrice.toLocaleString()} VND</div>

            <div class="order-books">${booksHtml}</div>

            <div>
                <button class="confirm-btn" onclick="selectShipper('${order.id}')">Xác nhận</button>
                <button class="cancel-btn" onclick="cancelOrder('${order.id}')">Hủy đơn</button>
            </div>
        `;

        container.appendChild(div);
    });
}

/** 🧾 Mở modal chọn shipper khi bấm xác nhận **/
async function selectShipper(orderId) {
    // Xóa modal cũ (nếu còn)
    document.querySelectorAll(".modal").forEach(m => m.remove());

    const response = await fetch(SHIPPER_API);
    const result = await response.json();
    const shippers = result.data;

    let options = "";
    shippers.forEach(s => {
        options += `<option value="${s.id}">${s.shipperName} - ${s.shipperPhone}</option>`;
    });

    // Sinh ID duy nhất (dựa theo orderId + timestamp)
    const uniqueSuffix = `${orderId}-${Date.now()}`;
    const selectId = `shipperSelect-${uniqueSuffix}`;
    const modalId = `shipperModal-${uniqueSuffix}`;

    // Tạo modal
    const modalHtml = `
        <div class="modal" id="${modalId}">
            <div class="modal-content">
                <span class="close" onclick="closeShipperModal('${modalId}')">&times;</span>
                <h3>Chọn Shipper Giao Hàng</h3>
                <select id="${selectId}" style="width:100%;padding:10px;margin-top:10px;">
                    <option value="">-- Chọn shipper --</option>
                    ${options}
                </select>
                <div style="margin-top:20px;text-align:right;">
                    <button onclick="confirmOrder('${orderId}', '${selectId}', '${modalId}')" class="confirm-btn">Xác nhận giao</button>
                </div>
            </div>
        </div>
    `;
    document.body.insertAdjacentHTML("beforeend", modalHtml);
}

/** 🔐 Gửi API xác nhận đơn hàng với shipper đã chọn **/
async function confirmOrder(orderId, selectId, modalId) {
    const shipperId = document.getElementById(selectId).value;
    if (!shipperId) {
        alert("⚠️ Vui lòng chọn shipper!");
        return;
    }

    const url = `${ORDERS_API}/confirm?bookOrderId=${orderId}&shipperId=${shipperId}`;
    const response = await fetch(url, { method: "PUT" });

    if (response.ok) {
        alert("✅ Xác nhận đơn hàng thành công!");
        closeShipperModal(modalId);
        loadOrders();
    } else {
        alert("❌ Xác nhận thất bại!");
    }
}

/** ❌ Đóng modal chọn shipper **/
function closeShipperModal(modalId) {
    const modal = document.getElementById(modalId);
    if (modal) modal.remove();
}

async function cancelOrder(orderId) {
    const reason = prompt("Nhập lý do hủy đơn:");
    if (!reason) return;

    const response = await fetch(`${ORDERS_API}/${orderId}/cancel`, {
        method: "PUT",
        headers: {
            "Content-Type": "application/json"
        },
        body: JSON.stringify({ cancelReason: reason })
    });

    if (response.ok) {
        alert("❌ Đã hủy đơn hàng!");
        loadOrders();
    } else {
        alert("Hủy đơn hàng thất bại!");
    }
}

// Phần Quản Lý Kho Sách (cập nhật tồn kho, thêm/bớt)
const BOOKS_API = "http://localhost:8080/api/storekeeper/books";

async function loadBooks() {
    const response = await fetch(BOOKS_API);
    const books = await response.json();

    const container = document.getElementById("books");
    container.innerHTML = "";

    books.forEach(book => {
        const div = document.createElement("div");
        div.className = "book";

        div.innerHTML = `
            <div class="book-header">
                <strong>Mã sách:</strong> ${book.id} |
                <strong>Tên sách:</strong> ${book.bookName} |
                <strong>Số lượng hiện tại:</strong> ${book.quantity || 0}
            </div>

            <div><strong>Tác giả:</strong> ${book.author || "Chưa có"}</div>
            <div><strong>Thể loại:</strong> ${book.genre || "Chưa có"}</div>
            <div><strong>Giá:</strong> ${book.price ? book.price.toLocaleString() : 0} VND</div>

            <div>
                <button class="increase-btn" onclick="increaseQuantity('${book.id}')">Tăng số lượng</button>
                <button class="decrease-btn" onclick="decreaseQuantity('${book.id}')">Giảm số lượng</button>
            </div>
        `;

        container.appendChild(div);
    });
}

async function increaseQuantity(bookId) {
    const qty = parseInt(prompt("Nhập số lượng cần tăng (lớn hơn 0):"));
    if (isNaN(qty) || qty <= 0) {
        alert("❌ Số lượng không hợp lệ!");
        return;
    }

    const response = await fetch(`${BOOKS_API}/${bookId}/add`, {
        method: "PUT",
        headers: {
            "Content-Type": "application/json"
        },
        body: JSON.stringify({ quantity: qty })
    });

    if (response.ok) {
        alert("✅ Đã tăng số lượng sách!");
        loadBooks();
    } else {
        alert("❌ Không thể cập nhật!");
    }
}

async function decreaseQuantity(bookId) {
    const qty = parseInt(prompt("Nhập số lượng cần giảm (lớn hơn 0):"));
    if (isNaN(qty) || qty <= 0) {
        alert("❌ Số lượng không hợp lệ!");
        return;
    }

    const response = await fetch(`${BOOKS_API}/${bookId}/subtract`, {
        method: "PUT",
        headers: {
            "Content-Type": "application/json"
        },
        body: JSON.stringify({ quantity: qty })
    });

    if (response.ok) {
        alert("✅ Đã giảm số lượng sách!");
        loadBooks();
    } else {
        alert("❌ Không thể cập nhật!");
    }
}

// Phần Lịch Sử Log Kho
const LOGS_API = "http://localhost:8080/api/storekeeper/warehouse-logs";

async function loadAllLogs() {
    const response = await fetch(LOGS_API);
    const logs = await response.json();

    displayLogs(logs);
}

async function loadLogsByBook() {
    const bookId = document.getElementById("bookIdInput").value.trim();
    if (!bookId) {
        alert("❌ Vui lòng nhập mã sách!");
        return;
    }

    const response = await fetch(`${LOGS_API}/book/${bookId}`);
    const logs = await response.json();

    displayLogs(logs);
}

function displayLogs(logs) {
    const container = document.getElementById("logs-container");
    container.innerHTML = "";

    if (logs.length === 0) {
        container.innerHTML = "<em>Chưa có log nào</em>";
        return;
    }

    logs.forEach(log => {
        const div = document.createElement("div");
        div.className = "log";

        const date = log.date ? new Date(log.date).toLocaleString("vi-VN") : "Chưa có ngày";

        div.innerHTML = `
            <div class="log-header">
                <strong>Mã log:</strong> ${log.id} |
                <strong>Ngày:</strong> ${date} |
                <strong>Loại:</strong> ${log.type || "Chưa xác định"}
            </div>

            <div><strong>Mã sách:</strong> ${log.book ? log.book.id : "Chưa có"}</div>
            <div><strong>Số lượng:</strong> ${log.quantity || 0}</div>
            <div><strong>Ghi chú:</strong> ${log.note || "Không có"}</div>
        `;

        container.appendChild(div);
    });
}

// Load đơn hàng mặc định khi trang mở
loadOrders();
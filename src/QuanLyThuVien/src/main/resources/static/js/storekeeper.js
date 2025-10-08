// Storekeeper.js (fixed + optimized, giữ toàn bộ chức năng)
// API endpoints (thay đổi BASE_URL nếu deploy)
const BASE_URL = ""; // ví dụ "" cho cùng origin, hoặc "http://localhost:8080"
const ORDERS_API = `${BASE_URL}/api/librarian/orders`;
const SHIPPER_API = `${BASE_URL}/api/shipper/get-all`;
const BOOKS_API = `${BASE_URL}/api/storekeeper/books`; // inventory
const LOGS_API = `${BASE_URL}/api/storekeeper/warehouse-logs`;

// Book management API base (module quản lý sách)
const BOOK_MANAGE_API_BASE = `${BASE_URL}/book`; // nếu backend khác, sửa ở đây
const UPLOAD_API = `${BASE_URL}/api/upload/image`;

/* ------------------ Utilities ------------------ */
// safe parse response as json or text
async function safeParseJsonOrText(response) {
    if (!response) return null;
    const ct = response.headers ? (response.headers.get("content-type") || "") : "";
    if (ct.includes("application/json")) {
        try { return await response.json(); } catch (e) { return { text: await response.text() }; }
    } else {
        const txt = await response.text();
        return txt;
    }
}

// escape text to avoid injecting HTML
function escapeHtml(unsafe) {
    if (unsafe === null || unsafe === undefined) return "";
    return String(unsafe)
        .replaceAll("&", "&amp;")
        .replaceAll("<", "&lt;")
        .replaceAll(">", "&gt;")
        .replaceAll('"', "&quot;")
        .replaceAll("'", "&#039;");
}

// produce a safe encoded JSON string for later decode
function encodeObjectForAttr(obj) {
    try {
        return encodeURIComponent(JSON.stringify(obj));
    } catch (e) {
        return encodeURIComponent('{}');
    }
}

// decode from encoded string back to object
function decodeObjectFromAttr(encoded) {
    try {
        return JSON.parse(decodeURIComponent(encoded));
    } catch (e) {
        return {};
    }
}

/* ------------------ UI: Tab + Modal helpers ------------------ */
function showTab(tabId, evt) {
    const tabs = document.querySelectorAll('.tab-content');
    tabs.forEach(tab => tab.style.display = 'none');
    const target = document.getElementById(tabId);
    if (target) target.style.display = 'block';

    const btns = document.querySelectorAll('.nav-btn');
    btns.forEach(btn => btn.classList.remove('active'));
    if (evt && evt.target) {
        evt.target.classList.add('active');
    } else {
        const fallbackBtn = Array.from(btns).find(b => b.getAttribute('data-tab') === tabId);
        if (fallbackBtn) fallbackBtn.classList.add('active');
    }

    if (tabId === 'orders') loadOrders();
    if (tabId === 'inventory') loadBooks();
    if (tabId === 'manage-books') manageLoadBooks();
    if (tabId === 'logs') loadAllLogs();
}

function openModal(id) {
    const m = document.getElementById(id);
    if (m) m.style.display = 'flex';
}
function closeModal(id) {
    const m = document.getElementById(id);
    if (m) m.style.display = 'none';
}

/* ------------------ ORDERS ------------------ */
async function loadOrders() {
    try {
        const response = await fetch(`${ORDERS_API}/approved`);
        if (!response.ok) throw new Error(`HTTP ${response.status}`);
        const orders = await response.json();

        const container = document.getElementById("orders-container");
        container.innerHTML = "";

        (Array.isArray(orders) ? orders : (orders.data || [])).forEach(order => {
            const div = document.createElement("div");
            div.className = "order";

            const date = order.orderDate ? new Date(order.orderDate).toLocaleString("vi-VN") : "Chưa có ngày";

            let booksHtml = "";
            if (order.bookOrders && order.bookOrders.length > 0) {
                booksHtml = order.bookOrders.map(bo =>
                    `<div class="book-item">📘 ${escapeHtml(bo.book.bookName)} x ${bo.quantity}</div>`
                ).join("");
            } else {
                booksHtml = "<em>Chưa có sách trong đơn</em>";
            }

            div.innerHTML = `
                <div class="order-header">
                    <div><strong>Mã đơn:</strong> ${escapeHtml(order.id)}</div>
                    <div><strong>Ngày đặt:</strong> ${escapeHtml(date)}</div>
                    <div><strong>Trạng thái:</strong> ${escapeHtml(order.orderStatus)}</div>
                </div>

                <div><strong>Địa chỉ giao hàng:</strong> ${escapeHtml(order.address || "")}</div>
                <div><strong>Hình thức thanh toán:</strong> ${escapeHtml(order.paymentType || "")}</div>
                <div><strong>Tổng tiền:</strong> ${(order.totalPrice || 0).toLocaleString()} VND</div>

                <div class="order-books">${booksHtml}</div>

                <div style="margin-top:10px;">
                    <button class="confirm-btn" data-order-id="${escapeHtml(order.id)}">Xác nhận</button>
                    <button class="cancel-btn" data-cancel-id="${escapeHtml(order.id)}">Hủy đơn</button>
                </div>
            `;
            container.appendChild(div);
        });

        // delegate events for confirm/cancel to avoid many listeners
        container.querySelectorAll('[data-order-id]').forEach(btn => {
            btn.onclick = (e) => selectShipper(btn.getAttribute('data-order-id'));
        });
        container.querySelectorAll('[data-cancel-id]').forEach(btn => {
            btn.onclick = (e) => cancelOrder(btn.getAttribute('data-cancel-id'));
        });

    } catch (err) {
        console.error("loadOrders error:", err);
        alert("Không thể tải danh sách đơn hàng.");
    }
}

async function selectShipper(orderId) {
    // remove any existing shipper modals we created earlier (avoid duplicates)
    document.querySelectorAll(".shipper-modal-generated").forEach(m => m.remove());

    try {
        const response = await fetch(SHIPPER_API);
        if (!response.ok) throw new Error(`HTTP ${response.status}`);
        const result = await response.json();
        const shippers = result.data || result || [];

        let options = "";
        shippers.forEach(s => {
            options += `<option value="${escapeHtml(s.id)}">${escapeHtml(s.shipperName)} - ${escapeHtml(s.shipperPhone)}</option>`;
        });

        const uniqueSuffix = `${orderId}-${Date.now()}`;
        const selectId = `shipperSelect-${uniqueSuffix}`;
        const modalId = `shipperModal-${uniqueSuffix}`;

        const modalHtml = `
            <div class="modal shipper-modal-generated" id="${modalId}">
                <div class="modal-content">
                    <span class="close" data-remove="${modalId}">&times;</span>
                    <h3>Chọn Shipper Giao Hàng</h3>
                    <select id="${selectId}" style="width:100%;padding:10px;margin-top:10px;">
                        <option value="">-- Chọn shipper --</option>
                        ${options}
                    </select>
                    <div style="margin-top:20px;text-align:right;">
                        <button id="confirmShip_${uniqueSuffix}" class="confirm-btn">Xác nhận giao</button>
                        <button id="cancelShip_${uniqueSuffix}" class="btn-close" data-remove="${modalId}">Hủy</button>
                    </div>
                </div>
            </div>
        `;
        document.body.insertAdjacentHTML("beforeend", modalHtml);

        // wire events
        document.getElementById(`confirmShip_${uniqueSuffix}`).onclick = () => confirmOrder(orderId, selectId, modalId);
        document.querySelectorAll(`[data-remove="${modalId}"]`).forEach(el => el.onclick = () => closeShipperModal(modalId));
    } catch (err) {
        console.error("selectShipper error:", err);
        alert("Không lấy được danh sách shipper.");
    }
}

async function confirmOrder(orderId, selectId, modalId) {
    const shipperId = document.getElementById(selectId).value;
    if (!shipperId) {
        alert("⚠️ Vui lòng chọn shipper!");
        return;
    }

    try {
        const url = `${ORDERS_API}/confirm?bookOrderId=${encodeURIComponent(orderId)}&shipperId=${encodeURIComponent(shipperId)}`;
        const response = await fetch(url, { method: "PUT" });

        if (response.ok) {
            alert("✅ Xác nhận đơn hàng thành công!");
            closeShipperModal(modalId);
            loadOrders();
        } else {
            const txt = await response.text();
            console.error("confirmOrder failed:", response.status, txt);
            alert("❌ Xác nhận thất bại!");
        }
    } catch (err) {
        console.error("confirmOrder error:", err);
        alert("❌ Xác nhận thất bại!");
    }
}

function closeShipperModal(modalId) {
    const modal = document.getElementById(modalId);
    if (modal) modal.remove();
}

async function cancelOrder(orderId) {
    const reason = prompt("Nhập lý do hủy đơn:");
    if (!reason) return;

    try {
        const response = await fetch(`${ORDERS_API}/${encodeURIComponent(orderId)}/cancel`, {
            method: "PUT",
            headers: { "Content-Type": "application/json" },
            body: JSON.stringify({ cancelReason: reason })
        });

        if (response.ok) {
            alert("❌ Đã hủy đơn hàng!");
            loadOrders();
        } else {
            const txt = await response.text();
            console.error("cancelOrder failed:", response.status, txt);
            alert("Hủy đơn hàng thất bại!");
        }
    } catch (err) {
        console.error("cancelOrder error:", err);
        alert("Hủy đơn hàng thất bại!");
    }
}

/* ------------------ BOOKS (Inventory) ------------------ */
async function loadBooks() {
    try {
        const response = await fetch(BOOKS_API);
        if (!response.ok) throw new Error(`HTTP ${response.status}`);
        const books = await response.json();

        const container = document.getElementById("books");
        if (!container) return;
        container.innerHTML = "";

        (Array.isArray(books) ? books : (books.data || [])).forEach(book => {
            const div = document.createElement("div");
            div.className = "book";

            div.innerHTML = `
                <div class="book-header">
                    <div><strong>Mã sách:</strong> ${escapeHtml(book.id)}</div>
                    <div><strong>Tên sách:</strong> ${escapeHtml(book.bookName)}</div>
                    <div><strong>Số lượng hiện tại:</strong> ${escapeHtml(String(book.quantity || 0))}</div>
                </div>

                <div><strong>Tác giả:</strong> ${escapeHtml(book.author || "Chưa có")}</div>
                <div><strong>Thể loại:</strong> ${escapeHtml(book.genre || "Chưa có")}</div>
                <div><strong>Giá:</strong> ${book.price ? Number(book.price).toLocaleString() : 0} VND</div>

                <div style="margin-top:10px;">
                    <button class="increase-btn" data-increase="${escapeHtml(book.id)}">Tăng số lượng</button>
                    <button class="decrease-btn" data-decrease="${escapeHtml(book.id)}">Giảm số lượng</button>
                </div>
            `;

            container.appendChild(div);
        });

        // bind buttons
        container.querySelectorAll('[data-increase]').forEach(btn => {
            btn.onclick = () => increaseQuantity(btn.getAttribute('data-increase'));
        });
        container.querySelectorAll('[data-decrease]').forEach(btn => {
            btn.onclick = () => decreaseQuantity(btn.getAttribute('data-decrease'));
        });

    } catch (err) {
        console.error("loadBooks error:", err);
        alert("Không thể tải danh sách sách.");
    }
}

async function increaseQuantity(bookId) {
    const qty = parseInt(prompt("Nhập số lượng cần tăng (lớn hơn 0):"), 10);
    if (isNaN(qty) || qty <= 0) {
        alert("❌ Số lượng không hợp lệ!");
        return;
    }

    try {
        const response = await fetch(`${BOOKS_API}/${encodeURIComponent(bookId)}/add`, {
            method: "PUT",
            headers: { "Content-Type": "application/json" },
            body: JSON.stringify({ quantity: qty })
        });

        if (response.ok) {
            alert("✅ Đã tăng số lượng sách!");
            loadBooks();
        } else {
            const txt = await response.text();
            console.error("increaseQuantity failed:", response.status, txt);
            alert("❌ Không thể cập nhật!");
        }
    } catch (err) {
        console.error("increaseQuantity error:", err);
        alert("❌ Không thể cập nhật!");
    }
}

async function decreaseQuantity(bookId) {
    const qty = parseInt(prompt("Nhập số lượng cần giảm (lớn hơn 0):"), 10);
    if (isNaN(qty) || qty <= 0) {
        alert("❌ Số lượng không hợp lệ!");
        return;
    }

    try {
        const response = await fetch(`${BOOKS_API}/${encodeURIComponent(bookId)}/subtract`, {
            method: "PUT",
            headers: { "Content-Type": "application/json" },
            body: JSON.stringify({ quantity: qty })
        });

        if (response.ok) {
            alert("✅ Đã giảm số lượng sách!");
            loadBooks();
        } else {
            const txt = await response.text();
            console.error("decreaseQuantity failed:", response.status, txt);
            alert("❌ Không thể cập nhật!");
        }
    } catch (err) {
        console.error("decreaseQuantity error:", err);
        alert("❌ Không thể cập nhật!");
    }
}

/* ------------------ BOOK MANAGEMENT (Quản lý sách - CRUD, Search, Pagination) ------------------ */

const managePageSize = 5;
let manageCurrentPage = 1;
let manageTotalPages = 1;

async function manageLoadBooks(page = 1) {
    try {
        const response = await fetch(`${BOOK_MANAGE_API_BASE}/get-all?page=${page}&size=${managePageSize}`);
        if (!response.ok) throw new Error(`HTTP ${response.status}`);
        const result = await response.json();

        if (result.code !== 1000) {  // Giống admin
            console.error("manageLoadBooks: server returned code", result.code, result);
            alert("Không thể tải danh sách sách (server).");
            return;
        }

        const books = result.data.books || [];

        const tbody = document.querySelector("#manageBookTable tbody");
        if (!tbody) {
            console.warn("manageLoadBooks: missing #manageBookTable tbody");
            return;
        }
        tbody.innerHTML = "";

        const fragment = document.createDocumentFragment();
        books.forEach(book => {
            const tr = document.createElement("tr");
            tr.dataset.id = book.id;

            const tdImg = document.createElement("td");
            const img = document.createElement("img");
            img.src = book.imageUrl || "/images/fallback.png";
            img.alt = `Bìa ${book.bookName || ''}`;
            img.loading = "lazy";
            img.width = 60;
            tdImg.appendChild(img);
            tr.appendChild(tdImg);

            const tdName = document.createElement("td");
            tdName.textContent = book.bookName || "";
            tr.appendChild(tdName);

            const tdAuthor = document.createElement("td");
            tdAuthor.textContent = book.authorship || book.author || "";
            tr.appendChild(tdAuthor);

            const tdGenre = document.createElement("td");
            tdGenre.textContent = book.bookGerne || book.genre || "";
            tr.appendChild(tdGenre);

            const tdPublisher = document.createElement("td");
            tdPublisher.textContent = book.bookPublisher || "";
            tr.appendChild(tdPublisher);

            const tdQty = document.createElement("td");
            tdQty.textContent = String(book.quantity || 0);
            tr.appendChild(tdQty);

            const tdPrice = document.createElement("td");
            tdPrice.textContent = (book.price !== undefined && book.price !== null) ? `${Number(book.price).toLocaleString()} $` : "0 $";
            tr.appendChild(tdPrice);

            const tdAction = document.createElement("td");
            const btnUpdate = document.createElement("button");
            btnUpdate.className = "btn-update";
            btnUpdate.textContent = "Cập Nhật";
            btnUpdate.addEventListener("click", () => manageOpenUpdateModal(book));
            tdAction.appendChild(btnUpdate);

            const btnDelete = document.createElement("button");
            btnDelete.className = "btn-delete";
            btnDelete.textContent = "Xóa";
            btnDelete.addEventListener("click", () => manageDeleteBook(book.id));
            tdAction.appendChild(btnDelete);

            tr.appendChild(tdAction);
            fragment.appendChild(tr);
        });
        tbody.appendChild(fragment);

        manageCurrentPage = result.data.currentPage;
        manageTotalPages = result.data.totalPages;
        manageRenderPagination();
    } catch (error) {
        console.error("manageLoadBooks error:", error);
        alert("Error loading books (manage).");
    }
}

function manageRenderPagination() {
    const paginationDiv = document.getElementById("managePagination");
    if (!paginationDiv) return;
    paginationDiv.innerHTML = "";

    const prevBtn = document.createElement("button");
    prevBtn.innerText = "«";
    prevBtn.disabled = manageCurrentPage === 1;
    prevBtn.onclick = () => manageLoadBooks(manageCurrentPage - 1);
    paginationDiv.appendChild(prevBtn);

    for (let i = 1; i <= manageTotalPages; i++) {  // Giống admin, render tất cả
        const btn = document.createElement("button");
        btn.innerText = i;
        btn.classList.toggle("active", i === manageCurrentPage);
        btn.onclick = () => manageLoadBooks(i);
        paginationDiv.appendChild(btn);
    }

    const nextBtn = document.createElement("button");
    nextBtn.innerText = "»";
    nextBtn.disabled = manageCurrentPage === manageTotalPages;
    nextBtn.onclick = () => manageLoadBooks(manageCurrentPage + 1);
    paginationDiv.appendChild(nextBtn);
}

// Delete book (manage)
async function manageDeleteBook(id) {
    if (!confirm("Bạn có chắc chắn muốn xóa sách này?")) return;

    try {
        const response = await fetch(`${BOOK_MANAGE_API_BASE}/delete/${encodeURIComponent(id)}`, { method: "DELETE" });
        const result = await response.json();

        if (result.code === 1000) {  // Giống admin
            alert("Xóa thành công!");
            manageLoadBooks(manageCurrentPage);
        } else {
            alert(result.message || "Xóa thất bại!");
        }
    } catch (error) {
        console.error("Error manageDeleteBook:", error);
        alert("Error deleting book.");
    }
}

// Open add modal
function manageOpenAddModal() {
    openModal('addModal');
}

// Close add modal and reset form
function manageCloseAddModal() {
    closeModal('addModal');
    const form = document.getElementById("addBookForm");
    if (form) form.reset();  // Thêm reset để tránh data cũ
}

// Open update modal and prefill
function manageOpenUpdateModal(book) {
    document.getElementById("updateBookId").value = book.id || "";
    document.getElementById("updateBookName").value = book.bookName || "";
    document.getElementById("updateAuthorship").value = book.authorship || book.author || "";
    document.getElementById("updateBookGerne").value = book.bookGerne || book.genre || "";
    document.getElementById("updateBookPublisher").value = book.bookPublisher || "";
    document.getElementById("updateQuantity").value = book.quantity || 0;
    document.getElementById("updatePrice").value = book.price || 0;
    document.getElementById("updatePreviewImage").src = book.imageUrl || "/images/fallback.png";

    openModal('updateModal');
}

// Close update modal
function manageCloseUpdateModal() {
    closeModal('updateModal');
}

/* Add Book (form submit handler) */
document.getElementById("addBookForm").addEventListener("submit", async (e) => {  // Giống admin, trực tiếp trên form
    e.preventDefault();

    const fileInput = document.getElementById("bookImage");
    if (fileInput.files.length === 0) {
        alert("Vui lòng chọn ảnh sách.");
        return;
    }

    const formData = new FormData();
    formData.append("file", fileInput.files[0]);

    try {
        // Upload ảnh trước
        const uploadRes = await fetch(UPLOAD_API, {
            method: "POST",
            body: formData
        });
        const imageUrl = await uploadRes.text();  // Giống admin, giả định trả string URL

        // Lấy dữ liệu form
        const bookData = {
            bookName: document.getElementById("bookName").value,
            authorship: document.getElementById("authorship").value,
            bookGerne: document.getElementById("bookGerne").value,
            bookPublisher: document.getElementById("bookPublisher").value,
            quantity: parseInt(document.getElementById("quantity").value),
            price: parseFloat(document.getElementById("price").value),
            imageUrl: imageUrl
        };

        // Gửi API thêm sách
        const res = await fetch(`${BOOK_MANAGE_API_BASE}/add`, {
            method: "POST",
            headers: { "Content-Type": "application/json" },
            body: JSON.stringify(bookData)
        });
        const result = await res.json();

        if (result.code === 1000) {  // Giống admin
            alert("Thêm sách thành công!");
            manageCloseAddModal();
            manageLoadBooks(manageCurrentPage);
        } else {
            alert("Thêm sách thất bại!");
        }
    } catch (error) {
        console.error("Error adding book:", error);
        alert("Lỗi khi thêm sách.");
    }
});

/* Update book submit handler */
document.getElementById("updateBookForm").addEventListener("submit", async (e) => {
    e.preventDefault();

    const id = document.getElementById("updateBookId").value;
    let imageUrl = document.getElementById("updatePreviewImage").src;

    const fileInput = document.getElementById("updateBookImage");
    if (fileInput.files.length > 0) {
        const formData = new FormData();
        formData.append("file", fileInput.files[0]);

        const uploadRes = await fetch(UPLOAD_API, {
            method: "POST",
            body: formData
        });
        imageUrl = await uploadRes.text();  // Giống admin
    }

    const updateData = {
        bookName: document.getElementById("updateBookName").value,
        authorship: document.getElementById("updateAuthorship").value,
        bookGerne: document.getElementById("updateBookGerne").value,
        bookPublisher: document.getElementById("updateBookPublisher").value,
        quantity: parseInt(document.getElementById("updateQuantity").value),
        price: parseFloat(document.getElementById("updatePrice").value),
        imageUrl: imageUrl
    };

    try {
        const response = await fetch(`${BOOK_MANAGE_API_BASE}/update/${id}`, {
            method: "PUT",
            headers: { "Content-Type": "application/json" },
            body: JSON.stringify(updateData)
        });
        const result = await response.json();

        if (result.code === 1000) {  // Giống admin
            alert("Cập nhật thành công!");
            manageCloseUpdateModal();
            manageLoadBooks(manageCurrentPage);
        } else {
            alert("Cập nhật thất bại!");
        }
    } catch (error) {
        console.error("Error updating book:", error);
    }
});

/* Search books (manage) */
async function manageSearchBooks(page = 1) {
    const keyword = document.getElementById("searchKeyword").value.trim();
    if (!keyword) {
        manageLoadBooks(page);
        return;
    }

    try {
        const response = await fetch(`${BOOK_MANAGE_API_BASE}/search?page=${page}&size=${managePageSize}&keyword=${encodeURIComponent(keyword)}`);
        if (!response.ok) throw new Error(`HTTP ${response.status}`);
        const result = await response.json();

        if (result.code !== 1000) {
            alert("Tìm kiếm thất bại!");
            return;
        }

        const books = result.data.books || [];

        const tbody = document.querySelector("#manageBookTable tbody");
        tbody.innerHTML = "";

        books.forEach(book => {
            const row = `
                <tr>
                    <td><img src="${book.imageUrl}" alt="Book Image"></td>
                    <td>${book.bookName}</td>
                    <td>${book.authorship}</td>
                    <td>${book.bookGerne}</td>
                    <td>${book.bookPublisher}</td>
                    <td>${book.quantity}</td>
                    <td>${book.price} $</td>
                    <td>
                        <button class="btn-update" onclick="manageOpenUpdateModal(${book})">Cập Nhật</button>
                        <button class="btn-delete" onclick="manageDeleteBook('${book.id}')">Xóa</button>
                    </td>
                </tr>
            `;
            tbody.innerHTML += row;
        });

        manageCurrentPage = result.data.currentPage;
        manageTotalPages = result.data.totalPages;
        manageRenderPagination();
    } catch (error) {
        console.error("manageSearchBooks error:", error);
        alert("Error searching books.");
    }
}

/* ------------------ LOGS ------------------ */
async function loadAllLogs() {
    try {
        const response = await fetch(LOGS_API);
        if (!response.ok) throw new Error(`HTTP ${response.status}`);
        const logs = await response.json();
        displayLogs(Array.isArray(logs) ? logs : (logs.data || []));
    } catch (err) {
        console.error("loadAllLogs error:", err);
        alert("Không thể tải log.");
    }
}

async function loadLogsByBook() {
    const bookId = document.getElementById("bookIdInput").value.trim();
    if (!bookId) {
        alert("❌ Vui lòng nhập mã sách!");
        return;
    }

    try {
        const response = await fetch(`${LOGS_API}/book/${encodeURIComponent(bookId)}`);
        if (!response.ok) throw new Error(`HTTP ${response.status}`);
        const logs = await response.json();
        displayLogs(Array.isArray(logs) ? logs : (logs.data || []));
    } catch (err) {
        console.error("loadLogsByBook error:", err);
        alert("Không thể tải log cho sách này.");
    }
}

function displayLogs(logs) {
    const container = document.getElementById("logs-container");
    container.innerHTML = "";

    if (!logs || logs.length === 0) {
        container.innerHTML = "<em>Chưa có log nào</em>";
        return;
    }

    logs.forEach(log => {
        const div = document.createElement("div");
        div.className = "log";

        const date = log.date ? new Date(log.date).toLocaleString("vi-VN") : "Chưa có ngày";

        div.innerHTML = `
            <div class="log-header">
                <div><strong>Mã log:</strong> ${escapeHtml(log.id)}</div>
                <div><strong>Ngày:</strong> ${escapeHtml(date)}</div>
                <div><strong>Loại:</strong> ${escapeHtml(log.type || "Chưa xác định")}</div>
            </div>

            <div><strong>Mã sách:</strong> ${escapeHtml(log.book ? log.book.id : "Chưa có")}</div>
            <div><strong>Số lượng:</strong> ${escapeHtml(String(log.quantity || 0))}</div>
            <div><strong>Ghi chú:</strong> ${escapeHtml(log.note || "Không có")}</div>
        `;

        container.appendChild(div);
    });
}

/* ------------------ INIT & Logout ------------------ */
document.addEventListener("DOMContentLoaded", () => {
    const logoutIcon = document.getElementById("logoutIcon");
    if (logoutIcon) {
        logoutIcon.onclick = () => {
            localStorage.removeItem("userId");
            alert("👋 Bạn đã đăng xuất!");
            window.location.href = "/login";
        };
    }

    // default tab
    loadOrders();

    // wire search button already in HTML with onclick; also ensure Enter key works on input
    const searchInput = document.getElementById("searchKeyword");
    if (searchInput) {
        searchInput.addEventListener("keydown", (e) => {
            if (e.key === "Enter") {
                manageSearchBooks(1);
            }
        });
    }

    // wire modal close buttons (data-close attributes)
    document.querySelectorAll('[data-close]').forEach(el => {
        const id = el.getAttribute('data-close');
        el.addEventListener('click', () => closeModal(id));
    });

    // for any dynamically created close (like shipper modal generated), delegate click on body
    document.body.addEventListener('click', (e) => {
        const rem = e.target.getAttribute && e.target.getAttribute('data-remove');
        if (rem) {
            const el = document.getElementById(rem);
            if (el) el.remove();
        }
    });
});
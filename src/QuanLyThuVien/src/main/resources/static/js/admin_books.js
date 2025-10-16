// ==================== ⚙️ CẤU HÌNH ====================
const API_BASE = "/book";
const UPLOAD_API = "/api/upload/image";

let currentPage = 1;
let totalPages = 1;
const pageSize = 5;
const bookCache = {}; // 🆕 cache nhẹ để giảm gọi API

// ==================== ⚡ HIỂN THỊ LOADING ====================
function showLoading() { // 🆕
    const tbody = document.querySelector("#bookTable tbody");
    tbody.innerHTML = `
        <tr><td colspan="8" style="text-align:center; padding: 20px;">
            <div class="loader"></div> Đang tải dữ liệu...
        </td></tr>
    `;
}

// ==================== ⚡ HIỂN THỊ SÁCH ====================
function renderBooks(data) { // 🆕 gom logic hiển thị chung
    const tbody = document.querySelector("#bookTable tbody");
    let html = "";

    data.books.forEach(book => {
        html += `
            <tr>
                <td><img src="${book.imageUrl}" alt="Book Image"></td>
                <td>${book.bookName}</td>
                <td>${book.authorship}</td>
                <td>${book.bookGerne}</td>
                <td>${book.bookPublisher}</td>
                <td>${book.quantity}</td>
                <td>${book.price} $</td>
                <td>
                    <button class="btn-update" onclick="updateBook('${book.id}')">Cập Nhật</button>
                    <button class="btn-delete" onclick="deleteBook('${book.id}')">Xóa</button>
                </td>
            </tr>
        `;
    });

    tbody.innerHTML = html;
}

// ==================== ⚡ LOAD DANH SÁCH SÁCH ====================
async function loadBooks(page = 1) {
    // 🆕 sử dụng cache để giảm gọi API
    if (bookCache[page]) {
        renderBooks(bookCache[page]);
        currentPage = bookCache[page].currentPage;
        totalPages = bookCache[page].totalPages;
        renderPagination();
        return;
    }

    showLoading(); // 🆕 loading khi chờ dữ liệu

    try {
        const response = await fetch(`${API_BASE}/get-all?page=${page}&size=${pageSize}`);
        const result = await response.json();

        if (result.code === 1000) {
            bookCache[page] = result.data; // 🆕 lưu cache
            renderBooks(result.data);

            currentPage = result.data.currentPage;
            totalPages = result.data.totalPages;
            renderPagination();

            scrollToTop(); // 🆕 cuộn lên đầu bảng
        }
    } catch (error) {
        console.error("Error loading books:", error);
    }
}

// ==================== ⚡ PHÂN TRANG ====================
function renderPagination() {
    const paginationDiv = document.getElementById("pagination");
    paginationDiv.innerHTML = "";

    const prevBtn = document.createElement("button");
    prevBtn.innerText = "«";
    prevBtn.disabled = currentPage === 1;
    prevBtn.onclick = () => loadBooks(currentPage - 1);
    paginationDiv.appendChild(prevBtn);

    for (let i = 1; i <= totalPages; i++) {
        const btn = document.createElement("button");
        btn.innerText = i;
        btn.classList.toggle("active", i === currentPage);
        btn.onclick = () => loadBooks(i);
        paginationDiv.appendChild(btn);
    }

    const nextBtn = document.createElement("button");
    nextBtn.innerText = "»";
    nextBtn.disabled = currentPage === totalPages;
    nextBtn.onclick = () => loadBooks(currentPage + 1);
    paginationDiv.appendChild(nextBtn);
}

// ==================== ⚡ XÓA SÁCH ====================
async function deleteBook(id) {
    if (!confirm("Bạn có chắc chắn muốn xóa sách này?")) return;

    try {
        const response = await fetch(`${API_BASE}/delete/${id}`, { method: "DELETE" });
        const result = await response.json();

        if (result.code === 1000) {
            alert("Xóa thành công!");
            loadBooks(currentPage); // ⚡ giữ nguyên trang hiện tại
        } else {
            alert("Xóa thất bại!");
        }
    } catch (error) {
        console.error("Error deleting book:", error);
    }
}

// ==================== ⚡ CẬP NHẬT SÁCH ====================
let currentUpdateId = null;

function updateBook(id) {
    const row = document.querySelector(`button[onclick="updateBook('${id}')"]`).closest("tr");
    const cells = row.querySelectorAll("td");

    document.getElementById("updateBookId").value = id;
    document.getElementById("updateBookName").value = cells[1].innerText;
    document.getElementById("updateAuthorship").value = cells[2].innerText;
    document.getElementById("updateBookGerne").value = cells[3].innerText;
    document.getElementById("updateBookPublisher").value = cells[4].innerText;
    document.getElementById("updateQuantity").value = cells[5].innerText;
    document.getElementById("updatePrice").value = parseFloat(cells[6].innerText);

    const imgSrc = row.querySelector("img").src;
    document.getElementById("updatePreviewImage").src = imgSrc;

    currentUpdateId = id;
    document.getElementById("updateModal").classList.add("show"); // 🆕 hiệu ứng mượt
}

function closeUpdateModal() {
    const modal = document.getElementById("updateModal");
    modal.classList.remove("show"); // 🆕
    setTimeout(() => modal.style.display = "none", 200); // 🆕 animation
    currentUpdateId = null;
}

document.getElementById("updateBookForm").addEventListener("submit", async (e) => {
    e.preventDefault();

    const id = document.getElementById("updateBookId").value;
    let imageUrl = document.getElementById("updatePreviewImage").src;

    const fileInput = document.getElementById("updateBookImage");
    if (fileInput.files.length > 0) {
        const formData = new FormData();
        formData.append("file", fileInput.files[0]);
        const uploadRes = await fetch(UPLOAD_API, { method: "POST", body: formData });
        imageUrl = await uploadRes.text();
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
        const response = await fetch(`${API_BASE}/update/${id}`, {
            method: "PUT",
            headers: { "Content-Type": "application/json" },
            body: JSON.stringify(updateData)
        });
        const result = await response.json();

        if (result.code === 1000) {
            alert("Cập nhật thành công!");
            closeUpdateModal();
            loadBooks(currentPage); // ⚡ giữ nguyên trang
        } else {
            alert("Cập nhật thất bại!");
        }
    } catch (error) {
        console.error("Error updating book:", error);
    }
});

// ==================== ⚡ THÊM SÁCH ====================
function openAddModal() {
    const modal = document.getElementById("addModal");
    modal.classList.add("show"); // 🆕 animation mượt
}

function closeAddModal() {
    const modal = document.getElementById("addModal");
    modal.classList.remove("show");
    setTimeout(() => modal.style.display = "none", 200);
}

document.getElementById("addBookForm").addEventListener("submit", async (e) => {
    e.preventDefault();

    const fileInput = document.getElementById("bookImage");
    const formData = new FormData();
    formData.append("file", fileInput.files[0]);

    try {
        const uploadRes = await fetch(UPLOAD_API, { method: "POST", body: formData });
        const imageUrl = await uploadRes.text();

        const bookData = {
            bookName: document.getElementById("bookName").value,
            authorship: document.getElementById("authorship").value,
            bookGerne: document.getElementById("bookGerne").value,
            bookPublisher: document.getElementById("bookPublisher").value,
            quantity: parseInt(document.getElementById("quantity").value),
            price: parseFloat(document.getElementById("price").value),
            imageUrl: imageUrl
        };

        const res = await fetch(`${API_BASE}/add`, {
            method: "POST",
            headers: { "Content-Type": "application/json" },
            body: JSON.stringify(bookData)
        });
        const result = await res.json();

        if (result.code === 1000) {
            alert("Thêm sách thành công!");
            closeAddModal();
            loadBooks();
        } else {
            alert("Thêm sách thất bại!");
        }
    } catch (error) {
        console.error("Error adding book:", error);
    }
});

// ==================== ⚡ TÌM KIẾM ====================
async function searchBooks(page = 1) {
    const keyword = document.getElementById("searchKeyword").value.trim();
    if (!keyword) {
        loadBooks(page);
        return;
    }

    showLoading(); // 🆕 hiển thị loader

    try {
        const response = await fetch(`${API_BASE}/search?page=${page}&size=${pageSize}&keyword=${encodeURIComponent(keyword)}`);
        const result = await response.json();

        if (result.code === 1000) {
            renderBooks(result.data);
            currentPage = result.data.currentPage;
            totalPages = result.data.totalPages;
            renderPaginationSearch(keyword);
            scrollToTop(); // 🆕 mượt khi chuyển trang
        }
    } catch (error) {
        console.error("Error searching books:", error);
    }
}

// ==================== ⚡ PHÂN TRANG TÌM KIẾM ====================
function renderPaginationSearch(keyword) {
    const paginationDiv = document.getElementById("pagination");
    paginationDiv.innerHTML = "";

    const prevBtn = document.createElement("button");
    prevBtn.innerText = "«";
    prevBtn.disabled = currentPage === 1;
    prevBtn.onclick = () => searchBooks(currentPage - 1);
    paginationDiv.appendChild(prevBtn);

    for (let i = 1; i <= totalPages; i++) {
        const btn = document.createElement("button");
        btn.innerText = i;
        btn.classList.toggle("active", i === currentPage);
        btn.onclick = () => searchBooks(i);
        paginationDiv.appendChild(btn);
    }

    const nextBtn = document.createElement("button");
    nextBtn.innerText = "»";
    nextBtn.disabled = currentPage === totalPages;
    nextBtn.onclick = () => searchBooks(currentPage + 1);
    paginationDiv.appendChild(nextBtn);
}

// ==================== ⚡ TỐI ƯU UX ====================
function scrollToTop() { // 🆕
    document.querySelector("#bookTable").scrollIntoView({ behavior: "smooth" });
}

// 🆕 debounce tìm kiếm để không gọi API liên tục
let searchTimeout = null;
document.getElementById("searchKeyword").addEventListener("input", () => {
    clearTimeout(searchTimeout);
    searchTimeout = setTimeout(() => searchBooks(), 400);
});

// ==================== ⚡ KHỞI ĐỘNG ====================
loadBooks();

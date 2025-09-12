const API_BASE = "/book";
const UPLOAD_API = "/api/upload/image";

let currentPage = 1;
let totalPages = 1;
const pageSize = 5; // số sách mỗi trang

// Load danh sách sách có phân trang
async function loadBooks(page = 1) {
    try {
        const response = await fetch(`${API_BASE}/get-all?page=${page}&size=${pageSize}`);
        const result = await response.json();

        if (result.code === 1000) {
            const tbody = document.querySelector("#bookTable tbody");
            tbody.innerHTML = "";

            result.data.books.forEach(book => {
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
                            <button class="btn-update" onclick="updateBook('${book.id}')">Cập Nhật</button>
                            <button class="btn-delete" onclick="deleteBook('${book.id}')">Xóa</button>
                        </td>
                    </tr>
                `;
                tbody.innerHTML += row;
            });

            // cập nhật phân trang
            currentPage = result.data.currentPage;
            totalPages = result.data.totalPages;
            renderPagination();
        }
    } catch (error) {
        console.error("Error loading books:", error);
    }
}

// Hiển thị nút phân trang
function renderPagination() {
    const paginationDiv = document.getElementById("pagination");
    paginationDiv.innerHTML = "";

    // Nút Previous
    const prevBtn = document.createElement("button");
    prevBtn.innerText = "«";
    prevBtn.disabled = currentPage === 1;
    prevBtn.onclick = () => loadBooks(currentPage - 1);
    paginationDiv.appendChild(prevBtn);

    // Nút số trang
    for (let i = 1; i <= totalPages; i++) {
        const btn = document.createElement("button");
        btn.innerText = i;
        btn.classList.toggle("active", i === currentPage);
        btn.onclick = () => loadBooks(i);
        paginationDiv.appendChild(btn);
    }

    // Nút Next
    const nextBtn = document.createElement("button");
    nextBtn.innerText = "»";
    nextBtn.disabled = currentPage === totalPages;
    nextBtn.onclick = () => loadBooks(currentPage + 1);
    paginationDiv.appendChild(nextBtn);
}
// Load khi mở trang
loadBooks();

// Xóa sách
async function deleteBook(id) {
    if (!confirm("Bạn có chắc chắn muốn xóa sách này?")) return;

    try {
        const response = await fetch(`${API_BASE}/delete/${id}`, { method: "DELETE" });
        const result = await response.json();

        if (result.code === 1000) {
            alert("Xóa thành công!");
            loadBooks();
        } else {
            alert("Xóa thất bại!");
        }
    } catch (error) {
        console.error("Error deleting book:", error);
    }
}

// Cập nhật sách
let currentUpdateId = null;

// Mở modal cập nhật và điền sẵn dữ liệu
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
    document.getElementById("updateModal").style.display = "block";
}

// Đóng modal cập nhật
function closeUpdateModal() {
    document.getElementById("updateModal").style.display = "none";
    currentUpdateId = null;
}

// Lắng nghe sự kiện submit form cập nhật
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
            loadBooks();
        } else {
            alert("Cập nhật thất bại!");
        }
    } catch (error) {
        console.error("Error updating book:", error);
    }
});
// ----------------- Thêm sách -----------------
function openAddModal() {
    document.getElementById("addModal").style.display = "block";
}

function closeAddModal() {
    document.getElementById("addModal").style.display = "none";
}

document.getElementById("addBookForm").addEventListener("submit", async (e) => {
    e.preventDefault();

    const fileInput = document.getElementById("bookImage");
    const formData = new FormData();
    formData.append("file", fileInput.files[0]);

    try {
        // Upload ảnh trước
        const uploadRes = await fetch(UPLOAD_API, {
            method: "POST",
            body: formData
        });
        const imageUrl = await uploadRes.text(); // server trả về url ảnh

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

// Load khi mở trang
loadBooks();

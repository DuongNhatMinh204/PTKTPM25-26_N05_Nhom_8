const apiUrl = "/book/get-all?page=1&size=10";

document.addEventListener("DOMContentLoaded", () => {
    loadBooks();
});

function loadBooks() {
    fetch(apiUrl)
        .then(res => res.json())
        .then(data => {
            const books = data.data.books;
            const container = document.getElementById("bookList");
            container.innerHTML = "";

            books.forEach(book => {
                const card = document.createElement("div");
                card.className = "book-card";
                card.innerHTML = `
                    <img src="${book.imageUrl}" alt="${book.bookName}">
                    <h3>${book.bookName}</h3>
                    <p>${book.price} ₫</p>
                `;
                card.onclick = () => showDetail(book);
                container.appendChild(card);
            });
        });
}

function showDetail(book) {
    const modal = document.getElementById("detailModal");
    const detail = document.getElementById("bookDetail");
    detail.innerHTML = `
        <h2>${book.bookName}</h2>
        <img src="${book.imageUrl}" style="width:200px"><br>
        <p><b>Tác giả:</b> ${book.authorship}</p>
        <p><b>Thể loại:</b> ${book.bookGerne}</p>
        <p><b>NXB:</b> ${book.bookPublisher}</p>
        <p><b>Số lượng:</b> ${book.quantity}</p>
        <p><b>Giá:</b> ${book.price} ₫</p>
    `;
    modal.style.display = "block";
}

function closeDetailModal() {
    document.getElementById("detailModal").style.display = "none";
}

// Tìm kiếm sách (client-side filter)
function searchBooks() {
    const keyword = document.getElementById("searchInput").value.toLowerCase();
    const cards = document.querySelectorAll(".book-card");
    cards.forEach(card => {
        const title = card.querySelector("h3").innerText.toLowerCase();
        card.style.display = title.includes(keyword) ? "block" : "none";
    });
}

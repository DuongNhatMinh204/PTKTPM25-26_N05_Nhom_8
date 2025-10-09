// const apiUrl = "/book/get-all?page=1&size=10";
//
// document.addEventListener("DOMContentLoaded", () => {
//     loadBooks();
//
//     // Gắn sự kiện cho icon giỏ hàng trên header
//     const cartIcon = document.querySelector(".icons .icon:first-child");
//     if (cartIcon) {
//         cartIcon.onclick = () => {
//             window.location.href = "/cart";
//         };
//     }
// });
//
// function loadBooks() {
//     fetch(apiUrl)
//         .then(res => res.json())
//         .then(data => {
//             const books = data.data.books;
//             const container = document.getElementById("bookList");
//             container.innerHTML = "";
//
//             books.forEach(book => {
//                 const card = document.createElement("div");
//                 card.className = "book-card";
//                 card.innerHTML = `
//                     <img src="${book.imageUrl}" alt="${book.bookName}">
//                     <h3>${book.bookName}</h3>
//                     <p>${book.price} ₫</p>
//                 `;
//                 card.onclick = () => showDetail(book);
//                 container.appendChild(card);
//             });
//         });
// }
//
// function showDetail(book) {
//     const modal = document.getElementById("detailModal");
//     const detail = document.getElementById("bookDetail");
//
//     detail.innerHTML = `
//         <div class="book-left">
//             <img src="${book.imageUrl}" alt="${book.bookName}">
//             <h2>${book.bookName}</h2>
//         </div>
//         <div class="book-right">
//             <p><strong>Tác giả:</strong> ${book.authorship}</p>
//             <p><strong>Thể loại:</strong> ${book.bookGerne}</p>
//             <p><strong>NXB:</strong> ${book.bookPublisher}</p>
//             <p><strong>Kho:</strong> ${book.quantity}</p>
//             <p><strong>Giá:</strong> <span class="price">${book.price} ₫</span></p>
//             <div class="action">
//                 <label for="quantity">Số lượng:</label>
//                 <input type="number" id="quantity" value="1" min="1" max="${book.quantity}">
//                 <button id="addCartBtn" class="add-cart">🛒 Thêm vào giỏ hàng</button>
//             </div>
//         </div>
//     `;
//
//     // Gắn sự kiện vào nút sau khi render
//     document.getElementById("addCartBtn").onclick = () => {
//         const quantity = parseInt(document.getElementById("quantity").value);
//         const requestBody = {
//             bookId: book.id,
//             userId: localStorage.getItem("userId"), //
//             quantity: quantity
//         };
//
//         fetch("/order-temp/create", {
//             method: "POST",
//             headers: {
//                 "Content-Type": "application/json"
//             },
//             body: JSON.stringify(requestBody)
//         })
//             .then(res => res.json())
//             .then(data => {
//                 if (data.code === 1000) {
//                     alert("✅ Đã thêm vào giỏ hàng!");
//                     closeDetailModal();
//                 } else {
//                     alert("❌ Thêm thất bại!");
//                 }
//             })
//             .catch(err => console.error("Error:", err));
//     };
//
//     modal.style.display = "flex";
// }
//
// function closeDetailModal() {
//     document.getElementById("detailModal").style.display = "none";
// }
//
// function searchBooks() {
//     const keyword = document.getElementById("searchInput").value.toLowerCase();
//     const cards = document.querySelectorAll(".book-card");
//     cards.forEach(card => {
//         const title = card.querySelector("h3").innerText.toLowerCase();
//         card.style.display = title.includes(keyword) ? "block" : "none";
//     });
// }
const apiUrl = "/book/get-all?page=1&size=10";

document.addEventListener("DOMContentLoaded", () => {
    loadBooks();

    // Icon giỏ hàng
    document.getElementById("cartIcon").onclick = () => {
        window.location.href = "/cart";
    };

    // Icon đơn hàng 📦
    document.getElementById("orderIcon").onclick = () => {
        window.location.href = "/orders";
    };

    // Icon người dùng 👤 → Đăng xuất
    document.getElementById("userIcon").onclick = () => {
        localStorage.removeItem("userId"); // Xoá userId khỏi localStorage
        alert("👋 Bạn đã đăng xuất!");
        window.location.href = "/login"; // Chuyển hướng về trang đăng nhập
    };
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
        <div class="book-left">
            <img src="${book.imageUrl}" alt="${book.bookName}">
            <h2>${book.bookName}</h2>
        </div>
        <div class="book-right">
            <p><strong>Tác giả:</strong> ${book.authorship}</p>
            <p><strong>Thể loại:</strong> ${book.bookGerne}</p>
            <p><strong>NXB:</strong> ${book.bookPublisher}</p>
            <p><strong>Kho:</strong> ${book.quantity}</p>
            <p><strong>Giá:</strong> <span class="price">${book.price} ₫</span></p>
            <div class="action">
                <label for="quantity">Số lượng:</label>
                <input type="number" id="quantity" value="1" min="1" max="${book.quantity}">
                <button id="addCartBtn" class="add-cart">🛒 Thêm vào giỏ hàng</button>
            </div>
        </div>
    `;

    document.getElementById("addCartBtn").onclick = () => {
        const quantity = parseInt(document.getElementById("quantity").value);
        const requestBody = {
            bookId: book.id,
            userId: localStorage.getItem("userId"),
            quantity: quantity
        };

        fetch("/order-temp/create", {
            method: "POST",
            headers: { "Content-Type": "application/json" },
            body: JSON.stringify(requestBody)
        })
            .then(res => res.json())
            .then(data => {
                if (data.code === 1000) {
                    alert("✅ Đã thêm vào giỏ hàng!");
                    closeDetailModal();
                } else {
                    alert("❌ Thêm thất bại!");
                }
            })
            .catch(err => console.error("Error:", err));
    };

    modal.style.display = "flex";
}

function closeDetailModal() {
    document.getElementById("detailModal").style.display = "none";
}

function searchBooks() {
    const keyword = document.getElementById("searchInput").value.toLowerCase();
    const cards = document.querySelectorAll(".book-card");
    cards.forEach(card => {
        const title = card.querySelector("h3").innerText.toLowerCase();
        card.style.display = title.includes(keyword) ? "block" : "none";
    });
}

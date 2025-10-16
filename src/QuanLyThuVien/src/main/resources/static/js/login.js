// 🌀 Hiệu ứng mượt khi trang load
window.addEventListener("DOMContentLoaded", () => {
    const container = document.querySelector(".login-container");
    container.style.opacity = 0;
    container.style.transform = "translateY(40px)";
    container.style.transition = "all 0.8s ease-out";

    // delay nhẹ để tạo cảm giác "fade in"
    requestAnimationFrame(() => {
        container.style.opacity = 1;
        container.style.transform = "translateY(0)";
    });
});

// 🟢 Thêm spinner khi đăng nhập
function showLoading() {
    const btn = document.getElementById("loginBtn");
    btn.disabled = true;
    btn.innerHTML = `<span class="loader"></span> Đang đăng nhập...`; // 🧩 thêm hiệu ứng
}

function hideLoading() {
    const btn = document.getElementById("loginBtn");
    btn.disabled = false;
    btn.innerHTML = "Đăng nhập";
}

// 🧩 Thêm CSS loader (bạn chèn vào file CSS)
const style = document.createElement("style");
style.innerHTML = `
.loader {
  border: 3px solid #f3f3f3;
  border-top: 3px solid #3498db;
  border-radius: 50%;
  width: 14px;
  height: 14px;
  animation: spin 1s linear infinite;
  display: inline-block;
  margin-right: 6px;
  vertical-align: middle;
}
@keyframes spin { 0% { transform: rotate(0deg); } 100% { transform: rotate(360deg); } }
`;
document.head.appendChild(style);

// 🧠 Xử lý đăng nhập
document.getElementById("loginForm").addEventListener("submit", async function (event) {
    event.preventDefault();

    const phone = document.getElementById("phone").value.trim();
    const password = document.getElementById("password").value.trim();

    if (!phone || !password) {
        alert("Vui lòng nhập đầy đủ thông tin!");
        return;
    }

    showLoading(); // 🧩 hiển thị spinner

    const loginData = { phone, password };

    try {
        const response = await fetch("/user/signin", {
            method: "POST",
            headers: { "Content-Type": "application/json" },
            body: JSON.stringify(loginData)
        });

        const result = await response.json();

        if (result.code === 1002) {
            // 🧩 Lưu thông tin vào localStorage
            localStorage.setItem("userId", result.data.id);
            localStorage.setItem("userPhone", result.data.phone);

            // 🧩 Hiệu ứng fade out trước khi chuyển trang
            const container = document.querySelector(".login-container");
            container.style.opacity = 0;
            container.style.transform = "translateY(-20px)";

            setTimeout(() => {
                switch (result.data.role) {
                    case "ROLE_USER":
                        window.location.href = "/user";
                        break;
                    case "ROLE_ADMIN":
                        window.location.href = "/admin";
                        break;
                    case "ROLE_STORE_KEEPER":
                        window.location.href = "/storekeeper";
                        break;
                    case "ROLE_SHIPPER":
                        window.location.href = "/shipper";
                        break;
                    default:
                        alert("Không xác định vai trò người dùng!");
                }
            }, 400);
        } else {
            // 🧩 Cải thiện hiển thị lỗi
            alert(`❌ Đăng nhập thất bại: ${result.message || "Sai thông tin đăng nhập!"}`);
        }
    } catch (error) {
        console.error("Error:", error);
        alert("🚫 Không thể kết nối tới server. Vui lòng thử lại sau.");
    } finally {
        hideLoading(); // 🧩 ẩn spinner
    }
});

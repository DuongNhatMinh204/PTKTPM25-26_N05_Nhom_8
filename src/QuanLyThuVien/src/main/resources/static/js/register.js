// 🌀 Hiệu ứng fade + slide lên khi tải trang
window.addEventListener("DOMContentLoaded", () => {
    const container = document.querySelector(".register-container");
    container.style.opacity = 0;
    container.style.transform = "translateY(50px)";
    container.style.transition = "all 0.8s ease-out"; // 🧩 chuyển lên sớm để mượt hơn

    // 🧩 dùng requestAnimationFrame để đảm bảo trình duyệt render trước rồi mới chạy animation
    requestAnimationFrame(() => {
        container.style.opacity = 1;
        container.style.transform = "translateY(0)";
    });
});

// 🟢 Hiển thị và ẩn loading khi đăng ký
function showLoading() {
    const btn = document.getElementById("registerBtn");
    btn.disabled = true;
    btn.innerHTML = `<span class="loader"></span> Đang xử lý...`;
}

function hideLoading() {
    const btn = document.getElementById("registerBtn");
    btn.disabled = false;
    btn.innerHTML = "Đăng ký";
}

// 🧩 Thêm CSS loader (dùng chung với file login nếu muốn)
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

// 🧠 Xử lý đăng ký
document.getElementById("registerForm").addEventListener("submit", async function (event) {
    event.preventDefault();

    // 🧩 Lấy dữ liệu từ form & trim() để tránh lỗi khoảng trắng
    const fullName = document.getElementById("fullName").value.trim();
    const email = document.getElementById("email").value.trim();
    const phone = document.getElementById("phone").value.trim();
    const password = document.getElementById("password").value.trim();
    const confirmPassword = document.getElementById("confirmpassword").value.trim();
    const birthday = document.getElementById("birthday").value;
    const gender = document.getElementById("gender").value;

    // 🧩 Kiểm tra dữ liệu cơ bản trước khi gửi
    if (!fullName || !email || !phone || !password || !confirmPassword || !birthday) {
        alert("Vui lòng nhập đầy đủ thông tin!");
        return;
    }

    if (password !== confirmPassword) {
        alert("❌ Mật khẩu xác nhận không khớp!");
        return;
    }

    // 🧩 Kiểm tra email định dạng cơ bản
    const emailRegex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
    if (!emailRegex.test(email)) {
        alert("Email không hợp lệ!");
        return;
    }

    showLoading(); // 🧩 bật hiệu ứng loading

    // Format ngày sinh (yyyy-mm-dd -> dd-MM-yyyy)
    const formattedBirthday = birthday.split("-").reverse().join("-");

    const userData = {
        fullName,
        email,
        phone,
        password,
        confirmPassword,
        birthday: formattedBirthday,
        gender
    };

    try {
        const response = await fetch("/user/signup", {
            method: "POST",
            headers: { "Content-Type": "application/json" },
            body: JSON.stringify(userData)
        });

        const result = await response.json();

        if (result.code === 1001) {
            // 🧩 Hiệu ứng fade out khi đăng ký thành công
            const container = document.querySelector(".register-container");
            container.style.opacity = 0;
            container.style.transform = "translateY(-20px)";

            setTimeout(() => {
                alert(`🎉 ${result.message}\nChào mừng ${result.data.fullName}!`);
                window.location.href = "/login";
            }, 400);
        } else {
            alert(`❌ Đăng ký thất bại: ${result.message}`);
        }
    } catch (error) {
        console.error("Lỗi khi gọi API:", error);
        alert("🚫 Không thể kết nối tới server. Vui lòng thử lại sau.");
    } finally {
        hideLoading(); // 🧩 tắt loading sau khi xử lý xong
    }
});

// Hiệu ứng fade + slide lên khi tải trang
window.addEventListener("DOMContentLoaded", () => {
    const container = document.querySelector(".register-container");
    container.style.opacity = 0;
    container.style.transform = "translateY(50px)";

    setTimeout(() => {
        container.style.transition = "all 0.8s ease-out";
        container.style.opacity = 1;
        container.style.transform = "translateY(0)";
    }, 100);
});

// Xử lý đăng ký
document.getElementById("registerForm").addEventListener("submit", async function(event) {
    event.preventDefault();

    // Lấy dữ liệu từ form
    const fullName = document.getElementById("fullName").value.trim();
    const email = document.getElementById("email").value.trim();
    const phone = document.getElementById("phone").value.trim();
    const password = document.getElementById("password").value.trim();
    const confirmPassword = document.getElementById("confirmpassword").value.trim();
    const birthday = document.getElementById("birthday").value;
    const gender = document.getElementById("gender").value;

    // Format ngày sinh (yyyy-mm-dd -> dd-MM-yyyy)
    const formattedBirthday = birthday.split("-").reverse().join("-");

    // Tạo object để gửi đi
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
            headers: {
                "Content-Type": "application/json"
            },
            body: JSON.stringify(userData)
        });

        const result = await response.json();

        if (result.code === 1001) {
            alert(`🎉 ${result.message}\nChào mừng ${result.data.fullName}!`);
            window.location.href = "/login"; // chuyển sang trang đăng nhập
        } else {
            alert(`❌ Đăng ký thất bại: ${result.message}`);
        }
    } catch (error) {
        console.error("Lỗi khi gọi API:", error);
        alert("Không thể kết nối tới server. Vui lòng thử lại sau.");
    }
});

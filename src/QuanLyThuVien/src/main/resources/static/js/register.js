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
document.getElementById("registerForm").addEventListener("submit", function(event) {
    event.preventDefault();

    const userData = {
        fullName: document.getElementById("fullName").value,
        email: document.getElementById("email").value,
        phone: document.getElementById("phone").value,
        password: document.getElementById("password").value,
        birthday: document.getElementById("birthday").value,
        gender: document.getElementById("gender").value,
        role: "user",
        status: 1
    };

    fetch("http://localhost:8080/api/users/register", {
        method: "POST",
        headers: {
            "Content-Type": "application/json"
        },
        body: JSON.stringify(userData)
    })
        .then(res => {
            if (res.ok) {
                alert("Đăng ký thành công!");
                window.location.href = "login.html";
            } else {
                alert("Đăng ký thất bại!");
            }
        })
        .catch(error => {
            console.error("Lỗi:", error);
            alert("Đã xảy ra lỗi.");
        });
});

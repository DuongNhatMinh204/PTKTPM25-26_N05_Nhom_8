// Hiệu ứng mượt khi trang load
window.addEventListener("DOMContentLoaded", () => {
    const container = document.querySelector(".login-container");
    container.style.opacity = 0;
    container.style.transform = "translateY(50px)";

    setTimeout(() => {
        container.style.transition = "all 0.8s ease-out";
        container.style.opacity = 1;
        container.style.transform = "translateY(0)";
    }, 100);
});

// Xử lý đăng nhập
document.getElementById("loginForm").addEventListener("submit", function(event) {
    event.preventDefault();

    const phone = document.getElementById("phone").value;
    const password = document.getElementById("password").value;

    const loginData = {
        phone: phone,
        password: password
    };

    fetch("http://localhost:8080/api/users/login", {
        method: "POST",
        headers: {
            "Content-Type": "application/json"
        },
        body: JSON.stringify(loginData)
    })
        .then(res => {
            if (res.ok) {
                alert("Đăng nhập thành công!");
                window.location.href = "index.html";
            } else {
                alert("Sai số điện thoại hoặc mật khẩu!");
            }
        })
        .catch(error => {
            console.error("Lỗi:", error);
            alert("Đã xảy ra lỗi.");
        });
});

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
document.getElementById("loginForm").addEventListener("submit", async function(event) {
    event.preventDefault();

    const phone = document.getElementById("phone").value;
    const password = document.getElementById("password").value;

    const loginData = {
        phone: phone,
        password: password
    };

    try{
        const response = await fetch("/user/signin", {
            method: "POST",
            headers: {
                "Content-Type": "application/json"
            },
            body: JSON.stringify(loginData)
        })

        const result = await response.json();

        if(result.code === 1002){
            // Lưu id và phone vào localStorage
            localStorage.setItem("userId", result.data.id);
            localStorage.setItem("userPhone", result.data.phone);
            if(result.data.role === "ROLE_USER"){
                window.location.href = "/user"
            }
            if(result.data.role === "ROLE_ADMIN"){
                window.location.href = "/admin"
            }
            if(result.data.role === "ROLE_STORE_KEEPER"){
                window.location.href = "/storekeeper"
            }
            if(result.data.role === "ROLE_SHIPPER"){
                window.location.href = "/shipper"
            }
        }else {
            alert(`❌ Đăng ký thất bại: ${result.message}`)
        }
    }catch (error){
        console.error("Error : ", error);
        alert("Không thể kết nối tới server. Vui lòng thử lại sau.")

    }

});
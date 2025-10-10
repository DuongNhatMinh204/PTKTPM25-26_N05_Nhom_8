// shipper.js (bản rút gọn & đã loại bỏ chức năng chỉnh sửa email/status)
const API_URL = "http://localhost:8080/api/shipper/get-book-order";
const BASE_API = "http://localhost:8080/api/shipper";

// AUTH KEYS nếu cần xóa lúc logout
const AUTH_KEYS = ["userId", "accessToken", "refreshToken", "username", "phone"];

// Lấy userId từ localStorage (giả sử đã lưu khi đăng nhập)
const userId = localStorage.getItem("userId");

// Nếu chưa có shipperId thì yêu cầu đăng nhập lại (bảo vệ trang)
if (!userId) {
    alert("⚠️ Bạn chưa đăng nhập hoặc phiên đã hết. Vui lòng đăng nhập lại!");
    window.location.assign("/login");
}

/* Helpers */
async function safeJson(res) {
    try { return await res.json(); }
    catch { return null; }
}
function clearAuthStorage() {
    for (const k of AUTH_KEYS) localStorage.removeItem(k);
}
function clearAllCookies() {
    document.cookie.split(";").forEach(c => {
        document.cookie = c.replace(/^ +/, "").replace(/=.*/, "=;expires=" + new Date(0).toUTCString() + ";path=/");
    });
}

/* Render profile (không có email/status/edit) */
function renderProfile(shipper) {
    const name = shipper?.shipperName ?? localStorage.getItem("username") ?? "Chưa cập nhật";
    const phone = shipper?.shipperPhone ?? localStorage.getItem("phone") ?? "—";
    const avatarEl = document.getElementById("profile-avatar");
    const nameEl = document.getElementById("profile-name");
    const phoneEl = document.getElementById("profile-phone");
    const headerAvatar = document.getElementById("header-avatar");
    const headerUsername = document.getElementById("header-username");

    if (nameEl) nameEl.textContent = name;
    if (phoneEl) phoneEl.textContent = `SĐT: ${phone}`;

    // header
    if (headerUsername) headerUsername.textContent = name;
    if (headerAvatar) {
        if (shipper?.avatarUrl) {
            headerAvatar.innerHTML = `<img src="${shipper.avatarUrl}" alt="avatar" style="width:28px;height:28px;border-radius:50%;object-fit:cover">`;
        } else {
            headerAvatar.textContent = name.charAt(0).toUpperCase();
        }
    }

    // profile avatar
    if (avatarEl) {
        if (shipper?.avatarUrl) {
            avatarEl.innerHTML = `<img src="${shipper.avatarUrl}" alt="avatar">`;
        } else {
            avatarEl.textContent = name.charAt(0).toUpperCase();
        }
    }
}

/* Load orders */
async function loadShipperOrders() {
    try {
        const res = await fetch(`${API_URL}?userId=${encodeURIComponent(userId)}`, {
            method: "GET",
            headers: { "Accept": "application/json" }
        });

        if (!res.ok) {
            const container = document.getElementById("orders-container");
            if (container) container.innerHTML = "<em>Không thể tải đơn hàng (lỗi server).</em>";
            return;
        }

        const result = await safeJson(res);
        const items = result?.data ?? (Array.isArray(result) ? result : []);
        // RENDER profile nếu API trả về shipper
        const shipperInfo = items?.[0]?.shipper ?? result?.shipper ?? null;
        if (shipperInfo) renderProfile(shipperInfo);
        else {
            // fallback
            const fallbackName = localStorage.getItem("username");
            if (fallbackName) renderProfile({ shipperName: fallbackName, shipperPhone: localStorage.getItem("phone") ?? ""});
        }

        const container = document.getElementById("orders-container");
        if (!container) return;

        if (!items || items.length === 0) {
            container.innerHTML = "<em>Không có đơn hàng nào được giao.</em>";
            return;
        }

        container.innerHTML = "";
        items.forEach(order => {
            const bo = order.bookOrder;
            let actionsHtml = "";
            const status = (order.shippingStatus ?? "").toUpperCase();

            if (status === "PENDING" || status === "WAITING") {
                actionsHtml = `<button class="start-btn" onclick="startShipping('${order.id}')">🚚 Bắt đầu giao hàng</button>`;
            } else if (status === "SHIPPING" || status === "IN_TRANSIT") {
                actionsHtml = `
                    <button class="success-btn" onclick="markDelivered('${order.id}')">✅ Xác nhận thành công</button>
                    <button class="fail-btn" onclick="markFailed('${order.id}')">❌ Xác nhận thất bại</button>
                `;
            } else {
                actionsHtml = `<em>Trạng thái: ${order.shippingStatus ?? "—"}</em>`;
            }

            const orderHtml = `
                <div class="order">
                  <div class="order-header">
                    <strong>Mã Giao Hàng:</strong> ${order.id ?? "—"} <br>
                    <strong>Trạng Thái:</strong> ${order.shippingStatus ?? "—"} <br>
                    <strong>Ngày Giao:</strong> ${order.shippingDate ? new Date(order.shippingDate).toLocaleString("vi-VN") : "—"}
                  </div>
                  <div><strong>Địa Chỉ Giao:</strong> ${order.shippingAddress ?? "—"}</div>
                  <div><strong>Ghi Chú:</strong> ${order.note ?? "Không có"}</div>
                  ${bo ? `
                    <div class="book-order">
                      <strong>Đơn Hàng:</strong> ${bo.id ?? "—"}<br>
                      <strong>Địa Chỉ:</strong> ${bo.address ?? "—"}<br>
                      <strong>Thanh Toán:</strong> ${bo.paymentType ?? "—"}<br>
                      <strong>Tổng Tiền:</strong> ${typeof bo.totalPrice === "number" ? bo.totalPrice.toLocaleString() + " VND" : (bo.totalPrice ?? "—")}<br>
                      <strong>Trạng Thái Đơn:</strong> ${bo.orderStatus ?? "—"}
                    </div>
                  ` : "<em>Không có thông tin đơn hàng liên kết.</em>"}
                  <div class="actions">${actionsHtml}</div>
                </div>
            `;
            container.insertAdjacentHTML("beforeend", orderHtml);
        });

        // option: update some simple stats on profile (count)
        updateProfileStats(items);
    } catch (err) {
        console.error("Lỗi tải đơn hàng:", err);
        const container = document.getElementById("orders-container");
        if (container) container.innerHTML = "<em>Không thể tải danh sách đơn hàng do lỗi kết nối.</em>";
    }
}

/* Update simple counts in profile-right */
function updateProfileStats(items){
    try {
        const shipping = items.filter(i => (i.shippingStatus ?? "").toUpperCase() === "SHIPPING").length;
        const delivered = items.filter(i => (i.shippingStatus ?? "").toUpperCase() === "DELIVERED" || (i.shippingStatus ?? "").toUpperCase() === "SUCCESS").length;
        const s1 = document.getElementById("stat-shipping");
        const s2 = document.getElementById("stat-delivered");
        if (s1) s1.textContent = shipping;
        if (s2) s2.textContent = delivered;
    } catch(e){}
}

/* API actions */
async function startShipping(shippingId) {
    if (!confirm("🚚 Xác nhận bắt đầu giao hàng đơn này?")) return;
    try {
        const res = await fetch(`${BASE_API}/start-shipping?shippingId=${encodeURIComponent(shippingId)}`, { method: "PUT" });
        if (res.ok) { alert("✅ Đã chuyển đơn sang trạng thái 'Đang giao'!"); await loadShipperOrders(); }
        else alert("❌ Không thể cập nhật trạng thái (server).");
    } catch (e) { console.error(e); alert("⚠️ Lỗi kết nối server!"); }
}

async function markDelivered(shippingId) {
    if (!confirm("✅ Xác nhận đơn hàng đã giao thành công?")) return;
    try {
        const res = await fetch(`${BASE_API}/delivered?shippingId=${encodeURIComponent(shippingId)}`, { method: "PUT" });
        if (res.ok) { alert("🎉 Đơn hàng đã được đánh dấu là 'Thành công'!"); await loadShipperOrders(); }
        else alert("❌ Cập nhật thất bại (server).");
    } catch (e) { console.error(e); alert("⚠️ Lỗi kết nối server!"); }
}

async function markFailed(shippingId) {
    if (!confirm("❌ Xác nhận giao hàng thất bại?")) return;
    try {
        const res = await fetch(`${BASE_API}/failed?shippingId=${encodeURIComponent(shippingId)}`, { method: "PUT" });
        if (res.ok) { alert("🚫 Đơn hàng đã được đánh dấu là 'Thất bại'!"); await loadShipperOrders(); }
        else alert("❌ Cập nhật thất bại (server).");
    } catch (e) { console.error(e); alert("⚠️ Lỗi kết nối server!"); }
}

/* Logout */
async function logout() {
    if (!confirm("🔒 Bạn có chắc chắn muốn đăng xuất?")) return;
    clearAuthStorage(); clearAllCookies();
    try { await fetch(`${BASE_API}/logout`, { method: "POST", credentials: "include" }).catch(()=>{}); } catch {}
    window.location.assign("/login");
}

/* Init */
document.addEventListener("DOMContentLoaded", () => {
    const b = document.getElementById("btn-logout");
    if (b) b.addEventListener("click", logout);

    // Start load
    loadShipperOrders();
});

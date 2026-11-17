# Nail Room Admin System

Dự án quản trị hệ thống đặt lịch salon Nail Room - Mit’s House (chuẩn schema DBML và phong cách thiết kế của [book.nailroom.vn](https://book.nailroom.vn)).

## Cấu trúc thư mục

- `nail-backend/`: Backend **Java Spring Boot 3 + Spring Data JPA + MySQL / H2 + Bean Validation**.
- `nail-admin/`: Frontend **React 18 + Vite + TypeScript + Tailwind CSS** (Vibe chuẩn thương hiệu: màu `#834506`, font `Fraunces` + `Be Vietnam Pro`).
- `nail/`: Frontend khách hàng (Next.js clone từ `book.nailroom.vn`).

---

## 1. Khởi động Backend (`nail-backend`)

Port mặc định: **8282**

```bash
cd /Users/mac/Documents/SupperDev/nail-backend

# Chạy với profile H2 (file database tự động, đã seed toàn bộ 9 chi nhánh, 3 nhóm DV, 12 dịch vụ, 52 thợ, 364 lịch làm việc):
mvn spring-boot:run -Dspring-boot.run.profiles=h2

# Hoặc chạy với MySQL: cấu hình DB trong file .env hoặc application.properties rồi chạy:
mvn spring-boot:run
```

### Tài khoản Admin mặc định
- **Tài khoản**: `admin`
- **Mật khẩu**: `admin123`
- Cấu hình trong `.env` (`ADMIN_USERNAME`, `ADMIN_PASSWORD`, `ADMIN_TOKEN_SECRET`)

---

## 2. Khởi động Frontend Admin (`nail-admin`)

Port mặc định: **5174**

```bash
cd /Users/mac/Documents/SupperDev/nail-admin
npm install
npm run dev
```

Truy cập: [http://localhost:5174](http://localhost:5174)

---

## 3. Các module CRUD đã hoàn thiện

1. **Locations (`/locations`)**:
   - Quản lý 9 chi nhánh tại Hà Nội & TP Hồ Chí Minh.
   - Thêm / Sửa / Xóa / Tìm kiếm / Lọc theo khu vực, trạng thái hoạt động.
   - Kiểm tra ràng buộc: Không cho xóa nếu chi nhánh đang có thợ hoặc lịch hẹn.
2. **Service Categories (`/service-categories`)**:
   - Nhóm dịch vụ (Nail Services, Eyelashes, Wax...).
   - Đếm số lượng dịch vụ trực thuộc, sắp xếp `sort_order`.
3. **Services (`/services`)**:
   - Quản lý dịch vụ, giá tiền (VND), thời gian thực hiện (`duration_minutes`), mô tả.
   - Lọc theo nhóm dịch vụ, trạng thái mở bán.
4. **Technicians (`/technicians`)**:
   - Quản lý 52 kỹ thuật viên, gắn chi nhánh công tác, SĐT, avatar, trạng thái (`active`, `busy`, `off`).
5. **Technician Schedules (`/schedules`)**:
   - Quản lý ca làm việc theo ngày (`work_date`, `start_time`, `end_time`, `is_off`).
   - Lọc theo thợ, chi nhánh, khoảng ngày `fromDate` - `toDate`.
   - **Xếp lịch hàng loạt (Bulk Schedule)**: Cho phép chọn nhiều thợ, chọn khoảng ngày, chọn các thứ trong tuần (T2 - CN), chọn giờ ca và ghi đè tự động.
   - Kiểm tra chống trùng giờ / trùng ngày nghỉ thông minh.

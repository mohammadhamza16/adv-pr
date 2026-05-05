# شرح ملفات المشروع وفائدتها

هذا الملف يشرح كل ملف أو مجلد مهم في المشروع، وفكرة عمله، وكيفية استخدامه.

## الملفات الرئيسية في جذر المشروع

### `pom.xml`
- الملف الرئيسي لإدارة مشروع Maven.
- يحتوي على الاعتمادات (`dependencies`) مثل Servlet API، Jackson، JWT، Jedis، HikariCP.
- يستخدم لبناء المشروع وإنشاء `WAR`.
- كيفية استخدامه: شغّل `mvn clean package` لبناء المشروع.

### `README.md`
- ملف شرح المشروع الأساسي بالإنجليزي.
- يحتوي على خطوات إعداد عامة وشرح بسيط عن التطبيق.
- كيفية استخدامه: اقرَأه أولاً لمعرفة المتطلبات العامة والروابط الأساسية.

### `SETUP_AR.md`
- دليل إعداد وتشغيل كامل باللغة العربية.
- يشرح الخطوات التفصيلية للعمل من IntelliJ و phpMyAdmin و Postman.
- كيفية استخدامه: اقرَأه لتنفيذ المشروع خطوة بخطوة في بيئة عربية.

### `PROJECT_DOCS.md`
- ملف توثيق عام للبنية والنظام.
- يُستخدم إذا أردت فهم التصميم العام والطبقات المختلفة في المشروع.
- كيفية استخدامه: اقرَأه لتفهم بنية clean architecture في المشروع.

### `postman_collection.json`
- ملف Postman جاهز للاستيراد يحتوي على جميع حالات الاستخدام (use cases).
- يشمل تسجيل، منتجات، سلة، طلبات، دفعات، إعادة ضبط.
- كيفية استخدامه: استورد الملف في Postman لبدء الاختبار.

### `postman_environment.json`
- ملف بيئة Postman جاهز يحتوي على متغيرات مثل `baseUrl` و `authToken`.
- يسهل تشغيل الطلبات مع تحديث المتغيرات تلقائياً.
- كيفية استخدامه: استورد الملف في Postman واختر البيئة قبل تشغيل الطلبات.

### `.gitignore`
- يمنع رفع ملفات البناء وملفات IntelliJ وملفات النظام إلى Git.
- يحتوي مثلاً على `/target/`, `/.idea/`, `*.iml`.
- كيفية استخدامه: لا تحتاج لعمل شيء، Git سيتجاهل الملفات المدرجة.

## مجلد الموارد

### `src/main/resources/application.properties`
- ملف إعدادات التطبيق.
- يحتوي على إعدادات MySQL و Redis و JWT و الكاش.
- كيفية استخدامه: عدّل `db.url`, `db.user`, `db.password`, `redis.host`, `redis.port` حسب بيئتك.

### `src/main/resources/schema.sql`
- يحتوي على تعريف الجداول وقواعد البيانات.
- ينشئ الجداول: `users`, `categories`, `products`, `carts`, `cart_items`, `orders`, `order_items`, `payments`, `auth_tokens`.
- كيفية استخدامه: نفّذ ملف SQL في phpMyAdmin أو MySQL لإنشاء السكيما.

## ملفات الويب

### `src/main/webapp/WEB-INF/web.xml`
- ملف إعدادات Servlets والتوجيه لكل Endpoint.
- يعرّف الـ Servlets التالية:
  - `AuthServlet`
  - `ProductServlet`
  - `CartServlet`
  - `OrderServlet`
  - `PaymentServlet`
  - `CategoryServlet`
  - `ResetServlet`
- يربط `AuthFilter` بكل المسارات `/api/*` لحماية الـ API.
- كيفية استخدامه: لا تحتاج لتعديله إلا إذا أردت تغيير عنوان أحد الـ Endpoints أو إضافة جديدة.

## مجلد Java

### `src/main/java/com/example/ecommerce/config/`

- `AppConfigListener.java`
  - تهيئة موارد التطبيق عند بدء التشغيل.
  - ينشئ `DataSource` لـ MySQL و `JedisPool` لـ Redis.
  - يخزن الإعدادات في `ServletContext` حتى تستخدمها كل الـ Servlets.
  - كيفية استخدامه: يعمل أوتوماتيكياً عند تشغيل السيرفر.

- `ContextAttributes.java`
  - ثابتات (`constants`) لأسماء الخصائص المخزنة في `ServletContext`.
  - يستخدم لتجنب الأخطاء في اسماء الخصائص.

### `src/main/java/com/example/ecommerce/util/`

- `JwtUtil.java`
  - يولّد ويقرأ JWT.
  - يحتوي على مفاتيح التوقيع ومدة الصلاحية.
  - كيفية استخدامه: يستخدمه `AuthService` و `AuthFilter` لحماية الـ API.

- `JsonUtil.java`
  - يحول الطلبات إلى JSON والعكس.
  - يرسل الردود بصيغة JSON.
  - كيفية استخدامه: يستخدم في كل Servlet لقراءة وكتابة JSON.

- `PasswordUtil.java`
  - يقوم بتجزئة كلمة المرور باستخدام SHA-256.
  - يستخدم لتخزين كلمات المرور بأمان.

- `PaymentRateLimiter.java`
  - يمنع الدفع المكرر لنفس الطلب باستخدام Redis lock.
  - يستخدم في `PaymentService`.

### `src/main/java/com/example/ecommerce/model/`

- يحتوي على الكيانات (Entities) أو النماذج (Models).
- أمثلة:
  - `User`, `Product`, `Category`
  - `Cart`, `CartItem`
  - `Order`, `OrderItem`
  - `Payment`, `AuthToken`
- كيفية استخدامه: تُستخدم هذه الكائنات في الـ Service والـ Repository.

### `src/main/java/com/example/ecommerce/dto/`

- يحتوي على طلبات الاستدعاء JSON (`Request DTO`) والردود (`Response DTO`).
- أمثلة:
  - `AuthRequest`, `AuthResponse`
  - `ProductRequest`, `CartRequest`, `OrderRequest`, `PaymentRequest`, `CategoryRequest`
  - `SimpleResponse`
- كيفية استخدامه: تُستخدم كـ واجهة بين JSON في الطلبات والبيزنس لوجيك.

### `src/main/java/com/example/ecommerce/repository/`

- يحتوي على طبقة الوصول للبيانات (`Data Access Layer`).
- لكل كيان يوجد Repository خاص به.
- أمثلة:
  - `UserRepository` للتعامل مع جدول `users`
  - `ProductRepository` للتعامل مع جدول `products`
  - `CategoryRepository` للتعامل مع `categories`
  - `CartRepository` للتعامل مع `carts` و `cart_items`
  - `OrderRepository` للتعامل مع `orders` و `order_items`
  - `PaymentRepository` للتعامل مع `payments`
  - `AuthRepository` لتخزين وإلغاء الـ JWT tokens
  - `ResetRepository` لمسح البيانات وإعادة ضبط النظام
- كيفية استخدامه: كل Repository يستخدم `DataSource` ويخدم الـ Service المناسب.

### `src/main/java/com/example/ecommerce/service/`

- يحتوي على منطق الأعمال (`Business Logic`).
- أمثلة:
  - `AuthService` يقوم بإنشاء المستخدم وتسجيل الدخول وتسجيل الخروج.
  - `ProductService` يدير المنتجات ويعتني بالكاش في Redis.
  - `CategoryService` يدير التصنيفات.
  - `CartService` يدير السلة.
  - `OrderService` ينشئ الطلبات ويحسب المجموع.
  - `PaymentService` ينفذ الدفعات ويمنع الدفع المكرر.
  - `ResetService` يعيد ضبط جميع البيانات ويحذف الكاش.
- كيفية استخدامه: Servlets تستدعي الخدمات مباشرة بعد قراءة الطلب.

### `src/main/java/com/example/ecommerce/filter/`

- `AuthFilter.java`
  - فلتر يتحقق من JWT في كل طلب إلى `/api/*` باستثناء تسجيل الدخول والتسجيل.
  - يضع بيانات المستخدم في الطلب إذا كان التوكن صالحاً.
  - كيفية استخدامه: يعمل تلقائياً لأن `web.xml` يعرفه على جميع المسارات.

### `src/main/java/com/example/ecommerce/servlet/`

- يحتوي على طبقة التحكم (`Controllers`) التي تتعامل مع HTTP.
- أمثلة:
  - `AuthServlet` للتسجيل، login، logout.
  - `ProductServlet` لعرض وإنشاء وتحديث وحذف المنتجات.
  - `CategoryServlet` لعرض وإنشاء وتعديل وحذف التصنيفات.
  - `CartServlet` لإضافة منتج إلى السلة وعرض السلة.
  - `OrderServlet` لإنشاء الطلبات وعرضها.
  - `PaymentServlet` لدفع الطلبات وعرض الدفعات.
  - `ResetServlet` لإعادة ضبط قاعدة البيانات والذاكرة المؤقتة.
- كيفية استخدامه: كل Servlet يستقبل الطلب من `web.xml` ويدير استجابة JSON.

## كيف تستخدم الملفات بشكل عملي

1. افتح `src/main/resources/application.properties` وعدّل إعدادات MySQL و Redis و JWT.
2. افتح `phpMyAdmin` ونفّذ `src/main/resources/schema.sql` لإنشاء الجداول.
3. افتح المشروع في IntelliJ واستورد `pom.xml` كمشروع Maven.
4. أضف إعداد Tomcat في `Run > Edit Configurations...` ثم شغّل التطبيق.
5. استخدم `postman_collection.json` و `postman_environment.json` لاختبار الـ API.
6. إذا احتجت إعادة ضبط البيانات، استخدم endpoint:
   - `POST /api/reset/all` مع صلاحية ADMIN.

## ملاحظة عامة
- لو أردت تعديل أي endpoint أو إضافة ميزة جديدة، ابدأ بالتعديل في `service` ثم `repository` ثم `servlet`.
- إذا أردت تغيير إعدادات الاتصال أو الكاش، عدّل `application.properties` فقط.
- إذا واجهت مشكلة في بناء المشروع، تأكد من `pom.xml` ونسخة Java ووجود Maven.

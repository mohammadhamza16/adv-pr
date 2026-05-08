# دليل التشغيل والاعداد بالعربي

## ١. المتطلبات الأساسية
- Java JDK 17 أو أحدث
- Maven
- MySQL
- Redis
- Tomcat أو أي Servlet Container يدعم Jakarta
- IntelliJ IDEA

## ٢. كيفية إعداد السكيما في phpMyAdmin
1. افتح phpMyAdmin في المتصفح: `http://localhost/phpmyadmin`
2. اضغط `New` لإنشاء قاعدة بيانات جديدة.
3. اكتب اسم القاعدة `ecommerce` أو أي اسم تريده.
4. اضغط `Create`.
5. بعد إنشاء القاعدة، اخترها من القائمة الجانبية.
6. اذهب إلى تبويب `SQL`.
7. افتح ملف `src/main/resources/schema.sql` وانسخ المحتوى.
8. الصقه في مربع SQL في phpMyAdmin.
9. اضغط `Go` لتنفيذ الاستعلامات.
10. تأكد من أن الجداول التالية تم إنشاؤها:
   - `users`
   - `categories`
   - `products`
   - `carts`
   - `cart_items`
   - `orders`
   - `order_items`
   - `payments`
   - `auth_tokens`

## ٣. ضبط إعدادات قاعدة البيانات و Redis
افتح ملف `src/main/resources/application.properties` وعدّل القيم التالية:
- `db.url=jdbc:mysql://localhost:3306/ecommerce?useSSL=false&serverTimezone=UTC`
- `db.user=root`
- `db.password=changeme`
- `redis.host=localhost`
- `redis.port=6379`
- `jwt.expiration-ms=86400000`

> غير `db.user` و `db.password` إلى بيانات الدخول الخاصة بك.

## ٤. تشغيل المشروع في IntelliJ IDEA
1. افتح IntelliJ IDEA.
2. اختر `File > New > Project from Existing Sources...`.
3. اختر المجلد `c:\Users\Computec\Downloads\adv pr`.
4. حدد ملف `pom.xml` ليتم استيراد المشروع كمشروع Maven.
5. انتظر IntelliJ حتى يكمل تحميل dependencies.

### إعداد Tomcat داخل IntelliJ
1. اذهب إلى `Run > Edit Configurations...`.
2. اضغط `+` ثم اختر `Tomcat Server > Local`.
3. في تبويب `Deployment` اضغط `+` ثم اختر `Artifact`.
4. اختر `ecommerce-servlet-api:war` ثم اضغط `OK`.
5. في خانة `Server` تأكد من أن مسار Tomcat صحيح.
6. احفظ التكوين واضغط زر التشغيل (`Run`).

### بعد التشغيل
- افتح المتصفح وزور:
  - `http://localhost:8080/ecommerce-servlet-api/api/auth/register`
  - `http://localhost:8080/ecommerce-servlet-api/api/auth/login`
  - `http://localhost:8080/ecommerce-servlet-api/api/products`

## ٥. أهم المشاكل الممكن تواجهها وكيف تحلها

### Postman
- الملفين الجاهزين:
  - `postman_collection.json`
  - `postman_environment.json`
- استورد `postman_collection.json` في Postman.
- بعد الاستيراد، اختر البيئة (`Environment`) ثم اضبط `baseUrl` إذا لزم الأمر.

### مشكلة ١: `mvn` غير معروف أو غير مثبت
- الحل: ثبت Maven أو استخدم Maven الموجود في IntelliJ.
- يمكنك التحقق بفتح Terminal وكتابة: `mvn -version`.

### مشكلة ٢: إصدار Java غير صحيح
- تأكد أن لديك JDK 17 أو أحدث.
- في IntelliJ: `File > Project Structure > Project` وتحقق من `Project SDK`.

### مشكلة ٣: فشل الاتصال بقاعدة MySQL
- تأكد أن MySQL يعمل.
- تأكد أن اسم المستخدم وكلمة السر صحيحان.
- تأكد أن القاعدة `ecommerce` موجودة.
- تأكد من أن `application.properties` يحتوي على نفس بيانات الاتصال.

### مشكلة ٤: الجداول غير موجودة
- السبب: `schema.sql` لم ينفذ بشكل صحيح.
- الحل: أعد تنفيذ SQL من phpMyAdmin.

### مشكلة ٥: Redis غير شغال
- تأكد أن خدمة Redis تعمل.
- تأكد من أن `redis.host` و `redis.port` صحيحتان.
- إذا Redis غير مثبت، ثبت Redis أو شغّله.

### مشكلة ٦: خطأ `404` عند الوصول للـ API
- تأكد أن التطبيق تم نشره في Tomcat.
- تأكد أن الـ URL صحيح.
- تأكد أن التطبيق يعمل في `http://localhost:8080/ecommerce-servlet-api/`.

### مشكلة ٧: خطأ `401 Unauthorized`
- هذا يعني أن الـ JWT غير موجود أو غير صالح.
- تأكد من إرسال `Authorization: Bearer <token>` في الطلب.
- سجّل دخول أولاً عبر `/api/auth/login` للحصول على الـ token.

### مشكلة ٨: خطأ `NoClassDefFoundError`
- عادة يعني أن dependency ناقص.
- تأكد من تشغيل Maven `mvn clean package` أو إعادة استيراد `pom.xml` في IntelliJ.

### مشكلة ٩: مشكلة صلاحيات MySQL
- إذا ظهر خطأ `Access denied`:
  - تأكد أن المستخدم لديه صلاحيات على القاعدة.
  - يمكنك تعديل الصلاحيات من phpMyAdmin أو من MySQL.

### مشكلة ١٠: بيانات المنتجات لا تتحدث بعد التعديل
- التطبيق يستخدم Redis للتخزين المؤقت.
- عند تعديل أو حذف منتج يجب أن يتم مسح الكاش.
- تأكد من أن Redis يعمل بدون أخطاء.

## ٦. أهم endpoints للتجربة
- تسجيل مستخدم جديد:
  - `POST /api/auth/register`
- تسجيل دخول:
  - `POST /api/auth/login`
- خروج:
  - `POST /api/auth/logout`
- عرض المنتجات:
  - `GET /api/products`
- إضافة منتج (Admin فقط):
  - `POST /api/products`
- إضافة للسلة:
  - `POST /api/cart/add`
- عرض السلة:
  - `GET /api/cart`
- إنشاء طلب:
  - `POST /api/orders`
- دفع الطلب:
  - `POST /api/payments`

## ٧. نصائح إضافية
- افتح `application.properties` دائماً بعد أي تغيير في إعدادات الاتصال.
- إذا أضفت مستخدم جديد كـ `ADMIN`، يجب أن يتم ذلك بواسطة حساب admin موجود أو أول تسجيل.
- إذا أردت تجربة الطلبات من Postman، أضف Header:
  - `Authorization: Bearer <token>`

## ٨. خريطة الملفات الأساسية
- `pom.xml` - إعدادات المشروع و dependencies.
- `src/main/resources/application.properties` - إعدادات DB و Redis و JWT.
- `src/main/resources/schema.sql` - إنشاء الجداول.
- `src/main/webapp/WEB-INF/web.xml` - إعداد المجلدات والـ servlets.
- `src/main/java/com/example/ecommerce/controller` - طبقة واجهة HTTP.
- `src/main/java/com/example/ecommerce/service` - منطق التطبيق.
- `src/main/java/com/example/ecommerce/dao` - الوصول إلى قاعدة البيانات.
- `src/main/java/com/example/ecommerce/model` - الكيانات والنماذج.
- `src/main/java/com/example/ecommerce/util` - أدوات مثل JWT و JSON و Redis.

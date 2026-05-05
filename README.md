# E-commerce Servlet REST API

This project is a clean-architecture REST API built with Java Servlet API, MySQL, Redis, JWT authentication, and Jackson for JSON handling.

## Features
- Servlet API only (no Spring)
- MySQL database persistence
- Redis caching for product lists and rate limiting
- JWT authentication with role-based authorization (`ADMIN`, `USER`)
- Shared configuration using `ServletContext`
- Endpoints for Auth, Products, Cart, Orders, Payments, Categories
- Clean architecture with controllers, services, repositories, models, utils

## Setup
1. Install Java 17+ and Maven.
2. Install MySQL and Redis.
3. Create the MySQL database and tables using `src/main/resources/schema.sql`.
4. Update database and Redis settings in `src/main/resources/application.properties`.
5. Build the WAR:
   ```powershell
   mvn clean package
   ```
6. Deploy the generated `target/ecommerce-servlet-api.war` to Tomcat or another Servlet container.

## Local Run
- Deploy to Apache Tomcat under `webapps/`.
- Use `http://localhost:8080/ecommerce-servlet-api/api/...`.

## تشغيل من خلال IntelliJ
1. افتح المشروع في IntelliJ IDEA.
2. اختر `File > New > Project from Existing Sources...` واختر مجلد المشروع `c:\Users\Computec\Downloads\adv pr`.
3. اترك IntelliJ يقرأ ملف `pom.xml` ويضبط المشروع كمشروع Maven.
4. افتح `Run > Edit Configurations...`.
5. اضغط `+` واختر `Tomcat Server > Local` إذا كان لديك Tomcat مثبت.
6. في تبويب `Deployment` اضغط `+` واختر `Artifact` ثم اختر `ecommerce-servlet-api:war`.
7. شغّل التكوين بالضغط على زر التشغيل.
8. بعد التشغيل، افتح المتصفح لدى `http://localhost:8080/ecommerce-servlet-api/api/`.

## إنشاء السكيما على phpMyAdmin
1. افتح phpMyAdmin في المتصفح (مثلاً `http://localhost/phpmyadmin`).
2. أنشئ قاعدة بيانات جديدة باسم `ecommerce` أو أي اسم تريده.
3. بعد إنشاء القاعدة، اخترها ثم افتح تبويب `SQL`.
4. انسخ محتوى `src/main/resources/schema.sql` والصقه في مربع SQL.
5. اضغط `Go` لتنفيذ الاستعلامات وإنشاء الجداول.
6. تأكد من تحديث `src/main/resources/application.properties` قيم `db.url`, `db.user`, و`db.password` لتطابق إعدادات MySQL الخاصة بك.

## Postman
- استورد `postman_collection.json` في Postman.
- استورد `postman_environment.json` لإعداد المتغيرات الجاهزة مثل `baseUrl` و `authToken`.
- بعد الاستيراد، استخدم البيئة لتشغيل الطلبات بسهولة.

## Notes
- `AuthServlet` handles register, login, logout.
- `ProductServlet` caches product list in Redis.
- `PaymentServlet` prevents duplicate payments via Redis rate-limiter.
- Shared config is loaded by `AppConfigListener` into `ServletContext`.

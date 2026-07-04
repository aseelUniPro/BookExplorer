# Book Explorer — دليل الاستخدام والتسليم

## 1. رفع المشروع على GitHub (من الجوال، بدون لابتوب)

1. افتحي GitHub.com من متصفح الجوال، أو ثبّتي تطبيق **GitHub** الرسمي.
2. سوّي مستودع (Repository) جديد اسمه مثلاً `BookExplorer`.
3. ارفعي كل ملفات هذا المشروع (كل المجلدات والملفات اللي بهاي الحزمة) لنفس بنية المجلدات بالضبط:
   - إذا استخدمتِ تطبيق GitHub: من داخل الريبو فيه خيار "Add file" أو ممكن تستخدمي تطبيق **Working Copy** (iOS) أو **Termux + git** (Android) لرفع المجلد كامل دفعة وحدة (أسهل بكتير من رفع ملف ملف).
   - أبسط طريقة عمليًا: نزّلي تطبيق **Termux** من F-Droid، وفيه نفّذي:
     ```
     pkg install git
     cd storage/downloads/BookExplorer   (بعد ما تفكي الضغط عن الزيب هون)
     git init
     git remote add origin https://github.com/USERNAME/BookExplorer.git
     git add .
     git commit -m "Book Explorer project"
     git branch -M main
     git push -u origin main
     ```
     (رح تحتاجي Personal Access Token بدل الباسورد وقت الـ push — تقدري تسويه من GitHub Settings > Developer settings > Personal access tokens)

## 2. بناء الـ APK أوتوماتيك

بمجرد ما ترفعي الملفات، GitHub Actions رح يشتغل لحاله (بسبب ملف `.github/workflows/build.yml`) ويبني الـ APK على سيرفراتهم.

- روحي لتبويب **Actions** بالريبو، رح تلاقي عملية اسمها "Build Debug APK" شغالة (تاخد 3-5 دقايق).
- لما تخلص وتصير علامة ✅ خضراء، افتحيها، وتحت بند **Artifacts** رح تلاقي ملف اسمه `BookExplorer-debug-apk` — حمّليه، هاد الـ APK جاهز.

## 3. تجربة التطبيق

- نزّلي الـ APK على جوالك (فعّلي "تثبيت من مصادر غير معروفة" إذا طلب).
- افتحيه: رح يجيب قائمة كتب من Google Books API تلقائيًا، بتقدري تدوسي على أي كتاب يوديكي لتبويب التفاصيل.
- اسحبي لتحت لتحديث القائمة (Pull-to-refresh)، أو استخدمي أيقونة البحث فوق للفلترة، أو اضغطي مطولًا على أي كتاب لقائمة سريعة.

## 4. نقاط الشرح للفيديو (8-10 دقايق)

اشرحيها بالترتيب هاد وبتغطي كل بنود التقييم:

1. **الفكرة العامة**: تطبيق يعرض كتب من Google Books API بتصميم Material.
2. **TabLayout + ViewPager2**: فيه تابين، كل تاب هو Fragment منفصل (`ListFragment`, `DetailFragment`) — بيّني ملف `MainActivity.java` و`ViewPagerAdapter.java`.
3. **RecyclerView**: بتعرضي `item_book.xml` وتشرحي إنه كل صف عبارة عن CardView بصورة، عنوان، ومؤلف — من `BooksAdapter.java`.
4. **جلب البيانات + AsyncTask**: افتحي `FetchBooksTask.java`، اشرحي إنه بيشتغل بخيط خلفية (`doInBackground`) عشان ما يعلّق الواجهة، وبيرجع النتيجة بـ `onPostExecute`.
5. **ProgressBar**: بيّني إنه بيظهر أثناء التحميل ويختفي لما تخلص.
6. **تمرير البيانات بين الفراغمنتين**: اشرحي `SharedBookViewModel.java` — إنه ViewModel مشترك بين التابين، فلما تدوسي على كتاب بالتاب الأول، بينحدّث فورًا بالتاب الثاني.
7. **Menu**: بيّني الـ Options Menu (بحث + تحديث) والـ Popup Menu (ضغطة طويلة على كتاب).
8. **Notifications**: افتحي `NotificationHelper.java`، ودوري إشعار ينزل لما تحمّل البيانات بنجاح أو لما يصير خطأ بالنت.
9. **ميزات إضافية**: Pull-to-refresh والبحث الفوري (SearchView).
10. **ختام**: لخّصي التقنيات المستخدمة (Fragments, RecyclerView, ViewModel, AsyncTask, Material Design).

## 5. التسليم

- APK + الكود المصدري (نفس هالمجلد كـ ZIP) → ارفعيهم Google Drive وشاركي الرابط.
- الفيديو → ارفعيه Google Drive أو YouTube (unlisted) وشاركي الرابط.

## بنية المشروع

```
BookExplorer/
├── app/
│   ├── build.gradle
│   └── src/main/
│       ├── AndroidManifest.xml
│       ├── java/com/aseel/bookexplorer/
│       │   ├── MainActivity.java
│       │   ├── ViewPagerAdapter.java
│       │   ├── ListFragment.java          (Tab 1)
│       │   ├── DetailFragment.java        (Tab 2)
│       │   ├── BooksAdapter.java          (RecyclerView + Popup Menu)
│       │   ├── Book.java                  (Model)
│       │   ├── SharedBookViewModel.java   (تمرير البيانات بين الفراغمنتين)
│       │   ├── FetchBooksTask.java        (AsyncTask لجلب الـ API)
│       │   └── NotificationHelper.java
│       └── res/
│           ├── layout/  (activity_main, fragment_list, fragment_detail, item_book)
│           ├── menu/    (main_menu — Options Menu مع بحث وتحديث)
│           ├── values/  (strings, colors, themes)
│           └── drawable/ (تصميم مخصص: cover_placeholder, ic_launcher)
├── .github/workflows/build.yml   (يبني الـ APK أوتوماتيك)
├── build.gradle
├── settings.gradle
└── gradle.properties
```

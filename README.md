# Şoför Rezervasyon ve Atama Sistemi (MVP)

Bu uygulama, bir acentenin müşteriler için hızlı rezervasyon oluşturmasını ve konum bazlı olarak **en yakın** müsait şoförü otomatik olarak atamasını sağlayan bir **Minimum Viable Product (MVP)** projesidir.

## Öne Çıkan Özellikler

*   **Hızlı Rezervasyon:** Müşteri adı ve koordinat bilgileriyle saniyeler içinde talep oluşturma.
*   **En Yakın Şoför Algoritması:** Mevcut şoförler arasından müşteriye coğrafi olarak en yakın ve durumu "Müsait" olan şoförü otomatik seçer.
*   **Canlı Operasyon Paneli:** Şoförlerin anlık durumlarını (Müsait/Meşgul) ve koordinatlarını takip edebileceğiniz web arayüzü.
*   **Döngüsel İş Yönetimi:** Tamamlanan yolculuklardan sonra şoförü "İşi Bitir" butonuyla tek tıkla tekrar sisteme kazandırma.
*   **Modern Teknoloji Yığını:** Spring Boot tabanlı RESTful API yapısı.

## 🛠 Teknik Detaylar

*   **Backend:** Java 22, Spring Boot 3.2.x, Spring Data JPA
*   **Veritabanı:** H2 Database (Hızlı test için In-memory yapı)
*   **Frontend:** HTML5, CSS3, JavaScript (Fetch API), Bootstrap 5
*   **Bağımlılık Yönetimi:** Maven

## 📋 Kurulum ve Çalıştırma

1.  **Projeyi Klonlayın:**
    ```bash
    git clone [https://github.com/gnskerem/sofor-rezervasyon-mvp.git](https://github.com/gnskerem/sofor-rezervasyon-mvp.git)
    ```
2.  **Dizine Girin:**
    ```bash
    cd sofor-rezervasyon-mvp
    ```
3.  **Uygulamayı Başlatın:**
    IntelliJ üzerinden `DriverAssignmentApplication` sınıfını çalıştırın veya terminalden:
    ```bash
    mvn spring-boot:run
    ```
4.  **Arayüze Erişin:**
    Tarayıcınızın adres çubuğuna şunu yazın:
    [http://localhost:8080](http://localhost:8080)

## 🖥 Kullanım Senaryosu
1. Panel açıldığında listedeki şoförlerin konumlarını ve puanlarını görebilirsiniz.
2. Sağdaki formdan müşteri adını ve konumunu (Enlem/Boylam) girip "Rezervasyon Oluştur"a basın.
3. Sistem en yakın şoförü bulup atayacak ve o şoförün durumu "Meşgul" (Kırmızı) olarak güncellenecektir.
4. Yolculuk bittiğinde "İşi Bitir" butonuna basarak şoförü tekrar müsait hale getirebilirsiniz.

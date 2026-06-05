<img width="1876" height="906" alt="image" src="https://github.com/user-attachments/assets/74075a60-a216-4418-9127-688649f23dce" />
<img width="1024" height="504" alt="image" src="https://github.com/user-attachments/assets/67cff8c3-232d-407d-aadf-59a37c695c88" />


## Temel Özellikler & Çözülen Problemler

* **Sürücü Odaklı Mikro Havuz (Batching Architecture):** Talepler anlık olarak ilk boştaki araca fırlatılmaz. Sistem, 20-30 saniyelik zamanlayıcı pencerelerinde (`@Scheduled`) talepleri bir havuzda toplar ve toplu optimizasyon matrisi çalıştırır.
* **Sürücü Onay Mekanizması (Proposed Status):** Sürücüler sistem tarafından dayatılan rotaları kabul etmek zorunda değildir. Havuzun önerdiği işler sürücü ekranına `PROPOSED` statüsünde düşer; sürücü işi kabul edebilir (`/accept`) veya reddedebilir (`/reject`). Reddedilen iş otomatik olarak havuzda sıradaki en uygun şoföre aktarılır.
* **Gerçek Yol Ağı Tabanlı Rotalama (OSRM Entegrasyonu):** Kuş uçuşu (as-the-crow-flies) mesafe hesaplamaları yerine, gerçek otoyol, sokak ve dönüş kısıtlamalarını içeren Open Source Routing Machine (OSRM) API'si entegre edilmiştir.
* **Akıllı Paydos Yönetimi (Going Home Mode):** Sürücüler mesai bitiminde ev adreslerini sisteme girerek "Paydos Modu"nu aktif edebilir. Sistem, haritada sürücünün evine doğru giden otoyol rotasını çizer ve sürücüye sadece evinin doğrultusundaki işleri önererek "boş yakıt tüketimini" sıfıra indirir.

---

## Teknolojik Yığın (Tech Stack)

* **Backend:** Java 17, Spring Boot, Spring Scheduler
* **Data & Persistence:** Spring Data JPA, H2 Database / PostgreSQL
* **Frontend & Map:** Vanilla JavaScript, Bootstrap 5, Leaflet.js (Canlı Harita Katmanı)
* **Routing Engine:** Open Source Routing Machine (OSRM) API

---

## Mimari Akış Şeması (Nasıl Çalışır?)

1. **Talep Oluşturma:** Müşteri veya Acente haritadan konum seçerek araç sınıfına göre (Sedan, VIP Vito, Minibüs) talep açar.
2. **Havuza Alınma:** Rezervasyon veritabanına `PENDING` olarak kaydedilir ve asenkron mikro havuza gönderilir.
3. **Akıllı Eşleştirme:** Arka planda dönen optimizasyon motoru, o an aktif ve müsait olan sürücülerin konumlarını, araç tiplerini ve paydos durumlarını OSRM matrisiyle tarar.
4. **Sürücü Onayı:** En yüksek skora sahip şoföre iş teklif edilir (`PROPOSED`). Şoför onaylarsa statü `ASSIGNED` olur ve rota haritada kilitlenir; reddederse havuz döngüsü bir sonraki şoför için yeniden başlar.

---

## 💻 Kurulum ve Çalıştırma

Projenin yerel ortamınızda ayağa kalkması için aşağıdaki adımları takip edebilirsiniz:

```bash
# 1. Projeyi klonlayın
git clone [https://github.com/gnskerem/sofor-rezervasyon-mvp.git](https://github.com/gnskerem/sofor-rezervasyon-mvp.git)

# 2. Proje dizinine gidin
cd sofor-rezervasyon-mvp

# 3. Maven bağımlılıklarını yükleyin ve derleyin
mvn clean install

# 4. Uygulamayı çalıştırın
mvn spring-boot:run

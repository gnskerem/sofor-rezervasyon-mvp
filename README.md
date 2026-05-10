VIP Transfer & Smart Driver Assignment MVP

Bu proje, VIP transfer operasyonlarını dijitalleştirmek ve şoför verimliliğini artırmak amacıyla geliştirilmiş bir **Akıllı Atama Sistemi**'dir. Sistem, sadece mesafeyi değil, şoförlerin iş-özel hayat dengesini ve maliyet optimizasyonunu (Back-haul) hesaba katar.

Öne Çıkan Özellikler

* **Akıllı Şoför Atama:** Yolcu sayısı, araç tipi (Sedan, VIP Vito, Minibüs) ve şoför puanına göre en uygun eşleşmeyi yapar.
* **Şoför Odaklı Eve Dönüş Optimizasyonu (Back-haul):** Şoförler "Paydos Et" moduna geçtiklerinde, sistem onları evlerine en yakın güzergahtaki yolcularla eşleştirir. Bu sayede "boş kilometre" (deadhead) maliyeti düşer.
* **Dinamik Fiyatlandırma:** Araç segmentine ve katedilen mesafeye göre gerçek zamanlı transfer ücreti hesaplar.
* **Canlı Operasyon Haritası:** Leaflet.js entegrasyonu ile şoförlerin konumlarını, durumlarını ve rotaları harita üzerinde anlık takip imkanı sunar.

## Teknoloji Yığını

* **Backend:** Java 17, Spring Boot 3.x, Spring Data JPA
* **Frontend:** HTML5, CSS3, Bootstrap 5, JavaScript (Fetch API)
* **Harita Servisi:** Leaflet.js & OpenStreetMap
* **Veritabanı:** H2 Database (In-Memory)
* **Algoritma:** Haversine Formülü (Koordinat tabanlı mesafe ölçümü)

## Mimari ve Mantık

### Akıllı Skorlama Algoritması
Sistem, bir şoförü atarken şu formülü kullanır:
- **Normal Mod:** `Skor = (Mesafe * 0.8) - (Rating * 0.2)`
- **Eve Dönüş Modu:** `Skor = (Eve Yakınlık Mesafesi * 0.4) - (Rating * 0.5)`
*Düşük skor alan şoför, göreve en uygun aday kabul edilir.*

## Kurulum ve Çalıştırma

1. Projeyi klonlayın ve dizine gidin:
   git clone [https://github.com/kullanici-adin/driver-assignment-mvp.git](https://github.com/kullanici-adin/driver-assignment-mvp.git)
   cd driver-assignment-mvp

2. Uygulamayı çalıştırın:
mvn spring-boot:run

3.Tarayıcıdan erişin: http://localhost:8080
   ```bash
   git clone [https://github.com/kullanici-adin/driver-assignment-mvp.git](https://github.com/kullanici-adin/driver-assignment-mvp.git)
   cd driver-assignment-mvp

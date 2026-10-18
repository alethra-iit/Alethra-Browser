# Alethra untuk Android

Browser Android berbasis WebView. Pencarian utama Google; alamat situs (http/https/domain) langsung dibuka.

Cara membangun APK tanpa Android Studio:
1. Buat repo baru di GitHub, upload semua isi folder ini (termasuk folder .github).
2. Buka tab Actions, tunggu "Build APK" selesai.
3. Buka Releases, unduh app-debug.apk, pasang di HP (izinkan "sumber tidak dikenal").

## Struktur tampilan

- **Navigasi bawah**: Beranda · Tab · Bookmark · Unduhan · Menu
- **Beranda**: logo, kolom cari (dengan mic), pintasan (tahan lama untuk menghapus, "Tambah" untuk menambah)
- **Tab**: filter Semua / Aktif / Tertutup
- **Histori** (dikelompokkan Hari ini / Kemarin, bisa dicari), **Unduhan** (Semua / Berlangsung / Selesai), **Bookmark**
- **Menu**: Tab baru, Histori, Unduhan, Bookmark, Pengaturan, Incognito, Hapus data, Mode full, Buka tab tertutup
- **Pengaturan**: Umum, Mesin pencari, Privasi & Keamanan (Kunci aplikasi, Hapus izin situs, DNS aman), Tampilan
- Warna: hanya putih (Terang) dan gelap (Gelap), ikon outline di `res/drawable/ic_*.xml`

## Mode Incognito

Ikon: huruf A di dalam mata yang dicoret. Saat aktif, Incognito membuka tab baru bertanda ikon itu dan:
- tidak menyimpan riwayat, sesi tab, tab tertutup, izin situs, kata sandi, maupun riwayat unduhan
- memakai profil WebView terpisah (cookie, localStorage, dan cache tidak bercampur dengan tab biasa) yang dihapus saat Incognito dimatikan, aplikasi ditutup, atau dibuka lagi
- menutup semua tab Incognito saat dinonaktifkan
- tanpa cache disk, tanpa isi otomatis formulir, cookie pihak ketiga diblokir
- memblokir screenshot dan pratinjau aplikasi terbaru (bisa dimatikan di Pengaturan > Privasi)
- keyboard tidak belajar dari ketikan (IME no-personalized-learning)
Jika WebView perangkat belum mendukung profil terpisah, cookie dimatikan sementara selama Incognito.

## Kunci aplikasi

Pengaturan > Privasi & Keamanan > Kunci aplikasi. Memakai sidik jari atau PIN/pola/kata sandi layar kunci Android (BiometricPrompt di Android 11+, layar kredensial sistem di Android 7-10).
- **Seluruh aplikasi**: layar kunci muncul saat aplikasi dibuka dan setelah ditinggalkan lebih lama dari waktu yang dipilih (Langsung / 30 detik / 1 / 5 / 15 menit).
- **Bagian tertentu**: pilih Tab Incognito, Bookmark, dan/atau Kata sandi (termasuk isi otomatis).
- Mengubah atau mematikan kunci harus diverifikasi dulu. Jika kunci layar perangkat dihapus, kunci aplikasi dimatikan otomatis.
- Saat aplikasi ke latar belakang dan kunci aktif, pratinjau aplikasi terbaru disembunyikan.

## DNS aman (DoH)

Pengaturan > Privasi & Keamanan > DNS aman. Pilihan: Cloudflare, Cloudflare Keamanan, AdGuard, Quad9, Google, atau alamat DoH kustom.
Alethra menjalankan proxy lokal (127.0.0.1) dan mengarahkan WebView ke sana lewat `ProxyController`; nama situs di-resolve lewat DNS-over-HTTPS (RFC 8484), isi lalu lintas HTTPS tidak dibuka. Jawaban 0.0.0.0, `::`, REFUSED (dan NXDOMAIN untuk Quad9) dianggap diblokir, jadi pelacak dan situs berbahaya tidak tersambung. Berlaku untuk halaman di Alethra saja, bukan seluruh perangkat (untuk itu ada tombol DNS pribadi sistem). Perlu Android System WebView yang mendukung `PROXY_OVERRIDE`; jika server DoH tidak terjangkau, DNS jaringan dipakai sebagai cadangan.

## Izin situs

Izin Kamera, Mikrofon, Lokasi, dan Notifikasi yang kamu setujui atau blokir diingat per situs. Untuk melupakannya: Pengaturan > Privasi & Keamanan > Hapus izin situs. Mode desktop, JavaScript, dan Zoom teks berlaku untuk semua situs (tidak per situs). Di mode Incognito izin hanya di memori.
Catatan notifikasi: WebView tidak punya Push API, jadi notifikasi hanya muncul saat halaman situs sedang terbuka dan aktif.

## Simpan halaman offline

Buka halaman web, lalu ketuk ⋮ > **Simpan offline**. Alethra menyimpan dua berkas di penyimpanan aplikasi: arsip halaman (MHTML) dan salinan PDF, supaya bisa dibaca tanpa internet.
- **Daftar**: Menu > **Halaman offline** (bisa dicari). Ketuk untuk membuka halaman di tab baru; tombol ⋮ atau tahan lama untuk Buka PDF, Buka versi online, Salin alamat, atau Hapus. PDF dibuka di penampil bawaan.
- Menyimpan halaman yang sama lagi akan menggantikan salinan lama.
- Gulir halaman sampai bawah sebelum menyimpan agar gambar yang dimuat lambat ikut tersimpan.
- Tidak tersedia di mode Incognito. Hapus data penjelajahan tidak menghapus halaman offline; hapus lewat daftar.
- Jika perangkat menolak membuat PDF otomatis, halaman tetap tersimpan sebagai arsip.

## Pembaca suara di mode baca

Di mode baca (⋮ > Mode baca) ada tombol **Baca** yang membacakan artikel dengan suara bawaan perangkat (Text-to-Speech). Paragraf yang sedang dibaca disorot dan layar ikut menggulir.
- **Baca / Jeda / Lanjut**: Jeda berhenti di paragraf yang sedang dibaca, Lanjut mengulang paragraf itu lalu meneruskan.
- **1x**: ketuk untuk ganti kecepatan (1x, 1.25x, 1.5x, 0.75x); pilihan diingat.
- **Suara**: pilih bahasa/aksen pembaca (bawaan Indonesia, atau Otomatis mengikuti bahasa halaman, atau bahasa lain). Pilihan diingat dan langsung berlaku saat sedang membaca. Jika suara bahasa itu belum terpasang, muncul pemberitahuan dan dipakai suara bawaan perangkat; tombol "Pengaturan suara" membuka pengaturan Text-to-speech Android untuk memasang suaranya. Suara dan paket bahasa diatur di Pengaturan Android > Output text-to-speech.
- Membaca berhenti saat mode baca ditutup atau pindah halaman.

## Pengelola unduhan

Di halaman Unduhan setiap berkas punya tombol sesuai statusnya:
- **Jeda** (saat berlangsung) dan **Lanjutkan** (saat dijeda). Android tidak menyediakan API resmi untuk ini; Alethra memakai jalur internal DownloadManager. Jika perangkat menolak, muncul pemberitahuan dan unduhan tetap berjalan seperti biasa.
- **Ulangi** (saat gagal): ketuk tombol atau barisnya untuk mengunduh ulang dari alamat yang sama. Alasan gagal ditampilkan (penyimpanan penuh, koneksi terputus, HTTP 404, dan seterusnya).
- Status lebih rinci: Mengantre, Menunggu jaringan, Menunggu Wi-Fi, Dijeda, dan persentase.
- Filter baru **Gagal**.

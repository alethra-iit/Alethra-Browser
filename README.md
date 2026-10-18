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

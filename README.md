# Alethra untuk Android

Browser Android berbasis WebView. Pencarian utama Google; alamat situs (http/https/domain) langsung dibuka.

Cara membangun APK tanpa Android Studio:
1. Buat repo baru di GitHub, upload semua isi folder ini (termasuk folder .github).
2. Buka tab Actions, tunggu "Build APK" selesai.
3. Buka Releases, unduh app-debug.apk, pasang di HP (izinkan "sumber tidak dikenal").

## Struktur tampilan

- **Navigasi bawah**: Beranda · Tab · Histori · Unduhan · Menu
- **Beranda**: logo, kolom cari (dengan mic), pintasan (tahan lama untuk menghapus, "Tambah" untuk menambah)
- **Tab**: filter Semua / Aktif / Tertutup
- **Histori** (dikelompokkan Hari ini / Kemarin, bisa dicari), **Unduhan** (Semua / Berlangsung / Selesai), **Bookmark**
- **Menu**: Tab baru, Histori, Unduhan, Bookmark, Pengaturan, Incognito, Hapus data, Mode full, Buka tab tertutup
- **Pengaturan**: Umum, Mesin pencari, Privasi & Keamanan, Tampilan
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

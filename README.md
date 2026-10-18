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
- **Menu**: Tab baru, Histori, Unduhan, Bookmark, Pengaturan, Mode Incognito
- **Pengaturan**: Umum, Mesin pencari, Privasi & Keamanan, Tampilan
- Warna: hanya putih (Terang) dan gelap (Gelap), ikon outline di `res/drawable/ic_*.xml`

# Alethra untuk Android

Browser Android berbasis WebView. Pencarian utama Google; alamat situs (http/https/domain) langsung dibuka.

Cara membangun APK tanpa Android Studio:
1. Buat repo baru di GitHub, upload semua isi folder ini (termasuk folder .github).
2. Buka tab Actions, tunggu "Build APK" selesai.
3. Buka Releases, unduh app-debug.apk, pasang di HP (izinkan "sumber tidak dikenal").
   (Workflow otomatis mengunduh library sherpa-onnx; tidak perlu ikut diunggah.)

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

Buka halaman web, lalu ketuk ⋮ > **Simpan halaman** (icon printer + halaman offline) lalu pilih **Simpan offline**. Alethra menyimpan dua berkas di penyimpanan aplikasi: arsip halaman (MHTML) dan salinan PDF, supaya bisa dibaca tanpa internet.
- **Daftar**: Menu > **Halaman offline** (bisa dicari). Ketuk untuk membuka halaman di tab baru; tombol ⋮ atau tahan lama untuk Buka PDF, Buka versi online, Salin alamat, atau Hapus. PDF dibuka di penampil bawaan.
- Menyimpan halaman yang sama lagi akan menggantikan salinan lama.
- Gulir halaman sampai bawah sebelum menyimpan agar gambar yang dimuat lambat ikut tersimpan.
- Tidak tersedia di mode Incognito. Hapus data penjelajahan tidak menghapus halaman offline; hapus lewat daftar.
- Jika perangkat menolak membuat PDF otomatis, halaman tetap tersimpan sebagai arsip.

## Suara ke teks (terjemahan ucapan video, hampir realtime)

Pengaturan > Play > Suara ke teks. Saat **aktif**, menu ⋮ di halaman web menampilkan **Suara ke teks**; saat dimatikan, menunya hilang dan fitur berhenti.
- **Tanpa mikrofon**: audio diambil langsung dari elemen video/audio di halaman (`captureStream`), jadi video tidak dijeda, tidak terpengaruh earphone atau volume, dan tidak ada bunyi bip.
- **Pemotongan per kalimat (VAD)**: audio direkam sebagai PCM 16 kHz (WAV) dan dipotong di jeda bicara, bukan tiap 6 detik tetap. Jeda ±0,3-0,65 dtk menutup satu potongan; ucapan panjang tanpa jeda dipaksa dipotong pada batas maksimum. Derau pendek dan bagian senyap tidak dikirim.
- **Pipeline paralel**: pengenalan suara dan terjemahan berjalan di thread terpisah. Terjemahan yang lambat (jaringan atau ML Kit) tidak lagi menahan potongan suara berikutnya, jadi potongan jarang dilewati. Tiap potongan bernomor urut; terjemahan yang selesai terlambat ditempatkan di slot "sebelumnya" dan tidak menimpa kalimat yang lebih baru. Jika terjemahan menumpuk (>6), kalimat ditampilkan apa adanya.
- **Penangkap audio**: memakai AudioWorklet (berjalan di thread audio, bukan thread halaman). Pada situs dengan CSP ketat yang menolak modul worklet, otomatis memakai ScriptProcessor. Audio dikirim sebagai WAV 16-bit 16 kHz yang langsung di-base64 (tanpa Blob/FileReader).
- **Dua tahap tampil**: begitu Whisper selesai, teks asli langsung muncul, terjemahan (Google Translate) menyusul sepersekian detik kemudian. Status di kotak menampilkan tunda terakhir, mis. "Mendengarkan… · tunda 1.4 dtk".
- **Kecepatan teks** (Pengaturan > Play > Suara ke teks): **Cepat** (potongan maks ±3,8 dtk), **Seimbang** (±5,5 dtk, bawaan), **Hemat** (±8,5 dtk). Makin cepat, makin banyak permintaan API per menit.
- **Kualitas**: ekor transkrip sebelumnya dikirim sebagai petunjuk (prompt) Whisper supaya kalimat yang terpotong tetap nyambung; segmen yang terdeteksi halusinasi (bukan ucapan, atau berulang-ulang) dibuang.
- **Persiapan**: isi **Kunci API** (Groq gratis, atau OpenAI) di Pengaturan > Play > Suara ke teks. Kunci disimpan hanya di perangkat dan tidak ikut ekspor pengaturan. Pilih **Penyedia** dan **Bahasa ucapan video** (Otomatis = Whisper mendeteksi sendiri; memilih bahasa sendiri lebih cepat dan akurat).
- **Pakai**: putar video, ketuk ⋮ > Suara ke teks. Kotak popup di bawah menampilkan terjemahan besar, teks asli kecil, dan satu terjemahan sebelumnya. Kotak bisa digeser; X atau ⋮ > Suara ke teks lagi untuk berhenti. **Bahasa tujuan** memakai Pengaturan > Play > Bahasa tujuan.
- **Layar penuh**: saat video dibuka layar penuh, terjemahan tampil sebagai subtitle (hanya teks, tanpa ikon mikrofon dan tombol X) dan hilang sendiri 8 detik setelah ucapan berhenti. Keluar dari layar penuh, kotak popup biasa kembali. Atur di Pengaturan > Play > Teks suara di layar penuh: ukuran, posisi (bawah/atas), latar, warna, dan tampilkan teks asli.
- **Batas Groq gratis** (per organisasi): 20 permintaan/menit, 2.000 permintaan/hari, 7.200 detik audio/jam, 28.800 detik audio/hari. Jika kena batas (429), Alethra jeda otomatis sesuai Retry-After lalu lanjut. Untuk pemakaian lama pilih **Hemat** atau naik ke paket berbayar.
- Keterbatasan: video di iframe lintas-situs dan video berDRM tidak bisa ditangkap (status menyebutnya); Whisper bukan streaming sejati, jadi tunda minimal ±1-2 dtk setelah kalimat selesai. Berhenti saat pindah tab atau aplikasi ke latar belakang (kecuali PiP).

## Suara ke teks offline (sherpa-onnx + ML Kit)

Pengaturan > Play > Suara ke teks > **Mode**: pilih **Online** (Whisper cloud + Google Translate, seperti sebelumnya) atau **Offline** (semua diproses di perangkat, tanpa internet dan tanpa Kunci API).
- **Alur**: audio video ditangkap dan dipotong per kalimat oleh `stt.js` (sama seperti mode Online), lalu `OfflineEngine.kt` mengubahnya jadi teks dengan **sherpa-onnx** dan menerjemahkannya dengan **ML Kit On-device Translation**. Kotak popup, subtitle layar penuh, dan tampilan dua tahap (teks asli lalu terjemahan) tidak berubah.
- **Model offline** (Pengaturan > Play > Suara ke teks > Model offline): daftar model dengan ukuran, status, progres, **Unduh**, **Batal**, **Pakai model ini**, dan **Hapus** (ada juga Hapus semua model suara). Unduhan yang terputus dilanjutkan (HTTP Range) dan dicek ruang penyimpanannya dulu. Peringatan muncul jika memakai data seluler.
  - **Suara**: SenseVoice (Mandarin, Kanton, Jepang, Korea, Inggris; cepat; disarankan), Whisper tiny/base/small (±99 bahasa), Whisper base/small khusus Inggris. Model disimpan di penyimpanan aplikasi (`files/offline_models`) dari Hugging Face (`csukuangfj/sherpa-onnx-*`).
  - **Terjemahan**: satu model per **bahasa** (±30 MB), bukan per pasangan. ML Kit memakai Inggris sebagai perantara, jadi Jepang > Indonesia butuh model Jepang, Indonesia, dan Inggris. Model Inggris tidak bisa dihapus selama masih terpasang karena dipakai semua terjemahan.
- **Bahasa sumber**: dipakai pilihan "Bahasa ucapan video" bila diisi; jika Otomatis, dipakai bahasa yang dideteksi SenseVoice, lalu deteksi bahasa ML Kit dari teksnya.
- **Model baru**: **Zipformer Jepang (ReazonSpeech, repo reazon-research/reazonspeech-k2-v2)** khusus bahasa Jepang, dan **Whisper turbo** (berat, untuk HP kuat). Daftar berkas kedua model dibaca dari API Hugging Face saat diunduh (memilih versi int8), jadi ukuran pasti baru muncul saat unduhan berjalan; angka di daftar hanya perkiraan.
- **Diagnosis**: kotak Suara ke teks (mode Offline) kini menampilkan "Mengenali … dtk suara…", "Hasil kosong dari model", atau "Diabaikan sebagai derau" bila relevan, dan kegagalan library native tidak lagi hilang diam-diam.
- **Model satu bahasa (dinamis)**: Zipformer Korea, Thai, Rusia (biasa dan kecil), NeMo GigaAM Rusia (transducer dan CTC), Zipformer Vietnam (30M dan int8), Kanton, Zipformer CTC Mandarin, TeleSpeech Mandarin, Zipformer Inggris (GigaSpeech dan 2023-06-26), Parakeet TDT 0.6b v2, Whisper tiny.en dan medium.en. Daftar berkas dibaca dari API Hugging Face saat diunduh; pengunduh mencoba organisasi `k2-fsa/` dan `csukuangfj/` bergantian. Untuk model satu bahasa, bahasa sumber terjemahan otomatis memakai bahasa modelnya, bukan pilihan "Bahasa ucapan video".
- **Urutkan model**: di Model offline, baris **Urutkan** mengatur daftar: *Bahasa (dikelompokkan)* (Jepang, Multibahasa, Korea, Mandarin, Kanton, Thai, Vietnam, Rusia, Inggris; yang terpasang di atas), *Ukuran (kecil dulu)*, atau *Terpasang dulu*. Pilihan diingat.
- **Cadangan terjemahan online**: jika model terjemahan belum diunduh, pakai Google Translate online (bisa dimatikan supaya benar-benar offline).
- **Catatan**: Whisper memproses jendela 30 detik, jadi `small` lambat di HP kelas menengah ke bawah; untuk subtitle terasa langsung pakai SenseVoice atau Whisper tiny/base. Model dimuat ke RAM saat Suara ke teks dinyalakan dan dilepas saat dimatikan. ML Kit membutuhkan Google Play Services untuk mengunduh model terjemahan.
- **Build**: workflow GitHub Actions mengunduh `sherpa-onnx-1.13.7.aar` ke `app/libs/` sebelum membangun APK (file ini tidak ikut di repo). Jika membangun di Android Studio, unduh sendiri dari https://github.com/k2-fsa/sherpa-onnx/releases/download/v1.13.7/sherpa-onnx-1.13.7.aar ke `app/libs/sherpa-onnx.aar`. APK dibatasi ke arm64-v8a dan armeabi-v7a, ukuran naik sekitar 40-60 MB.

## Pembaca suara di mode baca

Di mode baca (⋮ > Mode baca) bar atas memakai ikon (tidak ada teks, tahan lama untuk melihat nama). Ikon pengeras suara yang membacakan artikel dengan suara bawaan perangkat (Text-to-Speech). Paragraf yang sedang dibaca disorot dan layar ikut menggulir.
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

## Menu tahan-lama pada tautan dan gambar

Menahan lama tautan atau gambar membuka lembar aksi dari bawah dengan ikon di tiap pilihan.
- **Tautan**: Buka di tab baru, Salin alamat, Bagikan.
- **Gambar**: Buka di tab baru, Salin alamat, Bagikan, dan **Simpan gambar ke JPG / PNG / SVG** (juga GIF, WEBP, AVIF, BMP, ICO). Format dicek dari isi berkas, bukan hanya dari akhiran alamat, jadi label dan ekstensi berkas yang disimpan sesuai format aslinya. Berkas disimpan apa adanya tanpa dikonversi.
- Kepala lembar menampilkan pratinjau gambar, nama situs, dan alamat (gambar base64 ditampilkan ringkas, tidak lagi sebagai teks panjang).
- Gambar di dalam tautan: ada pilihan tambahan **Buka tautan di tab baru**.
- Gambar `data:` (base64) dan `blob:` ikut didukung untuk simpan dan bagikan (dibagikan sebagai berkas gambar).
- Gambar disimpan ke lokasi yang dipilih di Pengaturan > Unduhan > Lokasi simpan (termasuk folder pilihan). Gambar dari alamat web masuk ke daftar Unduhan; gambar base64/blob langsung ditulis ke folder tujuan. Jika lokasi yang dipilih Film atau Musik, gambar base64/blob dipindah ke Gambar.

## Lokasi simpan unduhan

- **Bawaan: Unduhan/Alethra** (folder dibuat otomatis saat unduhan pertama).
- Pengaturan > Unduhan > Lokasi simpan: pilih Unduhan, Dokumen, Gambar, Film, Musik, atau **Pilih folder lain…** untuk memilih folder mana saja (penyimpanan internal atau kartu SD) lewat pemilih folder Android. Pilihan diingat, dan izin aksesnya dipertahankan; jika izin hilang Alethra kembali ke Unduhan/Alethra.
- Untuk folder pilihan, DownloadManager tidak bisa menulis langsung ke sana. Berkas diunduh dulu ke penyimpanan sementara Alethra (progres, jeda, dan ulangi tetap berfungsi), lalu disalin ke folder pilihan setelah selesai dan berkas sementaranya dihapus. Butuh ruang kosong sebesar berkasnya selama proses. Di halaman Unduhan muncul status "Menyalin ke folder pilihan…"; bila gagal, ketuk barisnya untuk mencoba lagi.
- Unduhan dari tab Incognito ke folder pilihan tidak dicatat di riwayat.

## Bookmark berfolder, impor/ekspor, dan cadangan

- **Folder bookmark**: tahan lama atau ketuk baris bookmark > Ubah, isi kolom Folder (nama baru otomatis membuat folder). Folder muncul sebagai filter di atas daftar, lengkap dengan jumlahnya. Ubah juga bisa mengganti judul dan alamat.
- **Ekspor/impor bookmark**: tombol ⋮ di halaman Bookmark. Format HTML standar (Netscape), bisa dipakai bersama Chrome dan Firefox. Impor melewati alamat yang sudah ada dan mempertahankan folder.
- **Cadangan pengaturan**: Pengaturan > Ekspor pengaturan & bookmark / Pulihkan dari cadangan (berkas JSON). Tidak menyertakan sandi, kunci aplikasi, histori, unduhan, izin situs, dan pengaturan DNS aman.
- Pencadangan otomatis Android dimatikan (`allowBackup=false`) karena kunci enkripsi sandi tidak ikut terpulihkan.

## Keamanan jembatan JavaScript

Fungsi mode baca dan suara (`readerPref`, `tts*`) hanya bisa dipanggil saat mode baca benar-benar aktif di tab itu, sehingga situs biasa tidak bisa memicunya. Simpan sandi hanya diterima dari alamat yang sama dengan halaman. Notifikasi situs tetap lewat jembatan karena memang harus dipanggil situs (dan tetap butuh izin Anda).

## Video mini (Perkecil video)

Video yang diperkecil bisa digeser (seret) dan diubah ukurannya: cubit dengan dua jari, atau seret ikon maximize di sudut kiri bawah. Ukuran terakhir diingat.
- **Ketuk tengah**: putar/jeda.
- **Ketuk dua kali di sisi kiri**: mundur 10 detik. **Di sisi kanan**: maju 10 detik. Layar menampilkan sekilas "−10 dtk" / "+10 dtk".
- Tombol ⋮ di pojok kanan atas video mini menyembunyikan atau menampilkan tombol ubah ukuran, layar penuh, dan tutup. Setiap video mini dimulai dengan tombol tampil.

## Akses berkas lokal

WebView hanya boleh membuka berkas lokal berikut: aset bawaan aplikasi (beranda) dan arsip `<angka>.mhtml` di folder `offline/` milik aplikasi. Semua `file:` lain dan semua `content:` ditolak, baik dari bar alamat, tautan di halaman, maupun intent dari aplikasi lain. Aplikasi juga tidak lagi mendaftar sebagai pembuka berkas HTML lokal (intent filter `file:` dan `content:` dihapus), dan `allowContentAccess` dimatikan.

## Fitur tambahan

- **Kelola izin situs**: Pengaturan > Privasi & Keamanan > Kelola izin situs. Daftar situs beserta izin kamera, mikrofon, lokasi, dan notifikasi; tiap izin bisa diubah ke Tanya / Izinkan / Blokir.
- **Bagikan halaman offline**: di Halaman offline, ketuk ⋮ pada sebuah halaman, lalu Bagikan halaman (MHTML) atau Bagikan PDF. Berkas disalin sementara ke cache dengan nama sesuai judul halaman.
- **Tambah ke layar utama**: menu ⋮ saat membuka halaman web (Android 8+). Ikonnya favicon situs, atau huruf pertama jika tidak ada.
- **Saran di bar alamat**: saat mengetik muncul saran dari bookmark dan riwayat (tanpa jaringan, tidak dikirim ke mesin pencari). Riwayat tidak dipakai saat Incognito, dan bookmark tidak disarankan saat terkunci. Bisa dimatikan di Privasi & Keamanan > Saran saat mengetik.
- Impor dan ekspor bookmark (HTML) sudah ada sebelumnya di halaman Bookmark > ⋮.

## Cookie pihak ketiga dan HTTP

- **Cookie pihak ketiga**: Pengaturan > Privasi & Keamanan > Blokir cookie pihak ketiga. Bawaan: diizinkan di tab biasa (supaya login SSO dan pembayaran tertanam tetap jalan), selalu diblokir di Incognito.
- **HTTP tanpa enkripsi**: aplikasi tetap mengizinkan `http://` (`usesCleartextTraffic`) karena banyak alamat lokal dan situs lama belum memakai HTTPS. Untuk mengalihkan otomatis ke HTTPS, aktifkan Privasi & Keamanan > Gunakan HTTPS.

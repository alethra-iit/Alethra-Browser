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

## Notepad melayang

Pengaturan > **Widget beranda** > **Notepad** (sakelar on/off, seperti Jam dan tanggal). Saat aktif, tombol **Notepad** muncul di menu titik tiga (⋮).
- Ketuk tombol itu untuk membuka kotak catatan yang melayang di atas halaman. Ketuk lagi, atau tombol **X**, untuk menutup.
- Baris judul: geser untuk memindahkan, tombol **–** (perkecil) dan tombol **X** (tutup). Seret ikon di pojok kanan bawah untuk mengubah ukuran. Posisi dan ukuran diingat.
- **Perkecil**: kotak berubah menjadi ikon notepad kecil yang melayang dan bisa digeser. Ketuk ikon itu untuk menampilkan kotak lagi (atau ketuk Notepad di menu ⋮).
- Tombol di bawah judul (tekan lama untuk melihat namanya): **Buka**, **Salin** (teks yang dipilih, atau semua bila tidak ada yang dipilih), **Tempel**, dan **Simpan**.
- **Buka**: memilih berkas teks (.txt, .md, dll., maksimal 2 MB) dari folder mana pun. Jika catatan sedang berisi, pilih Ganti atau Tambahkan.
- **Simpan**: menyimpan salinan di aplikasi dan menulis berkas .txt ke lokasi unduhan, sehingga tampil di halaman **Unduhan** (mengikuti pengaturan lokasi dan "Tanya sebelum mengunduh"). Di mode Incognito berkas tetap disimpan tetapi tidak dicatat di riwayat unduhan.
- Kotak tetap terbuka saat berpindah tab atau membuka halaman Tab, Bookmark, dan Pengaturan.
- Teks yang belum disimpan tetap dijaga sebagai draf (indikator "Belum disimpan"); catatan yang sudah disimpan ikut dalam ekspor cadangan. Mematikan sakelar hanya menyembunyikan notepad, catatan tidak dihapus.

## Cari di halaman

Menu ⋮ > **Cari di halaman** membuka baris cari di atas halaman: ketik kata, semua kemunculan disorot, penghitung menampilkan posisi (mis. 3/12). Tombol panah atas/bawah (atau tombol Enter/Cari di keyboard) berpindah ke kemunculan sebelumnya/berikutnya dan berputar kembali ke awal. Kata yang sama dicari ulang otomatis saat berpindah tab atau memuat halaman baru. Tombol Kembali atau X menutup baris cari.

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

## Terjemahan Suara (terjemahan ucapan video, hampir realtime)

Pengaturan > Play > Terjemahan Suara. Saat **aktif**, menu ⋮ di halaman web menampilkan **Terjemahan Suara**; saat dimatikan, menunya hilang dan fitur berhenti.
- **Tanpa mikrofon**: audio diambil langsung dari elemen video/audio di halaman (`captureStream`), jadi video tidak dijeda, tidak terpengaruh earphone atau volume, dan tidak ada bunyi bip.
- **Pemotongan per kalimat (VAD)**: audio direkam sebagai PCM 16 kHz (WAV) dan dipotong di jeda bicara, bukan tiap 6 detik tetap. Jeda ±0,3-0,65 dtk menutup satu potongan; ucapan panjang tanpa jeda dipaksa dipotong pada batas maksimum. Derau pendek dan bagian senyap tidak dikirim.
- **Pipeline paralel**: pengenalan suara dan terjemahan berjalan di thread terpisah. Terjemahan yang lambat (jaringan atau ML Kit) tidak lagi menahan potongan suara berikutnya, jadi potongan jarang dilewati. Tiap potongan bernomor urut; terjemahan yang selesai terlambat ditempatkan di slot "sebelumnya" dan tidak menimpa kalimat yang lebih baru. Jika terjemahan menumpuk (>6), kalimat ditampilkan apa adanya.
- **Penangkap audio**: memakai AudioWorklet (berjalan di thread audio, bukan thread halaman). Pada situs dengan CSP ketat yang menolak modul worklet, otomatis memakai ScriptProcessor. Audio dikirim sebagai WAV 16-bit 16 kHz yang langsung di-base64 (tanpa Blob/FileReader).
- **Dua tahap tampil**: begitu Whisper selesai, teks asli langsung muncul, terjemahan (Google Translate) menyusul sepersekian detik kemudian. Status di kotak menampilkan tunda terakhir, mis. "Mendengarkan… · tunda 1.4 dtk".
- **Kecepatan teks** (Pengaturan > Play > Terjemahan Suara): **Cepat** (potongan maks ±3,8 dtk), **Seimbang** (±5,5 dtk, bawaan), **Hemat** (±8,5 dtk). Makin cepat, makin banyak permintaan API per menit.
- **Kualitas**: ekor transkrip sebelumnya dikirim sebagai petunjuk (prompt) Whisper supaya kalimat yang terpotong tetap nyambung; segmen yang terdeteksi halusinasi (bukan ucapan, atau berulang-ulang) dibuang.
- **Persiapan**: isi **Kunci API** (Groq gratis, atau OpenAI) di Pengaturan > Play > Terjemahan Suara. Kunci disimpan hanya di perangkat dan tidak ikut ekspor pengaturan. Pilih **Penyedia** dan **Bahasa ucapan video** (Otomatis = Whisper mendeteksi sendiri; memilih bahasa sendiri lebih cepat dan akurat).
- **Pakai**: putar video, ketuk ⋮ > Terjemahan Suara. Kotak popup di bawah menampilkan terjemahan besar, teks asli kecil, dan satu terjemahan sebelumnya. Kotak bisa digeser; X atau ⋮ > Terjemahan Suara lagi untuk berhenti. **Bahasa tujuan** memakai Pengaturan > Play > Bahasa tujuan.
- **Layar penuh**: saat video dibuka layar penuh, terjemahan tampil sebagai subtitle (hanya teks, tanpa ikon mikrofon dan tombol X) dan hilang sendiri 8 detik setelah ucapan berhenti. Keluar dari layar penuh, kotak popup biasa kembali. Atur di Pengaturan > Play > Teks suara di layar penuh: ukuran, posisi (bawah/atas), latar, warna, dan tampilkan teks asli.
- **Batas Groq gratis** (per organisasi): 20 permintaan/menit, 2.000 permintaan/hari, 7.200 detik audio/jam, 28.800 detik audio/hari. Jika kena batas (429), Alethra jeda otomatis sesuai Retry-After lalu lanjut. Untuk pemakaian lama pilih **Hemat** atau naik ke paket berbayar.
- Keterbatasan: video di iframe lintas-situs dan video berDRM tidak bisa ditangkap (status menyebutnya); Whisper bukan streaming sejati, jadi tunda minimal ±1-2 dtk setelah kalimat selesai. Berhenti saat pindah tab atau aplikasi ke latar belakang (kecuali PiP).

## Menu Terjemahan Suara (v89): mesin dan terjemahan dipisah

Menu **Mode** (Online/Offline/Hibrid) dihapus. Sekarang ada dua pilihan bebas yang dikombinasikan sendiri, di Pengaturan > Play > Terjemahan Suara:
- **Mesin terjemahan suara**: **Whisper cloud**, **Sherpa-onnx** (di perangkat), atau **Vosk** (streaming di perangkat).
- Baris kedua berubah sesuai mesin: **Kunci API** + **Penyedia** (Whisper cloud), **Model Sherpa-onnx** (halaman daftar model suara sherpa), atau **Model Vosk** (daftar model Vosk).
- **Terjemahan**: **Online** (Google Translate) atau **Offline**. Bisa dipakai dengan mesin apa pun, jadi Whisper cloud + terjemahan offline atau Vosk + Google Translate juga mungkin. Bila **Offline**, muncul menu **Bahasa terjemahan offline** yang membuka halaman tersendiri: pilih mesin terjemahan (ML Kit, NLLB-200, M2M100, Opus-MT, Argos), lalu unduh model/bahasanya; di sini juga ada **Cadangan terjemahan online**. Model suara dan model terjemahan tidak lagi digabung di satu halaman.
- Menu lain tetap tampil di semua mesin: **Bahasa ucapan video**, **Terjemahkan ke**, **Kecepatan teks**, dan **Teks langsung** (hanya Sherpa-onnx).
  - **Bahasa ucapan video**: bila model yang dipilih khusus satu bahasa (semua model Vosk, serta model Sherpa khusus Jepang/Korea/Mandarin/Inggris dsb.), barisnya menampilkan "Mengikuti model (bahasa)" dan mengetuknya menampilkan penjelasan; bahasa sumber terjemahan diambil dari model. Untuk model Sherpa multibahasa (SenseVoice, Whisper) dan Whisper cloud, menu ini dipakai seperti biasa.
  - **Kecepatan teks** kini berfungsi di Vosk: mengatur batas panjang ucapan tanpa jeda sebelum dipotong paksa (Cepat: 100 karakter, atau 40 untuk Jepang/Mandarin; Seimbang: 170/70; Hemat: 260/110). Jeda bicara tetap diputuskan Vosk sendiri.
- Kunci pengaturan: `stengine` (`whisper`/`sherpa`/`vosk`) dan `sttr` (`online`/`offline`). Pengaturan lama (`stengine=alethra`, `stmode`, `stvtr`) otomatis dibaca ke pilihan baru: Online → Whisper cloud + Google; Offline → Sherpa-onnx + terjemahan offline; Hibrid → Sherpa-onnx + Google; Vosk mengikuti `stvtr`.
- Catatan: keterangan di bagian-bagian di bawah yang menyebut "mode Online/Offline/Hibrid" kini berarti kombinasi di atas; "Model offline" kini bernama **Model Sherpa-onnx** (suara) dan **Bahasa terjemahan offline** (terjemahan).

## Pengaturan audio untuk Terjemahan Suara (v90)

Pengaturan > Play > Terjemahan Suara > **Pengaturan audio**. Semua opsi bisa dikombinasikan, berlaku untuk semua mesin (Whisper cloud, Sherpa-onnx, Vosk), dan **langsung diterapkan** ke Terjemahan Suara yang sedang berjalan (pipeline audio dibuat ulang tanpa mematikannya). Bawaan sama dengan perilaku lama.
- **Preset cepat**: Standar, Video bising, Suara pelan, Musik latar, Hanya suara jelas (preset mengganti semua opsi di bawah, lalu bisa diubah satu per satu).
- **Volume dan ambang**: *Penguat volume awal* (1× sampai 4×), *Volume minimum* (suara di bawah −36 sampai −20 dB dibisukan, jadi tidak jadi teks), *Volume maksimum* (suara di atas −6 sampai −20 dB dibisukan: musik keras/efek), *Kepekaan deteksi ucapan* (pengali di atas derau latar, 2,0 sampai 5,0; bawaan 2,8), *Ucapan minimum* (potongan lebih pendek dibuang; bawaan 300 ms).
- **Pembersih suara**: *Peredam derau* (subtraksi spektrum, 3 tingkat ±−4/−7/−11 dB), *Redam suara latar* (gerbang di sela ucapan, 3 tingkat), *Filter gemuruh* (high-pass 80/120/200/300 Hz; bawaan 80), *Filter desis* (low-pass 2 tahap, 7/5/3,8 kHz), *Penguat suara pelan otomatis* (maks 1×/3×/6×/12×; bawaan 6×).
- **Teks hasil**: *Panjang teks minimum* dan toggle *Saring halusinasi dan derau teks* (matikan bila kalimat yang sah ikut terbuang).
- **Diagnostik**: *Tampilkan level volume* menambahkan level dB (RMS) ke status kotak Terjemahan Suara, supaya Volume minimum/maksimum bisa ditentukan dari angka nyata. *Atur ulang pengaturan audio* mengembalikan semuanya.
- Tidak berlaku untuk Vosk (karena streaming tanpa pemotongan): Kepekaan deteksi ucapan, Ucapan minimum, dan Penguat suara pelan otomatis. Opsi lain berlaku.
- **Implementasi** (`stt.js`): rantai per blok audio 16 kHz: penguat -> high-pass -> low-pass -> ukur level -> peredam derau (STFT 512/256, jendela sqrt-Hann, pelacakan derau per bin dengan minimum, penghalusan gain antar-bin dan antar-waktu) -> gerbang volume (frame 10 ms, hold, attack 3 ms, release 40 ms; gain disejajarkan dengan keluaran peredam derau lewat antrean). Kunci pengaturan: `sta_pg`, `sta_min`, `sta_max`, `sta_sens`, `sta_dur`, `sta_ns`, `sta_gate`, `sta_hp`, `sta_lp`, `sta_auto`, `sta_minlen`, `sta_filt`, `sta_meter` (ikut ekspor pengaturan).
- Catatan: peredam derau menganggap suara yang stabil terus-menerus (dengung, nada panjang) sebagai derau; ucapan yang berjeda tidak terpengaruh.

## Terjemahan Suara: mesin Vosk (streaming di perangkat)

Pengaturan > Play > Terjemahan Suara > **Mesin terjemahan suara**: pilih **Whisper cloud**, **Sherpa-onnx**, atau **Vosk**.
- **Streaming sejati**: `stt.js` mengirim audio PCM 16-bit 16 kHz terus-menerus (±200 ms per kiriman, `AlSTT.pcm`), tanpa pemotongan kalimat. Vosk (`VoskAsr` di `VoskEngine.kt`) mengeluarkan hasil sementara kata per kata di setiap kiriman dan hasil final saat mendeteksi jeda bicara. Teks sementara tampil agak pudar lalu menjadi tegas saat final (kotak popup dan subtitle layar penuh sama seperti mode lain). Video dijeda atau berganti: kalimat yang berjalan diselesaikan (`AlSTT.pcmEnd`). Ucapan sangat panjang tanpa jeda (musik/latar) dipotong paksa supaya teks tidak menumpuk.
- **Pengenalan selalu di perangkat**, gratis, tanpa Kunci API. Menu **Terjemahan** menentukan terjemahannya: **Online** (Google Translate) atau **Offline** (ML Kit, NLLB, M2M100, Opus-MT, Argos; memakai pengaturan Mesin terjemahan offline yang sama). Terjemahan sementara (saat orang masih bicara) memakai Google Translate atau ML Kit; mesin offline yang berat baru menerjemahkan saat kalimat final.
- **Model Vosk** (Pengaturan > Play > Terjemahan Suara > Model Vosk): diunduh sekali dari `alphacephei.com/vosk/models` (zip, dilanjutkan bila terputus, diekstrak ke `files/vosk_models/<id>/model`). Daftar dikelompokkan **Jepang**, **Inggris**, **Bahasa lain**: Jepang kecil (±48 MB) dan besar (±1 GB), Inggris AS kecil (±40 MB), sedang `lgraph` (±128 MB), besar (±1,8 GB), Gigaspeech (±2,3 GB), Inggris India kecil, serta model kecil Mandarin, Korea, Rusia, Prancis, Jerman, Spanyol, Portugis, Italia, Belanda, Turki, Vietnam, Hindi, Persia, Polandia, Ceko, Katalan, Ukraina, Kazakh, Uzbek, dan Arab. Model yang ditandai **besar** adalah model server: butuh RAM sangat besar dan hanya cocok untuk HP kelas atas.
- Bahasa sumber terjemahan otomatis memakai bahasa modelnya; **Bahasa ucapan video**, **Kecepatan teks**, dan **Teks langsung** tidak berlaku untuk Vosk.
- Keterbatasan: Vosk tidak mendeteksi bahasa otomatis (pilih model sesuai video), hasilnya tanpa tanda baca dan akurasinya di bawah Whisper/Zipformer. Spasi antar kata pada hasil Jepang dan Mandarin dibuang otomatis.
- **Build**: dependensi `com.alphacephei:vosk-android:0.3.47@aar` dan `net.java.dev.jna:jna:5.13.0@aar` (Maven Central, sesuai panduan Vosk Android), tidak perlu langkah tambahan di workflow.

## Terjemahan Suara offline (sherpa-onnx + ML Kit)

Pengaturan > Play > Terjemahan Suara > **Mode**: pilih **Online** (Whisper cloud + Google Translate, seperti sebelumnya), **Offline** (semua diproses di perangkat, tanpa internet dan tanpa Kunci API), atau **Hibrid**.
- **Hibrid**: suara dikenali di perangkat dengan sherpa-onnx (memakai model suara yang diunduh, tanpa Kunci API), tetapi terjemahannya memakai Google Translate online seperti mode Online. Tidak perlu mengunduh model terjemahan ML Kit; hanya model suara. Perlu internet untuk terjemahan; jika Google Translate gagal, teks asli tetap tampil dan status menampilkan "Google Translate gagal, periksa internet".
- **Alur**: audio video ditangkap dan dipotong per kalimat oleh `stt.js` (sama seperti mode Online), lalu `OfflineEngine.kt` mengubahnya jadi teks dengan **sherpa-onnx** dan menerjemahkannya dengan **ML Kit On-device Translation**. Kotak popup, subtitle layar penuh, dan tampilan dua tahap (teks asli lalu terjemahan) tidak berubah.
- **Model offline** (Pengaturan > Play > Terjemahan Suara > Model offline): daftar model dengan ukuran, status, progres, **Unduh**, **Batal**, **Pakai model ini**, dan **Hapus** (ada juga Hapus semua model suara). Unduhan yang terputus dilanjutkan (HTTP Range) dan dicek ruang penyimpanannya dulu. Peringatan muncul jika memakai data seluler.
  - **Suara**: SenseVoice (Mandarin, Kanton, Jepang, Korea, Inggris; cepat; disarankan), Whisper tiny/base/small (±99 bahasa), Whisper base/small khusus Inggris. Model disimpan di penyimpanan aplikasi (`files/offline_models`) dari Hugging Face (`csukuangfj/sherpa-onnx-*`).
  - **Terjemahan**: satu model per **bahasa** (±30 MB), bukan per pasangan. ML Kit memakai Inggris sebagai perantara, jadi Jepang > Indonesia butuh model Jepang, Indonesia, dan Inggris. Model Inggris tidak bisa dihapus selama masih terpasang karena dipakai semua terjemahan.
- **Bahasa sumber**: dipakai pilihan "Bahasa ucapan video" bila diisi; jika Otomatis, dipakai bahasa yang dideteksi SenseVoice, lalu deteksi bahasa ML Kit dari teksnya.
- **Model Jepang**: **Zipformer Jepang (ReazonSpeech, repo reazon-research/reazonspeech-k2-v2)** dan **Parakeet TDT-CTC Jepang (NeMo, ±650 MB, repo csukuangfj/sherpa-onnx-nemo-parakeet-tdt_ctc-0.6b-ja-35000-int8)**, keduanya khusus bahasa Jepang. Ditambah **Moonshine Tiny/Base Jepang** (ringan, ±40/±130 MB), **FunASR Nano** (berbasis LLM, ±1 GB) dan **Qwen3-ASR 0.6B** (berbasis LLM, ±1 GB); ketiganya dipasang lewat daftar model Jepang dan memakai folder tokenizer/berkas `.ort` dari repo `csukuangfj/sherpa-onnx-*`. FunASR Nano dan Qwen3-ASR berat di RAM, sebaiknya dipakai di HP kelas atas. Whisper turbo, Whisper small Inggris, dan Whisper medium Inggris sudah dihapus dari daftar (berkasnya di perangkat dibersihkan otomatis saat aplikasi dibuka). Daftar berkas model dinamis dibaca dari API Hugging Face saat diunduh (memilih versi int8), jadi ukuran pasti baru muncul saat unduhan berjalan; angka di daftar hanya perkiraan.
- **Diagnosis**: kotak Terjemahan Suara (mode Offline) kini menampilkan "Mengenali … dtk suara…", "Hasil kosong dari model", atau "Diabaikan sebagai derau" bila relevan, dan kegagalan library native tidak lagi hilang diam-diam.
- **Model satu bahasa (dinamis)**: Zipformer Korea, Thai, Rusia (biasa dan kecil), NeMo GigaAM Rusia (transducer dan CTC), Zipformer Vietnam (30M dan int8), Kanton, Zipformer CTC Mandarin, TeleSpeech Mandarin, Zipformer Inggris (GigaSpeech dan 2023-06-26), Parakeet TDT 0.6b v2, Whisper tiny.en. Daftar berkas dibaca dari API Hugging Face saat diunduh; pengunduh mencoba organisasi `k2-fsa/` dan `csukuangfj/` bergantian. Untuk model satu bahasa, bahasa sumber terjemahan otomatis memakai bahasa modelnya, bukan pilihan "Bahasa ucapan video".
- **Urutkan model**: di Model offline, baris **Urutkan** mengatur daftar: *Bahasa (dikelompokkan)* (Jepang, Multibahasa, Korea, Mandarin, Kanton, Thai, Vietnam, Rusia, Inggris; yang terpasang di atas), *Ukuran (kecil dulu)*, atau *Terpasang dulu*. Pilihan diingat.
- **Mesin terjemahan offline** (hanya di mode Offline; Pengaturan > Play > Terjemahan Suara > Mesin terjemahan offline): pilih **ML Kit** (ringan, ±30 MB per bahasa, lewat perantara Inggris) atau **NLLB-200 distilled 600M** (CTranslate2, int8; lebih akurat, langsung dari bahasa sumber ke tujuan tanpa perantara Inggris, ±630 MB, butuh ±1 GB RAM, 1-3 detik per kalimat). Model NLLB diunduh di Model offline > *Terjemahan NLLB-200* (repo `JustFrederik/nllb-200-distilled-600M-ct2-int8`; lisensi model CC-BY-NC 4.0, non-komersial). Jika NLLB belum siap atau gagal, terjemahan memakai Cadangan terjemahan online bila menyala.
  - **Akurasi terjemahan suara** (`stt.js`, `OfflineEngine.kt`): audio diturunkan ke 16 kHz dengan konteks audio 16 kHz bawaan browser (cadangan: resampler windowed-sinc streaming), disaring high-pass 80 Hz, dan suara pelan dinaikkan hingga 6x. Mode offline memakai potongan lebih panjang (maks 9/12/15 dtk) dan pemotongan paksa dicari di titik paling senyap, sisanya dilanjutkan di potongan berikutnya. Deteksi ucapan memakai histeresis. Model transducer (Zipformer) memakai `modified_beam_search`. Halusinasi khas ("ご視聴ありがとうございました", dsb.) dan pengulangan berlebihan dibuang.
  - **Menu**: *Mesin terjemahan offline* berada di atas *Model offline*; halaman Model offline hanya menampilkan model terjemahan untuk mesin yang dipilih (mode Hibrid: hanya model suara).
  - **M2M100, Opus-MT, Argos** (tambahan, memakai pustaka CTranslate2 yang sama dengan NLLB): dipilih di *Mesin terjemahan offline*, modelnya diunduh di Model offline > *Terjemahan M2M100, Opus-MT dan Argos*. **M2M100 418M** (repo `jncraton/m2m100_418M-ct2-int8`, ±490 MB, ±100 bahasa langsung). **Opus-MT** (repo `manancode/opus-mt-*-ctranslate2-android`, ±75 MB per arah: ja→en, en→ja, en→id, id→en, zh→en, en→zh) dan **Argos** (paket `.argosmodel` dari indeks resmi argospm-index, per arah: ja/en/id/zh/ko). Model pasangan hanya satu arah; bila pasangan langsung tidak ada (mis. ja→id), teks diterjemahkan lewat Inggris asalkan kedua arahnya (ja→en dan en→id) terpasang. Bila model belum terpasang, memakai Cadangan terjemahan online. Kode: `OfflineMt.kt` (Kotlin) dan fungsi `MtNative_*` di `native/nllb/nllb_jni.cpp`.
  - **Build**: mesin NLLB adalah kode native (CTranslate2 + sentencepiece + JNI di `native/`). Langkah "Bangun mesin terjemahan NLLB-200" di GitHub Actions (`native/build_nllb.sh`) membangunnya untuk arm64-v8a dan menaruhnya di `app/src/main/jniLibs/`. Langkah ini `continue-on-error`: jika gagal, APK tetap jadi, dan opsi NLLB tampil "tidak tersedia di build ini" (ML Kit tetap jalan). Hanya arm64; perangkat 32-bit tidak mendukung NLLB. Android Studio: jalankan `bash native/build_nllb.sh` dulu (butuh NDK, cmake, ninja, git).
- **Teks langsung (hasil sementara)**: di mode Offline dan Hibrid, teks asli dan terjemahan mulai muncul **saat orang masih bicara**, bukan menunggu kalimat selesai. Selama kalimat berjalan, `stt.js` mengirim cuplikan sejauh ini tiap ±0,6 dtk (`AlSTT.partial`); Kotlin mengenalinya di thread terpisah (`sttLivePool`, `OfflineAsr.transcribeLive`) dan menampilkannya dengan teks agak pudar. Saat jeda bicara terdeteksi, hasil final menggantikannya dan teks menjadi tegas. Aturan: hanya satu decode sementara dalam satu waktu dan hanya saat tidak ada potongan final yang antre (final selalu didahulukan, jadi tidak ada antrean menumpuk); berlaku untuk model ringan (SenseVoice, Zipformer/transducer, CTC, Moonshine), tidak untuk Whisper, FunASR Nano, dan Qwen3-ASR. Terjemahan sementara memakai Google Translate (Hibrid) atau ML Kit (Offline); NLLB/M2M100/Opus-MT/Argos baru menerjemahkan saat kalimat final. Mode Online (Whisper cloud) tidak memakainya karena tiap cuplikan memakan kuota permintaan. Matikan di Pengaturan > Play > Terjemahan Suara > Teks langsung bila ingin hemat baterai.
- **Cadangan terjemahan online**: jika model terjemahan belum diunduh, pakai Google Translate online (bisa dimatikan supaya benar-benar offline).
- **Catatan**: Whisper memproses jendela 30 detik, jadi `small` lambat di HP kelas menengah ke bawah; untuk subtitle terasa langsung pakai SenseVoice atau Whisper tiny/base. Model dimuat ke RAM saat Terjemahan Suara dinyalakan dan dilepas saat dimatikan. ML Kit membutuhkan Google Play Services untuk mengunduh model terjemahan.
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

## Ikon baru dan Tampilan terjemahan suara (v91)

- **Ikon aplikasi** diganti dengan `Alethra-icon.png` (ikon "A" gradasi). Android 8+ memakai ikon adaptif (`drawable-anydpi-v26/ic_launcher.xml`), versi lama memakai PNG biasa (`drawable-nodpi/ic_launcher.png`).
- Menu **Suara ke teks** diganti nama menjadi **Terjemahan Suara** (menu ⋮ dan Pengaturan > Play).
- Bagian **Tampilan terjemahan suara** (menggantikan "Teks suara di layar penuh") punya dua tombol: **Layar penuh** dan **Di luar layar penuh**. Tiap tombol menampilkan pengaturannya sendiri:
  - **Ukuran teks**, **Warna teks**, **Warna latar** (24 warna siap pakai atau kode hex sendiri), dan **Transparansi latar** 0–100% (0% = pekat, 100% = tembus penuh).
  - Khusus layar penuh: **Posisi** dan **Tampilkan teks asli**.
  - **Atur ulang tampilan** mengembalikan mode yang sedang dipilih ke bawaan. Perubahan pada kotak popup langsung terlihat saat Terjemahan Suara sedang berjalan.

## Riwayat Terjemahan Suara dan Tampilan terpisah (v92)

### Riwayat terjemahan
- Tiap kalimat final Terjemahan Suara dicatat (teks asli, terjemahan, dan posisi video). Teks tidak lagi hilang saat kotak popup berganti kalimat.
- Ketuk ikon **riwayat** di kepala kotak popup (atau Pengaturan > Play > Terjemahan Suara > Riwayat terjemahan) untuk membuka daftar. Ketuk satu baris untuk menyalinnya. Tombol **Salin**, **.txt**, **.srt**, dan **Bagikan** memuat seluruh riwayat. **Isi** mengatur isi ekspor: terjemahan saja, teks asli saja, atau keduanya.
- **.srt** memakai posisi video (`currentTime`) yang dikirim `stt.js` lewat `chunkT` (Whisper/Sherpa) dan `pcmT` (Vosk). Bila tidak tersedia, dipakai waktu sejak Terjemahan Suara dinyalakan (kurang akurat bila video dijeda atau digeser).
- Riwayat ikut tersimpan ke `filesDir/stt_log.jsonl`, jadi masih bisa dibuka setelah aplikasi ditutup. Menyalakan Terjemahan Suara lagi memulai sesi baru dan mengganti riwayat sebelumnya.
- Tab penyamaran: riwayat hanya di memori dan dibuang saat Terjemahan Suara dimatikan. Saklar **Simpan riwayat terjemahan** mematikan pencatatan. Riwayat ikut terhapus oleh **Hapus data penjelajahan** dan tidak ikut ekspor pengaturan.

### Tampilan terpisah (split view)
- Menu ⋮ > **Tampilan terpisah** (atau ikon di kotak popup Terjemahan Suara) membagi layar jadi dua. Panel utama tetap tab aktif (letakkan video di sini dan nyalakan Terjemahan Suara). Panel kedua bisa berisi:
  - **Catatan dan riwayat suara**: daftar hasil terjemahan yang bergulir otomatis dan kolom catatan. Ketuk baris untuk menyisipkannya ke catatan, tahan untuk menyalin. Catatan tersimpan otomatis, bisa disalin atau disimpan .txt.
  - **Tab lain** (mis. chat atau situs catatan), atau **Tab baru**.
- Layar tegak: atas-bawah. Layar mendatar: kiri-kanan. Tombol di kepala panel kedua: **Tukar panel**, **Ubah susunan**, **Ganti isi panel**, **Tutup**. Garis pembatas bisa digeser (20–80%).
- Omnibox, tombol navigasi, dan Terjemahan Suara bekerja untuk panel utama. Gunakan **Tukar panel** untuk menjadikan tab di panel kedua sebagai panel utama (Terjemahan Suara harus dimatikan dulu).
- Halaman Pengaturan, Riwayat, dan sejenisnya tetap terbuka di panel utama.
- **Jendela ganda Android** (layar dibagi dengan aplikasi lain): Terjemahan Suara dan video tidak lagi dihentikan saat jendela Alethra kehilangan fokus selama masih dalam mode jendela ganda. Menekan Home tetap menghentikannya seperti biasa.

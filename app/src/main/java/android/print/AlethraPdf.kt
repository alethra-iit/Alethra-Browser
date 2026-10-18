package android.print

import android.os.CancellationSignal
import android.os.ParcelFileDescriptor
import java.io.File
import java.util.concurrent.atomic.AtomicBoolean

/**
 * Menulis isi PrintDocumentAdapter (mis. dari WebView) langsung ke berkas PDF tanpa dialog cetak.
 * Berada di paket android.print karena callback-nya hanya bisa dibuat dari paket itu.
 * Jika perangkat menolak, [done] dipanggil dengan false dan pemanggil memakai cadangan.
 */
object AlethraPdf {
    fun write(adapter: PrintDocumentAdapter, file: File, done: (Boolean) -> Unit) {
        val fired = AtomicBoolean(false)
        fun end(ok: Boolean) { if (fired.compareAndSet(false, true)) done(ok) }
        try {
            val attrs = PrintAttributes.Builder()
                .setMediaSize(PrintAttributes.MediaSize.ISO_A4)
                .setResolution(PrintAttributes.Resolution("pdf", "pdf", 300, 300))
                .setMinMargins(PrintAttributes.Margins.NO_MARGINS)
                .build()
            adapter.onStart()
            adapter.onLayout(null, attrs, CancellationSignal(), object : PrintDocumentAdapter.LayoutResultCallback() {
                override fun onLayoutFinished(info: PrintDocumentInfo?, changed: Boolean) {
                    var pfd: ParcelFileDescriptor? = null
                    try {
                        val out = ParcelFileDescriptor.open(file,
                            ParcelFileDescriptor.MODE_READ_WRITE or ParcelFileDescriptor.MODE_CREATE or ParcelFileDescriptor.MODE_TRUNCATE)
                        pfd = out
                        adapter.onWrite(arrayOf(PageRange.ALL_PAGES), out, CancellationSignal(), object : PrintDocumentAdapter.WriteResultCallback() {
                            override fun onWriteFinished(pages: Array<out PageRange>?) {
                                try { out.close() } catch (e: Exception) {}
                                try { adapter.onFinish() } catch (e: Exception) {}
                                end(true)
                            }
                            override fun onWriteFailed(error: CharSequence?) {
                                try { out.close() } catch (e: Exception) {}
                                try { adapter.onFinish() } catch (e: Exception) {}
                                end(false)
                            }
                            override fun onWriteCancelled() {
                                try { out.close() } catch (e: Exception) {}
                                end(false)
                            }
                        })
                    } catch (t: Throwable) {
                        try { pfd?.close() } catch (e: Exception) {}
                        end(false)
                    }
                }
                override fun onLayoutFailed(error: CharSequence?) { end(false) }
                override fun onLayoutCancelled() { end(false) }
            }, null)
        } catch (t: Throwable) {
            end(false)
        }
    }
}

package com.alethra.browser

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.security.keystore.KeyGenParameterSpec
import android.security.keystore.KeyProperties
import android.util.Base64
import android.view.View
import android.webkit.WebView
import com.alethra.browser.MainActivity.Companion.MP
import com.alethra.browser.MainActivity.Companion.WC
import org.json.JSONArray
import org.json.JSONObject
import java.security.KeyStore
import javax.crypto.Cipher
import javax.crypto.KeyGenerator
import javax.crypto.SecretKey
import javax.crypto.spec.GCMParameterSpec

/*
 * Kata sandi tersimpan: enkripsi AES-GCM (Android Keystore), simpan/perbarui/hapus,
 * isi otomatis, dan halaman daftar. Dipisah dari MainActivity.kt sebagai extension
 * atas MainActivity; isi dan perilakunya tidak berubah.
 */

internal fun MainActivity.pwKey(): SecretKey {
    val ks = KeyStore.getInstance("AndroidKeyStore"); ks.load(null)
    (ks.getKey("alethra_pw", null) as? SecretKey)?.let { return it }
    val kg = KeyGenerator.getInstance(KeyProperties.KEY_ALGORITHM_AES, "AndroidKeyStore")
    kg.init(KeyGenParameterSpec.Builder("alethra_pw", KeyProperties.PURPOSE_ENCRYPT or KeyProperties.PURPOSE_DECRYPT)
        .setBlockModes(KeyProperties.BLOCK_MODE_GCM)
        .setEncryptionPaddings(KeyProperties.ENCRYPTION_PADDING_NONE)
        .setKeySize(256).build())
    return kg.generateKey()
}

internal fun MainActivity.pwEnc(plain: String): String {
    val c = Cipher.getInstance("AES/GCM/NoPadding")
    c.init(Cipher.ENCRYPT_MODE, pwKey())
    val ct = c.doFinal(plain.toByteArray(Charsets.UTF_8))
    return Base64.encodeToString(c.iv + ct, Base64.NO_WRAP)
}

internal fun MainActivity.pwDec(s: String): String? = try {
    val b = Base64.decode(s, Base64.NO_WRAP)
    val c = Cipher.getInstance("AES/GCM/NoPadding")
    c.init(Cipher.DECRYPT_MODE, pwKey(), GCMParameterSpec(128, b.copyOfRange(0, 12)))
    String(c.doFinal(b, 12, b.size - 12), Charsets.UTF_8)
} catch (e: Exception) { null }

internal fun MainActivity.pwList(): JSONArray = getList("pw")

internal fun MainActivity.pwFind(h: String, user: String): JSONObject? {
    val a = pwList()
    for (i in 0 until a.length()) {
        val o = a.optJSONObject(i) ?: continue
        if (o.optString("h") == h && o.optString("u") == user) return o
    }
    return null
}

internal fun MainActivity.pwPut(h: String, user: String, pass: String) {
    try {
        val old = pwList()
        val n = JSONArray()
        n.put(JSONObject().put("h", h).put("u", user).put("p", pwEnc(pass)).put("ts", System.currentTimeMillis()))
        for (i in 0 until old.length()) {
            val o = old.optJSONObject(i) ?: continue
            if (o.optString("h") == h && o.optString("u") == user) continue
            if (n.length() < 500) n.put(o)
        }
        prefs.edit().putString("pw", n.toString()).apply()
        toast("Kata sandi disimpan")
    } catch (e: Exception) { toast("Gagal menyimpan kata sandi") }
}

internal fun MainActivity.pwDelete(h: String, user: String) {
    val old = pwList()
    val n = JSONArray()
    for (i in 0 until old.length()) {
        val o = old.optJSONObject(i) ?: continue
        if (o.optString("h") == h && o.optString("u") == user) continue
        n.put(o)
    }
    prefs.edit().putString("pw", n.toString()).apply()
}

internal fun MainActivity.offerSavePw(h: String, user: String, pass: String) {
    if (incognito || !bool("pwsave", true) || isFinishing || pwDlg) return
    val ex = pwFind(h, user)
    if (ex != null && pwDec(ex.optString("p")) == pass) return
    pwDlg = true
    val update = ex != null
    dlg().setTitle(if (update) "Perbarui kata sandi?" else "Simpan kata sandi?")
        .setMessage((if (user.isEmpty()) "" else user + "\n") + h)
        .setPositiveButton(if (update) "Perbarui" else "Simpan") { _, _ -> pwPut(h, user, pass) }
        .setNegativeButton("Jangan", null)
        .setOnDismissListener { pwDlg = false }
        .show()
}

internal fun MainActivity.pwAutofill(w: WebView, url: String) {
    if (!bool("pwfill", true) || tabs.none { it.web === w }) return
    if (partLocked("lockpw") && !authFresh()) return
    val h = hostOf(url) ?: return
    if (hostOf(w.url) != h) return
    val a = pwList()
    for (i in 0 until a.length()) {
        val o = a.optJSONObject(i) ?: continue
        if (o.optString("h") != h) continue
        val p = pwDec(o.optString("p")) ?: continue
        w.evaluateJavascript("window.__alFill&&window.__alFill(" + JSONObject.quote(o.optString("u")) + "," + JSONObject.quote(p) + ")", null)
        return
    }
}

internal fun MainActivity.injectPw(w: WebView, url: String) {
    if (!url.startsWith("http")) return
    w.evaluateJavascript(PW_JS, null)
    pwAutofill(w, url)
    handler.postDelayed({ pwAutofill(w, url) }, 800)
    handler.postDelayed({ pwAutofill(w, url) }, 2500)
}

internal fun MainActivity.pagePwList(): View {
    val clr = iconBtn(R.drawable.ic_trash) {
        if (pwList().length() > 0) dlg().setMessage("Hapus semua kata sandi tersimpan?")
            .setPositiveButton("Hapus") { _, _ -> prefs.edit().remove("pw").apply(); renderPage() }
            .setNegativeButton("Batal", null).show()
    }
    val (page, body) = shell("Kata sandi tersimpan", true, listOf(clr))
    val a = pwList()
    if (a.length() == 0) { body.addView(emptyState("Belum ada kata sandi tersimpan")); return page }
    for (i in 0 until a.length()) {
        val o = a.optJSONObject(i) ?: continue
        val h = o.optString("h"); val u = o.optString("u")
        val row = listRow(favTile(h, "https://" + h, null, cLine), h, if (u.isEmpty()) "(tanpa nama pengguna)" else u, null, onClick = {
            dlg().setTitle(h).setItems(arrayOf("Salin nama pengguna", "Salin kata sandi", "Hapus")) { _, w ->
                val cm = getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                when (w) {
                    0 -> { cm.setPrimaryClip(ClipData.newPlainText("user", u)); toast("Nama pengguna disalin") }
                    1 -> { val p = pwDec(o.optString("p")); if (p != null) { cm.setPrimaryClip(ClipData.newPlainText("pass", p)); toast("Kata sandi disalin") } else toast("Tidak bisa membaca kata sandi") }
                    else -> { pwDelete(h, u); renderPage() }
                }
            }.show()
        })
        row.background = shape(cSurf, 16)
        body.addView(row, lp(MP, WC).apply { setMargins(dp(16), dp(5), dp(16), dp(5)) })
    }
    return page
}

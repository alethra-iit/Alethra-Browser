package com.alethra.browser

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.app.AlertDialog
import android.app.DownloadManager
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.content.res.ColorStateList
import android.content.res.Configuration
import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.os.Message
import android.text.InputType
import android.text.TextUtils
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.webkit.CookieManager
import android.webkit.URLUtil
import android.webkit.ValueCallback
import android.webkit.WebChromeClient
import android.webkit.WebResourceError
import android.webkit.WebResourceRequest
import android.webkit.WebSettings
import android.webkit.WebStorage
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.EditText
import android.widget.FrameLayout
import android.widget.LinearLayout
import android.widget.PopupMenu
import android.widget.ProgressBar
import android.widget.ScrollView
import android.widget.TextView
import android.widget.Toast
import org.json.JSONArray
import org.json.JSONObject
import java.net.URLEncoder

@Suppress("DEPRECATION")
class MainActivity : Activity() {

    companion object {
        const val HOME = "file:///android_asset/home.html"
        const val SEARCH = "https://www.google.com/search?q="
        const val FILE_REQ = 7
    }

    class Tab(val web: WebView, var title: String = "Tab baru", var url: String = HOME)

    private val tabs = mutableListOf<Tab>()
    private var cur = 0
    private lateinit var prefs: SharedPreferences
    private lateinit var omnibox: EditText
    private lateinit var progress: ProgressBar
    private lateinit var container: FrameLayout
    private lateinit var tabsBtn: TextView
    private var fileCb: ValueCallback<Array<Uri>>? = null
    private var customView: View? = null
    private var customCb: WebChromeClient.CustomViewCallback? = null
    private var defaultUa = ""
    private var dark = false
    private var fg = Color.BLACK
    private var bar = Color.WHITE
    private var uiFlags = 0
    private val accent = Color.parseColor("#4285F4")

    private fun dp(v: Int) = (v * resources.displayMetrics.density).toInt()
    private fun toast(m: String) = Toast.makeText(this, m, Toast.LENGTH_SHORT).show()
    private fun cw() = tabs[cur].web
    private fun tabOf(v: WebView?) = tabs.firstOrNull { it.web === v }
    private fun isCur(v: WebView?) = tabs.getOrNull(cur)?.web === v

    override fun onCreate(b: Bundle?) {
        super.onCreate(b)
        prefs = getSharedPreferences("alethra", MODE_PRIVATE)
        dark = (resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK) == Configuration.UI_MODE_NIGHT_YES
        bar = if (dark) Color.parseColor("#1C1F28") else Color.WHITE
        fg = if (dark) Color.parseColor("#E8EAF0") else Color.parseColor("#1F2430")
        window.statusBarColor = bar
        window.navigationBarColor = bar
        if (!dark && Build.VERSION.SDK_INT >= 26) {
            uiFlags = View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR or View.SYSTEM_UI_FLAG_LIGHT_NAVIGATION_BAR
            window.decorView.systemUiVisibility = uiFlags
        }

        val root = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setBackgroundColor(bar)
            fitsSystemWindows = true
        }
        val top = LinearLayout(this).apply {
            orientation = LinearLayout.HORIZONTAL
            gravity = Gravity.CENTER_VERTICAL
            setPadding(dp(8), dp(6), dp(4), dp(6))
        }
        omnibox = EditText(this).apply {
            hint = "Cari di Google atau ketik alamat"
            isSingleLine = true
            textSize = 15f
            setTextColor(fg)
            setHintTextColor(Color.GRAY)
            setSelectAllOnFocus(true)
            imeOptions = EditorInfo.IME_ACTION_GO
            inputType = InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_URI
            background = GradientDrawable().apply {
                cornerRadius = dp(22).toFloat()
                setColor(if (dark) Color.parseColor("#2A2E3A") else Color.parseColor("#F1F3F8"))
            }
            setPadding(dp(16), dp(10), dp(16), dp(10))
            setOnEditorActionListener { v, id, _ ->
                if (id == EditorInfo.IME_ACTION_GO || id == EditorInfo.IME_NULL) {
                    go(v.text.toString()); hideKb(); true
                } else false
            }
        }
        tabsBtn = TextView(this).apply {
            text = "1"; textSize = 13f; setTextColor(fg); gravity = Gravity.CENTER
            background = GradientDrawable().apply { setStroke(dp(2), fg); cornerRadius = dp(6).toFloat() }
            setOnClickListener { showTabs() }
        }
        top.addView(omnibox, LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1f))
        top.addView(tabsBtn, LinearLayout.LayoutParams(dp(30), dp(30)).apply { leftMargin = dp(10) })
        top.addView(btn("⋮") { showMenu(it) })
        root.addView(top)

        progress = ProgressBar(this, null, android.R.attr.progressBarStyleHorizontal).apply {
            max = 100
            progressTintList = ColorStateList.valueOf(accent)
            visibility = View.GONE
        }
        root.addView(progress, LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, dp(3)))

        container = FrameLayout(this)
        root.addView(container, LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 0, 1f))

        val bottom = LinearLayout(this).apply { orientation = LinearLayout.HORIZONTAL }
        val items = listOf<Pair<String, () -> Unit>>(
            "◀" to { if (cw().canGoBack()) cw().goBack() },
            "▶" to { if (cw().canGoForward()) cw().goForward() },
            "⌂" to { cw().loadUrl(HOME) },
            "⟳" to { cw().reload() },
            "☆" to { addBookmark() }
        )
        items.forEach { (label, action) ->
            bottom.addView(btn(label) { action() }, LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1f))
        }
        root.addView(bottom)
        setContentView(root)

        CookieManager.getInstance().setAcceptCookie(true)
        defaultUa = WebSettings.getDefaultUserAgent(this)
        newTab(intent?.dataString ?: HOME)
    }

    private fun btn(label: String, onClick: (View) -> Unit) = TextView(this).apply {
        text = label; textSize = 20f; setTextColor(fg); gravity = Gravity.CENTER
        setPadding(dp(14), dp(10), dp(14), dp(10)); isClickable = true
        setOnClickListener { v -> onClick(v) }
    }

    private fun hideKb() {
        (getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager).hideSoftInputFromWindow(omnibox.windowToken, 0)
        omnibox.clearFocus()
    }

    private fun toUrl(input: String): String {
        val t = input.trim()
        if (t.isEmpty()) return HOME
        if (Regex("^[a-zA-Z][a-zA-Z0-9+.-]*://.*").matches(t) || t.startsWith("about:") || t.startsWith("file:")) return t
        if (!t.contains(" ")) {
            if (Regex("^(www\\.)?([a-z0-9-]+\\.)+[a-z]{2,}(:\\d+)?([/?#].*)?$", RegexOption.IGNORE_CASE).matches(t)) return "https://" + t
            if (Regex("^(localhost|\\d{1,3}(\\.\\d{1,3}){3})(:\\d+)?([/?#].*)?$").matches(t)) return "http://" + t
        }
        return SEARCH + URLEncoder.encode(t, "UTF-8")
    }

    private fun go(input: String) = cw().loadUrl(toUrl(input))

    private fun setUrlText(u: String) {
        if (omnibox.hasFocus()) return
        omnibox.setText(if (u.startsWith("file:///android_asset")) "" else u)
    }

    private fun desktopUa() = defaultUa
        .replace(Regex("\\(Linux; Android[^)]*\\)"), "(X11; Linux x86_64)")
        .replace("Version/4.0 ", "").replace(" Mobile", "")

    @Suppress("DEPRECATION")
    private fun applySite(w: WebView) {
        val s = w.settings
        s.userAgentString = if (prefs.getBoolean("desktop", false)) desktopUa() else defaultUa
        if (Build.VERSION.SDK_INT >= 29) {
            s.forceDark = if (prefs.getBoolean("dark", false)) WebSettings.FORCE_DARK_ON else WebSettings.FORCE_DARK_OFF
        }
    }

    @SuppressLint("SetJavaScriptEnabled")
    private fun makeWeb(): WebView {
        val w = WebView(this)
        w.settings.apply {
            javaScriptEnabled = true
            domStorageEnabled = true
            builtInZoomControls = true
            displayZoomControls = false
            loadWithOverviewMode = true
            useWideViewPort = true
            mediaPlaybackRequiresUserGesture = false
            setSupportMultipleWindows(true)
            javaScriptCanOpenWindowsAutomatically = true
            mixedContentMode = WebSettings.MIXED_CONTENT_COMPATIBILITY_MODE
            textZoom = prefs.getInt("zoom", 100)
        }
        CookieManager.getInstance().setAcceptThirdPartyCookies(w, true)
        applySite(w)

        w.webViewClient = object : WebViewClient() {
            override fun shouldOverrideUrlLoading(v: WebView?, r: WebResourceRequest?): Boolean {
                val u = r?.url?.toString() ?: return false
                if (u.startsWith("http://") || u.startsWith("https://") || u.startsWith("file:") ||
                    u.startsWith("about:") || u.startsWith("data:") || u.startsWith("blob:")) return false
                try {
                    val i = if (u.startsWith("intent:")) Intent.parseUri(u, Intent.URI_INTENT_SCHEME)
                    else Intent(Intent.ACTION_VIEW, Uri.parse(u))
                    i.addCategory(Intent.CATEGORY_BROWSABLE); i.component = null; i.selector = null
                    startActivity(i)
                } catch (e: Exception) {
                    toast("Tidak ada aplikasi untuk membuka tautan ini")
                }
                return true
            }

            override fun onPageStarted(v: WebView?, url: String?, favicon: Bitmap?) {
                val t = tabOf(v) ?: return
                t.url = url ?: t.url
                if (isCur(v)) setUrlText(t.url)
            }

            override fun onPageFinished(v: WebView?, url: String?) {
                val t = tabOf(v) ?: return
                t.url = url ?: t.url
                t.title = v?.title ?: t.url
                if (!t.url.startsWith("file:///android_asset") && !t.url.startsWith("data:") && t.url != "about:blank") addHistory(t.title, t.url)
                if (isCur(v)) setUrlText(t.url)
            }

            override fun onReceivedError(v: WebView?, r: WebResourceRequest?, e: WebResourceError?) {
                if (r?.isForMainFrame != true || v == null) return
                val u = r.url.toString()
                val html = "<meta name='viewport' content='width=device-width'><body style='font-family:sans-serif;padding:24px'>" +
                    "<h2>Tidak bisa membuka halaman</h2><p>" + TextUtils.htmlEncode(u) + "</p><p>" +
                    TextUtils.htmlEncode(e?.description?.toString() ?: "") + "</p><button onclick='location.reload()'>Coba lagi</button>"
                v.loadDataWithBaseURL(u, html, "text/html", "UTF-8", u)
            }
        }

        w.webChromeClient = object : WebChromeClient() {
            override fun onProgressChanged(v: WebView?, p: Int) {
                if (isCur(v)) {
                    progress.progress = p
                    progress.visibility = if (p in 1..99) View.VISIBLE else View.GONE
                }
            }

            override fun onReceivedTitle(v: WebView?, t: String?) {
                tabOf(v)?.title = t ?: ""
            }

            override fun onCreateWindow(v: WebView?, isDialog: Boolean, isUserGesture: Boolean, resultMsg: Message?): Boolean {
                val msg = resultMsg ?: return false
                val t = newTab(null)
                (msg.obj as WebView.WebViewTransport).webView = t.web
                msg.sendToTarget()
                return true
            }

            override fun onShowCustomView(view: View?, callback: WebChromeClient.CustomViewCallback?) {
                if (view == null) return
                if (customView != null) { callback?.onCustomViewHidden(); return }
                customView = view; customCb = callback
                (window.decorView as FrameLayout).addView(view, FrameLayout.LayoutParams(-1, -1))
                window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_FULLSCREEN or
                    View.SYSTEM_UI_FLAG_HIDE_NAVIGATION or View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
            }

            override fun onHideCustomView() = hideCustom()

            override fun onShowFileChooser(v: WebView?, cb: ValueCallback<Array<Uri>>?, p: WebChromeClient.FileChooserParams?): Boolean {
                if (cb == null || p == null) return false
                fileCb?.onReceiveValue(null)
                fileCb = cb
                return try { startActivityForResult(p.createIntent(), FILE_REQ); true } catch (e: Exception) { fileCb = null; false }
            }
        }

        w.setDownloadListener { url, ua, cd, mime, _ -> download(url, ua, cd, mime) }

        w.setOnLongClickListener {
            val r = w.hitTestResult
            val ex = r.extra
            val ok = r.type == WebView.HitTestResult.SRC_ANCHOR_TYPE ||
                r.type == WebView.HitTestResult.SRC_IMAGE_ANCHOR_TYPE ||
                r.type == WebView.HitTestResult.IMAGE_TYPE
            if (ok && ex != null) {
                AlertDialog.Builder(this).setTitle(ex.take(60))
                    .setItems(arrayOf("Buka di tab baru", "Salin alamat", "Bagikan")) { _, i ->
                        when (i) {
                            0 -> { newTab(ex) }
                            1 -> {
                                (getSystemService(CLIPBOARD_SERVICE) as ClipboardManager).setPrimaryClip(ClipData.newPlainText("url", ex))
                                toast("Alamat disalin")
                            }
                            else -> share(ex)
                        }
                    }.show()
                true
            } else false
        }
        return w
    }

    private fun hideCustom() {
        customView?.let { (window.decorView as FrameLayout).removeView(it) }
        customView = null
        customCb?.onCustomViewHidden()
        customCb = null
        window.decorView.systemUiVisibility = uiFlags
    }

    private fun download(url: String, ua: String?, cd: String?, mime: String?) {
        if (!url.startsWith("http")) { toast("Jenis unduhan ini belum didukung"); return }
        if (Build.VERSION.SDK_INT < 29 && checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE), 9)
            toast("Izinkan penyimpanan lalu coba unduh lagi")
            return
        }
        try {
            val name = URLUtil.guessFileName(url, cd, mime)
            val r = DownloadManager.Request(Uri.parse(url)).apply {
                setMimeType(mime)
                addRequestHeader("User-Agent", ua ?: defaultUa)
                CookieManager.getInstance().getCookie(url)?.let { addRequestHeader("Cookie", it) }
                setTitle(name)
                setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)
                setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, name)
            }
            (getSystemService(DOWNLOAD_SERVICE) as DownloadManager).enqueue(r)
            toast("Mengunduh " + name)
        } catch (e: Exception) {
            toast("Gagal mengunduh")
        }
    }

    /* ---------- Tab ---------- */
    private fun newTab(url: String?, select: Boolean = true): Tab {
        val t = Tab(makeWeb())
        tabs.add(t)
        if (url != null) t.web.loadUrl(url)
        if (select) selectTab(tabs.size - 1) else updateTabs()
        return t
    }

    private fun selectTab(i: Int) {
        cur = i
        container.removeAllViews()
        val w = tabs[i].web
        (w.parent as? ViewGroup)?.removeView(w)
        container.addView(w, FrameLayout.LayoutParams(-1, -1))
        omnibox.clearFocus()
        setUrlText(tabs[i].url)
        progress.visibility = View.GONE
        updateTabs()
    }

    private fun updateTabs() { tabsBtn.text = tabs.size.toString() }

    private fun closeTab(i: Int) {
        val t = tabs.removeAt(i)
        container.removeView(t.web)
        t.web.stopLoading(); t.web.destroy()
        if (tabs.isEmpty()) { newTab(HOME); return }
        val n = if (i < cur) cur - 1 else if (i == cur) minOf(cur, tabs.size - 1) else cur
        selectTab(n)
    }

    private fun showTabs() {
        val list = LinearLayout(this).apply { orientation = LinearLayout.VERTICAL; setPadding(dp(16), dp(8), dp(16), dp(8)) }
        val holder = arrayOfNulls<AlertDialog>(1)
        tabs.forEachIndexed { i, t ->
            val row = LinearLayout(this).apply { orientation = LinearLayout.HORIZONTAL; gravity = Gravity.CENTER_VERTICAL }
            val title = TextView(this).apply {
                text = (if (i == cur) "● " else "○ ") + t.title.ifEmpty { t.url }
                textSize = 15f; maxLines = 1; ellipsize = TextUtils.TruncateAt.END
                setPadding(0, dp(12), 0, dp(12))
                setOnClickListener { selectTab(i); holder[0]?.dismiss() }
            }
            val x = TextView(this).apply {
                text = "✕"; textSize = 18f; setPadding(dp(14), dp(12), dp(4), dp(12))
                setOnClickListener { closeTab(i); holder[0]?.dismiss(); showTabs() }
            }
            row.addView(title, LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1f))
            row.addView(x)
            list.addView(row)
        }
        holder[0] = AlertDialog.Builder(this).setTitle("Tab (" + tabs.size + ")").setView(ScrollView(this).apply { addView(list) })
            .setPositiveButton("Tab baru") { _, _ -> newTab(HOME) }
            .setNegativeButton("Tutup", null).create()
        holder[0]?.show()
    }

    /* ---------- Bookmark & riwayat ---------- */
    private fun getList(key: String): JSONArray = try { JSONArray(prefs.getString(key, "[]")) } catch (e: Exception) { JSONArray() }

    private fun addBookmark() {
        val t = tabs[cur]
        if (t.url.startsWith("file:///android_asset")) { toast("Halaman ini tidak bisa di-bookmark"); return }
        val a = getList("bm")
        for (i in 0 until a.length()) if (a.getJSONObject(i).optString("u") == t.url) { toast("Sudah ada di bookmark"); return }
        a.put(JSONObject().put("t", t.title.ifEmpty { t.url }).put("u", t.url))
        prefs.edit().putString("bm", a.toString()).apply()
        toast("Bookmark ditambahkan")
    }

    private fun addHistory(t: String, u: String) {
        val old = getList("hist")
        val n = JSONArray()
        n.put(JSONObject().put("t", t.ifEmpty { u }).put("u", u))
        var i = 0
        while (i < old.length() && n.length() < 200) {
            val o = old.getJSONObject(i)
            if (o.optString("u") != u) n.put(o)
            i++
        }
        prefs.edit().putString("hist", n.toString()).apply()
    }

    private fun showList(key: String, title: String) {
        val a = getList(key)
        if (a.length() == 0) { toast("Masih kosong"); return }
        val names = Array(a.length()) { i -> a.getJSONObject(i).let { it.optString("t").ifEmpty { it.optString("u") } } }
        AlertDialog.Builder(this).setTitle(title)
            .setItems(names) { _, i -> go(a.getJSONObject(i).getString("u")) }
            .setNeutralButton("Hapus semua") { _, _ -> prefs.edit().remove(key).apply() }
            .setNegativeButton("Tutup", null).show()
    }

    /* ---------- Menu ---------- */
    private fun share(text: String) {
        val i = Intent(Intent.ACTION_SEND).setType("text/plain").putExtra(Intent.EXTRA_TEXT, text)
        startActivity(Intent.createChooser(i, "Bagikan"))
    }

    private fun showMenu(anchor: View) {
        val pm = PopupMenu(this, anchor)
        val m = pm.menu
        m.add(0, 1, 0, "Tab baru")
        m.add(0, 2, 0, "Tambah bookmark")
        m.add(0, 3, 0, "Bookmark")
        m.add(0, 4, 0, "Riwayat")
        m.add(0, 5, 0, "Mode desktop").apply { isCheckable = true; isChecked = prefs.getBoolean("desktop", false) }
        m.add(0, 6, 0, "Mode gelap situs").apply { isCheckable = true; isChecked = prefs.getBoolean("dark", false) }
        m.add(0, 7, 0, "Ukuran teks")
        m.add(0, 8, 0, "Cari di halaman")
        m.add(0, 9, 0, "Bagikan")
        m.add(0, 10, 0, "Hapus data penelusuran")
        pm.setOnMenuItemClickListener { i ->
            when (i.itemId) {
                1 -> { newTab(HOME) }
                2 -> addBookmark()
                3 -> showList("bm", "Bookmark")
                4 -> showList("hist", "Riwayat")
                5 -> {
                    prefs.edit().putBoolean("desktop", !prefs.getBoolean("desktop", false)).apply()
                    tabs.forEach { applySite(it.web) }; cw().reload()
                }
                6 -> {
                    prefs.edit().putBoolean("dark", !prefs.getBoolean("dark", false)).apply()
                    tabs.forEach { applySite(it.web) }
                }
                7 -> zoomDialog()
                8 -> findDialog()
                9 -> share(tabs[cur].url)
                10 -> clearDialog()
            }
            true
        }
        pm.show()
    }

    private fun zoomDialog() {
        val vals = intArrayOf(80, 100, 120, 150)
        AlertDialog.Builder(this).setTitle("Ukuran teks")
            .setItems(arrayOf("80%", "100%", "120%", "150%")) { _, i ->
                prefs.edit().putInt("zoom", vals[i]).apply()
                tabs.forEach { it.web.settings.textZoom = vals[i] }
            }.show()
    }

    private fun findDialog() {
        val e = EditText(this).apply { isSingleLine = true }
        AlertDialog.Builder(this).setTitle("Cari di halaman").setView(e)
            .setPositiveButton("Cari") { _, _ -> cw().findAllAsync(e.text.toString()) }
            .setNegativeButton("Hapus sorotan") { _, _ -> cw().clearMatches() }.show()
    }

    private fun clearDialog() {
        AlertDialog.Builder(this).setMessage("Hapus riwayat, cookie, dan cache?")
            .setPositiveButton("Hapus") { _, _ ->
                CookieManager.getInstance().removeAllCookies(null)
                WebStorage.getInstance().deleteAllData()
                tabs.forEach { it.web.clearCache(true); it.web.clearHistory() }
                prefs.edit().remove("hist").apply()
                toast("Data dihapus")
            }.setNegativeButton("Batal", null).show()
    }

    /* ---------- Siklus hidup ---------- */
    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        intent?.dataString?.let { newTab(it) }
    }

    override fun onActivityResult(rc: Int, res: Int, data: Intent?) {
        if (rc == FILE_REQ) {
            fileCb?.onReceiveValue(WebChromeClient.FileChooserParams.parseResult(res, data))
            fileCb = null
        } else super.onActivityResult(rc, res, data)
    }

    override fun onBackPressed() {
        when {
            customView != null -> hideCustom()
            cw().canGoBack() -> cw().goBack()
            tabs.size > 1 -> closeTab(cur)
            else -> super.onBackPressed()
        }
    }

    override fun onPause() { super.onPause(); tabs.getOrNull(cur)?.web?.onPause() }
    override fun onResume() { super.onResume(); tabs.getOrNull(cur)?.web?.onResume() }
    override fun onDestroy() { tabs.forEach { it.web.destroy() }; super.onDestroy() }
}

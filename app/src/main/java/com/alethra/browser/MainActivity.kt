package com.alethra.browser

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.app.AlertDialog
import android.app.Dialog
import android.app.DownloadManager
import android.app.PictureInPictureParams
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.content.res.ColorStateList
import android.content.res.Configuration
import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.Typeface
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.GradientDrawable
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.os.Handler
import android.os.Looper
import android.os.Message
import android.speech.RecognizerIntent
import android.text.Editable
import android.text.InputType
import android.util.Rational
import android.text.TextUtils
import android.text.TextWatcher
import android.text.format.Formatter
import android.view.ContextThemeWrapper
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.webkit.CookieManager
import android.webkit.JavascriptInterface
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
import android.widget.HorizontalScrollView
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.PopupMenu
import android.widget.ProgressBar
import android.widget.ScrollView
import android.widget.SeekBar
import android.widget.Switch
import android.widget.TextView
import android.widget.Toast
import org.json.JSONArray
import org.json.JSONObject
import java.net.HttpURLConnection
import java.net.URL
import java.net.URLEncoder
import java.util.concurrent.ArrayBlockingQueue
import java.util.concurrent.ThreadPoolExecutor
import java.util.concurrent.TimeUnit
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

@Suppress("DEPRECATION")
class MainActivity : Activity() {

    companion object {
        const val HOME = "file:///android_asset/home.html"
        const val ASSET = "file:///android_asset"
        const val FILE_REQ = 7
        const val VOICE_REQ = 8
        const val JS_PLAYING = "(function(){var v=document.querySelectorAll('video');for(var i=0;i<v.length;i++){if(!v[i].paused&&!v[i].ended&&v[i].readyState>2)return 1}return 0})()"
        const val JS_FILL = "(function(){var v=document.querySelectorAll('video'),t=null,i;for(i=0;i<v.length;i++){if(!v[i].paused){t=v[i];break}}if(!t)t=v[0];if(!t)return;var o=document.getElementById('__alpip');if(o)o.remove();var p=t;while(p){p.classList.add('__ala');p=p.parentElement}var s=document.createElement('style');s.id='__alpip';s.textContent='html,body{overflow:hidden!important;background:#000!important}body *{visibility:hidden!important}.__ala{transform:none!important;contain:none!important;filter:none!important;perspective:none!important;will-change:auto!important;clip-path:none!important;backdrop-filter:none!important}video.__alv{visibility:visible!important;position:fixed!important;left:0!important;top:0!important;right:0!important;bottom:0!important;width:100vw!important;height:100vh!important;max-width:none!important;max-height:none!important;margin:0!important;z-index:2147483647!important;background:#000!important;object-fit:contain!important}';document.head.appendChild(s);t.classList.add('__alv')})()"
        const val JS_RESTORE = "(function(){var s=document.getElementById('__alpip');if(s)s.remove();var a=document.querySelectorAll('.__ala,.__alv');for(var i=0;i<a.length;i++){a[i].classList.remove('__ala');a[i].classList.remove('__alv')}})()"
        const val JS_PAUSE = "(function(){var v=document.querySelectorAll('video');for(var i=0;i<v.length;i++)v[i].pause()})()"
        const val MP = ViewGroup.LayoutParams.MATCH_PARENT
        const val WC = ViewGroup.LayoutParams.WRAP_CONTENT
    }

    class Tab(val web: WebView, var title: String = "Tab baru", var url: String = HOME, var icon: Bitmap? = null)
    class Closed(val title: String, val url: String)
    class Dl(val id: Long, val name: String, val status: Int, val total: Long, val sofar: Long, val mime: String?)

    private val tabs = mutableListOf<Tab>()
    private val closed = mutableListOf<Closed>()
    private var cur = 0
    private lateinit var prefs: SharedPreferences
    private lateinit var omnibox: EditText
    private lateinit var progress: ProgressBar
    private lateinit var container: FrameLayout
    private lateinit var root: LinearLayout
    private lateinit var top: LinearLayout
    private lateinit var nav: LinearLayout
    private val handler = Handler(Looper.getMainLooper())
    private var fileCb: ValueCallback<Array<Uri>>? = null
    private var customView: View? = null
    private var customCb: WebChromeClient.CustomViewCallback? = null
    private var defaultUa = ""
    private var uiFlags = 0

    // Warna: hanya putih dan gelap
    private var dark = false
    private var cBg = Color.WHITE
    private var cSurf = Color.WHITE
    private var cFg = Color.BLACK
    private var cSub = Color.GRAY
    private var cLine = Color.LTGRAY

    // Navigasi halaman
    private val pages = mutableListOf<String>()
    private var pageView: View? = null
    private var incognito = false
    private var lineView: View? = null
    private lateinit var host: FrameLayout
    private var miniTab: Tab? = null
    private var miniFrame: FrameLayout? = null
    private var videoPlaying = false
    private var pipActive = false
    private var trJs: String? = null
    private val trPool = ThreadPoolExecutor(2, 2, 0L, TimeUnit.SECONDS, ArrayBlockingQueue<Runnable>(40), ThreadPoolExecutor.DiscardOldestPolicy())
    private val trLangs = arrayOf("id", "en", "ja", "ko", "zh-CN", "es", "ar", "hi")
    private val trNames = arrayOf("Indonesia", "Inggris", "Jepang", "Korea", "Mandarin", "Spanyol", "Arab", "Hindi")
    private val videoPoll = object : Runnable {
        override fun run() { pollVideo(); handler.postDelayed(this, 2000) }
    }
    private var tabFilter = 0
    private var dlFilter = 0
    private var bmFilter = 0
    private var bmQuery = ""
    private var histQuery = ""

    private val engineKeys = arrayOf("google", "bing", "ddg")
    private val engineNames = arrayOf("Google", "Bing", "DuckDuckGo")
    private val engineUrls = arrayOf("https://www.google.com/search?q=", "https://www.bing.com/search?q=", "https://duckduckgo.com/?q=")

    private fun dp(v: Int) = (v * resources.displayMetrics.density).toInt()
    private fun toast(m: String) = Toast.makeText(this, m, Toast.LENGTH_SHORT).show()
    private fun cw() = tabs[cur].web
    private fun tabOf(v: WebView?) = tabs.firstOrNull { it.web === v }
    private fun isCur(v: WebView?) = tabs.getOrNull(cur)?.web === v
    private fun onHome() = tabs.getOrNull(cur)?.url?.startsWith(ASSET) == true
    private fun bool(k: String, d: Boolean) = prefs.getBoolean(k, d)
    private fun setBool(k: String, v: Boolean) = prefs.edit().putBoolean(k, v).apply()
    private fun lp(w: Int, h: Int, wt: Float = 0f) = LinearLayout.LayoutParams(w, h, wt)
    private fun engineIdx() = engineKeys.indexOf(prefs.getString("engine", "google")).coerceAtLeast(0)

    /* ================= Siklus hidup ================= */

    override fun onCreate(b: Bundle?) {
        super.onCreate(b)
        prefs = getSharedPreferences("alethra", MODE_PRIVATE)

        root = LinearLayout(this).apply { orientation = LinearLayout.VERTICAL; fitsSystemWindows = true }
        top = LinearLayout(this).apply {
            orientation = LinearLayout.HORIZONTAL
            gravity = Gravity.CENTER_VERTICAL
            setPadding(dp(12), dp(8), dp(4), dp(8))
        }
        omnibox = EditText(this).apply {
            hint = "Cari atau masukkan alamat web"
            isSingleLine = true
            textSize = 15f
            background = null
            setPadding(0, dp(10), 0, dp(10))
            setSelectAllOnFocus(true)
            imeOptions = EditorInfo.IME_ACTION_GO
            inputType = InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_URI
            setOnEditorActionListener { v, id, _ ->
                if (id == EditorInfo.IME_ACTION_GO || id == EditorInfo.IME_NULL) {
                    go(v.text.toString()); hideKb(); true
                } else false
            }
        }
        progress = ProgressBar(this, null, android.R.attr.progressBarStyleHorizontal).apply {
            max = 100
            visibility = View.GONE
        }
        container = FrameLayout(this)
        host = FrameLayout(this)
        host.addView(container, FrameLayout.LayoutParams(MP, MP))
        nav = LinearLayout(this).apply { orientation = LinearLayout.HORIZONTAL }

        setContentView(root)
        CookieManager.getInstance().setAcceptCookie(true)
        defaultUa = WebSettings.getDefaultUserAgent(this)
        applyChrome()
        newTab(intent?.dataString ?: home())
    }

    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)
        if (prefs.getString("theme", "system") == "system") { applyChrome(); reloadHomes() }
    }

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        intent?.dataString?.let { pages.clear(); newTab(it) }
    }

    override fun onActivityResult(rc: Int, res: Int, data: Intent?) {
        when (rc) {
            FILE_REQ -> {
                fileCb?.onReceiveValue(WebChromeClient.FileChooserParams.parseResult(res, data))
                fileCb = null
            }
            VOICE_REQ -> {
                val r = if (res == RESULT_OK) data?.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS) else null
                val q = r?.firstOrNull()
                if (!q.isNullOrEmpty()) go(q)
            }
            else -> super.onActivityResult(rc, res, data)
        }
    }

    override fun onBackPressed() {
        when {
            customView != null -> hideCustom()
            pageView != null -> backPage()
            cw().canGoBack() -> cw().goBack()
            tabs.size > 1 -> closeTab(cur)
            else -> super.onBackPressed()
        }
    }

    override fun onPause() {
        super.onPause()
        handler.removeCallbacks(videoPoll)
        if (!pipActive) tabs.getOrNull(cur)?.web?.onPause()
    }

    override fun onResume() {
        super.onResume()
        tabs.getOrNull(cur)?.web?.onResume()
        handler.removeCallbacks(videoPoll)
        handler.postDelayed(videoPoll, 1000)
    }

    override fun onUserLeaveHint() {
        super.onUserLeaveHint()
        if (bool("pip", false) && pipMode() == "out" && (videoPlaying || customView != null)) enterPip()
    }

    override fun onPictureInPictureModeChanged(isInPictureInPictureMode: Boolean, newConfig: Configuration) {
        super.onPictureInPictureModeChanged(isInPictureInPictureMode, newConfig)
        pipActive = isInPictureInPictureMode
        val vis = if (isInPictureInPictureMode) View.GONE else View.VISIBLE
        nav.visibility = vis
        lineView?.visibility = vis
        val w = tabs.getOrNull(cur)?.web
        if (isInPictureInPictureMode) {
            top.visibility = View.GONE
            if (customView == null) w?.evaluateJavascript(JS_FILL, null)
        } else {
            w?.evaluateJavascript(JS_RESTORE, null)
            updateChrome()
        }
    }

    private fun pipMode() = prefs.getString("pipmode", "out") ?: "out"

    private fun autoMini(): Boolean {
        if (bool("pip", false) && pipMode() == "in" && videoPlaying && miniTab == null && tabs.isNotEmpty() && !onHome()) {
            startMini()
            return true
        }
        return false
    }

    private fun startMini() {
        if (miniTab != null || tabs.isEmpty()) return
        val t = tabs[cur]
        val idx = cur
        container.removeView(t.web)
        miniTab = t
        val f = FrameLayout(this).apply { background = shape(Color.BLACK, 14); elevation = dp(8).toFloat(); clipToOutline = true }
        f.addView(t.web, FrameLayout.LayoutParams(MP, MP))
        fun btn(res: Int, right: Int, action: () -> Unit) {
            val b = FrameLayout(this).apply { background = shape(Color.argb(170, 0, 0, 0), 14); isClickable = true }
            b.addView(icon(res, 14, Color.WHITE), FrameLayout.LayoutParams(dp(14), dp(14), Gravity.CENTER))
            b.setOnClickListener { action() }
            f.addView(b, FrameLayout.LayoutParams(dp(28), dp(28), Gravity.TOP or Gravity.END).apply { setMargins(0, dp(6), right, 0) })
        }
        btn(R.drawable.ic_close, dp(6)) { stopMini(true) }
        btn(R.drawable.ic_expand, dp(40)) { val k = tabs.indexOf(t); if (k >= 0) selectTab(k) }
        host.addView(f, FrameLayout.LayoutParams(dp(220), dp(124), Gravity.BOTTOM or Gravity.END).apply { setMargins(0, 0, dp(12), dp(12)) })
        miniFrame = f
        t.web.evaluateJavascript(JS_FILL, null)
        if (tabs.size == 1) newTab(home()) else selectTab(if (idx > 0) idx - 1 else idx + 1)
    }

    private fun stopMini(pause: Boolean) {
        val t = miniTab ?: return
        miniFrame?.let { it.removeAllViews(); host.removeView(it) }
        (t.web.parent as? ViewGroup)?.removeView(t.web)
        t.web.evaluateJavascript(JS_RESTORE, null)
        if (pause) t.web.evaluateJavascript(JS_PAUSE, null)
        miniTab = null
        miniFrame = null
    }

    private fun pollVideo() {
        if (!bool("pip", false) || tabs.isEmpty()) { videoPlaying = false; return }
        cw().evaluateJavascript(JS_PLAYING) { r -> videoPlaying = (r == "1") }
    }

    private fun enterPip(): Boolean {
        return try {
            pipActive = true
            if (Build.VERSION.SDK_INT >= 26) {
                enterPictureInPictureMode(PictureInPictureParams.Builder().setAspectRatio(Rational(16, 9)).build())
            } else {
                enterPictureInPictureMode()
                true
            }
        } catch (e: Exception) { pipActive = false; false }
    }
    override fun onDestroy() { tabs.forEach { it.web.destroy() }; super.onDestroy() }

    /* ================= Tema & kerangka ================= */

    private fun isDark(): Boolean = when (prefs.getString("theme", "system")) {
        "dark" -> true
        "light" -> false
        else -> (resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK) == Configuration.UI_MODE_NIGHT_YES
    }

    private fun dlg() = AlertDialog.Builder(this,
        if (dark) android.R.style.Theme_Material_Dialog_Alert else android.R.style.Theme_Material_Light_Dialog_Alert)

    private fun applyChrome() {
        dark = isDark()
        if (dark) {
            cBg = Color.parseColor("#0B0B0D"); cSurf = Color.parseColor("#17171A")
            cFg = Color.parseColor("#F4F4F5"); cSub = Color.parseColor("#8E8E96"); cLine = Color.parseColor("#26262A")
        } else {
            cBg = Color.WHITE; cSurf = Color.parseColor("#F4F4F6")
            cFg = Color.parseColor("#111113"); cSub = Color.parseColor("#6B6B73"); cLine = Color.parseColor("#E8E8EC")
        }
        window.statusBarColor = cBg
        window.navigationBarColor = cBg
        uiFlags = 0
        if (!dark) {
            if (Build.VERSION.SDK_INT >= 23) uiFlags = uiFlags or View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
            if (Build.VERSION.SDK_INT >= 26) uiFlags = uiFlags or View.SYSTEM_UI_FLAG_LIGHT_NAVIGATION_BAR
        }
        if (customView == null) window.decorView.systemUiVisibility = uiFlags
        root.setBackgroundColor(cBg)
        omnibox.setTextColor(cFg)
        omnibox.setHintTextColor(cSub)
        progress.progressTintList = ColorStateList.valueOf(cFg)
        buildTop()
        buildNav()

        val line = View(this).apply { setBackgroundColor(cLine) }
        lineView = line
        root.removeAllViews()
        if (bool("barBottom", false)) {
            root.addView(host, lp(MP, 0, 1f))
            root.addView(progress, lp(MP, dp(2)))
            root.addView(top, lp(MP, WC))
            root.addView(line, lp(MP, 1))
            root.addView(nav, lp(MP, WC))
        } else {
            root.addView(top, lp(MP, WC))
            root.addView(progress, lp(MP, dp(2)))
            root.addView(host, lp(MP, 0, 1f))
            root.addView(line, lp(MP, 1))
            root.addView(nav, lp(MP, WC))
        }
        if (tabs.isNotEmpty()) updateChrome()
        if (pageView != null) renderPage()
    }

    private fun buildTop() {
        top.removeAllViews()
        (omnibox.parent as? ViewGroup)?.removeView(omnibox)
        val pill = LinearLayout(this).apply {
            orientation = LinearLayout.HORIZONTAL
            gravity = Gravity.CENTER_VERTICAL
            background = shape(cSurf, 22)
            minimumHeight = dp(44)
            setPadding(dp(14), 0, dp(14), 0)
        }
        pill.addView(icon(R.drawable.ic_search, 18, cSub), lp(dp(18), dp(18)).apply { rightMargin = dp(10) })
        pill.addView(omnibox, lp(0, WC, 1f))
        top.addView(pill, lp(0, WC, 1f))
        top.addView(iconBtn(R.drawable.ic_more) { showPageMenu(it) }, lp(dp(44), dp(44)))
    }

    private fun navSelected(): Int = when (pages.firstOrNull()) {
        null -> if (onHome()) 0 else -1
        "tabs" -> 1
        "history" -> 2
        "downloads" -> 3
        else -> 4
    }

    private fun buildNav() {
        nav.removeAllViews()
        nav.setBackgroundColor(cBg)
        val sel = navSelected()
        val items = listOf(
            "Beranda" to R.drawable.ic_home, "Tab" to R.drawable.ic_tab, "Histori" to R.drawable.ic_history,
            "Unduhan" to R.drawable.ic_download, "Menu" to R.drawable.ic_menu)
        items.forEachIndexed { i, (label, res) ->
            val c = if (i == sel) cFg else cSub
            val col = LinearLayout(this).apply {
                orientation = LinearLayout.VERTICAL; gravity = Gravity.CENTER_HORIZONTAL
                setPadding(0, dp(9), 0, dp(9)); isClickable = true
            }
            col.addView(icon(res, 22, c), lp(dp(22), dp(22)))
            col.addView(TextView(this).apply {
                text = label; textSize = 10f; setTextColor(c); gravity = Gravity.CENTER
                if (i == sel) typeface = Typeface.DEFAULT_BOLD
            }, lp(WC, WC).apply { topMargin = dp(3) })
            col.setOnClickListener { navTap(i) }
            nav.addView(col, lp(0, WC, 1f))
        }
    }

    private fun navTap(i: Int) {
        when (i) {
            0 -> { val m = autoMini(); closePages(); if (!m && !onHome()) cw().loadUrl(home()) }
            1 -> openRoot("tabs")
            2 -> openRoot("history")
            3 -> openRoot("downloads")
            4 -> showMenuSheet()
        }
    }

    private fun updateChrome() {
        val showTop = pageView == null && !onHome() && !pipActive
        top.visibility = if (showTop) View.VISIBLE else View.GONE
        if (!showTop) progress.visibility = View.GONE
        buildNav()
    }

    private fun hideKb() {
        (getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager).hideSoftInputFromWindow(omnibox.windowToken, 0)
        omnibox.clearFocus()
    }

    /* ================= Pembantu tampilan ================= */

    private fun shape(color: Int, r: Int, strokeColor: Int = 0, strokeDp: Int = 0) = GradientDrawable().apply {
        setColor(color); cornerRadius = dp(r).toFloat()
        if (strokeDp > 0) setStroke(dp(strokeDp), strokeColor)
    }

    private fun icon(res: Int, size: Int = 22, color: Int = cFg): ImageView = ImageView(this).apply {
        setImageResource(res)
        imageTintList = ColorStateList.valueOf(color)
        layoutParams = LinearLayout.LayoutParams(dp(size), dp(size))
    }

    private fun iconBtn(res: Int, onClick: (View) -> Unit): FrameLayout = FrameLayout(this).apply {
        isClickable = true
        addView(icon(res, 22), FrameLayout.LayoutParams(dp(22), dp(22), Gravity.CENTER))
        setOnClickListener { v -> onClick(v) }
    }

    private fun tv(t: String, sp: Float, color: Int = cFg, medium: Boolean = false) = TextView(this).apply {
        text = t; textSize = sp; setTextColor(color)
        if (medium) typeface = Typeface.create("sans-serif-medium", Typeface.NORMAL)
    }

    private fun iconTile(res: Int, inverted: Boolean = false): FrameLayout = FrameLayout(this).apply {
        background = shape(if (inverted) cFg else cLine, 12)
        addView(icon(res, 20, if (inverted) cBg else cFg), FrameLayout.LayoutParams(dp(20), dp(20), Gravity.CENTER))
    }

    private fun favTile(title: String, url: String, bmp: Bitmap?, tileBg: Int): FrameLayout {
        val f = FrameLayout(this).apply { background = shape(tileBg, 12) }
        if (bmp != null) {
            f.addView(ImageView(this).apply { setImageBitmap(bmp) }, FrameLayout.LayoutParams(dp(22), dp(22), Gravity.CENTER))
        } else {
            val host = try { Uri.parse(url).host ?: "" } catch (e: Exception) { "" }
            val ch = (title.firstOrNull { it.isLetterOrDigit() } ?: host.firstOrNull { it.isLetterOrDigit() })?.uppercaseChar()
            if (ch != null) f.addView(tv(ch.toString(), 16f, cFg, true), FrameLayout.LayoutParams(WC, WC, Gravity.CENTER))
            else f.addView(icon(R.drawable.ic_globe, 20, cSub), FrameLayout.LayoutParams(dp(20), dp(20), Gravity.CENTER))
        }
        return f
    }

    private fun header(title: String, back: Boolean, actions: List<View>): LinearLayout {
        val h = LinearLayout(this).apply {
            orientation = LinearLayout.HORIZONTAL; gravity = Gravity.CENTER_VERTICAL
            setPadding(dp(if (back) 4 else 20), dp(6), dp(8), dp(6))
        }
        if (back) h.addView(iconBtn(R.drawable.ic_back) { backPage() }, lp(dp(44), dp(44)))
        h.addView(tv(title, 22f, cFg, true).apply { if (back) setPadding(dp(4), 0, 0, 0) }, lp(0, WC, 1f))
        actions.forEach { h.addView(it, lp(dp(44), dp(44))) }
        return h
    }

    private fun shell(title: String, back: Boolean, actions: List<View> = emptyList()): Pair<LinearLayout, LinearLayout> {
        val page = LinearLayout(this).apply { orientation = LinearLayout.VERTICAL }
        page.addView(header(title, back, actions), lp(MP, WC))
        val body = LinearLayout(this).apply { orientation = LinearLayout.VERTICAL; setPadding(0, 0, 0, dp(24)) }
        val sv = ScrollView(this).apply { isVerticalScrollBarEnabled = false; addView(body) }
        page.addView(sv, lp(MP, 0, 1f))
        return Pair(page, body)
    }

    private fun sectionLabel(t: String) = tv(t, 14f, cSub, true).apply { setPadding(dp(20), dp(18), dp(20), dp(6)) }

    private fun emptyState(t: String) = tv(t, 14f, cSub).apply { gravity = Gravity.CENTER; setPadding(dp(24), dp(64), dp(24), dp(24)) }

    private fun chips(labels: List<String>, selected: Int, onSel: (Int) -> Unit): View {
        val h = HorizontalScrollView(this).apply { isHorizontalScrollBarEnabled = false }
        val row = LinearLayout(this).apply { orientation = LinearLayout.HORIZONTAL; setPadding(dp(16), dp(4), dp(16), dp(10)) }
        labels.forEachIndexed { i, l ->
            val c = TextView(this).apply {
                text = l; textSize = 13f; gravity = Gravity.CENTER
                typeface = Typeface.create("sans-serif-medium", Typeface.NORMAL)
                setPadding(dp(16), dp(8), dp(16), dp(8))
                setTextColor(if (i == selected) cBg else cFg)
                background = shape(if (i == selected) cFg else cSurf, 18)
                setOnClickListener { onSel(i) }
            }
            row.addView(c, lp(WC, WC).apply { rightMargin = dp(8) })
        }
        h.addView(row)
        return h
    }

    private fun searchField(ph: String, initial: String, onChange: (String) -> Unit): View {
        val pill = LinearLayout(this).apply {
            orientation = LinearLayout.HORIZONTAL; gravity = Gravity.CENTER_VERTICAL
            background = shape(cSurf, 14); setPadding(dp(14), 0, dp(14), 0)
        }
        pill.addView(icon(R.drawable.ic_search, 18, cSub), lp(dp(18), dp(18)).apply { rightMargin = dp(10) })
        val e = EditText(this).apply {
            background = null; this.hint = ph; setHintTextColor(cSub); setTextColor(cFg); textSize = 14f
            isSingleLine = true; setText(initial); setPadding(0, dp(12), 0, dp(12))
            addTextChangedListener(object : TextWatcher {
                override fun afterTextChanged(s: Editable?) { onChange(s?.toString() ?: "") }
                override fun beforeTextChanged(s: CharSequence?, a: Int, b: Int, c: Int) {}
                override fun onTextChanged(s: CharSequence?, a: Int, b: Int, c: Int) {}
            })
        }
        pill.addView(e, lp(0, WC, 1f))
        val wrap = FrameLayout(this)
        wrap.addView(pill, FrameLayout.LayoutParams(MP, WC))
        return wrap
    }

    private fun listRow(leading: View, title: String, subtitle: String, trailing: View?,
                        onClick: (() -> Unit)?, onLong: (() -> Unit)? = null): LinearLayout {
        val r = LinearLayout(this).apply {
            orientation = LinearLayout.HORIZONTAL; gravity = Gravity.CENTER_VERTICAL
            setPadding(dp(16), dp(10), dp(16), dp(10)); isClickable = true
        }
        r.addView(leading, lp(dp(40), dp(40)).apply { rightMargin = dp(14) })
        val col = LinearLayout(this).apply { orientation = LinearLayout.VERTICAL }
        col.addView(tv(title, 15f, cFg, true).apply { maxLines = 1; ellipsize = TextUtils.TruncateAt.END })
        if (subtitle.isNotEmpty()) col.addView(tv(subtitle, 12f, cSub).apply { maxLines = 1; ellipsize = TextUtils.TruncateAt.END })
        r.addView(col, lp(0, WC, 1f))
        if (trailing != null) r.addView(trailing, lp(WC, WC).apply { leftMargin = dp(10) })
        if (onClick != null) r.setOnClickListener { onClick() }
        if (onLong != null) r.setOnLongClickListener { onLong(); true }
        return r
    }

    private fun navRow(res: Int, title: String, subtitle: String, chevron: Boolean = true, onClick: () -> Unit) =
        listRow(iconTile(res), title, subtitle, if (chevron) icon(R.drawable.ic_chevron, 18, cSub) else null, onClick)

    private fun card(rows: List<View>): LinearLayout {
        val c = LinearLayout(this).apply { orientation = LinearLayout.VERTICAL; background = shape(cSurf, 18) }
        rows.forEachIndexed { i, r ->
            if (i > 0) c.addView(View(this).apply { setBackgroundColor(cLine) }, lp(MP, 1).apply { leftMargin = dp(16); rightMargin = dp(16) })
            c.addView(r, lp(MP, WC))
        }
        return c
    }

    private fun addCard(body: LinearLayout, rows: List<View>) =
        body.addView(card(rows), lp(MP, WC).apply { setMargins(dp(16), dp(4), dp(16), dp(8)) })

    private fun toggleRow(title: String, subtitle: String, checked: Boolean, onChange: (Boolean) -> Unit): LinearLayout {
        val r = LinearLayout(this).apply {
            orientation = LinearLayout.HORIZONTAL; gravity = Gravity.CENTER_VERTICAL
            setPadding(dp(16), dp(12), dp(16), dp(12)); isClickable = true
        }
        val col = LinearLayout(this).apply { orientation = LinearLayout.VERTICAL }
        col.addView(tv(title, 15f, cFg, true))
        if (subtitle.isNotEmpty()) col.addView(tv(subtitle, 12f, cSub))
        val st = Switch(this)
        st.isChecked = checked
        val states = arrayOf(intArrayOf(android.R.attr.state_checked), intArrayOf())
        st.thumbTintList = ColorStateList(states, intArrayOf(cBg, cSub))
        st.trackTintList = ColorStateList(states, intArrayOf(cFg, cLine))
        st.setOnCheckedChangeListener { _, c -> onChange(c) }
        r.addView(col, lp(0, WC, 1f))
        r.addView(st, lp(WC, WC))
        r.setOnClickListener { st.toggle() }
        return r
    }

    private fun popup(anchor: View, items: List<String>, onPick: (Int) -> Unit) {
        val pm = PopupMenu(ContextThemeWrapper(this, if (dark) android.R.style.Theme_Material else android.R.style.Theme_Material_Light), anchor)
        items.forEachIndexed { i, s -> pm.menu.add(0, i, i, s) }
        pm.setOnMenuItemClickListener { onPick(it.itemId); true }
        pm.show()
    }

    /* ================= Navigasi halaman ================= */

    private fun openRoot(key: String) { pages.clear(); pages.add(key); renderPage() }
    private fun openPage(key: String) { if (pages.lastOrNull() != key) pages.add(key); renderPage() }
    private fun closePages() { pages.clear(); renderPage() }
    private fun backPage() { if (pages.isNotEmpty()) pages.removeAt(pages.size - 1); renderPage() }

    private fun renderPage() {
        pageView?.let { container.removeView(it) }
        pageView = null
        val key = pages.lastOrNull()
        if (key != null) {
            val v = when (key) {
                "tabs" -> pageTabs()
                "history" -> pageHistory()
                "downloads" -> pageDownloads()
                "bookmarks" -> pageBookmarks()
                "settings" -> pageSettings()
                "general" -> pageGeneral()
                "engine" -> pageEngine()
                "privacy" -> pagePrivacy()
                "appearance" -> pageAppearance()
                "play" -> pagePlay()
                "trlang" -> pageTrLang()
                "pipmode" -> pagePipMode()
                else -> View(this)
            }
            v.setBackgroundColor(cBg)
            v.isClickable = true
            container.addView(v, FrameLayout.LayoutParams(MP, MP))
            pageView = v
            hideKb()
        }
        updateChrome()
    }

    /* ---------- Menu (bottom sheet) ---------- */

    private fun showMenuSheet() {
        val d = Dialog(this, android.R.style.Theme_Material_Dialog_NoActionBar)
        d.requestWindowFeature(Window.FEATURE_NO_TITLE)
        val r = dp(24).toFloat()
        val sheet = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(dp(8), dp(10), dp(8), dp(16))
            background = GradientDrawable().apply { setColor(cSurf); cornerRadii = floatArrayOf(r, r, r, r, 0f, 0f, 0f, 0f) }
        }
        sheet.addView(View(this).apply { background = shape(cLine, 3) }, lp(dp(36), dp(5)).apply { gravity = Gravity.CENTER_HORIZONTAL; bottomMargin = dp(10) })
        val head = LinearLayout(this).apply { orientation = LinearLayout.HORIZONTAL; gravity = Gravity.CENTER_VERTICAL; setPadding(dp(12), dp(6), dp(12), dp(10)) }
        head.addView(icon(R.drawable.ic_logo, 34, cFg), lp(dp(34), dp(34)).apply { rightMargin = dp(14) })
        head.addView(tv("Alethra", 20f, cFg, true))
        sheet.addView(head, lp(MP, WC))
        fun item(res: Int, label: String, action: () -> Unit) {
            val row = LinearLayout(this).apply { orientation = LinearLayout.HORIZONTAL; gravity = Gravity.CENTER_VERTICAL; setPadding(dp(12), dp(14), dp(12), dp(14)); isClickable = true }
            row.addView(icon(res, 22, cFg), lp(dp(22), dp(22)).apply { rightMargin = dp(18) })
            row.addView(tv(label, 16f, cFg), lp(0, WC, 1f))
            row.setOnClickListener { d.dismiss(); action() }
            sheet.addView(row, lp(MP, WC))
        }
        item(R.drawable.ic_plus, "Tab baru") { autoMini(); pages.clear(); newTab(home()) }
        item(R.drawable.ic_history, "Histori") { openRoot("history") }
        item(R.drawable.ic_download, "Unduhan") { openRoot("downloads") }
        item(R.drawable.ic_bookmark, "Bookmark") { openRoot("bookmarks") }
        item(R.drawable.ic_settings, "Pengaturan") { openRoot("settings") }
        sheet.addView(View(this).apply { setBackgroundColor(cLine) }, lp(MP, 1).apply { setMargins(dp(12), dp(6), dp(12), dp(6)) })
        val inc = LinearLayout(this).apply { orientation = LinearLayout.HORIZONTAL; gravity = Gravity.CENTER_VERTICAL; setPadding(dp(12), dp(10), dp(12), dp(10)); isClickable = true }
        inc.addView(icon(R.drawable.ic_incognito, 22, cFg), lp(dp(22), dp(22)).apply { rightMargin = dp(18) })
        inc.addView(tv("Mode Incognito", 16f, cFg), lp(0, WC, 1f))
        val st = Switch(this)
        st.isChecked = incognito
        val states = arrayOf(intArrayOf(android.R.attr.state_checked), intArrayOf())
        st.thumbTintList = ColorStateList(states, intArrayOf(cBg, cSub))
        st.trackTintList = ColorStateList(states, intArrayOf(cFg, cLine))
        st.setOnCheckedChangeListener { _, c ->
            incognito = c
            toast(if (c) "Riwayat tidak akan disimpan" else "Riwayat disimpan kembali")
        }
        inc.addView(st, lp(WC, WC))
        inc.setOnClickListener { st.toggle() }
        sheet.addView(inc, lp(MP, WC))
        d.setContentView(sheet)
        d.window?.apply {
            setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            setGravity(Gravity.BOTTOM)
            setLayout(MP, WC)
        }
        d.show()
    }

    /* ---------- Menu titik tiga di bar alamat ---------- */

    private fun showPageMenu(anchor: View) {
        val pm = PopupMenu(ContextThemeWrapper(this, if (dark) android.R.style.Theme_Material else android.R.style.Theme_Material_Light), anchor)
        val m = pm.menu
        m.add(0, 1, 0, "Muat ulang")
        m.add(0, 2, 0, "Maju")
        m.add(0, 3, 0, "Tambah bookmark")
        m.add(0, 4, 0, "Cari di halaman")
        m.add(0, 5, 0, "Bagikan")
        m.add(0, 6, 0, "Mode desktop").apply { isCheckable = true; isChecked = bool("desktop", false) }
        if (bool("pip", false)) m.add(0, 7, 0, "Perkecil video")
        pm.setOnMenuItemClickListener { i ->
            when (i.itemId) {
                1 -> cw().reload()
                2 -> if (cw().canGoForward()) cw().goForward()
                3 -> addBookmark()
                4 -> findDialog()
                5 -> share(tabs[cur].url)
                6 -> { setBool("desktop", !bool("desktop", false)); applyAllSites(true) }
                7 -> {
                    if (pipMode() == "in") { if (videoPlaying) startMini() else toast("Tidak ada video yang sedang diputar") }
                    else if (!videoPlaying && customView == null) toast("Tidak ada video yang sedang diputar")
                    else enterPip()
                }
            }
            true
        }
        pm.show()
    }

    /* ---------- Halaman Tab ---------- */

    private fun pageTabs(): View {
        val add = iconBtn(R.drawable.ic_plus) { autoMini(); pages.clear(); newTab(home()) }
        val more = iconBtn(R.drawable.ic_more) { v -> popup(v, listOf("Tutup semua tab")) { closeAllTabs() } }
        val (page, body) = shell("Tab", false, listOf(add, more))
        body.addView(chips(listOf("Semua", "Aktif (${tabs.size})", "Tertutup"), tabFilter) { tabFilter = it; renderPage() })
        val cardLp = { lp(MP, WC).apply { setMargins(dp(16), dp(5), dp(16), dp(5)) } }
        if (tabFilter != 2) {
            tabs.forEachIndexed { i, t ->
                val x = iconBtn(R.drawable.ic_close) { closeTab(i, true) }
                val row = listRow(favTile(t.title, t.url, t.icon, cLine), t.title.ifEmpty { t.url }, prettyUrl(t.url), x,
                    onClick = { if (i != cur) autoMini(); selectTab(i) })
                row.background = if (i == cur) shape(cSurf, 16, cFg, 2) else shape(cSurf, 16)
                body.addView(row, cardLp())
            }
        }
        if (tabFilter != 1 && closed.isNotEmpty()) {
            if (tabFilter == 0) body.addView(sectionLabel("Baru saja ditutup"))
            closed.toList().forEach { c ->
                val row = listRow(favTile(c.title, c.url, null, cLine), c.title.ifEmpty { c.url }, prettyUrl(c.url), null,
                    onClick = { closed.remove(c); pages.clear(); newTab(c.url) })
                row.background = shape(cSurf, 16)
                body.addView(row, cardLp())
            }
        } else if (tabFilter == 2) {
            body.addView(emptyState("Belum ada tab yang ditutup"))
        }
        return page
    }

    private fun prettyUrl(u: String) = if (u.startsWith(ASSET)) "Beranda Alethra" else u

    /* ---------- Halaman Histori ---------- */

    private fun dayStart(ts: Long): Long {
        val c = Calendar.getInstance(); c.timeInMillis = ts
        c.set(Calendar.HOUR_OF_DAY, 0); c.set(Calendar.MINUTE, 0); c.set(Calendar.SECOND, 0); c.set(Calendar.MILLISECOND, 0)
        return c.timeInMillis
    }

    private fun dayLabel(ts: Long): String {
        if (ts <= 0) return "Sebelumnya"
        val today = dayStart(System.currentTimeMillis())
        val yest = Calendar.getInstance().apply { timeInMillis = today; add(Calendar.DAY_OF_YEAR, -1) }.timeInMillis
        val d = dayStart(ts)
        return when (d) {
            today -> "Hari ini"
            yest -> "Kemarin"
            else -> SimpleDateFormat("d MMM yyyy", Locale("id", "ID")).format(Date(ts))
        }
    }

    private fun pageHistory(): View {
        val more = iconBtn(R.drawable.ic_more) { v ->
            popup(v, listOf("Hapus semua riwayat")) {
                dlg().setMessage("Hapus semua riwayat?")
                    .setPositiveButton("Hapus") { _, _ -> prefs.edit().remove("hist").apply(); renderPage() }
                    .setNegativeButton("Batal", null).show()
            }
        }
        val (page, body) = shell("Histori", true, listOf(more))
        val list = LinearLayout(this).apply { orientation = LinearLayout.VERTICAL }
        fun fill() {
            list.removeAllViews()
            val a = getList("hist")
            val q = histQuery.trim().lowercase()
            var last = ""
            var n = 0
            for (i in 0 until a.length()) {
                val o = a.getJSONObject(i)
                val t = o.optString("t"); val u = o.optString("u"); val ts = o.optLong("ts", 0L)
                if (q.isNotEmpty() && !t.lowercase().contains(q) && !u.lowercase().contains(q)) continue
                val lab = dayLabel(ts)
                if (lab != last) { list.addView(sectionLabel(lab).apply { setTextColor(cFg) }, lp(MP, WC)); last = lab }
                val time = if (ts > 0) tv(SimpleDateFormat("HH.mm", Locale.US).format(Date(ts)), 12f, cSub) else null
                list.addView(listRow(favTile(t, u, null, cSurf), t.ifEmpty { u }, hostOf(u), time,
                    { closePages(); go(u) },
                    { entryDialog("hist", t.ifEmpty { u }, u) { fill() } }), lp(MP, WC))
                n++
            }
            if (n == 0) list.addView(emptyState(if (q.isEmpty()) "Belum ada riwayat" else "Tidak ada hasil"))
        }
        body.addView(searchField("Cari histori…", histQuery) { histQuery = it; fill() },
            lp(MP, WC).apply { setMargins(dp(16), dp(4), dp(16), dp(8)) })
        body.addView(list, lp(MP, WC))
        fill()
        return page
    }

    private fun hostOf(u: String) = try { Uri.parse(u).host?.removePrefix("www.") ?: u } catch (e: Exception) { u }

    private fun entryDialog(key: String, t: String, u: String, after: () -> Unit) {
        dlg().setTitle(t.take(60)).setItems(arrayOf("Buka di tab baru", "Salin alamat", "Hapus")) { _, i ->
            when (i) {
                0 -> { pages.clear(); newTab(u) }
                1 -> {
                    (getSystemService(CLIPBOARD_SERVICE) as ClipboardManager).setPrimaryClip(ClipData.newPlainText("url", u))
                    toast("Alamat disalin")
                }
                else -> { removeEntry(key, u); after() }
            }
        }.show()
    }

    private fun removeEntry(key: String, url: String) {
        val a = getList(key); val n = JSONArray()
        for (i in 0 until a.length()) { val o = a.getJSONObject(i); if (o.optString("u") != url) n.put(o) }
        prefs.edit().putString(key, n.toString()).apply()
    }

    /* ---------- Halaman Unduhan ---------- */

    private fun queryDownloads(): List<Dl> {
        val a = getList("dl")
        if (a.length() == 0) return emptyList()
        val ids = LongArray(a.length()) { a.getJSONObject(it).getLong("id") }
        val out = mutableListOf<Dl>()
        try {
            val dm = getSystemService(DOWNLOAD_SERVICE) as DownloadManager
            dm.query(DownloadManager.Query().setFilterById(*ids))?.use { c ->
                while (c.moveToNext()) {
                    out.add(Dl(
                        c.getLong(c.getColumnIndexOrThrow(DownloadManager.COLUMN_ID)),
                        c.getString(c.getColumnIndexOrThrow(DownloadManager.COLUMN_TITLE)) ?: "Berkas",
                        c.getInt(c.getColumnIndexOrThrow(DownloadManager.COLUMN_STATUS)),
                        c.getLong(c.getColumnIndexOrThrow(DownloadManager.COLUMN_TOTAL_SIZE_BYTES)),
                        c.getLong(c.getColumnIndexOrThrow(DownloadManager.COLUMN_BYTES_DOWNLOADED_SO_FAR)),
                        c.getString(c.getColumnIndexOrThrow(DownloadManager.COLUMN_MEDIA_TYPE))))
                }
            }
        } catch (e: Exception) { }
        return out.sortedByDescending { it.id }
    }

    private fun isRunning(s: Int) = s == DownloadManager.STATUS_RUNNING || s == DownloadManager.STATUS_PENDING || s == DownloadManager.STATUS_PAUSED

    private fun pageDownloads(): View {
        val (page, body) = shell("Unduhan", true)
        val list = LinearLayout(this).apply { orientation = LinearLayout.VERTICAL }
        fun fill() {
            list.removeAllViews()
            val all = queryDownloads()
            val shown = all.filter { when (dlFilter) { 1 -> isRunning(it.status); 2 -> it.status == DownloadManager.STATUS_SUCCESSFUL; else -> true } }
            if (shown.isEmpty()) list.addView(emptyState("Belum ada unduhan"))
            shown.forEach { d ->
                val size = if (d.total > 0) Formatter.formatShortFileSize(this, d.total) else ""
                val status = when {
                    d.status == DownloadManager.STATUS_SUCCESSFUL -> "Selesai"
                    d.status == DownloadManager.STATUS_FAILED -> "Gagal"
                    d.status == DownloadManager.STATUS_PAUSED -> "Dijeda"
                    d.total > 0 -> "${(d.sofar * 100 / d.total).toInt()}% · Berlangsung"
                    else -> "Berlangsung"
                }
                val sub = if (size.isNotEmpty() && d.status == DownloadManager.STATUS_SUCCESSFUL) "$size · $status" else status
                val del = iconBtn(R.drawable.ic_trash) { _ -> removeDownload(d.id); fill() }
                list.addView(listRow(iconTile(R.drawable.ic_file), d.name, sub, del,
                    { if (d.status == DownloadManager.STATUS_SUCCESSFUL) openDownload(d) }), lp(MP, WC))
            }
            if (all.any { isRunning(it.status) }) {
                handler.postDelayed({ if (pageView === page && pages.lastOrNull() == "downloads") fill() }, 1500)
            }
        }
        body.addView(chips(listOf("Semua", "Berlangsung", "Selesai"), dlFilter) { dlFilter = it; renderPage() })
        body.addView(list, lp(MP, WC))
        fill()
        return page
    }

    private fun removeDownload(id: Long) {
        try { (getSystemService(DOWNLOAD_SERVICE) as DownloadManager).remove(id) } catch (e: Exception) { }
        val a = getList("dl"); val n = JSONArray()
        for (i in 0 until a.length()) { val o = a.getJSONObject(i); if (o.getLong("id") != id) n.put(o) }
        prefs.edit().putString("dl", n.toString()).apply()
    }

    private fun openDownload(d: Dl) {
        try {
            val uri = (getSystemService(DOWNLOAD_SERVICE) as DownloadManager).getUriForDownloadedFile(d.id)
            val i = Intent(Intent.ACTION_VIEW).setDataAndType(uri, d.mime ?: "*/*")
                .addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION or Intent.FLAG_ACTIVITY_NEW_TASK)
            startActivity(i)
        } catch (e: Exception) { toast("Tidak bisa membuka berkas ini") }
    }

    /* ---------- Halaman Bookmark ---------- */

    private fun pageBookmarks(): View {
        val (page, body) = shell("Bookmark", true)
        val all = getList("bm")
        val weekAgo = System.currentTimeMillis() - 7L * 86400000L
        var recent = 0
        for (i in 0 until all.length()) if (all.getJSONObject(i).optLong("ts", 0L) > weekAgo) recent++
        val list = LinearLayout(this).apply { orientation = LinearLayout.VERTICAL }
        fun fill() {
            list.removeAllViews()
            val q = bmQuery.trim().lowercase()
            var n = 0
            for (i in 0 until all.length()) {
                val o = all.getJSONObject(i)
                val t = o.optString("t"); val u = o.optString("u")
                if (bmFilter == 1 && o.optLong("ts", 0L) <= weekAgo) continue
                if (q.isNotEmpty() && !t.lowercase().contains(q) && !u.lowercase().contains(q)) continue
                list.addView(listRow(favTile(t, u, null, cSurf), t.ifEmpty { u }, u, null,
                    { closePages(); go(u) },
                    { entryDialog("bm", t.ifEmpty { u }, u) { renderPage() } }), lp(MP, WC))
                n++
            }
            if (n == 0) list.addView(emptyState("Belum ada bookmark"))
        }
        body.addView(searchField("Cari bookmark…", bmQuery) { bmQuery = it; fill() },
            lp(MP, WC).apply { setMargins(dp(16), dp(4), dp(16), dp(8)) })
        fun countTv(n: Int) = tv(n.toString(), 13f, cSub)
        addCard(body, listOf(
            listRow(iconTile(R.drawable.ic_bookmark, bmFilter == 0), "Semua Bookmark", "", countTv(all.length()), { bmFilter = 0; renderPage() }),
            listRow(iconTile(R.drawable.ic_history, bmFilter == 1), "Baru ditambahkan", "7 hari terakhir", countTv(recent), { bmFilter = 1; renderPage() })))
        body.addView(list, lp(MP, WC).apply { topMargin = dp(8) })
        fill()
        return page
    }

    /* ---------- Pengaturan ---------- */

    private fun pageSettings(): View {
        val (page, body) = shell("Pengaturan", true)
        addCard(body, listOf(
            navRow(R.drawable.ic_sliders, "Umum", "Mode desktop, teks, dan gambar") { openPage("general") },
            navRow(R.drawable.ic_play, "Play", "Video kecil dan terjemahan otomatis") { openPage("play") },
            navRow(R.drawable.ic_search, "Mesin pencari", engineNames[engineIdx()]) { openPage("engine") },
            navRow(R.drawable.ic_shield, "Privasi & Keamanan", "Perlindungan data dan situs") { openPage("privacy") },
            navRow(R.drawable.ic_palette, "Tampilan", "Tema dan tata letak") { openPage("appearance") }))
        addCard(body, listOf(
            navRow(R.drawable.ic_reset, "Reset pengaturan", "Kembalikan semua ke bawaan", false) {
                dlg().setMessage("Kembalikan semua pengaturan ke bawaan?")
                    .setPositiveButton("Reset") { _, _ ->
                        val ed = prefs.edit()
                        listOf("theme", "barBottom", "dark", "desktop", "zoom", "images", "js", "pinch", "popups", "tpc", "https", "engine", "pip", "pipmode", "tr", "trlang", "trhide")
                            .forEach { ed.remove(it) }
                        ed.apply()
                        applyChrome(); applyAllSites(true); reloadHomes(); injectTrAll()
                    }.setNegativeButton("Batal", null).show()
            }))
        return page
    }

    private fun pageGeneral(): View {
        val (page, body) = shell("Umum", true)
        addCard(body, listOf(
            toggleRow("Mode desktop", "Minta versi desktop situs", bool("desktop", false)) { c -> setBool("desktop", c); applyAllSites(true) },
            toggleRow("Muat gambar", "Tampilkan gambar di halaman", bool("images", true)) { c -> setBool("images", c); applyAllSites(true) },
            toggleRow("JavaScript", "Izinkan skrip di halaman", bool("js", true)) { c -> setBool("js", c); applyAllSites(true) },
            toggleRow("Zoom dua jari", "Cubit untuk memperbesar", bool("pinch", true)) { c -> setBool("pinch", c); applyAllSites(false) }))
        val zl = tv("Ukuran teks situs: ${prefs.getInt("zoom", 100)}%", 15f, cFg, true).apply { setPadding(dp(20), dp(18), dp(20), dp(4)) }
        body.addView(zl, lp(MP, WC))
        val sb = SeekBar(this)
        sb.max = 15
        sb.progress = (prefs.getInt("zoom", 100) - 50) / 10
        sb.progressTintList = ColorStateList.valueOf(cFg)
        sb.thumbTintList = ColorStateList.valueOf(cFg)
        sb.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(s: SeekBar?, p: Int, fromUser: Boolean) { zl.text = "Ukuran teks situs: ${50 + p * 10}%" }
            override fun onStartTrackingTouch(s: SeekBar?) {}
            override fun onStopTrackingTouch(s: SeekBar?) {
                prefs.edit().putInt("zoom", 50 + (s?.progress ?: 5) * 10).apply()
                applyAllSites(false)
            }
        })
        body.addView(sb, lp(MP, WC).apply { setMargins(dp(12), 0, dp(12), 0) })
        return page
    }

    private fun pageEngine(): View {
        val (page, body) = shell("Mesin pencari", true)
        val rows = engineNames.mapIndexed { i, n ->
            listRow(iconTile(R.drawable.ic_search), n, "", if (i == engineIdx()) icon(R.drawable.ic_check, 20, cFg) else null,
                { prefs.edit().putString("engine", engineKeys[i]).apply(); renderPage() })
        }
        addCard(body, rows)
        return page
    }

    private fun pagePrivacy(): View {
        val (page, body) = shell("Privasi & Keamanan", true)
        body.addView(sectionLabel("Perlindungan penelusuran"))
        addCard(body, listOf(
            toggleRow("Blokir pelacak pihak ketiga", "Cegah pelacak lintas situs", !bool("tpc", true)) { c -> setBool("tpc", !c); applyAllSites(false) },
            toggleRow("Blokir pop-up berbahaya", "Lindungi dari pop-up berbahaya", bool("popups", false)) { c -> setBool("popups", c); applyAllSites(false) },
            toggleRow("Gunakan HTTPS", "Enkripsi koneksi situs", bool("https", false)) { c -> setBool("https", c) }))
        body.addView(sectionLabel("Data & Riwayat"))
        addCard(body, listOf(
            navRow(R.drawable.ic_trash, "Hapus data penelusuran", "Riwayat, cookie, dan cache") { clearDialog() }))
        return page
    }

    private fun themeCard(label: String, isDarkCard: Boolean, selected: Boolean, onClick: () -> Unit): LinearLayout {
        val pbg = if (isDarkCard) Color.parseColor("#0B0B0D") else Color.WHITE
        val pbar = if (isDarkCard) Color.parseColor("#2A2A2E") else Color.parseColor("#E8E8EC")
        val box = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(dp(12), dp(12), dp(12), dp(12))
            background = shape(pbg, 14, if (selected) cFg else cLine, if (selected) 2 else 1)
        }
        box.addView(View(this).apply { background = shape(pbar, 3) }, lp(dp(40), dp(6)))
        box.addView(View(this).apply { background = shape(pbar, 8) }, lp(MP, dp(48)).apply { topMargin = dp(10) })
        val wrap = LinearLayout(this).apply { orientation = LinearLayout.VERTICAL; gravity = Gravity.CENTER_HORIZONTAL; isClickable = true }
        wrap.addView(box, lp(MP, WC))
        wrap.addView(tv(label, 13f, if (selected) cFg else cSub, selected), lp(WC, WC).apply { topMargin = dp(8) })
        wrap.setOnClickListener { onClick() }
        return wrap
    }

    private fun setTheme(v: String) { prefs.edit().putString("theme", v).apply(); applyChrome(); reloadHomes() }

    private fun pageAppearance(): View {
        val (page, body) = shell("Tampilan", true)
        body.addView(sectionLabel("Tema"))
        val row = LinearLayout(this).apply { orientation = LinearLayout.HORIZONTAL; setPadding(dp(14), 0, dp(14), 0) }
        row.addView(themeCard("Gelap", true, dark) { setTheme("dark") }, lp(0, WC, 1f).apply { setMargins(dp(6), 0, dp(6), 0) })
        row.addView(themeCard("Terang", false, !dark) { setTheme("light") }, lp(0, WC, 1f).apply { setMargins(dp(6), 0, dp(6), 0) })
        body.addView(row, lp(MP, WC))
        body.addView(View(this), lp(MP, dp(12)))
        addCard(body, listOf(
            toggleRow("Mode perangkat", "Ikuti pengaturan sistem", prefs.getString("theme", "system") == "system") { c ->
                setTheme(if (c) "system" else if (dark) "dark" else "light")
            },
            toggleRow("Mode gelap situs", "Gelapkan halaman web", bool("dark", false)) { c -> setBool("dark", c); applyAllSites(false) },
            toggleRow("Bar alamat di bawah", "Pindahkan bar alamat ke bawah", bool("barBottom", false)) { c -> setBool("barBottom", c); applyChrome() }))
        return page
    }

    private fun trIdx() = trLangs.indexOf(trLang()).coerceAtLeast(0)
    private fun trLang(): String = prefs.getString("trlang", "id") ?: "id"

    private fun pagePlay(): View {
        val (page, body) = shell("Play", true)
        body.addView(sectionLabel("Video"))
        addCard(body, listOf(
            toggleRow("Perkecil video", "Lanjutkan putar video di jendela kecil saat keluar dari aplikasi", bool("pip", false)) { c ->
                setBool("pip", c)
                if (c) toast("Berlaku di situs apa pun saat ada video yang diputar")
            },
            navRow(R.drawable.ic_expand, "Mode perkecil", if (pipMode() == "in") "Di dalam browser" else "Di luar browser") { openPage("pipmode") }))
        body.addView(sectionLabel("Terjemahan"))
        addCard(body, listOf(
            toggleRow("Terjemahan otomatis", "Terjemahkan chat live (YouTube, Twitch)", bool("tr", false)) { c -> setBool("tr", c); injectTrAll() },
            navRow(R.drawable.ic_globe, "Bahasa tujuan", trNames[trIdx()]) { openPage("trlang") },
            toggleRow("Sembunyikan teks asli", "Hanya tampilkan hasil terjemahan", bool("trhide", false)) { c -> setBool("trhide", c); injectTrAll() }))
        return page
    }

    private fun pagePipMode(): View {
        val (page, body) = shell("Mode perkecil", true)
        val opts = listOf(
            Triple("out", "Di luar browser", "Jendela kecil melayang di atas aplikasi lain saat kamu menekan Home"),
            Triple("in", "Di dalam browser", "Pemutar mini di dalam Alethra saat kamu membuka tab atau halaman lain"))
        addCard(body, opts.map { (k, n, d) ->
            listRow(iconTile(R.drawable.ic_expand), n, d, if (pipMode() == k) icon(R.drawable.ic_check, 20, cFg) else null,
                { prefs.edit().putString("pipmode", k).apply(); renderPage() })
        })
        return page
    }

    private fun pageTrLang(): View {
        val (page, body) = shell("Bahasa tujuan", true)
        val rows = trNames.mapIndexed { i, n ->
            listRow(iconTile(R.drawable.ic_globe), n, "", if (i == trIdx()) icon(R.drawable.ic_check, 20, cFg) else null,
                { prefs.edit().putString("trlang", trLangs[i]).apply(); injectTrAll(); renderPage() })
        }
        addCard(body, rows)
        return page
    }

    /* ================= Terjemahan otomatis ================= */

    inner class TrBridge(private val web: WebView) {
        @JavascriptInterface
        fun translate(id: Int, text: String, lang: String) {
            if (!bool("tr", false) || text.length > 400 || !trLangs.contains(lang)) return
            trPool.execute {
                val res = try { fetchTranslation(text, lang) } catch (e: Exception) { "" }
                runOnUiThread {
                    web.evaluateJavascript("window.__alTr&&window.__alTr(" + id + "," + JSONObject.quote(res) + ")", null)
                }
            }
        }
    }

    private fun fetchTranslation(text: String, lang: String): String {
        val u = URL("https://translate.googleapis.com/translate_a/single?client=gtx&sl=auto&tl=" + lang + "&dt=t&q=" + URLEncoder.encode(text, "UTF-8"))
        val c = u.openConnection() as HttpURLConnection
        c.connectTimeout = 6000
        c.readTimeout = 6000
        c.setRequestProperty("User-Agent", "Mozilla/5.0")
        try {
            if (c.responseCode != 200) return ""
            val body = c.inputStream.bufferedReader().use { it.readText() }
            val seg = JSONArray(body).getJSONArray(0)
            val sb = StringBuilder()
            for (i in 0 until seg.length()) sb.append(seg.getJSONArray(i).optString(0, ""))
            return sb.toString()
        } finally { c.disconnect() }
    }

    private fun trScript(): String {
        trJs?.let { return it }
        val s = try { assets.open("translate.js").bufferedReader().use { it.readText() } } catch (e: Exception) { "" }
        trJs = s
        return s
    }

    private fun injectTr(w: WebView) {
        val on = bool("tr", false)
        val cfg = "window.__alCfg={on:" + on + ",lang:'" + trLang() + "',hide:" + bool("trhide", false) + "};"
        w.evaluateJavascript(if (on) cfg + trScript() else cfg, null)
    }

    private fun injectTrAll() { tabs.forEach { if (!it.url.startsWith(ASSET)) injectTr(it.web) } }

    /* ================= Beranda & pintasan ================= */

    private fun shortcuts(): JSONArray {
        val s = prefs.getString("sc", null)
        if (s != null) { try { return JSONArray(s) } catch (e: Exception) { } }
        val a = JSONArray()
        listOf("Google" to "https://www.google.com", "YouTube" to "https://m.youtube.com", "Gmail" to "https://mail.google.com",
            "Spotify" to "https://open.spotify.com", "Wikipedia" to "https://id.wikipedia.org")
            .forEach { a.put(JSONObject().put("n", it.first).put("u", it.second)) }
        return a
    }

    private fun home(): String = HOME + "#t=" + (if (dark) "dark" else "light") + "&s=" + Uri.encode(shortcuts().toString())

    private fun reloadHomes() {
        tabs.filter { it.url.startsWith(ASSET) }.forEach { it.web.loadUrl(home()) }
    }

    private fun handleInternal(u: Uri) {
        when (u.host) {
            "go" -> go(u.getQueryParameter("q") ?: "")
            "settings" -> openRoot("settings")
            "mic" -> voice()
            "addshortcut" -> addShortcutDialog()
            "delshortcut" -> {
                val i = u.getQueryParameter("i")?.toIntOrNull() ?: return
                dlg().setMessage("Hapus pintasan ini?")
                    .setPositiveButton("Hapus") { _, _ ->
                        val a = shortcuts(); val n = JSONArray()
                        for (k in 0 until a.length()) if (k != i) n.put(a.getJSONObject(k))
                        prefs.edit().putString("sc", n.toString()).apply()
                        reloadHomes()
                    }.setNegativeButton("Batal", null).show()
            }
        }
    }

    private fun voice() {
        try {
            val i = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH)
                .putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
            startActivityForResult(i, VOICE_REQ)
        } catch (e: Exception) { toast("Pencarian suara tidak tersedia di perangkat ini") }
    }

    private fun addShortcutDialog() {
        val col = LinearLayout(this).apply { orientation = LinearLayout.VERTICAL; setPadding(dp(20), dp(8), dp(20), 0) }
        val name = EditText(this).apply { hint = "Nama"; isSingleLine = true; setTextColor(cFg); setHintTextColor(cSub) }
        val url = EditText(this).apply { hint = "Alamat web"; isSingleLine = true; setTextColor(cFg); setHintTextColor(cSub); inputType = InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_URI }
        col.addView(name, lp(MP, WC)); col.addView(url, lp(MP, WC))
        dlg().setTitle("Tambah pintasan").setView(col)
            .setPositiveButton("Tambah") { _, _ ->
                val raw = url.text.toString().trim()
                if (raw.isEmpty()) return@setPositiveButton
                val u = toUrl(raw)
                val n = name.text.toString().trim().ifEmpty { hostOf(u) }
                val a = shortcuts()
                a.put(JSONObject().put("n", n).put("u", u))
                prefs.edit().putString("sc", a.toString()).apply()
                reloadHomes()
            }.setNegativeButton("Batal", null).show()
    }

    /* ================= Pencarian & URL ================= */

    private fun toUrl(input: String): String {
        val t = input.trim()
        if (t.isEmpty()) return home()
        if (Regex("^[a-zA-Z][a-zA-Z0-9+.-]*://.*").matches(t) || t.startsWith("about:") || t.startsWith("file:")) return t
        if (!t.contains(" ")) {
            if (Regex("^(www\\.)?([a-z0-9-]+\\.)+[a-z]{2,}(:\\d+)?([/?#].*)?$", RegexOption.IGNORE_CASE).matches(t)) return "https://" + t
            if (Regex("^(localhost|\\d{1,3}(\\.\\d{1,3}){3})(:\\d+)?([/?#].*)?$").matches(t)) return "http://" + t
        }
        return engineUrls[engineIdx()] + URLEncoder.encode(t, "UTF-8")
    }

    private fun go(input: String) = cw().loadUrl(toUrl(input))

    private fun setUrlText(u: String) {
        if (omnibox.hasFocus()) return
        omnibox.setText(if (u.startsWith(ASSET)) "" else u)
    }

    private fun isLocalHost(h: String?): Boolean =
        h == null || h == "localhost" || h.endsWith(".local") || Regex("^\\d{1,3}(\\.\\d{1,3}){3}$").matches(h)

    /* ================= WebView ================= */

    private fun desktopUa() = defaultUa
        .replace(Regex("\\(Linux; Android[^)]*\\)"), "(X11; Linux x86_64)")
        .replace("Version/4.0 ", "").replace(" Mobile", "")

    @Suppress("DEPRECATION")
    private fun applySite(w: WebView) {
        val s = w.settings
        s.userAgentString = if (bool("desktop", false)) desktopUa() else defaultUa
        s.javaScriptEnabled = bool("js", true)
        s.loadsImagesAutomatically = bool("images", true)
        s.blockNetworkImage = !bool("images", true)
        s.textZoom = prefs.getInt("zoom", 100)
        val pinch = bool("pinch", true)
        s.setSupportZoom(pinch)
        s.builtInZoomControls = pinch
        s.javaScriptCanOpenWindowsAutomatically = !bool("popups", false)
        CookieManager.getInstance().setAcceptThirdPartyCookies(w, bool("tpc", true))
        if (Build.VERSION.SDK_INT >= 29) {
            s.forceDark = if (bool("dark", false)) WebSettings.FORCE_DARK_ON else WebSettings.FORCE_DARK_OFF
        }
    }

    private fun applyAllSites(reload: Boolean) {
        tabs.forEach { applySite(it.web) }
        if (reload) cw().reload()
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
        w.addJavascriptInterface(TrBridge(w), "AlethraTr")
        applySite(w)

        w.webViewClient = object : WebViewClient() {
            override fun shouldOverrideUrlLoading(v: WebView?, r: WebResourceRequest?): Boolean {
                val req = r ?: return false
                val u = req.url.toString()
                if (u.startsWith("alethra://")) {
                    // Hanya halaman beranda bawaan yang boleh memakai perintah internal
                    if (v?.url?.startsWith(HOME) == true) handleInternal(Uri.parse(u))
                    return true
                }
                if (bool("https", false) && req.isForMainFrame && u.startsWith("http://") && !isLocalHost(req.url.host)) {
                    v?.loadUrl("https://" + u.substring(7))
                    return true
                }
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
                if (favicon != null) t.icon = favicon
                if (isCur(v)) { setUrlText(t.url); updateChrome() }
            }

            override fun onPageFinished(v: WebView?, url: String?) {
                val t = tabOf(v) ?: return
                t.url = url ?: t.url
                t.title = v?.title ?: t.url
                if (!t.url.startsWith(ASSET) && !t.url.startsWith("data:") && t.url != "about:blank") addHistory(t.title, t.url)
                if (!t.url.startsWith(ASSET) && v != null) injectTr(v)
                if (isCur(v)) { setUrlText(t.url); updateChrome() }
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
                    progress.visibility = if (p in 1..99 && top.visibility == View.VISIBLE) View.VISIBLE else View.GONE
                }
            }

            override fun onReceivedTitle(v: WebView?, t: String?) { tabOf(v)?.title = t ?: "" }

            override fun onReceivedIcon(v: WebView?, icon: Bitmap?) { tabOf(v)?.icon = icon }

            override fun onCreateWindow(v: WebView?, isDialog: Boolean, isUserGesture: Boolean, resultMsg: Message?): Boolean {
                val msg = resultMsg ?: return false
                pages.clear()
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
                dlg().setTitle(ex.take(60))
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
            val id = (getSystemService(DOWNLOAD_SERVICE) as DownloadManager).enqueue(r)
            val a = getList("dl")
            val n = JSONArray()
            n.put(JSONObject().put("id", id).put("t", name).put("ts", System.currentTimeMillis()))
            for (i in 0 until minOf(a.length(), 99)) n.put(a.getJSONObject(i))
            prefs.edit().putString("dl", n.toString()).apply()
            toast("Mengunduh $name")
        } catch (e: Exception) {
            toast("Gagal mengunduh")
        }
    }

    /* ================= Tab ================= */

    private fun newTab(url: String?, select: Boolean = true): Tab {
        val t = Tab(makeWeb())
        tabs.add(t)
        if (url != null) t.web.loadUrl(url)
        if (select) selectTab(tabs.size - 1) else updateChrome()
        return t
    }

    private fun selectTab(i: Int, closeAllPages: Boolean = true) {
        if (tabs[i] === miniTab) stopMini(false)
        cur = i
        if (closeAllPages) { pages.clear(); pageView = null }
        container.removeAllViews()
        val w = tabs[i].web
        (w.parent as? ViewGroup)?.removeView(w)
        container.addView(w, FrameLayout.LayoutParams(MP, MP))
        omnibox.clearFocus()
        setUrlText(tabs[i].url)
        progress.visibility = View.GONE
        updateChrome()
    }

    private fun rememberClosed(t: Tab) {
        if (incognito || t.url.startsWith(ASSET) || t.url == "about:blank") return
        closed.add(0, Closed(t.title, t.url))
        if (closed.size > 20) closed.removeAt(closed.size - 1)
    }

    private fun closeTab(i: Int, fromPage: Boolean = false) {
        if (tabs[i] === miniTab) stopMini(true)
        val t = tabs.removeAt(i)
        rememberClosed(t)
        container.removeView(t.web)
        t.web.stopLoading(); t.web.destroy()
        if (tabs.isEmpty()) { pages.clear(); pageView = null; newTab(home()); return }
        val n = if (i < cur) cur - 1 else if (i == cur) minOf(cur, tabs.size - 1) else cur
        selectTab(n, !fromPage)
        if (fromPage) renderPage()
    }

    private fun closeAllTabs() {
        stopMini(true)
        tabs.toList().forEach { t ->
            rememberClosed(t)
            container.removeView(t.web)
            t.web.stopLoading(); t.web.destroy()
        }
        tabs.clear()
        pages.clear(); pageView = null
        newTab(home())
    }

    /* ================= Bookmark & riwayat ================= */

    private fun getList(key: String): JSONArray = try { JSONArray(prefs.getString(key, "[]")) } catch (e: Exception) { JSONArray() }

    private fun addBookmark() {
        val t = tabs[cur]
        if (t.url.startsWith(ASSET)) { toast("Halaman ini tidak bisa di-bookmark"); return }
        val a = getList("bm")
        for (i in 0 until a.length()) if (a.getJSONObject(i).optString("u") == t.url) { toast("Sudah ada di bookmark"); return }
        val n = JSONArray()
        n.put(JSONObject().put("t", t.title.ifEmpty { t.url }).put("u", t.url).put("ts", System.currentTimeMillis()))
        for (i in 0 until a.length()) n.put(a.getJSONObject(i))
        prefs.edit().putString("bm", n.toString()).apply()
        toast("Bookmark ditambahkan")
    }

    private fun addHistory(t: String, u: String) {
        if (incognito) return
        val old = getList("hist")
        val n = JSONArray()
        n.put(JSONObject().put("t", t.ifEmpty { u }).put("u", u).put("ts", System.currentTimeMillis()))
        var i = 0
        while (i < old.length() && n.length() < 300) {
            val o = old.getJSONObject(i)
            if (o.optString("u") != u) n.put(o)
            i++
        }
        prefs.edit().putString("hist", n.toString()).apply()
    }

    /* ================= Dialog lain ================= */

    private fun share(text: String) {
        val i = Intent(Intent.ACTION_SEND).setType("text/plain").putExtra(Intent.EXTRA_TEXT, text)
        startActivity(Intent.createChooser(i, "Bagikan"))
    }

    private fun findDialog() {
        val e = EditText(this).apply { isSingleLine = true; setTextColor(cFg) }
        dlg().setTitle("Cari di halaman").setView(e)
            .setPositiveButton("Cari") { _, _ -> cw().findAllAsync(e.text.toString()) }
            .setNegativeButton("Hapus sorotan") { _, _ -> cw().clearMatches() }.show()
    }

    private fun clearDialog() {
        dlg().setMessage("Hapus riwayat, cookie, dan cache?")
            .setPositiveButton("Hapus") { _, _ ->
                CookieManager.getInstance().removeAllCookies(null)
                WebStorage.getInstance().deleteAllData()
                tabs.forEach { it.web.clearCache(true); it.web.clearHistory() }
                prefs.edit().remove("hist").apply()
                toast("Data dihapus")
            }.setNegativeButton("Batal", null).show()
    }
}

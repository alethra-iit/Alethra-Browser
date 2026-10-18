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
import android.content.pm.ActivityInfo
import android.content.pm.PackageManager
import android.content.res.ColorStateList
import android.content.res.Configuration
import android.graphics.Bitmap
import android.graphics.BitmapFactory
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
import android.util.Base64
import android.util.Rational
import android.security.keystore.KeyGenParameterSpec
import android.security.keystore.KeyProperties
import android.app.SearchManager
import java.security.KeyStore
import javax.crypto.Cipher
import javax.crypto.KeyGenerator
import javax.crypto.SecretKey
import javax.crypto.spec.GCMParameterSpec
import android.text.TextUtils
import android.text.TextWatcher
import android.text.format.Formatter
import android.view.ContextThemeWrapper
import android.view.Gravity
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.webkit.CookieManager
import android.net.http.SslError
import android.webkit.GeolocationPermissions
import android.webkit.HttpAuthHandler
import android.webkit.JsPromptResult
import android.webkit.JsResult
import android.webkit.PermissionRequest
import android.webkit.SslErrorHandler
import android.webkit.JavascriptInterface
import android.webkit.URLUtil
import android.webkit.ValueCallback
import android.webkit.WebChromeClient
import android.webkit.WebResourceError
import android.webkit.WebResourceRequest
import android.webkit.WebResourceResponse
import android.webkit.WebSettings
import android.webkit.WebStorage
import android.webkit.WebView
import android.webkit.WebViewClient
import android.webkit.RenderProcessGoneDetail
import android.widget.EditText
import android.widget.FrameLayout
import android.widget.HorizontalScrollView
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.PopupMenu
import android.widget.PopupWindow
import android.widget.ProgressBar
import android.widget.ScrollView
import android.widget.SeekBar
import android.widget.Switch
import android.widget.TextView
import android.widget.Toast
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
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
        const val PERM_REQ = 8
        const val ROLE_REQ = 9
        const val WALL_REQ = 10
        const val LOCK_REQ = 11
        const val VOICE_REQ = 8
        const val JS_PLAYING = "(function(){var v=document.querySelectorAll('video');for(var i=0;i<v.length;i++){if(!v[i].paused&&!v[i].ended&&v[i].readyState>2)return 1}return 0})()"
        const val JS_FILL = "(function(){var v=document.querySelectorAll('video'),t=null,i;for(i=0;i<v.length;i++){if(!v[i].paused){t=v[i];break}}if(!t)t=v[0];if(!t)return;var o=document.getElementById('__alpip');if(o)o.remove();var p=t;while(p){p.classList.add('__ala');p=p.parentElement}var s=document.createElement('style');s.id='__alpip';s.textContent='html,body{overflow:hidden!important;background:#000!important;margin:0!important;width:100%!important;height:100%!important}body *{visibility:hidden!important}.__ala{transform:none!important;contain:none!important;filter:none!important;perspective:none!important;will-change:auto!important;clip-path:none!important;backdrop-filter:none!important}video.__alv{visibility:visible!important;position:fixed!important;left:0!important;top:0!important;right:0!important;bottom:0!important;width:100vw!important;height:100vh!important;max-width:none!important;max-height:none!important;margin:0!important;z-index:2147483647!important;background:#000!important;object-fit:contain!important;object-position:center center!important}';document.head.appendChild(s);var m=document.querySelector('meta[name=viewport]');if(m){if(m.getAttribute('data-alo')===null)m.setAttribute('data-alo',m.getAttribute('content')||'')}else{m=document.createElement('meta');m.name='viewport';m.setAttribute('data-alo','');m.setAttribute('data-alnew','1');document.head.appendChild(m)}m.setAttribute('content','width=device-width,initial-scale=1,minimum-scale=1,maximum-scale=1');t.classList.add('__alv');if(!HTMLMediaElement.prototype.__alp){var op=HTMLMediaElement.prototype.pause;HTMLMediaElement.prototype.__alp=1;HTMLMediaElement.prototype.pause=function(){if(window.__alMini&&!window.__alUP&&this.classList.contains('__alv'))return;return op.apply(this,arguments)}}window.__alUP=false;window.__alMini=1;if(!t.__alg){t.__alg=1;t.addEventListener('pause',function(){if(window.__alUP||t.ended||!document.getElementById('__alpip'))return;setTimeout(function(){if(!window.__alUP&&t.paused&&document.getElementById('__alpip'))t.play().catch(function(){})},60)})}window.dispatchEvent(new Event('resize'))})()"
        const val JS_RESTORE = "(function(){window.__alUP=true;window.__alMini=0;var s=document.getElementById('__alpip');if(s)s.remove();var m=document.querySelector('meta[data-alo]');if(m){if(m.getAttribute('data-alnew'))m.remove();else{m.setAttribute('content',m.getAttribute('data-alo'));m.removeAttribute('data-alo')}}var a=document.querySelectorAll('.__ala,.__alv');for(var i=0;i<a.length;i++){a[i].classList.remove('__ala');a[i].classList.remove('__alv')}})()"
        const val JS_TOGGLE = "(function(){var v=document.querySelector('video.__alv')||document.querySelector('video');if(!v)return;if(v.paused){window.__alUP=false;v.play()}else{window.__alUP=true;v.pause()}})()"
        const val JS_PAUSE = "(function(){var v=document.querySelectorAll('video');for(var i=0;i<v.length;i++)v[i].pause()})()"
        const val MP = ViewGroup.LayoutParams.MATCH_PARENT
        const val WC = ViewGroup.LayoutParams.WRAP_CONTENT
    }

    class Tab(val web: WebView, var title: String = "Tab baru", var url: String = HOME, var icon: Bitmap? = null) { var zoom = 1f; var adHandler: androidx.webkit.ScriptHandler? = null; var trOn = false; var pending: String? = null; var thumb: Bitmap? = null; var sadHandler: androidx.webkit.ScriptHandler? = null; var incog = false; var uaFix = false; var uaFixAt = 0L; var ntHandler: androidx.webkit.ScriptHandler? = null }
    class Closed(val title: String, val url: String)
    class Dl(val id: Long, val name: String, val status: Int, val total: Long, val sofar: Long, val mime: String?, val reason: Int = 0, val uri: String? = null)

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
    private var fab: FrameLayout? = null
    private lateinit var srl: SwipeRefreshLayout
    private val sslOk = HashSet<String>()
    private var sslShowing = false
    private var pwDlg = false
    private var permCb: ((Boolean) -> Unit)? = null
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
    private var cAcc = Color.BLACK
    private var cOnAcc = Color.WHITE
    private var cBarBg = Color.WHITE
    private var cBarFg = Color.BLACK
    private var cBarSub = Color.GRAY
    private var cBarSurf = Color.WHITE

    // Navigasi halaman
    private val pages = mutableListOf<String>()
    private var pageView: View? = null
    private var incognito = false
    private var incogWeak = false
    private var lineView: View? = null
    private lateinit var host: FrameLayout
    private var miniTab: Tab? = null
    private var miniFrame: FrameLayout? = null
    private var videoPlaying = false
    private var pipActive = false
    private var fsLayer: LinearLayout? = null
    private var fsRotBtn: View? = null
    private var trJs: String? = null
    private val trPool = ThreadPoolExecutor(2, 2, 0L, TimeUnit.SECONDS, ArrayBlockingQueue<Runnable>(40), ThreadPoolExecutor.DiscardOldestPolicy())
    private val trLangs = arrayOf("id", "en", "ja", "ko", "zh-CN", "es", "ar", "hi",
        "zh-TW", "fr", "de", "pt", "ru", "it", "nl", "tr", "th", "vi", "ms", "jv", "su", "tl",
        "bn", "ur", "ta", "fa", "he", "pl", "uk", "el", "sv", "ro", "cs", "hu", "da", "fi", "no", "sw")
    private val trNames = arrayOf("Indonesia", "Inggris", "Jepang", "Korea", "Mandarin", "Spanyol", "Arab", "Hindi",
        "Mandarin (Tradisional)", "Prancis", "Jerman", "Portugis", "Rusia", "Italia", "Belanda", "Turki", "Thai", "Vietnam", "Melayu", "Jawa", "Sunda", "Filipino",
        "Bengali", "Urdu", "Tamil", "Persia", "Ibrani", "Polandia", "Ukraina", "Yunani", "Swedia", "Rumania", "Ceko", "Hungaria", "Denmark", "Finlandia", "Norwegia", "Swahili")
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
        // Pemulihan: wallpaper yang bikin aplikasi berhenti dimatikan otomatis
        if (prefs.getInt("wallfix", 0) < 2 || prefs.getBoolean("wallpending", false)) {
            try { wallFile().delete() } catch (e: Exception) {}
            prefs.edit().remove("wallv").putBoolean("wallpending", false).putInt("wallfix", 2).apply()
        }

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
        srl = SwipeRefreshLayout(this)
        srl.addView(container, ViewGroup.LayoutParams(MP, MP))
        srl.setOnChildScrollUpCallback { _, _ ->
            !bool("pull", true) || pageView != null || customView != null || onHome() ||
                (tabs.getOrNull(cur)?.web?.canScrollVertically(-1) ?: true)
        }
        srl.setOnRefreshListener {
            val w = tabs.getOrNull(cur)?.web
            if (w == null) srl.isRefreshing = false
            else { w.reload(); handler.postDelayed({ srl.isRefreshing = false }, 5000) }
        }
        host.addView(srl, FrameLayout.LayoutParams(MP, MP))
        nav = LinearLayout(this).apply { orientation = LinearLayout.HORIZONTAL }

        setContentView(root)
        try { android.net.http.HttpResponseCache.install(java.io.File(cacheDir, "img"), 60L * 1024 * 1024) } catch (e: Exception) {}
        CookieManager.getInstance().setAcceptCookie(true)
        wipeIncognitoData()
        accumulateData()
        defaultUa = WebSettings.getDefaultUserAgent(this)
        applyChrome()
        migratePerms()
        migrateSiteKeys()
        if (lockMode() == "app") showLock()
        if (bool("clearexit", false) && prefs.getBoolean("clearpend", false)) { clearBrowsingData(); prefs.edit().putBoolean("clearpend", false).apply() }
        val extra = intentTarget(intent)
        var started = false
        val begin = {
            if (!started) {
                started = true
                if (!isFinishing && tabs.isEmpty() && !restoreSession(extra)) newTab(extra ?: home())
            }
        }
        applyDoh { begin() }
        handler.postDelayed({ begin() }, 1500)
    }

    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)
        if (prefs.getString("theme", "system") == "system") { applyChrome(); reloadHomes() }
        if (customView != null) updateFsLayout()
    }

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        intentTarget(intent)?.let { pages.clear(); newTab(it) }
    }

    override fun onActivityResult(rc: Int, res: Int, data: Intent?) {
        if (rc == FILE_REQ || rc == WALL_REQ || rc == VOICE_REQ) skipNextLock = true
        when (rc) {
            LOCK_REQ -> { skipNextLock = true; val cb = authCb; authCb = null; cb?.invoke(res == RESULT_OK) }
            FILE_REQ -> {
                fileCb?.onReceiveValue(WebChromeClient.FileChooserParams.parseResult(res, data))
                fileCb = null
            }
            WALL_REQ -> { val u = if (res == RESULT_OK) data?.data else null; if (u != null) saveWall(u) }
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
            lockView != null -> moveTaskToBack(true)
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
        if (lockMode() != "off") window.addFlags(android.view.WindowManager.LayoutParams.FLAG_SECURE)
        saveSession()
        accumulateData()
        prefs.edit().putLong("adsTotal", adCount.get()).apply()
        if (!pipActive) tabs.getOrNull(cur)?.web?.onPause()
    }

    override fun onResume() {
        super.onResume()
        updateSecureFlag()
        if (lockView == null) tabs.getOrNull(cur)?.web?.onResume()
        handler.removeCallbacks(videoPoll)
        handler.postDelayed(videoPoll, 1000)
    }

    override fun onUserLeaveHint() {
        super.onUserLeaveHint()
        if (bool("pip", true) && pipMode() == "out" && (videoPlaying || customView != null)) enterPip()
    }

    override fun onPictureInPictureModeChanged(isInPictureInPictureMode: Boolean, newConfig: Configuration) {
        super.onPictureInPictureModeChanged(isInPictureInPictureMode, newConfig)
        pipActive = isInPictureInPictureMode
        applyFullUi()
        val w = tabs.getOrNull(cur)?.web
        if (isInPictureInPictureMode) {
            top.visibility = View.GONE
            if (customView == null) w?.evaluateJavascript(JS_FILL, null)
        } else {
            w?.evaluateJavascript(JS_RESTORE, null)
            updateChrome()
        }
        if (customView != null) updateFsLayout()
    }

    private fun pipMode() = prefs.getString("pipmode", "in") ?: "in"

    private fun autoMini(): Boolean {
        if (bool("pip", true) && pipMode() == "in" && videoPlaying && miniTab == null && tabs.isNotEmpty() && !onHome()) {
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
        val f = FrameLayout(this).apply { background = shape(Color.BLACK, 0) }
        f.addView(t.web, FrameLayout.LayoutParams(MP, MP))
        val ov = View(this)
        var sx = 0f; var sy = 0f; var bx = 0f; var by = 0f; var moved = false
        ov.setOnTouchListener { _, e ->
            when (e.actionMasked) {
                android.view.MotionEvent.ACTION_DOWN -> { sx = e.rawX; sy = e.rawY; bx = f.translationX; by = f.translationY; moved = false }
                android.view.MotionEvent.ACTION_MOVE -> {
                    val ddx = e.rawX - sx; val ddy = e.rawY - sy
                    if (!moved && (Math.abs(ddx) > dp(6) || Math.abs(ddy) > dp(6))) moved = true
                    if (moved) { f.translationX = bx + ddx; f.translationY = by + ddy }
                }
                android.view.MotionEvent.ACTION_UP -> {
                    if (!moved) t.web.evaluateJavascript(JS_TOGGLE, null)
                    else {
                        f.translationX = f.translationX.coerceIn(-f.left.toFloat(), (host.width - f.right).toFloat())
                        f.translationY = f.translationY.coerceIn(-f.top.toFloat(), (host.height - f.bottom).toFloat())
                    }
                }
            }
            true
        }
        f.addView(ov, FrameLayout.LayoutParams(MP, MP))
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
        if (!bool("pip", true) || tabs.isEmpty()) { videoPlaying = false; return }
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
    override fun onStart() {
        super.onStart()
        if (prefs.getBoolean("clearpend", false)) prefs.edit().putBoolean("clearpend", false).apply()
        if (skipNextLock) skipNextLock = false
        else if (lockView == null && !authBusy && !pipActive && lockApplies()) {
            val g = lockGraceMs()
            if (g == 0L || android.os.SystemClock.elapsedRealtime() - leftAt >= g) showLock()
        }
    }

    override fun onStop() {
        super.onStop()
        leftAt = android.os.SystemClock.elapsedRealtime()
        if (bool("clearexit", false)) prefs.edit().putBoolean("clearpend", true).apply()
    }

    override fun onDestroy() {
        if (isFinishing && bool("clearexit", false)) clearBrowsingData()
        closePdf()
        try { tts?.stop(); tts?.shutdown() } catch (e: Exception) {}
        doh?.stop()
        val hadIncog = tabs.any { it.incog }
        tabs.forEach { it.web.destroy() }
        if (hadIncog) wipeIncognitoData()
        super.onDestroy()
    }

    /* ================= Tema & kerangka ================= */

    private fun isDark(): Boolean = when (prefs.getString("theme", "system")) {
        "dark" -> true
        "light" -> false
        else -> (resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK) == Configuration.UI_MODE_NIGHT_YES
    }

    private fun dlg() = Dlg()

    private fun pressBg(): android.graphics.drawable.Drawable =
        android.graphics.drawable.RippleDrawable(ColorStateList.valueOf(cLine), null, ColorDrawable(Color.WHITE))

    private fun pressRow(t: String, color: Int, onClick: () -> Unit): TextView = TextView(this).apply {
        text = t; textSize = 15f * fontF(); setTextColor(color)
        typeface = Typeface.create("sans-serif-medium", Typeface.NORMAL)
        setPadding(dp(20), dp(14), dp(20), dp(14))
        background = pressBg()
        isClickable = true
        setOnClickListener { onClick() }
    }

    private fun isDanger(t: String) = t.startsWith("Hapus") || t.startsWith("Reset") || t.startsWith("Tetap") || t.startsWith("Tutup semua")

    private fun styleInputs(v: View) {
        if (v is EditText) {
            v.background = shape(cBg, 12, cLine, 1)
            v.setPadding(dp(14), dp(12), dp(14), dp(12))
            v.setTextColor(cFg); v.setHintTextColor(cSub); v.textSize = 15f
            (v.layoutParams as? ViewGroup.MarginLayoutParams)?.apply { topMargin = dp(4); bottomMargin = dp(4) }
        } else if (v is ViewGroup) {
            for (i in 0 until v.childCount) styleInputs(v.getChildAt(i))
        }
    }

    /** Dialog bergaya Alethra dengan API mirip AlertDialog.Builder */
    inner class Dlg {
        private var title: CharSequence? = null
        private var message: CharSequence? = null
        private var view: View? = null
        private var items: Array<out CharSequence>? = null
        private var onItem: ((android.content.DialogInterface, Int) -> Unit)? = null
        private var pos: CharSequence? = null
        private var posL: ((android.content.DialogInterface, Int) -> Unit)? = null
        private var neg: CharSequence? = null
        private var negL: ((android.content.DialogInterface, Int) -> Unit)? = null
        private var neu: CharSequence? = null
        private var neuL: ((android.content.DialogInterface, Int) -> Unit)? = null
        private var onCancel: ((android.content.DialogInterface) -> Unit)? = null
        private var onDismiss: ((android.content.DialogInterface) -> Unit)? = null
        private var dialog: Dialog? = null
        private var posBtn: View? = null

        fun setTitle(t: CharSequence?): Dlg { title = t; return this }
        fun setMessage(t: CharSequence?): Dlg { message = t; return this }
        fun setView(v: View): Dlg { view = v; return this }
        fun setItems(a: Array<out CharSequence>, l: ((android.content.DialogInterface, Int) -> Unit)?): Dlg { items = a; onItem = l; return this }
        fun setPositiveButton(t: CharSequence, l: ((android.content.DialogInterface, Int) -> Unit)?): Dlg { pos = t; posL = l; return this }
        fun setNegativeButton(t: CharSequence, l: ((android.content.DialogInterface, Int) -> Unit)?): Dlg { neg = t; negL = l; return this }
        fun setNeutralButton(t: CharSequence, l: ((android.content.DialogInterface, Int) -> Unit)?): Dlg { neu = t; neuL = l; return this }
        fun setOnCancelListener(l: ((android.content.DialogInterface) -> Unit)?): Dlg { onCancel = l; return this }
        fun setOnDismissListener(l: ((android.content.DialogInterface) -> Unit)?): Dlg { onDismiss = l; return this }

        fun create(): Dlg { if (dialog == null) build(); return this }
        fun show(): Dlg { if (dialog == null) build(); dialog?.show(); return this }
        fun dismiss() { dialog?.dismiss() }
        fun getButton(which: Int): View = (if (which == AlertDialog.BUTTON_POSITIVE) posBtn else null) ?: View(this@MainActivity)

        private fun build() {
            val d = Dialog(this@MainActivity, android.R.style.Theme_Material_Dialog_NoActionBar)
            d.requestWindowFeature(Window.FEATURE_NO_TITLE)
            dialog = d
            val card = LinearLayout(this@MainActivity).apply { orientation = LinearLayout.VERTICAL; background = shape(cSurf, 24, cLine, 1) }
            val body = LinearLayout(this@MainActivity).apply { orientation = LinearLayout.VERTICAL; setPadding(0, dp(22), 0, dp(6)) }
            val t = title?.toString().orEmpty()
            if (t.isNotEmpty()) body.addView(tv(t, 18f, cFg, true).apply { maxLines = 3 },
                lp(MP, WC).apply { setMargins(dp(20), 0, dp(20), dp(10)) })
            val m = message?.toString().orEmpty()
            if (m.isNotEmpty()) body.addView(tv(m, 14.5f, cSub).apply { setLineSpacing(0f, 1.15f) },
                lp(MP, WC).apply { setMargins(dp(20), 0, dp(20), dp(12)) })
            view?.let { v0 ->
                (v0.parent as? ViewGroup)?.removeView(v0)
                val v1: View = if (v0 is EditText) FrameLayout(this@MainActivity).apply {
                    setPadding(dp(20), 0, dp(20), 0); addView(v0, FrameLayout.LayoutParams(MP, WC))
                } else v0
                styleInputs(v1)
                body.addView(v1, lp(MP, WC).apply { bottomMargin = dp(8) })
            }
            items?.let { arr ->
                arr.forEachIndexed { i, c ->
                    if (i > 0) body.addView(View(this@MainActivity).apply { setBackgroundColor(cLine) }, lp(MP, 1).apply { leftMargin = dp(20); rightMargin = dp(20) })
                    val txt = c.toString()
                    body.addView(pressRow(txt, if (isDanger(txt)) Color.parseColor("#E5484D") else cFg) { d.dismiss(); onItem?.invoke(d, i) }, lp(MP, WC))
                }
            }
            val sv = object : ScrollView(this@MainActivity) {
                override fun onMeasure(w: Int, h: Int) {
                    super.onMeasure(w, MeasureSpec.makeMeasureSpec((resources.displayMetrics.heightPixels * 0.6f).toInt(), MeasureSpec.AT_MOST))
                }
            }.apply { isVerticalScrollBarEnabled = false; addView(body) }
            card.addView(sv, lp(MP, WC))

            if (pos != null || neg != null || neu != null) {
                val row = LinearLayout(this@MainActivity).apply {
                    orientation = LinearLayout.HORIZONTAL; gravity = Gravity.END or Gravity.CENTER_VERTICAL
                    setPadding(dp(12), dp(4), dp(16), dp(16))
                }
                fun btn(txt: CharSequence, primary: Boolean, l: ((android.content.DialogInterface, Int) -> Unit)?, which: Int): TextView {
                    val s0 = txt.toString()
                    return TextView(this@MainActivity).apply {
                        text = s0; textSize = 14.5f * fontF(); gravity = Gravity.CENTER; minimumHeight = dp(42)
                        typeface = Typeface.create("sans-serif-medium", Typeface.NORMAL)
                        setPadding(dp(20), dp(10), dp(20), dp(10))
                        isClickable = true
                        if (primary) {
                            val danger = isDanger(s0)
                            setTextColor(if (danger) Color.WHITE else cOnAcc)
                            background = shape(if (danger) Color.parseColor("#E5484D") else cAcc, 22)
                        } else {
                            setTextColor(cFg); background = pressBg()
                        }
                        setOnClickListener { l?.invoke(d, which); d.dismiss() }
                    }
                }
                neu?.let { row.addView(btn(it, false, neuL, android.content.DialogInterface.BUTTON_NEUTRAL), lp(WC, WC).apply { rightMargin = dp(6) }) }
                neg?.let { row.addView(btn(it, pos == null, negL, android.content.DialogInterface.BUTTON_NEGATIVE), lp(WC, WC).apply { rightMargin = dp(6) }) }
                pos?.let { val b = btn(it, true, posL, android.content.DialogInterface.BUTTON_POSITIVE); posBtn = b; row.addView(b, lp(WC, WC)) }
                card.addView(row, lp(MP, WC))
            } else {
                card.addView(View(this@MainActivity), lp(MP, dp(8)))
            }

            d.setContentView(card, ViewGroup.LayoutParams(MP, WC))
            d.setOnCancelListener { onCancel?.invoke(d) }
            d.setOnDismissListener { onDismiss?.invoke(d) }
            d.window?.apply {
                setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
                setLayout(minOf(resources.displayMetrics.widthPixels - dp(40), dp(420)), android.view.WindowManager.LayoutParams.WRAP_CONTENT)
                setGravity(Gravity.CENTER)
                clearFlags(android.view.WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE or android.view.WindowManager.LayoutParams.FLAG_ALT_FOCUSABLE_IM)
                addFlags(android.view.WindowManager.LayoutParams.FLAG_DIM_BEHIND)
                setDimAmount(0.6f)
                setSoftInputMode(android.view.WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE or
                    (if (view != null) android.view.WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE else 0))
            }
        }
    }

    private fun applyChrome() {
        dark = isDark()
        if (dark) {
            cBg = Color.parseColor("#0B0B0D"); cSurf = Color.parseColor("#17171A")
            cFg = Color.parseColor("#F4F4F5"); cSub = Color.parseColor("#8E8E96"); cLine = Color.parseColor("#26262A")
        } else {
            cBg = Color.WHITE; cSurf = Color.parseColor("#F4F4F6")
            cFg = Color.parseColor("#111113"); cSub = Color.parseColor("#6B6B73"); cLine = Color.parseColor("#E8E8EC")
        }
        if (dark && bool("amoled", false)) { cBg = Color.BLACK; cSurf = Color.parseColor("#121214"); cLine = Color.parseColor("#1E1E21") }
        val ac = accentColor()
        if (ac != null) { cAcc = ac; cOnAcc = if (lumOf(ac) > 150) Color.parseColor("#111113") else Color.WHITE } else { cAcc = cFg; cOnAcc = cBg }
        val bt = if (ac != null) (prefs.getString("bartint", "off") ?: "off") else "off"
        when (bt) {
            "soft" -> { cBarBg = mix(cBg, cAcc, 0.12f); cBarFg = cFg; cBarSub = cSub; cBarSurf = mix(cSurf, cAcc, 0.16f) }
            "full" -> { cBarBg = cAcc; cBarFg = cOnAcc; cBarSub = mix(cOnAcc, cAcc, 0.35f); cBarSurf = mix(cAcc, cOnAcc, 0.14f) }
            else -> { cBarBg = cBg; cBarFg = cFg; cBarSub = cSub; cBarSurf = cSurf }
        }
        val stColor = if (bool("barBottom", false)) cBg else cBarBg
        window.statusBarColor = stColor
        window.navigationBarColor = cBarBg
        uiFlags = 0
        if (Build.VERSION.SDK_INT >= 23 && lumOf(stColor) > 150) uiFlags = uiFlags or View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
        if (Build.VERSION.SDK_INT >= 26 && lumOf(cBarBg) > 150) uiFlags = uiFlags or View.SYSTEM_UI_FLAG_LIGHT_NAVIGATION_BAR
        if (customView == null) window.decorView.systemUiVisibility = uiFlags
        root.setBackgroundColor(cBg)
        omnibox.setTextColor(cBarFg)
        omnibox.setHintTextColor(cBarSub)
        top.setBackgroundColor(cBarBg)
        progress.progressTintList = ColorStateList.valueOf(cAcc)
        srl.setProgressBackgroundColorSchemeColor(cSurf)
        srl.setColorSchemeColors(cAcc)
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
        buildFab()
        applyFullUi()
        if (tabs.isNotEmpty()) updateChrome()
        if (pageView != null) renderPage()
    }

    /* ---------- Mode full ---------- */

    private fun fullOn() = bool("full", false)

    private fun applyFullUi() {
        val hideBars = pipActive || fullOn()
        nav.visibility = if (hideBars) View.GONE else View.VISIBLE
        lineView?.visibility = if (hideBars) View.GONE else View.VISIBLE
        fab?.visibility = if (fullOn() && !pipActive) View.VISIBLE else View.GONE
    }

    private fun setFull(on: Boolean) {
        setBool("full", on)
        applyFullUi()
        if (tabs.isNotEmpty()) updateChrome()
        toast(if (on) "Mode full aktif" else "Mode full nonaktif")
    }

    @android.annotation.SuppressLint("ClickableViewAccessibility")
    private fun buildFab() {
        fab?.let { host.removeView(it) }
        val f = FrameLayout(this).apply {
            background = shape(cSurf, 24, cLine, 1)
            elevation = dp(6).toFloat()
            alpha = 0.92f
            isClickable = true
            contentDescription = "Menu"
        }
        f.addView(icon(R.drawable.ic_more, 22, cFg), FrameLayout.LayoutParams(dp(22), dp(22), Gravity.CENTER))
        var dx = 0f; var dy = 0f; var moved = false
        f.setOnTouchListener { v, e ->
            when (e.actionMasked) {
                MotionEvent.ACTION_DOWN -> { dx = v.x - e.rawX; dy = v.y - e.rawY; moved = false; true }
                MotionEvent.ACTION_MOVE -> {
                    if (Math.abs(v.x - (e.rawX + dx)) > dp(6) || Math.abs(v.y - (e.rawY + dy)) > dp(6)) moved = true
                    if (moved) {
                        v.x = (e.rawX + dx).coerceIn(0f, (host.width - v.width).toFloat().coerceAtLeast(0f))
                        v.y = (e.rawY + dy).coerceIn(0f, (host.height - v.height).toFloat().coerceAtLeast(0f))
                    }
                    true
                }
                MotionEvent.ACTION_UP -> { if (!moved) showPageMenu(v); true }
                else -> true
            }
        }
        host.addView(f, FrameLayout.LayoutParams(dp(48), dp(48), Gravity.BOTTOM or Gravity.END).apply { setMargins(0, 0, dp(12), dp(88)) })
        fab = f
    }

    private fun urlDialog() {
        val e = EditText(this).apply {
            isSingleLine = true; setTextColor(cFg)
            hint = "Cari atau masukkan alamat web"; setHintTextColor(cSub)
            inputType = InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_URI
            imeOptions = EditorInfo.IME_ACTION_GO
            if (!onHome() && pageView == null) setText(tabs[cur].url)
            setSelectAllOnFocus(true)
        }
        val box = FrameLayout(this).apply {
            setPadding(dp(20), dp(8), dp(20), 0)
            addView(e, FrameLayout.LayoutParams(MP, WC))
        }
        val d = dlg().setTitle("Buka alamat").setView(box)
            .setPositiveButton("Buka") { _, _ ->
                val t = e.text.toString().trim()
                if (t.isNotEmpty()) { if (pageView != null) closePages(); go(t) }
            }
            .setNegativeButton("Batal", null).create()
        e.setOnEditorActionListener { _, id, _ ->
            if (id == EditorInfo.IME_ACTION_GO) { d.getButton(AlertDialog.BUTTON_POSITIVE).performClick(); true } else false
        }
        d.show()
        e.requestFocus()
    }


    private fun buildTop() {
        top.removeAllViews()
        (omnibox.parent as? ViewGroup)?.removeView(omnibox)
        val pill = LinearLayout(this).apply {
            orientation = LinearLayout.HORIZONTAL
            gravity = Gravity.CENTER_VERTICAL
            background = shape(cBarSurf, 22)
            minimumHeight = dp(44)
            setPadding(dp(14), 0, dp(14), 0)
        }
        pill.addView(icon(R.drawable.ic_search, 18, cBarSub), lp(dp(18), dp(18)).apply { rightMargin = dp(10) })
        pill.addView(omnibox, lp(0, WC, 1f))
        top.addView(pill, lp(0, WC, 1f))
        top.addView(iconBtn(R.drawable.ic_more, cBarFg) { showPageMenu(it) }, lp(dp(44), dp(44)))
    }

    private fun navSelected(): Int = when (pages.firstOrNull()) {
        null -> if (onHome()) 0 else -1
        "tabs" -> 1
        "bookmarks" -> 2
        "downloads" -> 3
        else -> 4
    }

    private fun buildNav() {
        nav.removeAllViews()
        nav.setBackgroundColor(cBarBg)
        val sel = navSelected()
        val items = listOf(
            "Beranda" to R.drawable.ic_home, "Tab" to R.drawable.ic_tab, "Bookmark" to R.drawable.ic_bookmark,
            "Unduhan" to R.drawable.ic_download, "Menu" to R.drawable.ic_menu)
        items.forEachIndexed { i, (label, res) ->
            val c = if (incognito && i == 1) cAcc else if (i == sel) cBarFg else cBarSub
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
            2 -> openRoot("bookmarks")
            3 -> openRoot("downloads")
            4 -> showMenuSheet()
        }
    }

    private fun updateChrome() {
        val showTop = pageView == null && !pipActive
        val showTopBar = showTop && !fullOn()
        top.visibility = if (showTopBar) View.VISIBLE else View.GONE
        if (!showTopBar) progress.visibility = View.GONE
        applyFullUi()
        buildNav()
    }

    private fun hideKb() {
        (getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager).hideSoftInputFromWindow(omnibox.windowToken, 0)
        omnibox.clearFocus()
    }

    /* ================= Pembantu tampilan ================= */

    private fun shape(color: Int, r: Int, strokeColor: Int = 0, strokeDp: Int = 0) = GradientDrawable().apply {
        setColor(color); cornerRadius = dp(r).toFloat() * radiusF()
        if (strokeDp > 0) setStroke(dp(strokeDp), strokeColor)
    }

    private fun icon(res: Int, size: Int = 22, color: Int = cFg): ImageView = ImageView(this).apply {
        setImageResource(res)
        imageTintList = ColorStateList.valueOf(color)
        layoutParams = LinearLayout.LayoutParams(dp(size), dp(size))
    }

    private fun actBtn(res: Int, label: String, color: Int = cFg, onClick: (View) -> Unit): FrameLayout =
        iconBtn(res, color, onClick).apply {
            contentDescription = label
            setOnLongClickListener { toast(label); true }
        }

    private fun iconBtn(res: Int, color: Int = cFg, onClick: (View) -> Unit): FrameLayout = FrameLayout(this).apply {
        isClickable = true
        addView(icon(res, 22, color), FrameLayout.LayoutParams(dp(22), dp(22), Gravity.CENTER))
        setOnClickListener { v -> onClick(v) }
    }

    private fun tv(t: String, sp: Float, color: Int = cFg, medium: Boolean = false) = TextView(this).apply {
        text = t; textSize = sp * fontF(); setTextColor(color)
        if (medium) typeface = Typeface.create("sans-serif-medium", Typeface.NORMAL)
    }

    private fun iconTile(res: Int, inverted: Boolean = false): FrameLayout = FrameLayout(this).apply {
        background = shape(if (inverted) cAcc else cLine, 12)
        addView(icon(res, 20, if (inverted) cOnAcc else cFg), FrameLayout.LayoutParams(dp(20), dp(20), Gravity.CENTER))
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
                setTextColor(if (i == selected) cOnAcc else cFg)
                background = shape(if (i == selected) cAcc else cSurf, 18)
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
        st.thumbTintList = ColorStateList(states, intArrayOf(cOnAcc, cSub))
        st.trackTintList = ColorStateList(states, intArrayOf(cAcc, cLine))
        st.setOnCheckedChangeListener { _, c -> onChange(c) }
        r.addView(col, lp(0, WC, 1f))
        r.addView(st, lp(WC, WC))
        r.setOnClickListener { st.toggle() }
        return r
    }

    private fun popup(anchor: View, items: List<String>, onPick: (Int) -> Unit) {
        val wpx = dp(220)
        val card = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            background = shape(cSurf, 16, cLine, 1)
            setPadding(0, dp(6), 0, dp(6))
        }
        val holder = FrameLayout(this).apply { setPadding(dp(10), dp(6), dp(10), dp(14)); addView(card, FrameLayout.LayoutParams(MP, WC)) }
        val pw = PopupWindow(holder, wpx + dp(20), WC, true)
        pw.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        items.forEachIndexed { i, t ->
            card.addView(pressRow(t, if (isDanger(t)) Color.parseColor("#E5484D") else cFg) { pw.dismiss(); onPick(i) }, lp(MP, WC))
        }
        pw.showAsDropDown(anchor, anchor.width - wpx - dp(10), -dp(4))
    }

    /* ================= Navigasi halaman ================= */

    private fun lockKeyFor(key: String): String? = when (key) { "bookmarks" -> "lockbm"; "pwlist" -> "lockpw"; else -> null }

    private fun openRoot(key: String) {
        val lk = lockKeyFor(key)
        if (lk != null && partLocked(lk) && !authFresh()) { authenticate("Buka kunci Alethra") { ok -> if (ok) openRootNow(key) }; return }
        openRootNow(key)
    }

    private fun openRootNow(key: String) {
        if (key == "tabs") tabs.getOrNull(cur)?.let { captureThumb(it) }
        pages.clear(); pages.add(key); renderPage()
    }
    private fun openPage(key: String) {
        val lk = lockKeyFor(key)
        if (lk != null && partLocked(lk) && !authFresh()) { authenticate("Buka kunci Alethra") { ok -> if (ok) openPageNow(key) }; return }
        openPageNow(key)
    }
    private fun openPageNow(key: String) { if (pages.lastOrNull() != key) pages.add(key); renderPage() }
    private fun closePages() { pages.clear(); renderPage() }
    private fun backPage() { if (pages.isNotEmpty()) pages.removeAt(pages.size - 1); renderPage() }

    private fun renderPage() {
        pageView?.let { container.removeView(it) }
        pageView = null
        val key = pages.lastOrNull()
        if (key != "pdfview") closePdf()
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
                "pwlist" -> pagePwList()
                "appearance" -> pageAppearance()
                "play" -> pagePlay()
                "trlang" -> pageTrLang()
                "pipmode" -> pagePipMode()
                "imgq" -> pageImgQ()
                "dlset" -> pageDlSet()
                "dlloc" -> pageDlLoc()
                "lock" -> pageLock()
                "doh" -> pageDoh()
                "offline" -> pageOffline()
                "pdfview" -> pagePdf()
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

    private val adCount by lazy { java.util.concurrent.atomic.AtomicLong(prefs.getLong("adsTotal", 0L)) }
    private var lastTraffic = -1L

    /** Tambahkan pemakaian data aplikasi sejak pencatatan terakhir ke total tersimpan. */
    private fun accumulateData() {
        try {
            val uid = android.os.Process.myUid()
            val now = android.net.TrafficStats.getUidRxBytes(uid).coerceAtLeast(0) + android.net.TrafficStats.getUidTxBytes(uid).coerceAtLeast(0)
            if (lastTraffic >= 0 && now >= lastTraffic) prefs.edit().putLong("dataTotal", prefs.getLong("dataTotal", 0L) + (now - lastTraffic)).apply()
            lastTraffic = now
        } catch (e: Exception) {}
    }

    private fun fmtBytes(b: Long): String {
        val l = Locale("id", "ID")
        return when {
            b < 1024L * 1024 -> String.format(l, "%d KB", b / 1024)
            b < 1024L * 1024 * 1024 -> String.format(l, "%.1f MB", b / 1048576.0)
            else -> String.format(l, "%.2f GB", b / 1073741824.0)
        }
    }

    private fun fmtCount(n: Long): String = String.format(Locale("id", "ID"), "%,d", n)



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
        accumulateData()
        val head = LinearLayout(this).apply { orientation = LinearLayout.VERTICAL; setPadding(dp(14), dp(4), dp(14), dp(10)) }
        fun stat(res: Int, value: String, label: String) {
            val row = LinearLayout(this).apply { orientation = LinearLayout.HORIZONTAL; gravity = Gravity.CENTER_VERTICAL }
            row.addView(icon(res, 22, cFg), lp(dp(22), dp(22)).apply { rightMargin = dp(14) })
            row.addView(tv(value, 15f, cFg, true), lp(WC, WC).apply { rightMargin = dp(6) })
            row.addView(tv(label, 14f, cSub), lp(WC, WC))
            head.addView(row, lp(MP, WC).apply { topMargin = dp(5); bottomMargin = dp(5) })
        }
        stat(R.drawable.ic_globe, fmtBytes(prefs.getLong("dataTotal", 0L)), "data digunakan")
        stat(R.drawable.ic_tab, tabs.size.toString(), "halaman terbuka")
        stat(R.drawable.ic_shield, fmtCount(adCount.get()), "iklan terblokir")
        sheet.addView(head, lp(MP, WC))
        val hidden = prefs.getStringSet("menuhide", emptySet()) ?: emptySet()
        val all = listOf(
            "newtab" to MI(R.drawable.ic_tab, "Tab baru") { autoMini(); pages.clear(); newTab(home()) },
            "incognito" to MI(R.drawable.ic_incognito, "Incognito", incognito) { setIncognito(!incognito) },
            "bookmark" to MI(R.drawable.ic_bookmark, "Bookmark") { openRoot("bookmarks") },
            "downloads" to MI(R.drawable.ic_download, "Unduhan") { openRoot("downloads") },
            "history" to MI(R.drawable.ic_history, "Histori") { openRoot("history") },
            "offline" to MI(R.drawable.ic_offline, "Halaman offline") { openRoot("offline") },
            "vpn" to MI(R.drawable.ic_shield, "VPN") { openVpn() },
            "custom" to MI(R.drawable.ic_sliders, "Sesuaikan menu") { customizeMenuDialog() },
            "settings" to MI(R.drawable.ic_settings, "Pengaturan") { openRoot("settings") },
            "quit" to MI(R.drawable.ic_power, "Quit Alethra") { finishAndRemoveTask() })
        val items = all.filter { it.first !in hidden || it.first == "custom" || it.first == "settings" }.map { it.second }
        sheet.addView(iconPanel(listOf(items), 3, 88, true, true) { d.dismiss() }, lp(MP, WC))
        d.setContentView(sheet)
        d.window?.apply {
            setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            setGravity(Gravity.BOTTOM)
            setLayout(MP, WC)
        }
        d.show()
    }

    private val menuKeys = listOf(
        "newtab" to "Tab baru", "incognito" to "Incognito", "bookmark" to "Bookmark", "downloads" to "Unduhan",
        "history" to "Histori", "offline" to "Halaman offline", "vpn" to "VPN", "quit" to "Quit Alethra")

    private fun customizeMenuDialog() {
        val col = LinearLayout(this).apply { orientation = LinearLayout.VERTICAL; setPadding(dp(4), dp(4), dp(4), dp(4)) }
        val hidden = (prefs.getStringSet("menuhide", emptySet()) ?: emptySet()).toMutableSet()
        menuKeys.forEach { (k, label) ->
            col.addView(toggleRow(label, "Tampilkan di Menu", k !in hidden) { c ->
                if (c) hidden.remove(k) else hidden.add(k)
                prefs.edit().putStringSet("menuhide", HashSet(hidden)).apply()
            })
        }
        dlg().setTitle("Sesuaikan menu").setView(col)
            .setPositiveButton("Selesai", null)
            .setNegativeButton("Setel ulang") { _, _ -> prefs.edit().remove("menuhide").apply(); toast("Menu dikembalikan") }
            .show()
    }

    private fun openVpn() {
        try { startActivity(Intent(android.provider.Settings.ACTION_VPN_SETTINGS)) }
        catch (e: Exception) { toast("Pengaturan VPN tidak tersedia di perangkat ini") }
    }

    /* ---------- Menu titik tiga di bar alamat ---------- */

    /* ---------- Cetak, mode baca ---------- */

    private fun printPage() {
        val w = tabs.getOrNull(cur)?.web ?: return
        try {
            val pm = getSystemService(PRINT_SERVICE) as android.print.PrintManager
            val name = tabs[cur].title.ifEmpty { "Halaman" }.replace(Regex("[\\\\/:*?\"<>|]"), "_").take(60)
            pm.print(name, w.createPrintDocumentAdapter(name), android.print.PrintAttributes.Builder().build())
        } catch (e: Exception) { toast("Tidak bisa mencetak halaman ini") }
    }

    private fun readerMode() {
        val w = tabs.getOrNull(cur)?.web ?: return
        val size = prefs.getInt("rdsize", 18).coerceIn(14, 28)
        val th = prefs.getString("rdtheme", null)?.takeIf { it == "light" || it == "sepia" || it == "dark" } ?: if (dark) "dark" else "light"
        val js = READER_JS.replace("__SIZE__", size.toString()).replace("__THEME__", th).replace("__RATE__", prefs.getFloat("ttsrate", 1f).toString())
        w.evaluateJavascript(js) { r -> if (r == "\"no\"") toast("Halaman ini tidak cocok untuk mode baca") }
    }

    private val READER_JS = """
(function(){
 var SIZE=__SIZE__,THEME='__THEME__';
 var ex=document.getElementById('__alrd');
 if(ex){if(ex.__close)ex.__close();return 'off';}
 function txt(e){return (e.innerText||'').replace(/\s+/g,' ').trim();}
 var cands=document.querySelectorAll('article,main,[role=main],section,div'),scores=[],bs=0,i,j,best=null;
 var lim=Math.min(cands.length,600);
 for(i=0;i<lim;i++){
  var el=cands[i],ps=el.querySelectorAll('p'),s=0;
  if(ps.length<2){scores.push(0);continue;}
  for(j=0;j<ps.length;j++){var t=txt(ps[j]);if(t.length>60)s+=Math.min(t.length,300);}
  var all=txt(el).length||1,ln=0,as=el.querySelectorAll('a');
  for(j=0;j<as.length;j++)ln+=txt(as[j]).length;
  s=s*(1-Math.min(0.9,ln/all));
  scores.push(s);
  if(s>bs)bs=s;
 }
 if(bs<200)return 'no';
 var bn=1e9;
 for(i=0;i<lim;i++){if(scores[i]>=bs*0.85){var n=cands[i].getElementsByTagName('*').length;if(n<bn){bn=n;best=cands[i];}}}
 if(!best)return 'no';
 var ALLOW={P:1,H1:1,H2:1,H3:1,H4:1,H5:1,H6:1,UL:1,OL:1,LI:1,BLOCKQUOTE:1,PRE:1,CODE:1,STRONG:1,B:1,EM:1,I:1,BR:1,FIGURE:1,FIGCAPTION:1,TABLE:1,THEAD:1,TBODY:1,TR:1,TD:1,TH:1,A:1,HR:1};
 var SKIP={SCRIPT:1,STYLE:1,NAV:1,ASIDE:1,FOOTER:1,FORM:1,IFRAME:1,BUTTON:1,NOSCRIPT:1,SVG:1,VIDEO:1,AUDIO:1,CANVAS:1,SELECT:1,INPUT:1,TEXTAREA:1,HEADER:1,TEMPLATE:1,OBJECT:1,EMBED:1};
 var skipRe=/(comment|share|social|related|promo|advert|banner|sidebar|newsletter|subscribe|popup|modal|cookie|breadcrumb|toolbar)/i;
 function abs(u){try{var x=new URL(u,location.href);return(x.protocol==='http:'||x.protocol==='https:')?x.href:'';}catch(e){return '';}}
 function clean(src,dst){
  var c=src.firstChild;
  while(c){
   if(c.nodeType===3){dst.appendChild(document.createTextNode(c.nodeValue));}
   else if(c.nodeType===1){
    var tag=c.tagName.toUpperCase(),cls=(typeof c.className==='string'?c.className:'')+' '+(c.id||'');
    var hid=false;
    try{hid=c.hidden||getComputedStyle(c).display==='none';}catch(e){}
    if(SKIP[tag]||hid){}
    else if(skipRe.test(cls)&&txt(c).length<400){}
    else if(tag==='IMG'){
     var sr=abs(c.currentSrc||c.getAttribute('data-src')||c.src||'');
     var w=c.naturalWidth||parseInt(c.getAttribute('width')||'0',10);
     if(sr&&(w===0||w>=80)){var im=document.createElement('img');im.src=sr;im.alt=c.alt||'';dst.appendChild(im);}
    }
    else if(ALLOW[tag]){
     var nd=document.createElement(tag.toLowerCase());
     if(tag==='A'){var h=abs(c.getAttribute('href')||'');if(h)nd.href=h;}
     clean(c,nd);dst.appendChild(nd);
    }
    else{clean(c,dst);}
   }
   c=c.nextSibling;
  }
 }
 var h1=document.querySelector('article h1')||document.querySelector('h1');
 var title=(h1&&txt(h1))||document.title||'';
 var oldY=window.scrollY;
 var css='body.__alr>*:not(#__alrd){display:none!important}body.__alr{margin:0!important;padding:0!important;height:auto!important;overflow:visible!important}html{height:auto!important;overflow:visible!important}'+
 '#__alrd{display:block;box-sizing:border-box;min-height:100vh;font-family:Georgia,serif;text-align:left;margin:0;padding:0}'+
 '#__alrd *{box-sizing:border-box;max-width:100%}#__alrd.l{background:#fff;color:#1a1a1a}#__alrd.s{background:#f4ecd8;color:#3b3228}#__alrd.d{background:#121212;color:#d8d8d8}'+
 '#__alrd .b{position:sticky;top:0;display:flex;flex-wrap:wrap;gap:8px;justify-content:flex-end;padding:8px 12px;background:inherit;border-bottom:1px solid rgba(128,128,128,.25);z-index:2}'+
 '#__alrd .b button{border:0;padding:6px 11px;border-radius:14px;background:rgba(128,128,128,.2);color:inherit;font:600 14px sans-serif}'+
 '#__alrd .c{max-width:680px;margin:0 auto;padding:16px 20px 64px;line-height:1.7}'+
 '#__alrd h1{font-size:1.5em;line-height:1.25;margin:.4em 0}#__alrd h2{font-size:1.3em;margin:1.2em 0 .4em}#__alrd h3,#__alrd h4{font-size:1.15em;margin:1em 0 .3em}'+
 '#__alrd p,#__alrd ul,#__alrd ol{margin:0 0 1em}#__alrd a{color:inherit;text-decoration:underline}'+
 '#__alrd img{display:block;height:auto;margin:1em auto;border-radius:6px}#__alrd figure{margin:1em 0}#__alrd figcaption{font-size:.8em;opacity:.7;text-align:center}'+
 '#__alrd blockquote{border-left:3px solid rgba(128,128,128,.5);margin:1em 0;padding:0 1em;opacity:.9}'+
 '#__alrd pre{overflow:auto;background:rgba(128,128,128,.15);padding:12px;border-radius:6px;font-size:.85em}#__alrd table{border-collapse:collapse;display:block;overflow:auto}#__alrd td,#__alrd th{border:1px solid rgba(128,128,128,.4);padding:6px}';
 var sheet=null,st=null;
 try{sheet=new CSSStyleSheet();sheet.replaceSync(css);document.adoptedStyleSheets=(document.adoptedStyleSheets||[]).concat([sheet]);}
 catch(e){sheet=null;st=document.createElement('style');st.textContent=css;document.head.appendChild(st);}
 var wrap=document.createElement('div');wrap.id='__alrd';
 var bar=document.createElement('div');bar.className='b';
 var cont=document.createElement('div');cont.className='c';cont.style.fontSize=SIZE+'px';
 function applyTheme(){wrap.className=THEME==='dark'?'d':(THEME==='sepia'?'s':'l');}
 function save(){try{AlApp.readerPref(SIZE,THEME);}catch(e){}}
 function close(){
  try{AlApp.ttsStop();}catch(e){}
  ttsClear();
  document.body.classList.remove('__alr');
  wrap.remove();
  if(st)st.remove();
  if(sheet){document.adoptedStyleSheets=(document.adoptedStyleSheets||[]).filter(function(x){return x!==sheet;});}
  window.scrollTo(0,oldY);
 }
 wrap.__close=close;
 function mk(label,fn){var b=document.createElement('button');b.textContent=label;b.onclick=fn;bar.appendChild(b);return b;}
 mk('A-',function(){SIZE=Math.max(14,SIZE-2);cont.style.fontSize=SIZE+'px';save();});
 mk('A+',function(){SIZE=Math.min(28,SIZE+2);cont.style.fontSize=SIZE+'px';save();});
 mk('Tema',function(){THEME=THEME==='light'?'sepia':(THEME==='sepia'?'dark':'light');applyTheme();save();});
 var ttsOn=false,ttsIdx=0,ttsEls=[],ttsRate=__RATE__,ttsHi=null,tb=null,rb=null;
 var TS='h1,h2,h3,h4,p,li,blockquote,figcaption';
 function ttsClear(){if(ttsHi){ttsHi.style.background='';ttsHi=null;}}
 window.__alTts=function(i){ttsIdx=i;ttsClear();var e=ttsEls[i];if(e){ttsHi=e;e.style.background='rgba(255,193,7,.3)';try{e.scrollIntoView({block:'center',behavior:'smooth'});}catch(x){e.scrollIntoView();}}};
 window.__alTtsEnd=function(){ttsOn=false;ttsIdx=0;ttsClear();if(tb)tb.textContent='Baca';};
 function ttsPlay(){
  if(!ttsEls.length){ttsEls=[].slice.call(cont.querySelectorAll(TS)).filter(function(e){return !e.querySelector(TS)&&txt(e).length>1;});}
  if(!ttsEls.length)return;
  var arr=ttsEls.map(function(e){return txt(e);});
  ttsOn=true;tb.textContent='Jeda';
  try{AlApp.ttsStart(JSON.stringify(arr),ttsIdx,document.documentElement.lang||'',ttsRate);}catch(e){ttsOn=false;tb.textContent='Baca';}
 }
 function ttsPause(){ttsOn=false;tb.textContent='Lanjut';try{AlApp.ttsStop();}catch(e){}}
 tb=mk('Baca',function(){if(ttsOn)ttsPause();else ttsPlay();});
 rb=mk(ttsRate+'x',function(){
  var rs=[1,1.25,1.5,0.75],k=rs.indexOf(ttsRate);ttsRate=rs[(k+1)%rs.length];
  rb.textContent=ttsRate+'x';try{AlApp.ttsRate(ttsRate);}catch(e){}
  if(ttsOn)ttsPlay();
 });
 mk('Suara',function(){try{AlApp.ttsPick();}catch(e){}});
 window.__alTtsLang=function(){if(ttsOn)ttsPlay();};
 mk('Tutup',close);
 var ht=document.createElement('h1');ht.textContent=title;cont.appendChild(ht);
 var hs=document.createElement('div');hs.style.cssText='opacity:.6;font:13px sans-serif;margin-bottom:1em';hs.textContent=location.hostname;cont.appendChild(hs);
 clean(best,cont);
 var h1s=cont.querySelectorAll('h1');
 for(i=1;i<h1s.length;i++){if(txt(h1s[i])===title)h1s[i].remove();}
 wrap.appendChild(bar);wrap.appendChild(cont);
 applyTheme();
 document.body.appendChild(wrap);
 document.body.classList.add('__alr');
 window.scrollTo(0,0);
 return 'ok';
})()
"""

    /* ---------- Pembaca suara (text-to-speech) di mode baca ---------- */

    private var tts: android.speech.tts.TextToSpeech? = null
    private var ttsReady = false
    private var ttsFailed = false
    private var ttsWeb: WebView? = null
    private var ttsLast = -1
    private var ttsPending: (() -> Unit)? = null

    private fun ttsLangDialog(web: WebView) {
        val keys = listOf("auto") + trLangs.toList()
        val names = listOf("Otomatis (bahasa halaman)") + trNames.toList()
        val cur = keys.indexOf(prefs.getString("ttslang", "id")).coerceAtLeast(0)
        val labels = names.mapIndexed { i, n -> if (i == cur) "✓ $n" else n }.toTypedArray()
        dlg().setTitle("Bahasa suara").setItems(labels) { _, i ->
            prefs.edit().putString("ttslang", keys[i]).apply()
            try { web.evaluateJavascript("window.__alTtsLang&&window.__alTtsLang()", null) } catch (e: Exception) {}
        }.setNeutralButton("Pengaturan suara") { _, _ ->
            try { startActivity(Intent("com.android.settings.TTS_SETTINGS")) } catch (e: Exception) { toast("Pengaturan suara tidak tersedia") }
        }.show()
    }

    private fun ttsSpeak(web: WebView, json: String, start: Int, lang: String, rate: Float) {
        ttsWeb = web
        val run = {
            val t = tts
            if (t != null) {
                val arr = try { JSONArray(json) } catch (e: Exception) { JSONArray() }
                val pref = prefs.getString("ttslang", "id") ?: "id"
                val use = if (pref == "auto") lang else pref
                val loc = if (use.isNotEmpty()) Locale.forLanguageTag(use) else Locale.getDefault()
                val r = try { t.setLanguage(loc) } catch (e: Exception) { android.speech.tts.TextToSpeech.LANG_NOT_SUPPORTED }
                if (r == android.speech.tts.TextToSpeech.LANG_MISSING_DATA || r == android.speech.tts.TextToSpeech.LANG_NOT_SUPPORTED) {
                    toast("Suara bahasa ini belum terpasang, memakai suara bawaan perangkat")
                    try { t.setLanguage(Locale.getDefault()) } catch (e: Exception) {}
                }
                t.setSpeechRate(rate.coerceIn(0.5f, 2f))
                t.stop()
                ttsLast = arr.length() - 1
                for (i in start.coerceAtLeast(0) until arr.length()) {
                    val txt = arr.optString(i).take(3500)
                    if (txt.isNotBlank()) t.speak(txt, android.speech.tts.TextToSpeech.QUEUE_ADD, null, "al$i")
                }
            }
        }
        if (tts != null && ttsReady) { run(); return }
        ttsPending = run
        if (tts == null && !ttsFailed) {
            tts = android.speech.tts.TextToSpeech(this) { status ->
                runOnUiThread {
                    if (status == android.speech.tts.TextToSpeech.SUCCESS) {
                        ttsReady = true
                        tts?.setOnUtteranceProgressListener(object : android.speech.tts.UtteranceProgressListener() {
                            override fun onStart(id: String?) {
                                val i = id?.removePrefix("al")?.toIntOrNull() ?: return
                                runOnUiThread { try { ttsWeb?.evaluateJavascript("window.__alTts&&window.__alTts($i)", null) } catch (e: Exception) {} }
                            }
                            override fun onDone(id: String?) {
                                if (id?.removePrefix("al")?.toIntOrNull() == ttsLast)
                                    runOnUiThread { try { ttsWeb?.evaluateJavascript("window.__alTtsEnd&&window.__alTtsEnd()", null) } catch (e: Exception) {} }
                            }
                            @Deprecated("Deprecated in Java")
                            override fun onError(id: String?) {
                                runOnUiThread { try { ttsWeb?.evaluateJavascript("window.__alTtsEnd&&window.__alTtsEnd()", null) } catch (e: Exception) {} }
                            }
                        })
                        val p = ttsPending; ttsPending = null; p?.invoke()
                    } else {
                        ttsFailed = true
                        try { tts?.shutdown() } catch (e: Exception) {}
                        tts = null; ttsPending = null
                        toast("Pembaca suara tidak tersedia di perangkat ini")
                        try { ttsWeb?.evaluateJavascript("window.__alTtsEnd&&window.__alTtsEnd()", null) } catch (e: Exception) {}
                    }
                }
            }
        }
    }

    /* ---------- Kata sandi tersimpan ---------- */

    private val PW_JS = """
(function(){
 if(window.__alpw)return;window.__alpw=1;
 function pickUser(root,pw){
  var all=root.querySelectorAll('input'),u=null,i,t;
  for(i=0;i<all.length;i++){
   if(all[i]===pw)break;
   t=(all[i].type||'text').toLowerCase();
   if((t==='text'||t==='email'||t==='tel')&&all[i].offsetParent!==null)u=all[i];
  }
  return u;
 }
 function grab(root){
  var pws=root.querySelectorAll('input[type=password]'),pw=null,i;
  for(i=0;i<pws.length;i++){if(pws[i].value){pw=pws[i];break;}}
  if(!pw)return null;
  var u=pickUser(root,pw);
  return {u:u?u.value:'',p:pw.value};
 }
 function send(root){
  try{var c=grab(root);if(c&&window.AlApp)AlApp.savePw(location.href,c.u,c.p);}catch(e){}
 }
 document.addEventListener('submit',function(e){send(e.target||document);},true);
 document.addEventListener('click',function(e){
  var b=e.target&&e.target.closest?e.target.closest('button,input[type=submit],[role=button]'):null;
  if(!b)return;
  var label=(b.innerText||b.value||'')+'';
  if((b.form&&b.type==='submit')||/log\s*in|sign\s*in|masuk|login/i.test(label))send(b.form||document);
 },true);
 document.addEventListener('keydown',function(e){
  if(e.key==='Enter'){var t=e.target;if(t&&t.tagName==='INPUT')send(t.form||document);}
 },true);
 function setVal(el,v){
  var d=Object.getOwnPropertyDescriptor(HTMLInputElement.prototype,'value');
  d.set.call(el,v);
  el.dispatchEvent(new Event('input',{bubbles:true}));
  el.dispatchEvent(new Event('change',{bubbles:true}));
 }
 window.__alFill=function(u,p){
  var pws=document.querySelectorAll('input[type=password]'),pw=null,i;
  for(i=0;i<pws.length;i++){if(pws[i].offsetParent!==null&&!pws[i].value){pw=pws[i];break;}}
  if(!pw)return;
  var ue=pickUser(pw.form||document,pw);
  if(ue&&!ue.value&&u)setVal(ue,u);
  setVal(pw,p);
 };
})();
"""

    inner class AppBridge(private val web: WebView) {
        @JavascriptInterface
        fun savePw(url: String?, user: String?, pass: String?) {
            if (url == null || pass.isNullOrEmpty() || pass.length > 256 || (user?.length ?: 0) > 256) return
            runOnUiThread {
                val h = hostOf(url)
                if (h != null && h == hostOf(web.url)) offerSavePw(h, user ?: "", pass)
            }
        }

        @JavascriptInterface
        fun notifState(): String {
            val h = webHost[web] ?: return "denied"
            return when (sitePerm(h, "notif")) {
                2 -> "denied"
                1 -> if (notifAllowed()) "granted" else "default"
                else -> "default"
            }
        }

        @JavascriptInterface
        fun notifRequest(id: Int) {
            runOnUiThread {
                val h = webHost[web]
                fun reply(s: String) { try { web.evaluateJavascript("window.__alNotifRes&&window.__alNotifRes($id,'$s')", null) } catch (e: Exception) {} }
                if (h == null || isFinishing) { reply("denied"); return@runOnUiThread }
                when (sitePerm(h, "notif")) {
                    2 -> reply("denied")
                    1 -> askAndroidPerms(notifPerms(), false) { ok -> reply(if (ok) "granted" else "denied") }
                    else -> askSitePerm(h, h, "$h ingin mengirim notifikasi.", listOf("notif")) { ok ->
                        if (!ok) reply("denied") else askAndroidPerms(notifPerms(), false) { ok2 -> reply(if (ok2) "granted" else "denied") }
                    }
                }
            }
        }

        @JavascriptInterface
        fun notify(title: String?, body: String?, tag: String?) {
            val h = webHost[web] ?: return
            if (sitePerm(h, "notif") != 1) return
            runOnUiThread { showSiteNotif(h, title ?: "", body ?: "", tag ?: "") }
        }

        @JavascriptInterface
        fun ttsStart(json: String?, start: Int, lang: String?, rate: Float) {
            if (json == null || json.length > 600000) return
            runOnUiThread { ttsSpeak(web, json, start, lang ?: "", rate) }
        }

        @JavascriptInterface
        fun ttsPick() { runOnUiThread { ttsLangDialog(web) } }

        @JavascriptInterface
        fun ttsStop() { runOnUiThread { try { tts?.stop() } catch (e: Exception) {} } }

        @JavascriptInterface
        fun ttsRate(rate: Float) { prefs.edit().putFloat("ttsrate", rate.coerceIn(0.5f, 2f)).apply() }

        @JavascriptInterface
        fun readerPref(size: Int, theme: String?) {
            val th = if (theme == "light" || theme == "sepia" || theme == "dark") theme else "light"
            prefs.edit().putInt("rdsize", size.coerceIn(14, 28)).putString("rdtheme", th).apply()
        }
    }

    private fun pwKey(): SecretKey {
        val ks = KeyStore.getInstance("AndroidKeyStore"); ks.load(null)
        (ks.getKey("alethra_pw", null) as? SecretKey)?.let { return it }
        val kg = KeyGenerator.getInstance(KeyProperties.KEY_ALGORITHM_AES, "AndroidKeyStore")
        kg.init(KeyGenParameterSpec.Builder("alethra_pw", KeyProperties.PURPOSE_ENCRYPT or KeyProperties.PURPOSE_DECRYPT)
            .setBlockModes(KeyProperties.BLOCK_MODE_GCM)
            .setEncryptionPaddings(KeyProperties.ENCRYPTION_PADDING_NONE)
            .setKeySize(256).build())
        return kg.generateKey()
    }

    private fun pwEnc(plain: String): String {
        val c = Cipher.getInstance("AES/GCM/NoPadding")
        c.init(Cipher.ENCRYPT_MODE, pwKey())
        val ct = c.doFinal(plain.toByteArray(Charsets.UTF_8))
        return Base64.encodeToString(c.iv + ct, Base64.NO_WRAP)
    }

    private fun pwDec(s: String): String? = try {
        val b = Base64.decode(s, Base64.NO_WRAP)
        val c = Cipher.getInstance("AES/GCM/NoPadding")
        c.init(Cipher.DECRYPT_MODE, pwKey(), GCMParameterSpec(128, b.copyOfRange(0, 12)))
        String(c.doFinal(b, 12, b.size - 12), Charsets.UTF_8)
    } catch (e: Exception) { null }

    private fun pwList(): JSONArray = getList("pw")

    private fun pwFind(h: String, user: String): JSONObject? {
        val a = pwList()
        for (i in 0 until a.length()) {
            val o = a.optJSONObject(i) ?: continue
            if (o.optString("h") == h && o.optString("u") == user) return o
        }
        return null
    }

    private fun pwPut(h: String, user: String, pass: String) {
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

    private fun pwDelete(h: String, user: String) {
        val old = pwList()
        val n = JSONArray()
        for (i in 0 until old.length()) {
            val o = old.optJSONObject(i) ?: continue
            if (o.optString("h") == h && o.optString("u") == user) continue
            n.put(o)
        }
        prefs.edit().putString("pw", n.toString()).apply()
    }

    private fun offerSavePw(h: String, user: String, pass: String) {
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

    private fun pwAutofill(w: WebView, url: String) {
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

    private fun injectPw(w: WebView, url: String) {
        if (!url.startsWith("http")) return
        w.evaluateJavascript(PW_JS, null)
        pwAutofill(w, url)
        handler.postDelayed({ pwAutofill(w, url) }, 800)
        handler.postDelayed({ pwAutofill(w, url) }, 2500)
    }

    private fun pagePwList(): View {
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
                    val cm = getSystemService(CLIPBOARD_SERVICE) as ClipboardManager
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

    /* ---------- Hapus data, browser default, intent masuk ---------- */

    private fun clearBrowsingData() {
        try {
            CookieManager.getInstance().removeAllCookies(null)
            CookieManager.getInstance().flush()
            WebStorage.getInstance().deleteAllData()
            if (tabs.isEmpty()) { val tmp = WebView(this); tmp.clearCache(true); tmp.destroy() }
            else tabs.forEach { it.web.clearCache(true); it.web.clearHistory(); it.web.clearFormData() }
        } catch (e: Exception) {}
        prefs.edit().remove("hist").remove("session").remove("sessionCur").remove("permok").apply()
    }

    private fun isDefaultBrowser(): Boolean = try {
        val i = Intent(Intent.ACTION_VIEW, Uri.parse("https://example.com"))
        packageManager.resolveActivity(i, PackageManager.MATCH_DEFAULT_ONLY)?.activityInfo?.packageName == packageName
    } catch (e: Exception) { false }

    private fun requestDefaultBrowser() {
        try {
            if (Build.VERSION.SDK_INT >= 29) {
                val rm = getSystemService(android.app.role.RoleManager::class.java)
                if (rm != null && rm.isRoleAvailable(android.app.role.RoleManager.ROLE_BROWSER)) {
                    if (rm.isRoleHeld(android.app.role.RoleManager.ROLE_BROWSER)) { toast("Alethra sudah menjadi browser default"); return }
                    startActivityForResult(rm.createRequestRoleIntent(android.app.role.RoleManager.ROLE_BROWSER), ROLE_REQ)
                    return
                }
            }
            startActivity(Intent(android.provider.Settings.ACTION_MANAGE_DEFAULT_APPS_SETTINGS))
        } catch (e: Exception) {
            try { startActivity(Intent(android.provider.Settings.ACTION_SETTINGS)) } catch (e2: Exception) { toast("Buka Pengaturan > Aplikasi > Aplikasi default") }
        }
    }

    private fun intentTarget(i: Intent?): String? {
        if (i == null) return null
        return when (i.action) {
            Intent.ACTION_VIEW -> i.dataString
            Intent.ACTION_WEB_SEARCH -> i.getStringExtra(SearchManager.QUERY)?.trim()?.takeIf { it.isNotEmpty() }?.let { toUrl(it) }
            Intent.ACTION_SEND -> i.getStringExtra(Intent.EXTRA_TEXT)?.trim()?.takeIf { it.isNotEmpty() }?.let { t ->
                val m = Regex("https?://\\S+").find(t)
                if (m != null) m.value else toUrl(t.lines().first())
            }
            else -> null
        }
    }

    /* ---------- Izin situs, dialog JS, sesi tab ---------- */

    private fun sslReason(e: SslError?): String = when (e?.primaryError) {
        SslError.SSL_UNTRUSTED -> "Sertifikat situs tidak dipercaya."
        SslError.SSL_EXPIRED -> "Sertifikat situs sudah kedaluwarsa."
        SslError.SSL_IDMISMATCH -> "Nama situs tidak cocok dengan sertifikat."
        SslError.SSL_NOTYETVALID -> "Sertifikat situs belum berlaku."
        SslError.SSL_DATE_INVALID -> "Tanggal sertifikat situs tidak valid."
        else -> "Sertifikat situs tidak valid."
    }

    private fun jsTitle(url: String?) = hostOf(url) ?: "Halaman web"

    private fun askAndroidPerms(perms: Array<String>, anyOf: Boolean, cb: (Boolean) -> Unit) {
        val missing = perms.filter { checkSelfPermission(it) != PackageManager.PERMISSION_GRANTED }
        if (missing.isEmpty() || (anyOf && missing.size < perms.size)) { cb(true); return }
        permCb?.invoke(false)
        permCb = cb
        requestPermissions(missing.toTypedArray(), PERM_REQ)
    }

    override fun onRequestPermissionsResult(code: Int, perms: Array<out String>, results: IntArray) {
        super.onRequestPermissionsResult(code, perms, results)
        if (code == PERM_REQ) {
            val cb = permCb; permCb = null
            cb?.invoke(results.isNotEmpty() && (results.any { it == PackageManager.PERMISSION_GRANTED }))
        }
    }

    private fun saveSession() {
        if (tabs.isEmpty()) return
        val keep = tabs.filter { t0 -> val u = t0.pending ?: t0.url; !t0.incog && !u.startsWith("data:") && u != "about:blank" }
        if (keep.isEmpty()) return
        val arr = JSONArray()
        keep.forEach { t ->
            val u = t.pending ?: t.url
            arr.put(JSONObject().put("u", if (u.startsWith(ASSET)) "" else u).put("t", t.title))
        }
        val ci = keep.indexOf(tabs.getOrNull(cur)).coerceAtLeast(0)
        prefs.edit().putString("session", arr.toString()).putInt("sessionCur", ci).apply()
    }

    private fun restoreSession(extra: String?): Boolean {
        if (!bool("restore", true)) return false
        val arr = try { JSONArray(prefs.getString("session", "[]")) } catch (e: Exception) { JSONArray() }
        val saved = (0 until arr.length()).mapNotNull { arr.optJSONObject(it) }
        if (saved.isEmpty()) return false
        saved.forEach { o ->
            val u = o.optString("u").ifEmpty { home() }
            val t = newTab(null, false)
            t.url = u
            t.title = o.optString("t").ifEmpty { u }
            t.pending = u
        }
        selectTab(prefs.getInt("sessionCur", 0).coerceIn(0, tabs.size - 1))
        if (extra != null) { pages.clear(); newTab(extra) }
        return true
    }

    private class MI(val res: Int, val label: String, val checked: Boolean? = null, val action: () -> Unit)

    /** Panel berisi icon saja (tanpa teks). Tekan lama untuk melihat nama menu. */
    private fun iconPanel(groups: List<List<MI>>, cols: Int, tile: Int, fill: Boolean, boxed: Boolean = false, done: () -> Unit): LinearLayout {
        val col = LinearLayout(this).apply { orientation = LinearLayout.VERTICAL }
        groups.filter { it.isNotEmpty() }.forEachIndexed { gi, g ->
            if (gi > 0) col.addView(View(this).apply { setBackgroundColor(cLine) }, lp(MP, 1).apply { setMargins(dp(6), dp(4), dp(6), dp(4)) })
            g.chunked(cols).forEach { chunk ->
                val row = LinearLayout(this).apply { orientation = LinearLayout.HORIZONTAL }
                chunk.forEach { it0 ->
                    val on = it0.checked == true
                    val t = FrameLayout(this).apply {
                        background = if (on) shape(cAcc, if (boxed) 16 else 14) else if (boxed) shape(cBg, 16) else null
                        isClickable = true
                        contentDescription = it0.label
                        setOnClickListener { done(); it0.action() }
                        setOnLongClickListener { toast(it0.label); true }
                    }
                    val mg = if (boxed) 5 else 2
                    val fg = if (on) cOnAcc else cFg
                    if (boxed) {
                        val col2 = LinearLayout(this).apply { orientation = LinearLayout.VERTICAL; gravity = Gravity.CENTER_HORIZONTAL }
                        col2.addView(icon(it0.res, 26, fg), lp(dp(26), dp(26)))
                        col2.addView(tv(it0.label, 11f, fg, true).apply { maxLines = 1; gravity = Gravity.CENTER; ellipsize = TextUtils.TruncateAt.END }, lp(WC, WC).apply { topMargin = dp(6) })
                        t.addView(col2, FrameLayout.LayoutParams(WC, WC, Gravity.CENTER))
                    } else {
                        t.addView(icon(it0.res, 22, fg), FrameLayout.LayoutParams(dp(22), dp(22), Gravity.CENTER))
                    }
                    row.addView(t, (if (fill) lp(0, dp(tile), 1f) else lp(dp(tile), dp(tile))).apply { setMargins(dp(mg), dp(mg), dp(mg), dp(mg)) })
                }
                col.addView(row, lp(if (fill) MP else WC, WC))
            }
        }
        return col
    }

    private fun showPageMenu(anchor: View) {
        val onPage = pageView == null && !onHome()
        val pageUrl = tabs.getOrNull(cur)?.url
        var pw: PopupWindow? = null
        val done = { pw?.dismiss(); Unit }

        val navGroup = if (fullOn()) listOf(
            MI(R.drawable.ic_globe, "Buka alamat") { urlDialog() },
            MI(R.drawable.ic_home, "Beranda") { navTap(0) },
            MI(R.drawable.ic_tab, "Tab") { navTap(1) },
            MI(R.drawable.ic_bookmark, "Bookmark") { navTap(2) },
            MI(R.drawable.ic_download, "Unduhan") { navTap(3) },
            MI(R.drawable.ic_menu, "Menu") { navTap(4) }) else emptyList()

        val pageGroup = if (onPage) listOf(
            MI(R.drawable.ic_rotate, "Muat ulang") { cw().reload() },
            MI(R.drawable.ic_forward, "Maju") { if (cw().canGoForward()) cw().goForward() },
            MI(R.drawable.ic_bookmark, "Tambah bookmark") { addBookmark() },
            MI(R.drawable.ic_search, "Cari di halaman") { findDialog() },
            MI(R.drawable.ic_share, "Bagikan") { share(offOrig(tabs[cur].url) ?: tabs[cur].url) },
            MI(R.drawable.ic_print, "Cetak / simpan PDF") { printPage() },
            MI(R.drawable.ic_offline, "Simpan offline") { saveOffline() },
            MI(R.drawable.ic_reader, "Mode baca") { readerMode() }) else emptyList()

        val opts = mutableListOf<MI>()
        opts.add(MI(R.drawable.ic_expand, "Mode full", fullOn()) { setFull(!fullOn()) })
        if (onPage) {
            opts.add(MI(R.drawable.ic_desktop, "Mode desktop", bool("desktop", false)) { toggleSiteDesktop() })
            if (bool("ptr", true)) opts.add(MI(R.drawable.ic_translate, "Terjemahkan situs", tabs.getOrNull(cur)?.trOn == true) { togglePageTr() })
            opts.add(MI(R.drawable.ic_shield, "Blokir iklan", siteAdsOn(hostOf(pageUrl)) && hostOf(pageUrl) != null) { toggleSiteAds() })
            if (bool("pip", true)) opts.add(MI(R.drawable.ic_pip, "Perkecil video") {
                if (pipMode() == "in") { if (videoPlaying) startMini() else toast("Tidak ada video yang sedang diputar") }
                else if (!videoPlaying && customView == null) toast("Tidak ada video yang sedang diputar")
                else enterPip()
            })
            if (bool("zoomui", true)) opts.add(MI(R.drawable.ic_zoom, "Zoom situs") { zoomDialog() })
        }

        val homeGroups = if (onHome()) listOf(listOf(
            MI(R.drawable.ic_search, "Cari di halaman") { findDialog() },
            MI(R.drawable.ic_desktop, "Mode desktop", bool("desktop", false)) { toggleSiteDesktop() },
            MI(R.drawable.ic_shield, "Blokir iklan", bool("sadblock", true)) {
                setBool("sadblock", !bool("sadblock", true))
                tabs.forEach { applySiteAds(it) }
                toast(if (bool("sadblock", true)) "Blokir iklan aktif" else "Blokir iklan dimatikan")
            })) else null
        val panel = iconPanel(homeGroups ?: listOf(pageGroup, opts, navGroup), 4, 48, false, false, done)
        panel.setPadding(dp(6), dp(6), dp(6), dp(6))
        panel.measure(View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED), View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED))
        val pwW = panel.measuredWidth
        val pwH = panel.measuredHeight
        val loc = IntArray(2)
        anchor.getLocationOnScreen(loc)
        val vis = android.graphics.Rect()
        anchor.rootView.getWindowVisibleDisplayFrame(vis)
        val gap = dp(6)
        val ah = anchor.height
        val below = vis.bottom - (loc[1] + ah) - gap
        val above = loc[1] - vis.top - gap
        var y = when {
            pwH <= below -> loc[1] + ah + gap
            pwH <= above -> loc[1] - pwH - gap
            else -> vis.bottom - pwH - gap
        }
        y = y.coerceIn(vis.top + gap, (vis.bottom - pwH - gap).coerceAtLeast(vis.top + gap))
        val x = (loc[0] + anchor.width - pwW).coerceIn(vis.left + gap, (vis.right - pwW - gap).coerceAtLeast(vis.left + gap))
        pw = PopupWindow(panel, WC, WC, true).apply {
            setBackgroundDrawable(shape(cSurf, 18, cLine, 1))
            elevation = dp(8).toFloat()
            showAtLocation(anchor, Gravity.NO_GRAVITY, x, y)
        }
    }

    /* ---------- Halaman Tab ---------- */

    private fun pageTabs(): View {
        val add = actBtn(R.drawable.ic_plus, "Tab baru") { autoMini(); pages.clear(); newTab(home()) }
        val inc = actBtn(R.drawable.ic_incognito, if (incognito) "Penyamaran aktif" else "Mode penyamaran", if (incognito) cAcc else cFg) {
            setIncognito(!incognito)
        }
        val reopen = actBtn(R.drawable.ic_reset, "Buka lagi tab tertutup") {
            if (closed.isEmpty()) toast("Belum ada tab yang ditutup")
            else { val c = closed.removeAt(0); pages.clear(); newTab(c.url) }
        }
        val share = actBtn(R.drawable.ic_share, "Bagikan semua tab") {
            val urls = tabs.map { it.url }.filter { it.startsWith("http") }
            if (urls.isEmpty()) toast("Tidak ada tab untuk dibagikan")
            else try {
                startActivity(Intent.createChooser(Intent(Intent.ACTION_SEND).setType("text/plain")
                    .putExtra(Intent.EXTRA_TEXT, urls.joinToString("\n")), "Bagikan semua tab"))
            } catch (e: Exception) { toast("Tidak bisa membagikan") }
        }
        val closeAll = actBtn(R.drawable.ic_trash, "Tutup semua tab") {
            dlg().setTitle("Tutup semua tab?").setMessage("Ada ${tabs.size} tab yang akan ditutup.")
                .setPositiveButton("Tutup semua") { _, _ -> closeAllTabs() }
                .setNegativeButton("Batal", null).show()
        }
        val (page, body) = shell("Tab", false, listOf(add, inc, reopen, share, closeAll))
        body.addView(chips(listOf("Semua", "Aktif (${tabs.size})", "Tertutup"), tabFilter) { tabFilter = it; renderPage() })
        val cardLp = { lp(MP, WC).apply { setMargins(dp(16), dp(5), dp(16), dp(5)) } }
        if (tabFilter != 2) {
            tabs.chunked(2).forEachIndexed { ri, pair ->
                val row = LinearLayout(this).apply { orientation = LinearLayout.HORIZONTAL; setPadding(dp(10), dp(6), dp(10), dp(6)) }
                pair.forEachIndexed { ci, t ->
                    row.addView(tabCard(ri * 2 + ci, t), lp(0, WC, 1f).apply { setMargins(dp(6), 0, dp(6), 0) })
                }
                if (pair.size == 1) row.addView(View(this), lp(0, 1, 1f).apply { setMargins(dp(6), 0, dp(6), 0) })
                body.addView(row, lp(MP, WC))
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

    private fun captureThumb(t: Tab) {
        try {
            val w = t.web
            if (w.width <= 0 || w.height <= 0 || !w.isAttachedToWindow) return
            val sc = 360f / w.width
            val bmp = Bitmap.createBitmap((w.width * sc).toInt().coerceAtLeast(1), (w.height * sc).toInt().coerceAtLeast(1), Bitmap.Config.RGB_565)
            val c = android.graphics.Canvas(bmp)
            c.scale(sc, sc)
            w.draw(c)
            t.thumb = bmp
        } catch (e: Throwable) {}
    }

    private fun tabCard(i: Int, t: Tab): View {
        val card = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            background = if (i == cur) shape(cSurf, 16, cFg, 2) else shape(cSurf, 16)
            isClickable = true
            setPadding(dp(6), dp(6), dp(6), dp(6))
        }
        val head = LinearLayout(this).apply { orientation = LinearLayout.HORIZONTAL; gravity = Gravity.CENTER_VERTICAL }
        head.addView(if (t.incog) icon(R.drawable.ic_incognito, 20, cAcc) else favTile(t.title, t.url, t.icon, cLine), lp(dp(26), dp(26)).apply { rightMargin = dp(6) })
        head.addView(tv(t.title.ifEmpty { prettyUrl(t.url) }, 12f, cFg, true).apply { maxLines = 1; ellipsize = TextUtils.TruncateAt.END }, lp(0, WC, 1f))
        val x = FrameLayout(this).apply { isClickable = true; setOnClickListener { closeTab(i, true) } }
        x.addView(icon(R.drawable.ic_close, 16, cSub), FrameLayout.LayoutParams(dp(16), dp(16), Gravity.CENTER))
        head.addView(x, lp(dp(30), dp(30)))
        card.addView(head, lp(MP, WC))
        val holder = FrameLayout(this).apply { background = shape(cBg, 12); clipToOutline = true }
        val bm = t.thumb
        if (bm != null) {
            val img = ImageView(this).apply { scaleType = ImageView.ScaleType.MATRIX; setImageBitmap(bm) }
            img.addOnLayoutChangeListener { v, l, _, r, _, _, _, _, _ ->
                if (r - l > 0) {
                    val m = android.graphics.Matrix()
                    val sc = (r - l).toFloat() / bm.width
                    m.setScale(sc, sc)
                    (v as ImageView).imageMatrix = m
                }
            }
            holder.addView(img, FrameLayout.LayoutParams(MP, MP))
        } else {
            holder.addView(favTile(t.title, t.url, t.icon, cLine), FrameLayout.LayoutParams(dp(44), dp(44), Gravity.CENTER))
        }
        card.addView(holder, lp(MP, dp(170)).apply { topMargin = dp(6) })
        card.setOnClickListener { if (i != cur) autoMini(); selectTab(i) }
        swipeToClose(card) { closeTab(i, true) }
        return card
    }

    @SuppressLint("ClickableViewAccessibility")
    private fun swipeToClose(card: View, onClose: () -> Unit) {
        var x0 = 0f; var y0 = 0f; var drag = false
        card.setOnTouchListener { v, e ->
            when (e.actionMasked) {
                MotionEvent.ACTION_DOWN -> { x0 = e.rawX; y0 = e.rawY; drag = false; true }
                MotionEvent.ACTION_MOVE -> {
                    val dx = e.rawX - x0
                    val dy = e.rawY - y0
                    if (!drag && Math.abs(dx) > dp(12) && Math.abs(dx) > Math.abs(dy)) {
                        drag = true
                        v.parent?.requestDisallowInterceptTouchEvent(true)
                    }
                    if (drag) { v.translationX = dx; v.alpha = (1f - Math.abs(dx) / v.width).coerceIn(0.2f, 1f) }
                    true
                }
                MotionEvent.ACTION_UP -> {
                    if (drag) {
                        if (Math.abs(v.translationX) > v.width * 0.4f) {
                            v.animate().translationX(if (v.translationX > 0) v.width.toFloat() else -v.width.toFloat())
                                .alpha(0f).setDuration(140).withEndAction { onClose() }.start()
                        } else v.animate().translationX(0f).alpha(1f).setDuration(120).start()
                    } else if (Math.abs(e.rawX - x0) < dp(10) && Math.abs(e.rawY - y0) < dp(10)) v.performClick()
                    true
                }
                MotionEvent.ACTION_CANCEL -> { v.animate().translationX(0f).alpha(1f).setDuration(120).start(); true }
                else -> true
            }
        }
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
        val inc = actBtn(R.drawable.ic_incognito, if (incognito) "Penyamaran aktif" else "Mode penyamaran", if (incognito) cAcc else cFg) {
            setIncognito(!incognito)
        }
        val clearData = actBtn(R.drawable.ic_eraser, "Hapus data penelusuran") { clearDialog() }
        val clearHist = actBtn(R.drawable.ic_trash, "Hapus semua riwayat") {
            dlg().setMessage("Hapus semua riwayat?")
                .setPositiveButton("Hapus") { _, _ -> prefs.edit().remove("hist").apply(); renderPage() }
                .setNegativeButton("Batal", null).show()
        }
        val (page, body) = shell("Histori", true, listOf(inc, clearData, clearHist))
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
                list.addView(listRow(favTile(t, u, null, cSurf), t.ifEmpty { u }, displayHost(u), time,
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

    private fun displayHost(u: String) = try { Uri.parse(u).host?.removePrefix("www.") ?: u } catch (e: Exception) { u }

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
                        c.getString(c.getColumnIndexOrThrow(DownloadManager.COLUMN_MEDIA_TYPE)),
                        c.getInt(c.getColumnIndexOrThrow(DownloadManager.COLUMN_REASON)),
                        c.getString(c.getColumnIndexOrThrow(DownloadManager.COLUMN_URI))))
                }
            }
        } catch (e: Exception) { }
        return out.sortedByDescending { it.id }
    }

    private fun isRunning(s: Int) = s == DownloadManager.STATUS_RUNNING || s == DownloadManager.STATUS_PENDING || s == DownloadManager.STATUS_PAUSED

    private fun pageDownloads(): View {
        val dlSettings = actBtn(R.drawable.ic_settings, "Pengaturan unduhan") { openPage("dlset") }
        val (page, body) = shell("Unduhan", true, listOf(dlSettings))
        val list = LinearLayout(this).apply { orientation = LinearLayout.VERTICAL }
        var tick = 0
        fun fill() {
            val my = ++tick
            list.removeAllViews()
            val all = queryDownloads()
            val shown = all.filter { when (dlFilter) { 1 -> isRunning(it.status); 2 -> it.status == DownloadManager.STATUS_SUCCESSFUL; 3 -> it.status == DownloadManager.STATUS_FAILED; else -> true } }
            if (shown.isEmpty()) list.addView(emptyState("Belum ada unduhan"))
            shown.forEach { d ->
                val size = if (d.total > 0) Formatter.formatShortFileSize(this, d.total) else ""
                val pct = if (d.total > 0) "${(d.sofar * 100 / d.total).toInt()}% · " else ""
                val status = when {
                    d.status == DownloadManager.STATUS_SUCCESSFUL -> "Selesai"
                    d.status == DownloadManager.STATUS_FAILED -> "Gagal · " + dlErrorText(d.reason) + " · ketuk untuk ulangi"
                    d.status == DownloadManager.STATUS_PAUSED -> pct + when (d.reason) {
                        1 -> "Menunggu untuk mencoba lagi"
                        2 -> "Menunggu jaringan"
                        3 -> "Menunggu Wi-Fi"
                        else -> "Dijeda"
                    }
                    d.status == DownloadManager.STATUS_PENDING -> "Mengantre"
                    else -> pct + "Berlangsung"
                }
                val sub = if (size.isNotEmpty() && d.status == DownloadManager.STATUS_SUCCESSFUL) "$size · $status" else status
                val acts = LinearLayout(this).apply { orientation = LinearLayout.HORIZONTAL; gravity = Gravity.CENTER_VERTICAL }
                fun act(res: Int, onTap: () -> Unit) = acts.addView(iconBtn(res) { _ -> onTap() }, lp(dp(40), dp(40)))
                when (d.status) {
                    DownloadManager.STATUS_RUNNING, DownloadManager.STATUS_PENDING ->
                        act(R.drawable.ic_pause) { dlControl(d.id, true) { fill() } }
                    DownloadManager.STATUS_PAUSED ->
                        act(R.drawable.ic_play) { dlControl(d.id, false) { fill() } }
                    DownloadManager.STATUS_FAILED ->
                        act(R.drawable.ic_rotate) { retryDownload(d); handler.postDelayed({ fill() }, 500) }
                }
                act(R.drawable.ic_trash) { removeDownload(d.id); fill() }
                list.addView(listRow(iconTile(R.drawable.ic_file), d.name, sub, acts,
                    { when (d.status) {
                        DownloadManager.STATUS_SUCCESSFUL -> openDownload(d)
                        DownloadManager.STATUS_FAILED -> { retryDownload(d); handler.postDelayed({ fill() }, 500) }
                        else -> {}
                    } }), lp(MP, WC))
            }
            if (all.any { isRunning(it.status) }) {
                handler.postDelayed({ if (my == tick && pageView === page && pages.lastOrNull() == "downloads") fill() }, 1500)
            }
        }
        body.addView(chips(listOf("Semua", "Berlangsung", "Selesai", "Gagal"), dlFilter) { dlFilter = it; renderPage() })
        body.addView(list, lp(MP, WC))
        fill()
        return page
    }

    private fun dlErrorText(r: Int): String = when {
        r == DownloadManager.ERROR_INSUFFICIENT_SPACE -> "Penyimpanan penuh"
        r == DownloadManager.ERROR_DEVICE_NOT_FOUND -> "Penyimpanan tidak ditemukan"
        r == DownloadManager.ERROR_CANNOT_RESUME -> "Tidak bisa dilanjutkan"
        r == DownloadManager.ERROR_FILE_ALREADY_EXISTS -> "Berkas sudah ada"
        r == DownloadManager.ERROR_FILE_ERROR -> "Kesalahan berkas"
        r == DownloadManager.ERROR_HTTP_DATA_ERROR -> "Koneksi terputus"
        r == DownloadManager.ERROR_TOO_MANY_REDIRECTS -> "Terlalu banyak pengalihan"
        r in 400..599 -> "Server menolak (HTTP $r)"
        else -> "Kesalahan tidak diketahui"
    }

    /** Jeda atau lanjutkan unduhan. DownloadManager tidak menyediakan API publik untuk ini,
     *  jadi dicoba lewat jalur internal; jika perangkat menolak, pengguna diberi tahu. */
    private fun dlControl(id: Long, pause: Boolean, after: () -> Unit) {
        var ok = false
        try {
            val dm = getSystemService(DOWNLOAD_SERVICE) as DownloadManager
            val m = DownloadManager::class.java.getMethod(if (pause) "pauseDownload" else "resumeDownload", LongArray::class.java)
            ok = ((m.invoke(dm, longArrayOf(id)) as? Int) ?: 0) > 0
        } catch (e: Throwable) {}
        if (!ok) try {
            val cv = android.content.ContentValues().apply { put("control", if (pause) 1 else 0) }
            ok = contentResolver.update(Uri.parse("content://downloads/my_downloads/$id"), cv, null, null) > 0
        } catch (e: Throwable) {}
        if (!ok) toast(if (pause) "Perangkat ini tidak mengizinkan jeda unduhan" else "Perangkat ini tidak mengizinkan melanjutkan unduhan")
        handler.postDelayed({ after() }, 500)
    }

    /** Ulangi unduhan yang gagal: buat permintaan baru dari alamat yang sama dan hapus entri lama. */
    private fun retryDownload(d: Dl) {
        val u = d.uri
        if (u.isNullOrEmpty() || !u.startsWith("http")) { toast("Alamat unduhan tidak ditemukan"); return }
        removeDownload(d.id)
        startDownload(u, null, d.mime, d.name)
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

    /* ---------- Simpan halaman offline ---------- */

    private var offSaving = false
    private var offQuery = ""
    private var pdfDoc: android.graphics.pdf.PdfRenderer? = null
    private var pdfFd: android.os.ParcelFileDescriptor? = null
    private var pdfTitle = ""
    private var pdfId = 0L
    private val pdfPool = java.util.concurrent.Executors.newSingleThreadExecutor()

    private fun offDir(): java.io.File = java.io.File(filesDir, "offline").also { it.mkdirs() }
    private fun offFile(id: Long, ext: String) = java.io.File(offDir(), "$id.$ext")
    private fun offList(): JSONArray = getList("offline")
    private fun isOffUrl(u: String) = u.startsWith("file:") && u.contains("/offline/")

    /** Alamat asli dari halaman offline yang sedang dibuka (null jika bukan halaman offline). */
    private fun offOrig(u: String): String? {
        if (!isOffUrl(u)) return null
        val id = u.substringAfterLast('/').substringBefore('.').toLongOrNull() ?: return null
        val a = offList()
        for (i in 0 until a.length()) { val o = a.getJSONObject(i); if (o.optLong("id") == id) return o.optString("u").ifEmpty { null } }
        return null
    }

    private fun deleteOffline(id: Long) {
        try { offFile(id, "mhtml").delete(); offFile(id, "pdf").delete() } catch (e: Exception) {}
        val a = offList(); val n = JSONArray()
        for (i in 0 until a.length()) { val o = a.getJSONObject(i); if (o.optLong("id") != id) n.put(o) }
        prefs.edit().putString("offline", n.toString()).apply()
    }

    private fun saveOffline() {
        val t = tabs.getOrNull(cur) ?: return
        val w = t.web
        val u = t.url
        if (incognito || t.incog) { toast("Tidak tersedia di mode Incognito"); return }
        if (!(u.startsWith("http://") || u.startsWith("https://"))) { toast("Halaman ini tidak bisa disimpan offline"); return }
        if (offSaving) { toast("Masih menyimpan halaman…"); return }
        offSaving = true
        val id = System.currentTimeMillis()
        val title = t.title.ifEmpty { displayHost(u) }
        val mh = offFile(id, "mhtml")
        val pf = offFile(id, "pdf")
        val fin = java.util.concurrent.atomic.AtomicBoolean(false)
        fun complete(hasPdf: Boolean) {
            if (!fin.compareAndSet(false, true)) return
            offSaving = false
            if (!hasPdf) try { pf.delete() } catch (e: Exception) {}
            val e = JSONObject().put("id", id).put("t", title).put("u", u).put("ts", id)
                .put("ms", mh.length()).put("ps", if (hasPdf) pf.length() else 0L)
            val old = offList(); val n = JSONArray()
            n.put(e)
            for (i in 0 until old.length()) {
                val o = old.getJSONObject(i)
                if (o.optString("u") == u) { try { offFile(o.optLong("id"), "mhtml").delete(); offFile(o.optLong("id"), "pdf").delete() } catch (x: Exception) {} }
                else n.put(o)
            }
            prefs.edit().putString("offline", n.toString()).apply()
            toast(if (hasPdf) "Halaman dan PDF disimpan untuk dibaca offline" else "Halaman disimpan offline (PDF gagal dibuat)")
            if (pages.lastOrNull() == "offline") renderPage()
        }
        toast("Menyimpan halaman…")
        try {
            w.saveWebArchive(mh.absolutePath, false) { p ->
                if (p == null || !mh.exists() || mh.length() == 0L) {
                    try { mh.delete() } catch (e: Exception) {}
                    offSaving = false
                    toast("Gagal menyimpan halaman")
                } else {
                    val name = title.replace(Regex("[\\\\/:*?\"<>|]"), "_").take(60).ifEmpty { "Halaman" }
                    val adapter = try { w.createPrintDocumentAdapter(name) } catch (e: Exception) { null }
                    if (adapter == null) complete(false)
                    else {
                        handler.postDelayed({ complete(false) }, 90000)
                        android.print.AlethraPdf.write(adapter, pf) { ok -> runOnUiThread { complete(ok && pf.exists() && pf.length() > 0L) } }
                    }
                }
            }
        } catch (e: Exception) {
            offSaving = false
            toast("Gagal menyimpan halaman")
        }
    }

    private fun openOffline(id: Long) {
        val f = offFile(id, "mhtml")
        if (!f.exists()) { toast("Berkas offline tidak ditemukan"); return }
        autoMini(); pages.clear(); newTab(Uri.fromFile(f).toString())
    }

    private fun openOfflinePdf(id: Long, title: String) {
        if (!offFile(id, "pdf").exists()) { toast("PDF tidak ditemukan"); return }
        pdfId = id; pdfTitle = title
        openPageNow("pdfview")
    }

    private fun offlineDialog(o: JSONObject, after: () -> Unit) {
        val id = o.optLong("id"); val t = o.optString("t"); val u = o.optString("u")
        val hasPdf = offFile(id, "pdf").exists()
        val labels = mutableListOf("Buka halaman")
        if (hasPdf) labels.add("Buka PDF")
        labels.add("Buka versi online"); labels.add("Salin alamat"); labels.add("Hapus")
        dlg().setTitle(t.ifEmpty { u }.take(60)).setItems(labels.toTypedArray()) { _, i ->
            when (labels[i]) {
                "Buka halaman" -> openOffline(id)
                "Buka PDF" -> openOfflinePdf(id, t.ifEmpty { u })
                "Buka versi online" -> { autoMini(); pages.clear(); newTab(u) }
                "Salin alamat" -> {
                    (getSystemService(CLIPBOARD_SERVICE) as ClipboardManager).setPrimaryClip(ClipData.newPlainText("url", u))
                    toast("Alamat disalin")
                }
                else -> { deleteOffline(id); after() }
            }
        }.show()
    }

    private fun pageOffline(): View {
        val (page, body) = shell("Halaman offline", true)
        val list = LinearLayout(this).apply { orientation = LinearLayout.VERTICAL }
        val df = SimpleDateFormat("d MMM yyyy", Locale("id", "ID"))
        val all = offList()
        var total = 0L
        for (i in 0 until all.length()) { val o = all.getJSONObject(i); total += o.optLong("ms") + o.optLong("ps") }
        fun fill() {
            list.removeAllViews()
            val q = offQuery.trim().lowercase()
            var n = 0
            for (i in 0 until all.length()) {
                val o = all.getJSONObject(i)
                val t = o.optString("t"); val u = o.optString("u"); val id = o.optLong("id")
                if (q.isNotEmpty() && !t.lowercase().contains(q) && !u.lowercase().contains(q)) continue
                val hasPdf = o.optLong("ps") > 0L && offFile(id, "pdf").exists()
                val sub = displayHost(u) + " · " + df.format(Date(o.optLong("ts"))) + " · " +
                    Formatter.formatShortFileSize(this, o.optLong("ms") + o.optLong("ps")) + (if (hasPdf) " · PDF" else "")
                val more = iconBtn(R.drawable.ic_more, cSub) { _ -> offlineDialog(o) { renderPage() } }
                list.addView(listRow(favTile(t, u, null, cSurf), t.ifEmpty { u }, sub, more,
                    { openOffline(id) }, { offlineDialog(o) { renderPage() } }), lp(MP, WC))
                n++
            }
            if (n == 0) list.addView(emptyState(if (all.length() == 0)
                "Belum ada halaman offline.\nBuka sebuah halaman, lalu pilih ⋮ > Simpan offline." else "Tidak ada hasil"))
        }
        body.addView(searchField("Cari halaman offline…", offQuery) { offQuery = it; fill() },
            lp(MP, WC).apply { setMargins(dp(16), dp(4), dp(16), dp(8)) })
        if (all.length() > 0) body.addView(tv(all.length().toString() + " halaman · " + Formatter.formatShortFileSize(this, total) +
            " · halaman dan PDF dibaca tanpa internet", 12f, cSub).apply { setPadding(dp(20), 0, dp(20), dp(6)) })
        body.addView(list, lp(MP, WC))
        fill()
        return page
    }

    private fun closePdf() {
        val r = pdfDoc; val fd = pdfFd
        pdfDoc = null; pdfFd = null
        if (r == null && fd == null) return
        try {
            pdfPool.execute {
                try { r?.close() } catch (e: Throwable) {}
                try { fd?.close() } catch (e: Throwable) {}
            }
        } catch (e: Exception) {}
    }

    /** Penampil PDF bawaan: halaman digambar saat mendekati layar agar hemat memori. */
    private fun pagePdf(): View {
        closePdf()
        val page = LinearLayout(this).apply { orientation = LinearLayout.VERTICAL }
        page.addView(header(pdfTitle.ifEmpty { "PDF" }, true, emptyList()), lp(MP, WC))
        val sv = ScrollView(this).apply { isVerticalScrollBarEnabled = false }
        val col = LinearLayout(this).apply { orientation = LinearLayout.VERTICAL; setPadding(dp(8), dp(4), dp(8), dp(24)) }
        sv.addView(col)
        page.addView(sv, lp(MP, 0, 1f))
        try {
            val fd = android.os.ParcelFileDescriptor.open(offFile(pdfId, "pdf"), android.os.ParcelFileDescriptor.MODE_READ_ONLY)
            val r = android.graphics.pdf.PdfRenderer(fd)
            pdfFd = fd; pdfDoc = r
            val wPx = (resources.displayMetrics.widthPixels - dp(16)).coerceAtLeast(200)
            val n = r.pageCount
            val hs = IntArray(n)
            val views = ArrayList<ImageView>()
            for (i in 0 until n) {
                val p = r.openPage(i)
                hs[i] = (wPx.toFloat() * p.height / p.width).toInt().coerceAtLeast(1)
                p.close()
                val iv = ImageView(this).apply { setBackgroundColor(Color.WHITE); scaleType = ImageView.ScaleType.FIT_XY }
                views.add(iv)
                col.addView(iv, lp(wPx, hs[i]).apply { bottomMargin = dp(8) })
            }
            val loaded = BooleanArray(n)
            fun render(i: Int) {
                try {
                    pdfPool.execute {
                        if (!loaded[i]) return@execute
                        try {
                            val pg = r.openPage(i)
                            val bmp = Bitmap.createBitmap(wPx, hs[i], Bitmap.Config.ARGB_8888)
                            bmp.eraseColor(Color.WHITE)
                            pg.render(bmp, null, null, android.graphics.pdf.PdfRenderer.Page.RENDER_MODE_FOR_DISPLAY)
                            pg.close()
                            runOnUiThread { if (pdfDoc === r && loaded[i]) views[i].setImageBitmap(bmp) }
                        } catch (e: Throwable) {}
                    }
                } catch (e: Exception) {}
            }
            fun update() {
                if (pdfDoc !== r) return
                val top = sv.scrollY - sv.height
                val bottom = sv.scrollY + sv.height * 2
                for (i in 0 until n) {
                    val v = views[i]
                    val vis = v.bottom > top && v.top < bottom
                    if (vis && !loaded[i]) { loaded[i] = true; render(i) }
                    else if (!vis && loaded[i]) { loaded[i] = false; v.setImageDrawable(null) }
                }
            }
            sv.setOnScrollChangeListener { _, _, _, _, _ -> update() }
            sv.post { update() }
            handler.postDelayed({ update() }, 300)
        } catch (e: Throwable) {
            closePdf()
            col.addView(emptyState("Tidak bisa menampilkan PDF ini"))
        }
        return page
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
            navRow(R.drawable.ic_download, "Unduhan", "Lokasi, jaringan, dan notifikasi") { openPage("dlset") },
            navRow(R.drawable.ic_play, "Play", "Video kecil dan terjemahan otomatis") { openPage("play") },
            navRow(R.drawable.ic_search, "Mesin pencari", engineNames[engineIdx()]) { openPage("engine") },
            navRow(R.drawable.ic_shield, "Privasi & Keamanan", "Perlindungan data dan situs") { openPage("privacy") },
            navRow(R.drawable.ic_palette, "Tampilan", "Tema dan tata letak") { openPage("appearance") }))
        addCard(body, listOf(
            navRow(R.drawable.ic_globe, "Browser default", if (isDefaultBrowser()) "Alethra adalah browser default" else "Buka link dari aplikasi lain di Alethra") { requestDefaultBrowser() }))
        addCard(body, listOf(
            navRow(R.drawable.ic_reset, "Reset pengaturan", "Kembalikan semua ke bawaan", false) {
                dlg().setMessage("Kembalikan semua pengaturan ke bawaan?")
                    .setPositiveButton("Reset") { _, _ ->
                        val ed = prefs.edit()
                        listOf("theme", "barBottom", "dark", "desktop", "zoom", "images", "imgq", "js", "pinch", "popups", "tpc", "https", "engine", "pip", "pipmode", "adblock", "sadblock", "sadoff", "dnshint", "ptr", "zoomui", "dlloc", "dlask", "dlwifi", "dlroam", "dlprog", "fsland", "tr", "trlang", "trhide", "full", "pull", "restore", "permok", "pwsave", "pwfill", "incogsecure", "menuhide", "clock", "clock24", "clockpos", "clockalign", "clocksize", "clockcolor", "clockdate", "clocksec", "clockcity", "clocknum", "clockbg", "safeb", "clearexit", "rdsize", "rdtheme", "accent", "amoled", "round", "uifont", "homelogo", "homebg", "bartint", "wallv", "walldim", "doh", "dohcustom", "siteprefs")
                            .forEach { ed.remove(it) }
                        ed.apply()
                        siteCache = null; applyChrome(); applyAllSites(true); reloadHomes(); injectTrAll(); applyDoh()
                    }.setNegativeButton("Batal", null).show()
            }))
        return page
    }

    private fun pageGeneral(): View {
        val (page, body) = shell("Umum", true)
        addCard(body, listOf(
            toggleRow("Mode desktop", "Minta versi desktop situs", bool("desktop", false)) { c -> setBool("desktop", c); applyAllSites(true) },
            toggleRow("Muat gambar", "Tampilkan gambar di halaman", bool("images", true)) { c -> setBool("images", c); applyAllSites(true) },
            toggleRow("Zoom situs", "Tampilkan Zoom situs di menu titik tiga", bool("zoomui", true)) { c -> setBool("zoomui", c) },
            toggleRow("Mode full", "Sembunyikan bar alamat dan navigasi, menu titik tiga jadi melayang", bool("full", false)) { c -> setFull(c) },
            navRow(R.drawable.ic_sliders, "Kualitas gambar", imgQNames[imgQ().coerceIn(0, 2)]) { openPage("imgq") },
            toggleRow("JavaScript", "Izinkan skrip di halaman", bool("js", true)) { c -> setBool("js", c); applyAllSites(true) },
            toggleRow("Zoom dua jari", "Cubit untuk memperbesar", bool("pinch", true)) { c -> setBool("pinch", c); applyAllSites(false) },
            toggleRow("Tarik untuk muat ulang", "Tarik halaman ke bawah dari atas untuk refresh", bool("pull", true)) { c -> setBool("pull", c) },
            toggleRow("Pulihkan tab terakhir", "Buka kembali tab saat aplikasi dibuka lagi", bool("restore", true)) { c -> setBool("restore", c) }))
        val zl = tv("Ukuran teks situs: ${prefs.getInt("zoom", 100)}%", 15f, cFg, true).apply { setPadding(dp(20), dp(18), dp(20), dp(4)) }
        body.addView(zl, lp(MP, WC))
        val sb = SeekBar(this)
        sb.max = 15
        sb.progress = (prefs.getInt("zoom", 100) - 50) / 10
        sb.progressTintList = ColorStateList.valueOf(cAcc)
        sb.thumbTintList = ColorStateList.valueOf(cAcc)
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
            listRow(iconTile(R.drawable.ic_search), n, "", if (i == engineIdx()) icon(R.drawable.ic_check, 20, cAcc) else null,
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
            toggleRow("Blokir iklan situs", "Blokir iklan dan pelacak iklan di semua situs", bool("sadblock", true)) { c ->
                setBool("sadblock", c); tabs.forEach { applySiteAds(it) }; cw().reload()
            },
            toggleRow("Blokir pop-up berbahaya", "Lindungi dari pop-up berbahaya", bool("popups", false)) { c -> setBool("popups", c); applyAllSites(false) },
            toggleRow("Gunakan HTTPS", "Enkripsi koneksi situs", bool("https", false)) { c -> setBool("https", c) },
            toggleRow("Safe Browsing", "Peringatkan saat membuka situs berbahaya atau penipuan", bool("safeb", true)) { c -> setBool("safeb", c); applyAllSites(false) }))
        body.addView(sectionLabel("Kata sandi"))
        addCard(body, listOf(
            navRow(R.drawable.ic_key, "Kata sandi tersimpan", pwList().length().toString() + " akun") { openPage("pwlist") },
            toggleRow("Tawarkan simpan kata sandi", "Tanya saat kamu login di sebuah situs", bool("pwsave", true)) { c -> setBool("pwsave", c) },
            toggleRow("Blokir tangkapan layar Incognito", "Cegah screenshot dan pratinjau aplikasi terbaru saat Incognito aktif", bool("incogsecure", true)) { c -> setBool("incogsecure", c); applyIncogUi() },
            toggleRow("Isi otomatis", "Isi nama pengguna dan kata sandi di situs yang sama", bool("pwfill", true)) { c -> setBool("pwfill", c) }))
        body.addView(sectionLabel("Kunci aplikasi"))
        addCard(body, listOf(
            navRow(R.drawable.ic_lock, "Kunci aplikasi", lockSummary()) { requireCfg({ openPage("lock") }) }))
        body.addView(sectionLabel("Izin situs"))
        addCard(body, listOf(
            navRow(R.drawable.ic_trash, "Hapus izin situs", "Lupakan izin kamera, mikrofon, lokasi, dan notifikasi yang pernah disetujui", false) { resetSitePerms() }))
        body.addView(sectionLabel("Akses situs"))
        addCard(body, listOf(
            navRow(R.drawable.ic_shield, "DNS aman (DoH)", dohName()) { openPage("doh") },
            navRow(R.drawable.ic_globe, "DNS pribadi sistem", "Atur DNS Android untuk semua aplikasi") { dnsDialog() },
            toggleRow("Saran saat situs diblokir", "Tampilkan bantuan jika muncul halaman Internet Positif", bool("dnshint", true)) { c -> setBool("dnshint", c) }))
        body.addView(sectionLabel("Data & Riwayat"))
        addCard(body, listOf(
            navRow(R.drawable.ic_trash, "Hapus data penelusuran", "Riwayat, cookie, dan cache") { clearDialog() },
            toggleRow("Hapus data saat keluar", "Hapus riwayat, cookie, cache, dan tab tersimpan saat aplikasi ditutup", bool("clearexit", false)) { c -> setBool("clearexit", c) }))
        return page
    }

    private fun themeCard(label: String, isDarkCard: Boolean, selected: Boolean, onClick: () -> Unit): LinearLayout {
        val pbg = if (isDarkCard) Color.parseColor("#0B0B0D") else Color.WHITE
        val pbar = if (isDarkCard) Color.parseColor("#2A2A2E") else Color.parseColor("#E8E8EC")
        val box = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(dp(12), dp(12), dp(12), dp(12))
            background = shape(pbg, 14, if (selected) cAcc else cLine, if (selected) 2 else 1)
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

    private fun accentColor(): Int? {
        val v = prefs.getString("accent", "") ?: ""
        if (v.isEmpty()) return null
        return try { Color.parseColor(v) } catch (e: Exception) { null }
    }
    private fun mix(a: Int, b: Int, t: Float): Int = Color.rgb(
        (Color.red(a) * (1 - t) + Color.red(b) * t).toInt(),
        (Color.green(a) * (1 - t) + Color.green(b) * t).toInt(),
        (Color.blue(a) * (1 - t) + Color.blue(b) * t).toInt())

    private fun wallFile() = java.io.File(filesDir, "wall.jpg")
    private fun wallOn() = prefs.getLong("wallv", 0L) > 0L && wallFile().exists()

    private fun pickWall() {
        try {
            val i = Intent(Intent.ACTION_GET_CONTENT).setType("image/*").addCategory(Intent.CATEGORY_OPENABLE)
            startActivityForResult(Intent.createChooser(i, "Pilih wallpaper"), WALL_REQ)
        } catch (e: Exception) { toast("Galeri tidak tersedia") }
    }

    private fun saveWall(uri: Uri) {
        Thread {
            try {
                val o = BitmapFactory.Options().apply { inJustDecodeBounds = true }
                contentResolver.openInputStream(uri)?.use { BitmapFactory.decodeStream(it, null, o) }
                var ss = 1
                while (maxOf(o.outWidth, o.outHeight) / ss > 2400) ss *= 2
                val o2 = BitmapFactory.Options().apply { inSampleSize = ss }
                val bmp = contentResolver.openInputStream(uri)?.use { BitmapFactory.decodeStream(it, null, o2) }
                if (bmp == null) { runOnUiThread { toast("Gagal membaca gambar") }; return@Thread }
                val m = maxOf(bmp.width, bmp.height)
                val out = if (m > 1280) {
                    val k = 1280f / m
                    Bitmap.createScaledBitmap(bmp, (bmp.width * k).toInt().coerceAtLeast(1), (bmp.height * k).toInt().coerceAtLeast(1), true)
                } else bmp
                java.io.FileOutputStream(wallFile()).use { out.compress(Bitmap.CompressFormat.JPEG, 80, it) }
                runOnUiThread {
                    prefs.edit().putLong("wallv", System.currentTimeMillis()).putBoolean("wallpending", true).apply()
                    reloadHomes(); if (pageView != null) renderPage()
                    toast("Wallpaper diterapkan")
                }
            } catch (e: Throwable) { runOnUiThread { toast("Gagal memakai gambar ini") } }
        }.start()
    }

    private fun clearWall() {
        try { wallFile().delete() } catch (e: Exception) {}
        prefs.edit().remove("wallv").apply()
        reloadHomes(); if (pageView != null) renderPage()
    }

    private fun lumOf(c: Int) = (0.299 * Color.red(c) + 0.587 * Color.green(c) + 0.114 * Color.blue(c)).toInt()
    private fun accentHex(): String { val a = accentColor() ?: return ""; return String.format("#%06X", 0xFFFFFF and a) }
    private fun radiusF(): Float = when (prefs.getString("round", "mid")) { "sharp" -> 0.3f; "round" -> 1.5f; else -> 1f }
    private fun fontF(): Float = prefs.getInt("uifont", 100) / 100f

    private val accNames = arrayOf("Bawaan", "Biru", "Ungu", "Merah muda", "Merah", "Oranye", "Kuning", "Hijau", "Toska")
    private val accHex = arrayOf("", "#2F6BFF", "#8B5CF6", "#EC4899", "#EF4444", "#F97316", "#EAB308", "#22C55E", "#14B8A6")

    private fun accentRow(): View {
        val h = HorizontalScrollView(this).apply { isHorizontalScrollBarEnabled = false }
        val row = LinearLayout(this).apply { orientation = LinearLayout.HORIZONTAL; setPadding(dp(16), dp(4), dp(16), dp(10)) }
        val curHex = accentHex()
        accHex.forEachIndexed { i, hx ->
            val sel = curHex.equals(hx, true)
            val col = if (hx.isEmpty()) cFg else Color.parseColor(hx)
            val w = LinearLayout(this).apply { orientation = LinearLayout.VERTICAL; gravity = Gravity.CENTER_HORIZONTAL; isClickable = true }
            val dot = FrameLayout(this).apply { background = shape(col, 22, if (sel) cFg else cLine, if (sel) 3 else 1) }
            if (sel) dot.addView(icon(R.drawable.ic_check, 20, if (hx.isEmpty()) cBg else if (lumOf(col) > 150) Color.parseColor("#111113") else Color.WHITE),
                FrameLayout.LayoutParams(dp(20), dp(20), Gravity.CENTER))
            w.addView(dot, lp(dp(44), dp(44)))
            w.addView(tv(accNames[i], 11f, if (sel) cFg else cSub), lp(WC, WC).apply { topMargin = dp(4) })
            w.setOnClickListener { prefs.edit().putString("accent", hx).apply(); applyChrome(); reloadHomes() }
            row.addView(w, lp(WC, WC).apply { rightMargin = dp(12) })
        }
        h.addView(row)
        return h
    }

    private fun customAccentDialog() {
        val col = LinearLayout(this).apply { orientation = LinearLayout.VERTICAL; setPadding(dp(20), dp(8), dp(20), 0) }
        val et = EditText(this).apply { hint = "#RRGGBB"; isSingleLine = true; setTextColor(cFg); setHintTextColor(cSub); setText(accentHex()); setSelectAllOnFocus(true) }
        col.addView(et, lp(MP, WC))
        dlg().setTitle("Warna sendiri").setView(col)
            .setPositiveButton("Terapkan") { _, _ ->
                var v = et.text.toString().trim()
                if (v.isNotEmpty() && !v.startsWith("#")) v = "#$v"
                val ok = v.length == 7 && try { Color.parseColor(v); true } catch (e: Exception) { false }
                if (!ok) { toast("Kode warna tidak valid, contoh: #2F6BFF"); return@setPositiveButton }
                prefs.edit().putString("accent", v.uppercase()).apply(); applyChrome(); reloadHomes()
            }.setNegativeButton("Batal", null).show()
    }

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
            toggleRow("Hitam pekat", "Latar benar-benar hitam saat tema gelap (hemat baterai layar OLED)", bool("amoled", false)) { c -> setBool("amoled", c); applyChrome(); reloadHomes() },
            toggleRow("Mode gelap situs", "Gelapkan halaman web", bool("dark", false)) { c -> setBool("dark", c); applyAllSites(false) },
            toggleRow("Bar alamat di bawah", "Pindahkan bar alamat ke bawah", bool("barBottom", false)) { c -> setBool("barBottom", c); applyChrome() }))

        body.addView(sectionLabel("Warna aksen"))
        body.addView(accentRow())
        addCard(body, listOf(
            navRow(R.drawable.ic_palette, "Warna sendiri", if (accentHex().isEmpty()) "Masukkan kode warna, mis. #2F6BFF" else "Sekarang: " + accentHex(), true) { customAccentDialog() }))

        body.addView(sectionLabel("Warna bar atas & bawah (butuh warna aksen)"))
        val bk = listOf("off", "soft", "full")
        body.addView(chips(listOf("Mati", "Lembut", "Penuh"), bk.indexOf(prefs.getString("bartint", "off")).coerceAtLeast(0)) { i ->
            if (accentColor() == null && i > 0) toast("Pilih warna aksen dulu")
            prefs.edit().putString("bartint", bk[i]).apply(); applyChrome()
        })

        body.addView(sectionLabel("Bentuk"))
        val rk = listOf("sharp", "mid", "round")
        body.addView(chips(listOf("Tajam", "Sedang", "Bulat"), rk.indexOf(prefs.getString("round", "mid")).coerceAtLeast(0)) { i ->
            prefs.edit().putString("round", rk[i]).apply(); applyChrome(); reloadHomes()
        })
        body.addView(sectionLabel("Ukuran teks antarmuka"))
        val fv = listOf(90, 100, 112)
        body.addView(chips(listOf("Kecil", "Normal", "Besar"), fv.indexOf(prefs.getInt("uifont", 100)).coerceAtLeast(0)) { i ->
            prefs.edit().putInt("uifont", fv[i]).apply(); applyChrome()
        })

        body.addView(sectionLabel("Beranda"))
        addCard(body, listOf(
            toggleRow("Tampilkan logo", "Logo dan nama Alethra di halaman beranda", bool("homelogo", true)) { c -> setBool("homelogo", c); reloadHomes() },
            toggleRow("Latar berwarna", "Gradasi lembut dari warna aksen di beranda", bool("homebg", false)) { c -> setBool("homebg", c); reloadHomes() },
            navRow(R.drawable.ic_file, "Wallpaper beranda", if (wallOn()) "Terpasang, ketuk untuk ganti" else "Pilih gambar dari galeri") { pickWall() }))
        if (wallOn()) {
            body.addView(sectionLabel("Keredupan wallpaper"))
            body.addView(chips(listOf("Rendah", "Sedang", "Tinggi"), prefs.getInt("walldim", 1).coerceIn(0, 2)) { i ->
                prefs.edit().putInt("walldim", i).apply(); reloadHomes(); renderPage()
            })
            addCard(body, listOf(navRow(R.drawable.ic_trash, "Hapus wallpaper", "Kembali ke latar polos", false) { clearWall() }))
        }
        body.addView(sectionLabel("Widget jam dan tanggal"))
        fun strOpt(key: String, def: String, labels: List<String>, keys: List<String>) =
            chips(labels, keys.indexOf(prefs.getString(key, def)).coerceAtLeast(0)) { i ->
                prefs.edit().putString(key, keys[i]).apply(); reloadHomes(); renderPage()
            }
        fun intOpt(key: String, def: Int, labels: List<String>) =
            chips(labels, prefs.getInt(key, def).coerceIn(0, labels.size - 1)) { i ->
                prefs.edit().putInt(key, i).apply(); reloadHomes(); renderPage()
            }
        body.addView(strOpt("clock", "analog", listOf("Analog", "Digital", "Teks", "Jam saja", "Satu baris", "Sembunyikan"),
            listOf("analog", "digital", "text", "dial", "bar", "off")))
        body.addView(sectionLabel("Posisi di beranda"))
        body.addView(strOpt("clockpos", "mid", listOf("Atas", "Tengah", "Bawah"), listOf("top", "mid", "bot")))
        body.addView(sectionLabel("Perataan"))
        body.addView(strOpt("clockalign", "c", listOf("Kiri", "Tengah", "Kanan"), listOf("l", "c", "r")))
        body.addView(sectionLabel("Ukuran"))
        body.addView(intOpt("clocksize", 1, listOf("Kecil", "Normal", "Besar")))
        body.addView(sectionLabel("Warna jam"))
        body.addView(strOpt("clockcolor", "t", listOf("Tema", "Aksen", "Emas", "Biru", "Hijau"), listOf("t", "a", "g", "b", "h")))
        body.addView(sectionLabel("Format tanggal"))
        body.addView(intOpt("clockdate", 0, listOf("Panjang", "Singkat", "Sembunyikan")))
        addCard(body, listOf(
            toggleRow("Format 24 jam", "Matikan untuk format 12 jam (AM/PM)", bool("clock24", true)) { c -> setBool("clock24", c); reloadHomes() },
            toggleRow("Tampilkan detik", "Detik ikut tampil pada jam digital", bool("clocksec", false)) { c -> setBool("clocksec", c); reloadHomes() },
            toggleRow("Nama kota", "Tampilkan kota pada tampilan Analog", bool("clockcity", true)) { c -> setBool("clockcity", c); reloadHomes() },
            toggleRow("Angka pada jam analog", "Matikan untuk memakai garis penanda", bool("clocknum", true)) { c -> setBool("clocknum", c); reloadHomes() },
            toggleRow("Latar kartu", "Matikan agar widget transparan", bool("clockbg", true)) { c -> setBool("clockbg", c); reloadHomes() }))
        return page
    }

    private fun trIdx() = trLangs.indexOf(trLang()).coerceAtLeast(0)
    private fun trLang(): String = prefs.getString("trlang", "id") ?: "id"

    private fun pagePlay(): View {
        val (page, body) = shell("Play", true)
        body.addView(sectionLabel("Video"))
        addCard(body, listOf(
            toggleRow("Blokir iklan YouTube", "Hilangkan iklan video dan banner di YouTube", bool("adblock", true)) { c ->
                setBool("adblock", c); tabs.forEach { applyAdblock(it) }; cw().reload()
            },
            toggleRow("Perkecil video", "Lanjutkan putar video di jendela kecil saat keluar dari aplikasi", bool("pip", true)) { c ->
                setBool("pip", c)
                if (c) toast("Berlaku di situs apa pun saat ada video yang diputar")
            },
            navRow(R.drawable.ic_expand, "Mode perkecil", if (pipMode() == "in") "Di dalam browser" else "Di luar browser") { openPage("pipmode") }))
        body.addView(sectionLabel("Layar penuh"))
        addCard(body, listOf(
            toggleRow("Otomatis landscape", "Putar ke landscape saat video dibuka layar penuh", bool("fsland", true)) { c -> setBool("fsland", c) }))
        body.addView(sectionLabel("Terjemahan"))
        addCard(body, listOf(
            toggleRow("Terjemahkan situs", "Tampilkan Terjemahkan situs di menu titik tiga", bool("ptr", true)) { c ->
                setBool("ptr", c)
                if (!c) tabs.filter { it.trOn }.forEach { it.trOn = false; it.web.evaluateJavascript("window.__alPT&&window.__alPT.stop()", null) }
            },
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
            listRow(iconTile(R.drawable.ic_expand), n, d, if (pipMode() == k) icon(R.drawable.ic_check, 20, cAcc) else null,
                { prefs.edit().putString("pipmode", k).apply(); renderPage() })
        })
        return page
    }

    private fun pageTrLang(): View {
        val (page, body) = shell("Bahasa tujuan", true)
        val rows = trNames.mapIndexed { i, n ->
            listRow(iconTile(R.drawable.ic_globe), n, "", if (i == trIdx()) icon(R.drawable.ic_check, 20, cAcc) else null,
                { prefs.edit().putString("trlang", trLangs[i]).apply(); injectTrAll(); tabs.filter { it.trOn }.forEach { startPageTr(it) }; renderPage() })
        }
        addCard(body, rows)
        return page
    }

    /* ================= Terjemahan otomatis ================= */

    inner class TrBridge(private val web: WebView) {
        @JavascriptInterface
        fun translateBatch(id: Int, json: String, lang: String) {
            if (!bool("ptr", true) || json.length > 12000 || !trLangs.contains(lang)) return
            ptPool.execute {
                val res = try { fetchBatch(json, lang) } catch (e: Exception) { "[]" }
                runOnUiThread {
                    web.evaluateJavascript("window.__alTrB&&window.__alTrB(" + id + "," + JSONObject.quote(res) + ")", null)
                }
            }
        }

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

    private val ptPool = java.util.concurrent.Executors.newFixedThreadPool(3)
    private var ptJs: String? = null
    private fun ptScript(): String {
        ptJs?.let { return it }
        val s = try { assets.open("pagetr.js").bufferedReader().use { it.readText() } } catch (e: Exception) { "" }
        ptJs = s
        return s
    }

    // Terjemahkan banyak teks sekaligus; bila gagal, terjemahkan satu per satu
    private fun fetchBatch(json: String, lang: String): String {
        val arr = JSONArray(json)
        val n = arr.length()
        val out = JSONArray()
        try {
            val sb = StringBuilder("client=gtx&sl=auto&tl=" + lang + "&dt=t")
            for (i in 0 until n) sb.append("&q=").append(URLEncoder.encode(arr.getString(i), "UTF-8"))
            val c = URL("https://translate.googleapis.com/translate_a/t").openConnection() as HttpURLConnection
            try {
                c.requestMethod = "POST"
                c.doOutput = true
                c.connectTimeout = 8000; c.readTimeout = 12000
                c.setRequestProperty("User-Agent", "Mozilla/5.0")
                c.setRequestProperty("Content-Type", "application/x-www-form-urlencoded;charset=UTF-8")
                c.outputStream.use { it.write(sb.toString().toByteArray(Charsets.UTF_8)) }
                if (c.responseCode == 200) {
                    val r = JSONArray(c.inputStream.bufferedReader().use { it.readText() })
                    var good = 0
                    for (i in 0 until n) {
                        val e = r.opt(i)
                        val t = when (e) { is JSONArray -> e.optString(0, ""); is String -> e; else -> "" }
                        if (t.isNotEmpty()) good++
                        out.put(t)
                    }
                    if (good > 0) return out.toString()
                }
            } finally { c.disconnect() }
        } catch (e: Exception) { }
        val single = JSONArray()
        for (i in 0 until n) single.put(try { fetchTranslation(arr.getString(i), lang) } catch (e: Exception) { "" })
        return single.toString()
    }

    private fun startPageTr(t: Tab) {
        if (t.url.startsWith(ASSET)) return
        t.web.evaluateJavascript(ptScript() + ";window.__alPT&&window.__alPT.start(" + JSONObject.quote(trLang()) + ")", null)
    }

    private fun togglePageTr() {
        val t = tabs.getOrNull(cur) ?: return
        if (t.url.startsWith(ASSET)) { toast("Buka sebuah situs dulu untuk menerjemahkan"); return }
        if (!t.trOn) {
            t.trOn = true
            startPageTr(t)
            toast("Menerjemahkan ke " + trNames[trIdx()])
        } else {
            t.trOn = false
            t.web.evaluateJavascript("window.__alPT&&window.__alPT.stop()", null)
            toast("Terjemahan dimatikan")
        }
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

    private fun home(): String = HOME + "#t=" + (if (dark) "dark" else "light") + "&s=" + Uri.encode(shortcuts().toString()) +
        "&a=" + Uri.encode(accentHex()) + "&o=" + (if (dark && bool("amoled", false)) "1" else "0") +
        "&w=" + (if (wallOn()) prefs.getLong("wallv", 0L).toString() else "0") + "&d=" + prefs.getInt("walldim", 1) +
        "&l=" + (if (bool("homelogo", true)) "1" else "0") + "&g=" + (if (bool("homebg", false)) "1" else "0") +
        "&c=" + (prefs.getString("clock", "analog") ?: "analog") + "&h=" + (if (bool("clock24", true)) "1" else "0") +
        "&p=" + (prefs.getString("clockpos", "mid") ?: "mid") + "&z=" + prefs.getInt("clocksize", 1) +
        "&k=" + (prefs.getString("clockcolor", "t") ?: "t") + "&al=" + (prefs.getString("clockalign", "c") ?: "c") +
        "&d2=" + prefs.getInt("clockdate", 0) + "&q=" + (if (bool("clocksec", false)) "1" else "0") +
        "&y=" + (if (bool("clockcity", true)) "1" else "0") + "&n=" + (if (bool("clocknum", true)) "1" else "0") +
        "&b=" + (if (bool("clockbg", true)) "1" else "0")

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
                val n = name.text.toString().trim().ifEmpty { displayHost(u) }
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

    private fun go(input: String) { val u = toUrl(input); applySite(cw(), u); cw().loadUrl(u) }

    private fun setUrlText(u: String) {
        if (omnibox.hasFocus()) return
        omnibox.setText(if (u.startsWith(ASSET)) "" else (offOrig(u) ?: u))
    }

    private fun isLocalHost(h: String?): Boolean =
        h == null || h == "localhost" || h.endsWith(".local") || Regex("^\\d{1,3}(\\.\\d{1,3}){3}$").matches(h)

    /* ================= WebView ================= */

    private fun desktopUa() = defaultUa
        .replace(Regex("\\(Linux; Android[^)]*\\)"), "(X11; Linux x86_64)")
        .replace("Version/4.0 ", "").replace(" Mobile", "")

    @Suppress("DEPRECATION")
    private fun applySite(w: WebView, url: String? = tabOf(w)?.url) {
        val s = w.settings
        val sk = siteKey(hostOf(url))
        s.userAgentString = if (siteDesktop(sk)) desktopUa() else defaultUa
        s.javaScriptEnabled = siteJs(sk)
        s.loadsImagesAutomatically = bool("images", true)
        s.blockNetworkImage = !bool("images", true)
        s.textZoom = siteZoom(sk)
        val pinch = bool("pinch", true)
        s.setSupportZoom(true)
        s.builtInZoomControls = pinch
        s.javaScriptCanOpenWindowsAutomatically = !bool("popups", false)
        CookieManager.getInstance().setAcceptThirdPartyCookies(w, bool("tpc", true) && !isIncog(w))
        try {
            if (androidx.webkit.WebViewFeature.isFeatureSupported(androidx.webkit.WebViewFeature.SAFE_BROWSING_ENABLE))
                androidx.webkit.WebSettingsCompat.setSafeBrowsingEnabled(s, bool("safeb", true))
        } catch (e: Exception) {}
        if (Build.VERSION.SDK_INT >= 29) {
            s.forceDark = if (bool("dark", false)) WebSettings.FORCE_DARK_ON else WebSettings.FORCE_DARK_OFF
        }
    }

    private fun applyAllSites(reload: Boolean) {
        tabs.forEach { applySite(it.web) }
        if (reload) cw().reload()
    }

    @SuppressLint("SetJavaScriptEnabled")
    private fun makeWeb(incog: Boolean = false): WebView {
        val w = WebView(this)
        if (incog) {
            w.tag = "incog-weak"
            if (profilesOk()) try {
                androidx.webkit.WebViewCompat.setProfile(w, INCOG_PROFILE)
                w.tag = "incog"
            } catch (e: Exception) {}
        }
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
            allowFileAccess = true
        }
        CookieManager.getInstance().setAcceptThirdPartyCookies(w, true)
        w.addJavascriptInterface(TrBridge(w), "AlethraTr")
        w.addJavascriptInterface(AppBridge(w), "AlApp")
        applySite(w)
        if (incog) {
            w.settings.cacheMode = WebSettings.LOAD_NO_CACHE
            @Suppress("DEPRECATION") run { w.settings.saveFormData = false }
            if (w.tag == "incog-weak") w.settings.domStorageEnabled = false
        }

        w.webViewClient = object : WebViewClient() {
            override fun shouldInterceptRequest(v: WebView?, r: WebResourceRequest?): WebResourceResponse? {
                val req = r ?: return null
                if (req.url.host == "alethra.wall") {
                    val ok = (try { tabOf(v)?.url?.startsWith(ASSET) == true } catch (e: Exception) { false }) && wallFile().exists()
                    return try {
                        if (ok) WebResourceResponse("image/jpeg", null, java.io.FileInputStream(wallFile()))
                        else WebResourceResponse("text/plain", "utf-8", java.io.ByteArrayInputStream(ByteArray(0)))
                    } catch (e: Exception) { null }
                }
                if (adBlocked(v, req) || siteAdBlocked(v, req)) { adCount.incrementAndGet(); return WebResourceResponse("text/plain", "utf-8", java.io.ByteArrayInputStream(ByteArray(0))) }
                val lv = imgQ()
                if (lv == 0 || !bool("images", true) || isIncog(v)) return null
                if (req.method != "GET" || req.isForMainFrame) return null
                val u = req.url.toString()
                if (!u.startsWith("http")) return null
                val h = req.requestHeaders ?: return null
                val acc = h["Accept"] ?: h["accept"] ?: return null
                if (!acc.startsWith("image/") || h.containsKey("Range")) return null
                val target = lowerImg(u, lv)
                return fetchImg(target, h) ?: if (target != u) fetchImg(u, h) else null
            }

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
                if (req.isForMainFrame && v != null && (u.startsWith("http://") || u.startsWith("https://"))) applySite(v, u)
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
                t.zoom = 1f
                if (v != null) {
                    val sk = siteKey(hostOf(t.url))
                    if (sk != null) webHost[v] = sk else webHost.remove(v)
                    val ua0 = v.settings.userAgentString
                    applySite(v, t.url)
                    val nowMs = System.currentTimeMillis()
                    if (!t.uaFix && nowMs - t.uaFixAt > 5000 && ua0 != v.settings.userAgentString && t.url.startsWith("http")) {
                        t.uaFix = true
                        t.uaFixAt = nowMs
                        handler.post { try { v.reload() } catch (e: Exception) {} }
                    }
                }
                if (isCur(v)) try { tts?.stop() } catch (e: Exception) {}
                if (isCur(v) && bool("dnshint", true) && !dnsHinted && isBlockPage(t.url)) { dnsHinted = true; showDnsHint() }
                if (v != null && url != null) { injectAd(v, url); injectSiteAds(v, url) }
                if (favicon != null) t.icon = favicon
                if (isCur(v)) { setUrlText(t.url); updateChrome() }
            }

            override fun onRenderProcessGone(v: WebView?, detail: RenderProcessGoneDetail?): Boolean {
                val i = tabs.indexOfFirst { it.web === v }
                if (i < 0) return true
                val isHome = tabs[i].url.startsWith(ASSET)
                if (isHome && prefs.getLong("wallv", 0L) > 0L) {
                    try { wallFile().delete() } catch (e: Exception) {}
                    prefs.edit().remove("wallv").putBoolean("wallpending", false).apply()
                    toast("Wallpaper dimatikan karena membuat halaman berhenti")
                } else toast("Halaman berhenti karena kehabisan memori")
                handler.post { if (i < tabs.size && tabs[i].web === v) closeTab(i) }
                return true
            }

            override fun onPageFinished(v: WebView?, url: String?) {
                val t = tabOf(v) ?: return
                if (url != null && url.startsWith(HOME) && prefs.getBoolean("wallpending", false)) prefs.edit().putBoolean("wallpending", false).apply()
                t.url = url ?: t.url
                t.title = v?.title ?: t.url
                if (!t.url.startsWith(ASSET) && !t.url.startsWith("data:") && t.url != "about:blank" && !isOffUrl(t.url)) addHistory(t.title, t.url)
                if (!t.url.startsWith(ASSET) && v != null) injectTr(v)
                if (v != null && t.url.startsWith("http")) injectPw(v, t.url)
                if (v != null) { injectAd(v, t.url); injectSiteAds(v, t.url) }
                t.uaFix = false
                if (v != null) injectNotif(v, t.url)
                if (t.trOn) startPageTr(t)
                if (isCur(v)) { srl.isRefreshing = false; setUrlText(t.url); updateChrome() }
            }

            override fun onReceivedSslError(v: WebView?, h: SslErrorHandler?, e: SslError?) {
                if (h == null) return
                val site = hostOf(e?.url)
                val t = tabOf(v)
                if (site != null && sslOk.contains(site)) { h.proceed(); return }
                val isMain = t != null && isCur(v) && site != null && hostOf(t.url) == site
                if (!isMain || sslShowing || isFinishing) { h.cancel(); return }
                sslShowing = true
                dlg().setTitle("Koneksi tidak aman")
                    .setMessage((site ?: "Situs ini") + "\n\n" + sslReason(e) + "\n\nData yang kamu kirim bisa dibaca atau diubah pihak lain.")
                    .setPositiveButton("Kembali") { _, _ -> h.cancel() }
                    .setNegativeButton("Tetap lanjutkan") { _, _ -> if (site != null) sslOk.add(site); h.proceed() }
                    .setOnCancelListener { h.cancel() }
                    .setOnDismissListener { sslShowing = false }
                    .show()
            }

            override fun onReceivedHttpAuthRequest(v: WebView?, h: HttpAuthHandler?, hostName: String?, realm: String?) {
                if (h == null) return
                if (isFinishing || !isCur(v)) { h.cancel(); return }
                val box = LinearLayout(this@MainActivity).apply {
                    orientation = LinearLayout.VERTICAL
                    setPadding(dp(20), dp(8), dp(20), 0)
                }
                val user = EditText(this@MainActivity).apply {
                    hint = "Nama pengguna"; isSingleLine = true
                    setTextColor(cFg); setHintTextColor(cSub)
                    inputType = InputType.TYPE_CLASS_TEXT
                }
                val pass = EditText(this@MainActivity).apply {
                    hint = "Kata sandi"; isSingleLine = true
                    setTextColor(cFg); setHintTextColor(cSub)
                    inputType = InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD
                }
                box.addView(user, lp(MP, WC))
                box.addView(pass, lp(MP, WC))
                dlg().setTitle("Masuk ke " + (hostName ?: "situs"))
                    .setMessage(if (realm.isNullOrEmpty()) "Situs ini meminta nama pengguna dan kata sandi." else realm)
                    .setView(box)
                    .setPositiveButton("Masuk") { _, _ -> h.proceed(user.text.toString(), pass.text.toString()) }
                    .setNegativeButton("Batal") { _, _ -> h.cancel() }
                    .setOnCancelListener { h.cancel() }
                    .show()
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
                    if (p >= 100) srl.isRefreshing = false
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
                showFullscreen(view, callback)
            }

            override fun onHideCustomView() = hideCustom()

            override fun onPermissionRequest(req: PermissionRequest?) {
                if (req == null) return
                val wanted = mutableListOf<String>()
                val perms = mutableListOf<String>()
                val labels = mutableListOf<String>()
                val keys = mutableListOf<String>()
                req.resources.forEach { r ->
                    when (r) {
                        PermissionRequest.RESOURCE_VIDEO_CAPTURE -> { wanted.add(r); perms.add(android.Manifest.permission.CAMERA); labels.add("kamera"); keys.add("cam") }
                        PermissionRequest.RESOURCE_AUDIO_CAPTURE -> { wanted.add(r); perms.add(android.Manifest.permission.RECORD_AUDIO); labels.add("mikrofon"); keys.add("mic") }
                        PermissionRequest.RESOURCE_PROTECTED_MEDIA_ID -> wanted.add(r)
                    }
                }
                if (perms.isEmpty()) {
                    try { if (wanted.isEmpty()) req.deny() else req.grant(wanted.toTypedArray()) } catch (e: Exception) {}
                    return
                }
                webPerm(req.origin?.host, labels.joinToString(" dan "), perms.toTypedArray(), keys = keys) { ok ->
                    try { if (ok) req.grant(wanted.toTypedArray()) else req.deny() } catch (e: Exception) {}
                }
            }

            override fun onGeolocationPermissionsShowPrompt(origin: String?, cb: GeolocationPermissions.Callback?) {
                if (cb == null || origin == null) return
                val perms = arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION, android.Manifest.permission.ACCESS_COARSE_LOCATION)
                webPerm(Uri.parse(origin).host, "lokasi", perms, anyOf = true, keys = listOf("loc")) { ok -> cb.invoke(origin, ok, false) }
            }

            override fun onJsAlert(v: WebView?, url: String?, message: String?, r: JsResult?): Boolean {
                if (r == null) return false
                if (isFinishing || !isCur(v)) { r.cancel(); return true }
                dlg().setTitle(jsTitle(url)).setMessage(message ?: "")
                    .setPositiveButton("OK") { _, _ -> r.confirm() }
                    .setOnCancelListener { r.cancel() }.show()
                return true
            }

            override fun onJsConfirm(v: WebView?, url: String?, message: String?, r: JsResult?): Boolean {
                if (r == null) return false
                if (isFinishing || !isCur(v)) { r.cancel(); return true }
                dlg().setTitle(jsTitle(url)).setMessage(message ?: "")
                    .setPositiveButton("OK") { _, _ -> r.confirm() }
                    .setNegativeButton("Batal") { _, _ -> r.cancel() }
                    .setOnCancelListener { r.cancel() }.show()
                return true
            }

            override fun onJsPrompt(v: WebView?, url: String?, message: String?, defaultValue: String?, r: JsPromptResult?): Boolean {
                if (r == null) return false
                if (isFinishing || !isCur(v)) { r.cancel(); return true }
                val et = EditText(this@MainActivity).apply { isSingleLine = true; setText(defaultValue ?: ""); setTextColor(cFg); setSelectAllOnFocus(true) }
                val box = FrameLayout(this@MainActivity).apply {
                    setPadding(dp(20), dp(8), dp(20), 0)
                    addView(et, FrameLayout.LayoutParams(MP, WC))
                }
                dlg().setTitle(jsTitle(url)).setMessage(message ?: "").setView(box)
                    .setPositiveButton("OK") { _, _ -> r.confirm(et.text.toString()) }
                    .setNegativeButton("Batal") { _, _ -> r.cancel() }
                    .setOnCancelListener { r.cancel() }.show()
                return true
            }

            override fun onShowFileChooser(v: WebView?, cb: ValueCallback<Array<Uri>>?, p: WebChromeClient.FileChooserParams?): Boolean {
                if (cb == null || p == null) return false
                fileCb?.onReceiveValue(null)
                fileCb = cb
                return try { startActivityForResult(p.createIntent(), FILE_REQ); true } catch (e: Exception) { fileCb = null; false }
            }
        }

        w.setDownloadListener { url, ua, cd, mime, len -> download(url, ua, cd, mime, len) }

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
        fsRotBtn = null
        customView?.let { (it.parent as? ViewGroup)?.removeView(it) }
        fsLayer?.let { (window.decorView as FrameLayout).removeView(it) }
        fsLayer = null
        customView = null
        customCb?.onCustomViewHidden()
        customCb = null
        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED
        if (Build.VERSION.SDK_INT >= 28) {
            window.attributes = window.attributes.apply {
                layoutInDisplayCutoutMode = android.view.WindowManager.LayoutParams.LAYOUT_IN_DISPLAY_CUTOUT_MODE_DEFAULT
            }
        }
        window.decorView.systemUiVisibility = uiFlags
    }

    private fun isLandscape() = resources.configuration.orientation == Configuration.ORIENTATION_LANDSCAPE

    private fun fsRoundBtn(res: Int, action: () -> Unit): FrameLayout = FrameLayout(this).apply {
        background = shape(Color.argb(150, 0, 0, 0), 18)
        isClickable = true
        addView(icon(res, 20, Color.WHITE), FrameLayout.LayoutParams(dp(20), dp(20), Gravity.CENTER))
        setOnClickListener { action() }
    }

    private fun showFullscreen(view: View, callback: WebChromeClient.CustomViewCallback?) {
        customView = view; customCb = callback
        val layer = LinearLayout(this).apply { orientation = LinearLayout.HORIZONTAL; setBackgroundColor(Color.BLACK); isClickable = true }
        val holder = FrameLayout(this)
        (view.parent as? ViewGroup)?.removeView(view)
        holder.addView(view, FrameLayout.LayoutParams(MP, MP))

        val rot = fsRoundBtn(R.drawable.ic_rotate) {
            requestedOrientation = if (isLandscape()) ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
                else ActivityInfo.SCREEN_ORIENTATION_SENSOR_LANDSCAPE
        }
        holder.addView(rot, FrameLayout.LayoutParams(dp(36), dp(36), Gravity.TOP or Gravity.START).apply { setMargins(dp(12), dp(12), 0, 0) })
        layer.addView(holder, LinearLayout.LayoutParams(0, MP, 1f))

        fsLayer = layer; fsRotBtn = rot
        (window.decorView as FrameLayout).addView(layer, FrameLayout.LayoutParams(MP, MP))
        window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_FULLSCREEN or
            View.SYSTEM_UI_FLAG_HIDE_NAVIGATION or View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
        if (Build.VERSION.SDK_INT >= 28) {
            window.attributes = window.attributes.apply {
                layoutInDisplayCutoutMode = android.view.WindowManager.LayoutParams.LAYOUT_IN_DISPLAY_CUTOUT_MODE_SHORT_EDGES
            }
        }
        if (bool("fsland", true)) requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_SENSOR_LANDSCAPE

        updateFsLayout()
    }

    private fun updateFsLayout() {
        fsRotBtn?.visibility = if (pipActive) View.GONE else View.VISIBLE
    }

    /* ================= Blokir iklan situs (umum) ================= */

    private val siteAdDomains = setOf(
        "doubleclick.net", "googlesyndication.com", "googleadservices.com", "adservice.google.com", "adnxs.com", "adsrvr.org",
        "advertising.com", "taboola.com", "outbrain.com", "criteo.com", "criteo.net", "rubiconproject.com", "pubmatic.com",
        "openx.net", "casalemedia.com", "indexww.com", "smartadserver.com", "adform.net", "adcolony.com", "applovin.com",
        "moatads.com", "amazon-adsystem.com", "media.net", "revcontent.com", "mgid.com", "popads.net", "popcash.net",
        "propellerads.com", "exoclick.com", "juicyads.com", "trafficjunky.net", "adsterra.com", "hilltopads.net",
        "clickadu.com", "onclickads.net", "admaven.com", "ero-advertising.com", "yllix.com", "zedo.com", "yieldmo.com",
        "sharethrough.com", "teads.tv", "33across.com", "bidswitch.net", "contextweb.com", "lijit.com", "sovrn.com",
        "adsafeprotected.com", "serving-sys.com", "mathtag.com", "adition.com", "inmobi.com", "vungle.com", "chartboost.com",
        "adcash.com", "richads.com", "a-ads.com", "adsco.re", "bidgear.com", "adnxs-simple.com", "pagead2.googlesyndication.com",
        "tpc.googlesyndication.com", "securepubads.g.doubleclick.net", "ads.yahoo.com", "adtech.com", "adtechus.com",
        "everesttech.net", "tribalfusion.com", "undertone.com", "spotxchange.com", "spotx.tv", "connatix.com", "kargo.com",
        "triplelift.com", "gumgum.com", "nativo.com", "adblade.com", "adroll.com", "quantserve.com", "scorecardresearch.com")

    private fun hostOf(u: String?): String? {
        if (u == null || !(u.startsWith("http://") || u.startsWith("https://"))) return null
        return try { Uri.parse(u).host?.lowercase() } catch (e: Exception) { null }
    }

    private fun adOff(): MutableSet<String> = HashSet(prefs.getStringSet("sadoff", emptySet()) ?: emptySet())
    private fun isAdOff(h: String) = adOff().any { h == it || h.endsWith(".$it") }
    private fun siteAdsOn(h: String?) = bool("sadblock", true) && (h == null || !isAdOff(h))

    private var saJs: String? = null
    private fun siteAdScript(): String {
        saJs?.let { return it }
        val s = try { assets.open("siteads.js").bufferedReader().use { it.readText() } } catch (e: Exception) { "" }
        saJs = s
        return s
    }
    private fun siteAdFull(): String = "window.__alOff=" + JSONArray(adOff().toList()).toString() + ";" + siteAdScript()

    private fun applySiteAds(t: Tab) {
        try { t.sadHandler?.remove() } catch (e: Exception) {}
        t.sadHandler = null
        if (!bool("sadblock", true)) return
        try {
            if (androidx.webkit.WebViewFeature.isFeatureSupported(androidx.webkit.WebViewFeature.DOCUMENT_START_SCRIPT)) {
                t.sadHandler = androidx.webkit.WebViewCompat.addDocumentStartJavaScript(t.web, siteAdFull(), setOf("*"))
            }
        } catch (e: Exception) {}
    }

    private fun injectSiteAds(w: WebView, url: String) {
        val h = hostOf(url) ?: return
        if (!siteAdsOn(h)) return
        w.evaluateJavascript(siteAdFull(), null)
    }

    private fun siteAdBlocked(v: WebView?, req: WebResourceRequest): Boolean {
        if (!bool("sadblock", true) || req.isForMainFrame) return false
        val host = req.url.host?.lowercase() ?: return false
        if (!siteAdDomains.any { host == it || host.endsWith(".$it") }) return false
        val page = try { hostOf(tabOf(v)?.url) } catch (e: Exception) { null }
        if (page != null) {
            if (isAdOff(page)) return false
            if (page == host || page.endsWith(".$host") || host.endsWith(".$page")) return false
        }
        return true
    }

    private fun toggleSiteAds() {
        val t = tabs.getOrNull(cur) ?: return
        val h = hostOf(t.url)
        if (h == null) { toast("Buka sebuah situs dulu"); return }
        if (!bool("sadblock", true)) {
            setBool("sadblock", true)
            adOff().also { set -> set.removeAll { h == it || h.endsWith(".$it") }; prefs.edit().putStringSet("sadoff", set).apply() }
            toast("Blokir iklan aktif")
        } else if (isAdOff(h)) {
            adOff().also { set -> set.removeAll { h == it || h.endsWith(".$it") }; prefs.edit().putStringSet("sadoff", set).apply() }
            toast("Blokir iklan aktif untuk $h")
        } else {
            adOff().also { set -> set.add(h); prefs.edit().putStringSet("sadoff", set).apply() }
            toast("Blokir iklan dimatikan untuk $h")
        }
        tabs.forEach { applySiteAds(it) }
        t.web.reload()
    }

    /* ================= Blokir iklan YouTube ================= */

    private var adJs: String? = null
    private fun adScript(): String {
        adJs?.let { return it }
        val s = try { assets.open("adblock.js").bufferedReader().use { it.readText() } } catch (e: Exception) { "" }
        adJs = s
        return s
    }

    private fun isYt(u: String?): Boolean {
        if (u == null) return false
        val h = try { Uri.parse(u).host } catch (e: Exception) { null } ?: return false
        return h == "youtube.com" || h.endsWith(".youtube.com")
    }

    private fun applyAdblock(t: Tab) {
        try { t.adHandler?.remove() } catch (e: Exception) {}
        t.adHandler = null
        if (!bool("adblock", true)) return
        try {
            if (androidx.webkit.WebViewFeature.isFeatureSupported(androidx.webkit.WebViewFeature.DOCUMENT_START_SCRIPT)) {
                t.adHandler = androidx.webkit.WebViewCompat.addDocumentStartJavaScript(t.web, adScript(), setOf("https://*.youtube.com"))
            }
        } catch (e: Exception) {}
    }

    private fun injectAd(w: WebView, url: String) {
        if (!bool("adblock", true) || !isYt(url)) return
        w.evaluateJavascript(adScript(), null)
    }

    private val adHosts = setOf(
        "googleads.g.doubleclick.net", "ad.doubleclick.net", "static.doubleclick.net", "securepubads.g.doubleclick.net",
        "pagead2.googlesyndication.com", "tpc.googlesyndication.com", "www.googleadservices.com", "ade.googlesyndication.com")

    private fun adBlocked(v: WebView?, req: WebResourceRequest): Boolean {
        if (!bool("adblock", true) || req.isForMainFrame) return false
        val onYt = try { isYt(tabOf(v)?.url) } catch (e: Exception) { false }
        if (!onYt) return false
        val host = req.url.host ?: return false
        if (host in adHosts) return true
        if (host == "youtube.com" || host.endsWith(".youtube.com")) {
            val path = req.url.path ?: return false
            return path.startsWith("/pagead/") || path.startsWith("/api/stats/ads") || path.startsWith("/ptracking") ||
                path.startsWith("/get_midroll_") || path.contains("/ad_break")
        }
        return false
    }

    /* ================= DNS pribadi / situs diblokir ISP ================= */

    private var dnsHinted = false

    private fun isBlockPage(u: String): Boolean {
        val h = try { Uri.parse(u).host?.lowercase() } catch (e: Exception) { null } ?: return false
        return h.contains("internetpositif") || h.contains("internetsehat") || h.contains("trustpositif") || h.endsWith("uzone.id")
    }

    private fun openDnsSettings() {
        val tries = listOf(
            Intent("android.settings.PRIVATE_DNS_SETTINGS"),
            Intent(android.provider.Settings.ACTION_WIRELESS_SETTINGS),
            Intent(android.provider.Settings.ACTION_SETTINGS))
        for (i in tries) { try { startActivity(i); return } catch (e: Exception) { } }
    }

    private fun dnsDialog() {
        if (Build.VERSION.SDK_INT < 28) { toast("DNS pribadi membutuhkan Android 9 atau lebih baru"); return }
        val hosts = arrayOf("dns.google", "one.one.one.one", "dns.adguard-dns.com")
        dlg().setTitle("Pilih DNS, lalu tempel di pengaturan")
            .setItems(hosts) { _, i ->
                (getSystemService(CLIPBOARD_SERVICE) as ClipboardManager).setPrimaryClip(ClipData.newPlainText("dns", hosts[i]))
                toast(hosts[i] + " disalin. Pilih \"Nama host penyedia DNS pribadi\" lalu tempel")
                openDnsSettings()
            }
            .setNegativeButton("Batal", null).show()
    }

    private fun showDnsHint() {
        dlg().setTitle("Situs diblokir oleh ISP")
            .setMessage("Halaman Internet Positif muncul karena penyedia internet kamu memblokir situs ini. Mengaktifkan DNS aman (DoH) sering bisa mengatasinya.")
            .setPositiveButton("Aktifkan DNS aman") { _, _ -> openRoot("settings"); openPage("privacy"); openPage("doh") }
            .setNegativeButton("Tutup", null).show()
    }

    /* ================= Zoom situs ================= */

    /* ================= Kualitas gambar (hemat kuota) ================= */

    private fun imgQ() = prefs.getInt("imgq", 0)
    private val imgQNames = arrayOf("Sesuai situs", "Hemat", "Sangat hemat")
    private val imgQDesc = arrayOf(
        "Kualitas asli dari situs, tanpa perubahan",
        "Minta gambar lebih kecil (sekitar lebar 800px, mutu 60%)",
        "Gambar kecil dan agak buram (sekitar lebar 400px, mutu 35%)")

    private fun lowerImg(u: String, lv: Int): String {
        val capW = if (lv == 1) 800 else 400
        val capQ = if (lv == 1) 60 else 35
        var out = u
        val host = Uri.parse(u).host ?: return u
        if (host.endsWith("ytimg.com")) {
            val m = Regex("^(https?://[^/?#]+/vi(?:_webp)?/[^/?#]+/)(maxresdefault|sddefault|hq720|hqdefault)(\\.\\w+)").find(u)
            if (m != null) {
                val to = if (lv == 2) "mqdefault" else "hqdefault"
                if (to != m.groupValues[2]) return m.groupValues[1] + to + m.groupValues[3]
            }
            return u
        }
        if (host.endsWith("googleusercontent.com") || host.endsWith("ggpht.com")) {
            out = Regex("=s(\\d+)").replace(out) { "=s" + minOf(it.groupValues[1].toInt(), capW) }
            out = Regex("=w(\\d+)-h(\\d+)").replace(out) {
                val w = it.groupValues[1].toInt(); val h = it.groupValues[2].toInt()
                if (w > capW) "=w" + capW + "-h" + maxOf(1, h * capW / w) else it.value
            }
        }
        if (host == "upload.wikimedia.org") {
            val std = intArrayOf(20, 40, 60, 120, 250, 330, 500, 960, 1280, 1920, 3840)
            out = Regex("/(\\d+)px-([^/?]+)$").replace(out) {
                val n = it.groupValues[1].toInt()
                val pick = std.lastOrNull { x -> x <= capW } ?: 330
                if (n > pick) "/" + pick + "px-" + it.groupValues[2] else it.value
            }
        }
        val uri = Uri.parse(out)
        val names = uri.queryParameterNames
        if (names.isEmpty()) return out
        val wKey = names.firstOrNull { it.lowercase() in setOf("w", "width", "maxwidth", "imwidth") }
        val hKey = names.firstOrNull { it.lowercase() in setOf("h", "height", "maxheight") }
        val qKey = names.firstOrNull { it.lowercase() in setOf("q", "quality") }
        val wv = wKey?.let { uri.getQueryParameter(it)?.toIntOrNull() }
        val hv = hKey?.let { uri.getQueryParameter(it)?.toIntOrNull() }
        val qv = qKey?.let { uri.getQueryParameter(it)?.toIntOrNull() }
        var nw = wv; var nh = hv; var nq = qv; var changed = false
        if (wv != null && wv > capW) { nw = capW; if (hv != null) nh = maxOf(1, hv * capW / wv); changed = true }
        if (qv != null && qv in (capQ + 1)..100) { nq = capQ; changed = true }
        if (!changed) return out
        val b = uri.buildUpon().clearQuery()
        for (n in names) {
            val vals = when (n) {
                wKey -> listOf((nw ?: wv).toString())
                hKey -> listOf((nh ?: hv).toString())
                qKey -> listOf((nq ?: qv).toString())
                else -> uri.getQueryParameters(n)
            }
            vals.forEach { b.appendQueryParameter(n, it) }
        }
        return b.build().toString()
    }

    private fun fetchImg(url: String, h: Map<String, String>): WebResourceResponse? {
        var c: HttpURLConnection? = null
        return try {
            c = URL(url).openConnection() as HttpURLConnection
            c.connectTimeout = 10000; c.readTimeout = 20000
            for ((k, v) in h) if (!k.equals("Accept-Encoding", true) && !k.equals("Range", true)) c.setRequestProperty(k, v)
            c.setRequestProperty("Save-Data", "on")
            CookieManager.getInstance().getCookie(url)?.let { c.setRequestProperty("Cookie", it) }
            val code = c.responseCode
            val mime = (c.contentType ?: "").substringBefore(';').trim()
            if (code !in 200..299 || !mime.startsWith("image/")) { c.disconnect(); return null }
            val skip = setOf("content-encoding", "transfer-encoding", "content-length", "set-cookie")
            val rh = HashMap<String, String>()
            for ((k, vs) in c.headerFields) if (k != null && vs.isNotEmpty() && k.lowercase() !in skip) rh[k] = vs[0]
            val msg = c.responseMessage?.takeIf { it.isNotEmpty() } ?: "OK"
            WebResourceResponse(mime, null, code, msg, rh, c.inputStream)
        } catch (e: Exception) { try { c?.disconnect() } catch (_: Exception) {}; null }
    }

    private fun pageImgQ(): View {
        val (page, body) = shell("Kualitas gambar", true)
        addCard(body, imgQNames.indices.map { i ->
            listRow(iconTile(R.drawable.ic_sliders), imgQNames[i], imgQDesc[i],
                if (imgQ() == i) icon(R.drawable.ic_check, 20, cAcc) else null,
                { prefs.edit().putInt("imgq", i).apply(); renderPage() })
        })
        body.addView(tv("Berlaku untuk halaman yang dibuka setelah ini. Hasilnya bergantung pada situs: Alethra meminta versi gambar yang lebih kecil, jadi situs yang tidak menyediakannya tetap memuat gambar aslinya.", 13f, cSub, false).apply { setPadding(dp(20), dp(14), dp(20), 0) }, lp(MP, WC))
        return page
    }

    private val dlLocKeys = arrayOf("downloads", "alethra", "documents", "pictures", "movies", "music")
    private val dlLocNames = arrayOf("Unduhan", "Unduhan/Alethra", "Dokumen", "Gambar", "Film", "Musik")
    private fun dlLocIdx() = dlLocKeys.indexOf(prefs.getString("dlloc", "downloads")).coerceAtLeast(0)
    private fun dlLocName() = dlLocNames[dlLocIdx()]

    private fun dlTarget(i: Int): Pair<String, String> = when (dlLocKeys[i]) {
        "alethra" -> Pair(Environment.DIRECTORY_DOWNLOADS, "Alethra")
        "documents" -> Pair(Environment.DIRECTORY_DOCUMENTS, "")
        "pictures" -> Pair(Environment.DIRECTORY_PICTURES, "")
        "movies" -> Pair(Environment.DIRECTORY_MOVIES, "")
        "music" -> Pair(Environment.DIRECTORY_MUSIC, "")
        else -> Pair(Environment.DIRECTORY_DOWNLOADS, "")
    }

    private fun download(url: String, ua: String?, cd: String?, mime: String?, len: Long = -1) {
        if (!url.startsWith("http")) { toast("Jenis unduhan ini belum didukung"); return }
        if (Build.VERSION.SDK_INT < 29 && checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE), 9)
            toast("Izinkan penyimpanan lalu coba unduh lagi")
            return
        }
        val name = URLUtil.guessFileName(url, cd, mime)
        if (!bool("dlask", false)) { startDownload(url, ua, mime, name); return }
        val et = EditText(this).apply {
            setText(name); isSingleLine = true
            setTextColor(cFg); setSelectAllOnFocus(true)
        }
        val box = FrameLayout(this).apply {
            setPadding(dp(20), dp(8), dp(20), 0)
            addView(et, FrameLayout.LayoutParams(MP, WC))
        }
        val size = if (len > 0) Formatter.formatFileSize(this, len) + " · " else ""
        dlg().setTitle("Unduh berkas")
            .setMessage(size + "Disimpan di " + dlLocName())
            .setView(box)
            .setPositiveButton("Unduh") { _, _ ->
                val n = et.text.toString().replace(Regex("[/\\:*?\"<>|]"), "_").trim()
                startDownload(url, ua, mime, if (n.isEmpty()) name else n)
            }
            .setNegativeButton("Batal", null).show()
    }

    private fun startDownload(url: String, ua: String?, mime: String?, name: String) {
        val dm = getSystemService(DOWNLOAD_SERVICE) as DownloadManager
        fun build(dir: String, sub: String) = DownloadManager.Request(Uri.parse(url)).apply {
            setMimeType(mime)
            addRequestHeader("User-Agent", ua ?: defaultUa)
            cookieMgr(tabs.getOrNull(cur)?.web).getCookie(url)?.let { addRequestHeader("Cookie", it) }
            setTitle(name)
            setNotificationVisibility(
                if (bool("dlprog", false)) DownloadManager.Request.VISIBILITY_VISIBLE
                else DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)
            if (bool("dlwifi", false)) setAllowedNetworkTypes(DownloadManager.Request.NETWORK_WIFI)
            setAllowedOverRoaming(bool("dlroam", true))
            setDestinationInExternalPublicDir(dir, if (sub.isEmpty()) name else "$sub/$name")
        }
        try {
            val (dir, sub) = dlTarget(dlLocIdx())
            val id = try { dm.enqueue(build(dir, sub)) }
                catch (e: Exception) { dm.enqueue(build(Environment.DIRECTORY_DOWNLOADS, "")) }
            if (tabs.getOrNull(cur)?.incog == true) { toast("Mengunduh $name"); return }
            val a = getList("dl")
            val n = JSONArray()
            n.put(JSONObject().put("id", id).put("t", name).put("ts", System.currentTimeMillis()))
            for (i in 0 until minOf(a.length(), 99)) n.put(a.getJSONObject(i))
            prefs.edit().putString("dl", n.toString()).apply()
            toast(if (bool("dlwifi", false)) "$name menunggu Wi-Fi bila belum tersambung" else "Mengunduh $name")
        } catch (e: Exception) {
            toast("Gagal mengunduh")
        }
    }

    private fun pageDlSet(): View {
        val (page, body) = shell("Unduhan", true)
        body.addView(sectionLabel("Penyimpanan"))
        addCard(body, listOf(
            navRow(R.drawable.ic_download, "Lokasi simpan", dlLocName()) { openPage("dlloc") },
            toggleRow("Tanya sebelum mengunduh", "Tampilkan jendela untuk mengubah nama berkas", bool("dlask", false)) { c -> setBool("dlask", c) }))
        body.addView(sectionLabel("Jaringan"))
        addCard(body, listOf(
            toggleRow("Hanya lewat Wi-Fi", "Tahan unduhan sampai tersambung ke Wi-Fi untuk menghemat kuota", bool("dlwifi", false)) { c -> setBool("dlwifi", c) },
            toggleRow("Izinkan saat roaming", "Unduh meski sedang di luar jaringan asal", bool("dlroam", true)) { c -> setBool("dlroam", c) }))
        body.addView(sectionLabel("Notifikasi"))
        addCard(body, listOf(
            toggleRow("Tampilkan progres", "Notifikasi dengan progres saat mengunduh, bukan hanya saat selesai", bool("dlprog", false)) { c -> setBool("dlprog", c) }))
        body.addView(sectionLabel("Riwayat"))
        addCard(body, listOf(
            navRow(R.drawable.ic_trash, "Hapus riwayat unduhan", "Berkas di perangkat tidak ikut terhapus", false) {
                dlg().setMessage("Hapus semua riwayat unduhan di Alethra? Berkasnya tetap ada di perangkat.")
                    .setPositiveButton("Hapus") { _, _ -> prefs.edit().remove("dl").apply(); toast("Riwayat unduhan dihapus") }
                    .setNegativeButton("Batal", null).show()
            }))
        return page
    }

    private fun pageDlLoc(): View {
        val (page, body) = shell("Lokasi simpan", true)
        addCard(body, dlLocNames.indices.map { i ->
            listRow(iconTile(R.drawable.ic_file), dlLocNames[i],
                if (i == 1) "Folder khusus Alethra di dalam Unduhan" else "Folder " + dlLocNames[i] + " di penyimpanan",
                if (dlLocIdx() == i) icon(R.drawable.ic_check, 20, cAcc) else null,
                { prefs.edit().putString("dlloc", dlLocKeys[i]).apply(); renderPage() })
        })
        return page
    }

    /* ================= Incognito ================= */

    private val INCOG_PROFILE = "alethra_incognito"

    private fun profilesOk(): Boolean = try {
        androidx.webkit.WebViewFeature.isFeatureSupported(androidx.webkit.WebViewFeature.MULTI_PROFILE)
    } catch (e: Exception) { false }

    private fun isIncog(w: WebView?): Boolean = (w?.tag as? String)?.startsWith("incog") == true

    private fun cookieMgr(w: WebView?): CookieManager = try {
        if (w?.tag == "incog") androidx.webkit.ProfileStore.getInstance().getProfile(INCOG_PROFILE)?.cookieManager ?: CookieManager.getInstance()
        else CookieManager.getInstance()
    } catch (e: Exception) { CookieManager.getInstance() }

    /** Hapus semua jejak sesi Incognito (cookie, storage, cache profil terpisah). */
    private fun wipeIncognitoData() {
        incogSites = JSONObject()
        try {
            if (profilesOk()) {
                val ps = androidx.webkit.ProfileStore.getInstance()
                ps.getProfile(INCOG_PROFILE)?.let { it.cookieManager.removeAllCookies(null); it.webStorage.deleteAllData() }
                ps.deleteProfile(INCOG_PROFILE)
            }
        } catch (e: Exception) {}
        if (incogWeak) { incogWeak = false; try { CookieManager.getInstance().setAcceptCookie(true) } catch (e: Exception) {} }
    }

    private fun applyIncogUi() {
        updateSecureFlag()
        omnibox.hint = if (incognito) "Penyamaran: cari atau masukkan alamat" else "Cari atau masukkan alamat web"
        omnibox.imeOptions = if (incognito && Build.VERSION.SDK_INT >= 26)
            EditorInfo.IME_ACTION_GO or EditorInfo.IME_FLAG_NO_PERSONALIZED_LEARNING else EditorInfo.IME_ACTION_GO
        updateChrome()
    }

    private fun setIncognito(on: Boolean) {
        if (on && !incognito && partLocked("lockinc") && !authFresh()) {
            authenticate("Buka kunci Incognito") { ok -> if (ok) doSetIncognito(true) }
            return
        }
        doSetIncognito(on)
    }

    private fun doSetIncognito(on: Boolean) {
        if (on == incognito) return
        autoMini()
        if (on) {
            incognito = true
            incogWeak = !profilesOk()
            if (incogWeak) try { CookieManager.getInstance().setAcceptCookie(false) } catch (e: Exception) {}
            applyIncogUi()
            pages.clear(); pageView = null
            newTab(home(), true, true)
            toast("Mode Incognito aktif: riwayat, cookie, cache, dan data situs tidak disimpan")
        } else {
            val doomed = tabs.filter { it.incog }
            if (doomed.any { it === miniTab }) stopMini(true)
            incognito = false
            doomed.forEach { t ->
                tabs.remove(t)
                container.removeView(t.web)
                t.web.stopLoading(); t.web.destroy()
            }
            wipeIncognitoData()
            applyIncogUi()
            pages.clear(); pageView = null
            cur = -1
            if (tabs.isEmpty()) newTab(home(), true, false) else selectTab(tabs.size - 1)
            toast("Mode Incognito nonaktif: tab dan data sesi dihapus")
        }
    }

    /* ================= Tab ================= */

    private fun newTab(url: String?, select: Boolean = true, incog: Boolean = incognito): Tab {
        val t = Tab(makeWeb(incog))
        t.incog = incog
        tabs.add(t)
        applyAdblock(t)
        applySiteAds(t)
        applyNotif(t)
        if (url != null) { applySite(t.web, url); t.web.loadUrl(url) }
        if (select) selectTab(tabs.size - 1) else updateChrome()
        return t
    }

    private fun selectTab(i: Int, closeAllPages: Boolean = true) {
        if (tabs[i] === miniTab) stopMini(false)
        tabs.getOrNull(cur)?.let { if (it !== tabs[i]) captureThumb(it) }
        cur = i
        if (closeAllPages) { pages.clear(); pageView = null }
        container.removeAllViews()
        val w = tabs[i].web
        (w.parent as? ViewGroup)?.removeView(w)
        container.addView(w, FrameLayout.LayoutParams(MP, MP))
        srl.isRefreshing = false
        tabs[i].pending?.let { tabs[i].pending = null; w.loadUrl(it) }
        omnibox.clearFocus()
        setUrlText(tabs[i].url)
        progress.visibility = View.GONE
        updateChrome()
    }

    private fun rememberClosed(t: Tab) {
        if (incognito || t.incog || t.url.startsWith(ASSET) || t.url == "about:blank") return
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
        val bu = offOrig(t.url) ?: t.url
        val a = getList("bm")
        for (i in 0 until a.length()) if (a.getJSONObject(i).optString("u") == bu) { toast("Sudah ada di bookmark"); return }
        val n = JSONArray()
        n.put(JSONObject().put("t", t.title.ifEmpty { bu }).put("u", bu).put("ts", System.currentTimeMillis()))
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

    /* ================= Pengaturan per situs ================= */

    private var incogSites = JSONObject()
    private var siteCache: JSONObject? = null
    private val webHost: MutableMap<WebView, String> = java.util.Collections.synchronizedMap(java.util.WeakHashMap<WebView, String>())

    /** www., m., mobile., touch. dianggap situs yang sama, supaya redirect m.youtube.com <-> www.youtube.com tidak saling menimpa pengaturan. */
    private fun siteKey(h: String?): String? {
        var k = h?.lowercase()?.removePrefix("www.") ?: return null
        for (p in arrayOf("m.", "mobile.", "touch.")) {
            if (k.startsWith(p) && k.indexOf('.', p.length) > 0) { k = k.substring(p.length); break }
        }
        return k.takeIf { it.isNotEmpty() }
    }

    /** Pengaturan lama yang tersimpan dengan kunci "m.situs.com" digabung ke "situs.com". */
    private fun migrateSiteKeys() {
        val all = siteStore()
        val moves = ArrayList<Pair<String, String>>()
        all.keys().forEach { k -> val n = siteKey(k); if (n != null && n != k) moves.add(k to n) }
        if (moves.isEmpty()) return
        moves.forEach { (from, to) ->
            val src = all.optJSONObject(from) ?: return@forEach
            val dst = all.optJSONObject(to) ?: JSONObject()
            src.keys().forEach { f -> if (!dst.has(f)) dst.put(f, src.get(f)) }
            all.put(to, dst)
            all.remove(from)
        }
        prefs.edit().putString("siteprefs", all.toString()).apply()
    }

    private fun siteStore(): JSONObject {
        siteCache?.let { return it }
        val o = try { JSONObject(prefs.getString("siteprefs", "{}") ?: "{}") } catch (e: Exception) { JSONObject() }
        siteCache = o
        return o
    }

    /** Di mode Incognito perubahan hanya disimpan di memori dan hilang saat Incognito dimatikan. */
    private fun siteObj(h: String): JSONObject? = (if (incognito) incogSites.optJSONObject(h) else null) ?: siteStore().optJSONObject(h)
    private fun siteInt(h: String?, k: String, d: Int): Int = if (h == null) d else (siteObj(h)?.optInt(k, d) ?: d)
    private fun sitePerm(h: String?, k: String) = siteInt(h, k, 0)
    // Mode desktop, JavaScript, dan zoom berlaku sama untuk semua situs (tidak per situs)
    @Suppress("UNUSED_PARAMETER") private fun siteDesktop(h: String?) = bool("desktop", false)
    @Suppress("UNUSED_PARAMETER") private fun siteJs(h: String?) = bool("js", true)
    @Suppress("UNUSED_PARAMETER") private fun siteZoom(h: String?) = prefs.getInt("zoom", 100)

    private fun sitePut(h: String, k: String, v: Int?) {
        if (incognito) {
            val base = incogSites.optJSONObject(h) ?: JSONObject((siteStore().optJSONObject(h) ?: JSONObject()).toString())
            if (v == null) base.remove(k) else base.put(k, v)
            incogSites.put(h, base)
            return
        }
        val all = siteStore()
        val o = all.optJSONObject(h) ?: JSONObject()
        if (v == null) o.remove(k) else o.put(k, v)
        if (o.length() == 0) all.remove(h) else all.put(h, o)
        prefs.edit().putString("siteprefs", all.toString()).apply()
    }

    private fun siteClear(h: String) {
        if (incognito) { incogSites.put(h, JSONObject()); return }
        val all = siteStore()
        all.remove(h)
        prefs.edit().putString("siteprefs", all.toString()).apply()
    }

    private fun siteHosts(): List<String> {
        val s = HashSet<String>()
        val a = siteStore()
        a.keys().forEach { k -> if ((a.optJSONObject(k)?.length() ?: 0) > 0) s.add(k) }
        if (incognito) incogSites.keys().forEach { k -> if ((incogSites.optJSONObject(k)?.length() ?: 0) > 0) s.add(k) else s.remove(k) }
        return s.sorted()
    }

    /** Izin lama ("host|label") dipindahkan ke pengaturan situs agar bisa dilihat dan dicabut. */
    private fun migratePerms() {
        val s = prefs.getStringSet("permok", null) ?: return
        s.forEach { e ->
            val i = e.indexOf('|')
            val h = if (i > 0) siteKey(e.substring(0, i)) else null
            if (h != null) {
                val l = e.substring(i + 1)
                if (l.contains("kamera")) sitePut(h, "cam", 1)
                if (l.contains("mikrofon")) sitePut(h, "mic", 1)
                if (l.contains("lokasi")) sitePut(h, "loc", 1)
            }
        }
        prefs.edit().remove("permok").apply()
    }

    private fun toggleSiteDesktop() {
        val on = bool("desktop", false)
        setBool("desktop", !on)
        applyAllSites(true)
        toast(if (on) "Mode desktop dimatikan" else "Mode desktop aktif")
    }

    private fun resetSitePerms() {
        dlg().setMessage("Hapus semua izin situs yang tersimpan (kamera, mikrofon, lokasi, notifikasi)?")
            .setPositiveButton("Hapus") { _, _ ->
                prefs.edit().remove("siteprefs").apply()
                siteCache = null
                incogSites = JSONObject()
                toast("Izin situs dihapus")
            }.setNegativeButton("Batal", null).show()
    }

    /** Zoom teks untuk semua situs. */
    private fun zoomDialog() {
        val steps = intArrayOf(50, 75, 90, 100, 110, 125, 150, 175, 200, 250, 300)
        val label = tv("", 20f, cFg, true).apply { gravity = Gravity.CENTER; minWidth = dp(90) }
        fun cur0() = prefs.getInt("zoom", 100)
        fun refresh() { label.text = cur0().toString() + "%" }
        fun setZ(n: Int) {
            prefs.edit().putInt("zoom", n).apply()
            tabs.forEach { applySite(it.web, it.url) }
            refresh()
        }
        fun step(dir: Int) {
            val now = cur0()
            setZ(if (dir > 0) steps.firstOrNull { it > now } ?: steps.last() else steps.lastOrNull { it < now } ?: steps.first())
        }
        fun btn(txt: String, a: () -> Unit) = tv(txt, 22f, cFg, true).apply {
            gravity = Gravity.CENTER
            background = shape(cSurf, 14)
            setPadding(dp(22), dp(10), dp(22), dp(10))
            isClickable = true
            setOnClickListener { a() }
        }
        val row = LinearLayout(this).apply { orientation = LinearLayout.HORIZONTAL; gravity = Gravity.CENTER; setPadding(dp(16), dp(24), dp(16), dp(8)) }
        row.addView(btn("−") { step(-1) })
        row.addView(label, lp(WC, WC).apply { setMargins(dp(12), 0, dp(12), 0) })
        row.addView(btn("+") { step(1) })
        val col = LinearLayout(this).apply { orientation = LinearLayout.VERTICAL; gravity = Gravity.CENTER_HORIZONTAL }
        col.addView(row)
        col.addView(tv("Berlaku untuk semua situs", 12f, cSub).apply { gravity = Gravity.CENTER })
        col.addView(btn("Reset") { setZ(100) }.apply { textSize = 14f }, lp(WC, WC).apply { setMargins(0, dp(10), 0, dp(12)) })
        refresh()
        dlg().setTitle("Zoom teks").setView(col).setPositiveButton("Selesai", null).show()
    }

    /* ================= Izin situs & notifikasi ================= */

    private fun askSitePerm(h: String, sk: String?, msg: String, keys: List<String>, result: (Boolean) -> Unit) {
        var answered = false
        dlg().setTitle("Izin situs")
            .setMessage(msg)
            .setPositiveButton("Izinkan") { _, _ -> answered = true; if (sk != null) keys.forEach { sitePut(sk, it, 1) }; result(true) }
            .setNeutralButton("Blokir") { _, _ -> answered = true; if (sk != null) keys.forEach { sitePut(sk, it, 2) }; result(false) }
            .setNegativeButton("Tolak") { _, _ -> answered = true; result(false) }
            .setOnDismissListener { if (!answered) result(false) }
            .show()
    }

    /** Minta izin ke pengguna lalu ke Android. Pilihan Izinkan/Blokir diingat per situs. */
    private fun webPerm(site: String?, label: String, perms: Array<String>, anyOf: Boolean = false, keys: List<String> = emptyList(), result: (Boolean) -> Unit) {
        if (isFinishing) { result(false); return }
        val h = site ?: "Situs ini"
        val sk = siteKey(site)
        if (sk != null && keys.isNotEmpty()) {
            if (keys.any { sitePerm(sk, it) == 2 }) { result(false); return }
            if (keys.all { sitePerm(sk, it) == 1 }) { askAndroidPerms(perms, anyOf, result); return }
        }
        askSitePerm(h, sk, "$h ingin menggunakan $label perangkatmu.", keys) { ok ->
            if (ok) askAndroidPerms(perms, anyOf, result) else result(false)
        }
    }

    private fun notifPerms(): Array<String> =
        if (Build.VERSION.SDK_INT >= 33) arrayOf(Manifest.permission.POST_NOTIFICATIONS) else emptyArray()

    private fun notifAllowed(): Boolean =
        Build.VERSION.SDK_INT < 33 || checkSelfPermission(Manifest.permission.POST_NOTIFICATIONS) == PackageManager.PERMISSION_GRANTED

    private val notifTimes = ArrayDeque<Long>()
    private var notifSeq = 0

    private fun showSiteNotif(host: String, title: String, body: String, tag: String) {
        if (!notifAllowed()) return
        val now = System.currentTimeMillis()
        while (notifTimes.isNotEmpty() && now - notifTimes.first() > 60_000L) notifTimes.removeFirst()
        if (notifTimes.size >= 6) return
        notifTimes.addLast(now)
        try {
            val nm = getSystemService(NOTIFICATION_SERVICE) as android.app.NotificationManager
            if (Build.VERSION.SDK_INT >= 26)
                nm.createNotificationChannel(android.app.NotificationChannel("site_notif", "Notifikasi situs", android.app.NotificationManager.IMPORTANCE_DEFAULT))
            val pi = android.app.PendingIntent.getActivity(this, 0,
                Intent(this, MainActivity::class.java).addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP or Intent.FLAG_ACTIVITY_NEW_TASK),
                android.app.PendingIntent.FLAG_IMMUTABLE or android.app.PendingIntent.FLAG_UPDATE_CURRENT)
            val b = if (Build.VERSION.SDK_INT >= 26) android.app.Notification.Builder(this, "site_notif") else android.app.Notification.Builder(this)
            b.setSmallIcon(R.drawable.ic_logo).setContentTitle(title.take(100)).setContentText(body.take(300))
                .setSubText(host).setContentIntent(pi).setAutoCancel(true)
            val id = if (tag.isNotEmpty()) (host + tag).hashCode() else 1000 + (notifSeq++ % 50)
            nm.notify(id, b.build())
        } catch (e: Exception) {}
    }

    private var ntJs: String? = null
    private fun notifScript(): String {
        ntJs?.let { return it }
        val s = try { assets.open("notif.js").bufferedReader().use { it.readText() } } catch (e: Exception) { "" }
        ntJs = s
        return s
    }

    private fun applyNotif(t: Tab) {
        try { t.ntHandler?.remove() } catch (e: Exception) {}
        t.ntHandler = null
        try {
            if (androidx.webkit.WebViewFeature.isFeatureSupported(androidx.webkit.WebViewFeature.DOCUMENT_START_SCRIPT))
                t.ntHandler = androidx.webkit.WebViewCompat.addDocumentStartJavaScript(t.web, notifScript(), setOf("*"))
        } catch (e: Exception) {}
    }

    private fun injectNotif(w: WebView, url: String) {
        if (url.startsWith("http")) w.evaluateJavascript(notifScript(), null)
    }

    /* ================= Kunci aplikasi (sidik jari / PIN perangkat) ================= */

    private var lockView: View? = null
    private var authBusy = false
    private var authCb: ((Boolean) -> Unit)? = null
    private var lastAuth = 0L
    private var leftAt = 0L
    private var skipNextLock = false

    private fun lockMode() = prefs.getString("lock", "off") ?: "off"
    private fun lockGraceMs(): Long = prefs.getInt("lockgrace", 60) * 1000L
    private fun partLocked(k: String) = lockMode() == "parts" && bool(k, true)
    private fun authFresh(): Boolean = lastAuth > 0L && android.os.SystemClock.elapsedRealtime() - lastAuth < lockGraceMs()

    private fun lockApplies(): Boolean = when (lockMode()) {
        "app" -> true
        "parts" -> (bool("lockinc", true) && incognito) ||
            (bool("lockbm", true) && pages.lastOrNull() == "bookmarks") ||
            (bool("lockpw", true) && pages.lastOrNull() == "pwlist")
        else -> false
    }

    private fun authFlags(): Int =
        android.hardware.biometrics.BiometricManager.Authenticators.BIOMETRIC_WEAK or
            android.hardware.biometrics.BiometricManager.Authenticators.DEVICE_CREDENTIAL

    private fun deviceAuthOk(): Boolean = try {
        if (Build.VERSION.SDK_INT >= 30) {
            val bm = getSystemService(android.hardware.biometrics.BiometricManager::class.java)
            bm != null && bm.canAuthenticate(authFlags()) == android.hardware.biometrics.BiometricManager.BIOMETRIC_SUCCESS
        } else (getSystemService(KEYGUARD_SERVICE) as android.app.KeyguardManager).isDeviceSecure
    } catch (e: Exception) { false }

    /** Android 11+: BiometricPrompt (sidik jari/wajah atau PIN perangkat). Di bawah itu: layar PIN/pola sistem. */
    private fun authenticate(title: String, done: (Boolean) -> Unit) {
        if (authBusy) { done(false); return }
        if (!deviceAuthOk()) { noScreenLockDialog(); done(false); return }
        authBusy = true
        val end = { ok: Boolean ->
            authBusy = false
            if (ok) lastAuth = android.os.SystemClock.elapsedRealtime()
            done(ok)
        }
        if (Build.VERSION.SDK_INT >= 30) {
            try {
                val bp = android.hardware.biometrics.BiometricPrompt.Builder(this)
                    .setTitle(title)
                    .setSubtitle("Gunakan sidik jari atau PIN perangkat")
                    .setAllowedAuthenticators(authFlags())
                    .build()
                bp.authenticate(android.os.CancellationSignal(), mainExecutor, object : android.hardware.biometrics.BiometricPrompt.AuthenticationCallback() {
                    override fun onAuthenticationSucceeded(result: android.hardware.biometrics.BiometricPrompt.AuthenticationResult?) { end(true) }
                    override fun onAuthenticationError(errorCode: Int, errString: CharSequence?) { end(false) }
                })
            } catch (e: Exception) { end(false) }
        } else {
            try {
                val km = getSystemService(KEYGUARD_SERVICE) as android.app.KeyguardManager
                val i = km.createConfirmDeviceCredentialIntent(title, "Masukkan PIN, pola, atau kata sandi perangkat")
                if (i == null) { end(false); return }
                authCb = end
                startActivityForResult(i, LOCK_REQ)
            } catch (e: Exception) { authCb = null; end(false) }
        }
    }

    private fun noScreenLockDialog() {
        dlg().setTitle("Kunci layar belum diatur")
            .setMessage("Atur PIN, pola, kata sandi, atau sidik jari di pengaturan Android dulu, lalu coba lagi.")
            .setPositiveButton("Buka pengaturan") { _, _ ->
                try { startActivity(Intent(android.provider.Settings.ACTION_SECURITY_SETTINGS)) } catch (e: Exception) {}
            }
            .setNegativeButton("Batal", null).show()
    }

    /** Lewati jika kunci tidak aktif atau baru saja dibuka; jika tidak, minta verifikasi dulu. */
    private fun requireCfg(then: () -> Unit, deny: () -> Unit = {}) {
        if (lockMode() == "off" || authFresh()) { then(); return }
        authenticate("Ubah pengaturan kunci") { ok -> if (ok) then() else deny() }
    }

    private fun showLock() {
        if (lockView != null || isFinishing) return
        if (!deviceAuthOk()) {
            prefs.edit().putString("lock", "off").apply()
            toast("Kunci layar perangkat tidak ditemukan, kunci aplikasi dimatikan")
            return
        }
        val c = FrameLayout(this).apply { setBackgroundColor(cBg); isClickable = true; isFocusable = true }
        val col = LinearLayout(this).apply { orientation = LinearLayout.VERTICAL; gravity = Gravity.CENTER_HORIZONTAL; setPadding(dp(32), 0, dp(32), 0) }
        col.addView(icon(R.drawable.ic_lock, 44, cFg), lp(dp(44), dp(44)))
        col.addView(tv("Alethra terkunci", 20f, cFg, true), lp(WC, WC).apply { topMargin = dp(16) })
        col.addView(tv("Buka dengan sidik jari atau PIN perangkat", 14f, cSub).apply { gravity = Gravity.CENTER }, lp(WC, WC).apply { topMargin = dp(6) })
        col.addView(tv("Buka kunci", 15f, cOnAcc, true).apply {
            background = shape(cAcc, 22)
            setPadding(dp(28), dp(12), dp(28), dp(12))
            isClickable = true
            setOnClickListener { promptUnlock() }
        }, lp(WC, WC).apply { topMargin = dp(24) })
        c.addView(col, FrameLayout.LayoutParams(WC, WC, Gravity.CENTER))
        (window.decorView as FrameLayout).addView(c, FrameLayout.LayoutParams(MP, MP))
        lockView = c
        hideKb()
        tabs.getOrNull(cur)?.web?.onPause()
        handler.post { promptUnlock() }
    }

    private fun promptUnlock() {
        if (lockView == null || authBusy) return
        authenticate("Buka kunci Alethra") { ok -> if (ok) hideLock() }
    }

    private fun hideLock() {
        lockView?.let { (window.decorView as FrameLayout).removeView(it) }
        lockView = null
        tabs.getOrNull(cur)?.web?.onResume()
    }

    private fun updateSecureFlag() {
        if (incognito && bool("incogsecure", true)) window.addFlags(android.view.WindowManager.LayoutParams.FLAG_SECURE)
        else window.clearFlags(android.view.WindowManager.LayoutParams.FLAG_SECURE)
    }

    private fun lockSummary() = when (lockMode()) {
        "app" -> "Seluruh aplikasi"
        "parts" -> "Incognito, bookmark, kata sandi"
        else -> "Mati"
    }

    private fun graceLabel(): String = when (prefs.getInt("lockgrace", 60)) {
        0 -> "Langsung"; 30 -> "Setelah 30 detik"; 60 -> "Setelah 1 menit"; 300 -> "Setelah 5 menit"; 900 -> "Setelah 15 menit"
        else -> "Setelah " + prefs.getInt("lockgrace", 60) + " detik"
    }

    private fun setLockMode(k: String) {
        if (k == lockMode()) return
        val commit = { prefs.edit().putString("lock", k).apply(); renderPage() }
        if (k != "off" && !deviceAuthOk()) { noScreenLockDialog(); return }
        if (lockMode() != "off") requireCfg({ commit() })
        else authenticate("Aktifkan kunci aplikasi") { ok -> if (ok) commit() }
    }

    private fun pageLock(): View {
        val (page, body) = shell("Kunci aplikasi", true)
        body.addView(sectionLabel("Mode"))
        val modes = listOf(
            Triple("off", "Mati", "Tanpa kunci"),
            Triple("app", "Seluruh aplikasi", "Minta sidik jari atau PIN setiap membuka Alethra"),
            Triple("parts", "Bagian tertentu", "Hanya Incognito, bookmark, dan kata sandi"))
        addCard(body, modes.map { (k, t, s) ->
            listRow(iconTile(R.drawable.ic_lock), t, s, if (lockMode() == k) icon(R.drawable.ic_check, 20, cAcc) else null, { setLockMode(k) })
        })
        if (lockMode() == "parts") {
            body.addView(sectionLabel("Yang dikunci"))
            fun part(title: String, sub: String, k: String) = toggleRow(title, sub, bool(k, true)) { c ->
                if (c) setBool(k, true) else requireCfg({ setBool(k, false) }, { renderPage() })
            }
            addCard(body, listOf(
                part("Tab Incognito", "Minta kunci saat mengaktifkan atau kembali ke Incognito", "lockinc"),
                part("Bookmark", "Minta kunci saat membuka daftar bookmark", "lockbm"),
                part("Kata sandi", "Minta kunci untuk daftar dan isi otomatis kata sandi", "lockpw")))
        }
        if (lockMode() != "off") {
            body.addView(sectionLabel("Waktu kunci"))
            val opts = intArrayOf(0, 30, 60, 300, 900)
            addCard(body, listOf(navRow(R.drawable.ic_sliders, "Kunci otomatis", graceLabel()) {
                dlg().setTitle("Kunci otomatis")
                    .setItems(arrayOf("Langsung", "Setelah 30 detik", "Setelah 1 menit", "Setelah 5 menit", "Setelah 15 menit")) { _, i ->
                        requireCfg({ prefs.edit().putInt("lockgrace", opts[i]).apply(); renderPage() })
                    }.show()
            }))
        }
        body.addView(tv("Memakai sidik jari atau PIN, pola, dan kata sandi layar kunci Android. Jika kunci layar perangkat dihapus, kunci aplikasi dimatikan otomatis agar kamu tidak terkunci di luar.", 12f, cSub)
            .apply { setPadding(dp(20), dp(14), dp(20), dp(8)) })
        return page
    }

    /* ================= DNS aman (DoH) ================= */

    private val dohKeys = arrayOf("off", "cloudflare", "cfsec", "adguard", "quad9", "google", "custom")
    private val dohNames = arrayOf("Mati", "Cloudflare", "Cloudflare Keamanan", "AdGuard", "Quad9", "Google", "Kustom")
    private val dohDesc = arrayOf(
        "Pakai DNS bawaan jaringan",
        "Cepat dan privat",
        "Cloudflare, memblokir malware dan phishing",
        "Memblokir iklan, pelacak, dan situs berbahaya",
        "Memblokir situs malware dan phishing",
        "Cepat, tanpa pemblokiran",
        "Alamat server DoH milikmu sendiri")
    private val dohUrls = arrayOf(
        "", "https://cloudflare-dns.com/dns-query", "https://security.cloudflare-dns.com/dns-query",
        "https://dns.adguard-dns.com/dns-query", "https://dns.quad9.net/dns-query", "https://dns.google/dns-query", "")
    private var doh: DohProxy? = null

    private fun dohKey() = prefs.getString("doh", "off") ?: "off"
    private fun dohIdx() = dohKeys.indexOf(dohKey()).coerceAtLeast(0)
    private fun dohName() = dohNames[dohIdx()]
    private fun dohEndpoint(): String {
        val i = dohIdx()
        return when {
            i <= 0 -> ""
            dohKeys[i] == "custom" -> prefs.getString("dohcustom", "") ?: ""
            else -> dohUrls[i]
        }
    }

    /** Arahkan semua lalu lintas WebView ke proxy lokal DoH (atau lepas jika dimatikan). */
    private fun applyDoh(then: () -> Unit = {}) {
        val on = dohKey() != "off" && dohEndpoint().startsWith("https://")
        if (!on && !prefs.getBoolean("dohset", false)) { then(); return }
        val supported = try {
            androidx.webkit.WebViewFeature.isFeatureSupported(androidx.webkit.WebViewFeature.PROXY_OVERRIDE)
        } catch (e: Exception) { false }
        if (!supported) {
            if (on) toast("WebView perangkat ini belum mendukung DNS aman. Perbarui Android System WebView.")
            then(); return
        }
        try {
            val ctl = androidx.webkit.ProxyController.getInstance()
            val run = java.util.concurrent.Executor { it.run() }
            if (!on) {
                prefs.edit().putBoolean("dohset", false).apply()
                ctl.clearProxyOverride(run, Runnable { handler.post { then() } })
                return
            }
            val p = doh ?: DohProxy({ dohEndpoint() }, { dohKey() == "quad9" }).also { doh = it }
            val port = p.start()
            p.flush()
            val cfg = androidx.webkit.ProxyConfig.Builder().addProxyRule("127.0.0.1:$port").addDirect().build()
            prefs.edit().putBoolean("dohset", true).apply()
            ctl.setProxyOverride(cfg, run, Runnable { handler.post { then() } })
        } catch (e: Exception) {
            toast("DNS aman gagal diaktifkan")
            then()
        }
    }

    private fun chooseDoh(i: Int) {
        if (dohKeys[i] == "custom") {
            val e = EditText(this).apply {
                isSingleLine = true
                setText(prefs.getString("dohcustom", "https://"))
                setTextColor(cFg); setSelectAllOnFocus(true)
                inputType = InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_URI
            }
            dlg().setTitle("Alamat server DoH").setMessage("Contoh: https://dns.example.com/dns-query").setView(e)
                .setPositiveButton("Simpan") { _, _ ->
                    val u = e.text.toString().trim()
                    if (!u.startsWith("https://") || u.length < 12) toast("Alamat harus diawali https://")
                    else { prefs.edit().putString("dohcustom", u).putString("doh", "custom").apply(); applyDoh(); renderPage() }
                }
                .setNegativeButton("Batal", null).show()
            return
        }
        prefs.edit().putString("doh", dohKeys[i]).apply()
        applyDoh()
        toast(if (i == 0) "DNS aman dimatikan" else "DNS aman aktif: " + dohNames[i])
        renderPage()
    }

    private fun pageDoh(): View {
        val (page, body) = shell("DNS aman (DoH)", true)
        body.addView(tv("Alamat situs dicari lewat koneksi HTTPS terenkripsi sehingga tidak bisa dibaca atau diubah penyedia internet. Penyedia tertentu juga memblokir pelacak dan situs berbahaya. Berlaku untuk halaman di Alethra saja.", 13f, cSub)
            .apply { setPadding(dp(20), dp(8), dp(20), dp(8)) })
        body.addView(sectionLabel("Penyedia"))
        addCard(body, dohKeys.indices.map { i ->
            val desc = if (dohKeys[i] == "custom" && dohKey() == "custom") (prefs.getString("dohcustom", "") ?: "") else dohDesc[i]
            listRow(iconTile(R.drawable.ic_globe), dohNames[i], desc, if (dohIdx() == i) icon(R.drawable.ic_check, 20, cAcc) else null, { chooseDoh(i) })
        })
        val n = doh?.blocked?.get() ?: 0L
        if (dohIdx() != 0 && n > 0) body.addView(tv("${fmtCount(n)} permintaan diblokir DNS sejak Alethra dibuka", 12f, cSub).apply { setPadding(dp(20), dp(10), dp(20), dp(0)) })
        body.addView(sectionLabel("Seluruh perangkat"))
        addCard(body, listOf(navRow(R.drawable.ic_globe, "DNS pribadi sistem", "Atur DNS di Android untuk semua aplikasi") { dnsDialog() }))
        body.addView(tv("Jika server DoH tidak bisa dihubungi, Alethra memakai DNS jaringan supaya browsing tetap jalan.", 12f, cSub)
            .apply { setPadding(dp(20), dp(14), dp(20), dp(8)) })
        return page
    }
}

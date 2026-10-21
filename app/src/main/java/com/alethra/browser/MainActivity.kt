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
import android.content.Context
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
import android.provider.DocumentsContract
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
        const val EXP_REQ = 12
        const val IMP_BM_REQ = 13
        const val IMP_BK_REQ = 14
        const val DLDIR_REQ = 15
        const val JS_PLAYING = "(function(){var v=document.querySelectorAll('video');for(var i=0;i<v.length;i++){if(!v[i].paused&&!v[i].ended&&v[i].readyState>2)return 1}return 0})()"
        const val JS_FILL = "(function(){var v=document.querySelectorAll('video'),t=null,i;for(i=0;i<v.length;i++){if(!v[i].paused){t=v[i];break}}if(!t)t=v[0];if(!t)return;var o=document.getElementById('__alpip');if(o)o.remove();var p=t;while(p){p.classList.add('__ala');p=p.parentElement}var s=document.createElement('style');s.id='__alpip';s.textContent='html,body{overflow:hidden!important;background:#000!important;margin:0!important;width:100%!important;height:100%!important}body *{visibility:hidden!important}.__ala{transform:none!important;contain:none!important;filter:none!important;perspective:none!important;will-change:auto!important;clip-path:none!important;backdrop-filter:none!important}video.__alv{visibility:visible!important;position:fixed!important;left:0!important;top:0!important;right:0!important;bottom:0!important;width:100vw!important;height:100vh!important;max-width:none!important;max-height:none!important;margin:0!important;z-index:2147483647!important;background:#000!important;object-fit:contain!important;object-position:center center!important}';document.head.appendChild(s);var m=document.querySelector('meta[name=viewport]');if(m){if(m.getAttribute('data-alo')===null)m.setAttribute('data-alo',m.getAttribute('content')||'')}else{m=document.createElement('meta');m.name='viewport';m.setAttribute('data-alo','');m.setAttribute('data-alnew','1');document.head.appendChild(m)}m.setAttribute('content','width=device-width,initial-scale=1,minimum-scale=1,maximum-scale=1');t.classList.add('__alv');if(!HTMLMediaElement.prototype.__alp){var op=HTMLMediaElement.prototype.pause;HTMLMediaElement.prototype.__alp=1;HTMLMediaElement.prototype.pause=function(){if(window.__alMini&&!window.__alUP&&this.classList.contains('__alv'))return;return op.apply(this,arguments)}}window.__alUP=false;window.__alMini=1;if(!t.__alg){t.__alg=1;t.addEventListener('pause',function(){if(window.__alUP||t.ended||!document.getElementById('__alpip'))return;setTimeout(function(){if(!window.__alUP&&t.paused&&document.getElementById('__alpip'))t.play().catch(function(){})},60)})}window.dispatchEvent(new Event('resize'))})()"
        const val JS_RESTORE = "(function(){window.__alUP=true;window.__alMini=0;var s=document.getElementById('__alpip');if(s)s.remove();var m=document.querySelector('meta[data-alo]');if(m){if(m.getAttribute('data-alnew'))m.remove();else{m.setAttribute('content',m.getAttribute('data-alo'));m.removeAttribute('data-alo')}}var a=document.querySelectorAll('.__ala,.__alv');for(var i=0;i<a.length;i++){a[i].classList.remove('__ala');a[i].classList.remove('__alv')}})()"
        const val JS_TOGGLE = "(function(){var v=document.querySelector('video.__alv')||document.querySelector('video');if(!v)return;if(v.paused){window.__alUP=false;v.play()}else{window.__alUP=true;v.pause()}})()"
        const val JS_VQ = "(function(){var v=document.querySelector('video.__alv')||document.querySelector('video');if(!v)return '0|1|0';var w=v.videoWidth,h=v.videoHeight,d=v.duration;return (w&&h?Math.min(w,h):0)+'|'+v.playbackRate+'|'+(isFinite(d)&&d>0?1:0)})()"
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
    private val vqScript: String by lazy { try { assets.open("vq.js").bufferedReader().use { it.readText() } } catch (e: Exception) { "" } }
    private var miniFrame: FrameLayout? = null
    private var videoPlaying = false
    private var pipActive = false
    private var fsLayer: LinearLayout? = null
    private var fsRotBtn: View? = null
    private var fsHolder: FrameLayout? = null
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
        Thread { OfflineModels.cleanupRemoved(applicationContext) }.start()   // hapus berkas model yang sudah tidak ada di daftar
        try {
            val f = android.content.IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE)
            if (Build.VERSION.SDK_INT >= 33) registerReceiver(dlDoneReceiver, f, Context.RECEIVER_EXPORTED)
            else registerReceiver(dlDoneReceiver, f)
        } catch (e: Exception) { }
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
                    dismissSuggest(); go(v.text.toString()); hideKb(); true
                } else false
            }
            setOnFocusChangeListener { _, f -> if (!f) dismissSuggest() }
            addTextChangedListener(object : TextWatcher {
                override fun afterTextChanged(s: Editable?) { if (hasFocus()) updateSuggest(s?.toString() ?: "") }
                override fun beforeTextChanged(s: CharSequence?, a: Int, b: Int, c: Int) {}
                override fun onTextChanged(s: CharSequence?, a: Int, b: Int, c: Int) {}
            })
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
        splitBox = LinearLayout(this).apply { orientation = LinearLayout.VERTICAL }
        splitBox?.addView(srl, LinearLayout.LayoutParams(MP, 0, 1f))
        host.addView(splitBox, FrameLayout.LayoutParams(MP, MP))
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
        if (splitOn) applySplitLayout()
    }

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        intentTarget(intent)?.let { pages.clear(); newTab(it) }
    }

    override fun onActivityResult(rc: Int, res: Int, data: Intent?) {
        if (rc == FILE_REQ || rc == WALL_REQ || rc == VOICE_REQ || rc == EXP_REQ || rc == IMP_BM_REQ || rc == IMP_BK_REQ || rc == DLDIR_REQ) skipNextLock = true
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
            EXP_REQ -> {
                val u = if (res == RESULT_OK) data?.data else null
                val txt = pendingExport; pendingExport = null
                if (u != null && txt != null) {
                    try { contentResolver.openOutputStream(u, "wt")?.use { it.write(txt.toByteArray(Charsets.UTF_8)) }; toast("Berkas disimpan") }
                    catch (e: Exception) { toast("Gagal menyimpan berkas") }
                }
            }
            DLDIR_REQ -> {
                val u = if (res == RESULT_OK) data?.data else null
                if (u != null) setCustomDlDir(u)
            }
            IMP_BM_REQ, IMP_BK_REQ -> {
                val u = if (res == RESULT_OK) data?.data else null
                if (u != null) {
                    val txt = try { contentResolver.openInputStream(u)?.use { String(it.readBytes().take(8_000_000).toByteArray(), Charsets.UTF_8) } } catch (e: Exception) { null }
                    if (txt == null) toast("Tidak bisa membaca berkas")
                    else if (rc == IMP_BM_REQ) importBookmarksHtml(txt) else restoreBackup(txt)
                }
            }
            else -> super.onActivityResult(rc, res, data)
        }
    }

    override fun onBackPressed() {
        when {
            lockView != null -> moveTaskToBack(true)
            suggPw != null -> { dismissSuggest(); hideKb() }
            customView != null -> hideCustom()
            pageView != null -> backPage()
            cw().canGoBack() -> cw().goBack()
            tabs.size > 1 -> closeTab(cur)
            else -> super.onBackPressed()
        }
    }

    override fun onPause() {
        super.onPause()
        dismissSuggest()
        handler.removeCallbacks(videoPoll)
        if (sttOn && !pipActive && !inMulti()) stopStt()
        if (lockMode() != "off") window.addFlags(android.view.WindowManager.LayoutParams.FLAG_SECURE)
        saveSession()
        accumulateData()
        prefs.edit().putLong("adsTotal", adCount.get()).apply()
        if (!pipActive && !inMulti()) { tabs.getOrNull(cur)?.web?.onPause(); splitTab?.web?.onPause() }
    }

    override fun onResume() {
        super.onResume()
        updateSecureFlag()
        finalizeCustomDownloads()
        if (lockView == null) { tabs.getOrNull(cur)?.web?.onResume(); splitTab?.web?.onResume() }
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
        splitPip(isInPictureInPictureMode)
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
        fun clampPos() {
            f.translationX = f.translationX.coerceIn(-f.left.toFloat(), (host.width - f.right).toFloat())
            f.translationY = f.translationY.coerceIn(-f.top.toFloat(), (host.height - f.bottom).toFloat())
        }
        fun setW(w: Int) {
            val lpf = f.layoutParams as? FrameLayout.LayoutParams ?: return
            val nw = w.coerceIn(dp(140), (host.width - dp(16)).coerceAtLeast(dp(140)))
            lpf.width = nw; lpf.height = nw * 9 / 16
            f.layoutParams = lpf
            f.post { clampPos() }
        }
        val pinch = android.view.ScaleGestureDetector(this, object : android.view.ScaleGestureDetector.SimpleOnScaleGestureListener() {
            override fun onScale(d: android.view.ScaleGestureDetector): Boolean { setW((f.width * d.scaleFactor).toInt()); return true }
            override fun onScaleEnd(d: android.view.ScaleGestureDetector) { prefs.edit().putInt("miniw", f.width).apply() }
        })
        val fb = tv("", 13f, Color.WHITE, true).apply { background = shape(Color.argb(170, 0, 0, 0), 14); setPadding(dp(10), dp(4), dp(10), dp(4)); alpha = 0f }
        fun flash(msg: String, g: Int) {
            fb.text = msg
            (fb.layoutParams as? FrameLayout.LayoutParams)?.let { it.gravity = g or Gravity.CENTER_VERTICAL; fb.layoutParams = it }
            fb.animate().cancel(); fb.alpha = 1f
            fb.animate().alpha(0f).setStartDelay(500).setDuration(300).start()
        }
        fun seek(sec: Int) {
            t.web.evaluateJavascript("(function(){var v=document.querySelector('video.__alv')||document.querySelector('video');if(!v)return;var d=v.duration;var n=v.currentTime+($sec);if(isFinite(d))n=Math.min(d,n);v.currentTime=Math.max(0,n)})()", null)
        }
        val gd = android.view.GestureDetector(this, object : android.view.GestureDetector.SimpleOnGestureListener() {
            override fun onDown(e: MotionEvent): Boolean = true
            // Ketuk tengah = putar/jeda (tepi kiri/kanan hanya bereaksi pada ketuk dua kali)
            override fun onSingleTapConfirmed(e: MotionEvent): Boolean {
                val w = ov.width.toFloat()
                if (e.x >= w / 3f && e.x <= w * 2f / 3f) t.web.evaluateJavascript(JS_TOGGLE, null)
                return true
            }
            override fun onDoubleTap(e: MotionEvent): Boolean {
                val w = ov.width.toFloat()
                if (e.x < w / 3f) { seek(-10); flash("−10 dtk", Gravity.START) }
                else if (e.x > w * 2f / 3f) { seek(10); flash("+10 dtk", Gravity.END) }
                return true
            }
        })
        ov.setOnTouchListener { _, e ->
            pinch.onTouchEvent(e)
            gd.onTouchEvent(e)
            if (pinch.isInProgress || e.pointerCount > 1) { moved = true; return@setOnTouchListener true }
            when (e.actionMasked) {
                android.view.MotionEvent.ACTION_DOWN -> { sx = e.rawX; sy = e.rawY; bx = f.translationX; by = f.translationY; moved = false }
                android.view.MotionEvent.ACTION_MOVE -> {
                    val ddx = e.rawX - sx; val ddy = e.rawY - sy
                    if (!moved && (Math.abs(ddx) > dp(6) || Math.abs(ddy) > dp(6))) moved = true
                    if (moved) { f.translationX = bx + ddx; f.translationY = by + ddy }
                }
                android.view.MotionEvent.ACTION_UP -> { if (moved) clampPos() }
            }
            true
        }
        f.addView(ov, FrameLayout.LayoutParams(MP, MP))
        f.addView(fb, FrameLayout.LayoutParams(WC, WC, Gravity.CENTER).apply { setMargins(dp(12), 0, dp(12), 0) })
        fun btn(res: Int, right: Int, action: () -> Unit): FrameLayout {
            val b = FrameLayout(this).apply { background = shape(Color.argb(170, 0, 0, 0), 14); isClickable = true }
            b.addView(icon(res, 14, Color.WHITE), FrameLayout.LayoutParams(dp(14), dp(14), Gravity.CENTER))
            b.setOnClickListener { action() }
            f.addView(b, FrameLayout.LayoutParams(dp(28), dp(28), Gravity.TOP or Gravity.END).apply { setMargins(0, dp(6), right, 0) })
            return b
        }
        val rh = FrameLayout(this).apply { background = shape(Color.argb(170, 0, 0, 0), 14); isClickable = true; contentDescription = "Ubah ukuran" }
        rh.addView(icon(R.drawable.ic_resize, 14, Color.WHITE), FrameLayout.LayoutParams(dp(14), dp(14), Gravity.CENTER))
        var rx = 0f; var rw = 0
        rh.setOnTouchListener { v, e ->
            when (e.actionMasked) {
                android.view.MotionEvent.ACTION_DOWN -> { rx = e.rawX; rw = f.width; v.parent.requestDisallowInterceptTouchEvent(true) }
                android.view.MotionEvent.ACTION_MOVE -> setW(rw - (e.rawX - rx).toInt())
                android.view.MotionEvent.ACTION_UP, android.view.MotionEvent.ACTION_CANCEL -> prefs.edit().putInt("miniw", f.width).apply()
            }
            true
        }
        f.addView(rh, FrameLayout.LayoutParams(dp(30), dp(30), Gravity.BOTTOM or Gravity.START).apply { setMargins(dp(6), 0, 0, dp(6)) })
        // Tombol ⋮ di pojok kanan atas menyembunyikan/menampilkan tombol ubah ukuran, tutup, dan layar penuh
        val hideable = mutableListOf<View>(rh)
        hideable.add(btn(R.drawable.ic_close, dp(40)) { stopMini(true) })
        hideable.add(btn(R.drawable.ic_expand, dp(74)) { val k = tabs.indexOf(t); if (k >= 0) selectTab(k) })
        var ctlShown = true
        // Tombol kualitas video (mis. 480, 360) di kiri tombol layar penuh; angkanya mengikuti kualitas video yang sedang diputar
        var qShown = false
        val qb = tv("", 11f, Color.WHITE, true).apply {
            background = shape(Color.argb(170, 0, 0, 0), 14)
            gravity = Gravity.CENTER
            setPadding(dp(8), 0, dp(8), 0)
            minWidth = dp(28)
            visibility = View.GONE
            contentDescription = "Kualitas video"
            isClickable = true
        }
        // Tombol kecepatan putar (mis. 1x, 1.5x) di kiri tombol kualitas; hanya muncul untuk video yang punya durasi (bukan live)
        var sShown = false
        var curRate = 1.0
        fun fmtRate(r: Double) = (if (r % 1.0 == 0.0) r.toInt().toString() else r.toString()) + "x"
        val sb = tv("", 11f, Color.WHITE, true).apply {
            background = shape(Color.argb(170, 0, 0, 0), 14)
            gravity = Gravity.CENTER
            setPadding(dp(8), 0, dp(8), 0)
            minWidth = dp(28)
            visibility = View.GONE
            contentDescription = "Kecepatan putar"
            isClickable = true
        }
        val qrow = LinearLayout(this).apply { orientation = LinearLayout.HORIZONTAL; gravity = Gravity.CENTER_VERTICAL }
        qrow.addView(sb, LinearLayout.LayoutParams(WC, MP).apply { rightMargin = dp(4) })
        qrow.addView(qb, LinearLayout.LayoutParams(WC, MP))
        f.addView(qrow, FrameLayout.LayoutParams(WC, dp(28), Gravity.TOP or Gravity.END).apply { setMargins(0, dp(6), dp(108), 0) })
        sb.setOnClickListener {
            val speeds = doubleArrayOf(0.25, 0.5, 0.75, 1.0, 1.25, 1.5, 1.75, 2.0)
            val labels = speeds.map { fmtRate(it) + (if (it == 1.0) " (Normal)" else "") + (if (Math.abs(it - curRate) < 0.01) "  ✓" else "") }
            dlg().setTitle("Kecepatan putar").setItems(labels.toTypedArray<CharSequence>()) { _, i ->
                val r = speeds[i].toString()
                t.web.evaluateJavascript("(function(){var v=document.querySelector('video.__alv')||document.querySelector('video');if(!v)return;var r=$r;try{var p=document.getElementById('movie_player');if(p&&p.setPlaybackRate)p.setPlaybackRate(r)}catch(e){}v.playbackRate=r})()", null)
                curRate = speeds[i]
                sb.text = fmtRate(curRate)
                toast("Kecepatan: " + fmtRate(curRate))
            }.show()
        }
        // Ketuk tombol kualitas: vq.js mencari cara ganti kualitas yang cocok dengan player situs (YouTube, JW Player, hls.js, dash.js, Shaka, Video.js, Plyr, Twitch, dll.)
        qb.setOnClickListener {
            val js = vqScript
            if (js.isEmpty()) { toast("Fitur kualitas video tidak tersedia"); return@setOnClickListener }
            t.web.evaluateJavascript("$js;window.__alVQ.start()", null)
            var tries = 0
            fun poll() {
                if (miniFrame !== f) return
                t.web.evaluateJavascript("(function(){return window.__alVQ?window.__alVQ.res():null})()") { r ->
                    val raw = try { org.json.JSONTokener(r ?: "null").nextValue() as? String } catch (e: Exception) { null }
                    if (raw == null) {
                        if (++tries < 20) handler.postDelayed({ poll() }, 250)
                        else { t.web.evaluateJavascript("window.__alVQ&&window.__alVQ.close()", null); toast("Situs ini tidak menyediakan pilihan kualitas") }
                    } else {
                        try {
                            val arr = org.json.JSONObject(raw).optJSONArray("items")
                            if (arr == null || arr.length() == 0) {
                                toast("Situs ini tidak menyediakan pilihan kualitas")
                            } else {
                                val names = ArrayList<String>()
                                val labels = ArrayList<CharSequence>()
                                for (i in 0 until arr.length()) {
                                    val o = arr.getJSONObject(i)
                                    val n = o.optString("l").replace(Regex("\\s+"), " ")
                                    names.add(n)
                                    labels.add(if (o.optBoolean("c")) "$n  ✓" else n)
                                }
                                var picked = false
                                dlg().setTitle("Kualitas video").setItems(labels.toTypedArray()) { _, i ->
                                    picked = true
                                    t.web.evaluateJavascript("window.__alVQ&&window.__alVQ.pick($i)", null)
                                    toast("Kualitas: " + names[i])
                                }.setOnCancelListener { if (!picked) t.web.evaluateJavascript("window.__alVQ&&window.__alVQ.close()", null) }.show()
                            }
                        } catch (e: Exception) { toast("Gagal membaca kualitas video") }
                    }
                }
            }
            poll()
        }
        fun updateQ() {
            qb.visibility = if (ctlShown && qShown) View.VISIBLE else View.GONE
            sb.visibility = if (ctlShown && sShown) View.VISIBLE else View.GONE
        }
        val qPoll = object : Runnable {
            override fun run() {
                if (miniFrame !== f) return
                t.web.evaluateJavascript(JS_VQ) { r ->
                    val parts = r?.trim('"')?.split("|")
                    val h = parts?.getOrNull(0)?.toIntOrNull() ?: 0
                    val rate = parts?.getOrNull(1)?.toDoubleOrNull() ?: 1.0
                    if (miniFrame === f) {
                        qShown = h > 0
                        if (qShown) qb.text = h.toString()
                        sShown = parts?.getOrNull(2) == "1"
                        if (sShown) { curRate = rate; sb.text = fmtRate(rate) }
                        updateQ()
                    }
                }
                handler.postDelayed(this, 1000)
            }
        }
        btn(R.drawable.ic_more, dp(6)) {
            ctlShown = !ctlShown
            hideable.forEach { it.visibility = if (ctlShown) View.VISIBLE else View.GONE }
            updateQ()
        }
        val mw = prefs.getInt("miniw", dp(220)).coerceIn(dp(140), (host.width - dp(16)).coerceAtLeast(dp(140)))
        host.addView(f, FrameLayout.LayoutParams(mw, mw * 9 / 16, Gravity.BOTTOM or Gravity.END).apply { setMargins(0, 0, dp(12), dp(12)) })
        miniFrame = f
        handler.post(qPoll)
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
        try { unregisterReceiver(dlDoneReceiver) } catch (e: Exception) { }
        stopStt()
        dismissSuggest()
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
        val oldSig = cBg xor cSurf xor cLine
        dark = isDark()
        if (dark) {
            cBg = Color.parseColor("#0B0B0D"); cSurf = Color.parseColor("#17171A")
            cFg = Color.parseColor("#F4F4F5"); cSub = Color.parseColor("#8E8E96"); cLine = Color.parseColor("#26262A")
        } else {
            cBg = Color.WHITE; cSurf = Color.parseColor("#F4F4F6")
            cFg = Color.parseColor("#111113"); cSub = Color.parseColor("#6B6B73"); cLine = Color.parseColor("#E8E8EC")
        }
        if (dark && bool("amoled", false)) { cBg = Color.BLACK; cSurf = Color.parseColor("#121214"); cLine = Color.parseColor("#1E1E21") }
        if (splitOn && oldSig != (cBg xor cSurf xor cLine)) handler.post { val n = splitNotes; val t = splitTab; closeSplit(); if (n) openSplit(true, null) else if (t != null && tabs.contains(t)) openSplit(false, t) }
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

    // ---- Aksi alamat dari menu titik tiga ----
    private fun urlBarShown() = top.visibility == View.VISIBLE

    private fun focusOmnibox(selectAll: Boolean) {
        omnibox.requestFocus()
        if (selectAll) omnibox.selectAll() else omnibox.setSelection(omnibox.text.length)
        (getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager).showSoftInput(omnibox, InputMethodManager.SHOW_IMPLICIT)
    }

    /* ---------- Saran di bar alamat (dari bookmark dan riwayat, tanpa jaringan) ---------- */

    private class Sugg(val res: Int, val title: String, val sub: String, val target: String)
    private var suggPw: PopupWindow? = null

    private fun dismissSuggest() { try { suggPw?.dismiss() } catch (e: Exception) {}; suggPw = null }

    private fun updateSuggest(q0: String) {
        val q = q0.trim()
        if (q.isEmpty() || !bool("sugg", true) || !omnibox.hasFocus() || !urlBarShown() || isFinishing || top.width == 0) { dismissSuggest(); return }
        val ql = q.lowercase()
        val rows = ArrayList<Sugg>()
        rows.add(Sugg(R.drawable.ic_search, q, "Cari atau buka", q))
        val seen = HashSet<String>()
        fun scan(key: String, res: Int) {
            val a = getList(key)
            for (i in 0 until a.length()) {
                if (rows.size >= 5) return
                val o = a.optJSONObject(i) ?: continue
                val u = o.optString("u"); val t = o.optString("t")
                if (!(u.startsWith("http://") || u.startsWith("https://"))) continue
                if (!t.lowercase().contains(ql) && !u.lowercase().contains(ql)) continue
                if (!seen.add(u)) continue
                rows.add(Sugg(res, t.ifEmpty { u }, displayHost(u), u))
            }
        }
        if (!(partLocked("lockbm") && !authFresh())) scan("bm", R.drawable.ic_bookmark)
        if (!incognito) scan("hist", R.drawable.ic_history)
        if (rows.size == 1 && ql.length < 2) { dismissSuggest(); return }
        val card = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            background = shape(cSurf, 16, cLine, 1)
            setPadding(0, dp(4), 0, dp(4))
        }
        rows.forEach { r ->
            val row = LinearLayout(this).apply {
                orientation = LinearLayout.HORIZONTAL; gravity = Gravity.CENTER_VERTICAL
                setPadding(dp(16), dp(10), dp(16), dp(10)); isClickable = true; background = pressBg()
            }
            row.addView(icon(r.res, 18, cSub), lp(dp(18), dp(18)).apply { rightMargin = dp(14) })
            val col = LinearLayout(this).apply { orientation = LinearLayout.VERTICAL }
            col.addView(tv(r.title, 14.5f, cFg).apply { maxLines = 1; ellipsize = TextUtils.TruncateAt.END })
            if (r.sub.isNotEmpty()) col.addView(tv(r.sub, 11.5f, cSub).apply { maxLines = 1; ellipsize = TextUtils.TruncateAt.END })
            row.addView(col, lp(0, WC, 1f))
            row.setOnClickListener { dismissSuggest(); hideKb(); if (pageView != null) closePages(); go(r.target) }
            card.addView(row, lp(MP, WC))
        }
        val holder = FrameLayout(this).apply { setPadding(dp(6), dp(4), dp(6), dp(6)); addView(card, FrameLayout.LayoutParams(MP, WC)) }
        holder.measure(View.MeasureSpec.makeMeasureSpec(top.width, View.MeasureSpec.EXACTLY), View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED))
        dismissSuggest()
        val pw = PopupWindow(holder, top.width, WC, false)
        pw.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        pw.isOutsideTouchable = false
        pw.inputMethodMode = PopupWindow.INPUT_METHOD_NOT_NEEDED
        pw.animationStyle = 0
        try {
            if (bool("barBottom", false)) {
                val loc = IntArray(2); top.getLocationOnScreen(loc)
                pw.showAtLocation(top, Gravity.NO_GRAVITY, loc[0], (loc[1] - holder.measuredHeight).coerceAtLeast(0))
            } else pw.showAsDropDown(top, 0, 0)
            suggPw = pw
        } catch (e: Exception) { suggPw = null }
    }

    private fun urlEdit() {
        if (urlBarShown()) handler.post { focusOmnibox(true) } else urlDialog()
    }

    private fun urlCopy() {
        val u = tabs.getOrNull(cur)?.url
        if (u.isNullOrEmpty() || u.startsWith(ASSET)) { toast("Tidak ada alamat untuk disalin"); return }
        (getSystemService(CLIPBOARD_SERVICE) as ClipboardManager).setPrimaryClip(ClipData.newPlainText("url", offOrig(u) ?: u))
        toast("Alamat disalin")
    }

    private fun urlPaste() {
        val cm = getSystemService(CLIPBOARD_SERVICE) as ClipboardManager
        val txt = try { cm.primaryClip?.takeIf { it.itemCount > 0 }?.getItemAt(0)?.coerceToText(this)?.toString()?.trim() } catch (e: Exception) { null }
        if (txt.isNullOrEmpty()) { toast("Clipboard kosong"); return }
        if (urlBarShown()) handler.post { omnibox.setText(txt); focusOmnibox(false) } else urlDialog(txt)
    }

    private fun urlClear() {
        if (urlBarShown()) handler.post { omnibox.setText(""); focusOmnibox(false) } else urlDialog("")
    }

    private fun urlDialog(prefill: String? = null) {
        val e = EditText(this).apply {
            isSingleLine = true; setTextColor(cFg)
            hint = "Cari atau masukkan alamat web"; setHintTextColor(cSub)
            inputType = InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_URI
            imeOptions = EditorInfo.IME_ACTION_GO
            if (prefill != null) setText(prefill) else if (!onHome() && pageView == null) setText(tabs[cur].url)
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
            0 -> { closePages(); if (!onHome()) cw().loadUrl(home()) }
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

    private var lastRenderedKey: String? = null

    private fun findScroll(v: View?): ScrollView? {
        if (v is ScrollView) return v
        if (v is ViewGroup) for (i in 0 until v.childCount) findScroll(v.getChildAt(i))?.let { return it }
        return null
    }

    private fun renderPage() {
        val key = pages.lastOrNull()
        // Render ulang halaman yang sama (mis. setelah memilih warna): pertahankan posisi scroll
        val keepY = if (key != null && key == lastRenderedKey) findScroll(pageView)?.scrollY ?: 0 else 0
        pageView?.let { container.removeView(it) }
        pageView = null
        lastRenderedKey = key
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
                "stlang" -> pageSttLang()
                "stmodels" -> pageSttModels()
                "sttr" -> pageSttTr()
                "staudio" -> pageSttAudio()
                "stvosk" -> pageVosk()
                "pipmode" -> pagePipMode()
                "imgq" -> pageImgQ()
                "dlset" -> pageDlSet()
                "dlloc" -> pageDlLoc()
                "lock" -> pageLock()
                "doh" -> pageDoh()
                "sites" -> pageSites()
                "offline" -> pageOffline()
                "pdfview" -> pagePdf()
                else -> View(this)
            }
            v.setBackgroundColor(cBg)
            v.isClickable = true
            container.addView(v, FrameLayout.LayoutParams(MP, MP))
            pageView = v
            if (keepY > 0) {
                val sv = findScroll(v)
                if (sv != null) v.viewTreeObserver.addOnPreDrawListener(object : android.view.ViewTreeObserver.OnPreDrawListener {
                    override fun onPreDraw(): Boolean {
                        v.viewTreeObserver.removeOnPreDrawListener(this)
                        sv.scrollTo(0, keepY)
                        return true
                    }
                })
            }
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
        "newtab" to "Tab baru", "incognito" to "Incognito",
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
        w.evaluateJavascript(js) { r ->
            when (r) {
                "\"ok\"" -> readerWebs.add(w)
                "\"off\"" -> readerWebs.remove(w)
                "\"no\"" -> toast("Halaman ini tidak cocok untuk mode baca")
                else -> {}
            }
        }
    }

    private val readerWebs: MutableSet<WebView> = java.util.Collections.synchronizedSet(java.util.Collections.newSetFromMap(java.util.WeakHashMap<WebView, Boolean>()))

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
 '#__alrd .b button{border:0;display:inline-flex;align-items:center;justify-content:center;min-width:38px;height:36px;padding:0 9px;white-space:nowrap;border-radius:14px;background:rgba(128,128,128,.2);color:inherit;font:600 14px sans-serif}#__alrd .b svg{display:block;pointer-events:none}'+
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
 function ic(d){return '<svg viewBox="0 0 24 24" width="20" height="20" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">'+d+'</svg>';}
 var IC={
  minus:ic('<path d="M3.5 19l5-13 5 13M5.2 14.5h6.6M17 8h4"/>'),
  plus:ic('<path d="M3.5 19l5-13 5 13M5.2 14.5h6.6M17 8h4M19 6v4"/>'),
  theme:ic('<circle cx="12" cy="12" r="9"/><path d="M12 3a9 9 0 0 1 0 18z" fill="currentColor"/>'),
  speak:ic('<path d="M11 5L6 9H2v6h4l5 4z"/><path d="M15.5 8.5a5 5 0 0 1 0 7M18.5 5.5a9 9 0 0 1 0 13"/>'),
  pause:ic('<path d="M8 5v14M16 5v14"/>'),
  play:ic('<path d="M7 4l13 8-13 8z"/>'),
  globe:ic('<circle cx="12" cy="12" r="9"/><path d="M3 12h18M12 3a14 14 0 0 1 0 18M12 3a14 14 0 0 0 0 18"/>'),
  close:ic('<path d="M18 6L6 18M6 6l12 12"/>')
 };
 function mk(icon,label,fn){var b=document.createElement('button');if(icon.charAt(0)==='<')b.innerHTML=icon;else b.textContent=icon;b.setAttribute('aria-label',label);b.title=label;b.onclick=fn;bar.appendChild(b);return b;}
 mk(IC.minus,'Perkecil teks',function(){SIZE=Math.max(14,SIZE-2);cont.style.fontSize=SIZE+'px';save();});
 mk(IC.plus,'Perbesar teks',function(){SIZE=Math.min(28,SIZE+2);cont.style.fontSize=SIZE+'px';save();});
 mk(IC.theme,'Ganti tema',function(){THEME=THEME==='light'?'sepia':(THEME==='sepia'?'dark':'light');applyTheme();save();});
 var ttsOn=false,ttsIdx=0,ttsEls=[],ttsRate=__RATE__,ttsHi=null,tb=null,rb=null;
 var TS='h1,h2,h3,h4,p,li,blockquote,figcaption';
 function tbSet(k){if(!tb)return;tb.innerHTML=IC[k];var l={speak:'Baca artikel',pause:'Jeda',play:'Lanjutkan'};tb.setAttribute('aria-label',l[k]);tb.title=l[k];}
 function ttsClear(){if(ttsHi){ttsHi.style.background='';ttsHi=null;}}
 window.__alTts=function(i){ttsIdx=i;ttsClear();var e=ttsEls[i];if(e){ttsHi=e;e.style.background='rgba(255,193,7,.3)';try{e.scrollIntoView({block:'center',behavior:'smooth'});}catch(x){e.scrollIntoView();}}};
 window.__alTtsEnd=function(){ttsOn=false;ttsIdx=0;ttsClear();tbSet('speak');};
 function ttsPlay(){
  if(!ttsEls.length){ttsEls=[].slice.call(cont.querySelectorAll(TS)).filter(function(e){return !e.querySelector(TS)&&txt(e).length>1;});}
  if(!ttsEls.length)return;
  var arr=ttsEls.map(function(e){return txt(e);});
  ttsOn=true;tbSet('pause');
  try{AlApp.ttsStart(JSON.stringify(arr),ttsIdx,document.documentElement.lang||'',ttsRate);}catch(e){ttsOn=false;tbSet('speak');}
 }
 function ttsPause(){ttsOn=false;tbSet('play');try{AlApp.ttsStop();}catch(e){}}
 tb=mk(IC.speak,'Baca artikel',function(){if(ttsOn)ttsPause();else ttsPlay();});
 rb=mk(ttsRate+'x','Kecepatan baca',function(){
  var rs=[1,1.25,1.5,0.75],k=rs.indexOf(ttsRate);ttsRate=rs[(k+1)%rs.length];
  rb.textContent=ttsRate+'x';try{AlApp.ttsRate(ttsRate);}catch(e){}
  if(ttsOn)ttsPlay();
 });
 mk(IC.globe,'Bahasa suara',function(){try{AlApp.ttsPick();}catch(e){}});
 window.__alTtsLang=function(){if(ttsOn)ttsPlay();};
 mk(IC.close,'Tutup mode baca',close);
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
        fun blobData(id: Int, data: String?) { runOnUiThread { blobWait.remove(id)?.invoke(data) } }

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
            if (!readerWebs.contains(web)) return
            if (json == null || json.length > 600000) return
            runOnUiThread { ttsSpeak(web, json, start, lang ?: "", rate) }
        }

        @JavascriptInterface
        fun ttsPick() { if (readerWebs.contains(web)) runOnUiThread { ttsLangDialog(web) } }

        @JavascriptInterface
        fun ttsStop() { runOnUiThread { try { tts?.stop() } catch (e: Exception) {} } }

        @JavascriptInterface
        fun ttsRate(rate: Float) { if (!readerWebs.contains(web)) return; prefs.edit().putFloat("ttsrate", rate.coerceIn(0.5f, 2f)).apply() }

        @JavascriptInterface
        fun readerPref(size: Int, theme: String?) {
            if (!readerWebs.contains(web)) return
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
        sttLogClear()
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
            Intent.ACTION_VIEW -> i.dataString?.takeIf { !it.startsWith("file:", true) && !it.startsWith("content:", true) }
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

    /** Popup "Simpan halaman": dua pilihan dalam satu baris (Cetak / PDF dan Simpan offline). */
    private fun showSaveDialog() {
        val d = dlg()
        fun tile(res: Int, title: String, sub: String, onClick: () -> Unit): LinearLayout = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            gravity = Gravity.CENTER_HORIZONTAL
            background = shape(cBg, 16, cLine, 1)
            setPadding(dp(8), dp(18), dp(8), dp(16))
            isClickable = true
            addView(FrameLayout(context).apply {
                background = shape(cLine, 14)
                addView(icon(res, 24, cFg), FrameLayout.LayoutParams(dp(24), dp(24), Gravity.CENTER))
            }, lp(dp(48), dp(48)))
            addView(tv(title, 14f, cFg, true).apply { gravity = Gravity.CENTER; maxLines = 2 }, lp(MP, WC).apply { topMargin = dp(10) })
            addView(tv(sub, 11.5f, cSub).apply { gravity = Gravity.CENTER; maxLines = 2 }, lp(MP, WC).apply { topMargin = dp(2) })
            setOnClickListener { d.dismiss(); onClick() }
        }
        val row = LinearLayout(this).apply {
            orientation = LinearLayout.HORIZONTAL
            setPadding(dp(16), 0, dp(16), dp(8))
            addView(tile(R.drawable.ic_print, "Cetak / PDF", "Simpan jadi berkas PDF") { printPage() },
                lp(0, WC, 1f).apply { rightMargin = dp(5) })
            addView(tile(R.drawable.ic_offline, "Simpan offline", "Baca tanpa internet") { saveOffline() },
                lp(0, WC, 1f).apply { leftMargin = dp(5) })
        }
        d.setTitle("Simpan halaman").setView(row).setNegativeButton("Batal", null).show()
    }

    private fun showPageMenu(anchor: View) {
        val onPage = pageView == null && !onHome()
        val pageUrl = tabs.getOrNull(cur)?.url
        var pw: PopupWindow? = null
        val done = { pw?.dismiss(); Unit }

        // Di mode full: tombol navigasi yang sedang dibuka disembunyikan (sama seperti yang disorot di bar bawah)
        val navSel = navSelected()
        val navGroup = if (fullOn()) listOf(
            MI(R.drawable.ic_home, "Beranda") { navTap(0) },
            MI(R.drawable.ic_tab, "Tab") { navTap(1) },
            MI(R.drawable.ic_bookmark, "Bookmark") { navTap(2) },
            MI(R.drawable.ic_download, "Unduhan") { navTap(3) },
            MI(R.drawable.ic_menu, "Menu") { navTap(4) }
        ).filterIndexed { i, _ -> i != navSel } else emptyList()

        val pageGroup = if (onPage) listOf(
            MI(R.drawable.ic_back, "Mundur") { if (cw().canGoBack()) cw().goBack() else toast("Tidak ada halaman sebelumnya") },
            MI(R.drawable.ic_forward, "Maju") { if (cw().canGoForward()) cw().goForward() else toast("Tidak ada halaman berikutnya") },
            MI(R.drawable.ic_rotate, "Muat ulang") { cw().reload() },
            MI(R.drawable.ic_bookmark, "Tambah bookmark") { addBookmark() },
            MI(R.drawable.ic_search, "Cari di halaman") { findDialog() },
            MI(R.drawable.ic_share, "Bagikan") { share(offOrig(tabs[cur].url) ?: tabs[cur].url) },
            MI(R.drawable.ic_save_page, "Simpan halaman") { showSaveDialog() },
            MI(R.drawable.ic_reader, "Mode baca") { readerMode() }) else emptyList()

        val urlGroup = if (onPage || onHome()) listOf(
            MI(R.drawable.ic_edit, "Edit") { urlEdit() },
            MI(R.drawable.ic_copy, "Salin") { urlCopy() },
            MI(R.drawable.ic_paste, "Tempel") { urlPaste() },
            MI(R.drawable.ic_eraser, "Hapus") { urlClear() }) else emptyList()

        val opts = mutableListOf<MI>()
        opts.add(MI(R.drawable.ic_expand, "Mode full", fullOn()) { setFull(!fullOn()) })
        if (onPage) {
            opts.add(MI(R.drawable.ic_desktop, "Mode desktop", bool("desktop", false)) { toggleSiteDesktop() })
            if (bool("ptr", true)) opts.add(MI(R.drawable.ic_translate, "Terjemahkan situs", tabs.getOrNull(cur)?.trOn == true) { togglePageTr() })
            if (bool("stt", false)) opts.add(MI(R.drawable.ic_mic, "Terjemahan Suara", sttOn) { toggleStt() })
            if (bool("split", true)) opts.add(MI(R.drawable.ic_split, "Tampilan terpisah", splitOn) { if (splitOn) closeSplit() else splitChoose() })
            opts.add(MI(R.drawable.ic_shield, "Blokir iklan", siteAdsOn(hostOf(pageUrl)) && hostOf(pageUrl) != null) { toggleSiteAds() })
            if (bool("pip", true)) opts.add(MI(R.drawable.ic_pip, "Perkecil video") {
                if (pipMode() == "in") { if (videoPlaying) startMini() else toast("Tidak ada video yang sedang diputar") }
                else if (!videoPlaying && customView == null) toast("Tidak ada video yang sedang diputar")
                else enterPip()
            })
            if (bool("zoomui", true)) opts.add(MI(R.drawable.ic_zoom, "Zoom situs") { zoomDialog() })
            if (Build.VERSION.SDK_INT >= 26) opts.add(MI(R.drawable.ic_shortcut, "Tambah ke layar utama") { pinShortcut() })
        }

        val homeGroups = if (onHome()) listOf(listOf(
            MI(R.drawable.ic_back, "Mundur") { if (cw().canGoBack()) cw().goBack() else toast("Tidak ada halaman sebelumnya") },
            MI(R.drawable.ic_forward, "Maju") { if (cw().canGoForward()) cw().goForward() else toast("Tidak ada halaman berikutnya") }),
            urlGroup,
            listOf(
            MI(R.drawable.ic_expand, "Mode full", fullOn()) { setFull(!fullOn()) },
            MI(R.drawable.ic_search, "Cari di halaman") { findDialog() },
            MI(R.drawable.ic_desktop, "Mode desktop", bool("desktop", false)) { toggleSiteDesktop() },
            MI(R.drawable.ic_shield, "Blokir iklan", bool("sadblock", true)) {
                setBool("sadblock", !bool("sadblock", true))
                tabs.forEach { applySiteAds(it) }
                toast(if (bool("sadblock", true)) "Blokir iklan aktif" else "Blokir iklan dimatikan")
            }),
            navGroup) else null
        val panel = iconPanel(homeGroups ?: listOf(pageGroup, urlGroup, opts, navGroup), 4, 48, false, false, done)
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
        // Unduhan kustom yang sudah disalin: baris DownloadManager bisa hilang setelah berkas sementara dihapus
        val have = out.map { it.id }.toSet()
        for (i in 0 until a.length()) {
            val o = a.getJSONObject(i)
            if (o.optLong("id") !in have && o.optString("doc").isNotEmpty())
                out.add(Dl(o.getLong("id"), o.optString("t", "Berkas"), DownloadManager.STATUS_SUCCESSFUL, o.optLong("size"), o.optLong("size"), null, 0, null))
        }
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
                val ent = dlEntry(d.id)
                val copyState = if (ent != null && ent.optString("tree").isNotEmpty() && ent.optString("doc").isEmpty()
                    && d.status == DownloadManager.STATUS_SUCCESSFUL) { if (ent.optBoolean("err")) 2 else 1 } else 0
                val status = when {
                    copyState == 1 -> "Menyalin ke folder pilihan…"
                    copyState == 2 -> "Gagal menyalin ke folder pilihan"
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
            finalizeCustomDownloads()
            if (all.any { isRunning(it.status) || dlEntry(it.id)?.let { e -> e.optString("tree").isNotEmpty() && e.optString("doc").isEmpty() && !e.optBoolean("err") } == true }) {
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
            val doc = dlEntry(d.id)?.optString("doc").orEmpty()
            val ent = dlEntry(d.id)
            if (ent != null && ent.optString("tree").isNotEmpty() && doc.isEmpty()) {
                if (ent.optBoolean("err")) { dlUpdate(d.id) { it.remove("err") }; finalizeCustomDownloads(); toast("Mencoba menyalin ulang…") }
                else toast("Berkas sedang disalin ke folder pilihan")
                return
            }
            val uri = if (doc.isNotEmpty()) Uri.parse(doc)
                else (getSystemService(DOWNLOAD_SERVICE) as DownloadManager).getUriForDownloadedFile(d.id)
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
    /** Hanya arsip bernama <angka>.mhtml di folder offline/ milik aplikasi. */
    private fun offFileOk(path: String): Boolean = try {
        val f = java.io.File(path).canonicalFile
        f.parentFile == offDir().canonicalFile && Regex("^\\d+\\.mhtml$").matches(f.name)
    } catch (e: Exception) { false }

    private fun isOffUrl(u: String): Boolean =
        u.startsWith("file:", true) && try { offFileOk(Uri.parse(u).path ?: "") } catch (e: Exception) { false }

    /**
     * Berkas lokal yang boleh dibuka WebView: aset bawaan aplikasi (beranda) dan arsip di folder offline/.
     * Semua file: lain dan semua content: ditolak. Alamat web biasa selalu lolos.
     */
    private fun localUrlOk(u: String): Boolean {
        if (u.startsWith("content:", true)) return false
        if (!u.startsWith("file:", true)) return true
        return try {
            val uri = Uri.parse(u)
            val h = uri.host
            if (!h.isNullOrEmpty() && h != "localhost") return false
            val p = uri.path ?: return false
            if (p.split('/').any { it == ".." || it == "." }) return false
            p == "/android_asset" || p.startsWith("/android_asset/") || offFileOk(p)
        } catch (e: Exception) { false }
    }

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

    /** Bagikan arsip offline (MHTML atau PDF) lewat lembar bagikan Android, dengan nama berkas sesuai judul halaman. */
    private fun shareOfflineFile(id: Long, ext: String, title: String) {
        val f = offFile(id, ext)
        if (!f.exists()) { toast("Berkas tidak ditemukan"); return }
        try {
            val dir = java.io.File(cacheDir, "share").apply { mkdirs() }
            dir.listFiles()?.forEach { try { it.delete() } catch (e: Exception) {} }
            val base = title.replace(Regex("[\\\\/:*?\"<>|]"), "_").trim().take(60).ifEmpty { "Halaman" }
            val out = java.io.File(dir, "$base.$ext")
            f.copyTo(out, overwrite = true)
            val uri = androidx.core.content.FileProvider.getUriForFile(this, "$packageName.files", out)
            val i = Intent(Intent.ACTION_SEND).setType(if (ext == "pdf") "application/pdf" else "multipart/related")
                .putExtra(Intent.EXTRA_STREAM, uri).putExtra(Intent.EXTRA_SUBJECT, title)
                .addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            skipNextLock = true
            startActivity(Intent.createChooser(i, "Bagikan"))
        } catch (e: Exception) { toast("Tidak bisa membagikan berkas ini") }
    }

    private fun shortcutBitmap(label: String, fav: Bitmap?): Bitmap {
        val sz = 192
        val bmp = Bitmap.createBitmap(sz, sz, Bitmap.Config.ARGB_8888)
        val c = android.graphics.Canvas(bmp)
        val p = android.graphics.Paint(android.graphics.Paint.ANTI_ALIAS_FLAG)
        p.color = if (fav != null) Color.WHITE else cAcc
        c.drawRoundRect(0f, 0f, sz.toFloat(), sz.toFloat(), 40f, 40f, p)
        if (fav != null) {
            val s = 112
            c.drawBitmap(Bitmap.createScaledBitmap(fav, s, s, true), (sz - s) / 2f, (sz - s) / 2f, p)
        } else {
            p.color = cOnAcc; p.textSize = 104f; p.textAlign = android.graphics.Paint.Align.CENTER; p.typeface = Typeface.DEFAULT_BOLD
            val ch = (label.firstOrNull { it.isLetterOrDigit() } ?: 'A').uppercaseChar().toString()
            c.drawText(ch, sz / 2f, sz / 2f - (p.descent() + p.ascent()) / 2f, p)
        }
        return bmp
    }

    /** Pasang pintasan halaman yang sedang dibuka ke layar utama (Android 8+). */
    private fun pinShortcut() {
        val t = tabs.getOrNull(cur) ?: return
        val u = offOrig(t.url) ?: t.url
        if (incognito || t.incog) { toast("Tidak tersedia di mode Incognito"); return }
        if (!(u.startsWith("http://") || u.startsWith("https://"))) { toast("Halaman ini tidak bisa dipasang ke layar utama"); return }
        if (Build.VERSION.SDK_INT < 26) { toast("Perlu Android 8 atau lebih baru"); return }
        try {
            val sm = getSystemService(android.content.pm.ShortcutManager::class.java)
            if (sm == null || !sm.isRequestPinShortcutSupported) { toast("Launcher tidak mendukung pintasan"); return }
            val label = t.title.ifEmpty { displayHost(u) }.take(30)
            val intent = Intent(this, MainActivity::class.java).setAction(Intent.ACTION_VIEW).setData(Uri.parse(u))
                .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP)
            val info = android.content.pm.ShortcutInfo.Builder(this, "pin_" + u.hashCode())
                .setShortLabel(label).setLongLabel(label)
                .setIcon(android.graphics.drawable.Icon.createWithBitmap(shortcutBitmap(label, t.icon)))
                .setIntent(intent).build()
            sm.requestPinShortcut(info, null)
        } catch (e: Exception) { toast("Gagal membuat pintasan") }
    }

    private fun offlineDialog(o: JSONObject, after: () -> Unit) {
        val id = o.optLong("id"); val t = o.optString("t"); val u = o.optString("u")
        val hasPdf = offFile(id, "pdf").exists()
        val labels = mutableListOf("Buka halaman")
        if (hasPdf) labels.add("Buka PDF")
        labels.add("Bagikan halaman")
        if (hasPdf) labels.add("Bagikan PDF")
        labels.add("Buka versi online"); labels.add("Salin alamat"); labels.add("Hapus")
        dlg().setTitle(t.ifEmpty { u }.take(60)).setItems(labels.toTypedArray()) { _, i ->
            when (labels[i]) {
                "Buka halaman" -> openOffline(id)
                "Buka PDF" -> openOfflinePdf(id, t.ifEmpty { u })
                "Bagikan halaman" -> shareOfflineFile(id, "mhtml", t.ifEmpty { displayHost(u) })
                "Bagikan PDF" -> shareOfflineFile(id, "pdf", t.ifEmpty { displayHost(u) })
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
                "Belum ada halaman offline.\nBuka sebuah halaman, lalu pilih ⋮ > Simpan halaman > Simpan offline." else "Tidak ada hasil"))
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

    private var bmFolder = ""
    private var pendingExport: String? = null

    private fun pageBookmarks(): View {
        val more = actBtn(R.drawable.ic_more, "Impor / ekspor") { v ->
            popup(v, listOf("Ekspor bookmark (HTML)", "Impor bookmark (HTML)")) { i ->
                if (i == 0) exportBookmarks() else pickImport(IMP_BM_REQ, "text/html")
            }
        }
        val (page, body) = shell("Bookmark", true, listOf(more))
        val all = getList("bm")
        val weekAgo = System.currentTimeMillis() - 7L * 86400000L
        var recent = 0
        val folders = LinkedHashMap<String, Int>()
        for (i in 0 until all.length()) {
            val o = all.getJSONObject(i)
            if (o.optLong("ts", 0L) > weekAgo) recent++
            val f = o.optString("f")
            if (f.isNotEmpty()) folders[f] = (folders[f] ?: 0) + 1
        }
        if (bmFilter == 2 && !folders.containsKey(bmFolder)) bmFilter = 0
        val list = LinearLayout(this).apply { orientation = LinearLayout.VERTICAL }
        fun fill() {
            list.removeAllViews()
            val q = bmQuery.trim().lowercase()
            var n = 0
            for (i in 0 until all.length()) {
                val o = all.getJSONObject(i)
                val t = o.optString("t"); val u = o.optString("u")
                if (bmFilter == 1 && o.optLong("ts", 0L) <= weekAgo) continue
                if (bmFilter == 2 && o.optString("f") != bmFolder) continue
                if (q.isNotEmpty() && !t.lowercase().contains(q) && !u.lowercase().contains(q)) continue
                val f = o.optString("f")
                list.addView(listRow(favTile(t, u, null, cSurf), t.ifEmpty { u }, if (f.isNotEmpty() && bmFilter != 2) "$f · $u" else u, null,
                    { closePages(); go(u) },
                    { bookmarkDialog(o) { renderPage() } }), lp(MP, WC))
                n++
            }
            if (n == 0) list.addView(emptyState("Belum ada bookmark"))
        }
        body.addView(searchField("Cari bookmark…", bmQuery) { bmQuery = it; fill() },
            lp(MP, WC).apply { setMargins(dp(16), dp(4), dp(16), dp(8)) })
        fun countTv(n: Int) = tv(n.toString(), 13f, cSub)
        val rows = mutableListOf<View>(
            listRow(iconTile(R.drawable.ic_bookmark, bmFilter == 0), "Semua Bookmark", "", countTv(all.length()), { bmFilter = 0; renderPage() }),
            listRow(iconTile(R.drawable.ic_history, bmFilter == 1), "Baru ditambahkan", "7 hari terakhir", countTv(recent), { bmFilter = 1; renderPage() }))
        folders.forEach { (name, cnt) ->
            rows.add(listRow(iconTile(R.drawable.ic_folder, bmFilter == 2 && bmFolder == name), name, "Folder", countTv(cnt), { bmFilter = 2; bmFolder = name; renderPage() }))
        }
        addCard(body, rows)
        body.addView(list, lp(MP, WC).apply { topMargin = dp(8) })
        fill()
        return page
    }

    private fun bookmarkDialog(o: JSONObject, after: () -> Unit) {
        val t = o.optString("t"); val u = o.optString("u")
        dlg().setTitle(t.ifEmpty { u }.take(60)).setItems(arrayOf("Buka di tab baru", "Ubah", "Salin alamat", "Hapus")) { _, i ->
            when (i) {
                0 -> { pages.clear(); newTab(u) }
                1 -> editBookmark(o, after)
                2 -> {
                    (getSystemService(CLIPBOARD_SERVICE) as ClipboardManager).setPrimaryClip(ClipData.newPlainText("url", u))
                    toast("Alamat disalin")
                }
                else -> { removeEntry("bm", u); after() }
            }
        }.show()
    }

    private fun editBookmark(o: JSONObject, after: () -> Unit) {
        val oldU = o.optString("u")
        fun field(hint: String, text: String) = EditText(this).apply {
            setText(text); this.hint = hint; isSingleLine = true
            setTextColor(cFg); setHintTextColor(cSub)
        }
        val et = field("Judul", o.optString("t"))
        val eu = field("Alamat", oldU)
        val ef = field("Folder (kosongkan jika tidak ada)", o.optString("f"))
        val box = LinearLayout(this).apply { orientation = LinearLayout.VERTICAL; setPadding(dp(20), dp(8), dp(20), 0) }
        listOf(et, eu, ef).forEach { box.addView(it, lp(MP, WC)) }
        dlg().setTitle("Ubah bookmark").setView(box)
            .setPositiveButton("Simpan") { _, _ ->
                val nu = eu.text.toString().trim()
                val nt = et.text.toString().trim().ifEmpty { nu }
                val nf = ef.text.toString().trim().take(60)
                val a = getList("bm")
                var dup = false
                if (nu != oldU) for (i in 0 until a.length()) if (a.getJSONObject(i).optString("u") == nu) dup = true
                if (nu.isEmpty()) toast("Alamat tidak boleh kosong")
                else if (dup) toast("Alamat itu sudah ada di bookmark")
                else {
                    for (i in 0 until a.length()) {
                        val x = a.getJSONObject(i)
                        if (x.optString("u") == oldU) {
                            x.put("t", nt).put("u", nu)
                            if (nf.isEmpty()) x.remove("f") else x.put("f", nf)
                            break
                        }
                    }
                    prefs.edit().putString("bm", a.toString()).apply()
                    after()
                }
            }
            .setNegativeButton("Batal", null).show()
    }

    private fun pickImport(req: Int, mime: String) {
        val i = Intent(Intent.ACTION_OPEN_DOCUMENT).addCategory(Intent.CATEGORY_OPENABLE).setType("*/*")
            .putExtra(Intent.EXTRA_MIME_TYPES, arrayOf(mime, "text/plain", "application/octet-stream"))
        try { skipNextLock = true; startActivityForResult(i, req) } catch (e: Exception) { toast("Pemilih berkas tidak tersedia") }
    }

    private fun saveText(name: String, mime: String, text: String) {
        pendingExport = text
        val i = Intent(Intent.ACTION_CREATE_DOCUMENT).addCategory(Intent.CATEGORY_OPENABLE).setType(mime).putExtra(Intent.EXTRA_TITLE, name)
        try { skipNextLock = true; startActivityForResult(i, EXP_REQ) } catch (e: Exception) { pendingExport = null; toast("Pemilih berkas tidak tersedia") }
    }

    private fun exportBookmarks() {
        val a = getList("bm")
        if (a.length() == 0) { toast("Belum ada bookmark untuk diekspor"); return }
        fun link(o: JSONObject, ind: String) = ind + "<DT><A HREF=\"" + TextUtils.htmlEncode(o.optString("u")) + "\" ADD_DATE=\"" +
            (o.optLong("ts", 0L) / 1000) + "\">" + TextUtils.htmlEncode(o.optString("t").ifEmpty { o.optString("u") }) + "</A>\n"
        val sb = StringBuilder("<!DOCTYPE NETSCAPE-Bookmark-file-1>\n<META HTTP-EQUIV=\"Content-Type\" CONTENT=\"text/html; charset=UTF-8\">\n<TITLE>Bookmarks</TITLE>\n<H1>Bookmarks</H1>\n<DL><p>\n")
        val groups = LinkedHashMap<String, MutableList<JSONObject>>()
        for (i in 0 until a.length()) {
            val o = a.getJSONObject(i)
            if (o.optString("f").isEmpty()) sb.append(link(o, "    ")) else groups.getOrPut(o.optString("f")) { mutableListOf() }.add(o)
        }
        groups.forEach { (f, items) ->
            sb.append("    <DT><H3>").append(TextUtils.htmlEncode(f)).append("</H3>\n    <DL><p>\n")
            items.forEach { sb.append(link(it, "        ")) }
            sb.append("    </DL><p>\n")
        }
        sb.append("</DL><p>\n")
        saveText("alethra-bookmark.html", "text/html", sb.toString())
    }

    private fun importBookmarksHtml(src: String) {
        val re = Regex("<DL[^>]*>|</DL>|<H3[^>]*>(.*?)</H3>|<A\\s[^>]*?HREF\\s*=\\s*\"([^\"]*)\"[^>]*>(.*?)</A>", setOf(RegexOption.IGNORE_CASE, RegexOption.DOT_MATCHES_ALL))
        fun dec(x: String) = android.text.Html.fromHtml(x, android.text.Html.FROM_HTML_MODE_LEGACY).toString().trim()
        val stack = ArrayList<String>()
        var pend: String? = null
        val found = ArrayList<Triple<String, String, String>>()
        for (m in re.findAll(src)) {
            val g = m.value
            when {
                g.startsWith("</", true) -> if (stack.isNotEmpty()) stack.removeAt(stack.size - 1)
                g.startsWith("<DL", true) -> { stack.add(pend ?: ""); pend = null }
                g.startsWith("<H3", true) -> pend = dec(m.groupValues[1])
                else -> {
                    val u = dec(m.groupValues[2])
                    if (u.startsWith("http://") || u.startsWith("https://"))
                        found.add(Triple(dec(m.groupValues[3]), u, stack.filter { it.isNotEmpty() }.joinToString(" / ")))
                }
            }
        }
        if (found.isEmpty()) { toast("Tidak ada bookmark di berkas ini"); return }
        val old = getList("bm")
        val have = HashSet<String>()
        for (i in 0 until old.length()) have.add(old.getJSONObject(i).optString("u"))
        val n = JSONArray()
        var added = 0
        val now = System.currentTimeMillis()
        for ((t, u, f) in found) {
            if (!have.add(u)) continue
            val o = JSONObject().put("t", t.ifEmpty { u }).put("u", u).put("ts", now)
            if (f.isNotEmpty()) o.put("f", f.take(60))
            n.put(o); added++
        }
        for (i in 0 until old.length()) n.put(old.getJSONObject(i))
        prefs.edit().putString("bm", n.toString()).apply()
        toast("$added bookmark diimpor" + if (found.size - added > 0) ", ${found.size - added} sudah ada" else "")
        if (pages.lastOrNull() == "bookmarks") renderPage()
    }

    /* ---------- Cadangan pengaturan ---------- */

    private val backupDeny = setOf("pw", "lock", "lockgrace", "lockbm", "lockinc", "lockpw", "session", "sessionCur", "dl", "hist",
        "offline", "permok", "siteprefs", "wallv", "wallpending", "wallfix", "dataTotal", "adsTotal", "clearpend", "doh", "dohcustom", "dohset")

    private fun exportBackup() {
        val o = JSONObject()
        loop@ for ((k, v) in prefs.all) {
            if (k in backupDeny) continue@loop
            val e = JSONObject()
            when (v) {
                is String -> e.put("t", "s").put("v", v)
                is Boolean -> e.put("t", "b").put("v", v)
                is Int -> e.put("t", "i").put("v", v)
                is Long -> e.put("t", "l").put("v", v)
                is Float -> e.put("t", "f").put("v", v.toDouble())
                is Set<*> -> e.put("t", "set").put("v", JSONArray(v.map { it.toString() }))
                else -> continue@loop
            }
            o.put(k, e)
        }
        val root = JSONObject().put("app", "alethra").put("v", 1).put("prefs", o)
        saveText("alethra-cadangan.json", "application/json", root.toString(2))
    }

    private fun restoreBackup(text: String) {
        val p: JSONObject = try {
            val root = JSONObject(text)
            if (root.optString("app") != "alethra") { toast("Berkas bukan cadangan Alethra"); return }
            root.getJSONObject("prefs")
        } catch (e: Exception) { toast("Berkas cadangan tidak valid"); return }
        dlg().setMessage("Pulihkan " + p.length() + " pengaturan dari cadangan? Pengaturan dan bookmark saat ini akan ditimpa.")
            .setPositiveButton("Pulihkan") { _, _ ->
                try {
                    val ed = prefs.edit()
                    val keys = p.keys()
                    while (keys.hasNext()) {
                        val k = keys.next()
                        if (k in backupDeny) continue
                        val e = p.getJSONObject(k)
                        when (e.getString("t")) {
                            "s" -> ed.putString(k, e.getString("v"))
                            "b" -> ed.putBoolean(k, e.getBoolean("v"))
                            "i" -> ed.putInt(k, e.getInt("v"))
                            "l" -> ed.putLong(k, e.getLong("v"))
                            "f" -> ed.putFloat(k, e.getDouble("v").toFloat())
                            "set" -> {
                                val a = e.getJSONArray("v")
                                val set = HashSet<String>()
                                for (j in 0 until a.length()) set.add(a.getString(j))
                                ed.putStringSet(k, set)
                            }
                        }
                    }
                    ed.apply()
                    siteCache = null; applyChrome(); applyAllSites(true); reloadHomes(); injectTrAll()
                    renderPage()
                    toast("Cadangan dipulihkan")
                } catch (e: Exception) { toast("Gagal memulihkan cadangan") }
            }.setNegativeButton("Batal", null).show()
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
            navRow(R.drawable.ic_file, "Ekspor pengaturan & bookmark", "Simpan cadangan ke berkas (tanpa sandi dan kunci)", false) { exportBackup() },
            navRow(R.drawable.ic_download, "Pulihkan dari cadangan", "Muat pengaturan dan bookmark dari berkas", false) { pickImport(IMP_BK_REQ, "application/json") }))
        addCard(body, listOf(
            navRow(R.drawable.ic_globe, "Browser default", if (isDefaultBrowser()) "Alethra adalah browser default" else "Buka link dari aplikasi lain di Alethra") { requestDefaultBrowser() }))
        addCard(body, listOf(
            navRow(R.drawable.ic_reset, "Reset pengaturan", "Kembalikan semua ke bawaan", false) {
                dlg().setMessage("Kembalikan semua pengaturan ke bawaan?")
                    .setPositiveButton("Reset") { _, _ ->
                        val ed = prefs.edit()
                        listOf("theme", "barBottom", "dark", "desktop", "zoom", "images", "imgq", "js", "pinch", "popups", "tpc", "https", "engine", "pip", "pipmode", "adblock", "sadblock", "sadoff", "dnshint", "ptr", "stt", "stlang", "stspeed", "stfsize", "stfpos", "stfbg", "stfcolor", "stforig", "stfbgc", "stfbga", "stbsize", "stbcolor", "stbbgc", "stbbga", "stdisp", "stengine", "sttr", "sta_pg", "sta_hp", "sta_lp", "sta_min", "sta_max", "sta_sens", "sta_dur", "sta_ns", "sta_gate", "sta_auto", "sta_minlen", "sta_filt", "sta_meter", "sta_legacy", "stmode", "stoffeng", "stasr", "stfallback", "stlog", "split", "splitflip", "zoomui", "dlloc", "dlask", "dlwifi", "dlroam", "dlprog", "fsland", "tr", "trlang", "trhide", "full", "pull", "restore", "permok", "pwsave", "pwfill", "incogsecure", "menuhide", "clock", "clock24", "clockpos", "clockalign", "clocksize", "clockcolor", "clockdate", "clocksec", "clockcity", "clocknum", "clockbg", "safeb", "clearexit", "rdsize", "rdtheme", "accent", "amoled", "round", "uifont", "homelogo", "homebg", "bartint", "wallv", "walldim", "doh", "dohcustom", "siteprefs", "sugg")
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
            toggleRow("Blokir cookie pihak ketiga", "Cegah situs lain menyimpan cookie saat kamu membuka sebuah halaman. Di Incognito selalu diblokir. Sebagian login atau pembayaran tertanam bisa gagal.", !bool("tpc", true)) { c -> setBool("tpc", !c); applyAllSites(false) },
            toggleRow("Blokir iklan situs", "Blokir iklan dan pelacak iklan di semua situs", bool("sadblock", true)) { c ->
                setBool("sadblock", c); tabs.forEach { applySiteAds(it) }; cw().reload()
            },
            toggleRow("Blokir pop-up berbahaya", "Lindungi dari pop-up berbahaya", bool("popups", false)) { c -> setBool("popups", c); applyAllSites(false) },
            toggleRow("Gunakan HTTPS", "Alihkan alamat http:// ke https:// (kecuali localhost dan jaringan lokal)", bool("https", false)) { c -> setBool("https", c) },
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
            navRow(R.drawable.ic_globe, "Kelola izin situs", sitesWithPerms().size.toString() + " situs") { openPage("sites") },
            navRow(R.drawable.ic_trash, "Hapus semua izin situs", "Lupakan izin kamera, mikrofon, lokasi, dan notifikasi yang pernah disetujui", false) { resetSitePerms() }))
        body.addView(sectionLabel("Akses situs"))
        addCard(body, listOf(
            navRow(R.drawable.ic_shield, "DNS aman (DoH)", dohName()) { openPage("doh") },
            navRow(R.drawable.ic_globe, "DNS pribadi sistem", "Atur DNS Android untuk semua aplikasi") { dnsDialog() },
            toggleRow("Saran saat situs diblokir", "Tampilkan bantuan jika muncul halaman Internet Positif", bool("dnshint", true)) { c -> setBool("dnshint", c) }))
        body.addView(sectionLabel("Data & Riwayat"))
        addCard(body, listOf(
            toggleRow("Saran saat mengetik", "Tampilkan saran dari bookmark dan riwayat di bar alamat", bool("sugg", true)) { c -> setBool("sugg", c); if (!c) dismissSuggest() },
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
        body.addView(sectionLabel("Terjemahan Suara"))
        addCard(body, listOf<View?>(
            toggleRow("Terjemahan Suara", "Tampilkan Terjemahan Suara di menu titik tiga: audio video dikenali lalu diterjemahkan di kotak popup, tanpa mikrofon", bool("stt", false)) { c ->
                setBool("stt", c)
                if (!c) stopStt()
                else toast("Buka halaman berisi video, lalu ketuk ⋮ > Terjemahan Suara")
            },
            navRow(R.drawable.ic_mic, "Mesin terjemahan suara", sttEngineName()) { sttEngineDialog() },
            when (sttEngine()) {
                "vosk" -> navRow(R.drawable.ic_download, "Model Vosk", sttVoskSummary()) { openPage("stvosk") }
                "sherpa" -> navRow(R.drawable.ic_download, "Model Sherpa-onnx", sttModelSummary()) { openPage("stmodels") }
                else -> navRow(R.drawable.ic_mic, "Kunci API", if (sttKey().isEmpty()) "Belum diisi" else "••••" + sttKey().takeLast(4)) { sttKeyDialog() }
            },
            if (sttEngine() == "whisper") navRow(R.drawable.ic_globe, "Penyedia", sttProvName()) { sttProvDialog() } else null,
            navRow(R.drawable.ic_offline, "Terjemahan", sttTrName()) { sttTrDialog() },
            if (sttTrOffline()) navRow(R.drawable.ic_translate, "Bahasa terjemahan offline", sttEngSummary()) { openPage("sttr") } else null,
            if (sttModelLang().isNotEmpty()) navRow(R.drawable.ic_mic, "Bahasa ucapan video", "Mengikuti model (" + sttLangDisplay(sttModelLang()) + ")") { sttLangFixedInfo() }
            else navRow(R.drawable.ic_mic, "Bahasa ucapan video", sttNames[sttIdx()]) { openPage("stlang") },
            navRow(R.drawable.ic_globe, "Terjemahkan ke", trNames[trIdx()]) { openPage("trlang") },
            navRow(R.drawable.ic_play, "Kecepatan teks", sttSpeedName()) { sttSpeedDialog() },
            navRow(R.drawable.ic_sliders, "Pengaturan audio", staSummary()) { openPage("staudio") },
            toggleRow("Simpan riwayat terjemahan", "Hasil terjemahan suara disimpan supaya bisa dilihat lagi, disalin, atau diekspor .txt/.srt lewat ikon riwayat di kotak popup. Tidak disimpan di tab penyamaran", bool("stlog", true)) { c -> setBool("stlog", c); if (!c) sttLogClear() },
            navRow(R.drawable.ic_history, "Riwayat terjemahan", sttLogSummary()) { sttLogDialog() },
            if (sttSherpa()) toggleRow("Teks langsung", "Teks muncul sementara saat orang masih bicara, lalu dirapikan saat kalimat selesai. Lebih boros baterai", bool("stlive", true)) { c -> setBool("stlive", c) } else null).filterNotNull())
        addSttDisplaySection(body)
        body.addView(sectionLabel("Tampilan terpisah"))
        addCard(body, listOf<View?>(
            toggleRow("Tampilan terpisah", "Tampilkan Tampilan terpisah di menu titik tiga: layar dibagi dua, video di satu sisi dan catatan atau tab lain di sisi lain", bool("split", true)) { c -> setBool("split", c); if (!c) closeSplit() },
            toggleRow("Balik susunan panel", "Tukar susunan bawaan: layar tegak atas-bawah dan layar mendatar kiri-kanan menjadi sebaliknya", bool("splitflip", false)) { c -> setBool("splitflip", c); if (splitOn) applySplitLayout() }).filterNotNull())
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

    /* ================= Terjemahan Suara (ucapan video -> teks + terjemahan) ================= */

    private val sttCodes = arrayOf("", "en-US", "id-ID", "ja-JP", "ko-KR", "zh-CN", "es-ES", "ar-SA", "hi-IN",
        "fr-FR", "de-DE", "pt-BR", "ru-RU", "it-IT", "nl-NL", "tr-TR", "th-TH", "vi-VN", "ms-MY")
    private val sttNames = arrayOf("Otomatis (deteksi bahasa)", "Inggris", "Indonesia", "Jepang", "Korea", "Mandarin", "Spanyol", "Arab", "Hindi",
        "Prancis", "Jerman", "Portugis", "Rusia", "Italia", "Belanda", "Turki", "Thai", "Vietnam", "Melayu")
    private fun sttIdx() = sttCodes.indexOf(prefs.getString("stlang", "") ?: "").coerceAtLeast(0)

    private fun pageSttLang(): View {
        val (page, body) = shell("Bahasa ucapan video", true)
        val rows = sttNames.mapIndexed { i, n ->
            listRow(iconTile(R.drawable.ic_mic), n, "", if (i == sttIdx()) icon(R.drawable.ic_check, 20, cAcc) else null,
                { prefs.edit().putString("stlang", sttCodes[i]).apply(); if (sttOn) { stopStt(); toast("Nyalakan lagi Terjemahan Suara") }; renderPage() })
        }
        addCard(body, rows)
        return page
    }

    /* ---- Halaman Model offline: unduh, pilih, dan hapus model ---- */

    private var modelsRefresh: (() -> Unit)? = null      // isi ulang semua daftar
    private var modelsRefreshAsr: (() -> Unit)? = null   // hanya daftar model suara + ringkasan (dipanggil saat progres unduhan)
    private val trBusy = HashSet<String>()
    private var trInst: Set<String>? = null
    private var trQuery = ""
    private val dlNotified = HashSet<String>()
    private val trModelPool = java.util.concurrent.Executors.newSingleThreadExecutor()

    private fun metered(): Boolean = try {
        (getSystemService(Context.CONNECTIVITY_SERVICE) as android.net.ConnectivityManager).isActiveNetworkMetered
    } catch (e: Exception) { false }

    private fun onModelsPage() = pages.lastOrNull().let { it == "stmodels" || it == "sttr" }

    private fun hookDownloads() {
        ModelDownloader.listener = { id ->
            runOnUiThread {
                val j = ModelDownloader.job(id)
                if (j != null && j.finished && dlNotified.add(id + "@" + System.identityHashCode(j))) {
                    val m = OfflineModels.find(id)
                    if (j.error != null) toast("Unduhan gagal: " + j.error)
                    else if (!j.cancel && m != null) toast("Model " + m.title + " siap dipakai")
                }
                if (onModelsPage()) modelsRefreshAsr?.invoke()
            }
        }
    }

    private fun startModelDownload(m: AsrModel) {
        if (filesDir.usableSpace < m.totalBytes + m.totalBytes / 10) { toast("Ruang penyimpanan tidak cukup"); return }
        hookDownloads()
        val active = sttAsrModel()
        if (!OfflineModels.installed(this, active)) prefs.edit().putString("stasr", m.id).apply()
        ModelDownloader.start(applicationContext, m)
        modelsRefreshAsr?.invoke()
    }

    private fun asrModelDialog(m: AsrModel) {
        val job = ModelDownloader.job(m.id)
        val running = job != null && !job.finished
        val size = Formatter.formatShortFileSize(this, m.totalBytes)
        when {
            running -> dlg().setTitle(m.title)
                .setMessage("Unduhan sedang berjalan. Bagian yang sudah terunduh disimpan dan bisa dilanjutkan nanti.")
                .setPositiveButton("Batalkan unduhan") { _, _ -> ModelDownloader.cancel(m.id); toast("Unduhan dibatalkan") }
                .setNegativeButton("Lanjutkan", null).show()
            OfflineModels.installed(this, m) -> dlg().setTitle(m.title).setMessage(m.note)
                .setItems(arrayOf("Pakai model ini", "Hapus model ($size)")) { _, w ->
                    if (w == 0) {
                        prefs.edit().putString("stasr", m.id).apply()
                        if (sttOn && sttSherpa()) { stopStt(); toast("Nyalakan lagi Terjemahan Suara") }
                        modelsRefresh?.invoke()
                    } else {
                        dlg().setMessage("Hapus model " + m.title + "? Kamu bisa mengunduhnya lagi kapan saja.")
                            .setPositiveButton("Hapus") { _, _ ->
                                if (sttOn && sttSherpa() && sttAsrModel().id == m.id) stopStt()
                                sttPool.execute { OfflineAsr.release() }
                                OfflineModels.delete(this, m.id)
                                modelsRefresh?.invoke()
                                toast("Model dihapus")
                            }.setNegativeButton("Batal", null).show()
                    }
                }.show()
            else -> dlg().setTitle(m.title)
                .setMessage(m.note + "\n\nUkuran unduhan ±" + size + "." +
                    (if (metered()) "\n\nKamu sedang memakai data seluler. Disarankan memakai Wi-Fi." else ""))
                .setPositiveButton("Unduh") { _, _ -> startModelDownload(m) }
                .setNegativeButton("Batal", null).show()
        }
    }

    private fun trModelDialog(t: String) {
        val nm = OfflineTr.name(t)
        if (t in trBusy) return
        val has = trInst?.contains(t) == true
        if (t == "en" && has) { toast("Model Inggris dipakai sebagai perantara semua terjemahan, jadi tidak bisa dihapus"); return }
        if (has) {
            dlg().setTitle("Model " + nm).setMessage("Hapus model terjemahan " + nm + " dari perangkat? Kamu bisa mengunduhnya lagi nanti.")
                .setPositiveButton("Hapus") { _, _ -> trModelRun(t, false) }
                .setNegativeButton("Batal", null).show()
        } else {
            dlg().setTitle("Unduh model " + nm)
                .setMessage("Ukuran ±30 MB. Dipakai untuk menerjemahkan dari dan ke " + nm + " tanpa internet." +
                    (if (metered()) "\n\nKamu sedang memakai data seluler. Disarankan memakai Wi-Fi." else ""))
                .setPositiveButton("Unduh") { _, _ -> trModelRun(t, true) }
                .setNegativeButton("Batal", null).show()
        }
    }

    private fun trModelRun(t: String, download: Boolean) {
        trBusy.add(t)
        modelsRefresh?.invoke()
        trModelPool.execute {
            val err = try { if (download) OfflineTr.download(t) else OfflineTr.delete(t); null } catch (e: Exception) { e.message ?: "Gagal" }
            val set = try { OfflineTr.downloaded(true) } catch (e: Exception) { null }
            runOnUiThread {
                trBusy.remove(t)
                if (set != null) trInst = set
                val nm = OfflineTr.name(t)
                toast(if (err != null) (if (download) "Gagal mengunduh: " else "Gagal menghapus: ") + err.take(80)
                    else if (download) "Model $nm terpasang" else "Model $nm dihapus")
                if (onModelsPage()) modelsRefresh?.invoke()
            }
        }
    }

    private fun pageSttModels(): View {
        val (page, body) = shell("Model Sherpa-onnx", true)
        hookDownloads()
        body.addView(tv("Model suara Sherpa-onnx diunduh sekali, lalu Terjemahan Suara jalan tanpa internet. Terjemahan offline diatur terpisah di Bahasa terjemahan offline. Disarankan lewat Wi-Fi. Unduhan yang terputus bisa dilanjutkan. Untuk video Jepang, pilih Bahasa ucapan video = Jepang, dan pakai model khusus Jepang (Zipformer, Parakeet, Moonshine) atau Qwen3-ASR; Whisper tiny/base kurang akurat untuk Jepang.", 12f, cSub)
            .apply { setPadding(dp(20), dp(8), dp(20), dp(4)) })

        val asrList = LinearLayout(this).apply { orientation = LinearLayout.VERTICAL }
        body.addView(asrList, lp(MP, WC).apply { setMargins(dp(16), dp(4), dp(16), dp(8)) })
        val foot = LinearLayout(this).apply { orientation = LinearLayout.VERTICAL }

        fun fillAsr() {
            asrList.removeAllViews()
            val active = sttAsrModel().id
            fun rowFor(m: AsrModel): View {
                val job = ModelDownloader.job(m.id)
                val running = job != null && !job.finished
                val inst = OfflineModels.installed(this, m)
                val size = (if (m.dynamic) "±" else "") + Formatter.formatShortFileSize(this, m.totalBytes)
                val sub = when {
                    running -> "Mengunduh " + (job!!.done * 100 / job.total.coerceAtLeast(1)).coerceIn(0, 99) + "% · " +
                        Formatter.formatShortFileSize(this, job.done) + " / " + Formatter.formatShortFileSize(this, job.total)
                    inst -> Formatter.formatShortFileSize(this, OfflineModels.sizeOnDisk(this, m.id)) + " · " + (if (m.id == active) "Terpasang · dipakai" else "Terpasang")
                    job?.error != null -> "Gagal: " + job.error
                    else -> size + " · belum diunduh"
                }
                val trailing: View = when {
                    running -> icon(R.drawable.ic_close, 20, cSub)
                    inst && m.id == active -> icon(R.drawable.ic_check, 20, cAcc)
                    inst -> icon(R.drawable.ic_more, 20, cSub)
                    else -> icon(R.drawable.ic_download, 20, cSub)
                }
                return listRow(iconTile(R.drawable.ic_mic), m.title, sub, trailing, { asrModelDialog(m) }, { asrModelDialog(m) })
            }

            // Urutkan: per bahasa (dikelompokkan), per ukuran, atau yang terpasang dulu
            val sortKey = prefs.getString("stsort", "lang") ?: "lang"
            val sortName = when (sortKey) { "size" -> "Ukuran (kecil dulu)"; "inst" -> "Terpasang dulu"; else -> "Bahasa (dikelompokkan)" }
            asrList.addView(card(listOf(listRow(iconTile(R.drawable.ic_sliders), "Urutkan", sortName, icon(R.drawable.ic_chevron, 18, cSub), {
                dlg().setTitle("Urutkan model")
                    .setItems(arrayOf("Bahasa (dikelompokkan)", "Ukuran (kecil dulu)", "Terpasang dulu")) { _, w ->
                        prefs.edit().putString("stsort", arrayOf("lang", "size", "inst")[w]).apply(); fillAsr()
                    }.show()
            }))), lp(MP, WC).apply { bottomMargin = dp(8) })

            val all = OfflineModels.asr
            fun isIn(m: AsrModel) = OfflineModels.installed(this, m)
            when (sortKey) {
                "size" -> asrList.addView(card(all.sortedBy { it.totalBytes }.map { rowFor(it) }), lp(MP, WC))
                "inst" -> asrList.addView(card(all.sortedWith(compareBy<AsrModel>({ if (isIn(it)) 0 else 1 }, { OfflineModels.groupRank(OfflineModels.group(it)) }, { it.totalBytes })).map { rowFor(it) }), lp(MP, WC))
                else -> {
                    val groups = all.groupBy { OfflineModels.group(it) }.toList().sortedBy { OfflineModels.groupRank(it.first) }
                    for ((g, ms) in groups) {
                        val n = ms.count { isIn(it) }
                        asrList.addView(sectionLabel(OfflineModels.groupName(g) + (if (n > 0) " · $n terpasang" else "")))
                        asrList.addView(card(ms.sortedWith(compareBy<AsrModel>({ if (isIn(it)) 0 else 1 }, { it.totalBytes })).map { rowFor(it) }), lp(MP, WC))
                    }
                }
            }
            foot.removeAllViews()
            val total = OfflineModels.totalOnDisk(this)
            if (total > 0) {
                foot.addView(tv("Model suara memakai " + Formatter.formatShortFileSize(this, total) + " di perangkat", 12f, cSub)
                    .apply { setPadding(dp(20), dp(4), dp(20), dp(8)) })
                addCard(foot, listOf(listRow(iconTile(R.drawable.ic_trash), "Hapus semua model suara", "", null, {
                    dlg().setMessage("Hapus semua model suara yang terunduh (" + Formatter.formatShortFileSize(this, total) + ")?")
                        .setPositiveButton("Hapus") { _, _ ->
                            if (sttOn && sttSherpa()) stopStt()
                            sttPool.execute { OfflineAsr.release() }
                            OfflineModels.asr.forEach { OfflineModels.delete(this, it.id) }
                            modelsRefresh?.invoke()
                            toast("Semua model suara dihapus")
                        }.setNegativeButton("Batal", null).show()
                })))
            }
        }
        body.addView(foot, lp(MP, WC))
        modelsRefreshAsr = { fillAsr() }
        modelsRefresh = { fillAsr() }
        fillAsr()
        return page
    }

    private fun pageSttTr(): View {
        val (page, body) = shell("Bahasa terjemahan offline", true)
        hookDownloads()
        body.addView(tv("Model terjemahan diunduh sekali, lalu terjemahan jalan tanpa internet. Pilih mesin terjemahan, lalu unduh model bahasanya di daftar bawah. Dipakai untuk semua mesin terjemahan suara (Whisper cloud, Sherpa-onnx, Vosk). Disarankan lewat Wi-Fi. Unduhan yang terputus bisa dilanjutkan.", 12f, cSub)
            .apply { setPadding(dp(20), dp(8), dp(20), dp(4)) })
        val topBox = LinearLayout(this).apply { orientation = LinearLayout.VERTICAL }
        body.addView(topBox, lp(MP, WC))
        fun fillTop() {
            topBox.removeAllViews()
            addCard(topBox, listOf(
                navRow(R.drawable.ic_translate, "Mesin terjemahan offline", sttEngSummary()) { sttEngDialog() },
                toggleRow("Cadangan terjemahan online", "Jika model terjemahan belum diunduh, pakai Google Translate online", bool("stfallback", true)) { c -> setBool("stfallback", c) }))
        }
        // Hanya model untuk mesin yang dipilih di "Mesin terjemahan offline" yang ditampilkan.
        val tEng = prefs.getString("stoffeng", "mlkit") ?: "mlkit"
        if (tEng == "nllb") body.addView(sectionLabel("Terjemahan NLLB-200 (lebih akurat)"))
        else if (tEng.isNotEmpty() && tEng != "mlkit")
            body.addView(sectionLabel("Terjemahan " + OfflineMt.engineTitle(tEng) + if (tEng == OfflineMt.M2M) "" else " (model per pasangan bahasa; tanpa pasangan langsung dipakai lewat Inggris)"))
        else if (tEng == "mlkit") body.addView(sectionLabel("Terjemahan ML Kit (per bahasa)"))
        val nllbList = LinearLayout(this).apply { orientation = LinearLayout.VERTICAL }
        if (tEng == "nllb") body.addView(nllbList, lp(MP, WC).apply { setMargins(dp(16), dp(4), dp(16), dp(8)) })
        fun fillNllb() {
            nllbList.removeAllViews()
            val m = OfflineNllb.model
            val job = ModelDownloader.job(m.id)
            val running = job != null && !job.finished
            val inst = OfflineNllb.installed(this)
            val using = prefs.getString("stoffeng", "mlkit") == "nllb"
            val sub = when {
                running -> "Mengunduh " + (job!!.done * 100 / job.total.coerceAtLeast(1)).coerceIn(0, 99) + "% · " +
                    Formatter.formatShortFileSize(this, job.done) + " / " + Formatter.formatShortFileSize(this, job.total)
                inst -> Formatter.formatShortFileSize(this, OfflineModels.sizeOnDisk(this, m.id)) + " · " + (if (using) "Terpasang · dipakai" else "Terpasang")
                !OfflineNllb.available() -> "Tidak tersedia di build ini"
                job?.error != null -> "Gagal: " + job.error
                else -> "±" + Formatter.formatShortFileSize(this, m.totalBytes) + " · belum diunduh · dipakai di mode Offline"
            }
            val trailing: View = when {
                running -> icon(R.drawable.ic_close, 20, cSub)
                inst && using -> icon(R.drawable.ic_check, 20, cAcc)
                inst -> icon(R.drawable.ic_more, 20, cSub)
                else -> icon(R.drawable.ic_download, 20, cSub)
            }
            nllbList.addView(card(listOf(listRow(iconTile(R.drawable.ic_translate), m.title, sub, trailing, { nllbModelDialog() }, { nllbModelDialog() }))), lp(MP, WC))
        }

        val mtList = LinearLayout(this).apply { orientation = LinearLayout.VERTICAL }
        if (tEng in OfflineMt.engines) body.addView(mtList, lp(MP, WC).apply { setMargins(dp(16), dp(4), dp(16), dp(8)) })
        fun mtRow(m: AsrModel, eng: String): View {
            val job = ModelDownloader.job(m.id)
            val running = job != null && !job.finished
            val inst = OfflineModels.installed(this, m)
            val using = prefs.getString("stoffeng", "mlkit") == eng
            val sub = when {
                running -> "Mengunduh " + (job!!.done * 100 / job.total.coerceAtLeast(1)).coerceIn(0, 99) + "% · " +
                    Formatter.formatShortFileSize(this, job.done) + " / " + Formatter.formatShortFileSize(this, job.total)
                inst -> Formatter.formatShortFileSize(this, OfflineModels.sizeOnDisk(this, m.id)) + " · " + (if (using) "Terpasang · dipakai" else "Terpasang")
                !MtNative.available -> "Tidak tersedia di build ini"
                job?.error != null -> "Gagal: " + job.error
                else -> "±" + Formatter.formatShortFileSize(this, m.totalBytes) + " · belum diunduh"
            }
            val trailing: View = when {
                running -> icon(R.drawable.ic_close, 20, cSub)
                inst && using -> icon(R.drawable.ic_check, 20, cAcc)
                inst -> icon(R.drawable.ic_more, 20, cSub)
                else -> icon(R.drawable.ic_download, 20, cSub)
            }
            return listRow(iconTile(R.drawable.ic_translate), m.title, sub, trailing, { mtModelDialog(m, eng) }, { mtModelDialog(m, eng) })
        }
        fun fillMt() {
            mtList.removeAllViews()
            val rows = if (tEng == OfflineMt.M2M) listOf(mtRow(OfflineMt.m2m, OfflineMt.M2M))
                else OfflineMt.pairs(tEng).map { mtRow(it.model, tEng) }
            if (rows.isNotEmpty()) mtList.addView(card(rows), lp(MP, WC))
        }

        val trList = LinearLayout(this).apply { orientation = LinearLayout.VERTICAL }
        fun fillTr() {
            trList.removeAllViews()
            val inst = trInst
            val q = trQuery.trim().lowercase()
            val common = listOf("id", "en", "ja", "ko", "zh", "es", "ar", "hi", "fr", "de", "pt", "ru", "it", "nl", "tr", "th", "vi")
            val all = OfflineTr.all().sortedWith(compareBy<String>({ val i = common.indexOf(it); if (i < 0) 999 else i }, { OfflineTr.name(it) }))
            val rows = ArrayList<View>()
            for (t in all) {
                val nm = OfflineTr.name(t)
                if (q.isNotEmpty() && !nm.lowercase().contains(q) && !t.contains(q)) continue
                val busy = t in trBusy
                val has = inst?.contains(t) == true
                val sub = when {
                    inst == null -> "Memeriksa…"
                    busy -> "Memproses…"
                    has && t == "en" -> "Terpasang · dipakai sebagai perantara"
                    has -> "Terpasang · ±30 MB"
                    t == "en" -> "Belum diunduh · wajib untuk terjemahan offline"
                    else -> "Belum diunduh · ±30 MB"
                }
                val trailing: View? = when {
                    busy -> tv("…", 18f, cSub)
                    has -> icon(R.drawable.ic_check, 20, cAcc)
                    else -> icon(R.drawable.ic_download, 20, cSub)
                }
                rows.add(listRow(iconTile(R.drawable.ic_globe), nm, sub, trailing, { trModelDialog(t) }, { trModelDialog(t) }))
            }
            if (rows.isEmpty()) trList.addView(emptyState(if (OfflineTr.all().isEmpty()) "Terjemahan offline tidak tersedia di perangkat ini" else "Tidak ada hasil"))
            else trList.addView(card(rows), lp(MP, WC))
        }
        if (tEng == "mlkit") {
            body.addView(searchField("Cari bahasa…", trQuery) { trQuery = it; fillTr() },
                lp(MP, WC).apply { setMargins(dp(16), dp(4), dp(16), dp(8)) })
            body.addView(trList, lp(MP, WC).apply { setMargins(dp(16), 0, dp(16), dp(8)) })
        }

        fun fillTrans() { fillTop(); when { tEng == "nllb" -> fillNllb(); tEng in OfflineMt.engines -> fillMt(); tEng == "mlkit" -> fillTr() } }
        modelsRefreshAsr = { fillTrans() }
        modelsRefresh = { fillTrans() }
        fillTrans()
        if (tEng == "mlkit") trModelPool.execute {
            val set = try { OfflineTr.downloaded(true) } catch (e: Exception) { setOf("en") }
            runOnUiThread { trInst = set; if (onModelsPage()) fillTr() }
        }
        return page
    }

    private var sttOn = false
    private var sttWeb: WebView? = null
    private var sttToken = ""
    private var sttBox: LinearLayout? = null
    private var sttStatus: TextView? = null
    private var sttOrig: TextView? = null
    private var sttTrans: TextView? = null
    private var sttPrev: TextView? = null
    // Transkripsi (sttPool, satu thread) dan terjemahan (sttTrPool) dipisah supaya terjemahan yang lambat
    // tidak menahan potongan suara berikutnya. Tiap potongan diberi nomor urut (sttSeqGen) agar hasil
    // terjemahan yang selesai tidak berurutan tetap ditempatkan pada kalimat yang benar.
    private val sttPending = java.util.concurrent.atomic.AtomicInteger(0)     // potongan yang sedang/antre dikenali
    private val sttTrPending = java.util.concurrent.atomic.AtomicInteger(0)   // terjemahan yang sedang/antre
    private val sttSeqGen = java.util.concurrent.atomic.AtomicInteger(0)
    private var sttCurSeq = 0      // (thread utama) nomor kalimat yang teks aslinya sedang tampil
    private var sttTransSeq = 0    // nomor kalimat yang terjemahannya ada di kotak utama (0 = belum ada)
    private var sttPrevSeq = 0     // nomor kalimat yang terjemahannya ada di slot "sebelumnya"
    private var sttFinalOf = 0     // (thread utama) nomor kalimat yang sudah punya hasil final; hasil sementara untuknya diabaikan
    private val sttLiveBusy = java.util.concurrent.atomic.AtomicBoolean(false)    // satu decode sementara dalam satu waktu
    private val sttLiveTrBusy = java.util.concurrent.atomic.AtomicBoolean(false)  // satu terjemahan sementara dalam satu waktu
    private val sttLivePool = java.util.concurrent.Executors.newSingleThreadExecutor()
    @Volatile private var sttCtx = ""      // ekor transkrip sebelumnya, jadi petunjuk (prompt) Whisper agar kalimat nyambung
    private var sttMsg = "Mendengarkan…"       // pesan terakhir dari stt.js
    private var sttLat = ""                    // ", tunda x dtk" terakhir
    @Volatile private var sttCool = 0L     // jeda saat kena batas permintaan (429)
    private val sttPool = java.util.concurrent.Executors.newSingleThreadExecutor()
    private val sttTrPool = java.util.concurrent.Executors.newFixedThreadPool(2)
    private fun sttLive(tok: String) = sttOn && tok == sttToken
    private val sttJs: String by lazy { try { assets.open("stt.js").bufferedReader().use { it.readText() } } catch (e: Exception) { "" } }

    private fun sttKey() = (prefs.getString("stkey", "") ?: "").trim()
    private fun sttProv() = prefs.getString("stprov", "groq") ?: "groq"
    private fun sttProvName() = if (sttProv() == "openai") "OpenAI (whisper-1)" else "Groq (whisper-large-v3-turbo)"

    private fun sttKeyDialog() {
        val col = LinearLayout(this).apply { orientation = LinearLayout.VERTICAL; setPadding(dp(20), dp(8), dp(20), 0) }
        val et = EditText(this).apply {
            hint = if (sttProv() == "openai") "sk-..." else "gsk_..."
            isSingleLine = true; setTextColor(cFg); setHintTextColor(cSub)
            inputType = InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD
            setText(sttKey())
        }
        col.addView(et, lp(MP, WC))
        dlg().setTitle("Kunci API").setMessage("Dipakai untuk mengubah suara video menjadi teks (Whisper). Disimpan hanya di perangkat ini dan tidak ikut ekspor pengaturan.").setView(col)
            .setPositiveButton("Simpan") { _, _ -> prefs.edit().putString("stkey", et.text.toString().trim()).apply(); renderPage() }
            .setNegativeButton("Batal", null).show()
    }

    private fun sttProvDialog() {
        dlg().setTitle("Penyedia terjemahan suara")
            .setItems(arrayOf("Groq (gratis, disarankan)", "OpenAI")) { _, w ->
                prefs.edit().putString("stprov", if (w == 1) "openai" else "groq").apply(); renderPage()
            }.show()
    }

    private fun sttSpeed() = prefs.getString("stspeed", "bal") ?: "bal"
    private fun sttSpeedName() = if (sttVosk()) when (sttSpeed()) {
        "fast" -> "Cepat (kalimat panjang dipotong lebih awal)"
        "eco" -> "Hemat (kalimat lebih panjang sebelum dipotong)"
        else -> "Seimbang"
    } else when (sttSpeed()) {
        "fast" -> "Cepat (±1-2 dtk, banyak permintaan)"
        "eco" -> "Hemat (±3-4 dtk, sedikit permintaan)"
        else -> "Seimbang (±2-3 dtk)"
    }
    private fun sttSpeedDialog() {
        val keys = arrayOf("fast", "bal", "eco")
        dlg().setTitle("Kecepatan teks")
            .setItems(if (sttVosk()) arrayOf(
                "Cepat: ucapan panjang tanpa jeda dipotong lebih awal, jadi kalimat final dan terjemahannya muncul lebih cepat (kalimat lebih pendek)",
                "Seimbang (disarankan)",
                "Hemat: kalimat dibiarkan lebih panjang sebelum dipotong, konteks terjemahan lebih utuh tapi hasil final lebih lambat")
            else arrayOf(
                "Cepat: teks muncul paling awal, memakai paling banyak permintaan API",
                "Seimbang (disarankan)",
                "Hemat: potongan lebih panjang, jatah gratis awet lebih lama")) { _, w ->
                prefs.edit().putString("stspeed", keys[w]).apply()
                if (sttOn) { stopStt(); toast("Nyalakan lagi Terjemahan Suara") }
                renderPage()
            }.show()
    }

    /* ---- Pengaturan audio (Terjemahan Suara): semua opsi bisa dikombinasikan, berlaku untuk semua mesin ---- */

    private class StaOpt(val key: String, val title: String, val def: String, val vals: List<String>, val names: List<String>, val help: String)

    private val staOpts = listOf(
        StaOpt("pg", "Penguat volume awal", "1", listOf("1", "1.5", "2", "3", "4"), listOf("1×", "1,5×", "2×", "3×", "4×"),
            "Menaikkan volume audio video sebelum diproses. Berguna bila video terlalu pelan."),
        StaOpt("min", "Volume minimum", "0", listOf("0", "0.016", "0.025", "0.04", "0.063", "0.1"),
            listOf("Otomatis", "Rendah (−36 dB)", "Sedang (−32 dB)", "Tinggi (−28 dB)", "Sangat tinggi (−24 dB)", "Ekstrem (−20 dB)"),
            "Suara di bawah volume ini dibisukan, jadi tidak diubah jadi teks (bisikan, percakapan latar jauh, derau pelan). Nyalakan Tampilkan level volume di bawah untuk melihat angka dB video."),
        StaOpt("max", "Volume maksimum", "0", listOf("0", "0.5", "0.316", "0.2", "0.1"),
            listOf("Mati", "Sangat keras (−6 dB)", "Keras (−10 dB)", "Sedang (−14 dB)", "Pelan (−20 dB)"),
            "Suara di atas volume ini dibisukan (musik keras, ledakan, efek suara). Hati-hati: bila terlalu rendah, ucapan keras ikut hilang."),
        StaOpt("sens", "Kepekaan deteksi ucapan", "2.8", listOf("2.0", "2.4", "2.8", "3.6", "5.0"),
            listOf("Sangat peka", "Peka", "Normal", "Ketat", "Sangat ketat"),
            "Seberapa jauh suara harus melewati derau latar untuk dianggap ucapan. Ketat: derau dan musik pelan tidak memicu. Peka: ucapan pelan tertangkap. Tidak berlaku untuk Vosk."),
        StaOpt("dur", "Ucapan minimum", "300", listOf("150", "300", "500", "800"),
            listOf("150 ms (sangat peka)", "300 ms", "500 ms", "800 ms"),
            "Potongan suara yang lebih pendek dari ini dibuang sebagai derau (klik, napas, ketukan). Tidak berlaku untuk Vosk."),
        StaOpt("ns", "Peredam derau", "0", listOf("0", "1", "2", "3"),
            listOf("Mati", "Ringan (±−4 dB)", "Sedang (±−7 dB)", "Kuat (±−11 dB)"),
            "Meredam derau yang stabil (desis, dengung kipas/AC, angin, derau rekaman) dengan subtraksi spektrum. Kuat bisa membuat ucapan terdengar sedikit tipis."),
        StaOpt("gate", "Redam suara latar", "0", listOf("0", "1", "2", "3"),
            listOf("Mati", "Ringan", "Sedang", "Kuat"),
            "Menurunkan volume suara latar di sela ucapan (jeda). Mengurangi teks palsu pada bagian senyap atau musik pelan."),
        StaOpt("hp", "Filter gemuruh (high-pass)", "80", listOf("0", "80", "120", "200", "300"),
            listOf("Mati", "80 Hz", "120 Hz", "200 Hz", "300 Hz"),
            "Membuang gemuruh rendah (angin, getaran, dengung). Di atas 200 Hz suara bass bisa menipis."),
        StaOpt("lp", "Filter desis (low-pass)", "0", listOf("0", "7000", "5000", "3800"),
            listOf("Mati", "7 kHz", "5 kHz", "3,8 kHz"),
            "Membuang desis tinggi. Terlalu rendah membuat huruf s, sh, dan f kurang jelas."),
        StaOpt("auto", "Penguat suara pelan otomatis", "6", listOf("1", "3", "6", "12"),
            listOf("Mati (1×)", "Ringan (3×)", "Standar (6×)", "Kuat (12×)"),
            "Menaikkan otomatis potongan yang pelan sebelum dikirim ke pengenal, supaya konsonan lemah tidak hilang. Tidak berlaku untuk Vosk."),
        StaOpt("minlen", "Panjang teks minimum", "2", listOf("1", "2", "4", "6", "10"),
            listOf("1 karakter", "2 karakter", "4 karakter", "6 karakter", "10 karakter"),
            "Hasil teks yang lebih pendek dari ini dibuang."))

    private fun staOpt(k: String) = staOpts.first { it.key == k }
    private fun sta(k: String): String {
        val o = staOpt(k)
        val v = prefs.getString("sta_" + k, o.def) ?: o.def
        return if (v in o.vals) v else o.def
    }
    /** Mode klasik (v78): pemrosesan audio dan decoding seperti v78. Tidak berlaku untuk Vosk. */
    private fun staLegacy() = staBool("legacy", false) && !sttVosk()
    private fun staInt(k: String, d: Int) = try { sta(k).toInt() } catch (e: Exception) { d }
    private fun staBool(k: String, d: Boolean) = prefs.getBoolean("sta_" + k, d)
    private fun staLabel(o: StaOpt): String {
        val v = sta(o.key)
        return o.names[o.vals.indexOf(v).coerceAtLeast(0)] + (if (v == o.def) " (bawaan)" else "")
    }
    private fun staJson(): String {
        val sb = StringBuilder("{")
        for (o in staOpts) { if (o.key == "minlen") continue; sb.append(o.key).append(':').append(sta(o.key)).append(',') }
        sb.append("legacy:").append(staBool("legacy", false)).append(',')
        sb.append("meter:").append(staBool("meter", false)).append('}')
        return sb.toString()
    }
    private fun staSummary(): String {
        val n = staOpts.count { sta(it.key) != it.def } + (if (!staBool("filt", true)) 1 else 0) + (if (staBool("meter", false)) 1 else 0) + (if (staBool("legacy", false)) 1 else 0)
        return if (n == 0) "Standar" else "$n pengaturan diubah"
    }
    /** Terapkan perubahan ke Terjemahan Suara yang sedang berjalan tanpa mematikannya (pipeline audio dibuat ulang). */
    private fun staApply() {
        if (sttOn) { injectStt(sttWeb) }
        renderPage()
    }
    private fun staPick(o: StaOpt) {
        dlg().setTitle(o.title).setMessage(o.help)
            .setItems(Array(o.vals.size) { i -> o.names[i] + (if (o.vals[i] == o.def) " (bawaan)" else "") }) { _, w ->
                prefs.edit().putString("sta_" + o.key, o.vals[w]).apply(); staApply()
            }.show()
    }
    private val staPresets: List<Triple<String, String, Map<String, String>>> = listOf(
        Triple("Standar", "Semua pengaturan audio kembali ke bawaan", emptyMap()),
        Triple("Video bising", "Peredam derau sedang, redam latar ringan, buang gemuruh, deteksi lebih ketat", mapOf("ns" to "2", "gate" to "1", "hp" to "120", "sens" to "3.6", "dur" to "500")),
        Triple("Suara pelan", "Naikkan volume 3×, penguat otomatis kuat, deteksi lebih peka", mapOf("pg" to "3", "auto" to "12", "sens" to "2.4", "dur" to "150")),
        Triple("Musik latar", "Redam latar sedang, peredam derau ringan, fokus ke rentang suara manusia", mapOf("ns" to "1", "gate" to "2", "hp" to "200", "lp" to "5000", "sens" to "3.6", "dur" to "500")),
        Triple("Hanya suara jelas", "Abaikan bisikan dan percakapan latar: volume minimum tinggi, deteksi sangat ketat", mapOf("min" to "0.04", "sens" to "5.0", "gate" to "2", "dur" to "500")))
    private fun staPresetDialog() {
        dlg().setTitle("Preset cepat")
            .setItems(Array(staPresets.size) { i -> staPresets[i].first + ": " + staPresets[i].second }) { _, w ->
                val ed = prefs.edit()
                staOpts.forEach { ed.remove("sta_" + it.key) }
                ed.remove("sta_legacy")
                if (w == 0) { ed.remove("sta_filt"); ed.remove("sta_meter") }
                staPresets[w].third.forEach { (k, v) -> ed.putString("sta_$k", v) }
                ed.apply()
                toast("Preset " + staPresets[w].first + " diterapkan")
                staApply()
            }.show()
    }

    private fun pageSttAudio(): View {
        val (page, body) = shell("Pengaturan audio", true)
        body.addView(tv("Mengatur cara audio video diproses sebelum diubah jadi teks. Semua opsi bisa dikombinasikan dan berlaku untuk semua mesin (Whisper cloud, Sherpa-onnx, Vosk). Perubahan langsung diterapkan, tidak perlu mematikan Terjemahan Suara. Tips: nyalakan Tampilkan level volume untuk melihat angka dB, lalu atur Volume minimum/maksimum.", 12f, cSub)
            .apply { setPadding(dp(20), dp(8), dp(20), dp(4)) })
        body.addView(sectionLabel("Mode"))
        addCard(body, listOf(toggleRow("Mode klasik (v78)", "Proses audio ke teks seperti v78: penurunan ke 16 kHz dengan rata-rata jendela, tanpa pembersih audio, tanpa penguat otomatis, tanpa histeresis, tanpa pemotongan di titik senyap, potongan offline pendek, decoding Zipformer greedy, tanpa Teks langsung, dan saringan halusinasi sederhana. Semua pengaturan audio lain di bawah diabaikan. Tidak berlaku untuk Vosk.", staBool("legacy", false)) { c ->
            prefs.edit().putBoolean("sta_legacy", c).apply()
            if (sttOn) { stopStt(); toast("Nyalakan lagi Terjemahan Suara") }
            renderPage()
        }))
        addCard(body, listOf(navRow(R.drawable.ic_sliders, "Preset cepat", "Video bising, Suara pelan, Musik latar, dll.") { staPresetDialog() }))
        fun group(label: String, keys: List<String>) {
            body.addView(sectionLabel(label))
            addCard(body, keys.map { k -> val o = staOpt(k); navRow(R.drawable.ic_sliders, o.title, staLabel(o)) { staPick(o) } })
        }
        group("Volume dan ambang", listOf("pg", "min", "max", "sens", "dur"))
        group("Pembersih suara", listOf("ns", "gate", "hp", "lp", "auto"))
        body.addView(sectionLabel("Teks hasil"))
        addCard(body, listOf(
            navRow(R.drawable.ic_sliders, staOpt("minlen").title, staLabel(staOpt("minlen"))) { staPick(staOpt("minlen")) },
            toggleRow("Saring halusinasi dan derau teks", "Buang kalimat khas model pada musik/senyap (mis. \"thank you\", \"ご視聴ありがとうございました\") dan pengulangan berlebihan", staBool("filt", true)) { c -> prefs.edit().putBoolean("sta_filt", c).apply() }))
        body.addView(sectionLabel("Diagnostik"))
        addCard(body, listOf(
            toggleRow("Tampilkan level volume", "Tampilkan level volume (dB) di status kotak Terjemahan Suara, untuk menentukan Volume minimum dan maksimum", staBool("meter", false)) { c -> prefs.edit().putBoolean("sta_meter", c).apply(); if (sttOn) injectStt(sttWeb) },
            navRow(R.drawable.ic_reset, "Atur ulang pengaturan audio", "Kembalikan semua ke bawaan") {
                dlg().setMessage("Kembalikan semua Pengaturan audio ke bawaan?")
                    .setPositiveButton("Atur ulang") { _, _ ->
                        val ed = prefs.edit(); staOpts.forEach { ed.remove("sta_" + it.key) }; ed.remove("sta_filt"); ed.remove("sta_meter"); ed.remove("sta_legacy"); ed.apply()
                        toast("Pengaturan audio dikembalikan"); staApply()
                    }.setNegativeButton("Batal", null).show()
            }))
        return page
    }

    /* ---- Mode offline (sherpa-onnx + ML Kit) ---- */

    // Dua pilihan terpisah (v89):
    //  - Mesin terjemahan suara (stengine): "whisper" (cloud), "sherpa" (sherpa-onnx di perangkat), "vosk" (streaming di perangkat)
    //  - Terjemahan (sttr): "online" (Google Translate) atau "offline" (ML Kit / NLLB / M2M100 / Opus-MT / Argos)
    // Keduanya bebas dikombinasikan. Pengaturan lama (stengine "alethra", stmode, stvtr) dibaca sebagai cadangan.
    private fun sttEngine(): String {
        val e = prefs.getString("stengine", "") ?: ""
        if (e == "whisper" || e == "sherpa" || e == "vosk") return e
        return if ((prefs.getString("stmode", "online") ?: "online") == "online") "whisper" else "sherpa"
    }
    private fun sttVosk() = sttEngine() == "vosk"
    private fun sttSherpa() = sttEngine() == "sherpa"
    /** true bila suara dikenali di perangkat (sherpa-onnx atau Vosk), tanpa Whisper cloud. */
    private fun sttOffline() = sttEngine() != "whisper"
    /** true bila terjemahan memakai mesin offline; false = Google Translate online. */
    private fun sttTrOffline(): Boolean {
        val t = prefs.getString("sttr", null)
        if (t != null) return t == "offline"
        return if (prefs.getString("stengine", "") == "vosk") prefs.getString("stvtr", "online") == "offline"
        else prefs.getString("stmode", "online") == "offline"
    }
    private fun sttVoskModel(): VoskModel = VoskModels.find(prefs.getString("stvmodel", "") ?: "") ?: VoskModels.list[0]
    /** Bahasa tetap model yang dipilih; "" bila Whisper cloud atau model multibahasa (bahasa diatur lewat menu). */
    private fun sttModelLang(): String = when (sttEngine()) {
        "vosk" -> sttVoskModel().lang
        "sherpa" -> sttAsrModel().let { it.fixedLang.ifEmpty { if (it.prefix.endsWith(".en")) "en" else "" } }
        else -> ""
    }
    private fun sttFixedLang(): String = sttModelLang()
    private fun sttLangDisplay(code: String): String {
        val n = try { java.util.Locale(code).getDisplayLanguage(java.util.Locale("id")) } catch (e: Exception) { "" }
        return if (n.isBlank() || n.equals(code, true)) code else n.replaceFirstChar { it.uppercase() }
    }
    private fun sttLangFixedInfo() {
        val l = sttLangDisplay(sttModelLang())
        val msg = if (sttVosk()) "Model Vosk yang dipilih (" + sttVoskModel().title + ") khusus bahasa " + l + ", jadi bahasa video tidak diatur di sini. Untuk mengganti bahasa, pilih model lain di Model Vosk."
        else "Model Sherpa-onnx yang dipilih (" + sttAsrModel().title + ") khusus bahasa " + l + ", jadi bahasa video tidak diatur di sini. Untuk memilih bahasa sendiri, pakai model multibahasa (mis. SenseVoice atau Whisper) di Model Sherpa-onnx."
        dlg().setTitle("Bahasa ucapan video").setMessage(msg).setPositiveButton("OK", null).show()
    }
    private fun sttTrName() = if (sttTrOffline()) "Offline (di perangkat)" else "Online (Google Translate)"
    private fun sttAsrModel(): AsrModel = OfflineModels.find(prefs.getString("stasr", "sensevoice") ?: "") ?: OfflineModels.asr[0]
    private fun sttLang2() = (prefs.getString("stlang", "") ?: "").substringBefore('-').lowercase()

    private fun sttModelSummary(): String {
        val m = sttAsrModel()
        return m.title + " · " + when {
            ModelDownloader.running(m.id) -> "mengunduh…"
            OfflineModels.installed(this, m) -> "terpasang"
            else -> "belum diunduh"
        }
    }

    /** Mesin terjemahan offline: ML Kit (ringan) atau NLLB-200 lewat CTranslate2 (lebih akurat). */
    private fun sttNllb() = sttTrOffline() && prefs.getString("stoffeng", "mlkit") == "nllb"

    /** Mesin terjemahan offline selain NLLB (M2M100, Opus-MT, Argos); "" bila tidak dipakai. */
    private fun sttMtEng(): String {
        if (!sttTrOffline()) return ""
        val e = prefs.getString("stoffeng", "mlkit") ?: "mlkit"
        return if (e in OfflineMt.engines) e else ""
    }

    private fun sttEngSummary(): String {
        val e = prefs.getString("stoffeng", "mlkit") ?: "mlkit"
        if (e == "nllb")
            return "NLLB-200 · " + when {
                !OfflineNllb.available() -> "tidak tersedia di build ini"
                ModelDownloader.running(OfflineNllb.model.id) -> "mengunduh…"
                OfflineNllb.installed(this) -> "terpasang"
                else -> "belum diunduh"
            }
        if (e in OfflineMt.engines)
            return OfflineMt.engineTitle(e) + " · " + when {
                !MtNative.available -> "tidak tersedia di build ini"
                OfflineMt.ready(this, e) -> "terpasang"
                else -> "belum diunduh"
            }
        return "ML Kit (ringan)"
    }

    private fun sttEngDialog() {
        val keys = listOf("mlkit", "nllb", OfflineMt.M2M, OfflineMt.OPUS, OfflineMt.ARGOS)
        dlg().setTitle("Mesin terjemahan offline")
            .setItems(arrayOf(
                "ML Kit: ringan (±30 MB per bahasa), kualitas biasa",
                "NLLB-200 distilled 600M (CTranslate2): paling akurat, langsung tanpa perantara Inggris, ±630 MB dan lebih berat",
                "M2M100 418M (CTranslate2): ±100 bahasa langsung, ±490 MB, kualitas di bawah NLLB",
                "Opus-MT (CTranslate2): model kecil per pasangan bahasa (±75 MB), cepat; tanpa pasangan langsung lewat Inggris",
                "Argos Translate (CTranslate2): model per pasangan bahasa, ringan; tanpa pasangan langsung lewat Inggris")) { _, w ->
                val k = keys[w]
                val native = if (k == "nllb") OfflineNllb.available() else MtNative.available
                if (k != "mlkit" && !native) {
                    dlg().setTitle("Mesin tidak tersedia")
                        .setMessage("Mesin CTranslate2 tidak ikut terpasang di build ini. Pada GitHub Actions, langkah \"Bangun mesin terjemahan NLLB-200\" gagal atau perangkat bukan arm64. Pakai ML Kit, atau pakai Terjemahan online (Google Translate).")
                        .setPositiveButton("OK", null).show()
                    return@setItems
                }
                prefs.edit().putString("stoffeng", k).apply()
                if (sttOn) { stopStt(); toast("Nyalakan lagi Terjemahan Suara") }
                val have = when (k) { "mlkit" -> true; "nllb" -> OfflineNllb.installed(this); else -> OfflineMt.ready(this, k) }
                if (!have) toast("Unduh model terjemahan di daftar di bawah")
                renderPage()
            }.show()
    }

    private fun startMtDownload(m: AsrModel) {
        if (filesDir.usableSpace < m.totalBytes + m.totalBytes / 10) { toast("Ruang penyimpanan tidak cukup"); return }
        hookDownloads()
        ModelDownloader.start(applicationContext, m)
        modelsRefreshAsr?.invoke()
    }

    private fun mtModelDialog(m: AsrModel, eng: String) {
        val job = ModelDownloader.job(m.id)
        val size = Formatter.formatShortFileSize(this, m.totalBytes)
        when {
            job != null && !job.finished -> dlg().setTitle(m.title)
                .setMessage("Unduhan sedang berjalan. Bagian yang sudah terunduh disimpan dan bisa dilanjutkan nanti.")
                .setPositiveButton("Batalkan unduhan") { _, _ -> ModelDownloader.cancel(m.id); toast("Unduhan dibatalkan") }
                .setNegativeButton("Lanjutkan", null).show()
            OfflineModels.installed(this, m) -> dlg().setTitle(m.title).setMessage(m.note)
                .setItems(arrayOf("Pakai " + OfflineMt.engineTitle(eng) + " untuk terjemahan offline",
                    "Hapus model (" + Formatter.formatShortFileSize(this, OfflineModels.sizeOnDisk(this, m.id)) + ")")) { _, w ->
                    if (w == 0) {
                        if (!MtNative.available) { toast("Mesin terjemahan tidak tersedia di build ini"); return@setItems }
                        prefs.edit().putString("stoffeng", eng).putString("sttr", "offline").apply()
                        if (sttOn) { stopStt(); toast("Nyalakan lagi Terjemahan Suara") }
                        renderPage()
                    } else {
                        dlg().setMessage("Hapus model " + m.title + "? Kamu bisa mengunduhnya lagi kapan saja.")
                            .setPositiveButton("Hapus") { _, _ ->
                                if (sttOn && sttMtEng().isNotEmpty()) stopStt()
                                sttPool.execute { OfflineMt.release(m.id) }
                                OfflineModels.delete(this, m.id)
                                modelsRefresh?.invoke()
                                toast("Model dihapus")
                            }.setNegativeButton("Batal", null).show()
                    }
                }.show()
            !MtNative.available -> dlg().setTitle(m.title)
                .setMessage("Mesin CTranslate2 tidak ikut terpasang di build ini, jadi model tidak bisa dipakai. Penyebabnya biasanya langkah \"Bangun mesin terjemahan NLLB-200\" di GitHub Actions gagal, atau perangkat bukan arm64.")
                .setPositiveButton("OK", null).show()
            else -> dlg().setTitle(m.title)
                .setMessage(m.note + "\n\nUkuran unduhan ±" + size + "." +
                    (if (metered()) "\n\nKamu sedang memakai data seluler. Disarankan memakai Wi-Fi." else ""))
                .setPositiveButton("Unduh") { _, _ -> startMtDownload(m) }
                .setNegativeButton("Batal", null).show()
        }
    }

    private fun startNllbDownload() {
        val m = OfflineNllb.model
        if (filesDir.usableSpace < m.totalBytes + m.totalBytes / 10) { toast("Ruang penyimpanan tidak cukup"); return }
        hookDownloads()
        ModelDownloader.start(applicationContext, m)
        modelsRefreshAsr?.invoke()
    }

    private fun nllbModelDialog() {
        val m = OfflineNllb.model
        val job = ModelDownloader.job(m.id)
        val size = Formatter.formatShortFileSize(this, m.totalBytes)
        when {
            job != null && !job.finished -> dlg().setTitle(m.title)
                .setMessage("Unduhan sedang berjalan. Bagian yang sudah terunduh disimpan dan bisa dilanjutkan nanti.")
                .setPositiveButton("Batalkan unduhan") { _, _ -> ModelDownloader.cancel(m.id); toast("Unduhan dibatalkan") }
                .setNegativeButton("Lanjutkan", null).show()
            OfflineNllb.installed(this) -> dlg().setTitle(m.title).setMessage(m.note)
                .setItems(arrayOf("Pakai untuk terjemahan offline", "Hapus model ($size)")) { _, w ->
                    if (w == 0) {
                        if (!OfflineNllb.available()) { toast("NLLB tidak tersedia di build ini"); return@setItems }
                        prefs.edit().putString("stoffeng", "nllb").putString("sttr", "offline").apply()
                        if (sttOn) { stopStt(); toast("Nyalakan lagi Terjemahan Suara") }
                        renderPage()
                    } else {
                        dlg().setMessage("Hapus model " + m.title + "? Kamu bisa mengunduhnya lagi kapan saja.")
                            .setPositiveButton("Hapus") { _, _ ->
                                if (sttOn && sttNllb()) stopStt()
                                sttPool.execute { OfflineNllb.release() }
                                OfflineModels.delete(this, m.id)
                                modelsRefresh?.invoke()
                                toast("Model dihapus")
                            }.setNegativeButton("Batal", null).show()
                    }
                }.show()
            !OfflineNllb.available() -> dlg().setTitle(m.title)
                .setMessage("Mesin NLLB (CTranslate2) tidak ikut terpasang di build ini, jadi model tidak bisa dipakai. Penyebabnya biasanya langkah \"Bangun mesin terjemahan NLLB-200\" di GitHub Actions gagal, atau perangkat bukan arm64.")
                .setPositiveButton("OK", null).show()
            else -> dlg().setTitle(m.title)
                .setMessage(m.note + "\n\nUkuran unduhan ±" + size + "." +
                    (if (metered()) "\n\nKamu sedang memakai data seluler. Disarankan memakai Wi-Fi." else ""))
                .setPositiveButton("Unduh") { _, _ -> startNllbDownload() }
                .setNegativeButton("Batal", null).show()
        }
    }

    private fun sttEngineName() = when (sttEngine()) {
        "vosk" -> "Vosk (streaming, di perangkat)"
        "sherpa" -> "Sherpa-onnx (di perangkat)"
        else -> "Whisper cloud"
    }

    private fun sttEngineDialog() {
        val keys = listOf("whisper", "sherpa", "vosk")
        dlg().setTitle("Mesin terjemahan suara")
            .setItems(arrayOf(
                "Whisper cloud: akurat, diproses per kalimat (tunda ±1-3 dtk). Perlu internet dan Kunci API",
                "Sherpa-onnx: berjalan di perangkat, tanpa internet dan tanpa Kunci API. Akurat, diproses per kalimat; perlu unduh model sekali",
                "Vosk: streaming di perangkat, teks muncul kata per kata saat orang masih bicara. Gratis dan offline (unduh model sekali); akurasi di bawah yang lain, tanpa tanda baca")) { _, w ->
                prefs.edit().putString("stengine", keys[w]).apply()
                if (sttOn) { stopStt(); toast("Nyalakan lagi Terjemahan Suara") }
                renderPage()
            }.show()
    }

    private fun sttTrDialog() {
        dlg().setTitle("Terjemahan")
            .setItems(arrayOf(
                "Online: Google Translate (perlu internet, tanpa Kunci API)",
                "Offline: ML Kit, NLLB, dll. di perangkat (perlu unduh model terjemahan sekali)")) { _, w ->
                prefs.edit().putString("sttr", if (w == 1) "offline" else "online").apply()
                if (sttOn) { stopStt(); toast("Nyalakan lagi Terjemahan Suara") }
                renderPage()
            }.show()
    }

    private fun sttVoskSummary(): String {
        val m = sttVoskModel()
        return m.title + " · " + when {
            VoskModels.running(m.id) -> "mengunduh…"
            VoskModels.installed(this, m) -> "terpasang"
            else -> "belum diunduh"
        }
    }

    private fun openModelsPage() { pages.clear(); pages.addAll(listOf("settings", "play", "stmodels")); renderPage() }
    private fun openTrPage() { pages.clear(); pages.addAll(listOf("settings", "play", "sttr")); renderPage() }

    private fun toggleStt() {
        if (sttOn) { stopStt(); return }
        if (sttJs.isEmpty()) { toast("Skrip terjemahan suara tidak ditemukan"); return }
        if (!sttOffline()) {
            if (sttKey().isEmpty()) { toast("Isi kunci API dulu di Pengaturan > Play > Terjemahan Suara"); return }
        } else if (sttVosk()) {
            val vm = sttVoskModel()
            if (!VoskModels.installed(this, vm)) {
                dlg().setTitle("Model Vosk belum diunduh")
                    .setMessage("Terjemahan Suara Vosk butuh model " + vm.title + " (±" + Formatter.formatShortFileSize(this, vm.zipBytes) + "). Buka daftar model Vosk untuk mengunduhnya?")
                    .setPositiveButton("Buka daftar") { _, _ -> pages.clear(); pages.addAll(listOf("settings", "play", "stvosk")); renderPage() }
                    .setNegativeButton("Batal", null).show()
                return
            }
        } else {
            val m = sttAsrModel()
            if (!OfflineModels.installed(this, m)) {
                dlg().setTitle("Model belum diunduh")
                    .setMessage("Sherpa-onnx butuh model suara " + m.title + " (±" + Formatter.formatShortFileSize(this, m.totalBytes) + "). Buka daftar model untuk mengunduhnya?")
                    .setPositiveButton("Buka daftar") { _, _ -> openModelsPage() }
                    .setNegativeButton("Batal", null).show()
                return
            }
        }
        // Terjemahan online (Google Translate): tidak ada model terjemahan yang perlu dicek
        if (!sttTrOffline()) { startStt(); return }
        if (sttNllb()) {
            val fb = bool("stfallback", true)
            if (!OfflineNllb.ready(this) && !fb) {
                dlg().setTitle("Model terjemahan NLLB belum siap")
                    .setMessage(if (!OfflineNllb.available()) "NLLB tidak tersedia di build ini. Pilih ML Kit di Mesin terjemahan offline, atau nyalakan Cadangan terjemahan online."
                        else "Unduh model NLLB-200 (±" + Formatter.formatShortFileSize(this, OfflineNllb.model.totalBytes) + ") di Bahasa terjemahan offline, atau nyalakan Cadangan terjemahan online.")
                    .setPositiveButton("Buka daftar") { _, _ -> openTrPage() }
                    .setNegativeButton("Batal", null).show()
            } else startStt()
            return
        }
        val me = sttMtEng()
        if (me.isNotEmpty()) {
            val fb = bool("stfallback", true)
            if (!OfflineMt.ready(this, me) && !fb) {
                dlg().setTitle("Model terjemahan belum siap")
                    .setMessage(if (!MtNative.available) "Mesin CTranslate2 tidak tersedia di build ini. Pilih ML Kit di Mesin terjemahan offline, atau nyalakan Cadangan terjemahan online."
                        else "Unduh minimal satu model " + OfflineMt.engineTitle(me) + " di Bahasa terjemahan offline, atau nyalakan Cadangan terjemahan online.")
                    .setPositiveButton("Buka daftar") { _, _ -> openTrPage() }
                    .setNegativeButton("Batal", null).show()
            } else startStt()
            return
        }
        // cek model terjemahan bahasa tujuan di thread latar (ML Kit memblokir)
        sttPool.execute {
            val tg = OfflineTr.tag(trLang())
            val miss = if (tg == null) emptyList() else OfflineTr.missing("en", tg)
            val fb = bool("stfallback", true)
            runOnUiThread {
                if (tg == null && !fb) toast("Bahasa tujuan ini tidak didukung terjemahan offline")
                else if (miss.isNotEmpty() && !fb) {
                    dlg().setTitle("Model terjemahan belum diunduh")
                        .setMessage("Unduh model bahasa " + miss.joinToString { OfflineTr.name(it) } + " (±30 MB) di Bahasa terjemahan offline, atau nyalakan Cadangan terjemahan online.")
                        .setPositiveButton("Buka daftar") { _, _ -> openTrPage() }
                        .setNegativeButton("Batal", null).show()
                } else startStt()
            }
        }
    }

    private fun startStt() {
        if (sttOn) return
        sttOn = true
        sttPending.set(0); sttTrPending.set(0); sttSeqGen.set(0)
        sttCurSeq = 0; sttTransSeq = 0; sttPrevSeq = 0; sttFinalOf = 0
        sttLiveBusy.set(false); sttLiveTrBusy.set(false)
        sttVoskLast = ""; sttVoskBacklog.set(0)
        sttCtx = ""
        sttCool = 0L
        sttMsg = "Menyiapkan…"; sttLat = ""
        sttToken = java.util.UUID.randomUUID().toString()
        sttWeb = cw()
        sttLogBegin()
        showSttBox()
        if (customView != null) showSttSub()
        injectStt(sttWeb)
        if (sttOffline() || sttTrOffline()) {
            val m = sttAsrModel(); val lg = sttLang2(); val vk = sttVosk(); val vm = sttVoskModel(); val asrLocal = sttOffline()
            OfflineAsr.greedy = staLegacy()   // Mode klasik: decoding greedy seperti v78
            sttPool.execute {
                try {
                    if (asrLocal) {
                        runOnUiThread { if (sttOn) sttStatus?.text = "Memuat model…" }
                        if (vk) VoskAsr.ensure(applicationContext, vm) else OfflineAsr.ensure(applicationContext, m, lg)
                        runOnUiThread { if (sttOn) sttStatus?.text = sttMsg + sttLat }
                    }
                } catch (e: Throwable) {
                    runOnUiThread { if (sttOn) { toast("Gagal memuat model: " + (e.message ?: "tidak diketahui").take(70)); stopStt() } }
                    return@execute
                }
                // Model terjemahan NLLB (tidak fatal: bila gagal, terjemahan memakai cadangan online)
                if (sttNllb() && OfflineNllb.ready(applicationContext)) {
                    try {
                        runOnUiThread { if (sttOn) sttStatus?.text = "Memuat model terjemahan…" }
                        OfflineNllb.ensure(applicationContext)
                        runOnUiThread { if (sttOn) sttStatus?.text = sttMsg + sttLat }
                    } catch (e: Throwable) {
                        sttNote("Gagal memuat NLLB: " + (e.message ?: "tidak diketahui").take(40))
                    }
                }
                // M2M100 dimuat di depan; Opus-MT dan Argos dimuat per pasangan saat dibutuhkan
                if (sttMtEng() == OfflineMt.M2M && OfflineMt.ready(applicationContext, OfflineMt.M2M)) {
                    try {
                        runOnUiThread { if (sttOn) sttStatus?.text = "Memuat model terjemahan…" }
                        OfflineMt.preloadM2m(applicationContext)
                        runOnUiThread { if (sttOn) sttStatus?.text = sttMsg + sttLat }
                    } catch (e: Throwable) {
                        sttNote("Gagal memuat M2M100: " + (e.message ?: "tidak diketahui").take(40))
                    }
                }
            }
        }
    }

    private fun injectStt(w: WebView?) {
        if (w == null || !sttOn) return
        w.evaluateJavascript("window.__alSttCfg={tok:" + JSONObject.quote(sttToken) + ",mode:" + JSONObject.quote(sttSpeed()) + ",off:" + sttOffline() + ",live:" + (sttSherpa() && !staLegacy()) + ",vosk:" + sttVosk() + ",aud:" + staJson() + "};" + sttJs, null)
    }

    private fun stopStt() {
        val was = sttOn
        sttOn = false
        sttToken = ""
        try { sttWeb?.evaluateJavascript("window.__alSTT&&window.__alSTT.stop()", null) } catch (e: Exception) { }
        sttWeb = null
        hideSttSub()
        sttBox?.let { (it.parent as? ViewGroup)?.removeView(it) }
        sttBox = null; sttOrig = null; sttTrans = null; sttPrev = null; sttStatus = null; sttIcHist = null; sttIcSplit = null
        if (sttLogIncog) { sttLog.clear(); sttTimes.clear(); splitRefreshLog() }
        if (was) {
            toast("Terjemahan Suara dimatikan")
            try { sttPool.execute { OfflineAsr.release(); OfflineTr.close(); OfflineNllb.release(); OfflineMt.releaseAll() }; sttVoskPool.execute { VoskAsr.release() } } catch (e: Exception) { }
        }
    }

    // Jembatan dari stt.js; hanya diterima dengan token acak yang dibuat saat fitur dinyalakan
    inner class SttBridge {
        @JavascriptInterface
        fun status(tok: String, msg: String) {
            if (!sttOn || tok != sttToken) return
            runOnUiThread { if (sttOn) { sttMsg = msg.take(60); sttStatus?.text = sttMsg + sttLat } }
        }

        // Teks langsung: hanya model ringan (decode cepat); Whisper dan model berbasis LLM terlalu lambat untuk diulang tiap 0,6 dtk
        @JavascriptInterface
        fun canPartial(tok: String): Boolean =
            sttOn && tok == sttToken && sttLiveOn() && !sttLiveBusy.get() && sttPending.get() == 0

        @JavascriptInterface
        fun partial(tok: String, b64: String) {
            if (!canPartial(tok) || b64.length > 3_000_000) return
            val bytes = try { Base64.decode(b64, Base64.DEFAULT) } catch (e: Exception) { return }
            if (!sttLiveBusy.compareAndSet(false, true)) return
            val seq = sttSeqGen.get() + 1   // nomor yang akan dipakai potongan final kalimat ini
            sttLivePool.execute {
                try {
                    val o = OfflineAsr.transcribeLive(bytes) ?: return@execute
                    if (o.text.isBlank() || sttNoise(o.text, false)) return@execute
                    runOnUiThread { if (sttLive(tok)) sttShowLive(seq, o.text) }
                    sttLiveTranslate(tok, seq, o.text, o.lang)
                } catch (e: Throwable) {
                } finally { sttLiveBusy.set(false) }
            }
        }

        // Mode Vosk: PCM 16-bit 16 kHz dikirim terus-menerus (±200 ms per kiriman), diproses berurutan di satu thread
        @JavascriptInterface
        fun pcm(tok: String, b64: String) = pcmT(tok, b64, -1.0)

        // Sama dengan pcm, plus posisi video (ms) di akhir sampel ini
        @JavascriptInterface
        fun pcmT(tok: String, b64: String, te: Double) {
            if (!sttOn || tok != sttToken || !sttVosk() || b64.length > 600_000) return
            if (sttVoskBacklog.get() > 60) { runOnUiThread { if (sttOn) sttStatus?.text = "Perangkat sibuk, melewati sebagian suara…" }; return }
            val bytes = try { Base64.decode(b64, Base64.DEFAULT) } catch (e: Exception) { return }
            sttVoskBacklog.incrementAndGet()
            sttVoskPool.execute {
                try { if (te >= 0) sttVoskNow = te.toLong(); sttVoskFeed(tok, bytes) }
                catch (e: Throwable) {
                    val why = (e.message ?: e.javaClass.simpleName).take(40)
                    runOnUiThread { if (sttLive(tok)) sttStatus?.text = "Gagal mengenali suara: " + why }
                } finally { sttVoskBacklog.decrementAndGet() }
            }
        }

        // Video dijeda atau berganti: selesaikan kalimat yang sedang berjalan
        @JavascriptInterface
        fun pcmEnd(tok: String) {
            if (!sttOn || tok != sttToken || !sttVosk()) return
            sttVoskPool.execute { try { sttVoskEnd(tok) } catch (e: Throwable) { } }
        }

        @JavascriptInterface
        fun chunk(tok: String, b64: String, mime: String) = chunkT(tok, b64, mime, -1.0, -1.0)

        // Sama dengan chunk, plus posisi video (ms) awal dan akhir potongan untuk stempel waktu riwayat
        @JavascriptInterface
        fun chunkT(tok: String, b64: String, mime: String, ts: Double, te: Double) {
            if (!sttOn || tok != sttToken || b64.length > 3_000_000) return
            if (System.currentTimeMillis() < sttCool) { runOnUiThread { if (sttOn) sttStatus?.text = "Batas layanan tercapai, jeda sebentar…" }; return }
            if (sttPending.get() >= (if (sttOffline() && !staLegacy()) 6 else 4)) { runOnUiThread { if (sttOn) sttStatus?.text = if (sttOffline()) "Perangkat sibuk, melewati sebagian suara…" else "Jaringan lambat, melewati sebagian suara…" }; return }
            val bytes = try { Base64.decode(b64, Base64.DEFAULT) } catch (e: Exception) { return }
            val t0 = System.currentTimeMillis()
            val seq = sttSeqGen.incrementAndGet()
            sttTimes[seq] = longArrayOf(ts.toLong(), te.toLong())
            sttPending.incrementAndGet()
            sttPool.execute {
                val off = sttOffline()
                val dur = String.format(java.util.Locale.US, "%.1f", ((bytes.size - 44) / 32000.0).coerceAtLeast(0.0))
                try {
                    if (off) runOnUiThread { if (sttLive(tok)) sttStatus?.text = "Mengenali " + dur + " dtk suara…" }
                    var lang = ""
                    val text: String
                    if (off) { val o = OfflineAsr.transcribe(bytes); text = o.text; lang = o.lang }
                    else text = sttTranscribe(bytes, mime)
                    // Diagnosis (mode offline): beri tahu bila model memberi hasil kosong atau hasilnya dibuang sebagai derau
                    if (off && text.isBlank()) sttNote("Hasil kosong dari model (" + dur + " dtk suara)")
                    else if (off && sttNoise(text, false)) sttNote("Diabaikan sebagai derau: " + text.take(20))
                    if (text.isNotBlank() && !sttNoise(text, !off)) {
                        sttCtx = (sttCtx + " " + text).trim().takeLast(160)
                        // Tahap 1: teks asli langsung tampil. Tahap 2 (terjemahan) berjalan di thread lain,
                        // jadi thread ini langsung bebas mengenali potongan berikutnya.
                        runOnUiThread { if (sttLive(tok)) sttShowOrig(seq, text) }
                        sttTranslateAsync(tok, seq, text, lang, off, t0)
                    }
                } catch (e: SttError) {
                    runOnUiThread {
                        if (!sttLive(tok)) return@runOnUiThread
                        if (e.fatal) { toast(e.message ?: "Terjemahan Suara gagal"); stopStt() } else sttStatus?.text = e.message ?: "Gagal"
                    }
                } catch (e: Throwable) {
                    // Throwable, bukan hanya Exception: kegagalan library native (mis. kehabisan memori) tidak lagi hilang diam-diam
                    val why = (e.message ?: e.javaClass.simpleName).take(40)
                    runOnUiThread { if (sttLive(tok)) sttStatus?.text = if (off) "Gagal mengenali suara: " + why else "Gagal menghubungi layanan" }
                } finally { if (tok == sttToken) sttPending.decrementAndGet() }
            }
        }
    }

    // Terjemahan di thread sendiri. Bila terjemahan menumpuk (jaringan/perangkat lambat), kalimat itu
    // ditampilkan apa adanya daripada membuat antrean makin panjang.
    private fun sttTranslateAsync(tok: String, seq: Int, text: String, lang: String, off: Boolean, t0: Long) {
        if (sttTrPending.get() >= 6) {
            runOnUiThread { if (sttLive(tok)) sttShowTr(seq, text, System.currentTimeMillis() - t0, text) }
            return
        }
        sttTrPending.incrementAndGet()
        sttTrPool.execute {
            try {
                val tr = try {
                    // Terjemahan offline (ML Kit/NLLB/dll.) atau Google Translate online, sesuai menu Terjemahan; tidak bergantung pada mesin suara
                    if (sttTrOffline()) offlineTranslate(text, lang) else fetchTranslation(text.take(500), trLang())
                } catch (e: Throwable) { "" }
                if (tr.isBlank() && !sttTrOffline()) sttNote("Google Translate gagal, periksa internet")
                val ms = System.currentTimeMillis() - t0
                runOnUiThread { if (sttLive(tok)) sttShowTr(seq, tr.ifBlank { text }, ms, text) }
            } finally { if (tok == sttToken) sttTrPending.decrementAndGet() }
        }
    }

    private class SttError(m: String, val fatal: Boolean) : Exception(m)

    private val sttHalluc = listOf("ご視聴ありがとうございました", "ご視聴ありがとうございます", "ご清聴ありがとうございました", "チャンネル登録",
        "字幕提供", "最後までご視聴", "请不吝点赞", "感谢观看", "시청해 주셔서 감사합니다", "구독과 좋아요")

    // Whisper sering "berhalusinasi" pada suara senyap; buang kalimat umum itu
    private fun sttNoise(t: String, checkCtx: Boolean = true): Boolean {
        val rx = Regex("[^\\p{L}\\p{N} ]")
        val n = t.lowercase().replace(rx, "").trim()
        if (n.length < (if (staLegacy()) 2 else staInt("minlen", 2))) return true
        if (!staLegacy() && !staBool("filt", true)) return false   // Pengaturan audio: saring halusinasi dimatikan
        if (n.length < 2 || n in setOf("thank you", "thanks for watching", "thank you for watching", "you", "bye", "okay", "terima kasih")) return true
        if (staLegacy()) {   // Mode klasik (v78): hanya daftar Inggris di atas + pengulangan petunjuk
            if (!checkCtx) return false
            return n.length >= 12 && sttCtx.lowercase().replace(rx, "").contains(n)
        }
        // Halusinasi khas model suara pada musik/senyap (Jepang, Mandarin, Korea)
        val cjk = t.replace(Regex("[\\s\\p{P}\\p{S}]"), "")
        if (cjk.length <= 30 && sttHalluc.any { cjk.contains(it) }) return true
        // Pengulangan berlebihan (mis. "ああああああ", "はいはいはいはい"): satu pola pendek mengisi sebagian besar teks
        if (cjk.length >= 8) {
            val rep = Regex("(.{1,6}?)\\1{3,}").find(cjk)
            if (rep != null && rep.value.length * 10 >= cjk.length * 6) return true
        }
        // Whisper kadang mengulang petunjuk (prompt) saat suara tidak jelas
        if (!checkCtx) return false
        val c = sttCtx.lowercase().replace(rx, "")
        return n.length >= 12 && c.contains(n)
    }

    private fun sttNote(m: String) = runOnUiThread { if (sttOn) { sttMsg = m.take(60); sttStatus?.text = sttMsg + sttLat } }

    // Terjemahan ML Kit di perangkat; bila model belum ada, pakai Google Translate online jika cadangan menyala
    private fun offlineTranslate(text: String, srcHint: String): String {
        val fb = bool("stfallback", true)
        fun online(): String {
            if (!fb) return ""
            return try { fetchTranslation(text.take(500), trLang()) } catch (e: Exception) { "" }
        }
        if (sttNllb()) {
            if (!OfflineNllb.ready(applicationContext)) { sttNote("Model NLLB belum siap, pakai cadangan online"); return online() }
            val fxl = sttFixedLang()
            val raw = fxl.ifEmpty { sttLang2() }.ifEmpty { srcHint }.ifEmpty { OfflineTr.detect(text) ?: "en" }
            val sc = OfflineNllb.code(raw)
            val tc = OfflineNllb.code(trLang())
            if (sc == null || tc == null) { sttNote("Bahasa tidak didukung NLLB, pakai cadangan online"); return online() }
            if (sc == tc) return text
            return try { OfflineNllb.translate(text.take(500), sc, tc) }
            catch (e: Throwable) { sttNote("Terjemahan NLLB gagal: " + (e.message ?: "").take(30)); online() }
        }
        val me = sttMtEng()
        if (me.isNotEmpty()) {
            if (!OfflineMt.ready(applicationContext, me)) { sttNote("Model terjemahan belum siap, pakai cadangan online"); return online() }
            val raw = sttFixedLang().ifEmpty { sttLang2() }.ifEmpty { srcHint }.ifEmpty { OfflineTr.detect(text) ?: "en" }
            return try { OfflineMt.translate(applicationContext, me, text.take(500), raw, trLang()) }
            catch (e: OfflineMt.Missing) { sttNote((e.message ?: "Model belum terpasang").take(60)); online() }
            catch (e: Throwable) { sttNote("Terjemahan gagal: " + (e.message ?: "").take(30)); online() }
        }
        val tgt = OfflineTr.tag(trLang())
        if (tgt == null) { sttNote("Bahasa tujuan tidak didukung offline"); return online() }
        val fx = sttFixedLang()   // model satu bahasa: bahasa model lebih pasti daripada pilihan bahasa video
        val src = (if (fx.isNotEmpty()) OfflineTr.tag(fx) else null) ?: OfflineTr.tag(sttLang2()) ?: OfflineTr.tag(srcHint) ?: OfflineTr.detect(text) ?: "en"
        if (src == tgt) return text
        val miss = OfflineTr.missing(src, tgt)
        if (miss.isNotEmpty()) {
            sttNote("Model terjemahan belum diunduh: " + miss.joinToString { OfflineTr.name(it) })
            return online()
        }
        return try { OfflineTr.translate(text.take(500), src, tgt) } catch (e: Exception) { sttNote("Terjemahan offline gagal"); online() }
    }

    private fun sttTranscribe(bytes: ByteArray, mime: String): String {
        val openai = sttProv() == "openai"
        val u = URL(if (openai) "https://api.openai.com/v1/audio/transcriptions" else "https://api.groq.com/openai/v1/audio/transcriptions")
        val model = if (openai) "whisper-1" else "whisper-large-v3-turbo"
        val lang = (prefs.getString("stlang", "") ?: "").substringBefore('-')
        val bd = "----alethra" + System.currentTimeMillis()
        val c = u.openConnection() as HttpURLConnection
        c.requestMethod = "POST"
        c.doOutput = true
        c.connectTimeout = 8000
        c.readTimeout = 20000
        c.setRequestProperty("Authorization", "Bearer " + sttKey())
        c.setRequestProperty("Content-Type", "multipart/form-data; boundary=$bd")
        try {
            c.outputStream.use { o ->
                fun field(n: String, v: String) { o.write(("--$bd\r\nContent-Disposition: form-data; name=\"$n\"\r\n\r\n$v\r\n").toByteArray()) }
                field("model", model); field("response_format", "verbose_json"); field("temperature", "0")
                if (lang.isNotEmpty()) field("language", lang)
                val ctx = sttCtx.trim()
                if (ctx.isNotEmpty()) field("prompt", ctx.replace("\r", " ").replace("\n", " "))
                val ext = if (mime.contains("wav")) "wav" else if (mime.contains("ogg")) "ogg" else "webm"
                o.write(("--$bd\r\nContent-Disposition: form-data; name=\"file\"; filename=\"audio.$ext\"\r\nContent-Type: $mime\r\n\r\n").toByteArray())
                o.write(bytes)
                o.write("\r\n--$bd--\r\n".toByteArray())
            }
            val code = c.responseCode
            if (code == 401 || code == 403) throw SttError("Kunci API ditolak. Periksa kunci di Pengaturan > Play", true)
            if (code == 429) {
                val ra = (c.getHeaderField("retry-after")?.toDoubleOrNull() ?: 10.0).coerceIn(2.0, 60.0)
                sttCool = System.currentTimeMillis() + (ra * 1000).toLong()
                throw SttError("Batas penggunaan layanan tercapai, jeda ${ra.toInt()} dtk", false)
            }
            if (code != 200) throw SttError("Layanan terjemahan suara error ($code)", false)
            val j = JSONObject(c.inputStream.bufferedReader().use { it.readText() })
            val segs = j.optJSONArray("segments")
            if (segs == null || segs.length() == 0) return j.optString("text", "").trim()
            // Buang segmen yang kemungkinan halusinasi (bukan ucapan / berulang-ulang)
            val sb = StringBuilder()
            for (i in 0 until segs.length()) {
                val g = segs.getJSONObject(i)
                if (g.optDouble("no_speech_prob", 0.0) > 0.6 && g.optDouble("avg_logprob", 0.0) < -0.7) continue
                if (g.optDouble("compression_ratio", 0.0) > 2.4) continue
                sb.append(g.optString("text", "")).append(' ')
            }
            return sb.toString().replace(Regex("\\s+"), " ").trim()
        } finally { c.disconnect() }
    }

    /* ---- Vosk (streaming) ---- */

    private val sttVoskPool = java.util.concurrent.Executors.newSingleThreadExecutor()
    private val sttVoskBacklog = java.util.concurrent.atomic.AtomicInteger(0)
    private var sttVoskLast = ""   // hanya diakses thread sttVoskPool: teks sementara terakhir

    private fun sttVoskFeed(tok: String, bytes: ByteArray) {
        if (!sttLive(tok)) return
        val ev = VoskAsr.feed(bytes) ?: return
        val txt = ev.text
        if (ev.isFinal) { sttVoskLast = ""; sttVoskEmitFinal(tok, txt); return }
        if (txt.isBlank() || txt == sttVoskLast) return
        if (sttVoskLast.isEmpty()) sttVoskStart = if (sttVoskNow >= 0) maxOf(0L, sttVoskNow - 500) else -1L
        sttVoskLast = txt
        val cjk = sttVoskModel().lang.let { it == "ja" || it == "zh" }
        // Ucapan panjang tanpa jeda (musik/latar): paksa selesai supaya teks tidak menumpuk.
        // Batasnya mengikuti Kecepatan teks: Cepat = lebih pendek, Seimbang = bawaan, Hemat = lebih panjang.
        val cut = when (sttSpeed()) { "fast" -> if (cjk) 40 else 100; "eco" -> if (cjk) 110 else 260; else -> if (cjk) 70 else 170 }
        if (txt.length > cut) {
            val f = VoskAsr.finish()
            sttVoskLast = ""
            sttVoskEmitFinal(tok, f.ifBlank { txt })
            return
        }
        val seq = sttSeqGen.get() + 1   // nomor yang akan dipakai kalimat ini saat final
        val shown = txt.takeLast(if (sttSpeed() == "eco") 200 else 140)
        runOnUiThread { if (sttLive(tok)) sttShowLive(seq, shown) }
        sttLiveTranslate(tok, seq, shown, "")
    }

    private fun sttVoskEnd(tok: String) {
        if (!sttLive(tok)) return
        val f = VoskAsr.finish()
        sttVoskLast = ""
        sttVoskEmitFinal(tok, f)
    }

    private fun sttVoskEmitFinal(tok: String, text: String) {
        val vs = sttVoskStart; val ve = sttVoskNow
        sttVoskStart = -1L
        if (text.isBlank() || sttNoise(text, false)) return
        // Vosk Inggris sering mengeluarkan "the"/"huh" pada musik dan derau
        val cjk = sttVoskModel().lang.let { it == "ja" || it == "zh" || it == "ko" }
        if (!cjk && text.trim().length < 4) return
        val seq = sttSeqGen.incrementAndGet()
        sttTimes[seq] = if (ve >= 0) longArrayOf(if (vs >= 0) vs else maxOf(0L, ve - 1500), ve) else longArrayOf(-1L, -1L)
        runOnUiThread { if (sttLive(tok)) sttShowOrig(seq, text) }
        sttTranslateAsync(tok, seq, text, "", true, System.currentTimeMillis())
    }

    private var voskRefresh: (() -> Unit)? = null
    private fun onVoskPage() = pages.lastOrNull() == "stvosk"

    private fun hookVoskDownloads() {
        VoskModels.listener = { id ->
            runOnUiThread {
                val j = VoskModels.job(id)
                if (j != null && j.finished && dlNotified.add(id + "@" + System.identityHashCode(j))) {
                    val m = VoskModels.find(id)
                    if (j.error != null) toast("Unduhan gagal: " + j.error)
                    else if (!j.cancel && m != null) toast("Model " + m.title + " siap dipakai")
                }
                if (onVoskPage()) voskRefresh?.invoke()
            }
        }
    }

    private fun startVoskDownload(m: VoskModel) {
        if (filesDir.usableSpace < m.zipBytes * 5 / 2) { toast("Ruang penyimpanan tidak cukup"); return }
        hookVoskDownloads()
        if (!VoskModels.installed(this, sttVoskModel())) prefs.edit().putString("stvmodel", m.id).apply()
        VoskModels.start(applicationContext, m)
        voskRefresh?.invoke()
    }

    private fun voskModelDialog(m: VoskModel) {
        val running = VoskModels.running(m.id)
        val size = Formatter.formatShortFileSize(this, m.zipBytes)
        when {
            running -> dlg().setTitle(m.title)
                .setMessage("Unduhan sedang berjalan. Bagian yang sudah terunduh disimpan dan bisa dilanjutkan nanti.")
                .setPositiveButton("Batalkan unduhan") { _, _ -> VoskModels.cancel(m.id); toast("Unduhan dibatalkan") }
                .setNegativeButton("Lanjutkan", null).show()
            VoskModels.installed(this, m) -> dlg().setTitle(m.title).setMessage(m.note)
                .setItems(arrayOf("Pakai model ini", "Hapus model (" + Formatter.formatShortFileSize(this, VoskModels.sizeOnDisk(this, m.id)) + ")")) { _, w ->
                    if (w == 0) {
                        prefs.edit().putString("stvmodel", m.id).apply()
                        if (sttOn && sttVosk()) { stopStt(); toast("Nyalakan lagi Terjemahan Suara") }
                        voskRefresh?.invoke()
                    } else {
                        dlg().setMessage("Hapus model " + m.title + "? Kamu bisa mengunduhnya lagi kapan saja.")
                            .setPositiveButton("Hapus") { _, _ ->
                                if (sttOn && sttVosk() && sttVoskModel().id == m.id) stopStt()
                                sttVoskPool.execute {
                                    VoskAsr.release()
                                    VoskModels.delete(this, m.id)
                                    runOnUiThread { voskRefresh?.invoke(); toast("Model dihapus") }
                                }
                            }.setNegativeButton("Batal", null).show()
                    }
                }.show()
            else -> dlg().setTitle(m.title)
                .setMessage(m.note + "\n\nUkuran unduhan ±" + size + "." +
                    (if (m.big) "\n\nModel besar: butuh RAM sangat besar dan bisa membuat aplikasi tertutup di HP biasa." else "") +
                    (if (metered()) "\n\nKamu sedang memakai data seluler. Disarankan memakai Wi-Fi." else ""))
                .setPositiveButton("Unduh") { _, _ -> startVoskDownload(m) }
                .setNegativeButton("Batal", null).show()
        }
    }

    private fun pageVosk(): View {
        val (page, body) = shell("Model Vosk", true)
        hookVoskDownloads()
        body.addView(tv("Vosk mengenali suara secara streaming di perangkat: teks muncul kata per kata saat orang masih bicara, tanpa internet dan tanpa Kunci API. Unduh model sekali (disarankan lewat Wi-Fi), pilih sesuai bahasa video; Vosk tidak mendeteksi bahasa otomatis. Hasilnya tanpa tanda baca dan akurasinya di bawah Whisper atau Zipformer. Model besar butuh RAM sangat besar, hanya cocok untuk HP kelas atas.", 12f, cSub)
            .apply { setPadding(dp(20), dp(8), dp(20), dp(4)) })
        val list = LinearLayout(this).apply { orientation = LinearLayout.VERTICAL }
        body.addView(list, lp(MP, WC).apply { setMargins(dp(16), dp(4), dp(16), dp(8)) })
        val foot = LinearLayout(this).apply { orientation = LinearLayout.VERTICAL }
        body.addView(foot, lp(MP, WC))

        fun fill() {
            list.removeAllViews()
            val active = sttVoskModel().id
            fun rowFor(m: VoskModel): View {
                val job = VoskModels.job(m.id)
                val running = job != null && !job.finished
                val inst = VoskModels.installed(this, m)
                val sub = when {
                    running && job!!.extracting -> "Mengekstrak model…"
                    running -> "Mengunduh " + (job!!.done * 100 / job.total.coerceAtLeast(1)).coerceIn(0, 99) + "% · " +
                        Formatter.formatShortFileSize(this, job.done) + " / " + Formatter.formatShortFileSize(this, job.total)
                    inst -> Formatter.formatShortFileSize(this, VoskModels.sizeOnDisk(this, m.id)) + " · " + (if (m.id == active) "Terpasang · dipakai" else "Terpasang")
                    job?.error != null -> "Gagal: " + job.error
                    else -> "±" + Formatter.formatShortFileSize(this, m.zipBytes) + " · belum diunduh" + (if (m.big) " · model besar" else "")
                }
                val trailing: View = when {
                    running -> icon(R.drawable.ic_close, 20, cSub)
                    inst && m.id == active -> icon(R.drawable.ic_check, 20, cAcc)
                    inst -> icon(R.drawable.ic_more, 20, cSub)
                    else -> icon(R.drawable.ic_download, 20, cSub)
                }
                return listRow(iconTile(R.drawable.ic_mic), m.title, sub, trailing, { voskModelDialog(m) }, { voskModelDialog(m) })
            }
            val all = VoskModels.list
            val groups = listOf(
                "Jepang" to all.filter { it.lang == "ja" },
                "Inggris" to all.filter { it.lang == "en" },
                "Bahasa lain" to all.filter { it.lang != "ja" && it.lang != "en" })
            for ((g, ms) in groups) {
                if (ms.isEmpty()) continue
                val n = ms.count { VoskModels.installed(this, it) }
                list.addView(sectionLabel(g + (if (n > 0) " · $n terpasang" else "")))
                val sorted = ms.sortedWith(compareBy<VoskModel>({ if (VoskModels.installed(this, it)) 0 else 1 }, { it.zipBytes }))
                list.addView(card(sorted.map { rowFor(it) }), lp(MP, WC))
            }
            foot.removeAllViews()
            val total = VoskModels.totalOnDisk(this)
            if (total > 0) {
                foot.addView(tv("Model Vosk memakai " + Formatter.formatShortFileSize(this, total) + " di perangkat", 12f, cSub)
                    .apply { setPadding(dp(20), dp(4), dp(20), dp(8)) })
                addCard(foot, listOf(listRow(iconTile(R.drawable.ic_trash), "Hapus semua model Vosk", "", null, {
                    dlg().setMessage("Hapus semua model Vosk yang terunduh (" + Formatter.formatShortFileSize(this, total) + ")?")
                        .setPositiveButton("Hapus") { _, _ ->
                            if (sttOn && sttVosk()) stopStt()
                            sttVoskPool.execute {
                                VoskAsr.release()
                                VoskModels.list.forEach { if (!VoskModels.running(it.id)) VoskModels.delete(this, it.id) }
                                runOnUiThread { voskRefresh?.invoke(); toast("Semua model Vosk dihapus") }
                            }
                        }.setNegativeButton("Batal", null).show()
                })))
            }
        }
        voskRefresh = { fill() }
        fill()
        return page
    }

    private fun sttLiveOn(): Boolean {
        if (!bool("stlive", true) || !sttSherpa()) return false
        return sttAsrModel().kind in setOf("sense", "transducer", "nemo-transducer", "ctc-zipformer", "ctc-nemo", "ctc-tele", "moonshine")
    }

    // Terjemahan sementara hanya memakai jalur ringan (Google Translate online, ML Kit offline);
    // NLLB/M2M100/Opus-MT/Argos cukup berat sehingga terjemahannya baru muncul saat kalimat final.
    private fun sttLiveTranslate(tok: String, seq: Int, text: String, lang: String) {
        val light = !sttTrOffline() || (!sttNllb() && sttMtEng().isEmpty())
        if (!light || !sttLiveTrBusy.compareAndSet(false, true)) return
        try {
            sttTrPool.execute {
                try {
                    val tr = try {
                        if (!sttTrOffline()) fetchTranslation(text.take(300), trLang()) else offlineTranslate(text, lang)
                    } catch (e: Throwable) { "" }
                    if (tr.isNotBlank()) runOnUiThread { if (sttLive(tok)) sttShowLiveTr(seq, tr) }
                } finally { sttLiveTrBusy.set(false) }
            }
        } catch (e: Exception) { sttLiveTrBusy.set(false) }
    }

    // Hasil sementara kalimat yang masih berjalan. Slot dibuka sekali (terjemahan sebelumnya pindah ke "sebelumnya"),
    // lalu teksnya terus diperbarui sampai hasil final datang.
    private fun sttShowLive(seq: Int, orig: String) {
        if (seq < sttCurSeq || seq == sttFinalOf) return
        if (seq > sttCurSeq) {
            val old = sttTrans?.text?.toString().orEmpty()
            if (old.isNotBlank() && old != "…") {
                sttPrev?.apply { text = old; visibility = View.VISIBLE }
                sttPrevSeq = sttTransSeq
            }
            sttCurSeq = seq
            sttTransSeq = 0
            sttTrans?.text = "…"
        }
        sttOrig?.text = orig
        sttTrans?.alpha = 0.75f
    }

    private fun sttShowLiveTr(seq: Int, tr: String) {
        if (seq != sttCurSeq || seq == sttFinalOf) return
        sttTrans?.text = tr
        sttTrans?.alpha = 0.75f
        sttSubUpdate(tr, sttOrig?.text?.toString().orEmpty())
    }

    private fun sttShowOrig(seq: Int, orig: String) {
        // Kalimat yang lebih baru sudah tampil (teks langsung): terjemahan final ini nanti masuk slot "sebelumnya" lewat sttShowTr
        if (seq < sttCurSeq) return
        sttFinalOf = seq
        sttTrans?.alpha = 1f
        // Slot sudah dibuka oleh teks langsung: cukup rapikan teks asli, terjemahan sementara tetap sampai yang final datang
        if (seq == sttCurSeq) { sttOrig?.text = orig; return }
        val old = sttTrans?.text?.toString().orEmpty()
        if (old.isNotBlank() && old != "…") {
            sttPrev?.apply { text = old; visibility = View.VISIBLE }
            sttPrevSeq = sttTransSeq
        }
        sttCurSeq = seq
        sttTransSeq = 0
        sttOrig?.text = orig
        sttTrans?.text = "…"
    }

    private fun sttShowTr(seq: Int, tr: String, ms: Long, orig: String = "") {
        sttLogAdd(seq, orig, tr)
        if (seq < sttCurSeq) {
            // Terlambat: kalimat yang lebih baru sudah tampil. Jangan menimpa kotak utama atau subtitle;
            // taruh di slot "sebelumnya" selama belum ada yang lebih baru di sana.
            if (seq > sttPrevSeq && tr.isNotBlank()) { sttPrevSeq = seq; sttPrev?.apply { text = tr; visibility = View.VISIBLE } }
            return
        }
        sttCurSeq = seq
        sttTransSeq = seq
        sttSubUpdate(tr, orig)
        sttTrans?.text = tr
        sttTrans?.alpha = 1f
        sttLat = " · tunda " + String.format(java.util.Locale.US, "%.1f", ms / 1000.0) + " dtk"
        sttStatus?.text = sttMsg + sttLat
    }

    /* ---- Riwayat Terjemahan Suara: dicatat tiap kalimat final, bisa disalin / diekspor .txt dan .srt ---- */

    class SttEntry(val seq: Int, val startMs: Long, val endMs: Long, val orig: String, val tr: String)

    private val sttLog = java.util.TreeMap<Int, SttEntry>()   // hanya diakses thread utama; kunci = nomor kalimat
    private val sttTimes = java.util.concurrent.ConcurrentHashMap<Int, LongArray>()   // nomor kalimat -> [awal, akhir] posisi video (ms)
    private val sttLogPool = java.util.concurrent.Executors.newSingleThreadExecutor()
    private var sttWallStart = 0L
    private var sttLogIncog = false
    private var sttLogTitle = ""
    private var sttLogDate = 0L
    private var sttLogFileCount = 0
    @Volatile private var sttVoskNow = -1L     // posisi video (ms) di akhir sampel Vosk terakhir
    @Volatile private var sttVoskStart = -1L   // posisi video (ms) saat kalimat Vosk yang berjalan mulai terdengar
    private var sttIcHist: ImageView? = null
    private var sttIcSplit: ImageView? = null

    private fun sttLogOn() = bool("stlog", true)
    private fun sttLogFile() = java.io.File(filesDir, "stt_log.jsonl")

    private fun sttLogSummary(): String =
        if (sttLog.isNotEmpty()) sttLog.size.toString() + " kalimat"
        else if (sttLogFile().exists()) "Sesi terakhir tersimpan" else "Kosong"

    // Sesi baru: riwayat sesi sebelumnya diganti (stempel waktu tiap sesi mengikuti video yang berbeda)
    private fun sttLogBegin() {
        sttLog.clear(); sttTimes.clear()
        sttWallStart = System.currentTimeMillis()
        sttLogDate = sttWallStart
        sttVoskNow = -1L; sttVoskStart = -1L
        val t = tabOf(sttWeb)
        sttLogIncog = t?.incog == true
        sttLogTitle = (t?.title ?: "").take(120)
        sttLogFileCount = 0
        val persist = sttLogOn() && !sttLogIncog
        val head = JSONObject().put("h", sttLogTitle).put("d", sttWallStart).toString() + "\n"
        sttLogPool.execute {
            try { val f = sttLogFile(); if (persist) f.writeText(head, Charsets.UTF_8) else f.delete() } catch (e: Exception) { }
        }
        splitRefreshLog()
    }

    private fun sttLogAdd(seq: Int, orig: String, tr: String) {
        val t = sttTimes.remove(seq)
        if (!sttLogOn()) return
        val o = orig.trim(); val r = tr.trim()
        if (o.isEmpty() && r.isEmpty()) return
        var s = if (t != null) t[0] else -1L
        var e = if (t != null) t[1] else -1L
        // Posisi video tidak diketahui (mis. skrip lama): pakai waktu sejak Terjemahan Suara dinyalakan
        if (s < 0 || e < 0) { e = System.currentTimeMillis() - sttWallStart; s = maxOf(0L, e - 2500) }
        sttLog[seq] = SttEntry(seq, s, e, o, r)
        while (sttLog.size > 5000) sttLog.pollFirstEntry()
        if (!sttLogIncog && sttLogFileCount < 20000) {
            sttLogFileCount++
            val line = JSONObject().put("q", seq).put("s", s).put("e", e).put("o", o).put("t", r).toString() + "\n"
            sttLogPool.execute {
                try { java.io.FileOutputStream(sttLogFile(), true).use { it.write(line.toByteArray(Charsets.UTF_8)) } } catch (ex: Exception) { }
            }
        }
        if (splitOn && splitNotes) splitRefreshLog()
    }

    // Muat sesi terakhir dari berkas (setelah aplikasi dibuka ulang)
    private fun sttLogLoadFile() {
        if (sttLog.isNotEmpty()) return
        try {
            val f = sttLogFile()
            if (!f.exists()) return
            f.forEachLine { l ->
                try {
                    val j = JSONObject(l)
                    if (j.has("h")) { sttLogTitle = j.optString("h"); sttLogDate = j.optLong("d"); return@forEachLine }
                    val q = j.optInt("q")
                    sttLog[q] = SttEntry(q, j.optLong("s"), j.optLong("e"), j.optString("o"), j.optString("t"))
                } catch (ex: Exception) { }
            }
        } catch (ex: Exception) { }
    }

    private fun sttLogClear() {
        sttLog.clear(); sttTimes.clear(); sttLogFileCount = 0
        sttLogPool.execute { try { sttLogFile().delete() } catch (e: Exception) { } }
        splitRefreshLog()
    }

    private fun sttClock(ms: Long, srt: Boolean): String {
        val t = maxOf(0L, ms)
        val h = t / 3600000; val m = (t / 60000) % 60; val s = (t / 1000) % 60; val x = t % 1000
        val lc = java.util.Locale.US
        return if (srt) String.format(lc, "%02d:%02d:%02d,%03d", h, m, s, x)
        else if (h > 0) String.format(lc, "%d:%02d:%02d", h, m, s)
        else String.format(lc, "%02d:%02d", m, s)
    }

    // mode: 0 = terjemahan saja, 1 = teks asli saja, 2 = keduanya (terjemahan di atas, asli di bawah)
    private fun sttLines(e: SttEntry, mode: Int): List<String> {
        val ws = Regex("\\s+")
        val o = e.orig.replace(ws, " ").trim()
        val t = e.tr.replace(ws, " ").trim()
        val main = t.ifEmpty { o }
        val src = o.ifEmpty { t }
        return when (mode) {
            0 -> listOf(main)
            1 -> listOf(src)
            else -> if (main == src) listOf(main) else listOf(main, src)
        }
    }

    private fun sttTxt(mode: Int): String {
        val sb = StringBuilder("Terjemahan Suara")
        if (sttLogTitle.isNotBlank()) sb.append(" - ").append(sttLogTitle)
        sb.append('\n')
        if (sttLogDate > 0) sb.append(java.text.SimpleDateFormat("d MMM yyyy HH:mm", java.util.Locale.getDefault()).format(java.util.Date(sttLogDate))).append('\n')
        sb.append('\n')
        for (e in sttLog.values) {
            val pre = "[" + sttClock(e.startMs, false) + "] "
            val ls = sttLines(e, mode)
            sb.append(pre).append(ls[0]).append('\n')
            for (i in 1 until ls.size) sb.append(" ".repeat(pre.length)).append(ls[i]).append('\n')
        }
        return sb.toString()
    }

    private fun sttSrt(mode: Int): String {
        val list = sttLog.values.sortedBy { it.startMs }
        val sb = StringBuilder()
        for ((i, e) in list.withIndex()) {
            val s = maxOf(0L, e.startMs)
            var en = maxOf(e.endMs, s + 900)
            val nx = list.getOrNull(i + 1)
            if (nx != null && nx.startMs > s + 300 && en > nx.startMs) en = nx.startMs - 20
            if (en <= s) en = s + 500
            sb.append(i + 1).append('\n')
            sb.append(sttClock(s, true)).append(" --> ").append(sttClock(en, true)).append('\n')
            for (ln in sttLines(e, mode)) sb.append(ln).append('\n')
            sb.append('\n')
        }
        return sb.toString()
    }

    private fun sttStamp(): String = java.text.SimpleDateFormat("yyyyMMdd-HHmm", java.util.Locale.US).format(java.util.Date())

    private fun sttCopy(t: String, msg: String) {
        (getSystemService(CLIPBOARD_SERVICE) as ClipboardManager).setPrimaryClip(ClipData.newPlainText("terjemahan", t))
        toast(msg)
    }

    private fun sttLogSave(srt: Boolean, mode: Int) {
        if (sttLog.isEmpty()) return
        saveText("terjemahan-suara-" + sttStamp() + (if (srt) ".srt" else ".txt"),
            if (srt) "application/x-subrip" else "text/plain", if (srt) sttSrt(mode) else sttTxt(mode))
    }

    // kind: 0 = teks biasa, 1 = berkas .txt, 2 = berkas .srt
    private fun sttLogShare(kind: Int, mode: Int) {
        if (sttLog.isEmpty()) return
        if (kind == 0) { skipNextLock = true; share(sttTxt(mode)); return }
        try {
            val srt = kind == 2
            val dir = java.io.File(cacheDir, "share").apply { mkdirs() }
            dir.listFiles()?.forEach { try { it.delete() } catch (e: Exception) { } }
            val out = java.io.File(dir, "terjemahan-suara-" + sttStamp() + (if (srt) ".srt" else ".txt"))
            out.writeText(if (srt) sttSrt(mode) else sttTxt(mode), Charsets.UTF_8)
            val uri = androidx.core.content.FileProvider.getUriForFile(this, "$packageName.files", out)
            val i = Intent(Intent.ACTION_SEND).setType("text/plain")
                .putExtra(Intent.EXTRA_STREAM, uri).addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            skipNextLock = true
            startActivity(Intent.createChooser(i, "Bagikan"))
        } catch (e: Exception) { toast("Tidak bisa membagikan berkas") }
    }

    private fun chip(label: String, onClick: () -> Unit): TextView = tv(label, 13.5f, cFg, true).apply {
        gravity = Gravity.CENTER
        setPadding(dp(14), dp(9), dp(14), dp(9))
        background = shape(cLine, 18)
        isClickable = true
        setOnClickListener { onClick() }
    }

    private fun sttLogRow(e: SttEntry): View {
        val row = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL; setPadding(0, dp(9), 0, dp(9)); background = pressBg(); isClickable = true
        }
        val ls = sttLines(e, 2)
        row.addView(tv(sttClock(e.startMs, false), 11f, cSub))
        row.addView(tv(ls[0], 15f, cFg, true))
        if (ls.size > 1) row.addView(tv(ls[1], 12.5f, cSub))
        row.setOnClickListener { sttCopy(ls.joinToString("\n"), "Baris disalin") }
        return row
    }

    private fun sttLogDialog() {
        if (sttLog.isEmpty() && !sttOn) sttLogLoadFile()
        if (sttLog.isEmpty()) { toast(if (sttLogOn()) "Belum ada riwayat terjemahan" else "Riwayat dimatikan di Pengaturan > Play"); return }
        var mode = prefs.getInt("stlogmode", 2).coerceIn(0, 2)
        var dd: Dlg? = null
        val modeNames = arrayOf("terjemahan saja", "teks asli saja", "terjemahan dan asli")
        val col = LinearLayout(this).apply { orientation = LinearLayout.VERTICAL; setPadding(dp(20), 0, dp(20), 0) }
        col.addView(tv(sttLog.size.toString() + " kalimat" + (if (sttLogTitle.isNotBlank()) " · " + sttLogTitle.take(40) else ""), 12.5f, cSub),
            lp(MP, WC).apply { bottomMargin = dp(10) })
        val modeTv = chip("Isi: " + modeNames[mode]) { }
        modeTv.setOnClickListener {
            mode = (mode + 1) % 3
            prefs.edit().putInt("stlogmode", mode).apply()
            modeTv.text = "Isi: " + modeNames[mode]
        }
        col.addView(modeTv, lp(WC, WC).apply { bottomMargin = dp(8) })
        val bar = LinearLayout(this).apply { orientation = LinearLayout.HORIZONTAL }
        fun add(label: String, act: () -> Unit) { bar.addView(chip(label, act), lp(WC, WC).apply { rightMargin = dp(8) }) }
        add("Salin") { sttCopy(sttTxt(mode), "Riwayat disalin") }
        add(".txt") { dd?.dismiss(); sttLogSave(false, mode) }
        add(".srt") { dd?.dismiss(); sttLogSave(true, mode) }
        add("Bagikan") {
            dd?.dismiss()
            dlg().setTitle("Bagikan riwayat").setItems(arrayOf("Sebagai teks", "Sebagai berkas .txt", "Sebagai berkas .srt")) { _, w -> sttLogShare(w, mode) }.show()
        }
        col.addView(bar, lp(MP, WC).apply { bottomMargin = dp(8) })
        val items = sttLog.values.sortedByDescending { it.seq }.take(200)
        for ((i, e) in items.withIndex()) {
            if (i > 0) col.addView(View(this).apply { setBackgroundColor(cLine) }, lp(MP, 1))
            col.addView(sttLogRow(e))
        }
        if (sttLog.size > items.size) col.addView(tv("Menampilkan 200 terbaru. Salin dan ekspor memuat semuanya.", 12f, cSub), lp(MP, WC).apply { topMargin = dp(8) })
        dd = dlg().setTitle("Riwayat terjemahan suara").setView(col)
            .setPositiveButton("Tutup", null)
            .setNegativeButton("Hapus riwayat") { _, _ ->
                dlg().setTitle("Hapus riwayat?").setMessage("Seluruh riwayat terjemahan suara ini dihapus dan tidak bisa dikembalikan.")
                    .setPositiveButton("Hapus") { _, _ -> sttLogClear(); toast("Riwayat dihapus") }
                    .setNegativeButton("Batal", null).show()
            }.show()
    }

    /* ================= Tampilan terpisah (split view) ================= */

    private var splitOn = false
    private var splitNotes = false
    private var splitTab: Tab? = null
    private var splitBox: LinearLayout? = null
    private var splitDiv: FrameLayout? = null
    private var splitDivPill: View? = null
    private var splitPane: LinearLayout? = null
    private var splitBody: FrameLayout? = null
    private var splitTitle: TextView? = null
    private var splitNoteEt: EditText? = null
    private var splitLogLl: LinearLayout? = null
    private var splitLogSv: ScrollView? = null
    private var splitRatio = 0.5f

    // Jendela ganda bawaan Android (layar dibagi dengan aplikasi lain): jangan hentikan Terjemahan Suara dan video
    private fun inMulti() = Build.VERSION.SDK_INT >= 24 && isInMultiWindowMode

    private fun splitHoriz(): Boolean {
        val land = resources.configuration.orientation == Configuration.ORIENTATION_LANDSCAPE
        return land != bool("splitflip", false)
    }

    private fun applySplitRatio() {
        val r = splitRatio.coerceIn(0.2f, 0.8f)
        (srl.layoutParams as? LinearLayout.LayoutParams)?.weight = r
        (splitPane?.layoutParams as? LinearLayout.LayoutParams)?.weight = 1f - r
        splitBox?.requestLayout()
    }

    private fun applySplitLayout() {
        val box = splitBox ?: return
        if (!splitOn) {
            box.orientation = LinearLayout.VERTICAL
            srl.layoutParams = LinearLayout.LayoutParams(MP, 0, 1f)
            return
        }
        val h = splitHoriz()
        val r = splitRatio.coerceIn(0.2f, 0.8f)
        box.orientation = if (h) LinearLayout.HORIZONTAL else LinearLayout.VERTICAL
        srl.layoutParams = if (h) LinearLayout.LayoutParams(0, MP, r) else LinearLayout.LayoutParams(MP, 0, r)
        splitDiv?.layoutParams = if (h) LinearLayout.LayoutParams(dp(14), MP) else LinearLayout.LayoutParams(MP, dp(14))
        splitPane?.layoutParams = if (h) LinearLayout.LayoutParams(0, MP, 1f - r) else LinearLayout.LayoutParams(MP, 0, 1f - r)
        splitDivPill?.layoutParams = FrameLayout.LayoutParams(if (h) dp(4) else dp(36), if (h) dp(36) else dp(4), Gravity.CENTER)
    }

    private fun buildSplitViews() {
        val div = FrameLayout(this).apply { setBackgroundColor(cLine) }
        val pill = View(this).apply { background = shape(cSub, 4) }
        div.addView(pill, FrameLayout.LayoutParams(dp(36), dp(4), Gravity.CENTER))
        splitDivPill = pill
        div.setOnTouchListener { v, e ->
            val box = splitBox
            if (box == null) false else when (e.actionMasked) {
                MotionEvent.ACTION_DOWN -> { v.parent?.requestDisallowInterceptTouchEvent(true); true }
                MotionEvent.ACTION_MOVE -> {
                    val loc = IntArray(2)
                    box.getLocationOnScreen(loc)
                    val h = box.orientation == LinearLayout.HORIZONTAL
                    val size = (if (h) box.width else box.height).coerceAtLeast(1)
                    val p = if (h) e.rawX - loc[0] else e.rawY - loc[1]
                    splitRatio = (p / size).coerceIn(0.2f, 0.8f)
                    applySplitRatio()
                    true
                }
                MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL -> { prefs.edit().putFloat("splitratio", splitRatio).apply(); true }
                else -> true
            }
        }
        splitDiv = div

        val pane = LinearLayout(this).apply { orientation = LinearLayout.VERTICAL; setBackgroundColor(cBg) }
        val head = LinearLayout(this).apply {
            orientation = LinearLayout.HORIZONTAL; gravity = Gravity.CENTER_VERTICAL
            setBackgroundColor(cSurf); setPadding(dp(12), 0, dp(4), 0)
        }
        val title = tv("", 13f, cFg, true).apply { maxLines = 1; ellipsize = TextUtils.TruncateAt.END }
        splitTitle = title
        head.addView(title, lp(0, WC, 1f))
        fun hb(res: Int, label: String, act: () -> Unit) { head.addView(actBtn(res, label, cFg) { act() }, lp(dp(40), dp(38))) }
        hb(R.drawable.ic_swap, "Tukar panel") { splitSwap() }
        hb(R.drawable.ic_rotate, "Ubah susunan") { setBool("splitflip", !bool("splitflip", false)); applySplitLayout() }
        hb(R.drawable.ic_tab, "Ganti isi panel") { splitChoose() }
        hb(R.drawable.ic_close, "Tutup tampilan terpisah") { closeSplit() }
        pane.addView(head, lp(MP, dp(38)))
        pane.addView(View(this).apply { setBackgroundColor(cLine) }, lp(MP, 1))
        val body = FrameLayout(this)
        pane.addView(body, lp(MP, 0, 1f))
        splitBody = body
        splitPane = pane
    }

    // notes = true: panel catatan + riwayat suara; selain itu tab lain (tab) di panel 2
    private fun openSplit(notes: Boolean, tab: Tab?) {
        val box = splitBox ?: return
        if (!notes && (tab == null || tab === tabs.getOrNull(cur))) return
        if (!splitOn) {
            splitRatio = prefs.getFloat("splitratio", 0.5f)
            buildSplitViews()
            box.addView(splitDiv, LinearLayout.LayoutParams(MP, dp(14)))
            box.addView(splitPane, LinearLayout.LayoutParams(MP, 0, 0.5f))
            splitOn = true
        }
        splitBody?.removeAllViews()
        splitNoteEt = null; splitLogLl = null; splitLogSv = null
        splitTab?.let { if (it !== tab) (it.web.parent as? ViewGroup)?.removeView(it.web) }
        splitNotes = notes
        splitTab = if (notes) null else tab
        if (notes) {
            if (sttLog.isEmpty() && !sttOn) sttLogLoadFile()
            splitBody?.addView(buildNotesPanel(), FrameLayout.LayoutParams(MP, MP))
            splitRefreshLog()
        } else if (tab != null) {
            (tab.web.parent as? ViewGroup)?.removeView(tab.web)
            splitBody?.addView(tab.web, FrameLayout.LayoutParams(MP, MP))
            tab.web.onResume()
        }
        splitUpdateHeader()
        applySplitLayout()
    }

    private fun closeSplit() {
        if (!splitOn) return
        splitOn = false
        splitNotes = false
        try { prefs.edit().putFloat("splitratio", splitRatio).apply() } catch (e: Exception) { }
        splitBody?.removeAllViews()
        splitTab?.let { (it.web.parent as? ViewGroup)?.removeView(it.web) }
        splitTab = null
        val box = splitBox
        splitDiv?.let { box?.removeView(it) }
        splitPane?.let { box?.removeView(it) }
        splitDiv = null; splitDivPill = null; splitPane = null; splitBody = null; splitTitle = null
        splitNoteEt = null; splitLogLl = null; splitLogSv = null
        applySplitLayout()
    }

    // Dipanggil setelah selectTab mengganti isi panel utama: pasang ulang tab panel 2 ke tempatnya
    private fun splitSync() {
        if (!splitOn) return
        val st = splitTab
        val b = splitBody
        if (!splitNotes && st != null && b != null) {
            if (st.web.parent !== b) {
                (st.web.parent as? ViewGroup)?.removeView(st.web)
                b.removeAllViews()
                b.addView(st.web, FrameLayout.LayoutParams(MP, MP))
            }
            st.web.onResume()
        }
        splitUpdateHeader()
    }

    private fun splitUpdateHeader() {
        val t = splitTitle ?: return
        t.text = if (splitNotes) "Catatan dan riwayat suara" else (splitTab?.let { it.title.ifBlank { it.url } } ?: "")
    }

    private fun splitPip(inPip: Boolean) {
        if (!splitOn) return
        val v = if (inPip) View.GONE else View.VISIBLE
        splitPane?.visibility = v
        splitDiv?.visibility = v
    }

    private fun splitSwap() {
        val st = splitTab
        if (splitNotes || st == null) { toast("Panel catatan tidak bisa ditukar"); return }
        if (sttOn) { toast("Matikan Terjemahan Suara dulu untuk menukar panel"); return }
        val i = tabs.indexOf(st)
        if (i >= 0) selectTab(i)
    }

    private fun splitChoose() {
        if (tabs.isEmpty()) return
        if (customView != null) { toast("Keluar dari layar penuh dulu"); return }
        val others = tabs.filterIndexed { i, t -> i != cur && t !== miniTab }
        val labels = mutableListOf("Catatan dan riwayat suara")
        others.forEach { labels.add("Tab: " + (it.title.ifBlank { it.url }).take(40)) }
        labels.add("Tab baru")
        dlg().setTitle("Isi panel kedua").setItems(labels.toTypedArray()) { _, w ->
            when {
                w == 0 -> openSplit(true, null)
                w <= others.size -> openSplit(false, others[w - 1])
                else -> { val t = newTab(home(), false); openSplit(false, t) }
            }
        }.show()
    }

    private fun buildNotesPanel(): View {
        val col = LinearLayout(this).apply { orientation = LinearLayout.VERTICAL }
        val ll = LinearLayout(this).apply { orientation = LinearLayout.VERTICAL; setPadding(dp(12), dp(6), dp(12), dp(6)) }
        val sv = ScrollView(this).apply { isVerticalScrollBarEnabled = false; addView(ll) }
        splitLogLl = ll; splitLogSv = sv
        col.addView(sv, lp(MP, 0, 0.5f))
        col.addView(View(this).apply { setBackgroundColor(cLine) }, lp(MP, 1))
        val et = EditText(this).apply {
            hint = "Catatan…"
            setText(prefs.getString("splitnote", "") ?: "")
            gravity = Gravity.TOP or Gravity.START
            inputType = InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_FLAG_MULTI_LINE or InputType.TYPE_TEXT_FLAG_CAP_SENTENCES
            setTextColor(cFg); setHintTextColor(cSub); textSize = 15f * fontF()
            background = null
            setPadding(dp(12), dp(8), dp(12), dp(8))
            addTextChangedListener(object : TextWatcher {
                override fun afterTextChanged(s: Editable?) { prefs.edit().putString("splitnote", (s?.toString() ?: "").take(200000)).apply() }
                override fun beforeTextChanged(s: CharSequence?, a: Int, b: Int, c: Int) {}
                override fun onTextChanged(s: CharSequence?, a: Int, b: Int, c: Int) {}
            })
        }
        splitNoteEt = et
        col.addView(et, lp(MP, 0, 0.5f))
        val bar = LinearLayout(this).apply { orientation = LinearLayout.HORIZONTAL; setPadding(dp(8), dp(4), dp(8), dp(8)) }
        bar.addView(chip("Salin catatan") {
            val t = et.text.toString()
            if (t.isBlank()) toast("Catatan masih kosong") else sttCopy(t, "Catatan disalin")
        }, lp(WC, WC).apply { rightMargin = dp(8) })
        bar.addView(chip("Simpan .txt") {
            val t = et.text.toString()
            if (t.isBlank()) toast("Catatan masih kosong") else saveText("catatan-" + sttStamp() + ".txt", "text/plain", t)
        }, lp(WC, WC).apply { rightMargin = dp(8) })
        bar.addView(chip("Kosongkan") {
            if (et.text.isNotEmpty()) dlg().setTitle("Hapus catatan?").setMessage("Seluruh isi catatan dihapus.")
                .setPositiveButton("Hapus") { _, _ -> et.setText("") }.setNegativeButton("Batal", null).show()
        }, lp(WC, WC))
        col.addView(bar, lp(MP, WC))
        return col
    }

    private fun splitLogRow(e: SttEntry): View {
        val row = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL; setPadding(0, dp(6), 0, dp(6)); background = pressBg(); isClickable = true
        }
        val ls = sttLines(e, 2)
        row.addView(tv(ls[0], 14f, cFg, true))
        if (ls.size > 1) row.addView(tv(ls[1], 12f, cSub))
        row.setOnClickListener {
            val et = splitNoteEt
            if (et != null) {
                val ed = et.text
                if (ed.isNotEmpty() && !ed.endsWith("\n")) ed.append("\n")
                ed.append(ls[0])
            }
        }
        row.setOnLongClickListener { sttCopy(ls.joinToString("\n"), "Baris disalin"); true }
        return row
    }

    // Daftar riwayat di panel catatan: ketuk baris = sisipkan ke catatan, tahan = salin
    private fun splitRefreshLog() {
        val ll = splitLogLl ?: return
        val sv = splitLogSv
        val atBottom = sv == null || sv.scrollY + sv.height >= ll.height - dp(48)
        ll.removeAllViews()
        val items = sttLog.values.toList().takeLast(120)
        if (items.isEmpty()) {
            ll.addView(tv(if (sttOn) "Menunggu ucapan…" else "Nyalakan Terjemahan Suara (⋮ > Terjemahan Suara). Hasilnya muncul di sini: ketuk baris untuk menyisipkannya ke catatan, tahan untuk menyalin.", 13f, cSub))
            return
        }
        for (e in items) ll.addView(splitLogRow(e))
        if (atBottom) sv?.post { sv.fullScroll(View.FOCUS_DOWN) }
    }

    /* ---- Subtitle terjemahan suara saat video layar penuh ---- */

    private var sttSubBox: LinearLayout? = null
    private var sttSubTr: TextView? = null
    private var sttSubOg: TextView? = null
    private val sttSubHide = Runnable { sttSubBox?.visibility = View.GONE }

    private val sttFsSizeK = arrayOf("sm", "md", "lg", "xl")
    private val sttFsSizeN = arrayOf("Kecil", "Sedang", "Besar", "Sangat besar")
    private val sttFsPosK = arrayOf("bottom", "top")
    private val sttFsPosN = arrayOf("Bawah", "Atas")

    // Palet warna untuk warna teks dan warna latar (selain palet ini, kode hex bebas bisa diketik)
    private val sttSwHex = arrayOf(
        "#FFFFFF", "#FFEB3B", "#80DEEA", "#000000", "#212121", "#9E9E9E",
        "#F44336", "#F48FB1", "#E91E63", "#9C27B0", "#CE93D8", "#3F51B5",
        "#2196F3", "#4FC3F7", "#00BCD4", "#009688", "#4CAF50", "#A5D6A7",
        "#CDDC39", "#FFC107", "#FF9800", "#FF5722", "#795548", "#FFF8E1")
    private val sttSwName = arrayOf(
        "Putih", "Kuning", "Biru muda", "Hitam", "Abu-abu gelap", "Abu-abu",
        "Merah", "Merah muda", "Magenta", "Ungu", "Ungu muda", "Nila",
        "Biru", "Biru langit", "Toska", "Hijau tosca", "Hijau", "Hijau muda",
        "Hijau limau", "Emas", "Oranye", "Oranye tua", "Cokelat", "Krem")

    private fun sttDispMode() = if (prefs.getString("stdisp", "fs") == "box") "box" else "fs"

    private fun sttHex(c: Int) = String.format("#%06X", 0xFFFFFF and c)
    private fun sttParseHex(v: String): Int? {
        var t = v.trim()
        if (t.isEmpty()) return null
        if (!t.startsWith("#")) t = "#$t"
        if (t.length != 7) return null
        return try { Color.parseColor(t) } catch (e: Exception) { null }
    }
    // Nilai lama ("white", "yellow", "cyan") tetap terbaca
    private fun sttParseCol(v: String?, def: Int): Int = when (v) {
        null, "" -> def
        "white" -> Color.WHITE
        "yellow" -> Color.rgb(255, 235, 59)
        "cyan" -> Color.rgb(128, 222, 234)
        else -> sttParseHex(v) ?: def
    }
    private fun sttColName(c: Int): String {
        val i = sttSwHex.indexOfFirst { Color.parseColor(it) == c }
        return if (i >= 0) sttSwName[i] else sttHex(c)
    }

    // p = "stf" (layar penuh) atau "stb" (di luar layar penuh)
    private fun sttTextColor(p: String): Int = sttParseCol(prefs.getString(p + "color", "white"), Color.WHITE)
    private fun sttBgColor(p: String): Int =
        sttParseCol(prefs.getString(p + "bgc", if (p == "stf") "#000000" else "#121212"), if (p == "stf") Color.BLACK else Color.rgb(18, 18, 18))
    private fun sttTransp(p: String): Int {
        val def = if (p == "stf") when (prefs.getString("stfbg", "dim")) { "solid" -> 6; "none" -> 100; else -> 41 } else 12
        return prefs.getInt(p + "bga", def).coerceIn(0, 100)
    }
    private fun sttBgAlpha(p: String) = ((100 - sttTransp(p)) * 255 / 100)
    private fun sttBgArgb(p: String): Int {
        val c = sttBgColor(p)
        return Color.argb(sttBgAlpha(p), Color.red(c), Color.green(c), Color.blue(c))
    }

    private fun sttFsName(key: String, def: String, keys: Array<String>, names: Array<String>) =
        names[keys.indexOf(prefs.getString(key, def) ?: def).coerceAtLeast(0)]

    private fun sttFsPick(title: String, key: String, keys: Array<String>, names: Array<String>) {
        dlg().setTitle(title).setItems(names) { _, w ->
            prefs.edit().putString(key, keys[w]).apply(); sttApplyDisplay(); renderPage()
        }.show()
    }

    // Terapkan pengaturan tampilan ke kotak/subtitle yang sedang tampil
    private fun sttApplyDisplay() {
        styleSttBox()
        if (sttOn && customView != null) showSttSub()
    }

    private fun sttColorDot(c: Int): View = FrameLayout(this).apply {
        background = shape(c, 12, cLine, 1)
        minimumWidth = dp(26); minimumHeight = dp(26)
    }

    private fun sttColorDialog(title: String, cur: Int, onPick: (String) -> Unit) {
        val sc = ScrollView(this)
        val col = LinearLayout(this).apply { orientation = LinearLayout.VERTICAL; setPadding(dp(20), dp(8), dp(20), dp(4)) }
        val prev = FrameLayout(this).apply { background = shape(cur, 12, cLine, 1) }
        col.addView(prev, lp(MP, dp(44)))
        val et = EditText(this).apply {
            hint = "#RRGGBB"; isSingleLine = true; setTextColor(cFg); setHintTextColor(cSub)
            setText(sttHex(cur)); setSelectAllOnFocus(true)
        }
        col.addView(et, lp(MP, WC).apply { topMargin = dp(8) })
        et.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(t: CharSequence?, a: Int, b: Int, c: Int) {}
            override fun onTextChanged(t: CharSequence?, a: Int, b: Int, c: Int) {}
            override fun afterTextChanged(e: android.text.Editable?) {
                val c = sttParseHex(e?.toString().orEmpty()) ?: return
                prev.background = shape(c, 12, cLine, 1)
            }
        })
        col.addView(tv("Pilih warna atau ketik kode hex sendiri", 12f, cSub), lp(WC, WC).apply { topMargin = dp(10); bottomMargin = dp(6) })
        sttSwHex.toList().chunked(6).forEach { chunk ->
            val row = LinearLayout(this).apply { orientation = LinearLayout.HORIZONTAL }
            for (k in 0 until 6) {
                val cell = FrameLayout(this)
                val hx = chunk.getOrNull(k)
                if (hx != null) {
                    val c = Color.parseColor(hx)
                    val dot = FrameLayout(this).apply {
                        background = shape(c, 20, if (c == cur) cFg else cLine, if (c == cur) 3 else 1)
                        isClickable = true
                        contentDescription = sttSwName[sttSwHex.indexOf(hx)]
                        setOnClickListener { et.setText(hx) }
                    }
                    cell.addView(dot, FrameLayout.LayoutParams(dp(38), dp(38), Gravity.CENTER))
                }
                row.addView(cell, lp(0, dp(46), 1f))
            }
            col.addView(row, lp(MP, WC))
        }
        sc.addView(col)
        dlg().setTitle(title).setView(sc)
            .setPositiveButton("Terapkan") { _, _ ->
                val c = sttParseHex(et.text.toString())
                if (c == null) { toast("Kode warna tidak valid, contoh: #FFEB3B"); return@setPositiveButton }
                onPick(sttHex(c))
            }.setNegativeButton("Batal", null).show()
    }

    private fun sttTranspRow(p: String): View {
        val cur = sttTransp(p)
        val r = LinearLayout(this).apply { orientation = LinearLayout.VERTICAL; setPadding(dp(16), dp(12), dp(16), dp(8)) }
        val head = LinearLayout(this).apply { orientation = LinearLayout.HORIZONTAL; gravity = Gravity.CENTER_VERTICAL }
        head.addView(tv("Transparansi latar", 15f, cFg, true), lp(0, WC, 1f))
        val vl = tv("$cur%", 14f, cSub, true)
        head.addView(vl, lp(WC, WC))
        r.addView(head, lp(MP, WC))
        r.addView(tv("0% = pekat, 100% = tembus penuh (tanpa latar)", 12f, cSub), lp(MP, WC))
        val sb = SeekBar(this).apply {
            max = 100; progress = cur
            progressTintList = ColorStateList.valueOf(cAcc)
            thumbTintList = ColorStateList.valueOf(cAcc)
        }
        sb.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(s: SeekBar?, v: Int, fromUser: Boolean) {
                vl.text = "$v%"
                if (fromUser) { prefs.edit().putInt(p + "bga", v).apply(); sttApplyDisplay() }
            }
            override fun onStartTrackingTouch(s: SeekBar?) {}
            override fun onStopTrackingTouch(s: SeekBar?) {}
        })
        r.addView(sb, lp(MP, WC).apply { topMargin = dp(4) })
        return r
    }

    // Bagian "Tampilan terjemahan suara": pilih mode (layar penuh / di luar layar penuh), lalu atur tampilannya
    private fun addSttDisplaySection(body: LinearLayout) {
        body.addView(sectionLabel("Tampilan terjemahan suara"))
        val fs = sttDispMode() == "fs"
        val seg = LinearLayout(this).apply { orientation = LinearLayout.HORIZONTAL; setPadding(dp(16), dp(4), dp(16), dp(8)) }
        fun segBtn(label: String, on: Boolean, mode: String): TextView = tv(label, 14f, if (on) cOnAcc else cFg, true).apply {
            gravity = Gravity.CENTER
            setPadding(dp(8), dp(12), dp(8), dp(12))
            background = shape(if (on) cAcc else cSurf, 14)
            isClickable = true
            setOnClickListener { prefs.edit().putString("stdisp", mode).apply(); renderPage() }
        }
        seg.addView(segBtn("Layar penuh", fs, "fs"), lp(0, WC, 1f).apply { rightMargin = dp(6) })
        seg.addView(segBtn("Di luar layar penuh", !fs, "box"), lp(0, WC, 1f).apply { leftMargin = dp(6) })
        body.addView(seg, lp(MP, WC))
        body.addView(tv(if (fs) "Subtitle saat video dibuka layar penuh" else "Kotak popup saat video tidak layar penuh", 12f, cSub)
            .apply { setPadding(dp(20), 0, dp(20), dp(4)) }, lp(MP, WC))

        val p = if (fs) "stf" else "stb"
        val rows = mutableListOf<View>()
        rows.add(navRow(R.drawable.ic_sliders, "Ukuran teks", sttFsName(p + "size", "md", sttFsSizeK, sttFsSizeN)) {
            sttFsPick("Ukuran teks", p + "size", sttFsSizeK, sttFsSizeN) })
        if (fs) rows.add(navRow(R.drawable.ic_expand, "Posisi", sttFsName("stfpos", "bottom", sttFsPosK, sttFsPosN)) {
            sttFsPick("Posisi teks", "stfpos", sttFsPosK, sttFsPosN) })
        rows.add(sttTranspRow(p))
        val bgc = sttBgColor(p)
        rows.add(listRow(iconTile(R.drawable.ic_palette), "Warna latar", sttColName(bgc), sttColorDot(bgc), {
            sttColorDialog("Warna latar", bgc) { v -> prefs.edit().putString(p + "bgc", v).apply(); sttApplyDisplay(); renderPage() } }))
        val tc = sttTextColor(p)
        rows.add(listRow(iconTile(R.drawable.ic_palette), "Warna teks", sttColName(tc), sttColorDot(tc), {
            sttColorDialog("Warna teks", tc) { v -> prefs.edit().putString(p + "color", v).apply(); sttApplyDisplay(); renderPage() } }))
        if (fs) rows.add(toggleRow("Tampilkan teks asli", "Tampilkan ucapan asli di bawah terjemahan", bool("stforig", false)) { c -> setBool("stforig", c) })
        rows.add(navRow(R.drawable.ic_reset, "Atur ulang tampilan", "Kembalikan ke bawaan untuk mode ini", false) {
            val ed = prefs.edit().remove(p + "size").remove(p + "bga").remove(p + "bgc").remove(p + "color")
            if (fs) ed.remove("stfpos").remove("stforig").remove("stfbg")
            ed.apply(); sttApplyDisplay(); renderPage()
        })
        addCard(body, rows)
    }

    private fun showSttSub() {
        val holder = fsHolder ?: return
        hideSttSub()
        val sp = when (prefs.getString("stfsize", "md")) { "sm" -> 15f; "lg" -> 26f; "xl" -> 32f; else -> 20f }
        val col = sttTextColor("stf")
        val bgA = sttBgAlpha("stf")
        val noBg = sttTransp("stf") >= 85
        val shadowC = if (lumOf(col) < 90) Color.WHITE else Color.BLACK
        val top = prefs.getString("stfpos", "bottom") == "top"
        val box = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            if (bgA > 0) background = shape(sttBgArgb("stf"), 8)
            setPadding(dp(10), dp(4), dp(10), dp(6))
            visibility = View.GONE
        }
        fun line(size: Float, c: Int, bold: Boolean) = TextView(this).apply {
            textSize = size; setTextColor(c); gravity = Gravity.CENTER
            if (bold) typeface = Typeface.create("sans-serif-medium", Typeface.NORMAL)
            if (noBg) setShadowLayer(8f, 0f, 0f, shadowC)
            maxLines = 3; ellipsize = TextUtils.TruncateAt.END
        }
        val tr = line(sp, col, true)
        box.addView(tr)
        if (bool("stforig", false)) {
            val og = line(sp * 0.7f, Color.argb(200, Color.red(col), Color.green(col), Color.blue(col)), false)
            box.addView(og); sttSubOg = og
        }
        sttSubTr = tr; sttSubBox = box
        val lp = FrameLayout.LayoutParams(WC, WC, (if (top) Gravity.TOP else Gravity.BOTTOM) or Gravity.CENTER_HORIZONTAL).apply {
            setMargins(dp(56), if (top) dp(16) else 0, dp(56), if (top) 0 else dp(40))
        }
        holder.addView(box, lp)
    }

    private fun hideSttSub() {
        sttSubBox?.removeCallbacks(sttSubHide)
        sttSubBox?.let { (it.parent as? ViewGroup)?.removeView(it) }
        sttSubBox = null; sttSubTr = null; sttSubOg = null
    }

    private fun sttSubUpdate(tr: String, orig: String) {
        val box = sttSubBox ?: return
        if (tr.isBlank()) return
        if (pipActive) { box.visibility = View.GONE; return }
        sttSubTr?.text = tr
        sttSubOg?.text = orig
        box.visibility = View.VISIBLE
        box.removeCallbacks(sttSubHide)
        box.postDelayed(sttSubHide, 8000)
    }

    private fun showSttBox() {
        sttBox?.let { (it.parent as? ViewGroup)?.removeView(it) }
        val box = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            background = shape(Color.argb(225, 18, 18, 18), 16)
            setPadding(dp(14), dp(10), dp(14), dp(12))
            elevation = dp(10).toFloat()
        }
        val head = LinearLayout(this).apply { orientation = LinearLayout.HORIZONTAL; gravity = Gravity.CENTER_VERTICAL }
        val micIc = icon(R.drawable.ic_mic, 16, Color.WHITE)
        sttIcMic = micIc
        head.addView(micIc)
        val st = tv("Menyiapkan…", 12f, Color.argb(190, 255, 255, 255), true).apply { setPadding(dp(8), 0, 0, 0) }
        sttStatus = st
        head.addView(st, LinearLayout.LayoutParams(0, WC, 1f))
        val sp = icon(R.drawable.ic_split, 18, Color.WHITE).apply {
            layoutParams = LinearLayout.LayoutParams(dp(18), dp(18))
            contentDescription = "Tampilan terpisah"
        }
        sttIcSplit = sp
        if (bool("split", true)) head.addView(FrameLayout(this).apply {
            setPadding(dp(8), dp(4), dp(6), dp(4)); addView(sp)
            setOnClickListener { if (splitOn) closeSplit() else splitChoose() }
        })
        val hi = icon(R.drawable.ic_history, 18, Color.WHITE).apply {
            layoutParams = LinearLayout.LayoutParams(dp(18), dp(18))
            contentDescription = "Riwayat"
        }
        sttIcHist = hi
        head.addView(FrameLayout(this).apply {
            setPadding(dp(6), dp(4), dp(6), dp(4)); addView(hi)
            setOnClickListener { sttLogDialog() }
        })
        val x = icon(R.drawable.ic_close, 18, Color.WHITE).apply {
            layoutParams = LinearLayout.LayoutParams(dp(18), dp(18))
            contentDescription = "Tutup"
        }
        sttIcX = x
        val xw = FrameLayout(this).apply {
            setPadding(dp(8), dp(4), dp(2), dp(4)); addView(x)
            setOnClickListener { stopStt() }
        }
        head.addView(xw)
        box.addView(head)
        val prev = tv("", 13f, Color.argb(130, 255, 255, 255)).apply { visibility = View.GONE; setPadding(0, dp(6), 0, 0); maxLines = 2; ellipsize = TextUtils.TruncateAt.END }
        val tr = tv("", 17f, Color.WHITE, true).apply { setPadding(0, dp(6), 0, 0); minLines = 1; maxLines = 5; ellipsize = TextUtils.TruncateAt.END }
        val og = tv("", 12.5f, Color.argb(170, 255, 255, 255)).apply { setPadding(0, dp(4), 0, 0); maxLines = 3; ellipsize = TextUtils.TruncateAt.END }
        sttPrev = prev; sttTrans = tr; sttOrig = og
        box.addView(prev); box.addView(tr); box.addView(og)

        // Geser kotak lewat kepala/seluruh kotak
        var sx = 0f; var sy = 0f; var bx = 0f; var by = 0f
        box.setOnTouchListener { v, e ->
            when (e.actionMasked) {
                MotionEvent.ACTION_DOWN -> { sx = e.rawX; sy = e.rawY; bx = v.translationX; by = v.translationY }
                MotionEvent.ACTION_MOVE -> {
                    v.translationX = (bx + e.rawX - sx).coerceIn(-v.left.toFloat(), (host.width - v.right).toFloat())
                    v.translationY = (by + e.rawY - sy).coerceIn(-v.top.toFloat(), (host.height - v.bottom).toFloat())
                }
            }
            true
        }
        val lp = FrameLayout.LayoutParams(MP, WC, Gravity.BOTTOM or Gravity.CENTER_HORIZONTAL).apply {
            setMargins(dp(12), dp(12), dp(12), dp(20))
        }
        host.addView(box, lp)
        sttBox = box
        styleSttBox()
    }

    private var sttIcMic: ImageView? = null
    private var sttIcX: ImageView? = null

    // Gaya kotak popup (di luar layar penuh): ukuran teks, warna teks, warna dan transparansi latar
    private fun styleSttBox() {
        val box = sttBox ?: return
        val col = sttTextColor("stb")
        val f = when (prefs.getString("stbsize", "md")) { "sm" -> 0.85f; "lg" -> 1.25f; "xl" -> 1.5f; else -> 1f } * fontF()
        fun a(al: Int) = Color.argb(al, Color.red(col), Color.green(col), Color.blue(col))
        val bgA = sttBgAlpha("stb")
        box.background = shape(sttBgArgb("stb"), 16)
        box.elevation = if (bgA < 40) 0f else dp(10).toFloat()
        val noBg = sttTransp("stb") >= 85
        val shC = if (lumOf(col) < 90) Color.WHITE else Color.BLACK
        sttStatus?.apply { setTextColor(a(190)); textSize = 12f * f }
        sttPrev?.apply { setTextColor(a(130)); textSize = 13f * f }
        sttTrans?.apply { setTextColor(col); textSize = 17f * f }
        sttOrig?.apply { setTextColor(a(170)); textSize = 12.5f * f }
        listOf(sttStatus, sttPrev, sttTrans, sttOrig).forEach {
            if (noBg) it?.setShadowLayer(8f, 0f, 0f, shC) else it?.setShadowLayer(0f, 0f, 0f, 0)
        }
        sttIcMic?.imageTintList = ColorStateList.valueOf(col)
        sttIcX?.imageTintList = ColorStateList.valueOf(col)
        sttIcHist?.imageTintList = ColorStateList.valueOf(col)
        sttIcSplit?.imageTintList = ColorStateList.valueOf(col)
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
        if (t.startsWith("file:", true) || t.startsWith("content:", true)) {
            if (localUrlOk(t)) return t
            toast("Berkas lokal ini tidak bisa dibuka")
            return home()
        }
        if (Regex("^[a-zA-Z][a-zA-Z0-9+.-]*://.*").matches(t) || t.startsWith("about:")) return t
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
            // Berkas lokal hanya untuk aset beranda dan arsip offline (lihat localUrlOk); selebihnya ditolak
            allowFileAccess = true
            allowContentAccess = false
            allowFileAccessFromFileURLs = false
            allowUniversalAccessFromFileURLs = false
        }
        CookieManager.getInstance().setAcceptThirdPartyCookies(w, bool("tpc", true) && !incog)
        w.addJavascriptInterface(TrBridge(w), "AlethraTr")
        w.addJavascriptInterface(AppBridge(w), "AlApp")
        w.addJavascriptInterface(SttBridge(), "AlSTT")
        applySite(w)
        if (incog) {
            w.settings.cacheMode = WebSettings.LOAD_NO_CACHE
            @Suppress("DEPRECATION") run { w.settings.saveFormData = false }
            if (w.tag == "incog-weak") w.settings.domStorageEnabled = false
        }

        w.webViewClient = object : WebViewClient() {
            override fun shouldInterceptRequest(v: WebView?, r: WebResourceRequest?): WebResourceResponse? {
                val req = r ?: return null
                val ru = req.url.toString()
                if ((ru.startsWith("file:", true) || ru.startsWith("content:", true)) && !localUrlOk(ru))
                    return WebResourceResponse("text/plain", "utf-8", 403, "Forbidden", emptyMap(), java.io.ByteArrayInputStream(ByteArray(0)))
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
                if (!localUrlOk(u)) { toast("Berkas lokal ini tidak bisa dibuka"); return true }
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

            // Navigasi di dalam halaman (SPA: history.pushState/replaceState, pindah kategori tanpa muat ulang) tidak memicu onPageStarted/onPageFinished,
            // jadi alamat diperbarui di sini supaya kolom alamat, sesi, dan riwayat ikut mengikuti.
            override fun doUpdateVisitedHistory(v: WebView?, url: String?, isReload: Boolean) {
                super.doUpdateVisitedHistory(v, url, isReload)
                val t = tabOf(v) ?: return
                if (url.isNullOrEmpty() || url == "about:blank" || url.startsWith("data:") || url == t.url) return
                t.url = url
                if (isCur(v)) setUrlText(url)
                if (url.startsWith("http")) {
                    val tab = t
                    handler.postDelayed({
                        // judul SPA biasanya baru berganti beberapa saat setelah URL berubah
                        if (tab.url == url && tabs.contains(tab)) addHistory(tab.web.title ?: url, url)
                    }, 1200)
                }
            }

            override fun onPageStarted(v: WebView?, url: String?, favicon: Bitmap?) {
                val t = tabOf(v) ?: return
                if (v != null) readerWebs.remove(v)
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
                if (sttOn && v != null && v === sttWeb) injectStt(v)
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
            if (ex == null) false
            else when (r.type) {
                WebView.HitTestResult.IMAGE_TYPE -> { showImageMenu(w, ex, null); true }
                WebView.HitTestResult.SRC_IMAGE_ANCHOR_TYPE -> {
                    // extra berisi tautan; alamat gambarnya diminta terpisah
                    val h = Handler(Looper.getMainLooper()) { m ->
                        val img = m.data?.getString("url")
                        if (!img.isNullOrEmpty()) showImageMenu(w, img, ex) else showLinkMenu(ex)
                        true
                    }
                    w.requestImageRef(h.obtainMessage())
                    true
                }
                WebView.HitTestResult.SRC_ANCHOR_TYPE -> { showLinkMenu(ex); true }
                else -> false
            }
        }
        return w
    }

    private fun hideCustom() {
        hideSttSub()
        fsHolder = null
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

        fsLayer = layer; fsRotBtn = rot; fsHolder = holder
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
        if (sttOn) showSttSub()
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

    private val dlLocKeys = arrayOf("alethra", "downloads", "documents", "pictures", "movies", "music")
    private val dlLocNames = arrayOf("Unduhan/Alethra", "Unduhan", "Dokumen", "Gambar", "Film", "Musik")
    private fun dlLocIdx() = dlLocKeys.indexOf(prefs.getString("dlloc", "alethra")).coerceAtLeast(0)

    /** Folder kustom pilihan pengguna (Storage Access Framework), null bila belum dipilih atau izinnya hilang. */
    private fun customDlTree(): Uri? {
        val u = prefs.getString("dltree", null)?.let { Uri.parse(it) } ?: return null
        val ok = contentResolver.persistedUriPermissions.any { it.uri == u && it.isReadPermission && it.isWritePermission }
        return if (ok) u else null
    }
    private fun customDlActive() = prefs.getString("dlloc", "alethra") == "custom" && customDlTree() != null

    private fun customDlLabel(u: Uri): String {
        val id = try { DocumentsContract.getTreeDocumentId(u) } catch (e: Exception) { return "Folder pilihan" }
        val vol = id.substringBefore(':', "")
        val path = id.substringAfter(':', "").trim('/')
        val root = if (vol == "primary") "Penyimpanan internal" else if (vol.isEmpty()) "Penyimpanan" else "Kartu SD ($vol)"
        return if (path.isEmpty()) root else "$root/$path"
    }

    private fun dlLocName(): String =
        if (customDlActive()) customDlLabel(customDlTree()!!) else dlLocNames[dlLocIdx()]

    private fun pickDlDir() {
        val i = Intent(Intent.ACTION_OPEN_DOCUMENT_TREE).addFlags(
            Intent.FLAG_GRANT_READ_URI_PERMISSION or Intent.FLAG_GRANT_WRITE_URI_PERMISSION or
            Intent.FLAG_GRANT_PERSISTABLE_URI_PERMISSION or Intent.FLAG_GRANT_PREFIX_URI_PERMISSION)
        try { skipNextLock = true; startActivityForResult(i, DLDIR_REQ) } catch (e: Exception) { toast("Pemilih folder tidak tersedia") }
    }

    private fun setCustomDlDir(u: Uri) {
        try {
            contentResolver.takePersistableUriPermission(u, Intent.FLAG_GRANT_READ_URI_PERMISSION or Intent.FLAG_GRANT_WRITE_URI_PERMISSION)
        } catch (e: Exception) { toast("Tidak bisa mengakses folder ini, pilih folder lain"); return }
        // lepas izin folder lama agar tidak menumpuk
        prefs.getString("dltree", null)?.let { old ->
            if (old != u.toString()) try {
                contentResolver.releasePersistableUriPermission(Uri.parse(old), Intent.FLAG_GRANT_READ_URI_PERMISSION or Intent.FLAG_GRANT_WRITE_URI_PERMISSION)
            } catch (e: Exception) { }
        }
        prefs.edit().putString("dltree", u.toString()).putString("dlloc", "custom").apply()
        toast("Unduhan disimpan di " + customDlLabel(u))
        renderPage()
    }

    private fun dlTarget(i: Int): Pair<String, String> = when (dlLocKeys[i]) {
        "alethra" -> Pair(Environment.DIRECTORY_DOWNLOADS, "Alethra")
        "documents" -> Pair(Environment.DIRECTORY_DOCUMENTS, "")
        "pictures" -> Pair(Environment.DIRECTORY_PICTURES, "")
        "movies" -> Pair(Environment.DIRECTORY_MOVIES, "")
        "music" -> Pair(Environment.DIRECTORY_MUSIC, "")
        else -> Pair(Environment.DIRECTORY_DOWNLOADS, "")
    }

    /* ---- Unduhan ke folder kustom ----
     * DownloadManager tidak bisa menulis ke folder pilihan (SAF). Berkas diunduh dulu ke folder
     * sementara milik aplikasi (tanpa izin tambahan, progres/jeda/ulangi tetap jalan), lalu disalin
     * ke folder pilihan setelah selesai dan berkas sementaranya dihapus. */

    private val dlLock = Any()
    private val dlCopyPool = java.util.concurrent.Executors.newSingleThreadExecutor()
    private val dlCopying = java.util.Collections.synchronizedSet(HashSet<Long>())
    /** Unduhan Incognito ke folder kustom: hanya di memori, tidak masuk riwayat. id -> (tree, nama) */
    private val incogDl = java.util.concurrent.ConcurrentHashMap<Long, Pair<String, String>>()

    private val dlDoneReceiver = object : android.content.BroadcastReceiver() {
        override fun onReceive(c: Context?, i: Intent?) { finalizeCustomDownloads() }
    }

    private fun dlEntry(id: Long): JSONObject? = synchronized(dlLock) {
        val a = getList("dl")
        for (i in 0 until a.length()) { val o = a.getJSONObject(i); if (o.optLong("id") == id) return o }
        null
    }

    private fun dlUpdate(id: Long, f: (JSONObject) -> Unit) = synchronized(dlLock) {
        val a = getList("dl")
        for (i in 0 until a.length()) { val o = a.getJSONObject(i); if (o.optLong("id") == id) { f(o); break } }
        prefs.edit().putString("dl", a.toString()).apply()
    }

    /** Salin unduhan kustom yang sudah selesai ke folder tujuan. Aman dipanggil berulang. */
    private fun finalizeCustomDownloads() {
        val pending = synchronized(dlLock) {
            val a = getList("dl"); val out = mutableListOf<Pair<Long, String>>()
            for (i in 0 until a.length()) {
                val o = a.getJSONObject(i)
                if (o.optString("tree").isNotEmpty() && o.optString("doc").isEmpty() && !o.optBoolean("err"))
                    out.add(Pair(o.getLong("id"), o.getString("tree")))
            }
            incogDl.forEach { (id, v) -> out.add(Pair(id, v.first)) }
            out
        }
        if (pending.isEmpty()) return
        val dm = getSystemService(DOWNLOAD_SERVICE) as DownloadManager
        for ((id, tree) in pending) {
            val done = try {
                dm.query(DownloadManager.Query().setFilterById(id))?.use { c ->
                    c.moveToFirst() && c.getInt(c.getColumnIndexOrThrow(DownloadManager.COLUMN_STATUS)) == DownloadManager.STATUS_SUCCESSFUL
                } ?: false
            } catch (e: Exception) { false }
            if (!done || !dlCopying.add(id)) continue
            dlCopyPool.execute {
                var okCopy = false
                try { okCopy = copyToTree(dm, id, Uri.parse(tree)) } catch (e: Exception) { }
                if (incogDl.remove(id) != null) { try { dm.remove(id) } catch (e: Exception) { } }
                dlCopying.remove(id)
                runOnUiThread {
                    if (okCopy) toast("Unduhan disimpan di " + dlLocLabelFor(tree))
                    else toast("Gagal menyalin ke folder pilihan")
                    if (pages.lastOrNull() == "downloads") renderPage()
                }
            }
        }
    }

    private fun dlLocLabelFor(tree: String) = customDlLabel(Uri.parse(tree))

    private fun copyToTree(dm: DownloadManager, id: Long, tree: Uri): Boolean {
        val inc = incogDl[id]
        val e = dlEntry(id)
        if (e == null && inc == null) return false
        val name = (inc?.second ?: e!!.optString("t")).ifEmpty { "berkas" }
        fun mark(f: (JSONObject) -> Unit) { if (inc == null) dlUpdate(id, f) }
        var localPath: String? = null
        var mime: String? = null
        dm.query(DownloadManager.Query().setFilterById(id))?.use { c ->
            if (c.moveToFirst()) {
                localPath = c.getString(c.getColumnIndexOrThrow(DownloadManager.COLUMN_LOCAL_URI))?.let { Uri.parse(it).path }
                mime = c.getString(c.getColumnIndexOrThrow(DownloadManager.COLUMN_MEDIA_TYPE))
            }
        }
        val src = localPath?.let { java.io.File(it) }
        if (src == null || !src.exists()) { mark { it.put("err", true) }; return false }
        val parent = DocumentsContract.buildDocumentUriUsingTree(tree, DocumentsContract.getTreeDocumentId(tree))
        val type = mime?.takeIf { it.isNotEmpty() }
            ?: java.net.URLConnection.guessContentTypeFromName(name) ?: "application/octet-stream"
        var doc: Uri? = null
        try {
            doc = DocumentsContract.createDocument(contentResolver, parent, type, name) ?: throw java.io.IOException("gagal membuat berkas")
            contentResolver.openOutputStream(doc!!, "w")!!.use { out -> src.inputStream().use { it.copyTo(out, 64 * 1024) } }
        } catch (ex: Exception) {
            try { doc?.let { DocumentsContract.deleteDocument(contentResolver, it) } } catch (_: Exception) { }
            mark { it.put("err", true) }
            return false
        }
        val finalDoc = doc.toString()
        val size = src.length()
        mark { it.put("doc", finalDoc).put("size", size) }
        try { src.delete(); src.parentFile?.delete() } catch (_: Exception) { }
        return true
    }

    private fun download(url: String, ua: String?, cd: String?, mime: String?, len: Long = -1) {
        if (!url.startsWith("http")) { toast("Jenis unduhan ini belum didukung"); return }
        if (Build.VERSION.SDK_INT < 29 && !customDlActive() && checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE), 9)
            toast("Izinkan penyimpanan lalu coba unduh lagi")
            return
        }
        val name = URLUtil.guessFileName(url, cd, mime)
        askFileName(name, len) { n -> startDownload(url, ua, mime, n) }
    }

    /** Jika "Tanya sebelum mengunduh" aktif, tampilkan jendela ubah nama; jika tidak, langsung lanjut. */
    private fun askFileName(name: String, len: Long, onOk: (String) -> Unit) {
        if (!bool("dlask", false)) { onOk(name); return }
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
                val n = et.text.toString().replace(Regex("[/\\\\:*?\"<>|]"), "_").trim()
                onOk(if (n.isEmpty()) name else n)
            }
            .setNegativeButton("Batal", null).show()
    }

    private fun startDownload(url: String, ua: String?, mime: String?, name: String, referer: String? = null) {
        val dm = getSystemService(DOWNLOAD_SERVICE) as DownloadManager
        val tree = if (customDlActive()) customDlTree() else null
        fun build(dir: String, sub: String, t: Uri?) = DownloadManager.Request(Uri.parse(url)).apply {
            setMimeType(mime)
            addRequestHeader("User-Agent", ua ?: defaultUa)
            if (!referer.isNullOrEmpty() && referer.startsWith("http")) addRequestHeader("Referer", referer)
            cookieMgr(tabs.getOrNull(cur)?.web).getCookie(url)?.let { addRequestHeader("Cookie", it) }
            setTitle(name)
            setNotificationVisibility(
                if (bool("dlprog", false)) DownloadManager.Request.VISIBILITY_VISIBLE
                else DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)
            if (bool("dlwifi", false)) setAllowedNetworkTypes(DownloadManager.Request.NETWORK_WIFI)
            setAllowedOverRoaming(bool("dlroam", true))
            if (t != null) setDestinationInExternalFilesDir(this@MainActivity, Environment.DIRECTORY_DOWNLOADS, "tmp/" + System.nanoTime() + "/" + name)
            else setDestinationInExternalPublicDir(dir, if (sub.isEmpty()) name else "$sub/$name")
        }
        try {
            val (dir, sub) = dlTarget(dlLocIdx())
            var useTree = tree
            val id = try { dm.enqueue(build(dir, sub, tree)) }
                catch (e: Exception) { useTree = null; dm.enqueue(build(Environment.DIRECTORY_DOWNLOADS, "Alethra", null)) }
            if (tabs.getOrNull(cur)?.incog == true) {
                useTree?.let { incogDl[id] = Pair(it.toString(), name) }
                toast("Mengunduh $name"); return
            }
            val a = getList("dl")
            val n = JSONArray()
            val ne = JSONObject().put("id", id).put("t", name).put("ts", System.currentTimeMillis())
            if (useTree != null) ne.put("tree", useTree.toString())
            n.put(ne)
            for (i in 0 until minOf(a.length(), 99)) n.put(a.getJSONObject(i))
            synchronized(dlLock) { prefs.edit().putString("dl", n.toString()).apply() }
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
        val custom = customDlActive()
        val rows = dlLocNames.indices.map { i ->
            listRow(iconTile(R.drawable.ic_file), dlLocNames[i],
                if (i == 0) "Bawaan · folder khusus Alethra di dalam Unduhan" else "Folder " + dlLocNames[i] + " di penyimpanan",
                if (!custom && dlLocIdx() == i) icon(R.drawable.ic_check, 20, cAcc) else null,
                { prefs.edit().putString("dlloc", dlLocKeys[i]).apply(); renderPage() })
        }.toMutableList()
        val tree = customDlTree()
        rows.add(listRow(iconTile(R.drawable.ic_folder),
            if (tree != null) "Folder pilihan" else "Pilih folder lain…",
            if (tree != null) customDlLabel(tree) + " · ketuk untuk ganti" else "Pilih folder mana saja di penyimpanan atau kartu SD",
            if (custom) icon(R.drawable.ic_check, 20, cAcc) else null,
            { pickDlDir() }))
        addCard(body, rows)
        body.addView(tv("Untuk folder pilihan, berkas diunduh dulu ke penyimpanan sementara Alethra lalu disalin ke folder tersebut setelah selesai, jadi butuh ruang kosong sebesar berkasnya.", 13f, cSub, false).apply { setPadding(dp(20), dp(14), dp(20), 0) }, lp(MP, WC))
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
            if (doomed.any { it === splitTab }) closeSplit()
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
        val target = tabs[i]
        // Tampilan terpisah: bila tab yang dipilih sedang di panel 2, panel 2 diisi tab yang tadinya di panel utama
        if (splitOn && splitTab === target) {
            val old = tabs.getOrNull(cur)
            if (old != null && old !== target) splitTab = old else closeSplit()
        }
        if (sttOn && target.web !== sttWeb && !(splitOn && splitTab?.web === sttWeb)) stopStt()
        if (tabs[i] === miniTab) stopMini(false)
        tabs.getOrNull(cur)?.let { if (it !== tabs[i]) captureThumb(it) }
        cur = i
        if (closeAllPages) { pages.clear(); pageView = null }
        container.removeAllViews()
        val w = tabs[i].web
        (w.parent as? ViewGroup)?.removeView(w)
        container.addView(w, FrameLayout.LayoutParams(MP, MP))
        splitSync()
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
        if (splitOn && (tabs[i] === splitTab || i == cur)) closeSplit()
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
        closeSplit()
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

    /* ================= Menu tahan-lama: tautan & gambar ================= */

    inner class SheetItem(val icon: Int, val label: String, val action: () -> Unit)
    inner class Sheet(val dialog: Dialog, val labels: List<TextView>, private val thumb: ImageView, private val thumbIcon: ImageView) {
        fun setLabel(i: Int, t: String) { labels.getOrNull(i)?.text = t }
        fun setThumb(b: Bitmap) { thumbIcon.visibility = View.GONE; thumb.setImageBitmap(b); thumb.visibility = View.VISIBLE }
    }

    /** Lembar aksi dari bawah: kepala (pratinjau + judul + alamat) lalu baris aksi berikon. */
    private fun showSheet(headIcon: Int, title: String, sub: String, items: List<SheetItem>): Sheet {
        val d = Dialog(this, android.R.style.Theme_Material_Dialog_NoActionBar)
        d.requestWindowFeature(Window.FEATURE_NO_TITLE)
        val card = LinearLayout(this).apply { orientation = LinearLayout.VERTICAL; background = shape(cSurf, 24, cLine, 1); setPadding(0, dp(6), 0, dp(10)) }

        val head = LinearLayout(this).apply { orientation = LinearLayout.HORIZONTAL; gravity = Gravity.CENTER_VERTICAL; setPadding(dp(18), dp(14), dp(18), dp(14)) }
        val thumbBox = FrameLayout(this).apply {
            background = shape(cLine, 14)
            outlineProvider = android.view.ViewOutlineProvider.BACKGROUND; clipToOutline = true
        }
        val ti = icon(headIcon, 24, cSub)
        thumbBox.addView(ti, FrameLayout.LayoutParams(dp(24), dp(24), Gravity.CENTER))
        val th = ImageView(this).apply { scaleType = ImageView.ScaleType.CENTER_CROP; visibility = View.GONE }
        thumbBox.addView(th, FrameLayout.LayoutParams(MP, MP))
        head.addView(thumbBox, lp(dp(56), dp(56)))
        val col = LinearLayout(this).apply { orientation = LinearLayout.VERTICAL }
        col.addView(tv(title, 16f, cFg, true).apply { maxLines = 1; ellipsize = TextUtils.TruncateAt.END })
        col.addView(tv(sub, 12.5f, cSub).apply { maxLines = 2; ellipsize = TextUtils.TruncateAt.END; setPadding(0, dp(3), 0, 0) })
        head.addView(col, lp(0, WC, 1f).apply { leftMargin = dp(14) })
        card.addView(head, lp(MP, WC))
        card.addView(View(this).apply { setBackgroundColor(cLine) }, lp(MP, 1).apply { leftMargin = dp(18); rightMargin = dp(18); bottomMargin = dp(4) })

        val labels = ArrayList<TextView>()
        items.forEach { it0 ->
            val row = LinearLayout(this).apply {
                orientation = LinearLayout.HORIZONTAL; gravity = Gravity.CENTER_VERTICAL
                setPadding(dp(18), dp(9), dp(18), dp(9)); background = pressBg(); isClickable = true
                setOnClickListener { d.dismiss(); it0.action() }
            }
            row.addView(iconTile(it0.icon), lp(dp(40), dp(40)))
            val lt = tv(it0.label, 15f, cFg, true)
            labels.add(lt)
            row.addView(lt, lp(0, WC, 1f).apply { leftMargin = dp(14) })
            card.addView(row, lp(MP, WC))
        }
        val sv = object : ScrollView(this) {
            override fun onMeasure(w: Int, h: Int) {
                super.onMeasure(w, MeasureSpec.makeMeasureSpec((resources.displayMetrics.heightPixels * 0.85f).toInt(), MeasureSpec.AT_MOST))
            }
        }.apply { isVerticalScrollBarEnabled = false; addView(card) }
        d.setContentView(sv, ViewGroup.LayoutParams(MP, WC))
        d.window?.apply {
            setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            setLayout(minOf(resources.displayMetrics.widthPixels - dp(24), dp(440)), android.view.WindowManager.LayoutParams.WRAP_CONTENT)
            setGravity(Gravity.BOTTOM)
            attributes = attributes.apply { y = dp(12) }
            setWindowAnimations(android.R.style.Animation_InputMethod)
            addFlags(android.view.WindowManager.LayoutParams.FLAG_DIM_BEHIND)
            setDimAmount(0.5f)
        }
        d.show()
        return Sheet(d, labels, th, ti)
    }

    private fun copyAddress(u: String) {
        (getSystemService(CLIPBOARD_SERVICE) as ClipboardManager).setPrimaryClip(ClipData.newPlainText("url", u))
        toast("Alamat disalin")
    }

    private fun showLinkMenu(u: String) {
        showSheet(R.drawable.ic_globe, displayHost(u).ifEmpty { "Tautan" }, u, listOf(
            SheetItem(R.drawable.ic_tab, "Buka di tab baru") { newTab(u) },
            SheetItem(R.drawable.ic_copy, "Salin alamat") { copyAddress(u) },
            SheetItem(R.drawable.ic_share, "Bagikan") { share(u) }))
    }

    class ImgFmt(var ext: String?, var verified: Boolean)

    private fun extFromMime(m: String?): String? = when (m?.lowercase()?.trim()) {
        "image/jpeg", "image/jpg", "image/pjpeg" -> "jpg"
        "image/png" -> "png"
        "image/svg+xml" -> "svg"
        "image/gif" -> "gif"
        "image/webp" -> "webp"
        "image/avif" -> "avif"
        "image/bmp" -> "bmp"
        "image/x-icon", "image/vnd.microsoft.icon" -> "ico"
        else -> null
    }

    private fun extFromUrl(u: String): String? {
        val seg = try { Uri.parse(u).lastPathSegment ?: "" } catch (e: Exception) { "" }
        return when (seg.substringAfterLast('.', "").lowercase()) {
            "jpg", "jpeg", "jpe", "jfif" -> "jpg"
            "png" -> "png"; "svg" -> "svg"; "gif" -> "gif"; "webp" -> "webp"; "avif" -> "avif"; "bmp" -> "bmp"; "ico" -> "ico"
            else -> null
        }
    }

    private fun mimeForExt(e: String?): String = when (e) {
        "jpg" -> "image/jpeg"; "png" -> "image/png"; "svg" -> "image/svg+xml"; "gif" -> "image/gif"
        "webp" -> "image/webp"; "avif" -> "image/avif"; "bmp" -> "image/bmp"; "ico" -> "image/x-icon"
        else -> "application/octet-stream"
    }

    /** Cek format sebenarnya dari isi berkas (tanda awal berkas), baru jatuh ke tipe MIME. */
    private fun sniffImg(b: ByteArray, n: Int, mime: String?): String? {
        fun at(i: Int) = if (i < n) b[i].toInt() and 0xFF else -1
        fun str(o: Int, l: Int) = if (o + l <= n) String(b, o, l, Charsets.ISO_8859_1) else ""
        if (at(0) == 0xFF && at(1) == 0xD8) return "jpg"
        if (at(0) == 0x89 && at(1) == 0x50 && at(2) == 0x4E && at(3) == 0x47) return "png"
        if (str(0, 3) == "GIF") return "gif"
        if (str(0, 4) == "RIFF" && str(8, 4) == "WEBP") return "webp"
        if (str(4, 4) == "ftyp" && (str(8, 4) == "avif" || str(8, 4) == "avis")) return "avif"
        if (at(0) == 0x42 && at(1) == 0x4D) return "bmp"
        if (at(0) == 0 && at(1) == 0 && at(2) == 1 && at(3) == 0) return "ico"
        val head = String(b, 0, minOf(n, 600), Charsets.UTF_8).trimStart('\uFEFF', ' ', '\n', '\r', '\t').lowercase()
        if (head.startsWith("<svg") || ((head.startsWith("<?xml") || head.startsWith("<!doctype svg") || head.startsWith("<!--")) && head.contains("<svg"))) return "svg"
        return extFromMime(mime)
    }

    private fun thumbOf(b: ByteArray): Bitmap? = try {
        val o = BitmapFactory.Options().apply { inJustDecodeBounds = true }
        BitmapFactory.decodeByteArray(b, 0, b.size, o)
        var s = 1
        while (maxOf(o.outWidth, o.outHeight) / s > 320) s *= 2
        BitmapFactory.decodeByteArray(b, 0, b.size, BitmapFactory.Options().apply { inSampleSize = s })
    } catch (e: Throwable) { null }

    private fun decodeDataUri(u: String): Pair<ByteArray, String?>? {
        val c = u.indexOf(',')
        if (c < 0) return null
        val head = u.substring(5, c)
        val body = u.substring(c + 1)
        val mime = head.substringBefore(';').ifEmpty { null }
        val bytes = if (head.contains(";base64", true)) Base64.decode(body, Base64.DEFAULT)
            else Uri.decode(body).toByteArray(Charsets.UTF_8)
        return Pair(bytes, mime)
    }

    /** Unduh sebagian gambar sekali saja untuk mengecek format dan membuat pratinjau. Jalankan di thread latar. */
    private fun probeImage(url: String, ua: String?, referer: String?, cookie: String?): Pair<String?, Bitmap?> {
        var c: java.net.HttpURLConnection? = null
        try {
            c = java.net.URL(url).openConnection() as java.net.HttpURLConnection
            c.connectTimeout = 5000; c.readTimeout = 5000; c.instanceFollowRedirects = true
            c.setRequestProperty("User-Agent", ua ?: defaultUa)
            if (!referer.isNullOrEmpty() && referer.startsWith("http")) c.setRequestProperty("Referer", referer)
            if (!cookie.isNullOrEmpty()) c.setRequestProperty("Cookie", cookie)
            if (c.responseCode !in 200..299) return Pair(null, null)
            val mime = c.contentType?.substringBefore(';')?.trim()
            val out = java.io.ByteArrayOutputStream()
            val buf = ByteArray(16384)
            var tot = 0
            c.inputStream.use { ins ->
                while (tot < 3_000_000) { val n = ins.read(buf); if (n < 0) break; out.write(buf, 0, n); tot += n }
            }
            val b = out.toByteArray()
            val fmt = sniffImg(b, b.size, mime)
            return Pair(fmt, if (fmt != null && fmt != "svg") thumbOf(b) else null)
        } catch (e: Exception) {
            return Pair(null, null)
        } finally { try { c?.disconnect() } catch (_: Exception) { } }
    }

    private val blobWait = HashMap<Int, (String?) -> Unit>()
    private var blobSeq = 0

    /** Ambil byte gambar dari data: atau blob: (callback berjalan di thread latar). */
    private fun imageBytes(w: WebView, src: String, cb: (ByteArray, String?) -> Unit) {
        if (src.startsWith("data:", true)) {
            Thread {
                val r = try { decodeDataUri(src) } catch (e: Exception) { null }
                if (r == null) runOnUiThread { toast("Gambar tidak bisa dibaca") } else cb(r.first, r.second)
            }.start()
        } else if (src.startsWith("blob:")) {
            val id = ++blobSeq
            blobWait[id] = { data ->
                if (data == null || !data.startsWith("data:")) toast("Gambar tidak bisa dibaca")
                else imageBytes(w, data, cb)
            }
            w.evaluateJavascript("(function(u,id){fetch(u).then(function(r){return r.blob()}).then(function(b){var f=new FileReader();f.onload=function(){window.AlApp.blobData(id,String(f.result))};f.onerror=function(){window.AlApp.blobData(id,'')};f.readAsDataURL(b)}).catch(function(){window.AlApp.blobData(id,'')})})(" +
                org.json.JSONObject.quote(src) + "," + id + ")", null)
        } else toast("Jenis gambar ini belum didukung")
    }

    private fun stamp() = java.text.SimpleDateFormat("yyyyMMdd_HHmmss", java.util.Locale.US).format(java.util.Date())

    private fun imgFileName(url: String, ext: String): String {
        val seg = Uri.decode(try { Uri.parse(url).lastPathSegment ?: "" } catch (e: Exception) { "" })
        var base = if (extFromUrl(url) != null) seg.substringBeforeLast('.') else seg
        base = base.replace(Regex("[/\\\\:*?\"<>|\\s]+"), "_").trim('_', '.').take(80)
        if (base.isEmpty()) base = "gambar_" + stamp()
        return "$base.$ext"
    }

    /** Tulis byte ke lokasi simpan pilihan pengguna. Mengembalikan nama lokasi, atau null jika gagal. */
    private fun saveBytes(name: String, mime: String, data: ByteArray): String? {
        try {
            val tree = if (customDlActive()) customDlTree() else null
            if (tree != null) {
                val parent = DocumentsContract.buildDocumentUriUsingTree(tree, DocumentsContract.getTreeDocumentId(tree))
                val doc = DocumentsContract.createDocument(contentResolver, parent, mime, name) ?: return null
                contentResolver.openOutputStream(doc, "w")?.use { it.write(data) } ?: return null
                return customDlLabel(tree)
            }
            val (dir0, sub) = dlTarget(dlLocIdx())
            // gambar tidak boleh masuk Film/Musik di penyimpanan Android baru, pindahkan ke Gambar
            val dir = if (dir0 == Environment.DIRECTORY_MOVIES || dir0 == Environment.DIRECTORY_MUSIC) Environment.DIRECTORY_PICTURES else dir0
            if (Build.VERSION.SDK_INT >= 29) {
                val rel = if (sub.isEmpty()) dir else "$dir/$sub"
                val coll = when (dir) {
                    Environment.DIRECTORY_DOWNLOADS -> android.provider.MediaStore.Downloads.EXTERNAL_CONTENT_URI
                    Environment.DIRECTORY_PICTURES -> android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI
                    else -> android.provider.MediaStore.Files.getContentUri("external")
                }
                val cv = android.content.ContentValues().apply {
                    put(android.provider.MediaStore.MediaColumns.DISPLAY_NAME, name)
                    put(android.provider.MediaStore.MediaColumns.MIME_TYPE, mime)
                    put(android.provider.MediaStore.MediaColumns.RELATIVE_PATH, rel)
                    put(android.provider.MediaStore.MediaColumns.IS_PENDING, 1)
                }
                val uri = contentResolver.insert(coll, cv) ?: return null
                try { contentResolver.openOutputStream(uri)!!.use { it.write(data) } }
                catch (e: Exception) { try { contentResolver.delete(uri, null, null) } catch (_: Exception) { }; return null }
                contentResolver.update(uri, android.content.ContentValues().apply {
                    put(android.provider.MediaStore.MediaColumns.IS_PENDING, 0) }, null, null)
            } else {
                val folder = java.io.File(Environment.getExternalStoragePublicDirectory(dir), sub).apply { mkdirs() }
                var f = java.io.File(folder, name)
                var n = 1
                while (f.exists()) { f = java.io.File(folder, name.substringBeforeLast('.') + " ($n)." + name.substringAfterLast('.')); n++ }
                f.writeBytes(data)
                android.media.MediaScannerConnection.scanFile(this, arrayOf(f.absolutePath), arrayOf(mime), null)
            }
            return if (dir0 != dir) "Gambar" else dlLocName()
        } catch (e: Exception) { return null }
    }

    private fun saveImage(w: WebView, src: String, fmt: ImgFmt) {
        if (Build.VERSION.SDK_INT < 29 && !customDlActive() && checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE), 9)
            toast("Izinkan penyimpanan lalu coba simpan lagi")
            return
        }
        if (src.startsWith("http", true)) {
            val ua = w.settings.userAgentString
            val ref = w.url
            val ck = try { cookieMgr(w).getCookie(src) } catch (e: Exception) { null }
            Thread {
                val ext = (if (fmt.verified) fmt.ext else null) ?: probeImage(src, ua, ref, ck).first ?: fmt.ext ?: "jpg"
                runOnUiThread { askFileName(imgFileName(src, ext), -1) { n -> startDownload(src, ua, mimeForExt(ext), n, ref) } }
            }.start()
        } else {
            imageBytes(w, src) { bytes, mime ->
                val ext = sniffImg(bytes, bytes.size, mime) ?: "png"
                val name = "gambar_" + stamp() + "." + ext
                runOnUiThread {
                    askFileName(name, bytes.size.toLong()) { n ->
                        Thread {
                            val where = saveBytes(n, mimeForExt(ext), bytes)
                            runOnUiThread { toast(if (where != null) "Gambar disimpan di $where" else "Gagal menyimpan gambar") }
                        }.start()
                    }
                }
            }
        }
    }

    private fun shareImage(w: WebView, src: String) {
        if (src.startsWith("http", true)) { share(src); return }
        imageBytes(w, src) { bytes, mime ->
            try {
                val ext = sniffImg(bytes, bytes.size, mime) ?: "png"
                val dir = java.io.File(cacheDir, "share").apply { mkdirs() }
                dir.listFiles()?.forEach { try { it.delete() } catch (e: Exception) {} }
                val out = java.io.File(dir, "gambar.$ext").apply { writeBytes(bytes) }
                val uri = androidx.core.content.FileProvider.getUriForFile(this, "$packageName.files", out)
                val i = Intent(Intent.ACTION_SEND).setType(mimeForExt(ext))
                    .putExtra(Intent.EXTRA_STREAM, uri).addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                runOnUiThread { skipNextLock = true; startActivity(Intent.createChooser(i, "Bagikan")) }
            } catch (e: Exception) { runOnUiThread { toast("Tidak bisa membagikan gambar ini") } }
        }
    }

    private fun showImageMenu(w: WebView, src: String, link: String?) {
        val isData = src.startsWith("data:", true)
        val isBlob = src.startsWith("blob:")
        val isHttp = src.startsWith("http", true)
        val mime0 = if (isData) src.substring(5).substringBefore(',').substringBefore(';') else null
        val fmt = ImgFmt(if (isData) extFromMime(mime0) else if (isHttp) extFromUrl(src) else null, false)
        fun saveLabel(e: String?) = if (e == null) "Simpan gambar" else "Simpan gambar ke " + e.uppercase()

        val title = when { isData -> "Gambar tertanam"; isBlob -> "Gambar dari halaman"; else -> displayHost(src).ifEmpty { "Gambar" } }
        val sub = if (isData) (mime0?.takeIf { it.isNotEmpty() } ?: "image") + " · base64 · ≈" + Formatter.formatShortFileSize(this, src.length * 3L / 4) else src

        val items = mutableListOf(
            SheetItem(R.drawable.ic_tab, "Buka di tab baru") { newTab(src) },
            SheetItem(R.drawable.ic_copy, "Salin alamat") { copyAddress(src) },
            SheetItem(R.drawable.ic_share, "Bagikan") { shareImage(w, src) },
            SheetItem(R.drawable.ic_download, saveLabel(fmt.ext)) { saveImage(w, src, fmt) })
        if (link != null) items.add(SheetItem(R.drawable.ic_globe, "Buka tautan di tab baru") { newTab(link) })
        val sheet = showSheet(R.drawable.ic_image, title, sub, items)

        val ua = w.settings.userAgentString
        val ref = w.url
        val ck = if (isHttp) try { cookieMgr(w).getCookie(src) } catch (e: Exception) { null } else null
        if (isData || isHttp) Thread {
            val r: Pair<String?, Bitmap?> = if (isData) {
                val d = try { decodeDataUri(src) } catch (e: Exception) { null }
                if (d == null) Pair(null, null) else Pair(sniffImg(d.first, d.first.size, d.second), thumbOf(d.first))
            } else probeImage(src, ua, ref, ck)
            runOnUiThread {
                if (r.first != null) { fmt.ext = r.first; fmt.verified = true; sheet.setLabel(3, saveLabel(r.first)) }
                if (r.second != null && sheet.dialog.isShowing) sheet.setThumb(r.second!!)
            }
        }.start()
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

    /* ---------- Kelola izin situs ---------- */

    private val permDefs = listOf("cam" to "Kamera", "mic" to "Mikrofon", "loc" to "Lokasi", "notif" to "Notifikasi")

    private fun sitesWithPerms(): List<String> = siteHosts().filter { h -> permDefs.any { (k, _) -> sitePerm(h, k) != 0 } }

    private fun sitePermSummary(h: String): String =
        permDefs.filter { (k, _) -> sitePerm(h, k) != 0 }
            .joinToString(" · ") { (k, l) -> l + ": " + if (sitePerm(h, k) == 1) "boleh" else "blokir" }

    /** Ubah izin satu situs: Tanya / Izinkan / Blokir untuk tiap jenis izin. Perubahan langsung tersimpan. */
    private fun sitePermDialog(h: String, after: () -> Unit) {
        val box = LinearLayout(this).apply { orientation = LinearLayout.VERTICAL; setPadding(0, 0, 0, dp(4)) }
        fun build() {
            box.removeAllViews()
            permDefs.forEach { (k, label) ->
                box.addView(tv(label, 14f, cSub, true), lp(MP, WC).apply { setMargins(dp(20), dp(10), dp(20), 0) })
                val sel = when (sitePerm(h, k)) { 1 -> 1; 2 -> 2; else -> 0 }
                box.addView(chips(listOf("Tanya", "Izinkan", "Blokir"), sel) { i ->
                    sitePut(h, k, if (i == 0) null else i)
                    build()
                }, lp(MP, WC))
            }
        }
        build()
        dlg().setTitle(h).setView(box)
            .setPositiveButton("Selesai", null)
            .setNeutralButton("Atur ulang") { _, _ -> permDefs.forEach { (k, _) -> sitePut(h, k, null) } }
            .setOnDismissListener { after() }
            .show()
    }

    private fun pageSites(): View {
        val reset = actBtn(R.drawable.ic_trash, "Hapus semua izin") { _ -> resetSitePerms { renderPage() } }
        val (page, body) = shell("Izin situs", true, listOf(reset))
        val hosts = sitesWithPerms()
        if (hosts.isEmpty()) {
            body.addView(emptyState("Belum ada izin situs yang tersimpan.\nSitus muncul di sini setelah kamu mengizinkan atau memblokir kamera, mikrofon, lokasi, atau notifikasi."))
            return page
        }
        body.addView(tv(hosts.size.toString() + " situs · ketuk untuk mengubah izin", 12f, cSub).apply { setPadding(dp(20), dp(8), dp(20), dp(6)) })
        val list = LinearLayout(this).apply { orientation = LinearLayout.VERTICAL }
        hosts.forEach { h ->
            list.addView(listRow(favTile(h, "https://$h", null, cSurf), h, sitePermSummary(h), icon(R.drawable.ic_chevron, 18, cSub),
                { sitePermDialog(h) { renderPage() } }), lp(MP, WC))
        }
        body.addView(list, lp(MP, WC))
        return page
    }

    private fun resetSitePerms(after: () -> Unit = {}) {
        dlg().setMessage("Hapus semua izin situs yang tersimpan (kamera, mikrofon, lokasi, notifikasi)?")
            .setPositiveButton("Hapus") { _, _ ->
                prefs.edit().remove("siteprefs").apply()
                siteCache = null
                incogSites = JSONObject()
                toast("Izin situs dihapus")
                after()
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

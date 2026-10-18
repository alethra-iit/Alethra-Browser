(function () {
  if (window.__alNotif) return;
  window.__alNotif = 1;
  var cbs = {}, seq = 0;
  function bridge() { return window.AlApp; }
  function state() {
    try { var b = bridge(); return b && b.notifState ? String(b.notifState()) : 'denied'; } catch (e) { return 'denied'; }
  }
  function N(title, opts) {
    if (!(this instanceof N)) throw new TypeError("Failed to construct 'Notification': Please use the 'new' operator.");
    opts = opts || {};
    this.title = String(title);
    this.body = opts.body ? String(opts.body) : '';
    this.tag = opts.tag ? String(opts.tag) : '';
    this.data = opts.data;
    this.onclick = this.onclose = this.onerror = this.onshow = null;
    var self = this;
    if (state() === 'granted') {
      try { bridge().notify(this.title, this.body, this.tag); } catch (e) {}
      setTimeout(function () { if (typeof self.onshow === 'function') try { self.onshow(); } catch (e) {} }, 0);
    } else {
      setTimeout(function () { if (typeof self.onerror === 'function') try { self.onerror(); } catch (e) {} }, 0);
    }
  }
  N.prototype.close = function () {};
  N.prototype.addEventListener = function () {};
  N.prototype.removeEventListener = function () {};
  Object.defineProperty(N, 'permission', { get: function () { return state(); } });
  N.maxActions = 0;
  N.requestPermission = function (cb) {
    return new Promise(function (resolve) {
      var id = ++seq;
      cbs[id] = function (s) { if (typeof cb === 'function') { try { cb(s); } catch (e) {} } resolve(s); };
      try { bridge().notifRequest(id); } catch (e) { delete cbs[id]; resolve('denied'); }
    });
  };
  window.__alNotifRes = function (id, s) { var f = cbs[id]; if (f) { delete cbs[id]; f(s); } };
  try { Object.defineProperty(window, 'Notification', { value: N, configurable: true, writable: true }); }
  catch (e) { window.Notification = N; }
})();

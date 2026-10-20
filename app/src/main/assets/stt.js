(function(){
var C=window.__alSttCfg||{},B=window.AlSTT;
if(!B||!C.tok)return;
if(window.__alSTT){try{window.__alSTT.stop()}catch(e){}}
var tok=C.tok,RATE=16000;
// Preset pemotongan (ms): sil = jeda yang menutup potongan, soft = setelah durasi ini jeda pendek sudah cukup,
// max = batas paksa, min = potongan lebih pendek dari ini menunggu jeda lebih lama (hemat permintaan)
var PRE={fast:{sil:300,soft:1700,max:3800,min:800},bal:{sil:420,soft:2600,max:5500,min:1100},eco:{sil:650,soft:4200,max:8500,min:1800}};
// Mode offline (pengenal di perangkat): potongan lebih panjang karena model lokal butuh kalimat utuh supaya akurat
// (terutama Jepang yang jarang berjeda); pemotongan paksa dicari di titik paling senyap, bukan di tengah kata.
var PRO={fast:{sil:350,soft:3200,max:9000,min:1200},bal:{sil:450,soft:4500,max:12000,min:1500},eco:{sil:650,soft:6500,max:15000,min:2000}};
var PS=C.off?PRO:PRE,P=PS[C.mode]||PS.bal;
var alive=true,ac=null,curV=null,ms=null,src=null,node=null,proc=null,mute=null,poll=null,wd=null;
var lastInt=0,vbuf=[],vlen=0,vEnd=true;
var nf=0.004,pre=[],preLen=0,seg=null,segLen=0,voiced=0,sil=0,zeroMs=0,quietMs=0,lastSt='',ready=false;
// wk: 0 = belum dicoba, 1 = AudioWorklet sedang dimuat, 2 = siap, -1 = tidak bisa (pakai ScriptProcessor)
var wk=0;
// AudioWorklet berjalan di thread audio, bukan thread utama halaman. Ia menampung ±4096 sampel mono dan
// mengirimnya apa adanya; penurunan ke 16 kHz dilakukan oleh resampler berkualitas (lihat mkRs).
var WK='class P extends AudioWorkletProcessor{constructor(){super();this.n=4096;this.b=new Float32Array(this.n);this.i=0}'+
'process(inp){var c=inp[0]&&inp[0][0];if(!c)return true;for(var k=0;k<c.length;k++){this.b[this.i++]=c[k];if(this.i===this.n)this.emit()}return true}'+
'emit(){var o=new Float32Array(this.b);this.i=0;this.port.postMessage(o,[o.buffer])}}'+
'registerProcessor("al-stt",P);';
function st(m){if(m===lastSt)return;lastSt=m;try{B.status(tok,m)}catch(e){}}
function pick(){
  var v=document.querySelectorAll('video,audio'),i;
  for(i=0;i<v.length;i++){if(!v[i].paused&&!v[i].ended&&v[i].readyState>2)return v[i]}
  return null;
}
function detach(){
  try{if(node){node.port.onmessage=null;node.disconnect()}}catch(e){}
  try{if(proc){proc.onaudioprocess=null;proc.disconnect()}}catch(e){}
  node=null;proc=null;
}
function teardown(){
  detach();
  try{if(src)src.disconnect()}catch(e){}
  try{if(mute)mute.disconnect()}catch(e){}
  src=null;mute=null;ms=null;curV=null;ready=false;
}
var noRate=false,rs=null,hpx=0,hpy=0;
function mkAc(){
  var AC=window.AudioContext||window.webkitAudioContext;
  ac=null;
  // konteks 16 kHz: browser menurunkan sample rate dengan filter sinc yang bagus (lebih baik dari rata-rata jendela)
  if(!noRate){try{ac=new AC({sampleRate:RATE})}catch(e){ac=null}}
  if(!ac)ac=new AC();
  rs=mkRs(ac.sampleRate);hpx=0;hpy=0;
  wk=-1;
  try{
    if(ac.audioWorklet&&window.AudioWorkletNode&&window.Blob&&window.URL&&URL.createObjectURL){
      var u=URL.createObjectURL(new Blob([WK],{type:'application/javascript'}));
      wk=1;
      var fin=function(ok){
        if(wk!==1)return;
        wk=ok?2:-1;
        try{URL.revokeObjectURL(u)}catch(e){}
        if(alive&&src)attach();
      };
      // situs dengan CSP ketat bisa menolak modul blob: -> otomatis pakai ScriptProcessor
      ac.audioWorklet.addModule(u).then(function(){fin(true)},function(){fin(false)});
      setTimeout(function(){fin(false)},2500);
    }
  }catch(e){wk=-1}
}
// sambungkan penangkap audio ke grafik: AudioWorklet bila ada, jika tidak ScriptProcessor
function attach(){
  if(!src||!mute||node||proc)return;
  if(wk===1)return; // menunggu modul selesai dimuat; fin() memanggil attach() lagi
  if(wk===2){
    try{
      node=new AudioWorkletNode(ac,'al-stt',{numberOfInputs:1,numberOfOutputs:1,outputChannelCounts:[1],channelCount:1,channelCountMode:'explicit'});
      node.port.onmessage=function(e){if(alive&&e.data)onRaw(e.data)};
      src.connect(node);node.connect(mute);
      return;
    }catch(e){detach();wk=-1}
  }
  try{
    proc=ac.createScriptProcessor(4096,1,1);
    proc.onaudioprocess=function(e){if(alive)onRaw(new Float32Array(e.inputBuffer.getChannelData(0)))};
    src.connect(proc);proc.connect(mute);
  }catch(e){detach();src=null;ready=false}
}
function setup(v){
  teardown();
  var s=null;
  try{s=v.captureStream?v.captureStream():(v.mozCaptureStream?v.mozCaptureStream():null)}catch(e){}
  if(!s)return false;
  var t=s.getAudioTracks();
  if(!t.length)return false;
  try{
    ms=new MediaStream(t);
    if(!ac)mkAc();
    if(ac.state==='suspended'){try{ac.resume()}catch(e){}}
    try{src=ac.createMediaStreamSource(ms)}
    catch(e2){
      // sebagian WebView menolak sumber dengan sample rate berbeda: pakai konteks bawaan dan resampler sendiri
      if(ac.sampleRate===RATE&&!noRate){noRate=true;try{ac.close()}catch(e3){}ac=null;mkAc();src=ac.createMediaStreamSource(ms)}
      else throw e2;
    }
    mute=ac.createGain();mute.gain.value=0;
    mute.connect(ac.destination);
  }catch(e){teardown();return false}
  curV=v;ready=true;seg=null;pre=[];preLen=0;sil=0;voiced=0;zeroMs=0;
  attach();
  if(!src){teardown();return false}
  return true;
}
// Resampler streaming ke 16 kHz: low-pass windowed-sinc (jendela Hann, 8 persilangan nol) dengan status antarblok,
// jadi tidak ada aliasing di konsonan (fricative Jepang: s, sh, h) dan tidak ada patahan di batas blok.
function mkRs(sr){
  if(Math.abs(sr-RATE)<1)return {push:function(x){return x}};
  var step=sr/RATE,fc=0.46/step,L=Math.ceil(8/(2*fc)),PH=128,ph,k,d,u,t,w,sum,tab=[];
  for(ph=0;ph<PH;ph++){
    var row=new Float32Array(2*L);sum=0;
    for(k=0;k<2*L;k++){
      d=k-L+1-ph/PH;u=2*fc*d;
      t=Math.abs(u)<1e-9?1:Math.sin(Math.PI*u)/(Math.PI*u);
      w=Math.abs(d)<L?0.5*(1+Math.cos(Math.PI*d/L)):0;
      row[k]=t*w;sum+=row[k];
    }
    for(k=0;k<2*L;k++)row[k]/=sum;
    tab.push(row);
  }
  var buf=new Float32Array(0),pos=0;
  return {push:function(x){
    var a=new Float32Array(buf.length+x.length);a.set(buf,0);a.set(x,buf.length);
    var out=[],ip,fr,p,row,y,kk,idx;
    while(true){
      ip=Math.floor(pos);fr=pos-ip;p=Math.round(fr*PH);if(p===PH){p=0;ip++}
      if(ip+L>=a.length)break;
      row=tab[p];y=0;
      for(kk=0;kk<2*L;kk++){idx=ip-L+1+kk;if(idx>=0)y+=row[kk]*a[idx]}
      out.push(y);pos+=step;
    }
    var drop=Math.max(0,Math.floor(pos)-L);
    buf=a.slice(drop);pos-=drop;
    return new Float32Array(out);
  }};
}
// sampel mentah (laju bawaan konteks) -> 16 kHz mono -> high-pass 80 Hz (buang gemuruh) -> deteksi ucapan
function onRaw(x){
  if(!alive||!rs)return;
  var y=rs.push(x),i,v,o=new Float32Array(y.length),a=0.9693;
  for(i=0;i<y.length;i++){v=y[i];hpy=a*(hpy+v-hpx);hpx=v;o[i]=hpy}
  if(!o.length)return;
  if(C.vosk)vq(o);else onPcm(o);
}
// Mode Vosk: tanpa pemotongan kalimat. PCM 16-bit 16 kHz dikirim terus-menerus (±200 ms per kiriman) dan
// Vosk menentukan sendiri batas kalimatnya; hasil sementara keluar kata per kata.
function vq(o){
  var i,mx=0,a;
  for(i=0;i<o.length;i++){a=o[i];if(a<0)a=-a;if(a>mx)mx=a}
  if(mx===0){zeroMs+=o.length*1000/RATE;if(zeroMs>5000)st('Audio video ini tidak bisa ditangkap (dilindungi atau lintas-situs)')}else zeroMs=0;
  vbuf.push(o);vlen+=o.length;
  if(vlen>=3200)vsend();
}
function vsend(){
  if(!vlen)return;
  var all=new Float32Array(vlen),o=0,i,s;
  for(i=0;i<vbuf.length;i++){all.set(vbuf[i],o);o+=vbuf[i].length}
  vbuf=[];vlen=0;
  var u=new Uint8Array(all.length*2),d=new DataView(u.buffer);
  for(i=0;i<all.length;i++){s=Math.max(-1,Math.min(1,all[i]));d.setInt16(i*2,s<0?s*0x8000:s*0x7FFF,true)}
  vEnd=false;
  try{B.pcm(tok,b64(u))}catch(e){}
}
// video dijeda / berganti: selesaikan kalimat yang sedang berjalan
function vflush(){
  if(!C.vosk)return;
  vsend();
  if(!vEnd){vEnd=true;try{B.pcmEnd(tok)}catch(e){}}
}
// x: sampel mono 16 kHz
function onPcm(x){
  if(!alive)return;
  var i,r=0,mx=0,a;
  for(i=0;i<x.length;i++){a=x[i];r+=a*a;if(a<0)a=-a;if(a>mx)mx=a}
  r=Math.sqrt(r/x.length);
  var fms=x.length*1000/RATE;
  // video yang dilindungi/lintas-situs menghasilkan nol mutlak
  if(mx===0){zeroMs+=fms;if(zeroMs>5000)st('Audio video ini tidak bisa ditangkap (dilindungi atau lintas-situs)')}else zeroMs=0;
  var thr=Math.max(0.010,nf*2.8),speech=r>(seg?thr*0.7:thr); // histeresis: akhir kata yang pelan tidak ikut terpotong
  if(!speech)nf=Math.min(0.05,Math.max(0.001,nf*0.96+r*0.04));
  if(!seg){
    pre.push(x);preLen+=x.length;
    while(preLen>RATE*0.3&&pre.length>1){preLen-=pre.shift().length}
    if(speech){seg=pre;segLen=preLen;pre=[];preLen=0;voiced=fms;sil=0;quietMs=0;st('Mendengarkan…')}
    else{quietMs+=fms;if(quietMs>1500&&zeroMs<5000)st(quietMs>15000?'Tidak ada ucapan terdeteksi':'Menunggu ucapan…')}
    return;
  }
  seg.push(x);segLen+=x.length;
  if(speech){voiced+=fms;sil=0;if(C.live)interim()}else sil+=fms;
  var dur=segLen*1000/RATE;
  var need=dur<P.min?P.sil*2.2:(dur>=P.soft?P.sil*0.5:P.sil);
  if(dur>=P.max)flush(x,true);else if(sil>=need)flush(x,false);
}
// Teks langsung: selama kalimat masih berjalan, cuplikan sejauh ini dikirim berkala (hasil sementara, bukan final).
// Kotlin hanya menerima bila pengenal sedang menganggur, jadi tidak ada antrean yang menumpuk.
function interim(){
  if(!seg||voiced<350||segLen<RATE*0.7)return;
  var n=Date.now();
  if(n-lastInt<600)return;
  lastInt=n;
  try{if(!B.canPartial(tok))return}catch(e){return}
  var all=new Float32Array(segLen),o=0,i;
  for(i=0;i<seg.length;i++){all.set(seg[i],o);o+=seg[i].length}
  send(all,true);
}
// titik paling senyap di separuh akhir potongan (jendela 20 ms); dipakai untuk pemotongan paksa
function quietPoint(all){
  var len=all.length,from=Math.floor(len*0.5),to=len-Math.floor(RATE*0.15),best=-1,bv=1e9,i,k,e,d;
  for(i=from;i+320<=to;i+=160){
    e=0;for(k=0;k<320;k++){d=all[i+k];e+=d*d}
    if(e<bv){bv=e;best=i+160}
  }
  return best;
}
function flush(last,forced){
  lastInt=Date.now();
  var chunks=seg,len=segLen,v=voiced;
  seg=null;segLen=0;voiced=0;sil=0;pre=last?[last]:[];preLen=last?last.length:0;
  var all=new Float32Array(len),o=0,i;
  for(i=0;i<chunks.length;i++){all.set(chunks[i],o);o+=chunks[i].length}
  if(forced&&len>RATE*3){
    var cut=quietPoint(all);
    if(cut>RATE*1.5&&cut<len-RATE*0.1){
      // sisa setelah titik senyap dilanjutkan sebagai awal potongan berikutnya (kalimat tidak terbelah di tengah kata)
      var rest=all.slice(cut);
      seg=[rest];segLen=rest.length;voiced=segLen*1000/RATE*0.6;sil=0;pre=[];preLen=0;
      all=all.slice(0,cut);
    }
  }
  if(v<300)return; // hanya derau singkat
  send(all);
}
// WAV 16-bit mono 16 kHz langsung ke base64, tanpa Blob dan FileReader (satu lompatan async lebih sedikit)
function wav(f){
  var n=f.length,b=new ArrayBuffer(44+n*2),d=new DataView(b),i,s;
  function w(o,t){for(var q=0;q<t.length;q++)d.setUint8(o+q,t.charCodeAt(q))}
  w(0,'RIFF');d.setUint32(4,36+n*2,true);w(8,'WAVE');w(12,'fmt ');
  d.setUint32(16,16,true);d.setUint16(20,1,true);d.setUint16(22,1,true);
  d.setUint32(24,RATE,true);d.setUint32(28,RATE*2,true);d.setUint16(32,2,true);d.setUint16(34,16,true);
  w(36,'data');d.setUint32(40,n*2,true);
  for(i=0;i<n;i++){s=Math.max(-1,Math.min(1,f[i]));d.setInt16(44+i*2,s<0?s*0x8000:s*0x7FFF,true)}
  return new Uint8Array(b);
}
function b64(u8){
  var CH=0x8000,s='',i;
  for(i=0;i<u8.length;i+=CH)s+=String.fromCharCode.apply(null,u8.subarray(i,i+CH));
  return btoa(s);
}
function send(f,live){
  // suara yang terlalu pelan dinaikkan (maks 6x) supaya pengenal tidak kehilangan konsonan lemah
  var i,p=0,a;
  for(i=0;i<f.length;i++){a=f[i];if(a<0)a=-a;if(a>p)p=a}
  if(p>0.002&&p<0.5){var g=Math.min(6,0.7/p);for(i=0;i<f.length;i++)f[i]*=g}
  try{if(live)B.partial(tok,b64(wav(f)));else B.chunk(tok,b64(wav(f)),'audio/wav')}catch(e){}
}
function watch(){
  if(!alive)return;
  var v=pick();
  if(!v){
    if(seg&&segLen>0)flush(null);
    vflush();
    if(curV&&curV.paused)st('Video dijeda');else if(!curV)st('Menunggu video diputar…');
    return;
  }
  var dead=false;
  try{dead=!ms||ms.getAudioTracks()[0].readyState==='ended'}catch(e){dead=true}
  if(v!==curV||dead||!ready){
    if(seg&&segLen>0)flush(null);
    vflush();
    if(!setup(v)){st('Audio video ini tidak bisa ditangkap');return}
    st('Mendengarkan…');
  }
  if(ac&&ac.state==='suspended'){try{ac.resume()}catch(e){}st('Audio ditahan browser, ketuk layar sekali')}
}
function kick(){try{if(ac&&ac.state==='suspended')ac.resume()}catch(e){}}
document.addEventListener('pointerdown',kick,true);
wd=setInterval(watch,600);
window.__alSTT={stop:function(){
  alive=false;
  clearInterval(wd);
  document.removeEventListener('pointerdown',kick,true);
  teardown();
  try{if(ac)ac.close()}catch(e){}
  window.__alSTT=null;
}};
watch();
})();

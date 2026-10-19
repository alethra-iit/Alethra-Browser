(function(){
var C=window.__alSttCfg||{},B=window.AlSTT;
if(!B||!C.tok)return;
if(window.__alSTT){try{window.__alSTT.stop()}catch(e){}}
var tok=C.tok,RATE=16000;
// Preset pemotongan (ms): sil = jeda yang menutup potongan, soft = setelah durasi ini jeda pendek sudah cukup,
// max = batas paksa, min = potongan lebih pendek dari ini menunggu jeda lebih lama (hemat permintaan)
var PRE={fast:{sil:300,soft:1700,max:3800,min:800},bal:{sil:420,soft:2600,max:5500,min:1100},eco:{sil:650,soft:4200,max:8500,min:1800}};
var P=PRE[C.mode]||PRE.bal;
var alive=true,ac=null,curV=null,ms=null,src=null,node=null,proc=null,mute=null,poll=null,wd=null;
var nf=0.004,pre=[],preLen=0,seg=null,segLen=0,voiced=0,sil=0,zeroMs=0,quietMs=0,lastSt='',ready=false;
// wk: 0 = belum dicoba, 1 = AudioWorklet sedang dimuat, 2 = siap, -1 = tidak bisa (pakai ScriptProcessor)
var wk=0;
// AudioWorklet berjalan di thread audio, bukan thread utama halaman. Ia menampung ±4096 sampel, menurunkannya
// ke 16 kHz mono (rata-rata jendela), lalu mengirim Float32Array ke halaman.
var WK='class P extends AudioWorkletProcessor{constructor(){super();this.n=4096;this.b=new Float32Array(this.n);this.i=0}'+
'process(inp){var c=inp[0]&&inp[0][0];if(!c)return true;for(var k=0;k<c.length;k++){this.b[this.i++]=c[k];if(this.i===this.n)this.emit()}return true}'+
'emit(){var n=this.n,x=this.b,m=Math.max(1,Math.round(n*16000/sampleRate)),o=new Float32Array(m),j,k,a,e,s;'+
'for(j=0;j<m;j++){a=Math.floor(j*n/m);e=Math.max(a+1,Math.floor((j+1)*n/m));s=0;for(k=a;k<e;k++)s+=x[k];o[j]=s/(e-a)}'+
'this.i=0;this.port.postMessage(o,[o.buffer])}}'+
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
function mkAc(){
  ac=new (window.AudioContext||window.webkitAudioContext)();
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
      node.port.onmessage=function(e){if(alive&&e.data)onPcm(e.data)};
      src.connect(node);node.connect(mute);
      return;
    }catch(e){detach();wk=-1}
  }
  try{
    proc=ac.createScriptProcessor(4096,1,1);
    proc.onaudioprocess=function(e){if(alive)onPcm(down(e.inputBuffer.getChannelData(0)))};
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
    src=ac.createMediaStreamSource(ms);
    mute=ac.createGain();mute.gain.value=0;
    mute.connect(ac.destination);
  }catch(e){teardown();return false}
  curV=v;ready=true;seg=null;pre=[];preLen=0;sil=0;voiced=0;zeroMs=0;
  attach();
  if(!src){teardown();return false}
  return true;
}
// turunkan ke 16 kHz mono dengan rata-rata jendela (hanya dipakai jalur cadangan ScriptProcessor)
function down(x){
  var n=x.length,m=Math.max(1,Math.round(n*RATE/ac.sampleRate)),o=new Float32Array(m),j,k,a,b,s;
  for(j=0;j<m;j++){
    a=Math.floor(j*n/m);b=Math.max(a+1,Math.floor((j+1)*n/m));s=0;
    for(k=a;k<b;k++)s+=x[k];
    o[j]=s/(b-a);
  }
  return o;
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
  var thr=Math.max(0.010,nf*2.8),speech=r>thr;
  if(!speech)nf=Math.min(0.05,Math.max(0.001,nf*0.96+r*0.04));
  if(!seg){
    pre.push(x);preLen+=x.length;
    while(preLen>RATE*0.3&&pre.length>1){preLen-=pre.shift().length}
    if(speech){seg=pre;segLen=preLen;pre=[];preLen=0;voiced=fms;sil=0;quietMs=0;st('Mendengarkan…')}
    else{quietMs+=fms;if(quietMs>1500&&zeroMs<5000)st(quietMs>15000?'Tidak ada ucapan terdeteksi':'Menunggu ucapan…')}
    return;
  }
  seg.push(x);segLen+=x.length;
  if(speech){voiced+=fms;sil=0}else sil+=fms;
  var dur=segLen*1000/RATE;
  var need=dur<P.min?P.sil*2.2:(dur>=P.soft?P.sil*0.5:P.sil);
  if(dur>=P.max||sil>=need)flush(x);
}
function flush(last){
  var chunks=seg,len=segLen,v=voiced;
  seg=null;segLen=0;voiced=0;sil=0;pre=last?[last]:[];preLen=last?last.length:0;
  if(v<300)return; // hanya derau singkat
  var all=new Float32Array(len),o=0,i;
  for(i=0;i<chunks.length;i++){all.set(chunks[i],o);o+=chunks[i].length}
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
function send(f){
  try{B.chunk(tok,b64(wav(f)),'audio/wav')}catch(e){}
}
function watch(){
  if(!alive)return;
  var v=pick();
  if(!v){
    if(seg&&segLen>0)flush(null);
    if(curV&&curV.paused)st('Video dijeda');else if(!curV)st('Menunggu video diputar…');
    return;
  }
  var dead=false;
  try{dead=!ms||ms.getAudioTracks()[0].readyState==='ended'}catch(e){dead=true}
  if(v!==curV||dead||!ready){
    if(seg&&segLen>0)flush(null);
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

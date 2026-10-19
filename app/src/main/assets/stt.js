(function(){
var C=window.__alSttCfg||{},B=window.AlSTT;
if(!B||!C.tok)return;
if(window.__alSTT){try{window.__alSTT.stop()}catch(e){}}
var tok=C.tok,RATE=16000;
// Preset pemotongan (ms): sil = jeda yang menutup potongan, soft = setelah durasi ini jeda pendek sudah cukup,
// max = batas paksa, min = potongan lebih pendek dari ini menunggu jeda lebih lama (hemat permintaan)
var PRE={fast:{sil:300,soft:1700,max:3800,min:800},bal:{sil:420,soft:2600,max:5500,min:1100},eco:{sil:650,soft:4200,max:8500,min:1800}};
var P=PRE[C.mode]||PRE.bal;
var alive=true,ac=null,curV=null,ms=null,src=null,proc=null,mute=null,poll=null,wd=null;
var nf=0.004,pre=[],preLen=0,seg=null,segLen=0,voiced=0,sil=0,zeroMs=0,quietMs=0,lastSt='',ready=false;
function st(m){if(m===lastSt)return;lastSt=m;try{B.status(tok,m)}catch(e){}}
function pick(){
  var v=document.querySelectorAll('video,audio'),i;
  for(i=0;i<v.length;i++){if(!v[i].paused&&!v[i].ended&&v[i].readyState>2)return v[i]}
  return null;
}
function teardown(){
  try{if(proc){proc.onaudioprocess=null;proc.disconnect()}}catch(e){}
  try{if(src)src.disconnect()}catch(e){}
  try{if(mute)mute.disconnect()}catch(e){}
  proc=null;src=null;mute=null;ms=null;curV=null;ready=false;
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
    if(!ac)ac=new (window.AudioContext||window.webkitAudioContext)();
    if(ac.state==='suspended'){try{ac.resume()}catch(e){}}
    src=ac.createMediaStreamSource(ms);
    proc=ac.createScriptProcessor(4096,1,1);
    mute=ac.createGain();mute.gain.value=0;
    proc.onaudioprocess=onAudio;
    src.connect(proc);proc.connect(mute);mute.connect(ac.destination);
  }catch(e){teardown();return false}
  curV=v;ready=true;seg=null;pre=[];preLen=0;sil=0;voiced=0;zeroMs=0;
  return true;
}
// turunkan ke 16 kHz mono dengan rata-rata jendela
function down(x){
  var n=x.length,m=Math.max(1,Math.round(n*RATE/ac.sampleRate)),o=new Float32Array(m),j,k,a,b,s;
  for(j=0;j<m;j++){
    a=Math.floor(j*n/m);b=Math.max(a+1,Math.floor((j+1)*n/m));s=0;
    for(k=a;k<b;k++)s+=x[k];
    o[j]=s/(b-a);
  }
  return o;
}
function onAudio(e){
  if(!alive)return;
  var x=down(e.inputBuffer.getChannelData(0)),i,r=0,mx=0,a;
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
  send(wav(all));
}
function wav(f){
  var n=f.length,b=new ArrayBuffer(44+n*2),d=new DataView(b),i,s;
  function w(o,t){for(var q=0;q<t.length;q++)d.setUint8(o+q,t.charCodeAt(q))}
  w(0,'RIFF');d.setUint32(4,36+n*2,true);w(8,'WAVE');w(12,'fmt ');
  d.setUint32(16,16,true);d.setUint16(20,1,true);d.setUint16(22,1,true);
  d.setUint32(24,RATE,true);d.setUint32(28,RATE*2,true);d.setUint16(32,2,true);d.setUint16(34,16,true);
  w(36,'data');d.setUint32(40,n*2,true);
  for(i=0;i<n;i++){s=Math.max(-1,Math.min(1,f[i]));d.setInt16(44+i*2,s<0?s*0x8000:s*0x7FFF,true)}
  return new Blob([b],{type:'audio/wav'});
}
function send(blob){
  var r=new FileReader();
  r.onload=function(){
    var s=String(r.result||''),k=s.indexOf(',');
    if(k>0){try{B.chunk(tok,s.slice(k+1),'audio/wav')}catch(e){}}
  };
  r.readAsDataURL(blob);
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

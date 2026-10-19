(function(){
var C=window.__alSttCfg||{},B=window.AlSTT;
if(!B||!C.tok)return;
if(window.__alSTT){try{window.__alSTT.stop()}catch(e){}}
var tok=C.tok,SEC=(C.sec||6)*1000,alive=true,ac=null,rec=null,curV=null,ms=null,src=null,an=null,buf=null,peak=0,timer=null,poll=null,silent=0;
function st(m){try{B.status(tok,m)}catch(e){}}
function pick(){
  var v=document.querySelectorAll('video,audio'),i;
  for(i=0;i<v.length;i++){if(!v[i].paused&&!v[i].ended&&v[i].readyState>2)return v[i]}
  return null;
}
function setup(v){
  try{if(src)src.disconnect()}catch(e){}
  src=null;an=null;ms=null;curV=null;
  var s=null;
  try{s=v.captureStream?v.captureStream():(v.mozCaptureStream?v.mozCaptureStream():null)}catch(e){}
  if(!s)return false;
  var t=s.getAudioTracks();
  if(!t.length)return false;
  ms=new MediaStream(t);
  curV=v;
  try{
    if(!ac)ac=new (window.AudioContext||window.webkitAudioContext)();
    if(ac.state==='suspended')ac.resume();
    src=ac.createMediaStreamSource(ms);
    an=ac.createAnalyser();an.fftSize=1024;
    buf=new Uint8Array(an.fftSize);
    src.connect(an);
  }catch(e){an=null}
  return true;
}
function mime(){
  var a=['audio/webm;codecs=opus','audio/webm','audio/ogg;codecs=opus'],i;
  for(i=0;i<a.length;i++){try{if(MediaRecorder.isTypeSupported(a[i]))return a[i]}catch(e){}}
  return '';
}
function send(blob){
  var r=new FileReader();
  r.onload=function(){
    var s=String(r.result||''),k=s.indexOf(',');
    if(k>0){try{B.chunk(tok,s.slice(k+1),(blob.type||'audio/webm').split(';')[0])}catch(e){}}
  };
  r.readAsDataURL(blob);
}
function cycle(){
  if(!alive)return;
  var v=pick();
  if(!v){st('Menunggu video diputar…');timer=setTimeout(cycle,1000);return}
  var dead=false;
  try{dead=!ms||ms.getAudioTracks()[0].readyState==='ended'}catch(e){dead=true}
  if(v!==curV||dead){
    if(!setup(v)){st('Audio video ini tidak bisa ditangkap');timer=setTimeout(cycle,2500);return}
  }
  var chunks=[],mt=mime(),r;
  peak=0;
  try{r=new MediaRecorder(ms,mt?{mimeType:mt}:undefined)}catch(e){st('Perekam audio tidak didukung');timer=setTimeout(cycle,3000);return}
  rec=r;
  r.ondataavailable=function(e){if(e.data&&e.data.size)chunks.push(e.data)};
  r.onstop=function(){
    if(!alive)return;
    var gate=(an&&ac&&ac.state==='running')?peak>3:true;
    if(gate&&chunks.length){silent=0;send(new Blob(chunks,{type:r.mimeType||'audio/webm'}))}
    else if(!gate){silent++;st(silent>=3?'Tidak ada suara terdeteksi dari video':'Mendengarkan…')}
    cycle();
  };
  try{r.start()}catch(e){timer=setTimeout(cycle,1000);return}
  st('Mendengarkan…');
  timer=setTimeout(function(){try{r.stop()}catch(e){cycle()}},SEC);
}
poll=setInterval(function(){
  if(!an)return;
  try{an.getByteTimeDomainData(buf)}catch(e){return}
  var m=0,i,d;
  for(i=0;i<buf.length;i++){d=buf[i]-128;if(d<0)d=-d;if(d>m)m=d}
  if(m>peak)peak=m;
},150);
window.__alSTT={stop:function(){
  alive=false;
  clearTimeout(timer);clearInterval(poll);
  try{if(rec&&rec.state!=='inactive')rec.stop()}catch(e){}
  try{if(src)src.disconnect()}catch(e){}
  try{if(ac)ac.close()}catch(e){}
  window.__alSTT=null;
}};
cycle();
})();

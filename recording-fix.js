/* Recording fixes only: automatic live recording + teacher upload */
(function(){
  function safe(v){return String(v??'').replace(/[&<>"']/g,c=>({'&':'&amp;','<':'&lt;','>':'&gt;','"':'&quot;',"'":'&#39;'}[c]))}

  window.startRecording = async function(sid){
    if(!localStream || !localStream.getTracks().length) return;
    recordChunks=[];
    try{
      let opts={};
      if(window.MediaRecorder && MediaRecorder.isTypeSupported('video/webm;codecs=vp9,opus')) opts.mimeType='video/webm;codecs=vp9,opus';
      else if(window.MediaRecorder && MediaRecorder.isTypeSupported('video/webm;codecs=vp8,opus')) opts.mimeType='video/webm;codecs=vp8,opus';
      recorder=new MediaRecorder(localStream,opts);
      recorder.ondataavailable=e=>{if(e.data&&e.data.size)recordChunks.push(e.data)};
      recorder.onstop=async()=>{
        try{
          const blob=new Blob(recordChunks,{type:recorder.mimeType||'video/webm'});
          if(!blob.size) throw new Error('Recording file is empty');
          const {data:live,error:liveError}=await db.from('live_classes').select('id,classroom_id,teacher_id,title').eq('id',liveCurrentId).maybeSingle();
          if(liveError) throw liveError;
          if(!live) throw new Error('Live class not found');
          const chapter=(live.title||'Live Classes').split(' — ')[0].trim()||'Live Classes';
          const safeChapter=chapter.replace(/[^a-zA-Z0-9_-]/g,'_');
          const path=me.id+'/'+live.id+'/'+safeChapter+'/'+Date.now()+'.webm';
          const up=await db.storage.from('class-recordings').upload(path,blob,{contentType:blob.type||'video/webm',upsert:false});
          if(up.error) throw up.error;
          const url=db.storage.from('class-recordings').getPublicUrl(path).data.publicUrl;
          const ended=new Date().toISOString();
          const sx=await db.from('live_sessions').update({recording_url:url,ended_at:ended,active:false}).eq('id',sid);
          if(sx.error) throw sx.error;
          const lx=await db.from('live_classes').update({is_live:false,recording_url:url}).eq('id',live.id);
          if(lx.error) throw lx.error;
          const rx=await db.from('recorded_chapters').insert({classroom_id:live.classroom_id,teacher_id:me.id,title:live.title,chapter_name:chapter,video_url:url,storage_path:path});
          if(rx.error) throw rx.error;
          window.lastLiveRecording={url,title:live.title};
          const box=document.getElementById('liveRecordingResult');
          if(box) box.innerHTML='<div class="card"><b>Recording saved successfully</b><p>'+safe(live.title)+'</p><a href="'+safe(url)+'" download target="_blank"><button>⬇ Download Recording</button></a></div>';
        }catch(e){
          console.error('Recording save failed',e);
          alert('Recording save failed: '+(e.message||e));
        }
      };
      recorder.start(1000);
    }catch(e){console.error('Recorder start failed',e);alert('Recording could not start: '+(e.message||e));}
  };

  window.uploadRecording=async function(classroomId){
    if(!me || me.role!=='teacher') return alert('Teacher login required');
    const input=document.createElement('input');
    input.type='file';
    input.accept='video/*,.webm,.mp4,.mov,.m4v';
    input.onchange=async()=>{
      try{
        const file=input.files&&input.files[0];
        if(!file) return;
        const subject=prompt('Subject name (Example: Hindi, Science)','Hindi');
        if(!subject) return;
        const title=prompt('Lesson / Chapter name',file.name.replace(/\.[^.]+$/,''));
        if(!title) return;
        const chapter=subject.trim()||'Recorded Classes';
        const safeChapter=chapter.replace(/[^a-zA-Z0-9_-]/g,'_');
        const safeName=file.name.replace(/[^a-zA-Z0-9._-]/g,'_');
        const path=me.id+'/'+classroomId+'/'+safeChapter+'/'+Date.now()+'_'+safeName;
        const up=await db.storage.from('class-recordings').upload(path,file,{contentType:file.type||'video/webm',upsert:false});
        if(up.error) throw up.error;
        const url=db.storage.from('class-recordings').getPublicUrl(path).data.publicUrl;
        const ins=await db.from('recorded_chapters').insert({classroom_id:classroomId,teacher_id:me.id,title,chapter_name:chapter,video_url:url,storage_path:path});
        if(ins.error) throw ins.error;
        alert('Recording uploaded successfully. Students can now view and download it.');
      }catch(e){alert('Recording upload failed: '+(e.message||e))}
    };
    input.click();
  };

  function addTeacherUploadButtons(){
    if(!me || me.role!=='teacher') return;
    document.querySelectorAll('button[onclick^="teacherLive("]').forEach(btn=>{
      if(btn.parentElement.querySelector('.recording-upload-btn')) return;
      const m=(btn.getAttribute('onclick')||'').match(/teacherLive\('([^']+)'\)/);
      if(!m) return;
      const b=document.createElement('button');
      b.className='recording-upload-btn green';
      b.textContent='Upload Recording';
      b.onclick=()=>uploadRecording(m[1]);
      btn.parentElement.appendChild(b);
    });
  }

  const originalTeacherHome=window.teacherHome;
  if(originalTeacherHome){
    window.teacherHome=async function(){
      await originalTeacherHome();
      setTimeout(addTeacherUploadButtons,50);
    };
  }
})();

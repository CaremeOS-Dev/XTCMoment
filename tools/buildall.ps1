$ErrorActionPreference='Continue'
$log='logs\javac_loop.log'
Remove-Item $log -ErrorAction SilentlyContinue
for($i=1;$i -le 60;$i++){
  & pwsh -NoProfile -File build.ps1 -Tasks ':app:compileDebugJavaWithJavac' 2>&1 > logs\javac_iter.log
  $errs = Select-String -Path logs\javac_iter.log -Pattern 'error:'
  $n = ($errs | Measure-Object).Count
  Add-Content $log "ITER $i errors=$n"
  if($n -eq 0){ Add-Content $log "CLEAN at iter $i"; break }
  foreach($e in $errs){ Add-Content $log $e.Line }
  if($n -gt 400){ Add-Content $log "too many, stopping"; break }
}
Write-Output "done"
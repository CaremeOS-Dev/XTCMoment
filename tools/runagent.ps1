param(
    [string]$PromptFile,
    [string]$OutLog,
    [string]$ErrLog,
    [string]$WorkDir,
    [string]$Model = 'deepseek-v4.1-flash-sg',
    [string]$ReasoningEffort = 'high'
)
Set-Location $WorkDir
$modelArg = @()
if ($Model) { $modelArg = @('-m', $Model) }
$effortArg = @()
if ($ReasoningEffort) { $effortArg = @('-c', "model_reasoning_effort=$ReasoningEffort") }
Get-Content $PromptFile -Raw | codex exec @modelArg @effortArg --dangerously-bypass-approvals-and-sandbox --skip-git-repo-check - 1> $OutLog 2> $ErrLog
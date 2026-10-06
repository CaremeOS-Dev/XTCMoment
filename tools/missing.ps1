param(
    [string]$Filter = '',
    [switch]$Summary
)
$ErrorActionPreference = 'Stop'
$refRoot = 'D:\CaremiumWorkspace\Research\SystemApps\XTCMoment\decompiled\sources\com\xtc'
$roots = @(
    'D:\CaremiumWorkspace\Sources\moment\app\src\main\java\com\xtc',
    'D:\CaremiumWorkspace\Sources\moment\app\src\main\kotlin\com\xtc'
)
$existing = New-Object 'System.Collections.Generic.HashSet[string]' ([StringComparer]::OrdinalIgnoreCase)
foreach ($r in $roots) {
    if (-not (Test-Path $r)) { continue }
    Get-ChildItem -Recurse -File -Path $r | Where-Object { $_.Extension -in '.java', '.kt' } | ForEach-Object {
        $rel = $_.FullName.Substring($r.Length + 1)
        $dir = Split-Path $rel -Parent
        $base = [System.IO.Path]::GetFileNameWithoutExtension($rel)
        $key = if ($dir) { "$dir\$base" } else { $base }
        [void]$existing.Add($key)
    }
}
$missing = New-Object 'System.Collections.Generic.List[string]'
Get-ChildItem -Recurse -File -Filter *.java -Path $refRoot | ForEach-Object {
    $rel = $_.FullName.Substring($refRoot.Length + 1)
    $dir = Split-Path $rel -Parent
    $base = [System.IO.Path]::GetFileNameWithoutExtension($rel)
    $key = if ($dir) { "$dir\$base" } else { $base }
    if (-not $existing.Contains($key)) { [void]$missing.Add($key) }
}
$items = $missing | Sort-Object
if ($Filter) { $normalized = $Filter.Replace("/", "\"); $items = $items | Where-Object { $_ -like "$normalized*" } }
if ($Summary) {
    $items | ForEach-Object { $p = $_ -split '\\'; if ($p.Count -ge 3) { ($p[0..2] -join '/') } elseif ($p.Count -ge 2) { ($p[0..1] -join '/') } else { $p[0] } } |
        Group-Object | Sort-Object Count -Descending | ForEach-Object { '{0,5}  {1}' -f $_.Count, $_.Name }
} else {
    $items | ForEach-Object { $_ }
}
# Copyright 2015 the original author or authors.
#
# Licensed under the Apache License, Version 2.0 (the "License");
# you may not use this file except in compliance with the License.
# You may obtain a copy of the License at
#
#      https://www.apache.org/licenses/LICENSE-2.0
#
# Unless required by applicable law or agreed to in writing, software
# distributed under the License is distributed on an "AS IS" BASIS,
# WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
# See the License for the specific language governing permissions and
# limitations under the License.

##############################################################################
#
#   Gradle startup script for PowerShell, mirroring gradlew / gradlew.bat.
#
#   Usage:
#       .\gradlew.ps1 <tasks and options...>
#       .\gradlew.ps1 :app:assembleDebug --console=plain
#
#   Environment variables honoured (same as the shell scripts):
#       JAVA_HOME      - JDK to launch Gradle with; falls back to java on PATH
#       DEFAULT_JVM_OPTS / JAVA_OPTS / GRADLE_OPTS
#                      - extra JVM options, split on whitespace with support
#                        for single or double quoted groups
#
#   GRADLE_EXIT_CONSOLE is not needed here: unlike gradlew.bat there is no
#   _cmd.exe /c_ wrapper, so the java exit code is already propagated verbatim.
#
##############################################################################

Set-StrictMode -Version 2.0

# Gradle logs plenty of warnings to stderr; make sure PowerShell never treats a
# native command's stderr output as a terminating error (PS 7.4+ default).
if (Test-Path variable:PSNativeCommandUseErrorActionPreference) {
    $PSNativeCommandUseErrorActionPreference = $false
}

# Used for the -Dorg.gradle.appname property, like APP_BASE_NAME in the shell scripts
$APP_BASE_NAME = [System.IO.Path]::GetFileNameWithoutExtension($MyInvocation.MyCommand.Name)

# Resolve APP_HOME to the directory holding this script, like %~dp0 / ${0%/*}
if ($PSScriptRoot) {
    $APP_HOME = $PSScriptRoot
} elseif ($MyInvocation.MyCommand.Path) {
    $APP_HOME = Split-Path -Parent $MyInvocation.MyCommand.Path
} else {
    $APP_HOME = (Get-Location).Path
}
$APP_HOME = (Resolve-Path -LiteralPath $APP_HOME).Path

# Add default JVM options here. You can also use JAVA_OPTS and GRADLE_OPTS to pass JVM options to this script.
$DEFAULT_JVM_OPTS = '-Xmx64m', '-Xms64m'

# Split an option string into arguments, honouring single and double quotes.
function Split-JvmOpts([string] $Text) {
    if ([string]::IsNullOrWhiteSpace($Text)) { return @() }
    $result = New-Object System.Collections.Generic.List[string]
    $current = New-Object System.Text.StringBuilder
    $quote = [char]0
    $hasToken = $false
    foreach ($ch in $Text.ToCharArray()) {
        if ($quote -ne [char]0) {
            if ($ch -eq $quote) { $quote = [char]0 } else { [void]$current.Append($ch) }
        } elseif ($ch -eq '"' -or $ch -eq "'") {
            $quote = $ch
            $hasToken = $true
        } elseif ([char]::IsWhiteSpace($ch)) {
            if ($hasToken -or $current.Length -gt 0) {
                $result.Add($current.ToString())
                [void]$current.Clear()
                $hasToken = $false
            }
        } else {
            [void]$current.Append($ch)
        }
    }
    if ($hasToken -or $current.Length -gt 0) { $result.Add($current.ToString()) }
    return $result.ToArray()
}

# Find java.exe
if ($env:JAVA_HOME) {
    $JAVA_EXE = Join-Path $env:JAVA_HOME 'bin\java.exe'
    if (-not (Test-Path -LiteralPath $JAVA_EXE)) {
        [Console]::Error.WriteLine(@"
ERROR: JAVA_HOME is set to an invalid directory: $($env:JAVA_HOME)

Please set the JAVA_HOME variable in your environment to match the
location of your Java installation.
"@)
        exit 1
    }
} else {
    $javaCommand = Get-Command java.exe -CommandType Application -ErrorAction SilentlyContinue |
        Select-Object -First 1
    if (-not $javaCommand) {
        [Console]::Error.WriteLine(@"
ERROR: JAVA_HOME is not set and no 'java' command could be found in your PATH.

Please set the JAVA_HOME variable in your environment to match the
location of your Java installation.
"@)
        exit 1
    }
    $JAVA_EXE = $javaCommand.Source
}

# Setup the command line
$CLASSPATH = Join-Path $APP_HOME 'gradle\wrapper\gradle-wrapper.jar'

# Execute Gradle
$javaArgs = @()
$javaArgs += Split-JvmOpts $DEFAULT_JVM_OPTS
$javaArgs += Split-JvmOpts $env:JAVA_OPTS
$javaArgs += Split-JvmOpts $env:GRADLE_OPTS
$javaArgs += "-Dorg.gradle.appname=$APP_BASE_NAME"
$javaArgs += '-classpath'
$javaArgs += $CLASSPATH
$javaArgs += 'org.gradle.wrapper.GradleWrapperMain'
$javaArgs += $args

& $JAVA_EXE @javaArgs
exit $LASTEXITCODE

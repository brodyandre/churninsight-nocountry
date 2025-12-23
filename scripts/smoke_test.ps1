# scripts/smoke_test.ps1
# Smoke test ChurnInsight (Docker Compose)
# - Health do Spring e DS
# - Predição fim-a-fim (Spring -> DS -> Modelo)
# Saída:
#   exit 0 = OK
#   exit 1 = Falha

[Console]::OutputEncoding = [System.Text.Encoding]::UTF8
chcp 65001 | Out-Null


$ErrorActionPreference = "Stop"

function Write-Ok($msg)   { Write-Host "[OK]  $msg" -ForegroundColor Green }
function Write-Info($msg) { Write-Host "[INFO] $msg" -ForegroundColor Cyan }
function Write-Warn($msg) { Write-Host "[WARN] $msg" -ForegroundColor Yellow }
function Write-Fail($msg) { Write-Host "[FAIL] $msg" -ForegroundColor Red }

# Endpoints (ajuste se seu mapeamento de portas mudar)
$springHealthUrl = "http://localhost:8080/api/churn/health"
$springPredictUrl = "http://localhost:8080/api/churn/predict"
$dsHealthUrl = "http://localhost:8000/health"

try {
    Write-Info "Iniciando smoke test do ChurnInsight..."

    # 1) Health Spring
    Write-Info "Verificando health do Spring: $springHealthUrl"
    $springHealth = Invoke-RestMethod -Uri $springHealthUrl -Method Get -TimeoutSec 10

    if (-not $springHealth) {
        throw "Health do Spring retornou vazio."
    }

    # Alguns endpoints retornam objeto com campos; trate o mais comum
    if ($springHealth.status -and $springHealth.status -ne "ok") {
        throw "Health do Spring retornou status inesperado: $($springHealth.status)"
    }

    if ($springHealth.ds_service_url) {
        Write-Ok "Spring health OK. ds_service_url=$($springHealth.ds_service_url)"
    } else {
        Write-Warn "Spring health OK, porém ds_service_url não apareceu na resposta (isso pode ser normal dependendo da implementação)."
    }

    # 2) Health DS
    Write-Info "Verificando health do DS (FastAPI): $dsHealthUrl"
    $dsHealth = Invoke-RestMethod -Uri $dsHealthUrl -Method Get -TimeoutSec 10
    Write-Ok "DS health OK."

    # 3) Construir payload e JSON
    $payload = [ordered]@{
        gender="Female"
        SeniorCitizen=1
        Partner="No"
        Dependents="No"
        tenure=2
        PhoneService="Yes"
        MultipleLines="No"
        InternetService="Fiber optic"
        OnlineSecurity="No"
        OnlineBackup="No"
        DeviceProtection="No"
        TechSupport="No"
        StreamingTV="Yes"
        StreamingMovies="Yes"
        Contract="Month-to-month"
        PaperlessBilling="Yes"
        PaymentMethod="Electronic check"
        MonthlyCharges=99.85
        TotalCharges=199.7
    }

    $json = $payload | ConvertTo-Json -Depth 5 -Compress

    if ([string]::IsNullOrWhiteSpace($json)) {
        throw "JSON gerado está vazio (variável `$json` nula). Não é possível executar o predict."
    }

    Write-Info "Executando predição via Spring: $springPredictUrl"
    $pred = Invoke-RestMethod -Uri $springPredictUrl -Method Post -ContentType "application/json" -Body $json -TimeoutSec 20

    if (-not $pred) {
        throw "Predição retornou vazio."
    }

    # Tenta imprimir de forma amigável, mesmo se a API retornar campos diferentes
    $prev = $pred.previsao
    $prob = $pred.probabilidade
    $thr  = $pred.threshold
    $lat  = $pred.latencia_ms

    if ($null -eq $prev -and $null -eq $prob) {
        Write-Warn "Predição retornou um objeto sem campos esperados. Conteúdo bruto:"
        $pred | ConvertTo-Json -Depth 8
    } else {
        Write-Ok "Predição OK: previsao=$prev | probabilidade=$prob | threshold=$thr | latencia_ms=$lat"
    }

    Write-Ok "Smoke test concluído com sucesso."
    exit 0
}
catch {
    Write-Fail $_.Exception.Message

    Write-Info "Dicas rápidas de diagnóstico:"
    Write-Host "  - Ver logs: docker compose logs -f ds-service" -ForegroundColor Gray
    Write-Host "  - Ver logs: docker compose logs -f spring-backend" -ForegroundColor Gray
    Write-Host "  - Ver status: docker compose ps" -ForegroundColor Gray

    exit 1
}

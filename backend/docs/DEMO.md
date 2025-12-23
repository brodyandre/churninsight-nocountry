# ChurnInsight — Demo Runbook (Local)

## Visão geral
- UI (Spring Boot): http://localhost:8080/
- Spring Health: http://localhost:8080/api/churn/health
- DS Health via proxy: http://localhost:8080/api/churn/ds-health
- Presets via proxy: http://localhost:8080/api/churn/demo-examples
- FastAPI direto (opcional): http://127.0.0.1:8000/health

A arquitetura é: UI (Spring) → /api/churn/predict → proxy Java → FastAPI (/predict) → modelo (joblib).

---

## 1) Subir o DS Service (FastAPI)
No diretório raiz do repo:
```powershell
cd C:\Users\USER\Documents\Repositorios\churninsight-nocountry
python -m uvicorn ds_service.app:app --reload --host 127.0.0.1 --port 8000

```

### Valide: 

```powershel
curl.exe -i http://127.0.0.1:8000/health

```

## 2) Subir o Backend (Spring Boot)

Em outro terminal:
```powershell
cd C:\Users\USER\Documents\Repositorios\churninsight-nocountry\backend\churninsight-backend
.\mvnw spring-boot:run

```
### Valide:

```powershell
curl.exe -i http://localhost:8080/api/churn/health
curl.exe -i http://localhost:8080/api/churn/ds-health
curl.exe -i http://localhost:8080/api/churn/demo-examples

```

Observações:

 - Os endpoints proxy devem devolver headers X-Proxy-Latency-Ms e X-Target-Url.

 - Se o DS estiver fora, o proxy deve retornar 502.

## 3) Demo via UI (Spring Boot)

Abra:
http://localhost:8080/

Fluxo:

1 - Confira os KPIs (Spring OK, FastAPI OK, Latência DS, Threshold, Modelo).

2 - No dropdown, escolha um preset (alto / baixo / inválido).

3 - Clique em "Carregar" e depois "Prever".

Verifique:

 - previsao e probabilidade

 - threshold

 - latencia_ms (FastAPI) e/ou latência do proxy (headers/medida UI)

## 4) Casos de teste para apresentação (banca)
### 4.1) Caso feliz (200)

- Escolha “Alto risco” e clique em "Prever".

- Mostre que a UI chama Spring (proxy) e o Spring chama o FastAPI.

### 4.2) Validação (422)

- Escolha o preset “Inválido”.

- Resultado esperado: 422 (e UI exibe mensagem de payload inválido).

### 4.3) DS fora (502)

- Pare o FastAPI (Ctrl+C no terminal do uvicorn).

- Na UI, clique "Prever".

- Resultado esperado: erro 502 no proxy Java e indicação de indisponibilidade do DS.

## 5) Build (opcional)
```powershell
.\mvnw -DskipTests clean package

```
from fastapi import FastAPI, HTTPException, Request
from fastapi.responses import HTMLResponse, Response, PlainTextResponse
from fastapi.staticfiles import StaticFiles
from fastapi.openapi.docs import get_swagger_ui_html, get_redoc_html
from fastapi.exceptions import RequestValidationError
from pydantic import BaseModel
import pydantic
import joblib
import pandas as pd
from pathlib import Path
import json
from copy import deepcopy
from typing import Any, Dict, List, Optional, Tuple
import time


# ==========================================
# Paths
# ==========================================
BASE_DIR = Path(__file__).resolve().parent
STATIC_DIR = BASE_DIR / "static"
MODEL_PATH = BASE_DIR.parent / "model" / "churn_xgboost_pipeline_tuned.joblib"
NOTEBOOK_PATH = BASE_DIR.parent / "notebooks" / "churn_modeling.ipynb"  # mantido só para /health

STATIC_DIR.mkdir(parents=True, exist_ok=True)

# ==========================================
# Exemplo base
# ==========================================
EXAMPLE_CLIENTE = {
    "gender": "Female",
    "SeniorCitizen": 0,
    "Partner": "Yes",
    "Dependents": "No",
    "tenure": 12,
    "PhoneService": "Yes",
    "MultipleLines": "No",
    "InternetService": "Fiber optic",
    "OnlineSecurity": "No",
    "OnlineBackup": "Yes",
    "DeviceProtection": "No",
    "TechSupport": "No",
    "StreamingTV": "Yes",
    "StreamingMovies": "No",
    "Contract": "Month-to-month",
    "PaperlessBilling": "Yes",
    "PaymentMethod": "Electronic check",
    "MonthlyCharges": 70.35,
    "TotalCharges": 151.65,
}

# ==========================================
# Presets dinâmicos (Fonte única para UI/Proxy)
# Formato: lista [{id,label,risk,description,payload}, ...]
# ==========================================
def _build_demo_examples() -> List[Dict[str, Any]]:
    alto = dict(EXAMPLE_CLIENTE)
    alto.update({
        "tenure": 1,
        "Contract": "Month-to-month",
        "PaymentMethod": "Electronic check",
        "InternetService": "Fiber optic",
        "OnlineSecurity": "No",
        "TechSupport": "No",
        "MonthlyCharges": 99.0,
        "TotalCharges": 99.0,
    })

    baixo = dict(EXAMPLE_CLIENTE)
    baixo.update({
        "gender": "Male",
        "Partner": "Yes",
        "Dependents": "Yes",
        "tenure": 60,
        "Contract": "Two year",
        "PaymentMethod": "Credit card (automatic)",
        "InternetService": "DSL",
        "OnlineSecurity": "Yes",
        "TechSupport": "Yes",
        "MonthlyCharges": 29.0,
        "TotalCharges": 1700.0,
    })

    invalido = dict(EXAMPLE_CLIENTE)
    invalido.pop("gender", None)     # força 422 (campo obrigatório ausente)
    invalido["tenure"] = "doze"      # força 422 (tipo inválido)

    return [
        {
            "id": "alto_risco",
            "label": "Exemplo 1 – Alto risco (tende a churn)",
            "risk": "high",
            "description": "Tenure baixo, contrato mensal, cobrança alta, pouco suporte.",
            "payload": deepcopy(alto),
        },
        {
            "id": "baixo_risco",
            "label": "Exemplo 2 – Baixo risco (tende a ficar)",
            "risk": "low",
            "description": "Tenure alto, contrato 2 anos, cobrança baixa, segurança/suporte ativos.",
            "payload": deepcopy(baixo),
        },
        {
            "id": "invalido",
            "label": "Exemplo 3 – Inválido (testar validação)",
            "risk": "invalid",
            "description": "Payload propositalmente inválido (campo faltando e tipo incorreto).",
            "payload": deepcopy(invalido),
        },
    ]


DEMO_EXAMPLES: List[Dict[str, Any]] = _build_demo_examples()
DEMO_SOURCE: str = "fastapi"  # agora os presets são expostos no padrão do Passo 3


# ==========================================
# OpenAPI tags
# ==========================================
openapi_tags = [
    {"name": "Status", "description": "Verificações, exemplos e demos para apresentação."},
    {"name": "Predict", "description": "Previsão de churn (endpoint principal)."},
]

# ==========================================
# App
# ==========================================
app = FastAPI(
    title="ChurnInsight – Serviço de Previsão de Churn (Telco)",
    description=(
        "API REST em Python/FastAPI que expõe um modelo XGBoost para churn.\n\n"
        "Demo recomendada:\n"
        "1) /health\n"
        "2) /demo-examples (alto/baixo/inválido)\n"
        "3) POST /predict\n"
    ),
    version="1.0.0",
    docs_url=None,
    redoc_url=None,
    openapi_tags=openapi_tags,
)

app.mount("/static", StaticFiles(directory=str(STATIC_DIR)), name="static")

# ==========================================
# EXCEPTION HANDLER (422 -> "Inválido")
# ==========================================
@app.exception_handler(RequestValidationError)
async def validation_exception_handler(request: Request, exc: RequestValidationError):
    # Log no console e resposta simplificada para demo
    try:
        print(f"[VALIDATION 422] {request.method} {request.url.path} -> {exc.errors()}")
    except Exception:
        pass
    return PlainTextResponse("Inválido", status_code=422)

# ==========================================
# Globals
# ==========================================
model: Optional[Any] = None
MODEL_ERROR: Optional[str] = None

THRESHOLD: float = 0.50
MODEL_META: Dict[str, Any] = {}

# ==========================================
# Carregar modelo (aceita Pipeline ou dict com chaves)
# ==========================================
def carregar_modelo():
    global model, THRESHOLD, MODEL_META, MODEL_ERROR
    MODEL_ERROR = None

    if not MODEL_PATH.exists():
        raise FileNotFoundError(f"Arquivo de modelo não encontrado em: {MODEL_PATH}")

    obj = joblib.load(MODEL_PATH)

    if isinstance(obj, dict):
        if "model" not in obj:
            raise ValueError(f"Artefato dict sem chave 'model'. Chaves: {list(obj.keys())}")

        model = obj["model"]
        if obj.get("threshold") is not None:
            THRESHOLD = float(obj["threshold"])

        MODEL_META = {
            "threshold": obj.get("threshold"),
            "best_cv_pr_auc": obj.get("best_cv_pr_auc"),
            "best_params": obj.get("best_params"),
            "features_count": len(obj.get("features") or []),
        }
    else:
        model = obj
        MODEL_META = {}

    if not hasattr(model, "predict_proba"):
        raise TypeError(f"O objeto carregado em 'model' não possui predict_proba(). Tipo: {type(model)}")

    print(f"[OK] Modelo carregado: {MODEL_PATH} (tipo: {type(model)})")
    print(f"[OK] Threshold em uso: {THRESHOLD:.2f}")

# ==========================================
# Startup
# ==========================================
@app.on_event("startup")
def startup_event():
    global MODEL_ERROR
    try:
        carregar_modelo()
    except Exception as e:
        MODEL_ERROR = str(e)
        print(f"[ERROR] Erro ao carregar o modelo na inicialização: {e}")

# ==========================================
# Schemas (Pydantic v2 vs v1)
# ==========================================
PYDANTIC_V2 = pydantic.VERSION.startswith("2.")

if PYDANTIC_V2:
    class ClienteTelco(BaseModel):
        gender: str
        SeniorCitizen: int
        Partner: str
        Dependents: str
        tenure: int
        PhoneService: str
        MultipleLines: str
        InternetService: str
        OnlineSecurity: str
        OnlineBackup: str
        DeviceProtection: str
        TechSupport: str
        StreamingTV: str
        StreamingMovies: str
        Contract: str
        PaperlessBilling: str
        PaymentMethod: str
        MonthlyCharges: float
        TotalCharges: float

        model_config = {"json_schema_extra": {"examples": [EXAMPLE_CLIENTE]}}

    class ResultadoChurn(BaseModel):
        previsao: str
        probabilidade: float
        threshold: float
        latencia_ms: float
else:
    class ClienteTelco(BaseModel):
        gender: str
        SeniorCitizen: int
        Partner: str
        Dependents: str
        tenure: int
        PhoneService: str
        MultipleLines: str
        InternetService: str
        OnlineSecurity: str
        OnlineBackup: str
        DeviceProtection: str
        TechSupport: str
        StreamingTV: str
        StreamingMovies: str
        Contract: str
        PaperlessBilling: str
        PaymentMethod: str
        MonthlyCharges: float
        TotalCharges: float

        class Config:
            schema_extra = {"example": EXAMPLE_CLIENTE}

    class ResultadoChurn(BaseModel):
        previsao: str
        probabilidade: float
        threshold: float
        latencia_ms: float

# ==========================================
# Inferência (com latência)
# ==========================================
def prever_cliente(dados_cliente: dict) -> dict:
    if model is None:
        raise RuntimeError("Modelo ainda não foi carregado na aplicação.")

    t0 = time.perf_counter()

    X_novo = pd.DataFrame([dados_cliente])
    prob = float(model.predict_proba(X_novo)[:, 1][0])
    pred = int(prob >= THRESHOLD)

    lat_ms = round((time.perf_counter() - t0) * 1000.0, 2)

    return {
        "previsao": "Vai cancelar" if pred == 1 else "Vai continuar",
        "probabilidade": prob,
        "threshold": float(THRESHOLD),
        "latencia_ms": lat_ms,
    }

# ==========================================
# Docs custom
# ==========================================
@app.get("/docs", include_in_schema=False)
def custom_swagger_ui_html():
    return get_swagger_ui_html(
        openapi_url=app.openapi_url,
        title="ChurnInsight Docs",
        swagger_ui_parameters={
            "docExpansion": "list",
            "defaultModelsExpandDepth": -1,
            "displayRequestDuration": True,
            "persistAuthorization": True,
            "filter": True,
            "deepLinking": True,
            "tagsSorter": "alpha",
        },
        swagger_css_url="/static/swagger-dark.css",
        swagger_favicon_url="/favicon.ico",
    )

@app.get("/redoc", include_in_schema=False)
def redoc_html():
    return get_redoc_html(openapi_url=app.openapi_url, title="ChurnInsight ReDoc")

@app.get("/favicon.ico", include_in_schema=False)
def favicon():
    return Response(status_code=204)

# ==========================================
# Landing Page (UI consome /demo-examples)
# Compatível com:
# - formato NOVO (lista)
# - formato antigo (dict com items/presets/labels)
# ==========================================
@app.get("/", include_in_schema=False, response_class=HTMLResponse)
def landing():
    modelo_ok = model is not None
    status_text = "Modelo carregado ✅" if modelo_ok else "Modelo NÃO carregado ❌"
    status_class = "ok" if modelo_ok else "bad"

    best_pr_auc = MODEL_META.get("best_cv_pr_auc", None)
    best_pr_auc_str = f"{best_pr_auc:.4f}" if isinstance(best_pr_auc, (int, float)) else "—"
    features_count = MODEL_META.get("features_count", "—")

    html = f"""
    <!doctype html>
    <html lang="pt-br">
      <head>
        <meta charset="utf-8" />
        <meta name="viewport" content="width=device-width, initial-scale=1" />
        <title>ChurnInsight – Hackathon</title>
        <link rel="stylesheet" href="/static/site.css" />
      </head>
      <body>
        <div class="container">
          <header class="header">
            <div class="brand">
              <div class="logo" aria-hidden="true"></div>
              <div>
                <div class="title">ChurnInsight</div>
                <div class="subtitle">MVP de previsão de churn • FastAPI + XGBoost • Interface de demo</div>
              </div>
            </div>

            <div class="chips">
              <span class="chip">Threshold: {THRESHOLD:.2f}</span>
              <span class="chip">PR-AUC (CV): {best_pr_auc_str}</span>
              <span class="chip">Features: {features_count}</span>
              <span class="chip {status_class}">{status_text}</span>
            </div>
          </header>

          <nav class="nav">
            <a class="btn primary" href="/docs">Abrir Swagger (/docs)</a>
            <a class="btn" href="/redoc">Abrir ReDoc (/redoc)</a>
            <a class="btn" href="/health">Health (/health)</a>
            <a class="btn" href="/demo-examples">Demos (/demo-examples)</a>
          </nav>

          <main class="grid">
            <section class="card">
              <h2>Fluxo recomendado (para banca)</h2>
              <div class="steps">
                <div class="step"><span class="stepN">1</span> Verifique o serviço em <code>/health</code>.</div>
                <div class="step"><span class="stepN">2</span> Carregue um preset (alto/baixo/inválido) e clique em <b>Prever</b>.</div>
                <div class="step"><span class="stepN">3</span> Se quiser, abra <code>/docs</code> e rode o POST /predict no Swagger.</div>
              </div>
              <div class="hint">
                Os presets em <code>/demo-examples</code> seguem o padrão recomendado (lista com <b>id/label/risk/description/payload</b>).
              </div>
            </section>

            <aside class="card">
              <h2>Teste rápido (sem Swagger)</h2>
              <p>Selecione um preset de demo e clique em <b>Carregar exemplo</b>.</p>

              <label class="label">Presets de demo</label>
              <div class="row">
                <select id="demoSelect" class="select"></select>
                <button class="btn" id="btnLoad">Carregar exemplo</button>
                <button class="btn" id="btnClear">Limpar</button>
              </div>

              <label class="label" style="margin-top:12px;">Request JSON</label>
              <textarea id="payload" class="textarea"></textarea>

              <div class="row">
                <button class="btn primary" id="btnPredict">Prever</button>
              </div>

              <label class="label" style="margin-top:12px;">Resposta</label>
              <pre id="result" class="mono">Carregando presets de demo...</pre>

              <div class="hint" style="margin-top:12px;">
                <b>422</b>: saída simplificada para demo -> <b>Inválido</b>.<br/>
                <b>500</b>: erro interno (modelo/pipeline).
              </div>
            </aside>
          </main>

          <footer class="footer">
            ChurnInsight • Hackathon MVP • {MODEL_PATH.name}
          </footer>
        </div>

        <script>
          let demos = [];

          const elSelect  = document.getElementById("demoSelect");
          const elPayload = document.getElementById("payload");
          const elResult  = document.getElementById("result");

          function fillSelect() {{
            elSelect.innerHTML = "";
            for (const d of demos) {{
              const opt = document.createElement("option");
              opt.value = d.id;
              opt.textContent = d.label;
              elSelect.appendChild(opt);
            }}
          }}

          function getSelectedDemo() {{
            const id = elSelect.value;
            return demos.find(d => d.id === id) || demos[0];
          }}

          function loadSelectedIntoTextarea() {{
            if (!demos.length) return;
            const d = getSelectedDemo();
            elPayload.value = JSON.stringify(d.payload, null, 2);
          }}

          async function loadDemosFromApi() {{
            const resp = await fetch("/demo-examples", {{
              headers: {{ "Accept": "application/json" }},
              cache: "no-store"
            }});
            const data = await resp.json();

            // FORMATO NOVO: lista
            if (Array.isArray(data)) {{
              return data;
            }}

            // FORMATO ANTIGO: dict com items
            if (data && Array.isArray(data.items)) {{
              return data.items;
            }}

            // FORMATO ANTIGO: dict com labels/presets
            const labels = data.labels || {{}};
            const presets = data.presets || {{}};
            return Object.keys(presets).map(k => ({{
              id: k,
              label: labels[k] || k,
              risk: "unknown",
              description: "",
              payload: presets[k]
            }}));
          }}

          document.getElementById("btnLoad").addEventListener("click", () => {{
            if (!demos.length) {{
              elResult.textContent = "Nenhum preset disponível. Verifique /demo-examples.";
              return;
            }}
            const d = getSelectedDemo();
            elPayload.value = JSON.stringify(d.payload, null, 2);
            elResult.textContent = `Preset carregado: ${{d.label}}\\nClique em "Prever" para chamar POST /predict...`;
          }});

          elSelect.addEventListener("change", () => {{
            loadSelectedIntoTextarea();
          }});

          document.getElementById("btnClear").addEventListener("click", () => {{
            elPayload.value = "";
            elResult.textContent = "Payload limpo.";
          }});

          document.getElementById("btnPredict").addEventListener("click", async () => {{
            try {{
              const payload = JSON.parse(elPayload.value);

              elResult.textContent = "Chamando /predict...";
              const resp = await fetch("/predict", {{
                method: "POST",
                headers: {{ "Content-Type": "application/json" }},
                body: JSON.stringify(payload)
              }});

              const text = await resp.text();
              let data;
              try {{ data = JSON.parse(text); }} catch {{ data = text; }}

              elResult.textContent =
                "HTTP " + resp.status + "\\n\\n" + (typeof data === "string" ? data : JSON.stringify(data, null, 2));
            }} catch (e) {{
              elResult.textContent = "Erro: " + e.message + "\\n\\nVerifique se o JSON está válido.";
            }}
          }});

          (async () => {{
            try {{
              elResult.textContent = "Carregando presets de demo via /demo-examples...";
              demos = await loadDemosFromApi();

              if (!Array.isArray(demos) || demos.length === 0) {{
                elResult.textContent = "Nenhum preset retornado por /demo-examples.";
                return;
              }}

              fillSelect();
              loadSelectedIntoTextarea();
              const first = demos[0];
              elResult.textContent = `Preset carregado automaticamente: ${{first.label}}\\nClique em "Prever".`;
            }} catch (e) {{
              elResult.textContent = "Falha ao carregar /demo-examples: " + e.message;
            }}
          }})();
        </script>
      </body>
    </html>
    """
    return HTMLResponse(content=html)

# ==========================================
# Endpoints
# ==========================================
@app.get("/health", tags=["Status"], summary="Health check")
def health_check():
    return {
        "status": "ok",
        "modelo_carregado": model is not None,
        "model_error": MODEL_ERROR,
        "modelo_path": str(MODEL_PATH),
        "threshold": THRESHOLD,
        "meta": MODEL_META,
        "notebook_path": str(NOTEBOOK_PATH),
        "demo_source": DEMO_SOURCE,
    }

@app.get("/demo-examples", tags=["Status"], summary="Exemplos de requisição para demo (alto/baixo/inválido)")
def demo_examples():
    # NOVO PADRÃO (Passo 3): lista de objetos com risk/description
    return DEMO_EXAMPLES

@app.post("/predict", tags=["Predict"], response_model=ResultadoChurn, summary="Prever churn para um cliente (Telco)")
def predict_churn(cliente: ClienteTelco):
    try:
        dados = cliente.model_dump() if hasattr(cliente, "model_dump") else cliente.dict()
        return prever_cliente(dados)
    except RuntimeError as e:
        raise HTTPException(status_code=500, detail=str(e))
    except Exception as e:
        raise HTTPException(status_code=500, detail=f"Erro ao processar previsão: {e}")

if __name__ == "__main__":
    import uvicorn
    uvicorn.run("ds_service.app:app", host="0.0.0.0", port=8000, reload=True)

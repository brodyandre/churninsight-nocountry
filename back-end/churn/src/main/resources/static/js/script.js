(() => {
      const $ = (id) => document.getElementById(id);

      const setText = (id, txt) => { $(id).textContent = txt; };
      const setHtml = (id, html) => { $(id).innerHTML = html; };
      const pretty = (obj) => JSON.stringify(obj, null, 2);

      const safeJsonParse = (text) => {
        try { return { ok:true, value: JSON.parse(text) }; }
        catch(e){ return { ok:false, error: e.message }; }
      };

      const setHealthChip = (ok, text) => {
        const el = $("chipHealth");
        if (!el) return;
        el.textContent = text;
        el.classList.remove("warn","ok","bad");
        el.classList.add(ok ? "ok" : "bad");
      };

      const setRiskTag = (risk) => {
        const tag = $("riskTag");
        const txt = $("riskText");
        if (!tag | !txt) return;
        tag.classList.remove("high", "low", "invalid", "custom");

        if (risk === "high"){
          tag.classList.add("high"); txt.textContent = "Alto risco";
        } else if (risk === "low"){
          tag.classList.add("low"); txt.textContent = "Baixo risco";
        } else if (risk === "invalid"){
          tag.classList.add("invalid"); txt.textContent = "Inválido";
        } else {
          tag.classList.add("custom"); txt.textContent = "Customizado";
        }
      };

      // Tabs
      const activateTab = (tabId) => {
        document.querySelectorAll(".tabbtn").forEach(b => {
          b.classList.toggle("active", b.dataset.tab === tabId);
        });

        document.querySelectorAll(".tabpanel").forEach(p => {
          p.classList.toggle("active", p.id === tabId);
        });
      };

      // State
      const state = {
        demos: [],
        currentDemo: null,
        payload: null
      };

      // Form map
      const FIELD_MAP = [
        ["gender", "f_gender", "string"],
        ["SeniorCitizen", "f_SeniorCitizen", "int"],
        ["Partner", "f_Partner", "string"],
        ["Dependents", "f_Dependents", "string"],
        ["tenure", "f_tenure", "int"],
        ["PhoneService", "f_PhoneService", "string"],
        ["MultipleLines", "f_MultipleLines", "string"],
        ["InternetService", "f_InternetService", "string"],
        ["OnlineSecurity", "f_OnlineSecurity", "string"],
        ["OnlineBackup", "f_OnlineBackup", "string"],
        ["DeviceProtection", "f_DeviceProtection", "string"],
        ["TechSupport", "f_TechSupport", "string"],
        ["StreamingTV", "f_StreamingTV", "string"],
        ["StreamingMovies", "f_StreamingMovies", "string"],
        ["Contract", "f_Contract", "string"],
        ["PaperlessBilling", "f_PaperlessBilling", "string"],
        ["PaymentMethod", "f_PaymentMethod", "string"],
        ["MonthlyCharges", "f_MonthlyCharges", "float"],
        ["TotalCharges", "f_TotalCharges", "float"]
      ];

      const readFormToPayload = () => {
        const p = {};

        FIELD_MAP.forEach(([k, id, t]) => {
          const el = $(id);
          if (!el) return;

          let v = el.value;
          if (t === "int") {
            v = (v === "" || v === null) ? 0 : parseInt(v, 10);

            if (!Number.isFinite(v)) v = 0;
          }

          if (t === "float") {
            v = (v === "" || v === null) ? 0 : parseFloat(v);

            if (!Number.isFinite(v)) v = 0;
          }

          p[k] = v;
        });

        return p;
      };

      const applyPayloadToForm = (payload) => {
        if (!payload) return;

        FIELD_MAP.forEach(([k, id]) => {
          if (!(k in payload)) return;

          const el = $(id);
          if (el) {
            el.value = (payload[k] === null || payload[k] === undefined) ? "" : String(payload[k]);
          }
        });
      };

      const syncPayloadToJson = () => {
        const el = $("payloadJson");
        if (el) el.value = pretty(state.payload ?? {});
      };

      const syncJsonToPayload = () => {
        const jsonValue = $("payloadJson").value || "";
        const parsed = safeJsonParse(jsonValue);

        if (!parsed.ok) return { ok: false, error: parsed.error };

        state.payload = parsed.value;
        applyPayloadToForm(state.payload);

        setRiskTag("custom");
        $("demoHint").textContent = "Payload customizado aplicado a partir do JSON.";

        return { ok:true };
      };

      // Predict (CORRIGIDO: preset inválido deve enviar payload RAW e retornar 422/Inválido)
      const predict = async () => {
        const selected = getSelectedDemo();
        const isInvalidPreset = !!(selected && (selected.risk === "invalid"));

        let payloadToSend;

        const isAdvancedMode = document.querySelector("details").open;

        if (isInvalidPreset || isAdvancedMode) {
          const jsonText = $("payloadJson").value;
          const parsed = safeJsonParse(jsonText);

          if (!parsed.ok) {
            $("result").style.color = "var(--bad)";
            $("result").textContent = "Erro: o JSON que você editou está com erro de sintaxe.\n" + parsed.error;

            return;
          }

          payloadToSend = parsed.value;
        } else {
          payloadToSend = readFormToPayload();
        }

        state.payload = payloadToSend;

        if (!isInvalidPreset) setRiskTag("custom");

        $("result").textContent = "Chamando /churn/predict ...";
        $("result").style.color = "var(--text)";

        const t0 = performance.now();

        try{
          const resp = await fetch("/churn/predict", {
            method: "POST",
            headers: { "Content-Type": "application/json" },
            body: JSON.stringify(payloadToSend)
          });

          const body = await resp.json();

          const t1 = performance.now();
          const clientMs = Math.max(0, t1 - t0);
          $("kpiPredictLatency").textContent = `Latência predict: ${clientMs.toFixed(0)} ms`;

          const header = `HTTP ${resp.status} ${resp.ok ? "(OK)" : "(ERRO)"} • ${clientMs.toFixed(0)} ms`;

          if (!resp.ok) {
            $("result").style.color = "var(--bad)";

            let errorDetail = "";
            if (Array.isArray(body.details)) {
              errorDetail = "\n" + body.details
                .map(m => ` - ${m}`)
                .join("\n");
            } else if (body.details && typeof body.details === "object"){
              // Erro de validação de campos (MethodArgumentNotValidException)
              errorDetail = "nCampos inválidos:\n" + Object.entries(body.details)
                  .map(([field, msg]) => ` - ${field}: ${msg}`)
                  .join("\n");
            } else {
              // Erro genérico ou de integração (IntegrationException)
              errorDetail = `\nDetalhe: ${body.details || "Sem detalhes adicionais"}`;
            }

            $("result").textContent = `${header}\n${body.message || "Erro: "}${errorDetail}`;

            return;
          }

          // Sucesso
          if (body && "probabilidade" in body){
            const p = Number(body.probabilidade);
            const pct = (p * 100).toFixed(2).replace(".", ",") + "%";

            $("result").textContent = `${header}\n\n${pretty(body)}\n\nResumo:\n- Previsão: ${body.previsao}\n- Probabilidade: ${pct}`;
          } else {
            $("result").textContent = `${header}\n\n${pretty(body)}`;
          }

        } catch(e){
          $("result").style.color = "var(--bad)";
          $("result").textContent = "Falha crítica na requisição.\n\nDetalhe: " + e.message;
        }
      }

      // Demos
      const getSelectedDemo = () => $("demoSelect") ? state.demos.find(d => d.id === $("demoSelect").value) : null;

      const fillSelect = () => {
        const sel = $("demoSelect");

        if (!sel) return;

        sel.innerHTML = "";
        state.demos.forEach(d => {
          const opt = document.createElement("option");
          opt.value = d.id;
          opt.textContent = d.label || d.id;
          sel.appendChild(opt);
        });
      };

      const applyDemo = (demo) => {
        if (!demo) return;

        state.payload = JSON.parse(JSON.stringify(demo.payload));

        applyPayloadToForm(state.payload);
        syncPayloadToJson();

        setRiskTag(demo.risk || "custom");
        $("result").textContent = "Preset carregado. Clique em Prever.";
        $("result").style.color = "var(--text)";

        // opcional: ao carregar demo, abre a aba mais relevante
        if (demo.risk === "high" || demo.risk === "low" || demo.risk === "invalid") activateTab("tab_basic");
      }

      const loadDemos = async () => {
        const resp = await fetch("/churn/demo-examples");

        return await resp.json();
      }

      // Health
      const checkHealth = async () => {
        // Check Spring Boot
        try{
          const springResp = await fetch("/churn/health", { cache: "no-store" });
          const springJson = await springResp.json().catch(() => ({}));

          $("kpiSpring").textContent = `Spring: ${springResp.ok
            ? "OK"
            : "(ERRO)"} (HTTP ${springResp.status})`;

        } catch(e){
          $("kpiSpring").textContent = "Spring: indisponível";
          setHealthChip(false, "Spring OFF");

          return;
        }

        // Check DS (FastAPI) via Proxy Java
        try {
          const dsResp = await fetch("/churn/ds-health", { cache: "no-store" });
          const dsText = await dsResp.text();

          let dsJson;
          try {
            dsJson = JSON.parse(dsText);
          } catch (e) {
            console.error("Erro ao parsear o JSON: ", e);
            dsJson = { status: "offline" };
          }

          // Pega a URL do Data Science
          const dsUrl = dsJson.ds_service_url || dsJson.dsServiceUrl || "-";
          const el = document.getElementById("chipDsUrl");

          if (el) {
            el.innerHTML = `DS: <code>${dsUrl}</code>`;
          } else {
            console.error("Erro: O elemento com ID 'chipDsUrl' não foi encontrado no HTML.");
          }

          // Pega latência injetada no Header
          const proxyMs = dsResp.headers.get("X-Proxy-Latency-Ms");
          $("kpiDsLatency").textContent = `Latência proxy: ${proxyMs ? (proxyMs + " ms") : "—"}`;

          // Lógica de status baseada no JSON retornado pelo Java
          const isOnline = dsResp.ok && dsJson.status != "offline";

          if (isOnline) {
            $("kpiDs").textContent = `FastAPI: OK (HTTP ${dsResp.status})`;
            setHealthChip(true, "Health: OK");
          } else {
            $("kpiDs").textContent = "FastAPI: OFFLINE";
            $("kpiDs").style.color = "var(--danger)";
            setHealthChip(false, "Health: DS off");
          }

          // Atualiza KPIs de Threshold e Modelo
          const thrNum = parseFloat(dsJson && dsJson.threshold);
          $("kpiThreshold").textContent = `Threshold: ${(!isNaN(thrNum) && Number.isFinite(thrNum)) ? thrNum.toFixed(2) : "—"}`;

          // KPI modelo
          const rawPath = dsJson.model_path || "";
          const modelName = (rawPath && rawPath !== "indisponível" && rawPath !== "nenhum")
            ? rawPath.split(/[\\/]/).pop() 
            : "—";
          $("kpiModel").textContent = `Modelo: ${modelName}`;

        } catch(e){
          console.error("Erro fatal no fetch do Health: ", e);
          setHealthChip(false, "Health: DS off.");
          $("kpiDs").textContent = "FastAPI: indisponível";
          $("kpiDsLatency").textContent = "Latência proxy: —";
        }
      };

      // Eventos de Botões
      $("btnPredict").addEventListener("click", predict);

      $("btnLoad").addEventListener("click", () => applyDemo(getSelectedDemo()));

      $("demoSelect").addEventListener("change", () => {
        const d = getSelectedDemo();
        if (!d) return;
        $("demoHint").textContent = `Selecionado: ${d.label || d.id}. Clique em “Carregar”.`;
      });

      $("btnHealth").addEventListener("click", async () => {
        $("result").textContent = "Rechecando health...";
        await checkHealth();
        $("result").textContent = "Health atualizado.";
      });

      $("btnReset").addEventListener("click", () => {
        if (state.currentDemo) {
          applyDemo(state.currentDemo);
          return;
        } else {
          state.payload = readFormToPayload();
          syncPayloadToJson();
          setRiskTag("custom");
        }

        $("result").textContent = "Formulário resetado.";
        $("result").style.color = "var(--text)";
      });

      // Funções auxiliares do JSON
      $("btnCopyJson").addEventListener("click", async () => {
        try {
          await navigator.clipboard.writeText($("payloadJson").value);
          $("result").style.color = "var(--text)";
          $("result").textContent = "JSON copiado para a área de transferência.";

        } catch(e){
          $("result").style.color = "var(--bad)";
          $("result").textContent = "Erro ao copiar JSON.";
        }
      });

      $("btnFormatJson").addEventListener("click", () => {
        const parsed = safeJsonParse($("payloadJson").value);

        if (parsed.ok) $("payloadJson").value = pretty(parsed.value);
      });

      $("btnApplyJson").addEventListener("click", () => {
        const p = safeJsonParse($("payloadJson").value);
        if (p.ok) {
          state.payload = p.value;
          applyPayloadToForm(state.payload);
          $("result").style.color = "var(--text)";
          $("result").textContent = "JSON aplicado!";
        } else {
          $("result").textContent = "Erro: JSON inválido.\n\nDetalhe: " + p.error;
          $("result").style.color = "var(--bad)";
        }
      });

      // Observadores de mudança no formulário
      // Form change -> custom + sync JSON
      for (const [, id] of FIELD_MAP){
        const el = $(id);
        if (el) {
          el.addEventListener("change", () => {
            state.payload = readFormToPayload();
            syncPayloadToJson();
            setRiskTag("custom");
          });
        }
      }

      // Tab buttons
      document.querySelectorAll(".tabbtn").forEach(b => {
        b.addEventListener("click", () => activateTab(b.dataset.tab));
      });

      // Inicialização (self-invoking)
      (async () => {
        try {
          $("result").textContent = "Iniciando sistema...";

          await checkHealth().catch(err => console.log("Health offline, usando modo manual."));

          try {
            const data = await loadDemos();

            state.demos = Array.isArray(data) ? data : [];

            if (state.demos.length > 0){
              fillSelect();
              $("demoSelect").value = state.demos[0].id;
              applyDemo(state.demos[0]);
              $("result").textContent = "Presets carregados com sucesso.";
            } else {
              $("result").textContent = "Nenhum preset retornado por /churn/demo-examples.";
            }
          } catch(demoErr) {
            console.warn("Sem presets disponíveis: ", demoErr);
            $("result").textContent = "Pronto para entrada manual.";
          }

        } catch(e){
          setHealthChip(false, "Falha na inicialização.");
          $("result").textContent = "Erro:" + e.message;
        }
      })();
    })();
(() => {
    // --- 1. SELETORES E UTILITÁRIOS ---
    const $ = (id) => document.getElementById(id);
    
    const UI = {
        setTxt: (id, txt) => { const el = $(id); if(el) el.textContent = txt; },
        setHtml: (id, html) => { const el = $(id); if(el) el.innerHTML = html; },
        setColor: (id, color) => { const el = $(id); if(el) el.style.color = `var(--${color})`; },
        pretty: (obj) => JSON.stringify(obj, null, 2)
    };

    // --- 2. CONFIGURAÇÃO DE MAPEAMENTO DO FORMULÁRIO ---
    const FIELD_MAP = [
        ["gender", "f_gender", "string"], ["SeniorCitizen", "f_SeniorCitizen", "int"],
        ["Partner", "f_Partner", "string"], ["Dependents", "f_Dependents", "string"],
        ["tenure", "f_tenure", "int"], ["PhoneService", "f_PhoneService", "string"],
        ["MultipleLines", "f_MultipleLines", "string"], ["InternetService", "f_InternetService", "string"],
        ["OnlineSecurity", "f_OnlineSecurity", "string"], ["OnlineBackup", "f_OnlineBackup", "string"],
        ["DeviceProtection", "f_DeviceProtection", "string"], ["TechSupport", "f_TechSupport", "string"],
        ["StreamingTV", "f_StreamingTV", "string"], ["StreamingMovies", "f_StreamingMovies", "string"],
        ["Contract", "f_Contract", "string"], ["PaperlessBilling", "f_PaperlessBilling", "string"],
        ["PaymentMethod", "f_PaymentMethod", "string"], ["MonthlyCharges", "f_MonthlyCharges", "float"],
        ["TotalCharges", "f_TotalCharges", "float"]
    ];

    // --- 3. ESTADO GLOBAL DA APLICAÇÃO ---
    const state = {
        demos: [],
        payload: {},
        get currentSelectedDemo() {
            return this.demos.find(d => d.id === $("demoSelect")?.value);
        }
    };

    // --- 4. FUNÇÕES DE AÇÃO (LOGICA DE NEGÓCIO) ---
    const Actions = {
        // Sincroniza o que está nos inputs para o objeto state.payload e para o textarea JSON
        syncFormToPayload() {
            FIELD_MAP.forEach(([key, id, type]) => {
                const el = $(id);
                if (!el) return;
                let val = el.value;
                if (type === "int") val = parseInt(val) || 0;
                if (type === "float") val = parseFloat(val) || 0;
                state.payload[key] = val;
            });
            const jsonEl = $("payloadJson");
            if (jsonEl) jsonEl.value = UI.pretty(state.payload);
            
            // Quando o usuário mexe no form, o risco torna-se "custom"
            Actions.setRiskTag("custom");
        },

        // Aplica um objeto JSON nos inputs do formulário
        applyPayloadToForm(payload) {
            if (!payload) return;
            state.payload = { ...payload };
            FIELD_MAP.forEach(([key, id]) => {
                const el = $(id);
                if (el) el.value = (payload[key] === null || payload[key] === undefined) ? "" : String(payload[key]);
            });
            const jsonEl = $("payloadJson");
            if (jsonEl) jsonEl.value = UI.pretty(state.payload);
        },

        // Gerencia as classes CSS das tags de risco
        setRiskTag(risk) {
            const tag = $("riskTag");
            const txt = $("riskText");
            if (!tag || !txt) return;
            tag.classList.remove("high", "low", "invalid", "custom");

            const map = {
                high: "Alto risco",
                low: "Baixo risco",
                invalid: "Inválido",
                custom: "Customizado"
            };

            tag.classList.add(risk || "custom");
            txt.textContent = map[risk] || "Customizado";
        },

        // Atualiza os indicadores de saúde (Spring e FastAPI)
        async updateHealth() {
            UI.setColor("result", "text");

            // 1. Health do Spring (Java)
            try {
                const sResp = await fetch("/churn/health", { cache: "no-store" });
                UI.setTxt("kpiSpring", `Spring: ${sResp.ok ? "OK" : "Indisponível"} (HTTP ${sResp.status})`);
                UI.setColor("kpiSpring", sResp.ok ? "ok" : "bad");
            } catch(e) { UI.setTxt("kpiSpring", "Spring: Indisponível"); }

            // 2. Health do DS (FastAPI via Java)
            try {
                const resp = await fetch("/churn/ds-health", { cache: "no-store" });
                const data = await resp.json();
                const isOnline = resp.ok && data.status !== "OFFLINE";

                UI.setTxt("kpiDs", isOnline ? `FastAPI: OK (HTTP ${resp.status})` : "FastAPI: OFFLINE");
                UI.setColor("kpiDs", isOnline ? "ok" : "bad");
                
                const chip = $("chipHealth");
                if (chip) {
                    chip.textContent = isOnline ? "Health: OK" : "Health: DS OFF";
                    chip.className = `chip ${isOnline ? "ok" : "bad"}`;
                }

                if (isOnline) {
                    UI.setHtml("chipDsUrl", `DS URL: <code>${data.ds_service_url || "-"}</code>`);
                } else {
                    UI.setHtml("chipDsUrl", `DS URL: <span>—</span>`)
                }
                
                const proxyMs = resp.headers.get("X-Proxy-Latency-Ms");
                UI.setTxt("kpiDsLatency", `Latência proxy: ${proxyMs ? (proxyMs + " ms") : "—"}`);

                const thr = parseFloat(data.threshold);
                UI.setTxt("kpiThreshold", `Threshold: ${(!isNaN(thr) && isFinite(thr)) ? thr.toFixed(2) : "—"}`);

                const rawPath = data.modelo_path || data.model_path || "";
                const modelName = (rawPath && rawPath !== "Indisponível") ? rawPath.split(/[\\/]/).pop() : "—";
                UI.setTxt("kpiModel", `Modelo: ${modelName}`);
            } catch(e) {
                UI.setTxt("kpiDs", "FastAPI: Indisponível");
                UI.setColor("kpiDs", "bad");

                const chip = $("chipHealth");
                if (chip) { chip.textContent = "Health: DS OFF"; chip.className = "chip bad"; }

                const chipDsUrl = $("chipDsUrl");
                if (chipDsUrl) { chipDsUrl.innerHTML = "DS: —"; }
            }
        }
    };

    // --- 5. EVENTOS ---
    const bindEvents = () => {
        // Predict
        $("btnPredict")?.addEventListener("click", async () => {
            UI.setTxt("result", "Processando predição...");
            UI.setColor("result", "text");

            const isAdvanced = document.querySelector("details")?.open;
            let payload;

            try {
                payload = isAdvanced ? JSON.parse($("payloadJson").value) : state.payload;
            } catch (e) {
                UI.setColor("result", "bad");
                UI.setTxt("result", "Erro: JSON inválido no editor.\n" + e.message);
                return;
            }

            const t0 = performance.now();
            try {
                const resp = await fetch("/churn/predict", {
                    method: "POST",
                    headers: { "Content-Type": "application/json" },
                    body: JSON.stringify(payload)
                });

                const body = await resp.json();
                const ms = (performance.now() - t0).toFixed(0);
                UI.setTxt("kpiPredictLatency", `Latência predict: ${ms} ms`);

                if (!resp.ok) {
                    UI.setColor("result", "bad");
                    const detail = body.details
                        ? UI.pretty(body.details).replace(/\\n/g, "\n").replace(/^"|"$/g, '')
                        : "Sem detalhes";
                    UI.setTxt("result", `Erro ${resp.status}: ${body.message || "Erro"}\n${detail}`);
                    return;
                }

                if (body && body.probabilidade !== undefined) {
                    const prob = (Number(body.probabilidade) * 100).toFixed(2).replace(".", ",") + "%";
                    UI.setTxt("result", `Sucesso! [${ms}ms]\n\nPrevisão: ${body.previsao}\nProbabilidade de cancelar: ${prob}\n\nJSON Response:\n${UI.pretty(body)}`);
                } else {
                    UI.setTxt("result", UI.pretty(body));
                }
            } catch (e) {
                UI.setColor("result", "bad");
                UI.setTxt("result", "Falha na conexão: " + e.message);
            }
        });

        // BOTÃO RESET
        $("btnReset")?.addEventListener("click", () => {
            UI.setColor("result", "text");
            const demo = state.currentSelectedDemo;
            if (demo) {
                Actions.applyPayloadToForm(demo.payload);
                Actions.setRiskTag(demo.risk);
                UI.setTxt("result", "Formulário resetado para o preset atual.");
            } else {
                Actions.applyPayloadToForm({});
                Actions.setRiskTag("custom");
                UI.setTxt("result", "Formulário limpo.");
            }
        });

        // Abas
        document.querySelectorAll(".tab").forEach(tabLabel => {
            tabLabel.addEventListener("click", () => {
                const tabId = tabLabel.getAttribute("data-tab");
                
                document.querySelectorAll(".tabpanel").forEach(p => p.classList.remove("active"));

                const activePanel = $(tabId);
                if (activePanel) activePanel.classList.add("active");
            });
        });

        // Carregar Preset
        $("btnLoad")?.addEventListener("click", () => {
            const demo = state.currentSelectedDemo;
            if (demo) {
                Actions.applyPayloadToForm(demo.payload);
                Actions.setRiskTag(demo.risk);
                UI.setTxt("result", "Preset carregado.");
                UI.setColor("result", "text");
            }
        });

        // Utilitários JSON
        $("btnSyncJson")?.addEventListener("click", async () => {
            const jsonEl = $("payloadJson");

            UI.setColor("result", "text");

            try {
                const currentObj = JSON.parse(jsonEl.value);
                const formattedJson = UI.pretty(currentObj);

                Actions.applyPayloadToForm(currentObj);

                await navigator.clipboard.writeText(formattedJson);

                UI.setTxt("result", "JSON formatado e aplicado ao formulário.");

            } catch (error) {
                UI.setColor("result", "bad");
                UI.setColor("result", "Erro no processamento: JSON inválido.")
            }
        });

        $("btnHealth")?.addEventListener("click", async () => {
            UI.setColor("result", "text");
            UI.setTxt("result", "Atualizando health...");
            await Actions.updateHealth();
            UI.setTxt("result", "Health atualizado.");
        });

        // Sincronização automática do formulário
        FIELD_MAP.forEach(([, id]) => {
            $(id)?.addEventListener("change", Actions.syncFormToPayload);
        });

        // Sincronização automática do editor JSON
        $("payloadJson")?.addEventListener("input", () => {
            Actions.setRiskTag("custom");
        });
    };

    // --- 6. INICIALIZAÇÃO ---
    (async () => {
        if (state.demos.length > 0) return;

        bindEvents();
        await Actions.updateHealth();
        
        try {
            const resp = await fetch("/churn/demo-examples");
            const data = await resp.json();
            state.demos = Array.isArray(data) ? data : [];
            
            if (state.demos.length > 0) {
                const sel = $("demoSelect");
                sel.innerHTML = "";
                state.demos.forEach(d => sel.add(new Option(d.label || d.id, d.id)));
                Actions.applyPayloadToForm(state.demos[0].payload);
                Actions.setRiskTag(state.demos[0].risk);
                UI.setTxt("result", "Presets carregados.");
            }
        } catch(e) { UI.setTxt("result", "Pronto."); }
    })();
})();
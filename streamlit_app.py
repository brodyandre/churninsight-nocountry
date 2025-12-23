# streamlit_app.py
# Painel de clientes prioritários de churn – ChurnInsight

import streamlit as st
import pandas as pd
import numpy as np
import joblib
from pathlib import Path
from io import BytesIO


# ---------------------------------------------------------
# 1. Configuração básica da página
# ---------------------------------------------------------
st.set_page_config(
    page_title="ChurnInsight – Painel de Clientes Prioritários",
    layout="wide",
    initial_sidebar_state="expanded",
)

# ---------------------------------------------------------
# 1.1 Estilo customizado (hover em multiselects)
# ---------------------------------------------------------
# Este CSS afeta os itens de dropdown dos componentes de seleção.
# Quando o mouse passa por cima de uma opção, ela é destacada em vermelho.
st.markdown(
    """
    <style>
    /* Destacar opções de selects/multiselects ao passar o mouse */
    div[data-baseweb="select"] div[role="option"]:hover {
        background-color: #ff4d4f !important;  /* vermelho */
        color: #ffffff !important;             /* texto branco */
    }
    </style>
    """,
    unsafe_allow_html=True,
)


# ---------------------------------------------------------
# 2. Funções auxiliares (carregamento de dados e modelo)
# ---------------------------------------------------------
@st.cache_data(show_spinner=True)
def carregar_dados_e_modelo():
    """
    Carrega o dataset original de churn da Telco, aplica os mesmos
    tratamentos usados no notebook e gera a coluna de probabilidade
    de churn usando o pipeline salvo em joblib.
    """

    base_path = Path(__file__).resolve().parent

    # Caminho do CSV original
    caminho_csv = base_path / "data" / "raw" / "WA_Fn-UseC_-Telco-Customer-Churn.csv"

    # Caminho do modelo salvo (pipeline completo)
    caminho_modelo = base_path / "model" / "churn_xgboost_pipeline.joblib"

    # --- Carregar dados ---
    df = pd.read_csv(caminho_csv)

    # Tratamento de TotalCharges (igual ao notebook)
    df["TotalCharges"] = pd.to_numeric(
        df["TotalCharges"].replace(" ", np.nan),
        errors="coerce",
    )
    df["TotalCharges"] = df["TotalCharges"].fillna(df["TotalCharges"].median())

    # Remover linhas sem target
    df = df.dropna(subset=["Churn"])

    # Target numérico (0/1) apenas para métricas globais
    y = df["Churn"].map({"No": 0, "Yes": 1})

    # Features (sem customerID e sem Churn)
    X = df.drop(columns=["customerID", "Churn"])

    # --- Carregar modelo e obter probabilidade de churn ---
    modelo = joblib.load(caminho_modelo)

    prob_churn = modelo.predict_proba(X)[:, 1]
    df["prob_churn"] = prob_churn

    # Definir faixas de risco (valores vindos da calibração)
    thr_medio = 0.45
    thr_alto = 0.75

    def classificar_risco(prob):
        if prob >= thr_alto:
            return "alto"
        elif prob >= thr_medio:
            return "medio"
        else:
            return "baixo"

    df["risco"] = df["prob_churn"].apply(classificar_risco)

    # Guardar também o target numérico
    df["y_true"] = y.values

    return df, thr_medio, thr_alto


# Carrega dados e modelo (em cache)
df, thr_medio, thr_alto = carregar_dados_e_modelo()


# ---------------------------------------------------------
# 3. Título principal
# ---------------------------------------------------------
st.title("ChurnInsight – Painel de Clientes Prioritários")

st.caption(
    "Use os filtros à esquerda para focar em segmentos específicos de clientes "
    "e priorizar ações de retenção com base em risco de churn e impacto financeiro."
)


# ---------------------------------------------------------
# 4. Métricas de topo – VISÃO GLOBAL (base inteira)
# ---------------------------------------------------------
total_clientes = len(df)
taxa_global_churn = df["y_true"].mean()  # média de 0/1

# Quantidade de clientes em alto e médio risco (global)
clientes_alto = df[df["risco"] == "alto"]
clientes_medio = df[df["risco"] == "medio"]

qtd_alto = len(clientes_alto)
qtd_medio = len(clientes_medio)

pct_alto = qtd_alto / total_clientes if total_clientes > 0 else 0.0
pct_medio = qtd_medio / total_clientes if total_clientes > 0 else 0.0

st.markdown("### Visão geral da carteira (base completa)")

col1, col2, col3, col4 = st.columns(4)

with col1:
    st.metric(
        "Total de clientes",
        f"{total_clientes:,}".replace(",", "."),
    )

with col2:
    st.metric(
        "Taxa global de churn (histórico)",
        f"{taxa_global_churn * 100:,.1f}%".replace(",", "."),
    )

with col3:
    st.metric(
        "Clientes em ALTO risco",
        f"{qtd_alto} ({pct_alto * 100:.1f}%)",
    )

with col4:
    st.metric(
        "Clientes em MÉDIO risco",
        f"{qtd_medio} ({pct_medio * 100:.1f}%)",
    )

st.markdown("---")


# ---------------------------------------------------------
# 5. Filtros (sidebar)
# ---------------------------------------------------------
with st.sidebar:
    st.header("Configuração de filtros")

    # Filtro por risco (ZERADO por padrão)
    risco_opcoes = ["alto", "medio", "baixo"]
    risco_selecionado = st.multiselect(
        "Nível de risco",
        options=risco_opcoes,
        default=[],  # nenhum risco selecionado inicialmente
        help="Selecione os níveis de risco que deseja priorizar.",
    )

    # Slider de probabilidade mínima (0% = sem filtro)
    prob_min_percent = st.slider(
        "Probabilidade mínima de churn (%)",
        min_value=0.0,
        max_value=100.0,
        value=0.0,
        step=1.0,
        help="Exibe apenas clientes com probabilidade de churn maior ou igual a este valor.",
    )

    st.markdown("---")

    # Filtro por tipo de contrato (ZERADO por padrão)
    contratos_unicos = sorted(df["Contract"].unique())
    contratos_selecionados = st.multiselect(
        "Tipo de contrato",
        options=contratos_unicos,
        default=[],  # nenhum contrato selecionado inicialmente
    )

    st.markdown("---")

    # Filtro por tenure mínimo
    tenure_min = int(df["tenure"].min())
    tenure_max = int(df["tenure"].max())

    tenure_filtro = st.slider(
        "Tempo de casa (tenure) mínimo (meses)",
        min_value=tenure_min,
        max_value=tenure_max,
        value=tenure_min,  # valor mínimo = sem restrição extra
        step=1,
    )

    st.markdown("---")

    # -----------------------------------------------------
    # Filtros avançados (opcional)
    # -----------------------------------------------------
    with st.expander("Filtros avançados (opcional)", expanded=False):
        st.caption("Use estes filtros apenas quando precisar de um recorte mais específico.")

        # SeniorCitizen (0 / 1) – ZERADO
        senior_opts = st.multiselect(
            "SeniorCitizen (0 = não, 1 = sim)",
            options=sorted(df["SeniorCitizen"].unique().tolist()),
            default=[],
        )

        # InternetService – ZERADO
        internet_opts = st.multiselect(
            "InternetService",
            options=sorted(df["InternetService"].dropna().unique().tolist()),
            default=[],
        )

        # PaymentMethod – ZERADO
        payment_opts = st.multiselect(
            "PaymentMethod",
            options=sorted(df["PaymentMethod"].dropna().unique().tolist()),
            default=[],
        )

        # PaperlessBilling – ZERADO
        paperless_opts = st.multiselect(
            "PaperlessBilling",
            options=sorted(df["PaperlessBilling"].dropna().unique().tolist()),
            default=[],
        )

        # Faixa de MonthlyCharges (por padrão, toda a faixa)
        mc_min = float(df["MonthlyCharges"].min())
        mc_max = float(df["MonthlyCharges"].max())
        faixa_mensalidade = st.slider(
            "Faixa de MonthlyCharges (R$)",
            min_value=mc_min,
            max_value=mc_max,
            value=(mc_min, mc_max),
            step=5.0,
        )

    st.markdown("---")
    st.caption(
        "Dica: comece filtrando por **alto risco + alta probabilidade** para focar "
        "nos clientes com maior chance de cancelar e maior impacto financeiro."
    )


# ---------------------------------------------------------
# 6. Aplicação dos filtros – base filtrada (df_filt)
# ---------------------------------------------------------
df_filt = df.copy()

# 1) filtro por risco
if risco_selecionado:
    df_filt = df_filt[df_filt["risco"].isin(risco_selecionado)]

# 2) filtro por probabilidade mínima
df_filt = df_filt[df_filt["prob_churn"] >= (prob_min_percent / 100.0)]

# 3) filtro por contrato
if contratos_selecionados:
    df_filt = df_filt[df_filt["Contract"].isin(contratos_selecionados)]

# 4) filtro por tenure mínimo
df_filt = df_filt[df_filt["tenure"] >= tenure_filtro]

# 5) filtros avançados
# SeniorCitizen
if senior_opts:
    df_filt = df_filt[df_filt["SeniorCitizen"].isin(senior_opts)]

# InternetService
if internet_opts:
    df_filt = df_filt[df_filt["InternetService"].isin(internet_opts)]

# PaymentMethod
if payment_opts:
    df_filt = df_filt[df_filt["PaymentMethod"].isin(payment_opts)]

# PaperlessBilling
if paperless_opts:
    df_filt = df_filt[df_filt["PaperlessBilling"].isin(paperless_opts)]

# Faixa de MonthlyCharges
df_filt = df_filt[
    (df_filt["MonthlyCharges"] >= faixa_mensalidade[0])
    & (df_filt["MonthlyCharges"] <= faixa_mensalidade[1])
]


# ---------------------------------------------------------
# 7. Métricas – VISÃO DO RECORTE FILTRADO
# ---------------------------------------------------------
st.markdown("### Visão do subconjunto filtrado")

total_filtrado = len(df_filt)
taxa_churn_filtrada = df_filt["y_true"].mean() if total_filtrado > 0 else 0.0

filtrado_alto = df_filt[df_filt["risco"] == "alto"]
filtrado_medio = df_filt[df_filt["risco"] == "medio"]

qtd_alto_f = len(filtrado_alto)
qtd_medio_f = len(filtrado_medio)

pct_alto_f = qtd_alto_f / total_filtrado if total_filtrado > 0 else 0.0
pct_medio_f = qtd_medio_f / total_filtrado if total_filtrado > 0 else 0.0

colf1, colf2, colf3, colf4 = st.columns(4)

with colf1:
    st.metric(
        "Clientes filtrados",
        f"{total_filtrado:,}".replace(",", "."),
    )

with colf2:
    st.metric(
        "Taxa de churn (subconjunto)",
        f"{taxa_churn_filtrada * 100:,.1f}%".replace(",", ".")
        if total_filtrado > 0
        else "0,0%",
    )

with colf3:
    st.metric(
        "Filtrados em ALTO risco",
        f"{qtd_alto_f} ({pct_alto_f * 100:.1f}%)"
        if total_filtrado > 0
        else "0 (0,0%)",
    )

with colf4:
    st.metric(
        "Filtrados em MÉDIO risco",
        f"{qtd_medio_f} ({pct_medio_f * 100:.1f}%)"
        if total_filtrado > 0
        else "0 (0,0%)",
    )

st.markdown("---")


# ---------------------------------------------------------
# 8. Construção da fila priorizada
# ---------------------------------------------------------
if df_filt.empty:
    st.warning(
        "Nenhum cliente encontrado com os filtros atuais. "
        "Tente selecionar pelo menos um critério (por exemplo, algum nível de risco)."
    )
else:
    # Impacto financeiro aproximado: probabilidade * mensalidade
    df_filt = df_filt.copy()
    df_filt["impacto_mensal"] = df_filt["prob_churn"] * df_filt["MonthlyCharges"]

    # Ordenar riscos: alto (0) < medio (1) < baixo (2)
    ordem_risco = {"alto": 0, "medio": 1, "baixo": 2}
    df_filt["ordem_risco"] = df_filt["risco"].map(ordem_risco)

    df_fila = df_filt.sort_values(
        by=["ordem_risco", "impacto_mensal"],
        ascending=[True, False],
    )

    # Colunas a exibir na tabela
    colunas_exibir = [
        "customerID",
        "Churn",
        "prob_churn",
        "risco",
        "MonthlyCharges",
        "TotalCharges",
        "tenure",
        "Contract",
        "InternetService",
        "PaymentMethod",
        "impacto_mensal",
    ]

    df_exib = df_fila[colunas_exibir].copy()

    # -----------------------------------------------------
    # Arredondar colunas numéricas (2 casas decimais)
    # -----------------------------------------------------
    df_exib["prob_churn"] = (df_exib["prob_churn"] * 100).round(2)  # em %
    df_exib["MonthlyCharges"] = df_exib["MonthlyCharges"].round(2)
    df_exib["TotalCharges"] = df_exib["TotalCharges"].round(2)
    df_exib["impacto_mensal"] = df_exib["impacto_mensal"].round(2)

    # Renomear colunas para exibição (inclui rótulo novo do impacto financeiro)
    df_exib = df_exib.rename(
        columns={
            "customerID": "customerID",
            "Churn": "Churn",
            "prob_churn": "prob_churn(%)",
            "risco": "risco",
            "MonthlyCharges": "MonthlyCharges",
            "TotalCharges": "TotalCharges",
            "tenure": "tenure",
            "Contract": "Contract",
            "InternetService": "InternetService",
            "PaymentMethod": "PaymentMethod",
            "impacto_mensal": "Impacto_Financeiro_(R$/mês)",
        }
    )

    # ---------------------------------------------------------
    # 8.1. Semáforo de cores na coluna de risco (apenas visual)
    # ---------------------------------------------------------
    def risco_para_emoji(valor: str) -> str:
        if valor == "alto":
            return "🔴 alto"
        elif valor == "medio":
            return "🟡 médio"
        elif valor == "baixo":
            return "🟢 baixo"
        return str(valor)

    # DataFrame para exibição (com emoji em risco)
    df_exib_display = df_exib.copy()
    df_exib_display["risco"] = df_exib_display["risco"].map(risco_para_emoji)

    # ---------------------------------------------------------
    # 8.2. Gradiente de cor + formatação numérica
    # ---------------------------------------------------------
    styled_df = (
        df_exib_display.style.background_gradient(
            subset=["Impacto_Financeiro_(R$/mês)"],
            cmap="Greys",
        )
        .format(
            {
                "prob_churn(%)": "{:.2f}",
                "MonthlyCharges": "{:.2f}",
                "TotalCharges": "{:.2f}",
                "Impacto_Financeiro_(R$/mês)": "{:.2f}",
            }
        )
    )

    # ---------------------------------------------------------
    # 9. Texto explicativo e tabela
    # ---------------------------------------------------------
    st.subheader("Fila de clientes prioritários para ação de retenção")

    st.markdown(
        "Ordenado por **nível de risco** (alto → médio → baixo) e por "
        "**impacto financeiro potencial** (probabilidade × mensalidade). "
        "Use os filtros à esquerda para refinar o foco da análise."
    )

    st.caption(
        f"Exibindo **{len(df_exib_display)} clientes** após os filtros aplicados. "
        f"(Probabilidade mínima: {prob_min_percent:.0f}% | "
        f"Tenure mínimo: {tenure_filtro} meses)"
    )

    st.dataframe(
        styled_df,
        use_container_width=True,
        hide_index=True,
    )

    # ---------------------------------------------------------
    # 10. Download da fila priorizada (CSV e Excel)
    # ---------------------------------------------------------
    # Para os arquivos, usamos df_exib (sem emojis, já com rótulos e arredondamento)
    # 10.1 CSV
    csv_bytes = df_exib.to_csv(index=False).encode("utf-8")

    st.download_button(
        label="📥 Baixar fila priorizada (CSV)",
        data=csv_bytes,
        file_name="fila_clientes_prioritarios_churninsight.csv",
        mime="text/csv",
    )

    # 10.2 Excel (.xlsx) – usando XlsxWriter
    excel_buffer = BytesIO()
    with pd.ExcelWriter(excel_buffer, engine="xlsxwriter") as writer:
        df_exib.to_excel(writer, index=False, sheet_name="Fila_priorizada")
    excel_buffer.seek(0)

    st.download_button(
        label="📊 Baixar fila priorizada (Excel)",
        data=excel_buffer,
        file_name="fila_clientes_prioritarios_churninsight.xlsx",
        mime=(
            "application/vnd.openxmlformats-officedocument."
            "spreadsheetml.sheet"
        ),
    )

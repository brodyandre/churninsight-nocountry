from fastapi import FastAPI
import joblib
import pandas as pd

# Carregamento do modelo serializado
modelo = joblib.load('../models/modelo_churn_final.pkl')
app = FastAPI(title="API de Predição de Churn")

@app.post("/predict")
def predict(dados_cliente: dict):
    # Converte JSON de entrada para DataFrame
    df_input = pd.DataFrame([dados_cliente])
    predicao = modelo.predict(df_input)[0]
    return {"churn_prediction": int(predicao)}

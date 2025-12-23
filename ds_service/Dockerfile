FROM python:3.11-slim

ENV PYTHONDONTWRITEBYTECODE=1 \
    PYTHONUNBUFFERED=1

WORKDIR /app

# Dependências mínimas para xgboost/openmp em muitos ambientes
RUN apt-get update && apt-get install -y --no-install-recommends \
    libgomp1 \
 && rm -rf /var/lib/apt/lists/*

# Requisitos (coloque este arquivo no ds_service/requirements.txt)
COPY ds_service/requirements.txt /app/requirements.txt
RUN pip install --no-cache-dir -r /app/requirements.txt

# Código + artefatos necessários para o modelo
COPY ds_service /app/ds_service
COPY model /app/model

EXPOSE 8000

CMD ["uvicorn", "ds_service.app:app", "--host", "0.0.0.0", "--port", "8000"]

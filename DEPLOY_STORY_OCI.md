# Deploy story (OCI) — ChurnInsight (Spring Boot + FastAPI)

## 1) Arquitetura (alto nível)
- **FastAPI (ds-service)**: expõe `/predict`, `/health`; carrega `joblib` do modelo.
- **Spring Boot (spring-backend)**: expõe UI `/` e proxy `/api/churn/*`; chama o FastAPI via `ds.service.url`.

Fluxo:
Usuário → (Browser) → Spring UI (8080) → Spring API (/api/churn/predict) → FastAPI (/predict)

## 2) Contrato entre serviços
- O Spring precisa saber onde está o FastAPI.
- Variável padrão:
  - **DS_SERVICE_URL** (env no Spring) → mapeia para `ds.service.url`.

Exemplos:
- Docker Compose: `http://ds-service:8000`
- OKE (DNS interno): `http://ds-service:8000`
- VM/Compute (mesma máquina): `http://127.0.0.1:8000`

## 3) Opções de deploy no OCI (do mais simples ao mais robusto)
### Opção A — OCI Container Instances (mais simples)
- Subir 2 containers (Spring + FastAPI).
- Expor publicamente **apenas** a porta do Spring (8080).
- Manter FastAPI privado (rede interna).
- Configurar `DS_SERVICE_URL` no container do Spring apontando para o endpoint interno do FastAPI.

### Opção B — OKE (Kubernetes)
- 2 Deployments + 2 Services:
  - FastAPI: Service **ClusterIP** (interno)
  - Spring: Service **LoadBalancer** (público) ou ClusterIP + Ingress
- Spring usa `DS_SERVICE_URL=http://ds-service:8000`.
- Probes:
  - Spring: `/api/churn/health`
  - DS: `/health`

## 4) Observabilidade mínima (o que a banca costuma valorizar)
- Logs:
  - Spring: logs de proxy/erros e latência de chamada ao DS.
  - FastAPI: logs de startup, erros 422 (validação) e execução do `/predict`.
- Health:
  - `/api/churn/health` (Spring)
  - `/api/churn/ds-health` (prova conectividade real com o DS)

## 5) Como provar em demo (checklist)
- UI abrindo: `http://<SPRING_HOST>:8080/`
- Saúde do backend:
  - `curl http://<SPRING_HOST>:8080/api/churn/health`
  - `curl http://<SPRING_HOST>:8080/api/churn/ds-health`
- Predição via proxy (Spring):
  - `curl -X POST http://<SPRING_HOST>:8080/api/churn/predict -H "Content-Type: application/json" -d '{...}'`

## 6) Nota de Free-Tier/recursos
- Preferir imagens enxutas.
- Evitar notebooks/dados grandes dentro do container.
- Manter apenas o artefato do modelo (`.joblib`) e dependências necessárias.

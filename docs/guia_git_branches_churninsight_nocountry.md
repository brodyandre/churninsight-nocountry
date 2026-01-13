# Guia de Uso do Git e Branches – Projeto ChurnInsight (Hackaton No Country)

## 1. Repositório

Repositório oficial do time:

`https://github.com/brodyandre/churninsight-nocountry`

Cada integrante foi adicionado como colaborador e deve aceitar o convite no GitHub antes de tentar enviar código (push).

---

## 2. Estrutura de branches

Vamos usar a seguinte convenção:

- **main** → branch estável  
  - Versão oficial do projeto, usada para apresentação.  
  - Ninguém faz commit direto em `main`.

- **dev** → branch de integração  
  - Onde o código de **Data Science** e **Back-end** se encontra.  
  - Todo código pronto vem de uma `feature/...` via Pull Request para `dev`.

- **feature/...** → branches de trabalho  
  - Criadas a partir de `dev` para cada tarefa/Issue importante.  
  - Exemplos:  
    - `feature/ds-eda-telco`  
    - `feature/ds-modelo-baseline`  
    - `feature/backend-endpoint-predict`  
    - `feature/backend-integracao-microservico`  

Fluxo básico:
> `feature/...` → Pull Request → `dev`  
> (e, no final do hackathon, `dev` → `main`).

---

## 3. Primeiro acesso – clonar o repositório

Cada integrante deve executar no terminal (VS Code, PowerShell, etc.):

```bash
# escolher uma pasta de trabalho
cd C:\Users\SEU_USUARIO\Documents   # ou outra pasta

# clonar o repositório
git clone https://github.com/brodyandre/churninsight-nocountry.git

cd churninsight-nocountry
```

Para listar as branches existentes:

```bash
git branch -a
```

---

## 4. Sempre começar a partir da branch dev

Antes de iniciar qualquer tarefa, sempre atualizar a branch `dev`:

```bash
git checkout dev
git pull origin dev
```

A branch `dev` é a base comum para todo o time.

---

## 5. Criar sua branch de trabalho (feature)

Para cada Issue/tarefa que você pegar, crie uma branch `feature/...` a partir da `dev`.

### Exemplo – Data Science (EDA Telco)

```bash
git checkout dev
git pull origin dev

git checkout -b feature/ds-eda-telco
```

### Exemplo – Back-end (endpoint /predict)

```bash
git checkout dev
git pull origin dev

git checkout -b feature/backend-endpoint-predict
```

A partir daí, você trabalha **apenas** nessa branch até concluir a tarefa.

---

## 6. Como enviar seu código para o GitHub (push)

Depois de editar arquivos (código, notebooks, etc.):

1. Verificar o que mudou:

   ```bash
   git status
   ```

2. Adicionar os arquivos que serão versionados:

   ```bash
   git add caminho/do/arquivo
   # ou, se estiver seguro:
   git add .
   ```

3. Criar um commit com uma mensagem clara:

   ```bash
   git commit -m "Descrição breve do que foi feito"
   ```

4. Enviar a branch para o GitHub:

   ```bash
   git push origin nome-da-sua-branch
   ```

Exemplo:

```bash
git push origin feature/ds-eda-telco
```

---

## 7. Abrir um Pull Request (PR) para a branch dev

Depois do `git push`, vá até o repositório no GitHub:

1. Acesse `https://github.com/brodyandre/churninsight-nocountry`.
2. O GitHub pode sugerir **“Compare & pull request”** para a branch nova. Clique se aparecer.  
3. Caso não apareça, vá em **Pull requests → New pull request**:
   - Em **base**, escolha: `dev`
   - Em **compare**, escolha: `feature/sua-branch`

No formulário do PR:

- **Título:**  
  Algo como:  
  - `[DS] EDA inicial do Telco`  
  - `[BE] Cria endpoint POST /predict`

- **Descrição:**  
  - Resuma o que foi feito;  
  - Se estiver ligado a uma Issue específica, inclua:  
    - `Closes #N` (substituindo `N` pelo número da Issue).

Depois disso, outro integrante pode revisar e aprovar o PR.  
Quando o PR for mesclado (merged), suas alterações passam a fazer parte da branch `dev`.

---

## 8. Atualizar sua branch de trabalho com as mudanças da dev

Se você está trabalhando vários dias na mesma `feature/...`, é importante trazê-la em dia com a `dev`.

Fluxo recomendado:

```bash
# 1. Garantir que seu trabalho atual na feature está salvo em commit
git status
git add .
git commit -m "WIP: trabalho parcial na minha tarefa"

# 2. Ir para a dev e atualizar com o remoto
git checkout dev
git pull origin dev

# 3. Voltar para a sua feature e mesclar a dev nela
git checkout feature/nome-da-sua-branch
git merge dev
```

Se surgirem conflitos, resolva nos arquivos indicados, depois:

```bash
git add arquivos_corrigidos
git commit -m "Resolve conflitos com dev"
```

E continue trabalhando normalmente.

---

## 9. Resumo rápido (para consulta rápida)

**Primeira vez:**

```bash
git clone https://github.com/brodyandre/churninsight-nocountry.git
cd churninsight-nocountry
git checkout dev
git pull origin dev
git checkout -b feature/minha-tarefa
```

**Durante o desenvolvimento:**

```bash
# editar arquivos normalmente

git status
git add .
git commit -m "Implementa minha tarefa X"
git push origin feature/minha-tarefa
```

**Depois do push:**
- Abrir um Pull Request de `feature/minha-tarefa` → `dev` no GitHub.

---

## 10. Boas práticas gerais

- Sempre atualize a `dev` antes de criar uma nova branch `feature/...`.  
- Não faça commits diretamente em `main`.  
- Use mensagens de commit descritivas (o que foi feito, e não “coisas”).  
- Para cada tarefa importante, use uma Issue e linke o PR com `Closes #N`.  
- Em caso de dúvida, pergunte no grupo antes de forçar um `push` ou mexer em `main`.

Esse fluxo ajuda a manter o projeto organizado e facilita a integração do trabalho de Data Science e Back-end durante o hackaton.

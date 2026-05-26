# 💰 Controle Financeiro - Gestor de Gastos Pessoais

Sistema de controle mensal de gastos desenvolvido em **Java** com banco de dados **SQLite**.  
Projeto criado para praticar conceitos de JDBC, CRUD, versionamento com Git e boas práticas de organização de código.

---

## 🚀 Funcionalidades já implementadas (v1.0)

- ✅ Adicionar gasto (descrição, valor, data, categoria)
- ✅ Listar todos os gastos ordenados por data
- ✅ Editar um gasto existente
- ✅ Excluir um gasto
- ✅ Exibir total gasto no mês atual
- ✅ Banco de dados SQLite criado automaticamente
- ✅ Estrutura com Maven e código organizado em camadas (model, dao, view)
- ✅ Menu interativo no console

---

## 📌 Roadmap (próximas versões)

### 🔜 v2.0 – Categorias e Orçamento
- Gerenciar categorias (CRUD)
- Associar cada gasto a uma categoria
- Definir orçamento mensal por categoria
- Alertas ao exceder o limite

### 🔜 v3.0 – Receitas e Relatórios
- Controle de receitas (salário, freelas, etc.)
- Saldo mensal e acumulado
- Gráficos simples (console ou JFreeChart)
- Exportação para CSV/HTML

### 🔜 v4.0 – Interface Gráfica
- Migração para JavaFX (ou Swing)
- Tabelas interativas e formulários

### 🔜 v5.0 – Mobile (futuro)
- Adaptação para Android (SQLite nativo)

---

## 🛠️ Tecnologias utilizadas

- **Java 17+**
- **SQLite** (JDBC driver xerial)
- **Maven** (gerenciamento de dependências e build)
- **Git** (versionamento)

---

## ▶️ Como executar o projeto

### Pré-requisitos
- Java 17 ou superior
- Maven instalado
- Git (opcional, para clonar)

### Passos

```bash
# Clone o repositório
git clone https://github.com/seu-usuario/controle-financeiro.git
cd controle-financeiro

# Compile o projeto
mvn clean compile

# Execute o programa
mvn exec:java

## 📂 Estrutura do projeto
```
controle-financeiro/
├── pom.xml
├── README.md
├── .gitignore
└── src/main/java/br/com/controle/
    ├── database/
    │   └── ConnectionFactory.java
    ├── model/
    │   └── Gasto.java
    ├── dao/
    │   └── GastoDAO.java
    └── view/
        └── MenuConsole.java
```
## 🤝 Contribuição

Este é um projeto de estudo pessoal, mas sugestões e feedbacks são bem-vindos!  
Abra uma issue ou envie um pull request.

---

## 📄 Licença

Projeto livre para uso educacional e adaptações.

Desenvolvido por Douglas Lira – em evolução constante durante o curso de Análise e Desenvolvimento de Sistemas.
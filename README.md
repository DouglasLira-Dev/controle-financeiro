# 💰 Controle Financeiro — Gestor de Gastos Pessoais

![Java](https://img.shields.io/badge/Java-17+-orange?logo=openjdk&logoColor=white)
![Maven](https://img.shields.io/badge/Maven-3.x-blue?logo=apachemaven&logoColor=white)
![SQLite](https://img.shields.io/badge/SQLite-3-lightblue?logo=sqlite&logoColor=white)
![License](https://img.shields.io/badge/License-MIT-green)
![Status](https://img.shields.io/badge/Status-Em%20desenvolvimento-yellow)

Sistema de controle mensal de gastos desenvolvido em **Java** com banco de dados **SQLite**.  
Projeto criado para praticar conceitos de JDBC, CRUD, versionamento com Git e boas práticas de organização de código.

---

## 📋 Índice

- [Funcionalidades](#-funcionalidades)
- [Roadmap](#-roadmap)
- [Tecnologias](#️-tecnologias-utilizadas)
- [Como executar](#️-como-executar-o-projeto)
- [Estrutura do projeto](#-estrutura-do-projeto)
- [Contribuição](#-contribuição)
- [Licença](#-licença)

---

## ✅ Funcionalidades

### v1.0 — Base
- Adicionar gasto (descrição, valor, data, categoria)
- Listar todos os gastos ordenados por data
- Editar um gasto existente
- Excluir um gasto
- Exibir total gasto no mês atual
- Banco de dados SQLite criado automaticamente
- Estrutura com Maven e código organizado em camadas (model, dao, view)
- Menu interativo no console

### v2.0 — Categorias e Orçamento ✨
- Gerenciar categorias (CRUD completo)
- Definir limite mensal por categoria
- Associar cada gasto a uma categoria (em vez de texto livre)
- Alertas automáticos ao exceder o limite mensal
- Relatório de gastos por categoria no mês atual
- Menu integrado de gerenciamento de categorias
- Data no formato brasileiro (dd/MM/yyyy)
- Utilitário `DateUtils` para formatação de datas
- Classes `Categoria` e `CategoriaDAO` implementadas

---

## 📌 Roadmap

| Versão | Descrição | Status |
|--------|-----------|--------|
| v1.0 | Base — CRUD de gastos e menu no console | ✅ Concluída |
| v2.0 | Categorias e Orçamento | ✅ Concluída |
| v3.0 | Receitas e Relatórios | 🔜 Próxima |
| v4.0 | Interface Gráfica (JavaFX/Swing) | 🔜 Planejada |
| v5.0 | Mobile — Android com SQLite nativo | 🔜 Futura |

### 🔜 v3.0 — Receitas e Relatórios
- Controle de receitas (salário, freelas, etc.)
- Saldo mensal e acumulado
- Gráficos simples no console ou via JFreeChart
- Exportação para CSV/HTML

---

## 🛠️ Tecnologias utilizadas

- **Java 17+**
- **SQLite** — JDBC driver xerial
- **Maven** — gerenciamento de dependências e build
- **Git** — versionamento

---

## ▶️ Como executar o projeto

### Pré-requisitos

- Java 17 ou superior
- Maven instalado
- Git (opcional, para clonar)

### Passos

```bash
# Clone o repositório
git clone https://github.com/DouglasLira-Dev/controle-financeiro.git
cd controle-financeiro

# Compile o projeto
mvn clean compile

# Execute o programa
mvn exec:java
```

### Executar via script (Windows)

```bash
console.bat
```

---

## 📂 Estrutura do projeto

```
controle-financeiro/
├── pom.xml
├── README.md
├── LICENSE
├── .gitignore
├── console.bat
└── src/main/java/br/com/controle/
    ├── database/
    │   └── ConnectionFactory.java
    ├── model/
    │   ├── Gasto.java
    │   └── Categoria.java
    ├── dao/
    │   ├── GastoDAO.java
    │   └── CategoriaDAO.java
    ├── utils/
    │   └── DateUtils.java
    └── view/
        ├── MenuConsole.java
        └── MenuCategoria.java
```

---

## 🤝 Contribuição

Este é um projeto de estudo pessoal, mas sugestões e feedbacks são bem-vindos!  
Sinta-se à vontade para abrir uma *issue* ou enviar um *pull request*.

---

## 📄 Licença

Distribuído sob a licença MIT. Consulte o arquivo [LICENSE](LICENSE) para mais informações.

---

Desenvolvido por **Douglas Lira** — estudante de Análise e Desenvolvimento de Sistemas, em constante evolução. 🚀
# Catálogo de Filmes e Séries

Aplicativo Android nativo desenvolvido em **Kotlin** com **Jetpack Compose** para cadastrar, listar, visualizar, editar e excluir filmes e séries favoritos. Os dados ficam salvos localmente com **Room**.

Projeto final do módulo de desenvolvimento Android.

---

## Identificação

- **Aluno:** `Patric Araujo Barros`
- **Matrícula:** `20241015020150`
- **Data de entrega:** `30/10/2026`

---

## Sumário

1. [Justificativa do tema](#justificativa-do-tema)
2. [Funcionalidades](#funcionalidades)
3. [Telas](#telas)
4. [Arquitetura](#arquitetura)
5. [Estrutura do projeto](#estrutura-do-projeto)
6. [Tecnologias](#tecnologias)
7. [Como executar](#como-executar)
8. [Capturas de tela](#capturas-de-tela)
9. [Observações](#observações)

---

## Justificativa do tema

Escolhi o tema **Catálogo de Filmes e Séries** porque ele cobre, em um escopo pequeno, os pontos principais exigidos na atividade:

- **Persistência:** cada título tem atributos reais (título, gênero, tipo e descrição) que precisam sobreviver ao fechamento do app, o que justifica o uso do Room.
- **Navegação:** o fluxo lista → detalhes → edição é natural para o usuário e exige navegação com argumentos.
- **Reatividade:** a lista se atualiza automaticamente quando um item é criado, editado ou excluído, graças ao `Flow` retornado pelo DAO.
- **Reaproveitamento:** a mesma tela de formulário serve para cadastro e edição, o que mostra a separação entre interface e lógica.

---

## Funcionalidades

- Cadastrar filme ou série com título, gênero, tipo e descrição
- Listar todos os itens cadastrados, em ordem alfabética
- Visualizar os detalhes de um item
- Editar um item existente
- Excluir um item, com confirmação
- Persistência local com Room, sem necessidade de internet
- Validação do campo título (obrigatório)

---

## Telas

### 1. Lista (`MovieListScreen`)
- Mostra os títulos cadastrados em cards com nome, gênero e tipo.
- Exibe a mensagem "Nenhum título cadastrado ainda" quando a lista está vazia.
- Botão **+** (FloatingActionButton) para cadastrar um novo item.
- Toque em um card abre a tela de detalhes.

### 2. Detalhes (`MovieDetailScreen`)
- Exibe título, tipo, gênero e descrição.
- Botão **Editar**, que abre o formulário preenchido com os dados atuais.
- Botão **Excluir**, que pede confirmação antes de remover o item. Após excluir, o app volta para a lista.
- Se o item não existir mais, exibe uma mensagem de erro com opção de voltar.

### 3. Cadastro e edição (`MovieFormScreen`)
- Campos: título (obrigatório), gênero, tipo (Filme ou Série) e descrição.
- Se o título estiver vazio ao salvar, o campo mostra a mensagem de erro e nada é gravado.
- Ao salvar, o item é inserido (cadastro) ou atualizado (edição) e o app volta para a tela anterior.

---

## Arquitetura

O projeto segue o padrão **MVVM**, com as responsabilidades separadas em três camadas:

```
UI (Compose)  →  ViewModel (StateFlow)  →  Repository  →  DAO (Room)  →  SQLite
```

- **UI (`ui`):** composables que observam o estado e enviam eventos. Não acessam o banco diretamente.
- **ViewModel (`viewmodel`):** guarda o estado da tela em `StateFlow` (com `data class` imutável) e trata as ações do usuário.
- **Dados (`data`):** entidade, DAO e banco Room, além do `MovieRepository`, que é o único ponto de acesso aos dados para os ViewModels.

**Decisões técnicas:**
- **Injeção de dependências manual:** uma `ViewModelFactory` cria os ViewModels com o repositório. Optei por não usar Hilt para manter o projeto simples e focado nos conceitos da atividade.
- **Operações assíncronas:** todas as escritas no banco são `suspend`, executadas em coroutines, sem consultas na thread principal.
- **Navegação tipada:** as rotas são definidas em `Routes.kt`, e o identificador do item é passado como argumento.

---

## Estrutura do projeto

```
app/src/main/java/com/<seunome>/catalogo/
├── CatalogoApplication.kt
├── MainActivity.kt
├── data/
│   ├── local/          MovieEntity, MovieDao, AppDatabase
│   └── repository/     MovieRepository
├── viewmodel/          MovieListViewModel, MovieDetailViewModel,
│                       MovieFormViewModel, ViewModelFactory
└── ui/
    ├── navigation/     Routes, CatalogoNavHost
    ├── screens/        list/, detail/, form/
    ├── components/     MovieCard
    └── theme/          Color, Theme, Type
```

Os textos da interface estão em `res/values/strings.xml`.

---

## Tecnologias

- **Kotlin**
- **Jetpack Compose** com **Material 3**
- **Navigation Compose**
- **Room** com **KSP**
- **ViewModel** e **Lifecycle** (`lifecycle-viewmodel-compose`, `lifecycle-runtime-compose`)
- **Kotlin Coroutines** e **Flow**
- **Gradle** com Kotlin DSL e Version Catalog (`libs.versions.toml`)

As versões exatas de cada biblioteca estão no arquivo `gradle/libs.versions.toml`.

---

## Como executar

**Requisitos:** Android Studio (versão atual), JDK compatível com o Gradle do projeto e um emulador ou dispositivo Android.

1. Clone o repositório:
2. Abra a pasta no Android Studio.
3. Aguarde a sincronização do Gradle.
4. Execute o app no emulador ou dispositivo com o botão **Run**.

---

---

## Observações

- Os dados são armazenados apenas no dispositivo. Não há sincronização com a nuvem nem consulta a APIs externas de filmes.
- Não há autenticação de usuário, pois não era um requisito da atividade.
- A exclusão é definitiva e não há desfazer.

---

*Projeto desenvolvido por `Patric Araujo Barros` como atividade final do módulo de desenvolvimento Android.*

# CLAUDE.md — Catálogo de Filmes ou Séries

Este arquivo define o contexto, os requisitos e as regras para construir o projeto final do módulo de desenvolvimento Android. Leia-o inteiro antes de escrever qualquer código.

---

## 1. Contexto da atividade

- **Objetivo:** desenvolver um aplicativo Android completo e simples, em **Kotlin**, demonstrando navegação entre telas, armazenamento local e separação de responsabilidades.
- **Tema escolhido:** Projeto 2 — **Catálogo de Filmes ou Séries** (cadastrar filmes/séries favoritos, listar, ver detalhes).
- **Idioma:** o código (nomes de classes, funções, variáveis) fica em **inglês**; os textos exibidos ao usuário ficam em **português (pt-BR)**, em `strings.xml`.

---

## 2. Requisitos técnicos obrigatórios

Todos devem ser atendidos. Use esta lista como checklist de aceitação.

- [ ] Linguagem: **Kotlin**
- [ ] Interface: **Jetpack Compose** (sem XML de layout para as telas)
- [ ] Pelo menos **2 telas** em Compose (o projeto terá 3: lista, detalhes, cadastro/edição)
- [ ] Navegação entre telas com **Navigation Compose**
- [ ] Persistência local com **Room**, com pelo menos **1 entidade**
- [ ] Arquitetura **MVVM**
- [ ] Organização em camadas com separação entre **interface (ui)**, **lógica (viewmodel)** e **dados (data)**

---

## 3. Funcionalidades mínimas

- [ ] Cadastrar filme ou série (título, gênero, descrição)
- [ ] Listar os itens cadastrados
- [ ] Visualizar detalhes de um item
- [ ] Persistir todos os dados com Room (sobrevivem ao fechar o app)
- [ ] Organizar a lógica com MVVM

**Funcionalidades extras (opcionais, só implementar após todo o escopo mínimo funcionar):**
- Editar um item existente (recomendado, pois reaproveita a tela de cadastro)
- Excluir um item (botão na tela de detalhes, com confirmação)
- Campo de tipo: Filme ou Série

---

## 4. Telas

### Tela 1 — Lista de Filmes ou Séries (`MovieListScreen`)
- Lista com o **título** de cada item cadastrado (e, opcionalmente, o gênero).
- Botão flutuante (FAB) "+" para ir ao cadastro.
- Ao tocar em um item, abre a tela de detalhes.
- Estado vazio: mensagem "Nenhum título cadastrado ainda".

### Tela 2 — Detalhes (`MovieDetailScreen`)
- Exibe: **título**, **gênero** e **descrição**.
- Botão "Editar" (leva à tela de cadastro/edição com o item preenchido).
- Botão "Excluir" (opcional), com diálogo de confirmação.
- Se o item não existir, exibir mensagem de erro e botão de voltar.

### Tela 3 — Cadastro / Edição (`MovieFormScreen`)
- Campos de texto: **título** (obrigatório), **gênero**, **descrição**.
- Campo opcional: tipo (Filme ou Série).
- Botão "Salvar".
- Validação: título não pode estar vazio; mostrar erro no campo (`isError` + `supportingText`).
- Após salvar, volta para a tela anterior.
- Se receber um `movieId`, funciona como **edição** (carrega os dados e atualiza). Caso contrário, é **cadastro**.

---

## 5. Stack e dependências

- Android Studio atual, Gradle com **Kotlin DSL** (`build.gradle.kts`)
- **Jetpack Compose** com **Material 3**
- **Navigation Compose** (`androidx.navigation:navigation-compose`)
- **Room** (`androidx.room:room-runtime`, `room-ktx`, compilador via **KSP**)
- **ViewModel** e **lifecycle** (`androidx.lifecycle:lifecycle-viewmodel-compose`)
- **Kotlin Coroutines** e **Flow** para dados reativos
- Usar versões **estáveis** e compatíveis entre si. Antes de fixar versões, confirme a compatibilidade (Kotlin × KSP × Compose × Room). Não invente números de versão.

Não adicionar bibliotecas fora deste escopo (ex.: Hilt, Retrofit, Firebase) sem aprovação. Injeção de dependência manual (via `ViewModelProvider.Factory` ou um `AppContainer` simples) é suficiente.

---

## 6. Arquitetura MVVM

```
Model (data)  ←  Repository  ←  ViewModel  ←  UI (Compose)
```

- **UI** observa estados dos ViewModels e envia eventos. **Não** acessa o DAO nem o banco diretamente.
- **ViewModel** expõe `StateFlow` com um estado imutável (`data class` de UI state), recebe ações da tela e chama o repositório. **Não** conhece componentes Compose nem `Context` de Activity.
- **Repository** é a única porta de entrada para os dados; expõe `Flow` e funções `suspend`.
- **Data** contém a entidade Room, o DAO e o banco.

---

## 7. Estrutura de pacotes

Pacote base sugerido: `com.<seunome>.catalogo`

```
com.<seunome>.catalogo
├── CatalogoApplication.kt          // cria o AppContainer (opcional, para DI manual)
├── MainActivity.kt                 // setContent { CatalogoApp() }
│
├── data
│   ├── local
│   │   ├── MovieEntity.kt          // @Entity
│   │   ├── MovieDao.kt             // @Dao
│   │   └── AppDatabase.kt          // @Database + singleton
│   └── repository
│       └── MovieRepository.kt
│
├── viewmodel
│   ├── MovieListViewModel.kt
│   ├── MovieDetailViewModel.kt
│   ├── MovieFormViewModel.kt
│   └── ViewModelFactory.kt         // factory única para os ViewModels (DI manual)
│
└── ui
    ├── navigation
    │   ├── Routes.kt               // constantes/rotas tipadas
    │   └── CatalogoNavHost.kt      // NavHost e NavController
    ├── screens
    │   ├── list
    │   │   └── MovieListScreen.kt
    │   ├── detail
    │   │   └── MovieDetailScreen.kt
    │   └── form
    │       └── MovieFormScreen.kt
    ├── components                  // composables reutilizáveis (ex.: MovieCard)
    └── theme                       // Color.kt, Theme.kt, Type.kt (gerados pelo template)
```

Regra: **uma responsabilidade por arquivo**. Não colocar entidade, DAO e ViewModel no mesmo arquivo.

---

## 8. Modelo de dados (Room)

### Entidade
```kotlin
@Entity(tableName = "movies")
data class MovieEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val title: String,
    val genre: String,
    val description: String,
    val type: String = "Filme"   // "Filme" ou "Série"
)
```

### DAO
Operações mínimas:
- `@Query("SELECT * FROM movies ORDER BY title ASC") fun getAll(): Flow<List<MovieEntity>>`
- `@Query("SELECT * FROM movies WHERE id = :id") fun getById(id: Int): Flow<MovieEntity?>`
- `@Insert suspend fun insert(movie: MovieEntity): Long`
- `@Update suspend fun update(movie: MovieEntity)`
- `@Delete suspend fun delete(movie: MovieEntity)`

### Banco
- `AppDatabase` como `abstract class` com `@Database(entities = [MovieEntity::class], version = 1)`.
- Singleton com `Room.databaseBuilder(...)`.
- Não usar `allowMainThreadQueries()`. Todas as escritas são `suspend`.

---

## 9. Navegação

Rotas (definidas em `Routes.kt`):

| Tela | Rota | Argumento |
|---|---|---|
| Lista | `movies` | — |
| Detalhes | `movie/{movieId}` | `movieId: Int` |
| Cadastro | `movie/form?movieId={movieId}` | `movieId: Int`, default `-1` (novo) |

- Usar `NavHost` com `composable(...)` e `navArgument` tipado.
- Ao salvar no formulário: `navController.popBackStack()`.
- Ao excluir no detalhe: voltar para a lista, removendo o detalhe da pilha (`popUpTo`).
- Ao editar a partir do detalhe: a tela de formulário é empilhada normalmente.

---

## 10. Padrões de código

- Estado de UI como `data class` imutável, exposto por `StateFlow` com `stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), ...)`.
- Na Compose, coletar com `collectAsStateWithLifecycle()`.
- Eventos da UI chamam funções do ViewModel (ex.: `onTitleChange`, `onSave`). Não expor `MutableStateFlow` publicamente.
- Composables recebem **dados e lambdas**, não o ViewModel inteiro (exceto a tela raiz de cada rota).
- Strings visíveis sempre em `strings.xml`. Sem texto literal espalhado no código de UI.
- `Modifier` como primeiro parâmetro opcional dos composables reutilizáveis.
- Usar `@Preview` nas telas e componentes principais, quando ajudar.
- Sem código morto, comentários óbvios ou imports não usados.

---

## 11. Entrega

O aluno escolhe **uma** das opções abaixo.

**Opção 1 — GitHub:** repositório com o código completo e um `README.md` contendo:
- Nome do aluno
- Matrícula
- Data de entrega
- Justificativa da escolha do tema
- Descrição do funcionamento do app
- Observações relevantes

**Opção 2 — ZIP:** arquivo `.zip` com o código completo e um PDF com as mesmas informações acima.

O README (ou PDF) deve ser escrito por você, com os dados do aluno preenchidos.

---

## 12. Como trabalhar neste projeto (regras para o Claude)

1. **Respeite o escopo.** Construa primeiro o mínimo obrigatório. Funcionalidades extras só depois que tudo do item 3 estiver funcionando.
2. **Construa em ordem:** (a) Gradle e dependências → (b) entidade, DAO, banco e repositório → (c) ViewModels → (d) telas Compose → (e) navegação → (f) ligação final em `MainActivity`.
3. **Código completo e compilável.** Nunca deixe trechos com `// TODO` ou `...` no lugar de código necessário. Se algo depender de uma versão de biblioteca, diga isso explicitamente.
4. **Não invente APIs.** Se não tiver certeza de uma versão ou assinatura de função da Compose, Room ou Navigation, verifique antes de afirmar.
5. **Explique as decisões** de forma curta, principalmente as relacionadas ao MVVM, para ajudar o aluno a justificar o projeto no README.
6. **Não mude a arquitetura sem avisar.** Se achar necessário sair do padrão deste arquivo, pergunte primeiro.
7. **Ao final de cada etapa**, liste o que foi feito e o que falta, marcando o checklist da seção 2 e 3.
8. **Escreva o README** somente quando o projeto estiver funcional, e deixe os campos de nome e matrícula como placeholders para o aluno preencher.

---

## 13. Critérios de aceitação (checklist final)

- [ ] App compila e roda sem crash
- [ ] Lista exibe os itens salvos e o estado vazio
- [ ] Cadastro valida o título e salva no Room
- [ ] Detalhes mostram título, gênero e descrição
- [ ] Edição preenche o formulário e atualiza o item
- [ ] Dados permanecem após fechar e reabrir o app
- [ ] Navegação funciona nos dois sentidos, sem pilha quebrada
- [ ] Camadas separadas: `ui`, `viewmodel`, `data`
- [ ] Nenhuma chamada de banco na thread principal
- [ ] Textos em `strings.xml`
- [ ] README ou PDF com todas as informações da seção 11
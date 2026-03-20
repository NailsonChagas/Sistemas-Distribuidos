# Merge Sort 

## Descrição

Este projeto implementa duas versões do algoritmo Merge Sort:

- Versão sequencial
- Versão paralela otimizada

Ambas utilizam o mesmo princípio de divisão e conquista, mas diferem na forma como exploram o paralelismo e gerenciam o custo de execução.

## Lógica do Algoritmo

O Merge Sort segue três etapas principais:

1. Dividir o array em duas metades
2. Ordenar cada metade recursivamente
3. Realizar o merge das partes ordenadas

A diferença entre as versões está na execução da etapa recursiva.

## Versão Sequencial

### Funcionamento

A versão sequencial executa o algoritmo de forma linear:

- Divide recursivamente o array até atingir subarrays pequenos
- Aplica ordenação direta (Insertion Sort) nesses casos
- Realiza o merge das partes ordenadas

Não há criação de tarefas concorrentes. Toda a execução ocorre em uma única thread.

### Otimizações Aplicadas

#### Insertion Sort para Subarrays Pequenos

Assim como na versão paralela, subarrays pequenos (≤ 64 elementos) são ordenados com Insertion Sort para reduzir overhead.

#### Buffer Auxiliar Reutilizável

Um único array auxiliar é alocado no início e reutilizado durante todos os merges, evitando múltiplas alocações.

### Características

- Baixo overhead
- Execução previsível
- Melhor desempenho para arrays pequenos
- Não utiliza múltiplos núcleos

## Versão Paralela

### Funcionamento
A versão paralela segue a mesma lógica da versão sequencial, porém:

- As duas metades do array são processadas simultaneamente (quando permitido)
- O paralelismo ocorre durante a recursão
- Após o processamento paralelo, as partes são sincronizadas antes do merge


### Controle de Paralelismo
A criação de tarefas é limitada por uma profundidade máxima:

- Níveis superiores da recursão são paralelizados
- Níveis inferiores são executados sequencialmente

Isso evita:
- excesso de criação de tarefas
- overhead de sincronização
- degradação de desempenho

### Otimizações Aplicadas
- Uso de Insertion Sort para pequenos blocos
- Reutilização de buffer auxiliar
- Paralelismo controlado por profundidade para evitar criar muitas tarefas
- Execução independente das subpartes

## Comparação: Sequencial vs Paralelo

### Desempenho
- **Arrays pequenos**:
  - A versão sequencial tende a ser mais rápida
  - O overhead do paralelismo não compensa

- **Arrays médios e grandes**:
  - A versão paralela tende a ser superior
  - Melhor aproveitamento de múltiplos núcleos


### Escalabilidade

- **Sequencial**:
  - Limitada a um único núcleo
  - Não escala com hardware

- **Paralela**:
  - Escala com o número de threads disponíveis


## Fluxo de Execução

### Sequencial

1. Alocar buffer auxiliar
2. Dividir recursivamente o array
3. Aplicar Insertion Sort em subarrays pequenos
4. Realizar merges sucessivos
5. Liberar memória

### Paralelo

1. Alocar buffer auxiliar
2. Calcular profundidade máxima de paralelismo
3. Dividir recursivamente o array:
   - Criar tarefas nos níveis superiores
   - Executar sequencialmente nos níveis inferiores
4. Sincronizar subtarefas
5. Realizar merges
6. Liberar memória

## Considerações Finais

A versão sequencial é mais simples e eficiente para entradas menores, devido ao baixo overhead.

A versão paralela é mais adequada para grandes volumes de dados, pois permite explorar múltiplos núcleos de processamento, desde que o paralelismo seja controlado para evitar custos excessivos.
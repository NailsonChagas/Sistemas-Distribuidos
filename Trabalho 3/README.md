# Jogo 21 (Blackjack) com Java RMI

## Descrição

Implementação distribuída do jogo 21 (Blackjack) utilizando Java RMI. O servidor centraliza toda a lógica da partida, enquanto os clientes realizam chamadas remotas para executar as ações do jogador. Cada cliente possui uma partida independente contra o dealer.

## Funcionamento

A comunicação entre cliente e servidor é realizada através da interface remota `BlackjackRemoteInterface`, implementada por `BackjackServerLogic`.

As principais operações disponibilizadas são:

- `startRound()` – inicia uma nova rodada;
- `hit()` – solicita uma nova carta;
- `stand()` – encerra a vez do jogador;
- `score()` – consulta a pontuação acumulada;
- `heartbeat()` – informa ao servidor que o cliente continua ativo.

Toda a lógica do jogo é executada no servidor.

## Modelo da Aplicação

- **Card**: representa uma carta do baralho;
- **DeckCards**: implementa um baralho de 52 cartas embaralhadas;
- **Hand**: armazena as cartas e calcula a pontuação, tratando corretamente o Ás;
- **GameState**: armazena o estado completo de uma partida;
- **Score**: mantém a quantidade de vitórias e derrotas do jogador;
- **BackjackServerLogic**: implementa os métodos remotos e gerencia os jogadores conectados.

## Concorrência

O servidor utiliza `ConcurrentHashMap` para armazenar os estados das partidas e as pontuações dos jogadores, permitindo acesso concorrente seguro por múltiplos clientes.

Segundo a especificação do Java RMI, chamadas remotas para um mesmo objeto podem ser executadas concorrentemente. Por isso, as estruturas compartilhadas do servidor foram implementadas com `ConcurrentHashMap`. Os demais objetos, armazenados dentro de `GameState`, não necessitam de sincronização adicional, pois cada jogador possui sua própria instância de partida.

## Detecção de Desconexão

Foi implementado um mecanismo de *heartbeat* para detectar clientes desconectados inesperadamente.

Uma thread no cliente envia periodicamente mensagens de heartbeat ao servidor. O instante da última comunicação é armazenado e uma thread de limpeza verifica periodicamente os jogadores ativos. Caso um cliente permaneça sem enviar heartbeats por um determinado intervalo de tempo, suas informações são removidas automaticamente das estruturas internas do servidor.

## Uso do Java RMI

O servidor cria o registro RMI e publica o objeto remoto:

```java
LocateRegistry.createRegistry(1099);
Naming.rebind("Blackjack", server);
```

Os clientes obtêm uma referência para o objeto remoto através de:

```java
Naming.lookup("rmi://localhost/Blackjack");
```

## Compilação

```bash
chmod +x build.sh
./build.sh
```

O script gera:

```
BlackjackRMI_server/server.jar
BlackjackRMI_client/client.jar
```

## Execução

Servidor:

```bash
java -jar BlackjackRMI_server/server.jar
```

Cliente:

```bash
java --enable-preview -jar BlackjackRMI_client/client.jar
```

> É necessário utilizar Java 21 ou superior.

# Jogo 21 (Blackjack) com Java RMI

## Descrição

Este projeto implementa uma versão distribuída do jogo **21 (Blackjack)** utilizando **Java RMI (Remote Method Invocation)**. O sistema segue uma arquitetura cliente-servidor, na qual os jogadores interagem remotamente com um servidor responsável por controlar toda a lógica da partida.

Cada jogador possui uma partida independente contra o dealer, permitindo que vários clientes sejam atendidos simultaneamente.

## Estrutura do Projeto

```
BlackjackRMI_client
└── src
    ├── interfaces
    │   └── BlackjackRemoteInterface.java
    └── Main.java

BlackjackRMI_server
└── src
    ├── interfaces
    │   └── BlackjackRemoteInterface.java
    ├── models
    │   ├── Card.java
    │   ├── DeckCards.java
    │   ├── GameState.java
    │   └── Hand.java
    ├── server
    │   └── BackjackServerLogic.java
    └── Main.java
```

## Uso do Java RMI

O Java RMI foi utilizado para implementar a comunicação entre cliente e servidor. A interface `BlackjackRemoteInterface` define os métodos remotos disponíveis (`startRound()`, `hit()` e `stand()`), que são implementados pela classe `BackjackServerLogic`.

Ao iniciar, o servidor cria o registro RMI e publica o objeto remoto com o nome `"Blackjack"`. O cliente obtém uma referência para esse objeto através de `Naming.lookup()` e pode invocar os métodos remotamente. Dessa forma, toda a lógica do jogo é executada no servidor, enquanto os clientes apenas enviam requisições e recebem os resultados das jogadas.

## Arquitetura Cliente-Servidor

O sistema é dividido em três componentes principais:

### 1. Interface Remota

Arquivo:

```
interfaces/BlackjackRemoteInterface.java
```

Define os métodos remotos disponibilizados aos clientes:

```java
String startRound(String name);
String hit(String name);
String stand(String name);
```

Esses métodos correspondem às ações permitidas ao jogador durante uma partida.

### 2. Servidor RMI

Responsável por:

* Gerenciar os jogadores conectados;
* Criar partidas individuais;
* Manter os baralhos;
* Executar a lógica do dealer;
* Determinar o vencedor da rodada.

Classe principal:

```java
server.BackjackServerLogic
```

O servidor mantém um mapa:

```java
Map<String, GameState> players
```

onde cada jogador possui um objeto `GameState` próprio, garantindo isolamento entre partidas.

### 3. Cliente RMI

Arquivo:

```java
Main.java
```

O cliente:

1. Conecta-se ao servidor através do RMI;
2. Informa o nome do jogador;
3. Inicia uma rodada;
4. Permite escolher entre:

* Pedir carta (Hit);
* Parar (Stand);
* Sair.

Toda a lógica do jogo é executada no servidor.

## Modelo de Classes
Corresponde ao modelo de objetos da aplicação, ou seja, como o programa foi organizado internamente.

### Card

Representa uma carta do baralho.

A classe armazena:

* Valor da carta;
* Naipe.

Também fornece:

```java
getNumericValue()
```

que converte:

* A → 11;
* J, Q e K → 10;
* 2 a 10 → valor numérico correspondente.

### DeckCards

Representa um baralho de 52 cartas.

Funções:

* Criar todas as combinações de valores e naipes;
* Embaralhar as cartas usando:

```java
Collections.shuffle()
```

* Fornecer cartas através do método:

```java
buyCard()
```

### Hand

Representa a mão de um jogador.

Responsável por:

* Armazenar as cartas;
* Calcular a pontuação.

A implementação trata corretamente os ases, reduzindo seu valor de 11 para 1 quando necessário para evitar ultrapassar 21.

### GameState

Armazena o estado completo de uma partida:

* Baralho;
* Mão do jogador;
* Mão do dealer;
* Situação de término da partida.

Cada jogador conectado possui uma instância própria dessa classe.

### BackjackServerLogic

Implementa a interface remota:

```java
BlackjackRemoteInterface
```

e fornece os métodos:

#### startRound()

* Cria uma nova partida;
* Distribui duas cartas para o jogador;
* Distribui duas cartas para o dealer;
* Armazena a partida no mapa de jogadores.

#### hit()

* Compra uma nova carta;
* Recalcula a pontuação;
* Verifica se o jogador ultrapassou 21.

Caso isso ocorra:

```java
game.setFinished(true);
```

e o jogador perde imediatamente.

#### stand()

Após o jogador encerrar sua vez:

O dealer compra cartas automaticamente enquanto:

```java
dealerHand.calculateScore() < 17
```

Quando atinge 17 ou mais, para de jogar.

Em seguida, as pontuações são comparadas para determinar o vencedor.

## Relação com o Enunciado

### Conexão de jogadores por nome

**Requisito do trabalho**

> Conexão de jogadores por nome.

**Implementação**

No cliente:

```java
String name = scanner.nextLine();
```

No servidor:

```java
players.put(name, game);
```

Cada nome identifica uma partida independente.

### Início da rodada com duas cartas

**Requisito do trabalho**

> Início de rodada com duas cartas para o jogador.

**Implementação**

Na classe `GameState`:

```java
playerHand.addCard(deck.buyCard());
playerHand.addCard(deck.buyCard());
```

### Pedir carta (Hit)

**Requisito do trabalho**

> Ação de pedir carta.

**Implementação**

Método:

```java
hit(String name)
```

que adiciona uma carta à mão do jogador:

```java
game.getPlayerHand().addCard(
        game.getDeck().buyCard()
);
```

### Parar (Stand)

**Requisito do trabalho**

> Ação de parar.

**Implementação**

Método:

```java
stand(String name)
```

que encerra a jogada do usuário e inicia a vez do dealer.

### Dealer automático

**Requisito do trabalho**

> O dealer deve comprar até atingir 17 pontos.

**Implementação**

```java
while (dealerHand.calculateScore() < 17) {
    dealerHand.addCard(deck.buyCard());
}
```

Assim, o dealer segue exatamente as regras especificadas.

### Cálculo do resultado da rodada

**Requisito do trabalho**

> Cálculo e exibição do vencedor.

**Implementação**

Após a jogada do dealer:

```java
if (dealerScore > 21)
    return "Jogador venceu";

if (playerScore > dealerScore)
    return "Jogador venceu";

return "Dealer venceu";
```

Em caso de empate, o dealer vence, conforme especificado no enunciado.

### Isolamento entre jogadores

**Requisito do trabalho**

> Jogos individuais contra o dealer.

**Implementação**

Cada jogador possui seu próprio:

```java
GameState
```

armazenado em:

```java
ConcurrentHashMap<String, GameState>
```

Assim, os jogadores não compartilham cartas nem pontuações.

### Múltiplos jogadores simultâneos

**Requisito do trabalho**

> Suporte a múltiplos jogadores simultâneos.

**Implementação**

Utiliza-se:

```java
ConcurrentHashMap
```

permitindo acesso concorrente seguro por vários clientes.

## Uso de Java RMI

O servidor é criado através de:

```java
LocateRegistry.createRegistry(1099);
```

e registrado com:

```java
Naming.rebind("Blackjack", server);
```

Os clientes recuperam o objeto remoto por meio de:

```java
Naming.lookup("rmi://localhost/Blackjack");
```

A comunicação ocorre através da interface:

```java
BlackjackRemoteInterface
```

que estende:

```java
Remote
```

e lança:

```java
RemoteException
```

em todos os métodos remotos.

## Compilação e Execução
O projeto contém um script `build.sh` responsável por compilar o servidor e o cliente e gerar os arquivos `.jar`.

### 1. Compilar o projeto

No diretório raiz do projeto, execute:

```bash
chmod +x build.sh
./build.sh
```

O script irá:

* Remover arquivos de compilação antigos;
* Compilar o servidor;
* Compilar o cliente;
* Gerar os arquivos:

```
BlackjackRMI_server/server.jar
BlackjackRMI_client/client.jar
```

### 2. Executar o servidor

Em um terminal, execute:

```bash
java -jar BlackjackRMI_server/server.jar
```

O servidor iniciará e registrará o serviço RMI `Blackjack`.

### 3. Executar o cliente

Em outro terminal, execute:

```bash
java --enable-preview -jar BlackjackRMI_client/client.jar
```

O cliente se conectará ao servidor RMI em `localhost` e permitirá jogar Blackjack pelo terminal.

> **Observação:** é necessário utilizar o Java 21 ou superior, pois o cliente faz uso de funcionalidades em modo preview.

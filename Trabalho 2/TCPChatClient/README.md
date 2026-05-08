# TCP Chat Client (Java)

## Índice

1. [**O que é o projeto**](#o-que-e-o-projeto)
2. [**Arquitetura do cliente**](#arquitetura-do-cliente)
   1. [**TCPChatClient**](#tcpchatclient)
   2. [**Thread de recepção**](#thread-de-recepcao)
   3. [**Histórico local**](#historico-local)
3. [**Fluxo de comunicação**](#fluxo-de-comunicacao)
   1. [**Envio de mensagens**](#1-envio-de-mensagens)
   2. [**Recebimento de mensagens**](#2-recebimento-de-mensagens)
   3. [**Exibição no terminal**](#3-exibicao-no-terminal)
4. [**Sistema de comandos locais**](#sistema-de-comandos-locais)
   1. [**Funcionamento geral**](#funcionamento-geral)
   2. [**Estrutura de comandos**](#estrutura-de-comandos)
5. [**Comandos disponíveis**](#comandos-disponiveis)
   1. [**Locais (/local)**](#locais-local)
   2. [**Servidor (/)**](#servidor)
6. [**Sistema de histórico**](#sistema-de-historico)
7. [**Sistema de exibição (UI CLI)**](#sistema-de-exibicao-ui-cli)
8. [**Tipos de mensagens suportadas**](#tipos-de-mensagens-suportadas)
9. [**Resumo do funcionamento**](#resumo-do-funcionamento)


## O que é o projeto

Este é um cliente de chat TCP desenvolvido em Java com interface de linha de comando (CLI). Ele permite a comunicação com um servidor de chat, suportando mensagens privadas, mensagens em grupo e broadcast.

O cliente também mantém um histórico local de conversas e possui um sistema de comandos locais independentes do servidor.


## Arquitetura do cliente

### TCPChatClient

Responsável por:

- Conectar ao servidor via TCP
- Enviar mensagens
- Processar comandos locais
- Gerenciar estado do usuário (destinatário atual)
- Manter histórico local

### Thread de recepção

O cliente utiliza uma thread separada:

```
new Thread(this::receiveLoop).start();
```

Responsável por:

- Receber mensagens do servidor
- Processar mensagens em tempo real
- Atualizar histórico local
- Exibir mensagens no terminal


### Histórico local

O histórico é armazenado em:

```
Map<String, List<Mensagem>>
```

Cada chave representa:

- usuário
- grupo
- broadcast
- servidor



## Fluxo de comunicação

### 1. Envio de mensagens

O usuário envia uma mensagem para o destino atual:

```
Mensagem msg = new Mensagem(username, current_target, input);
```

A mensagem é enviada via:

```
out_stream.writeObject(msg);
```



### 2. Recebimento de mensagens

O cliente executa continuamente:

```
Mensagem msg = (Mensagem) in_stream.readObject();
```

Cada mensagem recebida:

- é armazenada no histórico
- é filtrada para exibição
- pode ser exibida imediatamente



### 3. Exibição no terminal

Mensagens são exibidas com:

- timestamp
- remetente
- destinatário
- conteúdo

O prompt é redesenhado automaticamente após cada mensagem.



## Sistema de comandos locais

### Funcionamento geral

Comandos locais começam com:

```
/local
```

Esses comandos são processados **exclusivamente no cliente**, sem interação com o servidor.



### Estrutura de comandos

Os comandos são tratados por:

```
handleLocalCommand(String input)
```

E processados via:

```
switch(command)
```



## Comandos disponíveis

### Locais (/local)

- `/local help` Mostra ajuda dos comandos locais

- `/local use <usuario|@grupo>` Seleciona o destinatário ativo

- `/local history` Mostra histórico do destino atual

- `/local history <alvo>` Mostra histórico de um destino específico

- `/local clear` Limpa o histórico local

- `/local quit` Encerra o cliente



### Servidor (/)

Qualquer comando que não começa com `/local` é enviado ao servidor:

- `/help` → ajuda do servidor
- `/online_users` → lista usuários
- `/groups` → lista grupos
- `/join_group @grupo` → entra em grupo
- `/leave_group @grupo` → sai de grupo
- `/create_group ...` → cria grupo



## Sistema de histórico

O histórico é organizado por chave:

```
String key = getString(msg);
```

Regras:

- Grupo (`@grupo`) → chave = grupo
- Mensagem privada → chave = remetente
- Broadcast → `"broadcast"`
- Sistema → `"SERVER"`

Cada chave armazena até 100 mensagens.



## Sistema de exibição (UI CLI)

O cliente implementa um sistema de interface responsiva no terminal:

### Características:

- limpeza de linha (`\r\033[K`)
- redraw automático de prompt
- exibição imediata de mensagens recebidas
- sincronização entre threads



## Tipos de mensagens suportadas

- Broadcast (destinatário = null)
- Privada (destinatário = usuário)
- Grupo (destinatário = @grupo)
- Sistema (remetente = SERVER)



## Resumo do funcionamento

- O cliente conecta ao servidor via TCP
- Uma thread envia mensagens
- Outra thread recebe mensagens em tempo real
- O usuário pode alternar destinos com `/local use`
- O histórico é armazenado localmente
- O terminal é atualizado dinamicamente para simular chat em tempo real

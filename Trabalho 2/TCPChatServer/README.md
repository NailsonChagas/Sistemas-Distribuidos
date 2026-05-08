# TCP Chat Server (Java)

## Índice
1. [**O que é o projeto**](#o-que-e-o-projeto)
2. [**Arquitetura do sistema**](#arquitetura-do-sistema)
    1. [**TCPServer**](#tcpserver)
    2. [**ClientHandler**](#clienthandler)
    3. [**Mensagem**](#mensagem)
3. [**Fluxo de uma mensagem**](#fluxo-de-uma-mensagem)
    1. [**Envio pelo cliente**](#1-envio-pelo-cliente)
    2. [**Recebimento no servidor**](#2-recebimento-no-servidor)
    3. [**Classificação da mensagem**](#3-classificacao-da-mensagem)
    4. [**Envio**](#4-envio)
4. [**Sistema de comandos**](#sistema-de-comandos)
    1. [**Funcionamento geral**](#funcionamento-geral)
    2. [**Estrutura de comandos**](#estrutura-de-comandos)
    3. [**Execução**](#execucao)
5. [**Comandos disponíveis**](#comandos-disponiveis)
    1. [**Usuário**](#usuario)
    2. [**Administrador**](#administrador)
6. [**Sistema de grupos**](#sistema-de-grupos)
7. [**Tipos de mensagens**](#tipos-de-mensagens)
8. [**Resumo do funcionamento**](#resumo-do-funcionamento)

## O que é o projeto
Este é um servidor de chat baseado em TCP desenvolvido em Java. Ele permite comunicação entre múltiplos clientes simultâneos, suporte a mensagens privadas, mensagens em grupo, broadcast, além de um sistema de comandos e controle de administradores.

O sistema é multithread, onde cada cliente conectado é tratado de forma independente pelo servidor.

## Arquitetura do sistema
O projeto é composto por três partes principais:

### TCPServer

Responsável por:

- Gerenciar conexões de clientes
- Controlar usuários online
- Gerenciar grupos
- Roteamento de mensagens
- Controle de timeout do servidor

### ClientHandler

Responsável por:

- Representar um cliente conectado
- Ler e processar mensagens
- Executar comandos
- Enviar mensagens ao cliente

### Mensagem

Objeto que representa a comunicação entre cliente e servidor, contendo:

- remetente
- destinatário
- conteúdo
- horário

## Fluxo de uma mensagem

### 1. Envio pelo cliente

O cliente envia um objeto do tipo Mensagem:

```
Mensagem(remetente, destinatário, conteúdo)
```

### 2. Recebimento no servidor

O ClientHandler recebe a mensagem:

```
Mensagem msg = (Mensagem) in_stream.readObject();
```

### 3. Classificação da mensagem

O servidor decide o tipo de envio com base no destinatário:
- null: broadcast para todos
- @grupo: mensagem para grupo
- usuário específico: mensagem privada

### 4. Envio

O servidor entrega a mensagem para os clientes conectados:
- broadcast: todos os usuários
- privateMessage: usuário específico
- groupMessage: todos os membros do grupo

## Sistema de comandos

### Funcionamento geral

Qualquer mensagem que comece com "/" é tratada como comando e não como mensagem de chat.

O comando é processado pelo método:

```
handleCommand(String command)
```

### Estrutura de comandos

Os comandos são armazenados em um Map:

```
Map<String, Command>
```

Cada comando é uma função que recebe um array de argumentos e retorna um boolean indicando se o cliente continua conectado.

### Execução

1. A mensagem é dividida em partes:

```
String[] args = command.split(" ");
String base = args[0];
```

2. O comando é buscado no mapa:

```
Command cmd = commands.get(base);
```

3. O comando é executado:

```
return cmd.execute(args);
```

## Comandos disponíveis

### Usuário

- `/help` Lista todos os comandos disponíveis
- `/online_users` Lista usuários conectados
- `/groups` Lista todos os grupos existentes
- `/group_info @grupo` Mostra membros do grupo
- `/join_group @grupo` Entra em um grupo
- `/leave_group @grupo` Sai de um grupo
- `/create_group @grupo usuario1 usuario2 ...` Cria um novo grupo

### Administrador (grupo @admins)

O sistema considera administradores os usuários presentes no grupo @admins.
- `/admin_add_user @grupo usuario` Adiciona um usuário ao grupo
- `/admin_remove_user @grupo usuario` Remove um usuário do grupo
- `/admin_delete_group @grupo` Remove um grupo inteiro

## Sistema de grupos

Os grupos são armazenados no servidor como:

```
Map<String, List<String>>
```

Regras:
- Grupos devem começar com @
- Usuários podem pertencer a múltiplos grupos
- O grupo @admins define permissões administrativas

## Tipos de mensagens
- Broadcast: enviada para todos os usuários conectados
- Privada: enviada para um usuário específico
- Grupo: enviada para todos os membros de um grupo

## Resumo do funcionamento
- Cada cliente possui uma thread própria
- O servidor gerencia múltiplas conexões simultâneas
- Mensagens são roteadas automaticamente
- Comandos são resolvidos via Map de funções
- Sistema de permissões baseado em grupos

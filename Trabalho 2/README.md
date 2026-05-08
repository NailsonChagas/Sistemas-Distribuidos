# TCP Chat System (Java)

## O que é o projeto

Este é um sistema de chat distribuído desenvolvido em Java utilizando TCP. Ele permite comunicação em tempo real entre múltiplos clientes conectados a um servidor central, com suporte a mensagens privadas, mensagens em grupo, broadcast e comandos.

O sistema foi projetado com foco em concorrência, modularidade e separação clara entre cliente e servidor.

## Estrutura do repositório

```
.
├── TCPChatClient
│   ├── out
│   ├── src
│   └── README.md
│
├── TCPChatServer
│   ├── out
│   ├── src
│   └── README.md
│
└── README.md   (este arquivo)
```

## Arquitetura geral do sistema

O sistema é dividido em dois módulos independentes:

- **Servidor TCP (TCPChatServer)** Responsável por gerenciar conexões, usuários, grupos e roteamento de mensagens.
- **Cliente TCP (TCPChatClient)** Responsável por interface de linha de comando, envio de mensagens e histórico local.

A comunicação ocorre via sockets TCP, utilizando objetos serializáveis para troca de mensagens.

## Módulo do Servidor (TCPChatServer)

O servidor é multithread e trata cada cliente conectado de forma independente.

### Responsabilidades principais

- Gerenciar conexões simultâneas
- Controlar usuários online
- Gerenciar grupos de chat
- Roteamento de mensagens
- Processamento de comandos
- Controle de permissões (administração via grupo especial)

### Componentes principais

- **TCPServer**: núcleo do servidor
- **ClientHandler**: thread por cliente conectado
- **Mensagem**: estrutura de comunicação


## Módulo do Cliente (TCPChatClient)

O cliente é uma aplicação CLI que permite interação em tempo real com o servidor.

### Responsabilidades principais

- Conexão TCP com o servidor
- Envio de mensagens
- Recebimento assíncrono de mensagens
- Sistema de comandos locais
- Histórico de conversas

### Características importantes

- Thread separada para recepção de mensagens
- Histórico local por usuário/grupo
- Sistema de prompt dinâmico no terminal

## Fluxo de comunicação completo

1. O cliente cria uma mensagem e envia ao servidor via socket TCP
2. O servidor recebe a mensagem no `ClientHandler`
3. O servidor identifica o tipo de mensagem:
   - privada
   - grupo
   - broadcast
4. O servidor roteia a mensagem para os destinatários corretos
5. O cliente receptor exibe a mensagem em tempo real

## Tipos de mensagens suportadas

- **Broadcast**: enviada para todos os usuários conectados
- **Privada**: enviada para um usuário específico
- **Grupo**: enviada para todos os membros de um grupo
- **Sistema**: mensagens geradas pelo servidor

## Sistema de comandos

### Servidor

Comandos iniciados com `/` são interpretados pelo servidor e podem incluir:

- gerenciamento de usuários
- controle de grupos
- informações do sistema
- operações administrativas

### Cliente

Comandos locais iniciados com:

```
/local
```

São processados apenas no cliente e incluem:

- troca de destinatário ativo
- visualização de histórico
- limpeza de histórico
- comandos de ajuda
- encerramento do cliente

## Como executar o projeto

### 1. Compilar o servidor

Dentro de `TCPChatServer/src`:

```bash
javac *.java
```

Executar:

```bash
java TCPServer
```

### 2. Compilar o cliente

Dentro de `TCPChatClient/src`:

```bash
javac *.java
```

Executar:

```bash
java TCPChatClient
```

### 3. Ordem de execução

1. Iniciar o servidor primeiro
2. Iniciar um ou mais clientes
3. Conectar usuários e iniciar comunicação


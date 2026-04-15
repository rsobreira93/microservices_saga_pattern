#  Arquitetura de Microsserviços: Padrão Saga Orquestrado

O projeto com a arquitetura visa uma prova de conceito de estudos, onde o resultado final a aplicação ficou de acordo com a imagem: 
![img.png](img.png)

### Sumário:

* [Tecnologias](#tecnologias)
* [Ferramentas utilizadas](#ferramentas-utilizadas)
* [Arquitetura Proposta](#arquitetura-proposta)
* [Execução do projeto](#Executando-o-projeto)
    * [01 - Execução geral via docker-compose](#01---execu%C3%A7%C3%A3o-geral-via-docker-compose)
    * [02 - Execução geral via automação com script em Python](#02---execu%C3%A7%C3%A3o-geral-via-automa%C3%A7%C3%A3o-com-script-em-python)
    * [03 - Executando os serviços de bancos de dados e Message Broker](#03---executando-os-servi%C3%A7os-de-bancos-de-dados-e-message-broker)
    * [04 - Executando manualmente via CLI](#04---executando-manualmente-via-cli)
* [Acessando a aplicação](#acessando-a-aplica%C3%A7%C3%A3o)
* [Acessando tópicos com Redpanda Console](#acessando-t%C3%B3picos-com-redpanda-console)
* [Dados da API](#dados-da-api)
    * [Produtos registrados e seu estoque](#produtos-registrados-e-seu-estoque)
    * [Endpoint para iniciar a saga](#endpoint-para-iniciar-a-saga)
    * [Endpoint para visualizar a saga](#endpoint-para-visualizar-a-saga)
    * [Acesso ao MongoDB](#acesso-ao-mongodb)
* [Tela do Zipkin](#Tela-do-zipkin)
## Tecnologias

* Java 17
* Springboot
* Rest API
* Apache Kafka
* PostrgresSQL
* MongoDB
* Docker
* docker-compose
* Redpanda console
* Zpkin

## Ferramentas utilizadas
* Intellij IDEA Community Edition
* Dokcer
* Gradle

## Arquitetura proposta

No projeto foi desenvolvida a seguinte arquiteura:
![img_1.png](img_1.png)

Na nossa arquitetura, temos 5 serviços:

* Order-Service: microsserviço responsável apenas por gerar um pedido inicial, e receber uma notificação. Aqui que temos os endpoints REST para inciar o processo e recuperar os dados dos eventos. O banco de dados utilizado será o MongoDB.
* Orchestrator-Service: microsserviço responsável por orquestrar todo o fluxo de execução da Saga, ele que sabe qual microsserviço foi executado e em qual estado, e para qual será o próximo microsserviço a ser enviado, este microsserviço também salva o processo dos eventos. Este serviço não possui banco de dados.
* Product-Validation-Service: microsserviço responsável por validar se o produto informado no pedido existe e está válido. Este microsserviço guarda a validação de um produto para o ID de um pedido. O banco de dados utilizado será o PostgreSQL.
* Payment-Service: microsserviço responsável por realizar um pagamento com base nos valores unitários e quantidades informadas no pedido. Este microsserviço guarda a informação de pagamento de um pedido. O banco de dados utilizado será o PostgreSQL.
* Inventory-Service: microsserviço responsável por realizar a baixa do estoque dos produtos de um pedido. Este microsserviço guarda a informação da baixa de um produto para o ID de um pedido. O banco de dados utilizado será o PostgreSQL.

* Todos os serviços da arquitetura irão subir através do arquivo docker-compose.yml.

## Executando o projeto

1. Executando tudo via docker-compose 
2. Executando tudo via script de automação que eu disponibilizei (build.py)
3. Executando apenas os serviços de bancos de dados e message broker (Kafka) separadamente 
4. Executando as aplicações manualmente via CLI (java -jar ou gradle bootRun ou via IntelliJ)

Para rodar as aplicações, será necessário ter instalado:

Docker
Java 17
Gradle 7.6 ou superior

### 01 - Execução geral via docker-compose

[Voltar ao nível anterior](#executando-o-projeto)

Basta executar o comando no diretório raiz do repositório:

`docker compose up --build -d`

**Obs.: para rodar tudo desta maneira, é necessário realizar o build das 5 aplicações, veja nos passos abaixo sobre como fazer isto.**

### 02 - Execução geral via automação com script em Python

[Voltar ao nível anterior](#executando-o-projeto)

Basta executar o arquivo `build.py`. Para isto, **é necessário ter o Python 3 instalado**.

Para executar, basta apenas executar o seguinte comando no diretório raiz do repositório:

`python3 build.py`

Será realizado o `build` de todas as aplicações, removidos todos os containers e em sequência, será rodado o `docker-compose`.

### 03 - Executando os serviços de bancos de dados e Message Broker

[Voltar ao nível anterior](#executando-o-projeto)

Para ser possível executar os serviços de bancos de dados e Message Broker, como MongoDB, PostgreSQL e Apache Kafka, basta ir no diretório raiz do repositório, onde encontra-se o arquivo `docker-compose.yml` e executar o comando:

`docker compose up --build -d order-db kafka product-db payment-db inventory-db`

Como queremos rodar apenas os serviços de bancos de dados e Message Broker, é necessário informá-los no comando do `docker-compose`, caso contrário, as aplicações irão subir também.

Para parar todos os containers, basta rodar:

`docker-compose down`

Ou então: (**cuidado**)

`docker stop ($docker ps -aq)`
`docker container prune -f`

### 04 - Executando manualmente via CLI

[Voltar ao nível anterior](#executando-o-projeto)

Antes da execução do projeto, realize o `build` da aplicação indo no diretório raiz e executando o comando:

`gradle build -x test`

Para executar os projetos com Gradle, basta entrar no diretório raiz de cada projeto, e executar o comando:

`gradle bootRun`

Ou então, entrar no diretório: `build/libs` e executar o comando:

## Acessando a aplicação

[Voltar ao início](#sum%C3%A1rio)

Para acessar as aplicações e realizar um pedido, basta acessar a URL:

http://localhost:3000/swagger-ui.html

Você chegará nesta página:

![img_2.png](img_2.png)

As aplicações executarão nas seguintes portas:

* Order-Service: 3000
* Orchestrator-Service: 8080
* Product-Validation-Service: 8090
* Payment-Service: 8091
* Inventory-Service: 8092
* Apache Kafka: 9092
* Redpanda Console: 8081
* zipkin: 9411
* PostgreSQL (Product-DB): 5432
* PostgreSQL (Payment-DB): 5433
* PostgreSQL (Inventory-DB): 5434
* MongoDB (Order-DB): 27017

## Acessando tópicos com Redpanda Console
[Voltar ao início](#sum%C3%A1rio)

Para acessar o Redpanda Console e visualizar tópicos e publicar eventos, basta acessar:

http://localhost:8081

Você chegará nesta página:
![img_3.png](img_3.png)

## Dados da API
[Voltar ao início](#sum%C3%A1rio)

É necessário conhecer o payload de envio ao fluxo da saga, assim como os produtos cadastrados e as suas quantidades.

Produtos registrados e seu estoque

Existem 3 produtos iniciais cadastrados no serviço `product-validation-service` e as suas quantidades disponíveis em `inventory-service`:

* **COMIC_BOOKS** (4 em estoque)
* **BOOKS** (2 em estoque)
* **MOVIES** (5 em estoque)
* **MUSIC** (9 em estoque)

### Endpoint para iniciar a saga:

**POST** http://localhost:3000/api/order

Payload:

```json
{
  "products": [
    {
      "product": {
        "code": "COMIC_BOOKS",
        "unitValue": 15.50
      },
      "quantity": 3
    },
    {
      "product": {
        "code": "BOOKS",
        "unitValue": 9.90
      },
      "quantity": 1
    }
  ]
}
```

Resposta:

```json
{
  "id": "64429e987a8b646915b3735f",
  "products": [
    {
      "product": {
        "code": "COMIC_BOOKS",
        "unitValue": 15.5
      },
      "quantity": 3
    },
    {
      "product": {
        "code": "BOOKS",
        "unitValue": 9.9
      },
      "quantity": 1
    }
  ],
  "createdAt": "2025-04-21T14:32:56.335943085",
  "transactionId": "1682087576536_99d2ca6c-f074-41a6-92e0-21700148b519"
}
```

### Endpoint para visualizar a saga:
[Voltar ao início](#sum%C3%A1rio)

É possível recuperar os dados da saga pelo **orderId** ou pelo **transactionId**, o resultado será o mesmo:

**GET** http://localhost:3000/api/event?orderId=64429e987a8b646915b3735f

**GET** http://localhost:3000/api/event?transactionId=1682087576536_99d2ca6c-f074-41a6-92e0-21700148b519

Resposta:

```json
{
  "id": "64429e9a7a8b646915b37360",
  "transactionId": "1682087576536_99d2ca6c-f074-41a6-92e0-21700148b519",
  "orderId": "64429e987a8b646915b3735f",
  "payload": {
    "id": "64429e987a8b646915b3735f",
    "products": [
      {
        "product": {
          "code": "COMIC_BOOKS",
          "unitValue": 15.5
        },
        "quantity": 3
      },
      {
        "product": {
          "code": "BOOKS",
          "unitValue": 9.9
        },
        "quantity": 1
      }
    ],
    "totalAmount": 56.40,
    "totalItems": 4,
    "createdAt": "2025-04-21T14:32:56.335943085",
    "transactionId": "1682087576536_99d2ca6c-f074-41a6-92e0-21700148b519"
  },
  "source": "ORCHESTRATOR",
  "status": "SUCCESS",
  "eventHistory": [
    {
      "source": "ORCHESTRATOR",
      "status": "SUCCESS",
      "message": "Saga started!",
      "createdAt": "2025-04-21T14:32:56.78770516"
    },
    {
      "source": "PRODUCT_VALIDATION_SERVICE",
      "status": "SUCCESS",
      "message": "Products are validated successfully!",
      "createdAt": "2025-04-21T14:32:57.169378616"
    },
    {
      "source": "PAYMENT_SERVICE",
      "status": "SUCCESS",
      "message": "Payment realized successfully!",
      "createdAt": "2025-04-21T14:32:57.617624655"
    },
    {
      "source": "INVENTORY_SERVICE",
      "status": "SUCCESS",
      "message": "Inventory updated successfully!",
      "createdAt": "2025-04-21T14:32:58.139176809"
    },
    {
      "source": "ORCHESTRATOR",
      "status": "SUCCESS",
      "message": "Saga finished successfully!",
      "createdAt": "2025-04-21T14:32:58.248630293"
    }
  ],
  "createdAt": "2025-04-21T14:32:58.28"
}
```

### Acesso ao MongoDB
[Voltar ao início](#sum%C3%A1rio)

Para conectar-se ao MongoDB via linha de comando (cli) diretamente do docker-compose, basta executar o comando abaixo:

**docker exec -it order-db mongosh "mongodb://admin:123456@localhost:27017"**

Para listar os bancos de dados existentes:

**show dbs**

Para selecionar um banco de dados:

**use admin**

Para visualizar as collections do banco:

**show collections**

Para realizar queries e validar se os dados existem:

**db.order.find()**

**db.event.find()**

**db.order.find(id=ObjectId("65006786d715e21bd38d1634"))**

**db.order.find({ "products.product.code": "COMIC_BOOKS"})**

## Tela do zipkin
[Voltar ao início](#sum%C3%A1rio)

![img_4.png](img_4.png)

### OBS: Os logs das outras aplicações podem não aparecer (exceto o order-service), caso queira que os logs apareçam, é só mudar o pattern no application.yml
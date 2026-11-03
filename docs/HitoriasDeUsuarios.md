# Histórias de Usuário - Sistema de Aluguel de Carros

## Cliente

| ID | História | Critérios de aceitação |
|----|----------|------------------------|
| HU01 | Como visitante, quero me cadastrar com usuário e senha e meus dados pessoais, para poder usar o sistema. | RG, CPF, nome, endereço e profissão obrigatórios; até 3 entidades empregadoras com rendimento; CPF e usuário únicos; senha com no mínimo 6 caracteres. |
| HU02 | Como cliente, quero entrar com usuário e senha, para acessar minha área. | Credenciais inválidas mostram erro; sem login nenhuma tela é acessível. |
| HU03 | Como cliente, quero atualizar meu perfil e meus empregadores/rendimentos, para manter meus dados corretos. | Máximo de 3 empregadores; só edito o meu próprio perfil. |
| HU04 | Como cliente, quero introduzir um pedido de aluguel escolhendo carro e período, para alugar um automóvel. | Data final não anterior à inicial; carro indisponível no período é recusado; pedido nasce PENDENTE. |
| HU05 | Como cliente, quero associar um contrato de crédito (banco e valor) ao pedido, para financiar o aluguel. | Banco deve ser um agente do tipo BANCO; valor maior que zero. |
| HU06 | Como cliente, quero consultar meus pedidos e o status de cada um, para acompanhar a análise. | Vejo somente os meus pedidos; posso filtrar por status; vejo o parecer do agente. |
| HU07 | Como cliente, quero modificar um pedido, para corrigir carro, datas ou crédito. | Permitido apenas enquanto o pedido está PENDENTE. |
| HU08 | Como cliente, quero cancelar um pedido, para desistir do aluguel. | Permitido em PENDENTE ou APROVADO; pedido CONTRATADO não cancela. |

## Agente (banco ou empresa)

| ID | História | Critérios de aceitação |
|----|----------|------------------------|
| HU09 | Como agente, quero ver todos os pedidos e a renda total do contratante, para fazer a análise financeira. | Lista completa com filtro por status; detalhe mostra empregadores, rendimentos e renda total. |
| HU10 | Como agente, quero aprovar ou reprovar um pedido com um parecer, para registrar a análise financeira. | Só pedidos PENDENTE; parecer de até 1000 caracteres. |
| HU11 | Como agente, quero executar o contrato de um pedido aprovado, para formalizar o aluguel. | Só pedidos APROVADO; o status passa a CONTRATADO. |
| HU12 | Como agente, quero modificar ou cancelar pedidos, para corrigir ou encerrar solicitações. | Modificar em PENDENTE ou APROVADO; cancelar em PENDENTE ou APROVADO. |
| HU13 | Como agente, quero cadastrar, editar, consultar e excluir clientes, para manter a base de contratantes. | CPF único; exclusão recusada se o cliente tiver usuário ou pedidos. |
| HU14 | Como agente, quero cadastrar, editar e excluir automóveis, para manter a frota. | Matrícula e placa únicas; propriedade CLIENTE, EMPRESA ou BANCO; exclusão recusada se o carro estiver em pedido. |

## Requisitos não funcionais

| ID | Requisito |
|----|-----------|
| RNF01 | Sistema web: React + Vite no front-end e Spring Boot (Java 25, Maven) no back-end, arquitetura MVC. |
| RNF02 | Banco de dados Supabase (PostgreSQL). |
| RNF03 | Autenticação obrigatória (usuário e senha), com sessão por token JWT e senhas armazenadas com BCrypt. |
| RNF04 | Autorização por papel (CLIENTE/AGENTE) validada no back-end, não só no front-end. |
// TypeScript: união de literais funciona como um "enum" leve, verificado em tempo de compilação.
export type Role = 'CLIENTE' | 'AGENTE'
export type AgentType = 'EMPRESA' | 'BANCO'
export type Owner = 'CLIENTE' | 'EMPRESA' | 'BANCO'
export type OrderStatus = 'PENDENTE' | 'APROVADO' | 'REPROVADO' | 'CONTRATADO' | 'CANCELADO'

// TypeScript: interface base; as demais usam "extends" para herdar o campo id.
export interface Entity { id: string }
export interface Employer { entidade: string; rendimento: number } // máximo 3 por cliente
export interface Client extends Entity { rg: string; cpf: string; nome: string; endereco: string; profissao: string; empregadores: Employer[] }
export interface User extends Entity { username: string; password?: string; role: Role; agentType?: AgentType; clientId?: string }
export interface Car extends Entity { matricula: string; ano: number; marca: string; modelo: string; placa: string; proprietario: Owner }
export interface Order extends Entity {
  clientId: string; carId: string; inicio: string; fim: string; status: OrderStatus
  credito?: { bancoId: string; valor: number } // "?" = campo opcional
  parecer?: string; criadoEm: string
}

// TypeScript: utility types. Omit remove campos; aqui "Draft" = objeto ainda sem id (antes de salvar).
export type Draft<T extends Entity> = Omit<T, 'id'>

// Regras de negócio compartilhadas pelas telas.
export const canEdit = (o: Order, role: Role) => (role === 'AGENTE' ? ['PENDENTE', 'APROVADO'] : ['PENDENTE']).includes(o.status)
export const canCancel = (o: Order) => o.status === 'PENDENTE' || o.status === 'APROVADO'

// ============ CAMADA DE SERVIÇO ============
// Para trocar o mock pelo Spring Boot: VITE_USE_MOCK=false no .env. Nenhuma tela precisa mudar.
import type { Car, Client, Draft, Entity, Order, User } from './types'

const USE_MOCK = import.meta.env.VITE_USE_MOCK !== 'false'
const API_URL: string = import.meta.env.VITE_API_URL ?? 'http://localhost:8080/api'

// Contrato único de CRUD. TypeScript: genérico <T extends Entity> reaproveita a interface para qualquer recurso.
export interface Repo<T extends Entity> {
  list(): Promise<T[]>
  get(id: string): Promise<T | undefined>
  create(d: Draft<T>): Promise<T>
  update(id: string, d: Partial<T>): Promise<T>
  remove(id: string): Promise<void>
}

// Implementação MOCK: persiste no localStorage.
function mockRepo<T extends Entity>(key: string, seed: T[]): Repo<T> {
  const read = (): T[] => {
    const raw = localStorage.getItem(key)
    if (raw) return JSON.parse(raw) as T[] // "as T[]" = type assertion
    localStorage.setItem(key, JSON.stringify(seed))
    return seed
  }
  const write = (items: T[]) => localStorage.setItem(key, JSON.stringify(items))
  return {
    list: async () => read(),
    get: async (id) => read().find((i) => i.id === id),
    create: async (d) => { const item = { ...d, id: crypto.randomUUID() } as unknown as T; write([...read(), item]); return item },
    update: async (id, d) => { const items = read().map((i) => (i.id === id ? { ...i, ...d } : i)); write(items); return items.find((i) => i.id === id)! },
    remove: async (id) => write(read().filter((i) => i.id !== id)),
  }
}

// Implementação HTTP: mesmos métodos, falando com os endpoints REST do back-end.
function httpRepo<T extends Entity>(path: string): Repo<T> {
  const call = async <R,>(url: string, init?: RequestInit): Promise<R> => {
    const r = await fetch(`${API_URL}${url}`, { headers: { 'Content-Type': 'application/json', Authorization: `Bearer ${sessionStorage.getItem('token') ?? ''}` }, ...init })
    if (!r.ok) throw new Error(await r.text())
    return r.status === 204 ? (undefined as R) : r.json()
  }
  return {
    list: () => call(`/${path}`),
    get: (id) => call(`/${path}/${id}`),
    create: (d) => call(`/${path}`, { method: 'POST', body: JSON.stringify(d) }),
    update: (id, d) => call(`/${path}/${id}`, { method: 'PUT', body: JSON.stringify(d) }),
    remove: (id) => call(`/${path}/${id}`, { method: 'DELETE' }),
  }
}

const make = <T extends Entity>(path: string, seed: T[] = []): Repo<T> => (USE_MOCK ? mockRepo<T>(`aluguel:${path}`, seed) : httpRepo<T>(path))

export const users = make<User>('users', [
  { id: 'u1', username: 'cliente', password: 'cliente123', role: 'CLIENTE', clientId: 'c1' },
  { id: 'u2', username: 'banco', password: 'banco123', role: 'AGENTE', agentType: 'BANCO' },
  { id: 'u3', username: 'empresa', password: 'empresa123', role: 'AGENTE', agentType: 'EMPRESA' },
])
export const clients = make<Client>('clients', [
  { id: 'c1', rg: 'MG-12.345.678', cpf: '123.456.789-00', nome: 'Maria Souza', endereco: 'Rua das Flores, 100 - BH', profissao: 'Engenheira', empregadores: [{ entidade: 'Construtora Alfa', rendimento: 8500 }] },
])
export const cars = make<Car>('cars', [
  { id: 'k1', matricula: 'MAT-0001', ano: 2023, marca: 'Toyota', modelo: 'Corolla', placa: 'ABC1D23', proprietario: 'EMPRESA' },
  { id: 'k2', matricula: 'MAT-0002', ano: 2022, marca: 'Honda', modelo: 'Civic', placa: 'DEF4G56', proprietario: 'BANCO' },
  { id: 'k3', matricula: 'MAT-0003', ano: 2024, marca: 'VW', modelo: 'Polo', placa: 'HIJ7K89', proprietario: 'CLIENTE' },
])
export const orders = make<Order>('orders')

// ---- Autenticação ----
export async function login(username: string, password: string): Promise<User> {
  if (USE_MOCK) {
    const u = (await users.list()).find((x) => x.username === username && x.password === password)
    if (!u) throw new Error('Usuário ou senha inválidos')
    return { ...u, password: undefined }
  }
  const r = await fetch(`${API_URL}/auth/login`, { method: 'POST', headers: { 'Content-Type': 'application/json' }, body: JSON.stringify({ username, password }) })
  if (!r.ok) throw new Error('Usuário ou senha inválidos')
  const { token, user } = (await r.json()) as { token: string; user: User }
  sessionStorage.setItem('token', token)
  return user
}

export async function register(cred: { username: string; password: string }, client: Draft<Client>): Promise<User> {
  if (USE_MOCK) {
    if ((await users.list()).some((u) => u.username === cred.username)) throw new Error('Usuário já existe')
    const c = await clients.create(client)
    return users.create({ ...cred, role: 'CLIENTE', clientId: c.id })
  }
  const r = await fetch(`${API_URL}/auth/register`, { method: 'POST', headers: { 'Content-Type': 'application/json' }, body: JSON.stringify({ ...cred, cliente: client }) })
  if (!r.ok) throw new Error(await r.text())
  return r.json()
}

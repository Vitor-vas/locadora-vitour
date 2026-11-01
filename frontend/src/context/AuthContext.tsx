import { createContext, useContext, useState, type ReactNode } from 'react'
import * as api from '../api'
import type { User } from '../types'

interface Auth { user: User | null; signIn(u: string, p: string): Promise<void>; signOut(): void }
// TypeScript: createContext<Auth | null> tipa o valor do contexto; null = fora do provider.
const Ctx = createContext<Auth | null>(null)

export function AuthProvider({ children }: { children: ReactNode }) {
  // Estado inicial lazy: lê a sessão salva (sessionStorage) apenas na primeira renderização.
  const [user, setUser] = useState<User | null>(() => JSON.parse(sessionStorage.getItem('user') ?? 'null'))
  const signIn = async (u: string, p: string) => {
    const logged = await api.login(u, p)
    sessionStorage.setItem('user', JSON.stringify(logged))
    setUser(logged)
  }
  const signOut = () => { sessionStorage.clear(); setUser(null) }
  return <Ctx.Provider value={{ user, signIn, signOut }}>{children}</Ctx.Provider>
}

// Hook customizado: garante (com type narrowing) que o retorno nunca é null.
export function useAuth(): Auth {
  const c = useContext(Ctx)
  if (!c) throw new Error('useAuth deve ser usado dentro de AuthProvider')
  return c
}

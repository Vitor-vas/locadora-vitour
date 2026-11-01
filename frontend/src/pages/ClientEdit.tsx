import { useEffect, useState } from 'react'
import { useNavigate, useParams } from 'react-router-dom'
import * as api from '../api'
import { useAuth } from '../context/AuthContext'
import ClientForm from '../components/ClientForm'
import { Page } from '../components/ui'
import type { Client } from '../types'

// Serve para 3 rotas: /perfil (cliente edita a si mesmo), /clientes/novo e /clientes/:id (agente).
export default function ClientEdit() {
  const { id: paramId } = useParams<{ id: string }>()
  const { user } = useAuth()
  const nav = useNavigate()
  const id = user?.role === 'CLIENTE' ? user.clientId : paramId
  const [c, setC] = useState<Client>(), [ready, setReady] = useState(!id), [saved, setSaved] = useState(false)
  useEffect(() => { if (id) api.clients.get(id).then((x) => { setC(x); setReady(true) }) }, [id])
  if (!ready) return <Page title="Cliente">Carregando…</Page>
  return (
    <Page title={id ? 'Dados do cliente' : 'Novo cliente'}>
      <ClientForm initial={c} onSubmit={async (d) => {
        if (id) await api.clients.update(id, d); else await api.clients.create(d)
        if (user?.role === 'AGENTE') nav('/clientes'); else setSaved(true)
      }} />
      {saved && <p>✓ Dados salvos.</p>}
    </Page>
  )
}

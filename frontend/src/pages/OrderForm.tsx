import { useEffect, useState, type FormEvent } from 'react'
import { useNavigate, useParams } from 'react-router-dom'
import * as api from '../api'
import { useAuth } from '../context/AuthContext'
import { Btn, Field, Glass, Page, SelectField } from '../components/ui'
import type { Car, Client, User } from '../types'

// Mesma tela para CRIAR (/pedidos/novo) e MODIFICAR (/pedidos/:id/editar): useParams define o modo.
export default function OrderForm() {
  const { id } = useParams<{ id: string }>()
  const { user } = useAuth()
  const nav = useNavigate()
  const [cars, setCars] = useState<Car[]>([]), [clients, setClients] = useState<Client[]>([]), [bancos, setBancos] = useState<User[]>([])
  const [f, setF] = useState({ clientId: user?.clientId ?? '', carId: '', inicio: '', fim: '', credito: false, bancoId: '', valor: '' })
  const [err, setErr] = useState('')
  const up = <K extends keyof typeof f>(k: K, v: (typeof f)[K]) => setF((p) => ({ ...p, [k]: v })) // genérico: v precisa combinar com o tipo do campo k

  useEffect(() => {
    (async () => {
      const [c, cl, u] = await Promise.all([api.cars.list(), api.clients.list(), api.users.list()])
      setCars(c); setClients(cl); setBancos(u.filter((x) => x.agentType === 'BANCO'))
      if (id) {
        const o = await api.orders.get(id)
        if (o) setF({ clientId: o.clientId, carId: o.carId, inicio: o.inicio, fim: o.fim, credito: !!o.credito, bancoId: o.credito?.bancoId ?? '', valor: String(o.credito?.valor ?? '') })
      }
    })()
  }, [id])

  async function submit(e: FormEvent) {
    e.preventDefault()
    if (f.fim < f.inicio) return setErr('A data final deve ser posterior à inicial')
    const data = { clientId: f.clientId, carId: f.carId, inicio: f.inicio, fim: f.fim, credito: f.credito ? { bancoId: f.bancoId, valor: Number(f.valor) } : undefined }
    if (id) await api.orders.update(id, data)
    else await api.orders.create({ ...data, status: 'PENDENTE', criadoEm: new Date().toISOString() })
    nav('/pedidos')
  }

  return (
    <Page title={id ? 'Modificar pedido' : 'Novo pedido'}>
      <Glass><form className="stack" onSubmit={submit}>
        {user?.role === 'AGENTE' && (
          <SelectField label="Cliente" value={f.clientId} onChange={(e) => up('clientId', e.target.value)} required>
            <option value="">Selecione…</option>{clients.map((c) => <option key={c.id} value={c.id}>{c.nome}</option>)}
          </SelectField>
        )}
        <SelectField label="Automóvel" value={f.carId} onChange={(e) => up('carId', e.target.value)} required>
          <option value="">Selecione…</option>{cars.map((c) => <option key={c.id} value={c.id}>{c.marca} {c.modelo} ({c.ano}) — {c.placa}</option>)}
        </SelectField>
        <div className="grid2">
          <Field label="Início" type="date" value={f.inicio} onChange={(e) => up('inicio', e.target.value)} required />
          <Field label="Fim" type="date" value={f.fim} onChange={(e) => up('fim', e.target.value)} required />
        </div>
        <label className="row"><input type="checkbox" checked={f.credito} onChange={(e) => up('credito', e.target.checked)} /> Associar contrato de crédito</label>
        {f.credito && <div className="grid2">
          <SelectField label="Banco" value={f.bancoId} onChange={(e) => up('bancoId', e.target.value)} required>
            <option value="">Selecione…</option>{bancos.map((b) => <option key={b.id} value={b.id}>{b.username}</option>)}
          </SelectField>
          <Field label="Valor do crédito (R$)" type="number" min="0" value={f.valor} onChange={(e) => up('valor', e.target.value)} required />
        </div>}
        {err && <p className="error">{err}</p>}
        <div className="row"><Btn type="button" onClick={() => nav('/pedidos')}>Voltar</Btn><Btn variant="primary" type="submit">Salvar pedido</Btn></div>
      </form></Glass>
    </Page>
  )
}

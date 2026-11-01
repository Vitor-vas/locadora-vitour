import { useCallback, useEffect, useState } from 'react'
import { Link, useParams } from 'react-router-dom'
import * as api from '../api'
import { useAuth } from '../context/AuthContext'
import { Badge, Btn, brl, Glass, Page } from '../components/ui'
import { canCancel, canEdit, type Car, type Client, type Order, type OrderStatus, type User } from '../types'

export default function OrderDetail() {
  const { id = '' } = useParams<{ id: string }>()
  const { user } = useAuth()
  const [o, setO] = useState<Order>(), [client, setClient] = useState<Client>(), [car, setCar] = useState<Car>(), [banco, setBanco] = useState<User>()
  const [parecer, setParecer] = useState('')
  const load = useCallback(async () => {
    const ord = await api.orders.get(id)
    if (!ord) return
    setO(ord); setParecer(ord.parecer ?? '')
    setClient(await api.clients.get(ord.clientId)); setCar(await api.cars.get(ord.carId))
    setBanco(ord.credito ? await api.users.get(ord.credito.bancoId) : undefined)
  }, [id])
  useEffect(() => { load() }, [load])
  if (!o || !user) return <Page title="Pedido">Carregando…</Page>

  const set = async (status: OrderStatus) => { await api.orders.update(o.id, { status, parecer }); load() }
  const renda = client?.empregadores.reduce((s, e) => s + e.rendimento, 0) ?? 0 // análise financeira: soma de rendimentos
  const agent = user.role === 'AGENTE'

  return (
    <Page title="Detalhes do pedido" actions={<Badge status={o.status} />}>
      <div className="grid2">
        <Glass><h3>Contratante</h3><p>{client?.nome}</p><p className="muted">CPF {client?.cpf} · RG {client?.rg}</p><p className="muted">{client?.profissao} — {client?.endereco}</p>
          {client?.empregadores.map((e, i) => <p key={i}>{e.entidade}: {brl(e.rendimento)}</p>)}<strong>Renda total: {brl(renda)}</strong></Glass>
        <Glass><h3>Automóvel</h3><p>{car?.marca} {car?.modelo} ({car?.ano})</p><p className="muted">Placa {car?.placa} · Matrícula {car?.matricula} · Propriedade: {car?.proprietario}</p>
          <p>Período: {o.inicio} → {o.fim}</p>
          {o.credito && <p>Crédito: {brl(o.credito.valor)} ({banco?.username})</p>}</Glass>
      </div>
      <Glass className="stack"><h3>Parecer financeiro</h3>
        {agent && ['PENDENTE', 'APROVADO'].includes(o.status)
          ? <textarea value={parecer} onChange={(e) => setParecer(e.target.value)} placeholder="Escreva o parecer…" />
          : <p>{o.parecer || 'Sem parecer até o momento.'}</p>}
        <div className="row">
          {agent && o.status === 'PENDENTE' && <><Btn variant="primary" onClick={() => set('APROVADO')}>Aprovar</Btn><Btn variant="danger" onClick={() => set('REPROVADO')}>Reprovar</Btn></>}
          {agent && o.status === 'APROVADO' && <Btn variant="primary" onClick={() => set('CONTRATADO')}>Executar contrato</Btn>}
          {canEdit(o, user.role) && <Link className="btn ghost" to={`/pedidos/${o.id}/editar`}>Modificar</Link>}
          {canCancel(o) && <Btn variant="danger" onClick={() => set('CANCELADO')}>Cancelar pedido</Btn>}
          <Link className="btn ghost" to="/pedidos">Voltar</Link>
        </div>
      </Glass>
    </Page>
  )
}

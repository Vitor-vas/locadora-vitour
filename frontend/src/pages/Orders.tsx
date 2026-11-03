import { useCallback, useEffect, useState } from 'react'
import { Link } from 'react-router-dom'
import { motion } from 'motion/react'
import * as api from '../api'
import { useAuth } from '../context/AuthContext'
import { Badge, Btn, Glass, Page, SelectField, STATUSES } from '../components/ui'
import { canCancel, canEdit, type Car, type Client, type Order, type OrderStatus } from '../types'

export default function Orders() {
  const { user } = useAuth()
  const [list, setList] = useState<Order[]>([]), [cl, setCl] = useState<Client[]>([]), [cars, setCars] = useState<Car[]>([])
  const [filter, setFilter] = useState<OrderStatus | ''>('')
  const isAgent = user?.role === 'AGENTE'
  const load = useCallback(async () => {
    // GET /clients é exclusivo de agentes (403 para cliente), então o cliente nem tenta carregar a lista.
    const noClients: Client[] = []
    const [o, c, k] = await Promise.all([api.orders.list(), isAgent ? api.clients.list() : noClients, api.cars.list()])
    setList(o); setCl(c); setCars(k)
  }, [isAgent])
  useEffect(() => { load() }, [load])

  // Cliente enxerga só os próprios pedidos; agente vê todos.
  const rows = list.filter((o) => (isAgent || o.clientId === user?.clientId) && (!filter || o.status === filter))
  const cancel = async (id: string) => { if (confirm('Cancelar este pedido?')) { await api.orders.update(id, { status: 'CANCELADO' }); load() } }

  return (
    <Page title={isAgent ? 'Pedidos de aluguel' : 'Meus pedidos'} actions={<Link className="btn primary" to="/pedidos/novo">+ Novo pedido</Link>}>
      <SelectField label="Filtrar por status" value={filter} onChange={(e) => setFilter(e.target.value as OrderStatus | '')}>
        <option value="">Todos</option>{STATUSES.map((s) => <option key={s}>{s}</option>)}
      </SelectField>
      <Glass><table><thead><tr><th>Cliente</th><th>Carro</th><th>Período</th><th>Status</th><th /></tr></thead><tbody>
        {rows.map((o, i) => {
          const car = cars.find((k) => k.id === o.carId)
          return (
            // delay crescente por linha = efeito cascata (stagger)
            <motion.tr key={o.id} initial={{ opacity: 0, x: -12 }} animate={{ opacity: 1, x: 0 }} transition={{ delay: i * 0.04 }}>
              <td>{cl.find((c) => c.id === o.clientId)?.nome ?? '—'}</td>
              <td>{car ? `${car.marca} ${car.modelo}` : '—'}</td>
              <td>{o.inicio} → {o.fim}</td><td><Badge status={o.status} /></td>
              <td className="row end">
                <Link className="btn ghost" to={`/pedidos/${o.id}`}>Ver</Link>
                {user && canEdit(o, user.role) && <Link className="btn ghost" to={`/pedidos/${o.id}/editar`}>Editar</Link>}
                {canCancel(o) && <Btn variant="danger" onClick={() => cancel(o.id)}>Cancelar</Btn>}
              </td>
            </motion.tr>
          )
        })}
        {!rows.length && <tr><td colSpan={5} className="muted">Nenhum pedido encontrado.</td></tr>}
      </tbody></table></Glass>
    </Page>
  )
}
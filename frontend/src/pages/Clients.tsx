import { useEffect, useState } from 'react'
import { Link } from 'react-router-dom'
import * as api from '../api'
import { Btn, brl, Glass, Page } from '../components/ui'
import type { Client } from '../types'

export default function Clients() {
  const [list, setList] = useState<Client[]>([])
  const load = () => api.clients.list().then(setList)
  useEffect(() => { load() }, [])
  return (
    <Page title="Clientes" actions={<Link className="btn primary" to="/clientes/novo">+ Novo cliente</Link>}>
      <Glass><table><thead><tr><th>Nome</th><th>CPF</th><th>Profissão</th><th>Renda</th><th /></tr></thead><tbody>
        {list.map((c) => (
          <tr key={c.id}><td>{c.nome}</td><td>{c.cpf}</td><td>{c.profissao}</td>
            <td>{brl(c.empregadores.reduce((s, e) => s + e.rendimento, 0))}</td>
            <td className="row end"><Link className="btn ghost" to={`/clientes/${c.id}`}>Editar</Link>
              <Btn variant="danger" onClick={async () => { if (confirm('Excluir cliente?')) { await api.clients.remove(c.id); load() } }}>Excluir</Btn></td></tr>
        ))}
      </tbody></table></Glass>
    </Page>
  )
}

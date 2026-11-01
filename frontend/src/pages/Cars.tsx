import { useEffect, useState, type FormEvent } from 'react'
import * as api from '../api'
import { Btn, Field, Glass, Page, SelectField } from '../components/ui'
import type { Car, Draft, Owner } from '../types'

const EMPTY: Draft<Car> = { matricula: '', ano: new Date().getFullYear(), marca: '', modelo: '', placa: '', proprietario: 'EMPRESA' }

export default function Cars() {
  const [list, setList] = useState<Car[]>([])
  const [f, setF] = useState<Draft<Car>>(EMPTY), [editId, setEditId] = useState<string>()
  const load = () => api.cars.list().then(setList)
  useEffect(() => { load() }, [])
  // TypeScript: união de literais restringe "k" aos campos de texto do carro.
  const txt = (k: 'matricula' | 'marca' | 'modelo' | 'placa') => ({ label: k[0].toUpperCase() + k.slice(1), value: f[k], required: true, onChange: (e: { target: { value: string } }) => setF({ ...f, [k]: e.target.value }) })
  async function submit(e: FormEvent) {
    e.preventDefault()
    if (editId) await api.cars.update(editId, f); else await api.cars.create(f)
    setF(EMPTY); setEditId(undefined); load()
  }
  return (
    <Page title="Automóveis">
      <Glass><form className="stack" onSubmit={submit}>
        <div className="grid2"><Field {...txt('marca')} /><Field {...txt('modelo')} /><Field {...txt('placa')} /><Field {...txt('matricula')} />
          <Field label="Ano" type="number" value={f.ano} onChange={(e) => setF({ ...f, ano: Number(e.target.value) })} required />
          <SelectField label="Propriedade" value={f.proprietario} onChange={(e) => setF({ ...f, proprietario: e.target.value as Owner })}>
            <option>CLIENTE</option><option>EMPRESA</option><option>BANCO</option></SelectField></div>
        <div className="row"><Btn variant="primary" type="submit">{editId ? 'Atualizar' : 'Adicionar'}</Btn>{editId && <Btn type="button" onClick={() => { setF(EMPTY); setEditId(undefined) }}>Cancelar</Btn>}</div>
      </form></Glass>
      <Glass><table><thead><tr><th>Carro</th><th>Ano</th><th>Placa</th><th>Matrícula</th><th>Propriedade</th><th /></tr></thead><tbody>
        {list.map((c) => <tr key={c.id}><td>{c.marca} {c.modelo}</td><td>{c.ano}</td><td>{c.placa}</td><td>{c.matricula}</td><td>{c.proprietario}</td>
          <td className="row end"><Btn onClick={() => { const { id, ...rest } = c; setEditId(id); setF(rest) }}>Editar</Btn>
            <Btn variant="danger" onClick={async () => { await api.cars.remove(c.id); load() }}>Excluir</Btn></td></tr>)}
      </tbody></table></Glass>
    </Page>
  )
}

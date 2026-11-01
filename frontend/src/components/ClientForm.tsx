import { useState, type ChangeEvent, type FormEvent, type ReactNode } from 'react'
import { AnimatePresence, motion } from 'motion/react'
import type { Client, Draft } from '../types'
import { Btn, Field, Glass } from './ui'

interface Props { initial?: Draft<Client>; submitLabel?: string; onSubmit: (d: Draft<Client>) => Promise<void> | void; children?: ReactNode }

// Formulário reutilizado em: cadastro, "meu perfil" e CRUD de clientes (agente).
export default function ClientForm({ initial, submitLabel = 'Salvar', onSubmit, children }: Props) {
  const [f, setF] = useState({ rg: initial?.rg ?? '', cpf: initial?.cpf ?? '', nome: initial?.nome ?? '', endereco: initial?.endereco ?? '', profissao: initial?.profissao ?? '' })
  // "k" é uma chave local estável para as animações de entrada/saída de cada empregador.
  const [emps, setEmps] = useState(() => (initial?.empregadores ?? []).map((e) => ({ k: crypto.randomUUID(), entidade: e.entidade, rendimento: String(e.rendimento) })))
  const [err, setErr] = useState('')
  // keyof typeof f = só aceita nomes de campos existentes em f.
  const set = (k: keyof typeof f) => (e: ChangeEvent<HTMLInputElement>) => setF({ ...f, [k]: e.target.value })
  const setEmp = (k: string, patch: Partial<(typeof emps)[number]>) => setEmps(emps.map((x) => (x.k === k ? { ...x, ...patch } : x)))

  async function submit(e: FormEvent) {
    e.preventDefault()
    try { await onSubmit({ ...f, empregadores: emps.map((x) => ({ entidade: x.entidade, rendimento: Number(x.rendimento) })) }); setErr('') }
    catch (x) { setErr((x as Error).message) }
  }

  return (
    <Glass>
      <form onSubmit={submit} className="stack">
        {children}
        <div className="grid2">
          <Field label="Nome" value={f.nome} onChange={set('nome')} required />
          <Field label="Profissão" value={f.profissao} onChange={set('profissao')} required />
          <Field label="RG" value={f.rg} onChange={set('rg')} required />
          <Field label="CPF" value={f.cpf} onChange={set('cpf')} required />
        </div>
        <Field label="Endereço" value={f.endereco} onChange={set('endereco')} required />
        <div className="row between"><h3>Entidades empregadoras ({emps.length}/3)</h3>
          {emps.length < 3 && <Btn type="button" onClick={() => setEmps([...emps, { k: crypto.randomUUID(), entidade: '', rendimento: '' }])}>+ Adicionar</Btn>}
        </div>
        {/* AnimatePresence permite animar a SAÍDA (exit) de elementos removidos da árvore React. */}
        <AnimatePresence initial={false}>
          {emps.map((x) => (
            <motion.div key={x.k} className="grid2 emp" initial={{ opacity: 0, height: 0 }} animate={{ opacity: 1, height: 'auto' }} exit={{ opacity: 0, height: 0 }}>
              <Field label="Entidade" value={x.entidade} onChange={(e) => setEmp(x.k, { entidade: e.target.value })} required />
              <div className="row end"><Field label="Rendimento (R$)" type="number" min="0" value={x.rendimento} onChange={(e) => setEmp(x.k, { rendimento: e.target.value })} required />
                <Btn type="button" variant="danger" onClick={() => setEmps(emps.filter((y) => y.k !== x.k))}>✕</Btn></div>
            </motion.div>
          ))}
        </AnimatePresence>
        {err && <p className="error">{err}</p>}
        <Btn variant="primary" type="submit">{submitLabel}</Btn>
      </form>
    </Glass>
  )
}

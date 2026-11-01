import { motion, type HTMLMotionProps } from 'motion/react'
import type { InputHTMLAttributes, ReactNode, SelectHTMLAttributes } from 'react'
import type { OrderStatus } from '../types'

export const brl = (n: number) => n.toLocaleString('pt-BR', { style: 'currency', currency: 'BRL' })

// motion.div = <div> animável. initial -> animate descreve a animação de entrada.
export function Glass({ children, className = '' }: { children: ReactNode; className?: string }) {
  return (
    <motion.div className={`glass ${className}`} initial={{ opacity: 0, y: 16 }} animate={{ opacity: 1, y: 0 }} transition={{ duration: 0.4 }}>
      {children}
    </motion.div>
  )
}

// Container padrão de página: título + ações (botões) + conteúdo.
export function Page({ title, actions, children }: { title: string; actions?: ReactNode; children: ReactNode }) {
  return (
    <motion.main className="page" initial={{ opacity: 0 }} animate={{ opacity: 1 }}>
      <div className="row between"><h1>{title}</h1><div className="row">{actions}</div></div>
      {children}
    </motion.main>
  )
}

// TypeScript: HTMLMotionProps<'button'> reaproveita todas as props de <button> + props do Motion.
// whileHover/whileTap = animações de gesto, ativas enquanto o gesto durar.
export function Btn({ variant = 'ghost', ...p }: HTMLMotionProps<'button'> & { variant?: 'primary' | 'ghost' | 'danger' }) {
  return <motion.button className={`btn ${variant}`} whileHover={{ scale: 1.04 }} whileTap={{ scale: 0.96 }} {...p} />
}

// TypeScript: "label" é extraída e o resto (...p) é repassado ao <input> com todos os atributos HTML tipados.
export function Field({ label, ...p }: { label: string } & InputHTMLAttributes<HTMLInputElement>) {
  return <label className="field"><span>{label}</span><input {...p} /></label>
}
export function SelectField({ label, children, ...p }: { label: string } & SelectHTMLAttributes<HTMLSelectElement>) {
  return <label className="field"><span>{label}</span><select {...p}>{children}</select></label>
}

// Record<OrderStatus, string> obriga a ter uma entrada para CADA status (erro de compilação se faltar).
const LABEL: Record<OrderStatus, string> = { PENDENTE: 'Pendente', APROVADO: 'Aprovado', REPROVADO: 'Reprovado', CONTRATADO: 'Contratado', CANCELADO: 'Cancelado' }
export const Badge = ({ status }: { status: OrderStatus }) => <span className={`badge ${status}`}>{LABEL[status]}</span>
export const STATUSES = Object.keys(LABEL) as OrderStatus[]

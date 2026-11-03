import { NavLink, Outlet, useNavigate } from 'react-router-dom'
import { motion } from 'motion/react'
import { useAuth } from '../context/AuthContext'
import { Btn } from './ui'

export default function Layout() {
  const { user, signOut } = useAuth()
  const nav = useNavigate()
  // TypeScript: tipo de tupla [string, string][] = lista de pares [rota, texto].
  const links: [string, string][] = user?.role === 'AGENTE'
    ? [['/pedidos', 'Pedidos'], ['/clientes', 'Clientes'], ['/carros', 'Carros']]
    : [['/pedidos', 'Meus pedidos'], ['/perfil', 'Meu perfil']]
  return (
    <>
      {/* y negativo -> barra "desce" ao entrar */}
      <motion.header className="glass nav" initial={{ y: -40, opacity: 0 }} animate={{ y: 0, opacity: 1 }}>
        <strong className="logo">◆ Locadora Vitour</strong>
        <nav className="row">{links.map(([to, t]) => <NavLink key={to} to={to}>{t}</NavLink>)}</nav>
        <div className="row"><small>{user?.username} · {user?.agentType ?? user?.role}</small>
          <Btn onClick={() => { signOut(); nav('/login') }}>Sair</Btn></div>
      </motion.header>
      <Outlet />
    </>
  )
}

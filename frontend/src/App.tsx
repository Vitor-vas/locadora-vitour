import { Navigate, Outlet, Route, Routes } from 'react-router-dom'
import { useAuth } from './context/AuthContext'
import Layout from './components/Layout'
import type { Role } from './types'
import Login from './pages/Login'
import Register from './pages/Register'
import Orders from './pages/Orders'
import OrderForm from './pages/OrderForm'
import OrderDetail from './pages/OrderDetail'
import ClientEdit from './pages/ClientEdit'
import Clients from './pages/Clients'
import Cars from './pages/Cars'

// Guarda de rota: sem login -> /login; papel errado -> /pedidos. <Outlet/> renderiza as rotas filhas.
function Protected({ role }: { role?: Role }) {
  const { user } = useAuth()
  if (!user) return <Navigate to="/login" replace />
  if (role && user.role !== role) return <Navigate to="/pedidos" replace />
  return <Outlet />
}

export default function App() {
  return (
    <Routes>
      <Route path="/login" element={<Login />} />
      <Route path="/cadastro" element={<Register />} />
      <Route element={<Protected />}><Route element={<Layout />}>
        <Route path="/" element={<Navigate to="/pedidos" replace />} />
        <Route path="/pedidos" element={<Orders />} />
        <Route path="/pedidos/novo" element={<OrderForm />} />
        <Route path="/pedidos/:id" element={<OrderDetail />} />
        <Route path="/pedidos/:id/editar" element={<OrderForm />} />
        <Route path="/perfil" element={<ClientEdit />} />
        <Route element={<Protected role="AGENTE" />}>
          <Route path="/clientes" element={<Clients />} />
          <Route path="/clientes/novo" element={<ClientEdit />} />
          <Route path="/clientes/:id" element={<ClientEdit />} />
          <Route path="/carros" element={<Cars />} />
        </Route>
      </Route></Route>
      <Route path="*" element={<Navigate to="/" replace />} />
    </Routes>
  )
}

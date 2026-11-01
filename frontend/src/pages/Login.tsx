import { useState, type FormEvent } from 'react'
import { Link, useNavigate } from 'react-router-dom'
import { useAuth } from '../context/AuthContext'
import { Btn, Field, Glass } from '../components/ui'

export default function Login() {
  const { signIn } = useAuth()
  const nav = useNavigate()
  const [username, setU] = useState(''), [password, setP] = useState(''), [err, setErr] = useState('')
  async function submit(e: FormEvent) {
    e.preventDefault()
    try { await signIn(username, password); nav('/pedidos') } catch (x) { setErr((x as Error).message) }
  }
  return (
    <div className="center"><Glass className="auth">
      <h1>◆ DriveRent</h1><p className="muted">Entre para gerenciar seus aluguéis</p>
      <form onSubmit={submit} className="stack">
        <Field label="Usuário" value={username} onChange={(e) => setU(e.target.value)} required />
        <Field label="Senha" type="password" value={password} onChange={(e) => setP(e.target.value)} required />
        {err && <p className="error">{err}</p>}
        <Btn variant="primary" type="submit">Entrar</Btn>
      </form>
      <p>Novo por aqui? <Link to="/cadastro">Cadastre-se</Link></p>
      <small className="muted">Demo: cliente/cliente123 · banco/banco123 · empresa/empresa123</small>
    </Glass></div>
  )
}

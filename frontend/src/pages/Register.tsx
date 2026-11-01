import { useState } from 'react'
import { Link, useNavigate } from 'react-router-dom'
import * as api from '../api'
import ClientForm from '../components/ClientForm'
import { Field } from '../components/ui'

export default function Register() {
  const nav = useNavigate()
  const [username, setU] = useState(''), [password, setP] = useState('')
  return (
    <div className="page narrow">
      <h1>Criar conta</h1>
      <ClientForm submitLabel="Cadastrar" onSubmit={async (c) => { await api.register({ username, password }, c); nav('/login') }}>
        <div className="grid2">
          <Field label="Usuário" value={username} onChange={(e) => setU(e.target.value)} required />
          <Field label="Senha" type="password" minLength={6} value={password} onChange={(e) => setP(e.target.value)} required />
        </div>
      </ClientForm>
      <p>Já tem conta? <Link to="/login">Entrar</Link></p>
    </div>
  )
}

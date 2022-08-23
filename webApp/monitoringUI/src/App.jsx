import { useState } from 'react'
import reactLogo from './assets/react.svg'
import './App.css'

function App() {
  const [count, setCount] = useState(0)

  return (
  <div>
    <h1>hello world</h1>
    <button type="button" onclick={()=>(setCount(count+1)}>click</button>
    <p>{count}</p>
  </div>
  )
}

export default App

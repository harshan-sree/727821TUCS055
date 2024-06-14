import React, { useState } from 'react'
import axios from 'axios';
function Calculator() {
        const [type, setType] = useState('');
        const [response, setResponse] = useState(null);
    
        const fetchNum = async () => {
            try {
                const res = await axios.get(`http://localhost:9876/numbers/${type}`);
                setResponse(res.data);
            } catch (error) {
                console.error("Error fetching data", error);
            }
        };
  return (
    <div>
    <h1>Average Calculator</h1>
            <input
                type="text"
                value={type}
                onChange={(e) => setType(e.target.value)}
                placeholder="even/prime/fibo/rand"
            />
            <br>
            </br>
            <button onClick={fetchNum}>Fetch Numbers</button>
            {response && (
                <div>
                    <h2>Response</h2>
                    <pre>{JSON.stringify(response, null, 3)}</pre>
                </div>
            )}
        </div>
  )
}

export default Calculator

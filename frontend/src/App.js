import React from 'react';
import { BrowserRouter as Router, Routes, Route } from 'react-router-dom';
import Homepage from './components/homepage';
import BuyerAccount from './components/BuyerAccount';

function App() {
  return (
    <Router>
      <Routes>
        <Route path="/" element={<Homepage />} />
        <Route path="/buyeraccount" element={<BuyerAccount />} />
      </Routes>
    </Router>
  );
}

export default App;
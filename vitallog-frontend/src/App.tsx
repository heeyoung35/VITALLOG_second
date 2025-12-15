// src/App.tsx
// src/App.tsx
import { BrowserRouter as Router, Routes, Route } from 'react-router-dom';
import Header from './components/Header';
import MainPage from './pages/MainPage';
import LoginPage from './pages/LoginPage';
import AiRecommendPage from './pages/AiRecommendPage';
import SupplementDetailPage from './pages/SupplementDetailPage';
// import CartPage from './pages/CartPage'; // (장바구니 구현 시 주석 해제)

function App() {
    return (
        <Router>
            <Header />
            <div className="min-h-screen bg-white">
                <Routes>
                    <Route path="/" element={<MainPage />} />
                    <Route path="/login" element={<LoginPage />} />
                    <Route path="/recommend" element={<AiRecommendPage />} />
                    <Route path="/supplement/:id" element={<SupplementDetailPage />} />
                    {/* <Route path="/cart" element={<CartPage />} /> */}
                </Routes>
            </div>
        </Router>
    );
}

export default App;
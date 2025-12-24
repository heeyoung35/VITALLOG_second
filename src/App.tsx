import { BrowserRouter as Router, Routes, Route } from 'react-router-dom';
import Header from './components/Header';
import MainPage from './pages/MainPage';
import LoginPage from './pages/LoginPage';
import AiRecommendPage from './pages/AiRecommendPage';
import SupplementDetailPage from './pages/SupplementDetailPage';
import CartPage from './pages/CartPage';

function App() {
    return (
        <Router>
            <Header />
            <div className="min-h-screen bg-white w-full overflow-x-hidden">
                <Routes>
                    <Route path="/" element={<MainPage />} />
                    <Route path="/login" element={<LoginPage />} />
                    <Route path="/recommend" element={<AiRecommendPage />} />
                    <Route path="/supplement/:id" element={<SupplementDetailPage />} />
                    <Route path="/cart" element={<CartPage />} />
                </Routes>
            </div>
        </Router>
    );
}

export default App;
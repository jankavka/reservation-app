import "./App.css";
import PublicLayout from "./layouts/publicLayout";
import { BrowserRouter as Router, Routes, Route } from "react-router";
import { QueryClient, QueryClientProvider } from "@tanstack/react-query";
import AuthProvider from "./components/AuthProvider";

function App() {
  const queryClient = new QueryClient();
  return (
    <div>
      <QueryClientProvider client={queryClient}>
        <AuthProvider>
          <Router>
            <Routes>
              <Route path={"/*"} element={<PublicLayout />} />
            </Routes>
          </Router>
        </AuthProvider>
      </QueryClientProvider>
    </div>
  );
}

export default App;

import "./App.css";
import PublicLayout from "./layouts/PublicLayout";
import { BrowserRouter as Router, Routes, Route } from "react-router";
import { QueryClient, QueryClientProvider } from "@tanstack/react-query";
import AuthProvider from "./components/AuthProvider";
import AdminRoute from "./components/AdminRoute";
import AdminLayout from "./layouts/AdminLayout";

function App() {
  const queryClient = new QueryClient();
  return (
    <div>
      <QueryClientProvider client={queryClient}>
        <AuthProvider>
          <Router>
            <Routes>
              <Route path={"/*"} element={<PublicLayout />} />
              <Route
                path={"/admin/*"}
                element={
                  <AdminRoute>
                    <AdminLayout />
                  </AdminRoute>
                }
              />
            </Routes>
          </Router>
        </AuthProvider>
      </QueryClientProvider>
    </div>
  );
}

export default App;

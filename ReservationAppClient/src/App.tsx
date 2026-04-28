import "./App.css";
import PublicLayout from "./layouts/publicLayout";
import { BrowserRouter as Router, Routes, Route } from "react-router";
import { QueryClient, QueryClientProvider } from "@tanstack/react-query";

function App() {
  const queryClient = new QueryClient()
  return (
    <div>
      <QueryClientProvider client={queryClient}>
        <Router>
          <Routes>
            <Route path={"/*"} element={<PublicLayout />} />
          </Routes>
        </Router>
      </QueryClientProvider>
    </div>
  );
}

export default App;

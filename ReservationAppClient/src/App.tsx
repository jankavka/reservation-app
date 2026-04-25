import "./App.css";
import PublicLayout from "./layouts/publicLayout";
import { BrowserRouter as Router, Routes, Route } from "react-router";


function App() {
  return (
    <div>
      <Router>
        <Routes>
          <Route path={"/*"} element={<PublicLayout/>}/>
        </Routes>
      </Router>
    </div>
  );
}

export default App;

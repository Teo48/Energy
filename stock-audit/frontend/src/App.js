import "./index.css";
import Header from "./components/Layout/Header";
import QueryForm from "./components/QueryForm/QueryForm";
import FormProvider from "./components/store/FormProvider";

function App() {
  return (
    <FormProvider>
      <Header />
      <QueryForm />
    </FormProvider>
  );
}

export default App;

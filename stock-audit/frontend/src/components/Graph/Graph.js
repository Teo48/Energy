import classes from "./Graph.module.css";
import CandleChart from "../CandleChart/CandleChart";
import { useContext, useEffect, useState } from "react";
import CircularProgress from "@mui/material/CircularProgress";
import Box from "@mui/material/Box";
import FormContext from "../store/form-context";
import { urlCreator } from "../../Helpers";

const Graph = () => {
  const formCtx = useContext(FormContext);
  const defaultUrl = "http://localhost:8080/api/stocks/select";

  const [stocksData, setStocksData] = useState([]);
  const [isLoading, setIsLoading] = useState(false);
  const [error, setError] = useState(null);

  useEffect(() => {
    const url = urlCreator(formCtx, defaultUrl);
    const fetchStocks = async () => {
      setIsLoading(true);
      setError(null);

      const response = await fetch(url);
      
      if (!response.ok) {
        console.log(response)
        throw new Error("Something went wrong!");
      }
      const data = await response.json();

      const loadedStocks = data.map((stock) => ({
        x: Date.parse(stock.timestamp),
        y: [stock.openPrice, stock.highPrice, stock.lowPrice, stock.closePrice],    
      }));

      setStocksData(loadedStocks);
      setIsLoading(false);
    };

    fetchStocks().catch((error) => {
      setIsLoading(false);
      setError(error.message);
    });
  }, [formCtx]);

  return (
    <section className={classes.graph}>
      <div className={classes["inner-graph-div"]}>
        {!isLoading && !error && <CandleChart data={stocksData} />}
        {isLoading && (
          <Box className={classes.box}>
            <CircularProgress />
          </Box>
        )}
        {!isLoading && error && <p className={classes.error}>{error}</p>}
      </div>
    </section>
  );
};

export default Graph;

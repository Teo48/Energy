import Button from "@mui/material/Button";
import DeleteIcon from "@mui/icons-material/Delete";
import SendIcon from "@mui/icons-material/Send";
import FormContext from "../store/form-context";
import { useContext } from "react";

const FormButtons = (props) => {
  const formCtx = useContext(FormContext);

  const resetHandler = () => {
    props.onHideGraph();
    formCtx.setStartDate(null);
    formCtx.setEndDate(null);
    formCtx.setCompanySymbol('');
    formCtx.setCompanyName('');
    formCtx.setSearchField('');
    formCtx.setValueOfField('');
  }

  const searchHandler = () => {
    props.onShowGraph();
  };

  return (
    <>
      <div>
        <Button
          style={{ width: "10%", marginRight: "20px" }}
          variant="outlined"
          startIcon={<DeleteIcon />}
          onClick={resetHandler}
        >
          RESET
        </Button>
        <Button
          style={{ width: "10%" }}
          variant="contained"
          startIcon={<SendIcon />}
          onClick={searchHandler}
        >
          SEARCH
        </Button>
      </div>
    </>
  );
};

export default FormButtons;

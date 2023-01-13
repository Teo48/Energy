import { useContext } from "react";
import {
  TextField,
} from "@mui/material";
import FormContext from "../store/form-context";

const SearchValueFields = () => {
  const formCtx = useContext(FormContext);

  return (
    <>
      <TextField
        fullWidth
        id="field"
        label="Search Field"
        value={formCtx.searchField}
        onChange={(e) => formCtx.setSearchField(e.target.value)}
        style={{ display: "block" }}
      />
      <TextField
        fullWidth
        id="word"
        label="Value Of Field"
        value={formCtx.valueOfField}
        onChange={(e) => formCtx.setValueOfField(e.target.value)}
        style={{ display: "block" }}
      />
    </>
  );
};

export default SearchValueFields;

import { useContext } from "react";
import { FormControl, InputLabel, MenuItem, Select } from "@mui/material";
import FormContext from "../store/form-context";
import { companies } from "../../Constants";

const CompanyDropdowns = () => {
  const formCtx = useContext(FormContext);

  return (
    <>
      <FormControl style={{ display: "block" }}>
        <InputLabel id="company-symbol-label">Company Symbol</InputLabel>
        <Select
          labelId="company-symbol-label"
          id="company-symbol"
          value={formCtx.companySymbol}
          label="Company Symbol"
          onChange={(e) => formCtx.setCompanySymbol(e.target.value)}
          style={{ width: "100%" }}
        >
          {companies.map((company) => (
            <MenuItem key={company.symbol} value={company.symbol}>
              {company.symbol}
            </MenuItem>
          ))}
        </Select>
      </FormControl>
      <FormControl style={{ display: "block" }}>
        <InputLabel id="company-name-label">Company Name</InputLabel>
        <Select
          labelId="company-name-label"
          id="company-name"
          value={formCtx.companyName}
          label="Company Name"
          onChange={(e) => formCtx.setCompanyName(e.target.value)}
          style={{ width: "100%" }}
        >
          {companies.map((company) => (
            <MenuItem key={company.symbol} value={company.name}>
              {company.name}
            </MenuItem>
          ))}
        </Select>
      </FormControl>
    </>
  );
};

export default CompanyDropdowns;

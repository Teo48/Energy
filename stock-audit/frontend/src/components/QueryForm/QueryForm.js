import { AdapterMoment } from "@mui/x-date-pickers/AdapterMoment";
import { DateTimePicker, LocalizationProvider } from "@mui/x-date-pickers";
import { useContext, useState } from "react";
import { TextField } from "@mui/material";
import classes from "./QueryForm.module.css";
import { Stack } from "@mui/system";
import CompanyDropdowns from "./CompanyDropdowns";
import SearchValueFields from "./SearchValueFields";
import FormButtons from "./FormButtons";
import Graph from "../Graph/Graph";
import FormContext from "../store/form-context";

const QueryFormOverlay = (props) => {
  return (
    <div className={classes["form-modal"]}>
      <LocalizationProvider dateAdapter={AdapterMoment}>
        <Stack spacing={3}>{props.children}</Stack>
      </LocalizationProvider>
    </div>
  );
};

const QueryForm = () => {
  const [graphIsShown, setGraphIsShown] = useState(false);
  const formCtx = useContext(FormContext);

  const showGraphHandler = () => {
    setGraphIsShown(true);
  };

  const hideGraphHandler = () => {
    setGraphIsShown(false);
  };

  return (
    <div
      className={`${
        graphIsShown
          ? classes["show-graph-container"]
          : classes["hide-graph-container"]
      }`}
    >
      <QueryFormOverlay>
        <DateTimePicker
          label="Start Date"
          value={formCtx.startDate}
          onChange={(newStartDateTime) => {
            formCtx.setStartDate(newStartDateTime);
          }}
          renderInput={(params) => <TextField {...params} />}
        />
        <DateTimePicker
          label="End Date"
          value={formCtx.endDate}
          onChange={(newEndDateTime) => {
            formCtx.setEndDate(newEndDateTime);
          }}
          renderInput={(params) => <TextField {...params} />}
        />
        <CompanyDropdowns />
        <SearchValueFields />
        <FormButtons
          onShowGraph={showGraphHandler}
          onHideGraph={hideGraphHandler}
        />
      </QueryFormOverlay>
      {graphIsShown && <Graph />}
    </div>
  );
};

export default QueryForm;

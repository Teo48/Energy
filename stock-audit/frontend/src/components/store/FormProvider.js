import { useReducer } from "react";
import { nameSymbolMap, symbolNameMap } from "../../Constants";
import FormContext from "./form-context";

const defaultFormState = {
  startDate: null,
  endDate: null,
  companySymbol: "",
  companyName: "",
  searchField: "",
  valueOfField: "",
};

const formReducer = (state, action) => {
  if (action.type === 'SET_START') {
    return {
      ...state,
      startDate: action.date,
    };
  } else if (action.type === 'SET_END') {
    return {
      ...state,
      endDate: action.date,
    };
  } else if (action.type === 'SET_SYMBOL') {
    console.log(action.symbol);
    console.log(symbolNameMap.get(action.symbol));
    return {
      ...state,
      companySymbol: action.symbol,
      companyName: symbolNameMap.get(action.symbol),
    };
  } else if (action.type === 'SET_NAME') {
    return {
      ...state,
      companyName: action.name,
      companySymbol: nameSymbolMap.get(action.name),
    };
  } else if (action.type === 'SET_FIELD') {
    return {
      ...state,
      searchField: action.field,
    };    
  } else if (action.type === 'SET_VALUE') {
    return {
      ...state,
      valueOfField: action.value,
    };  
  }

  return defaultFormState;
};

const FormProvider = (props) => {
  const [formState, dispatchFormAction] = useReducer(
    formReducer,
    defaultFormState
  );

  const setStartDateHandler = (date) => {
    dispatchFormAction({ type: 'SET_START', date});
  };

  const setEndDateHandler = (date) => {
    dispatchFormAction({ type: 'SET_END', date});
  };

  const setCompanySymbolHandler = (symbol) => {
    dispatchFormAction({ type: 'SET_SYMBOL', symbol});
  };

  const setCompanyNameHandler = (name) => {
    dispatchFormAction({ type: 'SET_NAME', name});
  };

  const setSearchFieldHandler = (field) => {
    dispatchFormAction({ type: 'SET_FIELD', field});
  };

  const setValueOfFieldHandler = (value) => {
    dispatchFormAction({ type: 'SET_VALUE', value});
  };

  const formContext = {
    startDate: formState.startDate,
    endDate: formState.endDate,
    companySymbol: formState.companySymbol,
    companyName: formState.companyName,
    searchField: formState.searchField,
    valueOfField: formState.valueOfField,
    setStartDate: setStartDateHandler,
    setEndDate: setEndDateHandler,
    setCompanySymbol: setCompanySymbolHandler,
    setCompanyName: setCompanyNameHandler,
    setSearchField: setSearchFieldHandler,
    setValueOfField: setValueOfFieldHandler,
  };

  return (
    <FormContext.Provider value={formContext}>
      {props.children}
    </FormContext.Provider>
  );
};

export default FormProvider;

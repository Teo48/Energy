import React from 'react';

const FormContext = React.createContext({
  startDate: null,
  endDate: null,
  companySymbol: '',
  companyName: '',
  searchField: '',
  valueOfField: '',
  setStartDate: (date) => {},
  setEndDate: (date) => {},
  setCompanySymbol: (symbol) => {},
  setCompanyName: (name) => {},
  setSearchField: (field) => {},
  setValueOfField: (value) => {},
});

export default FormContext;